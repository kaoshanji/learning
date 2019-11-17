# 17. External Configuration: Archaius

## 17.外部配置：Archaius

[Archaius](https://github.com/Netflix/archaius)是Netflix客户端配置库。它是所有Netflix OSS组件用于配置的库。Archaius是[Apache Commons Configuration](https://commons.apache.org/proper/commons-configuration)项目的扩展。它允许通过轮询源以进行更改或通过将源将更改推送到客户端来更新配置。Archaius使用Dynamic <Type> Property类作为属性的句柄，如以下示例所示：

**Archaius示例。** 

```java
class ArchaiusTest {
    DynamicStringProperty myprop = DynamicPropertyFactory
            .getInstance()
            .getStringProperty("my.prop");

    void doSomething() {
        OtherClass.someMethod(myprop.get());
    }
}
```



Archaius具有自己的一组配置文件和加载优先级。Spring应用程序通常不应该直接使用Archaius，但是仍然需要本地配置Netflix工具。Spring Cloud具有一个Spring Environment Bridge，以便Archaius可以从Spring Environment中读取属性。该桥允许Spring Boot项目使用常规配置工具链，同时允许它们按记录（大多数情况下）配置Netflix工具。