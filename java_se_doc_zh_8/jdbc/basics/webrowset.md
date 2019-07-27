# 使用WebRowSet对象

一个`WebRowSet`目标是非常特殊的，因为除了提供所有的的功能`CachedRowSet`对象，它可以自己写为一个XML文档，也可以读取XML文档本身转换回`WebRowSet`对象。由于XML是不同企业可以相互通信的语言，因此它已成为Web服务通信的标准。因此，`WebRowSet`通过使Web服务能够以XML文档的形式从数据库发送和接收数据，对象满足了真正的需求。

涵盖以下主题：

- [创建和填充WebRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/webrowset.html#creating-and-populating-webrowset-object)
- [编写和读取WebRowSet对象到XML](https://docs.oracle.com/javase/tutorial/jdbc/basics/webrowset.html#writing-and-reading-webrowset-object-to-xml)
- [什么是XML文档](https://docs.oracle.com/javase/tutorial/jdbc/basics/webrowset.html#what-is-in-xml-document)
- [对WebRowSet对象进行更改](https://docs.oracle.com/javase/tutorial/jdbc/basics/webrowset.html#making-changes-to-webrowset-object)

Coffee Break公司已经扩展到在线销售咖啡。用户从Coffee Break网站点击咖啡。通过从公司数据库获取最新信息，定期更新价目表。本节演示如何将价格数据作为带有`WebRowSet`对象和单个方法调用的XML文档发送。

## 创建和填充WebRowSet对象

`WebRowSet`使用参考实现中定义的默认构造函数创建新对象`WebRowSetImpl`，如以下代码行所示：

```java
WebRowSet priceList = new WebRowSetImpl();
```

虽然该`priceList`对象尚无数据，但它具有`BaseRowSet`对象的默认属性。它的`SyncProvider`对象首先设置为`RIOptimisticProvider`实现，这是所有断开连接的`RowSet`对象的默认设置。但是，`WebRowSet`实现将`SyncProvider`对象重置为`RIXMLProvider`实现。

您可以使用`RowSetFactory`从`RowSetProvider`类创建的实例来创建`WebRowSet`对象。有关更多信息，请参阅[使用](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#rowsetfactory)[JdbcRowSet对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html)中的[使用](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html)[RowSetFactory接口](https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#rowsetfactory)。

Coffee Break总部定期向其网站发送价目表更新。有关`WebRowSet`对象的信息将显示一种可以在XML文档中发送最新价目表的方法。

价格表包含列`COF_NAME`和`PRICE`表中的数据`COFFEES`。以下代码片段设置所需的属性，并`priceList`使用价目表数据填充对象：

```java
public void getPriceList(String username, String password) {
    priceList.setCommand("SELECT COF_NAME, PRICE FROM COFFEES");
    priceList.setURL("jdbc:mySubprotocol:myDatabase");
    priceList.setUsername(username);
    priceList.setPassword(password);
    priceList.execute();
    // ...
}
```

此时，除了默认属性之外，该`priceList`对象还包含表中的`COF_NAME`和`PRICE`列中的数据以及`COFFEES`有关这两列的元数据。

## 编写和读取WebRowSet对象到XML

要将`WebRowSet`对象编写为XML文档，请调用该方法`writeXml`。要将XML文档的内容读入`WebRowSet`对象，请调用该方法`readXml`。这两种方法都在后台完成它们的工作，这意味着除了结果之外的所有内容对您来说都是不可见的。

### 使用writeXml方法

该方法`writeXml`将`WebRowSet`调用它的对象写为表示其当前状态的XML文档。它将此XML文档写入您传递给它的流。流可以是`OutputStream`对象（例如`FileOutputStream`对象）或`Writer`对象（例如`FileWriter`对象）。如果传递方法`writeXml`是一个`OutputStream`对象，你将以字节为单位写入，它可以处理所有类型的数据; 如果你传递一个`Writer`对象，你将用字符写。以下代码演示如何将`WebRowSet`对象`priceList`作为XML文档写入`FileOutputStream`对象`oStream`：

```java
java.io.FileOutputStream oStream =
    new java.io.FileOutputStream("priceList.xml");
priceList.writeXml(oStream);
```

下面的代码将表示所述XML文档`priceList`的`FileWriter`对象`writer`，而不是一个`OutputStream`对象。本`FileWriter`类是字符写入文件的便捷类。

```java
java.io.FileWriter writer =
    new java.io.FileWriter("priceList.xml");
priceList.writeXml(writer);
```

该方法的另外两个版本`writeXml`允许您在将`WebRowSet`对象的内容`ResultSet`写入流之前使用对象的内容填充该对象。在下面的行的代码，该方法`writeXml`读取的内容`ResultSet`对象`rs`到`priceList`对象，然后写入`priceList`到`FileOutputStream`对象`oStream`作为XML文档。

```java
priceList.writeXml(rs, oStream);
```

在下一行代码中，该`writeXml`方法`priceList`使用内容填充`rs`，但它将XML文档写入`FileWriter`对象而不是`OutputStream`对象：

```java
priceList.writeXml(rs, writer);
```

### 使用readXml方法

该方法`readXml`解析XML文档以构造`WebRowSet`XML文档描述的对象。与方法类似`writeXml`，您可以传递从中读取XML文档`readXml`的`InputStream`对象或`Reader`对象。

```java
java.io.FileInputStream iStream =
    new java.io.FileInputStream("priceList.xml");
priceList.readXml(iStream);

java.io.FileReader reader = new
    java.io.FileReader("priceList.xml");
priceList.readXml(reader);
```

请注意，您可以将XML描述读入新`WebRowSet`对象或`WebRowSet`调用该`writeXml`方法的同一对象。在从总部向Web站点发送价目表信息的方案中，您将使用新`WebRowSet`对象，如以下代码行所示：

```java
WebRowSet recipient = new WebRowSetImpl();
java.io.FileReader reader =
    new java.io.FileReader("priceList.xml");
recipient.readXml(reader);
```

## 什么是XML文档

`RowSet`对象不仅仅是它们包含的数据。它们还具有有关其列的属性和元数据。因此，表示`WebRowSet`对象的XML文档除了其数据之外还包括该其他信息。此外，XML文档中的数据包括当前值和原始值。（回想一下，原始值是在最近的数据更改之前存在的值。这些值是检查数据库中相应值是否已更改所必需的，从而产生了应该持久保存值的冲突：放入`RowSet`对象的新值或其他人放入数据库的新值。）

WebRowSet XML Schema本身就是一个XML文档，它定义了表示`WebRowSet`对象的XML文档将包含的内容以及必须呈现它的格式。发件人和收件人都使用此架构，因为它告诉发件人如何编写XML文档（表示`WebRowSet`对象）和收件人如何解析XML文档。由于实际写作和阅读是通过方法的实现内部完成`writeXml`和`readXml`，你，作为用户，不需要了解什么是WebRowSet的XML架构文件内。

XML文档包含分层结构中的元素和子元素。以下是描述`WebRowSet`对象的XML文档中的三个主要元素：

- [属性](https://docs.oracle.com/javase/tutorial/jdbc/basics/webrowset.html#properties-webrowset)
- [元数据](https://docs.oracle.com/javase/tutorial/jdbc/basics/webrowset.html#metadata-webrowset)
- [数据](https://docs.oracle.com/javase/tutorial/jdbc/basics/webrowset.html#data-webrowset)

元素标签用信号通知元素的开头和结尾。例如，`<properties>`标签用信号通知属性元素的开头，`</properties>`标签用信号表示它的结束。该`<map/>`标签是说，地图子元件（在属性元件子元件中的一个）没有被分配一个值的简略方式。以下示例XML文档使用间距和缩进来使其更易于阅读，但这些不在实际的XML文档中使用，其中间距并不意味着什么。

接下来的三个部分将向您展示`WebRowSet` `priceList`在示例中创建的对象的三个主要元素[`WebRowSetSample.java`](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)。

### 属性

`writeXml`在`priceList`对象上调用方法将生成描述的XML文档`priceList`。此XML文档的属性部分如下所示：

```xml
<properties>
  <command>
    select COF_NAME, PRICE from COFFEES
  </command>
  <concurrency>1008</concurrency>
  <datasource><null/></datasource>
  <escape-processing>true</escape-processing>
  <fetch-direction>1000</fetch-direction>
  <fetch-size>0</fetch-size>
  <isolation-level>2</isolation-level>
  <key-columns>
    <column>1</column>
  </key-columns>
  <map>
  </map>
  <max-field-size>0</max-field-size>
  <max-rows>0</max-rows>
  <query-timeout>0</query-timeout>
  <read-only>true</read-only>
  <rowset-type>
    ResultSet.TYPE_SCROLL_INSENSITIVE
  </rowset-type>
  <show-deleted>false</show-deleted>
  <table-name>COFFEES</table-name>
  <url>jdbc:mysql://localhost:3306/testdb</url>
  <sync-provider>
    <sync-provider-name>
      com.sun.rowset.providers.RIOptimisticProvider
    </sync-provider-name>
    <sync-provider-vendor>
      Sun Microsystems Inc.
    </sync-provider-vendor>
    <sync-provider-version>
      1.0
    </sync-provider-version>
    <sync-provider-grade>
      2
    </sync-provider-grade>
    <data-source-lock>1</data-source-lock>
  </sync-provider>
</properties>
```

请注意，某些属性没有任何价值。例如，`datasource`属性用`<datasource/>`标记表示，这是一种简写的说法`<datasource></datasource>`。由于`url`属性已设置，因此未给出任何值。建立的任何连接都将使用此JDBC URL完成，因此不需要`DataSource`设置任何对象。此外，未列出`username`和`password`属性，因为它们必须保密。

### 元数据

描述`WebRowSet`对象的XML文档的元数据部分包含有关该对象中的列的信息`WebRowSet`。以下显示了此部分对于该`WebRowSet`对象的外观`priceList`。因为该`priceList`对象有两列，所以描述它的XML文档有两个`<column-definition>`元素。每个`<column-definition>`元素都有子元素，提供有关所描述列的信息。

```xml
<metadata>
  <column-count>2</column-count>
  <column-definition>
    <column-index>1</column-index>
    <auto-increment>false</auto-increment>
    <case-sensitive>false</case-sensitive>
    <currency>false</currency>
    <nullable>0</nullable>
    <signed>false</signed>
    <searchable>true</searchable>
    <column-display-size>
      32
    </column-display-size>
    <column-label>COF_NAME</column-label>
    <column-name>COF_NAME</column-name>
    <schema-name></schema-name>
    <column-precision>32</column-precision>
    <column-scale>0</column-scale>
    <table-name>coffees</table-name>
    <catalog-name>testdb</catalog-name>
    <column-type>12</column-type>
    <column-type-name>
      VARCHAR
    </column-type-name>
  </column-definition>
  <column-definition>
    <column-index>2</column-index>
    <auto-increment>false</auto-increment>
    <case-sensitive>true</case-sensitive>
    <currency>false</currency>
    <nullable>0</nullable>
    <signed>true</signed>
    <searchable>true</searchable>
    <column-display-size>
      12
    </column-display-size>
    <column-label>PRICE</column-label>
    <column-name>PRICE</column-name>
    <schema-name></schema-name>
    <column-precision>10</column-precision>
    <column-scale>2</column-scale>
    <table-name>coffees</table-name>
    <catalog-name>testdb</catalog-name>
    <column-type>3</column-type>
    <column-type-name>
      DECIMAL
    </column-type-name>
  </column-definition>
</metadata>
```

从此元数据部分，您可以看到每行中有两列。第一列是`COF_NAME`，它包含类型的值`VARCHAR`。第二列是`PRICE`，包含类型的值`REAL`，依此类推。请注意，列类型是数据源中使用的数据类型，而不是Java编程语言中的类型。要获取或更新`COF_NAME`列中的值，请使用方法`getString`或`updateString`，并且驱动程序会像往常一样转换为`VARCHAR`类型。

### 数据

数据部分给出了`WebRowSet`对象每行中每列的值。如果已填充`priceList`对象而未对其进行任何更改，则XML文档的数据元素将如下所示。在下一节中，您将看到在修改`priceList`对象中的数据时XML文档如何更改。

对于每一行都有一个`<currentRow>`元素，因为`priceList`有两列，每个`<currentRow>`元素包含两个`<columnValue>`元素。

```xml
<data>
  <currentRow>
    <columnValue>Colombian</columnValue>
    <columnValue>7.99</columnValue>
  </currentRow>
  <currentRow>
    <columnValue>
      Colombian_Decaf
    </columnValue>
    <columnValue>8.99</columnValue>
  </currentRow>
  <currentRow>
    <columnValue>Espresso</columnValue>
    <columnValue>9.99</columnValue>
  </currentRow>
  <currentRow>
    <columnValue>French_Roast</columnValue>
    <columnValue>8.99</columnValue>
  </currentRow>
  <currentRow>
    <columnValue>French_Roast_Decaf</columnValue>
    <columnValue>9.99</columnValue>
  </currentRow>
</data>
```

## 对WebRowSet对象进行更改

您可以像`WebRowSet`对象一样对对象进行更改`CachedRowSet`。`CachedRowSet`但是，与对象不同，`WebRowSet`对象会跟踪更新，插入和删除，以便该`writeXml`方法可以写入当前值和原始值。以下三个部分演示了对数据的更改，并显示了`WebRowSet`每次更改后描述对象的XML文档的样子。您不必对XML文档做任何事情; 对它的任何更改都是自动进行的，就像编写和读取XML文档一样。

### 插入行

如果Coffee Break连锁店的所有者想要在价目表中添加新咖啡，则代码可能如下所示：

```java
priceList.absolute(3);
priceList.moveToInsertRow();
priceList.updateString(COF_NAME, "Kona");
priceList.updateFloat(PRICE, 8.99f);
priceList.insertRow();
priceList.moveToCurrentRow();
```

在参考实现中，紧接在当前行之后进行插入。在前面的代码片段中，当前行是第三行，因此新行将在第三行之后添加并成为新的第四行。为了反映这种插入，XML文档将在`<insertRow>`元素中的第三个`<currentRow>`元素之后添加以下`<data>`元素。

该`<insertRow>`元素看起来类似于以下内容。

```xml
<insertRow>
  <columnValue>Kona</columnValue>
  <columnValue>8.99</columnValue>
</insertRow>
```

## 删除行

店主决定Espresso销售不足，应该从咖啡休息店出售的咖啡中取出。因此，所有者想要从价目表中删除Espresso。Espresso位于`priceList`对象的第三行，因此以下代码行将其删除：

```java
priceList.absolute(3); priceList.deleteRow();
```

以下`<deleteRow>`元素将出现在XML文档的数据部分中的第二行之后，表示已删除第三行。

```xml
<deleteRow>
  <columnValue>Espresso</columnValue>
  <columnValue>9.99</columnValue>
</deleteRow>
```

## 修改行

店主进一步决定哥伦比亚咖啡的价格过于昂贵，并希望将其降低至每磅6.99美元。以下代码将哥伦比亚咖啡的新价格设定为每磅6.99美元，这是第一排的价格：

```java
priceList.first();
priceList.updateFloat(PRICE, 6.99);
```

XML文档将在`<updateRow>`提供新值的元素中反映此更改。第一列的值没有改变，因此`<updateValue>`只有第二列的元素：

```xml
<currentRow>
  <columnValue>Colombian</columnValue>
  <columnValue>7.99</columnValue>
  <updateRow>6.99</updateRow>
</currentRow>
```

此时，通过插入行，删除行以及修改行，`priceList`对象的XML文档将如下所示：

```xml
<data>
  <insertRow>
    <columnValue>Kona</columnValue>
    <columnValue>8.99</columnValue>
  </insertRow>
  <currentRow>
    <columnValue>Colombian</columnValue>
    <columnValue>7.99</columnValue>
    <updateRow>6.99</updateRow>
  </currentRow>
  <currentRow>
    <columnValue>
      Colombian_Decaf
    </columnValue>
    <columnValue>8.99</columnValue>
  </currentRow>
  <deleteRow>
    <columnValue>Espresso</columnValue>
    <columnValue>9.99</columnValue>
  </deleteRow>
  <currentRow>
    <columnValue>French_Roast</columnValue>
    <columnValue>8.99</columnValue>
  </currentRow>
  <currentRow>
    <columnValue>
      French_Roast_Decaf
    </columnValue>
    <columnValue>9.99</columnValue>
  </currentRow>
</data>
```

## WebRowSet代码示例

该示例`WebRowSetSample.java`演示了此页面上描述的所有功能。