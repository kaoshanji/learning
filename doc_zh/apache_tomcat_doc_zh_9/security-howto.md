# 29.安全

### 介绍

默认情况下，将Tomcat配置为在大多数情况下具有合理的安全性。某些环境可能需要更多或更少的安全配置。该页面旨在为可能影响安全性的配置选项提供单一参考，并就更改这些选项的预期影响提供一些说明。目的是提供评估Tomcat安装的安全性时应考虑的配置选项列表。

**注意**：阅读此页不能代替阅读和理解详细的配置文档。有关这些属性的完整描述，请参见相关文档页面。

### 非Tomcat设置

Tomcat配置不应该是唯一的防线。系统中的其他组件（操作系统，网络，数据库等）也应得到保护。

Tomcat不应在root用户下运行。为Tomcat进程创建一个专用用户，并为该用户提供操作系统的最低必需权限。例如，应该不可能使用Tomcat用户远程登录。

文件权限也应适当限制。在 `.tar.gz`发行版中，文件和目录不是世界可读的，并且该组没有写访问权。在类似Unix的操作系统上，Tomcat以默认的umask运行，`0027`以维护在Tomcat运行时创建的文件（例如，日志文件，扩展的WAR等）的这些权限。

以ASF上的Tomcat实例为例（禁用自动部署，并将Web应用程序部署为分解目录），标准配置是使所有Tomcat文件归root用户所有，并拥有Tomcat组，而所有者拥有读/写特权。 ，群组只有人阅读，而世界没有任何权限。例外是Tomcat用户而不是root用户拥有的日志，临时目录和工作目录。这意味着即使攻击者破坏了Tomcat进程，他们也无法更改Tomcat配置，部署新的Web应用程序或修改现有的Web应用程序。Tomcat进程以umask 007运行以维护这些权限。

在网络级别，请考虑使用防火墙将传入和传出连接都限制为仅预期存在的那些连接。

#### JMX

JMX连接的安全性取决于JRE提供的实现，因此不在Tomcat的控制范围内。

通常，访问控制非常有限（对所有内容只读或对所有内容读写）。Tomcat通过JMX公开了大量内部信息和控制，以帮助调试，监视和管理。给定有限的访问控制，应将JMX访问等同于本地root / admin访问，并进行相应的限制。

大多数（所有？）JRE供应商提供的JMX访问控制不会记录失败的身份验证尝试，也不会在重复失败的身份验证后提供帐户锁定功能。这使得暴力攻击易于安装且难以检测。

鉴于上述所有内容，应注意确保适当使用JMX接口（如果使用）。您可能希望考虑保护JMX接口安全的选项包括：

- 为所有JMX用户配置一个强密码；
- 仅将JMX侦听器绑定到内部网络；
- 将对JMX端口的网络访问限制为受信任的客户端；和
- 提供特定于应用程序的运行状况页面，以供外部监视系统使用。

### 默认Web应用程序

#### 一般

Tomcat附带了许多默认启用的Web应用程序。过去已经在这些应用程序中发现了漏洞。应该删除不需要的应用程序，这样如果发现另一个漏洞，系统就不会受到威胁。

#### 根

ROOT Web应用程序的安全风险非常低，但是确实包含正在使用的Tomcat版本。通常，出于安全原因，应从可公开访问的Tomcat实例中删除ROOT Web应用程序，以便向用户显示更合适的默认页面。

#### 文献资料

该文档Web应用程序的安全风险非常低，但是它确实标识了正在使用的Tomcat版本。通常应从可公开访问的Tomcat实例中将其删除。

#### 例子

示例Web应用程序应始终从任何对安全敏感的安装中删除。尽管示例Web应用程序不包含任何已知漏洞，但已知包含一些功能（尤其是显示所有已接收内容并允许设置新Cookie的cookie示例），攻击者可能会结合使用这些功能在部署在Tomcat实例上的另一个应用程序中获取其他信息，否则这些信息将不可用。

#### 经理

