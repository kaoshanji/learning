# JDBC简介

JDBC API是一种Java API，可以访问任何类型的表格数据，尤其是存储在[关系数据库中的数据。](https://docs.oracle.com/javase/tutorial/jdbc/overview/index.html#relational)

JDBC帮助您编写管理这三种编程活动的Java应用程序：

1. 连接到数据源，如数据库
2. 将查询和更新语句发送到数据库
3. 检索并处理从数据库接收的结果以回答您的查询

以下简单的代码片段给出了这三个步骤的简单示例：

```java
public void connectToAndQueryDatabase(String username, String password) {

    Connection con = DriverManager.getConnection(
                         "jdbc:myDriver:myDatabase",
                         username,
                         password);

    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT a, b, c FROM Table1");

    while (rs.next()) {
        int x = rs.getInt("a");
        String s = rs.getString("b");
        float f = rs.getFloat("c");
    }
}
```

这个短代码片段实例化一个`DriverManager`对象以连接到数据库驱动程序并登录到数据库，实例化一个`Statement`带有SQL语言查询的对象到数据库; 实例化一个`ResultSet`检索查询结果的对象，并执行一个简单的`while`循环，该循环检索并显示这些结果。就这么简单。

## JDBC产品组件

JDBC包括四个组件：

1. **JDBC API**  - JDBC™API提供对Java™编程语言的关系数据的编程访问。使用JDBC API，应用程序可以执行SQL语句，检索结果并将更改传递回底层数据源。JDBC API还可以在分布式异构环境中与多个数据源进行交互。

   JDBC API是Java平台的一部分，其中包括*Java™标准版*（Java™SE）和*Java™企业版*（Java™EE）。在JDBC 4.0 API分为两个包：`java.sql`和`javax.sql.`两种封装都包含在Java SE和Java EE平台。

2. **JDBC驱动程序管理器**  - JDBC `DriverManager`类定义可以将Java应用程序连接到JDBC驱动程序的对象。`DriverManager`传统上一直是JDBC架构的支柱。它非常小而且简单。

   该标准扩展包`javax.naming`，并`javax.sql`让您使用`DataSource`与已注册对象*的Java命名和目录接口* ™（JNDI）命名服务，以建立与数据源的连接。您可以使用任一连接机制，但`DataSource`建议尽可能使用对象。

3. **JDBC测试套件**  - JDBC驱动程序测试套件可帮助您确定JDBC驱动程序将运行您的程序。这些测试并不全面或详尽，但它们确实运用了JDBC API中的许多重要功能。

4. **JDBC-ODBC Bridge**  - Java Software桥通过ODBC驱动程序提供JDBC访问。请注意，您需要将ODBC二进制代码加载到使用此驱动程序的每台客户端计算机上。因此，ODBC驱动程序最适用于客户端安装不是主要问题的公司网络，或者适用于以三层体系结构用Java编写的应用程序服务器代码。

此Trail使用这四个JDBC组件中的前两个连接到数据库，然后构建一个使用SQL命令与测试关系数据库通信的Java程序。最后两个组件用于特定环境以测试Web应用程序，或与支持ODBC的DBMS进行通信。

## JDBC架构



### 两层和三层处理模型

JDBC API支持用于数据库访问的两层和三层处理模型。



图1：用于数据访问的双层体系结构。





![DBMS专有协议提供客户端计算机和数据库服务器之间的双向通信](images/intro.anc2.gif)

在三层模型中，命令被发送到服务的“中间层”，然后将命令发送到数据源。数据源处理命令并将结果发送回中间层，然后中间层将它们发送给用户。MIS主管发现三层模型非常具有吸引力，因为中间层可以保持对访问的控制以及可以对公司数据进行的更新。另一个优点是它简化了应用程序的部署。最后，在许多情况下，三层架构可以提供性能优势。



图2：数据访问的三层体系结构。





![DBMS专有协议提供数据库服务器和服务器机器之间的双向通信。 HTTP，RMI，CORBA或其他调用提供服务器计算机和客户端计算机之间的双向通信](images/intro.anc1.gif)

随着企业越来越多地使用Java编程语言编写服务器代码，JDBC API在三层体系结构的中间层中越来越多地被使用。使JDBC成为服务器技术的一些功能是它支持连接池，分布式事务和断开连接的行集。JDBC API也允许从Java中间层访问数据源。



## 关系数据库概述

数据库是一种以可以从中检索信息的方式存储信息的方法。简单来说，关系数据库是在包含行和列的表中显示信息的数据库。表在某种意义上被称为关系，它是相同类型（行）的对象的集合。表中的数据可以根据公共密钥或概念进行关联，并且从表中检索相关数据的能力是术语关系数据库的基础。数据库管理系统（DBMS）处理数据的存储，维护和检索方式。对于关系数据库，关系数据库管理系统（RDBMS）执行这些任务。本书中使用的DBMS是包含RDBMS的通用术语。



### 重要规则

关系表遵循某些完整性规则，以确保它们包含的数据保持准确并始终可访问。首先，关系表中的行应该都是不同的。如果存在重复行，则可能存在解决两个可能选择中哪一个是正确选择的问题。对于大多数DBMS，用户可以指定不允许重复行，如果这样做，DBMS将阻止添加任何复制现有行的行。

传统关系模型的第二个完整性规则是列值不能是重复的组或数组。数据完整性的第三个方面涉及空值的概念。数据库通过使用空值来指示缺少值，从而处理数据可能不可用的情况。它不等于空白或零。空白被认为等于另一个空白，零等于另一个零，但两个空值不被认为是相等的。

当表中的每一行不同时，可以使用一列或多列来标识特定行。此唯一列或列组称为主键。作为主键一部分的任何列都不能为空; 如果是，则包含它的主键将不再是完整的标识符。此规则称为实体完整性。

该`Employees`表说明了这些关系数据库概念中的一些。它有五列六行，每行代表不同的员工。



`Employees` 表

| `Employee_Number` | `First_name` | `Last_Name` | `Date_of_Birth` | `Car_Number` |
| ----------------- | ------------ | ----------- | --------------- | ------------ |
| 10001             | Axel         | Washington  | 28-Aug-43       | 5            |
| 10083             | Arvid        | Sharma      | 24-Nov-54       | null         |
| 10120             | Jonas        | Ginsberg    | 01-Jan-69       | null         |
| 10005             | Florence     | Wojokowski  | 04-Jul-71       | 12           |
| 10099             | Sean         | Washington  | 21-Sep-66       | null         |
| 10035             | Elizabeth    | Yamaguchi   | 24-Dec-59       | null         |

该表的主键通常是员工编号，因为每个人都保证不同。（对于进行比较，数字也比字符串更有效。）也可以使用`First_Name`，`Last_Name`因为两者的组合也只能识别我们的示例数据库中的一行。单独使用姓氏是行不通的，因为有两名员工的姓氏为“华盛顿”。在这种特殊情况下，名字都是不同的，因此可以想象使用该列作为主键，但最好避免使用可能发生重复的列。如果伊丽莎白山口在这家公司找到工作，主要关键是`First_Name`，RDBMS不允许添加她的名字（如果已经指定不允许重复）。因为表中已经有一个Elizabeth，所以添加第二个会使主键无法用作识别一行的方法。请注意，尽管使用`First_Name`和`Last_Name`是此示例的唯一复合键，但它在较大的数据库中可能不是唯一的。另请注意，该`Employee`表假定每位员工只能有一辆车。

### `SELECT` 声明

SQL是一种旨在与关系数据库一起使用的语言。有一组基本的SQL命令被认为是标准的，并且被所有RDBMS使用。例如，所有RDBMS都使用该`SELECT`语句。

一个`SELECT`说法，也被称为查询，用于获取从表中的信息。它指定一个或多个列标题，一个或多个要从中选择的表，以及一些选择标准。RDBMS返回满足所述要求的列条目的行。一`SELECT`，如下面的语句将取谁拥有公司员工汽车的第一个和最后一个名字：

```sql
SELECT First_Name, Last_Name
FROM Employees
WHERE Car_Number IS NOT NULL
```

下面是结果集（满足列中不为null的要求的行集`Car_Number`）。为满足要求的每一行打印名字和姓氏，因为`SELECT`语句（第一行）指定了列`First_Name`和`Last_Name`。在`FROM`条款（第二行）给出了从该列将被选择的表。

| 名字     | 姓         |
| -------- | ---------- |
| 阿克塞尔 | 华盛顿     |
| 佛罗伦萨 | Wojokowski |

以下代码生成一个包含整个表的结果集，因为它要求Employees表中没有限制的所有列（无`WHERE`子句）。请注意，这`SELECT *`意味着“选择所有列”。

```sql
SELECT *
FROM Employees
```

### `WHERE` 条款

声明中的`WHERE`子句`SELECT`提供了选择值的标准。例如，在以下代码片段中，仅当值出现在Last_Name列以字符串“Washington”开头的行中时，才会选择值。

```sql
SELECT First_Name, Last_Name
FROM Employees
WHERE Last_Name LIKE 'Washington%'
```

该关键字`LIKE`用于比较字符串，它提供了可以使用包含通配符的模式的功能。例如，在上面的代码片段中，`%`'Washington'末尾有一个百分号（），表示包含字符串'Washington'和零个或多个附加字符的任何值都将满足此选择条件。所以'华盛顿'或'华盛顿'将是比赛，但'洗'不会。`LIKE`子句中使用的另一个通配符是underbar（`_`），代表任何一个字符。例如，

```sql
WHERE Last_Name LIKE 'Ba_man'
```

会匹配'蝙蝠侠'，'巴曼'，'巴德曼'，'巴尔曼'，'巴格曼'，'巴曼'等等。

下面的代码片段有一个`WHERE`使用等号（=）来比较数字的子句。它选择分配了汽车12的员工的名字和姓氏。

```sql
SELECT First_Name, Last_Name
FROM Employees
WHERE Car_Number = 12
```

`WHERE`在多个条件下，条款可以相当复杂，在某些DBMS中，嵌套条件也是如此。本概述不包含复杂的`WHERE`子句，但以下代码片段`WHERE`包含两个条件的子句; 此查询选择员工编号小于10100且没有公司汽车的员工的名字和姓氏。

```sql
SELECT First_Name, Last_Name
FROM Employees
WHERE Employee_Number > 10005
```

`WHERE`在多个条件下，条款可以相当复杂，在某些DBMS中，嵌套条件也是如此。本概述不包含复杂的`WHERE`子句，但以下代码片段`WHERE`包含两个条件的子句; 此查询选择员工编号小于10100且没有公司汽车的员工的名字和姓氏。

```sql
SELECT First_Name, Last_Name
FROM Employees
WHERE Employee_Number < 10100 and Car_Number IS NULL
```

一种特殊类型的`WHERE`子句涉及连接，这将在下一节中介绍。



### 加盟

关系数据库的一个显着特征是可以从所谓的连接中的多个表中获取数据。假设在检索拥有公司汽车的员工的姓名后，人们想知道谁拥有哪辆汽车，包括汽车的品牌，型号和年份。此信息存储在另一个表中`Cars`：



`Cars` 表

| `Car_Number` | `Make` | `Model` | `Year` |
| ------------ | ------ | ------- | ------ |
| 五           | 本田   | 思域DX  | 1996年 |
| 12           | 丰田   | 花冠    | 1999年 |

两个表中必须有一列才能将它们相互关联。此列必须是一个表中的主键，在另一个表中称为外键。在这种情况下，出现在两个表中的列是`Car_Number`，表是`Cars`Employees 表中的主键和外键。如果1996年的Honda Civic被破坏并从`Cars`表中删除，则`Car_Number`还必须从Employees表中删除5，以维持所谓的参照完整性。否则，表中的外键列（`Car_Number`）`Employees`将包含一个未引用任何内容的条目`Cars`。外键必须为null或等于其引用的表的现有主键值。这与主键不同，主键可能不为空。`Car_Number`表中的列中有多个空值，`Employees`因为员工可能没有公司汽车。

以下代码询问拥有公司汽车的员工的名字和姓氏，以及这些汽车的品牌，型号和年份。请注意，该`FROM`子句列出了Employees和Cars，因为请求的数据包含在两个表中。在列名称前使用表名和点（。）表示哪个表包含该列。

```sql
SELECT Employees.First_Name, Employees.Last_Name,
    Cars.Make, Cars.Model, Cars.Year
FROM Employees, Cars
WHERE Employees.Car_Number = Cars.Car_Number
```

这将返回一个类似于以下内容的结果集：

| `FIRST_NAME` | `LAST_NAME` | `MAKE` | `MODEL` | `YEAR` |
| ------------ | ----------- | ------ | ------- | ------ |
| 阿克塞尔     | 华盛顿      | 本田   | 思域DX  | 1996年 |
| 佛罗伦萨     | Wojokowski  | 丰田   | 花冠    | 1999年 |



### 常用SQL命令

SQL命令分为几类，两个主要是数据操作语言（DML）命令和数据定义语言（DDL）命令。DML命令处理数据，检索或修改数据以使其保持最新。DDL命令创建或更改表和其他数据库对象，例如视图和索引。

以下列出了更常见的DML命令：

- `SELECT — `用于查询和显示数据库中的数据。该`SELECT`语句指定要包含在结果集中的列。应用程序中使用的绝大多数SQL命令都是`SELECT`语句。
- `INSERT — `向表中添加新行。`INSERT`用于填充新创建的表或向已存在的表添加新行（或行）。
- `DELETE — ` 从表中删除指定的行或行集
- `UPDATE — ` 更改表中的列或列组中的现有值

更常见的DDL命令如下：

- `CREATE TABLE — `使用用户提供的列名创建一个表。用户还需要为每列中的数据指定类型。数据类型因RDBMS而异，因此用户可能需要使用元数据来建立特定数据库使用的数据类型。`CREATE TABLE`通常比数据操作命令使用频率低，因为表只创建一次，而添加或删除行或更改单个值通常更频繁地发生。
- `DROP TABLE — `删除所有行并从数据库中删除表定义。需要JDBC API实现来支持`DROP TABLE`SQL92，Transitional Level指定的命令。但是，对选项`CASCADE`和`RESTRICT`选项的支持`DROP TABLE`是可选的。此外，`DROP TABLE`当定义了引用要删除的表的视图或完整性约束时，行为是实现定义的。
- `ALTER TABLE — `从表中添加或删除列。它还添加或删除表约束并更改列属性



### 结果集和游标

满足查询条件的行称为结果集。结果集中返回的行数可以是零，一个或多个。用户可以一次访问结果集中的数据，并且游标提供了执行此操作的方法。可以将游标视为指向包含结果集行的文件的指针，并且该指针能够跟踪当前正在访问哪一行。游标允许用户从上到下处理结果集的每一行，因此可以用于迭代处理。大多数DBMS在生成结果集时自动创建游标。

早期的JDBC API版本为结果集的游标添加了新功能，允许它向前和向后移动，并允许它移动到指定行或位置相对于另一行的行。



### 事务

当一个用户访问数据库中的数据时，另一个用户可能同时访问相同的数据。例如，如果第一个用户正在同时更新表中的某些列，而第二个用户正在从同一个表中选择列，则第二个用户可能获得部分旧数据和部分更新数据。因此，DBMS使用事务来维护数据处于一致状态（数据一致性），同时允许多个用户同时访问数据库（数据并发）。

事务是一组构成逻辑工作单元的一个或多个SQL语句。事务以提交或回滚结束，具体取决于数据一致性或数据并发性是否存在任何问题。commit语句使由事务中的SQL语句产生的更改永久化，并且rollback语句撤消由事务中的SQL语句产生的所有更改。

锁是一种机制，禁止两个事务同时操作相同的数据。例如，如果该表上存在未提交的事务，则表锁可防止删除表。在某些DBMS中，表锁还会锁定表中的所有行。行锁可防止两个事务修改同一行，或者它阻止一个事务在另一个事务仍在修改它时选择一行。



### 存储过程

存储过程是一组可以按名称调用的SQL语句。换句话说，它是可执行代码，一个迷你程序，执行一个特定的任务，可以调用一个可以调用函数或方法的方式。传统上，存储过程是用DBMS特定的编程语言编写的。最新一代的数据库产品允许使用Java编程语言和JDBC API编写存储过程。用Java编程语言编写的存储过程在DBMS之间是可移植的字节码。一旦编写了存储过程，就可以使用它并重用它，因为支持存储过程的DBMS将顾名思义将其存储在数据库中。

以下代码是如何使用Java编程语言创建非常简单的存储过程的示例。请注意，存储过程只是一个包含普通JDBC代码的静态Java方法。它接受两个输入参数并使用它们来更改员工的车号。

如果您此时不理解该示例，请不要担心。下面的代码示例仅用于说明存储过程的外观。您将在下面的教程中学习如何编写此示例中的代码。

```java
import java.sql.*;

public class UpdateCar {

    public static void UpdateCarNum(int carNo, int empNo)
        throws SQLException {

        Connection con = null;
        PreparedStatement pstmt = null;   
      
        try {
            con = DriverManager.getConnection(
                      "jdbc:default:connection");

            pstmt = con.prepareStatement(
                        "UPDATE EMPLOYEES " +
                        "SET CAR_NUMBER = ? " +
                        "WHERE EMPLOYEE_NUMBER = ?");

            pstmt.setInt(1, carNo);
            pstmt.setInt(2, empNo);
            pstmt.executeUpdate();
        }
        finally {
            if (pstmt != null) pstmt.close();
        }
    }
}
```

### 元数据

数据库存储用户数据，它们还存储有关数据库本身的信息。大多数DBMS都有一组系统表，它们列出数据库中的表，每个表中的列名，主键，外键，存储过程等。每个DBMS都有自己的函数来获取有关表格布局和数据库功能的信息。JDBC提供了一个接口`DatabaseMetaData`，驱动程序编写者必须实现该接口，以便其方法返回有关为其编写驱动程序的驱动程序和/或DBMS的信息。例如，大量方法返回驱动程序是否支持特定功能。此界面为用户和工具提供了获取元数据的标准方法。

通常，编写工具和驱动程序的开发人员最有可能关注元数据。