# Deque接口

通常发音为`deck`，deque是双端队列。双端队列是元素的线性集合，支持在两个端点处插入和移除元素。该`Deque`接口比两者更丰富的抽象数据类型`Stack`和`Queue`，因为它在同一时间同时实现堆栈和队列。的 [`Deque`](https://docs.oracle.com/javase/8/docs/api/java/util/Deque.html)界面，定义方法来访问在所述的两端的元件`Deque`的实例。提供了插入，移除和检查元素的方法。预定义的类喜欢 [`ArrayDeque`](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayDeque.html)并 [`LinkedList`](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html)实现`Deque`接口。

请注意，该`Deque`接口既可以用作后进先出堆栈，也可以用作先进先出队列。`Deque`界面中给出的方法分为三个部分：

## 插入

该`addfirst`和`offerFirst`方法插入在年初元素`Deque`实例。方法`addLast`和`offerLast`插入元素在`Deque`实例的末尾。当`Deque`实例的容量受到限制时，首选方法是`offerFirst`，`offerLast`因为`addFirst`如果它已满，可能无法抛出异常。

## 去掉

该`removeFirst`和`pollFirst`方法去除从年初的元素`Deque`实例。的`removeLast`和`pollLast`的方法除去从端部元件。方法`pollFirst` 并`pollLast`返回`null`如果`Deque`为空而方法 `removeFirst`并`removeLast`在`Deque`实例为空时抛出异常。

## 取回

方法`getFirst`并`peekFirst`检索`Deque`实例的第一个元素。这些方法不会从`Deque`实例中删除值。同样，方法`getLast` 和`peekLast`检索最后一个元素。这些方法`getFirst`和`getLast`是否抛出一个异常 `deque`情况下是空的，而方法`peekFirst`和`peekLast` 回报`NULL`。

下面列出了12种Deque元素的插入，移除和回溯方法：

| 操作类型 | 第一个元素（`Deque`实例的开头） | 最后一个元素（`Deque`实例结束） |
| -------- | ------------------------------- | ------------------------------- |
| **插入** | `addFirst(e)` `offerFirst(e)`   | `addLast(e)` `offerLast(e)`     |
| **去掉** | `removeFirst()` `pollFirst()`   | `removeLast()` `pollLast()`     |
| **检查** | `getFirst()` `peekFirst()`      | `getLast()` `peekLast()`        |

除了插入，删除和检查`Deque`实例的这些基本方法之外，该`Deque`接口还具有一些更预定义的方法。其中之一是`removeFirstOccurence`，如果指定元素存在于`Deque`实例中，则此方法将删除指定元素的第一个出现。如果元素不存在，则`Deque`实例保持不变。另一种类似的方法是`removeLastOccurence`; 此方法删除`Deque`实例中指定元素的最后一次出现。这些方法的返回类型是`boolean`，`true`如果元素存在于`Deque`实例中，它们将返回。