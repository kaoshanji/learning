# Set实现

这些`Set`实现分为通用和专用实现。

## 通用集实现

有三种通用 [`Set`](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html)的实现- [`HashSet`](https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html)， [`TreeSet`](https://docs.oracle.com/javase/8/docs/api/java/util/TreeSet.html)和 [`LinkedHashSet`](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashSet.html)。使用这三种中的哪一种通常是直截了当的。`HashSet`比`TreeSet`大多数操作的恒定时间与对数时间快得多，但没有提供订购保证。如果需要在`SortedSet`界面中使用操作，或者需要按值进行迭代，请使用`TreeSet`; 否则，使用`HashSet`。这是一个公平的赌注，你最终会在`HashSet`大部分时间里使用。

`LinkedHashSet`在某种意义上介于`HashSet`和之间`TreeSet`。实现为具有贯穿它的链表的哈希表，它提供了*插入顺序的*迭代（最近最少插入到最近），并且运行速度几乎一样快`HashSet`。该`LinkedHashSet`实现使其客户免于提供的未指定的，通常是混乱的排序，`HashSet`而不会导致与之相关的增加的成本`TreeSet`。

值得记住的一件事`HashSet`是，迭代在条目数和桶数（*容量*）之和中是线性的。因此，选择太高的初始容量会浪费空间和时间。另一方面，选择一个太低的初始容量会在每次强制增加容量时复制数据结构，从而浪费时间。如果未指定初始容量，则默认值为16.过去，选择素数作为初始容量有一些优势。这不再是真的。在内部，容量总是四舍五入到2的幂。使用`int`构造函数指定初始容量。以下代码行分配`HashSet`其初始容量为64 的代码。

```java
Set<String> s = new HashSet<String>(64);
```

该`HashSet`班有叫另外一个调整参数*客座率*。如果您非常关心自己的空间消耗，请`HashSet`阅读`HashSet`文档以获取更多信息。否则，只接受默认值; 这几乎总是正确的事情。

如果您接受默认加载因子但想要指定初始容量，请选择一个大约是您希望该组增长的大小的两倍的数字。如果您的猜测偏远，您可能会浪费一些空间，时间或两者，但这不太可能是一个大问题。

`LinkedHashSet`具有相同的调整参数`HashSet`，但迭代时间不受容量影响。`TreeSet`没有调整参数。

## 专用集实现

有两个特殊用途的`Set`实现 - [`EnumSet`](https://docs.oracle.com/javase/8/docs/api/java/util/EnumSet.html)和 [`CopyOnWriteArraySet`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CopyOnWriteArraySet.html)。

`EnumSet`是`Set`枚举类型的高性能实现。枚举集的所有成员必须具有相同的枚举类型。在内部，它由位向量表示，通常是单个向量`long`。枚举在枚举类型的范围上设置支持迭代。例如，给定星期几的枚举声明，您可以迭代工作日。本`EnumSet`类提供了一个静态的工厂，可以很容易。

```java
 for (Day d : EnumSet.range(Day.MONDAY, Day.FRIDAY))
        System.out.println(d);
```

枚举集还为传统的位标志提供了丰富的，类型安全的替代品。

```java
 EnumSet.of(Style.BOLD, Style.ITALIC)
```

`CopyOnWriteArraySet`是一个`Set`由写时复制数组备份的实现。所有可变操作，如`add`，`set`，和`remove`，通过使所述阵列的一个新的复制来实现; 不需要锁定。甚至迭代也可以安全地与元素插入和删除同时进行。不像大多数`Set`实施中，`add`，`remove`，和`contains`方法需要的时间与集合的大小。此实现*仅*适用于很少修改但经常迭代的集合。它非常适合维护必须防止重复的事件处理程序列表。