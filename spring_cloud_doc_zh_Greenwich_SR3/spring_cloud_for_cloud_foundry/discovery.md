# 84. Discovery

## 84.发现

这是带有Cloud Foundry发现的Spring Cloud应用程序：

**app.groovy。** 

```java
@Grab('org.springframework.cloud:spring-cloud-cloudfoundry')
@RestController
@EnableDiscoveryClient
class Application {

  @Autowired
  DiscoveryClient client

  @RequestMapping('/')
  String home() {
    'Hello from ' + client.getLocalServiceInstance()
  }

}
```



如果运行时没有任何服务绑定：

```bash
$ spring jar app.jar app.groovy
$ cf push -p app.jar
```

它将在首页中显示其应用名称。

所述`DiscoveryClient`罐中列出了所有的应用程序中的空间中，根据它进行验证的凭证，其中，所述空间默认为一个客户机正在运行（如果有的话）。如果未配置组织和空间，则它们将根据Cloud Foundry中用户的配置文件默认设置。