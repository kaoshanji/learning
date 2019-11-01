# 18.默认Servlet

### 什么是DefaultServlet

缺省的Servlet是既提供静态资源又提供目录列表的Servlet（如果启用了目录列表）。

### 声明在哪里？

它在*$ CATALINA_BASE / conf / web.xml中*全局声明。默认情况下，它是声明：

```xml
    <servlet>
        <servlet-name>default</servlet-name>
        <servlet-class>
          org.apache.catalina.servlets.DefaultServlet
        </servlet-class>
        <init-param>
            <param-name>debug</param-name>
            <param-value>0</param-value>
        </init-param>
        <init-param>
            <param-name>listings</param-name>
            <param-value>false</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

...

    <servlet-mapping>
        <servlet-name>default</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
```

因此，默认情况下，默认servlet在webapp启动时加载，并且目录列表被禁用，调试被关闭。

如果您需要更改应用程序的DefaultServlet设置，则可以通过在中重新定义DefaultServlet来覆盖默认配置 `/WEB-INF/web.xml`。但是，如果您尝试将应用程序部署到另一个容器上，这将导致问题，因为将无法识别DefaultServlet类。您可以通过使用Tomcat特定的`/WEB-INF/tomcat-web.xml`部署描述符来解决此问题 。格式与相同`/WEB-INF/web.xml`。它将覆盖所有默认设置，但不会覆盖`/WEB-INF/web.xml`。由于它是特定于Tomcat的，因此只有在将应用程序部署在Tomcat上时才进行处理。

### 我可以改变什么？

DefaultServlet允许以下initParamters：

| 属性                   | 描述                                                         |
| :--------------------- | :----------------------------------------------------------- |
| `debug`                | 调试级别。除非您是tomcat开发人员，否则它不是很有用。在撰写本文时，有用的值为0、1、11、1000。[0] |
| `listings`             | 如果没有欢迎文件，可以显示目录列表吗？值可以是**true**或**false** [false] 欢迎文件是servlet api的一部分。 **警告：**包含许多条目的目录清单很昂贵。大型目录列表的多个请求会消耗大量服务器资源。 |
| `precompressed`        | 如果存在文件的预压缩版本（文件名带有`.br` 或`.gz`附加到原始文件旁边的文件），如果用户代理支持匹配的内容编码（br或gzip）并且已启用此选项，则Tomcat将提供该预压缩文件。 。[false] 如果直接请求 带有`.br`或`.gz`扩展名的预压缩文件，则可以访问该文件，因此，如果原始资源受安全约束保护，则预压缩版本也必须受到类似的保护。 也可以配置预压缩格式的列表。语法是用逗号分隔的`[content-encoding]=[file-extension]`成对列表 。例如： `br=.br,gzip=.gz,bzip2=.bz2`。如果指定了多种格式，则客户端支持多个格式，并且客户端不表达首选项，格式列表的顺序将被视为服务器首选项顺序，并用于选择返回的格式。 |
| `readmeFile`           | 如果显示目录列表，则自述文件也可能与列表一起显示。该文件将按原样插入，因此可能包含HTML。 |
| `globalXsltFile`       | 如果您希望自定义目录列表，则可以使用XSL转换。该值是一个相对文件名（到$ CATALINA_BASE / conf /或$ CATALINA_HOME / conf /），将用于所有目录列表。可以按上下文和/或每个目录覆盖。请参阅下面的**contextXsltFile**和 **localXsltFile**。xml的格式如下所示。 |
| `contextXsltFile`      | 您还可以通过配置通过上下文自定义目录列表`contextXsltFile`。这必须是`/path/to/context.xslt`带有`.xsl`或`.xslt`扩展名的文件的上下文相关路径（例如：）。这将覆盖 `globalXsltFile`。如果存在此值，但文件不存在，`globalXsltFile`则将使用该值。如果 `globalXsltFile`不存在，那么将显示默认目录列表。 |
| `localXsltFile`        | 您还可以通过配置来按目录自定义目录列表`localXsltFile`。该目录必须是清单所在目录中的文件，并带有 `.xsl`或`.xslt`扩展名。这将覆盖 `globalXsltFile`和`contextXsltFile`。如果存在此值，但文件不存在，`contextXsltFile`则将使用该值 。如果 `contextXsltFile`不存在， `globalXsltFile`则将使用。如果 `globalXsltFile`不存在，那么将显示默认目录列表。 |
| `input`                | 读取要提供的资源时的输入缓冲区大小（以字节为单位）。[2048]   |
| `output`               | 写入要服务的资源时的输出缓冲区大小（以字节为单位）。[2048]   |
| `readonly`             | 此上下文是“只读”的，因此拒绝了HTTP命令（例如PUT和DELETE）？[真正] |
| `fileEncoding`         | 读取静态资源时要使用的文件编码。[平台默认]                   |
| `useBomIfPresent`      | 如果静态文件包含字节顺序标记（BOM），则应优先于fileEncoding使用它来确定文件编码。[真正] |
| `sendfileSize`         | 如果使用的连接器支持sendfile，则表示将使用sendfile的最小文件大小（以KB为单位）。使用负值可始终禁用发送文件。[48] |
| `useAcceptRanges`      | 如果为true，则在适合响应时将设置Accept-Ranges标头。[真正]    |
| `showServerInfo`       | 启用目录列表后，应在发送给客户端的响应中显示服务器信息。[真正] |
| `sortListings`         | 服务器应将目录中的列表排序。[假]                             |
| `sortDirectoriesFirst` | 服务器应在所有文件之前列出所有目录。[假]                     |
| `allowPartialPut`      | 服务器是否应该将带有Range标头的HTTP PUT请求作为部分PUT？请注意，RFC 7233阐明了Range标头仅对GET请求有效。[真正] |

