# SortedSet接口

A [`SortedSet`](https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html)是 [`Set`](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html)按升序维护其元素的元素，根据元素的自然顺序或根据创建时`Comparator`提供的顺序进行排序`SortedSet`。除正常`Set`操作外，该`SortedSet`接口还提供以下操作：

- `Range view` - 允许对已排序集进行任意范围操作
- `Endpoints` - 返回有序集合中的第一个或最后一个元素
- `Comparator access`- 返回`Comparator`用于对集合进行排序的（如果有）

`SortedSet`接口的代码如下。

```java
public interface SortedSet<E> extends Set<E> {
    // Range-view
    SortedSet<E> subSet(E fromElement, E toElement);
    SortedSet<E> headSet(E toElement);
    SortedSet<E> tailSet(E fromElement);

    // Endpoints
    E first();
    E last();

    // Comparator access
    Comparator<? super E> comparator();
}
```

## 设置操作

`SortedSet`继承的操作`Set`在排序集和普通集上的行为相同，但有两个例外：

- 在`Iterator`由返回`iterator`以便在操作遍历有序集合。
- 返回的数组`toArray`按顺序包含有序集的元素。

虽然接口不保证它，但`toString`Java平台`SortedSet`实现的方法按顺序返回包含有序集的所有元素的字符串。

## 标准构造者

按照惯例，所有通用`Collection`实现都提供了一个标准的转换构造函数，它采用了`Collection`; `SortedSet`实现也不例外。在`TreeSet`，此构造函数创建一个实例，根据其自然顺序对其元素进行排序。这可能是一个错误。最好动态检查以查看指定的集合是否是`SortedSet`实例，如果是，则`TreeSet`根据相同的标准（比较器或自然排序）对新的集合进行排序。因为`TreeSet`采取了它所做的方法，它还提供了一个构造函数，它接受`SortedSet`并返回一个新的`TreeSet`包含根据相同标准排序的相同元素。请注意，它是参数的编译时类型，而不是其运行时类型，它确定调用这两个构造函数中的哪一个（以及是否保留了排序条件）。

`SortedSet`按照惯例，实现还提供了一个构造函数，它接受`Comparator`并返回根据指定的类型排序的空集`Comparator`。如果`null`传递给此构造函数，它将返回一个集合，该集合根据其自然顺序对其元素进行排序。

## 范围视图操作

这些`range-view`操作有点类似于`List`界面提供的操作，但有一个很大的区别。即使直接修改了后备排序集，排序集的范围视图仍然有效。这是可行的，因为有序集的范围视图的端点是元素空间中的绝对点，而不是后备集合中的特定元素，如列表的情况。一个`range-view`有序集合的其实只是一个窗口到任何一组的部分在于元素空间的指定部分。`range-view`写回到后备排序集的更改，反之亦然。因此，`range-view`与`range-view`列表中的s 不同，可以长时间在有序集合上使用s 。

排序集提供三种`range-view`操作。第一个，`subSet`有两个端点，比如`subList`。而不是指数，终点是对象，并必须与在有序set中的元素，使用`Set`的`Comparator`还是它的元素，取其自然顺序`Set`使用命令本身。比如`subList`，范围是半开放的，包括其低端点但不包括高端点。

因此，下面的代码行告诉你如何与许多话`"doorbell"`和`"pickle"`，包括`"doorbell"`但不包括`"pickle"`，包含在一个`SortedSet`字符串叫`dictionary`：

```java
int count = dictionary.subSet("doorbell", "pickle").size();
```

以类似的方式，以下单行删除以字母开头的所有元素`f`

```java
dictionary.subSet("f", "g").clear();
```

类似的技巧可以用来打印一个表格，告诉你每个字母开头有多少个单词。

```java
for (char ch = 'a'; ch <= 'z'; ) {
    String from = String.valueOf(ch++);
    String to = String.valueOf(ch);
    System.out.println(from + ": " + dictionary.subSet(from, to).size());
}
```

假设您要查看包含其两个端点的*封闭间隔*，而不是打开的间隔。如果元素类型允许在元件空间中给定值的后继的计算中，仅仅要求`subSet`从`lowEndpoint`到`successor(highEndpoint)`。虽然它不是完全明显，字符串的继任者`s`中`String`的自然排序`s + "\0"`-那就是，`s`一个`null`字符追加。

因此，下面的一行告诉你之间多少字`"doorbell"`和`"pickle"`包括门铃*和*咸菜，包含在字典中。

```java
count = dictionary.subSet("doorbell", "pickle\0").size();
```

可以使用类似的技术来查看不包含端点的*开放间隔*。从开间隔视图`lowEndpoint`到`highEndpoint`是从半开区间`successor(lowEndpoint)`到`highEndpoint`。使用以下内容计算`"doorbell"`和之间的单词数`"pickle"`，不包括两者。

```java
count = dictionary.subSet("doorbell\0", "pickle").size();
```

该`SortedSet`接口包含两个`range-view`操作- `headSet`和`tailSet`，这两者采取单一的`Object`参数。前者返回支持的初始部分的视图`SortedSet`，直到但不包括指定的对象。后者返回背景的最后部分的视图`SortedSet`，从指定的对象开始并继续到背景的末尾`SortedSet`。因此，以下代码允许您将字典视为两个不相交`volumes`（`a-m`和`n-z`）。

```java
SortedSet<String> volume1 = dictionary.headSet("n");
SortedSet<String> volume2 = dictionary.tailSet("n");
```

## 端点操作

该`SortedSet`接口包含操作的有序集合返回第一个和最后一个元素，这并不奇怪叫`first`和`last`。除了明显的用途之外，还`last`允许针对`SortedSet`界面缺陷的变通方法。你想做的一件事`SortedSet`就是进入内部`Set`并向前或向后迭代。从内部向前走很容易：只需获得`tailSet`并迭代它。不幸的是，没有简单的方法可以倒退。

以下习语获得`o`的元素空间中的第一个元素小于指定的对象。

```java
Object predecessor = ss.headSet(o).last();
```

这是从排序集内部的一个点向后移动一个元素的好方法。它可以重复应用以向后迭代，但这是非常低效的，需要查找返回的每个元素。

## 比较器访问器

该`SortedSet`接口包含一个调用的访问器方法`comparator`，该方法返回`Comparator`用于对集合进行排序的访问器方法，或者`null`是否根据元素的*自然顺序*对集合进行排序。提供此方法以便可以将排序的集合复制到具有相同排序的新排序集合中。它由[前面](#constructor)`SortedSet`描述的构造函数使用。