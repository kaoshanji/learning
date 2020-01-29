# DOS视图

特定于DOS文件系统（或Samba），DosFileAttributeView视图用以下方法扩展了基本视图：DOS属性（这意味着您可以直接从DOS视图访问基本视图）。 

-   有四个属性，它们通过以下方法进行映射：
    -   isReadOnly()：返回只读属性的值（如果为true，则该文件不能为删除或更新）
    -   isHidden():返回隐藏属性的值（如果为true，则文件对用户）
    -   isArchive():返回存档属性的值（特定于备份程序）
    -   isSystem():返回系统属性的值（如果为true，则文件属于操作系统）

下面的清单批量提取了给定路径的前面四个属性：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
...
DosFileAttributes attr = null;
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");

try {
    attr = Files.readAttributes(path, DosFileAttributes.class);
} catch (IOException e) {
    System.err.println(e);
}

System.out.println("Is read only ? " + attr.isReadOnly());
System.out.println("Is Hidden ? " + attr.isHidden());
System.out.println("Is archive ? " + attr.isArchive());
System.out.println("Is system ? " + attr.isSystem());

```

设置属性的值并按名称提取单个属性可以通过 setAttribute（）和getAttribute（）方法分别如下（我随机选择隐藏的属性）：

```Java
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
…
//setting the hidden attribute to true
try {
    Files.setAttribute(path, "dos:hidden", true, NOFOLLOW_LINKS);
} catch (IOException e) {
    System.err.println(e);
}

//getting the hidden attribute
try {
    boolean hidden = (Boolean) Files.getAttribute(path, "dos:hidden", NOFOLLOW_LINKS);
    System.out.println("Is hidden ? " + hidden);
} catch (IOException e) {
    System.err.println(e);
}
```

-   DOS属性可以使用以下名称获取：
    -   hidden
    -   readonly
    -   system
    -   archive

通常接受的形式是[view-name：]属性名称。 视图名称是dos。

----