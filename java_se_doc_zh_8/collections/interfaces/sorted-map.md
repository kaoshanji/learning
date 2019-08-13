# SortedMap接口

A [`SortedMap`](https://docs.oracle.com/javase/8/docs/api/java/util/SortedMap.html)是 [`Map`](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html)按升序维护其条目，根据键的自然顺序排序，或根据创建`Comparator`时提供的条目排序的`SortedMap`。自然排序和`Comparator`s在[对象排序](order.html)部分中讨论 。该`SortedMap`接口提供正常`Map`操作和以下操作：

- `Range view` - 对已排序的地图执行任意范围操作
- `Endpoints` - 返回有序映射中的第一个或最后一个键
- `Comparator access`- 返回`Comparator`用于对地图进行排序的（如果有）

以下界面是`Map`模拟的 [`SortedSet`](https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html)。

```java
public interface SortedMap<K, V> extends Map<K, V>{
    Comparator<? super K> comparator();
    SortedMap<K, V> subMap(K fromKey, K toKey);
    SortedMap<K, V> headMap(K toKey);
    SortedMap<K, V> tailMap(K fromKey);
    K firstKey();
    K lastKey();
}
```

## 地图操作

操作`SortedMap`继承`Map`行为相同的排序映射贴图和法线贴图有两个例外：

- 在`Iterator`由返回`iterator`任何的有序映射的操作`Collection`，以便意见遍历集合。
- `Collection`视图`toArray`操作返回的数组按顺序包含键，值或条目。

尽管它不是由接口保证的，所述`toString`的方法`Collection`中的所有Java平台的观点`SortedMap`实现返回包含视图的所有元素中，为了一个字符串。

## 标准构造者

按照惯例，所有通用`Map`实现都提供了一个标准的转换构造函数，它采用了`Map`; `SortedMap`实现也不例外。在`TreeMap`，此构造函数创建一个实例，根据其键的自然顺序对其条目进行排序。这可能是一个错误。最好动态检查以查看指定的`Map`实例是否为a `SortedMap`，如果是，则根据相同的标准（比较器或自然排序）对新映射进行排序。因为`TreeMap`采用了它所采用的方法，它还提供了一个构造函数，它接受`SortedMap`并返回一个`TreeMap`包含与给定相同映射的new`SortedMap`，按照相同的标准排序。请注意，它是参数的编译时类型，而不是其运行时类型，它确定是否`SortedMap`优先于普通`map`构造函数调用构造函数。

`SortedMap`按照惯例，实现还提供了一个构造函数，它接受`Comparator`并返回根据指定的类型排序的空映射`Comparator`。如果`null`传递给此构造函数，它将返回一个`Map`根据其键的自然顺序对其映射进行排序的方法。

## 与SortedSet的比较

因为这个接口是精确的`Map`模拟`SortedSet`，所以[The SortedSet Interface](sorted-set.html)部分中[的](sorted-set.html)所有习语和代码示例 `SortedMap`仅适用于简单的修改。