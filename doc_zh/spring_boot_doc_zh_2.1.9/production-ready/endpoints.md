# 53、端点

执行器端点使您可以监视应用程序并与之交互。Spring Boot包含许多内置端点，您可以添加自己的端点。例如，`health`端点提供基本的应用程序运行状况信息。

每个端点都可以[启用或禁用](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/production-ready-endpoints.html#production-ready-endpoints-enabling-endpoints)。这控制了是否创建了端点以及它的bean在应用程序上下文中是否存在。为了可以远程访问，端点还必须[通过JMX或HTTP公开](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/production-ready-endpoints.html#production-ready-endpoints-exposing-endpoints)。大多数应用程序选择HTTP，其中终结点的ID和前缀`/actuator`映射到URL。例如，默认情况下，`health`端点映射到`/actuator/health`。

可以使用以下与技术无关的端点：

| ID                 | 描述                                                         | 默认启用 |
| ------------------ | ------------------------------------------------------------ | -------- |
| `auditevents`      | 公开当前应用程序的审核事件信息。                             | 是       |
| `beans`            | 显示应用程序中所有Spring Bean的完整列表。                    | 是       |
| `caches`           | 公开可用的缓存。                                             | 是       |
| `conditions`       | 显示在配置和自动配置类上评估的条件以及它们匹配或不匹配的原因。 | 是       |
| `configprops`      | 显示所有的整理列表`@ConfigurationProperties`。               | 是       |
| `env`              | 公开Spring的属性`ConfigurableEnvironment`。                  | 是       |
| `flyway`           | 显示已应用的所有Flyway数据库迁移。                           | 是       |
| `health`           | 显示应用程序运行状况信息。                                   | 是       |
| `httptrace`        | 显示HTTP跟踪信息（默认情况下，最近100个HTTP请求-响应交换）。 | 是       |
| `info`             | 显示任意应用程序信息。                                       | 是       |
| `integrationgraph` | 显示Spring Integration图。                                   | 是       |
| `loggers`          | 显示和修改应用程序中记录器的配置。                           | 是       |
| `liquibase`        | 显示已应用的所有Liquibase数据库迁移。                        | 是       |
| `metrics`          | 显示当前应用程序的“指标”信息。                               | 是       |
| `mappings`         | 显示所有`@RequestMapping`路径的整理列表。                    | 是       |
| `scheduledtasks`   | 显示应用程序中的计划任务。                                   | 是       |
| `sessions`         | 允许从Spring Session支持的会话存储中检索和删除用户会话。使用Spring Session对反应式Web应用程序的支持时不可用。 | 是       |
| `shutdown`         | 使应用程序正常关闭。                                         | 没有     |
| `threaddump`       | 执行线程转储。                                               | 是       |

如果您的应用程序是Web应用程序（Spring MVC，Spring WebFlux或Jersey），则可以使用以下附加端点：

| ID           | 描述                                                         | 默认启用 |
| ------------ | ------------------------------------------------------------ | -------- |
| `heapdump`   | 返回`hprof`堆转储文件。                                      | 是       |
| `jolokia`    | 通过HTTP公开JMX bean（当Jolokia在类路径上时，不适用于WebFlux）。 | 是       |
| `logfile`    | 返回日志文件的内容（如果已设置`logging.file`或`logging.path`属性）。支持使用HTTP `Range`标头来检索部分日志文件的内容。 | 是       |
| `prometheus` | 以Prometheus服务器可以抓取的格式公开指标。                   | 是       |

要了解有关执行器端点及其请求和响应格式的更多信息，请参阅单独的API文档（[HTML](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/actuator-api//html)或[PDF](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/actuator-api//pdf/spring-boot-actuator-web-api.pdf)）。

## 53.1启用端点

默认情况下，所有端点`shutdown`均处于启用状态。要配置端点的启用，请使用其`management.endpoint.<id>.enabled`属性。以下示例启用`shutdown`端点：

```bash
management.endpoint.shutdown.enabled = true
```

如果您希望启用端点启用而不是退出启用，请将该`management.endpoints.enabled-by-default`属性设置为，`false`并使用各个端点`enabled`属性重新启用。以下示例启用该`info`端点并禁用所有其他端点：

```bash
management.endpoints.enabled-by-default=false
management.endpoint.info.enabled=true
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 禁用的端点将从应用程序上下文中完全删除。如果只想更改公开端点的技术，请使用[`include`和`exclude`属性](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/production-ready-endpoints.html#production-ready-endpoints-exposing-endpoints)。 |

## 53.2暴露端点

由于端点可能包含敏感信息，因此应谨慎考虑何时公开它们。下表显示了内置端点的默认暴露：

| ID                 | JMX    | 网页 |
| ------------------ | ------ | ---- |
| `auditevents`      | 是     | 没有 |
| `beans`            | 是     | 没有 |
| `caches`           | 是     | 没有 |
| `conditions`       | 是     | 没有 |
| `configprops`      | 是     | 没有 |
| `env`              | 是     | 没有 |
| `flyway`           | 是     | 没有 |
| `health`           | 是     | 是   |
| `heapdump`         | 不适用 | 没有 |
| `httptrace`        | 是     | 没有 |
| `info`             | 是     | 是   |
| `integrationgraph` | 是     | 没有 |
| `jolokia`          | 不适用 | 没有 |
| `logfile`          | 不适用 | 没有 |
| `loggers`          | 是     | 没有 |
| `liquibase`        | 是     | 没有 |
| `metrics`          | 是     | 没有 |
| `mappings`         | 是     | 没有 |
| `prometheus`       | 不适用 | 没有 |
| `scheduledtasks`   | 是     | 没有 |
| `sessions`         | 是     | 没有 |
| `shutdown`         | 是     | 没有 |
| `threaddump`       | 是     | 没有 |

要更改端点暴露，使用下面的特定技术`include`和`exclude`特性：

| 属性                                        | 默认           |
| ------------------------------------------- | -------------- |
| `management.endpoints.jmx.exposure.exclude` |                |
| `management.endpoints.jmx.exposure.include` | `*`            |
| `management.endpoints.web.exposure.exclude` |                |
| `management.endpoints.web.exposure.include` | `info, health` |

该`include`属性列出了公开的端点的ID。该`exclude`属性列出了不应公开的端点的ID。该`exclude`属性优先于该`include`属性。无论`include`和`exclude`性能可与端点ID列表进行配置。

例如，要停止通过JMX公开所有端点，而仅公开`health`和`info`端点，请使用以下属性：

```bash
management.endpoints.jmx.exposure.include=health,info
```

`*`可用于选择所有端点。例如，要通过HTTP公开除`env`和`beans`端点之外的所有内容，请使用以下属性：

```bash
management.endpoints.web.exposure.include=*
management.endpoints.web.exposure.exclude=env,beans
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| `*` 在YAML中有特殊含义，因此，如果要包括（或排除）所有端点，请确保添加引号，如以下示例所示： |

```bash
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果您的应用程序公开公开，我们强烈建议您也[保护端点](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/production-ready-endpoints.html#production-ready-endpoints-security)。 |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如果要实现暴露端点的自己的策略，则可以注册`EndpointFilter`Bean。 |

## 53.3保护HTTP端点

您应该像对待其他任何敏感URL一样，小心保护HTTP端点的安全。如果存在Spring Security，则默认情况下使用Spring Security的内容协商策略来保护端点安全。例如，如果您希望为HTTP端点配置自定义安全性，只允许具有特定角色的用户访问它们，Spring Boot提供了一些方便的`RequestMatcher`对象，可以将它们与Spring Security结合使用。

典型的Spring Security配置可能类似于以下示例：

```java
@Configuration
public class ActuatorSecurity extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests()
				.anyRequest().hasRole("ENDPOINT_ADMIN")
				.and()
			.httpBasic();
	}

}
```

前面的示例用于`EndpointRequest.toAnyEndpoint()`将请求匹配到任何端点，然后确保所有`ENDPOINT_ADMIN`角色都具有该角色。上还有其他几种匹配器方法`EndpointRequest`。有关详细信息，请参见API文档（[HTML](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/actuator-api//html)或[PDF](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/actuator-api//pdf/spring-boot-actuator-web-api.pdf)）。

如果将应用程序部署在防火墙后面，则可能希望无需进行身份验证即可访问所有执行器端点。您可以通过更改`management.endpoints.web.exposure.include`属性来做到这一点，如下所示：

**application.properties。** 

```bash
management.endpoints.web.exposure.include=*
```



此外，如果存在Spring Security，则需要添加自定义安全配置，该配置允许未经身份验证的端点访问，如以下示例所示：

```java
@Configuration
public class ActuatorSecurity extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests()
			.anyRequest().permitAll();
	}

}
```

## 53.4配置端点

端点自动缓存对不带任何参数的读取操作的响应。要配置端点缓存响应的时间，请使用其`cache.time-to-live`属性。以下示例将`beans`端点的缓存的生存时间设置为10秒：

**application.properties。** 

```bash
management.endpoint.beans.cache.time-to-live=10s
```



| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 该前缀`management.endpoint.<name>`用于唯一标识正在配置的端点。 |

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 发出经过身份验证的HTTP请求时，`Principal`会将视为端点的输入，因此将不缓存响应。 |

## 53.5用于执行器Web端点的超媒体

添加了“发现页面”，其中包含指向所有端点的链接。`/actuator`默认情况下，“发现页面”可用。

配置了自定义管理上下文路径后，“发现页面”将自动从`/actuator`管理上下文的根目录移到。例如，如果管理上下文路径为`/management`，则发现页面可从访问`/management`。将管理上下文路径设置为时`/`，将禁用发现页面，以防止与其他映射发生冲突的可能性。

## 53.6 CORS支持

[跨域资源共享](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing)（CORS）是[W3C规范](https://www.w3.org/TR/cors/)，使您可以灵活地指定授权哪种类型的跨域请求。如果使用Spring MVC或Spring WebFlux，则可以将Actuator的Web端点配置为支持此类方案。

默认情况下，CORS支持是禁用的，并且仅`management.endpoints.web.cors.allowed-origins`在设置属性后才启用。以下配置允许`GET`和`POST`从`example.com`域调用：

```bash
management.endpoints.web.cors.allowed-origins=https://example.com
management.endpoints.web.cors.allowed-methods=GET,POST
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 有关选项的完整列表，请参见[CorsEndpointProperties](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/endpoint/web/CorsEndpointProperties.java)。 |

