package org.zwx;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class MyCallable implements Callable<Integer> {

    private String name;
    private String path;

    public MyCallable(String name, String path) {
        this.name = name;
        this.path = path;
    }

    @Override
    public Integer call() {
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

    public static void main(String[] args) throws Exception {
        System.out.println("main thread start ...");

        // 此处启动线程的方法比较麻烦, 后面会介绍简单的方法
        // Callable -> FutureTask -> Thread
        MyCallable myRunnable1 = new MyCallable("MyCallable1", GenData.PATH1);
        MyCallable myRunnable2 = new MyCallable("MyCallable2", GenData.PATH2);

        FutureTask<Integer> task1 = new FutureTask<>(myRunnable1);
        FutureTask<Integer> task2 = new FutureTask<>(myRunnable2);

        new Thread(task1).start();
        new Thread(task2).start();

        int total1 = task1.get();
        int total2 = task2.get();
        int result = total1 + total2;
        System.out.printf("main thread get %s total: %s\n", "MyCallable1", total1);
        System.out.printf("main thread get %s total: %s\n", "MyCallable2", total2);
        System.out.printf("main thread compute result: %s\n", result);
        System.out.println("main thread end");
    }
}
