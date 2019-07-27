# 使用SQLXML对象

该`Connection`接口为`SQLXML`使用该方法创建对象提供支持`createSQLXML`。创建的对象不包含任何数据。数据可以通过调用被添加到对象`setString`，`setBinaryStream`，`setCharacterStream`或`setResult`在方法上`SQLXML`接口。

涵盖以下主题：

- [创建SQLXML对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlxml.html#creating_sqlxml)
- [在ResultSet中检索SQLXML值](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlxml.html#retrieving_sqlxml)
- [访问SQLXML对象数据](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlxml.html#accessing_sqlxml)
- [存储SQLXML对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlxml.html#storing_sqlxml)
- [初始化SQLXML对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlxml.html#initializing_sqlxml)
- [发布SQLXML资源](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlxml.html#releasing_sqlxml)
- [示例代码](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlxml.html#sample_code)

## 创建SQLXML对象

在以下摘录中，该方法`Connection.createSQLXML`用于创建空`SQLXML`对象。该`SQLXML.setString`方法用于将数据写入`SQLXML`已创建的对象。

```java
Connection con = DriverManager.getConnection(url, props);
SQLXML xmlVal = con.createSQLXML();
xmlVal.setString(val);
```

## 在ResultSet中检索SQLXML值

该`SQLXML`数据类型被类似地处理，以更原始的内置类型。甲`SQLXML`值可以通过调用被检索`getSQLXML`的方法`ResultSet`或`CallableStatement`接口。

例如，以下摘录`SQLXML`从`ResultSet` *rs*的第一列检索值：

```java
SQLXML xmlVar = rs.getSQLXML(1);
```

`SQLXML`除非`free`调用它们的方法，否则对象至少在创建它们的事务的持续时间内保持有效。

## 访问SQLXML对象数据

该`SQLXML`接口提供`getString`，`getBinaryStream`，`getCharacterStream`和`getSource`方法来访问其内部的内容。以下摘录`SQLXML`使用以下`getString`方法检索对象的内容：

```java
SQLXML xmlVal= rs.getSQLXML(1);
String val = xmlVal.getString();
```

所述`getBinaryStream`或`getCharacterStream`方法可用于获得`InputStream`或`Reader`可以直接传递到XML解析器对象。以下摘录`InputStream`从`SQLXML`Object 获取对象，然后使用DOM（文档对象模型）解析器处理流：

```java
SQLXML sqlxml = rs.getSQLXML(column);
InputStream binaryStream = sqlxml.getBinaryStream();
DocumentBuilder parser = 
    DocumentBuilderFactory.newInstance().newDocumentBuilder();
Document result = parser.parse(binaryStream);
```

该`getSource`方法返回一个`javax.xml.transform.Source`对象。源用作XML解析器和XSLT转换器的输入。

以下摘录`SQLXML`使用`SAXSource`通过调用`getSource`方法返回的对象检索和解析对象中的数据：

```java
SQLXML xmlVal= rs.getSQLXML(1);
SAXSource saxSource = sqlxml.getSource(SAXSource.class);
XMLReader xmlReader = saxSource.getXMLReader();
xmlReader.setContentHandler(myHandler);
xmlReader.parse(saxSource.getInputSource());
```

## 存储SQLXML对象

甲`SQLXML`对象可以作为输入参数的传递`PreparedStatement`就像其它数据类型的对象。该方法使用对象`setSQLXML`设置指定`PreparedStatement`参数`SQLXML`。

在以下摘录中，`authorData`是`java.sql.SQLXML`先前已初始化其数据的接口的实例。

```java
PreparedStatement pstmt = conn.prepareStatement("INSERT INTO bio " +
                              "(xmlData, authId) VALUES (?, ?)");
pstmt.setSQLXML(1, authorData);
pstmt.setInt(2, authorId);
```

该`updateSQLXML`方法可用于更新可更新结果集中的列值。

如果`java.xml.transform.Result`，`Writer`或`OutputStream`为对象`SQLXML`对象没有被调用之前关闭`setSQLXML`或者`updateSQLXML`，一个`SQLException`将被抛出。

## 初始化SQLXML对象

所述`SQLXML`接口提供的方法中`setString`，`setBinaryStream`，`setCharacterStream`，或`setResult`初始化用于该内容`SQLXML`已经通过调用创建的对象`Connection.createSQLXML`的方法。

以下摘录使用该方法`setResult`返回一个`SAXResult`对象以填充新创建的`SQLXML`对象：

```java
SQLXML sqlxml = con.createSQLXML();
SAXResult saxResult = sqlxml.setResult(SAXResult.class);
ContentHandler contentHandler = saxResult.getXMLReader().getContentHandler();
contentHandler.startDocument();
    
// set the XML elements and
// attributes into the result
contentHandler.endDocument();
```

以下摘录使用该`setCharacterStream`方法获取`java.io.Writer`对象以初始化`SQLXML`对象：

```java
SQLXML sqlxml = con.createSQLXML();
Writer out= sqlxml.setCharacterStream();
BufferedReader in = new BufferedReader(new FileReader("xml/foo.xml"));
String line = null;
while((line = in.readLine() != null) {
    out.write(line);
}
```

类似地，该`SQLXML` `setString`方法可用于初始化`SQLXML`对象。

如果试图调用`setString`，`setBinaryStream`，`setCharacterStream`，和`setResult`一对方法`SQLXML`以前已初始化的对象，`SQLException`将被抛出。如果有多个调用方法`setBinaryStream`，`setCharacterStream`以及`setResult`发生在同一个`SQLXML`对象，一个`SQLException`被关上，先前所返回`javax.xml.transform.Result`，`Writer`或`OutputStream`对象不会受到影响。

## 发布SQLXML资源

`SQLXML`对象至少在创建它们的事务的持续时间内保持有效。这可能导致应用程序在长时间运行的事务期间耗尽资源。应用程序可以`SQLXML`通过调用其`free`方法来释放资源。

在下面的摘录中，`method SQLXML.free`调用它来释放为先前创建的`SQLXML`对象保留的资源。

```java
SQLXML xmlVar = con.createSQLXML();
xmlVar.setString(val);
xmlVar.free();
```

## 示例代码

MySQL和Java DB及其各自的JDBC驱动程序不完全支持`SQLXML`JDBC数据类型，如本节所述。但是，该示例`RSSFeedsTable`演示了如何使用MySQL和Java DB处理XML数据。

The Coffee Break的所有者关注来自各种网站的多个RSS源，其中包括餐馆和饮料行业的新闻。RSS（Really Simple Syndication或Rich Site Summary）源是一个XML文档，其中包含一系列文章和相关元数据，例如每篇文章的发布日期和作者。所有者希望将这些RSS提要存储到数据库表中，包括来自The Coffee Break博客的RSS提要。

该文件`rss-the-coffee-break-blog.xml`是The Coffee Break博客的示例RSS源。

### 在MySQL中使用XML数据

该示例`RSSFeedsTable`在表中存储RSS提要，该提要`RSS_FEEDS`使用以下命令创建：

```sql
create table RSS_FEEDS
    (RSS_NAME varchar(32) NOT NULL,
    RSS_FEED_XML longtext NOT NULL,
    PRIMARY KEY (RSS_NAME));
```

MySQL不支持XML数据类型。相反，此示例将XML数据存储在类型的列中`LONGTEXT`，该列是`CLOB`SQL数据类型。MySQL有四种`CLOB`数据类型; 的`LONGTEXT`数据类型包含的字符的4个中最大的量。

该方法`RSSFeedsTable.addRSSFeed`将RSS提要添加到`RSS_FEEDS`表中。此方法的第一个语句将RSS提要（由此示例中的XML文件表示）转换为类型的对象`org.w3c.dom.Document`，该对象表示DOM（文档对象模型）文档。此类以及包`javax.xml`中包含的类和接口包含使您能够操作XML数据内容的方法。例如，以下语句使用XPath表达式从`Document`对象中检索RSS提要的标题：

```java
Node titleElement =
    (Node)xPath.evaluate("/rss/channel/title[1]",
        doc, XPathConstants.NODE);
```

XPath表达式`/rss/channel/title[1]`检索第一个`<title>`元素的内容。对于文件`rss-the-coffee-break-blog.xml`，这是字符串`The Coffee Break Blog`。

以下语句将RSS提要添加到表中`RSS_FEEDS`：

```java
// For databases that support the SQLXML
// data type, this creates a
// SQLXML object from
// org.w3c.dom.Document.

System.out.println("Adding XML file " + fileName);
String insertRowQuery =
    "insert into RSS_FEEDS " +
    "(RSS_NAME, RSS_FEED_XML) values " +
    "(?, ?)";
insertRow = con.prepareStatement(insertRowQuery);
insertRow.setString(1, titleString);

System.out.println("Creating SQLXML object with MySQL");
rssData = con.createSQLXML();
System.out.println("Creating DOMResult object");
DOMResult dom = (DOMResult)rssData.setResult(DOMResult.class);
dom.setNode(doc);

insertRow.setSQLXML(2, rssData);
System.out.println("Running executeUpdate()");
insertRow.executeUpdate();
```

该`RSSFeedsTable.viewTable`方法检索的内容`RSS_FEEDS`。对于每一行，该方法创建一个`org.w3c.dom.Document`名为的类型的对象`doc`，用于在列中存储XML内容`RSS_FEED_XML`。该方法检索XML内容并将其存储在`SQLXML`named 类型的对象中`rssFeedXML`。`rssFeedXML`解析内容并将其存储在`doc`对象中。

### 在Java DB中使用XML数据

**注意**：有关在[*Java DB中*](http://docs.oracle.com/javadb/index_jdk8.html)使用XML数据的更多信息，请参阅“ [*Java数据库开发人员指南*](http://docs.oracle.com/javadb/index_jdk8.html) ”中的“XML数据类型和运算符”部分。

该示例`RSSFeedsTable`在表中存储RSS提要，该提要`RSS_FEEDS`使用以下命令创建：

```sql
create table RSS_FEEDS
    (RSS_NAME varchar(32) NOT NULL,
    RSS_FEED_XML xml NOT NULL,
    PRIMARY KEY (RSS_NAME));
```

Java DB支持XML数据类型，但它不支持`SQLXML`JDBC数据类型。因此，您必须将任何XML数据转换为字符格式，然后使用Java DB运算符`XMLPARSE`将其转换为XML数据类型。

该`RSSFeedsTable.addRSSFeed`方法将RSS提要添加到`RSS_FEEDS`表中。此方法的第一个语句将RSS提要（由此示例中的XML文件表示）转换为类型的对象`org.w3c.dom.Document`。这[在MySQL中使用XML数据](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlxml.html#working-with-xml-data-in-mysql)一节中进行了描述。

该`RSSFeedsTable.addRSSFeed`方法使用该方法将RSS提要转换为`String`对象`JDBCTutorialUtilities.convertDocumentToString`。

Java DB有一个名为的运算符`XMLPARSE`，它将字符串表示形式解析为Java DB XML值，这由以下摘录说明：

```java
String insertRowQuery =
    "insert into RSS_FEEDS " +
    "(RSS_NAME, RSS_FEED_XML) values " +
    "(?, xmlparse(document cast " +
    "(? as clob) preserve whitespace))";
```

该`XMLPARSE`运营商需要将转换为XML文档的字符表示成Java DB的识别字符串数据类型。在此示例中，它将其转换为`CLOB`数据类型。有关Apache Xalan和Java DB要求的更多信息，请参阅“ [入门”](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)和Java DB文档。

该方法`RSSFeedsTable.viewTable`检索的内容`RSS_FEEDS`。由于Java DB不支持JDBC数据类型`SQLXML`，因此必须将XML内容检索为字符串。Java DB有一个名为的运算符`XMLSERIALIZE`，它将XML类型转换为字符类型：

```java
String query =
    "select RSS_NAME, " +
    "xmlserialize " +
    "(RSS_FEED_XML as clob) " +
    "from RSS_FEEDS";
```

与`XMLPARSE`运算符一样，`XMLSERIALIZE`运算符要求在您的Java类路径中列出Apache Xalan。