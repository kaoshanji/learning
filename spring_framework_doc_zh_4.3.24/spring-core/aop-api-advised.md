# 12.8 操作对象的建议

但是，您创建AOP代理，您可以使用该`org.springframework.aop.framework.Advised`界面操作它们 。任何AOP代理都可以转换为此接口，无论它实现哪个其他接口。该界面包括以下方法：

```java
Advisor[] getAdvisors();

void addAdvice(Advice advice) throws AopConfigException;

void addAdvice(int pos, Advice advice) throws AopConfigException;

void addAdvisor(Advisor advisor) throws AopConfigException;

void addAdvisor(int pos, Advisor advisor) throws AopConfigException;

int indexOf(Advisor advisor);

boolean removeAdvisor(Advisor advisor) throws AopConfigException;

void removeAdvisor(int index) throws AopConfigException;

boolean replaceAdvisor(Advisor a, Advisor b) throws AopConfigException;

boolean isFrozen();
```

该`getAdvisors()`方法将为已添加到工厂的每个顾问程序，拦截器或其他建议类型返回一个Advisor。如果添加了Advisor，则此索引处返回的顾问程序将是您添加的对象。如果您添加了一个拦截器或其他建议类型，Spring将把它包装在一个带有切入点的顾问程序中，该切入点总是返回true。因此，如果您添加了a `MethodInterceptor`，则为此索引返回的顾问程序将`DefaultPointcutAdvisor`返回您的 `MethodInterceptor`和一个匹配所有类和方法的切入点。

这些`addAdvisor()`方法可用于添加任何Advisor。通常，持有切入点和建议的顾问将是通用的`DefaultPointcutAdvisor`，可以与任何建议或切入点一起使用（但不适用于介绍）。

默认情况下，即使创建了代理，也可以添加或删除顾问程序或拦截器。唯一的限制是无法添加或删除介绍顾问，因为工厂的现有代理不会显示接口更改。（您可以从工厂获取新代理以避免此问题。）

将AOP代理转换为`Advised`接口并检查和操作其建议的简单示例：

```java
Advised advised = (Advised) myObject;
Advisor[] advisors = advised.getAdvisors();
int oldAdvisorCount = advisors.length;
System.out.println(oldAdvisorCount + " advisors");

// Add an advice like an interceptor without a pointcut
// Will match all proxied methods
// Can use for interceptors, before, after returning or throws advice
advised.addAdvice(new DebugInterceptor());

// Add selective advice using a pointcut
advised.addAdvisor(new DefaultPointcutAdvisor(mySpecialPointcut, myAdvice));

assertEquals("Added two advisors", oldAdvisorCount + 2, advised.getAdvisors().length);
```

尽管毫无疑问是合法的使用案例，否则修改生产中业务对象的建议是否可行（没有双关语）是值得怀疑的。但是，它在开发中非常有用：例如，在测试中。我有时发现能够以拦截器或其他建议的形式添加测试代码非常有用，进入我想要测试的方法调用。（例如，建议可以进入为该方法创建的事务内部：例如，在标记事务以进行回滚之前运行SQL以检查数据库是否已正确更新。）

根据您创建代理的方式，您通常可以设置一个`frozen`标志，在这种情况下该`Advised` `isFrozen()`方法将返回true，并且任何通过添加或删除来修改建议的尝试都将导致`AopConfigException`。在某些情况下，冻结建议对象状态的能力很有用，例如，防止调用代码删除安全拦截器。如果已知不需要运行时建议修改，它也可以在Spring 1.1中使用以允许积极优化。