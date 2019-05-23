# 7.11 使用JSR 330 规范注解

从Spring 3.0开始，Spring提供对JSR-330标准注释（依赖注入）的支持。这些注释的扫描方式与Spring注释相同。您只需要在类路径中包含相关的jar。

```xml
<dependency> 
    <groupId> javax.inject </ groupId> 
    <artifactId> javax.inject </ artifactId> 
    <version> 1 </ version> 
</ dependency>
```

### 7.11.1使用@Inject和@Named进行依赖注入

而不是`@Autowired`，`@javax.inject.Inject`可以使用如下：

```java
import javax.inject.Inject;

public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Inject
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    public void listMovies() {
        this.movieFinder.findMovies(...);
        ...
    }
}
```

与此同时`@Autowired`，可以`@Inject`在字段级别，方法级别和构造函数 - 参数级别使用。此外，您可以将注入点声明为a `Provider`，允许按需访问较短范围的bean或通过`Provider.get()`调用对其他bean进行延迟访问。作为上述示例的变体：

```java
import javax.inject.Inject;
import javax.inject.Provider;

public class SimpleMovieLister {

    private Provider<MovieFinder> movieFinder;

    @Inject
    public void setMovieFinder(Provider<MovieFinder> movieFinder) {
        this.movieFinder = movieFinder;
    }

    public void listMovies() {
        this.movieFinder.get().findMovies(...);
        ...
    }
}
```

如果要对应注入的依赖项使用限定名称，则应使用`@Named`注释，如下所示：

```java
import javax.inject.Inject;
import javax.inject.Named;

public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Inject
    public void setMovieFinder(@Named("main") MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}
```

喜欢`@Autowired`，`@Inject`也可以和`java.util.Optional`或 一起使用`@Nullable`。这更适用于此，因为`@Inject`没有`required`属性。

```java
public class SimpleMovieLister {

    @Inject
    public void setMovieFinder(Optional<MovieFinder> movieFinder) {
        ...
    }
}
```

```java
public class SimpleMovieLister {

    @Inject
    public void setMovieFinder(@Nullable MovieFinder movieFinder) {
        ...
    }
}
```

### 7.11.2 @Named和@ManagedBean：@Component注释的标准等价物

取而代之`@Component`，`@javax.inject.Named`或者`javax.annotation.ManagedBean`可以如下使用：

```java
import javax.inject.Inject;
import javax.inject.Named;

@Named("movieListener")  // @ManagedBean("movieListener") could be used as well
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Inject
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}
```

在`@Component`不指定组件名称的情况下使用是很常见的。 `@Named`可以以类似的方式使用：

```java
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Inject
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}
```

使用`@Named`或时`@ManagedBean`，可以使用与使用Spring注释时完全相同的方式使用组件扫描：

```java
@Configuration
@ComponentScan(basePackages = "org.example")
public class AppConfig  {
    ...
}
```

与此相反`@Component`，JSR-330 `@Named`和JSR-250 `ManagedBean` 注释不可组合。请使用Spring的构造型模型来构建自定义组件注解。

### 7.11.3 JSR-330标准注释的局限性

使用标准注释时，重要的是要知道某些重要功能不可用，如下表所示：



**表7.6。Spring组件模型元素与JSR-330变体**

| 弹簧                | javax.inject。*       | javax.inject限制/评论                                        |
| ------------------- | --------------------- | ------------------------------------------------------------ |
| @Autowired          | @Inject               | `@Inject`没有'必需'属性; 可以与Java 8一起使用`Optional`。    |
| @Component          | @Named / @ManagedBean | JSR-330不提供可组合模型，只是一种识别命名组件的方法。        |
| @Scope("singleton") | @Singleton            | JSR-330的默认范围就像Spring一样`prototype`。但是，为了使其与Spring的一般默认值保持一致，`singleton`默认情况下在Spring容器中声明的JSR-330 bean是一个默认值。为了使用除以外的范围`singleton`，您应该使用Spring的`@Scope`注释。`javax.inject`还提供了 [@Scope](https://download.oracle.com/javaee/6/api/javax/inject/Scope.html)注释。然而，这个仅用于创建自己的注释。 |
| @Qualifier          | @Qualifier / @Named   | `javax.inject.Qualifier`只是构建自定义限定符的元注释。具体字符串限定符（如`@Qualifier`带有值的Spring ）可以通过关联`javax.inject.Named`。 |
| @Value              | -                     | 没有等价物                                                   |
| @Required           | -                     | 没有等价物                                                   |
| @Lazy               | -                     | 没有等价物                                                   |
| ObjectFactory       | Provider              | `javax.inject.Provider`是一个直接替代Spring的方法`ObjectFactory`，只需要更短的`get()`方法名称。它也可以与Spring `@Autowired`或非注释构造函数和setter方法结合使用。 |

