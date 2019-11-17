# 管理元数据（文件和文件存储属性）

*元数据*的定义是“关于其他数据的数据”。使用文件系统，数据包含在其文件和目录中，元数据跟踪有关每个对象的信息：它是常规文件，目录还是链接？它的大小，创建日期，上次修改日期，文件所有者，组所有者和访问权限是什么？

文件系统的元数据通常称为其*文件属性*。所述`Files`类包括可以被用来获得一个文件的一个属性，或者设置一个属性的方法。

| 方法                                                         | 评论                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [`size(Path)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#size-java.nio.file.Path-) | 以字节为单位返回指定文件的大小。                             |
| [`isDirectory(Path, LinkOption)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#isDirectory-java.nio.file.Path-java.nio.file.LinkOption...-) | 如果指定`Path`的文件是目录，则返回true 。                    |
| [`isRegularFile(Path, LinkOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#isRegularFile-java.nio.file.Path-java.nio.file.LinkOption...-) | 如果指定`Path`的文件是常规文件，则返回true 。                |
| [`isSymbolicLink(Path)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#isSymbolicLink-java.nio.file.Path-) | 如果指定`Path`的文件是符号链接，则返回true 。                |
| [`isHidden(Path)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#isHidden-java.nio.file.Path-) | 如果指定`Path`的文件被认为是文件系统隐藏的文件，则返回true 。 |
| [`getLastModifiedTime(Path, LinkOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#getLastModifiedTime-java.nio.file.Path-java.nio.file.LinkOption...-) [`setLastModifiedTime(Path, FileTime)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#setLastModifiedTime-java.nio.file.Path-java.nio.file.attribute.FileTime-) | 返回或设置指定文件的上次修改时间。                           |
| [`getOwner(Path, LinkOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#getOwner-java.nio.file.Path-java.nio.file.LinkOption...-) [`setOwner(Path, UserPrincipal)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#setOwner-java.nio.file.Path-java.nio.file.attribute.UserPrincipal-) | 返回或设置文件的所有者。                                     |
| [`getPosixFilePermissions(Path, LinkOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#getPosixFilePermissions-java.nio.file.Path-java.nio.file.LinkOption...-) [`setPosixFilePermissions(Path, Set)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#setPosixFilePermissions-java.nio.file.Path-java.util.Set-) | 返回或设置文件的POSIX文件权限。                              |
| [`getAttribute(Path, String, LinkOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#getAttribute-java.nio.file.Path-java.lang.String-java.nio.file.LinkOption...-) [`setAttribute(Path, String, Object, LinkOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#setAttribute-java.nio.file.Path-java.lang.String-java.lang.Object-java.nio.file.LinkOption...-) | 返回或设置文件属性的值。                                     |

如果程序在同一时间需要多个文件属性，则使用检索单个属性的方法可能效率低下。重复访问文件系统以检索单个属性可能会对性能产生负面影响。因此，`Files`该类提供了两种`readAttributes`方法来在一次批量操作中获取文件的属性。

| 方法                                                         | 评论                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [`readAttributes(Path, String, LinkOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#readAttributes-java.nio.file.Path-java.lang.String-java.nio.file.LinkOption...-) | 将文件的属性读取为批量操作。该`String`参数标识要读取的属性。 |
| [`readAttributes(Path, Class, LinkOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#readAttributes-java.nio.file.Path-java.lang.Class-java.nio.file.LinkOption...-) | 将文件的属性读取为批量操作。该`Class<A>`参数是请求的属性的类型，并且该方法返回该类的一个对象。 |

在显示`readAttributes`方法的示例之前，应该提到的是，不同的文件系统对于应该跟踪哪些属性有不同的概念。因此，相关文件属性被组合在一起成为视图。甲*视图*映射到一个特定的文件系统的实施，如POSIX或DOS，或到共同的功能性，如文件所有权。

支持的视图如下：

- [`BasicFileAttributeView`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/attribute/BasicFileAttributeView.html) - 提供所有文件系统实现都需要支持的基本属性的视图。
- [`DosFileAttributeView`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/attribute/DosFileAttributeView.html) - 使用支持DOS属性的文件系统支持的标准四位扩展基本属性视图。
- [`PosixFileAttributeView`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/attribute/PosixFileAttributeView.html) - 使用支持POSIX标准系列的文件系统支持的属性扩展基本属性视图，例如UNIX。这些属性包括文件所有者，组所有者和九个相关的访问权限。
- [`FileOwnerAttributeView`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/attribute/FileOwnerAttributeView.html) - 由支持文件所有者概念的任何文件系统实现提供支持。
- [`AclFileAttributeView`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/attribute/AclFileAttributeView.html) - 支持读取或更新文件的访问控制列表（ACL）。支持NFSv4 ACL模型。也可以支持任何ACL模型，例如Windows ACL模型，它具有定义到NFSv4模型的定义良好的映射。
- [`UserDefinedFileAttributeView`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/attribute/UserDefinedFileAttributeView.html) - 支持用户定义的元数据。此视图可以映射到系统支持的任何扩展机制。例如，在Solaris OS中，您可以使用此视图来存储文件的MIME类型。

特定文件系统实现可能仅支持基本文件属性视图，或者它可能支持其中几个文件属性视图。文件系统实现可能支持此API中未包含的其他属性视图。

在大多数情况下，您不必直接处理任何`FileAttributeView`接口。（如果您确实需要直接使用`FileAttributeView`，可以通过该[`getFileAttributeView(Path, Class, LinkOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#getFileAttributeView-java.nio.file.Path-java.lang.Class-java.nio.file.LinkOption...-)方法访问它 。）

这些`readAttributes`方法使用泛型，可用于读取任何文件属性视图的属性。本页其余部分的示例使用这些`readAttributes`方法。

本节的其余部分包括以下主题：

- [基本文件属性](#basic)
- [设置时间戳](#time)
- [DOS文件属性](#dos)
- [POSIX文件权限](#posix)
- [设置文件或组所有者](#lookup)
- [用户定义的文件属性](#user)
- [文件存储属性](#store)

## 基本文件属性

如前所述，要读取文件的基本属性，可以使用其中一种`Files.readAttributes`方法，这些方法在一次批量操作中读取所有基本属性。这比单独访问文件系统以读取每个单独的属性要有效得多。varargs参数目前支持[`LinkOption`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/LinkOption.html)枚举，`NOFOLLOW_LINKS`。如果不希望遵循符号链接，请使用此选项。

------

**有关时间戳的一句话：**  这组基本属性包括三个时间戳：，，和。在特定实现中可能不支持任何这些时间戳，在这种情况下，相应的访问器方法返回特定于实现的值。支持时，时间戳将作为对象返回 。

以下代码段读取并打印给定文件的基本文件属性，并使用[`BasicFileAttributes`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/attribute/BasicFileAttributes.html)该类中的方法 。

```java
Path file = ...;
BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);

System.out.println("creationTime: " + attr.creationTime());
System.out.println("lastAccessTime: " + attr.lastAccessTime());
System.out.println("lastModifiedTime: " + attr.lastModifiedTime());

System.out.println("isDirectory: " + attr.isDirectory());
System.out.println("isOther: " + attr.isOther());
System.out.println("isRegularFile: " + attr.isRegularFile());
System.out.println("isSymbolicLink: " + attr.isSymbolicLink());
System.out.println("size: " + attr.size());
```

除了此示例中显示的访问器方法之外，还有一种`fileKey`方法可以返回唯一标识文件的对象，或者`null`没有可用的文件密钥。

## 设置时间戳

以下代码段设置上次修改时间（以毫秒为单位）：

```java
Path file = ...;
BasicFileAttributes attr =
    Files.readAttributes(file, BasicFileAttributes.class);
long currentTime = System.currentTimeMillis();
FileTime ft = FileTime.fromMillis(currentTime);
Files.setLastModifiedTime(file, ft);
}
```

## DOS文件属性

除DOS之外的文件系统也支持DOS文件属性，例如Samba。以下代码段使用[`DosFileAttributes`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/attribute/DosFileAttributes.html)该类的方法 。

```java
Path file = ...;
try {
    DosFileAttributes attr =
        Files.readAttributes(file, DosFileAttributes.class);
    System.out.println("isReadOnly is " + attr.isReadOnly());
    System.out.println("isHidden is " + attr.isHidden());
    System.out.println("isArchive is " + attr.isArchive());
    System.out.println("isSystem is " + attr.isSystem());
} catch (UnsupportedOperationException x) {
    System.err.println("DOS file" +
        " attributes not supported:" + x);
}
```

但是，您可以使用该[`setAttribute(Path, String, Object, LinkOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#setAttribute-java.nio.file.Path-java.lang.String-java.lang.Object-java.nio.file.LinkOption...-)方法设置DOS属性 ，如下所示：

```java
Path file = ...;
Files.setAttribute(file, "dos:hidden", true);
```

## POSIX文件权限

*POSIX*是用于UNIX的可移植操作系统接口的首字母缩写，是一组IEEE和ISO标准，旨在确保不同版本的UNIX之间的互操作性。如果程序符合这些POSIX标准，则应该可以轻松移植到其他符合POSIX标准的操作系统。

除文件所有者和组所有者外，POSIX还支持九种文件权限：文件所有者，同一组成员和“其他人”的读取，写入和执行权限。

以下代码段读取给定文件的POSIX文件属性并将其打印到标准输出。代码使用[`PosixFileAttributes`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/attribute/PosixFileAttributes.html)类中的方法 。

```java
Path file = ...;
PosixFileAttributes attr =
    Files.readAttributes(file, PosixFileAttributes.class);
System.out.format("%s %s %s%n",
    attr.owner().getName(),
    attr.group().getName(),
    PosixFilePermissions.toString(attr.permissions()));
```

该 [`PosixFilePermissions`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/attribute/PosixFilePermissions.html)助手类提供了一些有用的方法，具体如下：

- `toString`在前面的代码段中使用的方法将文件权限转换为字符串（例如，`rw-r--r--`）。
- 该`fromString`方法接受表示文件权限的字符串并构造`Set`文件权限。
- 该`asFileAttribute`方法接受`Set`文件权限并构造可以传递给`Path.createFile`or `Path.createDirectory`方法的文件属性。

以下代码段从一个文件中读取属性并创建一个新文件，将原始文件中的属性分配给新文件：

```java
Path sourceFile = ...;
Path newFile = ...;
PosixFileAttributes attrs =
    Files.readAttributes(sourceFile, PosixFileAttributes.class);
FileAttribute<Set<PosixFilePermission>> attr =
    PosixFilePermissions.asFileAttribute(attrs.permissions());
Files.createFile(file, attr);
```

该`asFileAttribute`方法将权限包装为`FileAttribute`。然后，代码尝试使用这些权限创建新文件。请注意，这`umask`也适用，因此新文件可能比请求的权限更安全。

要将文件的权限设置为表示为硬编码字符串的值，可以使用以下代码：

```java
Path file = ...;
Set<PosixFilePermission> perms =
    PosixFilePermissions.fromString("rw-------");
FileAttribute<Set<PosixFilePermission>> attr =
    PosixFilePermissions.asFileAttribute(perms);
Files.setPosixFilePermissions(file, perms);
```

该 [`Chmod`](examples/Chmod.java)示例以类似于`chmod`实用程序的方式递归地更改文件的权限。

## 设置文件或组所有者

要将名称转换为可以存储为文件所有者或组所有者的对象，可以使用该 [`UserPrincipalLookupService`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/attribute/UserPrincipalLookupService.html)服务。此服务将名称或组名称查找为字符串，并返回`UserPrincipal`表示该字符串的对象。您可以使用该[`FileSystem.getUserPrincipalLookupService`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getUserPrincipalLookupService--)方法获取默认文件系统的用户主体查找服务 。

以下代码段显示了如何使用以下`setOwner`方法设置文件所有者：

```java
Path file = ...;
UserPrincipal owner = file.GetFileSystem().getUserPrincipalLookupService()
        .lookupPrincipalByName("sally");
Files.setOwner(file, owner);
```

`Files`类中没有用于设置组所有者的特殊方法。但是，直接执行此操作的安全方法是通过POSIX文件属性视图，如下所示：

```java
Path file = ...;
GroupPrincipal group =
    file.getFileSystem().getUserPrincipalLookupService()
        .lookupPrincipalByGroupName("green");
Files.getFileAttributeView(file, PosixFileAttributeView.class)
     .setGroup(group);
```

## 用户定义的文件属性

如果文件系统实现支持的文件属性不足以满足您的需要，则可以使用`UserDefinedAttributeView`创建和跟踪自己的文件属性。

一些实现将此概念映射到NTFS备用数据流等功能以及文件系统（如ext3和ZFS）上的扩展属性。大多数实现都对值的大小施加了限制，例如，ext3将大小限制为4千字节。

通过使用以下代码片段，可以将文件的MIME类型存储为用户定义的属性：

```java
Path file = ...;
UserDefinedFileAttributeView view = Files
    .getFileAttributeView(file, UserDefinedFileAttributeView.class);
view.write("user.mimetype",
           Charset.defaultCharset().encode("text/html");
```

要阅读MIME类型属性，您可以使用以下代码段：

```java
Path file = ...;
UserDefinedFileAttributeView view = Files
.getFileAttributeView(file,UserDefinedFileAttributeView.class);
String name = "user.mimetype";
ByteBuffer buf = ByteBuffer.allocate(view.size(name));
view.read(name, buf);
buf.flip();
String value = Charset.defaultCharset().decode(buf).toString();
```

该 [`Xdd`](examples/Xdd.java)示例显示如何获取，设置和删除用户定义的属性。

------

**注意：**  在Linux中，您可能必须启用扩展属性才能使用户定义的属性起作用。如果在尝试访问用户定义的属性视图时收到，则需要重新装入文件系统。以下命令使用ext3文件系统的扩展属性重新安装根分区。如果此命令不适合您的Linux风格，请参阅文档。

如果要使更改成为永久更改，请添加条目`/etc/fstab`。

------

## 文件存储属性

您可以使用 [`FileStore`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileStore.html)该类来了解有关文件存储的信息，例如可用空间大小。该 [`getFileStore(Path)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#getFileStore-java.nio.file.Path-)方法获取指定文件的文件存储。

以下代码段打印特定文件所在的文件存储的空间使用情况：

```java
Path file = ...;
FileStore store = Files.getFileStore(file);

long total = store.getTotalSpace() / 1024;
long used = (store.getTotalSpace() -
             store.getUnallocatedSpace()) / 1024;
long avail = store.getUsableSpace() / 1024;
```

该 [`DiskUsage`](examples/DiskUsage.java)示例使用此API打印默认文件系统中所有存储的磁盘空间信息。此示例使用类中的 [`getFileStores`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getFileStores--)方法`FileSystem`来获取文件系统的所有文件存储。