# I. 云原生应用

 [Cloud Native](https://pivotal.io/platform-as-a-service/migrating-to-cloud-native-application-architectures-ebook)是一种应用程序开发样式，鼓励在持续交付和价值驱动型开发领域轻松采用最佳实践。一个相关的学科是构建[12要素应用程序](https://12factor.net/)，其中开发实践与交付和运营目标保持一致，例如，通过使用声明性编程，管理和监视。Spring Cloud通过多种特定方式促进了这些开发风格。起点是一组功能，分布式系统中的所有组件都需要轻松访问这些功能。 



Spring Cloud构建于其中的Spring [Boot](https://projects.spring.io/spring-boot)涵盖了许多这些功能。Spring Cloud作为两个库提供了更多功能：Spring Cloud上下文和Spring Cloud Commons。Spring Cloud Context为`ApplicationContext`Spring Cloud应用程序提供实用程序和特殊服务（引导上下文，加密，刷新作用域和环境端点）。Spring Cloud Commons是在不同Spring Cloud实现中使用的一组抽象和通用类（例如Spring Cloud Netflix和Spring Cloud Consul）。 