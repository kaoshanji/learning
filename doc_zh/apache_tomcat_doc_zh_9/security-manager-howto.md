# 8.安全管理

### 背景

Java **SecurityManager**允许Web浏览器在其自己的沙箱中运行小程序，以防止不受信任的代码访问本地文件系统上的文件，连接到加载小程序的主机之外的其他主机，依此类推。以与SecurityManager保护您免受浏览器中运行的不受信任小程序相同的方式，在运行Tomcat时使用SecurityManager可以保护您的服务器免受特洛伊木马servlet，JSP，JSP Bean和标记库的侵害。甚至是无心的错误。

想象一下，如果某人被授权在您的站点上发布JSP，则无意中将以下内容包括在其JSP中：

```
<% System.exit(1); %>
```

Tomcat每次执行此JSP时，Tomcat都会退出。使用Java SecurityManager只是系统管理员可以用来保持服务器安全可靠的另一道防线。

**警告** -已经使用Tomcat代码库进行了安全审核。大多数关键软件包已受到保护，并且已实施新的安全软件包保护机制。不过，在允许不受信任的用户发布Web应用程序，JSP，Servlet，Bean或标记库之前，请确保您对SecurityManager的配置感到满意。 **但是，使用SecurityManager运行绝对比不使用SecurityManager运行更好。**

### 权限

权限类用于定义Tomcat加载的类将具有哪些权限。有许多Permission类是JDK的标准部分，您可以创建自己的Permission类以用于自己的Web应用程序。这两种技术都在Tomcat中使用。

#### 标准权限

这只是适用于Tomcat的标准系统SecurityManager Permission类的简短摘要。有关 更多信息，请参见 http://docs.oracle.com/javase/7/docs/technotes/guides/security/。

- **java.util.PropertyPermission-**控制对JVM属性（例如）的读/写访问`java.home`。
- **java.lang.RuntimePermission-**控制某些系统/运行时功能（如`exit()`和）的使用 `exec()`。还控制软件包的访问/定义。
- **java.io.FilePermission-**控制对文件和目录的读/写/执行访问。
- **java.net.SocketPermission-**控制网络套接字的使用。
- **java.net.NetPermission-**控制多播网络连接的使用。
- **java.lang.reflect.ReflectPermission-**控制使用反射进行类自省。
- **java.security.SecurityPermission-**控制对Security方法的访问。
- **java.security.AllPermission-**允许访问所有权限，就像您在没有SecurityManager的情况下运行Tomcat一样。

### 使用SecurityManager配置Tomcat

### 策略文件格式

