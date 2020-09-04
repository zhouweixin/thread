package org.zwx;

import java.util.List;

public class MyRunnable implements Runnable {

    private String name;
    private String path;

    public MyRunnable(String name, String path) {
        this.name = name;
        this.path = path;
    }

    @Override
    public void run() {
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
        MyRunnable myRunnable1 = new MyRunnable("MyRunnable1", GenData.PATH1);
        MyRunnable myRunnable2 = new MyRunnable("MyRunnable2", GenData.PATH2);

        new Thread(myRunnable1).start();
        new Thread(myRunnable2).start();
        System.out.println("main thread end");
    }
}
