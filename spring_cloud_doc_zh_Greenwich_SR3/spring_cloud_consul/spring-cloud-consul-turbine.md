# 73. Hystrix metrics aggregation with Turbine and Consul

## 73.通过Turbine和Consul进行Hystrix指标聚合

Turbine（由Spring Cloud Netflix项目提供）汇总了多个实例Hystrix指标流，因此仪表板可以显示汇总视图。Turbine使用该`DiscoveryClient`界面查找相关实例。要将Turbine与Spring Cloud Consul一起使用，请以类似于以下示例的方式配置Turbine应用程序：

**pom.xml。** 

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-netflix-turbine</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```



请注意，Turbine依赖项不是启动器。涡轮启动器包括对Netflix Eureka的支持。

**application.yml。** 

```properties
spring.application.name: turbine
applications: consulhystrixclient
turbine:
  aggregator:
    clusterConfig: ${applications}
  appConfig: ${applications}
```



在`clusterConfig`与`appConfig`部分必须匹配，所以它是非常有用的投入的业务ID的逗号分隔列表到一个单独的配置属性。

**Turbine.java。** 

```java
@EnableTurbine
@SpringBootApplication
public class Turbine {
    public static void main(String[] args) {
        SpringApplication.run(DemoturbinecommonsApplication.class, args);
    }
}
```