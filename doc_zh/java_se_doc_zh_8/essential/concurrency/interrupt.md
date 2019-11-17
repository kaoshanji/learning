# 中断

一个*中断*是一个指示线程它应该终止它在做什么和做别的事情。由程序员决定线程如何响应中断，但线程终止是很常见的。这是本课程中强调的用法。

线程通过调用发送一个中断 [`interrupt`](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#interrupt--)的上`Thread`对象被中断的线程。为使中断机制正常工作，被中断的线程必须支持自己的中断。

## 支持中断

线程如何支持自己的中断？这取决于它目前正在做什么。如果线程经常调用抛出的方法`InterruptedException`，它只会`run`在捕获该异常后从方法返回。例如，假设示例中的中央消息循环位于线程对象`SleepMessages`的`run`方法中`Runnable`。然后可以按如下方式修改它以支持中断：

```java
for (int i = 0; i < importantInfo.length; i++) {
    // Pause for 4 seconds
    try {
        Thread.sleep(4000);
    } catch (InterruptedException e) {
        // We've been interrupted: no more messages.
        return;
    }
    // Print a message
    System.out.println(importantInfo[i]);
}
```

许多抛出的方法`InterruptedException`，例如`sleep`，设计用于取消当前操作并在收到中断时立即返回。

如果一个线程长时间没有调用抛出的方法`InterruptedException`怎么办？然后它必须定期调用`Thread.interrupted`，`true`如果收到中断则返回。例如：

```java
for (int i = 0; i < inputs.length; i++) {
    heavyCrunch(inputs[i]);
    if (Thread.interrupted()) {
        // We've been interrupted: no more crunching.
        return;
    }
}
```

在这个简单的例子中，代码只是测试中断并退出线程（如果已收到）。在更复杂的应用程序中，抛出一个更有意义`InterruptedException`：

```java
if (Thread.interrupted()) {
    throw new InterruptedException();
}
```

这允许中断处理代码集中在一个`catch`子句中。

## 中断状态标志

中断机制使用称为*中断状态*的内部标志来实现。调用`Thread.interrupt`设置此标志。当线程通过调用静态方法检查中断时，将`Thread.interrupted`清除中断状态。非静态`isInterrupted`方法（由一个线程用于查询另一个线程的中断状态）不会更改中断状态标志。

按照惯例，任何通过抛出`InterruptedException`清除中断状态而退出的方法。但是，通过另一个线程调用，总是可以立即再次设置中断状态`interrupt`。