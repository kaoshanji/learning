# 从结果集中检索和修改值

以下方法`CoffeesTable.viewTable`输出`COFFEES`表的内容，并演示`ResultSet`对象和游标的使用：

```java
public static void viewTable(Connection con, String dbName)
    throws SQLException {

    Statement stmt = null;
    String query =
        "select COF_NAME, SUP_ID, PRICE, " +
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

甲`ResultSet`对象是表示数据库结果集，这通常是通过执行查询数据库的语句产生的数据的表。例如，该`CoffeeTables.viewTable`方法创建一个`ResultSet`，`rs`当它执行通过查询`Statement`对象，`stmt`。请注意，`ResultSet`对象可以通过实现任何对象创建`Statement`界面，包括`PreparedStatement`，`CallableStatement`，和`RowSet`。

您可以`ResultSet`通过游标访问对象中的数据。请注意，此游标不是数据库游标。该游标是一个指向该行中一行数据的指针`ResultSet`。最初，光标位于第一行之前。该方法`ResultSet.next`将光标移动到下一行。`false`如果光标位于最后一行之后，则返回此方法。此方法`ResultSet.next`使用`while`循环重复调用该方法以遍历所有数据`ResultSet`。

此页面包含以下主题：

- [ResultSet接口](https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html#rs_interface)
- [从行检索列值](https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html#retrieve_rs)
- [游标](https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html#cursors)
- [更新ResultSet对象中的行](https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html#rs_update)
- [使用语句对象进行批量更新](https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html#batch_updates)
- [在ResultSet对象中插入行](https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html#rs_insert)

## ResultSet接口

该`ResultSet`接口提供用于检索和操纵执行的查询的结果的方法，并且`ResultSet`对象可以具有不同的功能和特性。这些特征是类型，并发和光标*可保存性*。

### ResultSet类型

`ResultSet`对象的类型在两个方面确定其功能级别：可以操作游标的方式，以及对象如何反映对基础数据源的并发更改`ResultSet`。

`ResultSet`对象的灵敏度由三种不同`ResultSet`类型中的一种决定：

- `TYPE_FORWARD_ONLY`：结果集无法滚动; 它的光标仅向前移动，从第一行之前到最后一行之后。结果集中包含的行取决于底层数据库如何生成结果。也就是说，它包含在执行查询时或检索行时满足查询的行。
- `TYPE_SCROLL_INSENSITIVE`：结果可以滚动; 它的光标可以相对于当前位置向前和向后移动，并且它可以移动到绝对位置。结果集对基础数据源打开时所做的更改不敏感。它包含在执行查询时或检索行时满足查询的行。
- `TYPE_SCROLL_SENSITIVE`：结果可以滚动; 它的光标可以相对于当前位置向前和向后移动，并且它可以移动到绝对位置。结果集反映了在结果集保持打开状态时对基础数据源所做的更改。

默认`ResultSet`类型是`TYPE_FORWARD_ONLY`。

**注意**：并非所有数据库和JDBC驱动程序都支持所有`ResultSet`类型。如果支持指定的类型，则`DatabaseMetaData.supportsResultSetType`返回该方法。`true``ResultSet``false`

### ResultSet并发

`ResultSet`对象的并发性决定了支持的更新功能级别。

有两个并发级别：

- `CONCUR_READ_ONLY`：`ResultSet`无法使用`ResultSet`界面更新对象。
- `CONCUR_UPDATABLE`：`ResultSet`可以使用`ResultSet`界面更新对象。

默认`ResultSet`并发是`CONCUR_READ_ONLY`。

**注意**：并非所有JDBC驱动程序和数据库都支持并发。如果驱动程序支持指定的并发级别，则`DatabaseMetaData.supportsResultSetConcurrency`返回该方法。`true``false`

该方法`CoffeesTable.modifyPrices`演示了如何使用`ResultSet`并发级别为的对象`CONCUR_UPDATABLE`。

### 光标可保持性

调用该方法`Connection.commit`可以关闭`ResultSet`在当前事务期间创建的对象。但是，在某些情况下，这可能不是理想的行为。该`ResultSet`属性*可保存性*给出了是否应用控制`ResultSet`调用commit当对象（游标）关闭。

以下`ResultSet`常数可以被提供给所述`Connection`的方法`createStatement`，`prepareStatement`以及`prepareCall`：

- `HOLD_CURSORS_OVER_COMMIT`：`ResultSet`游标没有关闭; 它们是可以*保持的*：当`commit`调用方法时它们保持打开状态。如果您的应用程序主要使用只读`ResultSet`对象，则可保持游标可能是理想的。
- `CLOSE_CURSORS_AT_COMMIT`：调用方法`ResultSet`时关闭对象（游标）`commit`。调用此方法时关闭游标可以为某些应用程序带来更好的性能。

默认光标可保持性因DBMS而异。

**注意**：并非所有JDBC驱动程序和数据库都支持可保持和不可保留的游标。以下的方法，`JDBCTutorialUtilities.cursorHoldabilitySupport`而输出的缺省光标可保存性`ResultSet`对象和是否`HOLD_CURSORS_OVER_COMMIT`与`CLOSE_CURSORS_AT_COMMIT`被支持：

```java
public static void cursorHoldabilitySupport(Connection conn)
    throws SQLException {

    DatabaseMetaData dbMetaData = conn.getMetaData();
    System.out.println("ResultSet.HOLD_CURSORS_OVER_COMMIT = " +
        ResultSet.HOLD_CURSORS_OVER_COMMIT);

    System.out.println("ResultSet.CLOSE_CURSORS_AT_COMMIT = " +
        ResultSet.CLOSE_CURSORS_AT_COMMIT);

    System.out.println("Default cursor holdability: " +
        dbMetaData.getResultSetHoldability());

    System.out.println("Supports HOLD_CURSORS_OVER_COMMIT? " +
        dbMetaData.supportsResultSetHoldability(
            ResultSet.HOLD_CURSORS_OVER_COMMIT));

    System.out.println("Supports CLOSE_CURSORS_AT_COMMIT? " +
        dbMetaData.supportsResultSetHoldability(
            ResultSet.CLOSE_CURSORS_AT_COMMIT));
}
```

## 从行检索列值

所述`ResultSet`接口声明吸气剂的方法（例如，`getBoolean`和`getLong`），用于从当前行检索列值。您可以使用列的索引号或列的别名或名称来检索值。列索引通常更有效。列从1开始编号。为了获得最大的可移植性，每行中的结果集列应按从左到右的顺序读取，每列应只读一次。

例如，以下方法`CoffeesTable.alternateViewTable`按编号检索列值：

```java
public static void alternateViewTable(Connection con)
    throws SQLException {

    Statement stmt = null;
    String query =
        "select COF_NAME, SUP_ID, PRICE, " +
        "SALES, TOTAL from COFFEES";

    try {
        stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            String coffeeName = rs.getString(1);
            int supplierID = rs.getInt(2);
            float price = rs.getFloat(3);
            int sales = rs.getInt(4);
            int total = rs.getInt(5);
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

用作getter方法输入的字符串不区分大小写。当使用字符串调用getter方法并且多个列具有与字符串相同的别名或名称时，将返回第一个匹配列的值。使用字符串而不是整数的选项设计用于在生成结果集的SQL查询中使用列别名和名称时使用。对于*未*在查询中明确命名的列（例如，`select * from COFFEES`），最好使用列号。如果使用列名，开发人员应保证使用列别名唯一引用预期的列。列别名有效地重命名结果集的列。要指定列别名，请`AS`在`SELECT`语句中使用SQL 子句。

适当类型的getter方法检索每列中的值。例如，在该方法中`CoffeeTables.viewTable`，`ResultSet` `rs`is的每一行中的第一列`COF_NAME`存储SQL类型的值`VARCHAR`。检索SQL类型值的方法`VARCHAR`是`getString`。每行中的第二列存储SQL类型的值`INTEGER`，并且用于检索该类型的值的方法是`getInt`。

需要注意的是，虽然该方法`getString`被推荐用于检索SQL类型`CHAR`和`VARCHAR`，就可以用它获取任何SQL的基本类型。获取所有值`getString`非常有用，但它也有其局限性。例如，如果它用于检索数字类型，`getString`则将数值转换为Java `String`对象，并且必须先将值转换回数字类型，然后才能将其作为数字进行操作。如果将值视为字符串，则没有任何缺点。此外，如果希望应用程序检索除SQL3类型之外的任何标准SQL类型的值，请使用该`getString`方法。

## 游标

如前所述，您可以`ResultSet`通过游标访问对象中的数据，该游标指向`ResultSet`对象中的一行。但是，`ResultSet`首次创建对象时，光标位于第一行之前。该方法`CoffeeTables.viewTable`通过调用方法来移动光标`ResultSet.next`。还有其他可用于移动光标的方法：

- `next`：将光标向前移动一行。返回`true`如果光标现位于一行，并`false`当光标位于最后一行之后。
- `previous`：将光标向后移动一行。返回`true`如果光标现位于一行，并`false`当光标位于第一行之前。
- `first`：将光标移动到`ResultSet`对象的第一行。返回`true`如果光标现位于第一行`false`，如果`ResultSet`对象不包含任何行。
- `last:`：将光标移动到`ResultSet`对象中的最后一行。返回`true`如果光标现位于最后一排并`false`如果`ResultSet`对象不包含任何行。
- `beforeFirst`：将光标定位在`ResultSet`对象的开头，在第一行之前。如果`ResultSet`对象不包含任何行，则此方法无效。
- `afterLast`：将光标定位在`ResultSet`对象的末尾，在最后一行之后。如果`ResultSet`对象不包含任何行，则此方法无效。
- `relative(int rows)`：相对于当前位置移动光标。
- `absolute(int row)`：将光标定位在参数指定的行上`row`。

注意a的默认灵敏度`ResultSet`是`TYPE_FORWARD_ONLY`，这意味着它不能滚动; 您无法调用任何移动光标的方法，除非`next`您`ResultSet`无法滚动。`CoffeesTable.modifyPrices`下一节中描述的方法演示了如何移动a的光标`ResultSet`。

## 更新ResultSet对象中的行

您无法更新默认`ResultSet`对象，也只能向前移动光标。但是，您可以创建`ResultSet`可以滚动的对象（光标可以向后移动或移动到绝对位置）并更新。

以下方法将每行`CoffeesTable.modifyPrices`的`PRICE`列乘以参数`percentage`：

```java
public void modifyPrices(float percentage) throws SQLException {

    Statement stmt = null;
    try {
        stmt = con.createStatement();
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                   ResultSet.CONCUR_UPDATABLE);
        ResultSet uprs = stmt.executeQuery(
            "SELECT * FROM " + dbName + ".COFFEES");

        while (uprs.next()) {
            float f = uprs.getFloat("PRICE");
            uprs.updateFloat( "PRICE", f * percentage);
            uprs.updateRow();
        }

    } catch (SQLException e ) {
        JDBCTutorialUtilities.printSQLException(e);
    } finally {
        if (stmt != null) { stmt.close(); }
    }
}
```

该字段`ResultSet.TYPE_SCROLL_SENSITIVE`创建一个`ResultSet`对象，其光标可以相对于当前位置向前和向后移动到绝对位置。该字段`ResultSet.CONCUR_UPDATABLE`创建一个`ResultSet`可以更新的对象。有关`ResultSet`可以指定的其他字段，请参阅Javadoc以修改`ResultSet`对象的行为。

该方法`ResultSet.updateFloat`更新指定的列（在此示例中，`PRICE`使用`float`光标所在行中的指定值。）`ResultSet`包含各种更新程序方法，使您可以更新各种数据类型的列值。但是，这些更新程序方法都不会修改数据库;您必须调用该方法`ResultSet.updateRow`来更新数据库。

## 使用语句对象进行批量更新

`Statement`，`PreparedStatement`和`CallableStatement`对象有一个与它们相关联的命令列表。该列表可能包含更新，插入或删除行的语句; 它也可能包含DDL语句，例如`CREATE TABLE`和`DROP TABLE`。但是，它不能包含会产生`ResultSet`对象的`SELECT`语句，例如语句。换句话说，列表只能包含产生更新计数的语句。

`Statement`在创建对象时与其关联的列表最初为空。您可以使用该方法将SQL命令添加到此列表中，`addBatch`并使用该方法将其清空`clearBatch`。完成向列表添加语句后，调用方法`executeBatch`将它们全部发送到数据库，以便作为一个单元或批处理执行。

例如，以下方法通过批量更新`CoffeesTable.batchUpdate`向`COFFEES`表中添加四行：

```java
public void batchUpdate() throws SQLException {

    Statement stmt = null;
    try {
        this.con.setAutoCommit(false);
        stmt = this.con.createStatement();

        stmt.addBatch(
            "INSERT INTO COFFEES " +
            "VALUES('Amaretto', 49, 9.99, 0, 0)");

        stmt.addBatch(
            "INSERT INTO COFFEES " +
            "VALUES('Hazelnut', 49, 9.99, 0, 0)");

        stmt.addBatch(
            "INSERT INTO COFFEES " +
            "VALUES('Amaretto_decaf', 49, " +
            "10.99, 0, 0)");

        stmt.addBatch(
            "INSERT INTO COFFEES " +
            "VALUES('Hazelnut_decaf', 49, " +
            "10.99, 0, 0)");

        int [] updateCounts = stmt.executeBatch();
        this.con.commit();

    } catch(BatchUpdateException b) {
        JDBCTutorialUtilities.printBatchUpdateException(b);
    } catch(SQLException ex) {
        JDBCTutorialUtilities.printSQLException(ex);
    } finally {
        if (stmt != null) { stmt.close(); }
        this.con.setAutoCommit(true);
    }
}
```

以下行禁用`Connection`对象con的自动提交模式，以便在`executeBatch`调用方法时不会自动提交或回滚事务。

```java
this.con.setAutoCommit(false);
```

要允许正确的错误处理，应始终在开始批量更新之前禁用自动提交模式。

该方法`Statement.addBatch`将命令添加到与该`Statement`对象关联的命令列表`stmt`。在此示例中，这些命令是所有`INSERT INTO`语句，每个语句都添加一个由五个列值组成的行。对于列中的值`COF_NAME`，并`PRICE`有咖啡，其价格的名义，分别。每行中的第二个值为49，因为这是供应商Superior Coffee的标识号。最后两个值，列条目`SALES`和`TOTAL`，都开始出来是零，因为没有出现过的销售呢。（这`SALES`是本周销售的这一排咖啡的磅数; `TOTAL`是这种咖啡累计销售额的总和。）

以下行将添加到其命令列表中的四个SQL命令发送到要作为批处理执行的数据库：

```java
int [] updateCounts = stmt.executeBatch();
```

请注意，`stmt`使用该方法`executeBatch`发送批量插入，而不是方法`executeUpdate`，该方法仅发送一个命令并返回单个更新计数。DBMS按照将命令添加到命令列表的顺序执行命令，因此它首先添加Amaretto的值行，然后添加Hazelnut，然后是Amaretto decaf，最后是Hazelnut decaf。如果所有四个命令都成功执行，则DBMS将按照执行顺序为每个命令返回更新计数。更新计数指示每个命令影响的行数存储在数组中`updateCounts`。

如果批处理中的所有四个命令都成功执行，`updateCounts`则将包含四个值，所有这些值都是1，因为插入会影响一行。与之关联的命令列表`stmt`现在将为空，因为先前添加的四个命令在`stmt`调用方法时被发送到数据库`executeBatch`。您可以随时使用该方法显式清空此命令列表`clearBatch`。

该`Connection.commit`方法使`COFFEES`表的更新批次永久化。需要显式调用此方法，因为此连接的自动提交模式先前已禁用。

以下行为当前`Connection`对象启用自动提交模式。

```java
this.con.setAutoCommit(true);
```

现在，示例中的每个语句将在执行后自动提交，并且不再需要调用该方法`commit`。

### 执行参数化批量更新

也可以进行参数化批量更新，如下面的代码片段所示，其中`con`是一个`Connection`对象：

```java
con.setAutoCommit(false);
PreparedStatement pstmt = con.prepareStatement(
                              "INSERT INTO COFFEES VALUES( " +
                              "?, ?, ?, ?, ?)");
pstmt.setString(1, "Amaretto");
pstmt.setInt(2, 49);
pstmt.setFloat(3, 9.99);
pstmt.setInt(4, 0);
pstmt.setInt(5, 0);
pstmt.addBatch();

pstmt.setString(1, "Hazelnut");
pstmt.setInt(2, 49);
pstmt.setFloat(3, 9.99);
pstmt.setInt(4, 0);
pstmt.setInt(5, 0);
pstmt.addBatch();

// ... and so on for each new
// type of coffee

int [] updateCounts = pstmt.executeBatch();
con.commit();
con.setAutoCommit(true);
```

### 处理批量更新例外

一些人`BatchUpdateException`在调用方法时会得到一个`executeBatch`如果（1）你添加到批处理中的一个SQL语句产生一个结果集（通常是一个查询）或（2）批处理中的一个SQL语句没有成功执行某些其他原因。

您不应该`SELECT`向一批SQL命令添加查询（语句），因为`executeBatch`返回更新计数数组的方法需要每个成功执行的SQL语句的更新计数。这意味着，只有返回更新计数（诸如命令的命令`INSERT INTO`，`UPDATE`，`DELETE`），或者返回0（例如`CREATE TABLE`，`DROP TABLE`，`ALTER TABLE`）可以作为与所述批处理成功执行`executeBatch`方法。

A `BatchUpdateException`包含一组更新计数，类似于方法返回的数组`executeBatch`。在这两种情况下，更新计数的顺序与生成它们的命令的顺序相同。这告诉您批处理中有多少命令成功执行以及它们是哪些命令。例如，如果成功执行了五个命令，则该数组将包含五个数字：第一个是第一个命令的更新计数，第二个是第二个命令的更新计数，依此类推。

`BatchUpdateException`源于`SQLException`。这意味着您可以使用`SQLException`对象可用的所有方法。以下方法`JDBCTutorialUtilities.printBatchUpdateException`打印所有`SQLException`信息以及`BatchUpdateException`对象中包含的更新计数。因为`BatchUpdateException.getUpdateCounts`返回一个数组`int`，代码使用`for`循环来打印每个更新计数：

```java
public static void printBatchUpdateException(BatchUpdateException b) {

    System.err.println("----BatchUpdateException----");
    System.err.println("SQLState:  " + b.getSQLState());
    System.err.println("Message:  " + b.getMessage());
    System.err.println("Vendor:  " + b.getErrorCode());
    System.err.print("Update counts:  ");
    int [] updateCounts = b.getUpdateCounts();

    for (int i = 0; i < updateCounts.length; i++) {
        System.err.print(updateCounts[i] + "   ");
    }
}
```

## 在ResultSet对象中插入行

**注意**：并非所有JDBC驱动程序都支持使用该`ResultSet`接口插入新行。如果尝试插入新行且JDBC驱动程序数据库不支持此功能，`SQLFeatureNotSupportedException`则会引发异常。

以下方法`CoffeesTable.insertRow`在`COFFEES`通过`ResultSet`对象中插入一行：

```java
public void insertRow(String coffeeName, int supplierID,
                      float price, int sales, int total)
    throws SQLException {

    Statement stmt = null;
    try {
        stmt = con.createStatement(
            ResultSet.TYPE_SCROLL_SENSITIVE
            ResultSet.CONCUR_UPDATABLE);

        ResultSet uprs = stmt.executeQuery(
            "SELECT * FROM " + dbName +
            ".COFFEES");

        uprs.moveToInsertRow();
        uprs.updateString("COF_NAME", coffeeName);
        uprs.updateInt("SUP_ID", supplierID);
        uprs.updateFloat("PRICE", price);
        uprs.updateInt("SALES", sales);
        uprs.updateInt("TOTAL", total);

        uprs.insertRow();
        uprs.beforeFirst();
    } catch (SQLException e ) {
        JDBCTutorialUtilities.printSQLException(e);
    } finally {
        if (stmt != null) { stmt.close(); }
    }
}
```

此示例`Connection.createStatement`使用两个参数调用该方法，`ResultSet.TYPE_SCROLL_SENSITIVE`并且`ResultSet.CONCUR_UPDATABLE`。第一个值使`ResultSet`对象的光标可以向前和向后移动。`ResultSet.CONCUR_UPDATABLE`如果要将行插入`ResultSet`对象，则需要第二个值; 它指定它可以更新。

在getter方法中使用字符串的相同规定也适用于updater方法。

该方法`ResultSet.moveToInsertRow`将光标移动到插入行。插入行是与可更新结果集关联的特殊行。它本质上是一个缓冲区，可以通过在将行插入结果集之前调用updater方法来构造新行。例如，此方法调用方法`ResultSet.updateString`将插入行的`COF_NAME`列更新为`Kona`。

该方法`ResultSet.insertRow`将插入行的内容插入到`ResultSet`对象和数据库中。

**注意**：使用插入行后`ResultSet.insertRow`，应将光标移动到插入行以外的行。例如，此示例使用该方法将其移动到结果集中的第一行之前`ResultSet.beforeFirst`。如果应用程序的另一部分使用相同的结果集并且光标仍指向插入行，则可能会出现意外结果。