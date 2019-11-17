# 使用数据链接对象

甲`DATALINK`值通过一个URL引用基础数据源之外的资源。URL，统一资源定位符，是指向万维网上的资源的指针。资源可以是文件或目录这样简单的东西，也可以是对更复杂的对象的引用，例如对数据库或搜索引擎的查询。

涵盖以下主题：

- [存储对外部数据的引用](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatalink.html#storing_datalink)
- [检索对外部数据的引用](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatalink.html#retrieving_datalink)

## 存储对外部数据的引用

使用此方法`PreparedStatement.setURL`指定`java.net.URL`预准备语句的对象。如果Java平台不支持所设置的URL类型，请使用该`setString`方法存储URL 。

例如，假设The Coffee Break的所有者想要在数据库表中存储重要URL列表。以下示例[`DatalinkSample.addURLRow`](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)将一行数据添加到表中`DATA_REPOSITORY`。该行由标识URL的字符串`DOCUMENT_NAME`和URL本身组成`URL`：

```java
public void addURLRow(String description, String url)
    throws SQLException {

    PreparedStatement pstmt = null;

    try {
        pstmt = this.con.prepareStatement(
            "INSERT INTO data_repository" +
            "(document_name,url) VALUES (?,?)");

        pstmt.setString(1, description);
        pstmt.setURL(2,new URL(url));
        pstmt.execute();
    } catch (SQLException sqlex) {
        JDBCTutorialUtilities.printSQLException(sqlex);
    } catch (Exception ex) {
        System.out.println("Unexpected exception");
        ex.printStackTrace();
    } finally {
        if (pstmt != null) {
            pstmt.close();
        }
    }
}
```

## 检索对外部数据的引用

使用该方法将对`ResultSet.getURL`外部数据的引用检索为`java.net.URL`对象。如果方法返回的URL类型`getObject`或`getURL`Java平台不支持，请`String`通过调用方法将URL检索为对象`getString`。

以下示例[`DatalinkSample.viewTable`](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)显示了表中存储的所有URL的内容`DATA_REPOSITORY`：

```java
public static void viewTable(Connection con, Proxy proxy)
    throws SQLException, IOException {

    Statement stmt = null;
    String query =
      "SELECT document_name, url " +
      "FROM data_repository";

    try {
        stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        if ( rs.next() )  {
            String documentName = null;
            java.net.URL url = null;

            documentName = rs.getString(1);

            // Retrieve the value as a URL object.
            url = rs.getURL(2);

            if (url != null) {

                // Retrieve the contents
                // from the URL
          
                URLConnection myURLConnection =
                    url.openConnection(proxy);
                BufferedReader bReader =
                    new BufferedReader(
                        new InputStreamReader(
                            myURLConnection.
                                getInputStream()));

                System.out.println("Document name: " + documentName);

                String pageContent = null;

                while ((pageContent = bReader.readLine()) != null ) {
                    // Print the URL contents
                    System.out.println(pageContent);
                }
            } else {
                System.out.println("URL is null");
            }
        }
    } catch (SQLException e) {
        JDBCTutorialUtilities.printSQLException(e);
    } catch(IOException ioEx) {
        System.out.println("IOException caught: " + ioEx.toString());
    } catch (Exception ex) {
        System.out.println("Unexpected exception");
        ex.printStackTrace();
    } finally {
        if (stmt != null) { stmt.close(); }
    }
}
```

该示例[`DatalinkSample`](https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html)将Oracle URL [http://www.oracle.com](http://www.oracle.com/)存储在表中`DATA_REPOSITORY`。之后，它显示存储在`DATA_REPOSITORY`其中的URL所引用的所有文档的内容，其中包括Oracle主页[http://www.oracle.com](http://www.oracle.com/)。

该示例`java.net.URL`使用以下语句从结果集中检索URL作为对象：

```java
url = rs.getURL(2);
```

该示例使用`URL`以下语句访问对象引用的数据：

```java
URLConnection myURLConnection = url.openConnection(proxy);
BufferedReader bReader = new BufferedReader(
    new InputStreamReader(
        myURLConnection.getInputStream()));

System.out.println("Document name: " + documentName);

String pageContent = null;

while ((pageContent = bReader.readLine()) != null ) {
    // Print the URL contents
    System.out.println(pageContent);
}
```

该方法`URLConnection.openConnection`不带参数，这意味着它`URLConnection`代表了与Internet的直接连接。如果需要代理服务器连接到Internet，则该`openConnection`方法接受`java.net.Proxy`对象作为参数。以下语句演示了如何使用服务器名称`www-proxy.example.com`和端口号创建HTTP代理`80`：

```java
Proxy myProxy;
InetSocketAddress myProxyServer;
myProxyServer = new InetSocketAddress("www-proxy.example.com", 80);
myProxy = new Proxy(Proxy.Type.HTTP, myProxyServer);
```

