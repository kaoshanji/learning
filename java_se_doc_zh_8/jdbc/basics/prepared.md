# 使用Prepared Statements

此页面包含以下主题：

- [准备陈述概述](https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html#overview_ps)
- [创建PreparedStatement对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html#create_ps)
- [为PreparedStatement参数提供值](https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html#supply_values_ps)

## 准备陈述概述

有时使用`PreparedStatement`对象将SQL语句发送到数据库会更方便。这种特殊类型的语句来自`Statement`您已经知道的更通用的类。

如果要`Statement`多次执行对象，通常会减少使用`PreparedStatement`对象的执行时间。

`PreparedStatement`对象的主要特征是，与`Statement`对象不同，它在创建时会被赋予一个SQL语句。这样做的好处是，在大多数情况下，此SQL语句会立即发送到DBMS，并在其中进行编译。因此，该`PreparedStatement`对象不仅包含SQL语句，还包含已预编译的SQL语句。这意味着当`PreparedStatement`执行时，DBMS可以只运行`PreparedStatement`SQL语句而无需先编译它。

虽然`PreparedStatement`对象可以用于没有参数的SQL语句，但是对于带参数的SQL语句，最常使用它们。使用带参数的SQL语句的优点是，您可以使用相同的语句，并在每次执行时为其提供不同的值。这方面的例子在以下部分中。

以下方法，为每种类型的咖啡`CoffeesTable.updateCoffeeSales`在`SALES`列中存储当前销售的咖啡磅数，并更新每种类型咖啡在`TOTAL`列中销售的咖啡总磅数：

```java
public void updateCoffeeSales(HashMap<String, Integer> salesForWeek)
    throws SQLException {

    PreparedStatement updateSales = null;
    PreparedStatement updateTotal = null;

    String updateString =
        "update " + dbName + ".COFFEES " +
        "set SALES = ? where COF_NAME = ?";

    String updateStatement =
        "update " + dbName + ".COFFEES " +
        "set TOTAL = TOTAL + ? " +
        "where COF_NAME = ?";

    try {
        con.setAutoCommit(false);
        updateSales = con.prepareStatement(updateString);
        updateTotal = con.prepareStatement(updateStatement);

        for (Map.Entry<String, Integer> e : salesForWeek.entrySet()) {
            updateSales.setInt(1, e.getValue().intValue());
            updateSales.setString(2, e.getKey());
            updateSales.executeUpdate();
            updateTotal.setInt(1, e.getValue().intValue());
            updateTotal.setString(2, e.getKey());
            updateTotal.executeUpdate();
            con.commit();
        }
    } catch (SQLException e ) {
        JDBCTutorialUtilities.printSQLException(e);
        if (con != null) {
            try {
                System.err.print("Transaction is being rolled back");
                con.rollback();
            } catch(SQLException excep) {
                JDBCTutorialUtilities.printSQLException(excep);
            }
        }
    } finally {
        if (updateSales != null) {
            updateSales.close();
        }
        if (updateTotal != null) {
            updateTotal.close();
        }
        con.setAutoCommit(true);
    }
}
```

## 创建PreparedStatement对象

下面创建一个带有`PreparedStatement`两个输入参数的对象：

```java
String updateString =
    "update " + dbName + ".COFFEES " +
    "set SALES = ? where COF_NAME = ?";
updateSales = con.prepareStatement(updateString);
```

## 为PreparedStatement参数提供值

在执行`PreparedStatement`对象之前，必须提供值来代替问号占位符（如果有）。通过调用`PreparedStatement`类中定义的setter方法之一来完成此操作。以下语句提供了`PreparedStatement`命名中的两个问号占位符`updateSales`：

```java
updateSales.setInt(1, e.getValue().intValue());
updateSales.setString(2, e.getKey());
```

每个setter方法的第一个参数指定问号占位符。在此示例中，`setInt`指定第一个占位符并`setString`指定第二个占位符。

使用值设置参数后，它会保留该值，直到将其重置为其他值，或者`clearParameters`调用该方法。使用该`PreparedStatement`对象`updateSales`，以下代码片段说明在重置其中一个参数的值并使另一个参数保持相同之后重用预准备语句：

```java
// changes SALES column of French Roast
//row to 100

updateSales.setInt(1, 100);
updateSales.setString(2, "French_Roast");
updateSales.executeUpdate();

// changes SALES column of Espresso row to 100
// (the first parameter stayed 100, and the second
// parameter was reset to "Espresso")

updateSales.setString(2, "Espresso");
updateSales.executeUpdate();
```

### 使用循环设置值

通过使用`for`循环或`while`循环来设置输入参数的值，通常可以使编码更容易。

该`CoffeesTable.updateCoffeeSales`方法使用for-each循环重复设置`PreparedStatement`对象中的值，`updateSales`并且`updateTotal`：

```java
for (Map.Entry<String, Integer> e : salesForWeek.entrySet()) {

    updateSales.setInt(1, e.getValue().intValue());
    updateSales.setString(2, e.getKey());

    // ...
}
```

该方法`CoffeesTable.updateCoffeeSales`采用一个参数，`HashMap`。`HashMap`参数中的每个元素都包含一种咖啡的名称以及本周销售的那种咖啡的磅数。的for-each循环遍历所述的每一个元素`HashMap`参数，并设置在适当的问号占位符`updateSales`和`updateTotal`。

## 执行PreparedStatement对象

与`Statement`对象一样，要执行`PreparedStatement`对象，请调用execute语句：`executeQuery`如果查询只返回一个`ResultSet`（如`SELECT`SQL语句），`executeUpdate`如果查询没有返回`ResultSet`（如`UPDATE`SQL语句），或者`execute`查询可能返回更多而不是一个`ResultSet`对象。两个`PreparedStatement`对象都`CoffeesTable.updateCoffeeSales`包含`UPDATE`SQL语句，因此两者都通过调用来执行`executeUpdate`：

```java
updateSales.setInt(1, e.getValue().intValue());
updateSales.setString(2, e.getKey());
updateSales.executeUpdate();

updateTotal.setInt(1, e.getValue().intValue());
updateTotal.setString(2, e.getKey());
updateTotal.executeUpdate();
con.commit();
```

没有参数提供给`executeUpdate`当它们被用来执行`updateSales`和`updateTotals`; 两个`PreparedStatement`对象都已包含要执行的SQL语句。

**注意**：在开始时`CoffeesTable.updateCoffeeSales`，自动提交模式设置为false：

```java
con.setAutoCommit(false);
```

因此，在`commit`调用该方法之前，不会提交任何SQL语句。有关自动提交模式的更多信息，请参阅 [事务](https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html)。

### executeUpdate方法的返回值

而`executeQuery`回报`ResultSet`包含发送到DBMS，返回值的查询结果的对象`executeUpdate`是一个`int`指示表的有多少行被更新后的值。例如，以下代码显示了`executeUpdate`分配给变量的返回值`n`：

```java
updateSales.setInt(1, 50);
updateSales.setString(2, "Espresso");
int n = updateSales.executeUpdate();
// n = 1 because one row had a change in it
```

表格`COFFEES`已更新; 值50将替换`SALES`行中列中的值`Espresso`。该更新会影响表中的一行，因此`n`等于1。

当该方法`executeUpdate`用于执行一个DDL（数据定义语言）语句，如在创建表时，它返回`int`值0。因此，在下面的代码段，其执行用于创建表的DDL语句`COFFEES`，`n`是赋值为0：

```java
// n = 0
int n = executeUpdate(createTableCoffees); 
```

请注意，当返回值为`executeUpdate`0时，它可能意味着以下两种情况之一：

- 执行的语句是一个影响零行的更新语句。
- 执行的语句是DDL语句。