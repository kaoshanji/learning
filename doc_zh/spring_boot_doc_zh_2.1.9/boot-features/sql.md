# 31、使用SQL数据库

在[Spring框架](https://spring.io/projects/spring-framework)提供了广泛的支持使用使用SQL数据库，直接JDBC访问`JdbcTemplate`来完成“对象关系映射”技术，比如Hibernate。 [Spring Data](https://spring.io/projects/spring-data)提供了更高级别的功能：`Repository`直接从接口创建实现，并使用约定从您的方法名称生成查询。

## 31.1配置数据源

Java的`javax.sql.DataSource`界面提供了使用数据库连接的标准方法。传统上，“数据源”使用`URL`以及一些凭据来建立数据库连接。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 有关更多高级示例，请参见[“操作方法”部分](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-data-access.html#howto-configure-a-datasource)，通常可以完全控制DataSource的配置。 |

### 31.1.1嵌入式数据库支持

使用内存嵌入式数据库来开发应用程序通常很方便。显然，内存数据库不提供持久存储。您需要在应用程序启动时填充数据库，并准备在应用程序结束时丢弃数据。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| “操作方法”部分包括[有关如何初始化数据库的部分](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-database-initialization.html)。 |

Spring Boot可以自动配置嵌入式[H2](https://www.h2database.com/)，[HSQL](http://hsqldb.org/)和[Derby](https://db.apache.org/derby/)数据库。您无需提供任何连接URL。您只需要包含要使用的嵌入式数据库的构建依赖项即可。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果您在测试中使用此功能，则可能会注意到，整个测试套件将重复使用同一数据库，而不管您使用的应用程序上下文有多少。如果要确保每个上下文都有一个单独的嵌入式数据库，则应设置`spring.datasource.generate-unique-name`为`true`。 |

例如，典型的POM依赖关系如下：

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
	<groupId>org.hsqldb</groupId>
	<artifactId>hsqldb</artifactId>
	<scope>runtime</scope>
</dependency>
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 您需要依赖`spring-jdbc`才能自动配置嵌入式数据库。在此示例中，它通过传递`spring-boot-starter-data-jpa`。 |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如果出于某种原因确实为嵌入式数据库配置了连接URL，请确保确保禁用了数据库的自动关闭功能。如果您使用H2，则应该`DB_CLOSE_ON_EXIT=FALSE`这样做。如果使用HSQLDB，则应确保`shutdown=true`未使用它。通过禁用数据库的自动关闭功能，Spring Boot可以控制何时关闭数据库，从而确保一旦不再需要访问数据库时就可以执行该操作。 |

### 31.1.2连接到生产数据库

生产数据库连接也可以通过使用pooling进行自动配置`DataSource`。Spring Boot使用以下算法来选择特定的实现：

1. 我们更喜欢[HikariCP](https://github.com/brettwooldridge/HikariCP)的性能和并发性。如果有HikariCP，我们总是选择它。
2. 否则，如果Tomcat池`DataSource`可用，我们将使用它。
3. 如果HikariCP和Tomcat池数据源均不可用，并且[Commons DBCP2](https://commons.apache.org/proper/commons-dbcp/)不可用，我们将使用它。

如果使用`spring-boot-starter-jdbc`或`spring-boot-starter-data-jpa`“启动器”，则会自动获得对的依赖`HikariCP`。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 您可以完全绕过该算法，并通过设置`spring.datasource.type`属性来指定要使用的连接池。如果您在`tomcat-jdbc`默认情况下提供的Tomcat容器中运行应用程序，则这一点尤其重要。 |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 其他连接池始终可以手动配置。如果定义自己的`DataSource`bean，则不会进行自动配置。 |

DataSource配置由中的外部配置属性控制`spring.datasource.*`。例如，您可以在中声明以下部分`application.properties`：

```bash
spring.datasource.url=jdbc:mysql://localhost/test
spring.datasource.username=dbuser
spring.datasource.password=dbpass
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 您至少应通过设置`spring.datasource.url`属性来指定URL 。否则，Spring Boot会尝试自动配置嵌入式数据库。 |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 您通常不需要指定`driver-class-name`，因为Spring Boot可以从推导大多数数据库`url`。 |

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 对于`DataSource`要创建的池，我们需要能够验证有效的`Driver`类是否可用，因此我们在进行任何操作之前都要进行检查。换句话说，如果您设置`spring.datasource.driver-class-name=com.mysql.jdbc.Driver`，则该类必须是可加载的。 |

请参阅[`DataSourceProperties`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/jdbc/DataSourceProperties.java)以获取更多受支持的选项。这些是不管实际实现如何都起作用的标准选项。也可以微调实现特定的设置，使用各自的前缀（`spring.datasource.hikari.*`，`spring.datasource.tomcat.*`，和`spring.datasource.dbcp2.*`）。有关更多详细信息，请参阅所用连接池实现的文档。

例如，如果使用[Tomcat连接池](https://tomcat.apache.org/tomcat-8.0-doc/jdbc-pool.html#Common_Attributes)，则可以自定义许多其他设置，如以下示例所示：

```bash
# Number of ms to wait before throwing an exception if no connection is available.
spring.datasource.tomcat.max-wait=10000

# Maximum number of active connections that can be allocated from this pool at the same time.
spring.datasource.tomcat.max-active=50

# Validate the connection before borrowing it from the pool.
spring.datasource.tomcat.test-on-borrow=true
```

### 31.1.3连接到JNDI数据源

如果您将Spring Boot应用程序部署到Application Server，则可能需要使用Application Server的内置功能来配置和管理DataSource，并使用JNDI对其进行访问。

该`spring.datasource.jndi-name`属性可以被用作一个替代`spring.datasource.url`，`spring.datasource.username`和`spring.datasource.password`属性来访问`DataSource`从一个特定的JNDI位置。例如，以下部分`application.properties`显示了如何访问定义的JBoss AS `DataSource`：

```bash
spring.datasource.jndi-name=java:jboss/datasources/customers
```

## 31.2使用JdbcTemplate

Spring `JdbcTemplate`和`NamedParameterJdbcTemplate`class是自动配置的，您可以将`@Autowire`它们直接放入自己的bean中，如以下示例所示：

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MyBean {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public MyBean(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// ...

}
```

您可以使用属性来自定义模板的某些属性`spring.jdbc.template.*`，如以下示例所示：

```bash
spring.jdbc.template.max-rows=500
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 在`NamedParameterJdbcTemplate`重复使用相同的`JdbcTemplate`幕后情况。如果`JdbcTemplate`定义了多个，并且不存在主要候选对象，`NamedParameterJdbcTemplate`则不会自动配置。 |