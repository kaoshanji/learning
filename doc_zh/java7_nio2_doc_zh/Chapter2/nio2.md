# NIO.2里的视图

NIO.2带有一组六个视图，其概述如下：

-   BasicFileAttributeView

这是所有文件系统的实现必须支持的基本属性的视图。 属性视图名称是基本的。

-   DosFileAttributeView

该视图提供了标准的四个受支持的属性

在支持DOS属性的文件系统上。 属性视图的名称为dos。

-   PosixFileAttributeView

该视图使用属性扩展了基本属性视图

在支持POSIX（便携式操作系统）的文件系统上受支持Unix接口）系列标准，例如Unix。 属性视图名称为posix。

-   FileOwnerAttributeView

任何文件系统都支持此视图，支持文件所有者概念的实现。 属性视图名称是所有者。

-   AclFileAttributeView

此视图支持读取或更新文件的ACL。 

支持NFSv4 ACL模型。 属性视图名称为acl。

-   UserDefinedFileAttributeView

此视图启用对用户元数据的支持
定义。

----