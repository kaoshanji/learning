# JDBC基础知识

在本课程中，您将学习JDBC API的基础知识。

- [Getting Started](basics/gettingstarted.md)设置了一个基本的数据库开发环境，并向您展示了如何编译和运行JDBC教程示例。
- [使用JDBC处理SQL语句](basics/processingsqlstatements.md)概述了处理任何SQL语句所需的步骤。以下页面更详细地描述了这些步骤：
  - [建立连接](basics/connecting.md)会将您连接到数据库。
  - [与DataSource对象](basics/sqldatasources.md)连接将向您展示如何使用`DataSource`对象连接到数据库，这是获取数据源连接的首选方式。
  - [处理SQLExceptions](basics/sqlexception.md)将向您展示如何处理由数据库错误引起的异常。
  - [设置表](basics/tables.md)描述了JDBC教程示例中使用的所有数据库表，以及如何使用JDBC API和SQL脚本创建和填充表。
  - [从结果集中检索和修改值可以](basics/retrieving.md)开发配置数据库，发送查询以及从数据库中检索数据的过程。
  - [使用Prepared Statements](basics/prepared.md)描述了一种更灵活的方法来创建数据库查询。
  - [使用事务可以](basics/transactions.md)向您显示如何控制实际执行数据库查询的时间。
- [使用RowSet对象](basics/rowset.md)向您介绍`RowSet`对象; 这些对象以一种方式保存表格数据，使其比结果集更灵活，更易于使用。以下页面描述了`RowSet`可用的不同类型的对象：
  - [使用JdbcRowSet对象](basics/jdbcrowset.md)
  - [使用CachedRowSetObjets](basics/cachedrowset.md)
  - [使用JoinRowSet对象](basics/joinrowset.md)
  - [使用FilteredRowSet对象](basics/filteredrowset.md)
  - [使用WebRowSet对象](basics/webrowset.md)
- [使用高级数据类型](basics/sqltypes.md)向您介绍其他数据类型; 以下页面更详细地描述了这些数据类型：
  - [使用大对象](basics/blob.md)
  - [使用SQLXML对象](basics/sqlxml.md)
  - [使用数组对象](basics/array.md)
  - [使用DISTINCT数据类型](basics/distinct.md)
  - [使用结构化对象](basics/sqlstructured.md)
  - [使用自定义类型映射](basics/sqlcustommapping.md)
  - [使用数据链接对象](basics/sqldatalink.md)
  - [使用RowId对象](basics/sqlrowid.md)