# 文件存储属性

如果您将计算机视为文件存储容器，则可以轻松确定更多类型的存储，例如分区，设备，卷等。 

NIO.2可以通过FileStore抽象类获得有关每种存储类型的信息。 

对于特定商店，您可以获取其名称，类型，总空间，已用空间和可用空闲空间。 

在以下小节中，您将了解如何获取有关默认文件系统中所有存储以及包含指定文件的存储的信息。

##  获取所有文件存储的属性

通过调用FileSystems.getDefault（）方法获得对默认文件系统的访问后，您可以轻松地遍历FileSystem.getFileStores（）方法提供的文件存储列表。 

以来每个实例（名称，类型，总空间，已用空间和可用可用空间）都是一个FileStore对象，可以调用相应的专用方法，例如name（），type（），getTotalSpace（）等。 

以下代码段可打印有关商店的信息：

```Java
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
…
FileSystem fs = FileSystems.getDefault();
for (FileStore store : fs.getFileStores()) {
    try {
        long total_space = store.getTotalSpace() / 1024;
        long used_space = (store.getTotalSpace() - store.getUnallocatedSpace()) / 1024;
        long available_space = store.getUsableSpace() / 1024;
        boolean is_read_only = store.isReadOnly();

        System.out.println("--- " + store.name() + " --- " + store.type());
        System.out.println("Total space: " + total_space);
        System.out.println("Used space: " + used_space);
        System.out.println("Available space: " + available_space);
        System.out.println("Is read only? " + is_read_only);
    } catch (IOException e) {
        System.err.println(e);
    }
}
```

以下是此代码的示例输出：

```base
--- --- NTFS

Total space: 39070048

Used space: 31775684

Available space: 7294364

--- --- NTFS

Total space: 39070048

Used space: 8530348

Available space: 30539700

--- SAMSUNG DVD RECORDER VOLUME --- UDF

Total space: 2936192

Used space: 2936192

Available space: 0
```

如上例所示，如果商店没有名称，则返回一个空白字符串。 

在另外，返回的磁盘空间量的值以字节表示，因此您可能想要将这些数字转换为千字节，兆字节或千兆字节，以使人类更容易阅读。

##  获取文件所在的文件存储的属性

基于FileStore类，您可以获取特定文件所在的文件存储的属性。 

这个可以通过调用Files.getFileStore（）方法来完成该任务，该方法获取单个参数表示文件（Path对象）。

NIO.2为您确定文件存储并提供对信息。 

以下代码显示了一种可能的方法：

```Java
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
…
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
try {
    FileStore store = Files.getFileStore(path);

    long total_space = store.getTotalSpace() / 1024;
    long used_space = (store.getTotalSpace() - store.getUnallocatedSpace()) / 1024;
    long available_space = store.getUsableSpace() / 1024;
    boolean is_read_only = store.isReadOnly();

    System.out.println("--- " + store.name() + " --- " + store.type());
    System.out.println("Total space: " + total_space);
    System.out.println("Used space: " + used_space);
    System.out.println("Available space: " + available_space);
    System.out.println("Is read only? " + is_read_only);

} catch (IOException e) {
    System.err.println(e);
}
```

此代码的示例输出如下：

```base
--- --- NTFS

Total space: 39070048

Used space: 8530348

Available space: 30539700

Is read only? false
```

文件存储区可能支持一个或多个FileStoreAttributeView类，这些类提供了只读或一组文件存储属性的可更新视图，如下所示：

```Java
FileStoreAttributeView fsav = store.getFileStoreAttributeView(FileStoreAttributeView.class);
```

另外，您可以使用getAttribute（）方法读取文件存储属性的值。

----