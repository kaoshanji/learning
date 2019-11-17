# 9、系统要求

## 9.系统要求

Spring Boot 2.1.9.RELEASE需要[Java 8，](https://www.java.com/)并且与Java 12（包括）兼容。 还需要[Spring Framework 5.1.10.RELEASE](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/)或更高版本。

为以下构建工具提供了明确的构建支持：

| 构建工具 | 版   |
| -------- | ---- |
| Maven    | 3.3+ |
| Gradle   | 4.4+ |

## 9.1 Servlet容器

Spring Boot支持以下嵌入式servlet容器：

| 名称         | Servlet版本 |
| ------------ | ----------- |
| Tomcat 9.0   | 4.0         |
| Jetty9.4     | 3.1         |
| Undertow 2.0 | 4.0         |

您还可以将Spring Boot应用程序部署到任何Servlet 3.1+兼容的容器中。