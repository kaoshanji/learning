# 7.领域和访问控制

### 快速开始

本文档介绍了如何通过连接到用户名，密码和用户角色的现有“数据库” 来配置Tomcat以支持*容器管理的安全性*。如果使用的Web应用程序包含一个或多个`<security-constraint>`元素，并且该`<login-config>`元素定义了要求用户如何进行身份验证的元素， 则只需关心这一点 。如果您没有使用这些功能，则可以安全地跳过此文档。

有关容器管理的安全性的基本背景信息，请参见[Servlet规范（版本2.4）的](https://wiki.apache.org/tomcat/Specifications)第12节。

有关使用Tomcat 的*单一登录*功能（允许用户在与虚拟主机相关联的整个Web应用程序集中进行一次身份验证）的信息，请参见 [此处](http://tomcat.apache.org/tomcat-9.0-doc/config/host.html#Single_Sign_On)。

### 总览

#### 什么是境界？

一个**领域**是一个用户名和标识Web应用程序的有效用户（或一组Web应用程序）的密码“数据库”，加上名单的枚举*角色*与每个有效用户相关联。你可以把角色相似的*群体*在类Unix的操作系统，因为访问特定的Web应用程序资源被授予具有特定角色（而不是列举相关的用户名的列表）的所有用户。特定用户可以具有与其用户名关联的任意数量的角色。

尽管Servlet规范描述了一种可移植的机制，供应用程序*声明*其安全要求（在 `web.xml`部署描述符中），但尚无可移植的API定义Servlet容器与关联的用户和角色信息之间的接口。但是，在许多情况下，希望将servlet容器“连接”到生产环境中已经存在的某些现有身份验证数据库或机制。因此，Tomcat定义了一个Java接口（`org.apache.catalina.Realm`），该接口可以通过“插入”组件来实现以建立此连接。提供了六个标准插件，支持与各种身份验证信息源的连接：

- [JDBCRealm-](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#JDBCRealm)访问存储在关系数据库中的身份验证信息，可通过JDBC驱动程序进行访问。
- [DataSourceRealm-](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#DataSourceRealm)访问存储在关系数据库中的身份验证信息，可通过命名的JNDI JDBC DataSource访问。
- [JNDIRealm-](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#JNDIRealm)访问通过JNDI提供程序访问的，存储在基于LDAP的目录服务器中的身份验证信息。
- [UserDatabaseRealm-](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#UserDatabaseRealm)访问存储在UserDatabase JNDI资源中的身份验证信息，该信息通常由XML文档（`conf/tomcat-users.xml`）支持。
- [MemoryRealm-](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#MemoryRealm)访问存储在内存中对象集中的身份验证信息，该信息是从XML文档（`conf/tomcat-users.xml`）初始化的。
- [JAASRealm-](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#JAASRealm)通过Java认证和授权服务（JAAS）框架访问认证信息。

也可以编写自己的`Realm`实现，并将其与Tomcat集成。为此，您需要：

- 实施`org.apache.catalina.Realm`，
- 将已编译的领域放置在$ CATALINA_HOME / lib中，
- 按照下面的“配置领域”部分所述声明您的领域，
- 向[MBeans描述符](http://tomcat.apache.org/tomcat-9.0-doc/mbeans-descriptors-howto.html)声明您的领域。

#### 配置领域

在深入了解标准Realm实现之前，重要的是要通俗地理解Realm的配置方式。通常，您将在`conf/server.xml` 配置文件中添加一个XML元素，如下所示：

```
<Realm className="... class name for this implementation"
       ... other attributes for this implementation .../>
```

该`<Realm>`元素可以嵌套在以下任何`Container`元素中。Realm元素的位置直接影响该Realm的“范围”（即哪些Web应用程序将共享相同的身份验证信息）：

- *在<Engine>元素内* -此领域将在所有虚拟主机上的所有Web应用程序之间共享，除非被嵌套在下属`<Host>` 或`<Context>`元素内的Realm元素覆盖。
- *在<Host>元素内* -此Realm将在此虚拟主机的所有Web应用程序之间共享，除非被嵌套在从属`<Context>` 元素内的Realm元素覆盖。
- *在<Context>元素内* -此领域仅用于此Web应用程序。

### 共同特征

#### 摘要密码

对于每个标准`Realm`实现，用户密码（默认情况下）均以明文形式存储。在许多环境中，这是不希望的，因为身份验证数据的临时观察者可以收集足够的信息以成功登录并模拟其他用户。为了避免这个问题，标准的实现支持的概念*消化* 用户密码。这样就可以对存储的密码版本进行编码（以不容易逆转的形式），但是该 `Realm`实现仍可以用于身份验证。

当标准领域通过检索存储的密码并将其与用户提供的值进行身份验证时，可以通过将元素放置在元素中来选择摘要密码 。支持SSHA，SHA或MD5算法之一的简单选择是使用。必须将此元素配置为类支持的摘要算法之一（SSHA，SHA或MD5）。选择此选项时，密码中存储的密码内容必须是指定算法提取的密码的明文版本。[ `CredentialHandler`](http://tomcat.apache.org/tomcat-9.0-doc/config/credentialhandler.html)`<Realm>``MessageDigestCredentialHandler``java.security.MessageDigest``Realm`

`authenticate()`调用Realm 的方法时，用户指定的（明文）密码本身会被相同的算法提取，并将结果与所返回的值进行比较 `Realm`。相等匹配表示原始密码的明文版本与用户提供的密码相同，因此应授权该用户。

要计算明文密码的摘要值，支持两种便捷技术：

- 如果要编写需要动态计算摘要密码的应用程序，请调用该类的静态`Digest()`方法 `org.apache.catalina.realm.RealmBase`，并将纯文本密码，摘要算法名称和编码作为参数传递。此方法将返回摘要密码。

- 如果要执行命令行实用工具来计算摘要密码，只需执行

  ```
  CATALINA_HOME/bin/digest.[bat|sh] -a {algorithm} {cleartext-password}
  ```

  并且此明文密码的摘要版本将返回到标准输出。

如果将摘要密码与DIGEST身份验证一起使用，则用于生成摘要的明文会有所不同，摘要必须使用MD5算法的一次迭代，且不能添加盐。在以上示例中， `{cleartext-password}`必须用替换 `{username}:{realm}:{cleartext-password}`。例如，在开发环境中，它可以采用形式 `testUser:Authentication required:testPassword`。的值 `{realm}`取自`<realm-name>` Web应用程序的元素`<login-config>`。如果未在web.xml中指定，`Authentication required`则使用默认值。

支持使用非平台默认编码的用户名和/或密码，使用

```
CATALINA_HOME/bin/digest.[bat|sh] -a {algorithm} -e {encoding} {input}
```

但需要注意确保将输入正确传递到摘要器。消化器返回`{input}:{digest}`。如果输入在返回中显示为损坏，则摘要将无效。

摘要的输出格式为`{salt}${iterations}${digest}`。如果盐长度为零且迭代计数为1，则输出简化为`{digest}`。

的完整语法`CATALINA_HOME/bin/digest.[bat|sh]`为：

```bash
CATALINA_HOME/bin/digest.[bat|sh] [-a <algorithm>] [-e <encoding>]
        [-i <iterations>] [-s <salt-length>] [-k <key-length>]
        [-h <handler-class-name>] <credentials>
```

- **-a-**用于生成存储的凭证的算法。如果未指定，将使用处理程序的默认值。如果未指定处理程序或算法，`SHA-512`则将使用默认值
- **-e-**可能需要用于任何字节到字符转换或从字符转换的编码。如果未指定，将使用系统编码（`Charset#defaultCharset()`）。
- **-i-**生成存储的凭证时要使用的迭代次数。如果未指定，将使用CredentialHandler的默认值。
- **-s-**生成和存储为凭证一部分的盐的长度（以字节为单位）。如果未指定，将使用CredentialHandler的默认值。
- **-k-**生成证书时创建的密钥的长度（以位为单位）（如果有）。如果未指定，将使用CredentialHandler的默认值。
- **-h-**要使用的CredentialHandler的完全限定的类名。如果未指定，则将依次测试内置处理程序（MessageDigestCredentialHandler然后SecretKeyCredentialHandler），并且将使用第一个接受指定算法的处理程序。

#### 应用范例

Tomcat附带的示例应用程序包括一个区域，该区域利用基于表单的登录受到安全约束的保护。要访问它，请将浏览器指向 [http：// localhost：8080 / examples / jsp / security / protected /](http://localhost:8080/examples/jsp/security/protected/) 并使用为默认[UserDatabaseRealm](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#UserDatabaseRealm)描述的用户名和密码之一登录 。

#### 经理申请

如果希望使用[Manager应用](http://tomcat.apache.org/tomcat-9.0-doc/manager-howto.html) 程序在正在运行的Tomcat安装中部署和取消部署应用程序，则必须在所选的Realm实现中，将“ manager-gui”角色至少添加到一个用户名中。这是因为管理器Web应用程序本身使用安全约束，该安全约束要求角色“ manager-gui”访问该应用程序的HTML界面内的任何请求URI。

出于安全考虑，默认Realm中没有用户名（即，使用 `conf/tomcat-users.xml`分配了“ manager-gui”角色）。因此，直到Tomcat管理员专门将此角色分配给一个或多个用户之前，任何人都无法使用此应用程序的功能。 。

#### 领域记录

由a记录的调试消息和异常消息将由`Realm`与该领域的容器相关联的日志记录配置记录：其周围的[Context](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)， [Host](http://tomcat.apache.org/tomcat-9.0-doc/config/host.html)或 [Engine](http://tomcat.apache.org/tomcat-9.0-doc/config/engine.html)。

### 标准领域实现

#### JDBC领域

##### 介绍

**JDBCRealm**是Tomcat `Realm`接口的实现，该 接口在通过JDBC驱动程序访问的关系数据库中查找用户。只要您的数据库结构符合以下要求，就有很大的配置灵活性，可以使您适应现有的表名和列名：

- 必须有一个表，下面称为*用户*表，该表包含`Realm` 应该识别的每个有效用户的一行。

- 在

  用户

  表必须包含至少两列（它可能包含更多的如果你的程序需要它）：

  - Tomcat在用户登录时可以识别的用户名。
  - 用户登录时Tomcat可以识别的密码。该值可以是明文形式或摘要形式-有关更多信息，请参见下文。

- 必须有一个表，下面称为*用户角色*表，其中包含分配给特定用户的每个有效角色的一行。用户具有零个，一个或多个有效角色是合法的。

- 该

  用户角色

  表格必须包括至少两列（它可能包含更多的如果你的程序需要它）：

  - Tomcat可以识别的用户名（与*用户*表中指定的值相同）。
  - 与此用户关联的有效角色的角色名称。

##### 快速开始

要将Tomcat设置为使用JDBCRealm，您将需要执行以下步骤：

1. 如果尚未这样做，请在数据库中创建符合上述要求的表和列。
2. 配置供Tomcat使用的数据库用户名和密码，该用户名和密码至少具有对上述表的只读访问权限。（Tomcat绝不会尝试写入这些表。）
3. 将要使用的JDBC驱动程序的副本放在 `$CATALINA_HOME/lib`目录中。请注意，**只能**识别JAR文件！
4. `<Realm>`如下所述，在`$CATALINA_BASE/conf/server.xml`文件中设置一个元素 。
5. 如果Tomcat已在运行，请重新启动它。

##### 领域元素属性

要配置JDBCRealm，你将创建一个`<Realm>` 在你的元素和巢它`$CATALINA_BASE/conf/server.xml`的文件，如描述[上面](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#Configuring_a_Realm)。JDBCRealm的属性在[Realm](http://tomcat.apache.org/tomcat-9.0-doc/config/realm.html)配置文档中定义。

##### 例

创建所需表的示例SQL脚本可能类似于以下内容（根据特定数据库的需要调整语法）：

```sql
create table users (
  user_name         varchar(15) not null primary key,
  user_pass         varchar(15) not null
);

create table user_roles (
  user_name         varchar(15) not null,
  role_name         varchar(15) not null,
  primary key (user_name, role_name)
);
```

示例`Realm`元素包含（注释掉）在默认`$CATALINA_BASE/conf/server.xml`文件中。这是一个使用名为“ authority”的MySQL数据库的示例，该数据库配置有上述表，并通过用户名“ dbuser”和密码“ dbpass”进行访问：

```
<Realm className="org.apache.catalina.realm.JDBCRealm"
      driverName="org.gjt.mm.mysql.Driver"
   connectionURL="jdbc:mysql://localhost/authority?user=dbuser&amp;password=dbpass"
       userTable="users" userNameCol="user_name" userCredCol="user_pass"
   userRoleTable="user_roles" roleNameCol="role_name"/>
```

##### 补充笔记

JDBCRealm根据以下规则进行操作：

- 当用户首次尝试访问受保护的资源时，Tomcat将调用`authenticate()`this 的方法 `Realm`。因此，您直接对数据库所做的任何更改（新用户，更改的密码或角色等）都将立即反映出来。
- 验证用户身份后，将在用户登录期间将用户（及其关联角色）缓存在Tomcat中。（对于基于FORM的身份验证，这意味着直到会话超时或无效；对于BASIC身份验证，这意味着直到用户关闭其浏览器）。缓存的用户**不会**在会话序列化中保存和还原。对于已经通过身份验证的用户，数据库信息的任何更改都**不会**反映，直到该用户下次再次登录。
- 管理*用户*和*用户角色* 表中的信息是您自己的应用程序的责任。Tomcat不提供任何内置功能来维护用户和角色。

#### 数据源领域

##### 介绍

**DataSourceRealm**是Tomcat `Realm`接口的实现，该 接口在通过名为JDBC DataSource的JNDI访问的关系数据库中查找用户。只要您的数据库结构符合以下要求，就有很大的配置灵活性，可以使您适应现有的表名和列名：

- 必须有一个表，下面称为*用户*表，该表包含`Realm` 应该识别的每个有效用户的一行。

- 在

  用户

  表必须包含至少两列（它可能包含更多的如果你的程序需要它）：

  - Tomcat在用户登录时可以识别的用户名。
  - 用户登录时Tomcat可以识别的密码。该值可以是明文形式或摘要形式-有关更多信息，请参见下文。

- 必须有一个表，下面称为*用户角色*表，其中包含分配给特定用户的每个有效角色的一行。用户具有零个，一个或多个有效角色是合法的。

- 该

  用户角色

  表格必须包括至少两列（它可能包含更多的如果你的程序需要它）：

  - Tomcat可以识别的用户名（与*用户*表中指定的值相同）。
  - 与此用户关联的有效角色的角色名称。

##### 快速开始

要将Tomcat设置为使用DataSourceRealm，您将需要执行以下步骤：

1. 如果尚未这样做，请在数据库中创建符合上述要求的表和列。
2. 配置供Tomcat使用的数据库用户名和密码，该用户名和密码至少具有对上述表的只读访问权限。（Tomcat绝不会尝试写入这些表。）
3. 为数据库配置一个名为JDBC DataSource的JNDI。有关如何配置名为JDBC DataSource的JNDI的信息，请参考 [JNDI DataSource示例方法](http://tomcat.apache.org/tomcat-9.0-doc/jndi-datasource-examples-howto.html)。确保 根据JNDI数据源的定义位置适当地设置`Realm`的`localDataSource`属性。
4. `<Realm>`如下所述，在`$CATALINA_BASE/conf/server.xml`文件中设置一个元素 。
5. 如果Tomcat已在运行，请重新启动它。

##### 领域元素属性

要配置DataSourceRealm，将[如上所述](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#Configuring_a_Realm)创建一个`<Realm>` 元素并将其嵌套在`$CATALINA_BASE/conf/server.xml`文件中。DataSourceRealm的属性在[Realm](http://tomcat.apache.org/tomcat-9.0-doc/config/realm.html) 配置文档中定义。

##### 例

创建所需表的示例SQL脚本可能类似于以下内容（根据特定数据库的需要调整语法）：

```sql
create table users (
  user_name         varchar(15) not null primary key,
  user_pass         varchar(15) not null
);

create table user_roles (
  user_name         varchar(15) not null,
  role_name         varchar(15) not null,
  primary key (user_name, role_name)
);
```

这是一个使用名为“ authority”的MySQL数据库的示例，该数据库已通过上述表进行配置，并通过名称为“ java：/ comp / env / jdbc / authority”的JNDI JDBC数据源进行访问。

```xml
<Realm className="org.apache.catalina.realm.DataSourceRealm"
   dataSourceName="jdbc/authority"
   userTable="users" userNameCol="user_name" userCredCol="user_pass"
   userRoleTable="user_roles" roleNameCol="role_name"/>
```

##### 补充笔记

DataSourceRealm根据以下规则进行操作：

- 当用户首次尝试访问受保护的资源时，Tomcat将调用`authenticate()`this 的方法 `Realm`。因此，您直接对数据库所做的任何更改（新用户，更改的密码或角色等）都将立即反映出来。
- 验证用户身份后，将在用户登录期间将用户（及其关联角色）缓存在Tomcat中。（对于基于FORM的身份验证，这意味着直到会话超时或无效；对于BASIC身份验证，这意味着直到用户关闭其浏览器）。缓存的用户**不会**在会话序列化中保存和还原。对于已经通过身份验证的用户，数据库信息的任何更改都**不会**反映，直到该用户下次再次登录。
- 管理*用户*和*用户角色* 表中的信息是您自己的应用程序的责任。Tomcat不提供任何内置功能来维护用户和角色。

#### JNDIRealm

##### 介绍

**JNDIRealm**是Tomcat `Realm`接口的实现，该 接口在JNDI提供程序（通常是JNDI API类提供的标准LDAP提供程序）访问的LDAP目录服务器中查找用户。该领域支持使用目录进行身份验证的多种方法。

###### 连接到目录

领域与目录的连接由**connectionURL**配置属性定义 。这是一个URL，其格式由JNDI提供程序定义。通常，它是一个LDAP URL，它指定要连接到的目录服务器的域名，还可以指定所需的根命名上下文的端口号和专有名称（DN）。

如果您有多个提供程序，则可以配置 **AlternativeURL**。如果一个套接字连接不能对供应商在做**的ConnectionURL**的将尝试使用**alternateURL**。

进行连接以搜索目录并检索用户和角色信息时，领域将使用由**connectionName**和 **connectionPassword**属性指定的用户名和密码对目录进行身份验证 。如果未指定这些属性，则连接为匿名。在许多情况下，这就足够了。

###### 选择用户的目录条目

每个可以通过身份验证的用户必须在目录中用一个单独的条目表示，该条目对应于`DirContext`由**connectionURL**属性定义 的初始元素。该用户条目必须具有包含用于身份验证的用户名的属性。

用户条目的可分辨名称通常包含用于身份验证的用户名，但对于所有用户而言都是相同的。在这种情况下，可以使用**userPattern**属性指定DN，并用“ {0}”标记应替换用户名的位置。

否则，领域必须搜索目录以找到包含用户名的唯一条目。以下属性配置此搜索：

- **userBase-**作为包含用户的子树的基础的条目。如果未指定，则搜索基础是顶级上下文。
- **userSubtree-**搜索范围。`true`如果希望搜索以**userBase**条目为根的整个子树，则设置为 。默认值是`false`请求仅包括顶层的单层搜索。
- **userSearch-**指定替换用户名后要使用的LDAP搜索过滤器的模式。

###### 验证用户

- **绑定模式**

  默认情况下，领域通过使用该用户条目的DN和该用户提供的密码绑定到目录来对用户进行身份验证。如果此简单绑定成功，则认为用户已通过身份验证。

  出于安全原因，目录可能存储用户密码的摘要，而不是明文版本（有关更多信息，请参见摘要 [密码](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#Digested_Passwords)）。在这种情况下，作为简单绑定操作的一部分，目录在根据存储的值进行验证之前，会自动计算用户提供的明文密码的正确摘要。因此，在绑定模式下，领域不参与摘要处理。本 **摘要**属性不使用，如果设置将被忽略。

- **比较模式**

  或者，领域可以从目录中检索存储的密码，并将其与用户提供的值进行显式比较。通过将**userPassword**属性设置为包含密码的用户条目中目录属性的名称来配置此模式 。

  比较模式有一些缺点。首先， 必须配置**connectionName**和 **connectionPassword**属性，以允许领域读取目录中用户的密码。出于安全原因，通常不希望这样做；实际上，许多目录实现甚至都不允许目录管理器读取这些密码。此外，领域必须自己处理密码摘要，包括使用的算法的变化形式以及在目录中表示密码哈希的方式。但是，该领域有时可能需要访问存储的密码，例如，以支持HTTP摘要访问身份验证（RFC 2069）。（请注意，如上所述，HTTP摘要认证不同于密码摘要在用户信息存储库中的存储）。

###### 为用户分配角色

目录领域支持两种方法来表示目录中的角色：

- **充当显式目录条目**

  角色可以由显式目录条目表示。角色条目通常是LDAP组条目，其一个属性包含角色名称，另一个属性值包含该角色中用户的专有名称或用户名。以下属性配置目录搜索以查找与已认证用户关联的角色的名称：

  - **roleBase-**角色搜索的基本条目。如果未指定，则搜索基础是顶级目录上下文。
  - **roleSubtree-**搜索范围。`true`如果要搜索以该`roleBase`条目为根的整个子树，则设置为。默认值是`false`请求单级搜索，仅包括顶级。
  - **roleSearch-**用于选择角色条目的LDAP搜索过滤器。它可选地包括已认证用户的模式替换“ {0}”（用于专有名称）和/或“ {1}”（用于用户名）和/或“ {2}”（用于来自用户目录条目的属性）。使用**userRoleAttribute**指定提供“ {2}”值的属性的名称。
  - **roleName-**角色条目中的属性，其中包含该角色的名称。
  - **roleNested-**启用嵌套角色。`true`如果要在角色中嵌套角色，则设置为 。如果已配置，则将递归地尝试每个新发现的roleName和专有名称，以进行新的角色搜索。默认值为`false`。

- **角色作为用户条目的属性**

  角色名称也可以作为属性的值保存在用户的目录条目中。使用**userRoleName**指定此属性的名称。

可以使用两种方法来组合角色表示。

##### 快速开始

要将Tomcat设置为使用JNDIRealm，您将需要执行以下步骤：

1. 确保您的目录服务器配置了符合上述要求的架构。
2. 如果需要，请配置用户名和密码以供Tomcat使用，该用户名和密码具有对上述信息的只读访问权限。（Tomcat绝不会尝试修改此信息。）
3. `<Realm>`如下所述，在`$CATALINA_BASE/conf/server.xml`文件中设置一个元素 。
4. 如果Tomcat已在运行，请重新启动它。

##### 领域元素属性

要配置JNDIRealm，您将创建一个`<Realm>` 在你的元素和巢它`$CATALINA_BASE/conf/server.xml`的文件，如描述[上面](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#Configuring_a_Realm)。JNDIRealm的属性在[Realm](http://tomcat.apache.org/tomcat-9.0-doc/config/realm.html)配置文档中定义。

##### 例

在目录服务器中创建适当的模式超出了本文档的范围，因为它对于每个目录服务器实现都是唯一的。在下面的示例中，我们假设您使用的是OpenLDAP目录服务器（版本2.0.11或更高版本），可以从[https://www.openldap.org](https://www.openldap.org/)下载 。假设您的`slapd.conf`文件包含以下设置（以及其他设置）：

```bash
database ldbm
suffix dc="mycompany",dc="com"
rootdn "cn=Manager,dc=mycompany,dc=com"
rootpw secret
```

我们将假定`connectionURL`目录服务器与Tomcat在同一台计算机上运行。 有关配置和使用JNDI LDAP提供程序的更多信息，请参见[ http://docs.oracle.com/javase/7/docs/technotes/guides/jndi/index.html](http://docs.oracle.com/javase/7/docs/technotes/guides/jndi/index.html)。

接下来，假定此目录服务器已填充如下所示的元素（采用LDIF格式）：

```
# Define top-level entry
dn: dc=mycompany,dc=com
objectClass: dcObject
dc:mycompany

# Define an entry to contain people
# searches for users are based on this entry
dn: ou=people,dc=mycompany,dc=com
objectClass: organizationalUnit
ou: people

# Define a user entry for Janet Jones
dn: uid=jjones,ou=people,dc=mycompany,dc=com
objectClass: inetOrgPerson
uid: jjones
sn: jones
cn: janet jones
mail: j.jones@mycompany.com
userPassword: janet

# Define a user entry for Fred Bloggs
dn: uid=fbloggs,ou=people,dc=mycompany,dc=com
objectClass: inetOrgPerson
uid: fbloggs
sn: bloggs
cn: fred bloggs
mail: f.bloggs@mycompany.com
userPassword: fred

# Define an entry to contain LDAP groups
# searches for roles are based on this entry
dn: ou=groups,dc=mycompany,dc=com
objectClass: organizationalUnit
ou: groups

# Define an entry for the "tomcat" role
dn: cn=tomcat,ou=groups,dc=mycompany,dc=com
objectClass: groupOfUniqueNames
cn: tomcat
uniqueMember: uid=jjones,ou=people,dc=mycompany,dc=com
uniqueMember: uid=fbloggs,ou=people,dc=mycompany,dc=com

# Define an entry for the "role1" role
dn: cn=role1,ou=groups,dc=mycompany,dc=com
objectClass: groupOfUniqueNames
cn: role1
uniqueMember: uid=fbloggs,ou=people,dc=mycompany,dc=com
```

`Realm`如上所述配置的OpenLDAP目录服务器的示例元素可能看起来像这样，假设用户使用其uid（例如jjones）登录到应用程序，并且匿名连接足以搜索目录并检索角色信息：

```xml
<Realm   className="org.apache.catalina.realm.JNDIRealm"
     connectionURL="ldap://localhost:389"
       userPattern="uid={0},ou=people,dc=mycompany,dc=com"
          roleBase="ou=groups,dc=mycompany,dc=com"
          roleName="cn"
        roleSearch="(uniqueMember={0})"
/>
```

使用此配置，领域将通过将用户名替换为来确定用户的专有名称 `userPattern`，通过使用此DN和从用户收到的密码绑定到目录进行身份验证，然后搜索目录以查找用户的角色。

现在假设希望用户在登录时输入其电子邮件地址而不是用户ID。在这种情况下，领域必须在目录中搜索用户的条目。（当用户条目保存在可能对应于不同组织单位或公司位置的多个子树中时，搜索也是必要的）。

此外，假设除了组条目外，您还想使用用户条目的属性来担任角色。现在，Janet Jones的条目可能如下所示：

```
dn: uid=jjones,ou=people,dc=mycompany,dc=com
objectClass: inetOrgPerson
uid: jjones
sn: jones
cn: janet jones
mail: j.jones@mycompany.com
memberOf: role2
memberOf: role3
userPassword: janet
```

此领域配置将满足新的要求：

```xml
<Realm   className="org.apache.catalina.realm.JNDIRealm"
     connectionURL="ldap://localhost:389"
          userBase="ou=people,dc=mycompany,dc=com"
        userSearch="(mail={0})"
      userRoleName="memberOf"
          roleBase="ou=groups,dc=mycompany,dc=com"
          roleName="cn"
        roleSearch="(uniqueMember={0})"
/>
```

现在，当珍妮特·琼斯（Janet Jones）以“ j.jones@mycompany.com”身份登录时，该领域在目录中搜索具有该值作为其邮件属性的唯一条目，并尝试`uid=jjones,ou=people,dc=mycompany,dc=com`使用给定的密码绑定到该目录 。如果身份验证成功，则会为她分配三个角色：“ role2”和“ role3”，这是她的目录条目中“ memberOf”属性的值；以及“ tomcat”，这是该目录唯一组条目中“ cn”属性的值。她是会员。

最后，要通过从目录中检索密码并在领域中进行本地比较来验证用户身份，可以使用如下领域配置：

```xml
<Realm   className="org.apache.catalina.realm.JNDIRealm"
    connectionName="cn=Manager,dc=mycompany,dc=com"
connectionPassword="secret"
     connectionURL="ldap://localhost:389"
      userPassword="userPassword"
       userPattern="uid={0},ou=people,dc=mycompany,dc=com"
          roleBase="ou=groups,dc=mycompany,dc=com"
          roleName="cn"
        roleSearch="(uniqueMember={0})"
/>
```

然而，如上所述，通常优选用于认证的默认绑定模式。

##### 补充笔记

JNDIRealm根据以下规则进行操作：

- 当用户首次尝试访问受保护的资源时，Tomcat将调用`authenticate()`this 的方法 `Realm`。因此，您对目录所做的任何更改（新用户，更改的密码或角色等）都将立即反映出来。
- 验证用户身份后，将在用户登录期间将用户（及其关联角色）缓存在Tomcat中。（对于基于FORM的身份验证，这意味着直到会话超时或无效；对于BASIC身份验证，这意味着直到用户关闭其浏览器）。缓存的用户**不会**在会话序列化中保存和还原。对于已经过身份验证的用户，目录信息的任何更改将**不会**反映，直到该用户下次再次登录。
- 在目录服务器中管理信息是您自己的应用程序的责任。Tomcat不提供任何内置功能来维护用户和角色。

#### UserDatabaseRealm

##### 介绍

**UserDatabaseRealm**是Tomcat `Realm`接口的实现，该 接口使用JNDI资源存储用户信息。默认情况下，JNDI资源由XML文件支持。它不适用于大规模生产。在启动时，UserDatabaseRealm从XML文档中加载有关所有用户及其相应角色的信息（默认情况下，该文档是从加载的 `$CATALINA_BASE/conf/tomcat-users.xml`）。用户，他们的密码及其角色都可以动态地进行编辑，通常是通过JMX进行。更改可能会保存并反映在XML文件中。

##### 领域元素属性

要配置UserDatabaseRealm，您将[如上所述](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#Configuring_a_Realm)创建一个`<Realm>` 元素并将其嵌套在`$CATALINA_BASE/conf/server.xml`文件中。UserDatabaseRealm的属性在[Realm](http://tomcat.apache.org/tomcat-9.0-doc/config/realm.html) 配置文档中定义。

##### 用户文件格式

用户文件使用与[MemoryRealm](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#MemoryRealm)相同的格式 。

##### 例

Tomcat的默认安装是通过在`<Engine>`元素内嵌套的UserDatabaseRealm配置的，因此它适用于所有虚拟主机和Web应用程序。该`conf/tomcat-users.xml`文件的默认内容 是：

```xml
<tomcat-users>
  <user username="tomcat" password="tomcat" roles="tomcat" />
  <user username="role1"  password="tomcat" roles="role1"  />
  <user username="both"   password="tomcat" roles="tomcat,role1" />
</tomcat-users>
```

##### 补充笔记

UserDatabaseRealm根据以下规则进行操作：

- Tomcat首次启动时，它将从用户文件中加载所有定义的用户及其相关信息。在重新启动Tomcat之前，**无法**识别对此文件中的数据所做的更改。可以通过UserDatabase资源进行更改。Tomcat为此提供了可通过JMX访问的MBean。
- 当用户首次尝试访问受保护的资源时，Tomcat将调用`authenticate()`this 的方法 `Realm`。
- 验证用户身份后，将在用户登录期间将用户（及其关联角色）缓存在Tomcat中。（对于基于FORM的身份验证，这意味着直到会话超时或无效；对于BASIC身份验证，这意味着直到用户关闭其浏览器）。缓存的用户**不会**在会话序列化中保存和还原。

#### 内存领域

##### 介绍

**MemoryRealm**是Tomcat `Realm`接口的简单演示实现。它不是设计用于生产用途。在启动时，MemoryRealm从XML文档中加载有关所有用户及其相应角色的信息（默认情况下，该文档是从加载的`$CATALINA_BASE/conf/tomcat-users.xml`）。在重新启动Tomcat之前，无法识别对此文件中数据的更改。

##### 领域元素属性

要配置MemoryRealm，您将[如上所述](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#Configuring_a_Realm)创建一个`<Realm>` 元素并将其嵌套在`$CATALINA_BASE/conf/server.xml`文件中。MemoryRealm的属性在[Realm](http://tomcat.apache.org/tomcat-9.0-doc/config/realm.html) 配置文档中定义。

##### 用户文件格式

用户文件（默认情况下，`conf/tomcat-users.xml`必须是带有根元素的XML文档`<tomcat-users>`。嵌套在根元素内的将是`<user>`每个有效用户的元素，由以下属性组成：

- name-该用户必须登录的用户**名**。
- **password-**此用户必须使用的登录密码（如果`digest`未在`<Realm>`元素上设置属性， 则为明文，否则将按[此处](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html#Digested_Passwords)所述进行适当摘要）。
- **角色** -与此用户关联的角色名称的逗号分隔列表。

##### 补充笔记

MemoryRealm根据以下规则进行操作：

- Tomcat首次启动时，它将从用户文件中加载所有定义的用户及其相关信息。在重新启动Tomcat之前，将**无法**识别对此文件中数据的更改。
- 当用户首次尝试访问受保护的资源时，Tomcat将调用`authenticate()`this 的方法 `Realm`。
- 验证用户身份后，将在用户登录期间将用户（及其关联角色）缓存在Tomcat中。（对于基于FORM的身份验证，这意味着直到会话超时或无效；对于BASIC身份验证，这意味着直到用户关闭其浏览器）。缓存的用户**不会**在会话序列化中保存和还原。
- 管理用户文件中的信息是您的应用程序的责任。Tomcat不提供任何内置功能来维护用户和角色。

#### JAASRealm

##### 介绍

**JAASRealm**是Tomcat `Realm`接口的实现，该 接口通过Java身份验证和授权服务（JAAS）框架对用户进行身份验证，该框架现在作为标准Java SE API的一部分提供。

使用JAASRealm，使开发人员能够将几乎所有可能的安全领域与Tomcat的CMA相结合。

JAASRealm是基于[JCP规范请求196](https://www.jcp.org/en/jsr/detail?id=196)的基于JAAS的J2EE v1.4的基于JAAS的J2EE身份验证框架的Tomcat的原型，以增强容器管理的安全性并促进实现与容器无关的“可插拔”身份验证机制。

基于JAAS登录模块和主体（请参阅`javax.security.auth.spi.LoginModule` 和`javax.security.Principal`），您可以开发自己的安全性机制或包装另一种第三方机制，以与Tomcat实施的CMA集成。

##### 快速开始

要将Tomcat设置为将JAASRealm与您自己的JAAS登录模块一起使用，您将需要执行以下步骤：

1. 编写一个基于JAAS（见自己的LoginModule，用户和角色等级 [的JAAS认证教程](http://docs.oracle.com/javase/7/docs/technotes/guides/security/jaas/tutorials/GeneralAcnOnly.html)，并 [在JAAS登录模块开发人员指南](http://docs.oracle.com/javase/7/docs/technotes/guides/security/jaas/JAASLMDevGuide.html)）由JAAS登录上下文（管理`javax.security.auth.login.LoginContext`）当开发你的LoginModule，注意JAASRealm的内置`CallbackHandler` 只能识别`NameCallback`和`PasswordCallback`目前。
2. 尽管没有在JAAS中指定，但是您应该创建单独的类来区分用户和角色，并扩展`javax.security.Principal`，以便Tomcat可以确定从登录模块返回的Principal是用户，而角色是角色（请参阅参考资料`org.apache.catalina.realm.JAASRealm`）。无论如何，返回的第一个委托人*始终*被视为用户委托人。
3. 将已编译的类放在Tomcat的类路径上
4. 为Java设置一个login.config文件（请参阅[ JAAS LoginConfig文件](http://docs.oracle.com/javase/7/docs/technotes/guides/security/jaas/tutorials/LoginConfigFile.html)），并通过指定其在JVM中的位置（例如通过设置环境变量）来告诉Tomcat在哪里找到它：`JAVA_OPTS=$JAVA_OPTS -Djava.security.auth.login.config==$CATALINA_BASE/conf/jaas.config`
5. 在web.xml中为要保护的资源配置安全约束
6. 在server.xml中配置JAASRealm模块
7. 如果Tomcat已在运行，请重新启动它。

##### 领域元素属性

要按照上述第6步配置JAASRealm，请创建一个`<Realm>`元素并将其嵌套在 节点`$CATALINA_BASE/conf/server.xml` 内的文件中`<Engine>`。JAASRealm的属性在[Realm](http://tomcat.apache.org/tomcat-9.0-doc/config/realm.html) 配置文档中定义。

##### 例

这是一个有关server.xml代码片段外观的示例。

```xml
<Realm className="org.apache.catalina.realm.JAASRealm"
                appName="MyFooRealm"
    userClassNames="org.foobar.realm.FooUser"
     roleClassNames="org.foobar.realm.FooRole"/>
```

登录模块负责创建和保存代表用户（`javax.security.auth.Subject`）的委托人的用户和角色对象。如果您的登录模块未创建用户对象，但也未引发登录异常，则Tomcat CMA将中断，您将留在http：// localhost：8080 / myapp / j_security_check URI或其他某个位置未指定位置。

JAAS方法的灵活性有两个方面：

- 您可以在自己的登录模块中进行后台所需的任何处理。
- 您可以通过更改配置并重新启动服务器来插入完全不同的LoginModule，而无需对应用程序进行任何代码更改。

##### 补充笔记

- 当用户首次尝试访问受保护的资源时，Tomcat将调用`authenticate()` this 的方法`Realm`。因此，您在安全性机制中直接进行的任何更改（新用户，更改的密码或角色等）都将立即反映出来。
- 验证用户身份后，将在用户登录期间将用户（及其关联角色）缓存在Tomcat中。对于基于FORM的身份验证，这意味着直到会话超时或无效；对于BASIC身份验证，这意味着直到用户关闭其浏览器。对已验证用户的安全性信息 所做的任何更改将**不会**反映，直到该用户下次再次登录。
- 与其他`Realm`实现一样，如果中的`<Realm>`元素`server.xml` 包含`digest`属性，则支持摘要密码。JAASRealm `CallbackHandler` 将先提取密码，然后再将其传递回`LoginModule`

#### 合并领域

##### 介绍

**CombinedRealm**是Tomcat `Realm`接口的一种实现，可 通过一个或多个子领域对用户进行身份验证。

使用CombinedRealm使开发人员能够组合相同或不同类型的多个Realm。这可以用于针对不同的来源进行身份验证，以防万一一个Realm失败或提供其他需要多个Realm的目的而回退。

子领域是通过`Realm`在`Realm`定义CombinedRealm的元素内嵌套元素 来定义的。将按照`Realm`列出的顺序尝试对它们进行身份验证。针对任何领域的身份验证就足以对用户进行身份验证。

##### 领域元素属性

要配置CombinedRealm，请创建一个`<Realm>` 元素并将其嵌套`$CATALINA_BASE/conf/server.xml` 在您的`<Engine>`或文件中`<Host>`。您还可以嵌套在文件中的`<Context>`节点 内`context.xml`。

##### 例

这是一个示例，说明您的server.xml代码片段应如何使用UserDatabase领域和DataSource领域。

```xml
<Realm className="org.apache.catalina.realm.CombinedRealm" >
   <Realm className="org.apache.catalina.realm.UserDatabaseRealm"
             resourceName="UserDatabase"/>
   <Realm className="org.apache.catalina.realm.DataSourceRealm"
             dataSourceName="jdbc/authority"
             userTable="users" userNameCol="user_name" userCredCol="user_pass"
             userRoleTable="user_roles" roleNameCol="role_name"/>
</Realm>
```

#### 锁定领域

##### 介绍

**LockOutRealm**是Tomcat `Realm`界面的一种实现， 它扩展了CombinedRealm，以提供锁定功能，以在给定的时间内失败的身份验证尝试过多时提供用户锁定机制。

为了确保正确操作，此领域中存在合理程度的同步。

该领域不需要修改基础领域或关联的用户存储机制。它通过记录所有失败的登录（包括那些不存在的用户的登录）来实现此目的。为了防止DOS通过考虑向无效用户发出请求（从而导致此高速缓存增长），限制了身份验证失败的用户列表的大小。

子领域是通过`Realm`在`Realm`定义LockOutRealm的元素内嵌套元素 来定义的。将按照`Realm`列出的顺序尝试对它们进行身份验证。针对任何领域的身份验证就足以对用户进行身份验证。

##### 领域元素属性

要配置LockOutRealm，请创建一个`<Realm>` 元素并将其嵌套`$CATALINA_BASE/conf/server.xml` 在您的`<Engine>`或文件中`<Host>`。您还可以嵌套在文件中的`<Context>`节点 内`context.xml`。LockOutRealm的属性在[Realm](http://tomcat.apache.org/tomcat-9.0-doc/config/realm.html) 配置文档中定义。

##### 例

这是您的server.xml代码片段应如何向UserDatabase领域添加锁定功能的示例。

```xml
<Realm className="org.apache.catalina.realm.LockOutRealm" >
   <Realm className="org.apache.catalina.realm.UserDatabaseRealm"
             resourceName="UserDatabase"/>
</Realm>
```