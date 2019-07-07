# 11.4 选择哪种风格的AOP

一旦确定某个方面是实现给定需求的最佳方法，您如何决定使用Spring AOP或AspectJ，以及Aspect语言（代码）样式，@ AspectJ注释样式还是Spring XML样式之间？这些决策受到许多因素的影响，包括应用程序要求，开发工具和团队对AOP的熟悉程度。

### 11.4.1 Spring AOP还是完整的AspectJ？

使用最简单的方法。Spring AOP比使用完整的AspectJ更简单，因为不需要将AspectJ编译器/ weaver引入开发和构建过程。如果您只需要建议在Spring bean上执行操作，那么Spring AOP是正确的选择。如果您需要建议不由Spring容器管理的对象（通常是域对象），那么您将需要使用AspectJ。如果您希望建议除简单方法执行之外的连接点（例如，字段获取或设置连接点等），您还需要使用AspectJ。

使用AspectJ时，您可以选择AspectJ语言语法（也称为“代码样式”）或@AspectJ注释样式。显然，如果您没有使用Java 5+，那么已经为您做出了选择...使用代码样式。如果方面在您的设计中发挥重要作用，并且您能够使用Eclipse 的[AspectJ开发工具（AJDT）](https://www.eclipse.org/ajdt/)插件，那么AspectJ语言语法是首选选项：它更清晰，更简单，因为该语言是专门为写入而设计的方面。如果您没有使用Eclipse，或只有几个方面在您的应用程序中不起主要作用，那么您可能需要考虑使用@AspectJ样式并在IDE中坚持使用常规Java编译，并添加一个方面编织阶段到您的构建脚本。

### 11.4.2 @AspectJ或Spring for AOP的XML？

如果您选择使用Spring AOP，那么您可以选择@AspectJ或XML样式。需要考虑各种权衡。

XML样式对于现有的Spring用户来说是最熟悉的，并且由真正的POJO支持。当使用AOP作为配置企业服务的工具时，XML可能是一个不错的选择（一个好的测试是你是否认为切入点表达式是你可能想要独立改变的配置的一部分）。使用XML风格可以说，从您的配置中可以更清楚地了解系统中存在哪些方面。

XML风格有两个缺点。首先，它没有完全封装它在一个地方解决的要求的实现。DRY原则规定，系统中的任何知识都应该有单一，明确，权威的表示。使用XML样式时，知识*如何*实现的需求分为支持bean类的声明和配置文件中的XML。使用@AspectJ样式时，有一个模块 - 方面 - 用于封装此信息。其次，XML样式在表达方面比@AspectJ样式稍微受限：仅支持“单例”方面实例化模型，并且不可能组合在XML中声明的命名切入点。例如，在@AspectJ样式中，您可以编写如下内容：

```java
@Pointcut(execution(* get*()))
public void propertyAccess() {}

@Pointcut(execution(org.xyz.Account+ *(..))
public void operationReturningAnAccount() {}

@Pointcut(propertyAccess() && operationReturningAnAccount())
public void accountPropertyAccess() {}
```

在XML样式中，我可以声明前两个切入点：

```xml
<aop:pointcut id="propertyAccess"
        expression="execution(* get*())"/>

<aop:pointcut id="operationReturningAnAccount"
        expression="execution(org.xyz.Account+ *(..))"/>
```

XML方法的缺点是您无法 `accountPropertyAccess`通过组合这些定义来定义切入点。

@AspectJ样式支持额外的实例化模型和更丰富的切入点组合。它具有将方面保持为模块化单元的优点。它还具有以下优点：Spring AOP和AspectJ可以理解（并因此消耗）@AspectJ方面 - 因此，如果您以后决定需要AspectJ的功能来实现其他需求，那么迁移到AspectJ非常容易基于方法。总的来说，只要你的方面不仅仅是企业服务的简单“配置”，Spring团队更喜欢@AspectJ风格。

