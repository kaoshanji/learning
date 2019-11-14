# 75. Service Discovery with Zookeeper

## 75.使用Zookeeper进行服务发现

服务发现是基于微服务的体系结构的关键原则之一。尝试手动配置每个客户端或某种形式的约定可能很困难并且很脆弱。[Curator](https://curator.apache.org/)（Zookeeper的Java库）通过[Service Discovery Extension](https://curator.apache.org/curator-x-discovery/)提供服务发现。Spring Cloud Zookeeper使用此扩展进行服务注册和发现。

## 75.1激活

包括对的依赖会 `org.springframework.cloud:spring-cloud-starter-zookeeper-discovery`启用自动配置，该自动配置会设置Spring Cloud Zookeeper Discovery。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 对于Web功能，您仍然需要包含 `org.springframework.boot:spring-boot-starter-web`。 |

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/caution.png) | 警告 |
| ------------------------------------------------------------ | ---- |
| 当使用Zookeeper 3.4版时，您需要按[此处](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-zookeeper-install.html)所述更改包含依赖项的方式。 |      |

## 75.2向Zookeeper注册

客户端向Zookeeper注册时，它会提供有关其自身的元数据（例如主机和端口，ID和名称）。

以下示例显示了Zookeeper客户端：

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

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 前面的示例是一个普通的Spring Boot应用程序。                  |

如果Zookeeper位于之外的其他`localhost:2181`位置，则配置必须提供服务器的位置，如以下示例所示：

**application.yml。** 

```properties
spring:
  cloud:
    zookeeper:
      connect-string: localhost:2181
```



| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/caution.png) | 警告 |
| ------------------------------------------------------------ | ---- |
| 如果使用[Spring Cloud Zookeeper Config](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-zookeeper-config.html)，则需要使用上一个示例中显示的值，`bootstrap.yml`而不是 `application.yml`。 |      |

默认服务名称，实例ID和端口（从中获取`Environment`）分别为 `${spring.application.name}`，Spring Context ID和`${server.port}`。

在`spring-cloud-starter-zookeeper-discovery`类路径上使用该应用程序既可以使其成为Zookeeper的“ 服务 ”（即它自己注册），又可以成为“ 客户端 ”（即，它可以查询Zookeeper来定位其他服务）。

如果要禁用Zookeeper Discovery Client，可以设置 `spring.cloud.zookeeper.discovery.enabled`为`false`。

## 75.3使用DiscoveryClient

Spring Cloud 使用逻辑服务名称而不是物理URL 支持 [Feign](https://github.com/spring-cloud/spring-cloud-netflix/blob/master/docs/src/main/asciidoc/spring-cloud-netflix.adoc#spring-cloud-feign) （REST客户端构建器）和 [Spring `RestTemplate`](https://github.com/spring-cloud/spring-cloud-netflix/blob/master/docs/src/main/asciidoc/spring-cloud-netflix.adoc#spring-cloud-ribbon)。

您还可以使用`org.springframework.cloud.client.discovery.DiscoveryClient`，它为发现客户端提供了一个简单的API，该API不特定于Netflix，如以下示例所示：

```java
@Autowired
private DiscoveryClient discoveryClient;

public String serviceUrl() {
    List<ServiceInstance> list = discoveryClient.getInstances("STORES");
    if (list != null && list.size() > 0 ) {
        return list.get(0).getUri().toString();
    }
    return null;
}
```