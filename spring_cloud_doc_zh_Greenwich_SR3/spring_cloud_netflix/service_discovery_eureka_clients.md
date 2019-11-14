# 11. Service Discovery: Eureka Clients

## 11.服务发现：Eureka客户

服务发现是基于微服务的体系结构的主要宗旨之一。尝试手动配置每个客户端或某种形式的约定可能很困难并且很脆弱。Eureka是Netflix Service Discovery服务器和客户端。可以将服务器配置和部署为高可用性，每个服务器将有关已注册服务的状态复制到其他服务器。

## 11.1如何包括尤里卡客户

要将Eureka Client包含在您的项目中，请使用组ID为`org.springframework.cloud`和工件ID为的启动器`spring-cloud-starter-netflix-eureka-client`。有关使用当前Spring Cloud Release Train设置构建系统的详细信息，请参见[Spring Cloud Project页面](https://projects.spring.io/spring-cloud/)。

## 11.2在尤里卡注册

客户端向Eureka注册时，它会提供有关其自身的元数据，例如主机，端口，运行状况指示器URL，主页和其他详细信息。Eureka从属于服务的每个实例接收心跳消息。如果心跳在可配置的时间表上进行故障转移，则通常会将实例从注册表中删除。

以下示例显示了一个最小的Eureka客户端应用程序：

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

请注意，前面的示例显示了一个普通的[Spring Boot](https://projects.spring.io/spring-boot/)应用程序。通过使用`spring-cloud-starter-netflix-eureka-client`类路径，您的应用程序将自动向Eureka Server注册。需要配置以找到Eureka服务器，如以下示例所示：

**application.yml。** 

```properties
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```



在前面的示例中，“ defaultZone”是一个魔术字符串后备值，它为任何不表达首选项的客户端提供服务URL（换句话说，这是一个有用的默认值）。

默认的应用程序名称（即，服务ID），虚拟主机，和非安全端口（从所拍摄的`Environment`）是`${spring.application.name}`，`${spring.application.name}`和`${server.port}`分别。

拥有`spring-cloud-starter-netflix-eureka-client`类路径可使应用程序同时成为Eureka的“ 实例 ”（即，它自己注册）和“ 客户端 ”（它可以查询注册表以定位其他服务）。实例行为由`eureka.instance.*`配置键驱动，但是如果确保您的应用程序具有值`spring.application.name`（这是Eureka服务ID或VIP 的默认值），则默认值很好。

有关[可](https://github.com/spring-cloud/spring-cloud-netflix/tree/master/spring-cloud-netflix-eureka-client/src/main/java/org/springframework/cloud/netflix/eureka/EurekaInstanceConfigBean.java)配置选项的更多详细信息，请参见[EurekaInstanceConfigBean](https://github.com/spring-cloud/spring-cloud-netflix/tree/master/spring-cloud-netflix-eureka-client/src/main/java/org/springframework/cloud/netflix/eureka/EurekaInstanceConfigBean.java)和[EurekaClientConfigBean](https://github.com/spring-cloud/spring-cloud-netflix/tree/master/spring-cloud-netflix-eureka-client/src/main/java/org/springframework/cloud/netflix/eureka/EurekaClientConfigBean.java)。

要禁用Eureka Discovery Client，可以设置`eureka.client.enabled`为`false`。当尤里卡发现客户端也将被禁止`spring.cloud.discovery.enabled`设置为`false`。

## 11.3通过Eureka服务器进行身份验证

如果其中一个`eureka.client.serviceUrl.defaultZone`URL嵌入了凭据（curl样式，如下所示`http://user:password@localhost:8761/eureka`），则会将HTTP基本身份验证自动添加到您的eureka客户端。对于更复杂的需求，您可以创建一个`@Bean`类型`DiscoveryClientOptionalArgs`并将其插入`ClientFilter`实例，所有这些都将应用于从客户端到服务器的调用。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 由于Eureka的限制，无法支持每服务器的基本身份验证凭据，因此仅使用找到的第一组凭据。 |

## 11.4状态页和运行状况指示器

Eureka实例的状态页面和运行状况指示器分别默认为`/info`和`/health`，这是Spring Boot Actuator应用程序中有用端点的默认位置。如果您使用非默认的上下文路径或servlet路径（例如`server.servletPath=/custom`），则即使对于Actuator应用程序，也需要更改它们。下面的示例显示两个设置的默认值：

**application.yml。** 

```properties
eureka:
  instance:
    statusPageUrlPath: ${server.servletPath}/info
    healthCheckUrlPath: ${server.servletPath}/health
```



这些链接显示在客户端使用的元数据中，并在某些情况下用于确定是否将请求发送到您的应用程序，因此，如果请求准确无误，这将很有帮助。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 在Dalston中，还需要在更改该管理上下文路径时设置状态和运行状况检查URL。从Edgware开始就删除了此要求。 |

## 11.5注册安全的应用程序

如果您想通过HTTPS与您的应用联系，则可以在中设置两个标志`EurekaInstanceConfig`：

- `eureka.instance.[nonSecurePortEnabled]=[false]`
- `eureka.instance.[securePortEnabled]=[true]`

这样做使Eureka发布实例信息，该实例信息显示出对安全通信的明确偏好。Spring Cloud `DiscoveryClient`始终会返回以`https`这种方式配置的服务开头的URI 。同样，以这种方式配置服务时，Eureka（本机）实例信息具有安全的运行状况检查URL。

由于Eureka在内部工作的方式，它仍然会为状态和主页发布非安全URL，除非您也明确地覆盖了这些URL。您可以使用占位符来配置eureka实例URL，如以下示例所示：

**application.yml。** 

```properties
eureka:
  instance:
    statusPageUrl: https://${eureka.hostname}/info
    healthCheckUrl: https://${eureka.hostname}/health
    homePageUrl: https://${eureka.hostname}/
```



（请注意，这`${eureka.hostname}`是本机占位符，仅在更高版本的Eureka中可用。您也可以使用Spring占位符来实现相同的目的，例如，使用`${eureka.instance.hostName}`。）

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果您的应用程序在代理之后运行，并且SSL终止在代理中（例如，如果您在Cloud Foundry或其他平台中作为服务运行），那么您需要确保代理的“ 转发 ”标头被拦截和处理通过应用程序。如果嵌入在Spring Boot应用程序中的Tomcat容器对'X-Forwarded-\ *`标头进行了显式配置，则此操作会自动发生。应用程序提供的指向其自身的链接错误（错误的主机，端口或协议）表明此配置错误。 |

## 11.6尤里卡的健康检查

默认情况下，Eureka使用客户端心跳来确定客户端是否启动。除非另有说明，否则，根据Spring Boot Actuator，发现客户端不会传播应用程序的当前运行状况检查状态。因此，在成功注册后，Eureka始终宣布该应用程序处于“启动”状态。可以通过启用Eureka运行状况检查来更改此行为，这会导致应用程序状态传播到Eureka。结果，所有其他应用程序都不会将流量发送到状态为“ UP”以外的其他应用程序。以下示例显示了如何为客户端启用运行状况检查：

**application.yml。** 

```properties
eureka:
  client:
    healthcheck:
      enabled: true
```



| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| `eureka.client.healthcheck.enabled=true`应该只在中设置`application.yml`。将值设置为`bootstrap.yml`会产生不良的副作用，例如在Eureka中注册`UNKNOWN`状态。 |

如果您需要对健康检查进行更多控制，请考虑实施自己的`com.netflix.appinfo.HealthCheckHandler`。

## 11.7实例和客户端的Eureka元数据

值得花费一些时间来了解Eureka元数据的工作原理，因此您可以在平台上使用有意义的方式使用它。有用于信息的标准元数据，例如主机名，IP地址，端口号，状态页和运行状况检查。这些将发布在服务注册表中，并由客户端用于以直接方式联系服务。可以将其他元数据添加到中的实例注册中`eureka.instance.metadataMap`，并且可以在远程客户端中访问此元数据。通常，除非让客户端知道元数据的含义，否则其他元数据不会更改客户端的行为。在本文档后面将介绍几种特殊情况，其中Spring Cloud已经为元数据映射分配了含义。

### 11.7.1在Cloud Foundry上使用Eureka

Cloud Foundry具有全局路由器，因此同一应用程序的所有实例都具有相同的主机名（其他具有类似体系结构的PaaS解决方案的布置也相同）。这不一定是使用尤里卡的障碍。但是，如果您使用路由器（建议或什至是强制性的，取决于平台的设置方式），则需要显式设置主机名和端口号（安全或非安全），以便它们使用路由器。您可能还希望使用实例元数据，以便可以区分客户端上的实例（例如，在自定义负载均衡器中）。默认情况下`eureka.instance.instanceId`为`vcap.application.instance_id`，如以下示例所示：

**application.yml。** 

```properties
eureka:
  instance:
    hostname: ${vcap.application.uris[0]}
    nonSecurePort: 80
```



根据在Cloud Foundry实例中设置安全规则的方式，您也许可以注册并使用主机VM的IP地址进行直接的服务到服务的调用。Pivotal Web服务（[PWS](https://run.pivotal.io/)）尚不提供此功能。

### 11.7.2在AWS上使用Eureka

如果计划将应用程序部署到AWS云，则必须将Eureka实例配置为可感知AWS。您可以通过如下自定义[EurekaInstanceConfigBean](https://github.com/spring-cloud/spring-cloud-netflix/tree/master/spring-cloud-netflix-eureka-client/src/main/java/org/springframework/cloud/netflix/eureka/EurekaInstanceConfigBean.java)来实现：

```java
@Bean
@Profile("!default")
public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
  EurekaInstanceConfigBean b = new EurekaInstanceConfigBean(inetUtils);
  AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
  b.setDataCenterInfo(info);
  return b;
}
```

### 11.7.3修改Eureka实例ID

一个普通的Netflix Eureka实例注册的ID等于其主机名（即，每个主机仅提供一项服务）。Spring Cloud Eureka提供了合理的默认值，定义如下：

```properties
${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${server.port}}}
```

一个例子是`myhost:myappname:8080`。

通过使用Spring Cloud，您可以通过在中提供唯一标识符来覆盖此值`eureka.instance.instanceId`，如以下示例所示：

**application.yml。** 

```properties
eureka:
  instance:
    instanceId: ${spring.application.name}:${vcap.application.instance_id:${spring.application.instance_id:${random.value}}}
```



通过前面示例中显示的元数据和在本地主机上部署的多个服务实例，在其中插入随机值以使实例唯一。在Cloud Foundry中，`vcap.application.instance_id`会在Spring Boot应用程序中自动填充，因此不需要随机值。

## 11.8使用EurekaClient

一旦拥有作为发现客户端的应用程序，就可以使用它从[Eureka Server](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-eureka-server.html)发现服务实例。一种方法是使用本机`com.netflix.discovery.EurekaClient`（与Spring Cloud相对`DiscoveryClient`），如以下示例所示：

```java
@Autowired
private EurekaClient discoveryClient;

public String serviceUrl() {
    InstanceInfo instance = discoveryClient.getNextServerFromEureka("STORES", false);
    return instance.getHomePageUrl();
}
```

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 不要`EurekaClient`在`@PostConstruct`方法中或`@Scheduled`方法中（或`ApplicationContext`可能尚未开始的任何地方）使用。它使用`SmartLifecycle`（`phase=0`）进行了初始化，因此最早可以依靠它的是处于`SmartLifecycle`更高阶段的另一个。 |

### 11.8.1不带球衣的EurekaClient

默认情况下，EurekaClient使用Jersey进行HTTP通信。如果希望避免来自Jersey的依赖关系，可以将其从依赖关系中排除。Spring Cloud基于Spring自动配置传输客户端`RestTemplate`。以下示例显示排除了Jersey：

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-client</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-core</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.sun.jersey.contribs</groupId>
            <artifactId>jersey-apache-client4</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 11.9本地Netflix EurekaClient的替代方案

您无需使用原始Netflix `EurekaClient`。而且，通常在某种包装器后面使用它会更方便。Spring Cloud 通过逻辑Eureka服务标识符（VIP）而非物理URL 支持[Feign](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-feign.html)（REST客户端构建器）和[Spring`RestTemplate`](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-ribbon.html)。要使用固定的物理服务器列表配置Ribbon，可以设置`.ribbon.listOfServers`为以逗号分隔的物理地址（或主机名）列表，其中``为客户端的ID。

您还可以使用`org.springframework.cloud.client.discovery.DiscoveryClient`，为发现客户端提供简单的API（非Netflix专用），如以下示例所示：

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

## 11.10为什么注册服务这么慢？

成为实例还涉及到注册表的周期性心跳（通过客户端`serviceUrl`），默认持续时间为30秒。直到实例，服务器和客户端在其本地缓存中都具有相同的元数据后，客户端才能发现该服务（因此可能需要3个心跳）。您可以通过设置更改周期`eureka.instance.leaseRenewalIntervalInSeconds`。将其设置为小于30的值可加快使客户端连接到其他服务的过程。在生产中，最好使用默认值，因为服务器中的内部计算对租约续订期进行了假设。

## 11.11区域

如果您已将Eureka客户端部署到多个区域，则您可能希望这些客户端在同一区域中使用服务，然后再尝试在另一个区域中使用服务。要进行设置，您需要正确配置Eureka客户端。

首先，您需要确保已将Eureka服务器部署到每个区域，并且它们彼此对等。有关 更多信息，请参见[区域和区域](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-eureka-server.html#spring-cloud-eureka-server-zones-and-regions)部分。

接下来，您需要告诉Eureka您的服务位于哪个区域。您可以使用`metadataMap`属性来实现。例如，如果`service 1`同时部署到`zone 1`和`zone 2`，则需要在中设置以下Eureka属性`service 1`：

**1区服务1**

```properties
eureka.instance.metadataMap.zone = zone1
eureka.client.preferSameZoneEureka = true
```

**2区服务1**

```properties
eureka.instance.metadataMap.zone = zone2
eureka.client.preferSameZoneEureka = true
```

## 11.12刷新Eureka客户

默认情况下，`EurekaClient`bean是可刷新的，这意味着可以更改和刷新Eureka客户端属性。发生刷新时，客户端将从Eureka服务器中注销，并且可能会在短暂的时间内不提供给定服务的所有实例。消除这种情况的一种方法是禁用刷新Eureka客户端的功能。为此设置`eureka.client.refresh.enable=false`。