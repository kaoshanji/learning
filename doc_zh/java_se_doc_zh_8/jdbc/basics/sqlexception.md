# 处理SQLExceptions

此页面包含以下主题：

- [SQLException概述](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html#overview_sqlexception)
- [检索例外](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html#retrieving_exceptions)
- [检索警告](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html#retrieving_warnings)
- [分类的SQLExceptions](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html#categorized_sqlexceptions)
- [SQLException的其他子类](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html#subclasses_sqlexception)

## SQLException概述

当JDBC在与数据源交互期间遇到错误时，它会抛出一个实例，`SQLException`而不是`Exception`。（此上下文中的数据源表示`Connection`对象所连接的数据库。）该`SQLException`实例包含以下信息，可帮助您确定错误原因：

- 错误的描述。`String`通过调用方法检索包含此描述的对象`SQLException.getMessage`。
- 一个SQLState代码。这些代码及其各自的含义已经由ISO / ANSI和Open Group（X / Open）标准化，尽管已经为数据库供应商保留了一些代码来自行定义。该`String`对象由五个字母数字字符组成。通过调用方法检索此代码`SQLException.getSQLState`。
- 错误代码。这是一个整数值，用于标识导致`SQLException`实例被抛出的错误。它的值和含义是特定于实现的，可能是底层数据源返回的实际错误代码。通过调用方法检索错误`SQLException.getErrorCode`。
- 一个原因。一个`SQLException`实例可能有一个因果关系，它由一个或多个`Throwable`引起的对象`SQLException`实例抛出。要导航此原因链，请递归调用该方法，`SQLException.getCause`直到`null`返回值。
- 对任何*链式*异常的引用。如果发生多个错误，则通过此链引用异常。通过调用`SQLException.getNextException`抛出的异常上的方法来检索这些异常。

## 检索例外

以下方法`JDBCTutorialUtilities.printSQLException`输出SQLState，错误代码，错误描述和原因（如果有）`SQLException`以及链接到它的任何其他异常：

```java
public static void printSQLException(SQLException ex) {

    for (Throwable e : ex) {
        if (e instanceof SQLException) {
            if (ignoreSQLException(
                ((SQLException)e).
                getSQLState()) == false) {

                e.printStackTrace(System.err);
                System.err.println("SQLState: " +
                    ((SQLException)e).getSQLState());

                System.err.println("Error Code: " +
                    ((SQLException)e).getErrorCode());

                System.err.println("Message: " + e.getMessage());

                Throwable t = ex.getCause();
                while(t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
```

例如，如果`CoffeesTable.dropTable`使用Java DB作为DBMS 调用该方法，该表`COFFEES`不存在，*并且*删除调用`JDBCTutorialUtilities.ignoreSQLException`，则输出将类似于以下内容：

```bash
SQLState: 42Y55
Error Code: 30000
Message: 'DROP TABLE' cannot be performed on
'TESTDB.COFFEES' because it does not exist.
```

`SQLException`您可以改为首先检索相应的`SQLState`后续处理，而不是输出信息`SQLException`。例如，如果等于代码（并且您使用Java DB作为DBMS），则该方法`JDBCTutorialUtilities.ignoreSQLException`返回，这会导致忽略：`true``SQLState``42Y55``JDBCTutorialUtilities.printSQLException``SQLException`

```java
public static boolean ignoreSQLException(String sqlState) {

    if (sqlState == null) {
        System.out.println("The SQL state is not defined!");
        return false;
    }

    // X0Y32: Jar file already exists in schema
    if (sqlState.equalsIgnoreCase("X0Y32"))
        return true;

    // 42Y55: Table already exists in schema
    if (sqlState.equalsIgnoreCase("42Y55"))
        return true;

    return false;
}
```

## 检索警告

`SQLWarning`对象是`SQLException`处理数据库访问警告的子类。警告不会像例外那样停止执行应用程序; 他们只是提醒用户某些事情没有按计划发生。例如，警告可能会让您知道您尝试撤销的权限未被撤销。或者警告可能会告诉您在请求的断开连接期间发生了错误。

警告可以在上报告`Connection`对象，`Statement`对象（包括`PreparedStatement`与`CallableStatement`对象），或一个`ResultSet`对象。这些类中的每一个都有一个`getWarnings`方法，您必须调用该方法才能看到在调用对象上报告的第一个警告。如果`getWarnings`返回警告，您可以调用其上的`SQLWarning`方法`getNextWarning`以获取任何其他警告。执行语句会自动清除先前语句中的警告，因此它们不会构建。但是，这意味着如果要检索语句上报告的警告，则必须在执行另一个语句之前执行此操作。

以下方法`JDBCTutorialUtilities`说明如何获取有关报告的任何警告`Statement`或`ResultSet`对象的完整信息：

```java
public static void getWarningsFromResultSet(ResultSet rs)
    throws SQLException {
    JDBCTutorialUtilities.printWarnings(rs.getWarnings());
}

public static void getWarningsFromStatement(Statement stmt)
    throws SQLException {
    JDBCTutorialUtilities.printWarnings(stmt.getWarnings());
}

public static void printWarnings(SQLWarning warning)
    throws SQLException {

    if (warning != null) {
        System.out.println("\n---Warning---\n");

    while (warning != null) {
        System.out.println("Message: " + warning.getMessage());
        System.out.println("SQLState: " + warning.getSQLState());
        System.out.print("Vendor error code: ");
        System.out.println(warning.getErrorCode());
        System.out.println("");
        warning = warning.getNextWarning();
    }
}
```

最常见的警告是`DataTruncation`警告，是子类`SQLWarning`。所有`DataTruncation`对象都具有SQLState `01004`，表示读取或写入数据时出现问题。`DataTruncation`方法可以让您找出截断的列或参数数据，截断是在读操作还是写操作，应该传输多少字节，以及实际传输了多少字节。

## 分类的SQLExceptions

您的JDBC驱动程序可能会抛出`SQLException`与常见SQLState相对应的子类或与特定SQLState类值无关的常见错误状态。这使您可以编写更多可移植的错误处理代码。这些异常是以下类之一的子类：

- `SQLNonTransientException`
- `SQLTransientException`
- `SQLRecoverableException`

`java.sql`有关这些子类的更多信息，请参阅软件包的最新Javadoc 或JDBC驱动程序的文档。

## SQLException的其他子类

`SQLException`还可以抛出以下子类：

- `BatchUpdateException`在批量更新操作期间发生错误时抛出。除了提供的信息之外`SQLException`，`BatchUpdateException`还提供了在错误发生之前执行的所有语句的更新计数。
- `SQLClientInfoException`在无法在Connection上设置一个或多个客户端信息属性时抛出。除了提供的信息之外`SQLException`，`SQLClientInfoException`还提供了未设置的客户端信息属性列表。