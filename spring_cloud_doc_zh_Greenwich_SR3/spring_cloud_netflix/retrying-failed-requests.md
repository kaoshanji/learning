# 20. Retrying Failed Requests

## 20.重试失败的请求

Spring Cloud Netflix提供了多种发出HTTP请求的方式。您可以使用负载平衡`RestTemplate`，功能区或伪装。无论您选择如何创建HTTP请求，始终都有可能导致请求失败。当请求失败时，您可能希望自动重试该请求。为此，在使用Sping Cloud Netflix时，您需要在应用程序的类路径中包括[Spring Retry](https://github.com/spring-projects/spring-retry)。如果存在Spring Retry，则load-balanced `RestTemplates`，Feign和Zuul会自动重试所有失败的请求（假设您的配置允许这样做）。

## 20.1退避政策

默认情况下，重试请求时不使用任何退避策略。如果要配置退避策略，则需要创建一个类型为Bean `LoadBalancedRetryFactory`并`createBackOffPolicy`为给定服务覆盖方法，如以下示例所示：

```java
@Configuration
public class MyConfiguration {
    @Bean
    LoadBalancedRetryFactory retryFactory() {
        return new LoadBalancedRetryFactory() {
            @Override
            public BackOffPolicy createBackOffPolicy(String service) {
                return new ExponentialBackOffPolicy();
            }
        };
    }
}
```

## 20.2配置

当将Ribbon与Spring Retry一起使用时，可以通过配置某些Ribbon属性来控制重试功能。要做到这一点，设置`client.ribbon.MaxAutoRetries`，`client.ribbon.MaxAutoRetriesNextServer`和`client.ribbon.OkToRetryOnAllOperations`性能。有关这些属性的说明，请参见[功能区文档](https://github.com/Netflix/ribbon/wiki/Getting-Started#the-properties-file-sample-clientproperties)。

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| 启用`client.ribbon.OkToRetryOnAllOperations`包括重试POST请求，由于请求正文的缓冲，这可能会对服务器资源产生影响。 |

此外，您可能想在响应中返回某些状态代码时重试请求。您可以通过设置`clientName.ribbon.retryableStatusCodes`属性列出希望功能区客户端重试的响应代码，如以下示例所示：

```properties
clientName:
  ribbon:
    retryableStatusCodes: 404,502
```

您还可以创建一个类型为bean的bean，`LoadBalancedRetryPolicy`并实现该`retryableStatusCode`方法以根据状态码重试请求。

### 20.2.1 Zuul

您可以通过设置`zuul.retryable`为来关闭Zuul的重试功能`false`。您还可以通过设置`zuul.routes.routename.retryable`为来逐个路由禁用重试功能`false`。