## 53.7实施自定义端点

如果添加带有的`@Bean`注释`@Endpoint`，则带`@ReadOperation`，`@WriteOperation`或注释的任何方法`@DeleteOperation`都将通过JMX以及Web应用程序中的HTTP自动公开。可以使用Jersey，Spring MVC或Spring WebFlux通过HTTP公开端点。

您也可以使用`@JmxEndpoint`或编写技术特定的端点`@WebEndpoint`。这些端点仅限于各自的技术。例如，`@WebEndpoint`仅通过HTTP而不是JMX公开。

您可以使用`@EndpointWebExtension`和编写特定于技术的扩展`@EndpointJmxExtension`。这些注释使您可以提供特定于技术的操作来扩展现有端点。

最后，如果需要访问特定于Web框架的功能，则可以实现Servlet或Spring `@Controller`和`@RestController`终结点，但要付出代价，即它们无法通过JMX或使用其他Web框架使用。

### 53.7.1接收输入

端点上的操作通过其参数接收输入。通过网络公开时，这些参数的值取自URL的查询参数和JSON请求正文。通过JMX公开时，参数将映射到MBean操作的参数。默认情况下，参数是必需的。可以通过使用注释使它们成为可选的`@org.springframework.lang.Nullable`。

JSON请求正文中的每个根属性都可以映射到端点的参数。考虑以下JSON请求正文：

