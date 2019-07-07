

# 11.2 @AspectJ

@AspectJ指的是将方面声明为使用注释注释的常规Java类的样式。作为AspectJ 5版本的一部分，[AspectJ项目](https://www.eclipse.org/aspectj)引入了@AspectJ样式 。Spring使用AspectJ提供的库解释与AspectJ 5相同的注释，用于切入点解析和匹配。AOP运行时仍然是纯Spring AOP，并且不依赖于AspectJ编译器或weaver。

使用AspectJ编译器和weaver可以使用完整的AspectJ语言

### 11.2.1启用@AspectJ支持

要在Spring配置需要启用配置基于@AspectJ方面的Spring AOP和Spring支持使用@AspectJ切面，*自动代理*基于它们是否被那些方面建议豆。通过autoproxying我们的意思是，如果Spring确定bean被一个或多个方面建议，它将自动生成该bean的代理以拦截方法调用并确保根据需要执行建议。

可以使用XML或Java样式配置启用@AspectJ支持。在任何一种情况下，您还需要确保AspectJ的`aspectjweaver.jar`库位于应用程序的类路径中（版本1.6.8或更高版本）。此库可在`'lib'`AspectJ分发的 目录中或通过Maven Central存储库获得。

#### 使用Java配置启用@AspectJ支持

要使用Java启用@AspectJ支持，请`@Configuration`添加`@EnableAspectJAutoProxy` 注释：

```java
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

}
```

#### 使用XML配置启用@AspectJ支持

要使用基于XML的配置启用@AspectJ支持，请使用以下`aop:aspectj-autoproxy` 元素：

```xml
<aop:aspectj-autoproxy/>
```

### 11.2.2声明一个方面

在启用了@AspectJ支持的情况下，应用程序上下文中定义的任何bean都具有@AspectJ方面的类（具有`@Aspect`注释）将由Spring自动检测并用于配置Spring AOP。以下示例显示了非常有用的方面所需的最小定义：

应用程序上下文中的常规bean定义，指向具有`@Aspect`注释的bean类：

```xml
<bean id="myAspect" class="org.xyz.NotVeryUsefulAspect">
    <!-- configure properties of aspect here as normal -->
</bean>
```

和`NotVeryUsefulAspect`类定义，注释 `org.aspectj.lang.annotation.Aspect`注释;

```java
package org.xyz;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class NotVeryUsefulAspect {

}
```

方面（带有注释的类`@Aspect`）可能具有与任何其他类一样的方法和字段。它们还可能包含切入点，建议和引入（类型间）声明。

您可以在Spring XML配置中将方面类注册为常规bean，或者通过类路径扫描自动检测它们 - 就像任何其他Spring管理的bean一样。但是，请注意，*@Aspect*注解是*不*足够的classpath中自动检测：为了这个目的，你需要添加一个单独的*@Component*注释（或可选择地有资格，按照Spring的组件扫描仪的规则自定义构造型注解）。

在Spring AOP，它是*不是*可以有自己的方面从其他方面意见的目标。类上的*@Aspect*注释将其标记为方面，因此将其从自动代理中排除。

### 11.2.3声明切入点

回想一下，切入点确定了感兴趣的连接点，从而使我们能够控制建议何时执行。*Spring AOP仅支持Spring bean的方法执行连接点*，因此您可以将切入点视为匹配Spring bean上方法的执行。切入点声明有两个部分：一个包含名称和任何参数的签名，以及一个*精确*确定我们感兴趣的方法执行的切入点表达式。在AOP的@AspectJ注释样式中，切入点签名由常规方法提供定义，并使用`@Pointcut`注释指示切入点表达式（用作切入点签名的方法 *必须*具有`void`返回类型）。

一个示例将有助于区分切入点签名和切入点表达式。以下示例定义了一个名为的切入点`'anyOldTransfer'`，它将匹配任何名为的方法的执行`'transfer'`：

```java
@Pointcut("execution(* transfer(..))")// the pointcut expression
private void anyOldTransfer() {}// the pointcut signature
```

形成`@Pointcut`注释值的切入点表达式是常规的AspectJ 5切入点表达式。

#### 支持的切入点指示符

Spring AOP支持以下AspectJ切入点指示符（PCD）用于切入点表达式：

**其他切入点类型**

完整的AspectJ切入点语言支持Spring中不支持的其他切入点指示符。这些是：`call, get, set, preinitialization, staticinitialization, initialization, handler, adviceexecution, withincode, cflow, cflowbelow, if, @this`和`@withincode`。在Spring AOP解释的切入点表达式中使用这些切入点指示符将导致`IllegalArgumentException`被抛出。

Spring AOP支持的切入点指示符集可以在将来的版本中进行扩展，以支持更多的AspectJ切入点指示符。

- **execution* * - 对于匹配方法执行连接点，这是在使用Spring AOP时将使用的主要切入点指示符
- *within* - 限制匹配某些类型中的连接点（只是在使用Spring AOP时执行在匹配类型中声明的方法）
- *this* - 限制匹配连接点（使用Spring AOP时执行方法），其中bean引用（Spring AOP代理）是给定类型的实例
- *target* - 限制匹配到连接点（使用Spring AOP时执行方法），其中目标对象（被代理的应用程序对象）是给定类型的实例
- *args* - 限制匹配连接点（使用Spring AOP时执行方法），其中参数是给定类型的实例
- *@target* - 限制匹配到连接点（使用Spring AOP时执行方法），其中执行对象的类具有给定类型的注释
- *@args* - 限制匹配到连接点（使用Spring AOP时执行方法），其中传递的实际参数的运行时类型具有给定类型的注释
- *@within* - 限制匹配以连接具有给定注释的类型中的点（使用Spring AOP时在具有给定注释的类型中声明的方法的执行）
- *@annotation* - 限制连接点的匹配，其中连接点的主题（在Spring AOP中执行的方法）具有给定的注释

由于Spring AOP仅限制与方法执行连接点的匹配，因此上面对切入点指示符的讨论给出了比在AspectJ编程指南中找到的更窄的定义。除此之外，AspectJ本身具有基于类型的语义，在执行的连接点`this`和`target`指代相同的对象-对象执行方法。Spring AOP是一个基于代理的系统，它区分代理对象本身（绑定到`this`）和代理后面的目标对象（绑定到`target`）

由于Spring的AOP框架基于代理的特性，目标对象内的调用根据定义*不会*被截获。对于JDK代理，只能拦截代理上的公共接口方法调用。使用CGLIB，代理上的公共和受保护方法调用将被拦截，甚至包括必要的包可见方法。但是，通过代理进行的常见交互应始终通过公共签名进行设计。

请注意，切入点定义通常与任何截获的方法匹配。如果切入点严格意义上是公开的，即使在通过代理进行潜在非公共交互的CGLIB代理方案中，也需要相应地定义切入点。

如果您的拦截需要包括目标类中的方法调用甚至构造函数，请考虑使用Spring驱动的[本机AspectJ编织](#aop-aj-ltw)而不是Spring的基于代理的AOP框架。这构成了具有不同特征的不同AOP使用模式，因此在做出决定之前一定要先熟悉编织。

Spring AOP还支持另一个名为的PCD `bean`。此PCD允许您将连接点的匹配限制为特定的命名Spring bean，或限制为一组命名的Spring bean（使用通配符时）。该`bean`PCD具有下列形式：

```java
bean(idOrNameOfBean)
```

该`idOrNameOfBean`令牌可以是任何Spring bean的名字：使用限定通配符`*`提供的性格，所以如果你建立一些命名约定，你的Spring豆你可以很容易地编写一个`bean`PCD表达挑出来。与其他切入点指示符的情况一样，`bean`PCD可以被&&'，||'和！（否定）也是。

请注意，`bean`PCD *仅*在Spring AOP中受支持 - 而*不是*在原生AspectJ编织中。它是AspectJ定义的标准PCD的Spring特定扩展，因此不适用于`@Aspect`模型中声明的方面。

该`bean`PCD的操作*实例*级别（建设于Spring bean的概念），而不是仅在类型级别（这是基于什么编织的AOP仅限于）。基于实例的切入点指示符是Spring基于代理的AOP框架的一种特殊功能，它与Spring bean工厂紧密集成，通过名称可以自然而直接地识别特定的bean。

#### 结合切入点表达式

可以使用'&&'，'||'组合切入点表达式 和'！'。也可以通过名称引用切入点表达式。以下示例显示了三个切入点表达式:( `anyPublicOperation`如果方法执行连接点表示任何公共方法的执行，则匹配）; `inTrading`（如果方法执行在交易模块中，`tradingOperation`则匹配），以及（如果方法执行代表交易模块中的任何公共方法，则匹配）。

```java
@Pointcut("execution(public * *(..))")
private void anyPublicOperation() {}

@Pointcut("within(com.xyz.someapp.trading..*)")
private void inTrading() {}

@Pointcut("anyPublicOperation() && inTrading()")
private void tradingOperation() {}
```

如上所示，最佳实践是从较小的命名组件构建更复杂的切入点表达式。当按名称引用切入点时，将应用常规Java可见性规则（您可以看到相同类型的私有切入点，层次结构中受保护的切入点，任何地方的公共切入点等等）。可见性不会影响切入点 *匹配*。

#### 共享通用切入点定义

使用企业应用程序时，您经常需要从几个方面引用应用程序的模块和特定的操作集。我们建议定义一个“SystemArchitecture”方面，为此目的捕获常见的切入点表达式。典型的这种方面看起来如下：

```java
package com.xyz.someapp;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SystemArchitecture {

    /**
     * A join point is in the web layer if the method is defined
     * in a type in the com.xyz.someapp.web package or any sub-package
     * under that.
     */
    @Pointcut("within(com.xyz.someapp.web..*)")
    public void inWebLayer() {}

    /**
     * A join point is in the service layer if the method is defined
     * in a type in the com.xyz.someapp.service package or any sub-package
     * under that.
     */
    @Pointcut("within(com.xyz.someapp.service..*)")
    public void inServiceLayer() {}

    /**
     * A join point is in the data access layer if the method is defined
     * in a type in the com.xyz.someapp.dao package or any sub-package
     * under that.
     */
    @Pointcut("within(com.xyz.someapp.dao..*)")
    public void inDataAccessLayer() {}

    /**
     * A business service is the execution of any method defined on a service
     * interface. This definition assumes that interfaces are placed in the
     * "service" package, and that implementation types are in sub-packages.
     *
     * If you group service interfaces by functional area (for example,
     * in packages com.xyz.someapp.abc.service and com.xyz.someapp.def.service) then
     * the pointcut expression "execution(* com.xyz.someapp..service.*.*(..))"
     * could be used instead.
     *
     * Alternatively, you can write the expression using the 'bean'
     * PCD, like so "bean(*Service)". (This assumes that you have
     * named your Spring service beans in a consistent fashion.)
     */
    @Pointcut("execution(* com.xyz.someapp..service.*.*(..))")
    public void businessService() {}

    /**
     * A data access operation is the execution of any method defined on a
     * dao interface. This definition assumes that interfaces are placed in the
     * "dao" package, and that implementation types are in sub-packages.
     */
    @Pointcut("execution(* com.xyz.someapp.dao.*.*(..))")
    public void dataAccessOperation() {}

}
```

在这样一个方面定义的切入点可以在任何需要切入点表达式的地方引用。例如，要使服务层具有事务性，您可以编写：

```xml
<aop:config>
    <aop:advisor
        pointcut="com.xyz.someapp.SystemArchitecture.businessService()"
        advice-ref="tx-advice"/>
</aop:config>

<tx:advice id="tx-advice">
    <tx:attributes>
        <tx:method name="*" propagation="REQUIRED"/>
    </tx:attributes>
</tx:advice>
```

这些`<aop:config>`和`<aop:advisor>`元素在 第11.3节“基于模式的AOP支持”中讨论。第17章“ *事务管理”*中讨论了事务元素。

#### 例子

Spring AOP用户可能`execution`最常使用切入点指示符。执行表达式的格式为：

```java
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern)
            throws-pattern?)
```

除返回类型模式（上面的代码段中的ret-type-pattern），名称模式和参数模式之外的所有部分都是可选的。返回类型模式确定方法的返回类型必须是什么才能匹配连接点。最常用的`*`是作为返回类型模式，它匹配任何返回类型。仅当方法返回给定类型时，完全限定类型名称才匹配。名称模式与方法名称匹配。您可以将`*`通配符用作名称模式的全部或部分。如果指定声明类型模式，则包括尾部`.`以将其连接到名称模式组件。参数模式稍微复杂一些：`()`匹配不带参数的方法，而`(..)`匹配任意数量的参数（零或更多）。模式`(*)`匹配采用任何类型的一个参数的`(*,String)`方法， 匹配采用两个参数的方法，第一个可以是任何类型，第二个必须是String。有关更多信息，请参阅AspectJ编程指南的 [语言语义](https://www.eclipse.org/aspectj/doc/released/progguide/semantics-pointcuts.html)部分。

下面给出了常见切入点表达式的一些示例。

- 执行任何公共方法：

```
execution(public * *(..))
```

- 名称以“set”开头的任何方法的执行：

```
execution(* set*(..))
```

- 执行`AccountService`接口定义的任何方法：

```
execution(* com.xyz.service.AccountService.*(..))
```

- 执行服务包中定义的任何方法：

```
execution(* com.xyz.service.*.*(..))
```

- 执行服务包或子包中定义的任何方法：

```
execution(* com.xyz.service..*.*(..))
```

- 服务包中的任何连接点（仅在Spring AOP中执行方法）：

```
within(com.xyz.service.*)
```

- 服务包或子包中的任何连接点（仅在Spring AOP中执行方法）：

```
within(com.xyz.service..*)
```

- 代理实现`AccountService`接口的任何连接点（仅在Spring AOP中执行方法） ：

```
this(com.xyz.service.AccountService)
```

'this'更常用于绑定形式： - 请参阅以下有关如何在建议体中提供代理对象的建议。

- 目标对象实现`AccountService`接口的任何连接点（仅在Spring AOP中执行方法）：

  ```java
  target(com.xyz.service.AccountService)
  ```

'target'更常用于绑定形式： - 请参阅以下有关如何在建议体中提供目标对象的建议。

- 任何连接点（仅在Spring AOP中执行的方法），它接受一个参数，并且在运行时传递的参数是`Serializable`：

  ```java
  args(java.io.Serializable)
  ```

'args'更常用于绑定形式： - 请参阅以下有关如何在建议体中提供方法参数的建议。

请注意，此示例中给出的切入点与以下内容不同`execution(* *(java.io.Serializable))`：如果在运行时传递的参数是Serializable，则args版本匹配，如果方法签名声明了单个类型参数，则执行版本匹配`Serializable`。

- 目标对象具有`@Transactional`注释的任何连接点（仅在Spring AOP中执行方法） ：

  ```java
  @target(org.springframework.transaction.annotation.Transactional)
  ```

'@target'也可以用于绑定形式： - 请参阅以下有关如何在建议体中提供注释对象的建议。

- 任何连接点（仅在Spring AOP中执行方法），其中目标对象的声明类型具有`@Transactional`注释：

  ```java
  @within（org.springframework.transaction.annotation.Transactional）
  ```

'@within'也可以用于绑定形式： - 请参阅以下有关如何在建议体中提供注释对象的建议。

- 任何连接点（仅在Spring AOP中执行方法），其中执行方法具有 `@Transactional`注释：

```
@annotation（org.springframework.transaction.annotation.Transactional）
```

'@annotation'也可以用于绑定形式： - 请参阅以下有关如何在建议体中提供注释对象的建议。

- 任何连接点（仅在Spring AOP中执行的方法），它接受一个参数，并且传递的参数的运行时类型具有`@Classified`注释：

```java
@args（com.xyz.security.Classified）
```

'@args'也可以用于绑定形式： - 请参阅以下有关如何在建议体中提供注释对象的建议。

- 名为的Spring bean上的任何连接点（仅在Spring AOP中执行方法） `tradeService`：

  ```java
  bean(tradeService)
  ```

- 具有与通配符表达式匹配的名称的Spring bean上的任何连接点（仅在Spring AOP中执行方法）`*Service`：

  ```java
  bean(*Service)
  ```

#### 写出好的切入点

在编译期间，AspectJ处理切入点以尝试和优化匹配性能。检查代码并确定每个连接点是否（静态地或动态地）匹配给定切入点是一个代价高昂的过程。（动态匹配意味着无法通过静态分析完全确定匹配，并且将在代码中放置测试以确定代码运行时是否存在实际匹配）。在第一次遇到切入点声明时，AspectJ会将其重写为匹配过程的最佳形式。这是什么意思？基本上，切入点在DNF（析取范式）中重写，并且切入点的组件被排序，以便首先检查那些评估成本更低的组件。

但是，AspectJ只能处理它所说的内容，并且为了获得最佳匹配性能，您应该考虑它们想要实现的目标，并在定义中尽可能缩小匹配的搜索空间。现有的指示符自然分为三组：kinded，scoping和context：

- Kinded指示符是选择特定类型的连接点的指示符。例如：执行，获取，设置，调用，处理程序
- 范围界定指示符是那些选择一组感兴趣的连接点（可能是多种类型）的指示符。例如：内部，内部代码
- 上下文指示符是基于上下文匹配（并且可选地绑定）的指示符。例如：this，target，@ annotation

一个写得很好的切入点应该尝试包括至少前两种类型（kinded和scoping），而如果希望基于连接点上下文匹配，则可以包括上下文指示符，或者绑定该上下文以在建议中使用。仅提供一个kinded指示符或仅提供上下文指示符将起作用，但由于所有额外的处理和分析，可能会影响编织性能（使用的时间和内存）。范围界定指示符非常快速匹配，它们的使用意味着AspectJ可以非常快速地解除不应该进一步处理的连接点组 - 这就是为什么一个好的切入点应该总是包括一个如果可能的原因。

### 11.2.4声明建议

建议与切入点表达式相关联，并在切入点匹配的方法执行之前，之后或周围运行。切入点表达式可以是对命名切入点的简单引用，也可以是在适当位置声明的切入点表达式。

#### 在建议之前

在使用`@Before`注释在方面声明建议之前：

```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class BeforeExample {

    @Before("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
    public void doAccessCheck() {
        // ...
    }

}
```

如果使用就地切入点表达式，我们可以将上面的示例重写为：

```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class BeforeExample {

    @Before("execution(* com.xyz.myapp.dao.*.*(..))")
    public void doAccessCheck() {
        // ...
    }

}
```

#### 回复建议后

返回建议后，匹配的方法执行正常返回。它使用`@AfterReturning`注释声明：

```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterReturning;

@Aspect
public class AfterReturningExample {

    @AfterReturning("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
    public void doAccessCheck() {
        // ...
    }

}
```

注意：当然可以在同一方面内有多个建议声明和其他成员。我们只是在这些例子中展示了一个建议声明，专注于当时正在讨论的问题。

有时您需要在建议体中访问返回的实际值。你可以使用它的形式`@AfterReturning`绑定返回值：

```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterReturning;

@Aspect
public class AfterReturningExample {

    @AfterReturning(
        pointcut="com.xyz.myapp.SystemArchitecture.dataAccessOperation()",
        returning="retVal")
    public void doAccessCheck(Object retVal) {
        // ...
    }

}
```

`returning`属性中使用的名称必须与advice方法中的参数名称相对应。当方法执行返回时，返回值将作为相应的参数值传递给advice方法。甲`returning`子句也限制了只能匹配到返回指定类型的值（这些方法执行`Object`在这种情况下，这将匹配任何返回值）。

请注意，这是*没有*可能返回一个完全不同的参考使用后置通知时。

#### After throwing advice

抛出建议运行时，匹配的方法执行通过抛出异常退出。它使用`@AfterThrowing`注释声明：

```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterThrowing;

@Aspect
public class AfterThrowingExample {

    @AfterThrowing("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
    public void doRecoveryActions() {
        // ...
    }

}
```

通常，您希望仅在抛出给定类型的异常时才运行建议，并且您还经常需要访问建议体中的抛出异常。使用该 `throwing`属性来限制匹配（如果需要，`Throwable`否则，将其用作异常类型）并将抛出的异常绑定到advice参数。

```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterThrowing;

@Aspect
public class AfterThrowingExample {

    @AfterThrowing(
        pointcut="com.xyz.myapp.SystemArchitecture.dataAccessOperation()",
        throwing="ex")
    public void doRecoveryActions(DataAccessException ex) {
        // ...
    }

}
```

`throwing`属性中使用的名称必须与advice方法中的参数名称相对应。当通过抛出异常退出方法时，异常将作为相应的参数值传递给advice方法。甲`throwing` 子句也限制了只能匹配到抛出指定类型的异常（那些方法执行`DataAccessException`在这种情况下）。

#### After (finally) advice

在（最终）建议运行之后，匹配的方法执行退出。它是使用`@After`注释声明的。在建议必须准备好处理正常和异常返回条件。它通常用于释放资源等。

```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.After;

@Aspect
public class AfterFinallyExample {

    @After("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
    public void doReleaseLock() {
        // ...
    }

}
```

#### Around advice

最后一种建议是建议。周围的建议围绕匹配的方法执行运行。它有机会在方法执行之前和之后完成工作，并确定方法实际上何时，如何，甚至是否实际执行。如果您需要以线程安全的方式（例如，启动和停止计时器）在方法执行之前和之后共享状态，则经常使用around建议。始终使用符合您要求的最不强大的建议形式（即如果在建议之前，请不要使用简单的建议）。

使用`@Around`注释声明around建议。advice方法的第一个参数必须是type `ProceedingJoinPoint`。在建议的主体内，调用导致底层方法执行`proceed()`的`ProceedingJoinPoint`原因。该`proceed`方法也可以被称为传入`Object[]`- 数组中的值将在进行时用作方法执行的参数。

使用Object []调用时，proceed的行为与由AspectJ编译器编译的around建议的行为略有不同。对于使用传统AspectJ语言编写的周围建议，传递给proceed的参数数量必须与传递给around通知的参数数量（不是基础连接点所采用的参数数量）相匹配，并且传递给的值继续给定的参数位置取代了值绑定到的实体的连接点的原始值（如果现在没有意义，请不要担心！）。Spring采用的方法更简单，更好地匹配其基于代理的，仅执行语义。如果要编译为Spring编写的@AspectJ方面并使用带有AspectJ编译器和weaver的参数继续，则只需要知道这种差异。有一种方法可以编写在Spring AOP和AspectJ上100％兼容的方面，这将在下面的建议参数部分中讨论。

```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;

@Aspect
public class AroundExample {

    @Around("com.xyz.myapp.SystemArchitecture.businessService()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        // start stopwatch
        Object retVal = pjp.proceed();
        // stop stopwatch
        return retVal;
    }

}
```

around通知返回的值将是方法调用者看到的返回值。例如，一个简单的缓存方面可以从缓存中返回一个值（如果有的话），如果没有，则调用proceed（）。请注意，可以在周围建议的主体内调用一次，多次或根本不调用，所有这些都是非常合法的。

#### 建议参数

Spring提供完全类型的建议 - 意味着您在建议签名中声明了所需的参数（正如我们在上面看到的返回和抛出示例所示），而不是一直使用`Object[]`数组。我们将看到如何在一瞬间为建议主体提供参数和其他上下文值。首先让我们来看看如何编写通用建议，以便了解建议目前建议的方法。

##### 访问当前的JoinPoint

任何通知方法都可以声明为它的第一个参数，类型的参数 `org.aspectj.lang.JoinPoint`（请注意，周围的建议*需要*声明类型的第一个参数`ProceedingJoinPoint`，它是一个子类`JoinPoint`。 `JoinPoint`接口提供了许多有用的方法，如`getArgs()`（返回方法）参数），`getThis()`（返回代理对象），`getTarget()`（返回目标对象），`getSignature()`（返回正在建议的方法的描述）和`toString()`（打印建议方法的有用描述）。请查阅javadocs以获取完整详细信息。

##### 将参数传递给建议

我们已经看到了如何绑定返回的值或异常值（在返回之后和抛出建议之后使用）。要使参数值可用于建议体，您可以使用绑定形式`args`。如果在args表达式中使用参数名称代替类型名称，则在调用通知时，相应参数的值将作为参数值传递。一个例子应该使这更清楚。假设您要建议执行以Account对象作为第一个参数的dao操作，并且您需要访问建议体中的帐户。你可以写下面的内容：

```java
@Before("com.xyz.myapp.SystemArchitecture.dataAccessOperation() && args(account,..)")
public void validateAccount(Account account) {
    // ...
}
```

`args(account,..)`切入点表达式的一部分有两个目的：首先，它将匹配仅限于那些方法至少接受一个参数的方法执行，而传递给该参数的参数是一个实例`Account`; 其次，它`Account`通过`account` 参数使实际对象可用于建议。

另一种编写方法是声明一个切入点，`Account` 当它与连接点匹配时“提供” 对象值，然后从建议中引用命名切入点。这看起来如下：

```java
@Pointcut("com.xyz.myapp.SystemArchitecture.dataAccessOperation() && args(account,..)")
private void accountDataAccessOperation(Account account) {}

@Before("accountDataAccessOperation(account)")
public void validateAccount(Account account) {
    // ...
}
```

感兴趣的读者再次参考AspectJ编程指南以获取更多详细信息。

代理对象（`this`），目标对象（`target`）和注释（`@within, @target, @annotation, @args`）都可以以类似的方式绑定。以下示例显示了如何匹配使用注释注释的方法的执行 `@Auditable`，并提取审计代码。

首先是`@Auditable`注释的定义：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auditable {
    AuditCode value();
}
```

然后是与`@Auditable`方法执行相匹配的建议：

```java
@Before("com.xyz.lib.Pointcuts.anyPublicMethod() && @annotation(auditable)")
public void audit(Auditable auditable) {
    AuditCode code = auditable.value();
    // ...
}
```

##### Advice parameters and generics

Spring AOP可以处理类声明和方法参数中使用的泛型。假设您有这样的泛型类型：

```java
public interface Sample<T> {
    void sampleGenericMethod(T param);
    void sampleGenericCollectionMethod(Collection<T> param);
}
```

您可以通过在要拦截方法的参数类型中键入advice参数，将方法类型的拦截限制为某些参数类型：

```java
@Before("execution(* ..Sample+.sampleGenericMethod(*)) && args(param)")
public void beforeSampleMethod(MyType param) {
    // Advice implementation
}
```

正如我们上面已经讨论的那样，这很有效。但是，值得指出的是，这不适用于通用集合。所以你不能像这样定义一个切入点：

```java
@Before("execution(* ..Sample+.sampleGenericCollectionMethod(*)) && args(param)")
public void beforeSampleMethod(Collection<MyType> param) {
    // Advice implementation
}
```

为了完成这项工作，我们必须检查集合中的每个元素，这是不合理的，因为我们也无法决定如何对待`null`一般的值。要实现与此类似的功能，您必须键入参数`Collection<?>`并手动检查元素的类型。

##### Determining argument names

通知调用中的参数绑定依赖于切入点表达式中使用的匹配名称与（advice和pointcut）方法签名中声明的参数名称。参数名*无法*通过Java反射来获取，所以Spring AOP使用如下的策略来确定参数名字：

- 如果用户明确指定了参数名称，则使用指定的参数名称：通知和切入点注释都有一个可选的“argNames”属性，可用于指定带注释的方法的参数名称 - 这些参数名字*是*在运行时可用。例如：

```java
@Before(value="com.xyz.lib.Pointcuts.anyPublicMethod() && target(bean) && @annotation(auditable)",
        argNames="bean,auditable")
public void audit(Object bean, Auditable auditable) {
    AuditCode code = auditable.value();
    // ... use code and bean
}
```

如果第一个参数是的`JoinPoint`，`ProceedingJoinPoint`或 `JoinPoint.StaticPart`类型，你可以在“argNames”属性的值中省去参数的名字。例如，如果修改前面的建议以接收连接点对象，则“argNames”属性不需要包含它：

```java
@Before(value="com.xyz.lib.Pointcuts.anyPublicMethod() && target(bean) && @annotation(auditable)",
        argNames="bean,auditable")
public void audit(JoinPoint jp, Object bean, Auditable auditable) {
    AuditCode code = auditable.value();
    // ... use code, bean, and jp
}
```

给出的第一个参数的特殊待遇`JoinPoint`， `ProceedingJoinPoint`和`JoinPoint.StaticPart`类型是不收取任何其它连接上下文的通知特别方便。在这种情况下，您可以简单地省略“argNames”属性。例如，以下建议无需声明“argNames”属性：

```java
@Before("com.xyz.lib.Pointcuts.anyPublicMethod()")
public void audit(JoinPoint jp) {
    // ... use jp
}
```

- 使用该`'argNames'`属性有点笨拙，因此如果`'argNames'`未指定该属性，则Spring AOP将查看该类的调试信息，并尝试从局部变量表中确定参数名称。只要使用调试信息（`'-g:vars'`至少）编译了类，就会出现此信息。使用此标志进行编译的后果是：（1）您的代码将更容易理解（逆向工程），（2）类文件大小将略微更大（通常无关紧要），（3）要删除的优化编译器不会应用未使用的局部变量。换句话说，使用此标志构建时不会遇到任何困难。

如果即使没有调试信息，AspectJ编译器（ajc）也编译了@AspectJ方面，则不需要添加argNames属性，因为编译器将保留所需的信息。

- 如果代码编译时没有必要的调试信息，那么Spring AOP将尝试推断绑定变量与参数的配对（例如，如果在切入点表达式中只绑定了一个变量，并且advice方法只接受一个参数，配对很明显！）。如果给定可用信息，变量的绑定是不明确的，那么`AmbiguousBindingException`将抛出一个。
- 如果所有上述策略都失败，那么`IllegalArgumentException`将抛出一个。

##### Proceeding with arguments

我们之前评论过，我们将描述如何*使用*在Spring AOP和AspectJ中一致工作的*参数*编写一个继续调用。解决方案只是确保建议签名按顺序绑定每个方法参数。例如：

```java
@Around("execution(List<Account> find*(..)) && " +
        "com.xyz.myapp.SystemArchitecture.inDataAccessLayer() && " +
        "args(accountHolderNamePattern)")
public Object preProcessQueryPattern(ProceedingJoinPoint pjp,
        String accountHolderNamePattern) throws Throwable {
    String newPattern = preProcess(accountHolderNamePattern);
    return pjp.proceed(new Object[] {newPattern});
}
```

在许多情况下，无论如何你都会做这个绑定（如上例所示）

#### Advice ordering

当多条建议都想在同一个连接点运行时会发生什么？Spring AOP遵循与AspectJ相同的优先级规则来确定建议执行的顺序。最高优先级的建议首先“在路上”（所以给出两条之前的建议，优先级最高的建议先运行）。从连接点“出路”，最高优先级建议最后运行（因此，给出两条后建议，具有最高优先级的建议将运行第二）。

当在*不同*方面定义的两条建议都需要在同一个连接点运行时，除非您另行指定，否则执行顺序是未定义的。您可以通过指定优先级来控制执行顺序。这是通过在方法类中实现`org.springframework.core.Ordered`接口或使用注释对其进行`Order`注释来以常规Spring方式完成的。给定两个方面，从`Ordered.getValue()`（或注释值）返回较低值的方面具有较高的优先级。

当在*同一*方面定义的两条建议都需要在同一个连接点上运行时，排序是未定义的（因为没有办法通过反射为javac编译的类检索声明顺序）。考虑将这些建议方法折叠到每个方面类中每个连接点的一个建议方法中，或者将这些建议重构为单独的方面类 - 可以在方面级别进行排序。

### 11.2.5介绍

简介（在AspectJ中称为类型间声明）使方面能够声明建议对象实现给定接口，并代表这些对象提供该接口的实现。

使用`@DeclareParents`注释进行介绍。此批注用于声明匹配类型具有新父级（因此名称）。例如，给定接口`UsageTracked`和该接口的实现`DefaultUsageTracked`，以下方面声明服务接口的所有实现者也实现`UsageTracked`接口。（例如，为了通过JMX公开统计信息。）

```java
@Aspect
public class UsageTracking {

    @DeclareParents(value="com.xzy.myapp.service.*+", defaultImpl=DefaultUsageTracked.class)
    public static UsageTracked mixin;

    @Before("com.xyz.myapp.SystemArchitecture.businessService() && this(usageTracked)")
    public void recordUsage(UsageTracked usageTracked) {
        usageTracked.incrementUseCount();
    }

}
```

要实现的接口由注释字段的类型确定。注释的 `value`属性`@DeclareParents`是AspectJ类型模式： - 任何匹配类型的bean都将实现UsageTracked接口。请注意，在上面示例的before advice中，服务bean可以直接用作`UsageTracked`接口的实现。如果以编程方式访问bean，您将编写以下内容：

```java
UsageTracked usageTracked = (UsageTracked) context.getBean("myService");
```

### 11.2.6方面实例化模型

（这是一个高级主题，所以如果你刚刚开始使用AOP，你可以安全地跳过它直到以后。）

默认情况下，应用程序上下文中的每个方面都有一个实例。AspectJ将其称为单例实例化模型。它可以与其他的生命周期定义方面： - Spring支持AspectJ的`perthis`和`pertarget` 实例化模型（`percflow, percflowbelow,`和`pertypewithin`目前不支持）。

通过`perthis`在`@Aspect` 注释中指定子句来声明“perthis”方面。让我们看一个例子，然后我们将解释它是如何工作的。

```java
@Aspect("perthis(com.xyz.myapp.SystemArchitecture.businessService())")
public class MyAspect {

    private int someState;

    @Before(com.xyz.myapp.SystemArchitecture.businessService())
    public void recordServiceUsage() {
        // ...
    }

}
```

该`'perthis'`子句的作用是为执行业务服务的每个唯一服务对象创建一个方面实例（每个唯一对象在由切入点表达式匹配的连接点处绑定到'this'）。方法实例是在第一次在服务对象上调用方法时创建的。当服务对象超出范围时，该方面超出范围。在创建方面实例之前，其中没有任何建议执行。一旦创建了方面实例，在其中声明的建议将在匹配的连接点处执行，但仅在服务对象是与此方面相关联的服务对象时执行。有关per子句的更多信息，请参阅AspectJ编程指南。

该`'pertarget'`实例化样板工程完全相同的方式perthis，但在匹配的连接点，为每个唯一目标对象的一个方面的实例。

### 11.2.7示例

既然你已经看到了所有组成部分的工作方式，那就让我们把它们放在一起做一些有用的事情吧！

由于并发问题（例如，死锁失败者），业务服务的执行有时可能会失败。如果重试该操作，下次很可能成功。对于适合在这种情况下重试的业务服务（幂等操作不需要返回给用户进行冲突解决），我们希望透明地重试操作以避免客户端看到 `PessimisticLockingFailureException`。这是明确跨越服务层中的多个服务的要求，因此是通过方面实现的理想选择。

因为我们想要重试操作，所以我们需要使用around建议，以便我们可以多次调用proceed。以下是基本方面实现的外观：

```java
@Aspect
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

    @Around("com.xyz.myapp.SystemArchitecture.businessService()")
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

请注意，该方面实现了`Ordered`接口，因此我们可以将方面的优先级设置为高于事务通知（我们每次重试时都需要一个新的事务）。在`maxRetries`和`order`属性都可以在Spring中配置。主要行动发生在`doConcurrentOperation`周围的建议中。请注意，目前我们正在将重试逻辑应用于所有人`businessService()s`。我们试图继续进行，如果我们失败了，`PessimisticLockingFailureException`我们只需再试一次，除非我们已经用尽所有的重试尝试。

相应的Spring配置是：

```xml
<aop:aspectj-autoproxy/>

<bean id="concurrentOperationExecutor" class="com.xyz.myapp.service.impl.ConcurrentOperationExecutor">
    <property name="maxRetries" value="3"/>
    <property name="order" value="100"/>
</bean>
```

为了优化方面以便它只重试幂等操作，我们可以定义一个 `Idempotent`注释：

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    // marker annotation
}
```

并使用注释来注释服务操作的实现。对方面的更改仅重试幂等操作只涉及改进切入点表达式，以便只有`@Idempotent`操作匹配：

```java
@Around("com.xyz.myapp.SystemArchitecture.businessService() && " +
        "@annotation(com.xyz.myapp.service.Idempotent)")
public Object doConcurrentOperation(ProceedingJoinPoint pjp) throws Throwable {
    ...
}
```

