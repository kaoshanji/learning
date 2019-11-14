# 80. Distributed Configuration with Zookeeper

## 80. Zookeeper的分布式配置

Zookeeper提供了一个 [分层的名称空间](https://zookeeper.apache.org/doc/current/zookeeperOver.html#sc_dataModelNameSpace) ，该[名称空间](https://zookeeper.apache.org/doc/current/zookeeperOver.html#sc_dataModelNameSpace)使客户端可以存储任意数据，例如配置数据。Spring Cloud Zookeeper Config是[Config Server和Client](https://github.com/spring-cloud/spring-cloud-config)的替代方案 。在特殊的“ 引导 ” 阶段将配置加载到Spring环境中。`/config`默认情况下，配置存储在名称空间中。`PropertySource`根据应用程序的名称和活动配置文件创建多个 实例，以模仿Spring Cloud Config解析属性的顺序。例如，名称为`testApp`和带有`dev`配置文件的应用程序为其创建了以下属性源：

- `config/testApp,dev`
- `config/testApp`
- `config/application,dev`
- `config/application`

最具体的属性来源在顶部，最不具体的属性在底部。`config/application`名称空间中的属性适用于所有使用zookeeper进行配置的应用程序。`config/testApp`命名空间中的属性仅对名为的服务实例可用`testApp`。

当前在启动应用程序时读取配置。发送HTTP `POST` 请求以`/refresh`导致重新加载配置。当前未实现监视配置名称空间（Zookeeper支持）。

## 80.1激活

包括对的依赖会 `org.springframework.cloud:spring-cloud-starter-zookeeper-config`启用自动配置，该自动配置会设置Spring Cloud Zookeeper Config。

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/caution.png) | 警告 |
| ------------------------------------------------------------ | ---- |
| 当使用Zookeeper 3.4版时，您需要按[此处](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-zookeeper-install.html)所述更改包含依赖项的方式。 |      |

## 80.2定制

可以通过设置以下属性来自定义Zookeeper Config：

**bootstrap.yml。** 

```properties
spring:
  cloud:
    zookeeper:
      config:
        enabled: true
        root: configuration
        defaultContext: apps
        profileSeparator: '::'
```



- `enabled`：设置此值以`false`禁用Zookeeper Config。
- `root`：设置配置名称。
- `defaultContext`：设置所有应用程序使用的名称。
- `profileSeparator`：设置分隔符的值，该分隔符用于在带有概要文件的属性源中分隔概要文件名称。

## 80.3访问控制列表（ACL）

您可以通过调用bean 的`addAuthInfo` 方法来添加Zookeeper ACL的身份验证信息`CuratorFramework`。实现此目的的一种方法是提供自己的 `CuratorFramework`bean，如以下示例所示：

```java
@BoostrapConfiguration
public class CustomCuratorFrameworkConfig {

  @Bean
  public CuratorFramework curatorFramework() {
    CuratorFramework curator = new CuratorFramework();
    curator.addAuthInfo("digest", "user:password".getBytes());
    return curator;
  }

}
```

请查阅 [ZookeeperAutoConfiguration类，](https://github.com/spring-cloud/spring-cloud-zookeeper/blob/master/spring-cloud-zookeeper-core/src/main/java/org/springframework/cloud/zookeeper/ZookeeperAutoConfiguration.java) 以查看`CuratorFramework`Bean的默认配置。

另外，您可以从依赖现有`CuratorFramework`Bean 的类中添加凭据 ，如以下示例所示：

```java
@BoostrapConfiguration
public class DefaultCuratorFrameworkConfig {

  public ZookeeperConfig(CuratorFramework curator) {
    curator.addAuthInfo("digest", "user:password".getBytes());
  }

}
```

此bean的创建必须在boostrapping阶段进行。您可以注册配置类以在此阶段运行，方法是在配置类中添加注释并将它们 `@BootstrapConfiguration`包括在以逗号分隔的列表中，并将其设置为文件中`org.springframework.cloud.bootstrap.BootstrapConfiguration`属性 的值`resources/META-INF/spring.factories`，如以下示例所示：

**资源/META-INF/spring.factories。** 

```properties
org.springframework.cloud.bootstrap.BootstrapConfiguration=\
my.project.CustomCuratorFrameworkConfig,\
my.project.DefaultCuratorFrameworkConfig
```