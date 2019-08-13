# 集合接口

A [`Collection`](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html)表示一组称为其元素的对象。该`Collection`接口用于传递需要最大通用性的对象集合。例如，按照惯例，所有通用集合实现都有一个带`Collection`参数的构造函数。此构造函数（称为*转换构造函数*）初始化新集合以包含指定集合中的所有元素，无论给定集合的子接口或实现类型如何。换句话说，它允许您*转换*集合的类型。

例如，假设您有一个`Collection<String> c`，可能是a `List`，a `Set`或另一种`Collection`。这个习惯用法创建一个新的`ArrayList`（`List`接口的实现），最初包含所有元素`c`。

```java
List<String> list = new ArrayList<String>(c);
```

或者 - 如果您使用的是JDK 7或更高版本 - 您可以使用菱形运算符：

```java
List<String> list = new ArrayList<>(c);
```

该`Collection`接口包含执行基本操作，如方法`int size()`，`boolean isEmpty()`， `boolean contains(Object element)`，`boolean add(E element)`，`boolean remove(Object element)`，和 `Iterator<E> iterator()`。

它还包含在整个集合，如操作方法`boolean containsAll(Collection<?> c)`， `boolean addAll(Collection<? extends E> c)`， `boolean removeAll(Collection<?> c)`，`boolean retainAll(Collection<?> c)`，和 `void clear()`。

阵列操作的其他方法（例如，`Object[] toArray()`也`<T> T[] toArray(T[] a)`存在）。

在JDK 8和后，`Collection`接口也暴露的方法`Stream<E> stream()`和`Stream<E> parallelStream()`，从底层集合获得顺序的或并行的流。（有关使用流的更多信息，请参阅标题为[聚合操作](../../collections/streams/index.html)的课程 。）

由于`Collection`a `Collection`表示一组对象，因此界面会对您的期望产生影响。它有方法告诉你集合中有多少元素（`size`，`isEmpty`），检查给定对象是否在collection（`contains`）中的方法，从集合中添加和删除元素的方法（`add`，`remove`），以及提供集合（`iterator`）上的迭代器。

该`add`方法通常被定义为足以使得对于允许重复的集合以及不具有重复的集合有意义。它保证了`Collection`通话完成后，将包含指定的元素，并返回`true`如果`Collection`修改为调用的结果。类似地，该`remove`方法旨在从中删除指定元素的单个实例`Collection`，假设它包含要开始的元素，并且`true`如果`Collection`作为结果被修改则返回。

## 遍历集合

有三种遍历集合的方法：（1）使用集合操作（2）和`for-each`构造，（3）使用`Iterator`s。

### 聚合操作

在JDK 8及更高版本中，迭代集合的首选方法是获取流并对其执行聚合操作。聚合操作通常与lambda表达式结合使用，以使用较少的代码行使编程更具表现力。以下代码按顺序遍历一组形状并打印出红色对象：

```java
myShapesCollection.stream()
.filter(e -> e.getColor() == Color.RED)
.forEach(e -> System.out.println(e.getName()));
```

同样，您可以轻松地请求并行流，如果集合足够大并且您的计算机具有足够的核心，这可能是有意义的：

```java
myShapesCollection.parallelStream()
.filter(e -> e.getColor() == Color.RED)
.forEach(e -> System.out.println(e.getName()));
```

使用此API收集数据的方法有很多种。例如，您可能希望将a的元素转换`Collection`为`String`对象，然后将它们连接起来，用逗号分隔：

```java
 String joined = elements.stream()
    .map(Object::toString)
    .collect(Collectors.joining(", "));
```

或者总结一下所有员工的工资：

```java
int total = employees.stream()
.collect(Collectors.summingInt(Employee::getSalary)));
```

这些只是您可以使用流和聚合操作执行的一些示例。有关更多信息和示例，请参阅标题为“ [聚合操作”](../../collections/streams/index.html)的课程 。

Collections框架一直提供许多所谓的“批量操作”作为其API的一部分。这些措施包括在整个集合进行操作的方法，如`containsAll`，`addAll`，`removeAll`，等不要混淆在JDK 8的新骨料业务和现有的批量操作（之间的主要区别中引入的聚合操作的那些方法`containsAll`，`addAll`等等。 ）是旧版本都是*变异的*，这意味着它们都修改了底层集合。相反，新的集合操作*则没有* 修改基础集合。使用新的聚合操作和lambda表达式时，必须注意避免突变，以免将来引入问题，如果您的代码稍后从并行流运行。

