# 对象流

正如数据流支持原始数据类型的I / O一样，对象流也支持对象的I / O. 大多数（但不是全部）标准类支持其对象的序列化。那些确实实现标记接口 [`Serializable`](https://docs.oracle.com/javase/8/docs/api/java/io/Serializable.html)。

对象流类是 [`ObjectInputStream`](https://docs.oracle.com/javase/8/docs/api/java/io/ObjectInputStream.html)和 [`ObjectOutputStream`](https://docs.oracle.com/javase/8/docs/api/java/io/ObjectOutputStream.html)。这些类实现 [`ObjectInput`](https://docs.oracle.com/javase/8/docs/api/java/io/ObjectInput.html)和 [`ObjectOutput`](https://docs.oracle.com/javase/8/docs/api/java/io/ObjectOutput.html)，这是子接口`DataInput`和`DataOutput`。这意味着[数据流](datastreams.html)中涵盖的所有原始数据I / O方法也在对象流中实现。因此，对象流可以包含原始值和对象值的混合。这个 [`ObjectStreams`](examples/ObjectStreams.java)例子说明了这一点 `ObjectStreams`通过`DataStreams`一些更改创建相同的应用程序。首先，价格现在是 [`BigDecimal`](https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html)对象，以更好地表示小数值。其次，将[`Calendar`](https://docs.oracle.com/javase/8/docs/api/java/util/Calendar.html)对象写入数据文件，指示发票日期。

如果`readObject()`未返回预期的对象类型，则尝试将其强制转换为正确的类型可能会抛出一个 [`ClassNotFoundException`](https://docs.oracle.com/javase/8/docs/api/java/lang/ClassNotFoundException.html)。在这个简单的例子中，这不可能发生，因此我们不会尝试捕获异常。相反，我们通过添加`ClassNotFoundException`到`main`方法的`throws`子句来通知编译器我们已经意识到了这个问题。

## 复杂对象的输出和输入

该`writeObject`和`readObject`方法是使用简单，但它们包含了一些非常复杂的对象管理逻辑。这对像Calendar这样的类来说并不重要，它只封装了原始值。但是许多对象包含对其他对象的引用。如果`readObject`要从流重建对象，则必须能够重新构建原始对象所引用的所有对象。这些附加对象可能有自己的引用，依此类推。在这种情况下，`writeObject`遍历整个对象引用Web，并将该Web中的所有对象写入流。因此，单个调用`writeObject`可以导致将大量对象写入流。

这在下图中进行了演示，其中`writeObject`调用了写入名为**a**的单个对象。该对象包含对象**b**和**c的**引用，而**b**包含对**d**和**e的**引用。调用`writeobject(a)`写不只是**一个**，但都需要重建的对象**一个**也都写，所以在这个网络中的其他四个对象。当读回**a时，**也会回读`readObject`其他四个对象，并保留所有原始对象引用。

![多个引用对象的I / O.](images/io-trav.gif)



多个引用对象的I / O.

您可能想知道如果同一个流上的两个对象都包含对单个对象的引用会发生什么。当他们回读时，他们都会引用一个对象吗？答案是肯定的。流只能包含一个对象的副本，尽管它可以包含任意数量的对象。因此，如果您明确地将对象写入流两次，那么您实际上只编写了两次引用。例如，如果以下代码将对象写入`ob`两次流：

```java
Object ob = new Object();
out.writeObject(ob);
out.writeObject(ob);
```

每个`writeObject`都必须匹配一个`readObject`，所以读回流的代码将如下所示：

```java
Object ob1 = in.readObject();
Object ob2 = in.readObject();
```

这将导致两个变量，`ob1`并且`ob2`，这是一个单一的对象引用。

但是，如果将单个对象写入两个不同的流，则它实际上是重复的 - 读取两个流的单个程序将看到两个不同的对象。