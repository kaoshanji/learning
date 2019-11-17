# 实现总结

实现是用于存储集合的数据对象，它实现了[Interfaces课程中](../interfaces/index.html)描述的 [接口](../interfaces/index.html)。

Java Collections Framework提供了几个核心接口的通用实现：

- 对于`Set`接口，`HashSet`是最常用的实现。
- 对于`List`接口，`ArrayList`是最常用的实现。
- 对于`Map`接口，`HashMap`是最常用的实现。
- 对于`Queue`接口，`LinkedList`是最常用的实现。
- 对于`Deque`接口，`ArrayDeque`是最常用的实现。

每个通用实现都提供其接口中包含的所有可选操作。

Java Collections Framework还为需要非标准性能，使用限制或其他异常行为的情况提供了几种特殊用途的实现。

该`java.util.concurrent`包包含多个集合实现，这些实现是线程安全的，但不受单个排除锁的控制。

的`Collections`类（相对于所述`Collection`接口），提供了上或返回集合，其已知为包装器的实现操作的静态方法。

最后，有几种便利实现，当您不需要它们的全部功能时，它可以比通用实现更有效。通过静态工厂方法提供便捷实现。