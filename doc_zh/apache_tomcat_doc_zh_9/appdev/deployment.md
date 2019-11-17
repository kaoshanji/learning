# 3.3、部署结构

### 背景

在描述如何组织源代码目录之前，检查Web应用程序的运行时组织非常有用。在Servlet API规范2.2版之前，服务器平台之间几乎没有一致性。但是，要求符合2.2（或更高版本）规范的服务器接受标准格式的 *Web应用程序存档*，下面将对其进行进一步讨论。

Web应用程序被定义为标准布局中目录和文件的层次结构。可以按其“未打包”形式（其中每个目录和文件分别存在于文件系统中）或“打包”形式（称为Web ARchive或WAR文件）访问这种层次结构。前一种格式在开发期间更有用，而后一种格式在分发要安装的应用程序时使用。

Web应用程序层次结构的顶级目录也是应用程序的 *文档根*。在这里，您将放置构成应用程序用户界面的HTML文件和JSP页面。当系统管理员将您的应用程序部署到特定的服务器上时，他或她会为您的应用程序分配*上下文路径*（本手册的后面部分介绍了在Tomcat上的部署）。因此，如果系统管理员将您的应用程序分配给上下文路径 `/catalog`，则引用的请求URI 将从您的文档根目录`/catalog/index.html`检索`index.html`文件。

### 标准目录布局

为了便于以所需格式创建Web应用程序存档文件，可以将Web应用程序的“可执行”文件（即Tomcat在执行应用程序时实际使用的文件）安排在与WAR格式本身。为此，您将在应用程序的“文档根目录”目录中获得以下内容：

- *** .html，\*。** jsp **等** -HTML和JSP页面以及应用程序的客户端浏览器必须可见的其他文件（例如JavaScript，样式表文件和图像）。在较大的应用程序中，您可以选择将这些文件划分为子目录层次结构，但是对于较小的应用程序，通常只为这些文件维护一个目录要简单得多。

  

- **/WEB-INF/web.xml-**您的应用程序的*Web应用程序部署描述符*。这是一个XML文件，描述了组成应用程序的servlet和其他组件，以及您希望服务器为您强制实施的所有初始化参数和容器管理的安全性约束。以下小节将详细讨论该文件。

  

