# 82. More Detail

## 82.更多细节

## 82.1单点登录

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 所有OAuth2 SSO和资源服务器功能已在1.3版中移至Spring Boot。您可以在[Spring Boot用户指南中](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)找到文档 。 |

## 82.2令牌中继

令牌中继是OAuth2使用者充当客户端，并将传入令牌转发到传出资源请求的地方。使用者可以是纯客户端（如SSO应用程序）或资源服务器。

### 82.2.1 Spring Cloud Gateway中的客户端令牌中继

如果您的应用程序还具有 [Spring Cloud Gateway](https://cloud.spring.io/spring-cloud-static/current/single/spring-cloud.html#_spring_cloud_gateway)嵌入式反向代理，则可以要求它向下游转发OAuth2访问令牌到它正在代理的服务。因此，可以像下面这样简单地增强上面的SSO应用程序：

**App.java。** 

```
@Autowired
private TokenRelayGatewayFilterFactory filterFactory;

@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder.routes()
            .route("resource", r -> r.path("/resource")
                    .filters(f -> f.filter(filterFactory.apply()))
                    .uri("http://localhost:9000"))
            .build();
}
```



或这个

**application.yaml。** 

```properties
spring:
  cloud:
    gateway:
      routes:
      - id: resource
        uri: http://localhost:9000
        predicates:
        - Path=/resource
        filters:
        - TokenRelay=
```



并且它将（除了登录用户并获取令牌之外）还将身份验证令牌传递给服务（在这种情况下 `/resource`）。

要为Spring Cloud Gateway启用此功能，请添加以下依赖项

- `org.springframework.boot:spring-boot-starter-oauth2-client`
- `org.springframework.cloud:spring-cloud-starter-security`

它是如何工作的？该 [滤波器](https://github.com/spring-cloud/spring-cloud-security/tree/master/src/main/java/org/springframework/cloud/security/oauth2/gateway/TokenRelayGatewayFilterFactory.java) 提取用于下游请求从当前认证的用户的访问令牌，并把它在请求报头。

有关完整的工作示例，请参见此[项目](https://github.com/spring-cloud-samples/sample-gateway-oauth2login)。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 使用的默认实现`ReactiveOAuth2AuthorizedClientService`使用`TokenRelayGatewayFilterFactory` 内存中的数据存储。`ReactiveOAuth2AuthorizedClientService` 如果需要更强大的解决方案，则需要提供自己的实现。 |

### 82.2.2客户端令牌中继

如果您的应用是面向OAuth2客户端的用户（即已声明 `@EnableOAuth2Sso`或`@EnableOAuth2Client`），则它具有`OAuth2ClientContext`Spring Boot 的 in请求范围。您可以`OAuth2RestTemplate`通过此上下文和autowired 创建自己的上下文`OAuth2ProtectedResourceDetails`，然后上下文将始终将访问令牌转发到下游，如果过期，访问令牌也会自动刷新。（这些是Spring Security和Spring Boot的功能。）

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| `OAuth2ProtectedResourceDetails`如果您使用`client_credentials`令牌，Spring Boot（1.4.1）不会自动 创建 。在这种情况下，您需要创建自己的 `ClientCredentialsResourceDetails`并使用进行配置 `@ConfigurationProperties("security.oauth2.client")`。 |

### 82.2.3 Zuul代理中的客户端令牌中继

如果您的应用程序还具有 [Spring Cloud Zuul](https://cloud.spring.io/spring-cloud.html#netflix-zuul-reverse-proxy)嵌入式反向代理（使用`@EnableZuulProxy`），则可以要求它向下游转发OAuth2访问令牌到它正在代理的服务。因此，可以像下面这样简单地增强上面的SSO应用程序：

**app.groovy。** 

```java
@Controller
@EnableOAuth2Sso
@EnableZuulProxy
class Application {

}
```



它将（除了登录用户并获取令牌外）还将身份验证令牌传递到`/proxy/*` 服务下游。如果使用这些服务实现， `@EnableResourceServer`则它们将在正确的标头中获得有效的令牌。

它是如何工作的？将`@EnableOAuth2Sso`在注释拉 `spring-cloud-starter-security`（你可以做手工在传统的应用程序），而这又引发了一些自动配置的`ZuulFilter`，其本身被激活，因为Zuul是在类路径中（通过`@EnableZuulProxy`）。该 [滤波器](https://github.com/spring-cloud/spring-cloud-security/tree/master/src/main/java/org/springframework/cloud/security/oauth2/proxy/OAuth2TokenRelayFilter.java) 只提取用于下游请求从当前认证的用户的访问令牌，并把它在请求报头。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| Spring Boot不会`OAuth2RestOperations`自动创建所需的`refresh_token`。在这种情况下，您需要创建自己 的令牌，`OAuth2RestOperations`因此`OAuth2TokenRelayFilter`可以根据需要刷新令牌。 |

### 82.2.4资源服务器令牌中继

如果您的应用程序有，`@EnableResourceServer`您可能希望将传入令牌下游中继到其他服务。如果您使用 `RestTemplate`来联系下游服务，那么这只是如何在正确的上下文中创建模板的问题。

如果您的服务用于`UserInfoTokenServices`对传入令牌进行身份验证（即它正在使用`security.oauth2.user-info-uri` 配置），那么您可以简单地`OAuth2RestTemplate` 使用自动装配创建一个`OAuth2ClientContext`（将在到达后端代码之前由身份验证过程填充）。同样（对于Spring Boot 1.4），您可以在配置中注入a `UserInfoRestTemplateFactory`并获取它`OAuth2RestTemplate`。例如：

**MyConfiguration.java。** 

```java
@Bean
public OAuth2RestTemplate restTemplate(UserInfoRestTemplateFactory factory) {
    return factory.getUserInfoRestTemplate();
}
```



这样，该剩余模板将具有与`OAuth2ClientContext` 身份验证过滤器相同的（请求范围），因此您可以使用它来发送具有相同访问令牌的请求。

如果您的应用未使用`UserInfoTokenServices`但仍是客户端（即，它声明`@EnableOAuth2Client`或`@EnableOAuth2Sso`），则使用Spring Security Cloud `OAuth2RestOperations`，用户从中创建的任何应用`@Autowired` `OAuth2Context`还将转发令牌。默认情况下，此功能作为MVC处理程序拦截器实现，因此仅在Spring MVC中有效。如果您不使用MVC，则可以使用包装了的自定义过滤器或AOP拦截器 `AccessTokenContextRelay`来提供相同的功能。

这是一个基本示例，展示了如何使用在其他位置创建的自动关联的休息模板（“ foo.com”是接受与周围应用程序相同的令牌的资源服务器）：

**MyController.java。** 

```java
@Autowired
private OAuth2RestOperations restTemplate;

@RequestMapping("/relay")
public String relay() {
    ResponseEntity<String> response =
      restTemplate.getForEntity("https://foo.com/bar", String.class);
    return "Success! (" + response.getBody() + ")";
}
```



如果您不希望转发令牌（这是一个有效的选择，因为您可能想扮演自己的角色，而不是向您发送令牌的客户端），那么您只需要创建自己的令牌， `OAuth2Context`而不是自动装配默认值一。

伪装的客户还将选择使用`OAuth2ClientContext`if 的拦截器（ 如果可用），因此他们还应在`RestTemplate`会的任何地方进行令牌中继。