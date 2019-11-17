# 其他有用的方法

一些有用的方法不适用于本课程的其他地方，并在此处介绍。本节包括以下内容：

- [确定MIME类型](#mime)
- [默认文件系统](#default)
- [路径字符串分隔符](#separator)
- [文件系统的文件存储](#stores)

## 确定MIME类型

要确定文件的MIME类型，您可能会发现该 [`probeContentType(Path)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#probeContentType-java.nio.file.Path-)方法很有用。例如：

```java
try {
    String type = Files.probeContentType(filename);
    if (type == null) {
        System.err.format("'%s' has an" + " unknown filetype.%n", filename);
    } else if (!type.equals("text/plain") {
        System.err.format("'%s' is not" + " a plain text file.%n", filename);
        continue;
    }
} catch (IOException x) {
    System.err.println(x);
}
```

请注意，`probeContentType`如果无法确定内容类型，则返回null。

此方法的实现具有高度平台特定性，并非绝对可靠。内容类型由平台的默认文件类型检测器确定。例如，如果检测器确定文件的内容类型`application/x-java`基于`.class`扩展名，则可能会被欺骗。

[`FileTypeDetector`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/spi/FileTypeDetector.html)如果默认值不足以满足您的需求，您可以提供自定义 。

该 [`Email`](examples/Email.java)示例使用该`probeContentType`方法。

## 默认文件系统

要检索默认文件系统，请使用该 [`getDefault`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystems.html#getDefault--)方法。通常，此`FileSystems`方法（注意复数）被链接到其中一个`FileSystem`方法（注意单数），如下所示：

```java
PathMatcher matcher =
    FileSystems.getDefault().getPathMatcher("glob:*.*");
```

## 路径字符串分隔符

POSIX文件系统的路径分隔符是正斜杠，`/`对于Microsoft Windows是反斜杠，`\`。其他文件系统可能使用其他分隔符。要检索`Path`默认文件系统的分隔符，可以使用以下方法之一：

```java
String separator = File.separator;
String separator = FileSystems.getDefault().getSeparator();
```

该 [`getSeparator`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getSeparator--)方法还用于检索任何可用文件系统的路径分隔符。

## 文件系统的文件存储

文件系统具有一个或多个文件存储来保存其文件和目录。的*文件存储*表示底层存储设备。在UNIX操作系统中，每个安装的文件系统都由文件存储表示。在Microsoft Windows中，每个卷都由文件存储：`C:`，`D:`等等表示。

要检索文件系统的所有文件存储列表，可以使用该 [`getFileStores`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getFileStores--)方法。此方法返回一个`Iterable`，它允许您使用 [增强的for](../../java/nutsandbolts/for.html)语句迭代所有根目录。

```java
for (FileStore store: FileSystems.getDefault().getFileStores()) {
   ...
}
```

如果要检索特定文件所在的文件存储，请使用类中的 [`getFileStore`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#getFileStore-java.nio.file.Path-)方法`Files`，如下所示：

```java
Path file = ...;
FileStore store= Files.getFileStore(file);
```

该 [`DiskUsage`](examples/DiskUsage.java)示例使用该`getFileStores`方法。