# 17.4 同步资源

现在应该清楚如何创建不同的事务管理器，以及它们如何链接到需要与事务同步的相关资源（例如 `DataSourceTransactionManager`，JDBC `DataSource`，`HibernateTransactionManager`Hibernate `SessionFactory`等）。本节描述直接或间接使用持久性API（如JDBC，Hibernate或JDO）的应用程序代码如何确保正确创建，重用和清理这些资源。本节还讨论了如何通过相关触发（可选）事务同步`PlatformTransactionManager`。

### 17.4.1高级同步方法

首选方法是使用Spring基于最高级别模板的持久性集成API，或者将本机ORM API与事务感知工厂bean或代理一起使用，以管理本机资源工厂。这些事务感知解决方案在内部处理资源创建和重用，清理，资源的可选事务同步以及异常映射。因此，用户数据访问代码不必解决这些任务，但可以完全专注于非样板持久性逻辑。通常，您使用本机ORM API或使用*模板*方法进行JDBC访问`JdbcTemplate`。这些解决方案将在本参考文档的后续章节中详细介绍。

### 17.4.2低级同步方法

诸如`DataSourceUtils`（对于JDBC），`EntityManagerFactoryUtils`（对于JPA）， `SessionFactoryUtils`（对于Hibernate），`PersistenceManagerFactoryUtils`（对于JDO）等类存在于较低级别。当您希望应用程序代码直接处理本机持久性API的资源类型时，您可以使用这些类来确保获得正确的Spring Framework托管实例，（可选）同步事务，并且在此过程中发生的异常是正确映射到一致的API。

例如，在JDBC的情况下，而不是传统的JDBC方法调用`getConnection()`方法`DataSource`，而是使用Spring的`org.springframework.jdbc.datasource.DataSourceUtils`类，如下所示：

```java
Connection conn = DataSourceUtils.getConnection(dataSource);
```

如果现有事务已经与其同步（链接）了连接，则返回该实例。否则，方法调用会触发新连接的创建，该连接（可选）与任何现有事务同步，并可在随后的同一事务中重用。如上所述，任何 `SQLException`包含在Spring Framework中`CannotGetJdbcConnectionException`，Spring Framework是未经检查的DataAccessExceptions的层次结构之一。这种方法为您提供了比从中轻松获得的更多信息`SQLException`，并确保跨数据库的可移植性，甚至跨不同的持久性技术。

这种方法在没有Spring事务管理（事务同步是可选的）的情况下也可以工作，因此无论您是否使用Spring进行事务管理，都可以使用它。

当然，一旦您使用了Spring的JDBC支持，JPA支持或Hibernate支持，您通常不会使用`DataSourceUtils`或其他帮助程序类，因为通过Spring抽象工作比直接使用相关API更快乐。例如，如果使用Spring `JdbcTemplate`或 `jdbc.object`包来简化JDBC的使用，则在后台进行正确的连接检索，您不需要编写任何特殊代码。

### 17.4.3 TransactionAwareDataSourceProxy

在最低级别存在`TransactionAwareDataSourceProxy`该类。这是目标的代理`DataSource`，它包装目标`DataSource`以增加对Spring管理的事务的认识。在这方面，它类似于`DataSource`Java EE服务器提供的事务性JNDI 。

除非必须调用现有代码并传递标准JDBC `DataSource`接口实现，否则几乎不需要或不希望使用此类。在这种情况下，此代码可能可用，但参与Spring托管事务。最好使用上面提到的更高级别的抽象来编写新代码。