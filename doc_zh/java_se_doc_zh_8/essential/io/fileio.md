# 文件I / O（以NIO.2为特色）

------

**注意：**本教程反映了JDK 7发行版中引入的文件I / O机制。Java SE 6版本的File I / O教程很简短，但您可以下载包含早期File I / O内容的教程的 [Java SE Tutorial 2008-03-14](http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-tutorials-419421.html#tutorial-2008_03_14-oth-JPR)版本。

------

该`java.nio.file`包及其相关的包装，`java.nio.file.attribute`提供文件I / O和用于访问默认文件系统的全面支持。虽然API有很多类，但您只需要关注几个入口点。您将看到此API非常直观且易于使用。

本教程首先询问 [路径是什么？](path.html)然后， 引入[Path类](pathClass.html)，即包的主要入口点。解释了`Path`与[句法操作](pathOps.html)有关的类中的 方法。然后，本教程将继续介绍包中的另一个主类，`Files`该类包含处理文件操作的方法。首先，介绍了许多[文件操作](fileOps.html)常见的一些概念 。然后，本教程介绍了 [检查](check.html)， [删除](delete.html)， [复制](copy.html)和 [移动](move.html)文件的方法。

在继续进行[文件I / O](file.html)和 [目录I / O](dirs.html)之前，本教程将介绍如何 管理 [元数据](fileAttr.html)。 解释了[随机访问文件](rafs.html)，并检查了特定于[符号和硬链接的](links.html)问题 。

接下来，介绍一些非常强大但更高级的主题。首先，演示了 [递归遍历文件树的](walk.html)功能，然后是有关如何[使用通配符搜索文件](find.html)的信息 。接下来，解释和演示如何 [查看目录以进行更改](notification.html)。然后， 一些[不适合其他地方的](misc.html)方法得到了一些关注。

最后，如果您在Java SE 7发行版之前编写了文件I / O代码，则会有[从旧API到新API](legacy.html#mapping)的 [映射](legacy.html#mapping)，以及有关`File.toPath`希望[利用新API的](legacy.html#interop)开发人员的方法的 重要信息[无需重写现有代码](legacy.html#interop)。