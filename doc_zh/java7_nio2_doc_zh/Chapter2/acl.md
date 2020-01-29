# ACL视图

访问控制列表（ACL）是权限的集合，旨在强制执行有关访问的严格规则到文件系统的对象。 

在ACL中，控件分别控制所有者，权限和每种标志的不同种类宾语。

NIO.2通过以下方式表示的ACL视图提供对ACL的控制：AclFileAttributeView接口，一个文件属性视图，支持读取或更新文件的ACL或文件所有者属性。

##  使用Files.getFileAttributeView（）读取ACL

如果您从未见过ACL的内容，请尝试以下代码，该代码使用 Files.getFileAttributeView（）以将ACL提取为 `List <AclEntry>`：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.util.List;
…
List<AclEntry> acllist = null;
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");

AclFileAttributeView aclview = Files.getFileAttributeView(path, AclFileAttributeView.class);
try {
    acllist = aclview.getAcl();
} catch (IOException e) {
    System.err.println(e);
}
```

##  使用Files.getAttribute（）读取ACL

您还可以使用getAttribute（）方法读取ACL：

```Java
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
…
List<AclEntry> acllist = null;
Path path = Paths.get("C:/rafaelnadal/tournaments/2009", "BNP.txt");

try {
    acllist = (List<AclEntry>) Files.getAttribute(path, "acl:acl", NOFOLLOW_LINKS);
} catch (IOException e) {
    System.err.println(e);
}
```

可能需要使用以下名称的ACL属性：acl、owner

通常接受的形式是[view-name：]属性名称。 视图名称是acl。

##  读取ACL条目

前两个示例向您展示了如何提取指定路径的ACL。 

结果是一个清单AclEntry的名称-映射来自ACL的条目的类。

-   每个条目都有四个组成部分：
    -   Type:确定条目是授予还是拒绝访问。 它可以是ALARM，ALLOW，AUDIT，或DENY。
    -   Principal:条目授予或拒绝访问的身份。 这已映射作为UserPrincipal。
    -   Permissions:一组权限。 映射为`Set <AclEntryPermission>`。
    -   Flags:一组标志，指示条目如何继承和传播。 映射作为`Set <AclEntryFlag>`。

您可以遍历列表并按以下步骤提取每个条目的组件，这是提取的列表在前面的部分中：

```Java
for (AclEntry aclentry : acllist) {
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Principal: " + aclentry.principal().getName());
    System.out.println("Type: " + aclentry.type().toString());
    System.out.println("Permissions: " + aclentry.permissions().toString());
    System.out.println("Flags: " + aclentry.flags().toString());
}
```

以下是此代码的示例输出（在Windows 7上测试）：

```base
++++++++++++++++++++++++++++++++++++++++++++++++++++

Principal: BUILTIN\Administrators

Type: ALLOW

Permissions: [WRITE_OWNER, READ_ACL, EXECUTE, WRITE_NAMED_ATTRS, READ_ATTRIBUTES,
READ_NAMED_ATTRS, WRITE_DATA, WRITE_ACL, READ_DATA, WRITE_ATTRIBUTES, SYNCHRONIZE, DELETE,
DELETE_CHILD, APPEND_DATA]

Flags: []

++++++++++++++++++++++++++++++++++++++++++++++++++++

Principal: NT AUTHORITY\SYSTEM

Type: ALLOW

Permissions: [WRITE_OWNER, READ_ACL, EXECUTE, WRITE_NAMED_ATTRS, READ_ATTRIBUTES,
READ_NAMED_ATTRS, WRITE_DATA, WRITE_ACL, READ_DATA, WRITE_ATTRIBUTES, SYNCHRONIZE, DELETE,
DELETE_CHILD, APPEND_DATA]

Flags: []

++++++++++++++++++++++++++++++++++++++++++++++++++++

Principal: NT AUTHORITY\Authenticated Users

Type: ALLOW

Permissions: [READ_ACL, EXECUTE, READ_DATA, WRITE_ATTRIBUTES, WRITE_NAMED_ATTRS,
SYNCHRONIZE, DELETE, READ_ATTRIBUTES, READ_NAMED_ATTRS, WRITE_DATA, APPEND_DATA]
Flags: []

++++++++++++++++++++++++++++++++++++++++++++++++++++

Principal: BUILTIN\Users

Type: ALLOW

Permissions: [READ_ACL, EXECUTE, READ_DATA, SYNCHRONIZE, READ_ATTRIBUTES, READ_NAMED_ATTRS]

Flags: []
```

##  在ACL中授予新访问权限

通过调用关联的AclEntry.Builder对象的build（）方法来创建ACL条目。 

对于例如，如果要授予对主体的新访问权限，则必须遵循以下过程：

-   通过调用查找主体FileSystem.getUserPrincipalLookupService（）方法。
-   获取ACL视图（如前所述）。
-   使用AclEntry.Builder对象创建一个新条目。
-   阅读ACL（如前所述）。
-   插入新条目（建议在任何DENY条目之前）。
-   使用setAcl（）或setAttribute（）重写ACL。

按照这些步骤，您可以编写一个代码片段以授予读取数据访问权限并追加数据访问名为apress的委托人：

```Java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
…
try {
    //Lookup for the principal
    UserPrincipal user = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName("apress");
    //Get the ACL view
    AclFileAttributeView view = Files.getFileAttributeView(path,AclFileAttributeView.class);
    //Create a new entry
    AclEntry entry = AclEntry.newBuilder().setType(AclEntryType.ALLOW).setPrincipal(user).setPermissions(AclEntryPermission.READ_DATA,AclEntryPermission.APPEND_DATA.build();
    //read ACL
    List<AclEntry> acl = view.getAcl();
    //Insert the new entry
    acl.add(0, entry);
    //rewrite ACL
    view.setAcl(acl);
    //or, like this
    //Files.setAttribute(path, "acl:acl", acl, NOFOLLOW_LINKS);
    } catch (IOException e) {
    System.err.println(e);
}

```

上例中使用的主体“ apress”将在您的计算机上不可用。

测试代码而不获取java.nio.file.attribute.UserPrincipalNotFoundException，添加您的主体名称（您计算机的管理员用户或具有适当操作系统特权的用户）。

前面的代码在现有文件的ACL中添加了一个新条目。 

在通常情况下，创建新文件时可能会这样做。


