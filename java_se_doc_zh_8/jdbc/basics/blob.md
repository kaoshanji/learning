# 使用大对象

的一个重要特点`Blob`，`Clob`以及`NClob`Java对象是，你可以操纵它们，而不必把所有的数据从数据库服务器到客户端计算机。某些实现表示这些类型的实例，其中包含实例所代表的数据库中对象的定位符（逻辑指针）。由于a `BLOB`，`CLOB`或`NCLOB`SQL对象可能非常大，因此使用定位器可以显着提高性能。但是，其他实现完全实现了客户端计算机上的大对象。

如果你想带上的数据`BLOB`，`CLOB`或`NCLOB`SQL值到客户端计算机，在使用方法`Blob`，`Clob`以及`NClob`提供为了这个目的，Java接口。这些大对象类型对象将它们表示的对象的数据实现为流。

涵盖以下主题：

- [将大对象类型对象添加到数据库](https://docs.oracle.com/javase/tutorial/jdbc/basics/blob.html#add_lob)
- [检索CLOB值](https://docs.oracle.com/javase/tutorial/jdbc/basics/blob.html#retrieve_clob)
- [添加和检索BLOB对象](https://docs.oracle.com/javase/tutorial/jdbc/basics/blob.html#add_retrieve_blob)
- [释放大对象所持有的资源](https://docs.oracle.com/javase/tutorial/jdbc/basics/blob.html#release_large_objects)

## 将大对象类型对象添加到数据库

以下摘录`ClobSample.addRowToCoffeeDescriptions`将`CLOB`SQL值添加到表中`COFFEE_DESCRIPTIONS`。该`Clob`Java对象`myClob`包含指定的文件的内容`fileName`。

```java
public void addRowToCoffeeDescriptions(
    String coffeeName, String fileName)
    throws SQLException {

    PreparedStatement pstmt = null;
    try {
        Clob myClob = this.con.createClob();
        Writer clobWriter = myClob.setCharacterStream(1);
        String str = this.readFile(fileName, clobWriter);
        System.out.println("Wrote the following: " +
            clobWriter.toString());

        if (this.settings.dbms.equals("mysql")) {
            System.out.println(
                "MySQL, setting String in Clob " +
                "object with setString method");
            myClob.setString(1, str);
        }
        System.out.println("Length of Clob: " + myClob.length());

        String sql = "INSERT INTO COFFEE_DESCRIPTIONS " +
                     "VALUES(?,?)";

        pstmt = this.con.prepareStatement(sql);
        pstmt.setString(1, coffeeName);
        pstmt.setClob(2, myClob);
        pstmt.executeUpdate();
    } catch (SQLException sqlex) {
        JDBCTutorialUtilities.printSQLException(sqlex);
    } catch (Exception ex) {
      System.out.println("Unexpected exception: " + ex.toString());
    } finally {
        if (pstmt != null)pstmt.close();
    }
}
```

以下行创建一个`Clob`Java对象：

```java
Clob myClob = this.con.createClob();
```

以下行检索用于将字符流写入Java对象的流（在本例中为`Writer`名为的对象`clobWriter`）。该方法写入此字符流; 流来自于指定的文件。method参数指示对象将开始在值的开头写入字符流：`Clob``myClob``ClobSample.readFile``String``fileName``1``Writer``Clob`

```java
Writer clobWriter = myClob.setCharacterStream(1);
```

该`ClobSample.readFile`方法逐行读取文件，`fileName`并将其写入由以下指定的`Writer`对象`writerArg`：

```java
private String readFile(String fileName, Writer writerArg)
        throws FileNotFoundException, IOException {

    BufferedReader br = new BufferedReader(new FileReader(fileName));
    String nextLine = "";
    StringBuffer sb = new StringBuffer();
    while ((nextLine = br.readLine()) != null) {
        System.out.println("Writing: " + nextLine);
        writerArg.write(nextLine);
        sb.append(nextLine);
    }
    // Convert the content into to a string
    String clobData = sb.toString();

    // Return the data.
    return clobData;
}
```

以下摘录创建一个`PreparedStatement`对象`pstmt`是插入`Clob`Java对象`myClob`为`COFFEE_DESCRIPTIONS`：

```java
PreparedStatement pstmt = null;
// ...
String sql = "INSERT INTO COFFEE_DESCRIPTIONS VALUES(?,?)";
pstmt = this.con.prepareStatement(sql);
pstmt.setString(1, coffeeName);
pstmt.setClob(2, myClob);
pstmt.executeUpdate();
```

## 检索CLOB值

该方法从列的值等于参数指定的值的行中`ClobSample.retrieveExcerpt`检索`CLOB`存储在`COF_DESC`列中的SQL值：`COFFEE_DESCRIPTIONS``COF_NAME``String``coffeeName`

```java
public String retrieveExcerpt(String coffeeName, int numChar)
    throws SQLException {

    String description = null;
    Clob myClob = null;
    PreparedStatement pstmt = null;

    try {
        String sql =
            "select COF_DESC " +
            "from COFFEE_DESCRIPTIONS " +
            "where COF_NAME = ?";

        pstmt = this.con.prepareStatement(sql);
        pstmt.setString(1, coffeeName);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            myClob = rs.getClob(1);
            System.out.println("Length of retrieved Clob: " +
                myClob.length());
        }
        description = myClob.getSubString(1, numChar);
    } catch (SQLException sqlex) {
        JDBCTutorialUtilities.printSQLException(sqlex);
    } catch (Exception ex) {
        System.out.println("Unexpected exception: " + ex.toString());
    } finally {
        if (pstmt != null) pstmt.close();
    }
    return description;
}
```

以下行`Clob`从`ResultSet`对象中检索Java值`rs`：

```java
myClob = rs.getClob(1);
```

以下行从`myClob`对象中检索子字符串。子字符串从值的第一个字符开始，`myClob`并且最多包含指定的连续字符数`numChar`，其中`numChar`是整数。

```java
description = myClob.getSubString(1, numChar);
```

## 添加和检索BLOB对象

添加和检索`BLOB`SQL对象与添加和检索SQL对象类似`CLOB`。使用该`Blob.setBinaryStream`方法检索`OutputStream`对象以写入Java对象（称为方法）表示的`BLOB`SQL值`Blob`。

## 释放大对象所持有的资源

`Blob`，`Clob`和`NClob`Java对象至少在创建它们的事务持续时间内保持有效。这可能导致应用程序在长时间运行的事务期间耗尽资源。应用程序可能会释放`Blob`，`Clob`以及`NClob`通过调用其资源`free`的方法。

在以下摘录中，`Clob.free`调用该方法以释放为先前创建的`Clob`对象保留的资源：

```java
Clob aClob = con.createClob();
int numWritten = aClob.setString(1, val);
aClob.free();
```

