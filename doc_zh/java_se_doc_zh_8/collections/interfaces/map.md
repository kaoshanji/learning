# Map接口

A [`Map`](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html)是将键映射到值的对象。地图不能包含重复键：每个键最多可映射一个值。它模拟数学*函数*抽象。所述`Map`界面包括用于基本操作的方法（如`put`，`get`，`remove`， `containsKey`，`containsValue`，`size`，和`empty`），批量操作（如`putAll`和`clear`），以及集合视图（如`keySet`，`entrySet`，和`values`）。

Java平台包含三个通用`Map`实现： [`HashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html)， [`TreeMap`](https://docs.oracle.com/javase/8/docs/api/java/util/TreeMap.html)，和 [`LinkedHashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashMap.html)。他们的行为和表现，正是类似`HashSet`，`TreeSet`和`LinkedHashSet`，如在 [Set接口](set.html)部分。

本页的其余部分`Map`详细讨论了该接口。但首先，这里有一些`Map`使用JDK 8聚合操作收集的例子。对现实世界对象进行建模是面向对象编程中的常见任务，因此可以合理地认为某些程序可能会按部门对员工进行分组：

```java
// Group employees by department
Map<Department, List<Employee>> byDept = employees.stream()
.collect(Collectors.groupingBy(Employee::getDepartment));
```

或者按部门计算所有工资的总和：

```java
// Compute sum of salaries by department
Map<Department, Integer> totalByDept = employees.stream()
.collect(Collectors.groupingBy(Employee::getDepartment,
Collectors.summingInt(Employee::getSalary)));
```

或者通过成绩或成绩不及分组学生：

```java
// Partition students into passing and failing
Map<Boolean, List<Student>> passingFailing = students.stream()
.collect(Collectors.partitioningBy(s -> s.getGrade()>= PASS_THRESHOLD)); 
```

您还可以按城市分组：

```java
// Classify Person objects by city
Map<String, List<Person>> peopleByCity
         = personStream.collect(Collectors.groupingBy(Person::getCity));
```

或者甚至级联两个收藏家按州和城市对人进行分类：

```java
// Cascade Collectors 
Map<String, Map<String, List<Person>>> peopleByStateAndCity
  = personStream.collect(Collectors.groupingBy(Person::getState,
  Collectors.groupingBy(Person::getCity)))
```

同样，这些只是如何使用新JDK 8 API的几个示例。有关lambda表达式和聚合操作的深入介绍，请参阅标题为[Aggregate Operations](../../collections/streams/index.html)的课程 。

## 映射接口基本操作

的基本操作`Map`（`put`，`get`，`containsKey`，`containsValue`，`size`，和`isEmpty`）表现得完全像他们在同行`Hashtable`。该 [`following program`](examples/Freq.java)生成的在参数列表中找到的字的频数分布表。频率表将每个单词映射到它在参数列表中出现的次数。

```java
import java.util.*;

public class Freq {
    public static void main(String[] args) {
        Map<String, Integer> m = new HashMap<String, Integer>();

        // Initialize frequency table from command line
        for (String a : args) {
            Integer freq = m.get(a);
            m.put(a, (freq == null) ? 1 : freq + 1);
        }

        System.out.println(m.size() + " distinct words:");
        System.out.println(m);
    }
}
```

关于这个程序唯一棘手的问题是`put`声明的第二个参数。该参数是一个条件表达式，如果该单词之前从未见过，则将频率设置为1，如果已经看到该单词，则将其设置为当前值。尝试使用以下命令运行此程序：

```bash
java Freq if it is to be it is up to me to delegate
```

该程序产生以下输出。

```bash
8 distinct words:
{to=3, delegate=1, be=1, it=2, up=1, if=1, me=1, is=2}
```

假设您希望按字母顺序查看频率表。您所要做的就是将`Map`from 的实现类型更改`HashMap`为`TreeMap`。进行这种四字符更改会导致程序从同一命令行生成以下输出。

```bash
8 distinct words:
{be=1, delegate=1, if=1, is=2, it=2, me=1, to=3, up=1}
```