### for-each Construct

该`for-each`构造允许您使用`for`循环简洁地遍历集合或数组- 请参阅 [The for Statement](../../java/nutsandbolts/for.html)。以下代码使用该`for-each`构造在单独的行上打印出集合的每个元素。

```java
for (Object o : collection)
    System.out.println(o);
```

### 迭代器

如果需要，An [`Iterator`](https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html)是一个对象，使您可以遍历集合并有选择地从集合中删除元素。你`Iterator`通过调用它的`iterator`方法得到一个集合。以下是`Iterator`界面。

```java
public interface Iterator<E> {
    boolean hasNext();
    E next();
    void remove(); //optional
}
```

该`hasNext`方法返回`true`如果迭代有多个元素，且`next`方法在迭代返回下一个元素。该`remove`方法删除`next`底层返回的最后一个元素`Collection`。`remove`每次调用时只能调用该方法一次，`next`如果违反此规则则抛出异常。

请注意，这`Iterator.remove`是在迭代期间修改集合的*唯一*安全方法; 如果在迭代进行过程中以任何其他方式修改基础集合，则行为未指定。

在需要时使用`Iterator`而不是`for-each`构造：

- 删除当前元素。该`for-each`构造隐藏了迭代器，因此您无法调用`remove`。因此，该`for-each`构造不可用于过滤。
- 并行迭代多个集合。

以下方法向您展示如何使用a `Iterator`来过滤任意对象`Collection`- 即遍历删除特定元素的集合。

```java
static void filter(Collection<?> c) {
    for (Iterator<?> it = c.iterator(); it.hasNext(); )
        if (!cond(it.next()))
            it.remove();
}
```

这段简单的代码是多态的，这意味着无论实现如何，它都适用于*任何* 代码`Collection`。此示例演示了使用Java Collections Framework编写多态算法是多么容易。

## 集合接口批量操作

*批量操作*对整个*操作*执行操作`Collection`。您可以使用基本操作实现这些速记操作，但在大多数情况下，此类实现效率较低。以下是批量操作：

- `containsAll`- `true`如果目标`Collection`包含指定的所有元素，则返回`Collection`。
- `addAll`- 将指定的所有元素添加`Collection`到目标`Collection`。
- `removeAll`- 从目标中删除`Collection`也包含在指定内容中的所有元素`Collection`。
- `retainAll`- 从目标中删除`Collection`所有*未*包含在指定内容中的元素`Collection`。也就是说，它仅保留目标`Collection`中也包含在指定内容中的那些元素`Collection`。
- `clear`- 从中删除所有元素`Collection`。

的`addAll`，`removeAll`和`retainAll`方法中的所有回`true`如果目标`Collection`是在执行操作的过程中改变。

作为批量操作的功率的一个简单的例子，考虑下面的成语以除去*所有*指定元素的情况下，`e`从一个`Collection`，`c`。

```
c.removeAll（Collections.singleton（E））;
```

更具体地说，假设您`null`要从a中删除所有元素`Collection`。

```
c.removeAll（Collections.singleton（空））;
```

这个习惯用法`Collections.singleton`，它是一个静态工厂方法，它返回一个`Set`只包含指定元素的不可变元素。

## 集合接口阵列操作

这些`toArray`方法是作为集合和旧API之间的桥梁提供的，这些API期望输入数组。数组操作允许将a的内容`Collection`转换为数组。没有参数的简单形式创建了一个新的数组`Object`。更复杂的形式允许调用者提供数组或选择输出数组的运行时类型。

例如，假设`c`是a `Collection`。以下代码段将内容转储`c`到新分配的数组中，`Object`该数组的长度与中的元素数相同`c`。

```java
Object[] a = c.toArray();
```

假设`c`已知只包含字符串（可能因为`c`类型`Collection<String>`）。以下代码段将内容转储`c`到新分配的数组中，`String`该数组的长度与中的元素数相同`c`。

```java
String[] a = c.toArray(new String[0]);
```