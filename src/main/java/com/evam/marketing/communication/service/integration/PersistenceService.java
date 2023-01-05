package com.evam.marketing.communication.service.integration;

import com.evam.marketing.communication.configuration.CustomPusherConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Persistence service
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Service
@Slf4j
public class PersistenceService {
    private final JdbcTemplate jdbcTemplate;
    private final LinkedBlockingQueue<ServiceRequest> queue;
    private final ScheduledExecutorService scheduledExecutorService;
    private final AtomicLong counter;
    private final CustomPusherConfig config;

    public PersistenceService(JdbcTemplate jdbcTemplate, CustomPusherConfig config) {
        this.jdbcTemplate = jdbcTemplate;
        this.config = config;
        queue = new LinkedBlockingQueue<>(config.getPersistJobBufferSize());
        counter = new AtomicLong(0);

        ThreadFactory threadFactory = new CustomizableThreadFactory("Persister-");

        scheduledExecutorService = Executors.newScheduledThreadPool(config.getPersistPoolSize(), threadFactory);

        for (int i = 0; i < config.getPersistPoolSize(); i++) {
            scheduledExecutorService.scheduleAtFixedRate(new BatchRunnable(), 0, 1,
                TimeUnit.NANOSECONDS);
        }
    }

    public void add(ServiceRequest request) {
        try {
            if (!queue.offer(request, 1, TimeUnit.SECONDS)) {
                persistSingle(request);
            }
        } catch (InterruptedException e) {
            persistSingle(request);
            Thread.currentThread().interrupt();
        }
    }

    private void persistSingle(ServiceRequest request) {
        try {
            jdbcTemplate.update(config.getPersistSql(), ps -> setPs(ps, request));
        } catch (DataAccessException e) {
            log.error("Error while persisting request {}", request, e);
        }
    }

    private void persist(List<ServiceRequest> requests) {
        if (requests.isEmpty()) {
            return;
        }

        try {
            jdbcTemplate.batchUpdate(config.getPersistSql(), new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    counter.incrementAndGet();
                    ServiceRequest req = requests.get(i);
                    setPs(ps, req);
                }

                @Override
                public int getBatchSize() {
                    return requests.size();
                }
            });
        } catch (DataAccessException e) {
            log.error("Error while persisting requests.");
            log.error("requests size {}", requests.size());
            log.error("Requests: {}", requests, e);
        }
        log.debug("Persisted {} requests.", requests.size());
    }

    private void setPs(PreparedStatement ps, ServiceRequest req) throws SQLException {
        ps.setString(1, req.getBase().getScenarioName());
        ps.setTimestamp(2, Timestamp.valueOf(req.getComputed().getTimestamp().toLocalDateTime()));
        ps.setString(3, req.getRaw().getMsisdn());
        ps.setString(4, req.getRaw().getTitle());
        ps.setString(5, req.getRaw().getBody());
        ps.setString(6, req.getRaw().getSilentMode());
        ps.setTimestamp(7, Timestamp.valueOf(req.getComputed().getTimestamp().toLocalDateTime()));
        ps.setString(8, req.getBase().getOfferUUID());
        LocalDate localDate = LocalDate.now();
        ps.setDate(9, Date.valueOf(localDate));
        ps.setString(10,req.getRaw().getSegmentName());
        ps.setString(11, req.getRaw().getCampaignNotification());
        ps.setString(12, req.getRaw().getDescription());
        ps.setString(13, req.getRaw().getToken());
    }

    public void shutDown() {
        log.info("Start Persister clean up");

        scheduledExecutorService.shutdown();
        try {
            /*
            * Poll every 0.5s, you can output the behavior log of the thread pool
            * during the shutdown process here.
            */
            while (!scheduledExecutorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                log.info("Awaiting the end of the main process execution");
            }
        } catch (InterruptedException e) {
            log.info("An exception occurred while waiting for the end of the main process execution");
            Thread.currentThread().interrupt();
        }
        ArrayList<ServiceRequest> requests = new ArrayList<>();
        queue.drainTo(requests);
        persist(requests);
        log.info("Persister clean up finished");
    }

    private class BatchRunnable implements Runnable {
        AtomicLong previousRunTime = new AtomicLong(System.currentTimeMillis());

        @Override
        public void run() {
            ArrayList<ServiceRequest> drainList = new ArrayList<>();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ServiceRequest poll = queue.poll(1000, TimeUnit.MILLISECONDS);
                    if (poll != null) {
                        drainList.add(poll);
                    }

                    boolean timedOut = System.currentTimeMillis() - previousRunTime.get()
                        > TimeUnit.SECONDS.toMillis(10);
                    if (drainList.size() >= config.getPersistJobBufferSize() / config.getPersistPoolSize()
                            || timedOut
                    ) {
                        persist(drainList);
                        previousRunTime.set(System.currentTimeMillis());
                        break;
                    }
                } catch (InterruptedException e) {
                    persist(drainList);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("Error while persisting", e);
                }
            }
        }
    }
}
