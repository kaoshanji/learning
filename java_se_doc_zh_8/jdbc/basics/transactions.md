# 使用事务

有时您不希望一个语句生效，除非另一个语句完成。例如，当咖啡休息的所有者更新每周销售的咖啡量时，所有者还将想要更新迄今为止销售的总量。但是，每周销售的金额和销售总额应同时更新; 否则，数据将不一致。确保两个操作都发生或两个操作都不发生的方法是使用事务。事务是一组一个或多个语句，它们作为一个单元执行，因此要么执行所有语句，要么不执行任何语句。

本页面包含以下主题

- [禁用自动提交模式](https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html#disable_auto_commit)
- [提交交易](https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html#commit_transactions)
- [使用事务来保持数据完整性](https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html#transactions_data_integrity)
- [设置和回滚到保存点](https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html#set_roll_back_savepoints)
- [发布保存点](https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html#release_savepoints)
- [何时进行Call Method回滚](https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html#call_rollback)

## 禁用自动提交模式

创建连接时，它处于自动提交模式。这意味着每个单独的SQL语句都被视为一个事务，并在执行后立即自动提交。（更确切地说，默认情况下，SQL语句在完成时提交，而不是在执行时提交。语句在检索到所有结果集和更新计数时完成。几乎在所有情况下， ，声明在执行后立即完成并因此被提交。）

允许将两个或多个语句分组到事务中的方法是禁用自动提交模式。这在以下代码中进行了演示，其中`con`是活动连接：

```java
con.setAutoCommit(false);
```

## 提交交易

禁用自动提交模式后，在`commit`显式调用方法之前不会提交任何SQL语句。在上一次调用方法之后执行的所有语句`commit`都包含在当前事务中，并作为一个单元一起提交。下面的方法，`CoffeesTable.updateCoffeeSales`在其中`con`是一个活动连接，示出了事务：

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

在该方法中，自动提交模式是为连接禁用`con`的，这意味着在两个准备的语句`updateSales`和`updateTotal`致力于一起当该方法`commit`被调用。无论何时`commit`调用该方法（在启用自动提交模式时自动执行，或在禁用它时显式执行），事务中语句产生的所有更改都将成为永久更改。在这种情况下，这意味着，`SALES`和`TOTAL`哥伦比亚咖啡列已更改为`50`（如果`TOTAL`已经`0`以前），直到他们与另一个更新语句更改将保留该值。

该语句`con.setAutoCommit(true);`启用自动提交模式，这意味着每个语句在完成后将再次自动提交。然后，您将返回默认状态，您无需`commit`自己调用该方法。建议仅在事务模式期间禁用自动提交模式。这样，您可以避免为多个语句保留数据库锁，这会增加与其他用户冲突的可能性。

## 使用事务来保持数据完整性

除了将语句组合在一起作为一个单元执行之外，事务还可以帮助保持表中数据的完整性。例如，假设一名员工应该在餐桌上输入新的咖啡价格，`COFFEES`但推迟了几天。与此同时，价格上涨，今天业主正在进入更高的价格。在所有者试图更新表格的同时，员工最终会进入现在过时的价格。在插入过时的价格后，员工意识到它们不再有效并调用该`Connection`方法`rollback`来撤消其效果。（该方法`rollback`中止事务并将值恢复为尝试更新之前的值。）同时，所有者正在执行`SELECT`声明和印刷新价格。在这种情况下，所有者可能会打印已回滚到其先前值的价格，从而使打印价格不正确。

通过使用事务可以避免这种情况，为两个用户同时访问数据时出现的冲突提供某种程度的保护。

为了避免在事务期间发生冲突，DBMS使用锁定机制来阻止其他人访问事务正在访问的数据。（请注意，在自动提交模式下，每个语句都是一个事务，只保留一个语句的锁定。）设置锁定后，它将一直有效，直到提交或回滚事务为止。例如，DBMS可以锁定表的一行，直到对其进行更新为止。此锁定的作用是防止用户获取脏读，即在永久化之前读取值。（访问尚未提交的更新值被视为*脏读*因为该值可以回滚到其先前的值。如果您读取稍后回滚的值，则会读取无效值。）

如何设置锁定由所谓的事务隔离级别决定，其范围从完全不支持事务到支持执行非常严格的访问规则的事务。

事务隔离级别的一个示例是`TRANSACTION_READ_COMMITTED`，在提交之前不允许访问值。换句话说，如果事务隔离级别设置为`TRANSACTION_READ_COMMITTED`，则DBMS不允许发生脏读。该接口`Connection`包含五个值，表示可在JDBC中使用的事务隔离级别：

| 隔离级别                       | 交易   | 脏读     | 不可重复读 | 幻影阅读 |
| ------------------------------ | ------ | -------- | ---------- | -------- |
| `TRANSACTION_NONE`             | 不支持 | *不适用* | *不适用*   | *不适用* |
| `TRANSACTION_READ_COMMITTED`   | 支持的 | 防止     | 允许       | 允许     |
| `TRANSACTION_READ_UNCOMMITTED` | 支持的 | 允许     | 允许       | 允许     |
| `TRANSACTION_REPEATABLE_READ`  | 支持的 | 防止     | 防止       | 允许     |
| `TRANSACTION_SERIALIZABLE`     | 支持的 | 防止     | 防止       | 防止     |

一个*不可重复读*当事务A检索行，事务B随后更新该行和事务稍后再检索同一行发生。事务A检索同一行两次但看到不同的数据。

甲*幻像读*当事务A检索一组满足给定的条件，事务B随后插入或更新的一行，使得行现在满足事务A的条件的行，和事务A以后重复该条件检索发生。事务A现在看到一个额外的行。这一行被称为幻像。

通常，您不需要对事务隔离级别执行任何操作; 您可以使用默认的DBMS。默认事务隔离级别取决于您的DBMS。例如，对于Java DB，它是`TRANSACTION_READ_COMMITTED`。JDBC允许您找出DBMS设置的事务隔离级别（使用该`Connection`方法`getTransactionIsolation`），并允许您将其设置为另一个级别（使用该`Connection`方法`setTransactionIsolation`）。

**注意**：JDBC驱动程序可能不支持所有事务隔离级别。如果驱动程序不支持调用中指定的隔离级别`setTransactionIsolation`，则驱动程序可以替换更高，更严格的事务隔离级别。如果驱动程序无法替换更高的事务级别，则会抛出一个`SQLException`。使用此方法`DatabaseMetaData.supportsTransactionIsolationLevel`确定驱动程序是否支持给定级别。

## 设置和回滚到保存点

该方法在当前事务中`Connection.setSavepoint`设置`Savepoint`对象。`Connection.rollback`重载该方法以获取`Savepoint`参数。

以下方法`CoffeesTable.modifyPricesByPercentage`将特定咖啡的价格提高一个百分比`priceModifier`。但是，如果新价格大于指定价格`maximumPrice`，则价格将恢复为原始价格：

```java
public void modifyPricesByPercentage(
    String coffeeName,
    float priceModifier,
    float maximumPrice)
    throws SQLException {
    
    con.setAutoCommit(false);

    Statement getPrice = null;
    Statement updatePrice = null;
    ResultSet rs = null;
    String query =
        "SELECT COF_NAME, PRICE FROM COFFEES " +
        "WHERE COF_NAME = '" + coffeeName + "'";

    try {
        Savepoint save1 = con.setSavepoint();
        getPrice = con.createStatement(
                       ResultSet.TYPE_SCROLL_INSENSITIVE,
                       ResultSet.CONCUR_READ_ONLY);
        updatePrice = con.createStatement();

        if (!getPrice.execute(query)) {
            System.out.println(
                "Could not find entry " +
                "for coffee named " +
                coffeeName);
        } else {
            rs = getPrice.getResultSet();
            rs.first();
            float oldPrice = rs.getFloat("PRICE");
            float newPrice = oldPrice + (oldPrice * priceModifier);
            System.out.println(
                "Old price of " + coffeeName +
                " is " + oldPrice);

            System.out.println(
                "New price of " + coffeeName +
                " is " + newPrice);

            System.out.println(
                "Performing update...");

            updatePrice.executeUpdate(
                "UPDATE COFFEES SET PRICE = " +
                newPrice +
                " WHERE COF_NAME = '" +
                coffeeName + "'");

            System.out.println(
                "\nCOFFEES table after " +
                "update:");

            CoffeesTable.viewTable(con);

            if (newPrice > maximumPrice) {
                System.out.println(
                    "\nThe new price, " +
                    newPrice +
                    ", is greater than the " +
                    "maximum price, " +
                    maximumPrice +
                    ". Rolling back the " +
                    "transaction...");

                con.rollback(save1);

                System.out.println(
                    "\nCOFFEES table " +
                    "after rollback:");

                CoffeesTable.viewTable(con);
            }
            con.commit();
        }
    } catch (SQLException e) {
        JDBCTutorialUtilities.printSQLException(e);
    } finally {
        if (getPrice != null) { getPrice.close(); }
        if (updatePrice != null) {
            updatePrice.close();
        }
        con.setAutoCommit(true);
    }
}
```

以下语句指定在调用方法时关闭`ResultSet`从`getPrice`查询生成的对象的游标`commit`。请注意，如果您的DBM不支持`ResultSet.CLOSE_CURSORS_AT_COMMIT`，则忽略此常量：

```java
getPrice = con.prepareStatement(query, ResultSet.CLOSE_CURSORS_AT_COMMIT);
```

该方法首先`Savepoint`使用以下语句创建：

```java
Savepoint save1 = con.setSavepoint();
```

该方法检查新价格是否大于该`maximumPrice`值。如果是，则该方法使用以下语句回滚事务：

```java
con.rollback(save1);
```

因此，当方法通过调用方法提交事务时`Connection.commit`，它不会提交任何`Savepoint`已关联的已回滚的行; 它将提交所有其他更新的行。

## 发布保存点

该方法`Connection.releaseSavepoint`将`Savepoint`对象作为参数，并将其从当前事务中删除。

释放保存点后，尝试在回滚操作中引用它会导致`SQLException`抛出该保存点。在事务提交时或回滚整个事务时，事务中创建的任何保存点都将自动释放并变为无效。将事务滚动回保存点会自动释放并使在相关保存点之后创建的任何其他保存点无效。

## 何时进行Call Method回滚

如前所述，调用该方法会`rollback`终止一个事务并返回修改为其先前值的任何值。如果您尝试在事务中执行一个或多个语句并获取a `SQLException`，请调用该方法`rollback`以结束事务并重新开始事务。这是了解已提交内容和未提交内容的唯一方法。捕获a `SQLException`告诉您出现了问题，但它没有告诉您什么是未提交的或未提交的。因为你不能指望什么都没有提交，所以调用方法`rollback`是唯一的方法。

该方法`CoffeesTable.updateCoffeeSales`演示了一个事务，并包含一个`catch`调用该方法的块`rollback`。如果应用程序继续并使用事务的结果，则`rollback`对`catch`块中方法的此调用可防止使用可能不正确的数据。

