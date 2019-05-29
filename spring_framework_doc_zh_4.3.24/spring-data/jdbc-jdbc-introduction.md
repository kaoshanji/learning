# 19.1 概述

## 19.1 Spring Framework JDBC简介

Spring Framework JDBC抽象提供的增值可能最好通过下表中列出的操作顺序显示出来。该表显示了Spring将采取哪些操作以及哪些操作是您（应用程序开发人员）的责任。



**表19.1。Spring JDBC - 谁做了什么？**

| 行动                           | 弹簧 | 您   |
| ------------------------------ | ---- | ---- |
| 定义连接参数。                 |      | X    |
| 打开连接。                     | X    |      |
| 指定SQL语句。                  |      | X    |
| 声明参数并提供参数值           |      | X    |
| 准备并执行声明。               | X    |      |
| 设置循环以迭代结果（如果有）。 | X    |      |
| 为每次迭代做好工作。           |      | X    |
| 处理任何异常。                 | X    |      |
| 处理交易。                     | X    |      |
| 关闭连接，语句和结果集。       | X    |      |

Spring Framework负责处理所有可以使JDBC成为一个繁琐的API的低级细节。

### 19.1.1选择JDBC数据库访问方法

您可以选择多种方法来构成JDBC数据库访问的基础。除了三种类型的JdbcTemplate之外，新的SimpleJdbcInsert和SimplejdbcCall方法还优化了数据库元数据，而RDBMS Object样式采用了类似于JDO Query设计的面向对象方法。一旦开始使用这些方法之一，您仍然可以混合和匹配以包含来自不同方法的功能。所有方法都需要JDBC 2.0兼容的驱动程序，而某些高级功能需要JDBC 3.0驱动程序。

- *JdbcTemplate*是经典的Spring JDBC方法，也是最受欢迎的方法。这种“最低级别”方法和所有其他方法都使用了JdbcTemplate。
- *NamedParameterJdbcTemplate*包装a`JdbcTemplate`来提供命名参数而不是传统的JDBC“？” 占位符。当您有多个SQL语句参数时，此方法可提供更好的文档和易用性。
- *SimpleJdbcInsert和SimpleJdbcCall*优化数据库元数据以限制必要的配置量。此方法简化了编码，因此您只需提供表或过程的名称，并提供与列名匹配的参数映射。这仅在数据库提供足够的元数据时有效。如果数据库未提供此元数据，则必须提供参数的显式配置。
- *RDBMS对象（包括MappingSqlQuery，SqlUpdate和StoredProcedure）*要求您在数据访问层初始化期间创建可重用且线程安全的对象。此方法在JDO Query之后建模，其中您定义查询字符串，声明参数和编译查询。执行此操作后，可以多次调用execute方法，并传入各种参数值。

### 19.1.2包层次结构

Spring框架的JDBC抽象框架由四个不同的包，即`core`，`datasource`，`object`，和`support`。

该`org.springframework.jdbc.core`包包含`JdbcTemplate`类及其各种回调接口，以及各种相关类。名为subpackage的 `org.springframework.jdbc.core.simple`包含`SimpleJdbcInsert`和 `SimpleJdbcCall`类。另一个名为subpackage的包 `org.springframework.jdbc.core.namedparam`包含`NamedParameterJdbcTemplate`类和相关的支持类。请参见[第19.2节“使用JDBC核心类来控制基本JDBC处理和错误处理”](jdbc.html#jdbc-core)，[第19.4节“JDBC批处理操作”](jdbc.html#jdbc-advanced-jdbc)和 [第19.5节“使用SimpleJdbc类简化JDBC操作”](jdbc.html#jdbc-simple-jdbc)。

该`org.springframework.jdbc.datasource`软件包包含一个易于`DataSource`访问的实用程序类 ，以及`DataSource`可用于在Java EE容器外测试和运行未修改的JDBC代码的各种简单实现。名为subpackage的程序包括`org.springfamework.jdbc.datasource.embedded`使用Java数据库引擎（如HSQL，H2和Derby）创建嵌入式数据库的支持。请参见 [第19.3节“控制数据库连接”](jdbc.html#jdbc-connections)和[第19.8节“嵌入式数据库支持”](jdbc.html#jdbc-embedded-database-support)。

该`org.springframework.jdbc.object`包包含将RDBMS查询，更新和存储过程表示为线程安全，可重用对象的类。请参见 [第19.6节“将JDBC操作建模为Java对象”](jdbc.html#jdbc-object)。这种方法由JDO建模，尽管查询返回的对象自然*与*数据库*断开连接*。这种更高级别的JDBC抽象取决于包中的低级抽象`org.springframework.jdbc.core`。

该`org.springframework.jdbc.support`包提供`SQLException`翻译功能和一些实用程序类。JDBC处理期间抛出的异常将转换为`org.springframework.dao`包中定义的异常。这意味着使用Spring JDBC抽象层的代码不需要实现JDBC或RDBMS特定的错误处理。所有已翻译的异常都是未选中的，这使您可以选择捕获可以恢复的异常，同时允许将其他异常传播给调用方。请参见[第19.2.3节“SQLExceptionTranslator”](jdbc.html#jdbc-SQLExceptionTranslator)。