# 实现

实现是用于存储集合的数据对象，它实现了[“接口”部分中](../interfaces/index.html)描述 [的接口](../interfaces/index.html)。本课程描述了以下几种实现：

- **通用实现**是最常用的实现，专为日常使用而设计。它们在标题为通用实现的表中进行了总结。
- **专用实现**旨在用于特殊情况，并显示非标准性能特征，使用限制或行为。
- **并发实现**旨在支持高并发性，通常以单线程性能为代价。这些实现是`java.util.concurrent`包的一部分。
- **包装器实现**与其他类型的实现（通常是通用实现）结合使用，以提供增加或限制的功能。
- **便利实现**是通常通过静态工厂方法提供的小型实现，为特殊集合（例如，单例集）的通用实现提供方便，有效的替代方案。
- **抽象实现**是骨架实现，有助于构建自定义实现 - 稍后将在“ [自定义集合实现”](../custom-implementations/index.html)部分中进行介绍。一个高级主题，并不是特别困难，但相对较少的人需要这样做。

通用实现总结 在下表中。

| 接口    | 哈希表实现 | 可调整大小的数组实现 | 树实现    | 链接列表实现 | 哈希表+链表实现 |
| ------- | ---------- | -------------------- | --------- | ------------ | --------------- |
| `Set`   | `HashSet`  |                      | `TreeSet` |              | `LinkedHashSet` |
| `List`  |            | `ArrayList`          |           | `LinkedList` |                 |
| `Queue` |            |                      |           |              |                 |
| `Deque` |            | `ArrayDeque`         |           | `LinkedList` |                 |
| `Map`   | `HashMap`  |                      | `TreeMap` |              | `LinkedHashMap` |

你可以从表中看到，Java集合框架提供了几种通用的实现 [`Set`](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html)， [`List`](https://docs.oracle.com/javase/8/docs/api/java/util/List.html)以及 [`Map`](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html)接口。在每种情况下，一个实现 - [`HashSet`](https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html)， [`ArrayList`](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html)和 [`HashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html)- 显然是用于大多数应用程序的实现，所有其他条件相同。请注意，表[`SortedSet`](https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html)和 [`SortedMap`](https://docs.oracle.com/javase/8/docs/api/java/util/SortedMap.html)接口在表中没有行。每个接口都有一个实现 [（`TreeSet`](https://docs.oracle.com/javase/8/docs/api/java/util/TreeSet.html)和 [`TreeMap`](https://docs.oracle.com/javase/8/docs/api/java/util/TreeMap.html)），并列在行`Set`和`Map`行中。有两个通用`Queue`实现 - [`LinkedList`](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html)也是一个`List`实现，并且 [`PriorityQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html)从表中省略。这两个实现提供了非常不同的语义：`LinkedList`提供FIFO语义，同时`PriorityQueue`根据其值对其元素进行排序。

每个通用实现都提供其接口中包含的所有可选操作。所有允许`null`元素，键和值。无同步（线程安全）。所有这些*都具有快速失败的迭代器*，它们在迭代期间检测非法并发修改，并且快速而干净地失败，而不是在未来的未确定时间冒任意，不确定的行为。所有人都`Serializable`支持公共`clone`方法。

这些实现是不同步的这一事实代表了对过去的打破：遗留集合`Vector`并且`Hashtable`是同步的。采用本方法是因为当同步没有任何好处时经常使用集合。这些用途包括单线程使用，只读使用，以及用作进行自身同步的大型数据对象的一部分。一般来说，良好的API设计实践不会让用户为他们不使用的功能付费。此外，在某些情况下，不必要的同步可能导致死锁。

如果需要线程安全集合，则“ [包装器实现”](wrapper.html)部分中描述的同步包装器 允许将*任何*集合转换为同步集合。因此，同步对于通用实现是可选的，而对于遗留实现是必需的。此外，该`java.util.concurrent`包提供的并发实现`BlockingQueue`接口，其延伸`Queue`，并且所述的`ConcurrentMap`接口，其延伸`Map`。这些实现提供了比仅仅同步实现更高的并发性。

通常，您应该考虑接口，*而不是*实现。这就是本节中没有编程示例的原因。在大多数情况下，实施的选择仅影响性能。[接口](../interfaces/index.html)部分中提到的首选样式 `Collection`是在创建a时选择实现，并立即将新集合分配给相应接口类型的变量（或将集合传递给期望接口参数的方法类型）。通过这种方式，程序不会依赖于给定实现中的任何添加方法，只要性能问题或行为细节保证程序员可以随时更改实现。

以下部分简要讨论了实现。使用诸如*constant-time*，*log*，*linear*，*n log（n）*和*quadratic之类的*单词来描述实现的性能，以指代执行操作的时间复杂度的渐近上限。所有这一切都是满口的，如果你不知道它意味着什么并不重要。如果您有兴趣了解更多信息，请参阅任何优秀的算法教科书。需要记住的一点是，这种性能指标有其局限性。有时，名义上较慢的实施可能会更快。如有疑问，请测量性能！