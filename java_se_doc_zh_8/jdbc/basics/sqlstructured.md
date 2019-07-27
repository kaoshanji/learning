# 使用结构化对象

**注意**：MySQL和Java DB当前不支持用户定义的类型。因此，没有JDBC教程示例可用于演示本节中描述的功能。

涵盖以下主题：

- [结构化类型概述](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlstructured.html#overview_structured)
- [在结构化类型中使用DISTINCT类型](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlstructured.html#using_distinct_in_structured)
- [使用结构化类型的引用](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlstructured.html#references_structured)
- [用于创建SQL REF对象的示例代码](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlstructured.html#code_ref)
- [使用用户定义的类型作为列值](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlstructured.html#udt_column_values)
- [将用户定义的类型插入表中](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlstructured.html#insert_udt)

## 结构化类型概述

SQL结构化类型和`DISTINCT`类型是用户可以在SQL中定义的两种数据类型。它们通常被称为UDT（用户定义的类型），您可以使用SQL `CREATE` `TYPE`语句创建它们。

回到咖啡休息时间的例子，假设所有者已经超出了所有人的预期，并且一直在扩展新的分支机构。所有者已决定向`STORES`数据库添加一个表，其中包含有关每个企业的信息。`STORES`将有四列：

- `STORE_NO` 对于每个商店的识别号码
- `LOCATION` 因为它的地址
- `COF_TYPES` 为它出售的咖啡
- `MGR` 为商店经理的名字

雇主使得柱`LOCATION`是一个SQL结构类型，列`COF_TYPES`一个SQL `ARRAY`，列`MGR`一个`REF(MANAGER)`，以`MANAGER`作为一个SQL结构类型。

所有者必须首先为地址和经理定义新的结构化类型。SQL结构类型类似于Java编程语言中的结构化类型，因为它具有可称为任何数据类型的成员，称为*属性*。所有者编写以下SQL语句来创建新数据类型`ADDRESS`：

```sql
CREATE TYPE ADDRESS
(
    NUM INTEGER,
    STREET VARCHAR(40),
    CITY VARCHAR(40),
    STATE CHAR(2),
    ZIP CHAR(5)
);
```

在此语句中，新类型`ADDRESS`有五个属性，类似于Java类中的字段。的属性`NUM`是一个`INTEGER`，属性`STREET`是一个`VARCHAR(40)`，属性`CITY`是一个`VARCHAR(40)`，属性`STATE`是一个`CHAR(2)`，并且所述属性`ZIP`是一个`CHAR(5)`。

以下摘录（其中`con`是有效`Connection`对象）将定义发送`ADDRESS`到数据库：

```java
String createAddress =
    "CREATE TYPE ADDRESS " +
    "(NUM INTEGER, STREET VARCHAR(40), " +
    "CITY VARCHAR(40), STATE CHAR(2), ZIP CHAR(5))";
Statement stmt = con.createStatement();
stmt.executeUpdate(createAddress);
```

现在，`ADDRESS`结构化类型作为数据类型在数据库中注册，并且所有者可以将其用作表列的数据类型或结构化类型的属性。

## 在结构化类型中使用DISTINCT类型

The Coffee Break的所有者计划在新结构类型中包含的属性之一`MANAGER`是经理的电话号码。因为所有者将始终将电话号码列为10位数字（以确保它包含区号）并且永远不会将其作为数字进行操作，所以所有者决定定义一个名为`PHONE_NO`10个字符的新类型。此数据类型的SQL定义（可以被视为仅具有一个属性的结构化类型）如下所示：

```sql
CREATE TYPE PHONE_NO AS CHAR(10);
```

或者，如前所述，对于某些驱动程序，定义可能如下所示：

```sql
CREATE DISTINCT TYPE PHONE_NO AS CHAR(10);
```

甲`DISTINCT`类型总是基于另一种数据类型，它必须是一个预定义的类型。换句话说，`DISTINCT`类型不能基于用户定义的类型（UDT）。要检索或设置作为`DISTINCT`类型的值，请对基础类型（它所基于的类型）使用适当的方法。例如，要检索`PHONE_NO`基于`CHAR`类型的实例，您将使用该方法，`getString`因为这是检索a的方法`CHAR`。

假设type的值`PHONE_NO`在`ResultSet`对象的当前行的第四列中`*rs*`，则以下代码行检索它：

```java
String phoneNumber = rs.getString(4);
```

类似地，以下代码行设置一个输入参数，该参数具有`PHONE_NO`发送到数据库的预准备语句的类型：

```java
pstmt.setString(1, phoneNumber);
```

添加到前面的代码片段，`PHONE_NO`将使用以下代码行将定义发送到数据库：

```java
stmt.executeUpdate(
    "CREATE TYPE PHONE_NO AS CHAR(10)");
```

在`PHONE_NO`使用数据库注册类型后，所有者可以将其用作表中的列类型或结构化类型中属性的数据类型。`MANAGER`以下SQL语句中的定义`PHONE_NO`用作属性的数据类型`PHONE`：

```sql
CREATE TYPE MANAGER
(
    MGR_ID INTEGER,
    LAST_NAME VARCHAR(40),
    FIRST_NAME VARCHAR(40),
    PHONE PHONE_NO
);
```

重用`*stmt*`，如前所述，以下代码片段将结构化类型的定义发送`MANAGER`到数据库：

```java
 String createManager =
    "CREATE TYPE MANAGER " +
    "(MGR_ID INTEGER, LAST_NAME " +
    "VARCHAR(40), " +
    "FIRST_NAME VARCHAR(40), " +
    "PHONE PHONE_NO)";
  stmt.executeUpdate(createManager);
```

## 使用结构化类型的引用

The Coffee Break的所有者创建了三种新数据类型，用作数据库中的列类型或属性类型：结构化类型`LOCATION`和`MANAGER`，以及`DISTINCT`类型`PHONE_NO`。企业家已将新类型中`PHONE_NO`的属性用作`PHONE`类型`MANAGER`，并将其`ADDRESS`用作`LOCATION`表中列的数据类型`STORES`。该`MANAGER`类型可以用作列的类型`MGR`，但企业家更喜欢使用该类型，`REF(MANAGER)`因为企业家通常有一个人管理两个或三个商店。使用`REF(MANAGER)`列类型可避免`MANAGER`在一个人管理多个商店时重复所有数据。

使用`MANAGER`已创建的结构化类型，所有者现在可以创建一个包含`MANAGER`可以引用的实例的表。对一个实例的引用`MANAGER`具有该类型`REF(MANAGER)`。SQL `REF`只不过是指向结构化类型的逻辑指针，因此一个实例`REF(MANAGER)`充当指向实例的逻辑指针`MANAGER`。

由于SQL `REF`值需要与它引用的结构化类型的实例永久关联，因此它与其关联的实例一起存储在特殊表中。程序员不`REF`直接创建类型，而是创建将存储可引用的特定结构化类型的实例的表。每个要引用的结构化类型都有自己的表。将结构化类型的实例插入表中时，数据库会自动创建`REF`实例。例如，要包含`MANAGER`可以引用的实例，所有者使用SQL创建以下特殊表：

```sql
 CREATE TABLE MANAGERS OF MANAGER
  (OID REF(MANAGER)
  VALUES ARE SYSTEM GENERATED);
```

此语句创建一个包含特殊列的表，该列`OID`存储类型的值`REF(MANAGER)`。每次将实例`MANAGER`插入表中时，数据库都将生成一个实例`REF(MANAGER)`并将其存储在列中`OID`。隐含地，附加列也存储`MANAGER`已插入表中的每个属性。例如，以下代码片段显示了企业家如何创建三个`MANAGER`结构化类型实例来表示三个管理器：

```sql
 INSERT INTO MANAGERS (
    MGR_ID, LAST_NAME,
    FIRST_NAME, PHONE) VALUES
  (
    000001,
    'MONTOYA',
    'ALFREDO',
    '8317225600'
  );

  INSERT INTO MANAGERS (
    MGR_ID, LAST_NAME,
    FIRST_NAME, PHONE) VALUES
  (
    000002,
    'HASKINS',
    'MARGARET',
    '4084355600'
  );

  INSERT INTO MANAGERS (
    MGR_ID, LAST_NAME,
    FIRST_NAME, PHONE) VALUES
  (
    000003,
    'CHEN',
    'HELEN',
    '4153785600'
   );
```

该表现在`MANAGERS`将有三行，到目前为止每个管理器插入一行。该列`OID`将包含三个类型的唯一对象标识符，`REF(MANAGER)`每个实例对应一个。`MANAGER.`这些对象标识符由数据库自动生成，并将永久存储在表中`MANAGERS`。隐式地，附加列存储每个属性`MANAGER`。例如，在表中`MANAGERS`，一行包含`REF(MANAGER)`引用Alfredo Montoya的行，另一行包含`REF(MANAGER)`引用Margaret Haskins的行，第三行包含`REF(MANAGER)`引用Helen Chen的行。

要访问`REF(MANAGER)`实例，请从其表中选择它。例如，所有者检索了对Alfredo Montoya的引用，其ID号为000001，具有以下代码片段：

```java
String selectMgr =
    "SELECT OID FROM MANAGERS " +
    "WHERE MGR_ID = 000001";
  ResultSet rs = stmt.executeQuery(selectMgr);
  rs.next();
  Ref manager = rs.getRef("OID");
```

现在变量`*manager*`可以用作引用Alfredo Montoya的列值。

## 用于创建SQL REF对象的示例代码

以下代码示例创建表`MANAGERS`，`MANAGER`可以引用的结构化类型的实例表，并在表中插入三个实例`MANAGER`。该`OID`表中的列将存储实例`REF(MANAGER)`。执行此代码后，`MANAGERS`表中将为`MANAGER`插入的三个对象中的每一个都有一行，并且`OID`列中的值将是`REF(MANAGER)`标识该`MANAGER`行中存储的实例的类型。

```java
package com.oracle.tutorial.jdbc;

import java.sql.*;

public class CreateRef {

    public static void main(String args[]) {

        JDBCTutorialUtilities myJDBCTutorialUtilities;
        Connection myConnection = null;

        if (args[0] == null) {
            System.err.println("Properties file not specified " +
                               "at command line");
            return;
        } else {
            try {
                myJDBCTutorialUtilities = new JDBCTutorialUtilities(args[0]);
            } catch (Exception e) {
                System.err.println("Problem reading properties " +
                                   "file " + args[0]);
                e.printStackTrace();
                return;
            }
        }

        Connection con = null;
        Statement stmt = null;

        try {
            String createManagers =
                "CREATE TABLE " +
                "MANAGERS OF MANAGER " +
                "(OID REF(MANAGER) " +
                "VALUES ARE SYSTEM " +
                "GENERATED)";

            String insertManager1 =
                "INSERT INTO MANAGERS " +
                "(MGR_ID, LAST_NAME, " +
                "FIRST_NAME, PHONE) " +
                "VALUES " +
                "(000001, 'MONTOYA', " +
                "'ALFREDO', " +
                "'8317225600')";

            String insertManager2 =
                "INSERT INTO MANAGERS " +
                "(MGR_ID, LAST_NAME, " +
                "FIRST_NAME, PHONE) " +
                "VALUES " +
                "(000002, 'HASKINS', " +
                "'MARGARET', " +
                "'4084355600')";

            String insertManager3 =
                "INSERT INTO MANAGERS " +
                "(MGR_ID, LAST_NAME, " +
                "FIRST_NAME, PHONE) " +
                "VALUES " +
                "(000003, 'CHEN', 'HELEN', " +
                "'4153785600')";
  
            con = myJDBCTutorialUtilities.getConnection();
            con.setAutoCommit(false);

            stmt = con.createStatement();
            stmt.executeUpdate(createManagers);

            stmt.addBatch(insertManager1);
            stmt.addBatch(insertManager2);
            stmt.addBatch(insertManager3);
            int [] updateCounts = stmt.executeBatch();

            con.commit();

            System.out.println("Update count for:  ");
            for (int i = 0; i < updateCounts.length; i++) {
                System.out.print("    command " + (i + 1) + " = ");
                System.out.println(updateCounts[i]);
            }
        } catch(BatchUpdateException b) {
            System.err.println("-----BatchUpdateException-----");
            System.err.println("Message:  " + b.getMessage());
            System.err.println("SQLState:  " + b.getSQLState());
            System.err.println("Vendor:  " + b.getErrorCode());
            System.err.print("Update counts for " + "successful commands:  ");
            int [] rowsUpdated = b.getUpdateCounts();
            for (int i = 0; i < rowsUpdated.length; i++) {
                System.err.print(rowsUpdated[i] + "   ");
            }
            System.err.println("");
        } catch(SQLException ex) {
            System.err.println("------SQLException------");
            System.err.println("Error message:  " + ex.getMessage());
            System.err.println("SQLState:  " + ex.getSQLState());
            System.err.println("Vendor:  " + ex.getErrorCode());
        } finally {
            if (stmt != null) { stmt.close(); }
              JDBCTutorialUtilities.closeConnection(con);
        }
    }
}
```

## 使用用户定义的类型作为列值

我们的企业家现在拥有创建表所需的UDT `STORES`。结构化类型`ADDRESS`是列`LOCATION`的类型`REF(MANAGER)`，类型是列的类型`MGR`。

UDT `COF_TYPES`基于SQL数据类型`ARRAY`，是列的类型`COF_TYPES`。以下代码行将类型创建`COF_ARRAY`为`ARRAY`具有10个元素的值。基本类型`COF_ARRAY`是`VARCHAR(40)`。

```sql
CREATE TYPE COF_ARRAY AS ARRAY(10) OF VARCHAR(40);
```

定义了新的数据类型后，以下SQL语句将创建表`STORES`：

```sql
 CREATE TABLE STORES
  (
    STORE_NO INTEGER,
    LOCATION ADDRESS,
    COF_TYPES COF_ARRAY,
    MGR REF(MANAGER)
  );
```

## 将用户定义的类型插入表中

下面的代码段中插入一个行插入到`STORES`表中，对于各列提供值`STORE_NO`，`LOCATION`，`COF_TYPES`，和`MGR`，在该顺序：

```sql
 INSERT INTO STORES VALUES
  (
    100001,
    ADDRESS(888, 'Main_Street',
      'Rancho_Alegre',
      'CA', '94049'),
    COF_ARRAY('Colombian', 'French_Roast',
      'Espresso', 'Colombian_Decaf',
      'French_Roast_Decaf'),
    SELECT OID FROM MANAGERS
      WHERE MGR_ID = 000001
  );
```

以下内容遍历每列并插入其中的值。

```bash
 STORE_NO: 100001
```

此列的类型`INTEGER`，而数字`100001`是一个`INTEGER`类型，类似于表中之前进行的条目`COFFEES`和`SUPPLIERS`。

```bash
LOCATION: ADDRESS(888, 'Main_Street',
    'Rancho_Alegre', 'CA', '94049')
```

此列的类型是结构化类型`ADDRESS`，此值是实例的构造函数`ADDRESS`。当我们将发送的定义`ADDRESS`发送到数据库时，它所做的一件事就是为新类型创建一个构造函数。括号中的逗号分隔值是`ADDRESS`类型属性的初始化值，它们必须按照`ADDRESS`类型定义中列出属性的顺序出现。`888`是属性的值`NUM`，它是一个`INTEGER`值。`"Main_Street"`是两个属性类型的值`STREET`，并且`"Rancho_Alegre"`是值的值。该属性的值是，它的类型的`CITY``VARCHAR(40)``STATE``"CA"``CHAR(2)`和用于该属性的值`ZIP`是`"94049"`，它的类型的`CHAR(5)`。

```bash
 COF_TYPES: COF_ARRAY(
    'Colombian',
    'French_Roast',
    'Espresso',
    'Colombian_Decaf',
    'French_Roast_Decaf'),
```

该列`COF_TYPES`的类型`COF_ARRAY`具有基本类型`VARCHAR(40)`，括号之间的逗号分隔值`String`是作为数组元素的对象。所有者将类型定义`COF_ARRAY`为最多包含10个元素。这个数组有5个元素，因为企业家只提供了5个`String`对象。

```bash
 MGR: SELECT OID FROM MANAGERS
    WHERE MGR_ID = 000001
```

该列`MGR`是type `REF(MANAGER)`，这意味着此列中的值必须是对结构化类型的引用`MANAGER`。所有实例`MANAGER`都存储在表中`MANAGERS`。所有实例`REF(MANAGER)`也存储在此表的列中`OID`。此表行中描述的商店经理是阿尔弗雷多·蒙托亚，他的信息存储在该实例中`MANAGER`具有`100001`的属性`MGR_ID`。要获得`REF(MANAGER)`与相关的实例`MANAGER`为阿尔弗雷多·蒙托亚对象，选择列`OID`这正是该行中`MGR_ID`是`100001`在表中`MANAGERS`。将存储在表的`MGR`列中的值`STORES`（`REF(MANAGER)`value）是DBMS生成的唯一标识`MANAGER`结构化类型实例的值。

使用以下代码片段将前面的SQL语句发送到数据库：

```java
String insertMgr =
    "INSERT INTO STORES VALUES " +
    "(100001, " +
    "ADDRESS(888, 'Main_Street', " +
      "'Rancho_Alegre', 'CA', " +
      "'94049'), " +
    "COF_ARRAY('Colombian', " +
      "'French_Roast', 'Espresso', " +
      "'Colombian_Decaf', " +
      "'French_Roast_Decaf'}, " +
    "SELECT OID FROM MANAGERS " +
    "WHERE MGR_ID = 000001)";

  stmt.executeUpdate(insertMgr);
```

但是，因为您要发送多个`INSERT INTO`语句，所以将它们作为批量更新一起发送将更有效，如下面的代码示例所示：

```java
package com.oracle.tutorial.jdbc;

import java.sql.*;

public class InsertStores {
    public static void main(String args[]) {

        JDBCTutorialUtilities myJDBCTutorialUtilities;
        Connection myConnection = null;

        if (args[0] == null) {
            System.err.println(
                "Properties file " +
                "not specified " +
                "at command line");
            return;
        } else {
            try {
                myJDBCTutorialUtilities = new
                    JDBCTutorialUtilities(args[0]);
            } catch (Exception e) {
                System.err.println(
                    "Problem reading " +
                    "properties file " +
                    args[0]);
                e.printStackTrace();
                return;
            }
        }

        Connection con = null;
        Statement stmt = null;

        try {
            con = myJDBCTutorialUtilities.getConnection();
            con.setAutoCommit(false);

            stmt = con.createStatement();

            String insertStore1 =
                "INSERT INTO STORES VALUES (" +
                "100001, " +
                "ADDRESS(888, 'Main_Street', " +
                    "'Rancho_Alegre', 'CA', " +
                    "'94049'), " +
                "COF_ARRAY('Colombian', " +
                    "'French_Roast', " +
                    "'Espresso', " +
                    "'Colombian_Decaf', " +
                    "'French_Roast_Decaf'), " +
                "(SELECT OID FROM MANAGERS " +
                "WHERE MGR_ID = 000001))";

            stmt.addBatch(insertStore1);

            String insertStore2 =
                "INSERT INTO STORES VALUES (" +
                "100002, " +
                "ADDRESS(1560, 'Alder', " +
                    "'Ochos_Pinos', " +
                    "'CA', '94049'), " +
                "COF_ARRAY('Colombian', " +
                    "'French_Roast', " +
                    "'Espresso', " +
                    "'Colombian_Decaf', " +
                    "'French_Roast_Decaf', " +
                    "'Kona', 'Kona_Decaf'), " +
                "(SELECT OID FROM MANAGERS " +
                "WHERE MGR_ID = 000001))";

            stmt.addBatch(insertStore2);

            String insertStore3 =
                "INSERT INTO STORES VALUES (" +
                "100003, " +
                "ADDRESS(4344, " +
                    "'First_Street', " +
                    "'Verona', " +
                    "'CA', '94545'), " +
                "COF_ARRAY('Colombian', " +
                    "'French_Roast', " +
                    "'Espresso', " +
                    "'Colombian_Decaf', " +
                    "'French_Roast_Decaf', " +
                    "'Kona', 'Kona_Decaf'), " +
                "(SELECT OID FROM MANAGERS " +
                "WHERE MGR_ID = 000002))";

            stmt.addBatch(insertStore3);

            String insertStore4 =
                "INSERT INTO STORES VALUES (" +
                "100004, " +
                "ADDRESS(321, 'Sandy_Way', " +
                    "'La_Playa', " +
                    "'CA', '94544'), " +
                "COF_ARRAY('Colombian', " +
                    "'French_Roast', " +
                    "'Espresso', " +
                    "'Colombian_Decaf', " +
                    "'French_Roast_Decaf', " +
                    "'Kona', 'Kona_Decaf'), " +
                "(SELECT OID FROM MANAGERS " +
                "WHERE MGR_ID = 000002))";

            stmt.addBatch(insertStore4);

            String insertStore5 =
                "INSERT INTO STORES VALUES (" +
                "100005, " +
                "ADDRESS(1000, 'Clover_Road', " +
                    "'Happyville', " +
                    "'CA', '90566'), " +
                "COF_ARRAY('Colombian', " +
                    "'French_Roast', " +
                    "'Espresso', " + 
                    "'Colombian_Decaf', " +
                    "'French_Roast_Decaf'), " +
                "(SELECT OID FROM MANAGERS " +
                "WHERE MGR_ID = 000003))";

            stmt.addBatch(insertStore5);

            int [] updateCounts = stmt.executeBatch();

            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM STORES");
            System.out.println("Table STORES after insertion:");
            System.out.println("STORE_NO   " + "LOCATION   " +
                "COF_TYPE   " + "MGR");

            while (rs.next()) {
                int storeNo = rs.getInt("STORE_NO");
                Struct location = (Struct)rs.getObject("LOCATION");
                Object[] locAttrs = location.getAttributes();
                Array coffeeTypes = rs.getArray("COF_TYPE");
                String[] cofTypes = (String[])coffeeTypes.getArray();

                Ref managerRef = rs.getRef("MGR");
                PreparedStatement pstmt = con.prepareStatement(
                    "SELECT MANAGER " +
                    "FROM MANAGERS " +
                    "WHERE OID = ?");
  
                pstmt.setRef(1, managerRef);
                ResultSet rs2 = pstmt.executeQuery();
                rs2.next();
                Struct manager = (Struct)rs2.getObject("MANAGER");
                Object[] manAttrs = manager.getAttributes();
      
                System.out.print(storeNo + "   ");
                System.out.print(
                    locAttrs[0] + " " +
                    locAttrs[1] + " " +
                    locAttrs[2] + ", " +
                    locAttrs[3] + " " +
                    locAttrs[4] + " ");

                for (int i = 0; i < cofTypes.length; i++)
                    System.out.print( cofTypes[i] + " ");
          
                System.out.println(
                    manAttrs[1] + ", " +
                    manAttrs[2]);
        
                rs2.close();
                pstmt.close();
            }

            rs.close();

        } catch(BatchUpdateException b) {
            System.err.println("-----BatchUpdateException-----");
            System.err.println("SQLState:  " + b.getSQLState());
            System.err.println("Message:  " + b.getMessage());
            System.err.println("Vendor:  " + b.getErrorCode());
            System.err.print("Update counts:  ");
            int [] updateCounts = b.getUpdateCounts();

            for (int i = 0; i < updateCounts.length; i++) {
                System.err.print(updateCounts[i] + "   ");
            }
            System.err.println("");

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            System.err.println("SQLState:  " + ex.getSQLState());
            System.err.println("Message:  " + ex.getMessage());
            System.err.println("Vendor:  " + ex.getErrorCode());
        } finally {
            if (stmt != null) { stmt.close(); }
                JDBCTutorialUtilities.closeConnection(con);
            }
        }
    }
}
```

