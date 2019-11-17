# 使用RowSet对象

JDBC `RowSet`对象以一种比结果集更灵活，更易于使用的方式保存表格数据。

Oracle `RowSet`为a的一些更常用的用途定义了五个接口`RowSet`，并且这些`RowSet`接口可以使用标准引用。在本教程中，您将学习如何使用这些参考实现。

这些版本的`RowSet`接口及其实现是为了方便程序员而提供的。程序员可以自由编写自己的`javax.sql.RowSet`接口版本，扩展五个`RowSet`接口的实现，或编写自己的实现。但是，许多程序员可能会发现标准参考实现已经满足他们的需求并将按原样使用它们。

本节介绍`RowSet`扩展此接口的接口和以下接口：

- `JdbcRowSet`
- `CachedRowSet`
- `WebRowSet`
- `JoinRowSet`
- `FilteredRowSet`

涵盖以下主题：

- [RowSet对象可以做什么？](https://docs.oracle.com/javase/tutorial/jdbc/basics/rowset.html#what_can_rowset_objects_do)
- [各种RowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/rowset.html#kinds_of_rowset_objects)

## RowSet对象可以做什么？

所有`RowSet`对象都从`ResultSet`接口派生，因此共享其功能。JDBC `RowSet`对象的特殊之处在于它们添加了以下新功能：

- [作为JavaBeans组件](https://docs.oracle.com/javase/tutorial/jdbc/basics/rowset.html#javabeans)
- [添加可滚动性或可更新性](https://docs.oracle.com/javase/tutorial/jdbc/basics/rowset.html#scrollability)

### 作为JavaBeans组件

所有`RowSet`对象都是JavaBeans组件。这意味着他们有以下内容：

- 属性
- JavaBeans通知机制

#### 属性

所有`RowSet`对象都有属性。属性是具有相应的getter和setter方法的字段。属性暴露给构建器工具（例如IDE JDveloper和Eclipse附带的那些工具），使您可以直观地操作bean。有关更多信息，请参阅[JavaBeans](https://docs.oracle.com/javase/tutorial/javabeans/)跟踪中的“ [属性”](https://docs.oracle.com/javase/tutorial/javabeans/writing/properties.html)课程 。

#### JavaBeans通知机制

`RowSet`对象使用JavaBeans事件模型，其中在发生某些事件时通知已注册的组件。对于所有`RowSet`对象，三个事件触发通知：

- 光标移动
- 更新，插入或删除行
- 对整个`RowSet`内容的更改

事件通知发送给所有*侦听器*，已实现`RowSetListener`接口的组件，并且已经将自己添加到`RowSet`对象的组件列表中，以便在发生任何三个事件时通知。

监听器可以是GUI组件，例如条形图。如果条形图跟踪`RowSet`对象中的数据，则只要数据发生更改，侦听器就会想知道新的数据值。因此，侦听器将实现`RowSetListener`方法以定义在特定事件发生时它将执行的操作。然后，还必须将侦听器添加到`RowSet`对象的侦听器列表中。以下代码行将条形图组件注册`bg`到`RowSet`对象`rs`。

```java
rs.addListener(bg);
```

现在`bg`每次光标移动，更改行或全部`rs`获取新数据时都会收到通知。

### 添加可滚动性或可更新性

某些DBMS不支持可滚动（可滚动）的结果集，有些DBMS不支持可更新（可更新）的结果集。如果该DBMS的驱动程序未添加滚动或更新结果集的功能，则可以使用`RowSet`对象执行此操作。一个`RowSet`对象是可滚动的，默认情况下更新的，所以通过填充一个`RowSet`具有结果集的内容，对象，就可以有效地使结果集可滚动和可更新的。

## 各种RowSet对象

甲`RowSet`对象被认为是可以连接或断开。一个*连接* `RowSet`对象使用JDBC驱动程序来做出一个关系数据库的连接，并保持在其整个生命跨度连接。甲*断开* `RowSet`对象进行到数据源的连接仅在从数据读出`ResultSet`对象或将数据写回至数据源。从数据源读取数据或向其数据源写入数据后，`RowSet`对象与其断开连接，从而变为“断开连接”。在其生命周期的大部分时间内，断开连接的`RowSet`对象与其数据源无关，并且独立运行。接下来的两个部分将告诉您连接或断开连接意味着`RowSet`对象可以做什么。

### 连接的RowSet对象

只有一个标准`RowSet`实现是连接`RowSet`对象：`JdbcRowSet`。始终连接到数据库，`JdbcRowSet`对象与对象最相似，`ResultSet`并且通常用作包装器，以使不可滚动且只读的`ResultSet`对象可滚动和可更新。

作为JavaBeans组件，`JdbcRowSet`可以使用对象，例如，在GUI工具中选择JDBC驱动程序。一个`JdbcRowSet`对象可以采用这种方式，因为它实际上是为获得其数据库连接的驱动器的包装。

### 断开的RowSet对象

其他四种实现是断开连接的`RowSet`实现。断开连接的`RowSet`对象具有连接对象的所有功能，`RowSet`并且它们具有仅对断开连接的`RowSet`对象可用的附加功能。例如，不必维护与数据源的连接使得断开连接的`RowSet`对象比`JdbcRowSet`对象或`ResultSet`对象轻得多。断开连接的`RowSet`对象也是可序列化的，并且可串行化和轻量级的组合使它们成为通过网络发送数据的理想选择。它们甚至可以用于向瘦客户端（如PDA和移动电话）发送数据。

该`CachedRowSet`接口定义了所有断开连接的`RowSet`对象可用的基本功能。其他三个是`CachedRowSet`接口的扩展，提供更多专业功能。以下信息显示了它们的相关性：

一个`CachedRowSet`对象具有的所有功能`JdbcRowSet`的对象，再加上它也可以做到以下几点：

- 获取与数据源的连接并执行查询
- 从结果`ResultSet`对象中读取数据并使用该数据填充自身
- 在数据断开连接时处理数据并对数据进行更改
- 重新连接到数据源以将更改写回它
- 检查与数据源的冲突并解决这些冲突

一个`WebRowSet`对象具有的所有功能`CachedRowSet`的对象，再加上它也可以做到以下几点：

- 将自己写为XML文档
- 阅读描述`WebRowSet`对象的XML文档

一个`JoinRowSet`对象具有的所有功能`WebRowSet`对象（因此也是那些的`CachedRowSet`对象），加上它也可以做到以下几点：

- 形成相当于a `SQL JOIN`而不必连接到数据源

一个`FilteredRowSet`对象同样具有的所有功能`WebRowSet`对象（因此也是一个`CachedRowSet`对象），加上它也可以做到以下几点：

- 应用过滤条件，以便只显示选定的数据。这相当于在`RowSet`对象上执行查询而无需使用查询语言或连接到数据源。