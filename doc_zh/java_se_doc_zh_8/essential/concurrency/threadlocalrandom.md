# 并发随机数

在JDK 7中， [`java.util.concurrent`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html)包括一个便利类， [`ThreadLocalRandom`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadLocalRandom.html)用于期望使用来自多个线程或`ForkJoinTask`s的随机数的应用程序。

对于并发访问，使用`ThreadLocalRandom`而不是`Math.random()`在较少的争用中获得结果，并最终获得更好的性能。

您需要做的就是调用`ThreadLocalRandom.current()`，然后调用其中一个方法来检索随机数。这是一个例子：

```java
int r = ThreadLocalRandom.current() .nextInt(4, 77);
```

