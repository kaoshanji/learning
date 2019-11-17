# 5.管理器

### 介绍

在许多生产环境中，具有部署新的Web应用程序或取消部署现有Web应用程序的功能而不必关闭并重新启动整个容器的功能非常有用。此外，即使您尚未将其声明`reloadable`在Tomcat服务器配置文件中，也可以请求现有应用程序重新加载自身。

为了支持这些功能，Tomcat包括一个Web应用程序（默认安装在context path上`/manager`），该应用程序支持以下功能：

- 从WAR文件的上载内容部署新的Web应用程序。
- 从服务器文件系统在指定的上下文路径上部署新的Web应用程序。
- 列出当前部署的Web应用程序以及这些Web应用程序当前处于活动状态的会话。
- 重新加载现有的Web应用程序，以反映`/WEB-INF/classes`或中内容的变化`/WEB-INF/lib`。
- 列出OS和JVM属性值。
- 列出可用的全局JNDI资源，以供正在准备`<ResourceLink>`嵌套在`<Context>`部署描述中的元素的部署工具中使用。
- 启动已停止的应用程序（从而使其再次可用）。
- 停止现有应用程序（以使其不可用），但不要取消部署它。
- 取消部署已部署的Web应用程序并删除其文档基本目录（除非已从文件系统中部署它）。

Tomcat的默认安装包括Manager。要将Manager Web应用程序的实例添加`Context`到新主机，请在`manager.xml`文件`$CATALINA_BASE/conf/[enginename]/[hostname]`夹中安装 上下文配置文件 。这是一个例子：

```
<Context privileged="true" antiResourceLocking="false"
         docBase="${catalina.home}/webapps/manager">
  <Valve className="org.apache.catalina.valves.RemoteAddrValve"
         allow="127\.0\.0\.1" />
</Context>
```

如果已将Tomcat配置为支持多个虚拟主机（网站），则需要为每个虚拟机配置一个Manager。

有三种方法使用**管理器**的Web应用程序。

