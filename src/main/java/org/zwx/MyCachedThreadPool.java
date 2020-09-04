package org.zwx;

import java.util.List;
import java.util.concurrent.*;

public class MyCachedThreadPool {
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

        // 有线程池的概念后, Runnable或Callable的一个实例就相当于一个task
        ExecutorService executorService = Executors.newCachedThreadPool();

        // 提交Runnable类型的任务
        executorService.submit(() -> compute("MySingleThreadExecutor1", GenData.PATH1));
        executorService.submit(() -> compute("MySingleThreadExecutor2", GenData.PATH2));
        // 提交Callable类型的任务
        Future<Integer> future1 = executorService.submit(() -> compute("MySingleThreadExecutor3", GenData.PATH1));
        Future<Integer> future2 = executorService.submit(() -> compute("MySingleThreadExecutor4", GenData.PATH2));

        int total1 = future1.get();
        int total2 = future2.get();
        int result = total1 + total2;
        System.out.printf("main thread get %s total: %s\n", "MySingleThreadExecutor3", total1);
        System.out.printf("main thread get %s total: %s\n", "MySingleThreadExecutor4", total2);
        System.out.printf("main thread compute result: %s\n", result);

        // 线程池里没有新的任务提交, 便会shutdown
        executorService.shutdown();
        System.out.println("main thread end");
    }
}
