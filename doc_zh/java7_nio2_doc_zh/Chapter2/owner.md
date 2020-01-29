# 文件权限视图

大多数文件系统接受文件所有者的概念作为用于确定对文件的访问权限的身份文件系统中的对象。 

NIO.2在名为UserPrincipal的接口中映射了此概念，并允许您通过文件所有者视图获取或设置文件的所有者，该视图由
FileOwnerAttributeView接口。 

实际上，正如您将在以下代码示例中看到的那样，NIO.2具有设置和获取文件所有者的多种方法。


##  使用 Files.setOwner（）设置文件所有者

您可以通过调用Files.setOwner（）方法来设置文件所有者。 

除了文件路径，此方法还获得了映射代表文件所有者的字符串的UserPrincipal实例。 

用户主体查找服务可以通过调用FileSystem.getUserPrincipalLookupService（）获得默认文件系统的信息。

这是设置文件所有者的一个简单示例：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
...
UserPrincipal owner = null;
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
try {
    owner = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName("apress");
    Files.setOwner(path, owner);
} catch (IOException e) {
    System.err.println(e);
}
```

##  使用 FileOwnerAttributeView.setOwner（） 设置文件所有者

FileOwnerAttributeView映射一个文件属性视图，该视图支持读取或更新文件所有者的文件。 

owner属性由名称所有者标识，并且该属性的值是UserPrincipal 宾语。 

以下代码段显示了如何使用此界面设置所有者：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
...
UserPrincipal owner = null;
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
FileOwnerAttributeView foav = Files.getFileAttributeView(path,FileOwnerAttributeView.class);

try {
    owner = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName("apress");
    foav.setOwner(owner);
} catch (IOException e) {
    System.err.println(e);
}
```

##  使用Files.setAttribute（）设置文件所有者

与大多数视图一样，文件所有者视图可以访问setAttribute（）方法。 

的全名该属性为owner：owner，如您在此处看到的：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
…
UserPrincipal owner = null;
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");

try {
    owner = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName("apress");
    Files.setAttribute(path, "owner:owner", owner, NOFOLLOW_LINKS);
} catch (IOException e) {
    System.err.println(e);
}
```

##  使用 FileOwnerAttributeView.getOwner（）获取文件所有者

在确定对文件系统中对象的访问权限时，读取文件所有者是一项常见的任务。

getOwner（）方法以UserPrincipal方法的形式返回文件的所有者-String表示可以通过调用UserPrincipal.getName（）方法获得文件所有者：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
…
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
FileOwnerAttributeView foav = Files.getFileAttributeView(path,FileOwnerAttributeView.class);

try {
    String owner = foav.getOwner().getName();
    System.out.println(owner);
} catch (IOException e) {
    System.err.println(e);
}
```

##  使用 Files.getAttribute（）获取文件所有者

本节的最后一个示例涉及Files.getAttribute（）方法。

我相信这种方法是以上部分对您非常熟悉，因此这里是代码片段：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
…
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");
try {
    UserPrincipal owner = (UserPrincipal) Files.getAttribute(path,"owner:owner", NOFOLLOW_LINKS);
    System.out.println(owner.getName());
} catch (IOException e) {
    System.err.println(e);
}
```

如果无法获得默认文件系统的用户主体查找服务或该服务无效指定用户名，则将引发java.nio.file.attribute.UserPrincipalNotFoundException。

可能需要使用以下名称的文件所有者属性：owner

通常接受的形式是[view-name：]属性名称。 视图名称是所有者。

----
