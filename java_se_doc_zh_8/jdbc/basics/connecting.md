# 建立连接

首先，您需要与要使用的数据源建立连接。数据源可以是DBMS，遗留文件系统或具有相应JDBC驱动程序的某些其他数据源。通常，JDBC应用程序使用以下两个类之一连接到目标数据源：

- `DriverManager`：这个完全实现的类将应用程序连接到数据源，数据源由数据库URL指定。当此类首次尝试建立连接时，它会自动加载在类路径中找到的任何JDBC 4.0驱动程序。请注意，您的应用程序必须在4.0之前手动加载任何JDBC驱动程序。
- `DataSource`：此接口是首选，`DriverManager`因为它允许有关基础数据源的详细信息对您的应用程序透明。甲`DataSource`对象的属性被设定为使得它代表一个特定的数据源。有关更多信息，请参见[使用DataSource对象连接](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html)。有关使用`DataSource`该类开发应用程序的更多信息，请参阅最新*的Java EE教程*。

**注意**：本教程中的示例使用`DriverManager`类而不是`DataSource`类，因为它更易于使用，并且示例不需要`DataSource`类的功能。

此页面包含以下主题：

- [使用DriverManager类](https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html#drivermanager)
- [指定数据库连接URL](https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html#db_connection_url)

## 使用DriverManager类

使用`DriverManager`类连接到DBMS 涉及调用方法`DriverManager.getConnection`。以下方法`JDBCTutorialUtilities.getConnection`建立数据库连接：

```java
public Connection getConnection() throws SQLException {

    Connection conn = null;
    Properties connectionProps = new Properties();
    connectionProps.put("user", this.userName);
    connectionProps.put("password", this.password);

    if (this.dbms.equals("mysql")) {
        conn = DriverManager.getConnection(
                   "jdbc:" + this.dbms + "://" +
                   this.serverName +
                   ":" + this.portNumber + "/",
                   connectionProps);
    } else if (this.dbms.equals("derby")) {
        conn = DriverManager.getConnection(
                   "jdbc:" + this.dbms + ":" +
                   this.dbName +
                   ";create=true",
                   connectionProps);
    }
    System.out.println("Connected to database");
    return conn;
}
```

该方法`DriverManager.getConnection`建立数据库连接。此方法需要数据库URL，具体取决于您的DBMS。以下是数据库URL的一些示例：

1. MySQL：，托管数据库的服务器名称`jdbc:mysql://localhost:3306/`在哪里`localhost`，`3306`是端口号

2. Java DB：，其中是要连接的数据库的名称，并指示DBMS创建数据库。`jdbc:derby:*testdb*;create=true``*testdb*``create=true`

   **注意**：此URL与Java DB Embedded Driver建立数据库连接。Java DB还包括一个网络客户端驱动程序，它使用不同的URL。

此方法指定使用`Properties`对象访问DBMS所需的用户名和密码。

**注意**：

- 通常，在数据库URL中，还可以指定要连接的现有数据库的名称。例如，URL `jdbc:mysql://localhost:3306/mysql`表示名为MySQL数据库的数据库URL `mysql`。本教程中的示例使用未指定特定数据库的URL，因为示例创建了一个新数据库。

- 在以前的JDBC版本中，要获得连接，首先必须通过调用方法初始化JDBC驱动程序`Class.forName`。这种方法需要一个类型的对象`java.sql.Driver`。每个JDBC驱动程序都包含一个或多个实现该接口的类`java.sql.Driver`。Java DB的驱动程序是`org.apache.derby.jdbc.EmbeddedDriver`和`org.apache.derby.jdbc.ClientDriver`，以及MySQL Connector / J 的驱动程序`com.mysql.jdbc.Driver`。请参阅DBMS驱动程序的文档以获取实现该接口的类的名称`java.sql.Driver`。

  在类路径中找到的任何JDBC 4.0驱动程序都会自动加载。（但是，您必须使用该方法在JDBC 4.0之前手动加载任何驱动程序`Class.forName`。）

该方法返回一个`Connection`对象，该对象表示与DBMS或特定数据库的连接。通过此对象查询数据库。

## 指定数据库连接URL

数据库连接URL是DBMS JDBC驱动程序用于连接数据库的字符串。它可以包含诸如搜索数据库的位置，要连接的数据库的名称以及配置属性等信息。数据库连接URL的确切语法由DBMS指定。

### Java DB数据库连接URL

以下是Java DB的数据库连接URL语法：

```properties
jdbc：derby：[ subsubprotocol：] [ databaseName ]
    [; attribute = value ] *
```

- `*subsubprotocol*`指定Java DB应在目录，内存，类路径或JAR文件中搜索数据库的位置。通常省略它。

- `*databaseName*` 是要连接的数据库的名称。

-  attribute=value 表示可选的，以分号分隔的属性列表。这些属性使您可以指示Java DB执行各种任务，包括以下内容：
  - 创建连接URL中指定的数据库。
  - 加密连接URL中指定的数据库。
  - 指定存储日志记录和跟踪信息的目录。
  - 指定用于连接数据库的用户名和密码。

见*的Java DB开发人员指南*和*Java DB的参考手册*从[Java DB的技术文档](http://docs.oracle.com/javadb/index_jdk8.html)以获取更多信息。

### MySQL Connector / J数据库URL

以下是MySQL Connector / J的数据库连接URL语法：

```properties
jdbc：mysql：// [ host ] [，failoverhost ...]
    [：port ] / [ 数据库 ]
    [？propertyName1 ] [= propertyValue1 ]
    [＆propertyName2 ] [= propertyValue2 ] ...
```

- `*host*:*port*`是托管数据库的计算机的主机名和端口号。如果未指定，则默认值为`*host*`和`*port*`分别为127.0.0.1和3306。
- `*database*`是要连接的数据库的名称。如果未指定，则建立连接而不使用默认数据库。
- `*failover*` 是备用数据库的名称（MySQL Connector / J支持故障转移）。
- `*propertyName*=*propertyValue*`表示一个可选的，与＆符号分隔的属性列表。这些属性使您可以指示MySQL Connector / J执行各种任务。