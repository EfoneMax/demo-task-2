package com.example.demo2;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HandlerImpl implements Task2Data.Handler {

    private static final ForkJoinPool HANDLER_JOIN_POOL = new ForkJoinPool();
    public static final ScheduledExecutorService SCHEDULED_EXECUTOR =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() - 1);

    private final Task2Data.Client client;

    public HandlerImpl(Task2Data.Client client) {
        this.client = client;
    }

    @Override
    public Duration timeout() {
        return Duration.ofSeconds(5);
    }

    @Override
    public void performOperation() {
        Task2Data.Event event = client.readData();
        CountDownLatch latch = new CountDownLatch(event.recipients().size());

        HANDLER_JOIN_POOL.submit(() ->
                event.recipients()
                        .parallelStream()
                        .forEach(address -> processData(address, event.payload(), latch)));

        try {
            latch.await();
        } catch (InterruptedException E) {
            // handle
        }
    }

    private void processData(Task2Data.Address address, Task2Data.Payload payload, CountDownLatch latch) {
        Task2Data.Result result = client.sendData(address, payload);
        if (Task2Data.Result.REJECTED == result) {
            SCHEDULED_EXECUTOR.schedule(() -> HANDLER_JOIN_POOL.submit(() -> processData(address, payload, latch)),
                    timeout().getSeconds(),
                    TimeUnit.SECONDS);
        } else {
            latch.countDown();
        }
    }
}
