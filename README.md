# java多线程的3种实现方式及线程池

# 1 准备数据

## 1.1 目标

为了形象地演示线程的工作现象, 准备两个文件`datas/odds.txt`和`datas/evens.txt`, 分别存储奇数和偶数, 内容如下:

odds.txt

```shell
1
3
5
7
9
11
13
15
17
19
```

evens.txt

```shell
2
4
6
8
10
12
14
16
18
20
```

## 1.2 实现思路

思路一:

1. 文件不多, 内容不多, 手动创建, 完全可以搞定
2. 选择此思路的同学可以跳过本章, 直接看后面线程的实现

思路二:

1. 写一个数据生成类`GenData`, 可以随意生成各种格式的数据
2. 数据生成类提供2个方法, 一个是写数据的方法`writeNums`, 另一个是读数据的方法`readNums`

## 1.3 源代码

GenData.java

```java
package org.zwx;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenData {
    public static final String PATH1 = "datas/odds.txt";
    public static final String PATH2 = "datas/evens.txt";

    public static void main(String[] args) throws IOException {
        GenData.writeNums();
    }

    // 写数据
    public static void writeNums() throws IOException {
        List<Integer> odds = new ArrayList<>();
        List<Integer> evens = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            if (i % 2 == 0) {
                evens.add(i);
            } else {
                odds.add(i);
            }
        }

        FileUtils.writeLines(new File(PATH1), odds);
        FileUtils.writeLines(new File(PATH2), evens);
    }

    // 读数据
    public static List<Integer> readNums(String path) {
        try {
            List<String> strings = FileUtils.readLines(new File(path), StandardCharsets.UTF_8);
            return strings.stream().map(Integer::parseInt).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}
```

## 1.4  分析

1. 该类提供了3个方法, 读写文件利用了`common-io`第三方库中的`FileUtils`
   1. `writeNums()`--把奇数和偶数分别写入文件`odds.txt`和`evens.txt`
   2. `readNums()`--读数据, 传入参数路径, 即偶数还是奇数
   3. `main()`--调用`writeNums()`写入数据

2. 如果不方便引入第三方库或不想用代码操作两个数据文件, 手动创建两个文件也可以且或许更快
   1. `datas/odds.txt`
   2. `datas/evens.txt`

#  2 继承Thread

## 2.1 目标

利用多个子线程并行计算文件`odds.txt`和`evens.txt`中数字的和

## 2.2 实现思路

1. 继承Thread类
2. 重写run方法

## 2.3 代码

```java
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
```

## 2.4 结果

```shell
main thread start ...
main thread end
MyThread2: 2
MyThread1: 1
MyThread2: 6
MyThread1: 4
MyThread2: 12
MyThread1: 9
MyThread2: 20
MyThread1: 16
MyThread2: 30
MyThread1: 25
MyThread1: 36
MyThread2: 42
MyThread2: 56
MyThread1: 49
MyThread2: 72
MyThread1: 64
MyThread2: 90
MyThread1: 81
MyThread2: 110
MyThread1: 100
MyThread2, total: 110
MyThread1, total: 100
```

## 2.5 分析

1. `MyThread`继承了`Thread`类, 并重写了`run()`方法
2. `run()`方法打印了每个数据, 并求和, 结束后打印
3. 从结果可以看出, 主线程没有阻塞等待子线程, 两个子线程相对于主线程独立运行, 且互相独立运行
4. 假如`MyThread`已经继承了另外的类, java类的单继承特性使之不能再继承`Thread`类, 又该怎么办?

# 3 实现Runnable

## 3.1 目标

利用多个子线程并行计算文件`odds.txt`和`evens.txt`中数字的和 (和第2章相同)

## 3.2 实现思路

1. 实现Runnable类
2. 重写run方法

## 3.3 代码

关于接口的实现, java目前已经提供了各种形式的写法

1. 接口实现版

```java
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
```

2. 匿名类版

```java
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
```

3. lambda表达式版

```java
package org.zwx;

import java.util.List;

public class MyRunnableLambda {
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
        new Thread(()->compute("MyRunnableLambda1", GenData.PATH1)).start();
        new Thread(()->compute("MyRunnableLambda2", GenData.PATH2)).start();
        System.out.println("main thread end");
    }
}
```

## 3.4 结果

```shell
main thread start ...
main thread end
MyRunnable1: 1
MyRunnable2: 2
MyRunnable1: 4
MyRunnable2: 6
MyRunnable2: 12
MyRunnable1: 9
MyRunnable2: 20
MyRunnable1: 16
MyRunnable2: 30
MyRunnable1: 25
MyRunnable1: 36
MyRunnable2: 42
MyRunnable1: 49
MyRunnable2: 56
MyRunnable1: 64
MyRunnable2: 72
MyRunnable1: 81
MyRunnable2: 90
MyRunnable1: 100
MyRunnable2: 110
MyRunnable1, total: 100
MyRunnable2, total: 110
```

