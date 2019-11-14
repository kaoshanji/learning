# 76. Using Spring Cloud Zookeeper with Spring Cloud Netflix Components

## 76.将Spring Cloud Zookeeper与Spring Cloud Netflix Components一起使用

Spring Cloud Netflix提供了有用的工具，无论`DiscoveryClient` 您使用哪种实施方式，它都可以工作。Feign，Turbine，Ribbon和Zuul均与Spring Cloud Zookeeper一起使用。

## 76.1带Zookeeper的功能区

Spring Cloud Zookeeper提供了Ribbon的实现`ServerList`。当您使用`spring-cloud-starter-zookeeper-discovery`，色带自动配置为使用 `ZookeeperServerList`默认。