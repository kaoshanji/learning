# 基本视图

大多数文件系统实现都支持一组通用属性（大小，创建时间，最后访问时间）时间，上次修改时间等）。 

这些属性被分组到名为BasicFileAttributeView的视图中并可以按照以下小节中的说明进行提取和设置。

##  使用readAttributes（）获取批量属性

可以使用readAttributes（）方法按如下方式批量提取属性（varargs参数当前支持LinkOption.NOFOLLOW_LINKS枚举-不跟随符号链接）：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
…
BasicFileAttributes attr = null;
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
try {
    attr = Files.readAttributes(path, BasicFileAttributes.class);
} catch (IOException e) {
    System.err.println(e);
}

System.out.println("File size: " + attr.size());
System.out.println("File creation time: " + attr.creationTime());
System.out.println("File was last accessed at: " + attr.lastAccessTime());
System.out.println("File was last modified at: " + attr.lastModifiedTime());

System.out.println("Is directory? " + attr.isDirectory());
System.out.println("Is regular file? " + attr.isRegularFile());
System.out.println("Is symbolic link? " + attr.isSymbolicLink());
System.out.println("Is other? " + attr.isOther());

```

##  使用getAttribute（）获取单个属性

如果您需要提取单个属性而不是批量提取所有属性，请使用getAttribute（）方法。 

您需要传递文件路径和属性名称，并指定是否需要遵循符号链接。 

以下代码片段显示了如何提取size属性值。 请注意，getAttribute（）方法返回一个Object，因此您需要进行显式转换，具体取决于在属性的值类型上。

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
…
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
try {
    long size = (Long)Files.getAttribute(path, "basic:size", NOFOLLOW_LINKS);
    System.out.println("Size: " + size);
} catch (IOException e) {
    System.err.println(e);
}
```

-   基本属性名称在此处列出：
    -   lastModifiedTime
    -   lastAccessTime
    -   creationTime
    -   size
    -   isRegularFile
    -   isDirectory
    -   isSymbolicLink
    -   isOther
    -   fileKey

检索单个属性的公认格式是[view-name：] attribute-name。

##  更新基本属性

更新任何或所有文件的上次修改时间，上次访问时间和创建时间属性可以是由setTimes（）方法完成，该方法接受代表最后修改的三个参数时间。

上次访问时间和创建时间作为FileTime实例，这是Java 7中的新类代表文件时间戳属性的值。 

如果lastModifiedTime，lastAccessTime或creationTime的值为null，因此不会更改相应的时间戳。

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
…
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
long time = System.currentTimeMillis();
FileTime fileTime = FileTime.fromMillis(time);

try {
    Files.getFileAttributeView(path,
    BasicFileAttributeView.class).setTimes(fileTime, fileTime, fileTime);
} catch (IOException e) {
    System.err.println(e);
}
```

更新文件的上次修改时间也可以通过Files.setLastModifiedTime（）方法：

```Java
long time = System.currentTimeMillis();
FileTime fileTime = FileTime.fromMillis(time);
try {
    Files.setLastModifiedTime(path, fileTime);
} catch (IOException e) {
    System.err.println(e);
}
```

更新文件的上次修改时间也可以使用setAttribute（）方法来完成。

实际上，此方法可用于更新文件的上次修改时间，上次访问时间或创建时间属性，就像通过调用setTimes（）方法：

```Java
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
…
try {
    Files.setAttribute(path, "basic:lastModifiedTime", fileTime, NOFOLLOW_LINKS);
    Files.setAttribute(path, "basic:creationTime", fileTime, NOFOLLOW_LINKS);
    Files.setAttribute(path, "basic:lastAccessTime", fileTime, NOFOLLOW_LINKS);
} catch (IOException e) {
    System.err.println(e);
}
```

显然，现在您必须提取三个属性的值才能看到更改。 你可以这样做使用getAttribute（）方法：

```Java
try {
    FileTime lastModifiedTime = (FileTime)Files.getAttribute(path,"basic:lastModifiedTime", NOFOLLOW_LINKS);
    FileTime creationTime = (FileTime)Files.getAttribute(path,"basic:creationTime", NOFOLLOW_LINKS);
    FileTime lastAccessTime = (FileTime)Files.getAttribute(path,"basic:lastAccessTime", NOFOLLOW_LINKS);

    System.out.println("New last modified time: " + lastModifiedTime);
    System.out.println("New creation time: " + creationTime);
    System.out.println("New last access time: " + lastAccessTime);

} catch (IOException e) {
    System.err.println(e);
}
```

----