```json
{
	"name": "test",
	"counter": 42
}
```

这可用于调用带有`String name`和`int counter`参数的写操作。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 由于端点与技术无关，因此只能在方法签名中指定简单类型。特别是，不支持使用定义了`name`和`counter`属性的自定义类型声明单个参数。 |

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 为了将输入映射到操作方法的参数，应使用编译实现端点的Java代码`-parameters`，并用编译实现端点的Kotlin代码`-java-parameters`。如果您使用的是Spring Boot的Gradle插件，或者您使用的是Maven和，则此操作将自动发生`spring-boot-starter-parent`。 |

#### 输入类型转换

如有必要，传递给端点操作方法的参数会自动转换为所需的类型。在调用操作方法之前，使用的实例将通过JMX或HTTP请求接收的输入转换为所需的类型`ApplicationConversionService`。

### 53.7.2自定义Web端点

上的操作`@Endpoint`，`@WebEndpoint`或者`@EndpointWebExtension`使用新泽西州，Spring MVC的，或Spring WebFlux自动曝光通过HTTP。

#### Web端点请求谓词

对于在暴露于Web的端点上的每个操作，都会自动生成一个请求谓词。

#### 路径

谓词的路径由终结点的ID和暴露于Web的终结点的基本路径确定。默认基本路径为`/actuator`。例如，具有ID的端点`sessions`将`/actuator/sessions`用作谓词中的路径。

