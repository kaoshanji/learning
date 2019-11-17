# Queue接口

A [`Queue`](https://docs.oracle.com/javase/8/docs/api/java/util/Queue.html)是用于在处理之前保持元素的集合。除基本`Collection`操作外，队列还提供额外的插入，删除和检查操作。该`Queue`界面如下。

```java
public interface Queue<E> extends Collection<E> {
    E element();
    boolean offer(E e);
    E peek();
    E poll();
    E remove();
}
```

每种`Queue`方法都有两种形式：（1）如果操作失败则抛出异常;（2）如果操作失败，则另一种返回特殊值（`null`或者`false`，取决于操作）。接口的常规结构 如下表所示。

| Type of Operation | Throws exception | Returns special value |
| ----------------- | ---------------- | --------------------- |
| Insert            | `add(e)`         | `offer(e)`            |
| Remove            | `remove()`       | `poll()`              |
| Examine           | `element()`      | `peek()`              |

队列通常（但不一定）以FIFO（先进先出）方式对元素进行排序。优先级队列除外，它们根据元素的值对元素进行[排序](order.html) - 有关详细信息，请参阅“ [对象排序”](order.html)部分。无论使用什么顺序，队列的头部是通过调用`remove`或删除的元素`poll`。在FIFO队列中，所有新元素都插入队列的尾部。其他类型的队列可能使用不同的放置规则。每个`Queue`实现都必须指定其排序属性。

这是可能的一个`Queue`实施限制，它拥有的元素的数目; 这样的队列被称为*有界*。有些`Queue`实现`java.util.concurrent`是有界的，但实现`java.util`不是。

该`add`方法`Queue`继承自`Collection`插入元素，除非它违反队列的容量限制，在这种情况下它会抛出`IllegalStateException`。该`offer`方法仅用于有界队列，不同之处`add`仅在于它表示无法通过返回插入元素`false`。

该`remove`和`poll`方法都删除并返回队列的头。确切地删除哪个元素是队列的排序策略的函数。在`remove`和`poll`只有当队列为空在他们的行为方式有所不同。在这种情况下，`remove`抛出`NoSuchElementException`，同时`poll`返回`null`。

该`element`和`peek`方法返回，但不移除，队列的头。他们从彼此完全相同的方式也不同`remove`和`poll`：如果队列为空，`element`抛出`NoSuchElementException`，而`peek`回报`null`。

`Queue`实现通常不允许插入`null`元素。该`LinkedList`实施被改进以实施`Queue`，是一个例外。由于历史原因，它允许`null`元素，但是你应该避免利用它，因为`null`它被`poll`和`peek`方法用作特殊的返回值。

队列实现通常不定义`equals`和`hashCode`方法的基于元素的版本，而是从中继承基于身份的版本`Object`。

该`Queue`接口并未定义阻塞队列的方法，这是在并行编程常见。等待元素出现或空间变得可用的这些方法在[`java.util.concurrent.BlockingQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html)扩展的接口中定义 `Queue`。

在以下示例程序中，队列用于实现倒数计时器。队列预先加载了从命令行上指定的数字到0的所有整数值，按降序排列。然后，从队列中删除值并以一秒的间隔打印。该程序是人为的，因为在不使用队列的情况下执行相同的操作会更自然，但它说明了在后续处理之前使用队列来存储元素。

```java
import java.util.*;

public class Countdown {
    public static void main(String[] args) throws InterruptedException {
        int time = Integer.parseInt(args[0]);
        Queue<Integer> queue = new LinkedList<Integer>();

        for (int i = time; i >= 0; i--)
            queue.add(i);

        while (!queue.isEmpty()) {
            System.out.println(queue.remove());
            Thread.sleep(1000);
        }
    }
}
```

在以下示例中，优先级队列用于对元素集合进行排序。同样，这个程序是人为的，因为没有理由使用它来支持所`sort`提供的方法`Collections`，但它说明了优先级队列的行为。

```java
static <E> List<E> heapSort(Collection<E> c) {
    Queue<E> queue = new PriorityQueue<E>(c);
    List<E> result = new ArrayList<E>();

    while (!queue.isEmpty())
        result.add(queue.remove());

    return result;
}
```

