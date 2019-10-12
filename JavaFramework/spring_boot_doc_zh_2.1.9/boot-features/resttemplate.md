# 35、RestTemplate

如果需要从应用程序调用远程REST服务，则可以使用Spring Framework的[`RestTemplate`](https://docs.spring.io/spring/docs/5.1.10.RELEASE/javadoc-api/org/springframework/web/client/RestTemplate.html)类。由于`RestTemplate`实例通常需要在使用前进行自定义，因此Spring Boot不提供任何单个自动配置的`RestTemplate`bean。但是，它会自动配置a `RestTemplateBuilder`，可以`RestTemplate`在需要时创建实例。自动配置`RestTemplateBuilder`可确保明智`HttpMessageConverters`地应用于`RestTemplate`实例。

以下代码显示了一个典型示例：

```java
@Service
public class MyService {

	private final RestTemplate restTemplate;

	public MyService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	public Details someRestCall(String name) {
		return this.restTemplate.getForObject("/{name}/details", Details.class, name);
	}

}
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| `RestTemplateBuilder`包括许多有用的方法，可用于快速配置`RestTemplate`。例如，要添加BASIC身份验证支持，可以使用`builder.basicAuthentication("user", "password").build()`。 |

## 35.1 RestTemplate自定义

有三种主要的`RestTemplate`自定义方法，具体取决于您希望自定义应用的范围。

为了使所有定制的范围尽可能狭窄，请注入自动配置的对象`RestTemplateBuilder`，然后根据需要调用其方法。每个方法调用都返回一个新`RestTemplateBuilder`实例，因此自定义仅影响构建器的使用。

要进行应用程序范围的附加自定义，请使用`RestTemplateCustomizer`Bean。所有此类bean都会自动向自动配置中注册，`RestTemplateBuilder`并应用于自动生成的任何模板。

以下示例显示了一个定制程序，该定制程序为除以下之外的所有主机配置代理的使用`192.168.0.5`：

```java
static class ProxyCustomizer implements RestTemplateCustomizer {

	@Override
	public void customize(RestTemplate restTemplate) {
		HttpHost proxy = new HttpHost("proxy.example.com");
		HttpClient httpClient = HttpClientBuilder.create().setRoutePlanner(new DefaultProxyRoutePlanner(proxy) {

			@Override
			public HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context)
					throws HttpException {
				if (target.getHostName().equals("192.168.0.5")) {
					return null;
				}
				return super.determineProxy(target, request, context);
			}

		}).build();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
	}

}
```

最后，最极端（很少使用）的选项是创建自己的`RestTemplateBuilder`bean。这样做会关闭a的自动配置，`RestTemplateBuilder`并防止使用任何`RestTemplateCustomizer`bean。