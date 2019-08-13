# 复制文件或目录

您可以使用该[`copy(Path, Path, CopyOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#copy-java.nio.file.Path-java.nio.file.Path-java.nio.file.CopyOption...-)方法复制文件或目录 。如果目标文件存在，则复制将失败，除非`REPLACE_EXISTING`指定了该选项。

目录可以复制。但是，目录中的文件不会被复制，因此即使原始目录包含文件，新目录也是空的。

复制符号链接时，将复制链接的目标。如果要复制链接本身而不是链接的内容，请指定`NOFOLLOW_LINKS`或`REPLACE_EXISTING`选项。

此方法采用varargs参数。支持以下`StandardCopyOption`和`LinkOption`枚举：

- `REPLACE_EXISTING` - 即使目标文件已存在，也执行复制。如果目标是符号链接，则复制链接本身（而不是链接的目标）。如果目标是非空目录，则复制将失败并显示`FileAlreadyExistsException`异常。
- `COPY_ATTRIBUTES` - 将与文件关联的文件属性复制到目标文件。支持的确切文件属性是文件系统和平台相关的，但`last-modified-time`跨平台支持并复制到目标文件。
- `NOFOLLOW_LINKS` - 表示不应遵循符号链接。如果要复制的文件是符号链接，则复制链接（而不是链接的目标）。

如果您不熟悉`enums`，请参阅 [枚举类型](../../java/javaOO/enum.html)。

以下显示了如何使用该`copy`方法：

```java
import static java.nio.file.StandardCopyOption.*;
...
Files.copy(source, target, REPLACE_EXISTING);
```

除了文件复制之外，`Files`该类还定义了可用于在文件和流之间进行复制的方法。该 [`copy(InputStream, Path, CopyOptions...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#copy-java.io.InputStream-java.nio.file.Path-java.nio.file.CopyOption...-)方法可用于将所有字节从输入流复制到文件。该 [`copy(Path, OutputStream)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#copy-java.nio.file.Path-java.io.OutputStream-)方法可用于将所有字节从文件复制到输出流。

该 [`Copy`](examples/Copy.java)示例使用`copy`和`Files.walkFileTree`方法来支持递归副本。有关更多信息，请参阅 [遍历文件树](walk.html)。