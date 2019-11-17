# 使用FilteredRowSet对象

一个`FilteredRowSet`对象可以让你砍倒在一个可见的行数`RowSet`对象，以便您可以仅是相关的，你正在做的事情中的数据。您可以决定要对数据设置的限制（您希望如何“过滤”数据）并将该过滤器应用于`FilteredRowSet`对象。换句话说，该`FilteredRowSet`对象仅显示符合您设置的限制的数据行。一个`JdbcRowSet`对象，它总是有其数据源的连接，能做到这一点的过滤与查询到，只有选择您希望看到的行和列的数据源。query的`WHERE`子句定义过滤条件。甲`FilteredRowSet`对象提供了一种用于断开的方式`RowSet`无需对数据源执行查询就可以执行此过滤，从而避免必须连接到数据源并向其发送查询。

例如，假设咖啡馆的Coffee Break连锁店已经发展到整个美国的数百家商店，并且所有商店都被列在一张名为的表中`COFFEE_HOUSES`。业主希望仅通过咖啡馆比较应用来衡量加利福尼亚州商店的成功，该应用不需要与数据库系统的持久连接。这种比较将考察销售商品与销售咖啡饮料的盈利能力以及各种其他成功衡量标准，并将根据咖啡饮料销售，商品销售和总销售额对加州商店进行排名。由于该表`COFFEE_HOUSES`有数百行，如果搜索的数据量仅减少到列中的值`STORE_ID`表示加利福尼亚的那些行，则这些比较将更快更容易。

这正是`FilteredRowSet`对象通过提供以下功能解决的问题：

- 能够根据设置的条件限制可见的行
- 能够选择哪些数据是可见的而无需连接到数据源

涵盖以下主题：

