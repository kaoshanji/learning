# 查找文件

如果您曾经使用过shell脚本，则很可能使用模式匹配来查找文件。事实上，你可能已经广泛使用它了。如果您还没有使用它，模式匹配使用特殊字符来创建模式，然后可以将文件名与该模式进行比较。例如，在大多数shell脚本中，星号`*`，匹配任意数量的字符。例如，以下命令列出当前目录中以下结尾的所有文件`.html`：

```bash
% ls *.html
```

该`java.nio.file`软件包为此有用功能提供了编程支持。每个文件系统实现提供一个 [`PathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html)。您可以`PathMatcher`使用类中的 [`getPathMatcher(String)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-)方法检索文件系统`FileSystem`。以下代码段获取默认文件系统的路径匹配器：

```java
String pattern = ...;
PathMatcher matcher =
    FileSystems.getDefault().getPathMatcher("glob:" + pattern);
```

传递给的字符串参数`getPathMatcher`指定语法flavor和要匹配的模式。此示例指定*glob*语法。如果您不熟悉glob语法，请参阅 [什么是Glob](fileOps.html#glob)。

Glob语法易于使用且灵活，但如果您愿意，还可以使用正则表达式或*正则表达式*语法。有关正则表达式的详细信息，请参阅正则 [表达式](../regex/index.html)课程。某些文件系统实现可能支持其他语法。

如果要使用其他形式的基于字符串的模式匹配，可以创建自己的`PathMatcher`类。此页面中的示例使用glob语法。

创建`PathMatcher`实例后，您就可以将文件与文件进行匹配。该`PathMatcher`接口有一个单一的方法， [`matches`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html#matches-java.nio.file.Path-)即采用一个`Path`参数，并返回一个布尔值：它或者与该模式匹配，或没有。下面的代码片段查找，在结尾的文件`.java`或`.class`并打印这些文件到标准输出：

```java
PathMatcher matcher =
    FileSystems.getDefault().getPathMatcher("glob:*.{java,class}");

Path filename = ...;
if (matcher.matches(filename)) {
    System.out.println(filename);
}
```

## 递归模式匹配

搜索与特定模式匹配的文件与遍历文件树同时进行。你知道文件在文件系统的*某个地方*有多少次，但在哪里？或者您可能需要在文件树中找到具有特定文件扩展名的所有文件。

这个 [`Find`](examples/Find.java)例子确实如此。`Find`类似于UNIX `find`实用程序，但在功能上已经减少了。您可以扩展此示例以包含其他功能。例如，该`find`实用程序支持`-prune`标志以从搜索中排除整个子树。您可以通过返回实现该功能`SKIP_SUBTREE`的`preVisitDirectory`方法。要实现`-L`符号链接之后的选项，可以使用四参数`walkFileTree`方法并传入`FOLLOW_LINKS`枚举（但请确保在`visitFile`方法中测试循环链接）。

要运行“查找”应用程序，请使用以下格式：

```java
% java Find <path> -name "<glob_pattern>"
```

模式放在引号内，因此shell不会解释任何通配符。例如：

```java
% java Find . -name "*.html"
```

以下是该`Find`示例的源代码：

```java
/**
 * Sample code that finds files that match the specified glob pattern.
 * For more information on what constitutes a glob pattern, see
 * https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
 *
 * The file or directories that match the pattern are printed to
 * standard out.  The number of matches is also printed.
 *
 * When executing this application, you must put the glob pattern
 * in quotes, so the shell will not expand any wild cards:
 *              java Find . -name "*.java"
 */

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import static java.nio.file.FileVisitOption.*;
import java.util.*;


public class Find {

    public static class Finder
        extends SimpleFileVisitor<Path> {

        private final PathMatcher matcher;
        private int numMatches = 0;

        Finder(String pattern) {
            matcher = FileSystems.getDefault()
                    .getPathMatcher("glob:" + pattern);
        }

        // Compares the glob pattern against
        // the file or directory name.
        void find(Path file) {
            Path name = file.getFileName();
            if (name != null && matcher.matches(name)) {
                numMatches++;
                System.out.println(file);
            }
        }

        // Prints the total number of
        // matches to standard out.
        void done() {
            System.out.println("Matched: "
                + numMatches);
        }

        // Invoke the pattern matching
        // method on each file.
        @Override
        public FileVisitResult visitFile(Path file,
                BasicFileAttributes attrs) {
            find(file);
            return CONTINUE;
        }

        // Invoke the pattern matching
        // method on each directory.
        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attrs) {
            find(dir);
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file,
                IOException exc) {
            System.err.println(exc);
            return CONTINUE;
        }
    }

    static void usage() {
        System.err.println("java Find <path>" +
            " -name \"<glob_pattern>\"");
        System.exit(-1);
    }

    public static void main(String[] args)
        throws IOException {

        if (args.length < 3 || !args[1].equals("-name"))
            usage();

        Path startingDir = Paths.get(args[0]);
        String pattern = args[2];

        Finder finder = new Finder(pattern);
        Files.walkFileTree(startingDir, finder);
        finder.done();
    }
}
```

步行文件树中介绍了递归遍历 [文件树](walk.html)。