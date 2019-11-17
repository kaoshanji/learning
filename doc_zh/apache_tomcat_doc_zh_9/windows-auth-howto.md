# 31.Windows身份验证

### 总览

集成Windows身份验证在Intranet环境中最常用，因为它要求执行身份验证的服务器和要身份验证的用户属于同一域。为了自动验证用户，用户使用的客户端计算机也必须是域的一部分。

使用Apache Tomcat实现集成Windows身份验证有多种选择。他们是：

- 内置的Tomcat支持。
- 使用第三方库，例如Waffle。
- 使用支持Windows身份验证的反向代理来执行身份验证步骤，例如IIS或httpd。

以下各节将讨论每个选项的配置。

### 内置Tomcat支持

Kerberos（集成Windows身份验证的基础）需要仔细配置。如果严格按照本指南中的步骤进行操作，则将产生有效的配置。务必严格遵循以下步骤。配置的灵活性范围很小。从测试至今，已知：

- 用于访问Tomcat服务器的主机名必须与SPN中的主机名完全匹配，否则身份验证将失败。在这种情况下，调试日志中可能会报告校验和错误。
- 客户端必须认为服务器是本地受信任Intranet的一部分。
- SPN必须为HTTP / <主机名>，并且在所有使用的位置上都必须完全相同。
- 该端口号不得包含在SPN中。
- 最多只能将一个SPN映射到一个域用户。
- Tomcat必须以与SPN关联的域帐户或域管理员身份运行。这是**不**建议域管理员用户下运行Tomcat。
- `DEV.LOCAL`在ktpass命令中使用或在jaas.conf中使用时，域名（）不区分大小写
- 使用ktpass命令时必须指定域

内置Tomcat支持Windows身份验证的配置包含四个组件。域控制器，托管Tomcat的服务器，希望使用Windows身份验证的Web应用程序和客户端计算机。以下各节描述了每个组件所需的配置。

下面的配置示例中使用的三台计算机的名称分别是win-dc01.dev.local（域控制器），win-tc01.dev.local（Tomcat实例）和win-pc01.dev.local（客户端）。全部都是DEV.LOCAL域的成员。

注意：为了在以下步骤中使用密码，必须放松域密码策略。不建议在生产环境中使用。

#### 域控制器

这些步骤假定服务器已被配置为充当域控制器。将Windows服务器配置为域控制器不在本操作方法的范围内。配置域控制器以使Tomcat支持Windows身份验证的步骤如下：

- 创建一个将映射到Tomcat服务器使用的服务名称的域用户。在此方法中，此用户被调用`tc01`并具有密码`tc01pass`。

- 将服务主体名称（SPN）映射到用户帐户。SPN采用以下形式

  ```
   <service class>/<host>:<port>/<service name>
  ```

  。本方法中使用的SPN是

  ```
  HTTP/win-tc01.dev.local
  ```

  。要将用户映射到SPN，请运行以下命令：

  ```
  setspn -A HTTP/win-tc01.dev.local tc01
  ```

- 生成密钥表文件，Tomcat服务器将使用该密钥表文件对域控制器进行身份验证。该文件包含服务提供商帐户的Tomcat私钥，应相应地加以保护。要生成文件，请运行以下命令（全部在一行上）：

  ```
  ktpass /out c:\tomcat.keytab /mapuser tc01@DEV.LOCAL
            /princ HTTP/win-tc01.dev.local@DEV.LOCAL
            /pass tc01pass /kvno 0
  ```

- 创建要在客户端上使用的域用户。在此方法中，域用户`test`的密码为`testpass`。

以上步骤已经在运行Windows Server 2008 R2 64位标准的域控制器上使用林和域的Windows Server 2003功能级别进行了测试。

#### Tomcat实例（Windows服务器）

这些步骤假定已经安装并配置了Tomcat和Java 6 JDK / JRE，并且Tomcat以tc01@DEV.LOCAL用户身份运行。为Windows身份验证配置Tomcat实例的步骤如下：

- 将在`tomcat.keytab`域控制器上创建的文件复制到`$CATALINA_BASE/conf/tomcat.keytab`。

