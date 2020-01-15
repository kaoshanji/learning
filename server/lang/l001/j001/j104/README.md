#   I/O

输入/输出，数据来源不同，处理过程不同，目的地也不同。

从外部文件把数据读取到 JVM 叫输入，把 JVM 里的数据写进外部文件叫输出，这是在堆内操作。

数据可以不被读取到 JVM 而直接在 OS 内存里处理，是堆外内存，由 OS 操作。

Linux 上所有的设备都是文件，包括网络。

在Java中，所有I/O都是通过输入/输出流来处理的，这些流使你可以按照统一的方式来处理与各种数据源之间的通信，例如文件、网络连接和内存块。

这么多的类接口都是为了解决一个问题：I/O，主要分为两类，数据传输的`方式`(文件/网络)和传输数据的`格式`(字节/字符)，然后，针对特定场景进行优化(BIO/NIO/AIO)



##  目录
-   [编程里的输入/输出](http://assets.processon.com/chart_image/5de475dae4b0d1f8f2c71681.png)
-   [Java里的输入/输出](http://assets.processon.com/chart_image/5ba305fce4b0534c9be411a6.png)
-   [IO思维导图](http://assets.processon.com/chart_image/5df5c25be4b004cc9a304392.png)
-   [NIO思维导图](http://assets.processon.com/chart_image/5df5c31ae4b0cfc88c3831c0.png)
-   [NIO2思维导图](http://assets.processon.com/chart_image/5df5c378e4b06f5f145b8be2.png)
-   IO UML 图
    -   [InputStream](http://assets.processon.com/chart_image/5df5ca0fe4b06f5f145b92c3.png)
    -   [OutputStream](http://assets.processon.com/chart_image/5dfa41ebe4b010171a4c8665.png)
    -   [Reader](http://assets.processon.com/chart_image/5dfb7b90e4b0fa593e07d4f1.png)
    -   [Writer](http://assets.processon.com/chart_image/5dfb7f06e4b06c8b0bb66673.png)
-   [NIO UML 图](http://assets.processon.com/chart_image/5dfe1404e4b0250e8ae62d94.png)


##  我要输出
-   [概述](bbb/README.md)
-   [IO](bbb/101x.md)
-   [NIO](bbb/201x.md)
-   [NIO.2](bbb/301x.md)


##  相关框架
-   [Netty](https://netty.io/)
-   [Tomcat](http://tomcat.apache.org/)
-   [Dubbo](http://dubbo.apache.org/zh-cn/index.html)
-   [grpc](https://grpc.io/)


##  参考资料
-   [Java核心技术 卷2 高级特性 原书第10版](books/b001/README.md)
-   [Java编程思想 第4版](books/b002/README.md)
-   [Java程序员修炼之道](books/b003/README.md)
-   [深入理解Java 7：核心技术与最佳实践](books/b004/README.md)
-   [Java编程的逻辑](books/b005/README.md)
-   [Java特种兵上](books/b006/README.md)
-   [Java技术手册 第6版](books/b007/README.md)
-   [深入分析Java Web技术内幕](books/b008/README.md)
-   [Java NIO 中文版](books/b009/README.md)
-   [NIO与Socket编程技术指南](books/b010/README.md)

