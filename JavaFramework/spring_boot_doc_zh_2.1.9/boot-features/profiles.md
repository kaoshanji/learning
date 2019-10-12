# 25、Profiles

Spring Profiles提供了一种隔离应用程序配置部分并使之仅在某些环境中可用的方法。任何`@Component`或`@Configuration`可以标记`@Profile`为限制时，如以下示例所示：

```java
@Configuration
@Profile("production")
public class ProductionConfiguration {

	// ...

}
```

您可以使用`spring.profiles.active` `Environment`属性来指定哪些配置文件处于活动状态。您可以通过本章前面介绍的任何方式指定属性。例如，您可以将其包括在您的中`application.properties`，如以下示例所示：

```bash
spring.profiles.active=dev,hsqldb
```

您也可以使用以下开关在命令行中指定它：`--spring.profiles.active=dev,hsqldb`。

## 25.1添加活动配置文件

该`spring.profiles.active`属性与其他属性遵循相同的排序规则：最高`PropertySource`获胜。这意味着您可以在其中指定活动配置文件`application.properties`，然后使用命令行开关**替换**它们。

有时，将特定于配置文件的属性**添加**到活动配置文件中而不是替换它们很有用。该`spring.profiles.include`属性可用于无条件添加活动配置文件。该`SpringApplication`入口点还设置附加配置文件的Java API（即那些由活化的顶级`spring.profiles.active`属性）。参见[SpringApplication中](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/api/org/springframework/boot/SpringApplication.html)的`setAdditionalProfiles()`方法。

例如，当使用开关运行具有以下属性的应用程序时`--spring.profiles.active=prod`，`proddb`和`prodmq`配置文件也会被激活：

```bash
---
my.property: fromyamlfile
---
spring.profiles: prod
spring.profiles.include:
  - proddb
  - prodmq
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 请记住，`spring.profiles`可以在YAML文档中定义该属性，以确定何时将该特定文档包括在配置中。有关更多详细信息[，](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-properties-and-configuration.html#howto-change-configuration-depending-on-the-environment)请参见[第77.7节“根据环境更改配置”](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-properties-and-configuration.html#howto-change-configuration-depending-on-the-environment)。 |

## 25.2以编程方式设置配置文件

您可以`SpringApplication.setAdditionalProfiles(…)`在应用程序运行之前通过调用来以编程方式设置活动配置文件。也可以通过使用Spring的`ConfigurableEnvironment`界面来激活配置文件。

## 25.3特定于配置文件的配置文件

`application.properties`（或`application.yml`）和通过引用引用的文件的特定于档案的特定变体`@ConfigurationProperties`都被视为文件并已加载。有关详细信息[，](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-profile-specific-properties)请参见“ [第24.4节“特定](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-profile-specific-properties)于[配置文件的属性”](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-profile-specific-properties) ”。

------