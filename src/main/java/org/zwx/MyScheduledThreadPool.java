package org.zwx;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MyScheduledThreadPool {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main thread start ...");

        AtomicInteger num = new AtomicInteger(1);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(()->{
            num.getAndIncrement();
            System.out.printf("执行次数: %d\n", num.get());
            if (num.get() >= 5) {
                executorService.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);

        System.out.println("main thread end");
    }
}
