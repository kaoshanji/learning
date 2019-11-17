# Deque实现

该`Deque`接口发音为*“deck”*，表示双端队列。该`Deque`接口可以实现为各种类型`Collections`。该`Deque`接口实现分为通用型和并发实现。

## 通用Deque实现

通用实现包括 `LinkedList`和`ArrayDeque`类。该`Deque`接口支持两端元素的插入，移除和检索。的 [`ArrayDeque`](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayDeque.html)类是的可调整大小的数组实现`Deque`接口，而 [`LinkedList`](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html)类是列表实现。

基本的插入，删除和retieval操作的`Deque`界面`addFirst`，`addLast`，`removeFirst`，`removeLast`，`getFirst`和`getLast`。该方法`addFirst`在头部`addLast`添加元素，而在`Deque`实例的尾部添加元素。

该`LinkedList`实施比更灵活`ArrayDeque`的实现。`LinkedList`实现所有可选列表操作。`null`在`LinkedList`实现中允许元素，但在`ArrayDeque`实现中不允许。

在效率方面，`ArrayDeque`比`LinkedList`两端的添加和删除操作更有效。实现中的最佳操作`LinkedList`是在迭代期间删除当前元素。`LinkedList`实现不是迭代的理想结构。

该`LinkedList`实施消耗比更多的内存`ArrayDeque`执行。对于`ArrayDeque`实例遍历，请使用以下任何一种方法：

### 的foreach

该`foreach`速度快，可用于各种列表。

```java
ArrayDeque<String> aDeque = new ArrayDeque<String>();

. . .
for (String str : aDeque) {
    System.out.println(str);
}
```

### 迭代器

它`Iterator`可以用于各种数据列表的前向遍历。

```java
ArrayDeque<String> aDeque = new ArrayDeque<String>();
. . .
for (Iterator<String> iter = aDeque.iterator(); iter.hasNext();  ) {
    System.out.println(iter.next());
}
```

本`ArrayDeque`类在本教程中用来实现`Deque`接口。本教程中使用的示例的完整代码可用于 [`ArrayDequeSample`](../interfaces/examples/ArrayDequeSample.java)。无论是 `LinkedList`和`ArrayDeque`类不支持多个线程的并发访问。

## 并发Deque实现

该 [`LinkedBlockingDeque`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/LinkedBlockingDeque.html)班是的并发执行`Deque`接口。如果双端队列为空，则方法如`takeFirst`并`takeLast`等待，直到元素变得可用，然后检索和删除相同的元件