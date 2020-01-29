# POSIX视图

对Unix爱好者来说是个好消息！ 

POSIX通过Unix及其支持的属性扩展了基本视图风格-文件所有者，组所有者和九个相关的访问权限（读，写，同一成员）组等）。

基于PosixFileAttributes类，可以按以下方式提取POSIX属性：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
…
PosixFileAttributes attr = null;
Path path = Paths.get("/home/rafaelnadal/tournaments/2009/BNP.txt");

try {
    attr = Files.readAttributes(path, PosixFileAttributes.class);
} catch (IOException e) {
    System.err.println(e);
}

System.out.println("File owner: " + attr.owner().getName());
System.out.println("File group: " + attr.group().getName());
System.out.println("File permissions: " + attr.permissions().toString());
```

或者，您可以通过调用Files.getFileAttributeView（）方法来使用“漫长的路途”：

```Java
import java.nio.file.attribute.PosixFileAttributeView;
…
try {
    attr = Files.getFileAttributeView(path,
    PosixFileAttributeView.class).readAttributes();
} catch (IOException e) {
    System.err.println(e);
}
```

可能需要使用以下名称的POSIX属性：group、permissions

通常接受的形式是[view-name：]属性名称。 视图名称是posix。

##  POSIX 权限

Permissions（）方法返回PosixFilePermissions对象的集合。 

PosixFilePermissions是权限帮助程序类。 

此类最有用的方法之一是asFileAttribute（），接受一组文件许可权，并构造一个文件属性，该属性可以传递给Path.createFile（）方法或Path.createDirectory（）方法。 

例如，您可以提取具有文件的POSIX权限，并创建具有以下相同属性的另一个文件（本示例使用前面示例中的attr对象）：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
…
Path new_path = Paths.get("/home/rafaelnadal/tournaments/2009/new_BNP.txt");
FileAttribute<Set<PosixFilePermission>> posixattrs = PosixFilePermissions.asFileAttribute(attr.permissions());
try {
    Files.createFile(new_path, posixattrs);
} catch (IOException e) {
    System.err.println(e);
}
```

此外，可以通过调用fromString（）方法将文件的权限设置为硬编码字符串：

```Java
Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rw-r--r--");
try {
    Files.setPosixFilePermissions(new_path, permissions);
} catch (IOException e) {
    System.err.println(e);
}
```

##  POSIX 组权限

可以使用名为group的POSIX属性设置文件组所有者。 

setGroup（）方法获取文件路径和一个GroupPrincipal实例，该实例映射表示组所有者的字符串（此类）扩展了UserPrincipal接口：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
…
Path path = Paths.get("/home/rafaelnadal/tournaments/2009/BNP.txt");

try {
    GroupPrincipal group = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByGroupName("apressteam");
    Files.getFileAttributeView(path, PosixFileAttributeView.class).setGroup(group);
} catch (IOException e) {
    System.err.println(e);
}
```

在前面的示例中使用了一个名为“ apressteam”的组主体，但是该组不会在您的机器上可用。 

要测试前面的代码而没有java.nio.file.attribute.UserPrincipalNotFoundException，您需要添加组主体名称（您计算机的管理员组或具有适当操作系统权限的组）。

您可以通过调用Files.getAttribute（）方法轻松找到该组：

```Java
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
…
try {
    GroupPrincipal group = (GroupPrincipal) Files.getAttribute(path, "posix:group",NOFOLLOW_LINKS);
    System.out.println(group.getName());
} catch (IOException e) {
    System.err.println(e);
}
```

您可以通过调用FileOwnerAttributeView.getOwner（）和 FileOwnerAttributeView.setOwner（），在POSIX视图中继承。

----