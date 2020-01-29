# 新建、查看、写入文件

文件最常见的操作可能涉及创建，读取和/或写入操作。

NIO.2带有许多专用方法，可以以各种复杂度执行这些操作和性能（从常用的小文件的方法（方便阅读所有文件的情况）字节数组中的字节数）以获取高级功能的方法，例如文件锁定和内存映射的I / O。

本节以小文件的方法开始，以缓冲和未缓冲的方法结束流。

流代表输入源或输出目的地（可以是从磁盘文件到内存阵列）。

流支持不同类型的数据，例如字符串，字节，原始数据类型，本地化字符和对象。

在无缓冲的流中，每个读取或写入请求均由基础操作系统，而在缓冲流中，则从称为“内存”的存储区读取数据缓冲区并且仅在缓冲区为空时才调用本机输入API。

同样，缓冲输出流将数据写入缓冲区，并且仅在缓冲区已满时才调用本机输出API。

当一个缓冲区被写出而没有等待它被填满，我们说缓冲区被刷新了。

##  使用标准打开选项

从NIO.2开始，专门用于创建，读取和写入动作（或任何其他动作）的方法涉及打开文件）支持可选参数OpenOption，用于配置如何打开文件或创建一个文件。 

实际上，OpenOption是java.nio.file包中的接口，它有两个实现：LinkOption类（记住众所周知的NOFOLLOW_LINKS枚举常量）和StandardOpenOption类，它定义了以下枚举：

|常量|说明|
|----|----|
|READ|打开文件进行读取访问|
|WRITE|打开文件进行写访问|
|CREATE|如果不存在，则创建一个新文件|
|CREATE_NEW|创建一个新文件，如果文件已存在|
|APPPEND|将数据追加到文件末尾（用于写入并创建）|
|DELETE_ON_CLOSE|流关闭时删除文件（用于删除临时文件）|
|TRUNCATE_EXISTING|将文件截断为0个字节（与WRITE一起使用）选项）|
|SPARSE|导致新创建的文件稀疏|
|SYNC|保持文件内容和元数据同步与底层存储设备|
|DSYNC|使文件内容与基础存储设备|

在您查看以下内容后，这些常量中的一些将在接下来的部分中显示。创建一个新文件。

##  创建一个新文件

创建新文件是一项常见任务，可以通过调用Files.createFile（）方法来完成。

此方法获取要创建的文件（路径）和文件属性的可选列表（FileAttribute <？>）在创建时自动设置，它返回创建的文件。 

以下代码片段在 `C:\rafaelnadal\tournaments\2010` 中创建一个名为SonyEricssonOpen.txt的新文件

具有默认属性的目录（目录必须存在）（最初，该文件不得存在；否则，将抛出FileAlreadyExistsException异常）：

```Java
Path newfile = FileSystems.getDefault().getPath("C:/rafaelnadal/tournaments/2010/SonyEricssonOpen.txt");
…
try {
    Files.createFile(newfile);
} catch (IOException e) {
    System.err.println(e);
}
```

您可以在创建时添加一组属性，如以下代码片段所示。 

此代码在具有特定权限的POSIX文件系统上创建一个新文件。

```Java
Path newfile = FileSystems.getDefault().getPath("/home/rafaelnadal/tournaments/2010/SonyEricssonOpen.txt");

Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-------");
FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

try {
    Files.createFile(newfile, attr);
} catch (IOException e) {
    System.err.println(e);
}
```

您将很快看到，这不是创建新文件的唯一方法。

##  写一个小文件

NIO.2带有一个优雅的解决方案，用于编写小的二进制/文本文件。 

该设施是通过以下方式提供的两个Files.write（）方法。 

这两种方法都会打开文件进行写入（这可能涉及创建文件（如果不存在）），或者最初将现有的常规文件截断为0个字节。

在所有字节之后或写入行，该方法关闭文件（即使发生I / O错误或异常，它也会关闭文件）。

简而言之，此方法的作用就像存在CREATE，TRUNCATE_EXISTING和WRITE选项一样。

当然，如果未指定其他选项，则默认情况下适用。

### 使用 write()方法写入字节

可以使用Files.write（）方法完成将字节写入文件的操作。 

此方法获取路径到文件，字节数组，其中包含要写入的字节，以及用于指定如何打开文件的选项。 

它返回写入文件的路径。

以下代码段将字节数组（代表一个小的网球图片）与默认的打开选项（文件名为ball.png，它将写在 `C:\rafaelnadal\photos` 中目录）：

