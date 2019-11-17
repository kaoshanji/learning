# 基本I/O

本课程介绍用于基本I / O的Java平台类。它首先关注*I / O Streams*，这是一个强大的概念，可以大大简化I / O操作。本课程还介绍了序列化，它允许程序将整个对象写入流并再次读取它们。然后，本课将介绍文件I / O和文件系统操作，包括随机访问文件。

本`I/O Streams`节中涉及的大多数类都在`java.io`包中。本`File I/O`节中涉及的大多数类都在`java.nio.file`包中。

## [I / O流](streams.md)

- [Byte Streams](bytestreams.md)处理原始二进制数据的I / O.
- [Character Streams](charstreams.md)处理字符数据的I / O，自动处理与本地字符集的转换。
- [缓冲流](buffers.md)通过减少对本机API的调用次数来优化输入和输出。
- [扫描和格式化](scanfor.md)允许程序读取和写入格式化文本。
- [命令行](cl.md)中的[I / O](cl.md)描述了标准流和控制台对象。
- [数据流](datastreams.md)处理原始数据类型和`String`值的二进制I / O.
- [对象流](objectstreams.md)处理[对象的](objectstreams.md)二进制I / O.

## [文件I / O（以NIO.2为特色）](fileio.md)

- [什么是路径？](path.md)检查文件系统上路径的概念。
- [Path类](pathClass.md)引入了`java.nio.file`包的基石类。
- [Path Operations](pathOps.md)查看`Path`类中处理语法操作的方法。
- [文件操作](fileOps.md)引入了许多文件I / O方法的共同概念。
- [检查文件或目录](check.md)显示了如何检查文件的存在及其可访问性级别。
- [删除文件或目录](delete.md)。
- [复制文件或目录](copy.md)。
- [移动文件或目录](move.md)。
- [管理元数据](fileAttr.md)说明了如何读取和设置文件属性。
- [读取，写入和创建文件](file.md)显示了用于读取和写入文件的流和通道方法。
- [随机访问文件](rafs.md)显示如何以非顺序方式读取或写入文件。
- [创建和读取目录](dirs.md)涵盖了特定于目录的API，例如如何列出目录的内容。
- [链接，符号或其他](links.md)包含特定于符号和硬链接的问题。
- [遍历文件树](walk.md)演示了如何递归访问[文件树中的](walk.md)每个文件和目录。
- [查找文件](find.md)显示如何使用模式匹配搜索文件。
- [查看“更改目录”](notification.md)显示了如何使用监视服务来检测在一个或多个目录中添加，删除或更新的文件。
- [其他有用的方法](misc.md)涵盖了本课程其他部分不适用的重要API。
- [传统文件I / O代码](legacy.md)显示`Path`如果您使用`java.io.File`该类的旧代码，如何利用功能。提供了将`java.io.File`API 映射到`java.nio.file`API的表。

## [摘要](summary.md)

这条线索所涵盖的关键点的摘要。