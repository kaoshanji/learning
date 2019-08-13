# 检查文件或目录

您有一个`Path`表示文件或目录的实例，但该文件是否存在于文件系统中？它可读吗？可写？可执行文件？

## 验证文件或目录的存在

`Path`类中的方法是语法，意味着它们在`Path`实例上运行。但最终您必须访问文件系统以验证特定`Path`存在或不存在。您可以使用 [`exists(Path, LinkOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#exists-java.nio.file.Path-java.nio.file.LinkOption...-)和 [`notExists(Path, LinkOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#notExists-java.nio.file.Path-java.nio.file.LinkOption...-)方法完成此操作。注意，这`!Files.exists(path)`不等于`Files.notExists(path)`。当您测试文件存在时，可能会有三个结果：

- 该文件已验证存在。
- 该文件已验证不存在。
- 文件的状态未知。当程序无权访问该文件时，可能会发生此结果。

如果同时`exists`和`notExists`回报`false`，该文件的存在，无法验证。

## 检查文件可访问性

要验证需要的程序可以访问一个文件，你可以使用 [`isReadable(Path)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#isReadable-java.nio.file.Path-)， [`isWritable(Path)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#isWritable-java.nio.file.Path-)和 [`isExecutable(Path)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#isExecutable-java.nio.file.Path-)方法。

以下代码段验证特定文件是否存在以及程序是否能够执行该文件。

```java
Path file = ...;
boolean isRegularExecutableFile = Files.isRegularFile(file) &
         Files.isReadable(file) & Files.isExecutable(file);
```

------

**注意：**  一旦这些方法中的任何一个完成，就无法保证可以访问该文件。许多应用程序中的常见安全漏洞是执行检查然后访问该文件。有关更多信息，请使用您最喜欢的搜索引擎查找（发音为）。

## 检查两个路径是否找到相同的文件

当您有一个使用符号链接的文件系统时，可能有两个不同的路径来定位同一个文件。该 [`isSameFile(Path, Path)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#isSameFile-java.nio.file.Path-java.nio.file.Path-)方法比较两个路径以确定它们是否在文件系统上找到相同的文件。例如：

```java
Path p1 = ...;
Path p2 = ...;

if (Files.isSameFile(p1, p2)) {
    // Logic when the paths locate the same file
}
```

