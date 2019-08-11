# 并发集合

该`java.util.concurrent`软件包包括Java Collections Framework的许多新增功能。这些最容易通过提供的集合接口进行分类：

- [`BlockingQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html) 定义先进先出数据结构，当您尝试添加到完整队列或从空队列中检索时，该数据结构会阻塞或超时。
- [`ConcurrentMap`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentMap.html)是[`java.util.Map`](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html)定义有用原子操作的子接口 。仅当密钥存在时，这些操作才会删除或替换键值对，或仅在密钥不存在时才添加键值对。使这些操作原子化有助于避免同步。标准的通用实现`ConcurrentMap`是 [`ConcurrentHashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html)，它是一个并发的模拟 [`HashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html)。
- [`ConcurrentNavigableMap`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentNavigableMap.html)是`ConcurrentMap`支持近似匹配的子接口。标准的通用实现`ConcurrentNavigableMap`是 [`ConcurrentSkipListMap`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentSkipListMap.html)，它是一个并发的模拟 [`TreeMap`](https://docs.oracle.com/javase/8/docs/api/java/util/TreeMap.html)。

所有这些集合通过定义将对象添加到集合的操作与访问或删除该对象的后续操作之间的先发生关系来帮助避免[内存一致性错误](memconsist.html)。