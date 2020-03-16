#   集合

Java 里的`集合`库是对数据结构与算法的部分实现。

数据结构与算法，是解决编程问题的好方法，能够有效提高个人编程能力，训练逻辑思维，延缓被淘汰，大厂很关注。

##  解决方案

-   [集合框架API-`导图`](https://www.processon.com/view/link/5e6df68ee4b011fcce9621fd)

Java 集合接口类组织方式，2个根接口、6个抽象骨架类、7种具体实现，算法是以类方法出现，另外还提供了一些针对性操作。

### 数据组织

层次结构很清晰

-   集合接口
    -   Collection：一组对象，没有添加任何属性
        -   List：也被称为序列，通常允许重复，允许位置访问
        -   Set：不允许重复的元素，可能会也肯不会排序
        -   Queue(队列)：在处理之前容纳元素
    -   Map：从键到值得映射，每个键可以映射到一个值，具有集合视图
        -   SortedMap：映射键按照自然顺序或指定顺序排序
        -   ConcurrentMap：并发版Map
-   抽象骨架类，实现集合接口公共功能
    -   AbstractCollection：Set和List的更抽象实现
    -   AbstractSet：Set骨架实现
    -   AbstractList：List骨架实现，随机访问数据存储(如数组)
    -   AbstractSequentialList：List骨架实现，顺序访问数据存储(如链表)
    -   AbstractQueue：Queue骨架实现
    -   AbstractMap：Map骨架实现
-   具体实现：通常以`实现样式-接口`的形式来命名
    -   通用实现：使用最为普遍，就像是I/O里的字节流，而其他类型实现就是过滤流，添加了一些额外属性
    -   包装实现：与其他实现一起使用的功能增强，只能通过静态工程方法访问
    -   适配器实现：使一个集合接口适应另一个集合接口的实现
    -   便利实现：集合接口的高性能微型实现
    -   旧版实现：对旧的集合类进行了改装，以实现集合接口
    -   特殊实现：特定场景下提供针对性优化
    -   并发实现：这些实现是java.util.concurrent的一部分
-   算法

### 数据操作

为集合处理提供额外的功能

-   泛型：将集合类型传达给编译器，以便可以对其进行检查，增加了安全和便捷。
-   lambda表达式
-   流式处理
-   基础设施
    -   迭代器
    -   排序
    -   性能

----

##  [设计实现](https://docs.oracle.com/javase/8/docs/technotes/guides/collections/index.html)

集合框架是用于表示和操作集合的统一体系结构，使它们可以独立于表示的细节进行操作。它减少了编程工作，同时提高了性能。它实现了不相关的API之间的互操作性，减少了设计和学习新API的工作量，并促进了软件重用。该框架基于十几个收集接口。它包括这些接口的实现和用于操纵它们的算法。

-   文档
    -   [集合框架概述](401x.md)
    -   [设计常见问题解答](404x.md)
-   相关 API 
    -   [java.util](https://docs.oracle.com/javase/8/docs/api/java/util/package-summary.html)
    -   [java.util.function](https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html)：提供lambda表达式
    -   [java.util.stream](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)：为集合提供流式处理


-----

##  应用模式

官方教程