- **/ WEB-INF / classes /** -此目录包含应用程序所需的任何Java类文件（和相关资源），包括未合并到JAR文件中的servlet和非servlet类。如果您的类被组织为Java包，则必须在目录层次结构下反映这一点 `/WEB-INF/classes/`。例如，名为的Java类 `com.mycompany.mypackage.MyServlet` 需要存储在名为的文件中 `/WEB-INF/classes/com/mycompany/mypackage/MyServlet.class`。

  

- **/ WEB-INF / lib /** -此目录包含JAR文件，这些文件包含您的应用程序所需的Java类文件（和相关资源），例如第三方类库或JDBC驱动程序。

当您将应用程序安装到Tomcat（或任何其他2.2或更高版本的Servlet容器）中时，该`WEB-INF/classes/` 目录中的类以及该目录中找到的JAR文件中的所有类对于 `WEB-INF/lib/`特定Web应用程序中的其他类都是可见的。因此，如果在这些位置之一中包含所有必需的库类（请确保检查许可证以获取对您使用的任何第三方库的再发行权），则将简化Web应用程序的安装-无需对系统进行任何调整类路径（或在服务器中安装全局库文件）将是必需的。

这些信息大部分是从Servlet API规范2.3版的第9章中提取的，有关更多详细信息，请查阅。

### 共享库文件

像大多数Servlet容器一样，Tomcat还支持一种机制，可以一次安装库JAR文件（或解压缩的类），并使它们对所有已安装的Web应用程序可见（不必包含在Web应用程序本身中）。“ [类装入器方法”](http://tomcat.apache.org/tomcat-9.0-doc/class-loader-howto.html)文档中介绍了Tomcat如何查找和共享此类的详细信息 。在Tomcat安装中，共享代码通常使用的位置是 **$ CATALINA_HOME / lib**。放置在此处的JAR文件对于Web应用程序和内部Tomcat代码均可见。这是放置应用程序或内部Tomcat使用（例如JDBCRealm）所需的JDBC驱动程序的好地方。

开箱即用的标准Tomcat安装包括各种预安装的共享库文件，包括：

- 该*Servlet的4.0*和*JSP 2.3*的API是编写的Servlet和JavaServer Pages的基础。

  

### Web应用程序部署描述符

如上所述，该`/WEB-INF/web.xml`文件包含您的应用程序的Web应用程序部署描述符。就像文件名扩展名所暗示的那样，此文件是XML文档，它定义了服务器需要了解的有关应用程序的所有信息（*上下文路径*除外，该*上下文路径*是在部署应用程序时由系统管理员分配的）。

部署描述符的完整语法和语义在Servlet API规范2.3版的第13章中定义。随着时间的流逝，预计将提供开发工具来为您创建和编辑部署描述符。同时，为了提供起点，提供了一个[基本的web.xml文件](http://tomcat.apache.org/tomcat-9.0-doc/appdev/web.xml.txt) 。该文件包含描述每个包含元素目的的注释。

**注** – Servlet规范包括用于Web应用程序部署描述符的文档类型描述符（DTD），并且Tomcat在处理应用程序的`/WEB-INF/web.xml`文件时会强制执行此处定义的规则 。特别是，你**必须** 输入你的描述符元素（如`<filter>`， `<servlet>`和`<servlet-mapping>`由DTD（见第13.3节）中定义的顺序。

### Tomcat上下文描述符

/META-INF/context.xml文件可用于定义特定于Tomcat的配置选项，例如访问日志，数据源，会话管理器配置等。该XML文件必须包含一个Context元素，这将被视为它是与Web应用程序所部署到的Host对应的Host元素的子元素。的 [Tomcat配置文件](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)包含上下文元素上的信息。

### 使用Tomcat部署

*下面的描述使用变量名$ CATALINA_BASE来引用可解决大多数相对路径的基本目录。如果尚未通过设置CATALINA_BASE目录为多个实例配置Tomcat，则$ CATALINA_BASE将设置为$ CATALINA_HOME的值，该目录已将Tomcat安装到该目录中。*

为了执行，必须将Web应用程序部署在Servlet容器上。即使在开发过程中也是如此。我们将描述使用Tomcat提供执行环境。可以通过以下方法之一在Tomcat中部署Web应用程序：

- *将解压后的目录层次结构复制到directory的子目录中 $CATALINA_BASE/webapps/*。Tomcat将根据您选择的子目录名称为您的应用程序分配上下文路径。我们将在`build.xml` 构建的文件中使用此技术，因为它是开发过程中最快，最简单的方法。在安装或更新应用程序后，请确保重新启动Tomcat。

  

- *将Web应用程序归档文件复制到directory中 $CATALINA_BASE/webapps/*。Tomcat启动后，它将自动将Web应用程序存档文件扩展为解压缩的形式，并以此方式执行该应用程序。这种方法通常用于将第三方供应商或内部开发人员提供的其他应用程序安装到现有的Tomcat安装中。 **注** –如果使用此方法，并希望以后更新应用程序，则必须同时替换Web应用程序归档文件**并**删除Tomcat创建的扩展目录，然后重新启动Tomcat，以反映您的更改。

  

- *使用Tomcat“管理器” Web应用程序来部署和取消部署Web应用程序*。Tomcat包括一个默认情况下部署在上下文路径上的Web应用程序，该应用程序`/manager`允许您在运行的Tomcat服务器上部署和取消部署应用程序，而无需重新启动它。有关 使用Manager Web应用程序的更多信息，请参见[Manager应用](http://tomcat.apache.org/tomcat-9.0-doc/manager-howto.html)程序方法。

  

- *在构建脚本中使用“经理” Ant任务*。Tomcat包括针对`Ant` 构建工具的一组自定义任务定义，这些定义使您可以自动执行对“ Manager” Web应用程序的命令。这些任务在Tomcat部署程序中使用。

  

- *使用Tomcat Deployer*。Tomcat包括捆绑了Ant任务的打包工具，可用于在部署到服务器之前自动预编译属于Web应用程序的JSP。

  

将应用程序部署在其他servlet容器上将特定于每个容器，但是与Servlet API规范（版本2.2或更高版本）兼容的所有容器都必须接受Web应用程序存档文件。请注意，其他容器**不**须接受非压缩的目录结构（如Tomcat的那样），或者为共享库文件的机制，但是这些功能通常是可用的。