# 79. Spring Cloud Zookeeper Dependency Watcher

## 79. Spring Cloud Zookeeper依赖关系观察器

Dependency Watcher机制使您可以将侦听器注册到您的依赖项。该功能实际上是该`Observator`模式的实现。当依赖项发生变化时，其状态（变为UP或DOWN）可以应用一些自定义逻辑。

## 79.1激活

需要启用Spring Cloud Zookeeper依赖关系功能，才能使用Dependency Watcher机制。

## 79.2注册侦听器

要注册一个侦听器，您必须实现一个名为的接口 `org.springframework.cloud.zookeeper.discovery.watcher.DependencyWatcherListener`并将其注册为Bean。该接口为您提供了一种方法：

```json
void stateChanged(String dependencyName, DependencyState newState);
```

如果您想为特定的依赖项注册一个侦听器，则`dependencyName`它将是您具体实现的区别。`newState`向您提供有关您的依存关系是否已更改为`CONNECTED`或的信息`DISCONNECTED`。

## 79.3使用状态检查器

依赖关系监视程序绑定的是称为状态检查器的功能。它使您可以在应用程序启动时提供自定义行为，以根据依赖项的状态做出反应。

抽象`org.springframework.cloud.zookeeper.discovery.watcher.presence.DependencyPresenceOnStartupVerifier` 类的默认实现 是 `org.springframework.cloud.zookeeper.discovery.watcher.presence.DefaultDependencyPresenceOnStartupVerifier`，它以下列方式工作。

1. 如果依赖项被标记为我们`required`并且不在Zookeeper中，则在您的应用程序启动时，它将引发异常并关闭。
2. 如果不是`required`，`org.springframework.cloud.zookeeper.discovery.watcher.presence.LogMissingDependencyChecker` 则在该`WARN`级别记录缺少依赖项的 日志。

因为`DefaultDependencyPresenceOnStartupVerifier`仅当没有类型的bean时才注册`DependencyPresenceOnStartupVerifier`，所以可以覆盖此功能。