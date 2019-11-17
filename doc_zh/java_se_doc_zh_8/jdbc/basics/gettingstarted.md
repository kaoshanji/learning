# 入门

本教程附带的示例代码创建了一个数据库，该数据库由一家名为The Coffee Break的小咖啡馆的老板使用，其中咖啡豆由英镑出售，而咖啡则由杯子出售。

以下步骤配置JDBC开发环境，您可以使用该环境编译和运行教程示例：

1. [在您的计算机上安装最新版本的Java SE SDK](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html#step1)
2. [如果需要，请安装数据库管理系统（DBMS）](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html#step2)
3. [从数据库的供应商安装JDBC驱动程序](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html#step3)
4. [安装Apache Ant](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html#step4)
5. [安装Apache Xalan](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html#step5)
6. [下载示例代码](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html#step6)
7. [修改`build.xml`文件](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html#step7)
8. [修改教程属性文件](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html#step8)
9. [编译并打包样本](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html#step9)
10. [创建数据库，表和填充表](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html#step10)
11. [运行示例](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html#step11)

## 在您的计算机上安装最新版本的Java SE SDK

在您的计算机上安装最新版本的Java SE SDK。

确保Java SE SDK `bin`目录的完整目录路径位于`PATH`环境变量中，以便可以从任何目录运行Java编译器和Java应用程序启动器。

## 如果需要，请安装数据库管理系统（DBMS）

您可以使用Java DB，它随最新版本的Java SE SDK一起提供。本教程已针对以下DBMS进行了测试：

- [Java DB](http://www.oracle.com/technetwork/java/javadb/overview/index.html)
- [MySQL](http://www.mysql.com/)

请注意，如果您使用的是其他DBMS，则可能必须更改教程示例的代码。

## 从数据库的供应商安装JDBC驱动程序

如果您使用的是Java DB，则它已经附带了JDBC驱动程序。如果你正在使用MySQL，安装最新版本的[连接器/ J](http://www.mysql.com/products/connector/)。

请与数据库的供应商联系以获取DBMS的JDBC驱动程序。

JDBC驱动程序有许多可能的实现。这些实现分类如下：

- **类型1**：实现JDBC API作为到另一个数据访问API的映射的驱动程序，例如ODBC（开放式数据库连接）。此类驱动程序通常依赖于本机库，这限制了它们的可移植性。JDBC-ODBC Bridge是Type 1驱动程序的示例。

  **注意**：JDBC-ODBC Bridge应被视为过渡解决方案。Oracle不支持它。仅当您的DBMS不提供仅Java的JDBC驱动程序时才考虑使用此选项。

- **类型2**：部分使用Java编程语言编写的驱动程序，部分使用本机代码编写的驱动程序。这些驱动程序使用特定于它们所连接的数据源的本机客户端库。同样，由于本机代码，它们的可移植性是有限的。Oracle的OCI（Oracle调用接口）客户端驱动程序是Type 2驱动程序的一个示例。

- **类型3**：使用纯Java客户端并使用独立于数据库的协议与中间件服务器通信的驱动程序。然后，中间件服务器将客户端的请求传递给数据源。

- **类型4**：纯Java的驱动程序，并为特定数据源实现网络协议。客户端直接连接到数据源。

检查DBMS附带的驱动程序类型。Java DB附带两个Type 4驱动程序，一个嵌入式驱动程序和一个网络客户端驱动程序。MySQL Connector / J是Type 4驱动程序。

安装JDBC驱动程序通常包括将驱动程序复制到计算机，然后将其位置添加到类路径中。此外，除Type 4驱动程序之外的许多JDBC驱动程序都要求您安装客户端API。通常不需要其他特殊配置。

## 安装Apache Ant

这些步骤使用Apache Ant（一种基于Java的工具）来构建，编译和运行JDBC教程示例。转到以下链接下载Apache Ant：

```
http://ant.apache.org/
```

确保Apache Ant可执行文件位于您的`PATH`环境变量中，以便您可以从任何目录运行它。

## 安装Apache Xalan

如果您的DBMS是Java DB `RSSFeedsTable.java`，则[使用SQLXML对象中](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlxml.html)描述的示例需要Apache Xalan。该示例使用Apache Xalan-Java。转到以下链接下载它：

```
http://xml.apache.org/xalan-j/
```

## 下载示例代码

示例代码`JDBCTutorial.zip`包含以下文件：

- ```
  properties
  ```

  - `javadb-build-properties.xml`
  - `javadb-sample-properties.xml`
  - `mysql-build-properties.xml`
  - `mysql-sample-properties.xml`

- ```
  sql
  ```

  - ```
    javadb
    ```

    - `create-procedures.sql`
    - `create-tables.sql`
    - `drop-tables.sql`
    - `populate-tables.sql`

  - ```
    mysql
    ```

    - `create-procedures.sql`
    - `create-tables.sql`
    - `drop-tables.sql`
    - `populate-tables.sql`

- ```
  src/com/oracle/tutorial/jdbc
  ```

  - `CachedRowSetSample.java`
  - `CityFilter.java`
  - `ClobSample.java`
  - `CoffeesFrame.java`
  - `CoffeesTable.java`
  - `CoffeesTableModel.java`
  - `DatalinkSample.java`
  - `ExampleRowSetListener.java`
  - `FilteredRowSetSample.java`
  - `JdbcRowSetSample.java`
  - `JDBCTutorialUtilities.java`
  - `JoinSample.java`
  - `ProductInformationTable.java`
  - `RSSFeedsTable.java`
  - `StateFilter.java`
  - `StoredProcedureJavaDBSample.java`
  - `StoredProcedureMySQLSample.java`
  - `SuppliersTable.java`
  - `WebRowSetSample.java`

- ```
  txt
  ```

  - `colombian-description.txt`

- ```
  xml
  ```

  - `rss-coffee-industry-news.xml`
  - `rss-the-coffee-break-blog.xml`

- `build.xml`

创建一个目录以包含该示例的所有文件。这些步骤将此目录称为`*<JDBC tutorial directory>*`。将内容解压缩`JDBCTutorial.zip`到`*<JDBC tutorial directory>*`。

## 修改build.xml文件

该`build.xml`文件是Apache Ant用于编译和执行JDBC示例的构建文件。这些文件`properties/javadb-build-properties.xml`和`properties/mysql-build-properties.xml`分别包含的Java DB和MySQL，需要额外的Apache Ant属性。文件`properties/javadb-sample-properties.xml`并`properties/mysql-sample-properties.xml`包含示例所需的属性。

修改这些XML文件，如下所示：

### 修改build.xml

在`build.xml`文件中，根据您的DBMS 修改属性`ANTPROPERTIES`以引用`properties/javadb-build-properties.xml`或`properties/mysql-build-properties.xml`。例如，如果您使用的是Java DB，则您的`build.xml`文件将包含以下内容：

```xml
<property
  name="ANTPROPERTIES"
  value="properties/javadb-build-properties.xml"/>

  <import file="${ANTPROPERTIES}"/>
```

同样，如果您使用MySQL，您的`build.xml`文件将包含以下内容：

```xml
<property
  name="ANTPROPERTIES"
  value="properties/mysql-build-properties.xml"/>

  <import file="${ANTPROPERTIES}"/>
```

### 修改特定于数据库的属性文件

在`properties/javadb-build-properties.xml`or `properties/mysql-build-properties.xml`文件中（取决于您的DBMS），修改以下属性，如下表所述：

| 属性                 | 描述                                                         |
| -------------------- | ------------------------------------------------------------ |
| `JAVAC`              | Java编译器的完整路径名， `javac`                             |
| `JAVA`               | Java运行时可执行文件的完整路径名， `java`                    |
| `PROPERTIESFILE`     | 属性文件的名称，`properties/javadb-sample-properties.xml`或者`properties/mysql-sample-properties.xml` |
| `MYSQLDRIVER`        | MySQL驱动程序的完整路径名。对于Connector / J，这通常是。`*<Connector/J installation directory>*/mysql-connector-java-*version-number*.jar` |
| `JAVADBDRIVER`       | Java DB驱动程序的完整路径名。这通常是`*<Java DB installation directory>*/lib/derby.jar`。 |
| `XALANDIRECTORY`     | 包含Apache Xalan的目录的完整路径名。                         |
| `CLASSPATH`          | JDBC教程使用的类路径。*您无需更改此值。*                     |
| `XALAN`              | 文件的完整路径名`xalan.jar`。                                |
| `DB.VENDOR`          | 值`derby`或者`mysql`取决于您是分别使用Java DB还是MySQL。本教程使用此值构造连接到DBMS所需的URL，并标识特定于DBMS的代码和SQL语句。 |
| `DB.DRIVER`          | JDBC驱动程序的标准类名。对于Java DB，这是`org.apache.derby.jdbc.EmbeddedDriver`。对于MySQL，这是`com.mysql.jdbc.Driver`。 |
| `DB.HOST`            | 托管DBMS的计算机的主机名。                                   |
| `DB.PORT`            | 托管DBMS的计算机的端口号。                                   |
| `DB.SID`             | 教程创建和使用的数据库的名称。                               |
| `DB.URL.NEWDATABASE` | 用于在创建新数据库时连接到DBMS的连接URL。*您无需更改此值。*  |
| `DB.URL`             | 用于连接到DBMS的连接URL。*您无需更改此值。*                  |
| `DB.USER`            | 有权访问DBMS中的数据库的用户的名称。                         |
| `DB.PASSWORD`        | 在中指定的用户的密码`DB.USER`。                              |
| `DB.DELIMITER`       | 用于分隔SQL语句的字符。*不要更改此值。*它应该是分号字符（`;`）。 |

## 修改教程属性文件

教程示例使用`properties/javadb-sample-properties.xml`文件或`properties/mysql-sample-properties.xml`文件中的值（取决于您的DBMS）连接到DBMS并初始化数据库和表，如下表所述：

| 属性            | 描述                                                         |
| --------------- | ------------------------------------------------------------ |
| `dbms`          | 值`derby`或者`mysql`取决于您是分别使用Java DB还是MySQL。本教程使用此值构造连接到DBMS所需的URL，并标识特定于DBMS的代码和SQL语句。 |
| `jar_file`      | 包含本教程的所有类文件的JAR文件的完整路径名。                |
| `driver`        | JDBC驱动程序的标准类名。对于Java DB，这是`org.apache.derby.jdbc.EmbeddedDriver`。对于MySQL，这是`com.mysql.jdbc.Driver`。 |
| `database_name` | 教程创建和使用的数据库的名称。                               |
| `user_name`     | 有权访问DBMS中的数据库的用户的名称。                         |
| `password`      | 在中指定的用户的密码`user_name`。                            |
| `server_name`   | 托管DBMS的计算机的主机名。                                   |
| `port_number`   | 托管DBMS的计算机的端口号。                                   |

**注意**：为了简单地演示JDBC API，JDBC教程示例代码不执行已部署系统通常使用的密码管理技术。在生产环境中，您可以遵循Oracle数据库密码管理准则并禁用任何示例帐户。请参阅部分[保护的应用程序设计的密码](http://docs.oracle.com/cd/B28359_01/network.111/b28531/app_devs.htm#CJADABGG)在[面向应用程序开发管理安全性](http://docs.oracle.com/cd/B28359_01/network.111/b28531/app_devs.htm)在[*Oracle数据库安全指南*](http://docs.oracle.com/cd/B28359_01/network.111/b28531/toc.htm)密码管理准则和其他安全建议。

## 编译并打包样本

在命令提示符处，将当前目录更改为`*<JDBC tutorial directory>*`。从此目录中，运行以下命令以编译示例并将其打包在jar文件中：

```bash
ant jar
```

## 创建数据库，表和填充表

如果您使用的是MySQL，请运行以下命令来创建数据库：

```sql
ant create-mysql-database
```

**注意**：在`build.xml`为Java DB创建数据库的文件中不存在相应的Ant目标。用于建立数据库连接的Java DB的数据库URL包括创建数据库的选项（如果它尚不存在）。有关更多信息，请参阅 [建立连接](https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html)。

如果您使用的是Java DB或MySQL，则从同一目录运行以下命令以删除现有的示例数据库表，重新创建表并填充它们。对于Java DB，此命令还会创建数据库（如果该数据库尚不存在）：

```bash
ant setup
```

**注意**：`ant setup`每次运行示例中的一个Java类之前，都应该运行该命令。其中许多样本都希望样本数据库表的内容中包含特定数据。

## 运行示例

`build.xml`文件中的每个目标对应于JDBC示例中的Java类或SQL脚本。下表列出了`build.xml`文件中的目标，每个目标执行的类或脚本，以及每个目标所需的其他类或文件：

| 蚂蚁目标                  | 类或SQL脚本                                                  | 其他必需的类或文件                                        |
| ------------------------- | ------------------------------------------------------------ | --------------------------------------------------------- |
| `javadb-create-procedure` | `javadb/create-procedures.sql`; 查看该`build.xml`文件以查看其他运行的SQL语句 | 没有其他必需的文件                                        |
| `mysql-create-procedure`  | `mysql/create-procedures.sql`。                              | 没有其他必需的文件                                        |
| `run`                     | `JDBCTutorialUtilities`                                      | 没有其他必需的课程                                        |
| `runct`                   | `CoffeesTable`                                               | `JDBCTutorialUtilities`                                   |
| `runst`                   | `SuppliersTable`                                             | `JDBCTutorialUtilities`                                   |
| `runjrs`                  | `JdbcRowSetSample`                                           | `JDBCTutorialUtilities`                                   |
| `runcrs`                  | `CachedRowSetSample`， `ExampleRowSetListener`               | `JDBCTutorialUtilities`                                   |
| `runjoin`                 | `JoinSample`                                                 | `JDBCTutorialUtilities`                                   |
| `runfrs`                  | `FilteredRowSetSample`                                       | `JDBCTutorialUtilities`，`CityFilter`，`StateFilter`      |
| `runwrs`                  | `WebRowSetSample`                                            | `JDBCTutorialUtilities`                                   |
| `runclob`                 | `ClobSample`                                                 | `JDBCTutorialUtilities`， `txt/colombian-description.txt` |
| `runrss`                  | `RSSFeedsTable`                                              | `JDBCTutorialUtilities`，包含在XML文件中`xml`的目录       |
| `rundl`                   | `DatalinkSample`                                             | `JDBCTutorialUtilities`                                   |
| `runspjavadb`             | `StoredProcedureJavaDBSample`                                | `JDBCTutorialUtilities`，`SuppliersTable`，`CoffeesTable` |
| `runspmysql`              | `StoredProcedureMySQLSample`                                 | `JDBCTutorialUtilities`，`SuppliersTable`，`CoffeesTable` |
| `runframe`                | `CoffeesFrame`                                               | `JDBCTutorialUtilities`， `CoffeesTableModel`             |

例如，要运行该类`CoffeesTable`，请将当前目录更改为`*<JDBC tutorial directory>*`，并从此目录运行以下命令：

```bash
ant runct
```

