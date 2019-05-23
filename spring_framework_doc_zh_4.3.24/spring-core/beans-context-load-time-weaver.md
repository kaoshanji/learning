# 7.14 注册一个LoadTimeWeaver

在`LoadTimeWeaver`用于由Spring动态变换的类，因为它们被装载到Java虚拟机（JVM）。

要启用加载时编织，请添加`@EnableLoadTimeWeaving`到其中一个 `@Configuration`类：

```java
@Configuration
@EnableLoadTimeWeaving
public class AppConfig {
}
```

或者对于XML配置使用`context:load-time-weaver`元素：

```xml
<beans>
    <context:load-time-weaver/>
</beans>
```

一旦配置为`ApplicationContext`。其中的任何bean都`ApplicationContext` 可以实现`LoadTimeWeaverAware`，从而接收对load-time weaver实例的引用。这与 Spring的JPA支持 结合使用特别有用，其中JPA类转换可能需要加载时编织。

