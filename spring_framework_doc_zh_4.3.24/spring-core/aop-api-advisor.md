# 12.4 切面

在Spring中，Advisor是一个只包含与切入点表达式关联的建议对象的方面。

除了介绍的特殊情况，任何顾问都可以使用任何建议。 `org.springframework.aop.support.DefaultPointcutAdvisor`是最常用的顾问类。例如，它可以与a `MethodInterceptor`，`BeforeAdvice`或 一起使用`ThrowsAdvice`。

可以在同一个AOP代理中混合Spring中的顾问程序和通知类型。例如，您可以在一个代理配置中使用拦截建议，抛出建议和建议之前：Spring将自动创建必要的拦截器链。