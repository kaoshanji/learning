# 10. Spring Cloud Config Client

## 10. Spring Cloud Config客户端

Spring Boot应用程序可以立即利用Spring Config Server（或应用程序开发人员提供的其他外部属性源）。它还选择了一些与`Environment`变更事件相关的其他有用功能。

## 10.1配置第一个引导程序

在类路径上具有Spring Cloud Config Client的任何应用程序的默认行为如下：当配置客户端启动时，它将绑定到Config Server（通过`spring.cloud.config.uri`bootstrap配置属性），并`Environment`使用远程属性源初始化Spring 。

此行为的最终结果是，所有要使用Config Server的客户端应用程序都需要一个`bootstrap.yml`（或环境变量），其服务器地址设置为该地址`spring.cloud.config.uri`（默认为“ http：// localhost：8888”）。

## 10.2发现第一引导程序

如果您使用`DiscoveryClient`诸如Spring Cloud Netflix和Eureka Service Discovery或Spring Cloud Consul之类的实现，则可以让Config Server向Discovery Service注册。但是，在默认的“ Config First ”模式下，客户端无法利用注册。

如果您更喜欢使用`DiscoveryClient`来定位Config Server，则可以通过设置`spring.cloud.config.discovery.enabled=true`（默认值为`false`）来进行定位。这样做的最终结果是，所有客户端应用程序都需要`bootstrap.yml`具有适当发现配置的（或环境变量）。例如，对于Spring Cloud Netflix，您需要定义Eureka服务器地址（例如，中的`eureka.client.serviceUrl.defaultZone`）。使用此选项的价格是启动时需要进行额外的网络往返，以查找服务注册。好处是，只要发现服务是固定点，配置服务器就可以更改其坐标。默认服务ID是`configserver`，但是您可以通过设置`spring.cloud.config.discovery.serviceId`（和在服务器上，以一种通常的服务方式，例如通过设置`spring.application.name`）在客户端上更改该ID 。

发现客户端实现均支持某种元数据映射（例如，`eureka.instance.metadataMap`对于Eureka ，我们拥有）。Config Server的某些其他属性可能需要在其服务注册元数据中进行配置，以便客户端可以正确连接。如果Config Server受HTTP Basic保护，则可以将凭据配置为`user`和`password`。另外，如果Config Server具有上下文路径，则可以设置`configPath`。例如，以下YAML文件适用于作为Eureka客户端的Config Server：

**bootstrap.yml。** 

```
eureka:
  instance:
    ...
    metadataMap:
      user: osufhalskjrtl
      password: lviuhlszvaorhvlo5847
      configPath: /config
```



## 10.3配置客户端快速失败

在某些情况下，如果服务无法连接到Config Server，您可能希望启动失败。如果这是所需的行为，请设置引导程序配置属性，`spring.cloud.config.fail-fast=true`以使客户端因Exception而暂停。

## 10.4配置客户端重试

如果您希望配置服务器在应用程序启动时偶尔不可用，则可以使其在失败后继续尝试。首先，您需要设置`spring.cloud.config.fail-fast=true`。然后，您需要添加`spring-retry`和`spring-boot-starter-aop`到您的类路径。默认行为是重试六次，初始回退间隔为1000ms，随后的回退的指数乘数为1.1。您可以通过设置`spring.cloud.config.retry.*`配置属性来配置这些属性（和其他属性）。

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 要完全控制重试行为，请添加ID为`@Bean`的type类型。Spring Retry有一个支持创建一个。`RetryOperationsInterceptor``configServerRetryInterceptor``RetryInterceptorBuilder` |

## 10.5查找远程配置资源

Config Service提供来自的属性源`/{name}/{profile}/{label}`，其中客户端应用程序中的默认绑定如下：

- “名称” = `${spring.application.name}`
- “个人资料” = `${spring.profiles.active}`（实际上`Environment.getActiveProfiles()`）
- “ label” =“主人”

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 设置属性时，`${spring.application.name}`请勿在您的应用名称前加上保留字，`application-`以防止解析正确的属性源时出现问题。 |

您可以通过设置覆盖所有的人`spring.cloud.config.*`（这里`*`是`name`，`profile`或`label`）。该`label`是回滚到配置的先前版本很有用。使用默认的Config Server实现，它可以是git标签，分支名称或提交ID。标签也可以逗号分隔的列表形式提供。在这种情况下，列表中的项目将一一尝试直到成功为止。在要素分支上工作时，此行为可能很有用。例如，您可能希望使config标签与分支对齐，但使其成为可选（在这种情况下，请使用`spring.cloud.config.label=myfeature,develop`）。

## 10.6为配置服务器指定多个地址

