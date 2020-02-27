#   Java I/O

解决的问题：人机、机器之间交换数据，数据只有交互才能产生价值

速度在不断的提高，针对的场景越来越具体化。

-   [编程里的输入/输出](http://assets.processon.com/chart_image/5de475dae4b0d1f8f2c71681.png)

----

##  特性

-   Java I/O 抽象出来的对象：流和通道
-   Java I/O 具体操作的对象：文件系统和套接字
-   Java I/O 三种模型的术语：同步与异步、阻塞与非阻塞

流可以操作文件系统和套接字，是同步阻塞的

通道可以操作文件系统和套接字，可以是同步非阻塞(阻塞)或异步

----

##  Java I/O, NIO, and NIO.2

-   [Java I/O `导图`](https://www.processon.com/view/link/5e52272fe4b07f2b831eed43)
-   [Java NIO `导图`](https://www.processon.com/view/link/5e522760e4b0d4dc876904a1)

-   Java I / O支持包含在java.io和 java.nio 软件包中。这些软件包一起包括以下功能：
    -   通过数据流，序列化和文件系统进行输入和输出。
    -   字符集，解码器和编码器，用于在字节和Unicode字符之间进行翻译。
    -   访问文件，文件属性和文件系统。
    -   使用异步或多路复用的非阻塞I / O构建可扩展服务器的API
-   [API规范](https://docs.oracle.com/javase/8/docs/technotes/guides/io/index.html)
-   [java.io](https://docs.oracle.com/javase/8/docs/api/java/io/package-summary.html)：支持系统输入和输出以及对象序列化和文件访问

通过数据流，序列化和文件系统提供系统输入和输出

-   [java.nio](111x.md)：为大容量存储器操作定义缓冲区，可以通过直接缓冲区实现高性能
-   [java.nio.channels](112x.md):定义 channel，这是能够执行I/O操作的新抽象；定义用于多路复用非阻塞I/O的选择器；包括异步I/O
-   [java.nio.channels.spi](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/spi/package-summary.html)：java.nio.channels软件包的服务提供者类

只有定义新选择器提供程序或异步通道提供程序的开发人员才需要直接使用此程序包

-   [java.nio.file](113x.md)：定义用于访问文件、文件属性和文件系统

-   [java.nio.file.attribute](114x.md)：定义用于访问文件系统属性
-   [java.nio.file.spi](https://docs.oracle.com/javase/8/docs/api/java/nio/file/spi/package-summary.html)：java.nio.file软件包的服务提供者类

只有定义新文件系统提供程序或文件类型检测器的开发人员才需要直接使用此软件包

-   [java.nio.charset](115x.md)：定义字符集，编解码器，用于在字节和Unicode字符之间进行转换
-   [java.nio.charset.spi](https://docs.oracle.com/javase/8/docs/api/java/nio/charset/spi/package-summary.html)：java.nio.charset软件包的服务提供者类。

只有定义新字符集的开发人员才需要直接使用此程序包

-   [com.sun.nio.sctp](116x.md)：流控制传输协议

##  Java网络
-   [Java I/O-net `导图`](https://www.processon.com/view/link/5e52274ae4b07f2b831eed6e)
-   [官方：](https://docs.oracle.com/javase/8/docs/technotes/guides/net/overview/overview.html)[网络概述](240x.md)
-   [API规范](https://docs.oracle.com/javase/8/docs/technotes/guides/net/index.html)
-   [java.net](121x.md)：提供用于实现联网应用程序的类
-   [javax.net](https://docs.oracle.com/javase/8/docs/api/javax/net/package-summary.html)：提供用于网络应用程序的类

这些类包括用于创建套接字的工厂。使用套接字工厂，您可以封装套接字的创建和配置行为

-   [javax.net.ssl](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/package-summary.html)：提供安全套接字包的类

使用安全套接字类，您可以使用SSL或相关的安全协议进行通信，以可靠地检测到网络字节流中引入的任何错误，并可以选择加密数据和/或验证通信对等方

-   [com.sun.net.httpserver](122x.md)：提供一个简单的高级Http服务器API，可用于构建嵌入式HTTP服务器
-   [com.sun.net.httpserver.spi](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/spi/package-summary.html)：提供可插入的服务提供程序接口，该接口允许将HTTP服务器实现替换为其他实现
-   [jdk.net](https://docs.oracle.com/javase/8/docs/jre/api/net/socketoptions/spec/jdk/net/package-summary.html)：java.net和java.nio.channels 套接字类的特定于平台的套接字选项

----

##  目录

### 语法API

梳理Java I/O 主要功能

-   内容
    -   [概述](100x.md)

### Java 网络编程模板

描述主要步骤+代码，完整代码在 ../jdkx.io.template 包下，代码仅做示例感受一下

主要的逻辑，客户端发送"服务端你好，我是客户端A，请告诉我当前时间."，服务端响应："客户端好，现在服务端的时间是：xxx"，其他全部都是套路模板代码，如同JDBC、Servlet一样。

-   Java  BIO/NIO/AIO 网络代码流程
    -   [Java BIO 网络代码流程](210x.md)
    -   [Java NIO 网络代码流程](220x.md)
    -   [Java AIO 网络代码流程](230x.md)
-   参考应用实践
    -   [Netty](https://netty.io/)
    -   [Tomcat](http://tomcat.apache.org/)

----

##  相关框架
-   [RFC](https://www.ietf.org/)：协议标准化文档
-   HTTP客户端
    -   [okhttp](https://square.github.io/okhttp/)
    -   [HttpComponents](http://hc.apache.org/)
-   文件操作
    -   [lucene](https://lucene.apache.org/)

----