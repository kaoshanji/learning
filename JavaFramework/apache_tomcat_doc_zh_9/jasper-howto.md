# 12.JSP页面

Tomcat 9.0使用Jasper 2 JSP Engine来实现[JavaServer Pages 2.3](https://wiki.apache.org/tomcat/Specifications) 规范。

Jasper 2已经过重新设计，以显着提高原始Jasper的性能。除了对常规代码进行了改进之外，还进行了以下更改：

- **JSP自定义标签池** -现在可以将为JSP自定义标签实例化的java对象**池**化并重新使用。这极大地提高了使用自定义标记的JSP页面的性能。
- **后台JSP编译** -如果更改了已经编译过的JSP页面，Jasper 2可以在后台重新编译该页面。先前编译的JSP页面仍可用于处理请求。成功编译新页面后，它将替换旧页面。这有助于提高生产服务器上JSP页面的可用性。
- **当包含的页面发生更改时重新编译JSP** -Jasper 2现在可以检测到在编译时包含在JSP中的页面何时发生了更改，然后重新编译父JSP。
- **用于编译JSP页面**的JDT-Eclipse JDT Java编译器现在用于执行JSP Java源代码编译。该编译器从容器类加载器加载源依赖项。仍然可以使用Ant和javac。

Jasper是使用servlet类实现的 `org.apache.jasper.servlet.JspServlet`。

### 组态

默认情况下，Jasper配置为在进行Web应用程序开发时使用。有关配置Jasper以在生产Tomcat服务器上使用的信息，请参见[ 生产配置](http://tomcat.apache.org/tomcat-9.0-doc/jasper-howto.html#Production_Configuration)部分。

在您的global中，使用init参数配置实现Jasper的servlet `$CATALINA_BASE/conf/web.xml`。

- **checkInterval-**如果开发错误，并且checkInterval大于零，则启用后台编译。checkInterval是两次检查以查看是否需要重新编译JSP页面（及其相关文件）的时间，以秒为单位。默认`0`秒。
- **classdebuginfo-**应该用调试信息编译类文件吗？ `true`或者`false`，默认 `true`。
- **classpath-**定义用于编译生成的servlet的类路径。仅当未设置ServletContext属性org.apache.jasper.Constants.SERVLET_CLASSPATH时，此参数才有效。在Tomcat中使用Jasper时，始终设置此属性。默认情况下，类路径是基于当前Web应用程序动态创建的。
- **编译**器-Ant应该使用哪个编译器来编译JSP页面。有效值与Ant的[javac](https://ant.apache.org/manual/Tasks/javac.html#compilervalues) 任务的编译器属性相同 。如果未设置该值，那么将使用默认的Eclipse JDT Java编译器，而不是使用Ant。没有默认值。如果设置了此属性，那么`setenv.[sh|bat]`应该用来增加 `ant.jar`，`ant-launcher.jar`以及`tools.jar` 对`CLASSPATH`环境变量。
- **compilerSourceVM** -什么JDK版本是源文件兼容？（默认值：`1.8`）
- **compilerTargetVM** -什么JDK版本是生成的文件兼容？（默认值：`1.8`）
- **开发** -Jasper是否在开发模式下使用？如果为true，则可以通过ModifyTestInterval参数指定检查JSP修改的频率。`true`或者`false`，默认`true`。
- **displaySourceFragment-**异常消息中应包含源片段吗？`true`或者`false`，默认`true`。
- **dumpSmap-**是否应将用于JSR45调试的SMAP信息转储到文件中？`true`或者`false`，默认 `false`。`false`如果preventSmap为true。
- **enablePooling-**确定是否启用标记处理程序池。这是一个编译选项。它不会改变已经编译的JSP的行为。`true`或者`false`，默认`true`。
- **engineOptionsClass-**允许指定用于配置Jasper的Options类。如果不存在，将使用默认的EmbeddedServletOptions。如果在SecurityManager下运行，则忽略此选项。
- **errorOnUseBeanInvalidClassAttribute-**当useBean操作中的类属性的值不是有效的bean类时，Jasper是否应发出错误？`true`或者`false`，默认 `true`。
- **fork-**是否已编译Ant fork JSP页面，以便在与Tomcat分开的JVM中执行它们？`true`或者 `false`，默认`true`。
- **genStringAsCharArray-**是否应将文本字符串生成为char数组，以在某些情况下提高性能？默认`false`。
- **ieClassId-**使用<jsp：plugin>标记时要发送到Internet Explorer的class-id值。默认 `clsid:8AD9C840-044E-11D1-B3E9-00805F499D93`。
- **javaEncoding-**用于生成Java源文件的Java文件编码。默认`UTF8`。
- **keepgeneration-**我们应该保留每个页面生成的Java源代码，而不是删除它吗？`true`或者 `false`，默认`true`。
- **映射文件** -我们是否应该在每行输入一条打印语句的情况下生成静态内容，以简化调试工作？ `true`或者`false`，默认`true`。
- **maxLoadedJsps-**将为Web应用程序加载的JSP的最大数量。如果加载的JSP数量超过此数量，则将卸载最近最少使用的JSP，以便在任何一次加载的JSP数量均不超过此限制。零或更少的值表示没有限制。默认`-1`
- **jspIdleTimeout** -JSP在被卸载之前可以空闲的时间（以秒为单位）。零或更少的值表示永不卸载。默认`-1`
- **modificationTestInterval** -导致一个JSP（及其相关文件），以不进行修改从JSP被检查修改的最后时间在指定的时间间隔（以秒）期间进行检查。值为0将导致在每次访问时都要检查JSP。仅在开发模式下使用。默认值为`4`秒。
- **recompileOnFail-**如果JSP编译失败，是否应忽略modificationTestInterval，并且下次访问会触发重新编译尝试？仅在开发模式下使用，并且默认情况下被禁用，因为编译可能很昂贵，并且可能导致过多的资源使用。
- **scratchdir-**编译JSP页面时应该使用哪个草稿目录？默认值为当前Web应用程序的工作目录。如果在SecurityManager下运行，则忽略此选项。
- **suppressSmap** -应该为JSR45调试SMAP信息的生成被抑制？`true`或者`false`，默认 `false`。
- **trimSpaces-**应该从输出（`true`）中删除完全由空格组成的模板文本，将其替换为单个空格（`single`）还是保持不变（`false`）？请注意，如果JSP页面或标记文件指定的`trimDirectiveWhitespaces` 值为`true`，则它将优先于该页面/标记的此配置设置。默认`false`。
- **xpoweredBy-**确定生成的servlet是否添加X-Powered-By响应标头。`true`或者`false`，默认`false`。
- **strictQuoteEscaping-**将scriptlet表达式用于属性值时，是否应严格应用JSP.1.6中用于将引号字符转义的规则？`true`或者`false`，默认 `true`。
- **quoteAttributeEL-**当在JSP页面的属性值中使用EL时，是否应将JSP.1.6中描述的属性引用规则应用于表达式？`true`或者`false`，默认 `true`。

Eclipse JDT中的Java编译器作为默认编译器包含在内。它是一个高级Java编译器，将从Tomcat类加载器加载所有依赖项，当在具有数十个JAR的大型安装中进行编译时，这将极大地帮助您。在快速服务器上，这将允许亚秒级的重新编译周期，即使是大型JSP页面也是如此。

通过如上所述配置编译器属性，可以在以前的Tomcat版本中使用Apache Ant代替新的编译器。

如果您需要更改JSP Servlet的设置应用程序，你可以通过重新定义在JSP的Servlet覆盖默认配置 `/WEB-INF/web.xml`。但是，如果您尝试部署在另一个容器中的应用为JSP Servlet类，可能无法识别，这可能会导致问题。您可以通过使用Tomcat特定的`/WEB-INF/tomcat-web.xml`部署描述符来解决此问题 。格式与相同`/WEB-INF/web.xml`。它将覆盖所有默认设置，但不会覆盖`/WEB-INF/web.xml`。由于它是特定于Tomcat的，因此只有在将应用程序部署在Tomcat上时才进行处理。

### 已知的问题

如 [错误39089中所述](https://bz.apache.org/bugzilla/show_bug.cgi?id=39089)，已知的JVM问题（ [错误6294277）](http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6294277)`java.lang.InternalError: name is too long to represent`在编译非常大的JSP时可能导致 异常。如果观察到这种情况，则可以使用以下方法之一解决该问题：

- 减小JSP的大小
- 通过设置`suppressSmap`为来禁用SMAP生成和JSR-045支持 `true`。

### 生产配置

这是可以做到的主要JSP优化是预编译JSP。但是，这可能是不可能的（例如，在使用jsp-property-group功能时）或不实用，在这种情况下，Jasper servlet的配置变得至关重要。

在生产Tomcat服务器中使用Jasper 2时，应考虑对默认配置进行以下更改。

- **development-**要禁用对JSP页面编译的访问检查，请将其设置为`false`。
- **genStringAsCharArray-**要生成效率更高的char数组，请将其设置为`true`。
- **modifyTestInterval-**如果`true`出于任何原因（例如，动态生成JSP）必须将开发设置为，将其设置为较高的值将大大 提高性能。
- **trimSpaces-**要从响应中删除无用的字节，请将其设置为`true`。

### Web应用程序编译

使用Ant是使用JSPC编译Web应用程序的首选方法。请注意，当预编译JSP时，仅当preventSmap为false且compile为true时，SMAP信息才包括在最终类中。使用下面给出的脚本（“部署程序”下载中包含类似的脚本）来预编译Webapp：

```xml
<project name="Webapp Precompilation" default="all" basedir=".">

   <import file="${tomcat.home}/bin/catalina-tasks.xml"/>

   <target name="jspc">

    <jasper
             validateXml="false"
             uriroot="${webapp.path}"
             webXmlInclude="${webapp.path}/WEB-INF/generated_web.xml"
             outputDir="${webapp.path}/WEB-INF/src" />

  </target>

  <target name="compile">

    <mkdir dir="${webapp.path}/WEB-INF/classes"/>
    <mkdir dir="${webapp.path}/WEB-INF/lib"/>

    <javac destdir="${webapp.path}/WEB-INF/classes"
           debug="on" failonerror="false"
           srcdir="${webapp.path}/WEB-INF/src"
           excludes="**/*.smap">
      <classpath>
        <pathelement location="${webapp.path}/WEB-INF/classes"/>
        <fileset dir="${webapp.path}/WEB-INF/lib">
          <include name="*.jar"/>
        </fileset>
        <pathelement location="${tomcat.home}/lib"/>
        <fileset dir="${tomcat.home}/lib">
          <include name="*.jar"/>
        </fileset>
        <fileset dir="${tomcat.home}/bin">
          <include name="*.jar"/>
        </fileset>
      </classpath>
      <include name="**" />
      <exclude name="tags/**" />
    </javac>

  </target>

  <target name="all" depends="jspc,compile">
  </target>

  <target name="cleanup">
    <delete>
        <fileset dir="${webapp.path}/WEB-INF/src"/>
        <fileset dir="${webapp.path}/WEB-INF/classes/org/apache/jsp"/>
    </delete>
  </target>

</project>
```

以下命令行可用于运行脚本（将令牌替换为Tomcat基本路径和应预编译的webapp路径）：

```
$ANT_HOME/bin/ant -Dtomcat.home=<$TOMCAT_HOME> -Dwebapp.path=<$WEBAPP_PATH>
```

然后，必须将在预编译期间生成的Servlet的声明和映射添加到Web应用程序部署描述符中。将插入文件`${webapp.path}/WEB-INF/generated_web.xml` 内的正确位置`${webapp.path}/WEB-INF/web.xml`。重新启动Web应用程序（使用管理器）并对其进行测试，以验证它是否可以在预编译的Servlet上正常运行。放置在Web应用程序部署描述符中的适当令牌也可以用于使用Ant过滤功能自动插入生成的servlet声明和映射。实际上，这就是在构建过程中如何自动编译与Tomcat一起分发的所有Web应用程序的方式。

在jasper任务中，您可以使用将该选项 与当前Web应用程序部署描述符 `addWebXmlMappings`自动合并。当您想在JSP中使用Java 6功能时，请添加以下javac编译器任务属性： 。对于实时应用程序，您还可以使用禁用调试信息。 `${webapp.path}/WEB-INF/generated_web.xml``${webapp.path}/WEB-INF/web.xml``source="1.6" target="1.6"``debug="off"`

当你不希望在第一个JSP语法错误，使用停止JSP生成 `failOnError="false"`，并与 `showSuccess="true"`所有成功的*JSP中的Java* 代被打印出来。有时，当您在清理生成的Java源文件`${webapp.path}/WEB-INF/src` 并在清理编译JSP servlet类时 ，这非常有帮助`${webapp.path}/WEB-INF/classes/org/apache/jsp`。

**提示：**

- 当您切换到另一个Tomcat版本时，请使用新的Tomcat版本重新生成并重新编译您的JSP。
- 在服务器运行时使用java系统属性来禁用PageContext池 `org.apache.jasper.runtime.JspFactoryImpl.USE_POOL=false`。并限制缓冲 `org.apache.jasper.runtime.BodyContentImpl.LIMIT_BUFFER=true`。请注意，更改默认设置可能会影响性能，但具体取决于应用程序。

### 优化

Jasper内提供了许多扩展点，使用户可以优化其环境的行为。

这些扩展点中的第一个是标签插件机制。这允许将标记处理程序的替代实现提供给Web应用程序使用。标记插件通过`tagPlugins.xml`位于下的文件进行注册`WEB-INF`。Jasper随附了JSTL的示例插件。

第二个扩展点是表达式语言解释器。可以通过配置其他解释器`ServletContext`。有关`ELInterpreterFactory`如何配置备用EL解释器的详细信息，请参见 javadoc。