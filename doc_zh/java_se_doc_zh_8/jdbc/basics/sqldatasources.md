# 连接DataSource对象

本节介绍`DataSource`对象，这是获取数据源连接的首选方法。除了将在后面解释的其他优点之外，`DataSource`对象还可以提供连接池和分布式事务。此功能对于企业数据库计算至关重要。特别是，它是Enterprise JavaBeans（EJB）技术不可或缺的一部分。

本节介绍如何使用`DataSource`接口获取连接以及如何使用分布式事务和连接池。这两者都涉及JDBC应用程序中很少的代码更改。

部署使这些操作成为可能的类（系统管理员通常使用工具（例如Apache Tomcat或Oracle WebLogic Server）执行）所执行的工作因`DataSource`所部署的对象类型而异。因此，本节的大部分内容专门用于展示系统管理员如何设置环境，以便程序员可以使用`DataSource`对象来获取连接。

涵盖以下主题：

- [使用DataSource对象获取连接](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html#datasource_connection)
- [部署基本数据源对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html#deploy_datasource)
- [部署其他DataSource实现](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html#datasource_implementation)
- [获取和使用池连接](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html#pooled_connection)
- [部署分布式事务](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html#deployment_distributed_transactions)
- [使用分布式事务的连接](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html#using_connections_distributed_transactions)

## 使用DataSource对象获取连接

在“ [建立连接”中](connecting.md)，您学习了如何使用`DriverManager`该类建立连接。本节介绍如何使用`DataSource`对象获取与数据源的连接，这是首选方法。

由实现`DataSource`代表特定DBMS或某些其他数据源（例如文件）的类实例化的对象。甲`DataSource`对象表示一个特定的DBMS或一些其它数据源，诸如一个文件。如果公司使用多个数据源，它将`DataSource`为每个数据源部署一个单独的对象。该`DataSource`接口由驱动程序供应商实现。它可以通过三种不同的方式实现：

- 基本`DataSource`实现生成`Connection`未在分布式事务中池化或使用的标准对象。
- 甲`DataSource`支持连接池的实施产生`Connection`参与连接池，即对象，可以回收再利用的连接。
- 甲`DataSource`支持分布式事务执行产生`Connection`，可以在分布式事务，即，访问两个或多个DBMS服务器事务中使用的对象。

JDBC驱动程序至少应包含一个基本`DataSource`实现。例如，Java DB JDBC驱动程序包括`org.apache.derby.jdbc.ClientDataSource`MySQL 的实现和`com.mysql.jdbc.jdbc2.optional.MysqlDataSource`。如果您的客户端在Java 8 compact profile 2上运行，那么Java DB JDBC驱动程序就是`org.apache.derby.jdbc.BasicClientDataSource40`。本教程的示例需要紧凑的配置文件3或更高版本。

一个`DataSource`支持分布式事务类通常还实现了连接池的支持。例如，`DataSource`EJB供应商提供的类几乎总是支持连接池和分布式事务。

假设从之前的例子中，兴旺的咖啡店连锁店的所有者决定通过互联网销售咖啡进一步扩大。由于预计会有大量的在线业务，所有者肯定需要连接池。打开和关闭连接涉及大量开销，并且所有者预计该在线订购系统将需要大量的查询和更新。通过连接池，可以反复使用连接池，从而避免为每个数据库访问创建新连接的费用。此外，所有者现在拥有第二个DBMS，其中包含最近收购的咖啡烘焙公司的数据。这意味着所有者希望能够编写使用旧DBMS服务器和新DBMS服务器的分布式事务。

连锁店主已重新配置计算机系统，以服务于更大的新客户群。所有者购买了最新的JDBC驱动程序和与其一起使用的EJB应用程序服务器，以便能够使用分布式事务并获得连接池带来的更高性能。许多JDBC驱动程序与最近购买的EJB服务器兼容。所有者现在具有三层体系结构，中间层有一个新的EJB应用服务器和JDBC驱动程序，第二层有两个DBMS服务器。发出请求的客户端计算机是第一层。

## 部署基本数据源对象

系统管理员需要部署`DataSource`对象，以便Coffee Break的编程团队可以开始使用它们。部署`DataSource`对象包含三个任务：

1. 创建`DataSource`类的实例
2. 设置其属性
3. 将其注册为使用Java命名和目录接口（JNDI）API的命名服务

首先，考虑最基本的情况，即使用`DataSource`接口的基本实现，即不支持连接池或分布式事务的实现。在这种情况下，只`DataSource`需要部署一个对象。基本实现`DataSource`产生类生成的相同类型的连接`DriverManager`。

### 创建DataSource类的实例并设置其属性

假设一个只想要基本实现的`DataSource`公司从JDBC供应商DB Access，Inc。购买了一个驱动程序。该驱动程序包含`com.dbaccess.BasicDataSource`实现该`DataSource`接口的类。以下代码摘录创建类的实例`BasicDataSource`并设置其属性。`BasicDataSource`部署实例后，程序员可以调用该方法`DataSource.getConnection`来获取与公司数据库的连接`CUSTOMER_ACCOUNTS`。首先，系统管理员使用默认构造函数创建`BasicDataSource`对象`*ds*`。然后系统管理员设置三个属性。请注意，以下代码通常由部署工具执行：

```java
com.dbaccess.BasicDataSource ds = new com.dbaccess.BasicDataSource();
ds.setServerName("grinder");
ds.setDatabaseName("CUSTOMER_ACCOUNTS");
ds.setDescription("Customer accounts database for billing");
```

该变量`*ds*`现在表示`CUSTOMER_ACCOUNTS`服务器上安装的数据库。`BasicDataSource`对象生成的任何连接`*ds*`都将是与数据库的连接`CUSTOMER_ACCOUNTS`。

### 使用使用JNDI API的命名服务注册DataSource对象

通过设置属性，系统管理员可以`BasicDataSource`使用JNDI（Java命名和目录接口）命名服务注册该对象。使用的特定命名服务通常由系统属性确定，此处未显示。以下代码摘录注册`BasicDataSource`对象并将其与逻辑名称绑定`jdbc/billingDB`：

```java
Context ctx = new InitialContext();
ctx.bind("jdbc/billingDB", ds);
```

此代码使用JNDI API。第一行创建一个`InitialContext`对象，该对象用作名称的起始点，类似于文件系统中的根目录。第二行将`BasicDataSource`对象关联或绑定`*ds*`到逻辑名称`jdbc/billingDB`。在下一个代码摘录中，您为命名服务提供此逻辑名称，并返回该`BasicDataSource`对象。逻辑名称可以是任何字符串。在这种情况下，公司决定使用该名称`billingDB`作为`CUSTOMER_ACCOUNTS`数据库的逻辑名称。

在前面的示例中，`jdbc`是初始上下文下的子上下文，就像根目录下的目录是子目录一样。该名称`jdbc/billingDB`类似于路径名，其中路径中的最后一项类似于文件名。在这种情况下，`billingDB`是给`BasicDataSource`对象的逻辑名称`*ds*`。子上下文`jdbc`保留用于绑定到`DataSource`对象的逻辑名称，因此`jdbc`将始终是数据源的逻辑名称的第一部分。

### 使用已部署的DataSource对象

在`DataSource`系统管理员部署基本实现之后，程序员可以使用它。这意味着程序员可以提供绑定到`DataSource`类实例的逻辑数据源名称，JNDI命名服务将返回`DataSource`该类的实例。`getConnection`然后可以在该`DataSource`对象上调用该方法以获得与其表示的数据源的连接。例如，程序员可能会编写以下两行代码来获取`DataSource`生成与数据库连接的对象`CUSTOMER_ACCOUNTS`。

```java
Context ctx = new InitialContext();
DataSource ds = (DataSource)ctx.lookup("jdbc/billingDB");
```

第一行代码获取初始上下文作为检索`DataSource`对象的起点。`jdbc/billingDB`为方法提供逻辑名称时`lookup`，该方法将返回`DataSource`系统管理员`jdbc/billingDB`在部署时绑定的对象。因为方法的返回值`lookup`是Java `Object`，所以`DataSource`在将其赋值给变量之前，必须将其转换为更具体的类型`*ds*`。

该变量`*ds*`是`com.dbaccess.BasicDataSource`实现`DataSource`接口的类的实例。调用该方法`*ds*.getConnection`会生成与`CUSTOMER_ACCOUNTS`数据库的连接。

```java
Connection con = ds.getConnection("fernanda","brewed");
```

该`getConnection`方法仅需要用户名和密码，因为该变量`*ds*`具有`CUSTOMER_ACCOUNTS`在其属性中与数据库建立连接所需的其余信息，例如数据库名称和位置。

### DataSource对象的优点

由于其属性，`DataSource`对象是`DriverManager`获取连接的类的更好选择。程序员不再需要在其应用程序中对驱动程序名称或JDBC URL进行硬编码，这使得它们更具可移植性。此外，`DataSource`属性使维护代码更加简单。如果有更改，系统管理员可以更新数据源属性，而不用担心更改连接到数据源的每个应用程序。例如，如果将数据源移动到其他服务器，则系统管理员必须将该`serverName`属性设置为新服务器名称。

除了便携性和易维护性之外，使用`DataSource`对象获取连接还可以提供其他优势。当实现`DataSource`接口以使用`ConnectionPoolDataSource`实现时，`DataSource`该类的实例生成的所有连接将自动成为池连接。类似地，当`DataSource`实现实现以使用`XADataSource`类时，它生成的所有连接将自动成为可以在分布式事务中使用的连接。下一节将介绍如何部署这些类型的`DataSource`实现。

## 部署其他DataSource实现

系统管理员或以该容量工作的其他人可以部署`DataSource`对象，以便它生成的连接是池连接。为此，他或她首先部署一个`ConnectionPoolDataSource`对象，然后部署一个`DataSource`实现它的对象。`ConnectionPoolDataSource`设置对象的属性，使其表示将生成连接的数据源。`ConnectionPoolDataSource`使用JNDI命名服务注册对象后，将`DataSource`部署该对象。通常，只能为`DataSource`对象设置两个属性：`description`和`dataSourceName`。赋予该`dataSourceName`属性的值是标识`ConnectionPoolDataSource`先前部署的对象的逻辑名称，该对象包含进行连接所需的属性。

通过部署`ConnectionPoolDataSource`和`DataSource`对象，您可以`DataSource.getConnection`在`DataSource`对象上调用方法并获得池化连接。此连接将是`ConnectionPoolDataSource`对象属性中指定的数据源。

以下示例描述了The Coffee Break的系统管理员如何部署`DataSource`实现的对象以提供池化连接。系统管理员通常使用部署工具，因此本节中显示的代码片段是部署工具将执行的代码。

为了获得更好的性能，The Coffee Break公司从DB Access，Inc。购买了一个JDBC驱动程序，该驱动程序包含`com.dbaccess.ConnectionPoolDS`实现该`ConnectionPoolDataSource`接口的类。系统管理员创建创建此类的实例，设置其属性，并将其注册到JNDI命名服务。Coffee Break 从其EJB服务器供应商Application Logic，Inc。购买了它的`DataSource`类`com.applogic.PooledDataSource`。该类`com.applogic.PooledDataSource`通过使用`ConnectionPoolDataSource`类提供的底层支持来实现连接池`com.dbaccess.ConnectionPoolDS`。

`ConnectionPoolDataSource`必须首先部署该对象。以下代码创建`com.dbaccess.ConnectionPoolDS`并设置其属性的实例：

```java
com.dbaccess.ConnectionPoolDS cpds = new com.dbaccess.ConnectionPoolDS();
cpds.setServerName("creamer");
cpds.setDatabaseName("COFFEEBREAK");
cpds.setPortNumber(9040);
cpds.setDescription("Connection pooling for " + "COFFEEBREAK DBMS");
```

`ConnectionPoolDataSource`部署对象后，系统管理员将部署该`DataSource`对象。以下代码使用JNDI命名服务注册该`com.dbaccess.ConnectionPoolDS`对象`*cpds*`。请注意，与`*cpds*`变量关联的逻辑名称在子上下文中`pool`添加了子上下文`jdbc`，这类似于将子目录添加到分层文件系统中的另一个子目录。该类的任何实例的逻辑名称`com.dbaccess.ConnectionPoolDS`将始终以`jdbc/pool`。Oracle建议将所有`ConnectionPoolDataSource`对象放在子上下文中`jdbc/pool`：

```java
Context ctx = new InitialContext();
ctx.bind("jdbc/pool/fastCoffeeDB", cpds);
```

接下来，部署`DataSource`实现与类的`*cpds*`变量和其他实例交互的`com.dbaccess.ConnectionPoolDS`类。以下代码创建此类的实例并设置其属性。请注意，此实例仅设置了两个属性`com.applogic.PooledDataSource`。`description`设置该属性是因为它始终是必需的。设置的另一个属性`dataSourceName`为逻辑JNDI名称提供`*cpds*`，该名称是`com.dbaccess.ConnectionPoolDS`类的实例。换句话说，`*cpds*`表示`ConnectionPoolDataSource`将实现对象的连接池的`DataSource`对象。

以下代码（可能由部署工具执行）创建`PooledDataSource`对象，设置其属性并将其绑定到逻辑名称`jdbc/fastCoffeeDB`：

```java
com.applogic.PooledDataSource ds = new com.applogic.PooledDataSource();
ds.setDescription("produces pooled connections to COFFEEBREAK");
ds.setDataSourceName("jdbc/pool/fastCoffeeDB");
Context ctx = new InitialContext();
ctx.bind("jdbc/fastCoffeeDB", ds);
```

此时，`DataSource`部署了一个对象，应用程序可以从该对象获得与数据库的池连接`COFFEEBREAK`。

## 获取和使用池连接

一个*连接池*是数据库连接对象的缓存。对象表示应用程序可用于连接到数据库的物理数据库连接。在运行时，应用程序从池请求连接。如果池包含可以满足请求的连接，则它将返回与应用程序的连接。如果未找到任何连接，则会创建新连接并将其返回给应用程序。应用程序使用连接对数据库执行某些操作，然后将对象返回池。然后，该连接可用于下一个连接请求。

连接池可以促进连接对象的重用，并减少创建连接对象的次数。连接池显着提高了数据库密集型应用程序的性能，因为创建连接对象在时间和资源方面都很昂贵。

现在已经部署了这些`DataSource`和`ConnectionPoolDataSource`对象，程序员可以使用该`DataSource`对象来获得池化连接。获取池连接的代码就像获取非池连接的代码一样，如以下两行所示：

```java
ctx = new InitialContext();
ds = (DataSource)ctx.lookup("jdbc/fastCoffeeDB");
```

该变量`*ds*`表示一个`DataSource`对象，该对象生成与数据库的池连接`COFFEEBREAK`。您只需要检索`DataSource`一次该对象，因为您可以根据需要使用它来生成尽可能多的池化连接。`getConnection`在`*ds*`变量上调用方法会自动生成池连接，因为变量表示的`DataSource`对象`*ds*`被配置为生成池连接。

连接池通常对程序员是透明的。使用池化连接时，只需要执行两项操作：

1. 使用`DataSource`对象而不是`DriverManager`类来获取连接。在下面的代码行中，`*ds*`是一个`DataSource`实现和部署的对象，它将创建池连接，`username`并且`password`是表示有权访问数据库的用户凭据的变量：

   ```java
   Connection con = ds.getConnection(username, password);
   ```

2. 使用`finally`语句关闭池连接。在适用于使用池化连接的代码`finally`的`try/catch`块之后，将出现以下块：

   ```
   try {
       Connection con = ds.getConnection(username, password);
       // ... code to use the pooled
       // connection con
   } catch (Exception ex {
       // ... code to handle exceptions
   } finally {
       if (con != null) con.close();
   }
   ```

否则，使用池化连接的应用程序与使用常规连接的应用程序相同。应用程序员在完成连接池时可能会注意到的另一件事是性能更好。

以下示例代码获取一个`DataSource`对象，该对象生成与数据库的连接`COFFEEBREAK`并使用它来更新表中的价格`COFFEES`：

```java
import java.sql.*;
import javax.sql.*;
import javax.ejb.*;
import javax.naming.*;

public class ConnectionPoolingBean implements SessionBean {

    // ...

    public void ejbCreate() throws CreateException {
        ctx = new InitialContext();
        ds = (DataSource)ctx.lookup("jdbc/fastCoffeeDB");
    }

    public void updatePrice(float price, String cofName,
                            String username, String password)
        throws SQLException{

        Connection con;
        PreparedStatement pstmt;
        try {
            con = ds.getConnection(username, password);
            con.setAutoCommit(false);
            pstmt = con.prepareStatement("UPDATE COFFEES " +
                        "SET PRICE = ? " +
                        "WHERE COF_NAME = ?");
            pstmt.setFloat(1, price);
            pstmt.setString(2, cofName);
            pstmt.executeUpdate();

            con.commit();
            pstmt.close();

        } finally {
            if (con != null) con.close();
        }
    }

    private DataSource ds = null;
    private Context ctx = null;
}
```

此代码示例中的连接参与连接池，因为以下情况属实：

- `ConnectionPoolDataSource`已部署实现类的实例。
- `DataSource`已部署实现类的实例，并且为其`dataSourceName`属性设置的值是绑定到先前部署的`ConnectionPoolDataSource`对象的逻辑名称。

请注意，尽管此代码与您之前看到的代码非常相似，但它在以下方面有所不同：

- 它进口`javax.sql`，`javax.ejb`和`javax.naming`包除`java.sql`。

  的`DataSource`和`ConnectionPoolDataSource`接口处于`javax.sql`封装，JNDI构造`InitialContext`和方法`Context.lookup`是的一部分`javax.naming`封装。此特定示例代码采用EJB组件的形式，该组件使用`javax.ejb`包中的API 。此示例的目的是显示您使用池化连接的方式与使用非池化连接的方式相同，因此您无需担心理解EJB API。

- 它使用`DataSource`对象来获取连接而不是使用`DriverManager`工具。

- 它使用`finally`块来确保连接已关闭。

获取和使用池化连接类似于获取和使用常规连接。当充当系统管理员的人正确部署了`ConnectionPoolDataSource`对象和`DataSource`对象时，应用程序使用该`DataSource`对象来获得池化连接。但是，应用程序应使用`finally`块来关闭池化连接。为简单起见，前面的示例使用了一个`finally`块但没有`catch`块。如果`try`块中的方法抛出异常，则默认情况下将抛出该异常，并且该`finally`子句将在任何情况下执行。

## 部署分布式事务

`DataSource`可以部署对象以获取可在分布式事务中使用的连接。与连接池一样，必须部署两个不同的类实例：一个`XADataSource`对象和一个`DataSource`实现与之一起使用的对象。

假设The Coffee Break企业家购买的EJB服务器包含`DataSource`类`com.applogic.TransactionalDS`，`XADataSource`该类与类等一起工作`com.dbaccess.XATransactionalDS`。它适用于任何`XADataSource`类的事实使EJB服务器可以跨JDBC驱动程序移植。当`DataSource`与`XADataSource`物体的部署，产生的连接将能够参与分布式事务。在这种情况下，实现类`com.applogic.TransactionalDS`以便生成的连接也是池连接，这通常是`DataSource`作为EJB服务器实现的一部分提供的类的情况。

`XADataSource`必须首先部署该对象。以下代码创建`com.dbaccess.XATransactionalDS`并设置其属性的实例：

```java
com.dbaccess.XATransactionalDS xads = new com.dbaccess.XATransactionalDS();
xads.setServerName("creamer");
xads.setDatabaseName("COFFEEBREAK");
xads.setPortNumber(9040);
xads.setDescription("Distributed transactions for COFFEEBREAK DBMS");
```

以下代码使用JNDI命名服务注册该`com.dbaccess.XATransactionalDS`对象`*xads*`。请注意，与之关联的逻辑名称后面添加`*xads*`了子上下文。Oracle建议始终以该类的任何实例的逻辑名开头。`xa``jdbc``com.dbaccess.XATransactionalDS``jdbc/xa`

```java
Context ctx = new InitialContext();
ctx.bind("jdbc/xa/distCoffeeDB", xads);
```

接下来，部署`DataSource`实现`*xads*`与其他`XADataSource`对象交互的对象。请注意，`DataSource`该类`com.applogic.TransactionalDS`可以与`XADataSource`任何JDBC驱动程序供应商的类一起使用。部署`DataSource`对象涉及创建`com.applogic.TransactionalDS`类的实例并设置其属性。该`dataSourceName`属性设置为`jdbc/xa/distCoffeeDB`与之关联的逻辑名称`com.dbaccess.XATransactionalDS`。这是实现`XADataSource`类的分布式事务功能的`DataSource`类。以下代码部署了`DataSource`该类的实例：

```java
com.applogic.TransactionalDS ds = new com.applogic.TransactionalDS();
ds.setDescription("Produces distributed transaction " +
                  "connections to COFFEEBREAK");
ds.setDataSourceName("jdbc/xa/distCoffeeDB");
Context ctx = new InitialContext();
ctx.bind("jdbc/distCoffeeDB", ds);
```

既然类的实例`com.applogic.TransactionalDS`，并`com.dbaccess.XATransactionalDS`已经部署，应用程序可以调用该方法`getConnection`的实例`TransactionalDS`类来获取到的连接`COFFEEBREAK`可在分布式事务中使用的数据库。

## 使用分布式事务的连接

要获得可用于分布式事务的连接，必须使用`DataSource`已正确实现和部署的对象，如“ [部署分布式事务](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html#deployment_distributed_transactions) ”一节中所示。使用这样的`DataSource`对象，`getConnection`在其上调用方法。连接后，使用它就像使用任何其他连接一样。因为`jdbc/distCoffeesDB`已与`XADataSource`JNDI命名服务中的对象关联，所以以下代码生成`Connection`可在分布式事务中使用的对象：

```java
Context ctx = new InitialContext();
DataSource ds = (DataSource)ctx.lookup("jdbc/distCoffeesDB");
Connection con = ds.getConnection();
```

当它是分布式事务的一部分时，对如何使用此连接存在一些次要但重要的限制。事务管理器控制分布式事务何时开始以及何时提交或回滚; 因此，应用程序代码永远不应该调用方法`Connection.commit`或`Connection.rollback`。应用程序同样也不应该调用`Connection.setAutoCommit(true)`，它启用自动提交模式，因为这也会干扰事务管理器对事务边界的控制。这解释了为什么在分布式事务范围内创建的新连接默认情况下禁用其自动提交模式。请注意，这些限制仅适用于连接参与分布式事务的情况; 连接不是分布式事务的一部分时没有限制。

对于以下示例，假设已发送咖啡订单，这会触发对位于不同DBMS服务器上的两个表的更新。第一个表是新`INVENTORY`表，第二个`COFFEES`表是表。由于这些表位于不同的DBMS服务器上，因此涉及它们的事务将是分布式事务。以下示例中的代码获取连接，更新`COFFEES`表并关闭连接，是分布式事务的第二部分。

请注意，代码未显式提交或回滚更新，因为分布式事务的范围由中间层服务器的底层系统基础结构控制。此外，假设用于分布式事务的连接是池连接，应用程序使用`finally`块来关闭连接。这可以保证即使抛出异常也会关闭有效连接，从而确保连接返回到连接池以进行回收。

以下代码示例演示了一个企业Bean，它是一个实现客户端计算机可以调用的方法的类。这个例子的目的是说明用于分布式事务应用程序代码是没有从其他代码不同，除了它不调用`Connection`方法`commit`，`rollback`或`setAutoCommit(true)`。因此，您无需担心了解所使用的EJB API。

```java
import java.sql.*;
import javax.sql.*;
import javax.ejb.*;
import javax.naming.*;

public class DistributedTransactionBean implements SessionBean {

    // ...

    public void ejbCreate() throws CreateException {

        ctx = new InitialContext();
        ds = (DataSource)ctx.lookup("jdbc/distCoffeesDB");
    }

    public void updateTotal(int incr, String cofName, String username,
                            String password)
        throws SQLException {

        Connection con;
        PreparedStatement pstmt;

        try {
            con = ds.getConnection(username, password);
            pstmt = con.prepareStatement("UPDATE COFFEES " +
                        "SET TOTAL = TOTAL + ? " +
                        "WHERE COF_NAME = ?");
            pstmt.setInt(1, incr);
            pstmt.setString(2, cofName);
            pstmt.executeUpdate();
            stmt.close();
        } finally {
            if (con != null) con.close();
        }
    }

    private DataSource ds = null;
    private Context ctx = null;
}
```