通过使用注释操作方法的一个或多个参数，可以进一步自定义路径`@Selector`。这样的参数作为路径变量添加到路径谓词。当端点操作被调用时，变量的值被传递到操作方法中。

#### HTTP方法

谓词的HTTP方法由操作类型决定，如下表所示：

| 运作方式           | HTTP方法 |
| ------------------ | -------- |
| `@ReadOperation`   | `GET`    |
| `@WriteOperation`  | `POST`   |
| `@DeleteOperation` | `DELETE` |

#### 消耗

对于使用请求正文的`@WriteOperation`（HTTP `POST`），谓词的消耗子句为`application/vnd.spring-boot.actuator.v2+json, application/json`。对于所有其他操作，消耗子句为空。

#### 产生

的产生谓词子句可以由被确定`produces`的属性`@DeleteOperation`，`@ReadOperation`和`@WriteOperation`注解。该属性是可选的。如果未使用，则会自动确定produces子句。

如果操作方法返回`void`或`Void`生产子句为空。如果操作方法返回a `org.springframework.core.io.Resource`，则Produces子句为`application/octet-stream`。对于所有其他操作，produces子句为`application/vnd.spring-boot.actuator.v2+json, application/json`。

#### Web端点响应状态

端点操作的默认响应状态取决于操作类型（读，写或删除）以及该操作返回的内容（如果有）。

A `@ReadOperation`返回一个值，响应状态将为200（确定）。如果未返回值，则响应状态将为404（未找到）。

如果a `@WriteOperation`或`@DeleteOperation`返回值，则响应状态将为200（确定）。如果未返回值，则响应状态将为204（无内容）。

如果在没有必需参数或无法将参数转换为必需类型的参数的情况下调用操作，则不会调用该操作方法，并且响应状态将为400（错误请求）。

#### Web端点范围请求

HTTP范围请求可用于请求HTTP资源的一部分。使用Spring MVC或Spring Web Flux时，返回`org.springframework.core.io.Resource`自动支持范围请求的操作。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 使用Jersey时，范围请求不受支持。                             |

#### Web端点安全

Web终结点或特定于Web的终结点扩展上的操作可以接收当前参数`java.security.Principal`或`org.springframework.boot.actuate.endpoint.SecurityContext`方法参数。前者通常与结合使用`@Nullable`，以为经过身份验证和未经身份验证的用户提供不同的行为。后者通常用于使用其`isUserInRole(String)`方法执行授权检查。

### 53.7.3 Servlet端点

`Servlet`可以通过实现一个`@ServletEndpoint`也带有的类来将A 公开为端点`Supplier<EndpointServlet>`。Servlet端点提供与Servlet容器的更深层集成，但以可移植性为代价。它们旨在用于将现有对象公开`Servlet`为端点。对于新端点，应尽可能使用`@Endpoint`和`@WebEndpoint`注释。

