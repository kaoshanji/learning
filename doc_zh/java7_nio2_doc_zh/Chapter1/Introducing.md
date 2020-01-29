# Path类概述

路径位于文件系统中，该文件系统“以某种形式的媒体（通常是一种或多种）存储和组织文件。

更多的硬盘，以便可以轻松地检索它们。” 

通过java.nio.file.FileSystems可以访问文件系统，该类用于获取我们要处理的java.nio.file.FileSystem。 FileSystems包含以下两个重要方法

-   getDefault()

这是一个静态方法，可将默认的FileSystem返回，通常是操作系统的默认文件系统。

-   getFileSystem(URI uri)

这是一个静态方法，可从中返回文件系统与给定URI模式匹配的一组可用文件系统提供程序。

Path 可以使用任何文件系统的任何文件系统（FileSystem）中操作文件存储位置（java.nio.file.FileStore；此类表示基础
存储）。 

默认情况下（通常），路径引用默认文件中的文件系统（计算机的文件系统），但NIO.2是完全模块化的-一个内存，网络或虚拟中数据的 FileSystem 实现文件系统完全适合NIO.2。 NIO.2为我们提供了所有文件系统我们可能需要对文件，目录或链接执行的功能。


Path 类是著名的java.io.File类的升级版本，但是 File 类具有保留了一些特定的操作，因此它不被弃用，不能被认为已经过时。

此外，从Java 7开始，两个类都可用，这意味着程序员可以混合使用它们的功能来获得最佳的I / O API。

Java 7为它们之间的转换提供了一个简单的API。 记住您必须执行以下操作是被允许的。

```Java
import java.io.File;
…
File file = new File("index.html");
```

好吧，那些日子已经过去了，因为使用Java 7可以做到这一点：

```Java
import java.nio.file.Path;
import java.nio.file.Paths;
…
Path path = Paths.get("index.html");
```

仔细观察，Path 是文件系统中路径的程序表示。 路径字符串包含文件名，目录列表和与操作系统相关的文件定界符（例如，反斜杠“ \”位于Microsoft Windows，在Solaris和Linux上为反斜杠“ /”），这表示路径不是系统独立的，因为它基于系统相关的字符串路径。 

由于Path基本上是一个字符串，因此引用的资源可能不存在。

----

