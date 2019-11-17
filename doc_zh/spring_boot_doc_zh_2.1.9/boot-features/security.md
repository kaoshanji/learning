# 30、安全性

如果[Spring Security](https://spring.io/projects/spring-security)在类路径上，则默认情况下Web应用程序是安全的。Spring Boot依靠Spring Security的内容协商策略来确定使用`httpBasic`还是`formLogin`。要将方法级安全性添加到Web应用程序，您还可以添加`@EnableGlobalMethodSecurity`所需的设置。可以在《[Spring Security参考指南》中](https://docs.spring.io/spring-security/site/docs/5.1.6.RELEASE/reference/htmlsingle/#jc-method)找到更多信息。

默认值`UserDetailsService`只有一个用户。用户名为`user`，密码为随机密码，并在应用程序启动时以INFO级别显示，如下例所示：

```bash
Using generated security password: 78fa095d-3f4c-48b1-ad50-e24c31d5cf35
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果您微调日志记录配置，请确保将`org.springframework.boot.autoconfigure.security`类别设置为日志`INFO`级别的消息。否则，不会打印默认密码。 |

您可以通过提供一个`spring.security.user.name`和来更改用户名和密码`spring.security.user.password`。

默认情况下，您在Web应用程序中获得的基本功能是：

- 一个具有内存存储的bean `UserDetailsService`（或`ReactiveUserDetailsService`WebFlux应用程序）和一个具有生成的密码的单个用户（请参阅参考资料[`SecurityProperties.User`](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/api/org/springframework/boot/autoconfigure/security/SecurityProperties.User.html)，获取用户的属性）。
- `Accept`整个应用程序的基于表单的登录或HTTP基本安全性（取决于请求中的标头）（如果执行器位于类路径上，则包括执行器端点）。
- 一个`DefaultAuthenticationEventPublisher`用于发布身份验证事件。

您可以`AuthenticationEventPublisher`通过添加一个bean 来提供不同的东西。

## 30.1 MVC安全性

默认的安全配置在`SecurityAutoConfiguration`和中实现`UserDetailsServiceAutoConfiguration`。 `SecurityAutoConfiguration`导入`SpringBootWebSecurityConfiguration`Web安全性并`UserDetailsServiceAutoConfiguration`配置身份验证，这在非Web应用程序中也很重要。要完全关闭默认的Web应用程序安全性配置，可以添加一个类型的bean `WebSecurityConfigurerAdapter`（这样做不会禁用`UserDetailsService`配置或执行器的安全性）。

为了还关闭`UserDetailsService`的配置，您可以添加类型的豆`UserDetailsService`，`AuthenticationProvider`或`AuthenticationManager`。[Spring Boot示例中](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-samples/)有几个安全应用程序，可以帮助您开始使用常见的用例。

可以通过添加自定义来覆盖访问规则`WebSecurityConfigurerAdapter`。Spring Boot提供了方便的方法，可用于覆盖执行器端点和静态资源的访问规则。 `EndpointRequest`可用于创建`RequestMatcher`基于`management.endpoints.web.base-path`属性的。 `PathRequest`可用于`RequestMatcher`在常用位置创建for资源。

## 30.2 WebFlux安全性

与Spring MVC应用程序类似，您可以通过添加`spring-boot-starter-security`依赖项来保护WebFlux应用程序。默认的安全配置在`ReactiveSecurityAutoConfiguration`和中实现`UserDetailsServiceAutoConfiguration`。 `ReactiveSecurityAutoConfiguration`导入`WebFluxSecurityConfiguration`Web安全性并`UserDetailsServiceAutoConfiguration`配置身份验证，这在非Web应用程序中也很重要。要完全关闭默认的Web应用程序安全性配置，可以添加一个类型的bean `WebFilterChainProxy`（这样做不会禁用`UserDetailsService`配置或执行器的安全性）。

要也关闭`UserDetailsService`配置，可以添加类型为`ReactiveUserDetailsService`或的Bean `ReactiveAuthenticationManager`。

可以通过添加自定义配置访问规则`SecurityWebFilterChain`。Spring Boot提供了方便的方法，可用于覆盖执行器端点和静态资源的访问规则。 `EndpointRequest`可用于创建`ServerWebExchangeMatcher`基于`management.endpoints.web.base-path`属性的。

`PathRequest`可用于`ServerWebExchangeMatcher`在常用位置创建for资源。

例如，您可以通过添加以下内容来自定义安全配置：

```java
@Bean
public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
	return http
		.authorizeExchange()
			.matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
			.pathMatchers("/foo", "/bar")
				.authenticated().and()
			.formLogin().and()
		.build();
}
```

## 30.3 OAuth2

[OAuth2](https://oauth.net/2/)是Spring支持的一种广泛使用的授权框架。

### 30.3.1客户端

如果您`spring-security-oauth2-client`在类路径中，则可以利用一些自动配置功能来轻松设置OAuth2 / Open ID Connect客户端。此配置使用的属性`OAuth2ClientProperties`。相同的属性适用于servlet和反应式应用程序。

您可以在`spring.security.oauth2.client`前缀下注册多个OAuth2客户端和提供者，如以下示例所示：

```bash
spring.security.oauth2.client.registration.my-client-1.client-id=abcd
spring.security.oauth2.client.registration.my-client-1.client-secret=password
spring.security.oauth2.client.registration.my-client-1.client-name=Client for user scope
spring.security.oauth2.client.registration.my-client-1.provider=my-oauth-provider
spring.security.oauth2.client.registration.my-client-1.scope=user
spring.security.oauth2.client.registration.my-client-1.redirect-uri-template=https://my-redirect-uri.com
spring.security.oauth2.client.registration.my-client-1.client-authentication-method=basic
spring.security.oauth2.client.registration.my-client-1.authorization-grant-type=authorization_code

