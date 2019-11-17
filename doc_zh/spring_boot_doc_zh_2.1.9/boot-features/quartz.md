# 41、quartz

Spring Boot为使用[Quartz Scheduler](https://www.quartz-scheduler.org/)提供了许多便利，包括`spring-boot-starter-quartz`“ Starter”。如果Quartz可用，`Scheduler`则自动配置a（通过`SchedulerFactoryBean`抽象）。

以下类型的Bean会被自动拾取并与关联`Scheduler`：

- `JobDetail`：定义特定的作业。 `JobDetail`实例可以使用`JobBuilder`API 构建。
- `Calendar`。
- `Trigger`：定义何时触发特定作业。

默认情况下，使用内存`JobStore`。但是，如果`DataSource`应用程序中有可用的bean，并且`spring.quartz.job-store-type`属性已相应配置，则可以配置基于JDBC的存储，如以下示例所示：

```bash
spring.quartz.job-store-type=jdbc
```

使用JDBC存储时，可以在启动时初始化模式，如以下示例所示：

```bash
spring.quartz.jdbc.initialize-schema=always
```

| ![[警告]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/warning.png) |
| ------------------------------------------------------------ |
| 默认情况下，使用Quartz库随附的标准脚本检测并初始化数据库。这些脚本将删除现有表，并在每次重新启动时删除所有触发器。也可以通过设置`spring.quartz.jdbc.schema`属性来提供自定义脚本。 |

要让Quartz使用`DataSource`应用程序的main之外的其他代码`DataSource`，请声明一个`DataSource`bean，并用注释其`@Bean`方法`@QuartzDataSource`。这样做可确保和`DataSource`都使用特定于Quartz的`SchedulerFactoryBean`模式进行初始化。

默认情况下，通过配置创建的作业将不会覆盖从持久性作业存储中读取的已注册作业。要启用覆盖现有作业定义的功能，请设置该`spring.quartz.overwrite-existing-jobs`属性。

可以使用`spring.quartz`属性和`SchedulerFactoryBeanCustomizer`bean 来定制Quartz Scheduler配置，从而允许以编程方式进行`SchedulerFactoryBean`定制。可以使用来定制高级Quartz配置属性`spring.quartz.properties.*`。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 特别是，`Executor`bean没有与调度程序相关联，因为Quartz通过提供了一种配置调度程序的方法`spring.quartz.properties`。如果您需要自定义任务执行器，请考虑实现`SchedulerFactoryBeanCustomizer`。 |

作业可以定义设置器以注入数据映射属性。常规豆也可以类似的方式注入，如以下示例所示：

```java
public class SampleJob extends QuartzJobBean {

	private MyService myService;

	private String name;

	// Inject "MyService" bean
	public void setMyService(MyService myService) { ... }

	// Inject the "name" job data property
	public void setName(String name) { ... }

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		...
	}

}
```