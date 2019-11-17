# 109. How to Include Spring Cloud Gateway

## 109.如何包括Spring Cloud Gateway

要将Spring Cloud Gateway包含在您的项目中，请使用具有组`org.springframework.cloud` 和工件ID 的启动器`spring-cloud-starter-gateway`。有关 使用当前Spring Cloud Release Train设置构建系统的详细信息，请参见[Spring Cloud Project页面](https://projects.spring.io/spring-cloud/)。

如果包括启动器，但是由于某种原因，您不希望启用网关，请设置`spring.cloud.gateway.enabled=false`。

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| Spring Cloud Gateway是基于[Spring Boot 2.x](https://spring.io/projects/spring-boot#learn)， [Spring WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)和[Project Reactor ](https://projectreactor.io/docs)[构建的](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)。因此，在使用Spring Cloud Gateway时，许多不熟悉的同步库（例如，Spring Data和Spring Security）和模式可能不适用。如果您对这些项目不熟悉，建议您在使用Spring Cloud Gateway之前先阅读它们的文档，以熟悉一些新概念。 |      |

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| Spring Cloud Gateway需要Spring Boot和Spring Webflux提供的Netty运行时。它不能在传统的Servlet容器中或作为WAR构建。 |      |