# 15.CGI

### 介绍

CGI（通用网关接口）为Web服务器定义了一种与外部内容生成程序进行交互的方式，这些程序通常称为CGI程序或CGI脚本。

在Tomcat中，当您将Tomcat用作HTTP服务器并需要CGI支持时，可以添加CGI支持。通常，当您不想运行Apache httpd这样的Web服务器时，这是在开发期间完成的。Tomcat的CGI支持与Apache httpd的兼容，但是存在一些限制（例如，只有一个cgi-bin目录）。

CGI支持是使用servlet类实现的 `org.apache.catalina.servlets.CGIServlet`。传统上，此servlet映射到URL模式“ / cgi-bin / *”。

默认情况下，Tomcat中禁用了CGI支持。

### 安装

**注意** -CGI脚本用于执行Tomcat JVM外部的程序。如果您使用的是Java SecurityManager，它将绕过您的安全策略配置`catalina.policy.`

要启用CGI支持：

1. 在默认`$CATALINA_BASE/conf/web.xml`文件中，有CGI servlet的注释掉的示例servlet和servlet映射元素。要在Web应用程序中启用CGI支持，请将该Servlet和Servlet映射声明复制到`WEB-INF/web.xml`Web应用程序的文件中。

   取消注释servlet和`$CATALINA_BASE/conf/web.xml`文件中的servlet映射， 可立即为所有已安装的Web应用程序启用CGI。

2. `privileged="true"`在Web应用程序的Context元素上设置。

   仅允许将标记为特权的上下文使用CGI Servlet。请注意，修改全局`$CATALINA_BASE/conf/context.xml` 文件会影响所有Web应用程序。有关详细信息，请参见 [上下文文档](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)。

### 组态

有几个servlet初始化参数可用于配置CGI servlet的行为。

- **cgiMethods-**逗号分隔的HTTP方法列表。使用这些方法之一的请求将传递到CGI脚本，以使脚本生成响应。默认值为`GET,POST`。使用 `*`的脚本来处理所有的请求，而不管方法。除非被此参数的配置所覆盖，否则使用HEAD，OPTIONS或TRACE的请求将由超类处理。
- **cgiPathPrefix** -CGI搜索路径将从Web应用程序的根目录+ File.separator +此前缀开始。默认情况下，没有值，这导致将Web应用程序根目录用作搜索路径。推荐值是 `WEB-INF/cgi`
- **cmdLineArgumentsDecoded-**如果启用了命令行参数（通过**enableCmdLineArguments**）并且Tomcat在Windows上运行，则每个单独的解码命令行参数都必须与此模式匹配，否则请求将被拒绝。这是为了防止已知问题从Java传递命令行参数到Windows。这些问题可能导致远程执行代码。有关这些问题的更多信息，请参阅 [Markus Wulftange的博客](https://codewhitesec.blogspot.com/2016/02/java-and-command-line-injections-in-windows.html)以及[Daniel Colascione的](https://web.archive.org/web/20161228144344/https://blogs.msdn.microsoft.com/twistylittlepassagesallalike/2011/04/23/everyone-quotes-command-line-arguments-the-wrong-way/)已存档 [博客](https://web.archive.org/web/20161228144344/https://blogs.msdn.microsoft.com/twistylittlepassagesallalike/2011/04/23/everyone-quotes-command-line-arguments-the-wrong-way/)。
- **cmdLineArgumentsEncoded-**如果启用了命令行参数（通过**enableCmdLineArguments**），则单个编码的命令行参数必须与此模式匹配，否则该请求将被拒绝。默认值与RFC3875定义的允许值匹配，并且为 `[a-zA-Z0-9\Q%;/?:@&,$-_.!~*'()\E]+`
- **enableCmdLineArguments-**是否根据3875 RFC的4.4节从查询字符串生成了命令行参数？默认值为 `false`。
- **环境变量** -为CGI脚本的执行环境设置的环境。变量的名称取自参数名称。要配置名为FOO的环境变量，请配置名为environment-variable-FOO的参数。参数值用作环境变量值。默认为无环境变量。
- **可执行文件** -用于运行脚本的可执行**文件**的名称。如果脚本本身是可执行文件（例如exe文件），则可以将该参数显式设置为空字符串。默认值为 `perl`。
- **可执行文件arg-1**，**可执行文件arg-2**等等-可执行文件的其他参数。这些在CGI脚本名称之前。默认情况下，没有其他参数。
- **envHttpHeaders-**一个正则表达式，用于选择作为环境变量传递给CGI进程的HTTP标头。请注意，在匹配之前，标头已转换为大写，并且整个标头名称必须与模式匹配。默认是 `ACCEPT[-0-9A-Z]*|CACHE-CONTROL|COOKIE|HOST|IF-[-0-9A-Z]*|REFERER|USER-AGENT`
- **parameterEncoding-**与CGI Servlet一起使用的参数编码的名称。默认值为 `System.getProperty("file.encoding","UTF-8")`。这是系统默认编码，如果该系统属性不可用，则为UTF-8。
- **passShellEnvironment-**是否应将Tomcat进程中的shell环境变量（如果有）传递给CGI脚本？默认值为 `false`。
- **stderrTimeout-**在终止CGI进程之前等待stderr读取完成的时间（以毫秒为单位）。默认值为`2000`。

执行的CGI脚本取决于CGI Servlet的配置以及请求如何映射到CGI Servlet。CGI搜索路径从Web应用程序的根目录+ File.separator + cgiPathPrefix开始。该 **PATHINFO**然后进行搜索，除非它是`null`-在这种情况下，**servletPath**搜索。

搜索从第一个路径段开始，并一次扩展一个路径段，直到没有剩余路径段（导致404）或找不到脚本为止。所有剩余的路径段都将传递到**PATH_INFO**环境变量中的脚本中 。