# 15. Hystrix Timeouts And Ribbon Clients

## 15. Hystrix超时和功能区客户端

使用用于包装Ribbon客户端的Hystrix命令时，您要确保将Hystrix超时配置为比配置的Ribbon超时更长，包括可能进行的任何重试。例如，如果您的功能区连接超时为一秒钟，并且功能区客户端可能重试该请求三次，则您的Hystrix超时应该稍微超过三秒钟。

## 15.1如何包括Hystrix仪表板

要将Hystrix仪表板包含在您的项目中，请使用组ID为`org.springframework.cloud`和工件ID为的启动器`spring-cloud-starter-netflix-hystrix-dashboard`。有关使用当前Spring Cloud Release Train设置构建系统的详细信息，请参见[Spring Cloud Project页面](https://projects.spring.io/spring-cloud/)。

要运行Hystrix仪表板，请使用注释您的Spring Boot主类`@EnableHystrixDashboard`。然后访问`/hystrix`并将仪表板指向`/hystrix.stream`Hystrix客户端应用程序中单个实例的端点。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 连接到`/hystrix.stream`使用HTTPS 的端点时，JVM必须信任服务器使用的证书。如果证书不受信任，则必须将证书导入JVM，以便Hystrix仪表板成功连接到流端点。 |

## 15.2涡轮

从系统的整体运行状况来看，查看单个实例的Hystrix数据不是很有用。[Turbine](https://github.com/Netflix/Turbine)是一种将所有相关`/hystrix.stream`端点聚合到一起的应用程序，可`/turbine.stream`在Hystrix仪表板中使用。个别实例通过Eureka定位。运行Turbine需要使用注释对您的主类进行`@EnableTurbine`注释（例如，通过使用spring-cloud-starter-netflix-turbine设置类路径）。[Turbine 1 Wiki中](https://github.com/Netflix/Turbine/wiki/Configuration-(1.x))记录的所有配置属性均适用。唯一的区别是`turbine.instanceUrlSuffix`不需要端口，因为除非，否则端口会自动处理`turbine.instanceInsertPort=false`。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 默认情况下，Turbine `/hystrix.stream`通过在Eureka中查找其实例`hostName`和`port`条目，然后追加`/hystrix.stream`到它，来在已注册实例上查找端点。如果实例的元数据包含`management.port`，则使用它代替端点的`port`值`/hystrix.stream`。默认情况下，调用的元数据条目`management.port`等于`management.port`配置属性。可以使用以下配置覆盖它： |

```properties
eureka:
  instance:
    metadata-map:
      management.port: ${management.port:8081}
```

该`turbine.appConfig`配置关键是Eureka serviceIds的列表，涡轮用来查找实例。然后在Hystrix仪表板中使用该涡轮流，并使用类似于以下内容的URL：

```
https://my.turbine.server:8080/turbine.stream?cluster=CLUSTERNAME
```

如果名称为，则可以省略cluster参数`default`。该`cluster`参数必须项匹配`turbine.aggregator.clusterConfig`。从Eureka返回的值是大写的。因此，如果有一个`customers`在Eureka进行了注册的应用程序，则下面的示例有效：

```properties
turbine:
  aggregator:
    clusterConfig: CUSTOMERS
  appConfig: customers
```

如果您需要定制Turbine应该使用哪些集群名称（因为您不想在`turbine.aggregator.clusterConfig`配置中存储集群名称 ），请提供type的bean `TurbineClustersProvider`。

所述`clusterName`可以通过SPEL表达被定制`turbine.clusterNameExpression`以根作为实例`InstanceInfo`。默认值为`appName`，这意味着Eureka `serviceId`成为群集密钥（即`InstanceInfo`for客户的`appName`of为`CUSTOMERS`）。一个不同的示例是`turbine.clusterNameExpression=aSGName`，它从AWS ASG名称获取集群名称。以下清单显示了另一个示例：

```properties
turbine:
  aggregator:
    clusterConfig: SYSTEM,USER
  appConfig: customers,stores,ui,admin
  clusterNameExpression: metadata['cluster']
```

在前面的示例中，来自四个服务的集群名称从它们的元数据映射中拉出，并且期望具有包括`SYSTEM`和的值`USER`。

要对所有应用程序使用“ 默认 ”群集，您需要一个字符串文字表达式（如果在YAML中，也要使用单引号和双引号进行转义）：

```properties
turbine:
  appConfig: customers,stores
  clusterNameExpression: "'default'"
```

Spring Cloud提供了一个`spring-cloud-starter-netflix-turbine`具有运行Turbine服务器所需的所有依赖项。要添加Turbine，请创建一个Spring Boot应用程序并使用对其进行注释`@EnableTurbine`。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 默认情况下，Spring Cloud允许Turbine使用主机和端口来允许每个主机，每个集群多个进程。如果你想内置式水轮机本地Netflix的行为*不*允许每个主机上的多个过程，每簇（关键实例ID是主机名），集`turbine.combineHostPort=false`。 |

### 15.2.1集群端点

在某些情况下，其他应用程序了解在Turbine中配置了哪些custers可能会很有用。为此，您可以使用`/clusters`终结点，该终结点将返回所有已配置集群的JSON数组。

**GET /群集。** 

```json
[
  {
    "name": "RACES",
    "link": "http://localhost:8383/turbine.stream?cluster=RACES"
  },
  {
    "name": "WEB",
    "link": "http://localhost:8383/turbine.stream?cluster=WEB"
  }
]
```



可以通过设置`turbine.endpoints.clusters.enabled`为禁用此端点`false`。

## 15.3涡轮流

在某些环境中（例如在PaaS设置中），从所有分布式Hystrix命令中提取指标的经典Turbine模型不起作用。在这种情况下，您可能想让Hystrix命令将度量标准推送到Turbine。Spring Cloud通过消息传递实现了这一点。要在客户端上执行此操作，请向`spring-cloud-netflix-hystrix-stream`和`spring-cloud-starter-stream-*`选择一个依赖项。请参阅[Spring Cloud Stream文档](https://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/)以获取有关代理以及如何配置客户端凭据的详细信息。对于本地代理，它应该开箱即用。

在服务器端，创建一个Spring Boot应用程序并用对其进行注释`@EnableTurbineStream`。Turbine Stream服务器需要使用Spring Webflux，因此`spring-boot-starter-webflux`需要包含在您的项目中。默认情况下`spring-boot-starter-webflux`，将其添加`spring-cloud-starter-netflix-turbine-stream`到您的应用程序时。

然后，您可以将Hystrix仪表板而不是单个Hystrix流指向Turbine Stream Server。如果Turbine Stream在myhost的端口8989上运行，则将其放入`http://myhost:8989`Hystrix仪表板的流输入字段中。电路的前缀是各自的`serviceId`，后跟点（`.`），然后是电路名称。

Spring Cloud提供了一个`spring-cloud-starter-netflix-turbine-stream`具有运行Turbine Stream服务器所需的所有依赖项。然后，您可以添加自己选择的Stream活页夹-例如`spring-cloud-starter-stream-rabbit`。

Turbine Stream服务器也支持该`cluster`参数。与Turbine服务器不同，Turbine Stream使用eureka serviceIds作为群集名称，并且这些名称不可配置。

如果Turbine Stream服务器在端口8989上运行，并且您的环境中`my.turbine.server`有两个eureka serviceId ，则以下URL将在您的Turbine Stream服务器上可用。空集群名称将提供Turbine Stream服务器接收的所有指标。`customers``products``default`

```properties
https://my.turbine.sever:8989/turbine.stream?cluster=customers
https://my.turbine.sever:8989/turbine.stream?cluster=products
https://my.turbine.sever:8989/turbine.stream?cluster=default
https://my.turbine.sever:8989/turbine.stream
```

因此，您可以将eureka serviceIds用作Turbine仪表板（或任何兼容的仪表板）的群集名称。你并不需要配置像任何性能`turbine.appConfig`，`turbine.clusterNameExpression`并`turbine.aggregator.clusterConfig`为您的涡轮机流服务器。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| Turbine Stream服务器使用Spring Cloud Stream从配置的输入通道收集所有指标。这意味着它不会从每个实例主动收集Hystrix指标。它只能提供每个实例已经收集到输入通道中的度量。 |