# 特定文件系统里的视图

在尝试访问视图的属性之前，请确保文件系统支持相应的视图。 

使用NIO.2，您可以按名称查看受支持视图的整个列表，也可以检查是否文件存储区-由FileStore类表示，该类映射任何类型的存储区，例如分区，设备，卷等-支持特定视图。

获得对默认文件系统的访问后，只需调用FileSystems.getDefault（）方法-您可以轻松地迭代由FileSystem.supportedFileAttributeViews（）方法。

以下代码段显示了如何执行此操作：

```Java
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Set;
…
FileSystem fs = FileSystems.getDefault();
Set<String> views = fs.supportedFileAttributeViews();

for (String view : views) {
    System.out.println(view);
}
```

例如，对于Windows 7，前面的代码返回以下结果：

```base
acl
basic
owner
user
dos
```

所有文件系统都支持基本视图，因此您应该至少在输出中获得基本名称。

可以通过调用FileStore.supportsFileAttributeView（）测试文件存储上的特定视图。

您可以将所需的视图作为字符串或类名传递。 

以下代码检查所有可用文件存储是否支持基本视图：

```Java
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.attribute.BasicFileAttributeView;
…
FileSystem fs = FileSystems.getDefault();
for (FileStore store : fs.getFileStores()) {
    boolean supported = store.supportsFileAttributeView(BasicFileAttributeView.class);
    System.out.println(store.name() + " ---" + supported);
}
```

此外，您可以检查特定文件所在的文件存储是否支持单个视图，例如在此示例中显示：

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
    boolean supported = store.supportsFileAttributeView("basic");
    System.out.println(store.name() + " ---" + supported);
} catch (IOException e) {
    System.err.println(e);
}
```

现在您可以确定文件系统支持哪些视图，是时候深入了解了并从基本视图开始探索每个视图的属性。

----