Manager应用程序允许远程部署Web应用程序，并且由于启用了Manager应用程序的弱密码和可公开访问的Tomcat实例的广泛使用，攻击者经常将其作为攻击目标。默认情况下，无法访问Manager应用程序，因为没有为用户配置必要的访问权限。如果启用了Manager应用**程序，**则应遵循“ **保护管理应用程序”**部分中的指导 。

#### 房东经理

Host Manager应用程序允许创建和管理虚拟主机-包括为虚拟主机启用Manager应用程序。默认情况下，主机管理器应用程序不可访问，因为没有为用户配置必要的访问权限。如果启用了主机管理器应用**程序，**则应遵循“ **保护管理应用程序”**部分中的指导。

#### 保护管理应用程序

部署提供Tomcat实例管理功能的Web应用程序时，应遵循以下准则：

- 确保允许访问管理应用程序的所有用户都使用强密码。
- 不要删除[LockOutRealm](http://tomcat.apache.org/tomcat-9.0-doc/config/realm.html#LockOut_Realm_-_org.apache.catalina.realm.LockOutRealm)的使用， 它可以防止对用户密码的暴力攻击。
- 在[context.xml](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)文件中为管理应用程序配置[RemoteAddrValve](http://tomcat.apache.org/tomcat-9.0-doc/config/valve.html#Remote_Address_Valve)，该应用程序默认限制对本地主机的访问。如果需要远程访问，请使用此阀将其限制为特定的IP地址。

### 安全经理

启用安全管理器会使Web应用程序在沙箱中运行，从而严重限制了Web应用程序执行恶意操作的能力，例如调用System.exit（），建立网络连接或访问Web应用程序的根目录和临时目录之外的文件系统。但是，应该注意，存在一些恶意行为，例如通过无限循环触发高CPU消耗，安全管理器无法阻止。

如果攻击者找到了一种破坏可信Web应用程序的方法，通常可以启用安全管理器以限制潜在的影响。安全管理器也可以用来降低运行不受信任的Web应用程序的风险（例如，在托管环境中），但应注意，安全管理器仅降低运行不受信任的Web应用程序的风险，而不能消除它们。如果运行多个不受信任的Web应用程序，建议将每个Web应用程序部署到单独的Tomcat实例（最好是单独的主机），以降低恶意Web应用程序影响其他应用程序可用性的能力。

Tomcat在启用安全管理器的情况下进行了测试；但是大多数Tomcat用户没有使用安全管理器运行，因此在此配置中，Tomcat的用户测试效果不佳。报告了并且继续存在由在安全管理器下运行触发的错误。

如果启用了安全管理器，则由安全管理器施加的限制可能会破坏大多数应用程序。未经广泛测试，不得使用安全管理器。理想情况下，应该在开发周期的开始就引入安全管理器的使用，因为跟踪和修复由于启用成熟的应用程序的安全管理器而导致的问题可能很耗时。

启用安全管理器会更改以下设置的默认值：

- **Host**元素的**deployXML**属性 的默认值更改为。`false`

### server.xml

#### 一般

缺省的server.xml包含大量注释，包括一些注释掉的示例组件定义。删除这些注释使阅读和理解server.xml非常容易。

如果未列出组件类型，则没有该类型的设置会直接影响安全性。

#### 服务器

设置**端口**属性以`-1`禁用关闭端口。

如果未禁用shutdown端口，则应为**shutdown**配置一个强密码。

#### 听众

如果使用gcc在Solaris上编译，则APR Lifecycle Listener不稳定。如果在Solaris上使用APR / native连接器，请使用Sun Studio编译器对其进行编译。

JNI库加载侦听器可用于加载本机代码。它仅应用于加载受信任的库。

应启用并适当配置安全生命周期侦听器。

#### 连接器

默认情况下，配置了HTTP和AJP连接器。应从server.xml中删除将不使用的连接器。

所述**地址**属性可以用来控制连接器侦听连接哪个IP地址。默认情况下，连接器侦听所有已配置的IP地址。

所述**allowTrace**属性可以被用来使其可以是用于调试TRACE请求。由于某些浏览器处理TRACE请求的响应的方式（使浏览器暴露于XSS攻击），因此默认情况下禁用对TRACE请求的支持。

所述**maxPostSize**属性控制将被解析为参数的POST请求的最大大小。在请求期间将缓存参数，因此默认情况下此参数限制为2MB，以减少遭受DOS攻击的风险。

该**maxSavePostSize**属性控制POST请求的形式和CLIENT-CERT验证过程中保存。在身份验证期间（可能要花几分钟），将缓存参数，因此默认情况下将其限制为4KB，以减少遭受DOS攻击的风险。

所述**maxParameterCount**属性控制参数和值对（GET加POST），其可被解析并存储在请求的最大数目。过多的参数将被忽略。如果要拒绝此类请求，请配置 [FailedRequestFilter](http://tomcat.apache.org/tomcat-9.0-doc/config/filter.html)。

所述**xpoweredBy**属性控制X供电-通过HTTP标头是否与每个请求一起发送。如果发送，则标头的值包含Servlet和JSP规范版本，完整的Tomcat版本（例如Apache Tomcat / 9.0），JVM供应商的名称和JVM的版本。默认情况下，此标头是禁用的。该标头可以为合法客户端和攻击者提供有用的信息。

所述**服务器**属性控制服务器HTTP标头的值。Tomcat 4.1.x至9.0.x的此标头的默认值为Apache-Coyote / 1.1。此标头可以为合法客户端和攻击者提供有限的信息。

该**SSLEnabled**，**方案**和 **安全**属性可以全部独立设置。当Tomcat位于反向代理后面并且代理通过HTTP或HTTPS连接到Tomcat时，通常使用这些命令。它们允许Tomcat查看客户端与代理（而不是代理与Tomcat）之间的连接的SSL属性。例如，客户端可以通过HTTPS连接到代理，但是代理使用HTTP连接到Tomcat。如果Tomcat必须能够区分代理收到的安全连接和非安全连接，则代理必须使用单独的连接器将安全请求和非安全请求传递给Tomcat。如果代理使用AJP，则客户端连接的SSL属性通过AJP协议传递，并且不需要单独的连接器。

的**tomcatAuthentication**和 **tomcatAuthorization**属性与AJP连接器用于确定是否Tomcat的应处理所有认证和授权，或者如果认证应该委托给反向代理（经认证的用户名传递到Tomcat作为AJP协议的一部分）与所述选项使Tomcat仍然执行授权。

AJP连接器中的**requiredSecret**属性可配置Tomcat与Tomcat前面的反向代理之间的共享机密。它用于防止通过AJP协议进行未经授权的连接。

#### 主办

主机元素控制部署。自动部署不仅可以简化管理，还可以使攻击者更轻松地部署恶意应用程序。自动部署由 **autoDeploy**和**deployOnStartup** 属性控制。如果两者均为`false`，则仅部署server.xml中定义的上下文，并且任何更改都需要重新启动Tomcat。

在Web应用程序可能不受信任的托管环境中，请将**deployXML**属性设置`false`为忽略与Web应用程序打包在一起的任何context.xml，这可能会尝试为Web应用程序分配更多的特权。请注意，如果启用了安全管理器，那么**deployXML**属性将默认为`false`。

#### 语境

这适用于 可以定义它们的所有位置的[Context](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)元素： `server.xml`文件，默认`context.xml`文件，每个主机`context.xml.default`文件，每个主机配置目录中或Web应用程序内部的Web应用程序上下文文件。

所述**crossContext**属性控制如果上下文被允许访问另一个上下文的资源。`false`默认情况下，它 仅应针对受信任的Web应用程序进行更改。

所述**特权**如果上下文允许使用容器提供的servlet像管理器的servlet属性控制。`false`默认情况下，它 仅应针对受信任的Web应用程序进行更改。

嵌套[Resources](http://tomcat.apache.org/tomcat-9.0-doc/config/resources.html)元素的**allowLinking**属性 控制是否允许上下文使用链接的文件。如果启用并且取消部署上下文，则在删除上下文资源时将遵循链接。从不区分大小写的操作系统（包括Windows）的默认设置更改此设置将禁用许多安全措施，并且除其他外，还可以直接访问WEB-INF目录。`false`

该**sessionCookiePathUsesTrailingSlash**可以用来解决一个bug在多个浏览器（Internet Explorer，Safari和边缘），以防止会话cookie被跨应用程序暴露的应用程序时，都有一个共同的路径前缀。但是，启用此选项可能会对Servlet映射到的应用程序造成问题 `/*`。还应注意，RFC6265第8.5节明确指出，不应将不同的路径视为足以将cookie与其他应用程序隔离开来。

#### 阀门

强烈建议配置AccessLogValve。Tomcat的默认配置包括AccessLogValve。这些通常按主机配置，但也可以根据需要按引擎或上下文进行配置。

任何管理应用程序都应由RemoteAddrValve保护（此Valve也可以作为过滤器使用）。在**允许**属性应该被用来限制访问一组已知的受信任主机。

默认的ErrorReportValve在发送给客户端的响应中包括Tomcat版本号。为了避免这种情况，可以在每个Web应用程序中配置自定义错误处理。或者，您可以显式配置[ErrorReportValve](http://tomcat.apache.org/tomcat-9.0-doc/config/valve.html)并将其**showServerInfo**属性设置 为`false`。或者，可以通过创建具有以下内容的文件CATALINA_BASE / lib / org / apache / catalina / util / ServerInfo.properties来更改版本号：

```
server.info=Apache Tomcat/9.0.x
```

根据需要修改值。请注意，这还将更改某些管理工具中报告的版本号，并可能使确定安装的实际版本更加困难。CATALINA_HOME / bin / version.bat | sh脚本仍将报告正确的版本号。

发生错误时，默认的ErrorReportValve可以向客户端显示堆栈跟踪和/或JSP源代码。为了避免这种情况，可以在每个Web应用程序中配置自定义错误处理。或者，您可以显式配置[ErrorReportValve](http://tomcat.apache.org/tomcat-9.0-doc/config/valve.html) 并将其**showReport**属性设置为`false`。

RewriteValve使用正则表达式，格式不正确的正则表达式模式可能容易受到“灾难性回溯”或“ ReDoS”的影响。有关更多详细信息，请参见 [重写文档](http://tomcat.apache.org/tomcat-9.0-doc/rewrite.html)。

#### 境界

MemoryRealm不适用于生产用途，因为tomcat-users.xml的任何更改都需要重新启动Tomcat才能生效。

不建议将JDBCRealm用于生产环境，因为它是用于所有身份验证和授权选项的单线程。请改用DataSourceRealm。

UserDatabaseRealm不适用于大规模安装。它旨在用于小型，相对静态的环境。

JAASRealm没有得到广泛使用，因此代码不如其他领域成熟。建议在使用此领域之前进行其他测试。

默认情况下，领域不实施任何形式的帐户锁定。这意味着蛮力攻击可以成功。为了防止暴力攻击，应将选定的领域包装在LockOutRealm中。

#### 经理

管理器组件用于生成会话ID。

可以使用**randomClass**属性更改用于生成随机会话ID的类。

会话ID的长度可以使用**sessionIdLength**属性更改 。

#### 簇

在将安全，受信任的网络用于所有与群集相关的网络流量的基础上编写群集实现。在不安全，不受信任的网络上运行群集是不安全的。

如果您在不受信任的网络上运行，或者希望采取过多的谨慎措施，则可以使用 [EncryptInterceptor](http://tomcat.apache.org/tomcat-9.0-doc/config/cluster-interceptor.html#org.apache.catalina.tribes.group.interceptors.EncryptInterceptor_Attributes) 对节点之间的流量进行加密。

### 系统属性

将**org.apache.catalina.connector.RECYCLE_FACADES** 系统属性设置为`true`将会为每个请求创建一个新的Facade对象。这减少了应用程序中的错误将数据从一个请求暴露到另一个请求的机会。

的 **org.apache.catalina.connector.CoyoteAdapter.ALLOW_BACKSLASH**和 **org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH** 系统属性允许请求URI的非标准解析。在反向代理后面使用这些选项可以使攻击者绕过该代理实施的任何安全限制。

该 **org.apache.catalina.connector.Response.ENFORCE_ENCODING_IN_GET_WRITER** 如果禁用系统性能带来了安全隐患。当应使用规范要求的默认ISO-8859-1时，许多违反RFC2616的用户代理尝试猜测文本媒体类型的字符编码。某些浏览器会将包含对于ISO-8859-1安全的字符的响应解释为UTF-7，但如果将其解释为UTF-7，则会触发XSS漏洞。

### web.xml

这适用于默认`conf/web.xml`文件， `/WEB-INF/tomcat-web.xml`和和`/WEB-INF/web.xml` Web应用程序中的文件（如果它们定义了此处提到的组件）。

所述[DefaultServlet](http://tomcat.apache.org/tomcat-9.0-doc/default-servlet.html)配置有**只读**设置为 `true`。将其更改为，`false`可以使客户端删除或修改服务器上的静态资源并上载新资源。通常，在不需要身份验证的情况下不应更改此设置。

DefaultServlet的**列表**设置为 `false`。这不是因为允许目录列表被认为是不安全的，而是因为生成包含数千个文件的目录列表会消耗大量CPU，从而导致DOS攻击。

DefaultServlet的**showServerInfo** 设置为`true`。启用目录列表后，发送给客户端的响应中将包含Tomcat版本号。为避免这种情况，您可以显式配置DefaultServlet并将其**showServerInfo**属性设置 为false。或者，可以通过创建具有以下内容的文件CATALINA_BASE / lib / org / apache / catalina / util / ServerInfo.properties来更改版本号：

```
server.info=Apache Tomcat/9.0.x
```

根据需要修改值。请注意，这还将更改某些管理工具中报告的版本号，并可能使确定安装的实际版本更加困难。CATALINA_HOME / bin / version.bat | sh脚本仍将报告正确的版本号。

默认情况下，CGI Servlet是禁用的。如果启用，则`10`在生产系统上不应将调试初始化参数设置为或更高，因为调试页面不安全。

在`enableCmdLineArguments`启用了Windows的Windows上使用CGI Servlet时 ，请`cmdLineArgumentsDecoded`仔细检查设置 并确保它适合您的环境。默认值为安全。不安全的配置可能会使服务器暴露于远程代码执行中。通过[CGI How To中](http://tomcat.apache.org/tomcat-9.0-doc/cgi-howto.html)的链接可以找到有关潜在风险和缓解措施的更多信息。

可以配置[FailedRequestFilter](http://tomcat.apache.org/tomcat-9.0-doc/config/filter.html)并将其用于拒绝在请求参数解析期间出错的请求。如果没有过滤器，则默认行为是忽略无效或过多的参数。

[HttpHeaderSecurityFilter](http://tomcat.apache.org/tomcat-9.0-doc/config/filter.html)可用于向响应添加标头以提高安全性。如果客户端直接访问Tomcat，则除非您的应用程序已经设置了过滤器，否则您可能要启用此过滤器及其设置的所有标头。如果通过反向代理访问Tomcat，则此过滤器的配置需要与反向代理设置的所有标头协调。

### 一般

BASIC和FORM身份验证以明文形式传递用户名和密码。将这些身份验证机制与通过不可信网络连接的客户端一起使用的Web应用程序应使用SSL。

与经过身份验证的用户进行会话的会话cookie几乎与攻击者的用户密码一样有用，并且在几乎所有情况下，都应提供与密码本身相同级别的保护。这通常意味着通过SSL进行身份验证，并继续使用SSL，直到会话结束。