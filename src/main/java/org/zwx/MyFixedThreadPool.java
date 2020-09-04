package org.zwx;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MyFixedThreadPool {
    public static Integer compute(String name, String path) {
        int total = 0;
        List<Integer> values = GenData.readNums(path);
        for (Integer value : values) {
            total += value;
            System.out.printf("%s: %d\n", name, total);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("%s, total: %d\n", name, total);
        return total;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main thread start ...");

        // 创建4个线程的线程池, 做4个task
        // 有线程池的概念后, Runnable或Callable的一个实例就相当于一个task
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        // 提交Runnable类型的任务
        executorService.submit(() -> compute("MyFixedThreadPool1", GenData.PATH1));
        executorService.submit(() -> compute("MyFixedThreadPool2", GenData.PATH2));
        // 提交Callable类型的任务
        Future<Integer> future1 = executorService.submit(() -> compute("MyFixedThreadPool3", GenData.PATH1));
        Future<Integer> future2 = executorService.submit(() -> compute("MyFixedThreadPool4", GenData.PATH2));

        int total1 = future1.get();
        int total2 = future2.get();
        int result = total1 + total2;
        System.out.printf("main thread get %s total: %s\n", "MyThreadPool1", total1);
        System.out.printf("main thread get %s total: %s\n", "MyThreadPool2", total2);
        System.out.printf("main thread compute result: %s\n", result);

        // 线程池里没有新的任务提交, 便会shutdown
        executorService.shutdown();
        System.out.println("main thread end");
    }
}
