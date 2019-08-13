# 旧文件I / O代码

## 与旧版代码的互操作性

在Java SE 7发布之前，`java.io.File`该类是用于文件I / O的机制，但它有几个缺点。

- 许多方法在失败时都没有抛出异常，因此无法获得有用的错误消息。例如，如果文件删除失败，程序将收到“删除失败”，但不知道是否因为该文件不存在，用户没有权限，或者还有其他问题。
- 该`rename`方法不能跨平台一致地工作。
- 没有真正支持符号链接。
- 需要更多对元数据的支持，例如文件权限，文件所有者和其他安全属性。
- 访问文件元数据效率低下。
- 许多`File`方法没有扩展。通过服务器请求大型目录列表可能会导致挂起。大目录也可能导致内存资源问题，导致拒绝服务。
- 如果存在循环符号链接，则无法编写可递归遍历文件树并正确响应的可靠代码。

也许你有遗留代码使用`java.io.File`并希望利用这些`java.nio.file.Path`功能，而对代码的影响最小。

的`java.io.File`类提供的 [`toPath`](https://docs.oracle.com/javase/8/docs/api/java/io/File.html#toPath--)方法，该方法旧式转换`File`实例的`java.nio.file.Path`实例，如下所示：

```java
Path input = file.toPath();
```

然后，您可以利用`Path`该类可用的丰富功能集。

例如，假设您有一些删除文件的代码：

```java
file.delete();
```

您可以修改此代码以使用该`Files.delete`方法，如下所示：

```java
Path fp = file.toPath();
Files.delete(fp);
```

相反，该 [`Path.toFile`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html#toFile--)方法构造`java.io.File`对象的`Path`对象。

## 将java.io.File功能映射到java.nio.file

由于文件I / O的Java实现已在Java SE 7发行版中完全重新构建，因此您无法将一种方法替换为另一种方法。如果要使用`java.nio.file`程序包提供的丰富功能，最简单的解决方案是使用[`File.toPath`](https://docs.oracle.com/javase/8/docs/api/java/io/File.html#toPath--)上一节中建议的 方法。但是，如果您不想使用该方法或者它不足以满足您的需求，则必须重写文件I / O代码。

这两个API之间没有一对一的对应关系，但下表让您大致了解`java.io.File`API中的哪些功能映射到`java.nio.file`API，并告诉您可以从何处获取更多信息。

| java.io.File功能                                             | java.nio.file功能                                            | 教程覆盖范围                                                 |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `java.io.File`                                               | `java.nio.file.Path`                                         | [路径类](pathClass.html)                                     |
| `java.io.RandomAccessFile`                                   | 该`SeekableByteChannel`功能。                                | [随机访问文件](rafs.html)                                    |
| `File.canRead`，`canWrite`，`canExecute`                     | `Files.isReadable`，`Files.isWritable`和`Files.isExecutable`。 在UNIX文件系统上， [管理元数据（文件和文件存储属性）](fileAttr.html)包用于检查九个文件权限。 | [检查文件或目录](check.html) [管理元数据](fileAttr.html)     |
| `File.isDirectory()`，`File.isFile()`和`File.length()`       | `Files.isDirectory(Path, LinkOption...)`，`Files.isRegularFile(Path, LinkOption...)`和`Files.size(Path)` | [管理元数据](fileAttr.html)                                  |
| `File.lastModified()` 和 `File.setLastModified(long)`        | `Files.getLastModifiedTime(Path, LinkOption...)` 和 `Files.setLastMOdifiedTime(Path, FileTime)` | [管理元数据](fileAttr.html)                                  |
| 在`File`该设置不同的属性方法：`setExecutable`，`setReadable`，`setReadOnly`，`setWritable` | 这些方法被该`Files`方法取代`setAttribute(Path, String, Object, LinkOption...)`。 | [管理元数据](fileAttr.html)                                  |
| `new File(parent, "newfile")`                                | `parent.resolve("newfile")`                                  | [路径操作](pathOps.html)                                     |
| `File.renameTo`                                              | `Files.move`                                                 | [移动文件或目录](move.html)                                  |
| `File.delete`                                                | `Files.delete`                                               | [删除文件或目录](delete.html)                                |
| `File.createNewFile`                                         | `Files.createFile`                                           | [创建文件](file.html#createFile)                             |
| `File.deleteOnExit`                                          | 替换`DELETE_ON_CLOSE`为`createFile`方法中指定的选项。        | [创建文件](file.html#createFile)                             |
| `File.createTempFile`                                        | `Files.createTempFile(Path, String, FileAttributes<?>)`， `Files.createTempFile(Path, String, String, FileAttributes<?>)` | [创建文件](file.html#createFile) [使用流I / O ](file.html#createStream)[通过使用通道I / O读取和写入文件来](file.html#channelio)[创建和写入文件](file.html#createStream) |
| `File.exists`                                                | `Files.exists` 和 `Files.notExists`                          | [验证文件或目录的存在](check.html)                           |
| `File.compareTo` 和 `equals`                                 | `Path.compareTo` 和 `equals`                                 | [比较两条路径](pathOps.html#compare)                         |
| `File.getAbsolutePath` 和 `getAbsoluteFile`                  | `Path.toAbsolutePath`                                        | [转换路径](pathOps.html#convert)                             |
| `File.getCanonicalPath` 和 `getCanonicalFile`                | `Path.toRealPath` 要么 `normalize`                           | [转换路径（`toRealPath`）](pathOps.html#convert) [从路径中删除冗余（`normalize`）](pathOps.html#normal) |
| `File.toURI`                                                 | `Path.toURI`                                                 | [转换路径](pathOps.html#convert)                             |
| `File.isHidden`                                              | `Files.isHidden`                                             | [检索有关路径的信息](pathOps.html#info)                      |
| `File.list` 和 `listFiles`                                   | `Path.newDirectoryStream`                                    | [列出目录的内容](dirs.html#listdir)                          |
| `File.mkdir` 和 `mkdirs`                                     | `Path.createDirectory`                                       | [创建目录](dirs.html#create)                                 |
| `File.listRoots`                                             | `FileSystem.getRootDirectories`                              | [列出文件系统的根目录](dirs.html#listall)                    |
| `File.getTotalSpace`，`File.getFreeSpace`，`File.getUsableSpace` | `FileStore.getTotalSpace`，`FileStore.getUnallocatedSpace`，`FileStore.getUsableSpace`，`FileStore.getTotalSpace` | [文件存储属性](fileAttr.html#store)                          |