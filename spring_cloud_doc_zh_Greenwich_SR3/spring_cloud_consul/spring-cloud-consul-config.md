# 69. Distributed Configuration with Consul

## 69.使用Consul进行分布式配置

领事提供[密钥/值存储，](https://consul.io/docs/agent/http/kv.html)用于存储配置和其他元数据。Spring Cloud Consul Config是[Config Server和Client](https://github.com/spring-cloud/spring-cloud-config)的替代方案。在特殊的“引导”阶段将配置加载到Spring环境中。`/config`默认情况下，配置存储在文件夹中。`PropertySource`根据应用程序的名称和模仿Spring Cloud Config解析属性顺序的活动配置文件，创建多个实例。例如，名称为“ testApp”且配置文件为“ dev”的应用程序将创建以下属性源：

```properties
config/testApp,dev/
config/testApp/
config/application,dev/
config/application/
```

最具体的属性来源在顶部，最不具体的属性在底部。`config/application`文件夹中的属性适用于所有使用consul进行配置的应用程序。`config/testApp`文件夹中的属性仅可用于名为“ testApp”的服务的实例。

当前在启动应用程序时读取配置。发送HTTP POST至`/refresh`将导致重新加载配置。[第69.3节“ Config Watch”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_spring-cloud-consul-config.html#spring-cloud-consul-config-watch)还将自动检测更改并重新加载应用程序上下文。

## 69.1如何激活

要开始使用Consul Configuration，请使用具有组`org.springframework.cloud`和工件ID 的启动器`spring-cloud-starter-consul-config`。有关使用当前Spring Cloud Release Train设置构建系统的详细信息，请参见[Spring Cloud Project页面](https://projects.spring.io/spring-cloud/)。

这将启用自动配置，该自动配置将设置Spring Cloud Consul Config。

## 69.2定制

Consul Config可以使用以下属性来自定义：

**bootstrap.yml。** 

```properties
spring:
  cloud:
    consul:
      config:
        enabled: true
        prefix: configuration
        defaultContext: apps
        profileSeparator: '::'
```



- `enabled` 将此值设置为“ false”将禁用Consul Config
- `prefix` 设置配置值的基本文件夹
- `defaultContext` 设置所有应用程序使用的文件夹名称
- `profileSeparator` 设置用于在属性源和配置文件中分隔配置文件名称的分隔符的值

## 69.3配置观察

Consul Config Watch利用consul [监视键前缀的功能](https://www.consul.io/docs/agent/watches.html#keyprefix)。Config Watch进行阻塞Consul HTTP API调用，以确定当前应用程序的任何相关配置数据是否已更改。如果有新的配置数据，则会发布刷新事件。这等效于调用`/refresh`执行器端点。

更改Config Watch称为change的频率`spring.cloud.consul.config.watch.delay`。默认值为1000，以毫秒为单位。延迟是上一次调用结束与下一次调用开始之间的时间量。

禁用配置监视集`spring.cloud.consul.config.watch.enabled=false`。

手表使用Spring `TaskScheduler`安排对领事的呼叫。默认情况下，它是a `ThreadPoolTaskScheduler`，其`poolSize`值为1。要更改`TaskScheduler`，请创建一个`TaskScheduler`以`ConsulConfigAutoConfiguration.CONFIG_WATCH_TASK_SCHEDULER_NAME`常量命名的类型的bean 。

## 69.4 YAML或具有配置的属性

与单个键/值对相反，以YAML或“属性”格式存储属性的对象可能更方便。将`spring.cloud.consul.config.format`属性设置为`YAML`或`PROPERTIES`。例如使用YAML：

**bootstrap.yml。** 

```properties
spring:
  cloud:
    consul:
      config:
        format: YAML
```



必须`data`在领事的相应密钥中设置YAML 。使用上面的默认值，键看起来像：

```properties
config/testApp,dev/data
config/testApp/data
config/application,dev/data
config/application/data
```

您可以将YAML文档存储在上面列出的任何键中。

您可以使用更改数据密钥`spring.cloud.consul.config.data-key`。

## 69.5 git2consul与配置

git2consul是一个Consul社区项目，它将git存储库中的文件加载到Consul中的各个键中。默认情况下，键的名称是文件的名称。YAML和属性文件分别以`.yml`和`.properties`为文件扩展名受支持。将`spring.cloud.consul.config.format`属性设置为`FILES`。例如：

**bootstrap.yml。** 

```properties
spring:
  cloud:
    consul:
      config:
        format: FILES
```



给定的以下键`/config`，`development`配置文件和应用程序名称为`foo`：

```properties
.gitignore
application.yml
bar.properties
foo-development.properties
foo-production.yml
foo.properties
master.ref
```

将创建以下属性源：

```properties
config/foo-development.properties
config/foo.properties
config/application.yml
```

每个键的值必须是格式正确的YAML或属性文件。

## 69.6快速失败

如果领事不可用于配置，则在某些情况下（例如本地开发或某些测试场景）不失败可能会很方便。设置`spring.cloud.consul.config.failFast=false`在`bootstrap.yml`会导致配置模块登录一个警告，而不是抛出一个异常。这将允许应用程序继续正常启动。