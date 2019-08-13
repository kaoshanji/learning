# Map实现

`Map` 实现分为通用，专用和并发实现。

## 通用地图实现

这三个通用 [`Map`](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html)实现是 [`HashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html)， [`TreeMap`](https://docs.oracle.com/javase/8/docs/api/java/util/TreeMap.html)和 [`LinkedHashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashMap.html)。如果您需要`SortedMap`操作或按键排序的`Collection`-view迭代，请使用`TreeMap`; 如果你想要最大速度而不关心迭代顺序，请使用`HashMap`; 如果您想要接近`HashMap`性能和插入顺序迭代，请使用`LinkedHashMap`。在这方面，情况`Map`类似于`Set`。同样，[Set Implementations](../implementations/set.html)部分中的其他所有内容 也适用于`Map`实现。

`LinkedHashMap`提供两种不可用的功能`LinkedHashSet`。创建a时`LinkedHashMap`，可以根据键访问而不是插入来订购。换句话说，仅查找与键相关联的值会将该键带到地图的末尾。此外，`LinkedHashMap`还提供了`removeEldestEntry`一种方法，可以覆盖该方法，以便在将新映射添加到地图时强制执行自动删除过时映射的策略。这使得实现自定义缓存变得非常容易。

例如，此覆盖将允许地图长达多达100个条目，然后每次添加新条目时它将删除最旧条目，从而保持100个条目的稳定状态。

```java
private static final int MAX_ENTRIES = 100;

protected boolean removeEldestEntry(Map.Entry eldest) {
    return size() > MAX_ENTRIES;
}
```

## 专用地图实现

有三个专用Map实现- [`EnumMap`](https://docs.oracle.com/javase/8/docs/api/java/util/EnumMap.html)， [`WeakHashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/WeakHashMap.html)和 [`IdentityHashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/IdentityHashMap.html)。`EnumMap`，内部实现为`array`，是一个`Map`与枚举键一起使用的高性能实现。该实现将`Map`接口的丰富性和安全性与接近阵列的速度相结合。如果要将枚举映射到值，则应始终`EnumMap`优先使用数组。

`WeakHashMap`是一个`Map`接口的实现，只存储对其键的弱引用。仅存储弱引用允许键值对在其键不再被引用时被垃圾收集`WeakHashMap`。此类提供了利用弱引用功能的最简单方法。它对于实现“类似注册表”的数据结构很有用，其中当任何线程不再可以访问其密钥时，条目的实用程序就会消失。

`IdentityHashMap`是`Map`基于哈希表的基于身份的实现。此类对于拓扑保留对象图转换非常有用，例如序列化或深度复制。要执行此类转换，您需要维护一个基于身份的“节点表”，以跟踪已经看到的对象。基于身份的映射还用于维护动态调试器和类似系统中的对象到元信息映射。最后，基于身份的映射可以有效地阻止有意反常`equals`方法导致的“欺骗攻击”，因为`IdentityHashMap`从不`equals`在其密钥上调用该方法。这种实现的另一个好处是它很快。

## 并发映射实现

所述 [`java.util.concurrent`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html)包中包含的 [`ConcurrentMap`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentMap.html)接口，它延伸`Map`与原子`putIfAbsent`，`remove`和`replace`方法，以及 [`ConcurrentHashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html)该接口的实现。

`ConcurrentHashMap`是一个由哈希表备份的高度并发，高性能的实现。执行检索时，此实现永远不会阻塞，并允许客户端选择更新的并发级别。它旨在作为以下内容的替代品`Hashtable`：除了实现之外`ConcurrentMap`，它还支持所有特有的遗留方法`Hashtable`。同样，如果您不需要遗留操作，请小心使用`ConcurrentMap`界面对其进行操作。