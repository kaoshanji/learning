# 7.2 容器概述

接口`org.springframework.context.ApplicationContext`代表Spring IoC容器，负责实例化、配置和组装上述bean。容器通过读取配置元数据获取有关要实例化、配置和组装的对象的指令。配置元数据以XML、Java注释或Java代码表示。它允许您表达组成应用程序的对象以及这些对象之间丰富的相互依赖性。

`ApplicationContext`是Spring的开箱即用的几个接口实现。在独立应用程序中，通常会创建一个[`ClassPathXmlApplicationContext`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/context/support/ClassPathXmlApplicationContext.html) 或[`FileSystemXmlApplicationContext`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/context/support/FileSystemXmlApplicationContext.html)。虽然XML一直是定义配置元数据的传统格式，但您可以通过提供少量XML配置来声明性地支持这些额外的元数据格式，从而指示容器使用Java注释或代码作为元数据格式。

在大多数应用程序方案中，不需要用户用显式代码来实例化Spring IoC容器的一个或多个实例。例如，在Web应用程序场景中，应用程序文件中的简单八行（左右）样板Web描述符XML `web.xml`通常就足够了。

下图是Spring工作原理的高级视图。您的应用程序类与配置元数据相结合，以便在`ApplicationContext`创建和初始化之后，您拥有一个完全配置且可执行的系统或应用程序。



**图7.1。Spring IoC容器**

![container-magic](../images/container-magic.png)

### 7.2.1配置元数据

如上图所示，Spring IoC容器使用一种*配置元数据*形式 ; 此配置元数据表示您作为应用程序开发人员告诉Spring容器如何在应用程序中实例化、配置和组装对象。

传统上，配置元数据以简单直观的XML格式提供，本章的大部分内容用于传达Spring IoC容器的关键概念和功能。

基于XML的元数据*不是*唯一允许的配置元数据形式。Spring IoC容器本身*完全*与实际编写此配置元数据的格式分离。

Spring配置包含容器必须管理的至少一个且通常不止一个bean定义。基于XML的配置元数据显示这些bean配置为`<bean/>`顶级元素<beans/>`内的元素。Java配置通常`@Bean`在`@Configuration`类中使用带注释的方法。

这些bean定义对应于构成应用程序的实际对象。通常，您定义服务层对象，数据访问对象（DAO），表示对象（如Struts `Action`实例），基础结构对象（如Hibernate`SessionFactories`，JMS `Queues`等）。通常，不会在容器中配置细粒度域对象，因为创建和加载域对象通常由DAO和业务逻辑负责。但是，您可以使用Spring与AspectJ的集成来配置在IoC容器控制之外创建的对象。

以下示例显示了基于XML的配置元数据的基本结构：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="..." class="...">
        <！ - 此bean的协作者和配置到这里 - > 
    </bean>

    <bean id="..." class="...">
        <！ - 此bean的协作者和配置到这里 - > 
    </bean>

    <！ - 更多bean定义到这里 - >

</beans>
```

该`id`属性是一个字符串，用于标识单个bean定义。该`class`属性定义bean的类型并使用完全限定的类名。id属性的值指的是协作对象。

### 7.2.2实例化容器

实例化Spring IoC容器非常简单。提供给`ApplicationContext`构造函数的位置路径实际上是资源字符串，允许容器从各种外部资源（如本地文件系统，Java等）加载配置元数据`CLASSPATH`。

```xml
ApplicationContext context = new ClassPathXmlApplicationContext（“services.xml”，“daos.xml”）;
```

以下示例显示了服务层对象`(services.xml)`配置文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">
    <！ - services  - > 

    <bean id="petStore" class="org.springframework.samples.jpetstore.services.PetStoreServiceImpl">
        <property name="accountDao" ref="accountDao"/>
        <property name="itemDao" ref="itemDao"/>
        <！ - 此bean的其他协作者和配置在这里 - > 
    </ bean> 

    <！ - 服务的更多bean定义到这里 - > 
</ beans>
```

以下示例显示了数据访问对象`daos.xml`文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="accountDao"
        class="org.springframework.samples.jpetstore.dao.jpa.JpaAccountDao">
        <！ - 此bean的其他协作者和配置到这里 - > 
    </bean>

    <bean id="itemDao" class="org.springframework.samples.jpetstore.dao.jpa.JpaItemDao">
        <！ - 此bean的其他协作者和配置在这里 - > 
    </ bean> 

    <！ - 更多数据访问对象的bean定义到这里 - - > 
</ beans>
```

在前面的示例中，服务层由类`PetStoreServiceImpl`，和类型的两个数据访问对象`JpaAccountDao`和`JpaItemDao`（基于JPA对象/关系映射标准）。该`property name`元素是指JavaBean属性的名称，以及`ref`元素指的是另一个bean定义的名称。元素`id`和`ref`元素之间的这种联系表达了协作对象之间的依赖关系。

#### 编写基于XML的配置元数据

让bean定义跨越多个XML文件会很有用。通常，每个单独的XML配置文件都代表架构中的逻辑层或模块。

您可以使用应用程序上下文构造函数从所有这些XML片段加载bean定义。此构造函数采用多个`Resource`位置，如上一节中所示。或者，使用一个或多个`<import/>`元素来从另一个或多个文件加载bean定义。例如：

```xml
<beans>
    <import resource="services.xml"/>
    <import resource="resources/messageSource.xml"/>
    <import resource="/resources/themeSource.xml"/>

    <bean id="bean1" class="..."/>
    <bean id="bean2" class="..."/>
</beans>
```

在前面的例子中，外部bean定义是从三个文件加载： `services.xml`，`messageSource.xml`，和`themeSource.xml`。所有位置路径都与执行导入的定义文件相关，因此`services.xml`必须与执行导入的文件位于相同的目录或类路径位置， `messageSource.xml`而且`themeSource.xml`必须位于`resources`导入文件位置下方的位置。如您所见，忽略前导斜杠，但鉴于这些路径是相对的，最好不要使用斜杠。`<beans/>`根据Spring Schema，导入的文件的内容（包括顶级元素）必须是有效的XML bean定义。

import指令是beans命名空间本身提供的功能。除了普通bean定义之外，Spring还有其他一系列可用XML命名空间的配置功能，例如“context”和“util”命名空间。

### 7.2.3使用容器

它`ApplicationContext`是高级工厂的接口，能够维护不同bean及其依赖项的注册表。使用该方法，`T getBean(String name, Class<T> requiredType)`您可以检索Bean的实例。

在`ApplicationContext`可以读取bean定义并访问它们，如下所示：

```java
// create and configure beans
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");

// retrieve configured instance
PetStoreService service = context.getBean("petStore", PetStoreService.class);

// use configured instance
List<String> userList = service.getUsernameList();
```