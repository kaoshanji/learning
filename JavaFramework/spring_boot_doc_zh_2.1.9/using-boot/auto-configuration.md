# 16、自动配置

Spring Boot自动配置会尝试根据添加的jar依赖项自动配置Spring应用程序。例如，如果`HSQLDB`位于类路径上，并且尚未手动配置任何数据库连接bean，那么Spring Boot会自动配置内存数据库。

您需要通过将`@EnableAutoConfiguration`或`@SpringBootApplication`注释添加到一个`@Configuration`类中来选择自动配置。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 您只能添加一个`@SpringBootApplication`或`@EnableAutoConfiguration`注释。我们通常建议您仅将一个或另一个添加到您的主要`@Configuration`课程中。 |

## 16.1逐步取代自动配置

自动配置是非侵入性的。在任何时候，您都可以开始定义自己的配置，以替换自动配置的特定部分。例如，如果您添加自己的`DataSource`bean，则默认的嵌入式数据库支持将退出。

如果您需要找出当前正在应用的自动配置以及原因，请使用`--debug`开关启动应用程序。这样做可以启用调试日志以供选择核心记录器，并将条件报告记录到控制台。

## 16.2禁用特定的自动配置类

如果发现正在应用不需要的特定自动配置类，则可以使用exclude属性`@EnableAutoConfiguration`来禁用它们，如以下示例所示：

```java
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.jdbc.*;
import org.springframework.context.annotation.*;

@Configuration
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class MyConfiguration {
}
```

如果该类不在类路径中，则可以使用`excludeName`注释的属性，并指定完全限定的名称。最后，您还可以使用`spring.autoconfigure.exclude`属性来控制要排除的自动配置类的列表。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 您可以在注释级别和使用属性来定义排除项。                     |

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 即使有自动配置类`public`，该类的唯一被认为是公共API的方面是可用于禁用自动配置的类的名称。这些类的实际内容（例如嵌套配置类或Bean方法）仅供内部使用，我们不建议直接使用它们。 |