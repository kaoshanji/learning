# 线程干扰

考虑一个叫做的简单类 [`Counter`](examples/Counter.java)



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

`Counter`的设计是为了每次调用`increment`都会加1 `c`，每次调用`decrement`都会从中减去1 `c`。但是，如果`Counter`从多个线程引用对象，则线程之间的干扰可能会阻止这种情况按预期发生。

当两种操作，在不同的线程运行，但作用于同一数据的干扰情况，*交错*。这意味着这两个操作由多个步骤组成，并且步骤序列重叠。

`Counter`对于交错实例的操作似乎不太可能，因为两个操作`c`都是单个简单的语句。但是，即使是简单的语句也可以由虚拟机转换为多个步骤。我们不会检查虚拟机采取的具体步骤 - 足以知道单个表达式`c++`可以分解为三个步骤：

1. 检索当前值`c`。
2. 将检索的值增加1。
3. 将递增的值存储回来`c`。

表达式`c--`可以以相同的方式分解，除了第二步减少而不是增量。

假设线程A `increment`在线程B调用的大约同一时间调用`decrement`。如果初始值为`c`is `0`，则它们的交错操作可能遵循以下顺序：

1. Thread A: Retrieve c.
2. Thread B: Retrieve c.
3. Thread A: Increment retrieved value; result is 1.
4. Thread B: Decrement retrieved value; result is -1.
5. Thread A: Store result in c; c is now 1.
6. Thread B: Store result in c; c is now -1.

线程A的结果丢失，被线程B覆盖。这种特殊的交错只是一种可能性。在不同情况下，可能是线程B的结果丢失，或者根本没有错误。因为它们是不可预测的，所以难以检测和修复线程干扰错误。