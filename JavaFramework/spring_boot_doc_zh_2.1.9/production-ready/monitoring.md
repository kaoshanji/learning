# 54、通过HTTP进行监控和管理

如果您正在开发Web应用程序，则Spring Boot Actuator会自动配置所有启用的端点以通过HTTP公开。默认约定是使用`id`前缀为的端点的`/actuator`作为URL路径。例如，`health`暴露为`/actuator/health`。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| Spring MVC，Spring WebFlux和Jersey本身支持Actuator。         |

## 54.1自定义管理端点路径

有时，自定义管理端点的前缀很有用。例如，您的应用程序可能已经`/actuator`用于其他用途。您可以使用该`management.endpoints.web.base-path`属性来更改管理端点的前缀，如以下示例所示：

```bash
management.endpoints.web.base-path=/manage
```

前面的`application.properties`示例将端点从更改`/actuator/{id}`为`/manage/{id}`（例如`/manage/info`）。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 除非管理端口已经被配置为[通过使用不同的HTTP端口暴露端点](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/production-ready-monitoring.html#production-ready-customizing-management-server-port)，`management.endpoints.web.base-path`相对于`server.servlet.context-path`。如果`management.server.port`已配置，`management.endpoints.web.base-path`则相对于`management.server.servlet.context-path`。 |

如果要将端点映射到其他路径，则可以使用该`management.endpoints.web.path-mapping`属性。

以下示例重新映射`/actuator/health`到`/healthcheck`：

**application.properties。** 

```bash
management.endpoints.web.base-path=/
management.endpoints.web.path-mapping.health=healthcheck
```



## 54.2定制管理服务器端口

对于基于云的部署，通过使用默认的HTTP端口公开管理端点是明智的选择。但是，如果您的应用程序在自己的数据中心内运行，则您可能更喜欢使用其他HTTP端口公开端点。

您可以设置该`management.server.port`属性以更改HTTP端口，如以下示例所示：

```bash
management.server.port=8081
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 在Cloud Foundry上，默认情况下，应用程序仅在端口8080上接收HTTP和TCP路由请求。如果要在Cloud Foundry上使用自定义管理端口，则需要明确设置应用程序的路由以将流量转发到自定义端口。 |

## 54.3配置特定于管理的SSL

当配置为使用自定义端口时，还可以通过使用各种`management.server.ssl.*`属性将管理服务器配置为其自己的SSL 。例如，这样做可以使管理服务器通过HTTP可用，而主应用程序使用HTTPS，如以下属性设置所示：

```bash
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:store.jks
server.ssl.key-password=secret
management.server.port=8080
management.server.ssl.enabled=false
```

或者，主服务器和管理服务器都可以使用SSL，但具有不同的密钥库，如下所示：

```bash
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:main.jks
server.ssl.key-password=secret
management.server.port=8080
management.server.ssl.enabled=true
management.server.ssl.key-store=classpath:management.jks
management.server.ssl.key-password=secret
```

## 54.4自定义管理服务器地址

您可以通过设置`management.server.address`属性来自定义管理端点可用的地址。如果您只想在内部或面向操作的网络上侦听或仅侦听来自的连接，则这样做很有用`localhost`。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 仅当端口与主服务器端口不同时，您才能在其他地址上侦听。       |

以下示例`application.properties`不允许远程管理连接：

```bash
management.server.port=8081
management.server.address=127.0.0.1
```

## 54.5禁用HTTP端点

如果您不想通过HTTP公开端点，则可以将管理端口设置为`-1`，如以下示例所示：

```bash
management.server.port=-1
```

也可以使用该`management.endpoints.web.exposure.exclude`属性来实现，如以下示例所示：

```bash
management.endpoints.web.exposure.exclude=*
```