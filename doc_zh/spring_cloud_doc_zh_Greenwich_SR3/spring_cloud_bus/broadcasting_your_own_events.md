# 49. Broadcasting Your Own Events

## 49.广播自己的事件

公交车可以携带任何类型的事件`RemoteApplicationEvent`。默认传输是JSON，解串器需要提前知道将要使用哪些类型。要注册新类型，您必须将其放入的子包中 `org.springframework.cloud.bus.event`。

要自定义事件名称，可以`@JsonTypeName`在自定义类上使用或依赖默认策略，即使用类的简单名称。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 生产者和消费者都需要访问类定义。                             |

## 49.1在自定义程序包中注册事件

如果您不能或不想使用`org.springframework.cloud.bus.event` 自定义事件的子包，则必须`RemoteApplicationEvent`使用`@RemoteApplicationEventScan`批注指定要扫描哪些包以查找类型的事件 。用`@RemoteApplicationEventScan`include子包指定的包。

例如，考虑以下定制事件`MyEvent`：

```java
package com.acme;

public class MyEvent extends RemoteApplicationEvent {
    ...
}
```

您可以通过以下方式在解串器中注册该事件：

```java
package com.acme;

@Configuration
@RemoteApplicationEventScan
public class BusConfiguration {
    ...
}
```

在不指定值的情况下，将`@RemoteApplicationEventScan` 注册使用该类的包。在此示例中，`com.acme`使用的包进行了注册 `BusConfiguration`。

您也可以明确地通过指定包扫描`value`，`basePackages` 或`basePackageClasses`在性能`@RemoteApplicationEventScan`，如下面的例子：

```java
package com.acme;

@Configuration
//@RemoteApplicationEventScan({"com.acme", "foo.bar"})
//@RemoteApplicationEventScan(basePackages = {"com.acme", "foo.bar", "fizz.buzz"})
@RemoteApplicationEventScan(basePackageClasses = BusConfiguration.class)
public class BusConfiguration {
    ...
}
```

前面的所有示例`@RemoteApplicationEventScan`都是等效的，因为 `com.acme`通过在上显式指定软件包来注册该软件包 `@RemoteApplicationEventScan`。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 您可以指定多个要扫描的基本软件包。                           |