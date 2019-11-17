# 8.6 资源依赖

如果bean本身将通过某种动态过程确定并提供资源路径，那么bean使用`ResourceLoader` 接口加载资源可能是有意义的。以某种模板的加载为例，其中所需的特定资源取决于用户的角色。如果资源是静态的，那么`ResourceLoader` 完全消除接口的使用是有意义的，只需让bean公开`Resource`它需要的属性，并期望它们被注入其中。

然后注入这些属性变得微不足道的是，所有应用程序上下文都注册并使用`PropertyEditor`可以将`String`路径转换为`Resource`对象的特殊JavaBeans 。因此，如果`myBean`具有类型的模板属性`Resource`，则可以使用该资源的简单字符串进行配置，如下所示：

```xml
<bean id="myBean" class="...">
    <property name="template" value="some/resource/path/myTemplate.txt"/>
</bean>
```

请注意，资源路径没有前缀，因为应用程序上下文本身将用作`ResourceLoader`，资源本身将通过，或（根据情况）加载 `ClassPathResource`，具体取决于上下文的确切类型。`FileSystemResource``ServletContextResource`

如果需要强制使用特定`Resource`类型，则可以使用前缀。以下两个示例显示了如何强制a `ClassPathResource`和a `UrlResource`（后者用于访问文件系统文件）。

```xml
<property name="template" value="classpath:some/resource/path/myTemplate.txt">
```

```xml
<property name="template" value="file:///some/resource/path/myTemplate.txt"/>
```

