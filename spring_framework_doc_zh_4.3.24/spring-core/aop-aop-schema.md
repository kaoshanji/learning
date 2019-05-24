# 11.3 Schema-based AOP

如果您更喜欢基于XML的格式，那么Spring还支持使用新的“aop”命名空间标记定义方面。使用@AspectJ样式时支持完全相同的切入点表达式和建议类型，因此在本节中我们将重点介绍新*语法*，并将读者引用到上一节（ 第11.2节“@AspectJ支持”）中的讨论。了解编写切入点表达式和建议参数的绑定。

要使用本节中描述的aop命名空间标记，您需要`spring-aop`按照 第41章*基于XML模式的配置中的*] 描述导入 模式。有关如何在命名空间中导入标记，请参见 第41.2.7节“aop模式” aop`。

在Spring配置中，所有aspect和advisor元素必须放在一个`<aop:config>`元素中（`<aop:config>`在应用程序上下文配置中可以有多个元素）。一个`<aop:config>`元素可以包含切入点，顾问和纵横元件（注意这些必须按照这个顺序进行声明）。

该`<aop:config>`风格的配置使得大量使用Spring的 自动代理 机制。如果您已经通过使用`BeanNameAutoProxyCreator`或类似的方式使用显式自动代理，这可能会导致问题（例如建议不被编织） 。建议的使用模式是仅使用`<aop:config>`样式，或仅使用`AutoProxyCreator`样式。

### 11.3.1声明一个方面

使用模式支持，方面只是在Spring应用程序上下文中定义为bean的常规Java对象。状态和行为在对象的字段和方法中捕获，切入点和建议信息在XML中捕获。

使用<aop：aspect>元素声明方面，并使用以下`ref`属性引用辅助bean ：

```xml
<aop:config>
    <aop:aspect id="myAspect" ref="aBean">
        ...
    </aop:aspect>
</aop:config>

<bean id="aBean" class="...">
    ...
</bean>
```

支持方面（`"aBean"`在这种情况下）的bean 当然可以配置和依赖注入，就像任何其他Spring bean一样。

### 11.3.2声明切入点

可以在<aop：config>元素内声明命名切入点，从而使切入点定义能够跨多个方面和顾问程序共享。

表示服务层中任何业务服务执行的切入点可以定义如下：

```xml
<aop:config>

    <aop:pointcut id="businessService"
        expression="execution(* com.xyz.myapp.service.*.*(..))"/>

</aop:config>
```

请注意，切入点表达式本身使用与 第11.2节“@AspectJ支持”中 所述相同的AspectJ切入点表达式语言。如果使用基于模式的声明样式，则可以引用切入点表达式中类型（@Aspects）中定义的命名切入点。定义上述切入点的另一种方法是：

```xml
<aop:config>

    <aop:pointcut id="businessService"
        expression="com.xyz.myapp.SystemArchitecture.businessService()"/>

</aop:config>
```

假设您有一个`SystemArchitecture`方面，如 “共享公共切入点定义”一节 中 所述。

在方面内部声明切入点与声明顶级切入点非常相似：

```xml
<aop:config>

    <aop:aspect id="myAspect" ref="aBean">

        <aop:pointcut id="businessService"
            expression="execution(* com.xyz.myapp.service.*.*(..))"/>

        ...

    </aop:aspect>

</aop:config>
```

在@AspectJ方面的方式大致相同，使用基于模式的定义样式声明的切入点可能会收集连接点上下文。例如，以下切入点将'this'对象收集为连接点上下文并将其传递给建议：

```xml
<aop:config>

    <aop:aspect id="myAspect" ref="aBean">

        <aop:pointcut id="businessService"
            expression="execution(* com.xyz.myapp.service.*.*(..)) &amp;&amp; this(service)"/>

        <aop:before pointcut-ref="businessService" method="monitor"/>

        ...

    </aop:aspect>

</aop:config>
```

必须通过包含匹配名称的参数来声明建议以接收收集的连接点上下文：

```java
public void monitor(Object service) {
    ...
}
```

当需要连接子表达式，`&&`是尴尬的XML文档中，所以关键字`and`，`or`和`not`可以代替使用`&&`，`||`和`!` 分别。例如，之前的切入点可能更好地写为：

```xml
<aop:config>

    <aop:aspect id="myAspect" ref="aBean">

        <aop:pointcut id="businessService"
            expression="execution(* com.xyz.myapp.service..(..)) and this(service)"/>

        <aop:before pointcut-ref="businessService" method="monitor"/>

        ...
    </aop:aspect>
