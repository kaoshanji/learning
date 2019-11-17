# 44、会话

Spring Boot为各种数据存储提供了[Spring Session](https://spring.io/projects/spring-session)自动配置。在构建Servlet Web应用程序时，可以自动配置以下存储：

- JDBC
- Redis
- Hazelcast
- MongoDB

构建反应式Web应用程序时，可以自动配置以下存储：

- Redis
- MongoDB

如果类路径上存在单个Spring Session模块，则Spring Boot会自动使用该存储实现。如果您有多个实现，则必须选择[`StoreType`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/session/StoreType.java)要用于存储会话的实现。例如，要将JDBC用作后端存储，可以按以下方式配置应用程序：

```bash
spring.session.store-type=jdbc
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 您可以通过将设置为`store-type`来禁用Spring Session `none`。  |

每个商店都有特定的附加设置。例如，可以为JDBC存储定制表的名称，如以下示例所示：

```bash
spring.session.jdbc.table-name=SESSIONS
```

要设置会话超时，您可以使用`spring.session.timeout`属性。如果未设置该属性，则自动配置将回退到的值`server.servlet.session.timeout`。