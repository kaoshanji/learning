# web系统-微服务

以Java Web 为背景

概要说明一下`微服务`相关技术。

Web系统下的微服务是一种分布式系统，一般大型网站都是分布式的，微服务在这里呈现出来的是把业务分割，逻辑单独，体现的价值是可以让项目可持续发展。

理解微服务，不能仅局限于微服务本身，还需要从更高一层上看待，他首先是个大型网站，是个分布式的应用。

微服务概念里关键词：上下文边界(业务划分)、集成方案、监控、部署，报表数据。

以上就分别需要不同的技术去支持，并不认同 spring cloud 就是代表了微服务框架，他只是解决了一部分问题，如同集中式Web应用，MVC框架很重要，但是，他也只解决一部分问题，对象依赖、数据存储，这些都组合起来才完成了Web应用，更不提这些软件的运行环境，前端技术等等，更深入一点，还有网络。


- 上下文边界：领域驱动，业务怎样划分，这个问题很关键。
- 集成方案
  - 数据库是不能作为集成方案，那样会更不稳定。
  - 同步：REST、RPC
  - 异步：消息代理、注册回调
- 监控
  - 独立运行的服务状态
  - 各种中间件服务器
  - 操作系统
- 部署
  - 服务项目多了，手动部署都嫌弃

**从功能上说-不断补充**
- [spring cloud](../spring-cloud/README.md)：服务应用，包括服务治理、配置中心、网关、服务依赖、服务监控
  - 也集成了很多其他组件
- zipkin：服务链路追踪监控
- [RabbitMQ](../rabbitmq/README.md)：消息代理服务器，专注于应用间传递消息
- ...
- Kafka：
- docker：打包部署
- zookeeper：全局时钟

说起来，引进这些软件，也引进了更多的风险，比如说，RabbitMQ挂了怎么办?消息服务本身也有一些常见问题：顺序性、重复性


本地演示示例与线上生产的差别就像是`王者荣耀`里路人局和KPL。