```Java
Path ball_path = Paths.get("C:/rafaelnadal/photos", "ball.png");
…
byte[] ball_bytes = new byte[]{
(byte)0x89,(byte)0x50,(byte)0x4e,(byte)0x47,(byte)0x0d,(byte)0x0a,(byte)0x1a,(byte)0x0a,
(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0d,(byte)0x49,(byte)0x48,(byte)0x44,(byte)0x52,
(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,
(byte)0x08,(byte)0x02,(byte)0x00,
…
(byte)0x49,(byte)0x45,(byte)0x4e,(byte)0x44,(byte)0xae,(byte)0x42,(byte)0x60,(byte)0x82
};

try {
    Files.write(ball_path, ball_bytes);
} catch (IOException e) {
    System.err.println(e);
}
```

现在，如果您检查相应的路径，您会发现一张代表网球的小图片。

此外，如果您需要编写文本（字符串）并且要使用此方法，请转换文本转换为字节数组，如下所示（文件名为wiki.txt，并在C：\ rafaelnadal \ wiki中创建）：

```Java
Path rf_wiki_path = Paths.get("C:/rafaelnadal/wiki", "wiki.txt");
…
String rf_wiki = "Rafael \"Rafa\" Nadal Parera (born 3 June 1986) is a Spanish professional
tennis " + "player and a former World No. 1. As of 29 August 2011 (2011 -08-29)[update], he is
ranked No. 2 " + "by the Association of Tennis Professionals (ATP). He is widely regarded as
one of the greatest players " + "of all time; his success on clay has earned him the nickname
\"The King of Clay\", and has prompted " + "many experts to regard him as the greatest clay
court player of all time. Some of his best wins are:";

try {
    byte[] rf_wiki_byte = rf_wiki.getBytes("UTF-8");
    Files.write(rf_wiki_path, rf_wiki_byte);
} catch (IOException e) {
    System.err.println(e);
}
```

即使这可行，使用下面介绍的write（）方法将文本写入文件也要容易得多。

### 用write（）方法写行

可以通过使用Files.write（）方法将行写入文件中（“行”是一个字符顺序）。 

在每行之后，此方法会附加平台的行分隔符（line.separator系统属性）。 

此方法获取文件的路径，char序列上的可迭代对象，用于编码，以及用于指定如何打开文件的选项。 

它返回写入文件的路径。

以下代码段将一些行写入文件（实际上，它会将一些行附加到文件的末尾）在上一节中创建的文件wiki.txt）：

```Java
Path rf_wiki_path = Paths.get("C:/rafaelnadal/wiki", "wiki.txt");
…
Charset charset = Charset.forName("UTF-8");
ArrayList<String> lines = new ArrayList<>();
lines.add("\n");
lines.add("Rome Masters - 5 titles in 6 years");
lines.add("Monte Carlo Masters - 7 consecutive titles (2005-2011)");
lines.add("Australian Open - Winner 2009");
lines.add("Roland Garros - Winner 2005-2008, 2010, 2011");
lines.add("Wimbledon - Winner 2008, 2010");
lines.add("US Open - Winner 2010");

try {
    Files.write(rf_wiki_path, lines, charset, StandardOpenOption.APPEND);
} catch (IOException e) {
    System.err.println(e);
}
```

##  读取小文件

NIO.2提供了一种快速读取单个字节/文本文件的快速方法。 

提供此设施通过Files.readAllBytes（）和Files.readAllLines（）方法。 

这些方法阅读了全文文件的字节或行分别读取一次，并注意打开和关闭流您在读取文件或发生I / O错误或异常之后。

### readAllBytes()

Files.readAllBytes（）方法将整个文件读入字节数组，而Files.readAllLines（）方法将整个文件读入String的集合中（如下一节所述）。 

专注于readAllBytes（）方法，以下代码片段读取先前创建的ball.png二进制文件（文件必须存在）到字节数组中（文件路径作为参数传递）：

```Java
Path ball_path = Paths.get("C:/rafaelnadal/photos", "ball.png");
…
try {
    byte[] ballArray = Files.readAllBytes(ball_path);
} catch (IOException e) {
    System.out.println(e);
}
```

如果您要确保返回的字节数组包含图片，则可以运行（作为测试）以下代码段将字节写入同一文件夹中的名为bytes_to_ball.png的文件中目录：

```Java
Files.write(ball_path.resolveSibling("bytes_to_ball.png"), ballArray);
```

或者，您可以按以下方式使用ImageIO。 

这行ImageIO.write（）将写入您的bufferedImage数据到您的磁盘作为PNG类型的文件，并将其存储在 `C:\rafaelnadal\photos` 目录中。

