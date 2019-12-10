# Apache Tomcat 概述

##  定位

Java Web 技术容器，提供运行环境。

Java Web 技术是Java版Web技术，Web技术是基于HTTP协议的一种应用，模式是客户端发送请求服务端响应，服务器接受请求转接到提供应用的程序，就需要HTTP服务端，这属于网络编程。

- 理论背景

Java Web 技术包含一些组件功能规范，但并没有提供可以使用的实现，只是规定了一些接口。

提供的功能：处理HTTP请求响应，HTTP内容和Java对象来回转换，基于Java Web技术规范实现请求的响应。

Java Web 技术主要包含：Servlet 4.0和JavaServer Pages 2.3 规范

HTTP服务器执行流程：接入请求、调用逻辑，返回响应

##  描述 

1.  了解需求

实现 Servlet 4.0和JavaServer Pages 2.3 规范。

主要技术：

- HTTP协议
- Java 网络编程：Socket
- 并发编程

2.  业务流程

接收HTTP请求，调用应用逻辑

