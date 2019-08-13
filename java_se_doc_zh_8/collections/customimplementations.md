# 自定义实现

许多程序员永远不需要实现自己`Collection`的类。您可以使用本章前面部分中描述的实现进行相当远的操作。但是，有一天你可能想编写自己的实现。借助Java平台提供的抽象实现，这很容易做到这一点。在我们讨论*如何*编写实现之前，让我们讨论*一下*为什么要编写一个实现。

## 编写实施的原因

以下列表说明了`Collection`您可能希望实现的自定义类型。它并非详尽无遗：

- **持久性**：所有内置`Collection`实现都驻留在主内存中，并在程序退出时消失。如果您希望下次程序启动时仍然存在的集合，您可以通过在外部数据库上构建胶合代码来实现它。这样的集合可以由多个程序同时访问。
- **特定应用**：这是一个非常广泛的类别。一个例子是`Map`包含实时遥测数据的不可修改的。键可以表示位置，并且可以响应于`get`操作从这些位置处的传感器读取值。
- **高性能，专用**：许多数据结构利用受限制的使用来提供比通用实现更好的性能。例如，考虑`List`包含长期运行的相同元素值。在文本处理中经常出现的这种列表可以是*行程编码的* - 运行可以表示为包含重复元素和连续重复次数的单个对象。这个例子很有意思，因为它会影响性能的两个方面：它需要的空间更少，但时间比一个更多`ArrayList`。
- **高性能，通用**：Java Collections Framework的设计者试图为每个接口提供最佳的通用实现，但是可以使用许多很多数据结构，并且每天都会发明新的数据结构。也许你可以更快地拿出一些东西！
- **增强功能**：假设您需要一个有效的包实现（也称为*多重集*）：a `Collection`提供持续时间包含检查，同时允许重复元素。在a上实现这样的集合是相当简单的`HashMap`。
- **便利性**：您可能希望获得除Java平台提供的便利之外的其他实现。例如，您可能经常需要`List`表示连续范围的`Integer`s的实例。
- **适配器**：假设您使用的是具有自己的ad hoc集合API的旧API。您可以编写一个适配器实现，允许这些集合在Java Collections Framework中运行。一个*适配器实现*是很薄的木皮，它包装一个类型的对象，使他们通过在后一种类型转换操作到对前操作的行为像其他类型的对象。

## 如何编写自定义实现

编写自定义实现非常简单。Java Collections Framework提供了明确设计的抽象实现，以方便自定义实现。我们将从下面的实现示例开始 [`Arrays.asList`](https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html#asList-T...-)。

```java
public static <T> List<T> asList(T[] a) {
    return new MyArrayList<T>(a);
}

private static class MyArrayList<T> extends AbstractList<T> {

    private final T[] a;

    MyArrayList(T[] array) {
        a = array;
    }

    public T get(int index) {
        return a[index];
    }

    public T set(int index, T element) {
        T oldValue = a[index];
        a[index] = element;
        return oldValue;
    }

    public int size() {
        return a.length;
    }
}
```

信不信由你，这非常接近于包含的实现`java.util.Arrays`。就这么简单！你提供一个构造函数和`get`，`set`和`size`方法，并`AbstractList`做了所有的休息。您可以免费获得`ListIterator`批量操作，搜索操作，哈希代码计算，比较和字符串表示。

假设您希望实现更快一点。抽象实现的API文档精确描述了每个方法的实现方式，因此您将知道要覆盖哪些方法以获得所需的性能。前面的实现的性能很好，但可以稍微改进一下。特别是，该`toArray`方法迭代`List`，一次复制一个元素。鉴于内部表示，克隆数组的速度更快，更明智。

```java
public Object[] toArray() {
    return (Object[]) a.clone();
}
```

通过添加此覆盖以及更多类似的覆盖，此实现正是在其中找到的`java.util.Arrays`。为了完全公开，使用其他抽象实现有点困难，因为你必须编写自己的迭代器，但它仍然不是那么困难。

以下列表总结了抽象实现：

- [`AbstractCollection`](https://docs.oracle.com/javase/8/docs/api/java/util/AbstractCollection.html)- `Collection`既不是a `Set`也不是a `List`。至少，您必须提供`iterator`和`size`方法。
- [`AbstractSet`](https://docs.oracle.com/javase/8/docs/api/java/util/AbstractSet.html)- a `Set`; 使用与...相同`AbstractCollection`。
- [`AbstractList`](https://docs.oracle.com/javase/8/docs/api/java/util/AbstractList.html)- `List`由随机访问数据存储（例如阵列）备份。至少，必须提供`positional access`的方法（`get`和，可选地，`set`，`remove`，和`add`）和`size`方法。抽象类负责`listIterator`（和`iterator`）。
- [`AbstractSequentialList`](https://docs.oracle.com/javase/8/docs/api/java/util/AbstractSequentialList.html)- `List`由顺序访问数据存储（例如链表）备份。至少，您必须提供`listIterator`和`size`方法。抽象类负责位置访问方法。（这与之相反`AbstractList`。）
- [`AbstractQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/AbstractQueue.html)-至少，你必须提供`offer`，`peek`，`poll`和`size`方法以及`iterator`支撑`remove`。
- [`AbstractMap`](https://docs.oracle.com/javase/8/docs/api/java/util/AbstractMap.html)- a `Map`。您至少必须提供`entrySet`视图。这通常是在`AbstractSet`课堂上实现的。如果`Map`可以修改，则还必须提供该`put`方法。

编写自定义实现的过程如下：

1. 从前面的列表中选择适当的抽象实现类。
2. 为类的所有抽象方法提供实现。如果您的自定义集合是可修改的，则还必须覆盖一个或多个具体方法。抽象实现类的API文档将告诉您要覆盖哪些方法。
3. 测试并在必要时调试实现。您现在有一个可用的自定义集合实现。
4. 如果您担心性能，请阅读您继承其实现的所有方法的抽象实现类的API文档。如果有任何看起来太慢，请覆盖它们。如果覆盖任何方法，请确保在覆盖之前和之后测量方法的性能。您在调整性能方面付出的努力应该取决于实现将获得多少使用以及对其使用性能的关键程度。（通常最好省略此步骤。）