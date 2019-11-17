# 遍历文件树

您是否需要创建一个递归访问文件树中所有文件的应用程序？也许您需要删除`.class`树中的每个文件，或者查找去年未访问过的每个文件。您可以使用[`FileVisitor`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileVisitor.html)界面执行此 操作。

本节包括以下内容：

- [FileVisitor接口](#filevisitor)
- [启动流程](#invoke)
- [创建FileVisitor时的注意事项](#order)
- [控制流量](#return)
- [例子](#ex)

## FileVisitor接口

要遍历文件树，首先需要实现一个`FileVisitor`。A `FileVisitor`指定遍历过程中关键点所需的行为：访问文件时，访问目录之前，访问目录之后或发生故障时。该接口有四种方法对应于这些情况：

- [`preVisitDirectory`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileVisitor.html#preVisitDirectory-T-java.nio.file.attribute.BasicFileAttributes-) - 在访问目录条目之前调用。
- [`postVisitDirectory`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileVisitor.html#postVisitDirectory-T-java.io.IOException-) - 访问目录中的所有条目后调用。如果遇到任何错误，则将特定异常传递给该方法。
- [`visitFile`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileVisitor.html#visitFile-T-java.nio.file.attribute.BasicFileAttributes-) - 调用正在访问的文件。文件`BasicFileAttributes`传递给方法，或者您可以使用 [文件属性](fileAttr.html)包来读取一组特定的属性。例如，您可以选择读取文件`DosFileAttributeView`以确定文件是否设置了“隐藏”位。
- [`visitFileFailed`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileVisitor.html#visitFileFailedy-T-java.io.IOException-) - 无法访问文件时调用。特定异常传递给该方法。您可以选择是抛出异常，将其打印到控制台还是日志文件，等等。

如果您不需要实现所有四种`FileVisitor`方法，而不是实现`FileVisitor`接口，则可以扩展 [`SimpleFileVisitor`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/SimpleFileVisitor.html)该类。该类实现`FileVisitor`接口，访问树中的所有文件，并`IOError`在遇到错误时抛出。您可以扩展此类并仅覆盖所需的方法。

以下示例扩展`SimpleFileVisitor`为打印文件树中的所有条目。它打印条目是条目是常规文件，符号链接，目录还是其他“未指定”类型的文件。它还会打印每个文件的大小（以字节为单位）。遇到的任何异常都会打印到控制台。

该`FileVisitor`方法以粗体显示：

```java
import static java.nio.file.FileVisitResult.*;

public static class PrintFiles
    extends SimpleFileVisitor<Path> {

    // Print information about
    // each type of file.
    @Override
    public FileVisitResult visitFile(Path file,
                                   BasicFileAttributes attr) {
        if (attr.isSymbolicLink()) {
            System.out.format("Symbolic link: %s ", file);
        } else if (attr.isRegularFile()) {
            System.out.format("Regular file: %s ", file);
        } else {
            System.out.format("Other: %s ", file);
        }
        System.out.println("(" + attr.size() + "bytes)");
        return CONTINUE;
    }

    // Print each directory visited.
    @Override
    public FileVisitResult postVisitDirectory(Path dir,
                                          IOException exc) {
        System.out.format("Directory: %s%n", dir);
        return CONTINUE;
    }

    // If there is some error accessing
    // the file, let the user know.
    // If you don't override this method
    // and an error occurs, an IOException 
    // is thrown.
    @Override
    public FileVisitResult visitFileFailed(Path file,
                                       IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }
}
```

## 启动流程

一旦实现了，您`FileVisitor`如何启动文件遍历？课堂上有两种`walkFileTree`方法`Files`。

- [`walkFileTree(Path, FileVisitor)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#walkFileTree-java.nio.file.Path-java.nio.file.FileVisitor-)
- [`walkFileTree(Path, Set, int, FileVisitor)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#walkFileTree-java.nio.file.Path-java.util.Set-int-java.nio.file.FileVisitor-)

第一种方法只需要一个起点和一个实例`FileVisitor`。您可以`PrintFiles`按如下方式调用文件访问者：

```java
Path startingDir = ...;
PrintFiles pf = new PrintFiles();
Files.walkFileTree(startingDir, pf);
```

第二种`walkFileTree`方法使您可以另外指定访问级别数和一组 [`FileVisitOption`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileVisitOption.html)枚举的限制。如果要确保此方法遍历整个文件树，可以指定`Integer.MAX_VALUE`最大深度参数。

您可以指定`FileVisitOption`枚举，`FOLLOW_LINKS`表示应遵循符号链接。

此代码段显示了如何调用四参数方法：

```java
import static java.nio.file.FileVisitResult.*;

Path startingDir = ...;

EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);

Finder finder = new Finder(pattern);
Files.walkFileTree(startingDir, opts, Integer.MAX_VALUE, finder);
```

## 创建FileVisitor时的注意事项

首先深度遍历文件树，但是您不能对访问子目录的迭代顺序做出任何假设。

如果您的程序将更改文件系统，则需要仔细考虑如何实现您的程序`FileVisitor`。

例如，如果您正在编写递归删除，则首先删除目录中的文件，然后再删除目录本身。在这种情况下，您删除该目录`postVisitDirectory`。

如果您正在编写递归副本，则`preVisitDirectory`在尝试将文件复制到其中之前创建新目录（in `visitFiles`）。如果要保留源目录的属性（类似于UNIX `cp -p`命令），则需要在复制文件*后执行*此操作`postVisitDirectory`。该 [`Copy`](examples/Copy.java)示例显示了如何执行此操作。

如果您正在编写文件搜索，则在`visitFile`方法中执行比较。此方法查找符合条件的所有文件，但找不到目录。如果要查找文件和目录，还必须在`preVisitDirectory`或`postVisitDirectory`方法中执行比较。该 [`Find`](examples/Find.java)示例显示了如何执行此操作。

您需要决定是否要遵循符号链接。例如，如果要删除文件，则可能不建议使用符号链接。如果要复制文件树，则可能需要允许它。默认情况下，`walkFileTree`不遵循符号链接。

`visitFile`为文件调用该方法。如果您指定了该`FOLLOW_LINKS`选项，并且您的文件树具有指向父目录的循环链接，则使用该`visitFileFailed`方法在方法中报告循环目录`FileSystemLoopException`。以下代码段显示了如何捕获循环链接，并且来自 [`Copy`](examples/Copy.java)示例：

```java
@Override
public FileVisitResult
    visitFileFailed(Path file,
        IOException exc) {
    if (exc instanceof FileSystemLoopException) {
        System.err.println("cycle detected: " + file);
    } else {
        System.err.format("Unable to copy:" + " %s: %s%n", file, exc);
    }
    return CONTINUE;
}
```

仅当程序遵循符号链接时才会出现这种情况。

## 控制流量

也许您想要遍历文件树以查找特定目录，并且在找到时，您希望该进程终止。也许你想跳过特定的目录。

该`FileVisitor`方法返回一个 [`FileVisitResult`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileVisitResult.html)值。您可以中止文件遍历过程或控制是否通过`FileVisitor`方法返回的值访问目录：

- `CONTINUE` - 表示文件行走应继续。如果`preVisitDirectory`方法返回`CONTINUE`，则访问该目录。
- `TERMINATE` - 立即中止文件行走。返回此值后，不会调用其他文件遍历方法。
- `SKIP_SUBTREE`- `preVisitDirectory`返回此值时，将跳过指定的目录及其子目录。这个分支被“修剪”出树。
- `SKIP_SIBLINGS`- `preVisitDirectory`返回此值时，不会访问指定的目录，`postVisitDirectory`也不会调用指定的目录，也不会访问其他未访问过的兄弟节点。如果从`postVisitDirectory`方法返回，则不会访问其他兄弟姐妹。基本上，在指定的目录中没有进一步发生。

在此代码段中，将`SCCS`跳过任何名为的目录：

```java
import static java.nio.file.FileVisitResult.*;

public FileVisitResult
     preVisitDirectory(Path dir,
         BasicFileAttributes attrs) {
    (if (dir.getFileName().toString().equals("SCCS")) {
         return SKIP_SUBTREE;
    }
    return CONTINUE;
}
```

在此代码段中，只要找到特定文件，文件名就会打印到标准输出，文件遍历将终止：

```java
import static java.nio.file.FileVisitResult.*;

// The file we are looking for.
Path lookingFor = ...;

public FileVisitResult
    visitFile(Path file,
        BasicFileAttributes attr) {
    if (file.getFileName().equals(lookingFor)) {
        System.out.println("Located file: " + file);
        return TERMINATE;
    }
    return CONTINUE;
}
```

## 例子

以下示例演示了文件遍历机制：

- [`Find`](examples/Find.java) - 递归文件树，查找与特定glob模式匹配的文件和目录。“ [查找文件”中](find.html)讨论了此示例 。
- [`Chmod`](examples/Chmod.java) - 递归更改文件树的权限（仅适用于POSIX系统）。
- [`Copy`](examples/Copy.java) - 递归复制文件树。
- [`WatchDir`](examples/WatchDir.java) - 演示监视目录以查找已创建，删除或修改的文件的机制。使用该`-r`选项调用此程序会监视整个树以进行更改。有关文件通知服务的详细信息，请参阅“查看 [目录以进行更改”](notification.html)。