# 12. Service Discovery: Eureka Server

## 12.服务发现：Eureka服务器

本节介绍如何设置Eureka服务器。

## 12.1如何包括Eureka服务器

要将Eureka Server包含在您的项目中，请使用组ID为`org.springframework.cloud`和工件ID为的启动器`spring-cloud-starter-netflix-eureka-server`。有关使用当前Spring Cloud Release Train设置构建系统的详细信息，请参见[Spring Cloud Project页面](https://projects.spring.io/spring-cloud/)。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果您的项目已经使用Thymeleaf作为其模板引擎，则可能无法正确加载Eureka服务器的Freemarker模板。在这种情况下，必须手动配置模板加载器： |

**application.yml。** 

```properties
spring:
  freemarker:
    template-loader-path: classpath:/templates/
    prefer-file-system-access: false
```



## 12.2如何运行尤里卡服务器

以下示例显示了最小的Eureka服务器：

```java
@SpringBootApplication
@EnableEurekaServer
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }

}
```

该服务器下有一个主页，其中包含UI和HTTP API端点，可用于下方的常规Eureka功能`/eureka/*`。

以下链接具有一些Eureka背景知识：[磁通电容器](https://github.com/cfregly/fluxcapacitor/wiki/NetflixOSS-FAQ#eureka-service-discovery-load-balancer)和[google小组讨论](https://groups.google.com/forum/?fromgroups#!topic/eureka_netflix/g3p2r7gHnN0)。

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 由于Gradle的依赖性解析规则以及缺少父bom功能，具体取决于`spring-cloud-starter-netflix-eureka-server`可能导致应用程序启动失败。要解决此问题，请添加Spring Boot Gradle插件并按如下所示导入Spring Cloud Starter父Bom：**build.gradle。** `buildscript {  依赖项{    classpath（“ org.springframework.boot：spring-boot-gradle-plugin：{spring-boot-docs-version}”）  } } 应用插件：“ spring-boot” dependencyManagement {  进口{    mavenBom “ org.springframework.cloud:spring-cloud-dependencies:{spring-cloud-version}”  } }` |

## 12.3高可用性，区域和区域

Eureka服务器没有后端存储，但是注册表中的所有服务实例都必须发送心跳信号以使其注册保持最新（因此可以在内存中完成）。客户端还具有Eureka注册的内存缓存（因此，对于每个对服务的请求，它们都不必进入注册表）。

默认情况下，每个Eureka服务器也是Eureka客户端，并且需要（至少一个）服务URL来定位对等方。如果您不提供该服务，则该服务将运行并运行，但是它将使您的日志充满关于无法向对等方注册的噪音。

另请参阅[以下](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-ribbon.html)有关客户端对区域和区域[的功能区支持的详细信息](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-ribbon.html)。

## 12.4独立模式

只要有某种监视器或弹性运行时（例如Cloud Foundry），这两个缓存（客户端和服务器）和心跳的组合就可以使独立的Eureka服务器对故障具有相当的恢复能力。在独立模式下，您可能希望关闭客户端行为，以使其不会继续尝试并无法到达其对等对象。下面的示例演示如何关闭客户端行为：

**application.yml（独立Eureka服务器）。** 

```properties
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```



请注意，`serviceUrl`指向与本地实例相同的主机。

## 12.5同行意识

通过运行多个实例并要求它们相互注册，可以使Eureka更具弹性并可以使用。实际上，这是默认行为，因此要使它起作用，您需要做的就是`serviceUrl`向对等方添加一个有效值，如以下示例所示：

**application.yml（两个对等感知Eureka服务器）。** 

```properties
---
spring:
  profiles: peer1
eureka:
  instance:
    hostname: peer1
  client:
    serviceUrl:
      defaultZone: http://peer2/eureka/

---
spring:
  profiles: peer2
eureka:
  instance:
    hostname: peer2
  client:
    serviceUrl:
      defaultZone: http://peer1/eureka/
```



在前面的示例中，我们有一个YAML文件，通过在不同的Spring配置文件中运行该服务器，可以在两个主机（`peer1`和`peer2`）上运行同一服务器。您可以使用此配置通过操作`/etc/hosts`解析主机名来测试单个主机上的对等感知（在生产环境中这样做没有太大价值）。实际上，`eureka.instance.hostname`如果您在知道其主机名的计算机上运行（默认情况下，通过使用查找该主机名），则不需要`java.net.InetAddress`。

您可以将多个对等方添加到系统，并且只要它们均通过至少一个边缘彼此连接，它们就可以在彼此之间同步注册。如果对等方在物理上分开（在一个数据中心内或在多个数据中心之间），则该系统原则上可以经受“ 裂脑 ”式故障的影响。您可以将多个对等方添加到系统中，并且只要它们都直接相互连接，它们就可以在彼此之间同步注册。

**application.yml（三个对等感知Eureka服务器）。** 

```properties
eureka:
  client:
    serviceUrl:
      defaultZone: http://peer1/eureka/,http://peer2/eureka/,http://peer3/eureka/

---
spring:
  profiles: peer1
eureka:
  instance:
    hostname: peer1

---
spring:
  profiles: peer2
eureka:
  instance:
    hostname: peer2

---
spring:
  profiles: peer3
eureka:
  instance:
    hostname: peer3
```



## 12.6何时首选IP地址

在某些情况下，尤里卡最好公布服务的IP地址而不是主机名。设置`eureka.instance.preferIpAddress`为`true`，当应用程序向eureka注册时，它将使用其IP地址而不是其主机名。

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 如果Java无法确定主机名，则IP地址将发送到Eureka。设置主机名的唯一明确方法是通过设置`eureka.instance.hostname`属性。您可以在运行时使用环境变量（例如，）设置主机名`eureka.instance.hostname=${HOST_NAME}`。 |

## 12.7保护Eureka服务器

您只需通过将Spring Security添加到服务器的类路径中，就可以保护Eureka服务器`spring-boot-starter-security`。默认情况下，当Spring Security在类路径上时，它将要求将有效的CSRF令牌与每个请求一起发送到应用程序。Eureka客户通常不会拥有有效的跨站点请求伪造（CSRF）令牌，您需要为`/eureka/**`端点禁用此要求。例如：

```java
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/eureka/**");
        super.configure(http);
    }
}
```

有关CSRF的更多信息，请参见[Spring Security文档](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#csrf)。

可以在Spring Cloud Samples存储[库中](https://github.com/spring-cloud-samples/eureka/tree/Eureka-With-Security)找到演示版的Eureka Server 。

## 12.8 JDK 11支持

在JDK 11中删除了Eureka服务器依赖的JAXB模块。如果打算在运行Eureka服务器时使用JDK 11，则必须在POM或Gradle文件中包括这些依赖项。

```xml
<dependency>
	<groupId>org.glassfish.jaxb</groupId>
	<artifactId>jaxb-runtime</artifactId>
</dependency>
```