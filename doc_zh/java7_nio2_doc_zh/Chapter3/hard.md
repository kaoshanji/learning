# 创建硬链接

您可以通过调用createLink（）方法来创建硬链接，该方法使用该链接创建路径到现有文件。 

它返回链接的路径，该路径代表新的目录条目。 

然后，您可以使用链接作为路径访问文件。

如果您的文件系统不支持硬链接，则出现UnsupportedOperationException异常将被抛出。 

此外，请记住，只能为现有文件创建硬链接。

以下代码段为文件创建名为rafael.nadal.4的硬链接 `C:\rafaelnadal\photos\rafa_winner.jpg`（文件必须存在，文件系统必须具有以下权限：创建硬链接）：

```Java
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
    Path link = FileSystems.getDefault().getPath("rafael.nadal.4");
    Path target = FileSystems.getDefault().getPath("C:/rafaelnadal/photos", "rafa_winner.jpg");

    try {
        Files.createLink(link, target);
        System.out.println("The link was successfully created!");
    } catch (IOException | UnsupportedOperationException | SecurityException e) {
        if (e instanceof SecurityException) {
            System.err.println("Permission denied!");
        }
        if (e instanceof UnsupportedOperationException) {
            System.err.println("An unsupported operation was detected!");
        }
        if (e instanceof IOException) {
            System.err.println("An I/O error occured!");
        }
        System.err.println(e);
        }
    }
}

```

如果硬链接已经存在，则将抛出FileAlreadyExistsException异常。

----