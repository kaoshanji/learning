# 12.2 切入点

让我们来看看Spring如何处理关键的切入点概念。

### 12.2.1概念

Spring的切入点模型使切入点重用独立于建议类型。可以使用相同的切入点来定位不同的建议。

该`org.springframework.aop.Pointcut`接口是中央接口，用来将通知到特定的类和方法。完整的界面如下所示：

```java
public interface Pointcut {

    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();

}
```

将`Pointcut`接口拆分为两部分允许重用类和方法匹配部分，以及细粒度合成操作（例如与另一个方法匹配器执行“联合”）。

该`ClassFilter`接口用于将切入点限制为给定的一组目标类。如果`matches()`方法始终返回true，则将匹配所有目标类：

```java
public interface ClassFilter {

    boolean matches(Class clazz);
}
```

该`MethodMatcher`接口通常更重要。完整的接口如下所示：

```java
public interface MethodMatcher {

    boolean matches(Method m, Class targetClass);

    boolean isRuntime();

    boolean matches(Method m, Class targetClass, Object[] args);
}
```

该`matches(Method, Class)`方法用于测试此切入点是否与目标类上的给定方法匹配。可以在创建AOP代理时执行此评估，以避免对每个方法调用进行测试。如果2参数matches方法对给定方法返回true，并且`isRuntime()`MethodMatcher 的方法返回true，则将在每次方法调用时调用3参数匹配方法。这使切入点能够在执行目标通知之前立即查看传递给方法调用的参数。

大多数MethodMatchers都是静态的，这意味着它们的`isRuntime()`方法返回false。在这种情况下，永远不会调用3参数匹配方法。

如果可能，尝试使切入点成为静态，允许AOP框架在创建AOP代理时缓存切入点评估的结果。

### 12.2.2切入点的操作

Spring支持对切入点的操作：特别是*联合*和*交集*。

- Union表示切入点匹配的方法。
- 交叉表示两个切入点匹配的方法。
- 联盟通常更有用。
- 可以使用*org.springframework.aop.support.Pointcuts*类中的静态方法或使用同一包中的*ComposablePointcut*类来 *组合*切入点 。但是，使用AspectJ切入点表达式通常是一种更简单的方法。

### 12.2.3 AspectJ表达式切入点

从2.0开始，Spring使用的最重要的切入点类型是 `org.springframework.aop.aspectj.AspectJExpressionPointcut`。这是一个切入点，它使用AspectJ提供的库来解析AspectJ切入点表达式字符串。

有关受支持的AspectJ切入点基元的讨论，请参见上一章。

### 12.2.4便捷切入点实现

Spring提供了几种方便的切入点实现。有些可以开箱即用; 其他的意图是在特定于应用程序的切入点中进行子类化。

#### 静态切入点

静态切入点基于方法和目标类，不能考虑方法的参数。静态切入点对于大多数用途来说足够 - *而且最好*。当首次调用方法时，Spring可能只评估一次静态切入点：之后，无需再次使用每个方法调用来评估切入点。

让我们考虑Spring中包含的一些静态切入点实现。

##### 正则表达式切入点

指定静态切入点的一种显而易见的方法是正则表达式。除Spring之外的几个AOP框架使这成为可能。`org.springframework.aop.support.JdkRegexpMethodPointcut`是一个通用的正则表达式切入点，使用JDK 1.4+中的正则表达式支持。

使用`JdkRegexpMethodPointcut`该类，您可以提供模式字符串列表。如果其中任何一个匹配，则切入点将评估为true。（所以结果实际上是这些切入点的结合。）

用法如下所示：

```xml
<bean id="settersAndAbsquatulatePointcut"
        class="org.springframework.aop.support.JdkRegexpMethodPointcut">
    <property name="patterns">
        <list>
            <value>.*set.*</value>
            <value>.*absquatulate</value>
        </list>
    </property>
</bean>
```

Spring提供了一个便利类，`RegexpMethodPointcutAdvisor`它允许我们也引用一个建议（记住建议可以是一个拦截器，建议之前，抛出建议等）。在幕后，Spring将使用一个`JdkRegexpMethodPointcut`。使用`RegexpMethodPointcutAdvisor`简化了布线，因为一个bean封装了切入点和建议，如下所示：

```xml
<bean id="settersAndAbsquatulateAdvisor"
        class="org.springframework.aop.support.RegexpMethodPointcutAdvisor">
    <property name="advice">
        <ref bean="beanNameOfAopAllianceInterceptor"/>
    </property>
    <property name="patterns">
        <list>
            <value>.*set.*</value>
            <value>.*absquatulate</value>
        </list>
    </property>
</bean>
```

*RegexpMethodPointcutAdvisor*可以与任何建议类型一起使用。

##### 属性驱动的切入点

一种重要的静态切入点是*元数据驱动的*切入点。这使用元数据属性的值：通常是源级元数据。

#### 动态切入点

与静态切入点相比，动态切入点的评估成本更高。它们考虑了方法*参数*以及静态信息。这意味着必须使用每个方法调用来评估它们; 参数不能缓存，因为参数会有所不同。

主要的例子是`control flow`切入点。

##### 控制流量切入点

Spring控制流切入点在概念上类似于AspectJ *cflow*切入点，虽然功能较弱。（目前无法指定切入点在由另一个切入点匹配的连接点下方执行。）控制流切入点与当前调用堆栈匹配。例如，如果连接点由`com.mycompany.web`包中的方法或`SomeCaller`类调用，则可能会触发。使用`org.springframework.aop.support.ControlFlowPointcut`类指定控制流切入点。

### 12.2.5切入点超类

Spring提供了有用的切入点超类来帮助您实现自己的切入点。

因为静态切入点最有用，所以您可能会将StaticMethodMatcherPointcut子类化，如下所示。这需要实现一个抽象方法（尽管可以覆盖其他方法来自定义行为）：

```java
class TestStaticPointcut extends StaticMethodMatcherPointcut {

    public boolean matches(Method m, Class targetClass) {
        // return true if custom criteria match
    }
}
```

还有动态切入点的超类。

您可以在Spring 1.0 RC2及更高版本中使用任何建议类型的自定义切入点。

### 12.2.6自定义切入点

因为Spring AOP中的切入点是Java类，而不是语言功能（如在AspectJ中），所以可以声明自定义切入点，无论是静态还是动态。Spring中的自定义切入点可以是任意复杂的。但是，如果可能，建议使用AspectJ切入点表达式语言。

更高版本的Spring可能会支持JAC提供的“语义切入点”：例如，“所有改变目标对象中实例变量的方法”。

