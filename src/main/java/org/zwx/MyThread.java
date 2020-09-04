package org.zwx;

import java.util.List;

public class MyThread extends Thread {
    private String name;
    private String path;

    public MyThread(String name, String path) {
        this.name = name;
        this.path = path;
    }

    @Override
    public void run() {
        int total = 0;

        List<Integer> values = GenData.readNums(path);
        for (Integer value : values) {
            total += value;
            System.out.printf("%s: %d\n",  name, total);

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
        MyThread myThread1 = new MyThread("MyThread1", GenData.PATH1);
        MyThread myThread2 = new MyThread("MyThread2", GenData.PATH2);
        myThread1.start();
        myThread2.start();
        System.out.println("main thread end");
    }
}