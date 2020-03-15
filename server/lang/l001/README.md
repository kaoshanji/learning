# Java 技术

Java 技术`核心`功能是 I/O(网络)系统、并发编程，两者相结合更是经典。

Java 技术`基础`功能：语言特性、类型系统、数据解析。

Java 技术所实现的设计`思想`、哲学理念有些抽象，其实就是用编程语言数据化解释现实世界事物关系。

>   对应 processon 里的`编程语言技术栈/Java技术栈`

>   来自 JDK API ，关注你需要关注的

每本书或网站都有各自的定位，这里是为了能够理解Java常见框架，这些框架是对Java特性的一种实践，而每种框架又是特定现实需求下的解决方案，框架里面基础支持都可以归纳到JDK里面来。

Java 每个特性或功能源自某个`JCP`，呈现方式是语法或包里，变更历史也记录在案，在这里开始，介绍的比较全面简洁直接。

模块功能或框架的`API`随着版本变更而演化增加，例如 I/O系统、并发部分、集合，可以从这里整理出该功能的逻辑线及针对的场景，看到的就是一点一点累积过程而不是一大堆乱七八糟理不顺的了


----

##  目录
-   [Java SE 语法](j001/README.md)
-   [JVM](j002/README.md)
    -   内存模型
-   [需求：JCP](j003/README.md)
-   [Java框架](j004/README.md)

----

##  变更
-   [Java 语言增强](https://docs.oracle.com/javase/8/docs/technotes/guides/language/enhancements.html)

----

##  权威! 权威! 权威!
-   [Java SE 规范列表](https://docs.oracle.com/javase/specs/index.html)
-   Java SE 8
    -   [Java 虚拟机规范](https://docs.oracle.com/javase/specs/jvms/se8/html/index.html)
    -   [Java 语言规范](https://docs.oracle.com/javase/specs/jls/se8/html/index.html)
    -   [API](https://docs.oracle.com/javase/8/docs/api/index.html)
-   Java SE 11
    -   [Java 虚拟机规范](https://docs.oracle.com/javase/specs/jvms/se11/html/index.html)
    -   [Java 语言规范](https://docs.oracle.com/javase/specs/jls/se11/html/index.html)
    -   [API](https://docs.oracle.com/en/java/javase/11/docs/api/index.html)
-   Java EE 8
    -   [官网](https://www.oracle.com/technetwork/java/javaee/overview/index.html)
    -   [API](https://javaee.github.io/javaee-spec/javadocs/)
    -   [tutorial](https://javaee.github.io/tutorial/)
    -   [javaee8-samples](https://github.com/javaee-samples/javaee8-samples)
-   OpenJDK
    -   [官网](https://hg.openjdk.java.net/)
    -   [GitHub](https://github.com/openjdk/jdk)
    -   [Gitee]()
-   [Java SE 概念图，起点](https://docs.oracle.com/javase/8/docs/)

![20200202-185537](images/20200202-185537.png)


----

##  学习Java 技术 1 2 3

-   完成思维导图

首先完成思维导图，把相关接口类梳理，找到其中的`组织`方式。

// 那些大佬写这些实现，肯定是有`思路`，一定的`逻辑`的啊，不然怎么写？你写业务代码也要想下该如何设计组织吧。

I/O、并发等接口类比较多，也不能直接找出什么规律，它的特点是当先这么多接口类都是多次更新增加进来的，可以考虑从API变更记录找线索

集合框架，这种比较有规律，从官方概要介绍文档就可以找到其中的规律，API变更可以不考虑。

在完成导图的基础上，使用自己的语言表达`总结`一下。

1.  [官方文档](https://docs.oracle.com/javase/8/docs/)

 在 Java SE 概念图，可以看到 Java 组成组件，记录一下 学习 I/O 的过程。

[Java I/O, NIO, and NIO.2](https://docs.oracle.com/javase/8/docs/technotes/guides/io/index.html) 把 Java 输入/输出 相关文档都列在这里了，有概述、教程、API 规范、示例和更多资料等等。

-   概述在全局整体上介绍，有个直观认知
-   教程描述了官方是怎么介绍这个技术，大概有什么东西
-   API规范，就把相关的 包集中在一起，API 文档是代码说明，了解这个会有一个直观认识
-   示例，把语法功能演示了一下
-   更多资料，补充资料，更多的是这个技术的来源和理论支撑

2.  补充资料

B站是个学习的好地方，资源贼多

各种介绍Java书中关于I/O部分，最好是专门介绍这个，比较有深度。这些是注解，换个角度和方式描述，会发现，都在上面范围之内

论如何读技术书。。

-   博文输出

3.  输出总结

费曼学习法+思维导图法

思维导图法：把看起来直面而杂乱无章的各个点，用某种规则串联起来，形成一条条线，组成一块块，逻辑关系理顺，对于自己理解记忆很有帮助，从官方文档输出

费曼学习法：把学习的东西，用自己的语言表达出来，遇到卡顿或说不清就表明这部分还理解不到位，再去学习这部分，不断重复这个过程，直到能全部说清楚，所以，`博文`写出来大概就算理解清楚了。从 注解说明 输出

考题解答：被人承认的东西才算有价值

4.  应用实践

Java 框架对该部分的应用，框架都是基于 Java 语法构建而成

Java 框架 也可以使用这个过程。

5.  复制复制

把上面的过程在 Java 其他部分复制，就逐渐覆盖所需要关注的 Java 技术了

----

相对独立涉及又广的部分：API、经验分享、原理源码

---