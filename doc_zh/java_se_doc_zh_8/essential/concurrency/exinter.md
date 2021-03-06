#   执行器接口

该`java.util.concurrent`包定义了三个执行器接口：

- `Executor`，一个支持启动新任务的简单界面。
- `ExecutorService`，它的子接口`Executor`，增加了有助于管理生命周期的功能，包括单个任务和执行程序本身。
- `ScheduledExecutorService`，子接口`ExecutorService`，支持未来和/或定期执行任务。

通常，引用执行程序对象的变量被声明为这三种接口类型之一，而不是执行程序类类型。

## 该`Executor`接口

该 [`Executor`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executor.html)接口提供了一种方法，`execute`旨在成为常见的线程创建习惯用语的替代品。如果`r`是`Runnable`对象，`e`则`Executor`可以替换为对象

```java
(new Thread(r)).start();
```

同

```java
e.execute(r);
```

但是，定义`execute`不太具体。低级习语创建一个新线程并立即启动它。根据`Executor`实现，`execute`可能会执行相同的操作，但更有可能使用现有的工作线程来运行`r`，或者放入`r`队列以等待工作线程变为可用。（我们将在[线程池](pools.html)的部分中描述工作线程。）

执行程序实现`java.util.concurrent`旨在充分利用更高级`ExecutorService`和`ScheduledExecutorService`接口，尽管它们也可以与基本`Executor`接口一起使用。

## 该`ExecutorService`接口

该 [`ExecutorService`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)接口补充剂`execute`具有相似的，但更通用的`submit`方法。喜欢`execute`，`submit`接受`Runnable`对象，但也接受 [`Callable`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html)允许任务返回值的对象。该`submit`方法返回一个 [`Future`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html)对象，该对象用于检索`Callable`返回值并管理两者`Callable`和`Runnable`任务的状态。

`ExecutorService`还提供了提交大量`Callable`对象的方法。最后，`ExecutorService`提供了许多用于管理执行程序关闭的方法。为了支持立即关闭，任务应该正确处理[中断](interrupt.html)。

## 该`ScheduledExecutorService`接口

该 [`ScheduledExecutorService`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledExecutorService.html)接口补充其父的方法`ExecutorService`有`schedule`，其执行`Runnable`或`Callable`在指定的延迟后的任务。此外，接口定义`scheduleAtFixedRate`并`scheduleWithFixedDelay`以规定的间隔重复执行指定的任务。