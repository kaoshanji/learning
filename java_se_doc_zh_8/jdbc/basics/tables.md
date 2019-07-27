# 设置表

此页面描述了JDBC教程中使用的所有表以及如何创建它们：

- [咖啡桌](https://docs.oracle.com/javase/tutorial/jdbc/basics/tables.html#coffees)
- [供应商表](https://docs.oracle.com/javase/tutorial/jdbc/basics/tables.html#suppliers)
- [COF_INVENTORY表](https://docs.oracle.com/javase/tutorial/jdbc/basics/tables.html#cof_inventory)
- [MERCH_INVENTORY表](https://docs.oracle.com/javase/tutorial/jdbc/basics/tables.html#merch_inventory)
- [COFFEE_HOUSES表](https://docs.oracle.com/javase/tutorial/jdbc/basics/tables.html#coffee_houses)
- [DATA_REPOSITORY表](https://docs.oracle.com/javase/tutorial/jdbc/basics/tables.html#data_repository)
- [创建表](https://docs.oracle.com/javase/tutorial/jdbc/basics/tables.html#create)
- [填充表格](https://docs.oracle.com/javase/tutorial/jdbc/basics/tables.html#populate)

## 咖啡桌

该`COFFEES`表存储了咖啡休息时间可供销售的咖啡的信息：

| `COF_NAME`         | `SUP_ID` | `PRICE` | `SALES` | `TOTAL` |
| ------------------ | -------- | ------- | ------- | ------- |
| Colombian          | 101      | 7.99    | 0       | 0       |
| French_Roast       | 49       | 8.99    | 0       | 0       |
| Espresso           | 150      | 9.99    | 0       | 0       |
| Colombian_Decaf    | 101      | 8.99    | 0       | 0       |
| French_Roast_Decaf | 49       | 9.99    | 0       | 0       |

以下描述了表中的每个列`COFFEES`：

- `COF_NAME`：存储咖啡名称。保存SQL类型的值，`VARCHAR`最大长度为32个字符。因为每种类型的咖啡的名称都不同，所以该名称唯一地标识特定的咖啡并且用作主键。
- `SUP_ID`：存储识别咖啡供应商的编号。保存SQL类型为的值`INTEGER`。它被定义为引用的列的外键`SUP_ID`的`SUPPLIERS`表。因此，DBMS将强制执行此列中的每个值与`SUPPLIERS`表中相应列中的某个值匹配。
- `PRICE`：存储每磅咖啡的成本。保持SQL类型的值，`FLOAT`因为它需要保存带小数点的值。（请注意，货币值通常存储在SQL类型中，`DECIMAL`或者`NUMERIC`由于DBMS之间的差异，并且为了避免与早期版本的JDBC不兼容，本教程使用更标准的类型`FLOAT`。）
- `SALES`：存储本周销售的咖啡磅数。保存SQL类型为的值`INTEGER`。
- `TOTAL`：存储迄今为止销售的咖啡磅数。保存SQL类型为的值`INTEGER`。

## 供应商表

`SUPPLIERS`关于每个供应商的商店信息：

| `SUP_ID` | `SUP_NAME`      | `STREET`         | `CITY`       | `STATE` | `ZIP` |
| -------- | --------------- | ---------------- | ------------ | ------- | ----- |
| 101      | Acme, Inc.      | 99 Market Street | Groundsville | CA      | 95199 |
| 49       | Superior Coffee | 1 Party Place    | Mendocino    | CA      | 95460 |
| 150      | The High Ground | 100 Coffee Lane  | Meadows      | CA      | 93966 |

以下描述了表中的每个列`SUPPLIERS`：

- `SUP_ID`：存储识别咖啡供应商的编号。保存SQL类型为的值`INTEGER`。它是此表中的主键。
- `SUP_NAME`：存储咖啡供应商的名称。
- `STREET`，`CITY`，`STATE`，和`ZIP`：这些列存储咖啡供应商的地址。

## COF_INVENTORY表

该表`COF_INVENTORY`存储有关每个仓库中存储的咖啡量的信息：

| `WAREHOUSE_ID` | `COF_NAME`        | `SUP_ID` | `QUAN` | `DATE_VAL` |
| -------------- | ----------------- | -------- | ------ | ---------- |
| 1234           | House_Blend       | 49       | 0      | 2006_04_01 |
| 1234           | House_Blend_Decaf | 49       | 0      | 2006_04_01 |
| 1234           | Colombian         | 101      | 0      | 2006_04_01 |
| 1234           | French_Roast      | 49       | 0      | 2006_04_01 |
| 1234           | Espresso          | 150      | 0      | 2006_04_01 |
| 1234           | Colombian_Decaf   | 101      | 0      | 2006_04_01 |

以下描述了表中的每个列`COF_INVENTORY`：

- `WAREHOUSE_ID`：存储标识仓库的编号。
- `COF_NAME`：存储特定类型咖啡的名称。
- `SUP_ID`：存储标识供应商的编号。
- `QUAN`：存储一个表示可用商品数量的数字。
- `DATE`：存储时间戳值，指示上次更新行的时间。

## MERCH_INVENTORY表

该表`MERCH_INVENTORY`存储有关库存中非咖啡商品数量的信息：

| `ITEM_ID` | `ITEM_NAME` | `SUP_ID` | `QUAN` | `DATE`     |
| --------- | ----------- | -------- | ------ | ---------- |
| 00001234  | Cup_Large   | 00456    | 28     | 2006_04_01 |
| 00001235  | Cup_Small   | 00456    | 36     | 2006_04_01 |
| 00001236  | Saucer      | 00456    | 64     | 2006_04_01 |
| 00001287  | Carafe      | 00456    | 12     | 2006_04_01 |
| 00006931  | Carafe      | 00927    | 3      | 2006_04_01 |
| 00006935  | PotHolder   | 00927    | 88     | 2006_04_01 |
| 00006977  | Napkin      | 00927    | 108    | 2006_04_01 |
| 00006979  | Towel       | 00927    | 24     | 2006_04_01 |
| 00004488  | CofMaker    | 08732    | 5      | 2006_04_01 |
| 00004490  | CofGrinder  | 08732    | 9      | 2006_04_01 |
| 00004495  | EspMaker    | 08732    | 4      | 2006_04_01 |
| 00006914  | Cookbook    | 00927    | 12     | 2006_04_01 |

以下描述了表中的每个列`MERCH_INVENTORY`：

- `ITEM_ID`：存储标识项目的编号。
- `ITEM_NAME`：存储项目的名称。
- `SUP_ID`：存储标识供应商的编号。
- `QUAN`：存储一个数字，表示该项目的可用数量。
- `DATE`：存储时间戳值，指示上次更新行的时间。

## COFFEE_HOUSES表

该表`COFFEE_HOUSES`存储咖啡馆的位置：

| `STORE_ID` | `CITY`     | `COFFEE` | `MERCH` | `TOTAL` |
| ---------- | ---------- | -------- | ------- | ------- |
| 10023      | Mendocino  | 3450     | 2005    | 5455    |
| 33002      | Seattle    | 4699     | 3109    | 7808    |
| 10040      | SF         | 5386     | 2841    | 8227    |
| 32001      | Portland   | 3147     | 3579    | 6726    |
| 10042      | SF         | 2863     | 1874    | 4710    |
| 10024      | Sacramento | 1987     | 2341    | 4328    |
| 10039      | Carmel     | 2691     | 1121    | 3812    |
| 10041      | LA         | 1533     | 1007    | 2540    |
| 33005      | Olympia    | 2733     | 1550    | 4283    |
| 33010      | Seattle    | 3210     | 2177    | 5387    |
| 10035      | SF         | 1922     | 1056    | 2978    |
| 10037      | LA         | 2143     | 1876    | 4019    |
| 10034      | San_Jose   | 1234     | 1032    | 2266    |
| 32004      | Eugene     | 1356     | 1112    | 2468    |

以下描述了表中的每个列`COFFEE_HOUSES`：

- `STORE_ID`：存储一个识别咖啡馆的号码。除其他外，它表示咖啡馆所处的状态。例如，以10开头的值表示该州是加利福尼亚州。`STORE_ID`以32开头的值表示俄勒冈州，而以33开头的值表示华盛顿州。
- `CITY`：存储咖啡馆所在城市的名称。
- `COFFEE`：存储一个表示销售咖啡量的数字。
- `MERCH`：存储一个表示销售商品数量的数字。
- `TOTAL`：存储一个数字，表示销售的咖啡和商品的总量。

## DATA_REPOSITORY表

表DATA_REPOSITORY存储引用The Coffee Break感兴趣的文档和其他数据的URL。该脚本`populate_tables.sql`不会向此表添加任何数据。以下描述了此表中的每个列：

- `DOCUMENT_NAME`：存储标识URL的字符串。
- `URL`：存储URL。

## 创建表

您可以使用Apache Ant或JDBC API创建表。

### 使用Apache Ant创建表

要创建与教程示例代码一起使用的表，请在目录中运行以下命令`*<JDBC tutorial directory>*`：

```bash
ant setup
```

此命令运行多个Ant目标，包括以下内容`build-tables`（从`build.xml`文件中）：

```xml
<target name="build-tables"
  description="Create database tables">
  <sql
    driver="${DB.DRIVER}"
    url="${DB.URL}"
    userid="${DB.USER}"
    password="${DB.PASSWORD}"
    classpathref="CLASSPATH"
    delimiter="${DB.DELIMITER}"
    autocommit="false" onerror="abort">
    <transaction src=
  "./sql/${DB.VENDOR}/create-tables.sql"/>
  </sql>
</target>
```

该示例指定以下`sql`Ant任务参数的值：

| 参数           | 描述                                                         |
| -------------- | ------------------------------------------------------------ |
| `driver`       | JDBC驱动程序的完全限定类名。此示例`org.apache.derby.jdbc.EmbeddedDriver`用于Java DB和`com.mysql.jdbc.Driver`MySQL Connector / J. |
| `url`          | DBMS JDBC驱动程序用于连接数据库的数据库连接URL。             |
| `userid`       | DBMS中有效用户的名称。                                       |
| `password`     | 在中指定的用户的密码 `userid`                                |
| `classpathref` | 包含在其中指定的类的JAR文件的完整路径名 `driver`             |
| `delimiter`    | 用于分隔SQL语句的字符串或字符。此示例使用分号（`;`）。       |
| `autocommit`   | 布尔值; 如果设置为`false`，则所有SQL语句都作为一个事务执行。 |
| `onerror`      | 语句失败时要执行的操作; 可能的值是`continue`，`stop`和`abort`。该值`abort`指定如果发生错误，则中止事务。 |

该示例将这些参数的值存储在单独的文件中。构建文件`build.xml`使用以下`import`任务检索这些值：

```xml
<import file="${ANTPROPERTIES}"/>
```

该`transaction`元素指定包含要执行的SQL语句的文件。该文件`create-tables.sql`包含用于创建此页面上描述的所有表的SQL语句。例如，以下摘录自此文件创建表`SUPPLIERS`和`COFFEES`：

```sql
create table SUPPLIERS
    (SUP_ID integer NOT NULL,
    SUP_NAME varchar(40) NOT NULL,
    STREET varchar(40) NOT NULL,
    CITY varchar(20) NOT NULL,
    STATE char(2) NOT NULL,
    ZIP char(5),
    PRIMARY KEY (SUP_ID));

create table COFFEES
    (COF_NAME varchar(32) NOT NULL,
    SUP_ID int NOT NULL,
    PRICE numeric(10,2) NOT NULL,
    SALES integer NOT NULL,
    TOTAL integer NOT NULL,
    PRIMARY KEY (COF_NAME),
    FOREIGN KEY (SUP_ID)
        REFERENCES SUPPLIERS (SUP_ID));
```

**注意**：该文件`build.xml`包含另一个名为`drop-tables`删除教程使用的表的目标。该`setup`目标将运行`drop-tables`在运行前`build-tables`的目标。

### 使用JDBC API创建表

以下方法`SuppliersTable.createTable`创建`SUPPLIERS`表：

```java
public void createTable() throws SQLException {
    String createString =
        "create table " + dbName +
        ".SUPPLIERS " +
        "(SUP_ID integer NOT NULL, " +
        "SUP_NAME varchar(40) NOT NULL, " +
        "STREET varchar(40) NOT NULL, " +
        "CITY varchar(20) NOT NULL, " +
        "STATE char(2) NOT NULL, " +
        "ZIP char(5), " +
        "PRIMARY KEY (SUP_ID))";

    Statement stmt = null;
    try {
        stmt = con.createStatement();
        stmt.executeUpdate(createString);
    } catch (SQLException e) {
        JDBCTutorialUtilities.printSQLException(e);
    } finally {
        if (stmt != null) { stmt.close(); }
    }
}
```

以下方法`CoffeesTable.createTable`创建`COFFEES`表：

```java
public void createTable() throws SQLException {
    String createString =
        "create table " + dbName +
        ".COFFEES " +
        "(COF_NAME varchar(32) NOT NULL, " +
        "SUP_ID int NOT NULL, " +
        "PRICE float NOT NULL, " +
        "SALES integer NOT NULL, " +
        "TOTAL integer NOT NULL, " +
        "PRIMARY KEY (COF_NAME), " +
        "FOREIGN KEY (SUP_ID) REFERENCES " +
        dbName + ".SUPPLIERS (SUP_ID))";

    Statement stmt = null;
    try {
        stmt = con.createStatement();
        stmt.executeUpdate(createString);
    } catch (SQLException e) {
        JDBCTutorialUtilities.printSQLException(e);
    } finally {
        if (stmt != null) { stmt.close(); }
    }
}
```

在这两种方法中，`con`都是一个`Connection`对象，并且`dbName`是您在其中创建表的数据库的名称。

要执行SQL查询（例如由其指定的查询）`String` `createString`，请使用`Statement`对象。要创建`Statement`对象，请`Connection.createStatement`从现有`Connection`对象调用该方法。要执行SQL查询，请调用该方法`Statement.executeUpdate`。

`Statement`关闭创建它们的连接时，将关闭所有对象。但是，`Statement`一旦完成对象，就明确关闭对象是一种很好的编码实践。这允许立即释放语句正在使用的任何外部资源。通过调用方法关闭语句`Statement.close`。将此语句放在a中`finally`以确保即使正常程序流因为抛出异常（例如`SQLException`）而中断，它也会关闭。

**注意**：您必须`SUPPLIERS`在`COFFEES`因为`COFFEES`包含`SUP_ID`引用的外键之前创建表`SUPPLIERS`。

## 填充表格

同样，您可以使用Apache Ant或JDBC API将数据插入表中。

### 使用Apache Ant填充表

除了创建本教程使用的表之外，该命令`ant setup`还会填充这些表。此命令运行Ant目标`populate-tables`，该目标运行SQL脚本`populate-tables.sql`。

以下是摘录`populate-tables.sql`用于填充表`SUPPLIERS`和`COFFEES`：

```sql
insert into SUPPLIERS values(
    49, 'Superior Coffee', '1 Party Place',
    'Mendocino', 'CA', '95460');
insert into SUPPLIERS values(
    101, 'Acme, Inc.', '99 Market Street',
    'Groundsville', 'CA', '95199');
insert into SUPPLIERS values(
    150, 'The High Ground',
    '100 Coffee Lane', 'Meadows', 'CA', '93966');
insert into COFFEES values(
    'Colombian', 00101, 7.99, 0, 0);
insert into COFFEES values(
    'French_Roast', 00049, 8.99, 0, 0);
insert into COFFEES values(
    'Espresso', 00150, 9.99, 0, 0);
insert into COFFEES values(
    'Colombian_Decaf', 00101, 8.99, 0, 0);
insert into COFFEES values(
    'French_Roast_Decaf', 00049, 9.99, 0, 0);
```

### 使用JDBC API填充表

以下方法`SuppliersTable.populateTable`将数据插入表中：

```java
public void populateTable() throws SQLException {

    Statement stmt = null;
    try {
        stmt = con.createStatement();
        stmt.executeUpdate(
            "insert into " + dbName +
            ".SUPPLIERS " +
            "values(49, 'Superior Coffee', " +
            "'1 Party Place', " +
            "'Mendocino', 'CA', '95460')");

        stmt.executeUpdate(
            "insert into " + dbName +
            ".SUPPLIERS " +
            "values(101, 'Acme, Inc.', " +
            "'99 Market Street', " +
            "'Groundsville', 'CA', '95199')");

        stmt.executeUpdate(
            "insert into " + dbName +
            ".SUPPLIERS " +
            "values(150, " +
            "'The High Ground', " +
            "'100 Coffee Lane', " +
            "'Meadows', 'CA', '93966')");
    } catch (SQLException e) {
        JDBCTutorialUtilities.printSQLException(e);
    } finally {
        if (stmt != null) { stmt.close(); }
    }
}
```

以下方法`CoffeesTable.populateTable`将数据插入表中：

```java
public void populateTable() throws SQLException {

    Statement stmt = null;
    try {
        stmt = con.createStatement();
        stmt.executeUpdate(
            "insert into " + dbName +
            ".COFFEES " +
            "values('Colombian', 00101, " +
            "7.99, 0, 0)");

        stmt.executeUpdate(
            "insert into " + dbName +
            ".COFFEES " +
            "values('French_Roast', " +
            "00049, 8.99, 0, 0)");

        stmt.executeUpdate(
            "insert into " + dbName +
            ".COFFEES " +
            "values('Espresso', 00150, 9.99, 0, 0)");

        stmt.executeUpdate(
            "insert into " + dbName +
            ".COFFEES " +
            "values('Colombian_Decaf', " +
            "00101, 8.99, 0, 0)");

        stmt.executeUpdate(
            "insert into " + dbName +
            ".COFFEES " +
            "values('French_Roast_Decaf', " +
            "00049, 9.99, 0, 0)");
    } catch (SQLException e) {
        JDBCTutorialUtilities.printSQLException(e);
    } finally {
        if (stmt != null) {
          stmt.close();
        }
    }
}
```

