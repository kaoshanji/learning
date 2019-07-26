# 使用CachedRowSetObjects

甲`CachedRowSet`目的是因为它可以在不连接到其数据源操作的特殊，也就是说，它是一个*断开的* `RowSet`对象。它的名称来源于它将数据存储（缓存）在内存中，以便它可以在自己的数据上运行，而不是在数据库中存储的数据上运行。

该`CachedRowSet`接口是所有断开的超接口`RowSet`的对象，所以这里的一切也展示了适用于`WebRowSet`，`JoinRowSet`和`FilteredRowSet`对象。

请注意，虽然`CachedRowSet`对象的数据源（以及`RowSet`从中派生的对象）几乎总是关系数据库，但是`CachedRowSet`对象能够从以表格格式存储其数据的任何数据源获取数据。例如，平面文件或电子表格可能是数据的来源。当实现`RowSetReader`断开连接的`RowSet`对象的对象以从这样的数据源读取数据时，这是真的。`CachedRowSet`接口的参考实现具有`RowSetReader`从关系数据库读取数据的对象，因此在本教程中，数据源始终是数据库。

涵盖以下主题：

- [设置CachedRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#setting-up-cachedrowset-object)
- [填充CachedRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#populating-cachedrowset-object)
- [读者做什么](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#reader)
- [更新CachedRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#updating-cachedrowset-object)
- [更新数据源](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#updating-data-source)
- [作家是做什么的](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#writer)
- [通知听众](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#notifying-listeners)
- [发送大量数据](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#sending-large-amounts-of-data)

## 设置CachedRowSet对象

设置`CachedRowSet`对象涉及以下内容：

- [创建CachedRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#creating-cachedrowset-object)
- [设置CachedRowSet属性](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#setting-cachedrowset-properties)
- [设置关键列](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#setting-key-columns)

### 创建CachedRowSet对象

您可以`CachedRowSet`通过不同方式创建新对象：

- [使用默认构造函数](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#using-default-constructor)
- 使用的一个实例`RowSetFactory`，其从类创建`RowSetProvider`：请参阅[使用在RowSetFactory接口](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#rowsetfactory)中[使用的JdbcRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html)的详细信息。

**注意**：或者，您可以使用`CachedRowSet`JDBC驱动程序实现中的构造函数。但是，`RowSet`接口的实现将与参考实现不同。这些实现将具有不同的名称和构造函数。例如，Oracle JDBC驱动程序的`CachedRowSet`接口实现已命名`oracle.jdbc.rowset.OracleCachedRowSet`。

#### 使用默认构造函数

可以创建`CachedRowSet`对象的方法之一是调用参考实现中定义的默认构造函数，如以下代码行中所示：

```java
CachedRowSet crs = new CachedRowSetImpl();
```

该对象`crs`具有与`JdbcRowSet`对象在首次创建时具有的属性相同的默认值。此外，还为它分配了一个默认`SyncProvider`实现的实例`RIOptimisticProvider`。

甲`SyncProvider`对象提供一个`RowSetReader`对象（一个*阅读器*）和一个`RowSetWriter`对象（一个*写入*），该断开连接的`RowSet`对象，以便从数据源读取数据或将数据写回到其数据源的需要。什么是读者和作家都在后面章节解释[什么阅读者](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#reader)和[什么Writer在](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#writer)。要记住的一件事是读者和作者完全在后台工作，所以解释他们的工作方式仅仅是为了您的信息。在读者和作者身上有一些背景知识可以帮助您了解`CachedRowSet`界面中定义的某些方法在后台进行的操作。

### 设置CachedRowSet属性

通常，属性的默认值可以正常，但您可以通过调用相应的setter方法来更改属性的值。有些属性没有默认值，您必须自己设置。

为了获取数据，断开连接的`RowSet`对象必须能够连接到数据源并具有一些选择要保存的数据的方法。以下属性包含获取与数据库的连接所必需的信息。

- `username`：用户提供给数据库的名称，作为获取访问权限的一部分
- `password`：用户的数据库密码
- `url`：用户要连接的数据库的JDBC URL
- `datasourceName`：用于检索已向JNDI命名服务注册的DataSource对象的名称

您必须设置以下哪些属性取决于您要如何建立连接。首选方法是使用`DataSource`对象，但是使用`DataSource`JNDI命名服务注册对象可能不实际，这通常由系统管理员完成。因此，代码示例都使用该`DriverManager`机制来获取连接，您使用该`url`属性而不是`datasourceName`属性。

以下代码行设置了`username`，`password`和`url`属性，以便可以使用`DriverManager`该类获取连接。（您将`url`在JDBC驱动程序的文档中找到要设置为属性值的JDBC URL 。）

```java
public void setConnectionProperties(
    String username, String password) {
    crs.setUsername(username);
    crs.setPassword(password);
    crs.setUrl("jdbc:mySubprotocol:mySubname");
    // ...
```

您必须设置的另一个属性是`command`属性。在参考实现中，数据`RowSet`从`ResultSet`对象读入对象。生成该`ResultSet`对象的查询是`command`属性的值。例如，以下代码行`command`使用查询设置属性，该查询生成`ResultSet`包含表中所有数据的对象`MERCH_INVENTORY`：

```java
crs.setCommand("select * from MERCH_INVENTORY");
```

### 设置关键列

如果要对`crs`对象进行任何更新并希望将这些更新保存在数据库中，则必须再设置一条信息：关键列。键列与主键基本相同，因为它们表示唯一标识行的一个或多个列。不同之处在于，在数据库中的表上设置了主键，而在特定`RowSet`对象上设置了键列。以下代码行为`crs`第一列设置了键列：

```java
int [] keys = {1};
crs.setKeyColumns(keys);
```

表中的第一列`MERCH_INVENTORY`是`ITEM_ID`。它可以作为键列，因为每个项标识符都不同，因此唯一标识表中的一行和一行`MERCH_INVENTORY`。此外，此列被指定为`MERCH_INVENTORY`表定义中的主键。该方法`setKeyColumns`采用数组来允许它可能需要两列或更多列来唯一地标识行。

作为兴趣点，该方法`setKeyColumns`不设置属性的值。在这种情况下，它设置字段的值`keyCols`。键列在内部使用，因此在设置它们之后，您不再对它们执行任何操作。您将在“ [使用SyncResolver对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#syncresolver) ”一节中看到如何以及何时使用键列。

## 填充CachedRowSet对象

`RowSet`填充断开连接的对象涉及比填充连接`RowSet`对象更多的工作。幸运的是，额外的工作是在后台完成的。完成设置`CachedRowSet`对象的初步工作后`crs`，将填充以下代码行`crs`：

```java
crs.execute();
```

数据输入`crs`是`ResultSet`通过在command属性中执行查询而生成的对象中的数据。

所不同的是，`CachedRowSet`为实施`execute`方法确实比多了不少`JdbcRowSet`的实现。或者更准确地说，`CachedRowSet`方法执行委托其任务的对象的读者做了很多。

每个断开连接的`RowSet`对象都有一个`SyncProvider`分配给它的对象，这个`SyncProvider`对象提供了`RowSet`对象的*读者*（一个`RowSetReader`对象）。当`crs`被创建的对象，它被用来作为默认`CachedRowSetImpl`构造，其中，除了用于属性设置默认值，分配的一个实例`RIOptimisticProvider`实现为默认`SyncProvider`对象。

## 读者做什么

当应用程序调用该方法时`execute`，断开连接的`RowSet`对象的阅读器在后台工作，以`RowSet`使用数据填充对象。新创建的`CachedRowSet`对象未连接到数据源，因此必须获取与该数据源的连接才能从中获取数据。默认`SyncProvider`对象（`RIOptimisticProvider`）的引用实现提供了一个读取器，它通过使用为用户名，密码以及JDBC URL或数据源名称（最近设置的那个）设置的值来获取连接。然后，阅读器执行该命令的查询集。它读取`ResultSet`查询生成的`CachedRowSet`对象中的数据，用该数据填充对象。最后，读者关闭连接。

## 更新CachedRowSet对象

在Coffee Break场景中，所有者希望简化操作。业主决定让仓库中的员工直接将库存输入PDA（个人数字助理），从而避免让第二个人进行数据输入的容易出错的过程。甲`CachedRowSet`目的是在这种情况下理想的，因为它重量轻，序列化的，并且可以在不到数据源的连接被更新。

所有者将让应用程序开发团队为PDA创建GUI工具，仓库员工将使用该工具输入库存数据。总部将创建一个`CachedRowSet`填充了表格的对象，该表格显示当前库存并使用互联网将其发送到PDA。当仓库员工使用GUI工具输入数据时，该工具会将每个条目添加到一个数组中，该`CachedRowSet`对象将用于在后台执行更新。完成清单后，PDA将新数据发送回总部，数据将上传到主服务器。

本节包括以下主题：

- [更新列值](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#updating-column-value)
- [插入和删除行](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#inserting-and-deleting-rows)

### 更新列值

更新`CachedRowSet`对象中的数据与更新`JdbcRowSet`对象中的数据相同。例如，以下代码片段`CachedRowSetSample.java`将列中的值增加`QUAN`1，其`ITEM_ID`列的项标识符为`12345`：

```java
while (crs.next()) {
    System.out.println(
        "Found item " + crs.getInt("ITEM_ID") +
        ": " + crs.getString("ITEM_NAME"));
    if (crs.getInt("ITEM_ID") == 1235) {
        int currentQuantity = crs.getInt("QUAN") + 1;
        System.out.println("Updating quantity to " +
          currentQuantity);
        crs.updateInt("QUAN", currentQuantity + 1);
        crs.updateRow();
        // Synchronizing the row
        // back to the DB
        crs.acceptChanges(con);
    }
```

### 插入和删除行

与更新列值一样，在`CachedRowSet`对象中插入和删除行的代码与对象相同`JdbcRowSet`。

以下摘录从对象中`CachedRowSetSample.java`插入新行：`CachedRowSet``crs`

```java
crs.moveToInsertRow();
crs.updateInt("ITEM_ID", newItemId);
crs.updateString("ITEM_NAME", "TableCloth");
crs.updateInt("SUP_ID", 927);
crs.updateInt("QUAN", 14);
Calendar timeStamp;
timeStamp = new GregorianCalendar();
timeStamp.set(2006, 4, 1);
crs.updateTimestamp(
    "DATE_VAL",
    new Timestamp(timeStamp.getTimeInMillis()));
crs.insertRow();
crs.moveToCurrentRow();
```

如果总部决定停止库存特定物品，那么它可能会删除该咖啡本身的行。但是，在该方案中，使用PDA的仓库员工也具有删除它的能力。下面的代码片断发现该行，其中在值`ITEM_ID`列`12345`，并从删除它`CachedRowSet` `crs`：

```java
while (crs.next()) {
    if (crs.getInt("ITEM_ID") == 12345) {
        crs.deleteRow();
        break;
    }
}
```

## 更新数据源

在对`JdbcRowSet`对象进行更改和对对象进行更改之间存在重大差异`CachedRowSet`。由于`JdbcRowSet`对象被连接到它的数据源，所述方法`updateRow`，`insertRow`和`deleteRow`可以同时更新`JdbcRowSet`对象和数据源。`RowSet`但是，对于断开连接的对象，这些方法会更新存储在`CachedRowSet`对象内存中的数据，但不会影响数据源。断开连接的`RowSet`对象必须调用该方法`acceptChanges`才能将其更改保存到数据源。在库存场景中，返回总部，应用程序将调用该方法`acceptChanges`以使用列的新值更新数据库`QUAN`。

```java
crs.acceptChanges();
```

## 作家是做什么的

与该方法一样`execute`，该方法`acceptChanges`无形地完成其工作。虽然该方法`execute`将其工作委托给`RowSet`对象的reader，但该方法`acceptChanges`将其任务委托给`RowSet`对象的writer。在后台，编写器打开与数据库的连接，使用对`RowSet`对象所做的更改来更新数据库，然后关闭连接。

### 使用默认实现

困难在于可能产生*冲突*。冲突是指另一方更新了数据库中与`RowSet`对象中更新的值相对应的值的情况。哪个值应该在数据库中保留？作者在发生冲突时所做的工作取决于如何实施，并且有很多可能性。在频谱的一端，编写器甚至不检查冲突，只是将所有更改写入数据库。这是实现的情况，`RIXMLProvider`由`WebRowSet`对象使用。另一方面，编写器通过设置阻止其他人进行更改的数据库锁来确保不存在冲突。

该`crs`对象的编写器是默认`SyncProvider`实现提供的编写器`RIOptimisticProvider`。该`RIOPtimisticProvider`实现的名称来自于它使用乐观并发模型的事实。此模型假定冲突很少（如果有），因此不设置数据库锁。编写器检查是否存在任何冲突，如果没有冲突，则将对`crs`对象所做的更改写入数据库，并且这些更改将成为持久性的。如果存在任何冲突，则默认情况下不将新`RowSet`值写入数据库。

在该方案中，默认行为非常有效。因为总部没有人可能会更改`QUAN`列中的值`COF_INVENTORY`，所以不会发生冲突。结果，输入`crs`仓库中对象的值将被写入数据库，因此将是持久的，这是期望的结果。

## 使用SyncResolver对象

但是，在其他情况下，冲突可能存在。为了适应这些情况，`RIOPtimisticProvider`实现提供了一个选项，使您可以查看冲突中的值并确定哪些值应该是持久的。此选项是`SyncResolver`对象的使用。

当编写器完成查找冲突并找到一个或多个冲突时，它会创建一个`SyncResolver`包含导致冲突的数据库值的对象。接下来，该方法`acceptChanges`抛出一个`SyncProviderException`对象，应用程序可以捕获该`SyncResolver`对象并使用它来检索该对象。以下代码行检索`SyncResolver`对象`resolver`：

```java
try {
    crs.acceptChanges();
} catch (SyncProviderException spe) {
    SyncResolver resolver = spe.getSyncResolver();
}
```

该对象`resolver`是`RowSet`复制`crs`对象的对象，除了它仅包含导致冲突的数据库中的值。所有其他列值均为null。

使用该`resolver`对象，您可以遍历其行以查找非空值，因此是导致冲突的值。然后，您可以在`crs`对象中的相同位置找到该值并进行比较。以下代码片段检索`resolver`并使用该`SyncResolver`方法`nextConflict`迭代具有冲突值的行。该对象`resolver`获取每个冲突值的状态，如果是`UPDATE_ROW_CONFLICT`，则表示在`crs`发生冲突时尝试更新，该`resolver`对象获取该值的行号。然后代码将`crs`对象的光标移动到同一行。接下来，代码找到该行中的列`resolver`包含冲突值的对象，该值将是一个非null值。从两个`resolver`和`crs`对象中检索该列中的值后，您可以比较两者并确定要保持哪一个。最后，代码`crs`使用该方法在对象和数据库中设置该值`setResolvedValue`，如以下代码所示：

```java
try {
    crs.acceptChanges();
} catch (SyncProviderException spe) {
    SyncResolver resolver = spe.getSyncResolver();
  
    // value in crs
    Object crsValue;
  
    // value in the SyncResolver object
    Object resolverValue; 
  
    // value to be persistent
    Object resolvedValue; 

    while (resolver.nextConflict()) {
        if (resolver.getStatus() ==
            SyncResolver.UPDATE_ROW_CONFLICT) {
            int row = resolver.getRow();
            crs.absolute(row);
            int colCount =
                crs.getMetaData().getColumnCount();
            for (int j = 1; j <= colCount; j++) {
                if (resolver.getConflictValue(j)
                    != null) {
                    crsValue = crs.getObject(j);
                    resolverValue = 
                        resolver.getConflictValue(j);

                    // ...
                    // compare crsValue and
                    // resolverValue to
                    // determine the value to be
                    // persistent

                    resolvedValue = crsValue;
                    resolver.setResolvedValue(
                        j, resolvedValue);
                }
            }
        }
    }
}
```

## 通知听众

作为JavaBeans组件意味着`RowSet`对象可以在发生某些事情时通知其他组件。例如，如果`RowSet`对象中的数据发生更改，则`RowSet`对象可以通知相关方该更改。这种通知机制的好处在于，作为应用程序员，您所要做的就是添加或删除将通知的组件。

本节包括以下主题：

- [设置监听器](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#setting-up-listeners)
- [通知如何工作](https://docs.oracle.com/javase/tutorial/jdbc/basics/cachedrowset.html#how-notification-works)

### 设置监听器

对象的*侦听*器`RowSet`是从`RowSetListener`接口实现以下方法的组件：

- `cursorMoved`：定义当`RowSet`对象中的光标移动时侦听器将执行的操作（如果有）。
- `rowChanged`：定义侦听器将执行的操作（如果有），行中的一个或多个列值发生更改，插入行或删除行时。
- `rowSetChanged`：定义在`RowSet`使用新数据填充对象时，侦听器将执行的操作（如果有）。

可能希望成为侦听器的组件示例是一个`BarGraph`对象，用于绘制对象中的数据`RowSet`。随着数据的变化，`BarGraph`对象可以自行更新以反映新数据。

作为应用程序程序员，利用通知机制必须做的唯一事情是添加或删除侦听器。以下代码行表示每次`crs`对象的光标移动，值都会`crs`更改，或者`crs`整体获取新数据时，将通知`BarGraph`对象`bar`：

```java
crs.addRowSetListener(bar);
```

您还可以通过删除侦听器来停止通知，如以下代码行中所示：

```java
crs.removeRowSetListener(bar);
```

使用Coffee Break场景，假设总部定期检查数据库，以获取其在线销售的咖啡的最新价目表。在这种情况下，监听器是`PriceList`物体`priceList`在休息的网站，必须实现`RowSetListener`方法`cursorMoved`，`rowChanged`以及`rowSetChanged`。该`cursorMoved`方法的实现可能是什么都不做，因为光标的位置不会影响`priceList`对象。另一方面，这些`rowChanged`和`rowSetChanged`方法的实现必须确定已经进行了哪些更改并相应地更新`priceList`。

### 通知如何工作

在参考实现中，导致任何`RowSet`事件的方法自动通知所有已注册的侦听器。例如，任何移动光标的方法也会调用`cursorMoved`每个侦听器上的方法。同样，该方法在所有侦听器上`execute`调用该方法`rowSetChanged`，并`acceptChanges`调用`rowChanged`所有侦听器。

## 发送大量数据

示例代码[`CachedRowSetSample.testCachedRowSet`](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)演示了如何以较小的片段发送数据。

