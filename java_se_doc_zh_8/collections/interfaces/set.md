# Set接口

A [`Set`](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html)是 [`Collection`](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html)不能包含重复元素的。它模拟了数学集抽象。该`Set`接口*仅*包含从中继承的方法，`Collection`并添加禁止重复元素的限制。`Set`还增加了对行为更强的合约`equals`和`hashCode`业务，允许`Set`情况下进行有意义的，即使它们的实现类型不同的比较。`Set`如果它们包含相同的元素，则两个实例相等。

Java平台包含三个通用`Set`实现：`HashSet`，`TreeSet`，和`LinkedHashSet`。 [`HashSet`](https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html)，将其元素存储在哈希表中，是性能最佳的实现; 但它不能保证迭代的顺序。 [`TreeSet`](https://docs.oracle.com/javase/8/docs/api/java/util/TreeSet.html)，将其元素存储在一个红黑树中，根据它们的值对元素进行排序; 它比...慢得多`HashSet`。 [`LinkedHashSet`](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashSet.html)，实现为具有贯穿其的链表的哈希表，根据它们插入集合（插入顺序）的顺序对其元素进行排序。`LinkedHashSet`使客户免受未指明的，通常混乱的订单的影响`HashSet`，但成本仅略高。

这是一个简单但有用的`Set`习语。假设您有一个`Collection`，`c`并且您希望创建另一个`Collection`包含相同元素但删除了所有重复项的元素。下面的单线程就可以了。

```java
Collection<Type> noDups = new HashSet<Type>(c);
```

它的工作原理是创建一个`Set`（根据定义，不能包含重复项），最初包含所有元素`c`。它使用[The Collection Interface](collection.html)部分中描述的标准转换构造函数 。

或者，如果使用JDK 8或更高版本，您可以轻松地收集到`Set`使用聚合操作：

```java
c.stream()
.collect(Collectors.toSet()); // no duplicates
```

这是一个稍长的例子，它将一个`Collection`名字累积到 `TreeSet`：

```java
Set<String> set = people.stream()
.map(Person::getName)
.collect(Collectors.toCollection(TreeSet::new));
```

以下是第一个成语的次要变体，它在删除重复元素时保留了原始集合的顺序：

```java
Collection<Type> noDups = new LinkedHashSet<Type>(c);
```

以下是封装前面的习惯用法的通用方法，返回`Set`与传递的通用类型相同的泛型类型。

```java
public static <E> Set<E> removeDups(Collection<E> c) {
    return new LinkedHashSet<E>(c);
}
```

## Set接口基本操作

该`size`操作返回`Set`（*基数*）中的元素数。该`isEmpty`方法完全符合您的想法。该`add`方法将指定的元素添加到`Set`if尚不存在的位置，并返回一个布尔值，指示是否添加了该元素。类似地，该`remove`方法从`Set`if中存在指定的元素，并返回一个布尔值，指示元素是否存在。该`iterator`方法返回一个`Iterator`通过`Set`。

以下 [`program`](examples/FindDups.java)打印出其参数列表中的所有不同单词。提供了该程序的两个版本。第一个使用JDK 8聚合操作。第二个使用for-each构造。

使用JDK 8聚合操作：

```java
import java.util.*;
import java.util.stream.*;

public class FindDups {
    public static void main(String[] args) {
        Set<String> distinctWords = Arrays.asList(args).stream()
		.collect(Collectors.toSet()); 
        System.out.println(distinctWords.size()+ 
                           " distinct words: " + 
                           distinctWords);
    }
}
```

使用`for-each`构造：

```java
import java.util.*;

public class FindDups {
    public static void main(String[] args) {
        Set<String> s = new HashSet<String>();
        for (String a : args)
               s.add(a);
               System.out.println(s.size() + " distinct words: " + s);
    }
}
```

现在运行该程序的任一版本。

```bash
java FindDups i came i saw i left
```

生成以下输出：

```bash
4 distinct words: [left, came, saw, i]
```

请注意，代码始终引用`Collection`其接口类型（`Set`）而不是其实现类型。这是一个*强烈*推荐的编程实践，因为它使您可以灵活地仅通过更改构造函数来更改实现。如果用于存储集合的变量或用于传递它的参数声明为其`Collection`实现类型而不是其接口类型，则必须更改*所有*此类变量和参数以更改其实现类型。

此外，无法保证生成的程序能够正常运行。如果程序使用原始实现类型中存在但未在新实现类型中存在的任何非标准操作，则程序将失败。仅通过其界面引用集合可防止您使用任何非标准操作。

`Set`前面例子中的实现类型是`HashSet`，它不保证元素的顺序`Set`。如果您希望程序按字母顺序打印单词列表，只需将`Set`实现类型更改`HashSet`为`TreeSet`。进行这个简单的单行更改会导致前一个示例中的命令行生成以下输出。

```bash
java FindDups i came i saw i left

4 distinct words: [came, i, left, saw]
```

## Set接口批量操作

批量操作特别适合于`Set`s; 应用时，它们执行标准的集合代数运算。假设`s1`并且`s2`是集合。以下是批量操作的作用：

- `s1.containsAll(s2)`-返回`true`如果`s2`是一个**子集**的`s1`。（`s2`是`s1`if集的子集，`s1`包含所有元素`s2`。）
- `s1.addAll(s2)`-转变`s1`到**工会**中`s1`和`s2`。（两个集合的并集是包含任一集合中包含的所有元素的集合。）
- `s1.retainAll(s2)`-转换`s1`成的交叉点`s1`和`s2`。（两个集合的交集是仅包含两个集合共有的元素的集合。）
- `s1.removeAll(s2)`-转换`s1`成的（非对称的）差集`s1`和`s2`。（例如，`s1`减去的设置差异`s2`是包含在`s1`但不在其中的所有元素的集合`s2`。）

要*非破坏性*地计算两个集合的并集，交集或集合差异（不修改任何一个集合），调用者必须在调用适当的批量操作之前复制一个集合。以下是由此产生的习语。

```java
Set<Type> union = new HashSet<Type>(s1);
union.addAll(s2);

Set<Type> intersection = new HashSet<Type>(s1);
intersection.retainAll(s2);

Set<Type> difference = new HashSet<Type>(s1);
difference.removeAll(s2);
```

`Set`前面的习语中的结果的实现类型`HashSet`，如已经提到的，`Set`是Java平台中最好的全面实现。但是，任何通用`Set`实现都可以替代。

让我们重新审视该`FindDups`计划。假设您想知道参数列表中的哪些单词只出现一次，哪些出现多次，但您不希望重复打印任何重复项。这种效果可以通过生成两个集合来实现 - 一个集合包含参数列表中的每个单词，另一个集合仅包含重复项目。仅出现一次的单词是这两组的集合差异，我们知道如何计算。这是 [`the resulting program`](examples/FindDups2.java)外观。

```java
import java.util.*;

public class FindDups2 {
    public static void main(String[] args) {
        Set<String> uniques = new HashSet<String>();
        Set<String> dups    = new HashSet<String>();

        for (String a : args)
            if (!uniques.add(a))
                dups.add(a);

        // Destructive set-difference
        uniques.removeAll(dups);

        System.out.println("Unique words:    " + uniques);
        System.out.println("Duplicate words: " + dups);
    }
}
```

当使用前面使用的相同参数列表（`i came i saw i left`）运行时，程序将生成以下输出。

```bash
Unique words:    [left, saw, came]
Duplicate words: [i]
```

不太常见的集合代数运算是*对称集合差异* - 两个指定集合中包含的元素集合，但两者都不包含。以下代码非破坏性地计算两组的对称集合差异。

```java
Set<Type> symmetricDiff = new HashSet<Type>(s1);
symmetricDiff.addAll(s2);
Set<Type> tmp = new HashSet<Type>(s1);
tmp.retainAll(s2);
symmetricDiff.removeAll(tmp);
```

## 设置接口阵列操作

`Set`除了他们为其他任何人做的事情之外，数组操作不会对s 做任何特殊操作`Collection`。[“收集接口”](collection.html)部分介绍了这些操作 。