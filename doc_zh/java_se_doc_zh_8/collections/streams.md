# 聚合操作

**注意**：为了更好地理解本节中的概念，请查看 [Lambda表达式](../../java/javaOO/lambdaexpressions.html)和 [方法引用部分](../../java/javaOO/methodreferences.html)。

你用什么收藏品？您不是简单地将对象存储在集合中并将其保留在那里。在大多数情况下，您使用集合来检索存储在其中的项目。

再次考虑[Lambda表达式](../../java/javaOO/lambdaexpressions.html)一节中描述的场景 。假设您正在创建社交网络应用程序。您希望创建一项功能，使管理员能够对满足特定条件的社交网络应用程序成员执行任何类型的操作，例如发送消息。

和以前一样，假设此社交网络应用程序的成员由以下[`Person`](examples/Person.java)类表示 ：

```java
public class Person {

    public enum Sex {
        MALE, FEMALE
    }

    String name;
    LocalDate birthday;
    Sex gender;
    String emailAddress;
    
    // ...

    public int getAge() {
        // ...
    }

    public String getName() {
        // ...
    }
}
```

以下示例`roster`使用for-each循环打印集合中包含的所有成员的名称：

```java
for (Person p : roster) {
    System.out.println(p.getName());
}
```

以下示例打印集合中包含`roster`但具有聚合操作的所有成员`forEach`：

```java
roster
    .stream()
    .forEach(e -> System.out.println(e.getName());
```

虽然在此示例中，使用聚合操作的版本比使用for-each循环的版本长，但您会看到使用批量数据操作的版本对于更复杂的任务将更简洁。

涵盖以下主题：

- [管道和流](#pipelines)
- [聚合操作和迭代器之间的差异](#differences)

查找示例中本节中描述的代码摘录 [`BulkDataOperationsExamples`](examples/BulkDataOperationsExamples.java)。

### 管道和流

一个*管道*是总的操作顺序。以下示例`roster`使用由聚合操作组成的管道打印集合中包含的male成员，`filter`并且`forEach`：

```java
roster
    .stream()
    .filter(e -> e.getGender() == Person.Sex.MALE)
    .forEach(e -> System.out.println(e.getName()));
```

将此示例与以下示例进行比较，`roster`以使用for-each循环打印集合中包含的male成员：

```java
for (Person p : roster) {
    if (p.getGender() == Person.Sex.MALE) {
        System.out.println(p.getName());
    }
}
```

管道包含以下组件：

- 源：这可以是集合，数组，生成器函数或I / O通道。在此示例中，源是集合`roster`。

- 零个或多个*中间操作*。中间操作（例如`filter`，）生成新流。

  甲*流*是元素的序列。与集合不同，它不是存储元素的数据结构。相反，流通过管道从源传输值。此示例`roster`通过调用方法从集合中创建流`stream`。

  该`filter`操作返回一个新流，其中包含与其谓词匹配的元素（此操作的参数）。在此示例中，谓词是lambda表达式`e -> e.getGender() == Person.Sex.MALE`。`true`如果`gender`object 的字段`e`具有值，则返回布尔值`Person.Sex.MALE`。因此，`filter`此示例中的操作返回包含集合中所有男性成员的流`roster`。

- 甲*终端操作*。终端操作，例如`forEach`，产生非流结果，例如原始值（如双值），集合，或者在`forEach`没有任何价值的情况下。在此示例中，`forEach`操作的参数是lambda表达式`e -> System.out.println(e.getName())`，它调用`getName`对象上的方法`e`。（Java运行时和编译器推断对象的类型`e`是`Person`。）

下面的例子计算所有男性成员的集合中包含的平均年龄`roster`与管道是由骨料业务`filter`，`mapToInt`以及`average`：

```java
double average = roster
    .stream()
    .filter(p -> p.getGender() == Person.Sex.MALE)
    .mapToInt(Person::getAge)
    .average()
    .getAsDouble();
```

该`mapToInt`操作返回一个新的类型流`IntStream`（这是一个只包含整数值的流）。该操作将其参数中指定的函数应用于特定流中的每个元素。在此示例中，函数是`Person::getAge`，它是返回成员年龄的方法引用。（或者，您可以使用lambda表达式`e -> e.getAge()`。）因此，`mapToInt`此示例中的操作返回一个流，该流包含集合中所有男性成员的年龄`roster`。

该`average`操作计算类型流中包含的元素的平均值`IntStream`。它返回一个类型的对象`OptionalDouble`。如果流不包含任何元素，则该`average`操作返回一个空实例`OptionalDouble`，并调用该方法`getAsDouble`抛出一个`NoSuchElementException`。JDK包含许多终端操作，例如`average`通过组合流的内容返回一个值。这些操作称为*还原操作* ; 有关详细信息，请参阅[缩减](../../collections/streams/reduction.html)部分 。

### 聚合操作和迭代器之间的差异

像聚合操作一样`forEach`，似乎就像迭代器一样。但是，它们有几个根本区别：

- **它们使用内部迭代**：聚合操作不包含`next`指示它们处理集合的下一个元素的方法。随着*内部委派*，您的应用程序决定*哪些*集合进行迭代，但是JDK决定*如何*遍历集合。使用*外部迭代*，您的应用程序确定它迭代的集合以及迭代它的方式。但是，外部迭代只能按顺序迭代集合的元素。内部迭代没有此限制。它可以更容易地利用并行计算，这涉及将问题分解为子问题，同时解决这些问题，然后将解决方案的结果与子问题相结合。有关更多信息，请参阅[并行性](../../collections/streams/parallelism.html)一节 。
- **它们处理流中的元素**：聚合操作处理**流**中的元素，而不是直接来自集合。因此，它们也称为*流操作*。
- **它们支持行为作为参数**：您可以将[lambda表达式](../../java/javaOO/lambdaexpressions.html)指定 为大多数聚合操作的参数。这使您可以自定义特定聚合操作的行为。

