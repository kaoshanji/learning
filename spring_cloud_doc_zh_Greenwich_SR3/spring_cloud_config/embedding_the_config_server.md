# 8. Embedding the Config Server

## 8.嵌入配置服务器

Config Server最好作为独立应用程序运行。但是，如果需要，可以将其嵌入另一个应用程序。为此，请使用`@EnableConfigServer`注释。`spring.cloud.config.server.bootstrap`在这种情况下，名为的可选属性会很有用。它是一个标志，指示服务器是否应从其自己的远程存储库中进行配置。默认情况下，该标志为关闭状态，因为它会延迟启动。但是，当嵌入到另一个应用程序中时，以与其他任何应用程序相同的方式初始化是有意义的。设置为时`spring.cloud.config.server.bootstrap`，`true`还必须使用[复合环境存储库配置](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_config_server.html#composite-environment-repositories)。例如

```properties
spring:
  application:
    name: configserver
  profiles:
    active: composite
  cloud:
    config:
      server:
        composite:
          - type: native
            search-locations: ${HOME}/Desktop/config
        bootstrap: true
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果使用bootstrap标志，则配置服务器需要在中配置其名称和存储库URI `bootstrap.yml`。 |

要更改服务器端点的位置，您可以（可选）设置`spring.cloud.config.server.prefix`（例如`/config`）以在前缀下提供资源。前缀应以开头，但不能以结束`/`。它应用于`@RequestMappings`Config Server中的（即Spring Boot `server.servletPath`和`server.contextPath`前缀之下）。

如果要直接从后端存储库（而不是从配置服务器）读取应用程序的配置，则基本上需要没有端点的嵌入式配置服务器。您可以不使用`@EnableConfigServer`批注（set `spring.cloud.config.server.bootstrap=true`）来完全关闭端点。