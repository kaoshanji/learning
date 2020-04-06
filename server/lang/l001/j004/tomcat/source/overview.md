# Apache Tomcat 概述


##  Web 应用、Servlet规范、Tomcat

Java Servlet 相关规范组成了Java版 Web 应用。

Web 应用是基于HTTP协议实现，模式是客户端根据地址发送请求，服务端以资源作为响应。

Java Servlet 标准规范只是一个接口，没有具体实现，Tomcat 是它的一个实现，本质是一款 Java Servlet 容器

-   Tomcat 主要优点
    -   轻量，仅依赖JDK，不需要其他额外资源
    -   开源，可以定制化


##  实现依赖
-   Java规范
    -   Servlet 4.0
    -   JSP 2.3
    -   EL 3.0
    -   WebSocket 1.1
    -   JASPIC
-   应用层协议
    -   HTTP/1.1
    -   HTTP/2
    -   AJP(本地协议)
-   Java技术
    -   Java I/O 模型
    -   Java 并发


----


-   理论背景

提供的功能：处理HTTP请求响应，HTTP内容和Java对象来回转换，基于Java Web技术规范实现请求的响应。

HTTP服务器执行流程：接入请求、调用逻辑，返回响应


2.  业务流程

接收HTTP请求，调用应用逻辑

