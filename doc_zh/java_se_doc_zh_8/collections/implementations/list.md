# List实现

`List` 实现分为通用和专用实现。

## 通用列表实现

有两个通用 [`List`](https://docs.oracle.com/javase/8/docs/api/java/util/List.html)实现 - [`ArrayList`](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html)和 [`LinkedList`](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html)。大多数情况下，您可能会使用`ArrayList`，它提供恒定时间位置访问，并且速度非常快。它不必为每个元素分配一个节点对象`List`，它可以利用`System.arraycopy`何时必须同时移动多个元素。想想`ArrayList`作为`Vector`没有同步开销。

如果您经常在元素的开头添加元素`List`或迭代`List`从内部删除元素，则应考虑使用`LinkedList`。这些操作需要a `LinkedList`和a的线性时间的恒定时间`ArrayList`。但是你的性能要付出很大的代价。位置访问需要a中的线性时间和a中的`LinkedList`恒定时间`ArrayList`。此外，常数因素`LinkedList`更糟糕。如果您认为要使用a `LinkedList`，请在做出选择之前`LinkedList`和`ArrayList`之前衡量应用程序的性能; `ArrayList`通常更快。

`ArrayList`有一个调整参数 - *初始容量*，它指的是`ArrayList`在必须增长之前可以容纳的元素数量。`LinkedList`没有调整参数和七个可选操作，其中一个是`clone`。另外六名`addFirst`，`getFirst`，`removeFirst`，`addLast`，`getLast`，和`removeLast`。`LinkedList`还实现了`Queue`接口。

## 专用列表实现

[`CopyOnWriteArrayList`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CopyOnWriteArrayList.html)是一个`List`由写时复制数组备份的实现。这种实现在性质上类似于`CopyOnWriteArraySet`。即使在迭代期间也不需要同步，并且保证迭代器永远不会抛出`ConcurrentModificationException`。此实现非常适合维护事件处理程序列表，其中更改很少发生，并且遍历频繁且可能耗时。

如果您需要同步，则a `Vector`会比`ArrayList`同步更快`Collections.synchronizedList`。但是`Vector`有大量遗留操作，所以要小心总是`Vector`使用`List`接口操作，否则你将无法在以后替换实现。

如果您的`List`规模是固定的 - 也就是说，您永远不会使用`remove`，`add`或者除了以外的任何批量操作`containsAll`- 您还有第三个选项，绝对值得考虑。有关详细信息，请参阅`Arrays.asList`“ [便捷实施”](convenience.html)部分。