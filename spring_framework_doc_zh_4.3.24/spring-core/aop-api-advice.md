# 12.3 通知

现在让我们来看看Spring AOP如何处理建议。

### 12.3.1建议生命周期

每个建议都是一个Spring bean。建议实例可以在所有建议对象之间共享，也可以对每个建议对象唯一。这对应于*每个类*或 *每个实例的*建议。

每类建议最常使用。它适用于交易顾问等通用建议。这些不依赖于代理对象的状态或添加新状态; 他们只是按照方法和论点行事。

每个实例的建议适用于介绍，以支持mixin。在这种情况下，建议将状态添加到代理对象。

可以在同一个AOP代理中混合使用共享和每个实例的建议。

### 12.3.2 Spring中的建议类型

Spring提供了几种开箱即用的建议类型，并且可以扩展以支持任意建议类型。让我们看看基本概念和标准建议类型。

#### 拦截建议

Spring中最基本的建议类型是*拦截建议*。

Spring符合AOP Alliance接口，可以使用方法拦截来获取建议。实现around建议的MethodInterceptors应该实现以下接口：

```java
public interface MethodInterceptor extends Interceptor {

    Object invoke(MethodInvocation invocation) throws Throwable;
}
```

该方法的`MethodInvocation`参数`invoke()`公开了被调用的方法; 目标连接点; AOP代理; 和方法的参数。该 `invoke()`方法应该返回调用的结果：连接点的返回值。

一个简单的`MethodInterceptor`实现如下：

```java
public class DebugInterceptor implements MethodInterceptor {

    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("Before: invocation=[" + invocation + "]");
        Object rval = invocation.proceed();
        System.out.println("Invocation returned");
        return rval;
    }
}
```

请注意对MethodInvocation `proceed()`方法的调用。这沿拦截器链向下进入连接点。大多数拦截器都会调用此方法，并返回其返回值。但是，与任何around建议一样，MethodInterceptor可以返回不同的值或抛出异常，而不是调用proceed方法。但是，你没有充分的理由不想这样做！

MethodInterceptors提供与其他符合AOP Alliance标准的AOP实现的互操作性。本节其余部分讨论的其他建议类型实现了常见的AOP概念，但是采用Spring特定的方式。虽然使用最具体的建议类型有一个优势，但如果您可能希望在另一个AOP框架中运行该方面，请坚持使用MethodInterceptor建议。请注意，切入点目前在框架之间不可互操作，AOP联盟目前不定义切入点接口。

#### Before advice

更简单的建议类型是*之前的建议*。这不需要`MethodInvocation` 对象，因为它只在进入方法之前被调用。

之前建议的主要优点是不需要调用该`proceed()` 方法，因此不会无意中无法继续拦截链。

的`MethodBeforeAdvice`接口如下所示。（Spring的API设计允许在建议之前提供字段，尽管通常的对象适用于字段拦截，并且Spring不太可能实现它）。

```java
public interface MethodBeforeAdvice extends BeforeAdvice {

    void before(Method m, Object[] args, Object target) throws Throwable;
}
```

请注意返回类型是`void`。在建议之前可以在连接点执行之前插入自定义行为，但不能更改返回值。如果before advice抛出异常，这将中止拦截器链的进一步执行。异常将传播回拦截链。如果未选中，或者在被调用方法的签名上，它将直接传递给客户端; 否则它将被AOP代理包装在未经检查的异常中。

Spring中一个before建议的示例，它计算所有方法调用：

```java
public class CountingBeforeAdvice implements MethodBeforeAdvice {

    private int count;

    public void before(Method m, Object[] args, Object target) throws Throwable {
        ++count;
    }

    public int getCount() {
        return count;
    }
}
```

可以与任何切入点一起使用。

#### Throws advice

如果连接点引发异常，则在返回连接点后调用*抛出建议*。Spring提供类型投掷建议。请注意，这意味着该 `org.springframework.aop.ThrowsAdvice`接口不包含任何方法：它是一个标记接口，用于标识给定对象实现一个或多个类型化throws建议方法。这些应该是以下形式：

