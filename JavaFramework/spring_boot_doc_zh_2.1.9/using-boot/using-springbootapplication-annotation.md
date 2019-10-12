# 18、使用 @SpringBootApplication 注解

许多Spring Boot开发人员喜欢他们的应用程序使用自动配置，组件扫描，并能够在其“应用程序类”上定义额外的配置。单个`@SpringBootApplication`注释可用于启用这三个功能，即：

- `@EnableAutoConfiguration`：启用[Spring Boot的自动配置机制](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-auto-configuration.html)
- `@ComponentScan`：启用`@Component`对应用程序所在的软件包的扫描（请参阅[最佳实践](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-structuring-your-code.html)）
- `@Configuration`：允许在上下文中注册额外的bean或导入其他配置类

的`@SpringBootApplication`注释是相当于使用`@Configuration`，`@EnableAutoConfiguration`以及`@ComponentScan`与他们的默认属性，如显示在下面的例子：

```java
package com.example.myapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| `@SpringBootApplication`还提供了别名定制的属性`@EnableAutoConfiguration`和`@ComponentScan`。 |

这些功能都不是强制性的，您可以选择用它启用的任何功能替换此单个注释。例如，您可能不想在应用程序中使用组件扫描：

```java
package com.example.myapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import({ MyConfig.class, MyAnotherConfig.class })
public class Application {

	public static void main(String[] args) {
			SpringApplication.run(Application.class, args);
	}

}
```

在此示例中，`Application`与其他任何Spring Boot应用程序一样，除了`@Component`不会自动检测到-带注释的类并且显式导入用户定义的Bean（请参阅参考资料`@Import`）