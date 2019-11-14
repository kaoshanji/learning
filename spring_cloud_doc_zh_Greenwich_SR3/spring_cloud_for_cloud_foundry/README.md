# XII. Spring Cloud for Cloud Foundry

# 第十二部分。Spring Cloud for Cloud Foundry

Spring Cloud for Cloudfoundry使在[Cloud Foundry](https://github.com/cloudfoundry)（平台即服务）中轻松运行[Spring Cloud](https://github.com/spring-cloud)应用程序 变得容易 。Cloud Foundry具有“服务”的概念，即您“绑定”到应用程序的中间件，本质上为它提供了一个包含凭据的环境变量（例如，用于服务的位置和用户名）。

该`spring-cloud-cloudfoundry-commons`模块可配置基于Reactor的Cloud Foundry Java客户端v 3.0，并且可以独立使用。

该`spring-cloud-cloudfoundry-web`项目为Cloud Foundry中的Webapp的某些增强功能提供了基本支持：自动绑定到单点登录服务，并可以选择启用粘性路由进行发现。

该`spring-cloud-cloudfoundry-discovery`项目提供了Spring Cloud Commons的实现，`DiscoveryClient`因此您可以 `@EnableDiscoveryClient`并提供凭据 `spring.cloud.cloudfoundry.discovery.[username,password]`（也`*.url`可以在未连接到[Pivotal Web Services的情况下](https://run.pivotal.io/)），然后可以`DiscoveryClient`直接使用或通过`LoadBalancerClient`。

首次使用它时，发现客户端可能会变慢，原因是它必须从Cloud Foundry获取访问令牌。