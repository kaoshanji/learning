# 原子变量

该 [`java.util.concurrent.atomic`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/package-summary.html)包定义了支持单个变量的原子操作的类。所有类都有`get`和`set`类似读取和写入`volatile`变量的方法。也就是说，a `set`与`get`同一变量上的任何后续关系具有先发生关系。原子`compareAndSet`方法也具有这些内存一致性功能，适用于整数原子变量的简单原子算法也是如此。

要查看如何使用此包，让我们返回到 [`Counter`](examples/Counter.java)我们最初用于演示线程干扰的类：

```java
class Counter {
    private int c = 0;

    public void increment() {
        c++;
    }

    public void decrement() {
        c--;
    }

    public int value() {
        return c;
    }

}
```

使一种方式`Counter`安全从线程干扰使其同步的方法，如 [`SynchronizedCounter`](examples/SynchronizedCounter.java)：

```java
class SynchronizedCounter {
    private int c = 0;

    public synchronized void increment() {
        c++;
    }

    public synchronized void decrement() {
        c--;
    }

    public synchronized int value() {
        return c;
    }

}
```

对于这个简单的类，同步是可接受的解决方案。但对于更复杂的类，我们可能希望避免不必要的同步对活动的影响。使用a替换`int`字段`AtomicInteger`允许我们在不诉诸同步的情况下防止线程干扰，如 [`AtomicCounter`](examples/AtomicCounter.java)：

```java
import java.util.concurrent.atomic.AtomicInteger;

class AtomicCounter {
    private AtomicInteger c = new AtomicInteger(0);

    public void increment() {
        c.incrementAndGet();
    }

    public void decrement() {
        c.decrementAndGet();
    }

    public int value() {
        return c.get();
    }

}
```