```Java
BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(ballArray));
ImageIO.write(bufferedImage, "png", (ball_path.resolveSibling("bytes_to_ball.png")).toFile());
```

readAllBytes（）方法还可以读取文本文件。

这次字节数组应转换为字符串，如以下示例所示（您可以使用适合于文本文件的任何字符集）：

```Java
Path wiki_path = Paths.get("C:/rafaelnadal/wiki", "wiki.txt");
…
try {
    byte[] wikiArray = Files.readAllBytes(wiki_path);
    String wikiString = new String(wikiArray, "ISO-8859-1");
    System.out.println(wikiString);
} catch (IOException e) {
    System.out.println(e);
}
```

如果文件太大（大于2GB），则无法分配阵列的大小，并且将抛出OutOfMemory错误。 

这取决于JVM上的Xmx参数：对于32位JVM，不能大于2GB（但默认情况下通常为256MB，具体取决于平台）。 

对于64位JVM，它可以是更大-可能达到数十GB。

### readAllLines()

在前面的示例中，您看到了如何通过readAllBytes（）方法读取文本文件。 

更多方便的解决方案是使用readAllLines（）方法，因为此方法将读取整个文件并返回一个字符串列表，可以很容易地如下循环（将此文件的路径传递给此方法）读取和用于解码的字符集）：

```Java
Path wiki_path = Paths.get("C:/rafaelnadal/wiki", "wiki.txt");
…
Charset charset = Charset.forName("ISO-8859-1");

try {
    List<String> lines = Files.readAllLines(wiki_path, charset);
    for (String line : lines) {
        System.out.println(line);
    }
} catch (IOException e) {
    System.out.println(e);
}
```

符合官方文档，此方法将以下内容识别为行终止符：

```base
\u000D followed by \u000A: CARRIAGE RETURN followed by LINE FEED

\u000A: LINE FEED

\u000D: CARRIAGE RETURN
```

##  使用缓存流

在大多数操作系统中，读取或写入数据的系统调用是一项昂贵的操作。 

缓冲区可以修复通过在缓冲的方法和操作系统之间提供内存空间来解决此问题。 

之前在调用本机API时，这些方法从操作之间获取数据或将数据放入缓冲区或将数据放入缓冲区系统和应用程序，从而减少了数量，从而提高了应用程序的效率系统调用次数-仅当缓冲区已满或为空时才访问磁盘，具体取决于它是写操作或读操作。 

NIO.2提供了两种通过以下方式读取和写入文件的方法缓冲区：分别为Files.newBufferedReader（）和Files.newBufferedWriter（）。 

这两个方法获取Path实例并返回旧的JDK 1.1 BufferedReader或BufferedWriter实例。

### newBufferedWriter()

newBufferedWriter（）方法获取文件的路径，用于编码的字符集以及选项指定如何打开文件。 

它返回一个新的默认缓冲编写器（这是特定于java.io的BufferedWriter）。 

该方法打开文件进行写入（如果没有，则可能涉及创建文件）存在）或最初将现有的常规文件截断为0个字节。 

简而言之，此方法就像存在CREATE，TRUNCATE_EXISTING和WRITE选项（默认情况下，当没有其他选项时适用选项）。

以下代码段使用缓冲区将数据附加到先前创建的wiki.txt文件中（该文件存在；您应该在C：\ rafaelnadal \ wiki目录中找到它）：

```Java
Path wiki_path = Paths.get("C:/rafaelnadal/wiki", "wiki.txt");
…
Charset charset = Charset.forName("UTF-8");
String text = "\nVamos Rafa!";

try (BufferedWriter writer = Files.newBufferedWriter(wiki_path, charset,StandardOpenOption.APPEND)) {
    writer.write(text);
} catch (IOException e) {
    System.err.println(e);
}

```

### newBufferedReader()

newBufferedReader（）方法可用于通过缓冲区读取文件。

该方法获取到的路径该文件和一个字符集，用于将字节解码为字符。 

它返回一个新的默认缓冲读取器（这是特定于java.io的BufferedReader）。

以下代码段使用UTF-8字符集读取wiki.txt文件：

```Java
Path wiki_path = Paths.get("C:/rafaelnadal/wiki", "wiki.txt");
…
Charset charset = Charset.forName("UTF-8");

try (BufferedReader reader = Files.newBufferedReader(wiki_path, charset)) {
    String line = null;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    System.err.println(e);
}
```

如果您遵循前面各节中的示例并创建了整个wiki.txt文件，那么前面的代码将输出以下内容：

