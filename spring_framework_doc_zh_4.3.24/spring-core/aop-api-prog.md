# 12.7 ProxyFactory创建AOP代理编程

使用Spring以编程方式创建AOP代理很容易。这使您可以使用Spring AOP而不依赖于Spring IoC。

以下清单显示了为目标对象创建代理，其中包含一个拦截器和一个顾问程序。目标对象实现的接口将自动代理：

```java
ProxyFactory factory = new ProxyFactory(myBusinessInterfaceImpl);
factory.addAdvice(myMethodInterceptor);
factory.addAdvisor(myAdvisor);
MyBusinessInterface tb = (MyBusinessInterface) factory.getProxy();
```

第一步是构造一个类型的对象 `org.springframework.aop.framework.ProxyFactory`。您可以使用目标对象创建它，如上例所示，或者指定要在备用构造函数中代理的接口。

您可以添加建议（使用拦截器作为专门的建议）和/或顾问，并在ProxyFactory的生命周期中对其进行操作。如果添加IntroductionInterceptionAroundAdvisor，则可以使代理实现其他接口。

ProxyFactory上有一些便捷方法（继承自`AdvisedSupport`），它允许您添加其他建议类型，例如before和throws建议。AdvisedSupport是ProxyFactory和ProxyFactoryBean的超类。

在大多数应用程序中，将AOP代理创建与IoC框架集成是最佳实践。我们建议您通常使用AOP从Java代码外部化配置。