- 创建kerberos配置文件 

  ```
  $CATALINA_BASE/conf/krb5.ini
  ```

  。该使用方法中使用的文件包含：

  ```
  [libdefaults]
  default_realm = DEV.LOCAL
  default_keytab_name = FILE:c:\apache-tomcat-9.0.x\conf\tomcat.keytab
  default_tkt_enctypes = rc4-hmac,aes256-cts-hmac-sha1-96,aes128-cts-hmac-sha1-96
  default_tgs_enctypes = rc4-hmac,aes256-cts-hmac-sha1-96,aes128-cts-hmac-sha1-96
  forwardable=true
  
  [realms]
  DEV.LOCAL = {
          kdc = win-dc01.dev.local:88
  }
  
  [domain_realm]
  dev.local= DEV.LOCAL
  .dev.local= DEV.LOCAL
  ```

  该文件的位置可以通过设置来改变 

  ```
  java.security.krb5.conf
  ```

  系统属性。

- 创建JAAS登录配置文件 

  ```
  $CATALINA_BASE/conf/jaas.conf
  ```

  。该使用方法中使用的文件包含：

  ```java
  com.sun.security.jgss.krb5.initiate {
      com.sun.security.auth.module.Krb5LoginModule required
      doNotPrompt=true
      principal="HTTP/win-tc01.dev.local@DEV.LOCAL"
      useKeyTab=true
      keyTab="c:/apache-tomcat-9.0.x/conf/tomcat.keytab"
      storeKey=true;
  };
  
  com.sun.security.jgss.krb5.accept {
      com.sun.security.auth.module.Krb5LoginModule required
      doNotPrompt=true
      principal="HTTP/win-tc01.dev.local@DEV.LOCAL"
      useKeyTab=true
      keyTab="c:/apache-tomcat-9.0.x/conf/tomcat.keytab"
      storeKey=true;
  };
  ```

  该文件的位置可以通过设置来改变 

  ```
  java.security.auth.login.config
  ```

  系统属性。使用的LoginModule是特定于JVM的，因此请确保指定的LoginModule与使用的JVM匹配。登录配置的名称必须与

  认证阀

  使用的值匹配。

