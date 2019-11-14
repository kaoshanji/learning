# 121. Developer Guide

## 121.开发人员指南

TODO：编写定制集成概述

## 121.1编写自定义路由谓词工厂

TODO：编写自定义路线谓词工厂的文档

## 121.2编写自定义GatewayFilter工厂

为了编写GatewayFilter，您需要实现`GatewayFilterFactory`。有一个`AbstractGatewayFilterFactory`可以扩展的抽象类。

**PreGatewayFilterFactory.java。** 

```java
public class PreGatewayFilterFactory extends AbstractGatewayFilterFactory<PreGatewayFilterFactory.Config> {

	public PreGatewayFilterFactory() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		// grab configuration from Config object
		return (exchange, chain) -> {
            //If you want to build a "pre" filter you need to manipulate the
            //request before calling chain.filter
            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
            //use builder to manipulate the request
            return chain.filter(exchange.mutate().request(request).build());
		};
	}

	public static class Config {
        //Put the configuration properties for your filter here
	}

}
```



**PostGatewayFilterFactory.java。** 

```java
public class PostGatewayFilterFactory extends AbstractGatewayFilterFactory<PostGatewayFilterFactory.Config> {

	public PostGatewayFilterFactory() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		// grab configuration from Config object
		return (exchange, chain) -> {
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				ServerHttpResponse response = exchange.getResponse();
				//Manipulate the response in some way
			}));
		};
	}

	public static class Config {
        //Put the configuration properties for your filter here
	}

}
```



## 121.3编写自定义全局过滤器

为了编写自定义全局过滤器，您将需要实现`GlobalFilter`接口。这会将过滤器应用于所有请求。

如何分别设置全局前置和后置过滤器的示例

```java
@Bean
public GlobalFilter customGlobalFilter() {
    return (exchange, chain) -> exchange.getPrincipal()
        .map(Principal::getName)
        .defaultIfEmpty("Default User")
        .map(userName -> {
          //adds header to proxied request
          exchange.getRequest().mutate().header("CUSTOM-REQUEST-HEADER", userName).build();
          return exchange;
        })
        .flatMap(chain::filter);
}

@Bean
public GlobalFilter customGlobalPostFilter() {
    return (exchange, chain) -> chain.filter(exchange)
        .then(Mono.just(exchange))
        .map(serverWebExchange -> {
          //adds header to response
          serverWebExchange.getResponse().getHeaders().set("CUSTOM-RESPONSE-HEADER",
              HttpStatus.OK.equals(serverWebExchange.getResponse().getStatusCode()) ? "It worked": "It did not work");
          return serverWebExchange;
        })
        .then();
}
```

## 121.4编写自定义路线定位器和编写器

TODO：编写自定义路线定位器和编写器的文档