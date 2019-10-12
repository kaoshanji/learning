# 24、外部化配置

Spring Boot使您可以外部化配置，以便可以在不同环境中使用相同的应用程序代码。您可以使用属性文件，YAML文件，环境变量和命令行参数来外部化配置。属性值可以通过直接注射到你的bean `@Value`注释，通过Spring的访问`Environment`抽象，或者被[绑定到结构化对象](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-typesafe-configuration-properties)通过`@ConfigurationProperties`。

Spring Boot使用一个非常特殊的`PropertySource`顺序，该顺序旨在允许合理地覆盖值。按以下顺序考虑属性：

1. 您的主目录上的[Devtools全局设置属性](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-devtools.html#using-boot-devtools-globalsettings)（`~/.spring-boot-devtools.properties`当devtools处于活动状态时）。
2. [`@TestPropertySource`](https://docs.spring.io/spring/docs/5.1.10.RELEASE/javadoc-api/org/springframework/test/context/TestPropertySource.html) 测试中的注释。
3. `properties`测试中的属性。可[用于测试应用程序的特定部分](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications-testing-autoconfigured-tests)[`@SpringBootTest`](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/api/org/springframework/boot/test/context/SpringBootTest.html)的[测试注释](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications-testing-autoconfigured-tests)和[注释](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications-testing-autoconfigured-tests)。
4. 命令行参数。
5. 来自的属性`SPRING_APPLICATION_JSON`（嵌入在环境变量或系统属性中的嵌入式JSON）。
6. `ServletConfig` 初始化参数。
7. `ServletContext` 初始化参数。
8. 的JNDI属性`java:comp/env`。
9. Java系统属性（`System.getProperties()`）。
10. 操作系统环境变量。
11. 一`RandomValuePropertySource`，只有在拥有性能`random.*`。
12. 打包的jar（`application-{profile}.properties`和YAML变体）之外的[特定于配置文件的应用程序属性](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-profile-specific-properties)。
13. 打包在jar中[的特定于配置文件的应用程序属性](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-profile-specific-properties)（`application-{profile}.properties`和YAML变体）。
14. 打包的jar（`application.properties`和YAML变体）之外的应用程序属性。
15. 打包在jar中的应用程序属性（`application.properties`和YAML变体）。
16. [`@PropertySource`](https://docs.spring.io/spring/docs/5.1.10.RELEASE/javadoc-api/org/springframework/context/annotation/PropertySource.html)`@Configuration`类上的注释。
17. 默认属性（通过设置指定`SpringApplication.setDefaultProperties`）。

为了提供一个具体的示例，假设您开发了一个`@Component`使用`name`属性的，如以下示例所示：

```java
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;

@Component
public class MyBean {

    @Value("${name}")
    private String name;

    // ...

}
```

在您的应用程序类路径上（例如，在jar内），您可以使用一个`application.properties`文件，该文件为提供合理的默认属性值`name`。在新环境中运行时，`application.properties`可以在jar外部提供一个文件，该文件将覆盖`name`。对于一次性测试，可以使用特定的命令行开关（例如，`java -jar app.jar --name="Spring"`）启动。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 这些`SPRING_APPLICATION_JSON`属性可以在命令行中提供环境变量。例如，您可以在UN * X shell中使用以下行：`$ SPRING_APPLICATION_JSON ='{“ acme”：{“ name”：“ test”}}}'java -jar myapp.jar`在前面的示例中，您最终`acme.name=test`在Spring中`Environment`。您还可以像`spring.application.json`在System属性中一样提供JSON ，如以下示例所示：`$ java -Dspring.application.json ='{“ name”：“ test”}'-jar myapp.jar`您还可以使用命令行参数来提供JSON，如以下示例所示：`$ java -jar myapp.jar --spring.application.json ='{“ name”：“ test”}'`您还可以将JSON作为JNDI变量提供，如下所示：`java:comp/env/spring.application.json`。 |

## 24.1配置随机值

的`RandomValuePropertySource`是用于注射的随机值（例如，进入机密或试验例）是有用的。它可以产生整数，longs，uuid或字符串，如以下示例所示：

```bash
my.secret=${random.value}
my.number=${random.int}
my.bignumber=${random.long}
my.uuid=${random.uuid}
my.number.less.than.ten=${random.int(10)}
my.number.in.range=${random.int[1024,65536]}
```

该`random.int*`语法是`OPEN value (,max) CLOSE`其中的`OPEN,CLOSE`任何字符和`value,max`是整数。如果`max`提供，`value`则为最小值，`max`为最大值（不包括）。

## 24.2访问命令行属性

默认情况下，`SpringApplication`将所有命令行选项参数（即以开头的参数`--`，例如`--server.port=9000`）转换为a `property`并将其添加到Spring `Environment`。如前所述，命令行属性始终优先于其他属性源。

如果您不想将命令行属性添加到中`Environment`，则可以使用禁用它们`SpringApplication.setAddCommandLineProperties(false)`。

## 24.3应用程序属性文件

`SpringApplication`从`application.properties`以下位置的文件加载属性并将其添加到Spring中`Environment`：

1. 一个`/config`当前目录的子目录
2. 当前目录
3. 类路径`/config`包
4. 类路径根

该列表按优先级排序（在列表较高位置定义的属性会覆盖在较低位置定义的属性）。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 您还可以[使用YAML（.yml）文件](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-yaml)来替代.properties。 |

如果您不喜欢`application.properties`配置文件名，则可以通过指定`spring.config.name`环境属性来切换到另一个文件名。您还可以使用`spring.config.location`环境属性（目录位置或文件路径的逗号分隔列表）来引用显式位置。下面的示例演示如何指定其他文件名：

```bash
$ java -jar myproject.jar --spring.config.name = myproject
```

下面的示例演示如何指定两个位置：

```bash
$ java -jar myproject.jar --spring.config.location = classpath：/default.properties,classpath：/override.properties
```

| ![[警告]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/warning.png) |
| ------------------------------------------------------------ |
| `spring.config.name`并且`spring.config.location`很早就用于确定必须加载哪些文件。必须将它们定义为环境属性（通常是OS环境变量，系统属性或命令行参数）。 |

如果`spring.config.location`包含目录（而不是文件），则应以目录结尾`/`（并且在运行时，在目录后附加从生成`spring.config.name`之前生成的名称，包括特定于配置文件的文件名）。指定的文件`spring.config.location`按原样使用，不支持特定于配置文件的变体，并且被任何特定于配置文件的属性覆盖。

配置位置以相反的顺序搜索。默认情况下，配置的位置是`classpath:/,classpath:/config/,file:./,file:./config/`。结果搜索顺序如下：

1. `file:./config/`
2. `file:./`
3. `classpath:/config/`
4. `classpath:/`

当使用来配置自定义配置位置时`spring.config.location`，它们将替换默认位置。例如，如果`spring.config.location`使用值配置`classpath:/custom-config/,file:./custom-config/`，则搜索顺序将变为以下内容：

1. `file:./custom-config/`
2. `classpath:custom-config/`

另外，当使用来配置自定义配置位置时`spring.config.additional-location`，除默认位置外，还会使用它们。在默认位置之前搜索其他位置。例如，如果`classpath:/custom-config/,file:./custom-config/`配置了的其他位置，则搜索顺序变为以下内容：

1. `file:./custom-config/`
2. `classpath:custom-config/`
3. `file:./config/`
4. `file:./`
5. `classpath:/config/`
6. `classpath:/`

通过此搜索顺序，您可以在一个配置文件中指定默认值，然后在另一个配置文件中有选择地覆盖这些值。您可以在以下默认位置之一中为您的应用程序提供默认值`application.properties`（或使用来选择的其他任何基本名称`spring.config.name`）。然后，可以在运行时使用自定义位置之一中的其他文件覆盖这些默认值。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果您使用环境变量而不是系统属性，则大多数操作系统都不允许使用句点分隔的键名，但是您可以使用下划线代替（例如，`SPRING_CONFIG_NAME`代替`spring.config.name`）。 |

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果您的应用程序在容器中运行，则可以使用JNDI属性（中的`java:comp/env`）或Servlet上下文初始化参数来代替环境变量或系统属性，也可以使用它们。 |

## 24.4特定于配置文件的属性

除`application.properties`文件外，还可以使用以下命名约定来定义特定于配置文件的属性：`application-{profile}.properties`。在`Environment`具有一组默认的配置文件（默认`[default]`）如果没有活动的简档设置中使用。换句话说，如果未显式激活任何概要文件，那么将从`application-default.properties`中加载属性。

特定于配置文件的属性是从与standard相同的位置加载的`application.properties`，特定于配置文件的文件始终会覆盖非特定文件，无论特定于配置文件的文件是在打包jar的内部还是外部。

如果指定了多个配置文件，则采用后赢策略。例如，由`spring.profiles.active`属性指定的配置文件会在通过`SpringApplication`API 配置的配置文件之后添加，因此具有优先权。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果您在中指定了任何文件`spring.config.location`，则不会考虑这些文件的特定于配置文件的变体。`spring.config.location`如果您还想使用特定于配置文件的属性，请使用目录。 |

## 24.5属性中的占位符

使用中的值时，它们会`application.properties`通过现有的值进行过滤`Environment`，因此您可以参考以前定义的值（例如，从“系统”属性中）。

```bash
app.name=MyApp
app.description=${app.name} is a Spring Boot application
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 您还可以使用这种技术来创建现有Spring Boot属性的“简短”变体。有关详细信息，请参见*第77.4节“使用“简短”命令行参数”操作*方法。 |

## 24.6加密属性

Spring Boot不提供对加密属性值的任何内置支持，但是，它提供了修改Spring包含的值所必需的挂钩点`Environment`。该`EnvironmentPostProcessor`界面允许您`Environment`在应用程序启动之前进行操作。有关详细信息[，](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-spring-boot-application.html#howto-customize-the-environment-or-application-context)请参见[第76.3节“在启动前自定义环境或ApplicationContext”](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-spring-boot-application.html#howto-customize-the-environment-or-application-context)。

如果您正在寻找一种安全的方式来存储凭据和密码，则[Spring Cloud Vault](https://cloud.spring.io/spring-cloud-vault/)项目提供了对在[HashiCorp Vault中](https://www.vaultproject.io/)存储外部化配置的支持。

## 24.7使用YAML代替属性

[YAML](https://yaml.org/)是JSON的超集，因此是一种用于指定层次结构配置数据的便捷格式。该`SpringApplication`级自动支持YAML来替代，只要你有属性[SnakeYAML](https://bitbucket.org/asomov/snakeyaml)在classpath库。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果您使用“入门”，则SnakeYAML将由自动提供`spring-boot-starter`。 |

### 24.7.1加载YAML

Spring Framework提供了两个方便的类，可用于加载YAML文档。该`YamlPropertiesFactoryBean`负载YAML作为`Properties`和`YamlMapFactoryBean`负载YAML作为`Map`。

例如，考虑以下YAML文档：

```bash
environments:
	dev:
		url: https://dev.example.com
		name: Developer Setup
	prod:
		url: https://another.example.com
		name: My Cool App
```

前面的示例将转换为以下属性：

```bash
environments.dev.url=https://dev.example.com
environments.dev.name=Developer Setup
environments.prod.url=https://another.example.com
environments.prod.name=My Cool App
```

YAML列表表示为带有`[index]`解引用器的属性键。例如，考虑以下YAML：

```bash
my:
servers:
	- dev.example.com
	- another.example.com
```

前面的示例将转换为以下属性：

```bash
my.servers[0]=dev.example.com
my.servers[1]=another.example.com
```

要使用Spring Boot的`Binder`实用程序绑定属性（`@ConfigurationProperties`确实如此），您需要在类型为`java.util.List`（或`Set`）的目标bean中具有一个属性，并且需要提供setter或使用可变值对其进行初始化。例如，以下示例绑定到前面显示的属性：

```java
@ConfigurationProperties(prefix="my")
public class Config {

	private List<String> servers = new ArrayList<String>();

	public List<String> getServers() {
		return this.servers;
	}
}
```

### 24.7.2在Spring环境中将YAML公开为属性

本`YamlPropertySourceLoader`类可用于暴露YAML作为`PropertySource`在春节`Environment`。这样做使您可以将`@Value`注释和占位符语法一起使用以访问YAML属性。

### 24.7.3多配置文件YAML文档

您可以使用一个`spring.profiles`键来指示文档何时适用，从而在一个文件中指定多个特定于配置文件的YAML文档，如以下示例所示：

```java
server:
	address: 192.168.1.100
---
spring:
	profiles: development
server:
	address: 127.0.0.1
---
spring:
	profiles: production & eu-central
server:
	address: 192.168.1.120
```

在前面的示例中，如果`development`配置文件处于活动状态，则`server.address`属性为`127.0.0.1`。同样，如果`production` **和** `eu-central`配置文件处于活动状态，则`server.address`属性为`192.168.1.120`。如果`development`，`production`并`eu-central`在配置文件**没有**启用，那么该属性的值`192.168.1.100`。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| `spring.profiles`因此可以包含一个简单的配置文件名称（例如`production`）或一个配置文件表达式。配置文件表达式允许例如表达更复杂的配置文件逻辑`production & (eu-central | eu-west)`。有关更多详细信息，请参阅[参考指南](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/core.html#beans-definition-profiles-java)。 |

如果在启动应用程序上下文时未明确激活任何活动，则会激活默认配置文件。因此，在以下YAML中，我们设置了一个**仅**在“默认”配置文件中`spring.security.user.password`可用的值：

```bash
server:
  port: 8000
---
spring:
  profiles: default
  security:
    user:
      password: weak
```

而在以下示例中，始终设置密码是因为该密码未附加到任何配置文件，并且必须根据需要在所有其他配置文件中将其显式重置：

```bash
server:
  port: 8000
spring:
  security:
    user:
      password: weak
```

使用`spring.profiles`元素指定的弹簧轮廓可以选择使用`!`字符来否定。如果为单个文档指定了否定的配置文件和非否定的配置文件，则至少一个非否定的配置文件必须匹配，并且否定的配置文件不能匹配。

### 24.7.4 YAML的缺点

无法通过使用`@PropertySource`注释加载YAML文件。因此，在需要以这种方式加载值的情况下，需要使用属性文件。

在特定于配置文件的YAML文件中使用多YAML文档语法可能会导致意外行为。例如，考虑文件中的以下配置：

**application-dev.yml。** 

```bash
server:
  port: 8000
---
spring:
  profiles: "!test"
  security:
    user:
      password: "secret"
```



如果将参数`--spring.profiles.active=dev" you might expect `security.user.password`设置为“秘密” 运行应用程序，则不是这种情况。

嵌套文档将被过滤，因为主文件名为`application-dev.yml`。它已经被认为是特定于配置文件的，并且嵌套文档将被忽略。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 我们建议您不要混用特定于配置文件的YAML文件和多个YAML文档。坚持只使用其中之一。 |

## 24.8类型安全的配置属性

使用`@Value("${property}")`注释注入配置属性有时会很麻烦，尤其是当您使用多个属性或数据本质上是分层的时。Spring Boot提供了一种使用属性的替代方法，该属性使强类型的Bean可以管理和验证应用程序的配置，如以下示例所示：

```java
package com.example;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("acme")
public class AcmeProperties {

	private boolean enabled;

	private InetAddress remoteAddress;

	private final Security security = new Security();

	public boolean isEnabled() { ... }

	public void setEnabled(boolean enabled) { ... }

	public InetAddress getRemoteAddress() { ... }

	public void setRemoteAddress(InetAddress remoteAddress) { ... }

	public Security getSecurity() { ... }

	public static class Security {

		private String username;

		private String password;

		private List<String> roles = new ArrayList<>(Collections.singleton("USER"));

		public String getUsername() { ... }

		public void setUsername(String username) { ... }

		public String getPassword() { ... }

		public void setPassword(String password) { ... }

		public List<String> getRoles() { ... }

		public void setRoles(List<String> roles) { ... }

	}
}
```

前面的POJO定义了以下属性：

- `acme.enabled`，`false`默认值为。
- `acme.remote-address`，其类型可以从强制转换`String`。
- `acme.security.username`，其嵌套的“安全”对象的名称由属性的名称确定。特别是，返回类型根本不使用，可能已经使用过`SecurityProperties`。
- `acme.security.password`。
- `acme.security.roles`，带有的集合`String`。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| Spring Boot自动配置大量利用`@ConfigurationProperties`来轻松配置自动配置的Bean。与自动配置类相似`@ConfigurationProperties`，Spring Boot中可用的类仅供内部使用。通过属性文件，YAML文件，环境变量等配置的映射到该类的属性是公共API，但是该类本身的内容并不意味着可以直接使用。 |

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| Getter和Setter通常是强制性的，因为绑定是通过标准Java Beans属性描述符进行的，就像在Spring MVC中一样。在以下情况下，可以忽略二传手：只要将地图初始化，它们就需要使用吸气剂，但不一定需要使用setter，因为它们可以被活页夹改变。可以通过索引（通常使用YAML）或使用单个逗号分隔的值（属性）来访问集合和数组。在后一种情况下，必须使用二传手。我们建议始终为此类类型添加设置器。如果初始化集合，请确保它不是不可变的（如上例所示）。如果嵌套的POJO属性已初始化（如`Security`前面示例中的字段），则不需要setter。如果希望活页夹通过使用其默认构造函数动态创建实例，则需要一个setter。有些人使用Lombok项目自动添加获取器和设置器。确保Lombok不会为这种类型生成任何特定的构造函数，因为容器会自动使用它来实例化该对象。最后，仅考虑标准Java Bean属性，不支持对静态属性的绑定。 |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 另请参阅[和](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-vs-value)[之间`@Value``@ConfigurationProperties`](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-vs-value)的[区别](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-vs-value)。 |

您还需要列出要在`@EnableConfigurationProperties`注释中注册的属性类，如以下示例所示：

```java
@Configuration
@EnableConfigurationProperties(AcmeProperties.class)
public class MyConfiguration {
}
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 以`@ConfigurationProperties`这种方式注册bean时，bean具有常规名称：`<prefix>-<fqn>`，其中`<prefix>`是在`@ConfigurationProperties`注释中指定的环境密钥前缀，并且`<fqn>`是bean的完全限定名称。如果注释不提供任何前缀，则仅使用Bean的完全限定名称。上例中的Bean名称为`acme-com.example.AcmeProperties`。 |

前面的配置为创建一个常规bean `AcmeProperties`。我们建议`@ConfigurationProperties`仅处理环境，尤其不要从上下文中注入其他bean。请记住，`@EnableConfigurationProperties`注释*也会*自动应用到您的项目中，以便从中配置任何*已*注释的*现有* bean 。取而代之的注释用，你可以做一个bean，如下面的例子：`@ConfigurationProperties``Environment``MyConfiguration``@EnableConfigurationProperties(AcmeProperties.class)``AcmeProperties`

```java
@Component
@ConfigurationProperties(prefix="acme")
public class AcmeProperties {

	// ... see the preceding example

}
```

这种配置样式特别适用于`SpringApplication`外部YAML配置，如以下示例所示：

```bash
# application.yml

acme:
	remote-address: 192.168.1.1
	security:
		username: admin
		roles:
		  - USER
		  - ADMIN

# additional configuration as required
```

要使用`@ConfigurationProperties`bean，可以像使用其他任何bean一样注入它们，如以下示例所示：

```java
@Service
public class MyService {

	private final AcmeProperties properties;

	@Autowired
	public MyService(AcmeProperties properties) {
	    this.properties = properties;
	}

 	//...

	@PostConstruct
	public void openConnection() {
		Server server = new Server(this.properties.getRemoteAddress());
		// ...
	}

}

```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 使用using `@ConfigurationProperties`还可以生成可由IDE使用的元数据文件，以为您自己的键提供自动完成功能。有关详细信息[，](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/configuration-metadata.html)请参见[附录B，](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/configuration-metadata.html)[*配置元数据*](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/configuration-metadata.html)附录。 |

### 24.8.1第三方配置

除了`@ConfigurationProperties`用于注释类之外，您还可以在公共`@Bean`方法上使用它。当您要将属性绑定到控件之外的第三方组件时，这样做特别有用。

要通过`Environment`属性配置bean ，请添加`@ConfigurationProperties`到其bean注册中，如以下示例所示：

```java
@ConfigurationProperties(prefix = "another")
@Bean
public AnotherComponent anotherComponent() {
	...
}
```

用`another`前缀定义的任何属性都`AnotherComponent`以类似于前面`AcmeProperties`示例的方式映射到该bean 。

### 24.8.2轻松绑定

Spring Boot使用一些宽松的规则将`Environment`属性绑定到`@ConfigurationProperties`Bean，因此`Environment`属性名称和Bean属性名称之间不需要完全匹配。有用的常见示例包括破折号分隔的环境属性（例如，`context-path`绑定到`contextPath`）和大写的环境属性（例如，`PORT`绑定到`port`）。

例如，考虑以下`@ConfigurationProperties`类：

```java
@ConfigurationProperties(prefix="acme.my-project.person")
public class OwnerProperties {

	private String firstName;

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

}
```

使用前面的代码，可以全部使用以下属性名称：



**表24.1。轻松绑定**

| 属性                                | 注意                                                         |
| ----------------------------------- | ------------------------------------------------------------ |
| `acme.my-project.person.first-name` | 烤肉串盒，建议在`.properties`和`.yml`文件中使用。            |
| `acme.myProject.person.firstName`   | 标准驼峰式语法。                                             |
| `acme.my_project.person.first_name` | 下划线表示法，是在`.properties`和`.yml`文件中使用的另一种格式。 |
| `ACME_MYPROJECT_PERSON_FIRSTNAME`   | 大写格式，使用系统环境变量时建议使用。                       |



| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| `prefix`注释的值*必须*为kebab大小写（小写并用分隔`-`，例如`acme.my-project.person`）。 |



**表24.2。每个属性源的宽松绑定规则**

| 财产来源 | 简单                                                   | 清单                                                         |
| -------- | ------------------------------------------------------ | ------------------------------------------------------------ |
| 属性文件 | 骆驼案，烤肉串案或下划线                               | 使用`[ ]`或以逗号分隔的值的标准列表语法                      |
| YAML文件 | 骆驼案，烤肉串案或下划线                               | 标准YAML列表语法或逗号分隔的值                               |
| 环境变量 | 以下划线作为定界符的大写格式。 `_`不应在属性名称中使用 | 下划线括起来的数值，例如 `MY_ACME_1_OTHER = my.acme[1].other` |
| 系统属性 | 骆驼案，烤肉串案或下划线                               | 使用`[ ]`或以逗号分隔的值的标准列表语法                      |



| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 我们建议，如果可能，属性以小写kebab格式存储，例如`my.property-name=acme`。 |

绑定到`Map`属性时，如果`key`包含除小写字母数字字符或之外的任何内容，则`-`需要使用方括号表示法，以便保留原始值。如果键没有被包围`[]`，则所有非字母数字或`-`已删除的字符。例如，考虑将以下属性绑定到`Map`：

```bash
acme:
  map:
    "[/key1]": value1
    "[/key2]": value2
    /key3: value3
```

上面的性质将结合一个`Map`具有`/key1`，`/key2`并`key3`作为映射中的键。

### 24.8.3合并复杂类型

如果在多个位置配置了列表，则通过替换整个列表来进行覆盖。

例如，假定默认情况下`MyPojo`具有`name`和`description`属性的对象`null`。以下示例公开了`MyPojo`来自的对象列表`AcmeProperties`：

```java
@ConfigurationProperties("acme")
public class AcmeProperties {

	private final List<MyPojo> list = new ArrayList<>();

	public List<MyPojo> getList() {
		return this.list;
	}

}
```

考虑以下配置：

```bash
acme:
  list:
    - name: my name
      description: my description
---
spring:
  profiles: dev
acme:
  list:
    - name: my another name
```

如果`dev`配置文件未激活，则`AcmeProperties.list`包含一个`MyPojo`条目，如先前所定义。`dev`但是，如果启用了配置文件，则`list` *仍然*仅包含一个条目（名称为`my another name`，说明为`null`）。此配置*不会*将第二个`MyPojo`实例添加到列表中，并且不会合并项目。

`List`在多个配置文件中指定a时，将使用优先级最高的一个（并且只有该优先级）。考虑以下示例：

```bash
acme:
  list:
    - name: my name
      description: my description
    - name: another name
      description: another description
---
spring:
  profiles: dev
acme:
  list:
    - name: my another name
```

在前面的示例中，如果`dev`配置文件处于活动状态，则`AcmeProperties.list`包含*一个* `MyPojo`条目（名称为`my another name`，说明为`null`）。对于YAML，可以使用逗号分隔的列表和YAML列表来完全覆盖列表的内容。

对于`Map`属性，可以绑定从多个来源绘制的属性值。但是，对于多个源中的同一属性，将使用优先级最高的属性。以下示例公开了一个`Map<String, MyPojo>`from `AcmeProperties`：

```java
@ConfigurationProperties("acme")
public class AcmeProperties {

	private final Map<String, MyPojo> map = new HashMap<>();

	public Map<String, MyPojo> getMap() {
		return this.map;
	}

}
```

考虑以下配置：

```bash
acme:
  map:
    key1:
      name: my name 1
      description: my description 1
---
spring:
  profiles: dev
acme:
  map:
    key1:
      name: dev name 1
    key2:
      name: dev name 2
      description: dev description 2
```

如果`dev`配置文件未处于活动状态，则`AcmeProperties.map`包含一个带有键的条目`key1`（名称为`my name 1`，说明为`my description 1`）。`dev`但是，如果启用了配置文件，则`map`包含两个带有键的条目`key1`（名称为`dev name 1`和的描述为`my description 1`和`key2`，名称为`dev name 2`和的描述为`dev description 2`）。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 前述合并规则不仅适用于YAML文件，而且适用于所有属性源中的属性。 |

### 24.8.4属性转换

当Spring Boot绑定到`@ConfigurationProperties`bean 时，它试图将外部应用程序属性强制为正确的类型。如果您需要自定义类型转换，则可以提供一个`ConversionService`Bean（具有一个名为的Bean `conversionService`）或一个定制属性编辑器（通过一个`CustomEditorConfigurer`Bean）或定制`Converters`（具有定义为的Bean `@ConfigurationPropertiesBinding`）。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 由于在应用程序生命周期中非常早就请求了此bean，因此请确保限制您`ConversionService`正在使用的依赖项。通常，您需要的任何依赖项可能在创建时未完全初始化。`ConversionService`如果配置键强制不需要自定义，则您可能想重命名自定义，而仅依赖具有限定符的自定义转换器`@ConfigurationPropertiesBinding`。 |

#### 转换时间

Spring Boot为表达持续时间提供了专门的支持。如果公开`java.time.Duration`属性，则应用程序属性中的以下格式可用：

- 常规`long`表示形式（使用毫秒作为默认单位，除非`@DurationUnit`已指定a）
- 标准的ISO-8601格式[的使用`java.time.Duration`](https://docs.oracle.com/javase/8/docs/api//java/time/Duration.html#parse-java.lang.CharSequence-)
- 值和单位耦合的更易读的格式（例如，`10s`表示10秒）

考虑以下示例：

```java
@ConfigurationProperties("app.system")
public class AppSystemProperties {

	@DurationUnit(ChronoUnit.SECONDS)
	private Duration sessionTimeout = Duration.ofSeconds(30);

	private Duration readTimeout = Duration.ofMillis(1000);

	public Duration getSessionTimeout() {
		return this.sessionTimeout;
	}

	public void setSessionTimeout(Duration sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public Duration getReadTimeout() {
		return this.readTimeout;
	}

	public void setReadTimeout(Duration readTimeout) {
		this.readTimeout = readTimeout;
	}

}
```

要指定30秒的会话超时`30`，`PT30S`和`30s`都等效。的500ms的读超时可以以任何形式如下指定：`500`，`PT0.5S`和`500ms`。

您也可以使用任何受支持的单位。这些是：

- `ns` 十亿分之一秒
- `us` 微秒
- `ms` 毫秒
- `s` 几秒钟
- `m` 几分钟
- `h` 用了几个小时
- `d` 好几天

默认单位是毫秒，可以使用`@DurationUnit`上面的示例中所示的方法进行覆盖。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如果您要从仅`Long`用于表示持续时间的先前版本进行升级，请确保`@DurationUnit`在切换到的时间不是毫秒数的情况下（使用）定义单位`Duration`。这样做可以提供透明的升级路径，同时支持更丰富的格式。 |

#### 转换数据大小

Spring Framework的`DataSize`值类型表示字节大小。如果公开`DataSize`属性，则应用程序属性中的以下格式可用：

- 常规`long`表示形式（除非`@DataSizeUnit`已指定，否则使用字节作为默认单位）
- 值和单位耦合的更具可读性的格式（例如，`10MB`意味着10兆字节）

考虑以下示例：

```java
@ConfigurationProperties("app.io")
public class AppIoProperties {

	@DataSizeUnit(DataUnit.MEGABYTES)
	private DataSize bufferSize = DataSize.ofMegabytes(2);

	private DataSize sizeThreshold = DataSize.ofBytes(512);

	public DataSize getBufferSize() {
		return this.bufferSize;
	}

	public void setBufferSize(DataSize bufferSize) {
		this.bufferSize = bufferSize;
	}

	public DataSize getSizeThreshold() {
		return this.sizeThreshold;
	}

	public void setSizeThreshold(DataSize sizeThreshold) {
		this.sizeThreshold = sizeThreshold;
	}

}
```

指定10 MB的缓冲区大小，`10`并且`10MB`等效。可以将256个字节的大小阈值指定为`256`或`256B`。

您也可以使用任何受支持的单位。这些是：

- `B` 对于字节
- `KB` 千字节
- `MB` 兆字节
- `GB` 千兆字节
- `TB` 太字节

默认单位是字节，可以使用`@DataSizeUnit`上面的示例中所示的方法覆盖它。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如果要从仅`Long`用于表示大小的先前版本进行升级，请确保`@DataSizeUnit`在切换到的旁边没有字节的情况下定义单位（使用）`DataSize`。这样做可以提供透明的升级路径，同时支持更丰富的格式。 |

### 24.8.5 @ConfigurationProperties验证

`@ConfigurationProperties`每当使用Spring的`@Validated`注释对其进行注释时，Spring Boot就会尝试验证类。您可以`javax.validation`直接在配置类上使用JSR-303 约束注释。为此，请确保在类路径上有兼容的JSR-303实现，然后将约束注释添加到字段中，如以下示例所示：

```java
@ConfigurationProperties(prefix="acme")
@Validated
public class AcmeProperties {

	@NotNull
	private InetAddress remoteAddress;

	// ... getters and setters

}
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 您还可以通过使用注释`@Bean`创建配置属性的方法来触发验证`@Validated`。 |

尽管绑定时也会验证嵌套属性，但最好也将关联字段注释为`@Valid`。这样可确保即使未找到嵌套属性也将触发验证。下面的示例基于前面的`AcmeProperties`示例：

```java
@ConfigurationProperties(prefix="acme")
@Validated
public class AcmeProperties {

	@NotNull
	private InetAddress remoteAddress;

	@Valid
	private final Security security = new Security();

	// ... getters and setters

	public static class Security {

		@NotEmpty
		public String username;

		// ... getters and setters

	}

}
```

您还可以`Validator`通过创建名为的bean定义来添加自定义Spring `configurationPropertiesValidator`。该`@Bean`方法应声明`static`。配置属性验证器是在应用程序生命周期的早期创建的，并且将`@Bean`方法声明为static可以使Bean得以创建而不必实例化`@Configuration`该类。这样做避免了由早期实例化引起的任何问题。有一个[属性验证示例](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-samples/spring-boot-sample-property-validation)，显示了如何进行设置。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 该`spring-boot-actuator`模块包括一个公开所有`@ConfigurationProperties`bean 的端点。将您的Web浏览器指向`/actuator/configprops`或使用等效的JMX端点。有关详细信息，请参见“ [生产就绪功能](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/production-ready-endpoints.html) ”部分。 |

### 24.8.6 @ConfigurationProperties与@Value

的`@Value`注释是核心容器的功能，和它不提供相同的功能，类型安全配置属性。下表总结了`@ConfigurationProperties`和支持的功能`@Value`：

| 特征                                                         | `@ConfigurationProperties` | `@Value` |
| ------------------------------------------------------------ | -------------------------- | -------- |
| [宽松的绑定](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-relaxed-binding) | 是                         | 没有     |
| [元数据支持](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/configuration-metadata.html) | 是                         | 没有     |
| `SpEL` 评价                                                  | 没有                       | 是       |

如果您为自己的组件定义了一组配置键，我们建议您将它们组合在以标记的POJO中`@ConfigurationProperties`。您还应该意识到，由于`@Value`不支持宽松的绑定，因此如果您需要通过使用环境变量来提供值，则不是一个很好的选择。

最后，尽管您可以在其中写入`SpEL`表达式`@Value`，但不会从[应用程序属性文件中](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files)处理此类表达式。