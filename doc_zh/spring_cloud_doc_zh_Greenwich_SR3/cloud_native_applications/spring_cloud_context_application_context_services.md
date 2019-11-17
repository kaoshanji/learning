# 2. Spring Cloud 上下文：应用内容服务

## 2. Spring Cloud Context：应用程序上下文服务

Spring Boot对于如何使用Spring构建应用程序有一个坚定的看法。例如，它具有用于公共配置文件的常规位置，并具有用于公共管理和监视任务的端点。Spring Cloud在此基础上构建并添加了一些功能，这些功能可能是系统中所有组件可能会使用或偶尔需要的。

## 2.1 Bootstrap应用程序上下文

Spring Cloud应用程序通过创建“ bootstrap ”上下文进行操作，该上下文是主应用程序的父上下文。它负责从外部源加载配置属性，并负责解密本地外部配置文件中的属性。这两个上下文共享一个`Environment`，这是任何Spring应用程序的外部属性的来源。默认情况下，引导程序属性（不是`bootstrap.properties`引导程序阶段加载的属性）具有较高的优先级，因此它们不能被本地配置覆盖。

引导上下文使用不同于主应用程序上下文的约定来定位外部配置。可以使用代替`application.yml`（或`.properties`），`bootstrap.yml`将引导程序的外部配置和主上下文很好地分开。以下清单显示了一个示例：

**bootstrap.yml。** 

```properties
spring:
  application:
    name: foo
  cloud:
    config:
      uri: ${SPRING_CONFIG_URI:http://localhost:8888}
```



如果您的应用程序需要来自服务器的任何特定于应用程序的配置，则最好设置`spring.application.name`（in `bootstrap.yml`或`application.yml`）。为了将该属性`spring.application.name`用作应用程序的上下文ID，您必须在中设置它`bootstrap.[properties | yml]`。

您可以通过设置`spring.cloud.bootstrap.enabled=false`来完全禁用引导过程（例如，在系统属性中）。

## 2.2应用程序上下文层次结构

如果从`SpringApplication`或构建应用程序上下文`SpringApplicationBuilder`，那么Bootstrap上下文将作为父级添加到该上下文。Spring的一个功能是子上下文从其父级继承属性源和配置文件，因此，与没有Spring Cloud Config的相同上下文相比，“ main ”应用程序上下文包含其他属性源。其他属性来源是：

