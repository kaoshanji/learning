# 18.2 一致的异常层次结构

Spring提供了一种方便的转换，从特定于技术的异常，例如 `SQLException`它自己的异常类层次结构，以及`DataAccessException`作为根异常。这些异常包装了原始异常，因此从来没有任何风险可能会丢失任何可能出错的信息。

除了JDBC异常之外，Spring还可以包装特定于Hibernate的异常，将它们转换为一组集中的运行时异常（对于JDO和JPA异常也是如此）。这允许人们只在适当的层中处理大多数不可恢复的持久性异常，而不需要在一个DAO中使用恼人的样板catch-and-throw块和异常声明。（人们仍然可以在任何需要的地方捕获和处理异常。）如上所述，JDBC异常（包括特定于数据库的方言）也被转换为相同的层次结构，这意味着可以在一致的编程模型中使用JDBC执行某些操作。 。

以上适用于Springs支持各种ORM框架的各种模板类。如果使用基于拦截器的类，则应用程序必须关心处理`HibernateExceptions`及其`JDOExceptions`自身，最好分别通过委托`SessionFactoryUtils’ `convertHibernateAccessException(..)`或 `convertJdoAccessException()`方法。这些方法将异常转换为与`org.springframework.dao` 异常层次结构中的异常兼容的异常。由于`JDOExceptions`未经检查，它们也可以简单地抛出，但就异常而言牺牲了通用的DAO抽象。

Spring提供的异常层次结构如下所示。（请注意，图像中详细说明的类层次结构仅显示整个`DataAccessException`层次结构的一个子集 。）

![DataAccessException的](../images/DataAccessException.gif)

