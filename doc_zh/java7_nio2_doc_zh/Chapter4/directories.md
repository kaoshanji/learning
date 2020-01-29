# 新建和查看目录

关于创建和读取目录，NIO.2在其中提供了一组Files专用方法。

在本节中，您将发现如何列出文件系统根目录，创建目录（包括临时目录），列出目录的内容，并为目录编写和使用过滤器。

##  列出根目录下文件

在Java 6中，文件系统根目录被提取为File对象的数组。

从Java 7开始，NIO.2获取文件系统根目录作为Path对象的Iterable。 

此Iterable由getRootDirectories（）方法如下：

```Java
Iterable<Path> dirs = FileSystems.getDefault().getRootDirectories();

for (Path name : dirs) {
    System.out.println(name);
}
```

可能的输出如下：

```base
C:\

D:\

E:\
```

您可以轻松地从Iterable进入数组，如下所示：

```Java
Iterable<Path> dirs = FileSystems.getDefault().getRootDirectories();
ArrayList<Path> list = new ArrayList<Path>();

for (Path name : dirs) {
// System.out.println(name);
    list.add(name);
}

Path[] arr = new Path[list.size()];
list.toArray(arr);

for(Path path : arr) {
    System.out.println(path);
}
```

如果需要将文件系统根目录提取为File数组，请使用Java 6解决方案：

```Java
File[] roots = File.listRoots();

for (File root : roots) {
    System.out.println(root);
}
```

##  创建一个新目录

创建新目录是一项常见任务，您可以通过调用 Files.createDirectory（）方法。 

此方法获取要创建的目录（路径）和以下内容的可选列表，在创建时自动设置的文件属性（FileAttribute <？>）。

它返回创建的目录

以下代码段在 `C:\rafaelnadal\tournaments` 下创建一个名为 `\2010` 的新目录。

具有默认属性的目录（该目录不能存在）：

```Java
Path newdir = FileSystems.getDefault().getPath("C:/rafaelnadal/tournaments/2010/");
…
try {
    Files.createDirectory(newdir);
} catch (IOException e) {
    System.err.println(e);
}
```

您可以在创建时添加一组属性，如以下示例代码片段所示，在具有特定权限的POSIX文件系统上创建一个新目录：

```Java
Path newdir = FileSystems.getDefault().getPath("/home/rafaelnadal/tournaments/2010/");
…
Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-x---");

FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
try {
    Files.createDirectory(newdir, attr);
} catch (IOException e) {
    System.err.println(e);
}
```

如果目录存在，则createDirectory（）方法将引发异常.

有时您需要创建的不仅仅是一个目录。 

例如，您可能需要创建一系列层次目录，例如 `\statistics\win\prizes`。 

您可以将级联称为createDirectory（）方法，或更优雅的是，使用Files.createDirectories（）方法，这将在单个调用中创建目录序列； 

根据需要从中创建目录自上而下，以`\statistics`为相对根，而`\prizes`为最后一叶的顺序目录作为Path实例传递，带有或不带有在以下情况下自动设置的文件属性列表创建目录。 

以下代码段显示了如何创建一系列层次结构 `C:\rafaelnadal` 目录下的目录：

```Java
Path newdir= FileSystems.getDefault().getPath("C:/rafaelnadal/", "statistics/win/prizes");
…
try {
    Files.createDirectories(newdir);
} catch (IOException e) {
    System.err.println(e);
}
```

如果在目录序列中已经存在一个或多个目录，则createDirectories（）方法不会引发异常，而只是“跳转”该目录并转到下一个目录。 

这个方法创建某些目录后可能会失败，但并非所有目录都失败。

##  列出目录的内容

使用目录和文件通常涉及循环目录内容以用于不同目的。

NIO.2通过名为DirectoryStream的可迭代流提供了此功能，该流是一个接口实现Iterable。 

通过以下命令可以直接访问目录流 Files.newDirectoryStream（）方法，该方法获取目录的路径并返回一个新的并打开目录流。

### 列出整个内容

以下代码段将以链接，文件，子目录，和隐藏文件（列出的目录为 `C:\rafaelnadal\tournaments\2009`）：

```Java
Path path = Paths.get("C:/rafaelnadal/tournaments/2009");
//no filter applied
System.out.println("\nNo filter applied:");

try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {

    for (Path file : ds) {
        System.out.println(file.getFileName());
    }

}catch(IOException e) {
    System.err.println(e);
}
```

可能的输出如下（这是`C:\rafaelnadal\tournaments\2009`的全部内容目录）：

