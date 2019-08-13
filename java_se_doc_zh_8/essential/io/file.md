# 阅读，编写和创建文件

本页讨论了阅读，编写，创建和打开文件的详细信息。有多种文件I / O方法可供选择。为了帮助理解API，下图按复杂性排列文件I / O方法。

![使用从最不复杂（在左侧）到最复杂（在右侧）排列的文件I / O方法的线条图。](images/io-fileiomethods.gif)





文件I / O方法从较简单到较复杂排列



最左边的图的是实用方法`readAllBytes`，`readAllLines`和`write`方法，设计简单，常见的情况。到那些的右侧是用于迭代流或文本行，如方法`newBufferedReader`，`newBufferedWriter`，然后`newInputStream`和`newOutputStream`。这些方法可与`java.io`包互操作。对于那些右边是处理方法`ByteChannels`，`SeekableByteChannels`及`ByteBuffers`如`newByteChannel`方法。最后，最右边是`FileChannel`用于需要文件锁定或内存映射I / O的高级应用程序的方法。

------

**注意：**  创建新文件的方法使您可以为文件指定一组可选的初始属性。例如，在支持POSIX标准集（例如UNIX）的文件系统上，您可以在创建文件时指定文件所有者，组所有者或文件权限。“ 页面介绍了文件属性以及如何访问和设置它们。

此页面包含以下主题：

