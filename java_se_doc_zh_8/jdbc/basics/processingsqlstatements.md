# 使用JDBC处理SQL语句

通常，要使用JDBC处理任何SQL语句，请执行以下步骤：

1. [建立连接。](https://docs.oracle.com/javase/tutorial/jdbc/basics/processingsqlstatements.html#establishing_connections)
2. [创建一个声明。](https://docs.oracle.com/javase/tutorial/jdbc/basics/processingsqlstatements.html#creating_statements)
3. [执行查询。](https://docs.oracle.com/javase/tutorial/jdbc/basics/processingsqlstatements.html#executing_queries)
4. [处理`ResultSet`对象。](https://docs.oracle.com/javase/tutorial/jdbc/basics/processingsqlstatements.html#processing_resultset_objects)
5. [关闭连接。](https://docs.oracle.com/javase/tutorial/jdbc/basics/processingsqlstatements.html#closing_connections)

此页面使用以下方法，`CoffeesTables.viewTable`从教程示例中演示这些步骤。此方法输出表的内容`COFFEES`。本教程后面将详细讨论此方法：

```java
public static void viewTable(Connection con, String dbName)
    throws SQLException {

    Statement stmt = null;
    String query = "select COF_NAME, SUP_ID, PRICE, " +
                   "SALES, TOTAL " +
                   "from " + dbName + ".COFFEES";
    try {
        stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            String coffeeName = rs.getString("COF_NAME");
            int supplierID = rs.getInt("SUP_ID");
            float price = rs.getFloat("PRICE");
            int sales = rs.getInt("SALES");
            int total = rs.getInt("TOTAL");
            System.out.println(coffeeName + "\t" + supplierID +
                               "\t" + price + "\t" + sales +
                               "\t" + total);
        }
    } catch (SQLException e ) {
        JDBCTutorialUtilities.printSQLException(e);
    } finally {
        if (stmt != null) { stmt.close(); }
    }
}
```

## 建立连接

首先，与要使用的数据源建立连接。数据源可以是DBMS，遗留文件系统或具有相应JDBC驱动程序的某些其他数据源。此连接由`Connection`对象表示。有关更多信息，请参阅 [建立连接](https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html)。

## 创建语句

A `Statement`是表示SQL语句的接口。您执行`Statement`对象，并生成`ResultSet`对象，这是一个表示数据库结果集的数据表。您需要一个`Connection`对象来创建一个`Statement`对象。

例如，使用以下代码`CoffeesTables.viewTable`创建`Statement`对象：

```java
stmt = con.createStatement();
```

有三种不同的陈述：

- `Statement`：用于实现没有参数的简单SQL语句。
- `PreparedStatement`:( Extends `Statement`。）用于预编译可能包含输入参数的SQL语句。有关更多信息，请参阅 [使用准备语句](https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html)。
- `CallableStatement:`（Extends `PreparedStatement`。）用于执行可能包含输入和输出参数的存储过程。有关更多信息，请参阅 [存储过程](https://docs.oracle.com/javase/tutorial/jdbc/basics/storedprocedures.html)。

## 执行查询

执行查询，调用一个`execute`从方法`Statement`如以下内容：

- `execute`：`true`如果查询返回的第一个对象是对象，则返回`ResultSet`。如果查询可以返回一个或多个`ResultSet`对象，请使用此方法。`ResultSet`通过重复调用来检索从查询返回的对象`Statement.getResultSet`。
- `executeQuery`：返回一个`ResultSet`对象。
- `executeUpdate`：返回一个整数，表示受SQL语句影响的行数。如果您正在使用，或使用SQL语句`INSERT`，请使用此方法。`DELETE``UPDATE`

例如，使用以下代码`CoffeesTables.viewTable`执行了一个`Statement`对象：

```java
ResultSet rs = stmt.executeQuery(query);
```

有关详细信息，请参阅 [从结果集中检索和修改值](https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html)。

## 处理ResultSet对象

您可以`ResultSet`通过游标访问对象中的数据。请注意，此游标不是数据库游标。该游标是指向`ResultSet`对象中一行数据的指针。最初，光标位于第一行之前。您可以调用`ResultSet`对象中定义的各种方法来移动光标。

例如，`CoffeesTables.viewTable`重复调用该方法`ResultSet.next`将光标向前移动一行。每次调用时`next`，该方法都会在光标当前所在的行中输出数据：

```java
try {
    stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery(query);
    while (rs.next()) {
        String coffeeName = rs.getString("COF_NAME");
        int supplierID = rs.getInt("SUP_ID");
        float price = rs.getFloat("PRICE");
        int sales = rs.getInt("SALES");
        int total = rs.getInt("TOTAL");
        System.out.println(coffeeName + "\t" + supplierID +
                           "\t" + price + "\t" + sales +
                           "\t" + total);
    }
}
// ...
```

有关详细信息，请参阅 [从结果集中检索和修改值](https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html)。

## 关闭连接

完成使用后`Statement`，调用该方法`Statement.close`立即释放它正在使用的资源。调用此方法时，其`ResultSet`对象将关闭。

例如，通过将对象包装在块中，该方法`CoffeesTables.viewTable`可确保在方法`Statement`结束时关闭对象，而不管`SQLException`抛出任何对象`finally`：

```java
} finally {
    if (stmt != null) { stmt.close(); }
}
```

JDBC `SQLException`在与数据源交互期间遇到错误时会引发错误。有关更多信息，请参阅 [处理SQL异常](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html)。

在JDBC 4.1，这是可以在Java SE 7版及更高版本，可以使用try-与资源语句自动关闭`Connection`，`Statement`和`ResultSet`对象，无论是否在`SQLException`已抛出。自动资源语句由`try`语句和一个或多个声明的资源组成。例如，您可以修改`CoffeesTables.viewTable`以使其`Statement`对象自动关闭，如下所示：

```java
public static void viewTable(Connection con) throws SQLException {

    String query = "select COF_NAME, SUP_ID, PRICE, " +
                   "SALES, TOTAL " +
                   "from COFFEES";

    try (Statement stmt = con.createStatement()) {

        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            String coffeeName = rs.getString("COF_NAME");
            int supplierID = rs.getInt("SUP_ID");
            float price = rs.getFloat("PRICE");
            int sales = rs.getInt("SALES");
            int total = rs.getInt("TOTAL");
            System.out.println(coffeeName + ", " + supplierID +
                               ", " + price + ", " + sales +
                               ", " + total);
        }
    } catch (SQLException e) {
        JDBCTutorialUtilities.printSQLException(e);
    }
}
```

以下语句是`try`-with-resources语句，它声明一个资源，`stmt`当`try`块终止时将自动关闭：

```java
try (Statement stmt = con.createStatement()) {
    // ...
}
```

