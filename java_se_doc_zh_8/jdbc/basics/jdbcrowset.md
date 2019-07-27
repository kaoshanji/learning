# 使用JdbcRowSet对象

甲`JdbcRowSet`对象是一种增强`ResultSet`对象。它保持与其数据源的连接，就像`ResultSet`对象一样。最大的区别在于它具有一组属性和一个侦听器通知机制，使其成为JavaBeans组件。

`JdbcRowSet`对象的一个主要用途是使`ResultSet`对象在没有这些功能的情况下可滚动和可更新。

本节包括以下主题：

- [创建JdbcRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#creating-jdbcrowset-object)
- [默认JdbcRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#default-jdbcrowset-objects)
- [设置属性](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#setting-properties)
- [使用JdbcRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#using-jdbcrowset-object)
- [代码示例](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#code-sample)

## 创建JdbcRowSet对象

您可以通过`JdbcRowSet`各种方式创建对象：

- 通过使用获取`ResultSet`对象的引用实现构造函数
- 通过使用获取`Connection`对象的引用实现构造函数
- 通过使用引用实现默认构造函数
- 通过使用`RowSetFactory`从类创建的实例`RowSetProvider`

**注意**：或者，您可以使用`JdbcRowSet`JDBC驱动程序实现中的构造函数。但是，`RowSet`接口的实现将与参考实现不同。这些实现将具有不同的名称和构造函数。例如，Oracle JDBC驱动程序的`JdbcRowSet`接口实现已命名`oracle.jdbc.rowset.OracleJDBCRowSet`。

### 传递ResultSet对象

创建`JdbcRowSet`对象的最简单方法是生成一个`ResultSet`对象并将其传递给`JdbcRowSetImpl`构造函数。这样做不仅可以创建一个`JdbcRowSet`对象，还可以使用`ResultSet`对象中的数据填充它。

**注意**：`ResultSet`传递给`JdbcRowSetImpl`构造函数的对象必须是可滚动的。

作为示例，以下代码片段使用该`Connection`对象`con`来创建`Statement`对象，`stmt`然后该对象执行查询。查询生成`ResultSet`对象`rs`，该对象将传递给构造函数以创建`JdbcRowSet`使用以下数据初始化的新对象`rs`：

```java
stmt = con.createStatement(
           ResultSet.TYPE_SCROLL_SENSITIVE,
           ResultSet.CONCUR_UPDATABLE);
rs = stmt.executeQuery("select * from COFFEES");
jdbcRs = new JdbcRowSetImpl(rs);
```

使用`JdbcRowSet`对象创建的`ResultSet`对象充当对象的包装器`ResultSet`。因为该`RowSet`对象`rs`是可滚动和可更新的，`jdbcRs`所以也是可滚动和可更新的。如果您运行`createStatement`没有任何参数的方法，则`rs`不可滚动或可更新，也不会`jdbcRs`。

### 传递连接对象

以下代码摘录中的第一个语句[`JdbcRowSetSample`](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)创建了一个`JdbcRowSet`使用该`Connection`对象连接到数据库的对象`con`：

```java
jdbcRs = new JdbcRowSetImpl(con);
jdbcRs.setCommand("select * from COFFEES");
jdbcRs.execute();
```

在`jdbcRs`使用该方法指定SQL语句之前，该对象不包含任何数据`setCommand`，然后运行该方法`execute`。

该对象`jdbcRs`是可滚动和可更新的; 默认情况下，除非另有说明，否则`JdbcRowSet`所有其他`RowSet`对象都是可滚动和可更新的。有关可以指定的属性的更多信息，请参见[默认JdbcRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#default-jdbcrowset-objects)`JdbcRowSet`。

### 使用默认构造函数

以下代码摘录中的第一个语句创建一个空`JdbcRowSet`对象。

```java
public void createJdbcRowSet(String username, String password) {

    jdbcRs = new JdbcRowSetImpl();
    jdbcRs.setCommand("select * from COFFEES");
    jdbcRs.setUrl("jdbc:myDriver:myAttribute");
    jdbcRs.setUsername(username);
    jdbcRs.setPassword(password);
    jdbcRs.execute();
    // ...
}
```

在`jdbcRs`使用该方法指定SQL语句`setCommand`，指定`JdbcResultSet`对象如何连接数据库，然后运行该方法之前，该对象不包含任何数据`execute`。

所有参考实现构造函数都为[Default JdbcRowSet Objects](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#default-jdbcrowset-objects)部分中列出的属性分配默认值。

### 使用RowSetFactory接口

使用RowSet 1.1（它是Java SE 7及更高版本的一部分），您可以使用实例`RowSetFactory`来创建`JdbcRowSet`对象。例如，以下代码摘录使用`RowSetFactory`接口的实例来创建`JdbcRowSet`对象`jdbcRs`：

```java
public void createJdbcRowSetWithRowSetFactory(
    String username, String password)
    throws SQLException {

    RowSetFactory myRowSetFactory = null;
    JdbcRowSet jdbcRs = null;
    ResultSet rs = null;
    Statement stmt = null;

    try {
        myRowSetFactory = RowSetProvider.newFactory();
        jdbcRs = myRowSetFactory.createJdbcRowSet();

        jdbcRs.setUrl("jdbc:myDriver:myAttribute");
        jdbcRs.setUsername(username);
        jdbcRs.setPassword(password);

        jdbcRs.setCommand("select * from COFFEES");
        jdbcRs.execute();

        // ...
    }
}
```

以下语句使用默认实现创建`RowSetProvider`对象：`myRowSetFactory``RowSetFactory``com.sun.rowset.RowSetFactoryImpl`

```java
myRowSetFactory = RowSetProvider.newFactory();
```

或者，如果JDBC驱动程序有自己的`RowSetFactory`实现，则可以将其指定为`newFactory`方法的参数。

以下语句创建`JdbcRowSet`对象`jdbcRs`并配置其数据库连接属性：

```java
jdbcRs = myRowSetFactory.createJdbcRowSet();
jdbcRs.setUrl("jdbc:myDriver:myAttribute");
jdbcRs.setUsername(username);
jdbcRs.setPassword(password);
```

该`RowSetFactory`接口包含创建`RowSet`RowSet 1.1及更高版本中可用的不同类型实现的方法：

- `createCachedRowSet`
- `createFilteredRowSet`
- `createJdbcRowSet`
- `createJoinRowSet`
- `createWebRowSet`

## 默认JdbcRowSet对象

`JdbcRowSet`使用默认构造函数创建对象时，新`JdbcRowSet`对象将具有以下属性：

- `type`:( `ResultSet.TYPE_SCROLL_INSENSITIVE`有一个可滚动的游标）
- `concurrency`:( `ResultSet.CONCUR_UPDATABLE`可以更新）
- `escapeProcessing`:( `true`驱动程序将执行转义处理;启用转义处理时，驱动程序将扫描任何转义语法并将其转换为特定数据库可以理解的代码）
- `maxRows`:( `0`行数没有限制）
- `maxFieldSize`：`0`（上一列值中的字节的数量没有限制;仅适用于存储列`BINARY`，`VARBINARY`，`LONGVARBINARY`，`CHAR`，`VARCHAR`，和`LONGVARCHAR`的值）
- `queryTimeout`:( `0`没有时间限制执行查询所需的时间）
- `showDeleted`:( `false`已删除的行不可见）
- `transactionIsolation`:( `Connection.TRANSACTION_READ_COMMITTED`只读取已提交的数据）
- `typeMap`:( `null`与此`Connection`对象使用的`RowSet`对象关联的类型映射`null`）

从这个列表中你必须记住的主要事情是a `JdbcRowSet`和所有其他`RowSet`对象是可滚动和可更新的，除非你为这些属性设置不同的值。

## 设置属性

[Default JdbcRowSet Objects](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#default-jdbcrowset-objects)部分列出了`JdbcRowSet`创建新对象时默认设置的属性。如果使用默认构造函数，则必须先设置一些其他属性，然后才能`JdbcRowSet`使用数据填充新对象。

为了获取其数据，`JdbcRowSet`对象首先需要连接到数据库。以下四个属性包含用于获取与数据库的连接的信息。

- `username`：用户提供给数据库的名称，作为获取访问权限的一部分
- `password`：用户的数据库密码
- `url`：用户要连接的数据库的JDBC URL
- `datasourceName`：用于检索`DataSource`已向JNDI命名服务注册的对象的名称

您设置的这些属性中的哪一个取决于您要如何建立连接。首选方法是使用`DataSource`对象，但是使用`DataSource`JNDI命名服务注册对象可能不实际，这通常由系统管理员完成。因此，代码示例都使用该`DriverManager`机制来获取连接，您使用该`url`属性而不是`datasourceName`属性。

您必须设置的另一个属性是`command`属性。此属性是确定`JdbcRowSet`对象将包含哪些数据的查询。例如，以下代码行`command`使用查询设置属性，该查询生成`ResultSet`包含表中所有数据的对象`COFFEES`：

```java
jdbcRs.setCommand("select * from COFFEES");
```

设置`command`连接所需的属性和属性后，可以`jdbcRs`通过调用`execute`方法填充对象的数据。

```java
jdbcRs.execute();
```

该`execute`方法在后台为您做了很多事情：

- 它让你使用分配给的值与数据库的连接`url`，`username`和`password`性能。
- 它执行您在`command`属性中设置的查询。
- 它将结果`ResultSet`对象中的数据读入`jdbcRs`对象。

## 使用JdbcRowSet对象

您更新，插入和删除`JdbcRowSet`对象中的行的方式与更新，插入和删除可更新`ResultSet`对象中的行的方式相同。同样，您导航`JdbcRowSet`对象的方式与导航可滚动`ResultSet`对象的方式相同。

咖啡馆的Coffee Break连锁店获得了另一家咖啡馆连锁店，现在拥有一个不支持滚动或更新结果集的遗留数据库。换句话说，`ResultSet`此遗留数据库生成的任何对象都没有可滚动的游标，并且其中的数据无法修改。但是，通过创建使用`JdbcRowSet`对象中的数据填充的`ResultSet`对象，实际上可以使`ResultSet`对象可滚动和可更新。

如前所述，`JdbcRowSet`默认情况下，对象是可滚动和可更新的。由于其内容与`ResultSet`对象中的内容相同，因此对`JdbcRowSet`对象进行操作等同于对`ResultSet`对象本身进行操作。并且因为`JdbcRowSet`对象具有与数据库的持续连接，所以对数据库中的数据也进行了对其自身数据的更改。

本节包括以下主题：

- [导航JdbcRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#navigating-jdbcrowset-object)
- [更新列值](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#updating-column-value)
- [插入行](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#inserting-row)
- [删除行](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#deleting-row)

### 导航JdbcRowSet对象

一`ResultSet`，是不是滚动的对象可以只使用`next`方法向前移动光标的，它可以从第一行仅向前移动光标到最后一行。`JdbcRowSet`但是，默认对象可以使用`ResultSet`界面中定义的所有光标移动方法。

甲`JdbcRowSet`对象可以调用该方法`next`，并且它还可以调用任何其他`ResultSet`光标移动的方法。例如，以下代码行将光标移动到`jdbcRs`对象中的第四行，然后返回到第三行：

```java
jdbcRs.absolute(4);
jdbcRs.previous();
```

该方法`previous`类似于该方法`next`，因为它可以在`while`循环中用于按顺序遍历所有行。不同之处在于您必须将光标移动到最后一行之后的位置，并将`previous`光标移向开头。

### 更新列值

您更新`JdbcRowSet`对象中的数据的方式与更新对象中的数据的方式相同`ResultSet`。

假设咖啡休息所有者想要提高一磅Espresso咖啡的价格。如果所有者知道Espresso位于`jdbcRs`对象的第三行，则执行此操作的代码可能如下所示：

```java
jdbcRs.absolute(3);
jdbcRs.updateFloat("PRICE", 10.99f);
jdbcRs.updateRow();
```

代码将光标移动到第三行，并将列的值更改`PRICE`为10.99，然后使用新价格更新数据库。

调用该方法会`updateRow`更新数据库，因为它`jdbcRs`已保持与数据库的连接。对于断开连接的`RowSet`对象，情况有所不同。

### 插入行

如果Coffee Break连锁店的所有者想要将一种或多种咖啡添加到他提供的产品中，则所有者将需要在`COFFEES`每张新咖啡的表中添加一行，如下面的代码片段所示`JdbcRowSetSample`。请注意，因为`jdbcRs`对象始终连接到数据库，所以在对象中插入行与将`JdbcRowSet`对象插入对象相同`ResultSet`：将光标移动到插入行，使用适当的updater方法为每个对象设置一个值列，并调用方法`insertRow`：

```java
jdbcRs.moveToInsertRow();
jdbcRs.updateString("COF_NAME", "HouseBlend");
jdbcRs.updateInt("SUP_ID", 49);
jdbcRs.updateFloat("PRICE", 7.99f);
jdbcRs.updateInt("SALES", 0);
jdbcRs.updateInt("TOTAL", 0);
jdbcRs.insertRow();

jdbcRs.moveToInsertRow();
jdbcRs.updateString("COF_NAME", "HouseDecaf");
jdbcRs.updateInt("SUP_ID", 49);
jdbcRs.updateFloat("PRICE", 8.99f);
jdbcRs.updateInt("SALES", 0);
jdbcRs.updateInt("TOTAL", 0);
jdbcRs.insertRow();
```

调用方法时`insertRow`，新行将插入到`jdbcRs`对象中，并且也会插入到数据库中。前面的代码片段经过两次这个过程，因此将两个新行插入到`jdbcRs`对象和数据库中。

### 删除行

与更新数据和插入新行一样，删除行`JdbcRowSet`对象与`ResultSet`对象的行相同。店主想要停止销售法国烤无咖啡因咖啡，这是该`jdbcRs`物品的最后一排。在以下代码行中，第一行将光标移动到最后一行，第二行删除`jdbcRs`对象和数据库中的最后一行：

```
jdbcRs.last（）;
jdbcRs.deleteRow（）;
```

## 代码示例

该示例`JdbcRowSetSample`执行以下操作：

- 创建一个新`JdbcRowSet`对象，该`ResultSet`对象使用通过执行检索`COFFEES`表中所有行的查询生成的对象进行初始化
- 将光标移动到`COFFEES`表的第三行并更新该`PRICE`行中的列
- 插入两个新行，一个用于`HouseBlend`，一个用于`HouseDecaf`
- 将光标移动到最后一行并删除它