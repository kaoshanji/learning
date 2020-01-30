# 小结

在本章中，您学习了如何使用NIO.2异步通道API。 

经过简短的在介绍同步I / O和异步I / O之间的差异时，您会收到详细的此API结构的概述。 

之后，您看到了理论付诸实践，从java.nio.channels.AsynchronousChannel接口，该接口使用异步I / O扩展了通道运营支持。 

实现此接口以进行异步操作的三个类然后提供了文件和套接字：AsynchronousFileChannel，AsynchronousSocketChannel和
AsynchronousServerSocketChannel。 

当前不可用的AsynchronousDatagramChannel类为在本章中也进行了介绍，以防将来再次出现。 

本章还介绍了AsynchronousChannelGroup，包括异步通道组的概念。 

本章总结了有关开发基于异步的应用程序的一些技巧。

----