### 如何自定义目录列表？

您可以使用自己的实现覆盖DefaultServlet，并在web.xml声明中使用它。如果您能理解上面所说的内容，我们将假定您可以阅读DefaultServlet servlet的代码并进行适当的调整。（如果没有，那么该方法不适合您）

您可以使用 `localXsltFile`或， `globalXsltFile`并且DefaultServlet将创建一个xml文档，并根据`localXsltFile`和中 提供的值通过xsl转换运行它`globalXsltFile`。`localXsltFile`首先检查，然后检查，`globalXsltFile`然后发生默认行为。

格式：

```xml
    <listing>
     <entries>
      <entry type='file|dir' urlPath='aPath' size='###' date='gmt date'>
        fileName1
      </entry>
      <entry type='file|dir' urlPath='aPath' size='###' date='gmt date'>
        fileName2
      </entry>
      ...
     </entries>
     <readme></readme>
    </listing>
```

- 如果缺少大小 `type='dir'`
- 自述文件是CDATA条目

以下是模拟默认tomcat行为的示例xsl文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="3.0">

  <xsl:output method="html" html-version="5.0"
    encoding="UTF-8" indent="no"
    doctype-system="about:legacy-compat"/>

  <xsl:template match="listing">
   <html>
    <head>
      <title>
        Sample Directory Listing For
        <xsl:value-of select="@directory"/>
      </title>
      <style>
        h1 {color : white;background-color : #0086b2;}
        h3 {color : white;background-color : #0086b2;}
        body {font-family : sans-serif,Arial,Tahoma;
             color : black;background-color : white;}
        b {color : white;background-color : #0086b2;}
        a {color : black;} HR{color : #0086b2;}
        table td { padding: 5px; }
      </style>
    </head>
    <body>
      <h1>Sample Directory Listing For
            <xsl:value-of select="@directory"/>
      </h1>
      <hr style="height: 1px;" />
      <table style="width: 100%;">
        <tr>
          <th style="text-align: left;">Filename</th>
          <th style="text-align: center;">Size</th>
          <th style="text-align: right;">Last Modified</th>
        </tr>
        <xsl:apply-templates select="entries"/>
        </table>
      <xsl:apply-templates select="readme"/>
      <hr style="height: 1px;" />
      <h3>Apache Tomcat/9.0</h3>
    </body>
   </html>
  </xsl:template>


  <xsl:template match="entries">
    <xsl:apply-templates select="entry"/>
  </xsl:template>

  <xsl:template match="readme">
    <hr style="height: 1px;" />
    <pre><xsl:apply-templates/></pre>
  </xsl:template>

  <xsl:template match="entry">
    <tr>
      <td style="text-align: left;">
        <xsl:variable name="urlPath" select="@urlPath"/>
        <a href="{$urlPath}">
          <pre><xsl:apply-templates/></pre>
        </a>
      </td>
      <td style="text-align: right;">
        <pre><xsl:value-of select="@size"/></pre>
      </td>
      <td style="text-align: right;">
        <pre><xsl:value-of select="@date"/></pre>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
```

### 如何保护目录列表？

在每个单独的Web应用程序中使用web.xml。请参阅Servlet规范的安全性部分。