```java
afterThrowing([Method, args, target], subclassOfThrowable)
```

只需要最后一个参数。方法签名可以有一个或四个参数，具体取决于通知方法是否对方法和参数感兴趣。以下类是throws建议的示例。

如果`RemoteException`抛出a（包括子类），则调用以下建议：

```java
public class RemoteThrowsAdvice implements ThrowsAdvice {

    public void afterThrowing(RemoteException ex) throws Throwable {
        // Do something with remote exception
    }
}
```

如果`ServletException`抛出a，则调用以下建议。与上面的建议不同，它声明了4个参数，因此它可以访问被调用的方法，方法参数和目标对象：

```java
public class ServletThrowsAdviceWithArguments implements ThrowsAdvice {

    public void afterThrowing(Method m, Object[] args, Object target, ServletException ex) {
        // Do something with all arguments
    }
}
```

最后一个例子示出了如何这两种方法可以在一个单一的类，它可以同时处理可以使用`RemoteException`和`ServletException`。可以在单个类中组合任意数量的throws建议方法。

```java
public static class CombinedThrowsAdvice implements ThrowsAdvice {

    public void afterThrowing(RemoteException ex) throws Throwable {
        // Do something with remote exception
    }

    public void afterThrowing(Method m, Object[] args, Object target, ServletException ex) {
        // Do something with all arguments
    }
}
```

如果throws-advice方法本身抛出异常，它将覆盖原始异常（即更改抛出给用户的异常）。覆盖异常通常是RuntimeException; 这与任何方法签名兼容。但是，如果throws-advice方法抛出一个已检查的异常，则它必须匹配目标方法的声明异常，因此在某种程度上耦合到特定的目标方法签名。*不要抛出与目标方法签名不兼容的未声明的已检查异常！*

可以与任何切入点一起使用。

#### After Returning advice

在Spring中返回后的建议必须实现 *org.springframework.aop.AfterReturningAdvice*接口，如下所示：

```java
public interface AfterReturningAdvice extends Advice {

    void afterReturning(Object returnValue, Method m, Object[] args, Object target)
            throws Throwable;
}
```

返回后的建议可以访问返回值（它无法修改），调用方法，方法参数和目标。

返回通知后的以下内容计算所有未抛出异常的成功方法调用：

```java
public class CountingAfterReturningAdvice implements AfterReturningAdvice {

    private int count;

    public void afterReturning(Object returnValue, Method m, Object[] args, Object target)
            throws Throwable {
        ++count;
    }

    public int getCount() {
        return count;
    }
}
```

此建议不会更改执行路径。如果它抛出异常，则会抛出拦截器链而不是返回值。

可以使用任何切入点。

#### Introduction advice

Spring将介绍建议视为一种特殊的拦截建议。

简介需要一个`IntroductionAdvisor`和一个`IntroductionInterceptor`实现以下接口：

```java
public interface IntroductionInterceptor extends MethodInterceptor {

    boolean implementsInterface(Class intf);
}
```

`invoke()`从AOP Alliance `MethodInterceptor`接口继承的方法必须实现介绍：即，如果被调用的方法在引入的接口上，则引入拦截器负责处理方法调用 - 它无法调用`proceed()`。

引言建议不能与任何切入点一起使用，因为它仅适用于类，而不是方法，级别。您只能使用带有`IntroductionAdvisor`以下方法的介绍建议 ：

```java
public interface IntroductionAdvisor extends Advisor, IntroductionInfo {

    ClassFilter getClassFilter();

    void validateInterfaces() throws IllegalArgumentException;
}

public interface IntroductionInfo {

    Class[] getInterfaces();
}
```

没有`MethodMatcher`，因此没有`Pointcut`与介绍建议相关联。只有类过滤是合乎逻辑的。

该`getInterfaces()`方法返回此顾问程序引入的接口。