## 3.5 分析

1. 通过实现`Runnable`同样实现了功能 (其实Thread本质也是实现了Runnable接口)
   1. `class Thread implements Runnable`
2. 与`Thread`实例的区别是, `Runnable`类型的实例没有启动的start方法
3. 需要创建一个Thread实例来启动
4. 当主线程需要获取子线程计算的结果时, 又该怎么办?

# 4 实现Callable

## 4.1 目标

利用多个子线程并行计算文件`odds.txt`和`evens.txt`中数字的和, 并汇总

## 4.2 实现思路

1. 实现Callable接口
2. 重写call()方法

## 4.3 代码

关于接口的实现, java目前已经提供了各种形式的写法

1. 接口实现版

```java
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
```

2. 匿名类实现版

```java
package org.zwx;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class MyCallableAnonymous {
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

    public static void main(String[] args) throws Exception {
        System.out.println("main thread start ...");

        FutureTask<Integer> task1 = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return compute("MyCallableAnonymous1", GenData.PATH1);
            }
        });
        FutureTask<Integer> task2 = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return compute("MyCallableAnonymous2", GenData.PATH2);
            }
        });

        new Thread(task1).start();
        new Thread(task2).start();

        int total1 = task1.get();
        int total2 = task2.get();
        int result = total1 + total2;
        System.out.printf("main thread get %s total: %s\n", "MyCallableAnonymous1", total1);
        System.out.printf("main thread get %s total: %s\n", "MyCallableAnonymous2", total2);
        System.out.printf("main thread compute result: %s\n", result);
        System.out.println("main thread end");
    }
}
```



3. lambda表达式实现版

```java
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
```

## 4.4 结果

```shell
main thread start ...
MyRunnable1: 1
MyRunnable2: 2
MyRunnable1: 4
MyRunnable2: 6
MyRunnable1: 9
MyRunnable2: 12
MyRunnable2: 20
MyRunnable1: 16
MyRunnable2: 30
MyRunnable1: 25
MyRunnable2: 42
MyRunnable1: 36
MyRunnable2: 56
MyRunnable1: 49
MyRunnable2: 72
MyRunnable1: 64
MyRunnable2: 90
MyRunnable1: 81
MyRunnable2: 110
MyRunnable1: 100
MyRunnable2, total: 110
MyRunnable1, total: 100
main thread get MyRunnable1 total: 100
main thread get MyRunnable2 total: 110
main thread compute result: 210
main thread end
```

## 4.5 分析

1. `Callable`是个泛型接口, 其中的泛型为线程的返回值类型

   * `interface Callable<V>`

2. `Callable`执行方法是带有返回值的`call()`, 不是`run()`

   * `V call() throws Exception`

3. `Thread`接收的参数为`Runnable`, 不接收`Callable`

   * `Thread(Runnable target)`

4. `FutureTask`接收的参数为`Callable`

   * `FutureTask(Callable<V> callable)`

5. `FutureTask`实现了`RunnableFuture`, `RunnableFuture`继承了`Runnable`和`Future`, 因此可以传入`Thread`

   * `class FutureTask<V> implements RunnableFuture<V>`

   * `interface RunnableFuture<V> extends Runnable, Future<V>`

6. `FutureTask`的`get()`方法会阻塞主线程, 等待子线程计算结束返回结果

7. 启动方式是不是有点太麻烦???

# 5 线程池

## 5.1 相关概念

1. 线程池: 许多创建好的线程
2. Task: 需要执行的任务, 例如本章中的compute方法
3. 线程池的作用
   1. 最大程度地利用线程, 减少创建和切换线程的额外开销
   2. 利用已有线程的多次循环执行多个任务从而提高系统的处理能力
   3. 方便管理线程的执行状态

4. `execute()`利用线程提交一个任务, 没有返回值
5. `sublimt()`利用线程提交一个任务, 有返回值

## 5.2 实现思路

1. 创建线程池
2. 定义任务函数
3. 提交任务

## 5.3 常见的线程池

java实现了许多不同策略的线程池, 以应对不同的应用场景

### 5.3.1 SingleThreadExecutor

英文释义

