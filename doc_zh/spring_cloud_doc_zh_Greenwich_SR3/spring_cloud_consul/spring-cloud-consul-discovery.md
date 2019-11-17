# 68. Service Discovery with Consul

## 68.使用领事进行服务发现

服务发现是基于微服务的体系结构的关键原则之一。尝试手动配置每个客户端或某种形式的约定可能非常困难并且非常脆弱。领事通过[HTTP API](https://www.consul.io/docs/agent/http.html)和[DNS](https://www.consul.io/docs/agent/dns.html)提供服务发现服务。Spring Cloud Consul利用HTTP API进行服务注册和发现。这不会阻止非Spring Cloud应用程序利用DNS接口。Consul Agents服务器在[群集中](https://www.consul.io/docs/internals/architecture.html)运行，该[群集](https://www.consul.io/docs/internals/architecture.html)通过[八卦协议进行通信](https://www.consul.io/docs/internals/gossip.html)并使用[Raft共识协议](https://www.consul.io/docs/internals/consensus.html)。

## 68.1如何激活

要激活Consul Service Discovery，请使用具有组`org.springframework.cloud`和工件ID 的启动器`spring-cloud-starter-consul-discovery`。有关使用当前Spring Cloud Release Train设置构建系统的详细信息，请参见[Spring Cloud Project页面](https://projects.spring.io/spring-cloud/)。

## 68.2向领事注册

当客户端向Consul注册时，它将提供有关其自身的元数据，例如主机和端口，id，名称和标签。默认情况下会创建一个HTTP [Check](https://www.consul.io/docs/agent/checks.html)，Consul `/health`每10秒就会命中一次端点。如果运行状况检查失败，则将该服务实例标记为关键。

领事客户端示例：

```java
@SpringBootApplication
@RestController
public class Application {

    @RequestMapping("/")
    public String home() {
        return "Hello world";
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }

}
```

（即完全正常的Spring Boot应用）。如果Consul客户端位于以外的其他位置`localhost:8500`，则需要进行配置才能找到该客户端。例：

**application.yml。** 

```properties
spring:
  cloud:
    consul:
      host: localhost
      port: 8500
```



| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/caution.png) | 警告 |
| ------------------------------------------------------------ | ---- |
| 如果您使用[Spring Cloud Consul Config](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-consul-config.html)，则需要将上述值`bootstrap.yml`代替`application.yml`。 |      |

默认服务名称，实例ID和端口，从所拍摄的`Environment`，是`${spring.application.name}`，Spring上下文和ID `${server.port}`分别。

要禁用Consul Discovery Client，您可以设置`spring.cloud.consul.discovery.enabled`为`false`。当领事发现客户端也将被禁止`spring.cloud.discovery.enabled`设置为`false`。

要禁用服务注册，可以设置`spring.cloud.consul.discovery.register`为`false`。

### 68.2.1将管理注册为单独的服务

当管理服务器端口设置为与应用程序端口不同时，通过设置`management.server.port`属性，管理服务将被注册为与应用程序服务不同的服务。例如：

**application.yml。** 

```properties
spring:
  application:
    name: myApp
management:
  server:
    port: 4452
```



以上配置将注册以下两项服务：

- 申请服务：

```properties
ID: myApp
Name: myApp
```

- 管理服务：

```properties
ID: myApp-management
Name: myApp-management
```

管理服务将继承它`instanceId`，并`serviceName`从应用程序的服务。例如：

**application.yml。** 

```properties
spring:
  application:
    name: myApp
management:
  server:
    port: 4452
spring:
  cloud:
    consul:
      discovery:
        instance-id: custom-service-id
        serviceName: myprefix-${spring.application.name}
```



以上配置将注册以下两项服务：

- 申请服务：

```properties
ID: custom-service-id
Name: myprefix-myApp
```

- 管理服务：

```properties
ID: custom-service-id-management
Name: myprefix-myApp-management
```

通过以下属性可以进行进一步的自定义：

```properties
/** Port to register the management service under (defaults to management port) */
spring.cloud.consul.discovery.management-port

/** Suffix to use when registering management service (defaults to "management" */
spring.cloud.consul.discovery.management-suffix

/** Tags to use when registering management service (defaults to "management" */
spring.cloud.consul.discovery.management-tags
```

## 68.3 HTTP运行状况检查

Consul实例的运行状况检查默认为“ / health”，这是Spring Boot Actuator应用程序中有用端点的默认位置。如果您使用非默认上下文路径或servlet路径（例如`server.servletPath=/foo`）或管理端点路径（例如`management.server.servlet.context-path=/admin`），则即使对于Actuator应用程序，也需要更改它们。还可以配置Consul用于检查运行状况终结点的时间间隔。“ 10s”和“ 1m”分别代表10秒和1分钟。例：

**application.yml。** 

```properties
spring:
  cloud:
    consul:
      discovery:
        healthCheckPath: ${management.server.servlet.context-path}/health
        healthCheckInterval: 15s
```



您可以通过设置禁用健康检查`management.health.consul.enabled=false`。

### 68.3.1元数据和领事标签

领事尚不支持有关服务的元数据。春季云的`ServiceInstance`具有`Map metadata`场。在Consul正式支持元数据之前，Spring Cloud Consul使用Consul标签来近似元数据。带有形式的标签`key=value`将被拆分并分别用作`Map`键和值。没有等号的标签`=`将用作键和值。

**application.yml。** 

```properties
spring:
  cloud:
    consul:
      discovery:
        tags: foo=bar, baz
```



上面的配置将生成带有`foo→bar`和的映射`baz→baz`。

### 68.3.2使领事实例ID唯一

默认情况下，领事实例的ID等于其Spring Application Context ID。默认情况下，Spring Application Context ID为`${spring.application.name}:comma,separated,profiles:${server.port}`。在大多数情况下，这将允许一项服务的多个实例在一台计算机上运行。如果需要进一步的唯一性，则可以使用Spring Cloud通过在中提供唯一标识符来覆盖它`spring.cloud.consul.discovery.instanceId`。例如：

**application.yml。** 

```properties
spring:
  cloud:
    consul:
      discovery:
        instanceId: ${spring.application.name}:${vcap.application.instance_id:${spring.application.instance_id:${random.value}}}
```



有了此元数据，并且在本地主机上部署了多个服务实例，随机值将在其中插入以使实例唯一。在Cloudfoundry中，`vcap.application.instance_id`它将自动填充在Spring Boot应用程序中，因此将不需要随机值。

### 68.3.3将标头应用于健康检查请求

标头可以应用于健康检查请求。例如，如果您尝试注册使用[Vault Backend](https://github.com/spring-cloud/spring-cloud-config/blob/master/docs/src/main/asciidoc/spring-cloud-config.adoc#vault-backend)的[Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/)服务器：

**application.yml。** 

```properties
spring:
  cloud:
    consul:
      discovery:
        health-check-headers:
          X-Config-Token: 6442e58b-d1ea-182e-cfa5-cf9cddef0722
```



根据HTTP标准，每个标头可以具有多个值，在这种情况下，可以提供一个数组：

**application.yml。** 

```properties
spring:
  cloud:
    consul:
      discovery:
        health-check-headers:
          X-Config-Token:
            - "6442e58b-d1ea-182e-cfa5-cf9cddef0722"
            - "Some other value"
```



## 68.4查找服务

### 68.4.1使用功能区

Spring Cloud支持[Feign](https://github.com/spring-cloud/spring-cloud-netflix/blob/master/docs/src/main/asciidoc/spring-cloud-netflix.adoc#spring-cloud-feign)（一个REST客户端构建器），还支持[Spring`RestTemplate`](https://github.com/spring-cloud/spring-cloud-netflix/blob/master/docs/src/main/asciidoc/spring-cloud-netflix.adoc#spring-cloud-ribbon) 使用逻辑服务名称/标识而不是物理URL查找服务。Feign和可发现发现的RestTemplate都使用[Ribbon](https://cloud.spring.io/spring-cloud-netflix/single/spring-cloud-netflix.html#spring-cloud-ribbon)进行客户端负载平衡。

如果要使用RestTemplate访问服务STORES，只需声明：

```java
@LoadBalanced
@Bean
public RestTemplate loadbalancedRestTemplate() {
     new RestTemplate();
}
```

并以这种方式使用它（注意我们如何使用Consul的STORES服务名称/ id而不是完全限定的域名）：

```java
@Autowired
RestTemplate restTemplate;

public String getFirstProduct() {
   return this.restTemplate.getForObject("https://STORES/products/1", String.class);
}
```

如果您在多个数据中心中拥有Consul群集，并且要访问另一个数据中心中的服务，则仅靠服务名称/ id是不够的。在这种情况下，您使用属性`spring.cloud.consul.discovery.datacenters.STORES=dc-west`where `STORES`是服务名称/ id，`dc-west`是STORES服务所在的数据中心。

### 68.4.2使用DiscoveryClient

您还可以使用，`org.springframework.cloud.client.discovery.DiscoveryClient`该API为发现客户端提供了一个简单的API，该API不特定于Netflix，例如

```java
@Autowired
private DiscoveryClient discoveryClient;

public String serviceUrl() {
    List<ServiceInstance> list = discoveryClient.getInstances("STORES");
    if (list != null && list.size() > 0 ) {
        return list.get(0).getUri();
    }
    return null;
}
```

## 68.5领事目录手表

领事目录监视利用领事[监视服务](https://www.consul.io/docs/agent/watches.html#services)的能力。Catalog Watch进行了阻塞的Consul HTTP API调用，以确定是否有任何服务已更改。如果有新的服务数据，则会发布心跳事件。

更改Config Watch称为change的频率`spring.cloud.consul.config.discovery.catalog-services-watch-delay`。默认值为1000，以毫秒为单位。延迟是上一次调用结束与下一次调用开始之间的时间量。

禁用目录监视集`spring.cloud.consul.discovery.catalogServicesWatch.enabled=false`。

手表使用Spring `TaskScheduler`安排对领事的呼叫。默认情况下，它是a `ThreadPoolTaskScheduler`，其`poolSize`值为1。要更改`TaskScheduler`，请创建一个`TaskScheduler`以`ConsulDiscoveryClientConfiguration.CATALOG_WATCH_TASK_SCHEDULER_NAME`常量命名的类型的bean 。