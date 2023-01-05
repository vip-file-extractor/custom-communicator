package com.evam.marketing.communication.service.client;

import com.evam.marketing.communication.utils.PerformanceCounter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract persistence service
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Slf4j
abstract class AbstractPersistenceService<T> {
    private final JdbcTemplate jdbcTemplate;
    private final LinkedBlockingQueue<T> queue;
    private final int persistPoolSize;
    private final int persistJobBufferSize;
    private final String persistSql;
    private final PerformanceCounter performanceCounter;
    private final ScheduledExecutorService scheduledExecutorService;

    protected AbstractPersistenceService(
            JdbcTemplate jdbcTemplate,
            int persistPoolSize,
            int persistJobBufferSize,
            String persistSql,
            PerformanceCounter performanceCounter
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.persistPoolSize = persistPoolSize;
        this.queue = new LinkedBlockingQueue<>(persistJobBufferSize);
        this.persistJobBufferSize = persistJobBufferSize;
        this.persistSql = persistSql;
        this.performanceCounter = performanceCounter;

        ThreadFactory threadFactory = new CustomizableThreadFactory("Persister_" + getName() + "-");

        this.scheduledExecutorService = Executors.newScheduledThreadPool(persistPoolSize, threadFactory);

        for (int i = 0; i < persistPoolSize; i++) {
            this.scheduledExecutorService.scheduleAtFixedRate(
                    new BatchRunnable(), 0, 1, TimeUnit.NANOSECONDS);
        }
    }

    protected abstract void setPs(PreparedStatement ps, T req) throws SQLException;

    protected abstract String getName();

    public void add(T request) {
        try {
            if (!this.queue.offer(request, 1, TimeUnit.SECONDS)) {
                persistSingle(request);
            }
        } catch (InterruptedException e) {
            persistSingle(request);
            Thread.currentThread().interrupt();
        }
    }

    public void persistSingle(T request) {
        long l = System.nanoTime();
        try {
            this.jdbcTemplate.update(this.persistSql, ps -> setPs(ps, request));
        } catch (DataAccessException e) {
            log.error("Error while persisting request {}", request, e);
        }
    }

    public void shutDown() {
        log.info("Start Persister-{} clean up", getName());

        scheduledExecutorService.shutdown();
        try {
            while (!scheduledExecutorService.awaitTermination(500,
                    TimeUnit.MILLISECONDS)) {// Poll every 0.5s, you can output the behavior log of the thread pool during the shutdown process here.
                log.info("Awaiting the end of the main process execution for {}", getName());
            }
        } catch (InterruptedException e) {
            log.info(
                    "An exception occurred while waiting for the end of the main process execution for {}",
                    getName());
            Thread.currentThread().interrupt();
        }
        ArrayList<T> requests = new ArrayList<>();
        queue.drainTo(requests);
        persist(requests);
        log.info("Persister-{} clean up finished", getName());
    }

    private void persist(List<T> requests) {
        if (requests.isEmpty()) {
            return;
        }

        long l = System.nanoTime();
        try {
            jdbcTemplate.batchUpdate(persistSql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    T req = requests.get(i);
                    setPs(ps, req);
                }

                @Override
                public int getBatchSize() {
                    return requests.size();
                }
            });
        } catch (DataAccessException e) {
            log.error("Error while persisting requests {}", requests, e);
        }
    }

    private class BatchRunnable implements Runnable {
        AtomicLong previousRunTime = new AtomicLong(System.currentTimeMillis());

        @Override
        public void run() {
            ArrayList<T> drainList = new ArrayList<>();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    T poll = queue.poll(1000, TimeUnit.MILLISECONDS);
                    if (poll != null) {
                        drainList.add(poll);
                    }

                    boolean timedOut = System.currentTimeMillis() - previousRunTime.get()
                            > TimeUnit.SECONDS.toMillis(10);
                    if (drainList.size() >= persistJobBufferSize / persistPoolSize || timedOut) {
                        persist(drainList);
                        previousRunTime.set(System.currentTimeMillis());
                        break;
                    }
                } catch (InterruptedException e) {
                    persist(drainList);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("Error while persisting-{}", getName(), e);
                }
            }
        }
    }
}
