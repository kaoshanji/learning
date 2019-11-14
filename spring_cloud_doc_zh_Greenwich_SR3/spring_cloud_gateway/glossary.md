# 110. Glossary

## 110.词汇

- **路由**：路由网关的基本构建块。它由ID，目标URI，谓词集合和过滤器集合定义。如果聚合谓词为true，则匹配路由。
- **谓词**：这是[ Java 8 Function谓词](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html)。输入类型是[ Spring Framework`ServerWebExchange`](https://docs.spring.io/spring/docs/5.0.x/javadoc-api/org/springframework/web/server/ServerWebExchange.html)。这使开发人员可以匹配HTTP请求中的所有内容，例如标头或参数。
- **Filter**：这些是使用特定工厂构建的[ Spring Framework`GatewayFilter`](https://docs.spring.io/spring/docs/5.0.x/javadoc-api/org/springframework/web/server/GatewayFilter.html)实例。在此，可以在发送下游请求之前或之后修改请求和响应。