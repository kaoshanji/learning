# 32.高并发JDBC池

### 介绍

的**JDBC连接池org.apache.tomcat.jdbc.pool** 是一个替换或到一个替代[Apache的百科全书DBCP](https://commons.apache.org/dbcp/) 连接池。

那么，为什么我们需要一个新的连接池？

原因如下：

1. Commons DBCP 1.x是单线程的。为了线程安全，Commons在对象分配和对象返回期间都将整个池锁定较短的时间。请注意，这不适用于Commons DBCP2.x。
2. Commons DBCP 1.x可能很慢。随着逻辑CPU数量的增加以及尝试借用或返回对象的并发线程的数量增加，性能会受到影响。对于高度并发的系统，影响可能很大。请注意，这不适用于Commons DBCP2.x。
3. Commons DBCP有60多个类。tomcat-jdbc-pool核心是8个类，因此为将来的需求进行修改将需要的更改要少得多。这就是运行连接池本身所需的全部，其余的都是肉汁。
4. Commons DBCP使用静态接口。这意味着您必须为给定的JRE版本使用正确的版本，否则可能会看到 `NoSuchMethodException`异常。
5. 当连接池可以用更简单的实现来实现时，不应该重写60个以上的类。
6. Tomcat jdbc池实现了异步检索连接的功能，而无需向库本身添加其他线程。
7. Tomcat jdbc池是Tomcat模块，它依赖于Tomcat JULI（Tomcat中使用的简化日志记录框架）。
8. 使用该`javax.sql.PooledConnection`接口检索基础连接 。
9. 饥饿证明。如果池为空，并且线程正在等待连接，则返回连接时，池将唤醒正确的线程等待。大多数游泳池只会饿死。

在其他连接池实现上添加的功能

1. 支持高度并发的环境和多核/ cpu系统。
2. 接口的动态实现，即使使用较低版本的JDK进行编译，也将为您的运行时环境提供支持`java.sql`和`javax.sql`接口（只要JDBC驱动程序也是如此）。
3. 验证间隔-我们不必每次使用连接时都进行验证，我们可以在借用或返回连接时进行验证，只是不超过我们可以配置的间隔。
4. 运行一次查询，这是一种可配置的查询，在建立与数据库的连接时将仅运行一次。这对于设置会话设置非常有用，您希望在整个连接建立期间都存在该会话设置。
5. 能够配置自定义拦截器。这使您可以编写自定义拦截器以增强功能。您可以使用拦截器来收集查询统计信息，缓存会话状态，在发生故障时重新连接，重试查询，缓存查询结果等等。您的选择是无止境的，并且拦截器是动态的，而不是绑定到`java.sql`/ `javax.sql`接口的JDK版本 。
6. 高性能-稍后我们将展示一些性能差异
7. 极其简单，由于非常简单的实现，行数和源文件的数量是非常低的，比较有超过200个源文件（我们最后一次检查）C3P0，Tomcat的JDBC具有8档核心，连接池本身大约一半。由于可能会发生错误，因此可以更快地找到它们，并且更容易修复。从一开始，降低复杂性就成为关注的焦点。
8. 异步连接检索-您可以将连接请求排队，然后获得`Future<Connection>`退款。
9. 更好的空闲连接处理。与其直接关闭连接，它还可以使用更智能的算法来缓冲连接并调整空闲池的大小。
10. 您可以通过指定池使用量阈值来决定何时将连接视为被放弃，是在池已满时还是在连接超时时。
11. 放弃连接计时器将在语句/查询活动时重置。允许长时间使用的连接不会超时。这是使用实现`ResetAbandonedTimer`
12. 连接一定时间后，请关闭连接。年龄根据返回游泳池的时间而定。
13. 当怀疑连接被放弃时，获取JMX通知和日志条目。这与相似，`removeAbandonedTimeout`但不执行任何操作，仅报告信息。这是使用`suspectTimeout`属性来实现的。
14. 可以从或检索连接`java.sql.Driver`， 这可以使用和属性来实现。`javax.sql.DataSource``javax.sql.XADataSource``dataSource``dataSourceJNDI`
15. XA连接支持

### 如何使用

Tomcat连接池的使用已尽可能地简单，对于熟悉commons-dbcp的人来说，过渡将非常简单。从其他连接池迁移也很简单。

#### 附加的功能

Tomcat连接池提供了大多数其他池所不具备的一些其他功能：

- `initSQL` -创建连接后，仅运行一次SQL语句的能力
- `validationInterval` -除了对连接运行验证之外，请避免过于频繁地运行它们。
- `jdbcInterceptors`-灵活且可插入的拦截器，可围绕池，查询执行和结果集处理创建任何自定义项。有关更多信息，请参见高级部分。
- `fairQueue` -将fair标志设置为true以实现线程公平或使用异步连接检索

#### 在Apache Tomcat容器内部

Tomcat连接池配置为[Tomcat JDBC文档中](http://tomcat.apache.org/tomcat-9.0-doc/jndi-datasource-examples-howto.html)描述的资源 ，唯一的区别是您必须指定`factory`属性并将值设置为 `org.apache.tomcat.jdbc.pool.DataSourceFactory`

#### 单机版

连接池仅具有另一个依赖项，即tomcat-juli.jar。要使用bean实例化在独立项目中配置池，要实例化的bean是 `org.apache.tomcat.jdbc.pool.DataSource`。与将连接池配置为JNDI资源时使用的相同属性（下面记录）用于将数据源配置为Bean。

#### JMX

连接池对象公开了可以注册的MBean。为了使连接池对象创建MBean，必须将标志`jmxEnabled`设置为true。这并不意味着该池将被注册到MBean服务器，而仅仅是创建了MBean。在像Tomcat这样的容器中，Tomcat本身会向MBean服务器注册数据源，`org.apache.tomcat.jdbc.pool.DataSource`然后该 对象将注册实际的连接池MBean。如果您在容器外部运行，则可以使用您指定的任何对象名称自行注册DataSource，并将其传播到基础池。为此，您将致电`mBeanServer.registerMBean(dataSource.getPool().getJmxPool(),objectname)`。在进行此调用之前，请通过调用确保已创建池`dataSource.createPool()`。

### 属性

为了提供与commons-dbcp和tomcat-jdbc-pool之间的非常简单的切换，大多数属性都是相同的，并且具有相同的含义。

#### JNDI工厂和类型

| 属性        | 描述                                                         |
| :---------- | :----------------------------------------------------------- |
| **factory** | 工厂是必填项，其值应为 `org.apache.tomcat.jdbc.pool.DataSourceFactory` |
| **type**    | 类型应始终为`javax.sql.DataSource`或`javax.sql.XADataSource`根据类型将创建a `org.apache.tomcat.jdbc.pool.DataSource`或a `org.apache.tomcat.jdbc.pool.XADataSource`。 |

#### 系统属性

系统属性是JVM范围的，影响在JVM中创建的所有池

| 属性                                                        | 描述                                                         |
| :---------------------------------------------------------- | :----------------------------------------------------------- |
| `org.apache.tomcat.jdbc.pool.onlyAttemptCurrentClassLoader` | （布尔值）控制动态类（例如JDBC驱动程序，拦截器和验证器）的类加载。如果设置为 `false`默认值，则池将首先尝试使用当前的加载器（即，加载池类的类加载器）进行加载，如果类加载失败，则尝试使用线程上下文加载器进行加载。`true`如果希望与Apache Tomcat 8.0.8及更早版本保持向后兼容，请将此值设置为 ，并且仅尝试使用当前的加载器。如果未设置，则默认值为`false`。 |

#### 共同属性

这些属性在commons-dbcp和tomcat-jdbc-pool之间共享，在某些情况下，默认值是不同的。

| 属性                                  | 描述                                                         |
| :------------------------------------ | :----------------------------------------------------------- |
| `defaultAutoCommit`                   | （布尔值）此池创建的连接的默认自动提交状态。如果未设置，则默认值为JDBC驱动程序默认值（如果未设置，则`setAutoCommit`不会调用该方法。） |
| `defaultReadOnly`                     | （布尔值）此池创建的连接的默认只读状态。如果未设置，则`setReadOnly`不会调用该方法。（某些驱动程序不支持只读模式，例如：Informix） |
| `defaultTransactionIsolation`         | （字符串）由该池创建的连接的缺省TransactionIsolation状态。以下之一：（请参阅javadoc）`NONE``READ_COMMITTED``READ_UNCOMMITTED``REPEATABLE_READ``SERIALIZABLE`如果未设置，则不会调用该方法，并且默认为JDBC驱动程序。 |
| `defaultCatalog`                      | （字符串）此池创建的默认连接目录。                           |
| **driverClassName**                   | （字符串）要使用的JDBC驱动程序的标准Java类名称。必须从与tomcat-jdbc.jar相同的类加载器中访问驱动程序 |
| **username**                          | （字符串）要传递给我们的JDBC驱动程序以建立连接的连接用户名。请注意，`DataSource.getConnection(username,password)` 默认情况下，该方法将不使用传递给该方法的凭据，而是将使用此处配置的凭据。请参阅`alternateUsernameAllowed` 属性以获取更多详细信息。 |
| **password**                          | （字符串）要传递给我们的JDBC驱动程序以建立连接的连接密码。请注意，`DataSource.getConnection(username,password)` 默认情况下，该方法将不使用传递给该方法的凭据，而是将使用此处配置的凭据。请参阅`alternateUsernameAllowed` 属性以获取更多详细信息。 |
| `maxActive`                           | （int）可以同时从该池分配的最大活动连接数。默认值为`100`     |
| `maxIdle`                             | （int）始终应保留在池中的最大连接数。默认值为 `maxActive`：`100` 定期检查空闲连接（如果启用），并且空闲时间长于`minEvictableIdleTimeMillis` 释放时间的连接。（另请参见`testWhileIdle`） |
| `minIdle`                             | （int）始终应保留在池中的已建立连接的最小数目。如果验证查询失败，则连接池可以缩小到该数字以下。默认值源自`initialSize`：（`10`另请参见`testWhileIdle`） |
| `initialSize`                         | （int）启动池时创建的初始连接数。默认值为`10`                |
| `maxWait`                             | （int）在引发异常之前，池将等待（无可用连接时）连接返回的最大毫秒数。默认值为`30000`（30秒） |
| `testOnBorrow`                        | （布尔值）指示从池中借用对象之前是否对其进行验证。如果对象验证失败，它将被从池中删除，我们将尝试借用另一个对象。为了进行更有效的验证，请参阅`validationInterval`。默认值为`false` |
| `testOnConnect`                       | （布尔值）指示在首次创建连接时是否将验证对象。如果对象验证失败，则将引发`SQLException`。默认值为`false` |
| `testOnReturn`                        | （布尔值）指示在将对象返回到池之前是否将对其进行验证。默认值为`false`。 |
| `testWhileIdle`                       | （布尔值）指示空闲对象退出者（如果有）是否将验证对象。如果对象验证失败，则会将其从池中删除。默认值为，`false`并且必须设置此属性才能运行池清洁器/测试线程（另请参见`timeBetweenEvictionRunsMillis`） |
| `validationQuery`                     | （字符串）SQL查询，该查询将用于验证来自此池的连接，然后再将其返回给调用方。如果指定，此查询不必返回任何数据，而不能抛出`SQLException`。默认值为`null`。如果未指定，则将通过isValid（）方法验证连接。示例值是`SELECT 1`（mysql），`select 1 from dual`（oracle）和`SELECT 1`（MS Sql Server） |
| `validationQueryTimeout`              | （int）连接验证查询失败之前的超时（以秒为单位）。这可以通过调用 `java.sql.Statement.setQueryTimeout(seconds)`执行的语句来实现`validationQuery`。池本身不会使查询超时，它仍然由JDBC驱动程序来强制执行查询超时。小于或等于零的值将禁用此功能。默认值为`-1`。 |
| `validatorClassName`                  | （字符串）实现`org.apache.tomcat.jdbc.pool.Validator`接口并提供无参数构造函数的类的名称 （可以是隐式的）。如果指定，该类将用于创建Validator实例，然后将其用于代替任何验证查询来验证连接。默认值为`null`。一个示例值是 `com.mycompany.project.SimpleValidator`。 |
| `timeBetweenEvictionRunsMillis`       | （int）空闲连接验证/清除线程的运行之间要休眠的毫秒数。此值不应在1秒内设置。它决定了我们检查空闲，被放弃的连接的频率以及验证空闲连接的频率。`maxAge`如果后者为非零或更低的值，则该值将被覆盖。默认值为`5000`（5秒）。 |
| `numTestsPerEvictionRun`              | （int）tomcat-jdbc-pool中未使用的属性。                      |
| `minEvictableIdleTimeMillis`          | （int）一个对象在有资格被驱逐之前可以在池中空闲的最短时间。默认值为`60000`（60秒）。 |
| `accessToUnderlyingConnectionAllowed` | （布尔值）未使用的属性。可以通过调用`unwrap`池中的连接来实现访问。查看`javax.sql.DataSource`界面，或`getConnection`通过反射调用或将对象转换为`javax.sql.PooledConnection` |
| `removeAbandoned`                     | （布尔值）标记以删除超过的已放弃连接`removeAbandonedTimeout`。如果设置为true，则连接的使用时间比将其`removeAbandonedTimeout`设置为“ 设置为`true`可以从无法关闭连接的应用程序恢复数据库连接”的时间更长，则被视为已被删除并可以删除。另请参见`logAbandoned` 默认值为`false`。 |
| `removeAbandonedTimeout`              | （int）超时（以秒为单位），可以删除已废弃（正在使用）的连接。默认值为`60`（60秒）。该值应设置为您的应用程序可能具有的最长运行查询。 |
| `logAbandoned`                        | （布尔值）标记为放弃连接的应用程序代码记录堆栈跟踪。记录废弃的连接会增加每次连接借用的开销，因为必须生成堆栈跟踪。默认值为`false`。 |
| `connectionProperties`                | （字符串）建立新连接时将发送到我们的JDBC驱动程序的连接属性。字符串的格式必须为[propertyName = property;] *注意-“用户”和“密码”属性将显式传递，因此不需要在此处包含它们。默认值为`null`。 |
| `poolPreparedStatements`              | （布尔值）未使用的属性。                                     |
| `maxOpenPreparedStatements`           | （int）不使用的属性。                                        |

#### Tomcat JDBC增强属性

| 属性                            | 描述                                                         |
| :------------------------------ | :----------------------------------------------------------- |
| `initSQL`                       | （字符串）首次创建连接时要运行的自定义查询。默认值为`null`。 |
| `jdbcInterceptors`              | （字符串）扩展名的`org.apache.tomcat.jdbc.pool.JdbcInterceptor`类名的列表，用分号分隔 。有关 语法和示例的详细说明，请参见下面的[配置JDBC拦截器](http://tomcat.apache.org/tomcat-9.0-doc/jdbc-pool.html#Configuring_JDBC_interceptors)。这些拦截器将作为拦截器插入到`java.sql.Connection`对象的操作链中。默认值为`null`。预定义的拦截器： -跟踪自动提交，只读，目录和事务隔离级别。 -跟踪打开的语句，并在连接返回到池时关闭它们。 `org.apache.tomcat.jdbc.pool.interceptor.ConnectionState` `org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer`更多预定义的拦截器在“ [JDBC拦截器”部分](http://tomcat.apache.org/tomcat-9.0-doc/jdbc-pool.html#JDBC_interceptors)中进行了详细描述 。 |
| `validationInterval`            | （长）避免过多的验证，最多只能在此频率下运行验证-时间以毫秒为单位。如果连接应进行验证，但之前已在此时间间隔内进行验证，则不会再次对其进行验证。默认值为`3000`（3秒）。 |
| `jmxEnabled`                    | （布尔值）是否向JMX注册池。默认值为`true`。                  |
| `fairQueue`                     | （布尔值）如果希望以真正的FIFO方式公平对待对getConnection的调用，则设置为true。这将`org.apache.tomcat.jdbc.pool.FairBlockingQueue` 实现用于空闲连接的列表。默认值为`true`。当您要使用异步连接检索时，此标志是必需的。 设置此标志可确保线程按到达顺序接收连接。 在性能测试期间，实现锁和等待锁的方式有很大的不同。当`fairQueue=true` 存在基于系统在运行什么操作系统的决策过程时。如果系统在Linux上运行（属性`os.name=Linux`。要禁用此Linux特定行为并仍使用公平队列，只需`org.apache.tomcat.jdbc.pool.FairBlockingQueue.ignoreOS=true`在加载连接池类之前将该属性添加 到系统属性中即可。 |
| `abandonWhenPercentageFull`     | （int）除非正在使用的连接数超过定义的百分比，否则已放弃（超时）的连接不会关闭并报告`abandonWhenPercentageFull`。该值应介于0到100之间。默认值为`0`，这表示一旦`removeAbandonedTimeout`达到连接就可以关闭连接。 |
| `maxAge`                        | （长）以毫秒为单位的时间，以重新建立连接。当从池中借用连接时，池将检查`now - time-when-connected > maxAge`是否已达到，如果是，则在借用之前重新连接。当连接返回到池中时，池将检查 `now - time-when-connected > maxAge`是否已达到，如果是，则尝试重新连接。当连接处于空闲状态并且`timeBetweenEvictionRunsMillis`大于零时，池将定期检查以查看是否 `now - time-when-connected > maxAge`已达到，如果是，则尝试重新连接。设置`maxAge`为小于该值`timeBetweenEvictionRunsMillis` 将覆盖它（因此，空闲连接验证/清除将更频繁地运行）。默认值为`0`，这意味着连接将保持打开状态，并且从池中借用，将连接返回到池中或检查空闲连接时都不会进行年龄检查。 |
| `useEquals`                     | （布尔值）如果您希望使用`ProxyConnection`该类，则设置为true；在比较方法名称时`String.equals`，`false` 当您希望使用该类时，设置为`==`。此属性不适用于添加的拦截器，因为它们是单独配置的。默认值为`true`。 |
| `suspectTimeout`                | （int）超时值，以秒为单位。默认值为`0`。 与`removeAbandonedTimeout`值类似，但是不是将连接视为已放弃并可能关闭连接，而是将警告（如果`logAbandoned`设置为true）记录 下来。如果该值等于或小于0，将不执行任何可疑检查。仅当超时值大于0并且未放弃连接或禁用放弃检查时，才进行可疑检查。如果怀疑连接，则记录WARN消息，并发送一次JMX通知。 |
| `rollbackOnReturn`              | （布尔值）如果`autoCommit==false`这样，则池可以通过在连接返回到池时调用连接上的回滚来终止事务，默认值为`false`。 |
| `commitOnReturn`                | （布尔值）如果是，`autoCommit==false`则池可以通过在连接返回到池时调用连接上的commit来完成事务。如果`rollbackOnReturn==true`是，则忽略此属性。默认值为`false`。 |
| `alternateUsernameAllowed`      | （布尔值）默认情况[`DataSource.getConnection(username,password)`](http://docs.oracle.com/javase/6/docs/api/javax/sql/DataSource.html#getConnection(java.lang.String, java.lang.String)) 下，出于性能原因，jdbc-pool将忽略该 调用，并仅在全局配置的属性`username`和下返回先前池化的连接`password`。但是，可以将池配置为允许每次请求连接时使用不同的凭据。要启用[`DataSource.getConnection(username,password)`](http://docs.oracle.com/javase/6/docs/api/javax/sql/DataSource.html#getConnection(java.lang.String, java.lang.String)) 呼叫中描述的功能 ，只需将属性设置`alternateUsernameAllowed` 为`true`。 如果您要求与证书用户1 /密码1的连接，并使用不同的用户2 /密码2连接以前连接，该连接将被关闭，并重新打开与要求的凭证。这样，池大小仍在全局级别而不是在每个架构级别进行管理。 默认值为`false`。 添加此属性是对[Bug 50025](https://bz.apache.org/bugzilla/show_bug.cgi?id=50025)的增强。 |
| `dataSource`                    | （javax.sql.DataSource）将数据源注入连接池，该池将使用数据源来检索连接，而不是使用`java.sql.Driver`接口来建立连接。当您希望合并XA连接或使用数据源而不是连接字符串建立的连接时，此功能很有用。默认值为`null` |
| `dataSourceJNDI`                | （字符串）要在JNDI中查找然后用于建立与数据库的连接的数据源的JNDI名称。请参阅`dataSource`属性。默认值为`null` |
| `useDisposableConnectionFacade` | （布尔值）如果希望在连接上放置外观，则将其设置为true，以使其在关闭后无法重复使用。这样可以防止线程保留已调用的已关闭连接的引用，以对其执行查询。默认值为`true`。 |
| `logValidationErrors`           | （布尔值）将此属性设置为true可以在验证阶段将错误记录到日志文件中。如果设置为true，则错误将记录为SEVERE。默认值是`false`为了向后兼容。 |
| `propagateInterruptState`       | （布尔值）将此值设置为true可以传播已被中断的线程的中断状态（不清除中断状态）。默认值是`false`为了向后兼容。 |
| `ignoreExceptionOnPreLoad`      | （布尔值）标记初始化池时是否忽略连接创建错误。如果要在初始化池时忽略连接创建错误，请设置为true。如果要通过引发异常使池初始化失败，请设置为false。默认值为`false`。 |
| `useStatementFacade`            | （布尔值）如果希望包装语句以便启用，`equals()`并且`hashCode()`在设置了任何语句代理的情况下在关闭的语句上调用方法，请将其设置为true 。默认值为`true`。 |

### 高级用法

#### JDBC拦截器

要查看有关如何使用拦截器的示例，请查看 `org.apache.tomcat.jdbc.pool.interceptor.ConnectionState`。这个简单的拦截器是三个属性的缓存，即事务隔离级别，自动提交和只读状态，以使系统避免不必要的数据库往返。

在有需要时进一步拦截器将被添加到池的核心。永远欢迎捐款！

拦截器当然不仅限于此，`java.sql.Connection`还可以用于包装来自方法调用的任何结果。您可以构建查询性能分析器，以在查询运行时间超过预期时间时提供JMX通知。

#### 配置JDBC拦截器

使用**jdbcInterceptors**属性完成JDBC拦截器的配置。该属性包含用分号分隔的类名称的列表。如果类名不完全限定，则将在该前缀之前加上 `org.apache.tomcat.jdbc.pool.interceptor.`前缀。

示例：
`jdbcInterceptors="org.apache.tomcat.jdbc.pool.interceptor.ConnectionState; org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer"`
与
`jdbcInterceptors="ConnectionState;StatementFinalizer"`

拦截器也可以具有属性。拦截器的属性在类名后的括号内指定。几个属性用逗号分隔。

例：
`jdbcInterceptors="ConnectionState;StatementFinalizer(useEquals=true)"`

类名，属性名和值周围的多余空格字符将被忽略。

#### org.apache.tomcat.jdbc.pool.JdbcInterceptor

所有拦截器的抽象基类都无法实例化。

| 属性        | 描述                                                         |
| :---------- | :----------------------------------------------------------- |
| `useEquals` | （布尔值）如果您希望使用`ProxyConnection`该类，则设置为true；在比较方法名称时`String.equals`，`false` 当您希望使用该类时，设置为`==`。默认值为`true`。 |

#### org.apache.tomcat.jdbc.pool.interceptor.ConnectionState

缓存下列属性的连接`autoCommit`，`readOnly`， `transactionIsolation`和`catalog`。这是一种性能增强，可以避免在调用getter或使用已设置的值调用setter时往返数据库。

| 属性 | 描述 |
| :--- | :--- |
|      |      |

#### org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer

跟踪使用或创建的所有语句`createStatement`， 并在连接返回池时关闭这些语句。 `prepareStatement``prepareCall`

| 属性    | 描述                                                         |
| :------ | :----------------------------------------------------------- |
| `trace` | （布尔值作为字符串）启用对未关闭语句的跟踪。启用并关闭连接且未关闭语句时，拦截器将记录所有堆栈跟踪。默认值为`false`。 |

#### org.apache.tomcat.jdbc.pool.interceptor.StatementCache

连接上的缓存`PreparedStatement`和/或`CallableStatement`实例。

语句按连接缓存。对于属于同一池的所有连接，该计数限制是全局计数的。一旦达到计数`max`，后续语句就不会返回到缓存，而是立即关闭。

| 属性       | 描述                                                         |
| :--------- | :----------------------------------------------------------- |
| `prepared` | （布尔值作为字符串）启用对`PreparedStatement` 使用`prepareStatement`调用创建的实例进行缓存。默认值为`true`。 |
| `callable` | （布尔值作为字符串）启用对`CallableStatement` 使用`prepareCall`调用创建的实例进行缓存。默认值为`false`。 |
| `max`      | （以String形式表示）（整数）限制整个连接池中缓存的语句的数量。默认值为`50`。 |

#### org.apache.tomcat.jdbc.pool.interceptor.StatementDecoratorInterceptor

参见[48392](https://bz.apache.org/bugzilla/show_bug.cgi?id=48392)。拦截器包装语句和结果集，以防止使用方法`ResultSet.getStatement().getConnection()`和访问实际连接`Statement.getConnection()`

| 属性 | 描述 |
| :--- | :--- |
|      |      |

#### org.apache.tomcat.jdbc.pool.interceptor.QueryTimeoutInterceptor

`java.sql.Statement.setQueryTimeout(seconds)`创建新语句时自动调用。池本身不会使查询超时，它仍然由JDBC驱动程序来强制执行查询超时。

| 属性             | 描述                                                         |
| :--------------- | :----------------------------------------------------------- |
| **queryTimeout** | （以字符串形式int）要为查询超时设置的秒数。小于或等于零的值将禁用此功能。默认值为`1`秒。 |

#### org.apache.tomcat.jdbc.pool.interceptor.SlowQueryReport

跟踪查询性能并在查询超过失败时间阈值时发出日志条目。使用的日志级别是`WARN`

| 属性         | 描述                                                         |
| :----------- | :----------------------------------------------------------- |
| `threshold`  | （以字符串形式int表示）发出日志警报之前查询必须超过的毫秒数。默认值是`1000`毫秒。 |
| `maxQueries` | （int作为String）为了保留内存空间而要跟踪的最大查询数。小于或等于0的值将禁用此功能。默认值为`1000`。 |
| `logSlow`    | （布尔值作为字符串）设置为`true`如果您希望记录慢查询。默认值为`true`。 |
| `logFailed`  | （布尔值作为字符串）设置为`true`是否希望记录失败的查询。默认值为`false`。 |

#### org.apache.tomcat.jdbc.pool.interceptor.SlowQueryReportJmx

扩展`SlowQueryReport`和，除了日志条目外，它还发出JMX通知以使监视工具做出反应。从其父类继承所有属性。此类使用Tomcat的JMX引擎，因此它将无法在Tomcat容器之外工作。默认情况下，如果启用了JMX通知，则会通过ConnectionPool mbean发送该通知。该`SlowQueryReportJmx`也可以注册一个MBean如果`notifyPool=false`

| 属性         | 描述                                                         |
| :----------- | :----------------------------------------------------------- |
| `notifyPool` | （布尔值为String）如果希望JMX通知转到`SlowQueryReportJmx`MBean，则设置为false 。默认值为`true`。 |
| `objectName` | （字符串）定义`javax.management.ObjectName`将用于在平台mbean服务器上注册该对象的有效字符串。默认值为，`null`并且将使用tomcat.jdbc：type = org.apache.tomcat.jdbc.pool.interceptor.SlowQueryReportJmx注册该对象，name =池名称 |

#### org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer

从池中检出连接后，废弃的计时器将启动。这意味着，如果您有30秒的超时时间，并使用该连接运行10x10秒的查询，则会根据`abandonWhenPercentageFull` 属性将其标记为已放弃并可能被回收。使用此拦截器，每次您对连接执行操作或成功执行查询时，它将重置结帐计时器。

| 属性 | 描述 |
| :--- | :--- |
|      |      |

### 代码示例

可以[在Tomcat文档中](https://tomcat.apache.org/tomcat-9.0-doc/jndi-datasource-examples-howto.html)找到有关JDBC使用的Tomcat配置的其他示例。

#### 普通的Java

这是有关如何创建和使用数据源的简单示例。

```java
  import java.sql.Connection;
  import java.sql.ResultSet;
  import java.sql.Statement;

  import org.apache.tomcat.jdbc.pool.DataSource;
  import org.apache.tomcat.jdbc.pool.PoolProperties;

  public class SimplePOJOExample {

      public static void main(String[] args) throws Exception {
          PoolProperties p = new PoolProperties();
          p.setUrl("jdbc:mysql://localhost:3306/mysql");
          p.setDriverClassName("com.mysql.jdbc.Driver");
          p.setUsername("root");
          p.setPassword("password");
          p.setJmxEnabled(true);
          p.setTestWhileIdle(false);
          p.setTestOnBorrow(true);
          p.setValidationQuery("SELECT 1");
          p.setTestOnReturn(false);
          p.setValidationInterval(30000);
          p.setTimeBetweenEvictionRunsMillis(30000);
          p.setMaxActive(100);
          p.setInitialSize(10);
          p.setMaxWait(10000);
          p.setRemoveAbandonedTimeout(60);
          p.setMinEvictableIdleTimeMillis(30000);
          p.setMinIdle(10);
          p.setLogAbandoned(true);
          p.setRemoveAbandoned(true);
          p.setJdbcInterceptors(
            "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
            "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
          DataSource datasource = new DataSource();
          datasource.setPoolProperties(p);

          Connection con = null;
          try {
            con = datasource.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from user");
            int cnt = 1;
            while (rs.next()) {
                System.out.println((cnt++)+". Host:" +rs.getString("Host")+
                  " User:"+rs.getString("User")+" Password:"+rs.getString("Password"));
            }
            rs.close();
            st.close();
          } finally {
            if (con!=null) try {con.close();}catch (Exception ignore) {}
          }
      }

  }
```

#### 作为资源

这是关于如何为JNDI查找配置资源的示例

```xml
<Resource name="jdbc/TestDB"
          auth="Container"
          type="javax.sql.DataSource"
          factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
          testWhileIdle="true"
          testOnBorrow="true"
          testOnReturn="false"
          validationQuery="SELECT 1"
          validationInterval="30000"
          timeBetweenEvictionRunsMillis="30000"
          maxActive="100"
          minIdle="10"
          maxWait="10000"
          initialSize="10"
          removeAbandonedTimeout="60"
          removeAbandoned="true"
          logAbandoned="true"
          minEvictableIdleTimeMillis="30000"
          jmxEnabled="true"
          jdbcInterceptors="org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;
            org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer"
          username="root"
          password="password"
          driverClassName="com.mysql.jdbc.Driver"
          url="jdbc:mysql://localhost:3306/mysql"/>
```

#### 异步连接检索

Tomcat JDBC连接池支持异步连接检索，而无需向池库中添加其他线程。通过向数据源中添加一个方法来实现此目的`Future<Connection> getConnectionAsync()`。为了使用异步检索，必须满足两个条件：

1. 您必须将`fairQueue`属性配置为`true`。
2. 您将必须将数据源转换为 `org.apache.tomcat.jdbc.pool.DataSource`

下面显示了使用异步功能的示例。

```java
  Connection con = null;
  try {
    Future<Connection> future = datasource.getConnectionAsync();
    while (!future.isDone()) {
      System.out.println("Connection is not yet available. Do some background work");
      try {
        Thread.sleep(100); //simulate work
      }catch (InterruptedException x) {
        Thread.currentThread().interrupt();
      }
    }
    con = future.get(); //should return instantly
    Statement st = con.createStatement();
    ResultSet rs = st.executeQuery("select * from user");
```

#### 拦截器

拦截器是启用，禁用或修改特定连接或其子组件上的功能的强大方法。有许多不同的使用情况进行拦截时是有用的。默认情况下，由于性能原因，连接池是无状态的。池本身刀片是唯一的国家`defaultAutoCommit`，`defaultReadOnly`，`defaultTransactionIsolation`，`defaultCatalog`如果这些设置。仅在创建连接时设置这4个属性。如果在使用连接期间修改了这些属性，则池本身不会重置它们。

拦截器必须扩展`org.apache.tomcat.jdbc.pool.JdbcInterceptor`类。此类非常简单，您将需要一个无参数的构造函数

```java
  public JdbcInterceptor() {
  }
```

当从池中借用连接时，拦截器可以通过执行以下操作来初始化事件或以其他方式对事件做出反应：

```java
  public abstract void reset(ConnectionPool parent, PooledConnection con);
```

方法。使用两个参数调用该方法，一个是对连接池本身`ConnectionPool parent` 的引用，另一个是对基础连接的引用`PooledConnection con`。

当`java.sql.Connection`对象上的方法被调用时，它将导致

```java
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
```

被调用的方法。在`Method method`被调用的实际方法，并且`Object[] args`是参数。看一个非常简单的示例，在该示例中，我们演示了如何`java.sql.Connection.close()`在连接已关闭的情况下对noop 进行调用

```java
  if (CLOSE_VAL==method.getName()) {
      if (isClosed()) return null; //noop for already closed.
  }
  return super.invoke(proxy,method,args);
```

正在观察。它是方法名称的比较。一种方法是这样做 `"close".equals(method.getName())`。上面我们看到了方法名称和`static final String`引用之间的直接引用比较。根据JVM规范，方法名称和静态最终String最终位于共享常量池中，因此引用比较应该起作用。当然也可以这样做：

```java
  if (compare(CLOSE_VAL,method)) {
      if (isClosed()) return null; //noop for already closed.
  }
  return super.invoke(proxy,method,args);
```

在`compare(String,Method)`将使用`useEquals`上的拦截器标志和做任一参考相比较时，或者当一个字符串值比较`useEquals=true`标志被设置。

池启动/停止
当连接池启动或关闭时，可以通知您。即使它是一个实例方法，每个拦截器类也只会通知您一次。并且会使用当前未附加到池中的拦截器来通知您。

```java
  public void poolStarted(ConnectionPool pool) {
  }

  public void poolClosed(ConnectionPool pool) {
  }
```

覆盖这些方法时，如果要扩展除以下类以外的其他类，请不要忘记调用super `JdbcInterceptor`

配置拦截
器使用`jdbcInterceptors`属性或`setJdbcInterceptors`方法配置拦截器。拦截器可以具有属性，并且可以这样配置

```java
  String jdbcInterceptors=
    "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState(useEquals=true,fast=yes)"
```

拦截器属性
由于拦截器可以具有属性，因此您需要能够在拦截器中读取这些属性的值。以上面的示例为例，您可以覆盖该`setProperties`方法。

```java
  public void setProperties(Map<String, InterceptorProperty> properties) {
     super.setProperties(properties);
     final String myprop = "myprop";
     InterceptorProperty p1 = properties.get(myprop);
     if (p1!=null) {
         setMyprop(Long.parseLong(p1.getValue()));
     }
  }
```

#### 获取实际的JDBC连接

连接池在实际连接周围创建包装器，以正确地对其进行缓冲。我们还在这些包装器中创建拦截器，以执行某些功能。如果需要检索实际连接，则可以使用该`javax.sql.PooledConnection` 接口进行检索。

```java
  Connection con = datasource.getConnection();
  Connection actual = ((javax.sql.PooledConnection)con).getConnection();
```

### 建造

我们用1.6构建JDBC池代码，但是对于运行时环境，它向下兼容到1.5。对于单元测试，我们使用1.6及更高版本

可以[在Tomcat文档中](https://tomcat.apache.org/tomcat-9.0-doc/jndi-datasource-examples-howto.html)找到有关JDBC使用的Tomcat配置的其他示例。

#### 从源头建造

建造非常简单。该池依赖于`tomcat-juli.jar`，如果您想要`SlowQueryReportJmx`

```bash
  javac -classpath tomcat-juli.jar \
        -d . \
        org/apache/tomcat/jdbc/pool/*.java \
        org/apache/tomcat/jdbc/pool/interceptor/*.java \
        org/apache/tomcat/jdbc/pool/jmx/*.java
```

可以在Tomcat [源存储库中](https://svn.apache.org/viewvc/tomcat/trunk/modules/jdbc-pool/)找到一个构建文件。

为方便起见，还包括一个构建文件，其中一个简单的构建命令将生成所需的所有文件。

```bash
  ant download  (downloads dependencies)
  ant build     (compiles and generates .jar files)
  ant dist      (creates a release package)
  ant test      (runs tests, expects a test database to be setup)
```

该系统是为Maven构建构建的，但确实会生成发行工件。只是图书馆本身。