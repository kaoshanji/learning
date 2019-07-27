# 使用自定义类型映射

**注意**：MySQL目前不支持用户定义的类型。MySQL和Java DB目前不支持结构化类型或`DISTINCT`SQL数据类型。没有JDBC教程示例可用于演示本节中描述的功能。

随着业务的蓬勃发展，The Coffee Break的所有者定期添加新商店并对数据库进行更改。所有者已决定对结构化类型使用自定义映射`ADDRESS`。这使所有者能够对映射该`ADDRESS`类型的Java类进行更改。Java类将为每个属性提供一个字段`ADDRESS`。类的名称及其字段的名称可以是任何有效的Java标识符。

涵盖以下主题：

- [实现SQLData](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlcustommapping.html#implementing_sqldata)
- [使用连接的类型映射](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlcustommapping.html#using_connection_type_map)
- [使用您自己的类型地图](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlcustommapping.html#using_your_own_type_map)

## 实现SQLData

自定义映射所需的第一件事是创建一个实现该接口的类`SQLData`。

结构化类型的SQL定义`ADDRESS`如下所示：

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

实现类型`SQLData`的自定义映射的接口的`ADDRESS`类可能如下所示：

```java
public class Address implements SQLData {
    public int num;
    public String street;
    public String city;
    public String state;
    public String zip;
    private String sql_type;
    
    public String getSQLTypeName() {
        return sql_type;
    }

    public void readSQL(SQLInput stream, String type)
        throws SQLException {
        sql_type = type;
        num = stream.readInt();
        street = stream.readString();
        city = stream.readString();
        state = stream.readString();
        zip = stream.readString();
    }

    public void writeSQL(SQLOutput stream)
        throws SQLException {
        stream.writeInt(num);
        stream.writeString(street);
        stream.writeString(city);
        stream.writeString(state);
        stream.writeString(zip);
    }
}
```

## 使用连接的类型映射

在编写实现接口的类之后`SQLData`，设置自定义映射所需要做的唯一事情就是在类型映射中创建一个条目。例如，这意味着输入类的完全限定SQL名称`ADDRESS`和`Class`类的对象`Address`。类型映射（`java.util.Map`接口的实例）在创建时与每个新连接相关联，因此您可以使用该连接。假设这`con`是活动连接，以下代码片段将UDT的条目添加`ADDRESS`到与之关联的类型映射`con`。

```java
java.util.Map map = con.getTypeMap();
map.put("SchemaName.ADDRESS", Class.forName("Address"));
con.setTypeMap(map);
```

无论何时调用该`getObject`方法来检索该`ADDRESS`类型的实例，驱动程序都将检查与该连接关联的类型映射，并查看它是否有条目`ADDRESS`。驱动程序将记录该类的`Class`对象`Address`，创建它的实例，并在后台执行许多其他操作以映射`ADDRESS`到该类`Address`。除了为映射生成类之外，您不必执行任何操作，然后在类型映射中创建条目以使驱动程序知道存在自定义映射。司机将完成剩下的工作。

存储具有自定义映射的结构化类型的情况类似。当您调用该方法时`setObject`，驱动程序将检查要设置的值是否是实现该接口的类的实例`SQLData`。如果是（意味着存在自定义映射），则驱动程序将使用自定义映射将值转换为SQL对应项，然后再将其返回到数据库。同样，驱动程序在幕后进行自定义映射; 您需要做的就是为该方法`setObject`提供一个具有自定义映射的参数。您将在本节后面看到此示例。

查看使用标准映射，`Struct`对象和自定义映射（Java编程语言中的类）之间的区别。以下代码片段显示了`Struct`对象的标准映射，该对象是驱动程序在连接的类型映射中没有条目时使用的映射。

```java
ResultSet rs = stmt.executeQuery(
    "SELECT LOCATION " +
    "WHERE STORE_NO = 100003");
rs.next();
Struct address = (Struct)rs.getObject("LOCATION");
```

变量`address`包含以下属性值：`4344`，`"First_Street"`，`"Verona"`，`"CA"`，`"94545"`。

以下代码片段显示了`ADDRESS`在连接的类型映射中存在结构化类型的条目时会发生什么。请记住，该列`LOCATION`存储类型的值`ADDRESS`。

```java
ResultSet rs = stmt.executeQuery(
    "SELECT LOCATION " +
    "WHERE STORE_NO = 100003");
rs.next();
Address store_3 = (Address)rs.getObject("LOCATION");
```

变量`store_3`现在是类的一个实例`Address`，每个属性值都是其中一个字段的当前值`Address`。请注意，在将其分配给对象之前，必须记住将`getObject`方法检索的对象转换为对象。另请注意，必须是对象。`Address``store_3``store_3``Address`

比较使用该`Struct`对象以使用`Address`该类的实例。假设商店移动到邻近城镇的更好位置，因此您必须更新数据库。使用自定义映射，重置字段`store_3`，如下面的代码片段所示：

```java
ResultSet rs = stmt.executeQuery(
    "SELECT LOCATION " +
    "WHERE STORE_NO = 100003");
rs.next();
Address store_3 = (Address)rs.getObject("LOCATION");
store_3.num = 1800;
store_3.street = "Artsy_Alley";
store_3.city = "Arden";
store_3.state = "CA";
store_3.zip = "94546";
PreparedStatement pstmt = con.prepareStatement(
    "UPDATE STORES " +
    "SET LOCATION = ? " +
    "WHERE STORE_NO = 100003");
pstmt.setObject(1, store_3);
pstmt.executeUpdate();
```

列`LOCATION`中的值是`ADDRESS`类型的实例。驱动程序检查连接的类型映射，并看到有一个条目链接`ADDRESS`到类`Address`，因此使用指示的自定义映射`Address`。当代码`setObject`使用变量`*store_3*`作为第二个参数调用方法时，驱动程序检查并查看`*store_3*`表示类的实例，该实例`Address`实现`SQLData`结构化类型的接口`ADDRESS`，并再次自动使用自定义映射。

如果没有自定义映射`ADDRESS`，则更新看起来更像是：

```java
PreparedStatement pstmt = con.prepareStatement(
    "UPDATE STORES " +
    "SET LOCATION.NUM = 1800, " +
    "LOCATION.STREET = 'Artsy_Alley', " + 
    "LOCATION.CITY = 'Arden', " +
    "LOCATION.STATE = 'CA', " +
    "LOCATION.ZIP = '94546' " +
    "WHERE STORE_NO = 100003");
pstmt.executeUpdate;
```

## 使用您自己的类型地图

到目前为止，您仅使用与连接关联的类型映射进行自定义映射。通常，这是大多数程序员将使用的唯一类型映射。但是，也可以创建类型映射并将其传递给某些方法，以便驱动程序将使用该类型映射而不是与连接关联的映射。这允许两个不同的映射用于相同的用户定义类型（UDT）。实际上，只要每个映射都使用实现`SQLData`接口的类和类型映射中的条目来设置，就可以为同一个UDT创建多个自定义映射。如果未将类型映射传递给可接受类型映射的方法，则驱动程序将默认使用与该连接关联的类型映射。

很少有情况要求使用除与连接关联的类型映射之外的类型映射。例如，如果在JDBC应用程序上工作的几个程序员将其组件组合在一起并使用相同的连接，则可能需要提供带有类型映射的方法。如果两个或多个程序员为同一个SQL UDT创建了自己的自定义映射，则每个程序员都需要提供他或她自己的类型映射，从而覆盖连接的类型映射。