- 作为具有用户界面的应用程序，您可以在浏览器中使用。这是一个示例URL，您可以在其中替换`localhost`为网站主机名： `http://localhost:8080/manager/html`。
- 仅使用HTTP请求的最低版本，适合系统管理员设置的脚本使用。命令作为请求URI的一部分给出，并且响应采用简单文本的形式，可以轻松地对其进行解析和处理。有关更多信息，请参见[ 支持的Manager命令](http://tomcat.apache.org/tomcat-9.0-doc/manager-howto.html#Supported_Manager_Commands)。
- *Ant* （1.4版或更高版本）构建工具的一组方便的任务定义。有关更多信息，请参见 [使用Ant执行Manager命令](http://tomcat.apache.org/tomcat-9.0-doc/manager-howto.html#Executing_Manager_Commands_With_Ant)。

### 配置Manager应用程序访问

*下面的描述使用变量名$ CATALINA_BASE来引用可解决大多数相对路径的基本目录。如果尚未通过设置CATALINA_BASE目录为多个实例配置Tomcat，则$ CATALINA_BASE将设置为$ CATALINA_HOME的值，该目录已将Tomcat安装到该目录中。*

如果使用默认设置允许Internet上的任何人都可以在您的服务器上执行Manager应用程序，则将Tomcat与默认设置一起交付是非常不安全的。因此，附带的Manager应用程序要求任何尝试使用该应用程序的用户都必须使用具有与其关联的**manager-xxx**角色之一的用户名和密码进行身份验证（角色名称取决于所需的功能）。此外，默认用户文件（`$CATALINA_BASE/conf/tomcat-users.xml`）中没有分配给这些角色的用户名。因此，默认情况下完全禁用对Manager应用程序的访问。

您可以`web.xml`在Manager Web应用程序的文件中找到角色名称。可用角色为：

- **manager-gui-**访问HTML界面。
- **manager-status-仅**访问“服务器状态”页面。
- **manager-script** —访问本文档中描述的工具友好的纯文本界面以及“服务器状态”页面。
- **manager-jmx-**访问JMX代理界面和“服务器状态”页面。

HTML界面受到了CSRF（跨站点请求伪造）攻击的保护，但是文本和JMX界面不能得到保护。这意味着在使用Web浏览器访问Manager应用程序时，被允许访问文本和JMX界面的用户必须谨慎。维护CSRF保护：

- 如果您使用的网络浏览器使用具有或者是用户访问管理器应用程序**管理器，脚本**或 **经理，JMX**角色（例如用于测试纯文本或JMX接口），你必须关闭浏览器的所有窗口之后终止会话。如果您不关闭浏览器并访问其他站点，则可能成为CSRF攻击的受害者。
- 建议不要向 具有**manager-gui**角色的用户授予**manager-script**或**manager-jmx**角色。

**请注意**，JMX代理接口实际上是Tomcat的低级类似于根的管理接口。如果他知道要调用什么命令，则可以做很多事情。启用**manager-jmx**角色时，请务必谨慎 。

要启用对Manager Web应用程序的访问，您必须创建新的用户名/密码组合，并将其中的**manager-xxx**角色与之关联 ，或者将**manager-xxx**角色添加 到某些现有的用户名/密码组合中。由于本文的大部分内容描述了文本界面的使用，因此本示例将使用角色名称**manager-script**。用户名/密码的确切配置方式取决于 您所使用的[Realm实现](http://tomcat.apache.org/tomcat-9.0-doc/config/realm.html)：

- UserDatabaseRealm

  加上

  MemoryUserDatabase

  或

  MemoryRealm

   — 默认情况下配置了

  UserDatabaseRealm

  和

  MemoryUserDatabase

  

  ```
  $CATALINA_BASE/conf/server.xml
  ```

  。无论

  MemoryUserDatabase

  和

  MemoryRealm

  读取存储在默认的XML格式的文件 

  ```
  $CATALINA_BASE/conf/tomcat-users.xml
  ```

  ，它可以用任何文本编辑器进行编辑。该文件包含

  ```
  <user>
  ```

  每个用户的XML ，看起来可能像这样：

  ```
  <user username="craigmcc" password="secret" roles="standard,manager-script" />
  ```

  其中定义了该个人用于登录的用户名和密码，以及他或她与之关联的角色名称。您可以将一个

  管理员脚本

  角色添加到

  ```
  roles
  ```

  一个或多个现有用户的逗号分隔 属性中，和/或用该分配的角色创建新用户。

- *DataSourceRealm*或*JDBCRealm* —您的用户和角色信息存储在通过JDBC访问的数据库中。按照您环境的标准过程，将**经理脚本**角色添加到一个或多个现有用户，和/或创建一个或多个新用户，并为其分配该角色。

- *JNDIRealm* —您的用户和角色信息存储在通过LDAP访问的目录服务器中。按照您环境的标准过程，将**经理脚本**角色添加 到一个或多个现有用户，和/或创建一个或多个新用户，并为其分配该角色。

首次尝试发出下一部分中描述的Manager命令之一时，将面临使用BASIC身份验证登录的挑战。输入的用户名和密码无关紧要，只要它们在用户数据库中标识拥有角色**manager-script**的有效用户即可。

除了密码限制外，**远程IP地址**或主机可以通过添加`RemoteAddrValve`或来限制对Manager Web应用程序的访问`RemoteHostValve`。有关 详细信息，请参见[阀门文档](http://tomcat.apache.org/tomcat-9.0-doc/config/valve.html#Remote_Address_Filter)。这是通过IP地址限制对本地主机的访问的示例：

```
<Context privileged="true">
         <Valve className="org.apache.catalina.valves.RemoteAddrValve"
                allow="127\.0\.0\.1"/>
</Context>
```

### HTML用户友好界面

Manager Web应用程序的用户友好HTML界面位于

```
http://{host}:{port}/manager/html
```

如前所述，您需要具有**manager-gui** 角色才能访问它。有一个单独的文档提供了有关此界面的帮助。看到：

- [HTML Manager文档](http://tomcat.apache.org/tomcat-9.0-doc/html-manager-howto.html)

HTML界面受到了CSRF（跨站点请求伪造）攻击的保护。对HTML页面的每次访问都会生成一个随机令牌，该令牌存储在您的会话中，并包含在页面上的所有链接中。如果您的下一个操作没有正确的令牌值，则该操作将被拒绝。如果令牌已过期，则可以从Manager的主页或“ *列出应用程序”*页面重新开始 。

### 支持的管理器命令

Manager应用程序知道如何处理的所有命令都在单个请求URI中指定，如下所示：

```
http://{host}:{port}/manager/text/{command}?{parameters}
```

其中`{host}`和`{port}`代表运行Tomcat的主机名和端口号，`{command}` 代表您要执行的Manager命令，并 `{parameters}`代表特定于该命令的查询参数。在以下插图中，为您的安装自定义主机和端口。

这些命令通常由HTTP GET请求执行。该 `/deploy`命令具有由HTTP PUT请求执行的形式。

#### 常用参数

大多数命令接受以下一个或多个查询参数：

- **路径** -你正在处理的Web应用程序的上下文路径（包括斜线）。要选择ROOT Web应用程序，请指定“ /”。
  **注意**：无法在Manager应用程序本身上执行管理命令。
  **注意**：如果未明确指定path参数，则将使用标准[Context命名](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Naming)规则从config参数或（如果不存在config参数的情况下）war参数派生路径和版本 。

- **版本** - [并行部署](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)功能使用的此Web应用程序的版本。如果在需要路径的任何地方使用并行部署，则除了路径之外，还必须指定版本，并且路径和版本的组合必须唯一，而不仅仅是路径。
  **注意**：如果未明确指定路径，则忽略version参数。

- war

   -Web应用程序存档（WAR）文件的URL，或包含Web应用程序的目录的路径名，或上下文配置“ .xml”文件。您可以使用以下任何格式的URL：

  - **文件：/ absolute / path / to / a / directory-**包含Web应用程序解压缩版本的**目录**的绝对路径。该目录将被附加到您指定的上下文路径，而无需进行任何更改。
  - **文件：/absolute/path/to/a/webapp.war-Web**应用程序存档（WAR）文件的绝对路径。这是有效的 **唯一**的`/deploy`命令，并且是于该命令的唯一可接受的格式。
  - **文件：/absolute/path/to/a/context.xml-Web**应用程序上下文配置“ .xml”文件的绝对路径，该文件包含上下文配置元素。
  - **directory-**主机的应用程序基本目录中Web应用程序上下文的目录名称。
  - **webapp.war-**位于主机的应用程序基本目录中的Web应用程序war文件的名称。

每个命令将以`text/plain`格式（即不带HTML标记的纯ASCII）返回响应，从而使人类和程序都易于阅读。响应的第一行将以`OK`或开头 `FAIL`，指示请求的命令是否成功。如果发生故障，第一行的其余部分将包含所遇到问题的描述。某些命令包括其他信息行，如下所述。

*国际化说明* -Manager应用程序在资源包中查找其消息字符串，因此可能已为您的平台转换了字符串。下面的示例显示消息的英语版本。

#### 远程部署新的应用程序存档（WAR）

```
http://localhost:8080/manager/text/deploy?path=/foo
```

上载在此HTTP PUT请求中指定为请求数据的Web应用程序存档（WAR）文件，将其安装到`appBase` 我们相应的虚拟主机的目录中，然后开始`appBase`从指定路径中导出添加到的WAR文件的名称。以后可以使用`/undeploy`命令取消部署该应用程序（并删除相应的WAR文件）。

该命令由HTTP PUT请求执行。

通过在中包含上下文配置XML文件，.WAR文件可以包括Tomcat特定的部署配置 `/META-INF/context.xml`。

URL参数包括：

- `update`：设置为true时，将首先取消部署任何现有更新。默认值设置为false。
- `tag`：指定标签名称，这允许将已部署的Web应用程序与标签或标签相关联。如果取消部署Web应用程序，则以后可以仅在使用标签时重新部署它。
- `config `：上下文配置“ .xml”文件的URL，格式为**文件：/absolute/path/to/a/context.xml**。这必须是包含上下文配置元素的Web应用程序上下文配置“ .xml”文件的绝对路径。

**注** –此命令与命令在逻辑上相反`/undeploy`。

如果安装和启动成功，您将收到以下响应：

```
OK - Deployed application at context path /foo
```

否则，响应将以开头`FAIL`并包含错误消息。问题的可能原因包括：

- 应用程序已存在于路径/ foo中

  当前所有正在运行的Web应用程序的上下文路径必须唯一。因此，您必须使用此上下文路径取消部署现有的Web应用程序，或者为新的应用程序选择其他上下文路径。所述`update`参数可以被指定为在URL的参数，具有值`true`来避免这种错误。在这种情况下，将在执行部署之前对现有应用程序执行取消部署。

- 遇到异常

  尝试启动新的Web应用程序时遇到异常。检查Tomcat日志以获取详细信息，但可能的解释包括解析`/WEB-INF/web.xml`文件时遇到问题，或初始化应用程序事件侦听器和过滤器时缺少类。

#### 从本地路径部署新应用程序

部署并启动一个附加到指定上下文的新Web应用程序 `path`（任何其他Web应用程序都不得使用该应用程序）。该命令与命令在逻辑上相反`/undeploy`。

该命令由HTTP GET请求执行。有许多不同的方式可以使用deploy命令。

#### 部署先前部署的Web应用程序

```
http://localhost:8080/manager/text/deploy?path=/footoo&tag=footag
```

这可以用于部署以前使用`tag`属性部署的Web应用程序。请注意，Manager Webapp的工作目录将包含以前部署的WAR；删除它会使部署失败。

#### 通过URL部署目录或WAR

在Tomcat服务器上部署Web应用程序目录或“ .war”文件。如果未`path`指定，则路径和版本将从目录名或war文件名派生。该`war`参数指定`file:`目录或Web应用程序归档（WAR）文件的URL（包括方案）。`java.net.JarURLConnection`该类的Javadocs页面上描述了引用WAR文件的URL的受支持语法 。仅使用引用整个WAR文件的URL。

在此示例中，位于`/path/to/foo`Tomcat服务器目录 中的Web应用程序被部署为名为的Web应用程序上下文`/footoo`。

```
http://localhost:8080/manager/text/deploy?path=/footoo&war=file:/path/to/foo
```

在此示例中，`/path/to/bar.war`Tomcat服务器上的“ .war”文件被部署为名为的Web应用程序上下文 `/bar`。请注意，由于没有`path`参数，因此上下文路径默认为Web应用程序归档文件的名称，不带“ .war”扩展名。

```
http://localhost:8080/manager/text/deploy?war=file:/path/to/bar.war
```

#### 从主机appBase部署目录或War

部署位于主机appBase目录中的Web应用程序目录或“ .war”文件。路径和可选版本来自目录或war文件名。

在本示例中，位于`foo`Tomcat服务器的Host appBase目录中名为 子目录中的Web应用程序被部署为名为的Web应用程序上下文`/foo`。请注意，使用的上下文路径是Web应用程序目录的名称。

```
http://localhost:8080/manager/text/deploy?war=foo
```

在此示例中，`bar.war`位于Tomcat服务器上Host appBase目录中的“ .war”文件被部署为名为的Web应用程序上下文`/bar`。

```
http://localhost:8080/manager/text/deploy?war=bar.war
```

#### 使用上下文配置“ .xml”文件进行部署

如果将Host deployXML标志设置为true，则可以使用上下文配置“ .xml”文件和可选的“ .war”文件或Web应用程序目录来部署Web应用程序。上下文`path` 使用上下文“.XML”配置文件部署Web应用程序时不被使用。

上下文配置“ .xml”文件可以包含用于Web应用程序上下文的有效XML，就像在Tomcat `server.xml`配置文件中对其进行配置一样。这是一个例子：

```
<Context path="/foobar" docBase="/path/to/application/foobar">
</Context>
```

当将可选`war`参数设置为Web应用程序“ .war”文件或目录的URL时，它将覆盖在上下文配置“ .xml”文件中配置的任何docBase。

这是一个使用上下文配置“ .xml”文件部署应用程序的示例。

```
http://localhost:8080/manager/text/deploy?config=file:/path/context.xml
```

这是使用位于服务器上的上下文配置“ .xml”文件和Web应用程序“ .war”文件部署应用程序的示例。

```
http://localhost:8080/manager/text/deploy
 ?config=file:/path/context.xml&war=file:/path/bar.war
```

#### 部署说明

如果为主机配置了unpackWARs = true且您部署了war文件，则战争将被解压缩到Host appBase目录中的目录中。

如果应用程序war或目录安装在Host appBase目录中，并且Host配置为autoDeploy = true或Context路径必须与目录名称或war文件名匹配，且扩展名不带“ .war”。

为了确保不受信任的用户可以管理Web应用程序时的安全性，可以将Host deployXML标志设置为false。这样可以防止不受信任的用户使用配置XML文件部署Web应用程序，也可以防止他们部署位于其主机appBase外部的应用程序目录或“ .war”文件。

#### 部署响应

如果安装和启动成功，您将收到以下响应：

```
OK - Deployed application at context path /foo
```

否则，响应将以开头`FAIL`并包含错误消息。问题的可能原因包括：

- 应用程序已存在于路径/ foo中

  当前所有正在运行的Web应用程序的上下文路径必须唯一。因此，您必须使用此上下文路径取消部署现有的Web应用程序，或者为新的应用程序选择其他上下文路径。所述`update`参数可以被指定为在URL的参数，具有值`true`来避免这种错误。在这种情况下，将在执行部署之前对现有应用程序执行取消部署。

- 文档库不存在或目录不可读

  该`war`参数指定的URL 必须标识此服务器上包含Web应用程序“解压缩”版本的目录，或包含此应用程序的Web应用程序存档（WAR）文件的绝对URL。更正`war`参数指定的值。

- 遇到异常

  尝试启动新的Web应用程序时遇到异常。检查Tomcat日志以获取详细信息，但可能的解释包括解析`/WEB-INF/web.xml`文件时遇到问题，或初始化应用程序事件侦听器和过滤器时缺少类。

- 指定了无效的应用程序URL

  您指定的目录或Web应用程序的URL无效。此类URL必须以开头`file:`，WAR文件的URL必须以“ .war”结尾。

- 指定了无效的上下文路径

  上下文路径必须以斜杠字符开头。要引用ROOT Web应用程序，请使用“ /”。

- 上下文路径必须与目录或WAR文件名匹配：

  如果将应用程序war或目录安装在您的主机appBase目录中，并且使用autoDeploy = true配置了主机，则上下文路径必须与目录名称或war文件名匹配，且不带“ .war”扩展名。

- 只能安装主机Web应用程序目录中的Web应用程序

  如果将Host deployXML标志设置为false，并且尝试将Web应用程序目录或“ .war”文件部署在Host appBase目录之外，则会发生此错误。

#### 列出当前部署的应用程序

```
http://localhost:8080/manager/text/list
```

列出所有当前部署的Web应用程序的上下文路径，当前状态（`running`或 `stopped`）以及活动会话数。启动Tomcat之后立即出现的典型响应如下所示：

```
OK - Listed applications for virtual host localhost
/webdav:running:0:webdav
/examples:running:0:examples
/manager:running:0:manager
/:running:0:ROOT
/test:running:0:test##2
/test:running:0:test##1
```

#### 重新加载现有的应用程序

```
http://localhost:8080/manager/text/reload?path=/examples
```

向现有应用发出信号以自行关闭并重新加载。当Web应用程序上下文不可重新加载并且`/WEB-INF/classes` 目录中有更新的类或属性文件时，或者目录中有添加或更新jar文件时，这很有用 `/WEB-INF/lib`。

如果此命令成功，您将看到如下响应：

```
OK - Reloaded application at context path /examples
```

否则，响应将以开头`FAIL`并包含错误消息。问题的可能原因包括：

- 遇到异常

  尝试重新启动Web应用程序时遇到异常。检查Tomcat日志以获取详细信息。

- 指定了无效的上下文路径

  上下文路径必须以斜杠字符开头。要引用ROOT Web应用程序，请使用“ /”。

- 路径/ foo不存在上下文

  您指定的上下文路径上没有部署的应用程序。

- 没有指定上下文路径

  该`path`参数是必需的。

- 在路径/ foo上部署的WAR不支持重新加载

  当前，`web.xml`当直接从WAR文件部署Web应用程序时，不支持应用程序重新加载（以获取对类或文件的更改 ）。仅当从解压目录中部署Web应用程序时，它才起作用。如果您使用的是WAR文件，则应使用`undeploy`，然后再使用参数`deploy`或 应用程序再次`deploy`使用该`update`参数来获取更改。

#### 列出OS和JVM属性

```
http://localhost:8080/manager/text/serverinfo
```

列出有关Tomcat版本，操作系统和JVM属性的信息。

如果发生错误，响应将以`FAIL`错误消息开头并包含错误消息。问题的可能原因包括：

- 遇到异常

  尝试枚举系统属性时遇到异常。检查Tomcat日志以获取详细信息。

#### 列出可用的全球JNDI资源

```
http://localhost:8080/manager/text/resources[?type=xxxxx]
```

列出可在上下文配置文件的资源链接中使用的全局JNDI资源。如果指定`type` request参数，则该值必须是您感兴趣的资源类型的标准Java类名称（例如，您将指定 `javax.sql.DataSource`获取所有可用JDBC数据源的名称）。如果未指定`type`request参数，则将返回所有类型的资源。

根据是否`type`指定了请求参数，普通响应的第一行将是：

```
OK - Listed global resources of all types
```

要么

```
OK - Listed global resources of type xxxxx
```

每个资源后跟一行。每行由用冒号（“：”）定界的字段组成，如下所示：

- *全局资源名称* -全局JNDI资源的名称，将在元素的`global`属性中使用 `<ResourceLink>`。
- *全局资源类型* -此全局JNDI资源的标准Java类名称。

如果发生错误，响应将以`FAIL`错误消息开头并包含错误消息。问题的可能原因包括：

- 遇到异常

  尝试枚举全局JNDI资源时遇到异常。检查Tomcat日志以获取详细信息。

- 没有可用的全球JNDI资源

  您正在运行的Tomcat服务器已配置为没有全局JNDI资源。

#### 会话统计

```
http://localhost:8080/manager/text/sessions?path=/examples
```

显示Web应用程序的默认会话超时，以及当前处于活动状态的一分钟范围内的实际会话数。例如，在重新启动Tomcat，然后在`/examples`Web应用程序中执行一个JSP示例之后，您可能会得到以下信息：

```
OK - Session information for application at context path /examples
Default maximum session inactive interval 30 minutes
<1 minutes: 1 sessions
1 - <2 minutes: 1 sessions
```

#### 届满时间

```
http://localhost:8080/manager/text/expire?path=/examples&idle=num
```

显示会话统计信息（如上述`/sessions` 命令），并使空闲时间超过`num` 几分钟的会话过期。要使所有会话到期，请使用`&idle=0`。

```
OK - Session information for application at context path /examples
Default maximum session inactive interval 30 minutes
1 - <2 minutes: 1 sessions
3 - <4 minutes: 1 sessions
>0 minutes: 2 sessions were expired
```

实际上，`/sessions`和`/expire`是同一命令的同义词。区别在于存在`idle` 参数。

#### 启动现有的应用程序

```
http://localhost:8080/manager/text/start?path=/examples
```

发出停止的应用程序重新启动的信号，然后使其再次可用。停止和启动很有用，例如，如果应用程序所需的数据库暂时不可用。通常最好停止依赖此数据库的Web应用程序，而不是让用户不断遇到数据库异常。

如果此命令成功，您将看到如下响应：

```
OK - Started application at context path /examples
```

否则，响应将以开头`FAIL`并包含错误消息。问题的可能原因包括：

- 遇到异常

  尝试启动Web应用程序时遇到异常。检查Tomcat日志以获取详细信息。

- 指定了无效的上下文路径

  上下文路径必须以斜杠字符开头。要引用ROOT Web应用程序，请使用“ /”。

- 路径/ foo不存在上下文

  您指定的上下文路径上没有部署的应用程序。

- 没有指定上下文路径

  该`path`参数是必需的。

#### 停止现有的应用程序

```
http://localhost:8080/manager/text/stop?path=/examples
```

发信号通知现有应用程序使其不可用，但将其部署。在应用程序停止时出现的任何请求都将看到HTTP错误404，并且该应用程序在列表应用程序命令上将显示为“已停止”。

如果此命令成功，您将看到如下响应：

```
OK - Stopped application at context path /examples
```

否则，响应将以开头`FAIL`并包含错误消息。问题的可能原因包括：

- 遇到异常

  尝试停止Web应用程序时遇到异常。检查Tomcat日志以获取详细信息。

- 指定了无效的上下文路径

  上下文路径必须以斜杠字符开头。要引用ROOT Web应用程序，请使用“ /”。

- 路径/ foo不存在上下文

  您指定的上下文路径上没有部署的应用程序。

- *没有指定上下文路径* 该`path`参数是必需的。

#### 取消部署现有应用程序

```
http://localhost:8080/manager/text/undeploy?path=/examples
```

**警告 -此命令将删除appBase此虚拟主机的目录（通常为“ webapps”）中存在的任何Web应用程序工件**。这将删除应用程序.WAR（如果存在），应用程序目录（如果存在的话），这些应用程序目录是通过解压缩形式的部署或.WAR扩展以及`$CATALINA_BASE/conf/[enginename]/[hostname]/`目录中的XML上下文定义生成的 。如果您只是想使应用程序退出服务，则应改用`/stop`命令。

向现有应用程序发出信号，以使其正常关闭，然后将其从Tomcat中删除（这也使该上下文路径可用于以后重用）。此外，如果文档根目录存在于`appBase`该虚拟主机的目录（通常为“ webapps”）中，则该目录也将被删除。该命令与命令在逻辑上相反 `/deploy`。

如果此命令成功，您将看到如下响应：

```
OK - Undeployed application at context path /examples
```

否则，响应将以开头`FAIL`并包含错误消息。问题的可能原因包括：

- 遇到异常

  尝试取消部署Web应用程序时遇到异常。检查Tomcat日志以获取详细信息。

- 指定了无效的上下文路径

  上下文路径必须以斜杠字符开头。要引用ROOT Web应用程序，请使用“ /”。

- 不存在名为/ foo的上下文

  没有使用您指定的名称的已部署应用程序。

- *没有指定上下文路径* 该`path`参数是必需的。

#### 查找内存泄漏

```
http://localhost:8080/manager/text/findleaks[?statusLine=[true|false]]
```

**查找泄漏诊断将触发完整的垃圾回收。在生产系统上使用时应格外小心。**

查找泄漏诊断尝试识别停止，重新加载或取消部署时已引起内存泄漏的Web应用程序。应始终使用分析器确认结果。该诊断使用StandardHost实现提供的其他功能。如果使用不扩展StandardHost的自定义主机，它将无法正常工作。

据记录，从Java代码中明确触发完整的垃圾回收是不可靠的。此外，根据使用的JVM，有一些选项可以禁用显式GC触发，例如`-XX:+DisableExplicitGC`。如果要确保诊断程序已成功运行完整的GC，则需要使用GC日志记录，JConsole等工具进行检查。

如果此命令成功，您将看到如下响应：

```
/leaking-webapp
```

如果您希望看到响应中包含状态行，请`statusLine`在请求中包含 查询参数，其值为 `true`。

已停止，重新加载或取消部署但以前运行的哪些类仍仍加载到内存中的Web应用程序的每个上下文路径将在新行中列出。如果某个应用程序已重新加载几次，则可能会列出几次。

如果命令未成功执行，则响应将以开头 `FAIL`并包含错误消息。

#### 连接器SSL / TLS密码信息

```
http://localhost:8080/manager/text/sslConnectorCiphers
```

“ SSL连接器/密码”诊断列出了当前为每个连接器配置的SSL / TLS密码。对于NIO和NIO2，列出了各个密码套件的名称。对于APR，将返回SSLCipherSuite的值。

响应如下所示：

```
OK - Connector / SSL Cipher information
Connector[HTTP/1.1-8080]
  SSL is not enabled for this connector
Connector[HTTP/1.1-8443]
  TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA
  TLS_DHE_RSA_WITH_AES_128_CBC_SHA
  TLS_ECDH_RSA_WITH_AES_128_CBC_SHA
  TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA
  ...
```

#### 连接器SSL / TLS证书链信息

```
http://localhost:8080/manager/text/sslConnectorCerts
```

“ SSL连接器/证书”诊断列出了当前为每个虚拟主机配置的证书链。

响应如下所示：

```
OK - Connector / Certificate Chain information
Connector[HTTP/1.1-8080]
SSL is not enabled for this connector
Connector[HTTP/1.1-8443]-_default_-RSA
[
[
  Version: V3
  Subject: CN=localhost, OU=Apache Tomcat PMC, O=The Apache Software Foundation, L=Wakefield, ST=MA, C=US
  Signature Algorithm: SHA256withRSA, OID = 1.2.840.113549.1.1.11
  ...
```

#### 连接器SSL / TLS可信证书信息

```
http://localhost:8080/manager/text/sslConnectorTrustedCerts
```

SSL连接器/证书诊断列出了当前为每个虚拟主机配置的受信任证书。

响应如下所示：

```
OK - Connector / Trusted Certificate information
Connector[HTTP/1.1-8080]
SSL is not enabled for this connector
Connector[AJP/1.3-8009]
SSL is not enabled for this connector
Connector[HTTP/1.1-8443]-_default_
[
[
  Version: V3
  Subject: CN=Apache Tomcat Test CA, OU=Apache Tomcat PMC, O=The Apache Software Foundation, L=Wakefield, ST=MA, C=US
  ...
```

#### 重新加载TLS配置

```
http://localhost:8080/manager/text/sslReload?tlsHostName=name
```

重新加载TLS配置文件（证书和密钥文件，这不会触发对server.xml的重新分析）。要为所有主机重新加载文件，请不要指定`tlsHostName`参数。

```
OK - Reloaded TLS configuration for [_default_]
```

#### 线程转储

```
http://localhost:8080/manager/text/threaddump
```

编写JVM线程转储。

响应如下所示：

```
OK - JVM thread dump
2014-12-08 07:24:40.080
Full thread dump Java HotSpot(TM) Client VM (25.25-b02 mixed mode):

"http-nio-8080-exec-2" Id=26 cpu=46800300 ns usr=46800300 ns blocked 0 for -1 ms waited 0 for -1 ms
   java.lang.Thread.State: RUNNABLE
        locks java.util.concurrent.ThreadPoolExecutor$Worker@1738ad4
        at sun.management.ThreadImpl.dumpThreads0(Native Method)
        at sun.management.ThreadImpl.dumpAllThreads(ThreadImpl.java:446)
        at org.apache.tomcat.util.Diagnostics.getThreadDump(Diagnostics.java:440)
        at org.apache.tomcat.util.Diagnostics.getThreadDump(Diagnostics.java:409)
        at org.apache.catalina.manager.ManagerServlet.threadDump(ManagerServlet.java:557)
        at org.apache.catalina.manager.ManagerServlet.doGet(ManagerServlet.java:371)
        at javax.servlet.http.HttpServlet.service(HttpServlet.java:618)
        at javax.servlet.http.HttpServlet.service(HttpServlet.java:725)
...
```

#### 虚拟机信息

```
http://localhost:8080/manager/text/vminfo
```

编写一些有关Java虚拟机的诊断信息。

响应如下所示：

```
OK - VM info
2014-12-08 07:27:32.578
Runtime information:
  vmName: Java HotSpot(TM) Client VM
  vmVersion: 25.25-b02
  vmVendor: Oracle Corporation
  specName: Java Virtual Machine Specification
  specVersion: 1.8
  specVendor: Oracle Corporation
  managementSpecVersion: 1.2
  name: ...
  startTime: 1418012458849
  uptime: 393855
  isBootClassPathSupported: true

OS information:
...
```

#### 保存配置

```
http://localhost:8080/manager/text/save
```

如果未指定任何参数，则此命令会将服务器的当前配置保存到server.xml。如果需要，现有文件将重命名为备份。

如果使用与`path`部署的Web应用程序的路径匹配的参数指定，则该Web应用程序的配置将保存到`xmlBase` 当前主机的适当命名的context.xml文件中。

要使用该命令，必须存在StoreConfig MBean。通常，这是使用[StoreConfigLifecycleListener](http://tomcat.apache.org/tomcat-9.0-doc/config/listeners.html#StoreConfig_Lifecycle_Listener_-_org.apache.catalina.storeconfig.StoreConfigLifecycleListener)配置的。

如果命令未成功执行，则响应将以开头 `FAIL`并包含错误消息。

### 服务器状态

通过以下链接，您可以查看有关服务器的状态信息。**manager-xxx**角色中的任何一个都允许访问此页面。

```
http://localhost:8080/manager/status
http://localhost:8080/manager/status/all
```

以HTML格式显示服务器状态信息。

```
http://localhost:8080/manager/status?XML=true
http://localhost:8080/manager/status/all?XML=true
```

以XML格式显示服务器状态信息。

首先，您具有服务器和JVM版本号，JVM提供程序，操作系统名称和编号，然后是体系结构类型。

其次，有关于JVM内存使用情况的信息。

然后，有关于Tomcat AJP和HTTP连接器的信息。两者都可以使用相同的信息：

- 线程信息：最大线程，最小和最大备用线程，当前线程数和当前线程繁忙。
- 请求信息：最大处理时间和处理时间，请求和错误计数，接收和发送的字节数。
- 该表显示阶段，时间，发送的字节数，接收的字节数，客户端，VHost和请求。表中列出了所有现有线程。这是可能的线程阶段的列表：
  - *“解析并准备请求”*：正在解析请求标头，或者正在进行必要的准备工作以读取请求正文（如果已指定传输编码）。
  - *“服务”*：线程正在处理请求并生成响应。此阶段在“解析和准备请求”阶段之后，并且在“完成”阶段之前。在此阶段，始终有至少一个线程（服务器状态页面）。
  - *“完成”*：请求处理的结束。仍保留在输出缓冲区中的任何其余响应都将发送到客户端。如果适合使连接保持活动状态，则此阶段之后为“保持活动”，如果不适合，则紧随其后的是“就绪”。
  - *“ Keep-Alive”*：如果客户端发送另一个请求，线程将保持与客户端的连接打开。如果收到另一个请求，则下一阶段将是“解析并准备请求”。如果在保持活动超时之前未收到任何请求，则连接将关闭，下一阶段将为“就绪”。
  - *“就绪”*：线程处于静止状态并准备使用。

如果使用`/status/all`命令，则将提供有关每个已部署Web应用程序的其他信息。

### 使用JMX代理Servlet

#### 什么是JMX代理Servlet

JMX代理Servlet是一种轻量级代理，用于获取和设置tomcat内部。（或任何通过MBean公开的类）它的用法不是非常用户友好，但是UI对于集成用于监视和更改tomcat内部的命令行脚本非常有帮助。您可以使用代理执行两件事：获取信息和设置信息。为了真正了解JMX代理Servlet，您应该对JMX有一个一般的了解。如果您不知道JMX是什么，那么请做好准备。

#### JMX查询命令

它采用以下形式：

```
http://webserver/manager/jmxproxy/?qry=STUFF
```

`STUFF`您希望执行的JMX查询在哪里。例如，以下是您可能希望运行的一些查询：

- `qry=*%3Atype%3DRequestProcessor%2C* --> type=RequestProcessor` 它将找到所有可以处理请求并报告其状态的工作人员。
- `qry=*%3Aj2eeType=Servlet%2c* --> j2eeType=Servlet` 返回所有已加载的servlet。
- `qry=Catalina%3Atype%3DEnvironment%2Cresourcetype%3DGlobal%2Cname%3DsimpleValue --> Catalina:type=Environment,resourcetype=Global,name=simpleValue` 通过给定名称查找特定的MBean。

您需要进行试验以真正了解其功能。如果不提供任何`qry`参数，则将显示所有MBean。我们确实建议您查看tomcat源代码并了解JMX规范，以更好地了解您可能会运行的所有查询。

#### JMX获取命令

JXMProxyServlet还支持“ get”命令，您可以使用该命令来获取特定MBean属性的值。该`get`命令的一般形式为：

```
http://webserver/manager/jmxproxy/?get=BEANNAME&att=MYATTRIBUTE&key=MYKEY
```

您必须提供以下参数：

1. `get`：完整的Bean名称
2. `att`：您希望获取的属性
3. `key`：（可选）进入CompositeData MBean属性的键

如果一切顺利，则提示“ OK”，否则将显示错误消息。例如，假设我们希望获取当前的堆内存数据：

```
http://webserver/manager/jmxproxy/?get=java.lang:type=Memory&att=HeapMemoryUsage
```

或者，如果您只想要“ used”键：

```
http://webserver/manager/jmxproxy/
 ?get=java.lang:type=Memory&att=HeapMemoryUsage&key=used
```

#### JMX Set命令

现在，您可以查询MBean了，现在该浪费Tomcat的内部知识了！set命令的一般形式为：

```
http://webserver/manager/jmxproxy/?set=BEANNAME&att=MYATTRIBUTE&val=NEWVALUE
```

因此，您需要提供3个请求参数：

1. `set`：完整的Bean名称
2. `att`：您希望更改的属性
3. `val`：新价值

如果一切正常，则会显示“ OK”，否则将显示错误消息。例如，假设我们希望为进行即时调试 `ErrorReportValve`。以下将调试设置为10。

```
http://localhost:8080/manager/jmxproxy/
 ?set=Catalina%3Atype%3DValve%2Cname%3DErrorReportValve%2Chost%3Dlocalhost
 &att=debug&val=10
```

我的结果是（YMMV）：

```
Result: ok
```

如果我传递的值不正确，这就是我看到的。这是我使用的URL，我尝试将调试设置为“ cow”：

```
http://localhost:8080/manager/jmxproxy/
 ?set=Catalina%3Atype%3DValve%2Cname%3DErrorReportValve%2Chost%3Dlocalhost
 &att=debug&val=cow
```

当我尝试时，我的结果是

```
Error: java.lang.NumberFormatException: For input string: "cow"
```

#### JMX调用命令

该`invoke`命令使方法可以在MBean上调用。该命令的一般形式为：

```
http://webserver/manager/jmxproxy/
 ?invoke=BEANNAME&op=METHODNAME&ps=COMMASEPARATEDPARAMETERS
```

例如，要调用**服务**的`findConnectors()`方法，请 使用：

```
http://localhost:8080/manager/jmxproxy/
 ?invoke=Catalina%3Atype%3DService&op=findConnectors&ps=
```