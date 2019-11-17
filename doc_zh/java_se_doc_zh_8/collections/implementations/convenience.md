# Convenience

本节描述了几种小型实现，当您不需要它们的全部功能时，它们比通用实现更方便，更高效。本节中的所有实现都是通过静态工厂方法而不是`public`类提供的。

## 列表的数组视图

该 [`Arrays.asList`](https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html#asList-T...-)方法返回`List`其数组参数的视图。更改`List`写入数组，反之亦然。集合的大小是数组的大小，不能更改。如果在`add`或`remove`上调用方法`List`，`UnsupportedOperationException`则会产生结果。

此实现的正常使用是作为基于阵列和基于集合的API之间的桥梁。它允许您将数组传递给期望a `Collection`或a 的方法`List`。但是，这种实现还有另一种用途。如果您需要固定大小`List`，它比任何通用`List`实现更有效。这是成语。

```java
List<String> list = Arrays.asList(new String[size]);
```

请注意，不保留对后备阵列的引用。

## 不可变的多重复制列表

偶尔你需要一个`List`由同一元素的多个副本组成的不可变元素。该 [`Collections.nCopies`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#nCopies-int-T-)方法返回这样的列表。该实现有两个主要用途。第一个是初始化一个新创建的`List`; 例如，假设您希望`ArrayList`最初由1,000个`null`元素组成。以下咒语起作用。

```java
List<Type> list = new ArrayList<Type>(Collections.nCopies(1000, (Type)null);
```

当然，每个元素的初始值不必是`null`。第二个主要用途是发展现有的`List`。例如，假设您要将69个字符串副本添加`"fruit bat"`到a的末尾`List<String>`。目前尚不清楚你为什么要做这样的事情，但让我们假设你做了。以下是你如何做到的。

```java
lovablePets.addAll(Collections.nCopies(69, "fruit bat"));
```

通过使用`addAll`带有索引和a的形式`Collection`，您可以将新元素添加到a的中间`List`而不是结尾。

## 不可变单身集

有时你需要一个不可变的*单例* `Set`，它由一个指定的元素组成。该 [`Collections.singleton`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#singleton-T-)方法返回这样的`Set`。此实现的一个用途是从a中删除所有出现的指定元素`Collection`。

```java
c.removeAll(Collections.singleton(e));
```

相关的习语会从a中删除映射到指定值的所有元素`Map`。例如，假设你有一个`Map`- `job`将人们映射到他们的工作线上，并假设你想要消灭所有的律师。以下单行将做的事情。

```java
job.values().removeAll(Collections.singleton(LAWYER));
```

此实现的另一个用途是为编写为接受值集合的方法提供单个输入值。

## 空集，列表和地图常量

本 [`Collections`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html)类提供了返回空`Set`，`List`和`Map`- [`emptySet`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#emptySet--)， [`emptyList`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#emptyList--)和 [`emptyMap`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#emptyMap--)。这些常量的主要用途是`Collection`当您不想提供任何值时采用值的方法的输入，如本示例所示。

```java
tourist.declarePurchases(Collections.emptySet());
```

