# 9.JNDI资源

### 介绍

Tomcat 以与[ Java Enterprise Edition](http://www.oracle.com/technetwork/java/javaee/overview/index.html)应用程序服务器提供的方式兼容的方式为其下运行的每个Web应用程序提供JNDI **InitialContext**实现实例 。Java EE标准在文件中提供了一组标准的元素，以引用/定义资源。`/WEB-INF/web.xml`

请参阅以下规范，以获取有关用于JNDI的编程API以及Tomcat为其提供的服务仿真的Java Enterprise Edition（Java EE）服务器支持的功能的更多信息：

- [Java命名和目录接口](http://docs.oracle.com/javase/7/docs/technotes/guides/jndi/index.html)（包含在JDK 1.4及更高版本中）
- [Java EE平台规范](http://www.oracle.com/technetwork/java/javaee/documentation/index.html)（尤其，请参阅有关*命名的*第5章）

### web.xml配置

Web应用程序的Web应用程序部署描述符（`/WEB-INF/web.xml`）中可以使用以下元素来定义资源：

- `**<env-entry>**` -环境条目，一个单值参数，可用于配置应用程序的运行方式。
- `**<resource-ref>**`-资源引用，通常是对象工厂的资源，例如JDBC `DataSource`，JavaMail `Session`或配置到Tomcat中的自定义对象工厂。
- `**<resource-env-ref>**`-资源环境参考，`resource-ref` 这是Servlet 2.4 中新增的一个变体，它对于不需要身份验证信息的资源更易于配置。

如果Tomcat能够标识用于创建资源的适当资源工厂，并且不需要其他配置信息，则Tomcat将使用其中的信息`/WEB-INF/web.xml`来创建资源。

Tomcat为无法在web.xml中指定的JNDI资源提供了许多Tomcat特定的选项。这些功能包括`closeMethod`在Web应用程序停止时可以更快地清除JNDI资源，并 `singleton`控制是否为每个JNDI查找都创建资源的新实例。要使用这些配置选项，必须在Web应用程序的[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)元素或的 元素中 指定资源 。[ `****`](http://tomcat.apache.org/tomcat-9.0-doc/config/globalresources.html)`$CATALINA_BASE/conf/server.xml`

### context.xml配置

如果Tomcat无法识别适当的资源工厂和/或需要其他配置信息，则在Tomcat可以创建资源之前，必须指定其他特定于Tomcat的配置。Tomcat特定的资源配置输入[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)到可以`$CATALINA_BASE/conf/server.xml`在每个Web应用程序上下文XML文件（`META-INF/context.xml`）中指定的元素中，或者最好在每个Web应用程序上下文XML文件（）中指定。

Tomcat特定资源配置是使用元素中的以下元素进行的[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html) ：

- [](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Environment_Entries) -配置将通过JNDI向Web应用程序公开的标量环境条目的名称和值 `InitialContext`（相当于`<env-entry>`在Web应用程序部署描述符中包含一个 元素）。
- [](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Resource_Definitions) -配置可供应用程序使用的资源的名称和数据类型（相当于`<resource-ref>`在Web应用程序部署描述符中包含 元素）。
- [](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Resource_Links) -将链接添加到在全局JNDI上下文中定义的资源。使用资源链接使Web应用程序可以访问 在[](http://tomcat.apache.org/tomcat-9.0-doc/config/server.html) 元素的[](http://tomcat.apache.org/tomcat-9.0-doc/config/globalresources.html)子元素中定义的资源。
- [](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Transaction) -添加一个资源工厂，用于实例化UserTransaction对象实例，该实例可从获得`java:comp/UserTransaction`。

这些元素中的任何数量都可以嵌套在一个 [``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)元素内，并且仅与该特定的Web应用程序关联。

如果在[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)元素中定义了资源 ，则无需在中定义该资源`/WEB-INF/web.xml`。但是，建议保留该条目`/WEB-INF/web.xml` 以记录Web应用程序的资源需求。

如果为`<env-entry>`Web应用程序部署描述符（`/WEB-INF/web.xml`）中包含的 `<Environment>`元素和作为[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)Web应用程序元素的一部分的 元素中定义了相同的资源名称 ，则**只有**在相应`<Environment>`元素允许的情况下， 部署描述符中的值**才**优先 （通过将`override` 属性设置为“ true”）。

### 全局配置

Tomcat为整个服务器维护一个单独的全局资源命名空间。这些是在中的元素中配置 的 。您可以使用[](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Resource_Links)将这些资源公开给Web应用程序， 以将其包含在每个Web应用程序上下文中。[ `****`](http://tomcat.apache.org/tomcat-9.0-doc/config/globalresources.html)`$CATALINA_BASE/conf/server.xml`

如果已经使用[](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Resource_Links)定义了 [资源](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Resource_Links)，则无需在中定义该资源`/WEB-INF/web.xml`。但是，建议保留该条目`/WEB-INF/web.xml` 以记录Web应用程序的资源需求。

### 使用资源

将`InitialContext`其配置为最初部署的Web应用程序，并可供Web应用程序组件使用（用于只读访问）。所有已配置的条目和资源都放置在`java:comp/env`JNDI名称空间的一部分中，因此对资源（在这种情况下为JDBC）的典型访问`DataSource`将如下所示：

```java
// Obtain our environment naming context
Context initCtx = new InitialContext();
Context envCtx = (Context) initCtx.lookup("java:comp/env");

// Look up our data source
DataSource ds = (DataSource)
  envCtx.lookup("jdbc/EmployeeDB");

// Allocate and use a connection from the pool
Connection conn = ds.getConnection();
... use this connection to access the database ...
conn.close();
```

### Tomcat标准资源工厂

Tomcat包括一系列标准资源工厂，这些工厂可以为您的Web应用程序提供服务，但是在[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)不修改Web应用程序或部署描述符的情况下（通过元素）为您提供了配置灵活性 。以下每个小节详细介绍了标准资源工厂的配置和使用。

有关如何在Tomcat中创建，安装，配置和使用您自己的自定义资源工厂类的信息，请参见[添加自定义资源工厂](http://tomcat.apache.org/tomcat-9.0-doc/jndi-resources-howto.html#Adding_Custom_Resource_Factories)。

*注* –在标准资源工厂中，只有“ JDBC数据源”和“用户事务”工厂被强制在其他平台上可用，然后，只有在该平台实现Java Enterprise Edition（Java EE）规范时才需要它们。所有其他标准资源工厂，以及您自己编写的自定义资源工厂，都是特定于Tomcat的，不能假定在其他容器上可用。

#### 通用JavaBean资源

##### 0.简介

此资源工厂可用于创建 符合标准JavaBeans命名约定的*任何* Java类的对象（即，它具有零参数构造函数，并具有符合setFoo（）命名模式的属性设置器。资源工厂将仅创建`lookup()`如果`singleton` factory属性设置为，则每次为此条目创建一个适当的bean类的新实例 `false`。

下面介绍了使用此工具所需的步骤。

##### 1.创建您的JavaBean类

创建JavaBean类，该类将在每次查找资源工厂时实例化。对于此示例，假设您创建一个类`com.mycompany.MyBean`，如下所示：

```java
package com.mycompany;

public class MyBean {

  private String foo = "Default Foo";

  public String getFoo() {
    return (this.foo);
  }

  public void setFoo(String foo) {
    this.foo = foo;
  }

  private int bar = 0;

  public int getBar() {
    return (this.bar);
  }

  public void setBar(int bar) {
    this.bar = bar;
  }


}
```

##### 2.声明您的资源需求

接下来，修改您的Web应用程序部署描述符（`/WEB-INF/web.xml`），以声明JNDI名称，在该名称下您将请求该bean的新实例。最简单的方法是使用`<resource-env-ref>`元素，如下所示：

```xml
<resource-env-ref>
  <description>
    Object factory for MyBean instances.
  </description>
  <resource-env-ref-name>
    bean/MyBeanFactory
  </resource-env-ref-name>
  <resource-env-ref-type>
    com.mycompany.MyBean
  </resource-env-ref-type>
</resource-env-ref>
```

**警告** -确保您遵守DTD对于Web应用程序部署描述符所要求的元素排序！有关详细信息，请参见 [Servlet规范](https://wiki.apache.org/tomcat/Specifications)。

##### 3.编写您的应用程序对此资源的使用代码

该资源环境参考的典型用法如下所示：

```java
Context initCtx = new InitialContext();
Context envCtx = (Context) initCtx.lookup("java:comp/env");
MyBean bean = (MyBean) envCtx.lookup("bean/MyBeanFactory");

writer.println("foo = " + bean.getFoo() + ", bar = " +
               bean.getBar());
```

##### 4.配置Tomcat的资源工厂

要配置Tomcat的资源工厂，请[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)在此Web应用程序的元素中添加这样的 元素。

```xml
<Context ...>
  ...
  <Resource name="bean/MyBeanFactory" auth="Container"
            type="com.mycompany.MyBean"
            factory="org.apache.naming.factory.BeanFactory"
            bar="23"/>
  ...
</Context>
```

请注意，资源名称（此处`bean/MyBeanFactory` 必须与Web应用程序部署描述符中指定的值匹配。我们也在初始化`bar` 属性的值，这将导致`setBar(23)`在返回新bean之前调用该`foo`属性。因为我们没有初始化该 属性（尽管我们可以这样做），bean将包含其构造函数设置的任何默认值。

某些bean具有无法自动从字符串值转换的类型的属性。使用Tomcat BeanFactory设置此类属性将失败，并显示NamingException。如果这些bean提供了从字符串值设置属性的方法，则可以将Tomcat BeanFactory配置为使用这些方法。使用`forceString`属性完成配置。

假设我们的bean看起来像这样：

```java
package com.mycompany;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyBean2 {

  private InetAddress local = null;

  public InetAddress getLocal() {
    return local;
  }

  public void setLocal(InetAddress ip) {
    local = ip;
  }

  public void setLocal(String localHost) {
    try {
      local = InetAddress.getByName(localHost);
    } catch (UnknownHostException ex) {
    }
  }

  private InetAddress remote = null;

  public InetAddress getRemote() {
    return remote;
  }

  public void setRemote(InetAddress ip) {
    remote = ip;
  }

  public void host(String remoteHost) {
    try {
      remote = InetAddress.getByName(remoteHost);
    } catch (UnknownHostException ex) {
    }
  }

}
```

Bean具有两个属性，两者均为type `InetAddress`。第一个属性`local`还有一个附加的setter，带有字符串参数。默认情况下，Tomcat BeanFactory会尝试使用参数类型与属性类型相同的自动检测到的setter，然后抛出NamingException，因为它不准备将给定的字符串属性值转换为`InetAddress`。我们可以告诉Tomcat BeanFactory像这样使用其他设置器：

```xml
<Context ...>
  ...
  <Resource name="bean/MyBeanFactory" auth="Container"
            type="com.mycompany.MyBean2"
            factory="org.apache.naming.factory.BeanFactory"
            forceString="local"
            local="localhost"/>
  ...
</Context>
```

bean属性`remote`也可以从字符串设置，但是必须使用非标准方法名称`host`。设置`local`和`remote`使用以下配置：

```xml
<Context ...>
  ...
  <Resource name="bean/MyBeanFactory" auth="Container"
            type="com.mycompany.MyBean2"
            factory="org.apache.naming.factory.BeanFactory"
            forceString="local,remote=host"
            local="localhost"
            remote="tomcat.apache.org"/>
  ...
</Context>
```

多个属性描述可以`forceString`通过使用逗号作为分隔符的方式合并 在一起。每个属性描述都仅包含属性名称，在这种情况下，BeanFactory会调用setter方法。或者由它组成，`name=method`在这种情况下，`name`可以通过调用method设置命名的属性 `method`。对于类型`String`或原始类型或其关联的原始包装器类的 属性，`forceString`不需要使用。将自动检测到正确的设置器，并将应用参数转换。

#### UserDatabase资源

##### 0.简介

通常将UserDatabase资源配置为供UserDatabase领域使用的全局资源。Tomcat包括一个UserDatabaseFactory，它创建由XML文件支持的UserDatabase资源-通常 `tomcat-users.xml`

下面介绍设置全局UserDatabase资源所需的步骤。

##### 1.创建/编辑XML文件

XML文件通常位于， `$CATALINA_BASE/conf/tomcat-users.xml`但是您可以随意在文件系统上的任何位置找到该文件。建议将XML文件放在中`$CATALINA_BASE/conf`。典型的XML如下所示：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<tomcat-users>
  <role rolename="tomcat"/>
  <role rolename="role1"/>
  <user username="tomcat" password="tomcat" roles="tomcat"/>
  <user username="both" password="tomcat" roles="tomcat,role1"/>
  <user username="role1" password="tomcat" roles="role1"/>
</tomcat-users>
```

##### 2.声明您的资源

接下来，修改`$CATALINA_BASE/conf/server.xml`以基于您的XML文件创建UserDatabase资源。它看起来应该像这样：

```xml
<Resource name="UserDatabase"
          auth="Container"
          type="org.apache.catalina.UserDatabase"
          description="User database that can be updated and saved"
          factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
          pathname="conf/tomcat-users.xml"
          readonly="false" />
```

该`pathname`属性可以是URL，绝对路径或相对路径。如果是相对的，则相对于`$CATALINA_BASE`。

该`readonly`属性是可选的，`true`如果未提供，则默认为 。如果XML是可写的，则它将在Tomcat启动时写入。**警告：**写入文件时，它将继承运行Tomcat的用户的默认文件权限。确保这些适当以维护安装的安全性。

如果在领域中引用，则默认情况下，UserDatabse将监视`pathname`更改并在观察到上次修改时间更改的情况下 重新加载文件。可以通过将`watchSource`属性设置为禁用此功能 `false`。

##### 3.配置领域

如[Realm配置文档中](http://tomcat.apache.org/tomcat-9.0-doc/config/realm.html)所述，配置UserDatabase Realm以使用此资源 。

#### JavaMail会话

##### 0.简介

在许多Web应用程序中，发送电子邮件消息是系统功能的必需部分。在 [Java邮件](http://www.oracle.com/technetwork/java/javamail/index.html) API使这个过程相对简单，但需要很多配置细节，客户端应用程序必须知道的（包括SMTP主机被用于报文发送的名称）。

Tomcat包括一个标准资源工厂，该工厂将为您创建`javax.mail.Session`会话实例，该 实例已经配置为连接到SMTP服务器。通过这种方式，应用程序完全不受电子邮件服务器配置环境中的更改的干扰-它仅在需要时请求并接收预配置的会话。

下面概述了为此所需的步骤。

##### 1.声明您的资源需求

您应该做的第一件事是修改Web应用程序部署描述符（`/WEB-INF/web.xml`），以声明JNDI名称，在该名称下您将查找预配置的会话。按照惯例，所有此类名称都应解析为`mail`子上下文（相对于作为`java:comp/env`所有提供的资源工厂的根的标准命名上下文。一个典型的`web.xml`条目可能如下所示：

```xml
<resource-ref>
  <description>
    Resource reference to a factory for javax.mail.Session
    instances that may be used for sending electronic mail
    messages, preconfigured to connect to the appropriate
    SMTP server.
  </description>
  <res-ref-name>
    mail/Session
  </res-ref-name>
  <res-type>
    javax.mail.Session
  </res-type>
  <res-auth>
    Container
  </res-auth>
</resource-ref>
```

**警告** -确保您遵守DTD对于Web应用程序部署描述符所要求的元素排序！有关详细信息，请参见 [Servlet规范](https://wiki.apache.org/tomcat/Specifications)。

##### 2.编写您的应用程序对此资源的使用代码

该资源引用的典型用法如下所示：

```java
Context initCtx = new InitialContext();
Context envCtx = (Context) initCtx.lookup("java:comp/env");
Session session = (Session) envCtx.lookup("mail/Session");

Message message = new MimeMessage(session);
message.setFrom(new InternetAddress(request.getParameter("from")));
InternetAddress to[] = new InternetAddress[1];
to[0] = new InternetAddress(request.getParameter("to"));
message.setRecipients(Message.RecipientType.TO, to);
message.setSubject(request.getParameter("subject"));
message.setContent(request.getParameter("content"), "text/plain");
Transport.send(message);
```

请注意，应用程序使用与Web应用程序部署描述符中声明的资源引用名称相同的名称。这与[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)在Web应用程序的元素中配置的资源工厂相匹配， 如下所述。

##### 3.配置Tomcat的资源工厂

要配置Tomcat的资源工厂，请[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)在此Web应用程序的元素中添加类似这样的 元素。

```xml
<Context ...>
  ...
  <Resource name="mail/Session" auth="Container"
            type="javax.mail.Session"
            mail.smtp.host="localhost"/>
  ...
</Context>
```

请注意，资源名称（此处为`mail/Session`）必须与Web应用程序部署描述符中指定的值匹配。自定义`mail.smtp.host`参数的值以指向为您的网络提供SMTP服务的服务器。

其他资源属性和值将转换为属性和值，并`javax.mail.Session.getInstance(java.util.Properties)`作为`java.util.Properties`集合的一部分传递 。除了JavaMail规范的附件A中定义的属性外，各个提供程序还可以支持其他属性。

如果为资源配置了一个`password`属性，或者使用属性`mail.smtp.user`或`mail.user`属性，则Tomcat的资源工厂将配置该属性并将其添加 `javax.mail.Authenticator`到邮件会话中。

##### 4.安装JavaMail库

[下载JavaMail API](http://javamail.java.net/)。

将发行版解压缩并将mail.jar放入$ CATALINA_HOME / lib中，以便在邮件会话资源初始化期间Tomcat可以使用它。**注意：**将此jar放在$ CATALINA_HOME / lib和Web应用程序的lib文件夹中都会导致错误，因此请确保仅将其放在$ CATALINA_HOME / lib位置。

##### 5.重新启动Tomcat

为了使其他JAR对Tomcat可见，必须重新启动Tomcat实例。

##### 应用范例

`/examples`Tomcat随附的应用程序包含利用此资源工厂的示例。可通过“ JSP示例”链接进行访问。实际发送邮件消息的servlet的源代码在中 `/WEB-INF/classes/SendMailServlet.java`。

**警告** -默认配置假定上的端口25上有一个SMTP服务器列表`localhost`。如果不是这种情况，请编辑[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)此Web应用程序的 元素，然后将该参数的参数值修改为 `mail.smtp.host`网络上SMTP服务器的主机名。

#### JDBC数据源

##### 0.简介

许多Web应用程序需要通过JDBC驱动程序访问数据库，以支持该应用程序所需的功能。Java EE平台规范要求Java EE应用服务器为此目的提供*数据源*实现（即，用于JDBC连接的连接池）。Tomcat提供了完全相同的支持，因此，使用此服务在Tomcat上开发的基于数据库的应用程序将在任何Java EE服务器上保持不变。

有关JDBC的信息，请查阅以下内容：

- [http://www.oracle.com/technetwork/java/javase/jdbc/index.html-](http://www.oracle.com/technetwork/java/javase/jdbc/index.html)有关Java数据库连接的信息的主页。
- [http://java.sun.com/j2se/1.3/docs/guide/jdbc/spec2/jdbc2.1.frame.html-JDBC](http://java.sun.com/j2se/1.3/docs/guide/jdbc/spec2/jdbc2.1.frame.html) 2.1 API规范。
- [http://java.sun.com/products/jdbc/jdbc20.stdext.pdf-JDBC](http://java.sun.com/products/jdbc/jdbc20.stdext.pdf) 2.0标准扩展API（包括 `javax.sql.DataSource`API）。该程序包现在称为“ JDBC可选程序包”。
- [http://www.oracle.com/technetwork/java/javaee/overview/index.htm-Java](http://www.oracle.com/technetwork/java/javaee/overview/index.htm) EE平台规范（涵盖所有Java EE平台必须提供给应用程序的JDBC功能）。

**注** – Tomcat中的默认数据源支持基于[Commons](https://commons.apache.org/) 项目中的**DBCP 2**连接池 。然而，也可以使用实现任何其他连接池，通过编写自己的自定义资源工厂，描述 [如下](http://tomcat.apache.org/tomcat-9.0-doc/jndi-resources-howto.html#Adding_Custom_Resource_Factories)。`javax.sql.DataSource`

##### 1.安装JDBC驱动程序

在使用*JDBC数据源* JNDI资源工厂，你需要作出适当的JDBC驱动程序可用于两个Tomcat的内部类和Web应用程序。通过将驱动程序的JAR文件安装到`$CATALINA_HOME/lib`目录中，可以最轻松地完成此操作 ，这使该驱动程序可用于资源工厂和应用程序。

##### 2.声明您的资源需求

接下来，修改Web应用程序部署描述符（`/WEB-INF/web.xml`），以声明JNDI名称，在该名称下您将查找预配置的数据源。按照惯例，所有此类名称都应解析为`jdbc`子上下文（相对于作为`java:comp/env`所有提供的资源工厂的根的标准命名上下文。一个典型的`web.xml`条目可能如下所示：

```xml
<resource-ref>
  <description>
    Resource reference to a factory for java.sql.Connection
    instances that may be used for talking to a particular
    database that is configured in the <Context>
    configuration for the web application.
  </description>
  <res-ref-name>
    jdbc/EmployeeDB
  </res-ref-name>
  <res-type>
    javax.sql.DataSource
  </res-type>
  <res-auth>
    Container
  </res-auth>
</resource-ref>
```

**警告** -确保您遵守DTD对于Web应用程序部署描述符所要求的元素排序！有关详细信息，请参见 [Servlet规范](https://wiki.apache.org/tomcat/Specifications)。

##### 3.编写您的应用程序对此资源的使用代码

该资源引用的典型用法如下所示：

```java
Context initCtx = new InitialContext();
Context envCtx = (Context) initCtx.lookup("java:comp/env");
DataSource ds = (DataSource)
  envCtx.lookup("jdbc/EmployeeDB");

Connection conn = ds.getConnection();
... use this connection to access the database ...
conn.close();
```

请注意，应用程序使用与Web应用程序部署描述符中声明的资源引用名称相同的名称。这与[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)在Web应用程序的元素中配置的资源工厂相匹配， 如下所述。

##### 4.配置Tomcat的资源工厂

要配置Tomcat的资源工厂，请[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)在Web应用程序的元素中添加这样的 元素。

```xml
<Context ...>
  ...
  <Resource name="jdbc/EmployeeDB"
            auth="Container"
            type="javax.sql.DataSource"
            username="dbusername"
            password="dbpassword"
            driverClassName="org.hsql.jdbcDriver"
            url="jdbc:HypersonicSQL:database"
            maxTotal="8"
            maxIdle="4"/>
  ...
</Context>
```

请注意，资源名称（此处为`jdbc/EmployeeDB`）必须与Web应用程序部署描述符中指定的值匹配。

本示例假定您正在使用HypersonicSQL数据库JDBC驱动程序。自定义`driverClassName`和 `driverName`参数以匹配实际数据库的JDBC驱动程序和连接URL。

Tomcat的标准数据源资源工厂（`org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory`）的配置属性如下：

- **driverClassName-**要使用的JDBC驱动程序的标准Java类名称。
- **用户名** -要传递给我们的JDBC驱动程序的数据库用户名。
- **password-**要传递给我们的JDBC驱动程序的数据库密码。
- **url-**要传递给我们的JDBC驱动程序的连接URL。（为了向后兼容，`driverName` 还可以识别该属性。）
- **initialSize-**在池初始化期间将在池中创建的初始连接数。默认值：0
- **maxTotal-**可以同时从此池分配的最大连接数。默认值：8
- **minIdle-**同时在此池中处于空闲状态的最小连接数。默认值：0
- **maxIdle-**可以同时在此池中处于空闲状态的最大连接数。默认值：8
- **maxWaitMillis-**抛出异常之前，池将等待（没有可用连接时）连接返回的最大毫秒数。默认值：-1（无限）

一些其他属性可处理连接验证：

- **validationQuery-**池可用于在将连接返回给应用程序之前验证连接的SQL查询。如果指定，则此查询必须是返回至少一行的SQL SELECT语句。
- **validationQueryTimeout-**验证查询返回的超时（以秒为单位）。默认值：-1（无限）
- **testOnBorrow-**是或否：每次从池中借用连接时，是否应该使用验证查询来验证连接。默认值：true
- **testOnReturn-**是或否：每次将连接返回到池时是否应使用验证查询来验证连接。默认值：false

可选的退出线程负责通过删除任何长时间处于空闲状态的连接来缩小池。驱逐者不尊重`minIdle`。请注意，如果仅希望根据配置的`maxIdle`属性缩小池，则无需激活退出线程。

逐出器默认情况下处于禁用状态，可以使用以下属性进行配置：

- **timeBetweenEvictionRunsMillis-逐次**运行两次之间的毫秒数。默认值：-1（禁用）
- **numTestsPerEvictionRun-**在每次运行**驱逐程序时，驱逐**程序将检查连接是否空闲的连接数。默认值：3
- **minEvictableIdleTimeMillis-**空闲时间（以毫秒为单位），在此时间之后，退出者可以从池中删除连接。默认值：30 * 60 * 1000（30分钟）
- **testWhileIdle-**正确或错误：是否应该在空闲状态下使退出线程使用验证查询来验证连接。默认值：false

另一个可选功能是删除废弃的连接。如果应用程序长时间不将其返回到池中，则该连接称为放弃连接。池可以自动关闭此类连接并将其从池中删除。这是应用程序泄漏连接的一种解决方法。

默认情况下，放弃功能是禁用的，可以使用以下属性进行配置：

- **removeAbandonedOnBorrow-**正确或错误：借用连接时是否从池中删除废弃的连接。默认值：false
- **removeAbandonedOnMaintenance-**正确或错误：是否在池维护期间从池中删除废弃的连接。默认值：false
- **removeAbandonedTimeout-**假定借用的连接被放弃之前经过的秒数。默认值：300
- **logAbandoned-**正确或错误：是否为放弃语句或连接的应用程序代码记录堆栈跟踪。这增加了严重的开销。默认值：false

最后，还有各种属性可以对池行为进行进一步的微调：

- **defaultAutoCommit-**正确或错误：此池创建的连接的默认自动提交状态。默认值：true
- **defaultReadOnly-**正确或错误：此池创建的连接的默认只读状态。默认值：false
- **defaultTransactionIsolation-**设置默认事务隔离级别。可以是一个 `NONE`，`READ_COMMITTED`， `READ_UNCOMMITTED`，`REPEATABLE_READ`， `SERIALIZABLE`。默认值：未设置默认值
- **poolPreparedStatements** -true或false：是否**合并** PreparedStatements和CallableStatements。默认值：false
- **maxOpenPreparedStatements-**可以同时从语句池分配的最大打开语句数。默认值：-1（无限制）
- **defaultCatalog-**默认目录的名称。默认值：未设置
- **connectionInitSqls-**创建连接后，SQL语句列表将运行一次。用分号（`;`）分隔多个语句。默认值：无声明
- **connectionProperties-**传递给驱动程序以创建连接的特定于驱动程序的属性。每个属性均以形式给出`name=value`，多个属性之间用分号（`;`）分隔。默认值：无属性
- **accessToUnderlyingConnectionAllowed-**正确或错误：是否允许访问基础连接。默认值：false

有关更多详细信息，请参阅Commons DBCP 2文档。

### 添加自定义资源工厂

如果没有标准资源工厂可以满足您的需求，则可以编写自己的工厂并将其集成到Tomcat中，然后[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)在Web应用程序的元素中配置该工厂的使用 。在下面的示例中，我们将创建一个工厂，该工厂仅知道如何`com.mycompany.MyBean`从上面的[Generic JavaBean Resources](http://tomcat.apache.org/tomcat-9.0-doc/jndi-resources-howto.html#Generic_JavaBean_Resources)示例中创建bean 。

#### 1.编写资源工厂类

您必须编写一个实现JNDI服务提供者 `javax.naming.spi.ObjectFactory`接口的类。每次您的Web应用程序调用`lookup()`绑定到该工厂的上下文条目时（假设工厂配置有 `singleton="false"`），该 `getObjectInstance()`方法将被调用，并带有以下参数：

- **Object obj-**（可能为null）对象，其中包含可用于创建对象的位置或参考信息。对于Tomcat，这将始终是type类型的对象 `javax.naming.Reference`，其中包含此工厂类的类名称以及[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)用于创建要返回的对象的配置属性（来自 Web应用程序的）。
- **名称** name-相对于`nameCtx`，此工厂绑定到的`null`名称，或者如果未指定名称。
- **上下文名称Ctx-**相对**于其**`name`指定参数的上下文 ，或者`null`if `name`相对于默认初始上下文。
- **哈希表环境** -创建此对象时使用的环境（可能为null）。这通常在Tomcat对象工厂中被忽略。

要创建一个知道如何产生`MyBean` 实例的资源工厂，您可以创建一个这样的类：

```java
package com.mycompany;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

public class MyBeanFactory implements ObjectFactory {

  public Object getObjectInstance(Object obj,
      Name name2, Context nameCtx, Hashtable environment)
      throws NamingException {

      // Acquire an instance of our specified bean class
      MyBean bean = new MyBean();

      // Customize the bean properties from our attributes
      Reference ref = (Reference) obj;
      Enumeration addrs = ref.getAll();
      while (addrs.hasMoreElements()) {
          RefAddr addr = (RefAddr) addrs.nextElement();
          String name = addr.getType();
          String value = (String) addr.getContent();
          if (name.equals("foo")) {
              bean.setFoo(value);
          } else if (name.equals("bar")) {
              try {
                  bean.setBar(Integer.parseInt(value));
              } catch (NumberFormatException e) {
                  throw new NamingException("Invalid 'bar' value " + value);
              }
          }
      }

      // Return the customized instance
      return (bean);

  }

}
```

在此示例中，我们将无条件创建`com.mycompany.MyBean`该类的新实例，并根据`<ResourceParams>` 配置此工厂的元素中包含的参数填充其属性（请参见下文）。您应该注意，`factory`应该跳过任何名为的参数-该参数用于指定工厂类本身的名称（在本例中为 `com.mycompany.MyBeanFactory`），而不是所配置bean的属性。

有关的更多信息`ObjectFactory`，请参见 [JNDI服务提供者接口（SPI）规范](http://docs.oracle.com/javase/7/docs/technotes/guides/jndi/index.html)。

您将需要针对包含目录中所有JAR文件的类路径编译该类`$CATALINA_HOME/lib`。完成操作后，将工厂类（和相应的Bean类）解压缩后放在 `$CATALINA_HOME/lib`或JAR文件中 `$CATALINA_HOME/lib`。这样，Catalina内部资源和您的Web应用程序都可以看到所需的类文件。

#### 2.声明您的资源需求

接下来，修改您的Web应用程序部署描述符（`/WEB-INF/web.xml`），以声明JNDI名称，在该名称下您将请求该bean的新实例。最简单的方法是使用`<resource-env-ref>`元素，如下所示：

```xml
<resource-env-ref>
  <description>
    Object factory for MyBean instances.
  </description>
  <resource-env-ref-name>
    bean/MyBeanFactory
  </resource-env-ref-name>
  <resource-env-ref-type>
    com.mycompany.MyBean
  </resource-env-ref-type>
</resource-env-ref>
```

**警告** -确保您遵守DTD对于Web应用程序部署描述符所要求的元素排序！有关详细信息，请参见 [Servlet规范](https://wiki.apache.org/tomcat/Specifications)。

#### 3.编写您的应用程序对此资源的使用代码

该资源环境参考的典型用法如下所示：

```java
Context initCtx = new InitialContext();
Context envCtx = (Context) initCtx.lookup("java:comp/env");
MyBean bean = (MyBean) envCtx.lookup("bean/MyBeanFactory");

writer.println("foo = " + bean.getFoo() + ", bar = " +
               bean.getBar());
```

#### 4.配置Tomcat的资源工厂

要配置Tomcat的资源工厂，请[``](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)在此Web应用程序的元素中添加类似这样的 元素。

```xml
<Context ...>
  ...
  <Resource name="bean/MyBeanFactory" auth="Container"
            type="com.mycompany.MyBean"
            factory="com.mycompany.MyBeanFactory"
            singleton="false"
            bar="23"/>
  ...
</Context>
```

请注意，资源名称（此处`bean/MyBeanFactory` 必须与Web应用程序部署描述符中指定的值匹配。我们也在初始化`bar` 属性的值，这将导致`setBar(23)`在返回新bean之前调用该`foo`属性。因为我们没有初始化该 属性（尽管我们可以这样做），bean将包含其构造函数设置的任何默认值。

您还会注意到，从应用程序开发者的角度来看，资源环境引用的声明，和用于请求新实例编程，是相同的用于接近 *通用的JavaBean资源*的例子。这说明了使用JNDI资源来封装功能的优势之一-你可以改变底层实现，而不必修改使用资源的应用程序，只要你保持兼容的API。