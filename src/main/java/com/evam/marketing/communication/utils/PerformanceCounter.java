package com.evam.marketing.communication.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance counter
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Slf4j
@Service
public class PerformanceCounter {
    private final AtomicLong eventCountTotal = new AtomicLong(0);

    private final AtomicLong eventCountSilent = new AtomicLong(0);
    private final AtomicLong eventCountTimeConstraint = new AtomicLong(0);

    private final AtomicLong eventCountSuccess = new AtomicLong(0);
    private final AtomicLong eventCountFail = new AtomicLong(0);

    private final AtomicLong wscallCount = new AtomicLong(0);
    private final AtomicLong wscallTimeAvarage = new AtomicLong(0);

    private final AtomicLong appLifetimeTotalWSCallCount = new AtomicLong(0);
    private final AtomicLong appLifeTimeWSCallAverage = new AtomicLong(0);

    private final AtomicLong ts = new AtomicLong(System.currentTimeMillis());

    private final AtomicLong batchCountTotal = new AtomicLong(0);
    private final AtomicLong batchCountSuccess = new AtomicLong(0);
    private final AtomicLong batchCountError = new AtomicLong(0);
    private final AtomicLong batchCountDuplicate = new AtomicLong(0);
    private final AtomicLong eventCountDuplicate = new AtomicLong(0);

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public PerformanceCounter() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
                new CustomizableThreadFactory("StatWorker-" + getClass().getName()));
        scheduledExecutorService.scheduleAtFixedRate(this::report, 30, 30, TimeUnit.SECONDS);
    }

    public void incrementBatchCountSuccess() {
        batchCountSuccess.incrementAndGet();
        batchCountTotal.incrementAndGet();
    }

    public void incrementBatchCountError() {
        batchCountError.incrementAndGet();
        batchCountTotal.incrementAndGet();
    }

    public void incrementEventCountDuplicate(long size) {
        eventCountDuplicate.addAndGet(size);
        eventCountTotal.addAndGet(size);
    }

    public void incrementEventCountSilent() {
        eventCountSilent.incrementAndGet();
        eventCountTotal.incrementAndGet();
    }

    public void incrementWSCallCount(long totalCallTime) {
        wscallCount.incrementAndGet();
        appLifetimeTotalWSCallCount.incrementAndGet();
        wscallTimeAvarage.addAndGet(totalCallTime);
        appLifeTimeWSCallAverage.addAndGet(totalCallTime);
    }

    public void incrementEventCountSuccess() {
        eventCountSuccess.incrementAndGet();
        eventCountTotal.incrementAndGet();
    }

    public void incrementEventCountFail() {
        eventCountFail.incrementAndGet();
        eventCountTotal.incrementAndGet();
    }

    public synchronized void report() {
        long eCountSilent = eventCountSilent.getAndSet(0);
        long eCountTimeConstraint = eventCountTimeConstraint.getAndSet(0);
        long eCountSuccess = eventCountSuccess.getAndSet(0);
        long eCountFail = eventCountFail.getAndSet(0);
        long eCountTotal = eventCountTotal.getAndSet(0);

        long bCountSuccess = batchCountSuccess.getAndSet(0);
        long bCountError = batchCountError.getAndSet(0);
        long bCountDuplicate = batchCountDuplicate.getAndSet(0);
        long bCountTotal = batchCountTotal.getAndSet(0);

        long eCountDuplicate = eventCountDuplicate.getAndSet(0);

        long appLifetimeWSCallCount = appLifetimeTotalWSCallCount.get();
        long appLifeTimeWSCallAverageMillis = appLifeTimeWSCallAverage.get();
        long totalWSCallCount = wscallCount.getAndSet(0);
        long periodicWSCallAverageMillis = wscallTimeAvarage.getAndSet(0);

        if (periodicWSCallAverageMillis == 0 || totalWSCallCount == 0) {
            periodicWSCallAverageMillis = 1;
            totalWSCallCount = 1;
        }

        if (appLifeTimeWSCallAverageMillis == 0 || appLifetimeWSCallCount == 0) {
            appLifeTimeWSCallAverageMillis = 1;
            appLifetimeWSCallCount = 1;
        }

        long now = System.currentTimeMillis();
        long diff = now - ts.getAndSet(now);
        double multiplier = 1000d / diff;
        Object[] counters = new Object[] {
                bCountSuccess,
                bCountError,
                bCountDuplicate,
                bCountTotal,
                decimalFormat.format(bCountTotal * multiplier),
                eCountDuplicate,
                eCountSilent,
                eCountTimeConstraint,
                eCountSuccess,
                eCountFail,
                eCountTotal,
                decimalFormat.format(eCountTotal * multiplier),
                totalWSCallCount,
                decimalFormat.format(periodicWSCallAverageMillis / totalWSCallCount),
                decimalFormat.format(appLifeTimeWSCallAverageMillis / appLifetimeWSCallCount)
        };

        String info = "batch(success={},error={},duplicate={},total={},tps={}), " +
                "event(duplicate={},silent={},timeConstraint={},success={},fail={},total={},tps={}), " +
                "WebServiceCall(PeriodicTotalWSCallCount={},PeriodicWSCallAverageMillis={}," +
                "AppLifeTimeWSCallAverage={})";
        log.info(info, counters);
    }
}
