#   Java程序员修炼之道

>   第2章：新I/O

被称为"再次更新的I/O"或NIO.2(即JSR-203)。NIO.2是一组新的类和方法，主要存在于 java.nio包内，主要有点：

-   完全取代了 java.io.File 与文件系统的交互
-   提供了新的异步处理类，让你无需手动配置线程池和其他底层并发控制，便可在后台线程中执行文件和网络I/O操作
-   引入了新的 Network-Channel 构造方法，简化了套接字(Socket)与通道的编码工作

从了解新的文件系统抽象层开始，即先了解 Path 和他的辅助类，在 Path 之上，接触常用的文件系统操作，比如复制和移动文件。

-   [Java I/O 简史](10x.md)
    -   Java 1.0 到 1.3：没有完整的I/O支持
    -   在 Java 1.4 中引入的 NIO，对文件和目录处理不够
    -   下一代 I/O--NIO.2
-   [文件I/O的基石：Path](11x.md)
    -   创建一个 Path
    -   从 Path 中获取信息
    -   移除冗余项
    -   转换 Path
    -   NIO.2 Path 和 Java 已有的 File 类
-   [处理目录和目录树](12x.md)
    -   在目录中查找文件
    -   遍历目录树
-   [NIO.2的文件系统I/O](13x.md)
    -   创建和删除文件
    -   文件的复制和移动
    -   文件的属性
    -   快速读写数据
    -   文件修改通知
    -   SeekableByteChannel
-   [异步I/O操作：多个后台线程读写文件、套接字和通道中的数据](14x.md)
    -   将来式
    -   回调式
-   [Socket 和 Channel 的整合](15x.md)
    -   NetworkChannel
    -   MulticastChannel