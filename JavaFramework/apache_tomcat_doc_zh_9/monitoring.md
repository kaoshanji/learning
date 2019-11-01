# 22.监视

### 介绍

监视是系统管理的关键方面。查看正在运行的服务器内部，获取一些统计信息或重新配置应用程序的某些方面，这都是日常管理任务。

### 启用JMX远程

**注意：**仅当您要远程监视Tomcat时才需要此配置。如果要使用与Tomcat一起运行的同一用户在本地对其进行监视，则不需要它。

Oracle网站包含选项列表以及如何在Java 6上配置JMX Remote：[ http](http://docs.oracle.com/javase/6/docs/technotes/guides/management/agent.html) : [//docs.oracle.com/javase/6/docs/technotes/guides/management/agent.html](http://docs.oracle.com/javase/6/docs/technotes/guides/management/agent.html)。

以下是Java 6的快速配置指南：

将以下参数添加到`setenv.bat`Tomcat的脚本中（有关详细信息，请参阅[RUNNING.txt](http://tomcat.apache.org/tomcat-9.0-doc/RUNNING.txt)）。
*注意：*此语法适用于Microsoft Windows。该命令必须在同一行上。它被包装为更具可读性。如果Tomcat作为Windows服务运行，请使用其配置对话框为该服务设置java选项。对于un * xes，请`"set "`从行首删除。

```bash
set CATALINA_OPTS=-Dcom.sun.management.jmxremote
  -Dcom.sun.management.jmxremote.port=%my.jmx.port%
  -Dcom.sun.management.jmxremote.ssl=false
  -Dcom.sun.management.jmxremote.authenticate=false
```

1. 如果您需要授权，请添加并更改此内容：

   ```bash
     -Dcom.sun.management.jmxremote.authenticate=true
     -Dcom.sun.management.jmxremote.password.file=../conf/jmxremote.password
     -Dcom.sun.management.jmxremote.access.file=../conf/jmxremote.access
   ```

2. 编辑访问授权文件

   $ CATALINA_BASE / conf / jmxremote.access

   ：

   ```bash
   monitorRole readonly
   controlRole readwrite
   ```

3. 编辑密码文件

   $ CATALINA_BASE / conf / jmxremote.password

   ：

   ```bash
   monitorRole tomcat
   controlRole tomcat
   ```

   提示

   ：密码文件应该是只读的，并且只能由运行Tomcat的操作系统用户访问。

**注意：** JSR 160 JMX适配器会在随机端口上打开第二个数据通道。当您安装了本地防火墙时，这就是一个问题。要解决此问题，请`JmxRemoteLifecycleListener`按照[侦听器](http://tomcat.apache.org/tomcat-9.0-doc/config/listeners.html)文档中的说明配置一个。

### 使用JMX远程Ant任务管理Tomcat

为了简化Ant 1.6.x的JMX使用，提供了一组可与antlib一起使用的任务。

**antlib**：将catalina-ant.jar从$ CATALINA_HOME / lib复制到$ ANT_HOME / lib。

下面的示例示出了访问器JMX使用：
*注意：*该`name`属性值在这里缠绕成为更具有可读性。它必须全部在同一行上，没有空格。

```xml
<project name="Catalina Ant JMX"
      xmlns:jmx="antlib:org.apache.catalina.ant.jmx"
      default="state"
      basedir=".">
  <property name="jmx.server.name" value="localhost" />
  <property name="jmx.server.port" value="9012" />
  <property name="cluster.server.address" value="192.168.1.75" />
  <property name="cluster.server.port" value="9025" />

  <target name="state" description="Show JMX Cluster state">
    <jmx:open
      host="${jmx.server.name}"
      port="${jmx.server.port}"
      username="controlRole"
      password="tomcat"/>
    <jmx:get
      name=
"Catalina:type=IDataSender,host=localhost,
senderAddress=${cluster.server.address},senderPort=${cluster.server.port}"
      attribute="connected"
      resultproperty="IDataSender.backup.connected"
      echo="false"
    />
    <jmx:get
      name="Catalina:type=ClusterSender,host=localhost"
      attribute="senderObjectNames"
      resultproperty="senderObjectNames"
      echo="false"
    />
    <!-- get current maxActiveSession from ClusterTest application
       echo it to Ant output and store at
       property <em>clustertest.maxActiveSessions.orginal</em>
    -->
    <jmx:get
      name="Catalina:type=Manager,context=/ClusterTest,host=localhost"
      attribute="maxActiveSessions"
      resultproperty="clustertest.maxActiveSessions.orginal"
      echo="true"
    />
    <!-- set maxActiveSession to 100
    -->
    <jmx:set
      name="Catalina:type=Manager,context=/ClusterTest,host=localhost"
      attribute="maxActiveSessions"
      value="100"
      type="int"
    />
    <!-- get all sessions and split result as delimiter <em>SPACE</em> for easy
       access all session ids directly with Ant property sessions.[0..n].
    -->
    <jmx:invoke
      name="Catalina:type=Manager,context=/ClusterTest,host=localhost"
      operation="listSessionIds"
      resultproperty="sessions"
      echo="false"
      delimiter=" "
    />
    <!-- Access session attribute <em>Hello</em> from first session.
    -->
    <jmx:invoke
      name="Catalina:type=Manager,context=/ClusterTest,host=localhost"
      operation="getSessionAttribute"
      resultproperty="Hello"
      echo="false"
    >
      <arg value="${sessions.0}"/>
      <arg value="Hello"/>
    </jmx:invoke>
    <!-- Query for all application manager.of the server from all hosts
       and bind all attributes from all found manager MBeans.
    -->
    <jmx:query
      name="Catalina:type=Manager,*"
      resultproperty="manager"
      echo="true"
      attributebinding="true"
    />
    <!-- echo the create properties -->
<echo>
senderObjectNames: ${senderObjectNames.0}
IDataSender.backup.connected: ${IDataSender.backup.connected}
session: ${sessions.0}
manager.length: ${manager.length}
manager.0.name: ${manager.0.name}
manager.1.name: ${manager.1.name}
hello: ${Hello}
manager.ClusterTest.0.name: ${manager.ClusterTest.0.name}
manager.ClusterTest.0.activeSessions: ${manager.ClusterTest.0.activeSessions}
manager.ClusterTest.0.counterSend_EVT_SESSION_EXPIRED:
 ${manager.ClusterTest.0.counterSend_EVT_SESSION_EXPIRED}
manager.ClusterTest.0.counterSend_EVT_GET_ALL_SESSIONS:
 ${manager.ClusterTest.0.counterSend_EVT_GET_ALL_SESSIONS}
</echo>

  </target>

</project>
```

**import：**使用*<import file =“ $ {CATALINA.HOME} /bin/catalina-tasks.xml” />*导入JMX Accessor项目， 并使用*jmxOpen*，*jmxSet*，*jmxGet*， *jmxQuery*，*jmxInvoke*，*jmxEquals*和*jmxCondition*引用任务。

### JMXAccessorOpenTask-JMX打开连接任务

属性清单

| 属性     | 描述                                                         | 默认值       |
| :------- | :----------------------------------------------------------- | :----------- |
| url      | 设置JMX连接URL- *service：jmx：rmi：/// jndi / rmi：// localhost：8050 / jmxrmi* |              |
| host     | 设置主机，快捷方式非常长的URL语法。                          | `localhost`  |
| port     | 设置远程连接端口                                             | `8050`       |
| username | 远程JMX连接用户名。                                          |              |
| password | 远程JMX连接密码。                                            |              |
| ref      | 内部连接引用的名称。使用此属性，您可以在同一Ant项目中配置更多一个连接。 | `jmx.server` |
| echo     | 回显命令用法（用于访问分析或调试）                           | `false`      |
| if       | 仅当当前项目中**存在**给定名称的属性时才执行。               |              |
| unless   | 仅当当前项目中**不存在**给定名称的属性时才执行。             |              |

打开新的JMX连接的示例

```xml
  <jmx:open
    host="${jmx.server.name}"
    port="${jmx.server.port}"
  />
```

通过URL打开具有授权的JMX连接并在其他参考处进行存储的示例

```xml
  <jmx:open
    url="service:jmx:rmi:///jndi/rmi://localhost:9024/jmxrmi"
    ref="jmx.server.9024"
    username="controlRole"
    password="tomcat"
  />
```

打开具有授权的URL并通过其他参考存储JMX连接的示例，但仅当属性*jmx.if*存在且 *jmx.unless*不存在时

```xml
  <jmx:open
    url="service:jmx:rmi:///jndi/rmi://localhost:9024/jmxrmi"
    ref="jmx.server.9024"
    username="controlRole"
    password="tomcat"
    if="jmx.if"
    unless="jmx.unless"
  />
```

**注意**：*jmxOpen*任务的所有属性在所有其他任务和条件下也存在。

### JMXAccessorGetTask：获取属性值Ant任务

属性清单

| 属性                 | 描述                                                         | 默认值       |
| :------------------- | :----------------------------------------------------------- | :----------- |
| name                 | 完全限定的JMX ObjectName- *Catalina：type = Server*          |              |
| attribute            | 现有的MBean属性（请参阅上面的Tomcat MBean描述）              |              |
| ref                  | JMX连接参考                                                  | `jmx.server` |
| echo                 | 回声命令用法（访问和结果）                                   | `false`      |
| resultproperty       | 将结果保存在此项目属性中                                     |              |
| delimiter            | 使用定界符（java.util.StringTokenizer）分割结果，并使用resultproperty作为前缀来存储令牌。 |              |
| separatearrayresults | 当返回值是数组时，将结果保存为属性列表（*$ resultproperty。[0..N]*和*$ resultproperty.length*） | `true`       |

从默认JMX连接获取远程MBean属性的示例

```xml
  <jmx:get
    name="Catalina:type=Manager,context=/servlets-examples,host=localhost"
    attribute="maxActiveSessions"
    resultproperty="servlets-examples.maxActiveSessions"
  />
```

获取和结果数组并将其拆分为单独属性的示例

```xml
  <jmx:get
      name="Catalina:type=ClusterSender,host=localhost"
      attribute="senderObjectNames"
      resultproperty="senderObjectNames"
  />
```

通过以下方式访问senderObjectNames属性：

```bash
  ${senderObjectNames.length} give the number of returned sender list.
  ${senderObjectNames.[0..N]} found all sender object names
```

仅在配置群集时使IDataSender属性连接的示例。
*注意：*将`name`属性值包装在此处以便于阅读。它必须全部在同一行上，没有空格。

```xml
  <jmx:query
    failonerror="false"
    name="Catalina:type=Cluster,host=${tomcat.application.host}"
    resultproperty="cluster"
  />
  <jmx:get
    name=
"Catalina:type=IDataSender,host=${tomcat.application.host},
senderAddress=${cluster.backup.address},senderPort=${cluster.backup.port}"
    attribute="connected"
    resultproperty="datasender.connected"
    if="cluster.0.name" />
```

### JMXAccessorSetTask：设置属性值Ant任务

属性清单

| 属性      | 描述                                                | 默认值             |
| :-------- | :-------------------------------------------------- | :----------------- |
| name      | 完全限定的JMX ObjectName- *Catalina：type = Server* |                    |
| attribute | 现有的MBean属性（请参阅上面的Tomcat MBean描述）     |                    |
| value     | 设置为属性的值                                      |                    |
| type      | 属性的类型。                                        | `java.lang.String` |
| ref       | JMX连接参考                                         | `jmx.server`       |
| echo      | 回声命令用法（访问和结果）                          | `false`            |

设置远程MBean属性值的示例

```xml
  <jmx:set
    name="Catalina:type=Manager,context=/servlets-examples,host=localhost"
    attribute="maxActiveSessions"
    value="500"
    type="int"
  />
```

### JMXAccessorInvokeTask：调用MBean操作Ant任务

属性清单

| 属性                 | 描述                                                         | 默认值       |
| :------------------- | :----------------------------------------------------------- | :----------- |
| name                 | 完全限定的JMX ObjectName- *Catalina：type = Server*          |              |
| operation            | 现有的MBean操作（请参阅Tomcat [funcspecs / fs-admin-opers.html](http://tomcat.apache.org/tomcat-9.0-doc/funcspecs/fs-admin-opers.html)）。 |              |
| ref                  | JMX连接参考                                                  | `jmx.server` |
| echo                 | 回声命令用法（访问和结果）                                   | `false`      |
| resultproperty       | 将结果保存在此项目属性中                                     |              |
| delimiter            | 使用定界符（java.util.StringTokenizer）分割结果，并使用resultproperty作为前缀来存储令牌。 |              |
| separatearrayresults | 当返回值是数组时，将结果保存为属性列表（*$ resultproperty。[0..N]*和*$ resultproperty.length*） | `true`       |

停止申请

```xml
  <jmx:invoke
    name="Catalina:type=Manager,context=/servlets-examples,host=localhost"
    operation="stop"/>
```

现在，您可以在*$ {sessions。[0..N}]*属性中找到sessionid，并使用$ {sessions.length}属性访问计数。

获取所有会话ID的示例

```xml
  <jmx:invoke
    name="Catalina:type=Manager,context=/servlets-examples,host=localhost"
    operation="listSessionIds"
    resultproperty="sessions"
    delimiter=" "
  />
```

现在，您可以在*$ {sessions。[0..N}]*属性中找到sessionid，并使用$ {sessions.length}属性访问计数。

从会话$ {sessionid.0}获取远程MBean会话属性的示例

```xml
  <jmx:invoke
    name="Catalina:type=Manager,context=/ClusterTest,host=localhost"
    operation="getSessionAttribute"
    resultproperty="hello">
     <arg value="${sessionid.0}"/>
     <arg value="Hello" />
  </jmx:invoke>
```

在vhost *本地主机*上创建新的访问记录器阀门的示例

```xml
 <jmx:invoke
         name="Catalina:type=MBeanFactory"
         operation="createAccessLoggerValve"
         resultproperty="accessLoggerObjectName"
 >
     <arg value="Catalina:type=Host,host=localhost"/>
 </jmx:invoke>
```

现在，您可以找到名称存储在*$ {accessLoggerObjectName}* 属性中的新MBean 。

### JMXAccessorQueryTask：查询MBean Ant任务

属性清单

| 属性                 | 描述                                                         | 默认值       |
| :------------------- | :----------------------------------------------------------- | :----------- |
| name                 | JMX ObjectName查询字符串*-Catalina：type = Manager，**       |              |
| ref                  | JMX连接参考                                                  | `jmx.server` |
| echo                 | 回声命令用法（访问和结果）                                   | `false`      |
| resultproperty       | 为所有已建立的MBean前缀项目属性名称（*mbeans。[0..N] .objectname*） |              |
| attributebinding     | 绑定除*名称*之外的所有MBean属性                              | `false`      |
| delimiter            | 使用定界符（java.util.StringTokenizer）分割结果，并使用resultproperty作为前缀来存储令牌。 |              |
| separatearrayresults | 当返回值是数组时，将结果保存为属性列表（*$ resultproperty。[0..N]*和*$ resultproperty.length*） | `true`       |

从所有服务和主机获取所有Manager ObjectNames

```xml
  <jmx:query
    name="Catalina:type=Manager,*
    resultproperty="manager" />
```

现在，您可以在*$ {manager。[0..N] .name}* 属性中找到会话管理器，并使用$ {manager.length}属性访问结果对象计数器。

从*servlet-examples*应用程序获取Manager 并绑定所有MBean属性的 *示例*

```xml
  <jmx:query
    name="Catalina:type=Manager,context=/servlet-examples,host=localhost*"
    attributebinding="true"
    resultproperty="manager.servletExamples" />
```

现在，您可以在*$ {manager.servletExamples.0.name}*属性中找到管理器，并可以使用*$ {manager.servletExamples.0。[manager属性名称]* } 从该管理器访问所有属性。MBeans的结果对象计数器存储在$ {manager.length}属性中。

从服务器获取所有MBean并将其存储在外部XML属性文件中的示例

```xml
<project name="jmx.query"
            xmlns:jmx="antlib:org.apache.catalina.ant.jmx"
            default="query-all" basedir=".">
<property name="jmx.host" value="localhost"/>
<property name="jmx.port" value="8050"/>
<property name="jmx.username" value="controlRole"/>
<property name="jmx.password" value="tomcat"/>

<target name="query-all" description="Query all MBeans of a server">
  <!-- Configure connection -->
  <jmx:open
    host="${jmx.host}"
    port="${jmx.port}"
    ref="jmx.server"
    username="${jmx.username}"
    password="${jmx.password}"/>

  <!-- Query MBean list -->
  <jmx:query
    name="*:*"
    resultproperty="mbeans"
    attributebinding="false"/>

  <echoproperties
    destfile="mbeans.properties"
    prefix="mbeans."
    format="xml"/>

  <!-- Print results -->
  <echo message=
    "Number of MBeans in server ${jmx.host}:${jmx.port} is ${mbeans.length}"/>
</target>
</project>
```

现在，您可以在文件*mbeans.properties中*找到所有MBean 。

### JMXAccessorCreateTask：远程创建MBean Ant任务

属性清单

| 属性        | 描述                                                         | 默认值       |
| :---------- | :----------------------------------------------------------- | :----------- |
| name        | 完全限定的JMX ObjectName- *Catalina：type = MBeanFactory*    |              |
| classname   | 现有的MBean完整合格的类名（请参阅上面的Tomcat MBean描述）    |              |
| classLoader | 服务器或Web应用程序类加载器的ObjectName （*Catalina：type = ServerClassLoader，name = [服务器，公共，共享]*或 *Catalina：type = WebappClassLoader，context = / myapps，host = localhost*） |              |
| ref         | JMX连接参考                                                  | `jmx.server` |
| echo        | 回声命令用法（访问和结果）                                   | `false`      |

例如创建远程MBean

```xml
  <jmx:create
    ref="${jmx.reference}"
    name="Catalina:type=MBeanFactory"
    className="org.apache.commons.modeler.BaseModelMBean"
    classLoader="Catalina:type=ServerClassLoader,name=server">
    <arg value="org.apache.catalina.mbeans.MBeanFactory" />
  </jmx:create>
```

**警告**：许多Tomcat MBean一旦
创建就无法链接到其父级。Valve，Cluster和Realm MBean不会自动
与其父级连接。请改用*MBeanFactory*创建
操作。

### JMXAccessorUnregisterTask：远程注销MBean Ant任务

属性清单

| 属性 | 描述                                                      | 默认值       |
| :--- | :-------------------------------------------------------- | :----------- |
| name | 完全限定的JMX ObjectName- *Catalina：type = MBeanFactory* |              |
| ref  | JMX连接参考                                               | `jmx.server` |
| echo | 回声命令用法（访问和结果）                                | `false`      |

取消注册远程MBean的示例

```xml
  <jmx:unregister
    name="Catalina:type=MBeanFactory"
  />
```

**警告**：不能取消注册许多Tomcat MBean。
MBean并未与其父级解除链接。请改用*MBeanFactory*
删除操作。

### JMXAccessorCondition：表达条件

属性清单

| 属性            | 描述                                                         | 默认值       |
| :-------------- | :----------------------------------------------------------- | :----------- |
| url             | 设置JMX连接URL- *service：jmx：rmi：/// jndi / rmi：// localhost：8050 / jmxrmi* |              |
| host            | 设置主机，快捷方式非常长的URL语法。                          | `localhost`  |
| port            | 设置远程连接端口                                             | `8050`       |
| username        | 远程JMX连接用户名。                                          |              |
| password        | 远程JMX连接密码。                                            |              |
| ref             | 内部连接引用的名称。使用此属性，您可以在同一Ant项目中配置更多一个连接。 | `jmx.server` |
| name            | 完全限定的JMX ObjectName- *Catalina：type = Server*          |              |
| echo            | 回声条件的使用（访问和结果）                                 | `false`      |
| if              | 仅当当前项目中**存在**给定名称的属性时才执行。               |              |
| unless          | 仅当当前项目中**不存在**给定名称的属性时才执行。             |              |
| value(required) | 操作的第二个参数                                             |              |
| type            | 表示操作的值类型（支持*long*和*double*）                     | `long`       |
| operation       | 表达一个==等于！=不等于>大于（＆gt;）> =大于或等于（＆gt; =）<小于（＆lt;）<=小于或等于（＆lt; =） | `==`         |

等待服务器连接，并且该群集备份节点可访问

```xml
<target name="wait">
  <waitfor maxwait="${maxwait}" maxwaitunit="second" timeoutproperty="server.timeout" >
    <and>
      <socket server="${server.name}" port="${server.port}"/>
      <http url="${url}"/>
      <jmx:condition
        operation="=="
        host="localhost"
        port="9014"
        username="controlRole"
        password="tomcat"
        name=
"Catalina:type=IDataSender,host=localhost,senderAddress=192.168.111.1,senderPort=9025"
        attribute="connected"
        value="true"
      />
    </and>
  </waitfor>
  <fail if="server.timeout" message="Server ${url} don't answer inside ${maxwait} sec" />
  <echo message="Server ${url} alive" />
</target>
```

### JMXAccessorEqualsCondition：等于MBean Ant条件

属性清单

| 属性     | 描述                                                         | 默认值       |
| :------- | :----------------------------------------------------------- | :----------- |
| url      | 设置JMX连接URL- *service：jmx：rmi：/// jndi / rmi：// localhost：8050 / jmxrmi* |              |
| host     | 设置主机，快捷方式非常长的URL语法。                          | `localhost`  |
| port     | 设置远程连接端口                                             | `8050`       |
| username | 远程JMX连接用户名。                                          |              |
| password | 远程JMX连接密码。                                            |              |
| ref      | 内部连接引用的名称。使用此属性，您可以在同一Ant项目中配置更多一个连接。 | `jmx.server` |
| name     | 完全限定的JMX ObjectName- *Catalina：type = Server*          |              |
| echo     | 回声条件的使用（访问和结果）                                 | `false`      |

等待服务器连接，并且该群集备份节点可访问

```xml
<target name="wait">
  <waitfor maxwait="${maxwait}" maxwaitunit="second" timeoutproperty="server.timeout" >
    <and>
      <socket server="${server.name}" port="${server.port}"/>
      <http url="${url}"/>
      <jmx:equals
        host="localhost"
        port="9014"
        username="controlRole"
        password="tomcat"
        name=
"Catalina:type=IDataSender,host=localhost,senderAddress=192.168.111.1,senderPort=9025"
        attribute="connected"
        value="true"
      />
    </and>
  </waitfor>
  <fail if="server.timeout" message="Server ${url} don't answer inside ${maxwait} sec" />
  <echo message="Server ${url} alive" />
</target>
```

### 使用JMXProxyServlet

Tomcat提供了使用远程（甚至本地）JMX连接的替代方法，同时仍使您可以访问JMX必须提供的所有功能：Tomcat的 [JMXProxyServlet](http://tomcat.apache.org/tomcat-9.0-doc/api/org/apache/catalina/manager/JMXProxyServlet.html)。

JMXProxyServlet允许客户端通过HTTP接口发出JMX查询。与直接从客户端程序使用JMX相比，该技术具有以下优点：

- 您不必启动完整的JVM并建立远程JMX连接，而只需从正在运行的服务器中请求一小部分数据
- 您不必知道如何使用JMX连接
- 您不需要本页面其余部分介绍的任何复杂配置
- 您的客户端程序不必用Java编写

在流行的服务器监视软件（例如Nagios或Icinga）中，可以看到JMX过度杀伤的一个完美示例：如果要通过JMX监视10个项目，则必须启动10个JVM，建立10个JMX连接，然后关闭他们每隔几分钟就下来。使用JMXProxyServlet，您可以建立10个HTTP连接并完成它。

您可以在[Tomcat管理器](http://tomcat.apache.org/tomcat-9.0-doc/manager-howto.html#Using_the_JMX_Proxy_Servlet)的文档中找到有关JMXProxyServlet的更多信息 。