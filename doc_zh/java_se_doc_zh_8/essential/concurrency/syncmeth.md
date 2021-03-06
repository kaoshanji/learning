# 同步方法

Java编程语言提供了两种基本的同步习惯用法：*synchronized方法*和*synchronized语句*。下两节将介绍两个同步语句中较为复杂的语句。本节介绍同步方法。

要使方法同步，只需将`synchronized`关键字添加到其声明：

```java
public class SynchronizedCounter {
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

如果`count`是实例`SynchronizedCounter`，则使这些方法同步有两个影响：

- 首先，对同一对象的两个同步方法的调用不可能进行交错。当一个线程正在为对象执行同步方法时，所有其他线程调用同一对象的同步方法（暂停执行）直到第一个线程完成对象。
- 其次，当同步方法退出时，它会自动与同一对象的同步方法的*任何后续调用*建立先发生关系。这可以保证对所有线程都可以看到对象状态的更改。

请注意，构造函数无法同步 - 使用`synchronized`带有构造函数的关键字是语法错误。同步构造函数没有意义，因为只有创建对象的线程在构造时才能访问它。

------

**警告：**  构造将在线程之间共享的对象时，要非常小心，对对象的引用不会过早“泄漏”。例如，假设您要维护一个包含每个类实例的调用。您可能想要将以下行添加到构造函数中：但是其他线程可以在构造对象完成之前用来访问对象。

同步方法支持一种简单的策略来防止线程干扰和内存一致性错误：如果一个对象对多个线程可见，则对该对象变量的所有读取或写入都是通过`synchronized`方法完成的。（一个重要的例外：`final`在构造对象之后无法修改的字段，一旦构造了对象，就可以通过非同步方法安全地读取）这种策略是有效的，但是可能会带来[活性](liveness.html)问题，正如我们将看到的那样本课后面的内容。