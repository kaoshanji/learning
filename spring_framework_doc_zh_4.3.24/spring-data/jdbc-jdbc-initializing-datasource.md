# 19.9 初始化数据源

该`org.springframework.jdbc.datasource.init`包提供了对初始化现有的支持`DataSource`。嵌入式数据库支持提供了一个用于创建和初始化`DataSource`应用程序的选项，但有时您需要初始化在某个服务器上运行的实例。

### 19.9.1使用Spring XML初始化数据库

如果要初始化数据库并且可以提供对`DataSource` bean 的引用，请使用命名空间中的`initialize-database`标记`spring-jdbc`：

```xml
<jdbc:initialize-database data-source="dataSource">
    <jdbc:script location="classpath:com/foo/sql/db-schema.sql"/>
    <jdbc:script location="classpath:com/foo/sql/db-test-data.sql"/>
</jdbc:initialize-database>
```

上面的示例执行针对数据库指定的两个脚本：第一个脚本创建模式，第二个脚本使用测试数据集填充表。脚本位置也可以是具有通用蚂蚁风格的通配符的模式，用于Spring中的资源（例如 `classpath*:/com/foo/**/sql/*-data.sql`）。如果使用模式，脚本将按其URL或文件名的词汇顺序执行。

数据库初始化程序的默认行为是无条件地执行提供的脚本。这并不总是您想要的，例如，如果您正在对已经包含测试数据的数据库执行脚本。通过遵循首先创建表格然后插入数据的公共模式（如上所示）来减少意外删除数据的可能性 - 如果表格已经存在，则第一步将失败。

但是，为了更好地控制现有数据的创建和删除，XML命名空间提供了一些其他选项。第一个是打开和关闭初始化的标志。这可以根据环境设置（例如，从系统属性或环境bean中提取布尔值），例如：

```xml
<jdbc:initialize-database data-source="dataSource"
    enabled="#{systemProperties.INITIALIZE_DATABASE}">
    <jdbc:script location="..."/>
</jdbc:initialize-database>
```

控制现有数据发生情况的第二个选择是更容忍失败。为此，您可以控制初始化程序忽略它从脚本执行的SQL中的某些错误的能力，例如：

```xml
<jdbc:initialize-database data-source="dataSource" ignore-failures="DROPS">
    <jdbc:script location="..."/>
</jdbc:initialize-database>
```

在这个例子中，我们说我们期望有时脚本将针对空数据库执行，并且`DROP`脚本中有一些语句因此会失败。因此失败的SQL `DROP`语句将被忽略，但其他失败将导致异常。如果您的SQL方言不支持`DROP … IF EXISTS`（或类似），但您希望在重新创建之前无条件地删除所有测试数据，这将非常有用。在这种情况下，第一个脚本通常是一组`DROP`语句，后跟一组`CREATE`语句。

该`ignore-failures`选项可以设置为`NONE`（默认值），`DROPS`（忽略失败的丢弃）或`ALL`（忽略所有失败）。

`;`如果`;`脚本中根本不存在该字符，则每个语句应该用新行分隔。您可以通过脚本控制全局或脚本，例如：

```xml
<jdbc:initialize-database data-source="dataSource" separator="@@">
    <jdbc:script location="classpath:com/foo/sql/db-schema.sql" separator=";"/>
    <jdbc:script location="classpath:com/foo/sql/db-test-data-1.sql"/>
    <jdbc:script location="classpath:com/foo/sql/db-test-data-2.sql"/>
</jdbc:initialize-database>
```

在这个例子中，这两个`test-data`脚本使用`@@`的语句分隔符，只有`db-schema.sql`用途`;`。此配置指定默认分隔符是`@@`并覆盖`db-schema`脚本的默认值。

如果您需要比从XML命名空间获得的更多控制，您可以直接使用 `DataSourceInitializer`它并将其定义为应用程序中的组件。

#### 初始化依赖于数据库的其他组件

一大类应用程序可以使用数据库初始化程序而没有进一步的复杂性：那些在Spring上下文启动之后才使用数据库的应用程序。如果您的应用程序*不是*其中之一，那么您可能需要阅读本节的其余部分。

数据库初始化程序依赖于`DataSource`实例并执行其初始化回调中提供的脚本（类似于`init-method`XML bean定义`@PostConstruct`中的一个，组件中的`afterPropertiesSet()` 方法或实现的组件中的方法`InitializingBean`）。如果其他bean依赖于相同的数据源并且还在初始化回调中使用数据源，那么可能存在问题，因为数据尚未初始化。一个常见的例子是一个缓存，它在应用程序启动时急切地初始化并从数据库加载数据。

要解决此问题，您有两种选择：将缓存初始化策略更改为稍后阶段，或者确保首先初始化数据库初始化程序。

如果应用程序在您的控制范围内，则第一个选项可能很容易，而不是其他选项。关于如何实现这一点的一些建议包括：

- 在首次使用时使缓存初始化，这可以缩短应用程序启动时间。
- 让你的缓存或初始化缓存实现一个单独的组件 `Lifecycle`或`SmartLifecycle`。当应用程序上下文启动时， `SmartLifecycle`如果`autoStartup`设置了其标志，则可以自动启动，并且`Lifecycle`可以通过调用`ConfigurableApplicationContext.start()` 封闭的上下文手动启动a 。
- 使用Spring `ApplicationEvent`或类似的自定义观察器机制来触发缓存初始化。`ContextRefreshedEvent`当它准备好使用时（在所有bean初始化之后）总是由上下文发布，因此这通常是一个有用的钩子（这是`SmartLifecycle`默认情况下的工作原理）。

第二种选择也很简单。关于如何实现这一点的一些建议包括：

- 依赖于Spring的默认行为，`BeanFactory`即bean按注册顺序初始化。您可以通过采用`<import/>`XML配置中的一组元素的通用实践来轻松地对您的应用程序模块进行排序，并确保首先列出数据库和数据库初始化。
- 分离`DataSource`使用它的业务组件，并通过将它们放在单独的`ApplicationContext`实例中来控制它们的启动顺序（例如，父上下文包含`DataSource`，而子上下文包含业务组件）。这种结构在Spring Web应用程序中很常见，但可以更普遍地应用。