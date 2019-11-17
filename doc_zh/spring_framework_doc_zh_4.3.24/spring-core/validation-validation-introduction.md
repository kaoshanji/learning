# 9.1 概述

**JSR-303 / JSR-349 Bean验证**

Spring Framework 4.0在设置支持方面支持Bean Validation 1.0（JSR-303）和Bean Validation 1.1（JSR-349），并使其适应Spring的`Validator`界面。

应用程序可以选择全局启用Bean Validation一次，如 第9.8节“Spring Validation”中所述 ，并专门用于所有验证需求。

应用程序还可以为`Validator`每个`DataBinder`实例注册其他Spring 实例 ，如 第9.8.3节“配置DataBinder”中所述 。这可以用于在不使用注释的情况下插入验证逻辑。

将验证视为业务逻辑有利有弊，而Spring提供了一种不排除其中任何一种的验证（和数据绑定）设计。具体的验证不应该与Web层绑定，应该易于本地化，并且应该可以插入任何可用的验证器。考虑到上述情况，Spring提出了一个`Validator`基本的界面，可以在应用程序的每一层中显着使用。

数据绑定对于允许用户输入动态绑定到应用程序的域模型（或用于处理用户输入的任何对象）非常有用。Spring提供了所谓的`DataBinder`那样做。在`Validator`和`DataBinder`补`validation`包，它主要在使用，但不限于MVC框架。

这`BeanWrapper`是Spring Framework中的一个基本概念，并且在很多地方使用。但是，您可能不需要`BeanWrapper` 直接使用。因为这是参考文献，我们觉得可能有些解释。我们将`BeanWrapper`在本章中解释，因为如果你要使用它，你很可能在尝试将数据绑定到对象时这样做。

Spring的DataBinder和较低级别的BeanWrapper都使用PropertyEditors来解析和格式化属性值。该`PropertyEditor`概念是JavaBeans规范的一部分，本章也对此进行了解释。Spring 3引入了一个“core.convert”包，它提供了一般类型转换工具，以及用于格式化UI字段值的更高级“格式”包。这些新包可以用作PropertyEditors的更简单的替代方法，本章也将对此进行讨论。