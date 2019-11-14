# 78. Zookeeper Dependencies

## 78.动物园管理员的依存关系

以下主题介绍了如何使用Spring Cloud Zookeeper依赖项：

- [第78.1节“使用Zookeeper依赖关系”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-zookeeper-dependencies.html#spring-cloud-zookeeper-dependencies-using)
- [第78.2节“激活Zookeeper依赖关系”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-zookeeper-dependencies.html#spring-cloud-zookeeper-dependencies-activating)
- [第78.3节“设置Zookeeper依赖关系”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-zookeeper-dependencies.html#spring-cloud-zookeeper-dependencies-setting-up)
- [第78.4节“配置Spring Cloud Zookeeper依赖关系”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-zookeeper-dependencies.html#spring-cloud-zookeeper-dependencies-configuring)

## 78.1使用Zookeeper依赖关系

Spring Cloud Zookeeper使您可以将应用程序的依赖项作为属性提供。作为依赖项，您可以了解在Zookeeper中注册的其他应用程序，以及您希望通过[Feign](https://github.com/spring-cloud/spring-cloud-netflix/blob/master/docs/src/main/asciidoc/spring-cloud-netflix.adoc#spring-cloud-feign) （REST客户端生成器）和[Spring`RestTemplate`](https://github.com/spring-cloud/spring-cloud-netflix/blob/master/docs/src/main/asciidoc/spring-cloud-netflix.adoc#spring-cloud-ribbon)调用的其他应用程序 。

您还可以使用Zookeeper依赖关系观察器功能来控制和监视依赖关系的状态。

## 78.2激活Zookeeper依赖关系

包括对的依赖会 `org.springframework.cloud:spring-cloud-starter-zookeeper-discovery`启用自动配置，该自动配置会设置Spring Cloud Zookeeper依赖关系。即使您在属性中提供了依赖关系，也可以关闭依赖关系。为此，请将`spring.cloud.zookeeper.dependency.enabled`属性设置 为false（默认为`true`）。

## 78.3设置Zookeeper依赖关系

考虑下面的依赖关系表示示例：

**application.yml。** 

```properties
spring.application.name: yourServiceName
spring.cloud.zookeeper:
  dependencies:
    newsletter:
      path: /path/where/newsletter/has/registered/in/zookeeper
      loadBalancerType: ROUND_ROBIN
      contentTypeTemplate: application/vnd.newsletter.$version+json
      version: v1
      headers:
        header1:
            - value1
        header2:
            - value2
      required: false
      stubs: org.springframework:foo:stubs
    mailing:
      path: /path/where/mailing/has/registered/in/zookeeper
      loadBalancerType: ROUND_ROBIN
      contentTypeTemplate: application/vnd.mailing.$version+json
      version: v1
      required: true
```



接下来的几节将逐一介绍依赖关系的每个部分。根属性名称为`spring.cloud.zookeeper.dependencies`。

### 78.3.1别名

在root属性下，您必须将每个依赖项表示为别名。这是由于Ribbon的约束所致，它要求将应用程序ID放在URL中。因此，您不能通过任何复杂的路径，例如`/myApp/myRoute/name`。别名是你使用的不是名称`serviceId`的`DiscoveryClient`，`Feign`或 `RestTemplate`。

在前面的示例中，别名为`newsletter`和`mailing`。以下示例显示带有`newsletter`别名的伪装用法：

```java
@FeignClient("newsletter")
public interface NewsletterService {
        @RequestMapping(method = RequestMethod.GET, value = "/newsletter")
        String getNewsletters();
}
```

### 78.3.2路径

该路径由`path`YAML属性表示，并且是在Zookeeper下注册依赖项的路径。如上 [一节所述](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-zookeeper-dependencies.html#spring-cloud-zookeeper-dependencies-setting-up-aliases)，Ribbon在URL上运行。结果，该路径不符合其要求。这就是为什么Spring Cloud Zookeeper将别名映射到正确的路径。

### 78.3.3负载均衡器类型

负载均衡器类型由`loadBalancerType`YAML属性表示。

如果您知道在调用此特定依赖项时必须应用哪种负载平衡策略，则可以在YAML文件中提供它，并自动应用它。您可以选择以下负载平衡策略之一：

- STICKY：选择后，将始终调用该实例。
- 随机：随机选择一个实例。
- ROUND_ROBIN：反复遍历实例。

### 78.3.4 `Content-Type`模板和版本

该`Content-Type`模板和版本是由代表`contentTypeTemplate`和 `version`YAML性能。

如果您在`Content-Type`标头中对API进行版本控制，则不想将此标头添加到每个请求中。另外，如果您要调用API的新版本，则不想在代码中漫游以提高API版本。因此，您可以提供 `contentTypeTemplate`一个特殊的`$version`占位符。该占位符将由`version`YAML属性的值填充 。考虑以下示例`contentTypeTemplate`：

```properties
application/vnd.newsletter.$version+json
```

进一步考虑以下几点`version`：

```
v1
```

`contentTypeTemplate`和版本的组合会`Content-Type`为每个请求创建一个 标头，如下所示：

```properties
application/vnd.newsletter.v1+json
```

### 78.3.5默认标题

默认标头由`headers`YAML中的映射表示。

有时，对依赖项的每次调用都需要设置一些默认头。要不在代码中执行此操作，可以在YAML文件中进行设置，如以下示例 `headers`部分所示：

```properties
headers:
    Accept:
        - text/html
        - application/xhtml+xml
    Cache-Control:
        - no-cache
```

该`headers`部分导致在您的HTTP请求中添加`Accept`和`Cache-Control`标头以及相应的值列表。

### 78.3.6所需的依赖关系

所需的依赖关系由`required`YAML中的属性表示。

如果在应用程序启动时需要建立依赖关系之一，则可以`required: true`在YAML文件中设置属性。

如果您的应用程序在启动期间无法本地化所需的依赖项，则会引发异常，并且Spring Context无法设置。换句话说，如果所需的依赖项未在Zookeeper中注册，则您的应用程序将无法启动。

您可以[在本文档后面](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-zookeeper-dependency-watcher.html#spring-cloud-zookeeper-dependency-watcher-presence-checker)阅读更多有关Spring Cloud Zookeeper在线状态检查器的 [信息](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-zookeeper-dependency-watcher.html#spring-cloud-zookeeper-dependency-watcher-presence-checker)。

### 78.3.7存根

您可以提供一个冒号分隔的指向JAR的路径，其中包含依赖项的存根，如以下示例所示：

```
stubs: org.springframework:myApp:stubs
```

哪里：

- `org.springframework`是`groupId`。
- `myApp`是`artifactId`。
- `stubs`是分类器。（请注意，这`stubs`是默认值。）

因为`stubs`是默认分类器，所以前面的示例等于以下示例：

```
stubs: org.springframework:myApp
```

## 78.4配置Spring Cloud Zookeeper依赖关系

您可以设置以下属性来启用或禁用部分Zookeeper依赖关系功能：

- `spring.cloud.zookeeper.dependencies`注意：如果不设置此属性，则不能使用Zookeeper依赖关系。
- `spring.cloud.zookeeper.dependency.ribbon.enabled`（默认情况下启用）：功能区需要显式全局配置或特定的依赖项配置。通过启用此属性，可以实现运行时负载平衡策略解析，并且您可以使用 `loadBalancerType`Zookeeper依赖关系部分。需要此属性的配置具有将`LoadBalancerClient`其委托给 `ILoadBalancer`下一个项目符号的实现。
- `spring.cloud.zookeeper.dependency.ribbon.loadbalancer`（默认情况下启用）：由于使用此属性，该自定义项`ILoadBalancer`知道传递给Ribbon的URI部分实际上可能是必须在Zookeeper中解析为正确路径的别名。没有此属性，您将无法在嵌套路径下注册应用程序。
- `spring.cloud.zookeeper.dependency.headers.enabled`（默认情况下启用）：此属性注册一个`RibbonClient`，该属性会自动将适当的标头和内容类型及其版本附加在Dependency配置中。没有此设置，这两个参数将不起作用。
- `spring.cloud.zookeeper.dependency.resttemplate.enabled`（默认情况下启用）：启用后，此属性会修改`@LoadBalanced`-annotated 的请求标头，以`RestTemplate`使其传递标头和内容类型以及在依赖项配置中设置的版本。没有此设置，这两个参数将不起作用。