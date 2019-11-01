# 3.2、安装

为了使用Tomcat开发Web应用程序，必须首先安装它（及其依赖的软件）。以下小节概述了所需的步骤。

#### JDK

Tomcat 9.0设计为在Java SE 8或更高版本上运行。

可在[http://www.oracle.com/technetwork/java/javase/downloads/index.html上](http://www.oracle.com/technetwork/java/javase/downloads/index.html)获得许多平台的兼容JDK（或指向它们的链接） 。

#### Tomcat

可以从https://tomcat.apache.org/获得**Tomcat**服务器的 二进制下载。本手册假定您使用的是Tomcat 9的最新版本。有关下载和安装Tomcat的详细说明，请参见[此处](http://tomcat.apache.org/tomcat-9.0-doc/setup.html)。

在本手册的其余部分中，示例shell脚本假定您已设置环境变量`CATALINA_HOME`，该环境变量包含指向已安装Tomcat的目录的路径名。（可选）如果已为多个实例配置了Tomcat，则每个实例将具有自己的`CATALINA_BASE`配置。

#### Ant

可从https://ant.apache.org/获得**Ant**构建工具的 二进制下载。本手册假定您使用的是Ant 1.8或更高版本。这些说明也可能与其他版本兼容，但是尚未经过测试。

下载并安装Ant。然后，按照操作系统平台的标准做法`bin`，将Ant分发目录添加到您的 `PATH`环境变量中。完成此操作后，您将可以`ant`直接执行shell命令。