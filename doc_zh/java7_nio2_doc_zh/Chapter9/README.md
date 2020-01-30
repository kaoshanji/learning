# 异步通道

我们终于达到了NIO.2中引入的最强大的功能，即异步通道API。

如您将在本章中看到，异步I / O（AIO）Java 7之旅始于java.nio.channels.AsynchronousChannel接口，该接口使用异步I / O扩展了通道运营支持。

该接口由三个类实现：AsynchronousFileChannel，AsynchronousSocketChannel和AsynchronousServerSocketChannel。

有第四等AsynchronousDatagramChannel，已在Java 7 beta版本中添加，然后在Java中删除7最终发布；在撰写本文时，该类不可用，但是它可能会出现在将来的Java 7版本中，因此本章将对其进行足够的深入介绍，以使您了解其用途。

这些类的风格与NIO.2通道API。

此外，还有一个名为AsynchronousByteChannel的异步通道可以读取和写入字节，并作为AsynchronousChannel的子接口（此子接口）站立由AsynchronousSocketChannel类实现）。

而且，新的API引入了一个类名为AsynchronousChannelGroup，在其中介绍了异步通道组的概念。

每个异步通道属于哪个通道组（默认通道或指定通道）共享一个Java线程池。

这些线程接收执行I / O事件的指令，然后分派结果交给完成处理程序。

所有的努力都是为了处理完成启动异步I / O操作。

在本章中，您将从Java的角度看到异步机制。

你会看见Java如何实现异步I / O的概图，之后您将开发相关的文件和套接字的应用程序。

我们将从探索文件的异步I / O开始AsynchronousFileChannel类，并继续用于TCP套接字和UDP套接字的异步I / O。

但是，在介绍API功能之前，请先简要介绍一下同步I / O和异步I / O正常。


