# 61. Customizations

## 61.定制

## 61.1定制器

使用Brave 5.7，您可以使用多种选项为项目提供定制程序。勇敢与

- `TracingCustomizer`-允许配置插件协作构建的实例`Tracing`。
- `CurrentTraceContextCustomizer`-允许配置插件协作构建的实例`CurrentTraceContext`。
- `ExtraFieldCustomizer`-允许配置插件协作构建的实例`ExtraFieldPropagation.Factory`。

Sleuth将搜索那些类型的bean并自动应用定制。

## 61.2 HTTP

如果需要的HTTP相关跨度的客户机/服务器解析的定制，只登记类型的豆`brave.http.HttpClientParser`或 `brave.http.HttpServerParser`。如果需要客户机/服务器采样，则只需注册一个类型为Bean的bean，`brave.http.HttpSampler`并将其命名 `sleuthClientSampler`为客户机采样器和`sleuthServerSampler`服务器采样器。为方便起见，`@ClientSampler`和`@ServerSampler` 注释可用于注入适当的bean或通过其静态String `NAME`字段引用bean名称。

查阅Brave的代码，以查看如何制作基于路径的采样器的示例 https://github.com/openzipkin/brave/tree/master/instrumentation/http#sampling-policy

如果要完全重写`HttpTracing`Bean，则可以使用该`SkipPatternProvider` 接口检索`Pattern`不应采样的范围的URL 。在下面，您可以看到`SkipPatternProvider`在服务器端内部使用示例`HttpSampler`。

```java
@Configuration
class Config {
  @Bean(name = ServerSampler.NAME)
  HttpSampler myHttpSampler(SkipPatternProvider provider) {
  	Pattern pattern = provider.skipPattern();
  	return new HttpSampler() {

  		@Override
  		public <Req> Boolean trySample(HttpAdapter<Req, ?> adapter, Req request) {
  			String url = adapter.path(request);
  			boolean shouldSkip = pattern.matcher(url).matches();
  			if (shouldSkip) {
  				return false;
  			}
  			return null;
  		}
  	};
  }
}
```

## 61.3 `TracingFilter`

您还可以修改的行为，该`TracingFilter`组件负责处理输入的HTTP请求并基于HTTP响应添加标签。您可以通过注册自己的`TracingFilter`Bean 实例来自定义标签或修改响应头。

在下面的示例中，我们注册`TracingFilter`Bean，添加`ZIPKIN-TRACE-ID`包含当前Span的跟踪ID 的响应标头，并向范围中添加包含key `custom`和value 的标签`tag`。

```java
@Component
@Order(TraceWebServletAutoConfiguration.TRACING_FILTER_ORDER + 1)
class MyFilter extends GenericFilterBean {

	private final Tracer tracer;

	MyFilter(Tracer tracer) {
		this.tracer = tracer;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Span currentSpan = this.tracer.currentSpan();
		if (currentSpan == null) {
			chain.doFilter(request, response);
			return;
		}
		// for readability we're returning trace id in a hex form
		((HttpServletResponse) response).addHeader("ZIPKIN-TRACE-ID",
				currentSpan.context().traceIdString());
		// we can also add some custom tags
		currentSpan.tag("custom", "tag");
		chain.doFilter(request, response);
	}

}
```

## 61.4定制服务名称

默认情况下，Sleuth假定，当您向Zipkin发送跨度时，您希望跨度的服务名称等于该`spring.application.name`属性的值。但是，并非总是如此。在某些情况下，您想为来自应用程序的所有范围显式提供不同的服务名称。为此，您可以将以下属性传递给应用程序以覆盖该值（该示例适用于名为的服务`myService`）：

```properties
spring.zipkin.service.name: myService
```

## 61.5自定义报告的跨度

在报告跨度之前（例如，向Zipkin报告），您可能需要以某种方式修改该跨度。您可以通过使用`FinishedSpanHandler`界面来实现。

在Sleuth中，我们生成具有固定名称的跨度。一些用户希望根据标签的值来修改名称。您可以实现`FinishedSpanHandler`接口来更改该名称。

以下示例说明如何注册实现的两个bean `FinishedSpanHandler`：

```java
@Bean
FinishedSpanHandler handlerOne() {
	return new FinishedSpanHandler() {
		@Override
		public boolean handle(TraceContext traceContext, MutableSpan span) {
			span.name("foo");
			return true; // keep this span
		}
	};
}

@Bean
FinishedSpanHandler handlerTwo() {
	return new FinishedSpanHandler() {
		@Override
		public boolean handle(TraceContext traceContext, MutableSpan span) {
			span.name(span.name() + " bar");
			return true; // keep this span
		}
	};
}
```

前面的示例导致在报告报告跨度`foo bar`之前将其名称更改为，例如（更改为Zipkin）。

## 61.6主机定位器

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 本节是关于通过服务发现定义**主机**。这是**不是**有关通过服务发现找到基普金。 |      |

要定义与特定跨度相对应的主机，我们需要解析主机名和端口。默认方法是从服务器属性中获取这些值。如果未设置，则尝试从网络接口检索主机名。

如果启用了发现客户端，并且希望从服务注册表中的注册实例中检索主机地址，则必须设置`spring.zipkin.locator.discovery.enabled`属性（该属性适用于基于HTTP和基于流的跨度报告），如下所示：

```properties
spring.zipkin.locator.discovery.enabled: true
```