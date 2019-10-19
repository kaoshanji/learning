# 7.1 IoC容器和bean概述

本章介绍了Spring Framework实现的控制反转（IoC）原理。IoC也称为*依赖注入*（DI）。这是一个过程，通过这个过程，对象定义它们的依赖关系，即它们使用的其他对象，只能通过构造函数参数，工厂方法的参数，或者在构造或从工厂方法返回后在对象实例上设置的属性。 。然后容器 在创建bean时*注入*这些依赖项。这个过程基本上是反向的，因此名称*Inversion of Control*（IoC），bean本身通过使用类的直接构造来控制其依赖关系的实例化或位置，或者诸如*服务定位器*模式。

`org.springframework.beans`和`org.springframework.context`包是Spring框架的IoC容器的基础。该 [`BeanFactory`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/beans/factory/BeanFactory.html) 接口提供了一种能够管理任何类型对象的高级配置机制。 [`ApplicationContext`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/context/ApplicationContext.html) 是`BeanFactory`一个子接口。它增加了与Spring的AOP功能的更容易的集成; 消息资源处理（用于国际化），事件发布; 和特定于应用程序层的上下文，例如`WebApplicationContext` 在Web应用程序中使用的上下文。

简而言之，`BeanFactory`提供了配置框架的基本功能，而`ApplicationContext`添加了更多特定于企业的功能，是`BeanFactory`完整的超集，`BeanFactory`在本章中仅用于Spring的IoC容器的描述。

在Spring中，构成应用程序主干并由Spring IoC *容器*管理的对象称为*bean*。bean是一个由Spring IoC容器实例化，组装和管理的对象。否则，bean只是应用程序中许多对象之一。Bean及其之间的*依赖* 关系反映在容器使用的*配置元数据*中。