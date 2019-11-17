# 85. Single Sign On

## 85.单点登录

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 所有OAuth2 SSO和资源服务器功能已在1.3版中移至Spring Boot。您可以在[Spring Boot用户指南中](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)找到文档 。 |

该项目提供了从CloudFoundry服务凭据到Spring Boot功能的自动绑定。例如，如果您有一个名为“ sso”的CloudFoundry服务，其凭证包含“ client_id”，“ client_secret”和“ auth_domain”，则该服务将自动绑定到您启用的Spring OAuth2客户端 `@EnableOAuth2Sso`（从Spring Boot）。可以使用来参数化服务的名称`spring.oauth2.sso.serviceId`。