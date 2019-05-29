# 19.3 数据连接池

### 19.3.1 DataSource

Spring通过a获得与数据库的连接`DataSource`。A `DataSource`是JDBC规范的一部分，是一个通用的连接工厂。它允许容器或框架从应用程序代码中隐藏连接池和事务管理问题。作为开发人员，您无需了解有关如何连接到数据库的详细信息; 这是设置数据源的管理员的责任。您最有可能在开发和测试代码时填充这两个角色，但您不必知道如何配置生产数据源。

使用Spring的JDBC层时，您可以从JNDI获取数据源，或者使用第三方提供的连接池实现来配置自己的数据源。流行的实现是Apache Jakarta Commons DBCP和C3P0。Spring发行版中的实现仅用于测试目的，不提供池。

本节使用Spring的`DriverManagerDataSource`实现，稍后将介绍其他几个实现。

只有使用`DriverManagerDataSource`该类才应该用于测试目的，因为它不提供池，并且在进行多个连接请求时性能很差。

您`DriverManagerDataSource`通常获得JDBC连接时获得连接。指定JDBC驱动程序的完全限定类名，以便 `DriverManager`可以加载驱动程序类。接下来，提供不同JDBC驱动程序之间的URL。（请参阅驱动程序的文档以获取正确的值。）然后提供用户名和密码以连接到数据库。以下是如何配置`DriverManagerDataSource`Java代码的示例：

```java
DriverManagerDataSource dataSource = new DriverManagerDataSource();
dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
dataSource.setUrl("jdbc:hsqldb:hsql://localhost:");
dataSource.setUsername("sa");
dataSource.setPassword("");
```

以下是相应的XML配置：

```xml
<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="${jdbc.driverClassName}"/>
    <property name="url" value="${jdbc.url}"/>
    <property name="username" value="${jdbc.username}"/>
    <property name="password" value="${jdbc.password}"/>
</bean>

<context:property-placeholder location="jdbc.properties"/>
```

以下示例显示了DBCP和C3P0的基本连接和配置。要了解有助于控制池功能的更多选项，请参阅相应连接池实现的产品文档。

DBCP配置：

```xml
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="${jdbc.driverClassName}"/>
    <property name="url" value="${jdbc.url}"/>
    <property name="username" value="${jdbc.username}"/>
    <property name="password" value="${jdbc.password}"/>
</bean>

<context:property-placeholder location="jdbc.properties"/>
```

C3P0配置：

```xml
<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource" destroy-method="close">
    <property name="driverClass" value="${jdbc.driverClassName}"/>
    <property name="jdbcUrl" value="${jdbc.url}"/>
    <property name="user" value="${jdbc.username}"/>
    <property name="password" value="${jdbc.password}"/>
</bean>

<context:property-placeholder location="jdbc.properties"/>
```

### 19.3.2 DataSourceUtils

该`DataSourceUtils`班是一个方便且功能强大的辅助类，提供 `static`方法来获取从JNDI和连接，如果有必要密切的联系。例如，它支持线程绑定连接`DataSourceTransactionManager`。

### 19.3.3 SmartDataSource

该`SmartDataSource`接口应该由能提供一个关系数据库的连接类实现。它扩展了`DataSource`接口，允许使用它的类查询在给定操作后是否应该关闭连接。当您知道将重用连接时，此用法很有效。

### 19.3.4 AbstractDataSource

`AbstractDataSource`是`abstract`Spring `DataSource` 实现的基类，它实现了所有`DataSource`实现共有的代码。`AbstractDataSource`如果您正在编写自己的`DataSource` 实现，则扩展该类。

### 19.3.5 SingleConnectionDataSource

的`SingleConnectionDataSource`类是的一个实现`SmartDataSource` ，它包装一个接口*单* `Connection`是*不*每次使用后关闭。显然，这不是多线程的。

