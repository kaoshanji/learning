# 48. Tracing Bus Events

## 48.跟踪总线事件

`RemoteApplicationEvent`可以通过设置跟踪总线事件（的子类） `spring.cloud.bus.trace.enabled=true`。如果这样做，Spring Boot `TraceRepository` （如果存在）将显示发送的每个事件以及每个服务实例的所有确认。以下示例来自`/trace`端点：

```json
{
  "timestamp": "2015-11-26T10:24:44.411+0000",
  "info": {
    "signal": "spring.cloud.bus.ack",
    "type": "RefreshRemoteApplicationEvent",
    "id": "c4d374b7-58ea-4928-a312-31984def293b",
    "origin": "stores:8081",
    "destination": "*:**"
  }
  },
  {
  "timestamp": "2015-11-26T10:24:41.864+0000",
  "info": {
    "signal": "spring.cloud.bus.sent",
    "type": "RefreshRemoteApplicationEvent",
    "id": "c4d374b7-58ea-4928-a312-31984def293b",
    "origin": "customers:9000",
    "destination": "*:**"
  }
  },
  {
  "timestamp": "2015-11-26T10:24:41.862+0000",
  "info": {
    "signal": "spring.cloud.bus.ack",
    "type": "RefreshRemoteApplicationEvent",
    "id": "c4d374b7-58ea-4928-a312-31984def293b",
    "origin": "customers:9000",
    "destination": "*:**"
  }
}
```

前面的跟踪显示a `RefreshRemoteApplicationEvent`是从发送 `customers:9000`，广播到所有服务，并由`customers:9000`和 接收（确认）的`stores:8081`。

自己处理的ACK信号，你可以添加一个`@EventListener`用于 `AckRemoteApplicationEvent`和`SentApplicationEvent`类型您的应用程序（并启用跟踪）。或者，您可以点击`TraceRepository`并从那里挖掘数据。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 任何Bus应用程序都可以跟踪acks。但是，有时，在中央服务中执行此操作很有用，该服务可以对数据进行更复杂的查询，或将其转发给专门的跟踪服务。 |