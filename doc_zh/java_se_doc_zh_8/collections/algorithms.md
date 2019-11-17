# 算法

这里描述的*多态算法*是Java平台提供的可重用功能。所有这些都来自 [`Collections`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html)类，并且都采用静态方法的形式，其第一个参数是要在其上执行操作的集合。Java平台提供的绝大多数算法都在[`List`](https://docs.oracle.com/javase/8/docs/api/java/util/List.html)实例上运行，但其中一些算法 在任意 [`Collection`](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html)实例上运行。本节简要介绍以下算法：

- [排序](#sorting)
- [洗牌](#shuffling)
- [常规数据操作](#rdm)
- [搜索](#searching)
- [组成](#composition)
- [寻找极端价值观](#fev)

该`sort`算法重新排序a `List`，使得其元素根据排序关系按升序排列。提供了两种形式的操作。简单形式`List`根据其元素的*自然顺序*对其进行*排序和排序*。如果您不熟悉自然排序的概念，请阅读 [对象排序](../interfaces/order.html)部分。

该`sort`操作使用了*一种*快速稳定的略微优化的*合并排序*算法：

- **快速**：保证及时运行`n log(n)`并在几乎排序的列表上运行得更快。经验测试显示它与高度优化的快速排序一样快。快速排序通常被认为比合并排序更快但不稳定并且不保证`n log(n)`性能。
- **稳定**：它不会重新排序相同的元素。如果您对不同的属性重复排序相同的列表，这一点很重要。如果邮件程序的用户通过邮寄日期对收件箱进行排序，然后按发件人对其进行排序，则用户自然希望来自给定发件人的现在连续的邮件列表（仍）按邮件日期排序。仅当第二种排序稳定时才能保证这一点。

以下 [`trivial program`](examples/Sort.java)以字典（字母）顺序打印出其参数。

```java
import java.util.*;

public class Sort {
    public static void main(String[] args) {
        List<String> list = Arrays.asList(args);
        Collections.sort(list);
        System.out.println(list);
    }
}
```

我们来运行该程序。

```bash
% java Sort i walk the line
```

生成以下输出。

```bash
[i, line, the, walk]
```

该程序仅用于向您展示算法确实像它们看起来一样容易使用。

第二种形式除了`sort`a [`Comparator`](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)之外还用a `List`对元素进行排序`Comparator`。假设您想要以相反的大小顺序打印出我们之前示例中的anagram组 - 首先是最大的anagram组。下面的示例向您展示了如何借助方法的第二种形式实现此目的`sort`。

回想一下，anagram组`Map`以`List`实例的形式存储为a 中的值。通过修改后的打印代码迭代`Map`的值查看，把每`List`即通过最小尺寸测试成`List`的`List`第 然后代码`List`使用`Comparator`期望`List`实例对此进行排序，并实现反向大小排序。最后，代码遍历排序`List`，打印其元素（anagram组）。以下代码替换示例中`main`方法末尾的打印代码`Anagrams`。

```java
// Make a List of all anagram groups above size threshold.
List<List<String>> winners = new ArrayList<List<String>>();
for (List<String> l : m.values())
    if (l.size() >= minGroupSize)
        winners.add(l);

// Sort anagram groups according to size
Collections.sort(winners, new Comparator<List<String>>() {
    public int compare(List<String> o1, List<String> o2) {
        return o2.size() - o1.size();
    }});

// Print anagram groups.
for (List<String> l : winners)
    System.out.println(l.size() + ": " + l);
```

运行 [`the program`](examples/Anagrams2.java)在 [`same dictionary`](../interfaces/examples/dictionary.txt)如在 [Map接口](../interfaces/map.html)部分，具有相同的最小变位字组的大小（8），产生以下输出。

```bash
12: [apers, apres, asper, pares, parse, pears, prase,
       presa, rapes, reaps, spare, spear]
11: [alerts, alters, artels, estral, laster, ratels,
       salter, slater, staler, stelar, talers]
10: [least, setal, slate, stale, steal, stela, taels,
       tales, teals, tesla]
9: [estrin, inerts, insert, inters, niters, nitres,
       sinter, triens, trines]
9: [capers, crapes, escarp, pacers, parsec, recaps,
       scrape, secpar, spacer]
9: [palest, palets, pastel, petals, plates, pleats,
       septal, staple, tepals]
9: [anestri, antsier, nastier, ratines, retains, retinas,
       retsina, stainer, stearin]
8: [lapse, leaps, pales, peals, pleas, salep, sepal, spale]
8: [aspers, parses, passer, prases, repass, spares,
       sparse, spears]
8: [enters, nester, renest, rentes, resent, tenser,
       ternes,��treens]
8: [arles, earls, lares, laser, lears, rales, reals, seral]
8: [earings, erasing, gainers, reagins, regains, reginas,
       searing, seringa]
8: [peris, piers, pries, prise, ripes, speir, spier, spire]
8: [ates, east, eats, etas, sate, seat, seta, teas]
8: [carets, cartes, caster, caters, crates, reacts,
       recast,��traces]
```

## 洗牌

该`shuffle`算法的作用与此相反`sort`，破坏了可能存在于a中的任何顺序`List`。也就是说，该算法`List`基于来自随机源的输入重新排序，使得假设公平的随机源，所有可能的排列以相等的可能性发生。该算法在实现机会游戏时很有用。例如，它可以用于混洗代表甲板`List`的`Card`对象。此外，它对生成测试用例很有用。

此操作有两种形式：一种采用a `List`并使用默认的随机源，另一种需要调用者提供 [Random](https://docs.oracle.com/javase/8/docs/api/java/util/Random.html)对象以用作[随机](https://docs.oracle.com/javase/8/docs/api/java/util/Random.html)源。该算法的代码用作本[`List`节中](../interfaces/list.html#shuffle)的示例 。

## 常规数据操作

本`Collections`类提供了做日常的数据处理5个对比算法`List`对象，所有这些都是非常简单：

- `reverse`- 颠倒a中元素的顺序`List`。
- `fill`- `List`用指定的值覆盖a中的每个元素。此操作对于重新初始化a非常有用`List`。
- `copy`- 接受两个参数，一个目标`List`和一个源`List`，并将源的元素复制到目标中，覆盖其内容。目的地`List`必须至少与来源一样长。如果更长，则目标`List`中的其余元素不受影响。
- `swap`- 将元素交换到a中的指定位置`List`。
- `addAll`- 将所有指定的元素添加到a `Collection`。要添加的元素可以单独指定，也可以作为数组指定。



## 搜索

该`binarySearch`算法搜索已排序的指定元素`List`。该算法有两种形式。第一个采用a `List`和元素搜索（“搜索关键字”）。此表单假定`List`根据其元素的自然顺序按升序排序。第二种形式`Comparator`除了`List`搜索键和“搜索”键外，还假设`List`按照指定的顺序按升序排序`Comparator`。该`sort`算法可用于`List`在调用之前对其进行排序`binarySearch`。

两个表单的返回值相同。如果`List`包含搜索键，则返回其索引。如果不是，则返回值为`(-(insertion point) - 1)`，其中插入点是将值插入到的点`List`，或者第一个元素的索引大于该值，或者`list.size()`如果所有元素`List`都小于指定值。这个公认的丑陋公式保证了`>= 0`当且仅当找到搜索关键字时返回值。将布尔值`(found)`和整数组合`(index)`成单个`int`返回值基本上是一种破解。

以下习惯用法可用于两种形式的`binarySearch`操作，它查找指定的搜索关键字并将其插入适当的位置（如果它尚不存在）。

```java
int pos = Collections.binarySearch(list, key);
if (pos < 0)
   l.add(-pos-1, key);
```

## 组成

频率和不相交算法测试一个或多个组成的某些方面`Collections`：

- `frequency` - 计算指定元素在指定集合中出现的次数
- `disjoint`- 确定两个`Collections`是否不相交; 也就是说，它们是否不包含任何共同的元素



## 寻找极端价值观

的`min`和`max`算法返回，分别包含在指定的最小和最大元素`Collection`。这两种操作都有两种形式。简单形式只接受a `Collection`并根据元素的自然顺序返回最小（或最大）元素。第二种形式`Comparator`除了`Collection`和之外还根据指定的方式返回最小（或最大）元素`Comparator`。