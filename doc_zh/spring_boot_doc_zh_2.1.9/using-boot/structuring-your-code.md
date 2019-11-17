# 14、构建代码

Spring Boot不需要任何特定的代码布局即可工作。但是，有一些最佳做法会有所帮助。

## 14.1使用“默认”包

当一个类不包含`package`声明时，它被认为是在“默认包”中。通常不建议使用“默认程序包”，应避免使用。这可能会导致使用了Spring启动应用程序的特殊问题`@ComponentScan`，`@EntityScan`或`@SpringBootApplication`注解，因为从每一个罐子每一个类被读取。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 我们建议您遵循Java建议的程序包命名约定，并使用反向域名（例如`com.example.project`）。 |

## 14.2查找主要应用程序类别

我们通常建议您将主应用程序类放在其他类之上的根包中。该[`@SpringBootApplication`注解](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-using-springbootapplication-annotation.html)往往放在你的主类，它隐含地定义为某些项目一基地“搜索包”。例如，如果您正在编写JPA应用程序，`@SpringBootApplication`则使用带注释的类的包搜索`@Entity`项目。使用根软件包还允许组件扫描仅应用于您的项目。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如果您不想使用`@SpringBootApplication`，它导入的`@EnableAutoConfiguration`和`@ComponentScan`注释将定义该行为，因此您也可以使用它。 |

以下清单显示了典型的布局：

```java
com
 +- example
     +- myapplication
         +- Application.java
         |
         +- customer
         |   +- Customer.java
         |   +- CustomerController.java
         |   +- CustomerService.java
         |   +- CustomerRepository.java
         |
         +- order
             +- Order.java
             +- OrderController.java
             +- OrderService.java
             +- OrderRepository.java
```

该`Application.java`文件将声明`main`方法以及basic方法，`@SpringBootApplication`如下所示：

```java
package com.example.myapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
```

------