该`validateInterfaces()`方法在内部用于查看引入的接口是否可以由已配置的接口实现`IntroductionInterceptor`。

让我们看一下Spring测试套件中的一个简单示例。假设我们想要将以下接口引入一个或多个对象：

```java
public interface Lockable {
    void lock();
    void unlock();
    boolean locked();
}
```

这说明了一个*混合*。我们希望能够将建议对象转换为Lockable，无论其类型如何，并调用锁定和解锁方法。如果我们调用lock（）方法，我们希望所有setter方法都抛出一个`LockedException`。因此，我们可以添加一个方面，提供使对象不可变的能力，而不需要它们知道它：AOP的一个很好的例子。

首先，我们需要一个`IntroductionInterceptor`能够解决繁重问题的工作。在这种情况下，我们扩展了`org.springframework.aop.support.DelegatingIntroductionInterceptor` 便利类。我们可以直接实现IntroductionInterceptor，但`DelegatingIntroductionInterceptor`在大多数情况下使用 最好。

该`DelegatingIntroductionInterceptor`设计将导入委托到真正实现导入接口（S）的，隐藏拦截的使用来做到这一点。可以使用构造函数参数将委托设置为任何对象; 默认委托（当使用no-arg构造函数时）就是这个。因此，在下面的示例中，委托是`LockMixin`子类`DelegatingIntroductionInterceptor`。给定一个委托（默认情况下），一个`DelegatingIntroductionInterceptor`实例查找委托实现的所有接口（除了IntroductionInterceptor），并支持对其中任何接口的介绍。子类`LockMixin`可以调用该`suppressInterface(Class intf)` 方法来抑制不应该公开的接口。但是，无论`IntroductionInterceptor`准备支持多少接口，都可以 `IntroductionAdvisor`used将控制实际暴露的接口。引入的接口将隐藏目标对同一接口的任何实现。

从而`LockMixin`扩展`DelegatingIntroductionInterceptor`和实现`Lockable` 自己。超类自动选择可以支持Lockable引入，因此我们不需要指定。我们可以用这种方式引入任意数量的接口。

请注意`locked`实例变量的使用。这有效地将附加状态添加到目标对象中保存的状态。

```java
public class LockMixin extends DelegatingIntroductionInterceptor implements Lockable {

    private boolean locked;

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }

    public boolean locked() {
        return this.locked;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (locked() && invocation.getMethod().getName().indexOf("set") == 0) {
            throw new LockedException();
        }
        return super.invoke(invocation);
    }

}
```

通常没有必要覆盖该`invoke()`方法： `DelegatingIntroductionInterceptor`实现 - 如果引入方法则调用委托方法，否则向连接点前进 - 通常就足够了。在本例中，我们需要添加一个检查：如果处于锁定模式，则不能调用setter方法。

需要的介绍顾问很简单。它需要做的只是保持一个独特的 `LockMixin`实例，并指定引入的接口 - 在这种情况下，只是 `Lockable`。一个更复杂的例子可能会引用引入拦截器（它将被定义为原型）：在这种情况下，没有与a相关的配置`LockMixin`，所以我们只是使用它来创建它`new`。

```java
public class LockMixinAdvisor extends DefaultIntroductionAdvisor {

    public LockMixinAdvisor() {
        super(new LockMixin(), Lockable.class);
    }
}
```

我们可以非常简单地应用这个顾问：它不需要配置。（但是，*有* 必要：`IntroductionInterceptor`没有 *IntroductionAdvisor就*不可能使用。）和往常一样，顾问必须是每个实例，因为它是有状态的。我们需要一个不同的实例`LockMixinAdvisor`，因此 `LockMixin`需要每个建议的对象。顾问包括建议对象的状态的一部分。

我们可以`Advised.addAdvisor()`像其他任何顾问一样，以编程方式，使用XML配置中的方法或（推荐方式）应用此顾问程序。下面讨论的所有代理创建选项，包括“自动代理创建器”，正确处理引入和有状态混合。