- [在谓词对象中定义过滤条件](https://docs.oracle.com/javase/tutorial/jdbc/basics/filteredrowset.html#defining-filtering-criteria-in-predicate-object)
- [创建FilteredRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/filteredrowset.html#creating-filteredrowset-object)
- [创建和设置谓词对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/filteredrowset.html#creating-and-setting-predicate-object)
- [使用新的谓词对象设置FilteredRowSet对象以进一步过滤数据](https://docs.oracle.com/javase/tutorial/jdbc/basics/filteredrowset.html#filter-data-further)
- [更新FilteredRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/filteredrowset.html#updating-filteredrowset-object)
- [插入或更新行](https://docs.oracle.com/javase/tutorial/jdbc/basics/filteredrowset.html#inserting-or-updating-row)
- [删除所有过滤器以使所有行都可见](https://docs.oracle.com/javase/tutorial/jdbc/basics/filteredrowset.html#removing-all-filters)
- [删除行](https://docs.oracle.com/javase/tutorial/jdbc/basics/filteredrowset.html#deleting-row)

## 在谓词对象中定义过滤条件

要设置`FilteredRowSet`对象中哪些行可见的条件，请定义实现该`Predicate`接口的类。使用此类创建的对象使用以下内容进行初始化：

- 价值必须下降的范围的高端
- 价值必须下降的范围的低端
- 列的列名或列号，其值必须在由高和低边界设置的值范围内

请注意，值的范围是包含的，这意味着边界处的值包含在范围内。例如，如果范围具有100的高值和50的低值，则认为值50在该范围内。值49不是。同样，100在范围内，但101不在。

根据业主想要比较加利福尼亚商店的情况，`Predicate`必须编写一个过滤器，用于过滤位于加利福尼亚州的Coffee Break咖啡馆。没有一种正确的方法可以做到这一点，这意味着在编写实现方面存在很大的自由度。例如，您可以根据需要为类及其成员命名，并以任何方式实现构造函数和三个计算方法，以实现所需的结果。

列出所有名为咖啡馆的表格`COFFEE_HOUSES`有数百行。为了使事情更易于管理，本示例使用行少得多的表，这足以说明如何完成过滤。

该列`STORE_ID`中的`int`值是指示咖啡馆所处的状态等的值。例如，以10开头的值表示该州是加利福尼亚州。`STORE_ID`以32开头的值表示俄勒冈州，而以33开头的值表示华盛顿州。

以下类[`StateFilter`](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)实现`Predicate`接口：

```java
public class StateFilter implements Predicate {

    private int lo;
    private int hi;
    private String colName = null;
    private int colNumber = -1;

    public StateFilter(int lo, int hi, int colNumber) {
        this.lo = lo;
        this.hi = hi;
        this.colNumber = colNumber;
    }

    public StateFilter(int lo, int hi, String colName) {
        this.lo = lo;
        this.hi = hi;
        this.colName = colName;
    }

    public boolean evaluate(Object value, String columnName) {
        boolean evaluation = true;
        if (columnName.equalsIgnoreCase(this.colName)) {
            int columnValue = ((Integer)value).intValue();
            if ((columnValue >= this.lo)
                &&
                (columnValue <= this.hi)) {
                evaluation = true;
            } else {
                evaluation = false;
            }
        }
        return evaluation;
    }

    public boolean evaluate(Object value, int columnNumber) {

        boolean evaluation = true;

        if (this.colNumber == columnNumber) {
            int columnValue = ((Integer)value).intValue();
            if ((columnValue >= this.lo)
                &&
                (columnValue <= this.hi)) {
                evaluation = true;
            } else {
                evaluation = false;
            }
        }
        return evaluation;
    }


    public boolean evaluate(RowSet rs) {
    
        CachedRowSet frs = (CachedRowSet)rs;
        boolean evaluation = false;

        try {
            int columnValue = -1;

            if (this.colNumber > 0) {
                columnValue = frs.getInt(this.colNumber);
            } else if (this.colName != null) {
                columnValue = frs.getInt(this.colName);
            } else {
                return false;
            }

            if ((columnValue >= this.lo)
                &&
                (columnValue <= this.hi)) {
                evaluation = true;
            }
        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
            return false;
        } catch (NullPointerException npe) {
            System.err.println("NullPointerException caught");
            return false;
        }
        return evaluation;
    }
}
```

这是一个非常简单的实现，检查由任一中所指定的列中的值`colName`或`colNumber`以查看其是否是在的范围内`lo`，以`hi`以下。以下代码行from [`FilteredRowSetSample`](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)，创建一个过滤器，该过滤器仅允许`STORE_ID`列值指示介于10000和10999之间的值的行，这表示加利福尼亚州的位置：

```java
StateFilter myStateFilter = new StateFilter(10000, 10999, 1);
```

请注意，`StateFilter`刚定义的类适用于一列。通过使每个参数数组而不是单个值，可以将它应用于两个或更多列。例如，`Filter`对象的构造函数可能如下所示：

```java
public Filter2(Object [] lo, Object [] hi, Object [] colNumber) {
    this.lo = lo;
    this.hi = hi;
    this.colNumber = colNumber;
}
```

`colNumber`对象中的第一个元素给出了第一列，其中将根据第一个元素`lo`和第一个元素来检查值`hi`。在所指示的第二列的值`colNumber`将针对在第二元件进行检查`lo`和`hi`，依此类推。因此，三个数组中的元素数应该相同。以下代码是对象的实现方式`evaluate(RowSet rs)`，`Filter2`对象的参数是数组：

```java
public boolean evaluate(RowSet rs) {
    CachedRowSet crs = (CachedRowSet)rs;
    boolean bool1;
    boolean bool2;
    for (int i = 0; i < colNumber.length; i++) {

        if ((rs.getObject(colNumber[i] >= lo [i]) &&
            (rs.getObject(colNumber[i] <= hi[i]) {
            bool1 = true;
        } else {
            bool2 = true;
        }

        if (bool2) {
            return false;
        } else {
            return true;
        }
    }
}
```

使用`Filter2`实现的优点是您可以使用任何`Object`类型的参数，并且可以检查一列或多列而无需编写其他实现。但是，您必须传递一个`Object`类型，这意味着您必须将基本类型转换为其`Object`类型。例如，如果`int`对`lo`and 使用值`hi`，则必须在将`int`值`Integer`传递给构造函数之前将其转换为对象。`String`对象已经是`Object`类型，因此您不必转换它们。

## 创建FilteredRowSet对象

`FilteredRowSet`接口的参考实现`FilteredRowSetImpl`包括一个默认构造函数，在下面的代码行中用于创建空`FilteredRowSet`对象`frs:`

```java
FilteredRowSet frs = new FilteredRowSetImpl();
```

实现扩展了`BaseRowSet`抽象类，因此该`frs`对象具有定义的默认属性`BaseRowSet`。这意味着它`frs`是可滚动的，可更新的，不显示已删除的行，已启用转义处理，等等。而且，因为`FilteredRowSet`接口是一个子接口`CachedRowSet`，`Joinable`以及`WebRowSet`，所述`frs`对象具有各自的功能。它可以作为断开连接的`RowSet`对象运行，可以是对象的一部分`JoinRowSet`，并且可以以XML格式读写自己。

**注意**：或者，您可以使用`WebRowSet`JDBC驱动程序实现中的构造函数。但是，`RowSet`接口的实现将与参考实现不同。这些实现将具有不同的名称和构造函数。例如，Oracle JDBC驱动程序的`WebRowSet`接口实现已命名`oracle.jdbc.rowset.OracleWebRowSet`。

您可以使用`RowSetFactory`从类创建的实例`RowSetProvider`来创建`FilteredRowSet`对象。有关更多信息，请参阅[使用](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#rowsetfactory)[JdbcRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html)中的[使用](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html)[RowSetFactory接口](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#rowsetfactory)。

与其他断开连接的`RowSet`对象一样，该`frs`对象必须使用表格数据源中的数据填充自身，该表格数据源是参考实现中的关系数据库。以下代码片段[`FilteredRowSetSample`](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)设置连接到数据库以执行其命令所需的属性。请注意，此代码使用`DriverManager`类进行连接，这样做是为了方便起见。通常，最好使用`DataSource`已在实现Java命名和目录接口（JNDI）的命名服务中注册的对象：

```java
frs.setCommand("SELECT * FROM COFFEE_HOUSES");
frs.setUsername(settings.userName);
frs.setPassword(settings.password);
frs.setUrl(settings.urlString);
```

以下代码行`frs`使用`COFFEE_HOUSE`表中存储的数据填充对象：

```java
frs.execute();
```

该方法`execute`通过调用`RowSetReader`对象来`frs`执行各种操作，该对象创建连接，执行命令`frs`，填充`frs`来自生成的`ResultSet`对象的数据，并关闭连接。请注意，如果表`COFFEE_HOUSES`中的行数多于`frs`对象在内存中可以容纳的行数，`CachedRowSet`则会使用分页方法。

在该场景中，咖啡休息所有者将在办公室中完成前述任务，然后将存储在该`frs`对象中的信息导入或下载到咖啡馆比较应用程序。从现在开始，该`frs`对象将独立运行，而无需连接到数据源。

## 创建和设置谓词对象

现在该`FilteredRowSet`对象`frs`包含Coffee Break场所列表，您可以设置选择标准以缩小`frs`对象中可见的行数。

以下代码行使用`StateFilter`先前定义的类来创建对象`myStateFilter`，该对象检查列`STORE_ID`以确定哪些商店位于加利福尼亚（如果商店的ID号介于10000和10999之间，则商店位于加利福尼亚州）：

```java
StateFilter myStateFilter = new StateFilter(10000, 10999, 1);
```

以下行设置`myStateFilter`为过滤器`frs`。

```java
frs.setFilter(myStateFilter);
```

要进行实际过滤，请调用方法`next`，该方法在参考实现中调用`Predicate.evaluate`之前已实现的方法的相应版本。

如果返回值为`true`，则该行将可见; 如果返回值为`false`，则该行将不可见。

## 使用新的谓词对象设置FilteredRowSet对象以进一步过滤数据

您可以串行设置多个过滤器。第一次调用方法`setFilter`并将其传递给`Predicate`对象时，您已在该过滤器中应用了过滤条件。在`next`每行调用方法后，只显示那些满足过滤器的行，您可以`setFilter`再次调用，将其传递给另一个`Predicate`对象。即使一次只设置一个过滤器，效果是两个过滤器都累积应用。

例如，所有者通过将其设置`stateFilter`为`Predicate`对象来检索加利福尼亚州的Coffee Break商店列表`frs`。现在，业主希望比较加利福尼亚州两个城市的商店，旧金山（表中的SF `COFFEE_HOUSES`）和洛杉矶（表中的LA）。首先要做的是编写一个`Predicate`过滤SF或LA中的商店的实现：

```java
public class CityFilter implements Predicate {

    private String[] cities;
    private String colName = null;
    private int colNumber = -1;

    public CityFilter(String[] citiesArg, String colNameArg) {
        this.cities = citiesArg;
        this.colNumber = -1;
        this.colName = colNameArg;
    }

    public CityFilter(String[] citiesArg, int colNumberArg) {
        this.cities = citiesArg;
        this.colNumber = colNumberArg;
        this.colName = null;
    }

    public boolean evaluate Object valueArg, String colNameArg) {

        if (colNameArg.equalsIgnoreCase(this.colName)) {
            for (int i = 0; i < this.cities.length; i++) {
                if (this.cities[i].equalsIgnoreCase((String)valueArg)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean evaluate(Object valueArg, int colNumberArg) {

        if (colNumberArg == this.colNumber) {
            for (int i = 0; i < this.cities.length; i++) {
                if (this.cities[i].equalsIgnoreCase((String)valueArg)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean evaluate(RowSet rs) {

        if (rs == null) return false;

        try {
            for (int i = 0; i < this.cities.length; i++) {

                String cityName = null;

                if (this.colNumber > 0) {
                    cityName = (String)rs.getObject(this.colNumber);
                } else if (this.colName != null) {
                    cityName = (String)rs.getObject(this.colName);
                } else {
                    return false;
                }

                if (cityName.equalsIgnoreCase(cities[i])) {
                    return true;
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }
}
```

以下代码片段[`FilteredRowSetSample`](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)设置新过滤器并遍历行`frs`，打印出`CITY`列包含SF或LA的行。请注意，`frs`目前仅包含商店位于加利福尼亚州的行，因此当过滤器更改为其他对象时，`Predicate`对象的`state`条件仍然有效`Predicate`。下面的代码将过滤器设置为`CityFilter`对象`city`。该`CityFilter`实现使用数组作为构造函数的参数来说明如何完成：

```java
public void testFilteredRowSet() {

    FilteredRowSet frs = null;
    StateFilter myStateFilter = new StateFilter(10000, 10999, 1);
    String[] cityArray = { "SF", "LA" };

    CityFilter myCityFilter = new CityFilter(cityArray, 2);

    try {
        frs = new FilteredRowSetImpl();

        frs.setCommand("SELECT * FROM COFFEE_HOUSES");
        frs.setUsername(settings.userName);
        frs.setPassword(settings.password);
        frs.setUrl(settings.urlString);
        frs.execute();

        System.out.println("\nBefore filter:");
        FilteredRowSetSample.viewTable(this.con);

        System.out.println("\nSetting state filter:");
        frs.beforeFirst();
        frs.setFilter(myStateFilter);
        this.viewFilteredRowSet(frs);

        System.out.println("\nSetting city filter:");
        frs.beforeFirst();
        frs.setFilter(myCityFilter);
        this.viewFilteredRowSet(frs);
    } catch (SQLException e) {
        JDBCTutorialUtilities.printSQLException(e);
    }
}
```

对于位于加利福尼亚州旧金山或加利福尼亚州洛杉矶的每个商店，输出应包含一行。如果`CITY`列中包含LA并且`STORE_ID`列包含40003，则它将不会包含在列表中，因为当过滤器设置为已过滤时，它已被过滤掉`state`。（40003不在10000到10999的范围内。）

## 更新FilteredRowSet对象

您可以对`FilteredRowSet`对象进行更改，但前提是该更改不违反当前有效的任何过滤条件。例如，如果新值或值在过滤条件内，则可以插入新行或更改现有行中的一个或多个值。

## 插入或更新行

假设两个新的咖啡休息咖啡馆刚刚开业，店主想将它们添加到所有咖啡馆的名单中。如果要插入的行不符合有效的累积过滤条件，则将阻止添加该行。

`frs`对象的当前状态`StateFilter`是设置了`CityFilter`对象，然后设置了对象。因此，`frs`当前仅显示满足两个过滤器条件的那些行。同样重要的是，`frs`除非满足两个过滤器的条件，否则不能向对象添加行。以下代码片段尝试在`frs`对象中插入两个新行，其中一行中的值`STORE_ID`和`CITY`列中的值都满足条件，另一行中值in `STORE_ID`不通过过滤器，但`CITY`列中的值执行：

```java
frs.moveToInsertRow();
frs.updateInt("STORE_ID", 10101);
frs.updateString("CITY", "SF");
frs.updateLong("COF_SALES", 0);
frs.updateLong("MERCH_SALES", 0);
frs.updateLong("TOTAL_SALES", 0);
frs.insertRow();

frs.updateInt("STORE_ID", 33101);
frs.updateString("CITY", "SF");
frs.updateLong("COF_SALES", 0);
frs.updateLong("MERCH_SALES", 0);
frs.updateLong("TOTAL_SALES", 0);
frs.insertRow();
frs.moveToCurrentRow();
```

如果您`frs`使用该方法迭代对象`next`，您会在加利福尼亚州旧金山的新咖啡馆找到一排，但不会在华盛顿州旧金山的商店找到一排。

## 删除所有过滤器以使所有行都可见

所有者可以通过使过滤器无效来在华盛顿添加商店。如果没有设置过滤器，则`frs`对象中的所有行都将再次可见，并且可以将任何位置的商店添加到商店列表中。以下代码行取消`Predicate`设置当前过滤器，从而有效地取消先前在`frs`对象上设置的两个实现。

```java
frs.setFilter(null);
```

## 删除行

如果店主决定关闭或出售其中一个咖啡休息咖啡馆，店主将希望将其从`COFFEE_HOUSES`餐桌上删除。只要行可见，所有者就可以删除表现不佳的咖啡馆的行。

例如，假设`setFilter`刚刚使用参数null调用该方法，则`frs`对象上没有设置过滤器。这意味着所有行都是可见的，因此可以删除。但是，在设置`StateFilter`对象后`myStateFilter`，过滤掉加利福尼亚州以外的任何州，只能删除位于加利福尼亚州的商店。当为`CityFilter`对象`myCityFilter`设置`frs`对象时，只能删除加利福尼亚州旧金山或加利福尼亚州洛杉矶的咖啡馆，因为它们只在可见的行中。