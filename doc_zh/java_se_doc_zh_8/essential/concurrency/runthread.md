# 定义和启动线程

创建实例的应用程序`Thread`必须提供将在该线程中运行的代码。有两种方法可以做到这一点：

- *提供一个Runnable对象。*该 [`Runnable`](https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html)接口定义了一个方法，`run`，意在包含在线程执行的代码。该`Runnable`对象被传递给`Thread`构造函数，如 [`HelloRunnable`](examples/HelloRunnable.java)示例中所示：

```java
public class HelloRunnable implements Runnable {

    public void run() {
        System.out.println("Hello from a thread!");
    }

    public static void main(String args[]) {
        (new Thread(new HelloRunnable())).start();
    }

}
```

- 子类Thread。在Thread类本身实现了Runnable，虽然它的run方法不起作用。应用程序可以子类化Thread，提供自己的实现run，如 HelloThread示例中所示：

```java
public class HelloThread extends Thread {

    public void run() {
        System.out.println("Hello from a thread!");
    }

    public static void main(String args[]) {
        (new HelloThread()).start();
    }

}
```

请注意，两个示例都会调用`Thread.start`以启动新线程。

你应该使用哪些成语？第一个使用`Runnable`对象的习语更为通用，因为该`Runnable`对象可以继承一个类以外的类`Thread`。第二个习惯用法在简单的应用程序中更容易使用，但受到任务类必须是其后代的限制`Thread`。本课重点介绍第一种方法，该方法将`Runnable`任务与`Thread`执行任务的对象分开。这种方法不仅更灵活，而且适用于后面介绍的高级线程管理API。

本`Thread`类定义了大量的线程管理有用的方法。这些`static`方法包括提供有关调用方法的线程的信息或影响其状态的方法。从管理线程和`Thread`对象所涉及的其他线程调用其他方法。我们将在以下部分中研究其中一些方法。