</aop:config>
```

请注意，以这种方式定义的切入点由其XML ID引用，不能用作命名切入点来形成复合切入点。因此，基于模式的定义样式中的命名切入点支持比@AspectJ样式提供的更有限。

### 11.3.3声明建议

对于@AspectJ样式，支持相同的五种建议类型，它们具有完全相同的语义。

#### 在建议之前

在匹配的方法执行之前运行建议之前。它`<aop:aspect>`使用<aop：before>元素在一个内部声明 。

```xml
<aop:aspect id="beforeExample" ref="aBean">

    <aop:before
        pointcut-ref="dataAccessOperation"
        method="doAccessCheck"/>

    ...

</aop:aspect>
```

这`dataAccessOperation`是在top（`<aop:config>`）级别定义的切入点的id 。要改为内联定义切入点，请使用`pointcut-ref`属性替换该`pointcut`属性：

```xml
<aop:aspect id="beforeExample" ref="aBean">

    <aop:before
        pointcut="execution(* com.xyz.myapp.dao.*.*(..))"
        method="doAccessCheck"/>

    ...

</aop:aspect>
```

正如我们在讨论@AspectJ样式时所提到的，使用命名切入点可以显着提高代码的可读性。

method属性标识`doAccessCheck`提供建议正文的method（）。必须为包含通知的aspect元素引用的bean定义此方法。在执行数据访问操作（由切入点表达式匹配的方法执行连接点）之前，将调用方面bean上的“doAccessCheck”方法。

#### After returning advice

在匹配的方法执行正常完成后返回通知运行。它`<aop:aspect>`以与建议之前相同的方式在内部声明。例如：

```xml
<aop:aspect id="afterReturningExample" ref="aBean">

    <aop:after-returning
        pointcut-ref="dataAccessOperation"
        method="doAccessCheck"/>

    ...

</aop:aspect>
```

就像在@AspectJ样式中一样，可以在建议体内获得返回值。使用returns属性指定应将返回值传递到的参数的名称：

```xml
<aop:aspect id="afterReturningExample" ref="aBean">

    <aop:after-returning
        pointcut-ref="dataAccessOperation"
        returning="retVal"
        method="doAccessCheck"/>

    ...

