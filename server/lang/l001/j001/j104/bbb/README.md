#   Java I/O

解决的问题：人机、机器之间交换数据，数据只有交互才能产生价值

速度在不断的提高，针对的场景越来越具体化。

----

##  特性

-   Java I/O 抽象出来的对象：流和通道
-   Java I/O 具体操作的对象：文件系统和套接字
-   Java I/O 三种模型的术语：同步与异步、阻塞与非阻塞

流可以操作文件系统和套接字，是同步阻塞的

通道可以操作文件系统和套接字，可以是同步非阻塞(阻塞)或异步

----

##  目录

### 语法API

梳理Java I/O 主要功能

-   内容
    -   [概述](100x.md)

### Java 网络编程模板

描述主要步骤+代码，完整代码在 ../jdkx.io.template 包下，代码仅做示例感受一下

主要的逻辑，客户端发送"服务端你好，我是客户端A，请告诉我当前时间."，服务端响应："客户端好，现在服务端的时间是：xxx"，其他全部都是套路模板代码，如同JDBC、Servlet一样。

-   Java  BIO/NIO/AIO
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
    -   [JZlib](http://www.jcraft.com/jzlib/)
    -   [lucene](https://lucene.apache.org/)

----