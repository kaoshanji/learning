# 62. Sending Spans to Zipkin

## 62.将跨度发送到Zipkin

默认情况下，如果您添加`spring-cloud-starter-zipkin`为项目的依赖项，则关闭跨度后，跨度将通过HTTP发送到Zipkin。通信是异步的。您可以通过设置`spring.zipkin.baseUrl`属性来配置URL ，如下所示：

```properties
spring.zipkin.baseUrl: https://192.168.99.100:9411/
```

如果要通过服务发现找到Zipkin，可以在URL内传递Zipkin的服务ID，如以下`zipkinserver`服务ID的示例所示：

```properties
spring.zipkin.baseUrl: http://zipkinserver/
```

要禁用此功能，只需将其设置`spring.zipkin.discoveryClientEnabled`为“ false”即可。

启用发现客户端功能后，Sleuth会使用它 `LoadBalancerClient`来查找Zipkin服务器的URL。这意味着您可以例如通过功能区来设置负载平衡配置。

```properties
zipkinserver:
  ribbon:
    ListOfServers: host1,host2
```

如果您在类路径中同时使用了web，rabbit或kafka，则可能需要选择将跨度发送到zipkin的方法。要做到这一点，设置`web`，`rabbit`或`kafka`在`spring.zipkin.sender.type`财产。以下示例显示了如何设置发件人类型`web`：

```properties
spring.zipkin.sender.type: web
```

要自定义`RestTemplate`通过HTTP发送跨度到Zipkin的，您可以注册`ZipkinRestTemplateCustomizer`Bean。

```java
@Configuration
class MyConfig {
	@Bean ZipkinRestTemplateCustomizer myCustomizer() {
		return new ZipkinRestTemplateCustomizer() {
			@Override
			void customize(RestTemplate restTemplate) {
				// customize the RestTemplate
			}
		};
	}
}
```

但是，如果您想控制创建`RestTemplate` 对象的整个过程，则必须创建一个`zipkin2.reporter.Sender`类型的bean 。

```java
	@Bean Sender myRestTemplateSender(ZipkinProperties zipkin,
			ZipkinRestTemplateCustomizer zipkinRestTemplateCustomizer) {
		RestTemplate restTemplate = mySuperCustomRestTemplate();
		zipkinRestTemplateCustomizer.customize(restTemplate);
		return myCustomSender(zipkin, restTemplate);
	}
```