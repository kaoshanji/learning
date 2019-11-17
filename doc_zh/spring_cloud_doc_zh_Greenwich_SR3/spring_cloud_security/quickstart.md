# 81. Quickstart

## 81.快速入门

## 81.1 OAuth2单点登录

这是一个具有HTTP Basic身份验证和单个用户帐户的Spring Cloud“ Hello World”应用程序：

**app.groovy。** 

```java
@Grab('spring-boot-starter-security')
@Controller
class Application {

  @RequestMapping('/')
  String home() {
    'Hello World'
  }

}
```



您可以使用它运行`spring run app.groovy`并查看日志中的密码（用户名是“ user”）。到目前为止，这只是Spring Boot应用程序的默认设置。

这是带有OAuth2 SSO的Spring Cloud应用程序：

**app.groovy。** 

```java
@Controller
@EnableOAuth2Sso
class Application {

  @RequestMapping('/')
  String home() {
    'Hello World'
  }

}
```



指出不同？该应用程序实际上与上一个应用程序的行为完全相同，因为它尚不知道它是OAuth2凭据。

您可以很容易地在github中注册一个应用程序，因此如果您想在自己的域上使用生产应用程序，请尝试使用该应用程序。如果您愿意在localhost：8080上进行测试，请在应用程序配置中设置以下属性：

**application.yml。** 

```properties
security:
  oauth2:
    client:
      clientId: bd1c0a783ccdd1c9b9e4
      clientSecret: 1a9030fbca47a5b2c28e92f19050bb77824b5ad1
      accessTokenUri: https://github.com/login/oauth/access_token
      userAuthorizationUri: https://github.com/login/oauth/authorize
      clientAuthenticationScheme: form
    resource:
      userInfoUri: https://api.github.com/user
      preferTokenInfo: false
```



运行上面的应用程序，它将重定向到github进行授权。如果您已经登录github，您甚至不会注意到它已通过身份验证。仅当您的应用程序在端口8080上运行时，这些凭据才有效。

要限制客户端在获取访问令牌时要求的范围，可以设置`security.oauth2.client.scope`（逗号分隔或YAML中的数组）。默认情况下，作用域为空，并且由授权服务器决定默认值是什么，通常取决于它所拥有的客户端注册中的设置。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 上面的示例都是Groovy脚本。如果要用Java（或Groovy）编写相同的代码，则需要将Spring Security OAuth2添加到类路径中（例如，在[此处](https://github.com/spring-cloud-samples/sso)查看 [示例](https://github.com/spring-cloud-samples/sso)）。 |

## 81.2 OAuth2受保护的资源

您想使用OAuth2令牌保护API资源吗？这是一个简单的示例（与上面的客户端配对）：

**app.groovy。** 

```java
@Grab('spring-cloud-starter-security')
@RestController
@EnableResourceServer
class Application {

  @RequestMapping('/')
  def home() {
    [message: 'Hello World']
  }

}
```



和

**application.yml。** 

```properties
security:
  oauth2:
    resource:
      userInfoUri: https://api.github.com/user
      preferTokenInfo: false
```