### 53.7.4控制器端点

`@ControllerEndpoint`并且`@RestControllerEndpoint`可以用于实现仅由Spring MVC或Spring WebFlux公开的端点。使用Spring MVC和Spring WebFlux的标准注释（例如`@RequestMapping`和）映射方法`@GetMapping`，并将端点的ID用作路径的前缀。控制器端点提供了与Spring Web框架的更深层集成，但以可移植性为代价。的`@Endpoint`和`@WebEndpoint`注解应当优选只要有可能。

## 53.8健康信息

您可以使用运行状况信息来检查正在运行的应用程序的状态。监视软件通常使用它在生产系统出现故障时向某人发出警报。`health`端点公开的信息取决于`management.endpoint.health.show-details`可以使用以下值之一配置的属性：

| 名称              | 描述                                                         |
| ----------------- | ------------------------------------------------------------ |
| `never`           | 详细信息永远不会显示。                                       |
| `when-authorized` | 详细信息仅显示给授权用户。可以使用来配置授权角色`management.endpoint.health.roles`。 |
| `always`          | 向所有用户显示详细信息。                                     |

默认值为`never`。当用户担任一个或多个端点的角色时，该用户被视为已授权。如果端点没有配置的角色（默认值），则所有通过身份验证的用户均被视为已授权。可以使用`management.endpoint.health.roles`属性配置角色。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果您已保护应用程序安全并希望使用`always`，则安全配置必须允许经过身份验证的用户和未经身份验证的用户都可以访问运行状况端点。 |

运行状况信息是从的内容中收集的[`HealthIndicatorRegistry`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/HealthIndicatorRegistry.java)（默认情况下，[`HealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/HealthIndicator.java).boot中定义的所有实例都`ApplicationContext`包含自动配置的内容`HealthIndicators`，您也可以编写自己的实例。默认情况下，最终系统状态由导出，系统对状态`HealthAggregator`进行排序`HealthIndicator`根据状态的有序列表从每个列表中进行排序，将已排序列表中的第一个状态用作整体运行状况。如果否，则`HealthIndicator`返回已知`HealthAggregator`的`UNKNOWN`状态。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 该`HealthIndicatorRegistry`可用于注册和在运行时注销卫生指标。 |

### 53.8.1自动配置的健康指标

`HealthIndicators`适当时，Spring Boot会自动配置以下内容：

| 名称                                                         | 描述                               |
| ------------------------------------------------------------ | ---------------------------------- |
| [`CassandraHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/cassandra/CassandraHealthIndicator.java) | 检查Cassandra数据库是否已启动。    |
| [`CouchbaseHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/couchbase/CouchbaseHealthIndicator.java) | 检查Couchbase群集是否已启动。      |
| [`DiskSpaceHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/system/DiskSpaceHealthIndicator.java) | 检查磁盘空间不足。                 |
| [`DataSourceHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/jdbc/DataSourceHealthIndicator.java) | 检查是否可以建立连接`DataSource`。 |
| [`ElasticsearchHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/elasticsearch/ElasticsearchHealthIndicator.java) | 检查Elasticsearch集群是否已启动。  |
| [`InfluxDbHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/influx/InfluxDbHealthIndicator.java) | 检查InfluxDB服务器是否已启动。     |
| [`JmsHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/jms/JmsHealthIndicator.java) | 检查JMS代理是否启动。              |
| [`MailHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/mail/MailHealthIndicator.java) | 检查邮件服务器是否已启动。         |
| [`MongoHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/mongo/MongoHealthIndicator.java) | 检查Mongo数据库是否已启动。        |
| [`Neo4jHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/neo4j/Neo4jHealthIndicator.java) | 检查Neo4j服务器是否已启动。        |
| [`RabbitHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/amqp/RabbitHealthIndicator.java) | 检查Rabbit服务器是否已启动。       |
| [`RedisHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/redis/RedisHealthIndicator.java) | 检查Redis服务器是否启动。          |
| [`SolrHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/solr/SolrHealthIndicator.java) | 检查Solr服务器是否已启动。         |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 您可以通过设置`management.health.defaults.enabled`属性来全部禁用它们。 |

