package org.zwx;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MyCallableLambda {
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

        FutureTask<Integer> task1 = new FutureTask<>(()->compute("MyCallableLambda1", GenData.PATH1));
        FutureTask<Integer> task2 = new FutureTask<>(()->compute("MyCallableLambda2", GenData.PATH2));

        new Thread(task1).start();
        new Thread(task2).start();

        // 阻塞等待子线程计算返回
        int total1 = task1.get();
        int total2 = task2.get();
        int result = total1 + total2;

        System.out.printf("main thread get %s total: %s\n", "MyCallableLambda1", total1);
        System.out.printf("main thread get %s total: %s\n", "MyCallableLambda2", total2);
        System.out.printf("main thread compute result: %s\n", result);
        System.out.println("main thread end");
    }
}
