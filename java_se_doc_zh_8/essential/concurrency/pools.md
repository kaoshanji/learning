#   线程池

大多数执行程序实现都在`java.util.concurrent`使用*线程池*，它由*工作线程*组成。这种线程`Runnable`与`Callable`它执行的任务分开存在，通常用于执行多个任务。

使用工作线程可以最大限度地减少由于线程创建而产生 线程对象使用大量内存，而在大型应用程序中，分配和释放许多线程对象会产生大量的内存管理开销。

一种常见类型的线程池是*固定线程池*。这种类型的池始终具有指定数量的线程运行; 如果某个线程在仍在使用时以某种方式终止，它将自动替换为新线程。任务通过内部队列提交到池中，只要有多个活动任务而不是线程，该队列就会保存额外的任务。

固定线程池的一个重要优点是使用它的应用程序可以*优雅地降级*。要理解这一点，请考虑一个Web服务器应用程序，其中每个HTTP请求都由一个单独的线程处理。如果应用程序只是为每个新的HTTP请求创建一个新线程，并且系统接收的请求数超过它可以立即处理的数量，那么当所有这些线程的开销超过系统容量时，应用程序将突然停止响应*所有*请求。由于可以创建的线程数量有限制，应用程序不会像它们进入时那样快速地为HTTP请求提供服务，但它将在系统可以维持的时间内尽快为它们提供服务。

创建使用固定线程池的执行程序的一种简单方法是调用 [`newFixedThreadPool`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newFixedThreadPool-int-)工厂方法。 [`java.util.concurrent.Executors`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html)此类还提供以下工厂方法：

- 该 [`newCachedThreadPool`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newCachedThreadPool-int-)方法使用可扩展线程池创建执行程序。此执行程序适用于启动许多短期任务的应用程序。
- 该 [`newSingleThreadExecutor`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newSingleThreadExecutor-int-)方法创建一次执行单个任务的执行程序。
- 几种工厂方法是`ScheduledExecutorService`上述执行器的版本。

如果上述工厂方法提供的执行者都不满足您的需求，则构建实例 [`java.util.concurrent.ThreadPoolExecutor`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html)或 [`java.util.concurrent.ScheduledThreadPoolExecutor`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledThreadPoolExecutor.html)将为您提供其他选项。