spring.security.oauth2.client.registration.my-client-2.client-id=abcd
spring.security.oauth2.client.registration.my-client-2.client-secret=password
spring.security.oauth2.client.registration.my-client-2.client-name=Client for email scope
spring.security.oauth2.client.registration.my-client-2.provider=my-oauth-provider
spring.security.oauth2.client.registration.my-client-2.scope=email
spring.security.oauth2.client.registration.my-client-2.redirect-uri-template=https://my-redirect-uri.com
spring.security.oauth2.client.registration.my-client-2.client-authentication-method=basic
spring.security.oauth2.client.registration.my-client-2.authorization-grant-type=authorization_code

spring.security.oauth2.client.provider.my-oauth-provider.authorization-uri=http://my-auth-server/oauth/authorize
spring.security.oauth2.client.provider.my-oauth-provider.token-uri=http://my-auth-server/oauth/token
spring.security.oauth2.client.provider.my-oauth-provider.user-info-uri=http://my-auth-server/userinfo
spring.security.oauth2.client.provider.my-oauth-provider.user-info-authentication-method=header
spring.security.oauth2.client.provider.my-oauth-provider.jwk-set-uri=http://my-auth-server/token_keys
spring.security.oauth2.client.provider.my-oauth-provider.user-name-attribute=name
```

对于支持[OpenID Connect发现的](https://openid.net/specs/openid-connect-discovery-1_0.html) OpenID Connect提供程序，可以进一步简化配置。提供者需要配置为，`issuer-uri`后者是它声明为其发布者标识符的URI。例如，如果`issuer-uri`提供的是“ https://example.com”，则将`OpenID Provider Configuration Request`对“ https://example.com/.well-known/openid-configuration”进行标记。结果预期为`OpenID Provider Configuration Response`。以下示例显示如何使用来配置OpenID Connect提供程序`issuer-uri`：

```bash
spring.security.oauth2.client.provider.oidc-provider.issuer-uri=https://dev-123456.oktapreview.com/oauth2/default/
```

默认情况下，Spring Security `OAuth2LoginAuthenticationFilter`只处理匹配的URL `/login/oauth2/code/*`。如果要自定义，`redirect-uri`以使用其他模式，则需要提供配置以处理该自定义模式。例如，对于Servlet应用程序，您可以添加`WebSecurityConfigurerAdapter`类似于以下内容的自己的应用程序：

```
public class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.anyRequest().authenticated()
				.and()
			.oauth2Login()
				.redirectionEndpoint()
					.baseUri("/custom-callback");
	}
}
```

#### 普通提供商的OAuth2客户端注册

对于常见的OAuth2和OpenID提供商，包括谷歌，Github上，Facebook和1563，我们提供了一组供应商默认的（`google`，`github`，`facebook`，和`okta`，分别）。

如果不需要自定义这些提供程序，则可以将`provider`属性设置为需要为其推断默认值的属性。另外，如果用于客户端注册的密钥与默认支持的提供程序匹配，则Spring Boot也会进行推断。

换句话说，以下示例中的两个配置都使用Google提供程序：

```
spring.security.oauth2.client.registration.my-client.client-id=abcd
spring.security.oauth2.client.registration.my-client.client-secret=password
spring.security.oauth2.client.registration.my-client.provider=google

spring.security.oauth2.client.registration.google.client-id=abcd
spring.security.oauth2.client.registration.google.client-secret=password
```

### 30.3.2资源服务器

如果您`spring-security-oauth2-resource-server`在类路径中，则只要指定了JWK Set URI或OIDC Issuer URI，Spring Boot就可以设置OAuth2资源服务器，如以下示例所示：

```bash
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://example.com/oauth2/default/v1/keys
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://dev-123456.oktapreview.com/oauth2/default/
```

相同的属性适用于servlet和反应式应用程序。

另外，您可以`JwtDecoder`为Servlet应用程序或`ReactiveJwtDecoder`响应式应用程序定义自己的bean 。

### 30.3.3授权服务器

当前，Spring Security不提供对实现OAuth 2.0授权服务器的支持。但是，[Spring Security OAuth](https://spring.io/projects/spring-security-oauth)项目提供了此功能，最终将被Spring Security完全取代。在此之前，您可以使用该`spring-security-oauth2-autoconfigure`模块轻松设置OAuth 2.0授权服务器；有关说明，请参见其[文档](https://docs.spring.io/spring-security-oauth2-boot)。

## 30.4执行器安全

为了安全起见，默认情况下，除`/health`和以外的所有执行器`/info`都是禁用的。该`management.endpoints.web.exposure.include`属性可用于启用执行器。

如果春季安全是在类路径上，并没有其他WebSecurityConfigurerAdapter存在，比其他所有的驱动器`/health`，并`/info`通过春天开机自动配置安全。如果您定义一个custom `WebSecurityConfigurerAdapter`，Spring Boot自动配置将退出，您将完全控制执行器访问规则。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 在设置之前`management.endpoints.web.exposure.include`，请确保裸露的执行器不包含敏感信息和/或通过将它们放置在防火墙后面或通过诸如Spring Security之类的东西进行保护。 |

### 30.4.1跨站点请求伪造保护

由于Spring Boot依赖于Spring Security的默认值，因此默认情况下CSRF保护是打开的。这意味着在使用默认安全性配置时，要求`POST`（关闭和记录器端点）的执行器端点`PUT`或`DELETE`将收到403禁止错误。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 我们建议仅在创建非浏览器客户端使用的服务时完全禁用CSRF保护。 |

关于CSRF保护的其他信息可以在[Spring Security Reference Guide中找到](https://docs.spring.io/spring-security/site/docs/5.1.6.RELEASE/reference/htmlsingle/#csrf)。