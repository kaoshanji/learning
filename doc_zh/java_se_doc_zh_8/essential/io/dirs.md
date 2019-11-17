# 创建和阅读目录

前面讨论过的一些方法，例如`delete`，处理文件，链接*和*目录。但是如何列出文件系统顶部的所有目录？如何列出目录的内容或创建目录？

本节介绍以下特定于目录的功能：

- [列出文件系统的根目录](#listall)
- [创建目录](#create)
- [创建临时目录](#createTemp)
- [列出目录的内容](#listdir)
- [使用Globbing过滤目录列表](#glob)
- [编写自己的目录过滤器](#filter)

## 列出文件系统的根目录

您可以使用该[`FileSystem.getRootDirectories`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getRootDirectories--)方法列出文件系统的所有根目录 。此方法返回一个`Iterable`，这使您可以使用 [增强的for](../../java/nutsandbolts/for.html)语句迭代所有根目录。

以下代码段打印默认文件系统的根目录：

```java
Iterable<Path> dirs = FileSystems.getDefault().getRootDirectories();
for (Path name: dirs) {
    System.err.println(name);
}
```

## 创建目录

您可以使用该[`createDirectory(Path, FileAttribute)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createDirectory-java.nio.file.Path-java.nio.file.attribute.FileAttribute...-)方法创建新目录 。如果未指定any `FileAttributes`，则新目录将具有默认属性。例如：

```java
Path dir = ...;
Files.createDirectory(path);
```

以下代码段在具有特定权限的POSIX文件系统上创建新目录：

```java
Set<PosixFilePermission> perms =
    PosixFilePermissions.fromString("rwxr-x---");
FileAttribute<Set<PosixFilePermission>> attr =
    PosixFilePermissions.asFileAttribute(perms);
Files.createDirectory(file, attr);
```

要在一个或多个父目录可能尚不存在时创建多个级别的目录，可以使用方便方法 [`createDirectories(Path, FileAttribute)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createDirectories-java.nio.file.Path-java.nio.file.attribute.FileAttribute...-)。与`createDirectory(Path, FileAttribute<?>)`方法一样，您可以指定一组可选的初始文件属性。以下代码段使用默认属性：

```java
Files.createDirectories(Paths.get("foo/bar/test"));
```

根据需要，从上到下创建目录。在该`foo/bar/test`示例中，如果该`foo`目录不存在，则创建该目录。接下来，`bar`如果需要，将`test`创建目录，最后创建目录。

创建一些（但不是全部）父目录后，此方法可能会失败。

## 创建临时目录

您可以使用以下`createTempDirectory`方法之一创建临时目录：

- [`createTempDirectory(Path, String, FileAttribute...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createTempDirectory-java.nio.file.Path-java.lang.String-java.nio.file.attribute.FileAttribute...-)
- [`createTempDirectory(String, FileAttribute...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createTempDirectory-java.lang.String-java.nio.file.attribute.FileAttribute...-)

第一种方法允许代码指定临时目录的位置，第二种方法在默认的temporary-fle目录中创建新目录。

## 列出目录的内容

您可以使用该[`newDirectoryStream(Path)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#newDirectoryStream-java.nio.file.Path-)方法列出目录的所有内容 。此方法返回实现[`DirectoryStream`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/DirectoryStream.html)接口的对象 。实现`DirectoryStream`接口的类也实现`Iterable`，因此您可以遍历目录流，读取所有对象。这种方法适用于非常大的目录。

------

**记住：**  返回的是一个。如果您没有使用with-resources语句，请不要忘记关闭块中的流。在与资源语句采用这种照顾你。

以下代码段显示了如何打印目录的内容：

```java
Path dir = ...;
try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
    for (Path file: stream) {
        System.out.println(file.getFileName());
    }
} catch (IOException | DirectoryIteratorException x) {
    // IOException can never be thrown by the iteration.
    // In this snippet, it can only be thrown by newDirectoryStream.
    System.err.println(x);
}
```

`Path`迭代器返回的对象是针对目录解析的条目的名称。所以，如果你列出的内容`/tmp`目录，则这些条目的形式返回`/tmp/a`，`/tmp/b`等。

此方法返回目录的全部内容：文件，链接，子目录和隐藏文件。如果您希望对检索的内容更具选择性，可以使用其他`newDirectoryStream`方法之一，如本页后面所述。

请注意，如果在目录迭代期间存在异常，`DirectoryIteratorException`则抛出`IOException`原因。迭代器方法不能抛出异常异常。

## 使用Globbing过滤目录列表

如果只想获取每个名称与特定模式匹配的文件和子目录，可以使用 [`newDirectoryStream(Path, String)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#newDirectoryStream-java.nio.file.Path-java.lang.String-)提供内置glob过滤器的方法来实现。如果您不熟悉glob语法，请参阅[什么是](fileOps.html#glob)全局 [？](fileOps.html#glob)

例如，以下代码段列出了与Java相关的文件：*.class*，*.java*和*.jar*文件：

```java
Path dir = ...;
try (DirectoryStream<Path> stream =
     Files.newDirectoryStream(dir, "*.{java,class,jar}")) {
    for (Path entry: stream) {
        System.out.println(entry.getFileName());
    }
} catch (IOException x) {
    // IOException can never be thrown by the iteration.
    // In this snippet, it can // only be thrown by newDirectoryStream.
    System.err.println(x);
}
```

## 编写自己的目录过滤器

您可能希望根据模式匹配以外的某些条件过滤目录的内容。您可以通过实现[`DirectoryStream.Filter`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/DirectoryStream.Filter.html)接口来创建自己的过滤器 。此接口由一个方法组成，该方法`accept`确定文件是否满足搜索要求。

例如，以下代码段实现了仅检索目录的过滤器：

```java
DirectoryStream.Filter<Path> filter =
    newDirectoryStream.Filter<Path>() {
    public boolean accept(Path file) throws IOException {
        try {
            return (Files.isDirectory(path));
        } catch (IOException x) {
            // Failed to determine if it's a directory.
            System.err.println(x);
            return false;
        }
    }
};
```

创建过滤器后，可以使用该[`newDirectoryStream(Path, DirectoryStream.Filter)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#newDirectoryStream-java.nio.file.Path-java.nio.file.DirectoryStream.Filter-)方法调用它 。以下代码段使用`isDirectory`过滤器仅将目录的子目录打印到标准输出：

```java
Path dir = ...;
try (DirectoryStream<Path>
                       stream = Files.newDirectoryStream(dir, filter)) {
    for (Path entry: stream) {
        System.out.println(entry.getFileName());
    }
} catch (IOException x) {
    System.err.println(x);
}
```

此方法仅用于过滤单个目录。但是，如果要查找文件树中的所有子目录，可以使用“ [遍历文件树”](walk.html)的机制 。