```base
Rafael "Rafa" Nadal Parera (born 3 June 1986) is a Spanish professional tennis player and a
former World No. 1. As of 29 August 2011 (2011 -08-29)[update], he is ranked No. 2 by the
Association of Tennis Professionals (ATP). He is widely regarded as one of the greatest
players of all time; his success on clay has earned him the nickname "The King of Clay", and
has prompted many experts to regard him as the greatest clay court player of all time. Some
of his best wins are:

Rome Masters - 5 titles in 6 years

Monte Carlo Masters - 7 consecutive titles (2005-2011)

Australian Open - Winner 2009

Roland Garros - Winnner 2005-2008, 2010, 2011
```

##  使用无缓冲流

可以通过新的NIO.2方法获得未缓冲的流，并且可以使用其中任何一种可以使用java.io提供的包装习惯原样或转换为缓冲流
API。 

未缓冲的流方法是Files.newInputStream（）（从文件读取的输入流）和Files.newOutputStream（）（要写入文件的输出流）。

### newOutputStream()

newOutputStream（）方法获取文件的路径以及用于指定如何打开文件的选项。 

它返回一个新的默认线程安全无缓冲流，该流可用于将字节写入文件（这是一个特定于java.io的OutputStream）。

该方法打开文件进行写入（如果需要，则可以创建文件它不存在）或最初将现有的常规文件截断为0个字节的大小。 

简而言之，此方法起作用就像存在CREATE，TRUNCATE_EXISTING和WRITE选项一样（默认情况下，未指定其他选项）。

以下代码段会将文本行“ Racquet：Babolat AeroPro Drive GT”写入文件 `C:\rafaelnadal\equipment\racquet.txt`（该文件最初不存在，但会自动因为未指定选项而创建）：

```Java
Path rn_racquet = Paths.get("C:/rafaelnadal/equipment", "racquet.txt");
String racquet = "Racquet: Babolat AeroPro Drive GT";

byte data[] = racquet.getBytes();

try (OutputStream outputStream = Files.newOutputStream(rn_racquet)) {
    outputStream.write(data);
} catch (IOException e) {
    System.err.println(e);
}
```

此外，如果您决定最好使用缓冲流而不是前面的方法代码，建议基于java.io API进行转换，如以下代码所示，会在文件racquet.txt（文件必须存在）的后面附加文本“字符串：Babolat RPM Blast 16”：

```Java
Path rn_racquet = Paths.get("C:/rafaelnadal/equipment", "racquet.txt");
String string = "\nString: Babolat RPM Blast 16";

try (OutputStream outputStream = Files.newOutputStream(rn_racquet, StandardOpenOption.APPEND);
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
    writer.write(string);
} catch (IOException e) {
    System.err.println(e);
}
```

### newInputStream()

newInputStream（）方法获取打开文件的路径以及指定如何打开文件的选项。

它返回一个新的默认线程安全无缓冲流，该流可用于从文件读取字节（这是特定于java.io的InputStream）。 

该方法打开文件以供读取； 如果没有选项，则等效于使用READ选项打开文件。

以下代码段读取文件racquet.txt的内容（该文件必须存在）：

```Java
Path rn_racquet = Paths.get("C:/rafaelnadal/equipment", "racquet.txt");
…
int n;
try (InputStream in = Files.newInputStream(rn_racquet)) {
    while ((n = in.read()) != -1) {
        System.out.print((char)n);
    }
} catch (IOException e) {
    System.err.println(e);
}
```

您可能已经从java.io API知道了，InputStream类还提供了read（）填充字节类型的缓冲区数组的方法。 

因此，您可以按如下方式修改前面的代码（请记住，您仍在处理无缓冲的流）：

```Java
Path rn_racquet = Paths.get("C:/rafaelnadal/equipment", "racquet.txt");
…
int n;
byte[] in_buffer = new byte[1024];
try (InputStream in = Files.newInputStream(rn_racquet)) {
    while ((n = in.read(in_buffer)) != -1) {
        System.out.println(new String(in_buffer));
    }
} catch (IOException e) {
    System.err.println(e);
}
```

调用read（in_buffer）方法与调用read（in_buffer，0，in_buffer.length）方法

此外，您可以通过与java.io API。 

下面的示例与前面的示例具有相同的效果，但更多效率：

```Java
Path rn_racquet = Paths.get("C:/rafaelnadal/equipment", "racquet.txt");
…
try (InputStream in = Files.newInputStream(rn_racquet);
    BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
    String line = null;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    System.err.println(e);
}
```

过去三个示例将具有相同的输出：

```base
Racquet: Babolat AeroPro Drive GT

String: Babolat RPM Blast 16
```

----
