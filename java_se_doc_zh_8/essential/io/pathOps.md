# 路径操作

所述 [`Path`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)类包括可以用来获得关于所述路径信息的各种方法，该路径的接入元件，所述路径转换为其它形式的，或提取物部分的路径的。还存在用于匹配路径字符串的方法和用于去除路径中的冗余的方法。本课程介绍了这些`Path`方法，有时称为*语法*操作，因为它们在路径*上*运行，不访问文件系统。

本节包括以下内容：

- [创建路径](#create)
- [检索有关路径的信息](#info)
- [从路径中删除冗余](#normal)
- [转换路径](#convert)
- [加入两条道路](#resolve)
- [在两条路径之间创建路径](#relativize)
- [比较两条路径](#compare)

## 创建路径

一个`Path`实例包含用于指定一个文件或目录的位置的信息。在定义时，a `Path`提供有一系列一个或多个名称。可能包含根元素或文件名，但两者都不是必需的。A `Path`可能只包含一个目录或文件名。

您可以`Path`使用（注意复数）辅助类中的以下`get`方法 之一轻松创建对象[`Paths`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Paths.html)：

```java
Path p1 = Paths.get("/tmp/foo");
Path p2 = Paths.get(args[0]);
Path p3 = Paths.get(URI.create("file:///Users/joe/FileTest.java"));
```

该`Paths.get`方法是以下代码的简写：

```java
Path p4 = FileSystems.getDefault().getPath("/users/sally");
```

`/u/joe/logs/foo.log`假设您的主目录是`/u/joe`，或者`C:\joe\logs\foo.log`如果您在Windows上，则以下示例创建。

```java
Path p5 = Paths.get(System.getProperty("user.home"),"logs", "foo.log");
```

## 检索有关路径的信息

您可以将其`Path`视为将这些名称元素存储为序列。目录结构中的最高元素将位于索引0处。目录结构中的最低元素将位于索引处`[n-1]`，其中`n`是名称元素的数量`Path`。方法可用于检索单个元素或`Path`使用这些索引的子序列。

本课中的示例使用以下目录结构。

![示例目录结构](images/io-dirStructure.gif)





示例目录结构

以下代码片段定义了一个`Path`实例，然后调用了几个方法来获取有关该路径的信息：

```java
// None of these methods requires that the file corresponding
// to the Path exists.
// Microsoft Windows syntax
Path path = Paths.get("C:\\home\\joe\\foo");

// Solaris syntax
Path path = Paths.get("/home/joe/foo");

System.out.format("toString: %s%n", path.toString());
System.out.format("getFileName: %s%n", path.getFileName());
System.out.format("getName(0): %s%n", path.getName(0));
System.out.format("getNameCount: %d%n", path.getNameCount());
System.out.format("subpath(0,2): %s%n", path.subpath(0,2));
System.out.format("getParent: %s%n", path.getParent());
System.out.format("getRoot: %s%n", path.getRoot());
```

以下是Windows和Solaris OS的输出：

| 方法调用       | 在Solaris OS中返回 | 在Microsoft Windows中返回 | 评论                                                         |
| -------------- | ------------------ | ------------------------- | ------------------------------------------------------------ |
| `toString`     | `/home/joe/foo`    | `C:\home\joe\foo`         | 返回的字符串表示形式`Path`。如果路径是使用`Filesystems.getDefault().getPath(String)`或创建的`Paths.get`（后者是方便的方法`getPath`），则该方法执行次要的语法清理。例如，在UNIX操作系统中，它会将输入字符串更正`//home/joe/foo`为`/home/joe/foo`。 |
| `getFileName`  | `foo`              | `foo`                     | 返回文件名或name元素序列的最后一个元素。                     |
| `getName(0)`   | `home`             | `home`                    | 返回与指定索引对应的路径元素。第0个元素是最靠近根的路径元素。 |
| `getNameCount` | `3`                | `3`                       | 返回路径中的元素数。                                         |
| `subpath(0,2)` | `home/joe`         | `home\joe`                | 返回`Path`由开始和结束索引指定的（不包括根元素）的子序列。   |
| `getParent`    | `/home/joe`        | `\home\joe`               | 返回父目录的路径。                                           |
| `getRoot`      | `/`                | `C:\`                     | 返回路径的根。                                               |

前面的示例显示了绝对路径的输出。在以下示例中，指定了相对路径：

```java
// Solaris syntax
Path path = Paths.get("sally/bar");
or
// Microsoft Windows syntax
Path path = Paths.get("sally\\bar");
```

以下是Windows和Solaris OS的输出：

| 方法调用       | 在Solaris OS中返回 | 在Microsoft Windows中返回 |
| -------------- | ------------------ | ------------------------- |
| `toString`     | `sally/bar`        | `sally\bar`               |
| `getFileName`  | `bar`              | `bar`                     |
| `getName(0)`   | `sally`            | `sally`                   |
| `getNameCount` | `2`                | `2`                       |
| `subpath(0,1)` | `sally`            | `sally`                   |
| `getParent`    | `sally`            | `sally`                   |
| `getRoot`      | `null`             | `null`                    |

## 从路径中删除冗余

许多文件系统使用“。” 符号表示当前目录，“..”表示父目录。您可能遇到`Path`包含冗余目录信息的情况。也许服务器配置为将其日志文件保存在“ `/dir/logs/.`”目录中，并且您希望`/.`从路径中删除尾随的“ ”表示法。

以下示例均包含冗余：

```java
/home/./joe/foo
/home/sally/../joe/foo
```

该`normalize`方法删除任何冗余元素，包括任何“ `.`”或“ `*directory*/..`”事件。前面的两个示例都标准化为`/home/joe/foo`。

重要的是要注意，`normalize`在清理路径时不检查文件系统。这是一种纯粹的语法操作。在第二个示例中，如果`sally`是符号链接，则删除`sally/..`可能会导致`Path`不再定位目标文件。

要在确保结果找到正确文件的同时清理路径，可以使用该`toRealPath`方法。此方法将在下一节“ [转换路径”中介绍](#convert)。

## 转换路径

您可以使用三种方法来转换`Path`。如果需要将路径转换为可以从浏览器打开的字符串，则可以使用 [`toUri`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html#toUri--)。例如：

```java
Path p1 = Paths.get("/home/logfile");
// Result is file:///home/logfile
System.out.format("%s%n", p1.toUri());
```

该 [`toAbsolutePath`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html#toAbsolutePath--)方法将路径转换为绝对路径。如果传入的路径已经是绝对路径，则返回相同的`Path`对象。`toAbsolutePath`处理用户输入的文件名时，该方法非常有用。例如：

```java
public class FileTest {
    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("usage: FileTest file");
            System.exit(-1);
        }

        // Converts the input string to a Path object.
        Path inputPath = Paths.get(args[0]);

        // Converts the input Path
        // to an absolute path.
        // Generally, this means prepending
        // the current working
        // directory.  If this example
        // were called like this:
        //     java FileTest foo
        // the getRoot and getParent methods
        // would return null
        // on the original "inputPath"
        // instance.  Invoking getRoot and
        // getParent on the "fullPath"
        // instance returns expected values.
        Path fullPath = inputPath.toAbsolutePath();
    }
}
```

该`toAbsolutePath`方法转换用户输入并返回一个`Path`在查询时返回有用值的方法。此方法无需存在该文件。

该 [`toRealPath`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html#toRealPath-java.nio.file.LinkOption...-)方法返回现有文件的*实际*路径。此方法在一个中执行多个操作：

- 如果`true`传递给此方法并且文件系统支持符号链接，则此方法将解析路径中的所有符号链接。
- 如果`Path`是相对的，则返回绝对路径。
- 如果`Path`包含任何冗余元素，则返回删除了这些元素的路径。

如果文件不存在或无法访问，则此方法将引发异常。当您想要处理任何这些情况时，您可以捕获异常。例如：

```java
try {
    Path fp = path.toRealPath();
} catch (NoSuchFileException x) {
    System.err.format("%s: no such" + " file or directory%n", path);
    // Logic for case when file doesn't exist.
} catch (IOException x) {
    System.err.format("%s%n", x);
    // Logic for other sort of file error.
}
```

## 加入两条道路

您可以使用该`resolve`方法组合路径。传入*部分路径*，该路径是不包含根元素的路径，并且该部分路径将附加到原始路径。

例如，请考虑以下代码段：

```java
// Solaris
Path p1 = Paths.get("/home/joe/foo");
// Result is /home/joe/foo/bar
System.out.format("%s%n", p1.resolve("bar"));

or

// Microsoft Windows
Path p1 = Paths.get("C:\\home\\joe\\foo");
// Result is C:\home\joe\foo\bar
System.out.format("%s%n", p1.resolve("bar"));
```

传递方法的绝对路径将`resolve`返回传入的路径：

```java
// Result is /home/joe
Paths.get("foo").resolve("/home/joe");
```

## 在两条路径之间创建路径

编写文件I / O代码时的一个常见要求是能够构建从文件系统中的一个位置到另一个位置的路径。您可以使用该`relativize`方法满足此要求。此方法构造一个源自原始路径并在传入路径指定的位置结束的路径。新路径*相*对于原始路径。

例如，考虑定义为`joe`和的两个相对路径`sally`：

```java
Path p1 = Paths.get("joe");
Path p2 = Paths.get("sally");
```

在没有任何其他信息的情况下，假设`joe`并且`sally`是兄弟姐妹，意味着节点位于树结构中的相同级别。从导航`joe`到`sally`，你会希望先浏览一个水平提高到父节点再向下`sally`：

```java
// Result is ../sally
Path p1_to_p2 = p1.relativize(p2);
// Result is ../joe
Path p2_to_p1 = p2.relativize(p1);
```

考虑一个稍微复杂的例子：

```java
Path p1 = Paths.get("home");
Path p3 = Paths.get("home/sally/bar");
// Result is sally/bar
Path p1_to_p3 = p1.relativize(p3);
// Result is ../..
Path p3_to_p1 = p3.relativize(p1);
```

在此示例中，两条路径共享同一节点`home`。从导航`home`到`bar`，首先导航到一个水平`sally`又回落到一个更加水平`bar`。从导航`bar`到`home`需要向上移动两个级别。

如果只有一个路径包含根元素，则不能构造相对路径。如果两个路径都包含根元素，则构造相对路径的能力取决于系统。

递归 [`Copy`](examples/Copy.java)示例使用`relativize`和`resolve`方法。

## 比较两条路径

该`Path`级支持 [`equals`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html#equals-java.lang.Object-)，使您能够测试相等的两条路径。使用 [`startsWith`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html#startsWith-java.nio.file.Path-)和 [`endsWith`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html#endsWith-java.nio.file.Path-)方法可以测试路径是以特定字符串开头还是结尾。这些方法易于使用。例如：

```java
Path path = ...;
Path otherPath = ...;
Path beginning = Paths.get("/home");
Path ending = Paths.get("foo");

if (path.equals(otherPath)) {
    // equality logic here
} else if (path.startsWith(beginning)) {
    // path begins with "/home"
} else if (path.endsWith(ending)) {
    // path ends with "foo"
}
```

本`Path`类实现了 [`Iterable`](https://docs.oracle.com/javase/8/docs/api/java/lang/Iterable.html)接口。该 [`iterator`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html#iterator--)方法返回一个对象，使您可以迭代路径中的名称元素。返回的第一个元素是最接近目录树中的根的元素。以下代码片段遍历路径，打印每个名称元素：

```java
Path path = ...;
for (Path name: path) {
    System.out.println(name);
}
```

本`Path`类还实现了 [`Comparable`](https://docs.oracle.com/javase/8/docs/api/java/lang/Comparable.html)接口。您可以`Path`使用`compareTo`哪个对象进行排序来比较对象。

您也可以将`Path`对象放入`Collection`。有关此强大功能的更多信息，请参阅 [集合](../../collections/index.html)跟踪。

如果要验证两个`Path`对象是否找到同一文件，可以使用该`isSameFile`方法，如 [检查两个路径是否找到同一文件中所述](check.html#same)。