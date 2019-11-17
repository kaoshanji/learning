# 8.5 ResourceLoaderAware接口

该`ResourceLoaderAware`接口是一个特殊的回调接口，用于标识希望提供`ResourceLoader`引用的组件：

```java
public interface ResourceLoaderAware {

    void setResourceLoader(ResourceLoader resourceLoader);
}
```

当一个类实现`ResourceLoaderAware`并部署到应用程序上下文中时（作为Spring管理的bean），它被`ResourceLoaderAware`应用程序上下文识别。然后，应用程序上下文将调用 `setResourceLoader(ResourceLoader)`，将自身作为参数提供（请记住，Spring中的所有应用程序上下文都实现了`ResourceLoader`接口）。

当然，由于a `ApplicationContext`是a `ResourceLoader`，bean也可以实现`ApplicationContextAware`接口并直接使用提供的应用程序上下文来加载资源，但一般情况下，`ResourceLoader`如果需要的话，最好使用专用 接口。代码只是耦合到资源加载接口，可以将其视为实用程序接口，而不是整个Spring `ApplicationContext`接口。

从Spring 2.5开始，您可以依靠自动装配`ResourceLoader`作为实现`ResourceLoaderAware`接口的替代方案。“传统” `constructor`和 `byType`自动装配模式（如 第7.4.5节“自动装配协作者”中所述）现在能够分别为`ResourceLoader`构造函数参数或setter方法参数提供类型的依赖性。为了获得更大的灵活性（包括自动装配字段和多参数方法的能力），请考虑使用新的基于注释的自动装配功能。在这种情况下，只要有问题的字段，构造函数或方法带有，`ResourceLoader`就会将自动装入一个字段，构造函数参数或方法参数中，该参数需要该`ResourceLoader`类型。`@Autowired` 注解。