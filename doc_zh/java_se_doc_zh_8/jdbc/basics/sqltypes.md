# 使用高级数据类型

本节中介绍的高级数据类型为关系数据库提供了更多的灵活性，可以用作表列的值。例如，列可用于存储`BLOB`（二进制大对象）值，这些值可以将非常大量的数据存储为原始字节。列也可以是类型`CLOB`（字符大对象），它能够以字符格式存储非常大量的数据。

最新版本的ANSI / ISO SQL标准通常称为SQL：2003。本标准规定了以下数据类型：

- SQL92内置类型，其中包括大家熟悉的SQL列类型如`CHAR`，`FLOAT`和`DATE`

- SQL99内置类型，由SQL99添加的类型组成：

  - `BOOLEAN`：布尔值（true或false）
  - `BLOB`：二进制大型Bobject
  - `CLOB`：字符大对象

- SQL：2003添加的新内置类型：

  - `XML`：XML对象

- 用户定义的类型：

  - 结构化类型：用户定义的类型; 例如：

    ```
    创建类型PLANE_POINT
    AS（X FLOAT，Y FLOAT）不是最终的
    ```

  - `DISTINCT`type：基于内置类型的用户定义类型; 例如：

    ```
    创建类型钱
    AS NUMERIC（10,2）FINAL
    ```

- 构造类型：基于给定基类型的新类型：

  - `REF(*structured-type*)`：指针持久表示驻留在数据库中的结构化类型的实例
  - `*base-type* ARRAY[*n*]`：*n个*基本类型元素的数组

- 定位器：作为驻留在数据库服务器上的数据的逻辑指针的实体。甲*定位器*存在于客户端计算机，并且是短暂的，合乎逻辑的指针到服务器上的数据。定位器通常指的是太大而无法在客户端上实现的数据，例如图像或音频。（*物化视图*是预先作为模式对象存储或“物化”的查询结果。）在SQL级别定义了运算符，以检索由定位符表示的随机访问的数据片段：

  - `LOCATOR(*structured-type*)`：服务器中结构化实例的定位器
  - `LOCATOR(*array*)`：服务器中数组的定位器
  - `LOCATOR(*blob*)`：定位器到服务器中的二进制大对象
  - `LOCATOR(*clob*)`：定位器到服务器中的字符大对象

- `Datalink`：用于管理数据源外部数据的类型。`Datalink`值是SQL MED（外部数据管理）的一部分，是SQL ANSI / ISO标准规范的一部分。

## 映射高级数据类型

JDBC API为SQL：2003标准指定的高级数据类型提供默认映射。以下列表给出了数据类型以及它们映射到的接口或类：

- `BLOB`：`Blob`界面
- `CLOB`：`Clob`界面
- `NCLOB`：`NClob`界面
- `ARRAY`：`Array`界面
- `XML`：`SQLXML`界面
- 结构化类型：`Struct`界面
- `REF(structured type)`：`Ref`界面
- `ROWID`：`RowId`界面
- `DISTINCT`：基本类型映射到的类型。例如，`DISTINCT`基于SQL `NUMERIC`类型的值映射到`java.math.BigDecimal`类型，因为`NUMERIC`映射到`BigDecimal`Java编程语言。
- `DATALINK`：`java.net.URL`对象

## 使用高级数据类型

您可以像处理其他数据类型一样检索，存储和更新高级数据类型。您可以使用其中一种或多种方法来检索它们，存储它们的方法以及更新它们的方法。（变量是一个Java接口或类映射到一个先进的数据类型的名称。），使用涉及先进的数据类型执行的操作的大概90％ ，和方法。下表显示了要使用的方法：`ResultSet.get*DataType*``CallableStatement.get*DataType*``PreparedStatement.set*DataType*``ResultSet.update*DataType*``*DataType*``get*DataType*``set*DataType*``update*DataType*`

| **高级数据类型**       | **getDataType 方法** | **setDataType 方法** | **updateDataType 方法** |
| ---------------------- | -------------------- | -------------------- | ----------------------- |
| `BLOB`                 | `getBlob`            | `setBlob`            | `updateBlob`            |
| `CLOB`                 | `getClob`            | `setClob`            | `updateClob`            |
| `NCLOB`                | `getNClob`           | `setNClob`           | `updateNClob`           |
| `ARRAY`                | `getArray`           | `setArray`           | `updateArray`           |
| `XML`                  | `getSQLXML`          | `setSQLXML`          | `updateSQLXML`          |
| `Structured type`      | `getObject`          | `setObject`          | `updateObject`          |
| `REF(structured type)` | `getRef`             | `setRef`             | `updateRef`             |
| `ROWID`                | `getRowId`           | `setRowId`           | `updateRowId`           |
| `DISTINCT`             | `getBigDecimal`      | `setBigDecimal`      | `updateBigDecimal`      |
| `DATALINK`             | `getURL`             | `setURL`             | `updateURL`             |

**注意**：`DISTINCT`数据类型的行为与其他高级SQL数据类型不同。作为基于已存在的内置类型的用户定义类型，它没有接口作为Java编程语言中的映射。因此，您使用与`DISTINCT`数据类型所基于的Java类型对应的方法。有关更多信息，请参阅[使用DISTINCT数据类型](https://docs.oracle.com/javase/tutorial/jdbc/basics/distinct.html)。

例如，以下代码片段检索SQL `ARRAY`值。对于此示例，假设`SCORES`表`STUDENTS`中的列包含类型的值`ARRAY`。变量`*stmt*`是一个`Statement`对象。

```java
ResultSet rs = stmt.executeQuery(
    "SELECT SCORES FROM STUDENTS " +
    "WHERE ID = 002238");
rs.next();
Array scores = rs.getArray("SCORES");
```

该变量`*scores*`是一个逻辑指针，指向`ARRAY`存储在`STUDENTS`student的行中的表中的SQL 对象`002238`。

如果要在数据库中存储值，请使用适当的`set`方法。例如，以下代码片段（其中`*rs*`是`ResultSet`对象）存储`Clob`对象：

```java
Clob notes = rs.getClob("NOTES");
PreparedStatement pstmt =
    con.prepareStatement(
        "UPDATE MARKETS SET COMMENTS = ? " +
        "WHERE SALES < 1000000");
pstmt.setClob(1, notes);
pstmt.executeUpdate();
```

此代码设置`*notes*`为发送到数据库的update语句中的第一个参数。`Clob`指定的值`*notes*`将存储在`MARKETS`列`COMMENTS`中的值`SALES`小于一百万的每一行的列表中。