# 39、JTA进行分布式事务

通过使用[Atomikos](https://www.atomikos.com/)或[Bitronix](https://github.com/bitronix/btm)嵌入式事务管理器，Spring Boot支持跨多个XA资源的分布式JTA事务。部署到合适的Java EE应用程序服务器时，还支持JTA事务。

当检测到JTA环境时，`JtaTransactionManager`将使用Spring 来管理事务。自动配置的JMS，DataSource和JPA Bean已升级为支持XA事务。您可以使用标准Spring习惯用法（例如`@Transactional`）来参与分布式事务。如果您在JTA环境中，但仍要使用本地事务，则可以将该`spring.jta.enabled`属性设置`false`为禁用JTA自动配置。

## 39.1使用Atomikos交易管理器

[Atomikos](https://www.atomikos.com/)是一种流行的开源事务管理器，可以嵌入到您的Spring Boot应用程序中。您可以使用`spring-boot-starter-jta-atomikos`入门程序来引入适当的Atomikos库。Spring Boot自动配置Atomikos，并确保将适当的`depends-on`设置应用于您的Spring Bean，以正确启动和关闭命令。

默认情况下，Atomikos事务日志将写入`transaction-logs`应用程序主目录中的目录（应用程序jar文件所在的目录）。您可以通过`spring.jta.log-dir`在`application.properties`文件中设置属性来自定义此目录的位置。以开头的属性`spring.jta.atomikos.properties`也可以用于自定义Atomikos `UserTransactionServiceImp`。有关完整的详细信息，请参见[`AtomikosProperties`Javadoc](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/api/org/springframework/boot/jta/atomikos/AtomikosProperties.html)。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 为了确保多个事务管理器可以安全地协调同一资源管理器，必须为每个Atomikos实例配置一个唯一的ID。默认情况下，此ID是运行Atomikos的计算机的IP地址。为确保生产中的唯一性，应`spring.jta.transaction-manager-id`为应用程序的每个实例为属性配置一个不同的值。 |

## 39.2使用Bitronix交易管理器

[Bitronix](https://github.com/bitronix/btm)是流行的开源JTA事务管理器实现。您可以使用`spring-boot-starter-jta-bitronix`启动器将适当的Bitronix依赖项添加到项目中。与Atomikos一样，Spring Boot自动配置Bitronix并对您的bean进行后处理，以确保启动和关闭顺序正确。

默认情况下，Bitronix事务日志文件（`part1.btm`和`part2.btm`）被写入`transaction-logs`应用程序主目录中的目录。您可以通过设置`spring.jta.log-dir`属性来自定义此目录的位置。以开头的属性`spring.jta.bitronix.properties`也绑定到`bitronix.tm.Configuration`Bean，从而可以进行完全自定义。有关详细信息，请参见[Bitronix文档](https://github.com/bitronix/btm/wiki/Transaction-manager-configuration)。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 为了确保多个事务管理器可以安全地协调同一资源管理器，必须为每个Bitronix实例配置唯一的ID。默认情况下，此ID是运行Bitronix的计算机的IP地址。为确保生产中的唯一性，应`spring.jta.transaction-manager-id`为应用程序的每个实例为属性配置一个不同的值。 |

## 39.3使用Java EE托管事务管理器

如果将Spring Boot应用程序打包为`war`或`ear`文件，并将其部署到Java EE应用程序服务器，则可以使用应用程序服务器的内置事务管理器。Spring Boot尝试通过查看常见的JNDI位置（`java:comp/UserTransaction`，`java:comp/TransactionManager`等）来自动配置事务管理器。如果使用应用程序服务器提供的事务服务，通常还需要确保所有资源都由服务器管理并通过JNDI公开。Spring Boot尝试通过`ConnectionFactory`在JNDI路径（`java:/JmsXA`或`java:/XAConnectionFactory`）中查找来尝试自动配置JMS ，您可以使用该[`spring.datasource.jndi-name`属性](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-sql.html#boot-features-connecting-to-a-jndi-datasource)来配置`DataSource`。

## 39.4混合XA和非XA JMS连接

使用JTA时，主要的JMS `ConnectionFactory`Bean可识别XA，并参与分布式事务。在某些情况下，您可能想通过使用非XA处理某些JMS消息`ConnectionFactory`。例如，您的JMS处理逻辑可能需要比XA超时更长的时间。

如果要使用非XA `ConnectionFactory`，则可以注入`nonXaJmsConnectionFactory`bean而不是`@Primary` `jmsConnectionFactory`bean。为了保持一致性，`jmsConnectionFactory`还使用bean别名提供了bean `xaJmsConnectionFactory`。

以下示例显示了如何注入`ConnectionFactory`实例：

```bash
// Inject the primary (XA aware) ConnectionFactory
@Autowired
private ConnectionFactory defaultConnectionFactory;

// Inject the XA aware ConnectionFactory (uses the alias and injects the same as above)
@Autowired
@Qualifier("xaJmsConnectionFactory")
private ConnectionFactory xaConnectionFactory;

// Inject the non-XA aware ConnectionFactory
@Autowired
@Qualifier("nonXaJmsConnectionFactory")
private ConnectionFactory nonXaConnectionFactory;
```

## 39.5支持备用嵌入式事务管理器

该[`XAConnectionFactoryWrapper`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/jms/XAConnectionFactoryWrapper.java)和[`XADataSourceWrapper`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/jdbc/XADataSourceWrapper.java)接口可用于支持替代嵌入式事务经理。这些接口负责包装`XAConnectionFactory`和`XADataSource`bean，并将它们作为常规`ConnectionFactory`和`DataSource`bean 公开，它们透明地注册到分布式事务中。数据源和JMS自动配置使用JTA变体，前提是您拥有一个`JtaTransactionManager`Bean并在其中注册了适当的XA包装器Bean `ApplicationContext`。

该[BitronixXAConnectionFactoryWrapper](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/jta/bitronix/BitronixXAConnectionFactoryWrapper.java)和[BitronixXADataSourceWrapper](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/jta/bitronix/BitronixXADataSourceWrapper.java)提供了如何编写XA包装很好的例子。