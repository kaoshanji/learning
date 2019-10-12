# 15、配置类

Spring Boot支持基于Java的配置。尽管可以`SpringApplication`与XML源一起使用，但是我们通常建议您的主要源为单个`@Configuration`类。通常，定义`main`方法的类是主要的候选者`@Configuration`。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 在Internet上已经发布了许多使用XML配置的Spring配置示例。如果可能，请始终尝试使用等效的基于Java的配置。搜索`Enable*`注释可以是一个很好的起点。 |

## 15.1导入其他配置类

您无需将所有内容都`@Configuration`放在一个类中。所述`@Import`注释可以用于导入额外的配置类。另外，您可以`@ComponentScan`用来自动拾取所有Spring组件，包括`@Configuration`类。

## 15.2导入XML配置

如果绝对必须使用基于XML的配置，我们建议您仍然从一个`@Configuration`类开始。然后，您可以使用`@ImportResource`批注来加载XML配置文件。