> Creates an Executor that uses a single worker thread operating off an unbounded queue. (Note however that if this single thread terminates due to a failure during execution prior to shutdown, a new one will take its place if needed to execute subsequent tasks.)  Tasks are guaranteed to execute sequentially, and no more than one task will be active at any given time. Unlike the otherwise equivalent {@code newFixedThreadPool(1)} the returned executor is guaranteed not to be reconfigurable to use additional threads.

中文释义

1. 创建一个单线程的线程池，适用于需要保证顺序执行各个任务

```java
package org.zwx;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MySingleThreadExecutor {
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
        // 创建一个线程, 执行4个task
        ExecutorService executorService = Executors.newSingleThreadExecutor();

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
```

结果

```
main thread start ...
MySingleThreadExecutor1: 1
MySingleThreadExecutor1: 4
...
MySingleThreadExecutor1, total: 100
MySingleThreadExecutor2: 2
MySingleThreadExecutor2: 6
...
MySingleThreadExecutor2: 110
MySingleThreadExecutor2, total: 110
MySingleThreadExecutor3: 1
MySingleThreadExecutor3: 4
...
MySingleThreadExecutor3: 100
MySingleThreadExecutor3, total: 100
MySingleThreadExecutor4: 2
MySingleThreadExecutor4: 6
...
MySingleThreadExecutor4: 110
MySingleThreadExecutor4, total: 110
main thread get MySingleThreadExecutor3 total: 100
main thread get MySingleThreadExecutor4 total: 110
main thread compute result: 210
main thread end
```

### 5.3.2 FixedThreadPool

英文释义:

> Creates a thread pool that reuses a fixed number of threads operating off a shared unbounded queue.  At any point, at most {nThreads} threads will be active processing tasks. If additional tasks are submitted when all threads are active, they will wait in the queue until a thread is available. If any thread terminates due to a failure during execution prior to shutdown, a new one will take its place if needed to execute subsequent tasks.  The threads in the pool will exist until it is explicitly {ExecutorService#shutdown shutdown}.

中文释义:

1. 创建一个固定大小的线程池，因为采用无界的阻塞队列，所以实际线程数量永远不会变化，适用于负载较重的场景，对当前线程数量进行限制。

2. 在任何时候，最多`nThreads`个线程处在活动状态处理任务。

3. 如果没有空闲线程时提交了新任务，则新任务将在队列中等待，直到某个线程可用为止。 

4. 如果在关闭之前执行过程中由于执行失败导致任何线程终止，则在执行后续任务时将使用新线程代替, 即线程数总是维持在`nThreads`个。
5. 关闭线程池, 需要调用shutdown方法, 否则线程池将会一直活动。

```java
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

    public static void main(String[] args) throws Exception {
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
```

结果

```
main thread start ...
MyFixedThreadPool1: 1
MyFixedThreadPool3: 1
MyFixedThreadPool4: 2
MyFixedThreadPool2: 2
...
MyFixedThreadPool3: 100
MyFixedThreadPool1: 100
MyFixedThreadPool4: 110
MyFixedThreadPool2: 110
MyFixedThreadPool1, total: 100
MyFixedThreadPool2, total: 110
MyFixedThreadPool3, total: 100
MyFixedThreadPool4, total: 110
main thread get MyThreadPool1 total: 100
main thread get MyThreadPool2 total: 110
main thread compute result: 210
main thread end
```

### 5.3.3 CachedThreadPool

英文释义

> Creates a thread pool that creates new threads as needed, but will reuse previously constructed threads when they are available.  These pools will typically improve the performance of programs that execute many short-lived asynchronous tasks. Calls to {@code execute} will reuse previously constructed threads if available. If no existing thread is available, a new thread will be created and added to the pool. Threads that have not been used for sixty seconds are terminated and removed from the cache. Thus, a pool that remains idle for long enough will not consume any resources. Note that pools with similar properties but different details (for example, timeout parameters) may be created using {@link ThreadPoolExecutor} constructors.

中文释义

1. 创建一个线程池，如果有空闲的线程, 直接重用; 否则, 创建新线程, 个数无上限
2. 如果一个线程连续空闲达到60秒将从缓存线程池中删除

```java
package org.zwx;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        // 创建一个线程, 执行4个task
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
```

结果与5.3.2相似

### 5.3.4 ScheduledThreadPool

英文释义

> Creates a thread pool that can schedule commands to run after a given delay, or to execute periodically. @param corePoolSize the number of threads to keep in the pool, even if they are idle @return a newly created scheduled thread pool @throws IllegalArgumentException if {@code corePoolSize < 0}

中文释义

1. 创建一个线程池，该线程池可以安排命令在给定的延迟后运行或定期执行。 

```java
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
```

# 参考

1. [java 线程池机制的原理是什么？](https://zhidao.baidu.com/question/473492095.html)
