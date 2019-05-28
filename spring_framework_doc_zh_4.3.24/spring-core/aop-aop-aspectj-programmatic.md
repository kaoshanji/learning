# 11.7 创建@AspectJ代理

除了使用无论是在配置方面的声明`<aop:config>`或者 `<aop:aspectj-autoproxy>`，还可以通过编程方式来创建通知目标对象的代理。有关Spring的AOP API的完整详细信息，请参阅下一章。在这里，我们希望关注使用@AspectJ方面自动创建代理的能力。

该类`org.springframework.aop.aspectj.annotation.AspectJProxyFactory`可用于为一个或多个@AspectJ方面建议的目标对象创建代理。此类的基本用法非常简单，如下所示。有关完整信息，请参阅javadocs。

```java
// create a factory that can generate a proxy for the given target object
AspectJProxyFactory factory = new AspectJProxyFactory(targetObject);

// add an aspect, the class must be an @AspectJ aspect
// you can call this as many times as you need with different aspects
factory.addAspect(SecurityManager.class);

// you can also add existing aspect instances, the type of the object supplied must be an @AspectJ aspect
factory.addAspect(usageTracker);

// now get the proxy object...
MyInterfaceType proxy = factory.getProxy();
```

