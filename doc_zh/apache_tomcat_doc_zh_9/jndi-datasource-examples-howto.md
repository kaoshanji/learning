# 10.JDBC数据源

### 介绍

JNDI-Resources-HOWTO中广泛介绍了JNDI数据源配置。但是，来自的反馈`tomcat-user`表明，单个配置的细节可能非常棘手。

然后是一些已发布到tomcat-user的流行数据库的示例配置，以及一些有关db使用的常规提示。

您应该意识到，由于这些说明来自于配置和/或发布到`tomcat-user`YMMV :-)的反馈。如果您有对其他用户有用的其他经过测试的配置，或者您认为我们仍然可以改进此部分，请告诉我们。

**请注意，由于Tomcat 7.x和Tomcat 8.x使用不同版本的Apache Commons DBCP库，因此JNDI资源配置有所不同。** 你很可能需要修改旧的JNDI资源配置，以在下面的示例中的语法匹配，以使他们在Tomcat中9.看做工[Tomcat的迁移指南](https://tomcat.apache.org/migration.html) 了解详情。

另外，请注意，一般而言，尤其是本教程，JNDI DataSource配置都假定您已阅读并理解了 [Context](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)和 [Host](http://tomcat.apache.org/tomcat-9.0-doc/config/host.html)配置参考，包括后一参考中有关自动应用程序部署的部分。

### DriverManager，服务提供者机制和内存泄漏

`java.sql.DriverManager`支持 [服务提供者](http://docs.oracle.com/javase/6/docs/api/index.html?java/sql/DriverManager.html)机制。此功能是通过提供`META-INF/services/java.sql.Driver` 文件来自动声明的所有可用JDBC驱动程序都会被自动发现，加载和注册，从而使您无需在创建JDBC连接之前显式加载数据库驱动程序。但是，对于servlet容器环境，该实现在所有Java版本中都基本中断。问题是， `java.sql.DriverManager`仅扫描一次驱动程序。

Apache Tomcat随附的[JRE内存泄漏防护侦听器](http://tomcat.apache.org/tomcat-9.0-doc/config/listeners.html)通过在Tomcat启动期间触发驱动程序扫描来解决此问题。默认情况下启用。这意味着将只扫描公共类加载器及其父级可见的库，以查找数据库驱动程序。这包括，中的驱动程序`$CATALINA_HOME/lib`， `$CATALINA_BASE/lib`类路径以及认可的目录（JRE支持该目录的目录）。打包在Web应用程序中的驱动程序（在 `WEB-INF/lib`）和共享类加载器（已配置的位置）中将不可见，也不会自动加载。如果要考虑禁用此功能，请注意，该扫描将由使用JDBC的第一个Web应用程序触发，从而导致在重新加载此Web应用程序以及其他依赖此功能的Web应用程序时失败。

因此，`WEB-INF/lib`目录中具有数据库驱动程序的Web应用程序 不能依赖服务提供者机制，而应显式注册驱动程序。

其中的驱动程序列表`java.sql.DriverManager`也是已知的内存泄漏源。Web应用程序停止时，必须注销该Web应用程序注册的所有驱动程序。当Web应用程序停止时，Tomcat将尝试自动发现并注销由Web应用程序类加载器加载的所有JDBC驱动程序。但是，期望应用程序通过自己完成此操作`ServletContextListener`。

### 数据库连接池（DBCP 2）配置

Apache Tomcat中的默认数据库连接池实现依赖于[Apache Commons](https://commons.apache.org/)项目中的库 。使用以下库：

- Commons DBCP 2
- Commons Pool 2

这些库位于的单个JAR中 `$CATALINA_HOME/lib/tomcat-dbcp.jar`。但是，仅包括连接池所需的类，并且已对包进行重命名以避免干扰应用程序。

DBCP 2提供了对JDBC 4.1的支持。

#### 安装

有关配置参数的完整列表，请参见[ DBCP 2文档](https://commons.apache.org/dbcp/configuration.html)。

#### 防止数据库连接池泄漏

数据库连接池创建和管理与数据库的连接池。回收和重用数据库的现有连接比打开新连接更有效。

连接池存在一个问题。Web应用程序必须显式关闭ResultSet的，Statement的和Connection的。Web应用程序无法关闭这些资源可能导致它们再也无法重用，即数据库连接池“泄漏”。如果没有更多可用连接，最终可能会导致Web应用程序数据库连接失败。

有一个解决此问题的方法。可以将Apache Commons DBCP 2配置为跟踪和恢复这些废弃的数据库连接。它不仅可以恢复它们，还可以为打开这些资源而从未关闭它们的代码生成堆栈跟踪。

要配置DBCP 2数据源，以便删除和回收废弃的数据库连接`Resource`，请在DBCP 2数据源的配置中添加以下一个或两个属性 ：

```properties
removeAbandonedOnBorrow=true
removeAbandonedOnMaintenance=true
```

这两个属性的默认值为`false`。请注意， `removeAbandonedOnMaintenance`除非通过将池维护设置`timeBetweenEvictionRunsMillis` 为正值来启用池维护，否则该设置无效。有关这些属性的完整文档，请参阅 [DBCP 2文档](https://commons.apache.org/dbcp/configuration.html)。

使用该`removeAbandonedTimeout`属性可以设置数据库连接空闲之前被认为被放弃的秒数。

```properties
removeAbandonedTimeout="60"
```

删除废弃连接的默认超时为300秒。

如果希望DBCP 2记录放弃了数据库连接资源的代码的堆栈跟踪，则`logAbandoned`可以 将该属性设置为`true`。

```properties
logAbandoned="true"
```

默认值为`false`。

#### MySQL DBCP 2示例

##### 0.简介

据报告可以正常工作的[MySQL](https://www.mysql.com/products/mysql/index.html)和JDBC驱动程序版本：

- MySQL 3.23.47，使用InnoDB的MySQL 3.23.47，MySQL 3.23.58，MySQL 4.0.1alpha
- [Connector / J](https://www.mysql.com/products/connector-j) 3.0.11稳定（官方JDBC驱动程序）
- [mm.mysql](http://mmmysql.sourceforge.net/) 2.0.14（旧的第三方JDBC驱动程序）

在继续之前，请不要忘记将JDBC驱动程序的jar复制到中`$CATALINA_HOME/lib`。

##### 1. MySQL配置

确保您遵循这些说明，否则会引起问题。

创建一个新的测试用户，一个新的数据库和一个测试表。您的MySQL用户**必须**分配密码。如果尝试使用空密码连接，驱动程序将失败。

```mysql
mysql> GRANT ALL PRIVILEGES ON *.* TO javauser@localhost
    ->   IDENTIFIED BY 'javadude' WITH GRANT OPTION;
mysql> create database javatest;
mysql> use javatest;
mysql> create table testdata (
    ->   id int not null auto_increment primary key,
    ->   foo varchar(25),
    ->   bar int);
```

> **注意：**测试完成后，应删除上述用户！

接下来，将一些测试数据插入到testdata表中。

```mysql
mysql> insert into testdata values(null, 'hello', 12345);
Query OK, 1 row affected (0.00 sec)

mysql> select * from testdata;
+----+-------+-------+
| ID | FOO   | BAR   |
+----+-------+-------+
|  1 | hello | 12345 |
+----+-------+-------+
1 row in set (0.00 sec)

mysql>
```

##### 2.上下文配置

通过将资源的声明添加到[Context中，](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)在Tomcat中配置JNDI数据源。

例如：

```xml
<Context>

    <!-- maxTotal: Maximum number of database connections in pool. Make sure you
         configure your mysqld max_connections large enough to handle
         all of your db connections. Set to -1 for no limit.
         -->

    <!-- maxIdle: Maximum number of idle database connections to retain in pool.
         Set to -1 for no limit.  See also the DBCP 2 documentation on this
         and the minEvictableIdleTimeMillis configuration parameter.
         -->

    <!-- maxWaitMillis: Maximum time to wait for a database connection to become available
         in ms, in this example 10 seconds. An Exception is thrown if
         this timeout is exceeded.  Set to -1 to wait indefinitely.
         -->

    <!-- username and password: MySQL username and password for database connections  -->

    <!-- driverClassName: Class name for the old mm.mysql JDBC driver is
         org.gjt.mm.mysql.Driver - we recommend using Connector/J though.
         Class name for the official MySQL Connector/J driver is com.mysql.jdbc.Driver.
         -->

    <!-- url: The JDBC connection url for connecting to your MySQL database.
         -->

  <Resource name="jdbc/TestDB" auth="Container" type="javax.sql.DataSource"
               maxTotal="100" maxIdle="30" maxWaitMillis="10000"
               username="javauser" password="javadude" driverClassName="com.mysql.jdbc.Driver"
               url="jdbc:mysql://localhost:3306/javatest"/>

</Context>
```

##### 3. web.xml配置

现在`WEB-INF/web.xml`为此测试应用程序创建一个。

```xml
<web-app xmlns="http://java.sun.com/xml/ns/j2ee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd"
    version="2.4">
  <description>MySQL Test App</description>
  <resource-ref>
      <description>DB Connection</description>
      <res-ref-name>jdbc/TestDB</res-ref-name>
      <res-type>javax.sql.DataSource</res-type>
      <res-auth>Container</res-auth>
  </resource-ref>
</web-app>
```

##### 4.测试代码

现在创建一个简单的`test.jsp`页面，供以后使用。

```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<sql:query var="rs" dataSource="jdbc/TestDB">
select id, foo, bar from testdata
</sql:query>

<html>
  <head>
    <title>DB Test</title>
  </head>
  <body>

  <h2>Results</h2>

<c:forEach var="row" items="${rs.rows}">
    Foo ${row.foo}<br/>
    Bar ${row.bar}<br/>
</c:forEach>

  </body>
</html>
```

该JSP页面使用了 [JSTL](http://www.oracle.com/technetwork/java/index-jsp-135995.html)的SQL和Core标签库。您可以从[Apache Tomcat Taglibs-标准标记库](https://tomcat.apache.org/taglibs/standard/) 项目中获得它 -只需确保获得1.1.x或更高版本即可。拥有JSTL之后，复制`jstl.jar`并复制`standard.jar`到Web应用程序的 `WEB-INF/lib`目录中。

最后，将您的Web应用程序部署`$CATALINA_BASE/webapps`为一个称为warfile的文件`DBTest.war`或一个名为的子目录 `DBTest`

部署后，将浏览器指向， `http://localhost:8080/DBTest/test.jsp`以查看辛勤工作的成果。

#### Oracle 8i，9i和10g

##### 0.简介

除了通常的陷阱外，Oracle对MySQL配置的更改要求极低：-)

较早的Oracle版本的驱动程序可能以* .zip文件而不是* .jar文件的形式分发。Tomcat只使用`*.jar`安装在中的文件 `$CATALINA_HOME/lib`。因此，`classes111.zip` 或`classes12.zip`将需要使用`.jar` 扩展名重命名。由于jarfile是zipfile，因此无需解压缩和jar这些文件-一个简单的重命名就足够了。

对于Oracle 9i及更高版本，您应该使用`oracle.jdbc.OracleDriver` 而不是`oracle.jdbc.driver.OracleDriver`Oracle已声明`oracle.jdbc.driver.OracleDriver`不推荐使用的版本，并且在下一个主要版本中将不再对此驱动程序类的支持。

##### 1.上下文配置

与上述mysql配置类似，您将需要在[Context中](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)定义数据源。在这里，我们使用瘦驱动程序定义了一个名为myoracle的数据源，以用户scott的身份将密码Tiger连接到名为mysid的sid。（注意：对于瘦驱动程序，此sid与tnsname不同）。使用的模式将是用户scott的默认模式。

使用OCI驱动程序应该只涉及更改URL字符串中的oci到Thin。

```xml
<Resource name="jdbc/myoracle" auth="Container"
              type="javax.sql.DataSource" driverClassName="oracle.jdbc.OracleDriver"
              url="jdbc:oracle:thin:@127.0.0.1:1521:mysid"
              username="scott" password="tiger" maxTotal="20" maxIdle="10"
              maxWaitMillis="-1"/>
```

##### 2. web.xml配置

创建应用程序web.xml文件时，应确保遵守DTD定义的元素顺序。

```xml
<resource-ref>
 <description>Oracle Datasource example</description>
 <res-ref-name>jdbc/myoracle</res-ref-name>
 <res-type>javax.sql.DataSource</res-type>
 <res-auth>Container</res-auth>
</resource-ref>
```

##### 3.代码示例

您可以使用与上述相同的示例应用程序（假设您创建了所需的数据库实例，表等），将Datasource代码替换为类似的内容

```java
Context initContext = new InitialContext();
Context envContext  = (Context)initContext.lookup("java:/comp/env");
DataSource ds = (DataSource)envContext.lookup("jdbc/myoracle");
Connection conn = ds.getConnection();
//etc.
```

#### PostgreSQL的

##### 0.简介

PostgreSQL的配置与Oracle类似。

##### 1.所需文件

复制的Postgres JDBC罐子$ CATALINA_HOME / lib目录下。与Oracle一样，jar必须位于此目录中，以便DBCP 2的Classloader找到它们。无论接下来要执行哪个配置步骤，都必须执行此操作。

##### 2.资源配置

您在这里有两个选择：定义一个在所有Tomcat应用程序之间共享的数据源，或者定义一个专门用于一个应用程序的数据源。

###### 2a。共享资源配置

如果您希望定义一个在多个Tomcat应用程序之间共享的数据源，或者仅希望在此文件中定义数据源，请使用此选项。

*尽管没有其他人报道，但该作者在这里没有取得成功。澄清将不胜感激。*

```xml
<Resource name="jdbc/postgres" auth="Container"
          type="javax.sql.DataSource" driverClassName="org.postgresql.Driver"
          url="jdbc:postgresql://127.0.0.1:5432/mydb"
          username="myuser" password="mypasswd" maxTotal="20" maxIdle="10" maxWaitMillis="-1"/>
```

###### 2b。特定于应用程序的资源配置

如果您希望定义特定于您的应用程序的数据源，而其他Tomcat应用程序不可见，请使用此选项。此方法对Tomcat安装的侵入性较小。

为[Context](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)创建资源定义。Context元素应类似于以下内容。

```xml
<Context>

<Resource name="jdbc/postgres" auth="Container"
          type="javax.sql.DataSource" driverClassName="org.postgresql.Driver"
          url="jdbc:postgresql://127.0.0.1:5432/mydb"
          username="myuser" password="mypasswd" maxTotal="20" maxIdle="10"
maxWaitMillis="-1"/>
</Context>
```

##### 3. web.xml配置

```xml
<resource-ref>
 <description>postgreSQL Datasource example</description>
 <res-ref-name>jdbc/postgres</res-ref-name>
 <res-type>javax.sql.DataSource</res-type>
 <res-auth>Container</res-auth>
</resource-ref>
```

##### 4.访问数据源

以编程方式访问数据源时，请记住在 `java:/comp/env`您的JNDI查找之前，如下面的代码片段所示。还请注意，“ jdbc / postgres”可以替换为您喜欢的任何值，前提是您也可以在上述资源定义文件中对其进行更改。

```java
InitialContext cxt = new InitialContext();
if ( cxt == null ) {
   throw new Exception("Uh oh -- no context!");
}

DataSource ds = (DataSource) cxt.lookup( "java:/comp/env/jdbc/postgres" );

if ( ds == null ) {
   throw new Exception("Data source not found!");
}
```

### 非DBCP解决方案

这些解决方案或者利用到数据库的单一连接（除了测试之外，不建议使用其他任何连接！）或其他一些池化技术。

### 带有OCI客户端的Oracle 8i

#### 介绍

尽管不严格解决使用OCI客户端创建JNDI数据源的问题，但这些说明可以与上述Oracle和DBCP 2解决方案结合使用。

为了使用OCI驱动程序，您应该安装了Oracle客户端。您应该已经从cd安装了Oracle8i（8.1.7）客户端，并从[otn.oracle.com](http://otn.oracle.com/)下载了合适的JDBC / OCI驱动程序（Oracle8i 8.1.7.1 JDBC / OCI驱动程序）。

将`classes12.zip`文件重命名`classes12.jar` 为Tomcat之后，将其复制到中`$CATALINA_HOME/lib`。您可能还必须`javax.sql.*`从此文件中删除这些类，具体取决于所使用的Tomcat和JDK的版本。

#### 放在一起

请确保您有`ocijdbc8.dll`或者`.so`在你`$PATH`或`LD_LIBRARY_PATH` （可能`$ORAHOME\bin`），并确认本机库可以用一个简单的测试程序被加载`System.loadLibrary("ocijdbc8");`

接下来，您应该创建一个具有以下**关键行**的简单测试servlet或JSP ：

```java
DriverManager.registerDriver(new
oracle.jdbc.driver.OracleDriver());
conn =
DriverManager.getConnection("jdbc:oracle:oci8:@database","username","password");
```

现在数据库的形式如下：`host:port:SID`如果您尝试访问测试servlet / JSP的URL，并且得到 `ServletException`的根源为`java.lang.UnsatisfiedLinkError:get_env_handle`。

首先，`UnsatisfiedLinkError`表明您有

- JDBC类文件和Oracle客户端版本之间不匹配。这里的赠品是消息，指出找不到所需的库文件。例如，您可能将Oracle版本8.1.6中的classes12.zip文件与版本8.1.5 Oracle客户端一起使用。classesXXX.zip文件和Oracle客户端软件版本必须匹配。
- A `$PATH`，`LD_LIBRARY_PATH`问题。
- 据报道，也可以忽略从otn下载的驱动程序，并使用目录中的classes12.zip文件`$ORAHOME\jdbc\lib`。

接下来，您可能会遇到错误 `ORA-06401 NETCMN: invalid driver designator`

Oracle文档说：“原因：登录（连接）字符串包含无效的驱动程序指示符。操作：更正该字符串并重新提交。” 使用以下命令更改数据库连接字符串（格式为`host:port:SID`）： `(description=(address=(host=myhost)(protocol=tcp)(port=1521))(connect_data=(sid=orcl)))`

*埃德 嗯，如果您整理出TNSName，我认为这不是真的需要-但我不是Oracle DBA ：-)*

### 常见问题

这是使用数据库的Web应用程序遇到的一些常见问题，以及解决这些问题的技巧。

#### 间歇性数据库连接失败

Tomcat在JVM中运行。JVM定期执行垃圾回收（GC）来删除不再使用的Java对象。当JVM执行GC时，Tomcat中的代码将冻结。如果配置用于建立数据库连接的最大时间少于垃圾回收所花费的时间，则可能会导致数据库连接失败。

要收集有关垃圾收集需要多长时间的数据`-verbose:gc`，请`CATALINA_OPTS` 在启动Tomcat时将参数添加 到您的环境变量中。启用详细gc后，您的`$CATALINA_BASE/logs/catalina.out`日志文件将包含每个垃圾收集的数据，包括花费了多长时间。

当您的JVM正确地调整了99％的时间时，GC将花费不到一秒钟的时间。其余的只需几秒钟。很少，如果GC需要花费超过10秒的时间。

确保数据库连接超时设置为10-15秒。对于DBCP 2，可以使用参数进行设置`maxWaitMillis`。

#### 随机连接封闭异常

当一个请求从连接池获取数据库连接并将其关闭两次时，可能会发生这些情况。使用连接池时，关闭连接只是将其返回到池中，以供另一个请求重用，它不会关闭连接。Tomcat使用多个线程来处理并发请求。这是可能在Tomcat中导致此错误的事件序列的示例：

```
  在线程1中运行的请求1获得数据库连接。

  请求1关闭数据库连接。

  JVM将正在运行的线程切换到线程2

  在线程2中运行的请求2获得数据库连接
  （同一数据库连接刚刚被请求1关闭）。

  JVM将正在运行的线程切换回线程1

  请求1在finally块中第二次关闭数据库连接。

  JVM将正在运行的线程切换回线程2

  请求2线程2尝试使用数据库连接，但失败
  因为请求1已将其关闭。
```

这是使用从连接池获得的数据库连接的正确编写的代码示例：

```java
  Connection conn = null;
  Statement stmt = null;  // Or PreparedStatement if needed
  ResultSet rs = null;
  try {
    conn = ... get connection from connection pool ...
    stmt = conn.createStatement("select ...");
    rs = stmt.executeQuery();
    ... iterate through the result set ...
    rs.close();
    rs = null;
    stmt.close();
    stmt = null;
    conn.close(); // Return to connection pool
    conn = null;  // Make sure we don't close it twice
  } catch (SQLException e) {
    ... deal with errors ...
  } finally {
    // Always make sure result sets and statements are closed,
    // and the connection is returned to the pool
    if (rs != null) {
      try { rs.close(); } catch (SQLException e) { ; }
      rs = null;
    }
    if (stmt != null) {
      try { stmt.close(); } catch (SQLException e) { ; }
      stmt = null;
    }
    if (conn != null) {
      try { conn.close(); } catch (SQLException e) { ; }
      conn = null;
    }
  }
```

#### 上下文与GlobalNamingResources

请注意，尽管以上说明将JNDI声明放置在Context元素中，但有时甚至可能希望将这些声明放置在服务器配置文件的 [GlobalNamingResources](http://tomcat.apache.org/tomcat-9.0-doc/config/globalresources.html)部分中。放置在GlobalNamingResources部分中的资源将在服务器的上下文之间共享。

#### JNDI资源命名和领域交互

为了使领域起作用，领域必须引用<GlobalNamingResources>或<Context>部分中定义的数据源，而不是使用<ResourceLink>重命名的数据源。