- [该`OpenOptions`参数](#openOptions)
- [小文件的常用方法](#common)
- [文本文件的缓冲I / O方法](#textfiles)
- [无缓冲流的方法和`java.io`API的互操作性](#streams)
- [频道和频道的方法 `ByteBuffers`](#channels)
- [创建常规和临时文件的方法](#creating)

------

## 该`OpenOptions`参数

本节中的几个方法采用可选`OpenOptions`参数。此参数是可选的，API会告诉您在未指定方法时该方法的默认行为。

`StandardOpenOptions`支持以下枚举：

- `WRITE` - 打开文件以进行写访问。
- `APPEND` - 将新数据附加到文件末尾。此选项与`WRITE`或`CREATE`选项一起使用。
- `TRUNCATE_EXISTING` - 将文件截断为零字节。此选项与`WRITE`选项一起使用。
- `CREATE_NEW` - 如果文件已存在，则创建新文件并引发异常。
- `CREATE` - 打开文件（如果存在）或创建新文件（如果不存在）。
- `DELETE_ON_CLOSE` - 关闭流时删除文件。此选项对临时文件很有用。
- `SPARSE` - 提示新创建的文件将是稀疏的。此高级选项在某些文件系统（例如NTFS）上受到尊重，其中具有数据“间隙”的大文件可以以更有效的方式存储，其中这些空间隙不占用磁盘空间。
- `SYNC` - 使文件（内容和元数据）与底层存储设备保持同步。
- `DSYNC` - 使文件内容与底层存储设备保持同步。

------

## 小文件的常用方法

### 从文件中读取所有字节或行

如果您有一个小文件，并且您希望一次性读取其全部内容，则可以使用 [`readAllBytes(Path)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#readAllBytes-java.nio.file.Path-)或 [`readAllLines(Path, Charset)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#readAllLines-java.nio.file.Path-java.nio.charset.Charset-)方法。这些方法可以为您完成大部分工作，例如打开和关闭流，但不用于处理大型文件。以下代码显示了如何使用该`readAllBytes`方法：

```java
Path file = ...;
byte[] fileArray;
fileArray = Files.readAllBytes(file);
```

### 将所有字节或行写入文件

您可以使用其中一种写入方法将字节或行写入文件。

- [`write(Path, byte[\], OpenOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#write-java.nio.file.Path-byte:A-java.nio.file.OpenOption...-)
- [`write(Path, Iterable< extends CharSequence>, Charset, OpenOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#write-java.nio.file.Path-java.lang.Iterable-java.nio.charset.Charset-java.nio.file.OpenOption...-)

以下代码段显示了如何使用`write`方法。

```java
Path file = ...;
byte[] buf = ...;
Files.write(file, buf);
```

## 文本文件的缓冲I / O方法

该`java.nio.file`软件包支持通道I / O，它可以在缓冲区中移动数据，绕过一些可能会阻塞流I / O的层。

### 使用缓冲流I / O读取文件

该 [`newBufferedReader(Path, Charset)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#newBufferedReader-java.nio.file.Path-java.nio.charset.Charset-)方法打开一个文件进行读取，返回一个`BufferedReader`可用于以有效方式从文件中读取文本的文件。

以下代码段显示了如何使用该`newBufferedReader`方法从文件中读取。该文件以“US-ASCII”编码。

```java
Charset charset = Charset.forName("US-ASCII");
try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
    String line = null;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException x) {
    System.err.format("IOException: %s%n", x);
}
```

### 使用缓冲流I / O编写文件

您可以使用该 [`newBufferedWriter(Path, Charset, OpenOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#newBufferedWriter-java.nio.file.Path-java.nio.charset.Charset-java.nio.file.OpenOption...-)方法使用a写入文件`BufferedWriter`。

以下代码段显示了如何使用此方法创建以“US-ASCII”编码的文件：

```java
Charset charset = Charset.forName("US-ASCII");
String s = ...;
try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
    writer.write(s, 0, s.length());
} catch (IOException x) {
    System.err.format("IOException: %s%n", x);
}
```

## 无缓冲流的方法和`java.io`API的互操作性

### 使用流I / O读取文件

要打开文件进行阅读，可以使用该 [`newInputStream(Path, OpenOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#newInputStream-java.nio.file.Path-java.nio.file.OpenOption...-)方法。此方法返回一个无缓冲的输入流，用于从文件中读取字节。

```java
Path file = ...;
try (InputStream in = Files.newInputStream(file);
    BufferedReader reader =
      new BufferedReader(new InputStreamReader(in))) {
    String line = null;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException x) {
    System.err.println(x);
}
```

### 使用流I / O创建和写入文件

您可以使用该[`newOutputStream(Path, OpenOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#newOutputStream-java.nio.file.Path-java.nio.file.OpenOption...-)方法创建文件，附加到文件或写入文件 。此方法打开或创建用于写入字节的文件，并返回无缓冲的输出流。

该方法采用可选`OpenOption`参数。如果未指定打开选项，并且该文件不存在，则会创建一个新文件。如果文件存在，则会被截断。此选项相当于使用`CREATE`和`TRUNCATE_EXISTING`选项调用方法。

以下示例打开一个日志文件。如果该文件不存在，则创建该文件。如果该文件存在，则打开该文件以进行追加。

```java
import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;

public class LogFileTest {

  public static void main(String[] args) {

    // Convert the string to a
    // byte array.
    String s = "Hello World! ";
    byte data[] = s.getBytes();
    Path p = Paths.get("./logfile.txt");

    try (OutputStream out = new BufferedOutputStream(
      Files.newOutputStream(p, CREATE, APPEND))) {
      out.write(data, 0, data.length);
    } catch (IOException x) {
      System.err.println(x);
    }
  }
}
```

## 频道和频道的方法 `ByteBuffers`

### 使用通道I / O读取和写入文件

当流I / O一次读取一个字符时，通道I / O一次读取一个缓冲区。该 [`ByteChannel`](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/ByteChannel.html)接口提供了基本的`read`和`write`功能性。A [`SeekableByteChannel`](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SeekableByteChannel.html)是`ByteChannel`能够维持通道中的位置并改变该位置的能力。A `SeekableByteChannel`还支持截断与通道关联的文件并查询文件的大小。

移动到文件中的不同点然后从该位置读取或写入的能力使得可以随机访问文件。有关更多信息，请参阅 [随机访问文件](rafs.html)

读取和写入通道I / O有两种方法。

- [`newByteChannel(Path, OpenOption...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#newByteChannel-java.nio.file.Path-java.nio.file.OpenOption...-)
- [`newByteChannel(Path, Set, FileAttribute...)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#newByteChannel-java.nio.file.Path-java.util.Set-java.nio.file.attribute.FileAttribute...-)

------

**注：**  该方法返回的一个实例。使用默认文件系统，您可以将此可搜索字节通道转换为 提供对更高级功能的访问，例如将文件区域直接映射到内存中以便更快地访问，锁定文件的某个区域以便其他进程无法访问它，或者读取从绝对位置写入字节而不影响通道的当前位置。

这两种`newByteChannel`方法都可以指定`OpenOption`选项列表。除了另外一个选项之外，还支持方法使用的相同[打开](#openOptions)`newOutputStream`选项：`READ`因为`SeekableByteChannel`支持读取和写入，所以需要这些选项。

指定`READ`打开要读取的频道。指定`WRITE`或`APPEND`打开要写入的频道。如果未指定这些选项，则打开通道进行读取。

以下代码段读取文件并将其打印到标准输出：

```java
// Defaults to READ
try (SeekableByteChannel sbc = Files.newByteChannel(file)) {
    ByteBuffer buf = ByteBuffer.allocate(10);

    // Read the bytes with the proper encoding for this platform.  If
    // you skip this step, you might see something that looks like
    // Chinese characters when you expect Latin-style characters.
    String encoding = System.getProperty("file.encoding");
    while (sbc.read(buf) > 0) {
        buf.rewind();
        System.out.print(Charset.forName(encoding).decode(buf));
        buf.flip();
    }
} catch (IOException x) {
    System.out.println("caught exception: " + x);
```

以下示例是为UNIX和其他POSIX文件系统编写的，它创建了一个具有一组特定文件权限的日志文件。此代码创建日志文件或附加到日志文件（如果已存在）。创建日志文件时，对所有者具有读/写权限，对组具有只读权限。

```java
import static java.nio.file.StandardOpenOption.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

public class LogFilePermissionsTest {

  public static void main(String[] args) {
  
    // Create the set of options for appending to the file.
    Set<OpenOption> options = new HashSet<OpenOption>();
    options.add(APPEND);
    options.add(CREATE);

    // Create the custom permissions attribute.
    Set<PosixFilePermission> perms =
      PosixFilePermissions.fromString("rw-r-----");
    FileAttribute<Set<PosixFilePermission>> attr =
      PosixFilePermissions.asFileAttribute(perms);

    // Convert the string to a ByteBuffer.
    String s = "Hello World! ";
    byte data[] = s.getBytes();
    ByteBuffer bb = ByteBuffer.wrap(data);
    
    Path file = Paths.get("./permissions.log");

    try (SeekableByteChannel sbc =
      Files.newByteChannel(file, options, attr)) {
      sbc.write(bb);
    } catch (IOException x) {
      System.out.println("Exception thrown: " + x);
    }
  }
}
```

## 创建常规和临时文件的方法

### 创建文件

您可以使用该[`createFile(Path, FileAttribute)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createFile-java.nio.file.Path-java.nio.file.attribute.FileAttribute...-)方法创建具有初始属性集的空文件 。例如，如果在创建时希望文件具有特定的文件权限集，请使用该`createFile`方法执行此操作。如果未指定任何属性，则使用默认属性创建文件。如果该文件已存在，则`createFile`抛出异常。

在单个原子操作中，该`createFile`方法检查文件是否存在并使用指定的属性创建该文件，这使得该过程对恶意代码更安全。

以下代码段创建一个具有默认属性的文件：

```java
Path file = ...;
try {
    // Create the empty file with default permissions, etc.
    Files.createFile(file);
} catch (FileAlreadyExistsException x) {
    System.err.format("file named %s" +
        " already exists%n", file);
} catch (IOException x) {
    // Some other sort of failure, such as permissions.
    System.err.format("createFile error: %s%n", x);
}
```

[POSIX文件权限](fileAttr.html#posix)有一个示例，用于`createFile(Path, FileAttribute<?>)`创建具有预设权限的文件。

您还可以使用这些`newOutputStream`方法创建新文件，如 [使用流I / O创建和写入文件中所述](file.html#createStream)。如果打开新输出流并立即关闭它，则会创建一个空文件。

### 创建临时文件

您可以使用以下`createTempFile`方法之一创建临时文件：

- [`createTempFile(Path, String, String, FileAttribute)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createTempFile-java.nio.file.Path-java.lang.String-java.lang.String-java.nio.file.attribute.FileAttribute...-)
- [`createTempFile(String, String, FileAttribute)`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createTempFile-java.lang.String-java.lang.String-java.nio.file.attribute.FileAttribute...-)

第一种方法允许代码指定临时文件的目录，第二种方法在默认临时文件目录中创建新文件。这两种方法都允许您为文件名指定后缀，第一种方法允许您指定前缀。以下代码段给出了第二种方法的示例：

```java
try {
    Path tempFile = Files.createTempFile(null, ".myapp");
    System.out.format("The temporary file" +
        " has been created: %s%n", tempFile)
;
} catch (IOException x) {
    System.err.format("IOException: %s%n", x);
}
```

运行此文件的结果如下所示：

```java
The temporary file has been created: /tmp/509668702974537184.myapp
```

临时文件名的特定格式是特定于平台的。