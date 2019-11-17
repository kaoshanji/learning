# 45. Addressing All Instances of a Service

##  45.处理服务的所有实例

的“ 目的地 ”参数在一个Spring使用`PathMatcher`（与路径分隔作为结肠- `:`），以确定是否一个实例处理该消息。使用前面的示例，`/bus-env/customers:**`定位“ 客户 ”服务的所有实例， 而与其余服务ID无关。