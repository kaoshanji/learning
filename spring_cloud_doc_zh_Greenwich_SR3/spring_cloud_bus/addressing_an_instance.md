# 44. Addressing an Instance

## 44.寻址实例

应用程序的每个实例都有一个服务ID，该服务ID的值可以设置， `spring.cloud.bus.id`并且其值应按冒号分隔的标识符列表（从最小到最具体）排列。默认值是根据环境构造的`spring.application.name`和 `server.port`（或`spring.application.index`，如果设置）的组合。ID的默认值以的形式构造`app:index:id`，其中：

- `app`是`vcap.application.name`，如果存在，或者`spring.application.name`
- `index`是`vcap.application.instance_index`，如果存在的话， `spring.application.index`，`local.server.port`，`server.port`，或`0`（以该顺序）。
- `id`是`vcap.application.instance_id`，（如果存在）或随机值。

HTTP端点接受“ destination ”路径参数，例如 `/bus-refresh/customers:9000`，其中`destination`是服务ID。如果该ID由总线上的一个实例拥有，它将处理该消息，而所有其他实例将忽略它。