SPNEGO身份验证器可与任何[ 领域一起](http://tomcat.apache.org/tomcat-9.0-doc/config/realm.html)使用，但如果与JNDI领域一起使用，则默认情况下，JNDI领域将使用用户的委派凭据连接到Active Directory。如果仅需要经过身份验证的用户名，则可以使用AuthenticatedUserRealm，该方法将仅基于不具有任何角色的经过身份验证的用户名返回一个Principal。

以上步骤已在运行Windows Server 2008 R2 64位标准版和Oracle 1.6.0_24 64位JDK的Tomcat服务器上进行了测试。

#### Tomcat实例（Linux服务器）

经过测试：

- Java 1.7.0，更新45，64位
- Ubuntu Server 12.04.3 LTS 64位
- Tomcat 8.0.x（R1546570）

尽管建议使用最新的稳定版本，但它应可与任何Tomcat 8发行版一起使用。

该配置与Windows相同，但有以下更改：

- Linux服务器不必是Windows域的一部分。
- 应该使用Linux样式文件路径（例如/ usr / local / tomcat / ...）更新krb5.ini和jaas.conf中的keytab文件的路径，以反映Linux服务器上的keytab文件的路径。

#### Web应用程序

需要将Web应用程序配置为`SPNEGO`在web.xml中使用Tomcat特定的身份验证方法（而不是BASIC等）。与其他身份验证器一样，可以通过显式配置[ 身份验证阀门](http://tomcat.apache.org/tomcat-9.0-doc/config/valve.html#SPNEGO_Valve)并在Valve上设置属性来自定义行为。

#### 客户

客户端必须配置为使用Kerberos身份验证。对于Internet Explorer，这意味着确保Tomcat实例位于“本地Intranet”安全域中，并且已将其配置为（工具> Internet选项>高级）并启用了集成Windows身份验证。请注意，如果您为客户端和Tomcat实例使用同一台计算机，则这**将**不起作用，因为Internet Explorer将使用不受支持的NTLM协议。

#### 参考文献

正确配置Kerberos身份验证可能很棘手。以下参考资料可能会有所帮助。[Tomcat用户邮件列表中](https://tomcat.apache.org/lists.html#tomcat-users)也始终可以提供建议 。

1. [IIS和Kerberos](http://www.adopenstatic.com/cs/blogs/ken/archive/2006/10/19/512.aspx)
2. [SourceForge的SPNEGO项目](http://spnego.sourceforge.net/index.html)
3. [Oracle Java GSS-API教程（Java 7）](http://docs.oracle.com/javase/7/docs/technotes/guides/security/jgss/tutorials/index.html)
4. [Oracle Java GSS-API教程-故障排除（Java 7）](http://docs.oracle.com/javase/7/docs/technotes/guides/security/jgss/tutorials/Troubleshooting.html)
5. [用于Windows身份验证的Geronimo配置](https://cwiki.apache.org/confluence/display/GMOxDOC21/Using+SPNEGO+in+Geronimo#UsingSPNEGOinGeronimo-SettinguptheDomainControllerMachine)
6. [Kerberos交换中的加密选择](http://blogs.msdn.com/b/openspecification/archive/2010/11/17/encryption-type-selection-in-kerberos-exchanges.aspx)
7. [支持的Kerberos密码套件](http://support.microsoft.com/kb/977321)

### 第三方库

#### Waffle

可以通过[Waffle网站](http://waffle.codeplex.com/)找到该解决方案的完整详细信息 。主要功能是：

- 嵌入式解决方案
- 简单配置（无需JAAS或Kerberos keytab配置）
- 使用本地库

#### Spring Security-Kerberos扩展

可通过[Kerberos扩展网站](http://static.springsource.org/spring-security/site/extensions/krb/index.html)找到此解决方案的完整详细信息 。主要功能是：

- 扩展到Spring Security
- 需要生成一个Kerberos keytab文件
- 纯Java解决方案

#### SourceForge的SPNEGO项目

该解决方案的完整详细信息可以在[项目站点中](http://spnego.sourceforge.net/index.html/)找到 。主要功能是：

- 使用Kerberos
- 纯Java解决方案

#### Jespa

可通过[项目网站](http://www.ioplex.com/)找到此解决方案的完整详细信息 [。](http://www.ioplex.com/)主要功能是：

- 纯Java解决方案
- 先进的Active Directory集成

### 反向代理

#### Microsoft IIS

配置IIS以提供Windows身份验证需要执行三个步骤。他们是：

1. 将IIS配置为Tomcat的反向代理（请参阅 [IIS Web服务器方法）](https://tomcat.apache.org/connectors-doc/webserver_howto/iis.html)。
2. 配置IIS以使用Windows身份验证
3. 配置Tomcat通过设置在tomcatAuthentication属性使用从IIS认证用户信息[ AJP连接](http://tomcat.apache.org/tomcat-9.0-doc/config/ajp.html)到`false`。或者，将tomcatAuthorization属性设置`true`为允许IIS在Tomcat执行授权时进行身份验证。

#### Apache httpd

Apache httpd开箱即用不支持Windows身份验证，但是可以使用许多第三方模块。这些包括：

1. 在Windows平台上使用的[mod_auth_sspi](http://sourceforge.net/projects/mod-auth-sspi/)。
2. 对于非Windows平台，请使用[mod_auth_ntlm_winbind](http://adldap.sourceforge.net/wiki/doku.php?id=mod_auth_ntlm_winbind)。已知可在32位平台上使用httpd2.0.x。一些用户报告了httpd 2.2.x版本和64位Linux版本的稳定性问题。

要配置httpd以提供Windows身份验证，需要执行三个步骤。他们是：

1. 将httpd配置为Tomcat的反向代理（请参阅 [Apache httpd Web服务器操作方法）](https://tomcat.apache.org/connectors-doc/webserver_howto/apache.html)。
2. 将httpd配置为使用Windows身份验证
3. 配置Tomcat通过设置在tomcatAuthentication属性使用从httpd的认证的用户信息[ AJP连接](http://tomcat.apache.org/tomcat-9.0-doc/config/ajp.html)到`false`。