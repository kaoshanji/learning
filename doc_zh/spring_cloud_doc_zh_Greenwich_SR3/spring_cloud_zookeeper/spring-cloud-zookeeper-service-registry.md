# 77. Spring Cloud Zookeeper and Service Registry

## 77. Spring Cloud Zookeeper和服务注册表

Spring Cloud Zookeeper实现了该`ServiceRegistry`接口，使开发人员可以通过编程方式注册任意服务。

的`ServiceInstanceRegistration`类提供了一个`builder()`方法来创建一个 `Registration`能够由使用对象`ServiceRegistry`，如图以下示例：

```java
@Autowired
private ZookeeperServiceRegistry serviceRegistry;

public void registerThings() {
    ZookeeperRegistration registration = ServiceInstanceRegistration.builder()
            .defaultUriSpec()
            .address("anyUrl")
            .port(10)
            .name("/a/b/c/d/anotherservice")
            .build();
    this.serviceRegistry.register(registration);
}
```

## 77.1实例状态

Netflix Eureka支持`OUT_OF_SERVICE`在服务器上注册实例。这些实例不作为活动服务实例返回。这对于诸如蓝色/绿色部署之类的行为很有用。（请注意，Curator Service Discovery配方不支持此行为。）利用灵活的有效负载，Spring Cloud Zookeeper可以`OUT_OF_SERVICE`通过更新一些特定的元数据，然后在Ribbon中过滤该元数据来实现`ZookeeperServerList`。将`ZookeeperServerList` 过滤掉所有非空实例的状态是不相等的`UP`。如果实例状态字段为空，则认为是`UP`向后兼容。要更改实例的状态，使`POST`与`OUT_OF_SERVICE`该`ServiceRegistry` 实例状态执行器端点，如以下示例所示：

```bash
$ http POST http://localhost:8081/service-registry status=OUT_OF_SERVICE
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 前面的示例使用`http`来自[https://httpie.org](https://httpie.org/)的命令。 |