</aop:aspect>
```

doAccessCheck方法必须声明一个名为的参数`retVal`。此参数的类型以与@AfterReturning所述相同的方式约束匹配。例如，方法签名可以声明为：

```java
public void doAccessCheck(Object retVal) {...
```

#### After throwing advice

抛出建议执行时，匹配的方法执行通过抛出异常退出。它在`<aop:aspect>`使用投掷后元素内部声明：

```xml
<aop:aspect id="afterThrowingExample" ref="aBean">

    <aop:after-throwing
        pointcut-ref="dataAccessOperation"
        method="doRecoveryActions"/>

    ...

</aop:aspect>
```

就像在@AspectJ样式中一样，可以在建议体内获取抛出的异常。使用throwing属性指定应将异常传递到的参数的名称：

```xml
<aop:aspect id="afterThrowingExample" ref="aBean">

    <aop:after-throwing
        pointcut-ref="dataAccessOperation"
        throwing="dataAccessEx"
        method="doRecoveryActions"/>

    ...

</aop:aspect>
```

doRecoveryActions方法必须声明一个名为的参数`dataAccessEx`。此参数的类型以与@AfterThrowing相同的方式约束匹配。例如，方法签名可以声明为：

```java
public void doRecoveryActions(DataAccessException dataAccessEx) {...
```

#### After (finally) advice

在（最终）建议运行之后，匹配的方法执行退出。它使用`after`元素声明：

```xml
<aop:aspect id="afterFinallyExample" ref="aBean">

    <aop:after
        pointcut-ref="dataAccessOperation"
        method="doReleaseLock"/>

    ...

</aop:aspect>
```

#### Around advice

最后一种建议是建议。周围的建议围绕匹配的方法执行运行。它有机会在方法执行之前和之后完成工作，并确定方法实际上何时，如何，甚至是否实际执行。如果您需要以线程安全的方式（例如，启动和停止计时器）在方法执行之前和之后共享状态，则经常使用around建议。始终使用符合您要求的最不强大的建议形式; 如果简单，建议之前不要使用周围的建议。

使用`aop:around`元素声明around建议。advice方法的第一个参数必须是type `ProceedingJoinPoint`。在建议的主体内，调用导致底层方法执行`proceed()`的`ProceedingJoinPoint`原因。该`proceed`方法也可以调用传入`Object[]`- 数组中的值将用作方法执行时的参数。有关 调用 的说明，请参阅 “周围建议” 一节`Object[]`。

```xml
<aop:aspect id="aroundExample" ref="aBean">

    <aop:around
        pointcut-ref="businessService"
        method="doBasicProfiling"/>

    ...

</aop:aspect>
```

`doBasicProfiling`建议的实现与@AspectJ示例中的完全相同（当然减去注释）：

```java
public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
    // start stopwatch
    Object retVal = pjp.proceed();
    // stop stopwatch
    return retVal;
}
```

#### Advice parameters

基于模式的声明样式支持完全类型化的建议，方法与@AspectJ支持描述的方式相同 - 通过名称匹配切入点参数和建议方法参数。有关详细信息，请参阅 “建议参数”一节 。如果您希望显式指定通知方法的参数名称（不依赖于前面描述的检测策略），那么这是使用`arg-names` advice元素的属性完成的，该属性的处理方式与建议中的“argNames”属性相同。注释，如 “确定参数名称”一节 中 所述。例如：

```xml
<aop:before
    pointcut="com.xyz.lib.Pointcuts.anyPublicMethod() and @annotation(auditable)"
    method="audit"
    arg-names="auditable"/>
```

该`arg-names`属性接受以逗号分隔的参数名称列表。

下面是一个基于XSD的方法的一个稍微复杂的例子，它说明了与一些强类型参数一起使用的一些建议。

```java
package x.y.service;

public interface FooService {

    Foo getFoo(String fooName, int age);
}

public class DefaultFooService implements FooService {

    public Foo getFoo(String name, int age) {
        return new Foo(name, age);
    }
}
```

接下来是方面。请注意，该`profile(..)`方法接受许多强类型参数，第一个参数恰好是用于继续进行方法调用的连接点：此参数的存在表明该参数 `profile(..)`将用作`around`建议：

```java
package x.y;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StopWatch;

public class SimpleProfiler {

    public Object profile(ProceedingJoinPoint call, String name, int age) throws Throwable {
        StopWatch clock = new StopWatch("Profiling for '" + name + "' and '" + age + "'");
        try {
            clock.start(call.toShortString());
            return call.proceed();
        } finally {
            clock.stop();
            System.out.println(clock.prettyPrint());
        }
    }
}
```

最后，这是为特定连接点执行上述建议所需的XML配置：

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- this is the object that will be proxied by Spring's AOP infrastructure -->
    <bean id="fooService" class="x.y.service.DefaultFooService"/>

    <!-- this is the actual advice itself -->
    <bean id="profiler" class="x.y.SimpleProfiler"/>

    <aop:config>
        <aop:aspect ref="profiler">

            <aop:pointcut id="theExecutionOfSomeFooServiceMethod"
                expression="execution(* x.y.service.FooService.getFoo(String,int))
                and args(name, age)"/>

            <aop:around pointcut-ref="theExecutionOfSomeFooServiceMethod"
                method="profile"/>

        </aop:aspect>
    </aop:config>

</beans>
```

如果我们有以下驱动程序脚本，我们将在标准输出上获得类似的输出：

```java
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import x.y.service.FooService;

public final class Boot {

    public static void main(final String[] args) throws Exception {
        BeanFactory ctx = new ClassPathXmlApplicationContext("x/y/plain.xml");
        FooService foo = (FooService) ctx.getBean("fooService");
        foo.getFoo("Pengo", 12);
    }
}
```

```bash
StopWatch 'Profiling for 'Pengo' and '12'': running time (millis) = 0
-----------------------------------------
ms     %     Task name
-----------------------------------------
00000  ?  execution(getFoo)
```

#### Advice ordering

当多个建议需要在同一个连接点（执行方法）执行时，排序规则如 “建议排序”一节 中 所述 。方面之间的优先级是通过将`Order`注释添加到支持方面的bean或通过让bean实现`Ordered`接口来确定的。

### 11.3.4介绍

简介（在AspectJ中称为类型间声明）使方面能够声明建议对象实现给定接口，并代表这些对象提供该接口的实现。

使用`aop:declare-parents`内部元素进行介绍。`aop:aspect` 此元素用于声明匹配类型具有新父级（因此名称）。例如，给定接口`UsageTracked`和该接口的实现`DefaultUsageTracked`，以下方面声明服务接口的所有实现者也实现`UsageTracked`接口。（例如，为了通过JMX公开统计信息。）

```xml
<aop:aspect id="usageTrackerAspect" ref="usageTracking">

    <aop:declare-parents
        types-matching="com.xzy.myapp.service.*+"
        implement-interface="com.xyz.myapp.service.tracking.UsageTracked"
        default-impl="com.xyz.myapp.service.tracking.DefaultUsageTracked"/>

    <aop:before
        pointcut="com.xyz.myapp.SystemArchitecture.businessService()
            and this(usageTracked)"
            method="recordUsage"/>

</aop:aspect>
```

支持`usageTracking`bean 的类将包含以下方法：

```java
public void recordUsage(UsageTracked usageTracked) {
    usageTracked.incrementUseCount();
}
```

要实现的接口由`implement-interface`属性确定。`types-matching`属性的值是AspectJ类型模式： - 任何匹配类型的bean都将实现该`UsageTracked`接口。请注意，在上面示例的before advice中，服务bean可以直接用作`UsageTracked`接口的实现。如果以编程方式访问bean，您将编写以下内容：

```java
UsageTracked usageTracked = (UsageTracked) context.getBean("myService");
```

### 11.3.5方面实例化模型

模式定义方面唯一支持的实例化模型是单例模型。未来的版本可能支持其他实例化模型。

### 11.3.6 Advisors

“顾问”的概念是从Spring中定义的AOP支持中提出的，并且在AspectJ中没有直接的等价物。顾问就像一个小小的自足方面，只有一条建议。建议本身由bean表示，并且必须实现[第12.3.2节“Spring中的建议类型”中](#aop-api-advice-types)描述的建议接口之一 。顾问可以利用AspectJ切入点表达式。

Spring支持使用`<aop:advisor>`元素的顾问概念。您最常见的是它与事务性建议一起使用，它在Spring中也有自己的命名空间支持。以下是它的外观：

```xml
<aop:config>

    <aop:pointcut id="businessService"
        expression="execution(* com.xyz.myapp.service.*.*(..))"/>

    <aop:advisor
        pointcut-ref="businessService"
        advice-ref="tx-advice"/>

</aop:config>

<tx:advice id="tx-advice">
    <tx:attributes>
        <tx:method name="*" propagation="REQUIRED"/>
    </tx:attributes>
</tx:advice>
```

除了`pointcut-ref`上例中使用的`pointcut`属性外，您还可以使用该 属性来内联定义切入点表达式。

要定义顾问程序的优先级以便建议可以参与排序，请使用该`order`属性来定义`Ordered`顾问程序的值。

### 11.3.7示例

让我们看看如何使用模式支持重写[第11.2.7节“示例”中](#aop-ataspectj-example)的并发锁定失败重试示例 。

由于并发问题（例如，死锁失败者），业务服务的执行有时可能会失败。如果重试该操作，下一次它很可能会成功。对于适合在这种情况下重试的业务服务（幂等操作不需要返回给用户进行冲突解决），我们希望透明地重试操作以避免客户端看到 `PessimisticLockingFailureException`。这是明确跨越服务层中的多个服务的要求，因此是通过方面实现的理想选择。

因为我们想要重试操作，所以我们需要使用around建议，以便我们可以多次调用proceed。以下是基本方面实现的外观（它只是使用模式支持的常规Java类）：

```java
public class ConcurrentOperationExecutor implements Ordered {

    private static final int DEFAULT_MAX_RETRIES = 2;

    private int maxRetries = DEFAULT_MAX_RETRIES;
    private int order = 1;

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Object doConcurrentOperation(ProceedingJoinPoint pjp) throws Throwable {
        int numAttempts = 0;
        PessimisticLockingFailureException lockFailureException;
        do {
            numAttempts++;
            try {
                return pjp.proceed();
            }
            catch(PessimisticLockingFailureException ex) {
                lockFailureException = ex;
            }
        } while(numAttempts <= this.maxRetries);
        throw lockFailureException;
    }

}
```

请注意，该方面实现了`Ordered`接口，因此我们可以将方面的优先级设置为高于事务通知（我们每次重试时都需要一个新的事务）。在`maxRetries`和`order`属性都可以在Spring中配置。主要操作发生在`doConcurrentOperation`around advice方法中。我们试图继续进行，如果我们失败了，`PessimisticLockingFailureException`我们只需再试一次，除非我们已经用尽所有的重试尝试。

此类与@AspectJ示例中使用的类相同，但删除了注释。

相应的Spring配置是：

```xml
<aop:config>

    <aop:aspect id="concurrentOperationRetry" ref="concurrentOperationExecutor">

        <aop:pointcut id="idempotentOperation"
            expression="execution(* com.xyz.myapp.service.*.*(..))"/>

        <aop:around
            pointcut-ref="idempotentOperation"
            method="doConcurrentOperation"/>

    </aop:aspect>

</aop:config>

<bean id="concurrentOperationExecutor"
    class="com.xyz.myapp.service.impl.ConcurrentOperationExecutor">
        <property name="maxRetries" value="3"/>
        <property name="order" value="100"/>
</bean>
```

请注意，目前我们假设所有业务服务都是幂等的。如果不是这种情况，我们可以通过引入`Idempotent`注释来优化方面，以便它只重试真正的幂等操作：

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    // marker annotation
}
```

并使用注释来注释服务操作的实现。仅重试幂等操作的方面更改只涉及改进切入点表达式，以便只有`@Idempotent`操作匹配：

```xml
<aop:pointcut id="idempotentOperation"
        expression="execution(* com.xyz.myapp.service.*.*(..)) and
        @annotation(com.xyz.myapp.service.Idempotent)"/>
```

