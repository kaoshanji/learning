# 创建符号链接

在NIO.2中，创建符号链接非常容易。 

您只需访问 Files.createSymbolicLink（）方法，该方法使用符号链接的路径来创建目标的符号链接，以及在创建符号链接时自动设置的属性数组。

它返回符号链接的路径。

如果您的文件系统不支持符号链接，则出现UnsupportedOperationException将会抛出异常。 

另外，请记住，符号链接的目标可以是绝对的或相对（如第1章所述），并且可能存在或可能不存在。

以下代码段使用默认属性创建一个简单的符号链接。 

它创建一个文件 `C:\rafaelnadal\photos\rafa_winner.jpg` 的符号链接名为rafael.nadal.1（该文件为建议存在，并且文件系统必须具有创建符号链接的权限）。

```Java
Path link = FileSystems.getDefault().getPath("rafael.nadal.1");
Path target = FileSystems.getDefault().getPath("C:/rafaelnadal/photos", "rafa_winner.jpg");

try {
    Files.createSymbolicLink(link, target);
    } catch (IOException | UnsupportedOperationException | SecurityException e) {
        if (e instanceof SecurityException) {
        System.err.println("Permission denied!");
    }
        if (e instanceof UnsupportedOperationException) {
    System.err.println("An unsupported operation was detected!");
    }
        if (e instanceof IOException) {
    System.err.println("An I/O error occurred!");
    }
    System.err.println(e);
}
```

当您想要修改链接的默认属性时，可以使用 createSymbolicLink（）方法。 

此参数是FileAttribute类型的属性数组-该类封装了可以在创建新文件时自动设置的文件属性的值，目录或链接。 

以下代码段读取目标文件的属性并创建一个链接，从目标向链接分配属性。 

它为创建一个名为rafael.nadal.2的符号链接。

文件 `C:\rafaelnadal\photos\rafa_winner.jpg`（文件必须存在且文件系统必须具有创建符号链接的权限）。

```Java
…
Path link = FileSystems.getDefault().getPath("rafael.nadal.2");
Path target = FileSystems.getDefault().getPath("C:/rafaelnadal/photos", "rafa_winner.jpg");
try {
    PosixFileAttributes attrs = Files.readAttributes(target, PosixFileAttributes.class);
    FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(attrs.permissions());
    Files.createSymbolicLink(link, target, attr);
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
```

另外，可以在创建后使用setAttribute（）方法来修改链接属性。 

对于例如，以下代码片段读取了以下代码的lastModifiedTime和lastAccessTime属性：定位并将其设置为链接。

它为文件创建一个名为rafael.nadal.3的符号链接。

`C:\rafaelnadal\photos\rafa_winner.jpg`（文件必须存在，文件系统必须具有以下权限：创建符号链接）。

```Java
…
Path link = FileSystems.getDefault().getPath("rafael.nadal.3");
Path target = FileSystems.getDefault().getPath("C:/rafaelnadal/photos", "rafa_winner.jpg");

try {
    Files.createSymbolicLink(link, target);
    
    FileTime lm = (FileTime) Files.getAttribute(target, "basic:lastModifiedTime", NOFOLLOW_LINKS);
    FileTime la = (FileTime) Files.getAttribute(target,"basic:lastAccessTime", NOFOLLOW_LINKS);
    
    Files.setAttribute(link, "basic:lastModifiedTime", lm, NOFOLLOW_LINKS);
    Files.setAttribute(link, "basic:lastAccessTime", la, NOFOLLOW_LINKS);
    
    } catch (IOException | UnsupportedOperationException | SecurityException e) {
    if (e instanceof SecurityException) {
        System.err.println("Permision denied!");
    }
    if (e instanceof UnsupportedOperationException) {
        System.err.println("An unsupported operation was detected!");
    }
    if (e instanceof IOException) {
        System.err.println("An I/O error occured!");
    }
    System.err.println(e);
}
```

如果符号链接已经存在，则将抛出FileAlreadyExistsException异常。

----