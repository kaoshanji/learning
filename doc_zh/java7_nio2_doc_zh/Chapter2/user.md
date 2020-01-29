# 自定义文件属性视图

如果发现没有足够的内置属性来满足您的需求，或者您有一些独特的属性要与文件关联的元数据（对文件系统有意义），您可以定义自己的属性。 

NIO.2通过以下方式提供用户定义的文件属性视图（扩展属性）：UserDefinedFileAttributeView接口。 

此功能使您可以将文件的任何属性关联到文件。

您认为对您的用例有用。 

例如，如果您开发分布式文件系统。 

例如，您可以添加一个布尔属性，以验证文件是否为复制或分发到其他位置。

##  检查用户定义的属性可支持性

在尝试创建自己的属性之前，请检查您的文件系统是否支持此功能。

由于是通过文件存储而不是文件本身检查的，因此首先需要获取所需的文件存储。

然后，可以调用带有字符串参数的supportsFileAttributeView（）方法。

表示文件属性视图的名称，或表示为UserDefinedFileAttributeView.class的视图。

它返回一个布尔值，如下所示：

```Java
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
…
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");

try {
    FileStore store = Files.getFileStore(path);
    if (!store.supportsFileAttributeView(UserDefinedFileAttributeView.class)) {
        System.out.println("The user defined attributes are not supported on: " + store);
    } else {
        System.out.println("The user defined attributes are supported on: " + store);
    }
} catch (IOException e) {
    System.err.println(e);
}
```

您可以通过直接从默认文件中获取所有文件存储或一组文件存储来执行此检查文件系统。 、

不需要从文件所在的位置获取文件存储。

##  用户定义属性的操作

如果您的文件系统支持用户定义的属性，那么您都可以创建自己的属性。 

接下来，您将了解如何定义属性，如何列出用户定义的属性以及如何删除用户定义的属性属性。

本节的重点应放在用户定义属性的生命周期上。

### 定义用户属性

首先，您将定义一个名为file.description的属性，其值为“此文件包含私有信息！”。

通过调用Files.getFileAttributeView（）获得视图后，您可以编写此用户定义的属性如下：

```Java
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
…
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
UserDefinedFileAttributeView udfav = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);

try {

    int written = udfav.write("file.description", Charset.defaultCharset().encode("This file contains private information!"));
} catch (IOException e) {
    System.err.println(e);
}
```

write（）方法将给定缓冲区中的属性值作为字节序列写入。 

它接收两个参数：属性名称和包含属性值的缓冲区。 

如果一个属性给定名称的值已经存在，则其值将被替换。 

如您所见，该方法返回一个int，代表写入的字节数，可能为零。

### 列出用户定义的属性名称和值大小

您随时可以通过调用以下命令来查看用户定义的属性名称和值大小的列表 UserDefinedFileAttributeView.list（）方法。 

返回的列表是代表以下内容的字符串的集合属性名称。 

将其名称传递给UserDefinedFileAttributeView.size（）方法将导致属性值的大小。

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
…
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
UserDefinedFileAttributeView udfav = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);

try {
    for (String name : udfav.list()) {
    System.out.println(udfav.size(name) + " " + name);
    }
} catch (IOException e) {
    System.err.println(e);
}
```

### 获取用户定义属性的值

读取用户定义属性的值是通过使用 UserDefinedFileAttributeView.read（）方法。 

您将属性名称和目标传递给它缓冲区，它返回指定缓冲区中的值。 

以下代码段显示了如何操作它：

```Java
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
…
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
UserDefinedFileAttributeView udfav = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);

try {
    int size = udfav.size("file.description");
    ByteBuffer bb = ByteBuffer.allocateDirect(size);
    udfav.read("file.description", bb);
    bb.flip();
    System.out.println(Charset.defaultCharset().decode(bb).toString());
} catch (IOException e) {
    System.err.println(e);
}

```

使用UserDefinedFileAttributeView.size（）方法，您可以轻松设置正确的代表用户定义属性值的缓冲区。

您也可以使用getAttribute（）方法读取属性。 该值以字节数组形式返回（字节[]）。

### 删除文件的用户定义属性

当用户定义的属性不再有用时，您可以通过调用 UserDefinedFileAttributeView.delete（）方法。 

您只需将属性名称提供给方法，它将为您完成其余的工作。 

下面显示了如何删除属性先前定义：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
…
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
UserDefinedFileAttributeView udfav = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);

try {
    udfav.delete("file.description");
} catch (IOException e) {
    System.err.println(e);
}
```

----