```base
No filter applied:

AEGON.txt

BNP.txt

MutuaMadridOpen.txt

supershot.bmp

Tickets.zip

TournamentsCalendar.xls

Videos
```

### 通过应用Glob模式列出内容

有时，您可能只需要列出满足某些条件的内容，这需要应用一个过滤到目录的内容。 

通常，您只需要提取其文件和子目录名称与特定模式匹配。 

NIO.2将此特定模式定义为内置的全局过滤器。

符合NIO.2文档，全局模式只是与其他模式匹配的字符串字符串-在这种情况下，目录和文件名。 

由于这是一种模式，因此必须遵守一些规则，例如如下：

-   *：表示（匹配）任意数量的字符，包括无字符。
-   **：与*类似，但跨目录边界。
-   ？：仅代表（匹配）一个字符。
-   {}：表示以逗号分隔的子模式的集合。 例如，{A，B，C}匹配A，B或C。
-   []：传达一组单个字符或一系列字符（如果连字符）字符存在。 一些常见的示例包括：
    -   [0-9]：匹配任意数字
    -   [A-Z]：匹配任何大写字母
    -   [a-z，A-Z]：匹配任何大写或小写字母
    -   [12345]：匹配1、2、3、4或5中的任何一个
-   在方括号内，* 、?和\匹配。
-   所有其他字符匹配。
-   要匹配* 、?或其他特殊字符，可以使用反斜杠字符\。 例如，\\匹配单个反斜杠，而\？匹配问号。

既然您知道了如何构建全局模式，那么现在该介绍newDirectoryStream（）了。

获取目录路径的方法和要应用的全局过滤器。 

以下示例将提取所有 `C:\ rafaelnadal\tournaments\2009`中的PNG，JPG和BMP类型的文件（无论它们的名称如何）目录：

```Java
Path path = Paths.get("C:/rafaelnadal/tournaments/2009");
…
//glob pattern applied
System.out.println("\nGlob pattern applied:");
try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, "*.{png,jpg,bmp}")) {
    for (Path file : ds) {
        System.out.println(file.getFileName());
    }
} catch (IOException e) {
    System.err.println(e);
}
```

输出将如下所示：

```base
Glob pattern applied:

supershot.bmp
```

### 通过应用用户定义的过滤器列出内容

如果全局模式不能满足您的需求，那么该是编写自己的过滤器的时候了。 

这是一个简单的任务需要实现`DirectoryStream.Filter <T>`接口，该接口只有一个方法，命名为accept（）。 

根据您的实现，路径是被接受还是被拒绝。 例如，以下代码片段在最终结果中仅接受目录：

```Java
Path path = Paths.get("C:/rafaelnadal/tournaments/2009");
…
//user-defined filter - only directories are accepted
DirectoryStream.Filter<Path> dir_filter = new DirectoryStream.Filter<Path>() {

public boolean accept(Path path) throws IOException {
    return (Files.isDirectory(path, NOFOLLOW_LINKS));
    }
};
```

接下来，将创建的过滤器作为参数传递给newDirectoryStream（）方法：

```Java
System.out.println("\nUser defined filter applied:");

try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, dir_filter)) {
    for (Path file : ds) {
        System.out.println(file.getFileName());
    }
    
    } catch (IOException e) {
        System.err.println(e);
}

```

输出将如下所示：

```base
User defined filter applied:

videos
```

下表列出了一组常用的过滤器：

-   仅接受大于200KB的文件/目录的过滤器：

```Java
DirectoryStream.Filter<Path> size_filter = new DirectoryStream.Filter<Path>() {

public boolean accept(Path path) throws IOException {
    return (Files.size(path) > 204800L);
    }
};
```

-   仅接受当天修改的文件的过滤器：

```Java
DirectoryStream.Filter<Path> time_filter = new DirectoryStream.Filter<Path>() {

public boolean accept(Path path) throws IOException {
    long currentTime = FileTime.fromMillis(System.currentTimeMillis()).to(TimeUnit.DAYS);
    long modifiedTime = ((FileTime) Files.getAttribute(path, "basic:lastModifiedTime",NOFOLLOW_LINKS)).to(TimeUnit.DAYS);
    
    if (currentTime == modifiedTime) {
        return true;
    }
        return false;
    }
};
```

-   仅接受隐藏文件/目录的过滤器：

```Java
DirectoryStream.Filter<Path> hidden_filter = new DirectoryStream.Filter<Path>() {

public boolean accept(Path path) throws IOException {
    return (Files.isHidden(path));
    }
};
```

----