如果任何客户端代码`close`在假设池化连接的情况下调用，就像使用持久性工具一样，请将`suppressClose`属性设置为`true`。此设置返回包含物理连接的封闭抑制代理。请注意，您将无法再将其强制转换为本机Oracle `Connection`等。

这主要是一个测试类。例如，它可以在简单的JNDI环境中轻松测试应用程序服务器外部的代码。与此相反 `DriverManagerDataSource`，它始终重用相同的连接，避免过度创建物理连接。

### 19.3.6 DriverManagerDataSource

该`DriverManagerDataSource`班是标准的实现`DataSource` 是通过配置bean的属性的纯JDBC驱动程序接口，并返回一个新的 `Connection`每次。

此实现对于Java EE容器外部的测试和独立环境非常有用，可以作为`DataSource`Spring IoC容器中的bean ，也可以与简单的JNDI环境结合使用。池假定`Connection.close()`调用将简单地关闭连接，因此任何`DataSource`感知持久性代码都应该起作用。但是，`commons-dbcp`即使在测试环境中，使用JavaBean样式的连接池也很容易，因此几乎总是优先使用这样的连接池 `DriverManagerDataSource`。

### 19.3.7 TransactionAwareDataSourceProxy

`TransactionAwareDataSourceProxy`是目标的代理`DataSource`，它包装该目标`DataSource`以增加对Spring管理的事务的认识。在这方面，它类似于`DataSource`Java EE服务器提供的事务性JNDI 。

除了必须调用并传递标准JDBC `DataSource`接口实现的现有代码之外，很少使用此类。在这种情况下，仍然可以使此代码可用，同时使此代码参与Spring托管事务。通常最好使用更高级别的资源管理抽象编写自己的新代码，例如 `JdbcTemplate`或`DataSourceUtils`。

### 19.3.8 DataSourceTransactionManager

该`DataSourceTransactionManager`班是`PlatformTransactionManager` 为单JDBC数据源的实现。它将JDBC连接从指定的数据源绑定到当前正在执行的线程，可能允许每个数据源一个线程连接。

需要应用程序代码来检索JDBC连接， `DataSourceUtils.getConnection(DataSource)`而不是Java EE的标准 `DataSource.getConnection`。它抛出未经检查的`org.springframework.dao`异常而不是检查`SQLExceptions`。所有框架类都`JdbcTemplate`隐式使用此策略。如果不与此事务管理器一起使用，则查找策略的行为与常见策略完全相同 - 因此可以在任何情况下使用它。

该`DataSourceTransactionManager`级支持自定义隔离级别，并得到应用适当的SQL语句查询超时的设定。为了支持后者，应用程序代码必须为每个创建的语句使用`JdbcTemplate`或调用 `DataSourceUtils.applyTransactionTimeout(..)`方法。

可以使用此实现而不是`JtaTransactionManager`单个资源情况，因为它不需要容器支持JTA。如果您坚持所需的连接查找模式，则在两者之间切换只是配置问题。JTA不支持自定义隔离级别！

### 19.3.9 NativeJdbcExtractor

有时，您需要访问与标准JDBC API不同的供应商特定JDBC方法。如果你是在应用服务器或正在运行的这可能是问题的 `DataSource`一个包装`Connection`，`Statement`并`ResultSet`用自己的包装对象的对象。要访问本机对象，您可以配置您的 `JdbcTemplate`或`OracleLobHandler`使用`NativeJdbcExtractor`。

在`NativeJdbcExtractor`有多种口味，以配合您的执行环境：

- SimpleNativeJdbcExtractor
- C3P0NativeJdbcExtractor
- CommonsDbcpNativeJdbcExtractor
- JBossNativeJdbcExtractor
- WebLogicNativeJdbcExtractor
- WebSphereNativeJdbcExtractor
- XAPoolNativeJdbcExtractor

通常`SimpleNativeJdbcExtractor`足以`Connection` 在大多数环境中展开对象。有关更多详细信息，请参阅javadocs。