### 53.8.2编写自定义健康指标

为了提供定制的健康信息，您可以注册实现该[`HealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/HealthIndicator.java)接口的Spring bean 。您需要提供该`health()`方法的实现并返回`Health`响应。的`Health`响应应该包括一个状态，并且可以任选地包括另外的细节被显示。以下代码显示了示例`HealthIndicator`实现：

```java
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MyHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		int errorCode = check(); // perform some specific health check
		if (errorCode != 0) {
			return Health.down().withDetail("Error Code", errorCode).build();
		}
		return Health.up().build();
	}

}
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 给定的标识符`HealthIndicator`是不带`HealthIndicator`后缀的Bean的名称（如果存在）。在前面的示例中，健康信息在名为的条目中可用`my`。 |

除了Spring Boot的预定义[`Status`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/Status.java)类型之外，还可以`Health`返回`Status`代表新系统状态的自定义。在这种情况下，[`HealthAggregator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/HealthAggregator.java)还需要提供接口的自定义实现，或者必须使用`management.health.status.order`配置属性来配置默认实现。

例如，假设在您的一种实现中使用了`Status`带有代码的new 。要配置严重性顺序，请将以下属性添加到您的应用程序属性中：`FATAL``HealthIndicator`

```
management.health.status.order = FATAL，DOWN，OUT_OF_SERVICE，UNKNOWN，UP
```

在响应中的HTTP状态代码反映总体健康状况（例如，`UP`映射到200，而`OUT_OF_SERVICE`并`DOWN`映射到503）。如果通过HTTP访问运行状况终结点，则可能还需要注册自定义状态映射。例如，以下属性映射`FATAL`到503（服务不可用）：

```
management.health.status.http-mapping.FATAL = 503
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如果需要更多控制，则可以定义自己的`HealthStatusHttpMapper`bean。 |

下表显示了内置状态的默认状态映射：

| 状态     | 制图                                  |
| -------- | ------------------------------------- |
| 下       | SERVICE_UNAVAILABLE（503）            |
| 中止服务 | SERVICE_UNAVAILABLE（503）            |
| 上       | 默认情况下没有映射，因此http状态为200 |
| 未知     | 默认情况下没有映射，因此http状态为200 |

### 53.8.3反应健康指标

对于反应式应用程序，例如使用Spring WebFlux的那些应用程序，`ReactiveHealthIndicator`提供了无阻塞合同来获取应用程序的运行状况。与传统方法类似`HealthIndicator`，健康信息是从[`ReactiveHealthIndicatorRegistry`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/ReactiveHealthIndicatorRegistry.java)（默认情况下，您的所有[`HealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/HealthIndicator.java)和[`ReactiveHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/ReactiveHealthIndicator.java)实例中定义的实例的）内容收集的`ApplicationContext`。`HealthIndicator`不检查反应式API的常规对象是在弹性调度程序上执行的。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 在反应式应用程序中，The `ReactiveHealthIndicatorRegistry`可用于在运行时注册和注销健康指标。 |

为了从反应式API提供自定义健康信息，您可以注册实现该[`ReactiveHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/ReactiveHealthIndicator.java)接口的Spring bean 。以下代码显示了示例`ReactiveHealthIndicator`实现：

```java
@Component
public class MyReactiveHealthIndicator implements ReactiveHealthIndicator {

	@Override
	public Mono<Health> health() {
		return doHealthCheck() //perform some specific health check that returns a Mono<Health>
			.onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()));
	}

}
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 要自动处理错误，请考虑从扩展`AbstractReactiveHealthIndicator`。 |

### 53.8.4自动配置的ReactiveHealthIndicators

`ReactiveHealthIndicators`适当时，Spring Boot会自动配置以下内容：

