# 4.部署程序

### 介绍

部署是用于将Web应用程序（第三方WAR或您自己的自定义Web应用程序）安装到Tomcat服务器的过程的术语。

Web应用程序部署可以在Tomcat服务器中以多种方式完成。

- 静态地 在启动Tomcat之前先设置Web应用程序
- 动态地 通过直接操作已经部署的Web应用程序（依靠*自动部署* 功能）或通过使用Tomcat Manager Web应用程序远程

在[Tomcat管理器](http://tomcat.apache.org/tomcat-9.0-doc/manager-howto.html)是一个可以交互使用（通过HTML GUI）或以编程方式（通过基于URL的API）来部署和管理Web应用程序的Web应用程序。

有许多依赖Manager Web应用程序执行部署的方法。Apache Tomcat提供了Apache Ant构建工具的任务。 [Apache Tomcat Maven插件](https://tomcat.apache.org/maven-plugin.html) 项目提供了与Apache Maven的集成。还有一个名为Client Deployer的工具，可以从命令行使用它，并提供其他功能，例如编译和验证Web应用程序以及将Web应用程序打包到Web应用程序资源（WAR）文件中。

### 安装

静态部署Web应用程序不需要安装，因为Tomcat是开箱即用的。尽管需要某些配置（如[Tomcat Manager手册](http://tomcat.apache.org/tomcat-9.0-doc/manager-howto.html)中所述），但使用Tomcat Manager进行部署功能也不需要进行任何安装 。但是，如果您想使用Tomcat Client Deployer（TCD），则需要进行安装。

TCD没有与Tomcat核心发行版打包在一起，因此必须从“下载”区域单独下载。该下载通常标有 *apache-tomcat-9.0.x-deployer*。

TCD具有Apache Ant 1.6.2+和Java安装的先决条件。您的环境应定义一个指向Ant安装根目录的ANT_HOME环境值，以及一个指向Java安装的JAVA_HOME值。另外，您应该确保Ant的ant命令，并且Java javac编译器命令是从操作系统提供的命令外壳运行的。

1. 下载TCD发行版
2. TCD软件包无需解压缩到任何现有的Tomcat安装中，可以解压缩到任何位置。
3. 阅读使用[ Tomcat客户端部署程序](http://tomcat.apache.org/tomcat-9.0-doc/deployer-howto.html#Deploying_using_the_Client_Deployer_Package)

### 语境

在谈论Web应用程序的部署时，需要理解*上下文*的概念 。上下文是Tomcat所谓的Web应用程序。

为了在Tomcat中配置上下文， 需要*上下文描述符*。上下文描述符只是一个XML文件，其中包含与Tomcat相关的Context配置，例如命名资源或会话管理器配置。在早期版本的Tomcat中，上下文描述符配置的内容通常存储在Tomcat的主要配置文件*server.xml中，*但是现在不建议这样做（尽管目前仍然可以使用）。

上下文描述符不仅帮助Tomcat知道如何配置上下文，而且其他工具（例如Tomcat Manager和TCD）经常使用这些上下文描述符来正确执行其角色。

上下文描述符的位置为：

1. $ CATALINA_BASE / conf / [引擎名称] / [主机名] / [webappname] .xml
2. $ CATALINA_BASE / webapps / [webappname] /META-INF/context.xml

（1）中的文件名为[webappname] .xml，但（2）中的文件命名为context.xml。如果未为上下文提供上下文描述符，则Tomcat使用默认值配置上下文。

### 在Tomcat启动时进行部署

如果您对使用Tomcat Manager或TCD不感兴趣，则需要将Web应用程序静态部署到Tomcat，然后再启动Tomcat。您为此类部署将Web应用程序部署到的位置称为“ `appBase`每个主机指定的位置”。您可以将所谓的*爆炸式Web应用程序*（即未压缩的*Web应用程序）*复制到此位置，也可以将压缩的Web应用程序资源.WAR文件复制到该位置。

`appBase`仅当主机`deployOnStartup`属性为“ true”时，才会在Tomcat启动时部署 由主机（默认主机为“ localhost”）属性（默认appBase为“ $ CATALINA_BASE / webapps”）指定的位置中存在的Web应用程序。

在这种情况下，将在Tomcat启动时执行以下部署顺序：

1. 任何上下文描述符都将首先部署。
2. 然后将部署未由任何上下文描述符引用的爆炸性Web应用程序。如果他们在appBase中具有关联的.WAR文件，并且该文件比分解的Web应用程序新，则分解的目录将被删除，并且将从.WAR重新部署Webapp。
3. .WAR文件将被部署

### 在正在运行的Tomcat服务器上进行部署

可以将Web应用程序部署到正在运行的Tomcat服务器。

如果Host `autoDeploy`属性为“ true”，则Host将尝试根据需要动态地部署和更新Web应用程序，例如，如果将新的.WAR放入`appBase`。为此，主机需要启用后台处理，这是默认配置。

`autoDeploy` 设置为“ true”，并且正在运行的Tomcat允许：

- 部署.WAR文件复制到主机中`appBase`。
- 部署爆炸性Web应用程序，并将其复制到Host中`appBase`。
- 当提供新的.WAR时，重新部署已经从.WAR部署的Web应用程序。在这种情况下，将删除爆炸的Web应用程序，然后再次扩展.WAR。请注意，如果将主机配置为不会将`unpackWARs` 属性设置为“ false”的.WAR爆炸，则不会发生爆炸，在这种情况下，将简单地将Web应用程序重新部署为压缩档案。
- 如果/WEB-INF/web.xml文件（或定义为WatchedResource的任何其他资源）已更新，则重新加载Web应用程序。
- 如果更新了从中部署Web应用程序的上下文描述符文件，则重新部署Web应用程序。
- 如果更新了Web应用程序使用的全局或每个主机的上下文描述符文件，则重新部署相关Web应用程序。
- 如果将上下文描述符文件（文件名与先前部署的Web应用程序的Context路径相对应）添加到该`$CATALINA_BASE/conf/[enginename]/[hostname]/` 目录中，则重新部署该Web应用程序 。
- 如果删除了Web应用程序的文档库（docBase），则将其取消部署。请注意，在Windows上，这假定已启用了防锁定功能（请参阅上下文配置），否则无法删除正在运行的Web应用程序的资源。

需要注意的是Web应用程序重新加载，也可在装载机，在这种情况下加载的类将被跟踪更改配置。

### 使用Tomcat管理器进行部署

Tomcat管理器包含在其[自己的手册页中](http://tomcat.apache.org/tomcat-9.0-doc/manager-howto.html)。

### 使用客户端部署程序包进行部署

最后，可以使用Tomcat Client Deployer来实现Web应用程序的部署。这是一个软件包，可用于验证，编译，压缩为.WAR并将Web应用程序部署到生产或开发Tomcat服务器。应当注意，此功能使用Tomcat管理器，因此目标Tomcat服务器应正在运行。

假定用户将熟悉使用Apache TCD的Apache Ant。Apache Ant是一个脚本化的构建工具。TCD预先打包了构建脚本以供使用。仅需要对Apache Ant有一定的了解（如本页面前面列出的安装，并且熟悉使用操作系统命令外壳和配置环境变量）。

TCD包括Ant任务，在部署之前用于JSP编译的Jasper页面编译器，以及用于验证Web应用程序上下文描述符的任务。验证程序任务（类 `org.apache.catalina.ant.ValidatorTask`）仅允许一个参数：展开的Web应用程序的基本路径。

TCD使用分解的Web应用程序作为输入（请参阅下面使用的属性列表）。通过部署程序以编程方式部署的Web应用程序可能在中包含Context Descriptor `/META-INF/context.xml`。

TCD包含一个现成的Ant脚本，其目标如下：

- `compile`（默认）：编译和验证Web应用程序。可以独立使用，不需要运行的Tomcat服务器。编译的应用程序将仅在关联的 *Tomcat XYZ*服务器版本上运行，并且不能保证可以在另一个Tomcat版本上运行，因为Jasper生成的代码取决于其运行时组件。还应注意，此目标还将自动编译位于`/WEB-INF/classes`Web应用程序文件夹中的任何Java源文件 。
- `deploy`：将Web应用程序（已编译或未编译）部署到Tomcat服务器。
- `undeploy`：取消部署Web应用程序
- `start`：启动Web应用程序
- `reload`：重新加载Web应用程序
- `stop`：停止Web应用程序

为了配置部署，请`deployer.properties`在TCD安装目录根目录中创建一个名为的文件。在此文件中，每行添加以下名称=值对：

此外，您将需要确保已为目标Tomcat Manager（TCD使用的目标用户）设置了用户，否则TCD将不会通过Tomcat Manager进行身份验证，并且部署将失败。为此，请参见“ Tomcat管理器”页面。

- `build`：默认情况下，使用的构建文件夹为 `${build}/webapp/${path}`（`${build}`，默认情况下，指向`${basedir}/build`）。`compile`目标执行结束后，Web应用程序.WAR将位于 `${build}/webapp/${path}.war`。
- `webapp`：包含展开的Web应用程序的目录，将对其进行编译和验证。默认情况下，该文件夹为 `myapp`。
- `path`：默认情况下，Web应用程序的已部署上下文路径`/myapp`。
- `url`：指向正在运行的Tomcat服务器的Tomcat Manager Web应用程序的绝对URL，它将用于部署和取消部署Web应用程序。默认情况下，部署者将尝试访问在localhost上运行的Tomcat实例 `http://localhost:8080/manager/text`。
- `username`：Tomcat Manager用户名（用户应具有manager-script角色）
- `password`：Tomcat管理器密码。