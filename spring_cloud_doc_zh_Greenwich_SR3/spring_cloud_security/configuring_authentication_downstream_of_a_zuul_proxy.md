# 83. Configuring Authentication Downstream of a Zuul Proxy

## 83.配置Zuul代理的下游身份验证

您可以`@EnableZuulProxy`通过`proxy.auth.*`设置控制授权行为的下游 。例：

**application.yml。** 

```properties
proxy:
  auth:
    routes:
      customers: oauth2
      stores: passthru
      recommendations: none
```



在此示例中，“客户”服务获取OAuth2令牌中继，“商店”服务获取直通（授权标头仅向下游传递），“推荐”服务删除其授权标头。默认行为是在有令牌可用时进行令牌中继，否则通过。

有关完整的详细信息，请参见 [ProxyAuthenticationProperties](https://github.com/spring-cloud/spring-cloud-security/tree/master/src/main/java/org/springframework/cloud/security/oauth2/proxy/ProxyAuthenticationProperties)。