package org.zwx;

import java.util.List;

public class MyRunnableAnonymous {

    public static void compute(String name, String path) {
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
    }

    public static void main(String[] args) {
        System.out.println("main thread start ...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 调用计算方法
                compute("MyRunnableAnonymous1", GenData.PATH1);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 调用计算方法
                compute("MyRunnableAnonymous2", GenData.PATH2);
            }
        }).start();

        System.out.println("main thread end");
    }
}
