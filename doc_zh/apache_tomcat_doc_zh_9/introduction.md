# 1.简介

### 介绍

对于管理员和Web开发人员一样，在开始之前，您应该熟悉一些重要的信息。本文档简要介绍了Tomcat容器背后的一些概念和术语。同样，需要帮助时该去哪里。

### 术语

在阅读这些文档的过程中，您将遇到许多术语。一些特定于Tomcat，另一些特定于 [Servlet和JSP规范](https://wiki.apache.org/tomcat/Specifications)。

- **上下文** -简而言之，上下文是一个Web应用程序。

这就对了。如果您发现需要在本部分中添加的其他术语，请告知我们。

### 目录和文件

这些是一些关键的tomcat目录：

- **/ bin-**启动，关闭和其他脚本。这些 `*.sh`文件（对于Unix系统）是这些`*.bat`文件的功能副本（对于Windows系统）。由于Win32命令行缺少某些功能，因此此处包含一些其他文件。
- **/ conf-**配置文件和相关的DTD。这里最重要的文件是server.xml。它是容器的主要配置文件。
- **/ logs-**日志文件默认位于此处。
- **/ webapps-**这是您的webapp所在的位置。

### CATALINA_HOME和CATALINA_BASE

在整个文档中，都引用了以下两个属性：

- **CATALINA_HOME**：表示Tomcat安装的根目录，例如`/home/tomcat/apache-tomcat-9.0.10` 或`C:\Program Files\apache-tomcat-9.0.10`。
- **CATALINA_BASE**：表示特定Tomcat实例的运行时配置的根。如果要在一台计算机上拥有多个Tomcat实例，请使用该`CATALINA_BASE` 属性。



如果将属性设置为其他位置，则CATALINA_HOME位置包含静态源，例如`.jar`文件或二进制文件。CATALINA_BASE位置包含配置文件，日志文件，已部署的应用程序和其他运行时要求。

#### 为什么要使用CATALINA_BASE

默认情况下，CATALINA_HOME和CATALINA_BASE指向同一目录。需要在一台计算机上运行多个Tomcat实例时，手动设置CATALINA_BASE。这样做具有以下好处：

- 升级到较新版本的Tomcat的管理更加轻松。因为具有单个CATALINA_HOME位置的所有实例共享一组 `.jar`文件和二进制文件，所以您可以轻松地将文件升级到较新版本，并使用同一CATALIA_HOME目录将更改传播到所有Tomcat实例。
- 避免重复相同的静态`.jar`文件。
- 共享某些设置的可能性，例如`setenv`shell或bat脚本文件（取决于您的操作系统）。

#### CATALINA_BASE的内容

在开始使用CATALINA_BASE之前，请首先考虑并创建CATALINA_BASE使用的目录树。请注意，如果未创建所有建议的目录，则Tomcat会自动创建目录。如果由于权限问题而无法创建必要的目录，Tomcat将无法启动或无法正常运行。

考虑以下目录列表：

- 在`bin`与目录`setenv.sh`， `setenv.bat`和`tomcat-juli.jar`文件。

  *推荐：*第

  *查找顺序：*首先检查CATALINA_BASE；向CATALINA_HOME提供后备广告。

- `lib`具有更多资源 的目录将添加到classpath上。

  *推荐：*是的，如果您的应用程序依赖于外部库。

  *查找顺序：*首先检查CATALINA_BASE；CATALINA_HOME加载第二。

- 该`logs`目录实例特定的日志文件。

  *推荐：*是的。

- `webapps`自动加载的Web应用程序 的目录。

  *推荐：*是，如果要部署应用程序。

  *查找顺序：*仅CATALINA_BASE。

- 该`work`目录包含已部署的Web应用程序的临时工作目录。

  *推荐：*是的。

- 该`temp`临时文件使用的JVM目录。

  *推荐：*是的。



我们建议您不要更改`tomcat-juli.jar`文件。但是，如果您需要自己的日志记录实现，则可以将`tomcat-juli.jar`CATALINA_BASE位置中的文件替换为特定的Tomcat实例。

我们还建议您将所有配置文件从`CATALINA_HOME/conf`目录复制 到 `CATALINA_BASE/conf/`目录中。如果CATALINA_BASE中缺少配置文件，则不会回退到CATALINA_HOME。因此，这可能会导致故障。

至少CATALINA_BASE必须包含：

- conf / server.xml
- conf / web.xml

包括`conf`



有关高级配置信息，请参见 [RUNNING.txt ](https://tomcat.apache.org/tomcat-9.0-doc/RUNNING.txt)文件。

#### 如何使用CATALINA_BASE

CATALINA_BASE属性是一个环境变量。您可以在执行Tomcat启动脚本之前进行设置，例如：

- 在Unix上： `CATALINA_BASE=/tmp/tomcat_base1 bin/catalina.sh start`
- 在Windows上： `CATALINA_BASE=C:\tomcat_base1 bin/catalina.bat start`



### 配置Tomcat

本部分将使您熟悉在配置容器时使用的基本信息。

启动时会读取配置文件中的所有信息，这意味着对文件的任何更改都必须重新启动容器。

### 去哪里寻求帮助

尽管我们已尽最大努力确保这些文档书写清晰且易于理解，但我们可能会遗漏一些东西。下面提供了各种网站和邮件列表，以防万一。

请记住，在主要版本的Tomcat之间，某些问题和解决方案有所不同。当您在网上搜索时，会有一些文档与Tomcat 9不相关，而仅与早期版本有关。

- 当前文档-大多数文档将列出潜在的挂断。确保完全阅读相关文档，因为它可以节省大量时间和精力。没有什么比在网上搜寻仅能找到答案一直就在您面前更重要了！
- [Tomcat常见问题](https://wiki.apache.org/tomcat/FAQ)
- [雄猫维基](https://wiki.apache.org/tomcat/)
- [jGuru上的](http://www.jguru.com/faq/home.jsp?topic=Tomcat) Tomcat常见问题解答
- Tomcat的邮件列表存档-许多网站存档Tomcat的邮件列表。由于链接随时间而变化，因此单击此处将搜索 [Google](https://www.google.com/search?q=tomcat+mailing+list+archives)。
- 在TOMCAT-USER邮件列表，你可以订阅 [这里](https://tomcat.apache.org/lists.html)。如果您没有得到答复，则很有可能在列表存档或常见问题解答之一中回答了您的问题。尽管有时会询问和回答有关Web应用程序开发的一般问题，但请将您的问题集中在特定于Tomcat的问题上。
- 您可以在[此处](https://tomcat.apache.org/lists.html)订阅TOMCAT-DEV邮件列表 。该列表 **保留**用于讨论Tomcat本身的开发。有关Tomcat配置的问题，以及在开发和运行应用程序时遇到的问题，通常会更适合在TOMCAT-USER列表中使用。

而且，如果您认为文档中应该包含某些内容，请务必在TOMCAT-DEV列表中告知我们。