类似地，您可以通过将地图的实现类型更改为，使程序按照单词首次出现在命令行上的顺序打印频率表`LinkedHashMap`。这样做会产生以下输出。

```bash
8 distinct words:
{if=1, it=2, is=2, to=3, be=1, up=1, me=1, delegate=1}
```

这种灵活性提供了基于接口的框架功能的有力说明。

与[`Set`](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html)和 [`List`](https://docs.oracle.com/javase/8/docs/api/java/util/List.html)接口一样 ，`Map`增强了对`equals`和`hashCode`方法的要求，以便`Map`可以比较两个对象的逻辑相等性，而不考虑它们的实现类型。`Map`如果两个实例表示相同的键值映射，则它们是相等的。

按照惯例，所有通用`Map`实现都提供了构造函数，这些构造函数接受一个`Map`对象并初始化new `Map`以包含指定的所有键值映射`Map`。这个标准`Map`转换构造函数完全类似于标准`Collection`构造函数：它允许调用者创建`Map`一个所需的实现类型，该类型最初包含另一个的所有映射`Map`，而不管其他`Map`的实现类型。例如，假设您有一个`Map`名为`m`。以下单行创建一个新的，`HashMap`最初包含所有相同的键值映射`m`。

```java
Map<K, V> copy = new HashMap<K, V>(m);
```

## 映射接口批量操作

该`clear`操作完全按照您的想法执行：它从中移除所有映射`Map`。该`putAll`操作是接口操作的`Map`模拟。除了明显使用倾倒到另一个之外，它还有第二个更微妙的用途。假设a 用于表示属性 - 值对的集合; 该操作与转换构造函数结合使用，提供了一种使用默认值实现属性映射创建的简洁方法。以下是演示此技术的静态工厂方法。

```java
static <K, V> Map<K, V> newAttributeMap(Map<K, V>defaults, Map<K, V> overrides) {
    Map<K, V> result = new HashMap<K, V>(defaults);
    result.putAll(overrides);
    return result;
}
```

## 集合视图

该`Collection`视图方法允许`Map`被视为`Collection`在这三个方面：

- `keySet`- `Set`包含在中的密钥`Map`。
- `values`- `Collection`包含在中的值`Map`。这`Collection`不是a `Set`，因为多个键可以映射到相同的值。
- `entrySet`- `Set`包含在中的键值对`Map`。该`Map`接口提供了一个小的嵌套接口，称为`Map.Entry`此类中的元素类型`Set`。

该`Collection`视图提供的*唯一*遍历的一种手段`Map`。此示例说明了`Map`使用`for-each`构造迭代a中的键的标准习惯用法：

```java
for (KeyType key : m.keySet())
    System.out.println(key);
```

以及`iterator`：

```java
// Filter a map based on some 
// property of its keys.
for (Iterator<Type> it = m.keySet().iterator(); it.hasNext(); )
    if (it.next().isBogus())
        it.remove();
```

迭代值的习语是类似的。以下是迭代键值对的习语。

```java
for (Map.Entry<KeyType, ValType> e : m.entrySet())
    System.out.println(e.getKey() + ": " + e.getValue());
```

起初，许多人担心这些习惯用法可能会很慢，因为每次调用视图操作时`Map`都必须创建一个新`Collection`实例`Collection`。轻松休息：`Map`每次要求给定`Collection`视图时，没有理由不能总是返回相同的对象。这正是所有`Map`实现中`java.util`所做的。

与所有三个`Collection`视图，调用一个`Iterator`的`remove`操作从所述背衬相关联的条目`Map`，假定背衬`Map`支持元素移除开始。这由前面的过滤习语说明。

与`entrySet`图，但也可以改变与通过调用一个键相关联的值`Map.Entry`的`setValue`迭代期间方法（再次，假定`Map`支撑值与修改开始）。请注意，这些是修改迭代期间*唯一*安全的方法`Map`; 如果`Map`在迭代进行过程中以任何其他方式修改底层，则行为未指定。

该`Collection`视图的支持元素的移除在其所有的多种形式- ，`remove`，`removeAll`，`retainAll`和`clear`操作，以及与`Iterator.remove`操作。（再次，这假设背衬`Map`支持元素移除。）

在任何情况下，`Collection`视图*都不*支持元素添加。这也就没有什么意义了`keySet`和`values`意见，这是不必要的`entrySet`看法，因为后盾`Map`的`put`和`putAll`方法提供相同的功能。

## 集合视图的花哨用途：地图代数

当施加到`Collection`意见，批量操作（`containsAll`，`removeAll`，和`retainAll`）是令人惊讶地有效的工具。对于初学者，假设您想知道一个是否`Map`是另一个的子图 - 也就是说，第一个是否`Map`包含第二个中的所有键值映射。以下成语可以解决这个问题。

```java
if (m1.entrySet().containsAll(m2.entrySet())) {
    ...
}
```

沿着类似的路线，假设您想知道两个`Map`对象是否包含所有相同键的映射。

```java
if (m1.keySet().equals(m2.keySet())) {
    ...
}
```

假设您有一个`Map`表示属性 - 值对的集合，两个`Set`s表示必需的属性和允许的属性。（允许的属性包括必需的属性。）以下代码段确定属性映射是否符合这些约束，如果不符合则打印详细的错误消息。

```java
static <K, V> boolean validate(Map<K, V> attrMap, Set<K> requiredAttrs, Set<K>permittedAttrs) {
    boolean valid = true;
    Set<K> attrs = attrMap.keySet();

    if (! attrs.containsAll(requiredAttrs)) {
        Set<K> missing = new HashSet<K>(requiredAttrs);
        missing.removeAll(attrs);
        System.out.println("Missing attributes: " + missing);
        valid = false;
    }
    if (! permittedAttrs.containsAll(attrs)) {
        Set<K> illegal = new HashSet<K>(attrs);
        illegal.removeAll(permittedAttrs);
        System.out.println("Illegal attributes: " + illegal);
        valid = false;
    }
    return valid;
}
```

假设您想知道两个`Map`对象共有的所有键。

```java
Set<KeyType>commonKeys = new HashSet<KeyType>(m1.keySet());
commonKeys.retainAll(m2.keySet());
```

类似的习语可以为你提供共同的价值观。

到目前为止提出的所有习语都是非破坏性的; 也就是说，他们不会修改支持`Map`。这里有一些。假设您要删除`Map`与另一个共有的所有键值对。

```java
m1.entrySet().removeAll(m2.entrySet());
```

假设您要从一个中删除`Map`所有在另一个中具有映射的键。

```java
m1.keySet().removeAll(m2.keySet());
```

在同一批量操作中开始混合键和值时会发生什么？假设您有一个`Map`，`managers`将公司中的每个员工映射到员工的经理。我们会故意模糊键和值对象的类型。没关系，只要它们是相同的。现在假设您想知道所有“个人贡献者”（或非管理者）是谁。以下代码段将准确告诉您您想要了解的内容。

```java
Set<Employee> individualContributors = new HashSet<Employee>(managers.keySet());
individualContributors.removeAll(managers.values());
```

假设您要解雇所有直接向某位经理Simon报告的员工。

```java
Employee simon = ... ;
managers.values().removeAll(Collections.singleton(simon));
```

请注意，这个习惯用法是`Collections.singleton`一个静态工厂方法，它`Set`使用单个指定元素返回一个不可变的方法。

一旦你完成了这项工作，你可能会有一大堆员工，他们的经理不再为公司工作（如果任何西蒙的直接报告本身就是经理）。以下代码将告诉您哪些员工拥有不再为公司工作的经理。

```java
Map<Employee, Employee> m = new HashMap<Employee, Employee>(managers);
m.values().removeAll(managers.keySet());
Set<Employee> slackers = m.keySet();
```

这个例子有点棘手。首先，它创建一个临时副本`Map`，并从临时副本中删除其（管理者）值是原始密钥的所有条目`Map`。请记住，原件`Map`有每个员工的条目。因此，临时中的其余条目`Map`包括原始`Map`的所有条目，其（经理）值不再是雇员。因此，临时副本中的密钥恰好代表了我们正在寻找的员工。

还有很多成语，比如本节中包含的成语，但列出它们都是不切实际和乏味的。一旦掌握了它，在你需要的时候找出合适的产品并不困难。

## 屈德宁

甲*多重映射*就像是一个`Map`，但它可以图中的每个键的多个值。Java Collections Framework不包含多重映射的接口，因为它们并未全部使用。使用`Map`其值为`List`实例的多重映射是一件相当简单的事情。在下一个代码示例中演示了此技术，该示例读取每行包含一个单词（全部小写）的单词列表，并打印出符合大小标准的所有anagram组。一个*字谜组*是一堆单词，所有单词都包含完全相同的字母，但顺序不同。该程序在命令行上有两个参数：（1）字典文件的名称和（2）要打印的anagram组的最小大小。不打印包含少于指定最小值的单词组的Anagram组。

找到anagram组有一个标准技巧：对于字典中的每个单词，按字母顺序排列单词中的字母（即将单词的字母重新排序为字母顺序）并将条目放入多图，将字母顺序排列的单词映射到原始单词字。例如，单词*bad*导致将*abd*条目映射为*bad*以将其放入multimap中。片刻的反射将显示任何给定键映射形成anagram组的所有单词。迭代多图中的键，打印出满足大小约束的每个anagram组是一件简单的事情。

[`The following program`](examples/Anagrams.java) 是这种技术的直接实现。

```java
import java.util.*;
import java.io.*;

public class Anagrams {
    public static void main(String[] args) {
        int minGroupSize = Integer.parseInt(args[1]);

        // Read words from file and put into a simulated multimap
        Map<String, List<String>> m = new HashMap<String, List<String>>();

        try {
            Scanner s = new Scanner(new File(args[0]));
            while (s.hasNext()) {
                String word = s.next();
                String alpha = alphabetize(word);
                List<String> l = m.get(alpha);
                if (l == null)
                    m.put(alpha, l=new ArrayList<String>());
                l.add(word);
            }
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }

        // Print all permutation groups above size threshold
        for (List<String> l : m.values())
            if (l.size() >= minGroupSize)
                System.out.println(l.size() + ": " + l);
    }

    private static String alphabetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```

在173,000字的字典文件上运行此程序，最小anagram组大小为8会产生以下输出。

```bash
9: [estrin, inerts, insert, inters, niters, nitres, sinter,
     triens, trines]
8: [lapse, leaps, pales, peals, pleas, salep, sepal, spale]
8: [aspers, parses, passer, prases, repass, spares, sparse,
     spears]
10: [least, setal, slate, stale, steal, stela, taels, tales,
      teals, tesla]
8: [enters, nester, renest, rentes, resent, tenser, ternes,
     treens]
8: [arles, earls, lares, laser, lears, rales, reals, seral]
8: [earings, erasing, gainers, reagins, regains, reginas,
     searing, seringa]
8: [peris, piers, pries, prise, ripes, speir, spier, spire]
12: [apers, apres, asper, pares, parse, pears, prase, presa,
      rapes, reaps, spare, spear]
11: [alerts, alters, artels, estral, laster, ratels, salter,
      slater, staler, stelar, talers]
9: [capers, crapes, escarp, pacers, parsec, recaps, scrape,
     secpar, spacer]
9: [palest, palets, pastel, petals, plates, pleats, septal,
     staple, tepals]
9: [anestri, antsier, nastier, ratines, retains, retinas,
     retsina, stainer, stearin]
8: [ates, east, eats, etas, sate, seat, seta, teas]
8: [carets, cartes, caster, caters, crates, reacts, recast,
     traces]
```

许多这些词似乎有点虚伪，但这不是程序的错; 他们在字典文件中。这是 [`dictionary file`](examples/dictionary.txt)我们使用的。它源自Public Domain ENABLE基准参考词列表。

