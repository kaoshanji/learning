# Introduction

本书涵盖了开发基于NIO.2的应用程序涉及的所有重要方面。

它提供明确说明，以充分利用NIO.2，并提供许多练习和案例研究，以助您一臂之力。

使用新的 I/O 功能来设置 Java 7应用程序。

您将学习开发NIO.2应用程序，从简单但必不可少的东西开始，然后逐步发展到套接字等复杂功能和异步渠道。

##  这本书适合谁

本书适用于Java 7新手和有一定经验的Java程序员。

具有Java 7的经验。对于开头的章节（第1-5章），熟悉Java就足够了。语法，并了解如何打开和运行NetBeans项目。

对于第6-10章，有一些知识关于一些基本的编程概念，例如递归，多线程和并发，Internet协议和网络应用程序至关重要。

### 本书涵盖的内容

本节包含各章内容的简要概述。

### Path

第1章：在这里，您将了解用于处理文件路径的新API。

您现在使用java.nio.file.Path类，用于在任何文件系统中操作文件。

在本章中，我将介绍诸如声明路径实例和语法操作。

### 通过新的 java.nio.file.attribute API获取/设置文件元数据（包括POSIX）

第2章：使用NIO.2，您可以管理有关文件元数据的更多详细信息。

属性被分为几类，现在它们也涵盖了POSIX系统。

第2章深入探讨了这些类别。

### 管理符号链接和硬链接

第3章：NIO.2现在揭示了Java的未开发领域。

本章介绍如何创建，遵循和操纵符号链接和硬链接。

### 通过新的 java.nio.file.Files 处理文件和目录API

第4章：在这里您将学习涉及文件/目录的最常见任务，例如创建，读取，编写，更新等。

您将学习如何检查文件状态和循环文件存储，以及如何使用临时文件，以及如何删除，复制和移动文件和目录。

### 使用 FileVisitor API开发递归文件操作

第5章：是否需要复制，移动或删除整个目录？您来对地方了。

第五章向您展示如何通过全新的FileVisitor API进行所有操作。

您还将了解如何开发一个搜索文件工具。

### 探索 Watch Service API和文件更改通知

第6章：是否要监视文件/目录的更改，例如创建，删除或修改的条目？这是监视服务最擅长的。

在这一章当中我还介绍了观看打印纸盘和调查摄像机。

在这里，您可以发现新的Watch Service API的灵活性和通用性。

### 使用新的 SeekableByteChannel API来处理随机访问文件

第7章：随机访问文件（RAF）是右侧的强大工具。

本章介绍新的SeekableByteChannel API，并提供了许多利用其方法的示例。

### 开发基于阻塞/非阻塞套接字的应用程序

第8章：了解如何以阻塞和非阻塞样式开发基于Java网络的应用程序。

详细介绍TCP和UDP，并在整个套接字中介绍套接字编程的重要方面本章。

### 使用NIO.2冠冕上的宝石：异步通道 API

第9章：这是我个人最喜欢的一章。 很高兴写，希望您能喜欢有用，因为我发现它很有趣。 

使用异步通道API，您可以开发基于异步网络的具有一组类和选项的Java应用程序。 

异步通道API实在太难了！

### 使用Zip文件系统提供程序并编写自定义文件系统提供者

第10章：这最后一章以使用新的Zip文件系统的示例结束本书提供者。 

我还介绍了有关编写自定义文件系统提供程序的一些注意事项。 

第10章还包含一个表格，其中包含java.io.File和java.nio.file.Path API之间的详细转换。

----