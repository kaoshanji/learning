# 37. Metrics Emitter

## 37.指标发射器

Spring Boot Actuator为[Micrometer](https://micrometer.io/)提供了依赖项管理和自动配置，[Micrometer](https://micrometer.io/)是一种支持多种[监视系统](https://docs.spring.io/spring-boot/docs/2.0.0.RELEASE/reference/htmlsingle/#production-ready-metrics)的应用程序指标外观。

Spring Cloud Stream提供了将任何可用的基于千分尺的度量标准发送到绑定目标的支持，从而允许从流应用程序定期收集度量标准数据，而无需依赖于轮询各个端点。

通过定义该`spring.cloud.stream.bindings.applicationMetrics.destination`属性来激活“度量标准发射器”，该属性指定当前绑定程序用于发布度量标准消息的绑定目标的名称。

例如：

```properties
spring.cloud.stream.bindings.applicationMetrics.destination=myMetricDestination
```

前面的示例指示绑定程序进行绑定`myMetricDestination`（即Rabbit交换，Kafka主题等）。

以下属性可用于自定义指标的发出：

- spring.cloud.stream.metrics.key

  发出的度量标准的名称。每个应用程序应为唯一值。默认： `${spring.application.name:${vcap.application.name:${spring.config.name:application}}}`

- spring.cloud.stream.metrics.properties

  允许白名单应用程序属性添加到度量有效负载默认值：null。

- spring.cloud.stream.metrics.meter-filter

  控制要捕获的“仪表”的模式。例如，指定`spring.integration.*`名称为名称开头的仪表的捕获度量信息`spring.integration.`默认值：捕获所有“仪表”。

- spring.cloud.stream.metrics.schedule-interval

  控制发布度量标准数据的速率的时间间隔。默认值：1分钟

考虑以下：

```bash
java -jar time-source.jar \
    --spring.cloud.stream.bindings.applicationMetrics.destination=someMetrics \
    --spring.cloud.stream.metrics.properties=spring.application** \
    --spring.cloud.stream.metrics.meter-filter=spring.integration.*
```

下面的示例显示由于上述命令而发布到绑定目标的数据的有效负载：

```json
{
	"name": "application",
	"createdTime": "2018-03-23T14:48:12.700Z",
	"properties": {
	},
	"metrics": [
		{
			"id": {
				"name": "spring.integration.send",
				"tags": [
					{
						"key": "exception",
						"value": "none"
					},
					{
						"key": "name",
						"value": "input"
					},
					{
						"key": "result",
						"value": "success"
					},
					{
						"key": "type",
						"value": "channel"
					}
				],
				"type": "TIMER",
				"description": "Send processing time",
				"baseUnit": "milliseconds"
			},
			"timestamp": "2018-03-23T14:48:12.697Z",
			"sum": 130.340546,
			"count": 6,
			"mean": 21.72342433333333,
			"upper": 116.176299,
			"total": 130.340546
		}
	]
}
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 鉴于在迁移到Micrometer后Metric消息的格式已略有更改，因此已发布的消息还将具有`STREAM_CLOUD_STREAM_VERSION`标头集，`2.x`以帮助区分Spring Cloud Stream的较旧版本中的Metric消息。 |