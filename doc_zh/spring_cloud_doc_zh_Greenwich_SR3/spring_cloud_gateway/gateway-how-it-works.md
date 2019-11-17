# 111. How It Works

## 111.工作原理

![Spring Cloud网关图](../images/spring_cloud_gateway_diagram.png)

客户端向Spring Cloud Gateway发出请求。如果网关处理程序映射确定请求与路由匹配，则将其发送到网关Web处理程序。该处理程序运行通过特定于请求的筛选器链发送请求。筛选器由虚线分隔的原因是，筛选器可以在发送代理请求之前或之后执行逻辑。执行所有“前置”过滤器逻辑，然后发出代理请求。发出代理请求后，将执行“发布”过滤器逻辑。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 在没有端口的路由中定义的URI将分别将HTTP和HTTPS URI的默认端口分别设置为80和443。 |