为确保在部署了Config Server的多个实例并希望不时有一个或多个实例不可用时的高可用性，可以指定多个URL（作为逗号分隔的列表，位于`spring.cloud.config.uri`属性），或让您的所有实例在服务注册表中注册，例如Eureka（如果使用Discovery-First Bootstrap模式）。请注意，只有在未运行Config Server时（即，应用程序退出时）或发生连接超时时，这样做才能确保高可用性。例如，如果Config Server返回500（内部服务器错误）响应，或者Config Client从Config Server收到401（由于凭据错误或其他原因），则Config Client不会尝试从其他URL获取属性。此类错误表示用户问题，而不是可用性问题。

如果在Config Server上使用HTTP基本安全性，则仅当将凭据嵌入在`spring.cloud.config.uri`属性下指定的每个URL中时，当前才有可能支持per-Config Server身份验证凭据。如果使用任何其他类型的安全性机制，则您（当前）不能支持每个Config Server的身份验证和授权。

## 10.7配置超时

如果要配置超时阈值：

- 可以使用属性配置读取超时`spring.cloud.config.request-read-timeout`。
- 可以使用属性配置连接超时`spring.cloud.config.request-connect-timeout`。

## 10.8安全性

如果在服务器上使用HTTP基本安全性，则客户端需要知道密码（如果不是默认用户名，则需要用户名）。您可以通过配置服务器URI或通过单独的用户名和密码属性来指定用户名和密码，如以下示例所示：

**bootstrap.yml。** 

```properties
spring:
  cloud:
    config:
     uri: https://user:secret@myconfig.mycompany.com
```



以下示例显示了传递相同信息的另一种方法：

**bootstrap.yml。** 

```properties
spring:
  cloud:
    config:
     uri: https://myconfig.mycompany.com
     username: user
     password: secret
```



在`spring.cloud.config.password`与`spring.cloud.config.username`那就是在URI提供的值覆盖任何东西。

如果您在Cloud Foundry上部署应用程序，则提供密码的最佳方法是通过服务凭据（例如URI，因为它不需要在配置文件中）。以下示例在本地运行，并且适用于Cloud Foundry上名为的用户提供的服务`configserver`：

**bootstrap.yml。** 

```properties
spring:
  cloud:
    config:
     uri: ${vcap.services.configserver.credentials.uri:http://user:password@localhost:8888}
```



如果您使用安全的另一种形式，你可能需要[提供`RestTemplate`](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_config_client.html#custom-rest-template)的`ConfigServicePropertySourceLocator`（例如，通过在引导方面抓住它，并注入它）。

### 10.8.1健康指标

Config Client提供一个Spring Boot Health Indicator，该指示器尝试从Config Server加载配置。可以通过设置禁用健康指示器`health.config.enabled=false`。由于性能原因，响应也被缓存。默认的生存时间为5分钟。要更改该值，请设置`health.config.time-to-live`属性（以毫秒为单位）。

### 10.8.2提供自定义的RestTemplate

在某些情况下，您可能需要自定义来自客户端对配置服务器的请求。通常，这样做涉及传递特殊的`Authorization`标头以验证对服务器的请求。提供一个习惯`RestTemplate`：

1. 创建一个具有的实现的新配置Bean `PropertySourceLocator`，如以下示例所示：

**CustomConfigServiceBootstrapConfiguration.java。** 

```java
@Configuration
public class CustomConfigServiceBootstrapConfiguration {
    @Bean
    public ConfigServicePropertySourceLocator configServicePropertySourceLocator() {
        ConfigClientProperties clientProperties = configClientProperties();
       ConfigServicePropertySourceLocator configServicePropertySourceLocator =  new ConfigServicePropertySourceLocator(clientProperties);
        configServicePropertySourceLocator.setRestTemplate(customRestTemplate(clientProperties));
        return configServicePropertySourceLocator;
    }
}
```



1. 在中`resources/META-INF`，创建一个名为的文件 `spring.factories`并指定您的自定义配置，如以下示例所示：

**弹簧工厂。** 

```properties
org.springframework.cloud.bootstrap.BootstrapConfiguration = com.my.config.client.CustomConfigServiceBootstrapConfiguration
```



### 10.8.3库

使用保管库作为配置服务器的后端时，客户端需要为服务器提供令牌以从保管库检索值。该令牌可以在客户端内设置被提供`spring.cloud.config.token` 在`bootstrap.yml`，如显示在下面的例子：

**bootstrap.yml。** 

```properties
spring:
  cloud:
    config:
      token: YourVaultToken
```



## 10.9保险柜中的嵌套键

保险柜支持将键嵌套在保险柜中存储的值中的功能，如以下示例所示：

```bash
echo -n '{"appA": {"secret": "appAsecret"}, "bar": "baz"}' | vault write secret/myapp -
```

此命令将JSON对象写入您的保险柜。要在Spring中访问这些值，您将使用传统的dot（`.`）批注，如以下示例所示

```java
@Value("${appA.secret}")
String name = "World";
```

前面的代码会将`name`变量的值设置为`appAsecret`。