# 内存一致性错误

当不同的线程具有应该是相同数据的不一致视图时，会发生*内存一致性错误*。内存一致性错误的原因很复杂，超出了本教程的范围。幸运的是，程序员不需要详细了解这些原因。所需要的只是避免它们的策略。

避免内存一致性错误的关键是理解*之前发生的*关系。这种关系只是保证一个特定语句的内存写入对另一个特定语句可见。要查看此内容，请考虑以下示例。假设`int`定义并初始化了一个简单字段：

```java
int counter = 0;
```

该`counter`字段在两个线程A和B之间共享。假设线程A递增`counter`：

```java
counter++;
```

然后，不久之后，线程B打印出来`counter`：

```java
System.out.println(counter);
```

如果两个语句已在同一个线程中执行，则可以安全地假设打印出的值为“1”。但是如果这两个语句是在不同的线程中执行的，那么打印出来的值可能是“0”，因为不能保证线程A的更改`counter`对于线程B是可见的 - 除非程序员已经在这些之间建立了先发生关系。两个陈述。

有几种行为可以创造先发生过的关系。其中之一是同步，我们将在以下部分中看到。

我们已经看到了两种创造前发生关系的行为。

- 当一个语句调用时`Thread.start`，与该语句有一个before-before关系的每个语句也与新线程执行的每个语句都有一个before-before关系。新线程可以看到导致创建新线程的代码的影响。
- 当一个线程终止并导致`Thread.join`另一个线程返回时，终止线程执行的所有语句与成功连接后的所有语句都有一个before-before关系。现在，执行连接的线程可以看到线程中代码的效果。

有关创建先发生关系的操作列表，请参阅[包](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#MemoryVisibility)的“ [摘要”页面`java.util.concurrent`。](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#MemoryVisibility)。