| 名称                                                         | 描述                            |
| ------------------------------------------------------------ | ------------------------------- |
| [`CassandraReactiveHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/cassandra/CassandraReactiveHealthIndicator.java) | 检查Cassandra数据库是否已启动。 |
| [`CouchbaseReactiveHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/couchbase/CouchbaseReactiveHealthIndicator.java) | 检查Couchbase群集是否已启动。   |
| [`MongoReactiveHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/mongo/MongoReactiveHealthIndicator.java) | 检查Mongo数据库是否已启动。     |
| [`RedisReactiveHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/redis/RedisReactiveHealthIndicator.java) | 检查Redis服务器是否启动。       |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如有必要，可用无功指示器代替常规指示器。此外，任何`HealthIndicator`未明确处理的内容都会自动包装。 |

## 53.9申请信息

应用程序信息公开了从中[`InfoContributor`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/InfoContributor.java)定义的所有bean 收集的各种信息`ApplicationContext`。Spring Boot包含许多自动配置的`InfoContributor`bean，您可以编写自己的bean。

### 53.9.1自动配置的InfoContributor

`InfoContributor`适当时，Spring Boot会自动配置以下bean：

| 名称                                                         | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [`EnvironmentInfoContributor`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/EnvironmentInfoContributor.java) | 从键`Environment`下方公开任何`info`键。                      |
| [`GitInfoContributor`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/GitInfoContributor.java) | 如果`git.properties`文件可用，则公开git信息。                |
| [`BuildInfoContributor`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/BuildInfoContributor.java) | 如果`META-INF/build-info.properties`文件可用，则公开构建信息。 |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 通过设置该`management.info.defaults.enabled`属性，可以全部禁用它们。 |

### 53.9.2定制应用程序信息

您可以`info`通过设置`info.*`Spring属性来自定义端点公开的数据。键`Environment`下的所有属性`info`都将自动显示。例如，您可以将以下设置添加到`application.properties`文件中：

```bash
info.app.encoding=UTF-8
info.app.java.source=1.8
info.app.java.target=1.8
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 除了对这些值进行硬编码之外，您还可以[在构建时扩展info属性](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-properties-and-configuration.html#howto-automatic-expansion)。假设您使用Maven，则可以按如下所示重写前面的示例：`info.app.encoding =@project.build.sourceEncoding @  info.app.java.source =@java.version @  info.app.java.target =@java.version @` |

### 53.9.3 Git提交信息

`info`端点的另一个有用的功能是它能够`git`在构建项目时发布有关源代码存储库状态的信息。如果`GitProperties`豆可用，`git.branch`，`git.commit.id`，和`git.commit.time`属性暴露出来。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 一个`GitProperties`bean是自动配置，如果一个`git.properties`文件可在classpath的根目录。有关更多详细[信息，](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-build.html#howto-git-info)请参见“ [生成git信息](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-build.html#howto-git-info) ”。 |

如果要显示完整的git信息（即的完整内容`git.properties`），请使用`management.info.git.mode`属性，如下所示：

```bash
management.info.git.mode=full
```

### 53.9.4版本信息

如果有`BuildProperties`可用的bean，则`info`端点也可以发布有关构建的信息。如果`META-INF/build-info.properties`文件在类路径中可用，则会发生这种情况。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| Maven和Gradle插件都可以生成该文件。有关更多详细[信息，](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-build.html#howto-build-info)请参见“ [生成构建信息](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-build.html#howto-build-info) ”。 |

### 53.9.5编写自定义信息提供者

为了提供定制的应用程序信息，您可以注册实现该[`InfoContributor`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/InfoContributor.java)接口的Spring bean 。

以下示例`example`使用单个值贡献一个条目：

```java
import java.util.Collections;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class ExampleInfoContributor implements InfoContributor {

	@Override
	public void contribute(Info.Builder builder) {
		builder.withDetail("example",
				Collections.singletonMap("key", "value"));
	}

}
```

如果到达`info`端点，则应该看到包含以下附加条目的响应：

```json
{
	"example": {
		"key" : "value"
	}
}
```