在`$CATALINA_BASE/conf/catalina.policy`文件中配置了Java SecurityManager实施的安全策略。该文件将完全替换`java.policy`JDK系统目录中存在的文件。`catalina.policy`可以手动编辑该文件，也可以使用 Java 1.2或更高版本随附的 [policytool](http://docs.oracle.com/javase/6/docs/technotes/guides/security/PolicyGuide.html)应用程序。

`catalina.policy`文件中的条目使用标准 `java.policy`文件格式，如下所示：

```
// Example policy file entry

grant [signedBy <signer>,] [codeBase <code source>] {
  permission  <class>  [<name> [, <action list>]];
};
```

授予权限时，**signedBy**和**codeBase**条目是可选的。注释行以“ //”开头，并在当前行的结尾处结束。的`codeBase`是在URL的形式，和用于文件的URL可以使用`${java.home}` 和`${catalina.home}`属性（其扩展了由为它们定义的目录路径`JAVA_HOME`， `CATALINA_HOME`和`CATALINA_BASE`环境变量）。

### 默认策略文件

默认`$CATALINA_BASE/conf/catalina.policy`文件如下所示：

```
// Licensed to the Apache Software Foundation (ASF) under one or more
// contributor license agreements.  See the NOTICE file distributed with
// this work for additional information regarding copyright ownership.
// The ASF licenses this file to You under the Apache License, Version 2.0
// (the "License"); you may not use this file except in compliance with
// the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// ============================================================================
// catalina.policy - Security Policy Permissions for Tomcat
//
// This file contains a default set of security policies to be enforced (by the
// JVM) when Catalina is executed with the "-security" option.  In addition
// to the permissions granted here, the following additional permissions are
// granted to each web application:
//
// * Read access to the web application's document root directory
// * Read, write and delete access to the web application's working directory
// ============================================================================


// ========== SYSTEM CODE PERMISSIONS =========================================


// These permissions apply to javac
grant codeBase "file:${java.home}/lib/-" {
        permission java.security.AllPermission;
};

// These permissions apply to all shared system extensions
grant codeBase "file:${java.home}/jre/lib/ext/-" {
        permission java.security.AllPermission;
};

// These permissions apply to javac when ${java.home] points at $JAVA_HOME/jre
grant codeBase "file:${java.home}/../lib/-" {
        permission java.security.AllPermission;
};

// These permissions apply to all shared system extensions when
// ${java.home} points at $JAVA_HOME/jre
grant codeBase "file:${java.home}/lib/ext/-" {
        permission java.security.AllPermission;
};


// ========== CATALINA CODE PERMISSIONS =======================================


// These permissions apply to the daemon code
grant codeBase "file:${catalina.home}/bin/commons-daemon.jar" {
        permission java.security.AllPermission;
};

// These permissions apply to the logging API
// Note: If tomcat-juli.jar is in ${catalina.base} and not in ${catalina.home},
// update this section accordingly.
//  grant codeBase "file:${catalina.base}/bin/tomcat-juli.jar" {..}
grant codeBase "file:${catalina.home}/bin/tomcat-juli.jar" {
        permission java.io.FilePermission
         "${java.home}${file.separator}lib${file.separator}logging.properties", "read";

        permission java.io.FilePermission
         "${catalina.base}${file.separator}conf${file.separator}logging.properties", "read";
        permission java.io.FilePermission
         "${catalina.base}${file.separator}logs", "read, write";
        permission java.io.FilePermission
         "${catalina.base}${file.separator}logs${file.separator}*", "read, write, delete";

        permission java.lang.RuntimePermission "shutdownHooks";
        permission java.lang.RuntimePermission "getClassLoader";
        permission java.lang.RuntimePermission "setContextClassLoader";

        permission java.lang.management.ManagementPermission "monitor";

        permission java.util.logging.LoggingPermission "control";

        permission java.util.PropertyPermission "java.util.logging.config.class", "read";
        permission java.util.PropertyPermission "java.util.logging.config.file", "read";
        permission java.util.PropertyPermission "org.apache.juli.AsyncLoggerPollInterval", "read";
        permission java.util.PropertyPermission "org.apache.juli.AsyncMaxRecordCount", "read";
        permission java.util.PropertyPermission "org.apache.juli.AsyncOverflowDropType", "read";
        permission java.util.PropertyPermission "org.apache.juli.ClassLoaderLogManager.debug", "read";
        permission java.util.PropertyPermission "catalina.base", "read";

        // Note: To enable per context logging configuration, permit read access to
        // the appropriate file. Be sure that the logging configuration is
        // secure before enabling such access.
        // E.g. for the examples web application (uncomment and unwrap
        // the following to be on a single line):
        // permission java.io.FilePermission "${catalina.base}${file.separator}
        //  webapps${file.separator}examples${file.separator}WEB-INF
        //  ${file.separator}classes${file.separator}logging.properties", "read";
};

// These permissions apply to the server startup code
grant codeBase "file:${catalina.home}/bin/bootstrap.jar" {
        permission java.security.AllPermission;
};

// These permissions apply to the servlet API classes
// and those that are shared across all class loaders
// located in the "lib" directory
grant codeBase "file:${catalina.home}/lib/-" {
        permission java.security.AllPermission;
};


// If using a per instance lib directory, i.e. ${catalina.base}/lib,
// then the following permission will need to be uncommented
// grant codeBase "file:${catalina.base}/lib/-" {
//         permission java.security.AllPermission;
// };


// ========== WEB APPLICATION PERMISSIONS =====================================


// These permissions are granted by default to all web applications
// In addition, a web application will be given a read FilePermission
// for all files and directories in its document root.
grant {
    // Required for JNDI lookup of named JDBC DataSource's and
    // javamail named MimePart DataSource used to send mail
    permission java.util.PropertyPermission "java.home", "read";
    permission java.util.PropertyPermission "java.naming.*", "read";
    permission java.util.PropertyPermission "javax.sql.*", "read";

    // OS Specific properties to allow read access
    permission java.util.PropertyPermission "os.name", "read";
    permission java.util.PropertyPermission "os.version", "read";
    permission java.util.PropertyPermission "os.arch", "read";
    permission java.util.PropertyPermission "file.separator", "read";
    permission java.util.PropertyPermission "path.separator", "read";
    permission java.util.PropertyPermission "line.separator", "read";

    // JVM properties to allow read access
    permission java.util.PropertyPermission "java.version", "read";
    permission java.util.PropertyPermission "java.vendor", "read";
    permission java.util.PropertyPermission "java.vendor.url", "read";
    permission java.util.PropertyPermission "java.class.version", "read";
    permission java.util.PropertyPermission "java.specification.version", "read";
    permission java.util.PropertyPermission "java.specification.vendor", "read";
    permission java.util.PropertyPermission "java.specification.name", "read";

    permission java.util.PropertyPermission "java.vm.specification.version", "read";
    permission java.util.PropertyPermission "java.vm.specification.vendor", "read";
    permission java.util.PropertyPermission "java.vm.specification.name", "read";
    permission java.util.PropertyPermission "java.vm.version", "read";
    permission java.util.PropertyPermission "java.vm.vendor", "read";
    permission java.util.PropertyPermission "java.vm.name", "read";

    // Required for OpenJMX
    permission java.lang.RuntimePermission "getAttribute";

    // Allow read of JAXP compliant XML parser debug
    permission java.util.PropertyPermission "jaxp.debug", "read";

    // All JSPs need to be able to read this package
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.tomcat";

    // Precompiled JSPs need access to these packages.
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.jasper.el";
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.jasper.runtime";
    permission java.lang.RuntimePermission
     "accessClassInPackage.org.apache.jasper.runtime.*";

    // Applications using WebSocket need to be able to access these packages
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.tomcat.websocket";
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.tomcat.websocket.server";
};


// The Manager application needs access to the following packages to support the
// session display functionality. It also requires the custom Tomcat
// DeployXmlPermission to enable the use of META-INF/context.xml
// These settings support the following configurations:
// - default CATALINA_HOME == CATALINA_BASE
// - CATALINA_HOME != CATALINA_BASE, per instance Manager in CATALINA_BASE
// - CATALINA_HOME != CATALINA_BASE, shared Manager in CATALINA_HOME
grant codeBase "file:${catalina.base}/webapps/manager/-" {
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.catalina";
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.catalina.ha.session";
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.catalina.manager";
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.catalina.manager.util";
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.catalina.util";
    permission org.apache.catalina.security.DeployXmlPermission "manager";
};
grant codeBase "file:${catalina.home}/webapps/manager/-" {
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.catalina";
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.catalina.ha.session";
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.catalina.manager";
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.catalina.manager.util";
    permission java.lang.RuntimePermission "accessClassInPackage.org.apache.catalina.util";
    permission org.apache.catalina.security.DeployXmlPermission "manager";
};

// The Host Manager application needs the custom Tomcat DeployXmlPermission to
// enable the use of META-INF/context.xml
// These settings support the following configurations:
// - default CATALINA_HOME == CATALINA_BASE
// - CATALINA_HOME != CATALINA_BASE, per instance Host Manager in CATALINA_BASE
// - CATALINA_HOME != CATALINA_BASE, shared Host Manager in CATALINA_HOME
grant codeBase "file:${catalina.base}/webapps/host-manager/-" {
    permission org.apache.catalina.security.DeployXmlPermission "host-manager";
};
grant codeBase "file:${catalina.home}/webapps/host-manager/-" {
    permission org.apache.catalina.security.DeployXmlPermission "host-manager";
};


// You can assign additional permissions to particular web applications by
// adding additional "grant" entries here, based on the code base for that
// application, /WEB-INF/classes/, or /WEB-INF/lib/ jar files.
//
// Different permissions can be granted to JSP pages, classes loaded from
// the /WEB-INF/classes/ directory, all jar files in the /WEB-INF/lib/
// directory, or even to individual jar files in the /WEB-INF/lib/ directory.
//
// For instance, assume that the standard "examples" application
// included a JDBC driver that needed to establish a network connection to the
// corresponding database and used the scrape taglib to get the weather from
// the NOAA web server.  You might create a "grant" entries like this:
//
// The permissions granted to the context root directory apply to JSP pages.
// grant codeBase "file:${catalina.base}/webapps/examples/-" {
//      permission java.net.SocketPermission "dbhost.mycompany.com:5432", "connect";
//      permission java.net.SocketPermission "*.noaa.gov:80", "connect";
// };
//
// The permissions granted to the context WEB-INF/classes directory
// grant codeBase "file:${catalina.base}/webapps/examples/WEB-INF/classes/-" {
// };
//
// The permission granted to your JDBC driver
// grant codeBase "jar:file:${catalina.base}/webapps/examples/WEB-INF/lib/driver.jar!/-" {
//      permission java.net.SocketPermission "dbhost.mycompany.com:5432", "connect";
// };
// The permission granted to the scrape taglib
// grant codeBase "jar:file:${catalina.base}/webapps/examples/WEB-INF/lib/scrape.jar!/-" {
//      permission java.net.SocketPermission "*.noaa.gov:80", "connect";
// };

// To grant permissions for web applications using packed WAR files, use the
// Tomcat specific WAR url scheme.
//
// The permissions granted to the entire web application
// grant codeBase "war:file:${catalina.base}/webapps/examples.war*/-" {
// };
//
// The permissions granted to a specific JAR
// grant codeBase "war:file:${catalina.base}/webapps/examples.war*/WEB-INF/lib/foo.jar" {
// };
```

### 使用SecurityManager启动Tomcat

一旦配置了`catalina.policy`文件以便与SecurityManager一起使用，就可以使用“ -security”选项通过适当的SecurityManager来启动Tomcat：

```
$CATALINA_HOME/bin/catalina.sh start -security    (Unix)
%CATALINA_HOME%\bin\catalina start -security      (Windows)
```

#### 打包的WAR文件的权限

使用打包的WAR文件时，有必要使用Tomcat的自定义war URL协议为Web应用程序代码分配权限。

要将权限分配给整个Web应用程序，策略文件中的条目应如下所示：

```
// Example policy file entry
grant codeBase "war:file:${catalina.base}/webapps/examples.war*/-" {
    ...
};
```

要将权限分配给Web应用程序中的单个JAR，策略文件中的条目应如下所示：

```
// Example policy file entry
grant codeBase "war:file:${catalina.base}/webapps/examples.war*/WEB-INF/lib/foo.jar" {
    ...
};
```

### 在Tomcat中配置软件包保护

从Tomcat 5开始，现在可以配置保护哪些Tomcat内部软件包不受软件包定义和访问的影响。有关 更多信息，请参见 http://www.oracle.com/technetwork/java/seccodeguide-139067.html。

**警告**：请注意，删除默认的软件包保护可能会打开一个安全漏洞

### 默认属性文件

默认`$CATALINA_BASE/conf/catalina.properties`文件如下所示：

```
#
# List of comma-separated packages that start with or equal this string
# will cause a security exception to be thrown when
# passed to checkPackageAccess unless the
# corresponding RuntimePermission ("accessClassInPackage."+package) has
# been granted.
package.access=sun.,org.apache.catalina.,org.apache.coyote.,org.apache.tomcat.,
org.apache.jasper.
#
# List of comma-separated packages that start with or equal this string
# will cause a security exception to be thrown when
# passed to checkPackageDefinition unless the
# corresponding RuntimePermission ("defineClassInPackage."+package) has
# been granted.
#
# by default, no packages are restricted for definition, and none of
# the class loaders supplied with the JDK call checkPackageDefinition.
#
package.definition=sun.,java.,org.apache.catalina.,org.apache.coyote.,
org.apache.tomcat.,org.apache.jasper.
```

配置`catalina.properties`文件以与SecurityManager一起使用后，请记住重新启动Tomcat。

### 故障排除

如果您的Web应用程序尝试执行由于缺少必需的权限而被禁止的操作，则 当SecurityManager检测到违规时，它将抛出 `AccessControLException`或`SecurityException`。调试缺少的权限可能很困难，并且一种选择是打开执行过程中制定的所有安全决策的调试输出。这是通过在启动Tomcat之前设置系统属性来完成的。最简单的方法是通过`CATALINA_OPTS`环境变量。执行以下命令：

```
export CATALINA_OPTS=-Djava.security.debug=all    (Unix)
set CATALINA_OPTS=-Djava.security.debug=all       (Windows)
```

在启动Tomcat之前。

**警告** -这将产生*许多兆字节* 的输出！但是，它可以通过搜索单词“ FAILED”并确定要检查的权限来帮助您查找问题。有关您也可以在此处指定的更多选项的信息，请参见Java安全性文档。