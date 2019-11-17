# 使用数组对象

**注意**：MySQL和Java DB当前不支持`ARRAY`SQL数据类型。因此，没有JDBC教程示例可用于演示`Array`JDBC数据类型。

涵盖以下主题：

- [创建数组对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/array.html#creating_array)
- [在ResultSet中检索和访问数组值](https://docs.oracle.com/javase/tutorial/jdbc/basics/array.html#retrieving_array)
- [存储和更新数组对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/array.html#storing_array)
- [释放阵列资源](https://docs.oracle.com/javase/tutorial/jdbc/basics/array.html#releasing_array)

## 创建数组对象

使用该方法`Connection.createArrayOf`创建`Array`对象。

例如，假设您的数据库包含一个名为的表`REGIONS`，该表已使用以下SQL语句创建并填充; 请注意，这些语句的语法因数据库而异：

```sql
create table REGIONS
    (REGION_NAME varchar(32) NOT NULL,
    ZIPS varchar32 ARRAY[10] NOT NULL,
    PRIMARY KEY (REGION_NAME));

insert into REGIONS values(
    'Northwest',
    '{"93101", "97201", "99210"}');
insert into REGIONS values(
    'Southwest',
    '{"94105", "90049", "92027"}');
Connection con = DriverManager.getConnection(url, props);
String [] northEastRegion = { "10022", "02110", "07399" };
Array aArray = con.createArrayOf("VARCHAR", northEastRegionnewYork);
```

Oracle数据库JDBC驱动程序实现`java.sql.Array`与`oracle.sql.ARRAY`类的接口。

## 在ResultSet中检索和访问数组值

由于与JDBC 4.0大对象的接口（`Blob`，`Clob`，`NClob`），你可以操纵`Array`的对象，而不必把所有的数据从数据库服务器到客户端计算机。一个`Array`对象物化的SQL `ARRAY`它表示无论是作为结果集或Java阵列。

以下摘录检索`ARRAY`列中的SQL 值`ZIPS`并将其分配给`java.sql.Array`对象`z`对象。摘录检索内容`z`并将其存储在`zips`包含类型对象的Java数组中`String`。摘录遍历`zips`数组并检查每个邮政（zip）代码是否有效。此代码假定类`ZipCode`已经与方法前面定义的`isValid`返回`true`，如果给定邮政编码相匹配的有效邮政编码的主列表的zip代码之一：

```java
ResultSet rs = stmt.executeQuery(
    "SELECT region_name, zips FROM REGIONS");

while (rs.next()) {
    Array z = rs.getArray("ZIPS");
    String[] zips = (String[])z.getArray();
    for (int i = 0; i < zips.length; i++) {
        if (!ZipCode.isValid(zips[i])) {
            // ...
            // Code to display warning
        }
    }
}
```

在以下语句中，该`ResultSet`方法`getArray`返回存储在`ZIPS`当前行的列中的值作为`java.sql.Array`对象`z`：

```java
Array z = rs.getArray("ZIPS");
```

该变量`*z*`包含一个定位符，它是`ARRAY`服务器上SQL的逻辑指针; 它不包含`ARRAY`自身的元素。作为逻辑指针，`*z*`可用于操作服务器上的数组。

在以下行中，`getArray`是`Array.getArray`方法，而不是`ResultSet.getArray`上一行中使用的方法。因为该`Array.getArray`方法`Object`在Java编程语言中返回一个，并且因为每个邮政编码都是一个`String`对象，所以`String`在分配给变量之前，结果会转换为对象数组`zips`。

```java
String[] zips = (String[])z.getArray();
```

该`Array.getArray`方法`ARRAY`将客户端上的SQL 元素实现为`String`对象数组。因为，实际上，该变量`*zips*`包含数组的元素，能够通过迭代`zips`在一个`for`循环中，在寻找无效的邮政编码。

## 存储和更新数组对象

使用的方法`PreparedStatement.setArray`和`PreparedStatement.setObject`传递的`Array`值作为输入参数的`PreparedStatement`对象。

以下示例将`Array`对象`northEastRegion`（在前面的示例中创建）设置为PreparedStatement的第二个参数`pstmt`：

```java
PreparedStatement pstmt = con.prepareStatement(
    "insert into REGIONS (region_name, zips) " + "VALUES (?, ?)");
pstmt.setString(1, "NorthEast");
pstmt.setArray(2, northEastRegion);
pstmt.executeUpdate();
```

同样，使用方法`PreparedStatement.updateArray`并使用值`PreparedStatement.updateObject`更新表中的列`Array`。

## 释放阵列资源

`Array`对象至少在创建它们的事务的持续时间内保持有效。这可能导致应用程序在长时间运行的事务期间耗尽资源。应用程序可以`Array`通过调用其`free`方法来释放资源。

在以下摘录中，`Array.free`调用该方法以释放为先前创建的`Array`对象保留的资源。

```java
Array aArray = con.createArrayOf("VARCHAR", northEastRegionnewYork);
// ...
aArray.free();
```