- “ bootstrap ”：如果`PropertySourceLocators`在Bootstrap上下文中找到任何内容，并且它们具有非空属性，`CompositePropertySource`则会显示具有高优先级的可选内容。一个示例就是Spring Cloud Config Server中的属性。有关如何自定义此属性源内容的说明[，](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_context_application_context_services.html#customizing-bootstrap-property-sources)请参见“ [第2.6节“自定义Bootstrap属性源”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_context_application_context_services.html#customizing-bootstrap-property-sources) ”。
- “ applicationConfig：[classpath：bootstrap.yml] ”（如果Spring配置文件处于活动状态，则为相关文件）：如果具有`bootstrap.yml`（或`.properties`），则这些属性用于配置Bootstrap上下文。然后，当它们的父级被设置时，它们被添加到子级上下文中。它们的优先级低于`application.yml`（或`.properties`）和在创建Spring Boot应用程序过程中通常添加到子级的任何其他属性源的优先级。有关如何自定义这些属性源内容的说明，请参见“ [第2.3节“更改引导程序属性的位置”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_context_application_context_services.html#customizing-bootstrap-properties) ”。

由于属性源的排序规则，“ bootstrap ”条目优先。但是，请注意，这些不包含来自的任何数据，这些数据的`bootstrap.yml`优先级非常低，但可用于设置默认值。

您可以通过设置任何父上下文扩展上下文结构`ApplicationContext`创建-例如，通过使用自己的接口或与`SpringApplicationBuilder`方便的方法（`parent()`，`child()`和`sibling()`）。引导上下文是您创建的最高级祖先的父级。层次结构中的每个上下文都有其自己的“ bootstrap ”（可能为空）属性源，以避免无意间将价值从父辈提升到子孙后代。如果有配置服务器，则层次结构中的每个上下文原则上也可以有不同的`spring.application.name`因此，另一个远程资源来源。常规的Spring应用程序上下文行为规则适用于属性解析：子上下文中的属性会按名称以及属性源名称覆盖父级属性。（如果子项具有与父项同名的属性源，则子项中不包括父项的值）。

请注意，`SpringApplicationBuilder`可以让您`Environment`在整个层次结构中共享一个，但这不是默认设置。因此，同级上下文尤其不需要具有相同的配置文件或属性源，即使它们可能与其父级共享相同的值。

## 2.3更改引导程序属性的位置

的`bootstrap.yml`（或`.properties`）位置可以通过设置来指定`spring.cloud.bootstrap.name`（默认值：`bootstrap`）或`spring.cloud.bootstrap.location`（默认值：空） -例如，在系统性能。这些属性的行为类似于`spring.config.*`具有相同名称的变量。实际上，它们是通过在中设置引导程序`ApplicationContext`属性来设置引导程序的`Environment`。如果有一个活动的配置文件（从`spring.profiles.active`或通过`Environment`在上下文你正在构建API），在配置文件属性获取加载以及，同样作为普通春天启动的应用程序-例如，从`bootstrap-development.properties`一个`development`轮廓。

## 2.4覆盖远程属性的值

通过引导上下文添加到应用程序的属性源通常是“ 远程的 ”（例如，来自Spring Cloud Config Server）。默认情况下，不能在本地覆盖它们。如果要让您的应用程序使用其自己的系统属性或配置文件覆盖远程属性，则远程属性源必须通过设置来授予其权限`spring.cloud.config.allowOverride=true`（在本地设置此属性无效）。设置该标志后，将使用两个更细粒度的设置来控制远程属性相对于系统属性和应用程序本地配置的位置：

- `spring.cloud.config.overrideNone=true`：从任何本地属性源覆盖。
- `spring.cloud.config.overrideSystemProperties=false`注意：仅系统属性，命令行参数和环境变量（而不是本地配置文件）应覆盖远程设置。

## 2.5自定义Bootstrap配置

通过将条目添加到`/META-INF/spring.factories`名为的键下，可以将引导上下文设置为执行您喜欢的任何操作`org.springframework.cloud.bootstrap.BootstrapConfiguration`。它包含`@Configuration`用于创建上下文的Spring 类的逗号分隔列表。您可以在此处创建要用于主应用程序上下文进行自动装配的任何bean。`@Beans`类型的特殊合同`ApplicationContextInitializer`。如果要控制启动顺序，则可以用`@Order`注释标记类（默认顺序为`last`）。

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| 添加custom时`BootstrapConfiguration`，请注意不要将添加的类`@ComponentScanned`错误地插入到可能不需要它们的“ 主 ”应用程序上下文中。为引导配置类使用单独的程序包名称，并确保您的名称`@ComponentScan`或带`@SpringBootApplication`注释的配置类尚未覆盖该名称。 |

引导过程结束时，将初始化程序注入到主`SpringApplication`实例中（这是正常的Spring Boot启动序列，无论它是作为独立应用程序运行还是在应用程序服务器中部署）。首先，从中找到的类创建引导上下文`spring.factories`。然后，在启动之前将所有`@Beans`类型`ApplicationContextInitializer`添加到主体`SpringApplication`。

## 2.6自定义Bootstrap属性源

引导过程添加的外部配置的默认属性源是Spring Cloud Config Server，但是您可以通过将类型的bean添加`PropertySourceLocator`到引导上下文（通过`spring.factories`）来添加其他源。例如，您可以从其他服务器或数据库插入其他属性。

例如，请考虑以下定制定位器：

```java
@Configuration
public class CustomPropertySourceLocator implements PropertySourceLocator {

    @Override
    public PropertySource<?> locate(Environment environment) {
        return new MapPropertySource("customProperty",
                Collections.<String, Object>singletonMap("property.from.sample.custom.source", "worked as intended"));
    }

}
```

该`Environment`传递进来的是一个用于`ApplicationContext`将要创建的-换句话说，一个为我们提供更多的财产来源。它已经有其正常的Spring Boot提供的属性源，因此您可以使用这些属性源来查找特定于此的属性源`Environment`（例如，通过将其键入`spring.application.name`，就像在默认的Spring Cloud Config Server属性源定位器中所做的那样）。

如果您在其中包含此类的jar创建一个jar，然后添加一个`META-INF/spring.factories`包含以下`customProperty` `PropertySource`内容的jar，则在包含该jar的classpath的任何应用程序中都会出现：

```bash
org.springframework.cloud.bootstrap.BootstrapConfiguration=sample.custom.CustomPropertySourceLocator
```

## 2.7日志配置

如果要使用Spring Boot配置日志设置，则应将此配置放在`bootstrap。[yml | 属性]，如果您希望将其应用于所有事件。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 为了使Spring Cloud正确初始化日志记录配置，您不能使用自定义前缀。例如，`custom.loggin.logpath`初始化日志记录系统时，Spring Cloud不会识别使用。 |

## 2.8环境变化

用于该应用程序监听`EnvironmentChangeEvent`并反应在几个标准方法的变化（附加`ApplicationListeners`可添加如`@Beans`通过以正常的方式的用户）。当`EnvironmentChangeEvent`观察到时，它具有已更改的键值的列表，应用程序使用这些键值来：

- 重新绑定`@ConfigurationProperties`上下文中的所有bean
- 在以下位置设置记录器级别的任何属性 `logging.level.*`

请注意，默认情况下，Config Client不会轮询中的更改`Environment`。通常，我们不建议您使用这种方法来检测更改（尽管您可以使用 `@Scheduled`批注进行设置）。如果您具有横向扩展的客户端应用程序，最好将其广播`EnvironmentChangeEvent`给所有实例，而不是让它们轮询更改（例如，通过使用[Spring Cloud Bus](https://github.com/spring-cloud/spring-cloud-bus)）。

`EnvironmentChangeEvent`只要您可以实际更改`Environment`和发布事件，本章就涵盖了一大类刷新用例。请注意，这些API是公共的，并且是核心Spring的一部分）。您可以`@ConfigurationProperties`通过访问`/configprops`端点来验证更改是否已绑定到bean （正常的Spring Boot Actuator功能）。例如，`DataSource`可以`maxPoolSize`在运行时更改其名称（`DataSource`Spring Boot创建的默认值为`@ConfigurationProperties`Bean）并动态增加容量。重新绑定`@ConfigurationProperties`不涉及另一类大类用例，在这些用例中，您需要对刷新有更多的控制，并且需要进行更改以使整体成为原子`ApplicationContext`。为了解决这些问题，我们有`@RefreshScope`。

## 2.9刷新范围

进行配置更改时，`@Bean`标记为的Spring `@RefreshScope`会得到特殊处理。此功能解决了有状态Bean的问题，只有在初始化它们时才注入配置。例如，如果`DataSource`通过更改数据库URL时a 具有开放的连接`Environment`，则您可能希望这些连接的持有人能够完成它们的工作。然后，下次某物从池中借用连接时，它将获得具有新URL的连接。

有时，甚至可能必须将`@RefreshScope` 注释应用于只能初始化一次的某些bean。如果bean是“不可变的”，则必须使用注释bean `@RefreshScope` 或在属性key下指定类名 `spring.cloud.refresh.extra-refreshable`。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 如果您`DataSource`自己创建一个bean并且实现是a `HikariDataSource`，则返回最特定的类型，在这种情况下为`HikariDataSource`。否则，您需要设置 `spring.cloud.refresh.extra-refreshable=javax.sql.DataSource`。 |      |

刷新作用域bean是惰性代理，它们在使用时（即，在调用方法时）进行初始化，并且作用域充当初始化值的缓存。要强制bean在下一个方法调用上重新初始化，必须使它的缓存条目无效。

该`RefreshScope`是在上下文中的豆和具有公共`refreshAll()`方法通过清除目标缓存刷新范围内的所有豆。的`/refresh`端点暴露该功能（通过HTTP或JMX）。要通过名称刷新单个bean，还有一种`refresh(String)`方法。

要公开`/refresh`端点，您需要在应用程序中添加以下配置：

```properties
management:
  endpoints:
    web:
      exposure:
        include: refresh
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| `@RefreshScope`（在技术上）在一个`@Configuration`类上工作，但这可能会导致令人惊讶的行为。例如，这并不意味着`@Beans`该类中的所有定义本身都在中`@RefreshScope`。具体来说，除非刷新本身位于，否则任何依赖于这些bean的东西都不能依赖于刷新启动时对其进行更新`@RefreshScope`。在这种情况下，将在刷新时重建它，并重新注入其依赖项。此时，它们将从刷新的`@Configuration`）中重新初始化。 |

## 2.10加密和解密

Spring Cloud有一个`Environment`预处理器，用于在本地解密属性值。它遵循与Config Server相同的规则，并通过进行相同的外部配置`encrypt.*`。因此，您可以使用`{cipher}*`和形式的加密值，只要有有效的密钥，就可以在主应用程序上下文获得`Environment`设置之前对它们进行解密。要在应用程序中使用加密功能，您需要在类路径中包含Spring Security RSA（Maven坐标：“ org.springframework.security:spring-security-rsa”），并且您还需要在其中包含完整的JCE扩展。您的JVM。

如果由于“密钥大小非法”而导致异常，并且使用Sun的JDK，则需要安装Java密码学扩展（JCE）无限强度管辖权策略文件。有关更多信息，请参见以下链接：

- [Java 6 JCE](https://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html)
- [Java 7 JCE](https://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html)
- [Java 8 JCE](https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

将文件解压缩到所使用的JRE / JDK x64 / x86版本的JDK / jre / lib / security文件夹中。

## 2.11端点

对于Spring Boot Actuator应用程序，可以使用一些其他管理端点。您可以使用：

- `POST`以`/actuator/env`更新`Environment`和重新绑定`@ConfigurationProperties`以及日志级别。
- `/actuator/refresh`重新加载引导上下文并刷新`@RefreshScope`Bean。
- `/actuator/restart`关闭`ApplicationContext`并重新启动它（默认情况下处于禁用状态）。
- `/actuator/pause`和`/actuator/resume`调用的`Lifecycle`方法（`stop()`和`start()`上`ApplicationContext`）。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果您禁用`/actuator/restart`端点，则`/actuator/pause`和`/actuator/resume`端点也将被禁用，因为它们只是的特殊情况`/actuator/restart`。 |