# Queue实现

这些`Queue`实现分为通用和并发实现。

## 通用队列实现

正如上一节中所提到的，`LinkedList`实现了`Queue`接口，用于提供先入先出（FIFO）队列操作`add`，`poll`等。

的 [`PriorityQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html)类是基于所述一个优先级队列*堆*的数据结构。此队列根据构造时指定的顺序对元素进行排序，这可以是元素的自然顺序或显式强加的排序`Comparator`。

队列检索操作- ，，`poll` 和-在队列的头访问的元素。*队列*的*头部*是指定排序的最小元素。如果多个元素被绑定为最小值，则头部是这些元素之一; 关系被任意打破。`remove``peek``element`

`PriorityQueue`并且它的迭代器实现了`Collection`和`Iterator`接口的所有可选方法。方法中提供的迭代器`iterator`不保证遍历`PriorityQueue`任何特定顺序的元素。对于有序遍历，请考虑使用`Arrays.sort(pq.toArray())`。

## 并发队列实现

该`java.util.concurrent`包包含一组同步的`Queue`接口和类。 [`BlockingQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html)扩展`Queue`时，在检索元素时等待队列变为非空的操作以及在存储元素时队列中可用的空间。此接口由以下类实现：

- [`LinkedBlockingQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/LinkedBlockingQueue.html) - 由链接节点支持的可选有界FIFO阻塞队列
- [`ArrayBlockingQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ArrayBlockingQueue.html) - 由数组支持的有界FIFO阻塞队列
- [`PriorityBlockingQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/PriorityBlockingQueue.html) - 由堆支持的无界阻塞优先级队列
- [`DelayQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/DelayQueue.html) - 由堆支持的基于时间的调度队列
- [`SynchronousQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/SynchronousQueue.html)- 使用该`BlockingQueue`接口的简单集合点机制

在JDK 7中， [`TransferQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TransferQueue.html)专门`BlockingQueue`用于向队列添加元素的代码可以选择等待（阻塞）另一个线程中的代码来检索元素。`TransferQueue`有一个实现：

- [`LinkedTransferQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/LinkedTransferQueue.html)- `TransferQueue`基于链接节点的无界限