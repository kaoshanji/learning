# 包装器实现

包装器实现将所有实际工作委托给指定的集合，但在此集合提供的功能之上添加额外的功能。对于设计模式粉丝，这是*装饰器*模式的一个示例。虽然它看起来有点奇特，但它真的非常简单。

这些实现是匿名的; 该库提供静态工厂方法，而不是提供公共类。所有这些实现都可以在[`Collections`](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html)类中找到 ，它只包含静态方法。

## 同步包装器

同步包装器将自动同步（线程安全性）添加到任意集合。每六个核心集合接口- ， [`Collection`](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html)， [`Set`](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html)， [`List`](https://docs.oracle.com/javase/8/docs/api/java/util/List.html)， [`Map`](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html)， [`SortedSet`](https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html)和 [`SortedMap`](https://docs.oracle.com/javase/8/docs/api/java/util/SortedMap.html)-有一个静态的工厂方法。

```java
public static <T> Collection <T> synchronizedCollection（Collection <T> c）;
public static <T> Set <T> synchronizedSet（Set <T> s）;
public static <T> List <T> synchronizedList（List <T> list）;
public static <K，V> Map <K，V> synchronizedMap（Map <K，V> m）;
public static <T> SortedSet <T> synchronizedSortedSet（SortedSet <T> s）;
public static <K，V> SortedMap <K，V> synchronizedSortedMap（SortedMap <K，V> m）;
```

这些方法中的每一个都返回`Collection`由指定集合备份的同步（线程安全）。为了保证串行访问，必须通过返回的集合完成对后备集合的*所有*访问。保证这一点的简单方法是不保留对支持集合的引用。使用以下技巧创建同步集合。

```java
List<Type> list = Collections.synchronizedList(new ArrayList<Type>());
```

以这种方式创建的集合与正常同步的集合（例如a）一样具有线程安全性 [`Vector`](https://docs.oracle.com/javase/8/docs/api/java/util/Vector.html)。

面对并发访问，用户必须在迭代时手动同步返回的集合。原因是迭代是通过对集合的多次调用来完成的，集合必须组成一个单独的原子操作。以下是迭代包装器同步集合的习惯用法。

```java
Collection<Type> c = Collections.synchronizedCollection(myCollection);
synchronized(c) {
    for (Type e : c)
        foo(e);
}
```

如果使用显式迭代器，则`iterator`必须从`synchronized`块内调用该方法。不遵循此建议可能会导致不确定的行为。迭代`Collection`同步视图的习语`Map`是类似的。`Map`当迭代任何`Collection`视图而不是在`Collection`视图本身上进行同步时，用户必须同步同步，如以下示例所示。

```java
Map<KeyType, ValType> m = Collections.synchronizedMap(new HashMap<KeyType, ValType>());
    ...
Set<KeyType> s = m.keySet();
    ...
// Synchronizing on m, not s!
synchronized(m) {
    while (KeyType k : s)
        foo(k);
}
```

使用包装器实现的一个小缺点是您无法执行包装实现的任何*非接口*操作。因此，例如，在前面的`List`示例中，您无法调用对包装`ArrayList`的 [`ensureCapacity`](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html#ensureCapacity-int-)操作`ArrayList`。

## 不可修改的包装

与为包装集合添加功能的同步包装器不同，不可修改的包装器可以消除功能。特别是，它们通过拦截将修改集合并抛出一个集合的所有操作来消除修改集合的能力`UnsupportedOperationException`。不可修改的包装有两个主要用途，如下所示：

- 在构建集合后使集合不可变。在这种情况下，最好不要维护对支持集合的引用。这绝对保证了不变性。
- 允许某些客户端以只读方式访问您的数据结构。您保留对支持集合的引用，但分发对包装器的引用。通过这种方式，客户可以查看但不能修改，同时保持完全访问权限。

与同步包装器一样，六个核心`Collection`接口中的每一个都有一个静态工厂方法。

```java
public static <T> Collection<T> unmodifiableCollection(Collection<? extends T> c);
public static <T> Set<T> unmodifiableSet(Set<? extends T> s);
public static <T> List<T> unmodifiableList(List<? extends T> list);
public static <K,V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> m);
public static <T> SortedSet<T> unmodifiableSortedSet(SortedSet<? extends T> s);
public static <K,V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> m);
```

## 检查接口包装器

该`Collections.checked` *接口*提供了与泛型集合的包装纸。这些实现返回指定集合的*动态*类型安全视图，`ClassCastException`如果客户端尝试添加错误类型的元素，则会抛出该视图。该语言中的泛型机制提供了编译时（静态）类型检查，但有可能打败这种机制。动态类型安全的视图完全消除了这种可能性。