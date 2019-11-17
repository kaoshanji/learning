# 使用JoinRowSet对象

一个`JoinRowSet`实现可以让你创建一个SQL `JOIN`之间`RowSet`的物体时，他们没有连接到数据源。这很重要，因为它节省了必须创建一个或多个连接的开销。

涵盖以下主题：

- [创建JoinRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/joinrowset.html#creating-joinrowset-object)
- [添加RowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/joinrowset.html#adding-rowset-objects)
- [管理匹配列](https://docs.oracle.com/javase/tutorial/jdbc/basics/joinrowset.html#managing-match-columns)

该`JoinRowSet`接口是一个子接口`CachedRowSet`的接口，从而继承的功能`CachedRowSet`对象。这意味着`JoinRowSet`对象是一个断开连接的`RowSet`对象，可以在不始终连接到数据源的情况下运行。

## 创建JoinRowSet对象

甲`JoinRowSet`对象作为SQL的持有者`JOIN`。以下代码行显示创建`JoinRowSet`对象：

```java
JoinRowSet jrs = new JoinRowSetImpl();
```

在向对象添加对象`jrs`之前，变量不会保留任何`RowSet`内容。

**注意**：或者，您可以使用`JoinRowSet`JDBC驱动程序实现中的构造函数。但是，`RowSet`接口的实现将与参考实现不同。这些实现将具有不同的名称和构造函数。例如，Oracle JDBC驱动程序的`JoinRowSet`接口实现已命名`oracle.jdbc.rowset.OracleJoinRowSet`。

## 添加RowSet对象

任何`RowSet`对象都可以添加到`JoinRowSet`对象中，只要它可以是SQL的一部分即可`JOIN`。`JdbcRowSet`可以添加始终连接到其数据源的对象，但通常它`JOIN`通过直接操作数据源而不是成为`JOIN`通过添加到`JoinRowSet`对象的一部分而构成a 的一部分。提供`JoinRowSet`实现的目的是使断开连接的`RowSet`对象成为`JOIN`关系的一部分。

The Coffee Break咖啡馆连锁店的老板想要获得他从Acme，Inc。购买的咖啡清单。为了做到这一点，店主必须从两张桌子上获取信息，`COFFEES`并且`SUPPLIERS`。在`RowSet`技术之前的数据库世界中，程序员会将以下查询发送到数据库：

```java
String query =
    "SELECT COFFEES.COF_NAME " +
    "FROM COFFEES, SUPPLIERS " +
    "WHERE SUPPLIERS.SUP_NAME = Acme.Inc. " +
    "and " +
    "SUPPLIERS.SUP_ID = COFFEES.SUP_ID";
```

在`RowSet`技术领域，您无需向数据源发送查询即可完成相同的结果。您可以将`RowSet`包含两个表中的数据的对象添加到`JoinRowSet`对象。然后，因为所有相关数据都在`JoinRowSet`对象中，您可以对其执行查询以获取所需数据。

以下代码片段[`JoinSample.testJoinRowSet`](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)创建两个`CachedRowSet`对象，`coffees`使用表中的数据填充`COFFEES`，并`suppliers`使用表中的数据填充`SUPPLIERS`。在`coffees`和`suppliers`对象必须做出与数据库的连接来执行他们的命令，并逐渐加入数据，但做到这一点后，他们不必为了形成再重新连接`JOIN`。

```java
coffees = new CachedRowSetImpl();
coffees.setCommand("SELECT * FROM COFFEES");
coffees.setUsername(settings.userName);
coffees.setPassword(settings.password);
coffees.setUrl(settings.urlString);
coffees.execute();

suppliers = new CachedRowSetImpl();
suppliers.setCommand("SELECT * FROM SUPPLIERS");
suppliers.setUsername(settings.userName);
suppliers.setPassword(settings.password);
suppliers.setUrl(settings.urlString);
suppliers.execute(); 
```

## 管理匹配列

看看`SUPPLIERS`表格，您可以看到Acme，Inc。的识别号码是101.表格中的咖啡`COFFEES`与供应商的识别号码101是Colombian和Colombian_Decaf。可以连接两个表中的信息，因为这两个表都有列`SUP_ID in common`。在JDBC `RowSet`技术中，`SUP_ID`基于它的*列*`JOIN`称为*匹配列*。

`RowSet`添加到`JoinRowSet`对象的每个对象都必须具有匹配列，即所`JOIN`基于的列。有两种方法可以为`RowSet`对象设置匹配列。第一种方法是将匹配列传递给`JoinRowSet`方法`addRowSet`，如以下代码行所示：

```java
jrs.addRowSet(coffees, 2);
```

这行代码将对象添加`coffees` `CachedRowSet`到`jrs`对象中，并将`coffees`（`SUP_ID`）的第二列设置为匹配列。代码行也可以使用列名而不是列号。

```java
jrs.addRowSet(coffees, "SUP_ID");
```

此时，`jrs`只有`coffees`它。`RowSet`添加到的下一个对象`jrs`必须能够形成一个`JOIN`with `coffees`，这是正确的，`suppliers`因为两个表都有SUP_ID列。下面的代码行添加`suppliers`到`jrs`和设置列SUP_ID的匹配列。

```java
jrs.addRowSet(suppliers, 1);
```

现在`jrs`包含`JOIN`之间`coffees`并`suppliers`从业主可以得到由极致，Inc.提供由于代码没有设置类型的咖啡的名字`JOIN`，`jrs`持有内部连接，这是默认的。换句话说，一行`jrs`合并了一行`coffees`和一行`suppliers`。它保存列中`coffees`的列`suppliers`以及行中的列，其中`COFFEES.SUP_ID`列中的值与中的值匹配`SUPPLIERS.SUP_ID`。下面的代码打印出通过的Acme公司，其中所提供的咖啡的名称`String` `supplierName`是等于`Acme, Inc.`注意，这是可能的，因为该列`SUP_NAME`，这从`suppliers`，并且`COF_NAME`，这是从`coffees`，现在既包括在`JoinRowSet`对象`jrs`。

```java
System.out.println("Coffees bought from " + supplierName + ": ");

while (jrs.next()) {
    if (jrs.getString("SUP_NAME").equals(supplierName)) {
        String coffeeName = jrs.getString(1);
        System.out.println("     " + coffeeName);
    }
}
```

这将产生类似于以下的输出：

```sql
Coffees bought from Acme, Inc.:
     Colombian
     Colombian_Decaf
```

该`JoinRowSet`接口提供了用于设置`JOIN`将要形成的类型的常量，但是当前唯一实现的类型是`JoinRowSet.INNER_JOIN`。