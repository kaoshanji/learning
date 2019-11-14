# 43. Bus Endpoints

## 43.总线端点

春云总线提供了两个端点，`/actuator/bus-refresh`以及`/actuator/bus-env` 对应于个体传动端点春季云共享， `/actuator/refresh`并`/actuator/env`分别。

## 43.1总线刷新端点

该`/actuator/bus-refresh`端点清除`RefreshScope`缓存和重新绑定 `@ConfigurationProperties`。有关更多信息，请参见[刷新作用域](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_context_application_context_services.html#refresh-scope)文档。

要公开`/actuator/bus-refresh`端点，您需要在应用程序中添加以下配置：

```properties
management.endpoints.web.exposure.include=bus-refresh
```

## 43.2总线环境端点

的`/actuator/bus-env`端点更新与在多个实例中的指定键/值对每个实例的环境。

要公开`/actuator/bus-env`端点，您需要在应用程序中添加以下配置：

```
management.endpoints.web.exposure.include =总线环境
```

该`/actuator/bus-env`端点接受`POST`具有以下形状的要求：

```json
{
	"name": "key1",
	"value": "value1"
}
```