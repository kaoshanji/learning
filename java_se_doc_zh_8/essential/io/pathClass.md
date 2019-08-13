# Path

[`Path`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)Java SE 7发行版中介绍的 该类是该[`java.nio.file`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/package-summary.html)软件包的主要入口点之一 。如果您的应用程序使用文件I / O，您将需要了解此类的强大功能。

------

**版本注意：**  如果您使用的是JDK7之前的代码，则仍可以通过使用该方法来利用类功能 。有关更多信息，请参阅 。

顾名思义，`Path`该类是文件系统中路径的编程表示。一个`Path`对象包含用于构建路径的文件名和目录列表，用于检查，定位和操作文件。

一个`Path`实例反映了基础平台。在Solaris OS中，a `Path`使用Solaris语法（`/home/joe/foo`），在Microsoft Windows中，a `Path`使用Windows语法（`C:\home\joe\foo`）。A `Path`不是系统独立的。您无法比较`Path`Solaris文件系统中的a并希望它与`Path`Windows文件系统匹配，即使目录结构相同且两个实例都找到相同的相对文件。

对应的文件或目录`Path`可能不存在。您可以创建一个`Path`实例并以各种方式对其进行操作：您可以追加它，提取它，将它与另一个路径进行比较。在适当的时候，您可以使用[`Files`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html)类中的方法 来检查对应的文件是否存在`Path`，创建文件，打开文件，删除文件，更改权限等等。

下一页将`Path`详细介绍该课程。