# 使用RowId对象

**注意**：MySQL和Java DB当前不支持`RowId`JDBC接口。因此，没有JDBC教程示例可用于演示本节中描述的功能。

甲`RowId`对象表示在数据库中的表中的地址的行。但请注意，该`ROWID`类型不是标准SQL类型。`ROWID`值很有用，因为它们通常是访问单行的最快方式，并且是表中行的唯一标识。但是，您不应将`ROWID`值用作表的主键。例如，如果从表中删除特定行，则数据库可能会将其`ROWID`值重新分配给稍后插入的行。

涵盖以下主题：

- [检索RowId对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlrowid.html#retrieving_rowid_objects)
- [使用RowId对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlrowid.html#using_rowid_objects)
- [RowId有效期的生命周期](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlrowid.html#lifetime_rowid_validity)

## 检索RowId对象

检索`java.sql.RowId`通过调用在接口中定义的getter方法对象`ResultSet`和`CallableStatement`。`RowId`返回的对象是一个不可变对象，您可以将其用作后续引用作为行的唯一标识符。以下是调用该`ResultSet.getRowId`方法的示例：

```java
java.sql.RowId rowId_1 = rs.getRowId(1);
```

## 使用RowId对象

您可以将`RowId`对象设置为参数化`PreparedStatement`对象中的参数：

```java
Connection conn = ds.getConnection(username, password);
PreparedStatement ps = conn.prepareStatement(
    "INSERT INTO BOOKLIST" +
    "(ID, AUTHOR, TITLE, ISBN) " +
    "VALUES (?, ?, ?, ?)");
ps.setRowId(1, rowId_1);
```

您还可以`RowId`使用可更新`ResultSet`对象中的特定对象更新列：

```java
ResultSet rs = ...
rs.next();
rs.updateRowId(1, rowId_1);
```

甲`RowId`对象值通常不是数据源之间的便携式和应在使用时所述一组或更新方法被认为是特定于数据源`PreparedStatement`和`ResultSet`目的，分别。因此，不建议`RowId`从具有`ResultSet`与一个数据源的连接的对象获取对象，然后尝试`RowId`在不相关的`ResultSet`对象中使用相同的对象并连接到不同的数据源。

## RowId有效期的生命周期

甲`RowId`目的是有效，只要所识别的行没有被删除和的寿命`RowId`目标是通过用于该数据源所指定的寿命的范围内`RowId`。

要确定`RowId`数据库或数据源的对象的生命周期，请调用该方法`DatabaseMetaData.getRowIdLifetime`。它返回`RowIdLifetime`枚举数据类型的值。以下方法[`JDBCTutorialUtilities.rowIdLifeTime`](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)返回`RowId`对象的生命周期：

```java
public static void rowIdLifetime(Connection conn)
    throws SQLException {

    DatabaseMetaData dbMetaData = conn.getMetaData();
    RowIdLifetime lifetime = dbMetaData.getRowIdLifetime();

    switch (lifetime) {
        case ROWID_UNSUPPORTED:
            System.out.println("ROWID type not supported");
            break;

        case ROWID_VALID_FOREVER:
            System.out.println("ROWID has unlimited lifetime");
            break;

        case ROWID_VALID_OTHER:
            System.out.println("ROWID has indeterminate lifetime");
            break;

        case ROWID_VALID_SESSION:
            System.out.println(
                "ROWID type has lifetime that " +
                "is valid for at least the " +
                "containing session");
            break;

        case ROWID_VALID_TRANSACTION:
            System.out.println(
                "ROWID type has lifetime that " +
                "is valid for at least the " +
                "containing transaction");
            break;
    }
}
```

