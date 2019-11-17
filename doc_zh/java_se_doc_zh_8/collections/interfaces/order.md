# 排序

A `List` `l`可以如下排序。

```java
Collections.sort(l);
```

如果`List`由`String`元素组成，它将按字母顺序排序。如果它由`Date`元素组成，它将按时间顺序排序。这是怎么发生的？`String`并且`Date`都实现了接口。实现为类提供了*自然顺序*，允许自动对该类的对象进行排序。在 下面的表格总结了一些实现更重要的Java平台类的

| Class          | Natural Ordering                            |
| -------------- | ------------------------------------------- |
| `Byte`         | Signed numerical                            |
| `Character`    | Unsigned numerical                          |
| `Long`         | Signed numerical                            |
| `Integer`      | Signed numerical                            |
| `Short`        | Signed numerical                            |
| `Double`       | Signed numerical                            |
| `Float`        | Signed numerical                            |
| `BigInteger`   | Signed numerical                            |
| `BigDecimal`   | Signed numerical                            |
| `Boolean`      | `Boolean.FALSE < Boolean.TRUE`              |
| `File`         | System-dependent lexicographic on path name |
| `String`       | Lexicographic                               |
| `Date`         | Chronological                               |
| `CollationKey` | Locale-specific lexicographic               |

如果您尝试排序列表中，不落实的元素`Comparable`，`Collections.sort(list)`将抛出一个 [`ClassCastException`](https://docs.oracle.com/javase/8/docs/api/java/lang/ClassCastException.html)。同样，如果您尝试对列表进行排序，`Collections.sort(list, comparator)`则会抛出一个`ClassCastException`列表，其列表的元素无法使用`comparator`。可以相互比较的元素称为*相互比较*。虽然不同类型的元素可以相互比较，但这里列出的类别都不允许进行类间比较。

`Comparable`如果您只想对可比较元素的列表进行排序或创建它们的已排序集合，那么这就是您真正需要了解的界面。如果要实现自己的`Comparable`类型，下一部分将是您感兴趣的。

## 编写自己的可比类型

该`Comparable`界面由下列方法。

```java
public interface Comparable<T> {
    public int compareTo(T o);
}
```

该`compareTo`方法将接收对象与指定对象进行比较，并根据接收对象是否小于，等于或大于指定对象返回负整数0或正整数。如果无法将指定的对象与接收对象进行比较，则该方法抛出一个`ClassCastException`。

该 [`following class representing a person's name`](examples/Name.java)工具`Comparable`。

```java
import java.util.*;

public class Name implements Comparable<Name> {
    private final String firstName, lastName;

    public Name(String firstName, String lastName) {
        if (firstName == null || lastName == null)
            throw new NullPointerException();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String firstName() { return firstName; }
    public String lastName()  { return lastName;  }

    public boolean equals(Object o) {
        if (!(o instanceof Name))
            return false;
        Name n = (Name) o;
        return n.firstName.equals(firstName) && n.lastName.equals(lastName);
    }

    public int hashCode() {
        return 31*firstName.hashCode() + lastName.hashCode();
    }

    public String toString() {
	return firstName + " " + lastName;
    }

    public int compareTo(Name n) {
        int lastCmp = lastName.compareTo(n.lastName);
        return (lastCmp != 0 ? lastCmp : firstName.compareTo(n.firstName));
    }
}
```

为了保持前面的例子简短，这个类有点受限：它不支持中间名，它既需要名字也要求姓，并且不以任何方式国际化。尽管如此，它还说明了以下要点：

- `Name`对象是*不可变的*。在所有其他条件相同的情况下，不可变类型是要走的路，特别是对于将用作`Set`s中的元素或s中的键`Map`的对象。如果您在集合中修改元素或键，这些集合将会中断。
- 构造函数检查其参数`null`。这可以确保所有`Name`对象都很好地形成，这样其他任何方法都不会抛出`NullPointerException`。
- 该`hashCode`方法被重新定义。这对于重新定义`equals`方法的任何类都是必不可少的。（等于对象必须具有相同的哈希码。）
- 如果指定的对象是不合适的类型，则`equals`返回该方法。该方法在这些情况下抛出运行时异常。这些行为都是各方法的一般合同所要求的。`false``null``compareTo`
- 该`toString`方法已重新定义，因此它`Name`以人类可读的形式打印。这总是一个好主意，特别是对于要放入集合的对象。各种集合类型的`toString`方法取决于`toString`其元素，键和值的方法。

由于这部分是有关元素排序，让我们讨论多一点`Name`的`compareTo`方法。它实现了标准的名称排序算法，其中姓氏优先于名字。这正是您想要的自然顺序。如果自然顺序不自然，那将会非常混乱！

看一下如何`compareTo`实现，因为它很典型。首先，比较对象的最重要部分（在本例中为姓氏）。通常，您可以使用零件类型的自然顺序。在这种情况下，该部分是a`String`而自然（词典）排序正是所要求的。如果比较结果为零，表示相等，那么您就完成了：您只需返回结果。如果最重要的部分相同，则继续比较下一个最重要的部分。在这种情况下，只有两个部分 - 名字和姓氏。如果有更多的零件，你会以明显的方式进行，比较零件，直到你发现两个不相等或你正在比较最不重要的零件，此时你将返回比较的结果。

只是为了表明一切正常，这里是 [`a program that builds a list of names and sorts them`](examples/NameSort.java)。

```java
import java.util.*;

public class NameSort {
    public static void main(String[] args) {
        Name nameArray[] = {
            new Name("John", "Smith"),
            new Name("Karl", "Ng"),
            new Name("Jeff", "Smith"),
            new Name("Tom", "Rich")
        };

        List<Name> names = Arrays.asList(nameArray);
        Collections.sort(names);
        System.out.println(names);
    }
}
```

如果你运行这个程序，这是它打印的内容。

```bash
[Karl Ng, Tom Rich, Jeff Smith, John Smith]
```

对`compareTo`方法的行为有四个限制，我们现在不会讨论这些限制，因为它们技术性很差，很无聊，最好留在API文档中。所有实现的类都`Comparable`遵守这些限制非常重要，因此，`Comparable`如果您正在编写实现它的类，请阅读文档。尝试对违反限制的对象列表进行排序具有未定义的行为。从技术上讲，这些限制确保了自然顺序是实现它的类的对象的*总顺序* ; 这对于确保明确定义排序是必要的。

## 比较

如果您想按照自然顺序排序某些对象，该怎么办？或者，如果要对某些未实现的对象进行排序，该怎么办`Comparable`？要做这些事情中的任何一个，你需要提供一个 [`Comparator`](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)封装排序的对象。与`Comparable`界面一样，`Comparator`界面由单个方法组成。

```java
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```

该`compare`方法比较其两个参数，返回负整数，0或正整数，具体取决于第一个参数是小于，等于还是大于第二个参数。如果其中一个参数的类型不合适`Comparator`，则该`compare`方法抛出一个`ClassCastException`。

所说的大部分内容也`Comparable`适用`Comparator`于此。编写`compare`方法几乎与编写`compareTo`方法完全相同，只是前者将两个对象作为参数传入。该`compare`方法具有服从同样的四个技术限制为`Comparable`的`compareTo`出于同样的原因方法-一个`Comparator`必须引起它比较对象进行整体秩序。

假设您有一个名为的类`Employee`，如下所示。

```java
public class Employee implements Comparable<Employee> {
    public Name name()     { ... }
    public int number()    { ... }
    public Date hireDate() { ... }
       ...
}
```

我们假设`Employee`实例的自然排序是`Name`对员工姓名的排序（如前面的例子中所定义）。不幸的是，老板要求按照资历顺序列出员工名单。这意味着我们必须做一些工作，但并不多。以下程序将生成所需的列表。

```java
import java.util.*;
public class EmpSort {
    static final Comparator<Employee> SENIORITY_ORDER = 
                                        new Comparator<Employee>() {
            public int compare(Employee e1, Employee e2) {
                return e2.hireDate().compareTo(e1.hireDate());
            }
    };

    // Employee database
    static final Collection<Employee> employees = ... ;

    public static void main(String[] args) {
        List<Employee> e = new ArrayList<Employee>(employees);
        Collections.sort(e, SENIORITY_ORDER);
        System.out.println(e);
    }
}
```

该`Comparator`计划在相当简单。它依赖于`Date`应用于`hireDate`访问器方法返回的值的自然顺序。请注意，将`Comparator`第二个参数的雇用日期传递给第一个参数，而不是反过来。原因是最近雇用的员工是最不高级的; 按雇用日期顺序排序会使列表按反向资历顺序排列。人们有时用来实现这种效果的另一种技术是维持参数顺序但是否定比较的结果。

```java
// Don't do this!!
return -r1.hireDate().compareTo(r2.hireDate());
```

你应该总是使用前一种技术来支持后者，因为后者不能保证工作。这样做的原因是，如果`compareTo`方法`int`的参数小于调用它的对象，则该方法可以返回任何负数。`int`否定时会有一个负面因素，看似奇怪。

```java
-Integer.MIN_VALUE == Integer.MIN_VALUE
```

将`Comparator`在前面的程序工作正常进行排序一个`List`它不能用于排序排序集合，如：，但它确实有一个缺陷`TreeSet`，因为它产生的顺序是，*不兼容*等号。这意味着这`Comparator`等于该`equals`方法没有的对象。特别是，在同一天雇佣的任何两名员工将相同。当你排序时`List`，这没关系; 但是当你使用它`Comparator`来订购一个有序的集合时，它是致命的。如果您使用此`Comparator`方法将在同一日期雇用的多名员工插入a中`TreeSet`，则只会将第一个员工添加到该组中; 第二个将被视为重复元素，将被忽略。

要解决此问题，只需调整它`Comparator`以使其生成*与之兼容* 的排序`equals`。换句话说，调整它以便在使用时看到相同的唯一元素`compare`是那些在使用时被视为相等的元素`equals`。这样做的方法是执行两部分比较（至于`Name`），其中第一部分是我们感兴趣的部分 - 在这种情况下，雇用日期 - 第二部分是唯一标识宾语。员工编号在这里是明显的属性。这就是`Comparator`结果。

```java
static final Comparator<Employee> SENIORITY_ORDER = 
                                        new Comparator<Employee>() {
    public int compare(Employee e1, Employee e2) {
        int dateCmp = e2.hireDate().compareTo(e1.hireDate());
        if (dateCmp != 0)
            return dateCmp;

        return (e1.number() < e2.number() ? -1 :
               (e1.number() == e2.number() ? 0 : 1));
    }
};
```

最后一点说明：您可能想用更简单的方法替换最终`return`语句`Comparator`：

```java
return e1.number() - e2.number();
```

除非你*绝对确定*没有人会有负面的员工编号，否则不要这样做！这个技巧一般不起作用，因为有符号整数类型不足以表示两个任意有符号整数的差异。如果`i`是一个大的正整数并且`j`是一个大的负整数，`i - j`则会溢出并返回一个负整数。结果`comparator`违反了我们一直在谈论的四个技术限制之一（传递性）并产生可怕的，微妙的错误。这不是纯粹的理论问题; 人们被它烧了。