# 57. Instrumentation

## 57.仪器仪表

Spring Cloud Sleuth会自动检测所有Spring应用程序，因此您无需执行任何操作即可激活它。通过根据可用的堆栈使用多种技术来添加检测。例如，对于servlet Web应用程序，我们使用`Filter`，对于Spring Integration，我们使用`ChannelInterceptors`。

您可以自定义跨度标签中使用的键。为了限制范围数据的数量，默认情况下，HTTP请求仅使用少量元数据（例如状态码，主机和URL）进行标记。您可以通过配置`spring.sleuth.keys.http.headers`（标头名称列表）添加请求标头。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 标签只有在`Sampler`允许的情况下才被收集和导出。默认情况下，没有这样的方法`Sampler`来确保不存在不进行任何配置而意外收集过多数据的危险。 |