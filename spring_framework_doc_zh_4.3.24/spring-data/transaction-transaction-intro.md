# 17.1 spring事务管理概述

全面的事务支持是使用Spring Framework的最有说服力的理由之一。Spring Framework为事务管理提供了一致的抽象，具有以下优势：

- 跨不同事务API的一致编程模型，例如Java Transaction API（JTA），JDBC，Hibernate，Java Persistence API（JPA）和Java Data Objects（JDO）。
- 支持[声明式事务管理](transaction.html#transaction-declarative)。
- 与复杂的事务API（如JTA）相比， 用于[编程](transaction.html#transaction-programmatic)事务管理的API更简单。
- 与Spring的数据访问抽象集成。

以下部分描述了Spring Framework的事务增值和技术。（本章还包括对最佳实践，应用程序服务器集成以及常见问题解决方案的讨论。）

- [Spring Framework的事务支持模型的优点](transaction.html#transaction-motivation)描述了*为什么*要使用Spring Framework的事务抽象而不是EJB容器管理事务（CMT）或选择通过专有API（如Hibernate）来驱动本地事务。
- [理解Spring Framework事务抽象](transaction.html#transaction-strategies) 概述了核心类，并描述了如何`DataSource` 从各种源配置和获取实例。
- [将资源与事务同步](transaction.html#tx-resource-synchronization)描述了应用程序代码如何确保正确创建，重用和清理资源。
- [声明式事务管理](transaction.html#transaction-declarative)描述了对声明式事务管理的支持。
- [程序化事务管理](transaction.html#transaction-programmatic)包括对程序化（即明确编码）事务管理的支持。
- [事务绑定事件](transaction.html#transaction-event)描述了如何在事务中使用应用程序事件。