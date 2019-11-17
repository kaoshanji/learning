# 19.7 参数和数据值处理的常见问题

Spring Framework JDBC提供的不同方法中存在参数和数据值的常见问题。

### 19.7.1提供参数的SQL类型信息

通常，Spring根据传入的参数类型确定参数的SQL类型。可以显式提供设置参数值时要使用的SQL类型。有时需要正确设置NULL值。

您可以通过多种方式提供SQL类型信息：

- 许多更新和查询方法`JdbcTemplate`采用`int`数组形式的附加参数。此数组用于使用类中的常量值指示相应参数的SQL类型`java.sql.Types`。为每个参数提供一个条目。
- 您可以使用`SqlParameterValue`该类来包装需要此附加信息的参数值。为每个值创建一个新实例，并在构造函数中传入SQL类型和参数值。您还可以为数值提供可选的缩放参数。
- 对于使用命名参数的方法，请使用`SqlParameterSource`类 `BeanPropertySqlParameterSource`或`MapSqlParameterSource`。它们都具有为任何命名参数值注册SQL类型的方法。

### 19.7.2处理BLOB和CLOB对象

您可以在数据库中存储图像，其他二进制数据和大块文本。这些大对象称为二进制数据的BLOB（二进制大对象）和字符数据的CLOB（字符大对象）。在Spring中，您可以`JdbcTemplate`直接使用这些大对象，也可以在使用RDBMS Objects和`SimpleJdbc`类提供的更高抽象时处理这些大对象。所有这些方法都使用`LobHandler`接口的实现来实际管理LOB（大对象）数据。在 `LobHandler`提供对`LobCreator`类，通过`getLobCreator`方法，用于创建新的对象LOB要插入。

在`LobCreator/LobHandler`提供了LOB输入和输出以下支持：

- BLOB
  - `byte[]` -  `getBlobAsBytes`和`setBlobAsBytes`
  - `InputStream` -  `getBlobAsBinaryStream`和`setBlobAsBinaryStream`
- CLOB
  - `String` -  `getClobAsString`和`setClobAsString`
  - `InputStream` -  `getClobAsAsciiStream`和`setClobAsAsciiStream`
  - `Reader` -  `getClobAsCharacterStream`和`setClobAsCharacterStream`

下一个示例显示如何创建和插入BLOB。稍后您将看到如何从数据库中读取它。

这个例子使用了一个`JdbcTemplate`和一个实现 `AbstractLobCreatingPreparedStatementCallback`。它实现了一种方法 `setValues`。此方法提供了一个`LobCreator`用于在SQL插入语句中设置LOB列的值的方法。

对于此示例，我们假设有一个变量，`lobHandler`已经设置为a的实例`DefaultLobHandler`。您通常通过依赖注入设置此值。

```java
final File blobIn = new File("spring2004.jpg");
final InputStream blobIs = new FileInputStream(blobIn);
final File clobIn = new File("large.txt");
final InputStream clobIs = new FileInputStream(clobIn);
final InputStreamReader clobReader = new InputStreamReader(clobIs);

jdbcTemplate.execute(
    "INSERT INTO lob_table (id, a_clob, a_blob) VALUES (?, ?, ?)",
    new AbstractLobCreatingPreparedStatementCallback(lobHandler) { 
        protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
            ps.setLong(1, 1L);
            lobCreator.setClobAsCharacterStream(ps, 2, clobReader, (int)clobIn.length()); 
            lobCreator.setBlobAsBinaryStream(ps, 3, blobIs, (int)blobIn.length()); 
        }
    }
);

blobIs.close();
clobReader.close();
```

`lobHandler`在这个例子中传递的是一个平原`DefaultLobHandler`

使用该方法`setClobAsCharacterStream`，传入CLOB的内容

使用该方法`setBlobAsBinaryStream`，传入BLOB的内容

如果您调用`setBlobAsBinaryStream`，`setClobAsAsciiStream`或 `setClobAsCharacterStream`在方法`LobCreator`从返回的`DefaultLobHandler.getLobCreator()`，您可以选择指定负值 `contentLength`的说法。如果指定的内容长度为负， `DefaultLobHandler`则将使用set-stream方法的JDBC 4.0变体而不使用length参数; 否则，它会将指定的长度传递给驱动程序。

请参阅正在使用的JDBC驱动程序的文档，以验证是否支持流式传输LOB而不提供内容长度。

现在是时候从数据库中读取LOB数据了。同样，您使用`JdbcTemplate` 具有相同实例变量的`lobHandler`a和对a的引用`DefaultLobHandler`。

```java
List<Map<String, Object>> l = jdbcTemplate.query("select id, a_clob, a_blob from lob_table",
    new RowMapper<Map<String, Object>>() {
        public Map<String, Object> mapRow(ResultSet rs, int i) throws SQLException {
            Map<String, Object> results = new HashMap<String, Object>();
            String clobText = lobHandler.getClobAsString(rs, "a_clob"); 
results.put("CLOB", clobText); byte[] blobBytes = lobHandler.getBlobAsBytes(rs, "a_blob"); 
results.put("BLOB", blobBytes); return results; } });
```

使用该方法`getClobAsString`，检索CLOB的内容

使用该方法`getBlobAsBytes`，检索BLOB的内容

### 19.7.3传入IN子句的值列表

SQL标准允许基于包含变量值列表的表达式来选择行。一个典型的例子是`select * from T_ACTOR where id in (1, 2, 3)`。JDBC标准对预准备语句不直接支持此变量列表; 您不能声明可变数量的占位符。您需要准备好所需占位符数量的多种变体，或者一旦知道需要多少占位符，就需要动态生成SQL字符串。在提供的命名参数支持`NamedParameterJdbcTemplate`，并`JdbcTemplate`采取了后一种方式。将值作为`java.util.List`原始对象传递。此列表将用于插入所需的占位符，并在语句执行期间传入值。

传递许多值时要小心。JDBC标准不保证您可以为`in`表达式列表使用100个以上的值。各种数据库超过此数量，但它们通常对允许的值有多少硬性限制。Oracle的限制是1000。

除了值列表中的原始值之外，您还可以创建一个`java.util.List` 对象数组。此列表将支持为`in` 子句定义的多个表达式，例如`select * from T_ACTOR where (id, last_name) in ((1, 'Johnson'), (2, 'Harrop'\))`。这当然要求您的数据库支持此语法。

### 19.7.4处理存储过程调用的复杂类型

调用存储过程时，有时可以使用特定于数据库的复杂类型。为了适应这些类型，Spring提供了一个`SqlReturnType`用于在从存储过程调用返回`SqlTypeValue`它们以及将它们作为参数传递给存储过程时处理它们的方法。

以下是返回`STRUCT`用户声明类型的Oracle 对象的值的示例`ITEM_TYPE`。该`SqlReturnType`接口具有`getTypeValue`必须实现的单个方法 。此接口用作声明的一部分`SqlOutParameter`。

```java
public class TestItemStoredProcedure extends StoredProcedure {

    public TestItemStoredProcedure(DataSource dataSource) {
        ...
        declareParameter(new SqlOutParameter("item", OracleTypes.STRUCT, "ITEM_TYPE",
            new SqlReturnType() {
                public Object getTypeValue(CallableStatement cs, int colIndx, int sqlType, String typeName) throws SQLException {
                    STRUCT struct = (STRUCT) cs.getObject(colIndx);
                    Object[] attr = struct.getAttributes();
                    TestItem item = new TestItem();
                    item.setId(((Number) attr[0]).longValue());
                    item.setDescription((String) attr[1]);
                    item.setExpirationDate((java.util.Date) attr[2]);
                    return item;
                }
            }));
        ...
    }
```

您可以使用`SqlTypeValue`它将Java对象的值传递`TestItem`到存储过程中。该`SqlTypeValue`接口有一个`createTypeValue`必须实现的名为的方法 。传入活动连接，您可以使用它来创建特定于数据库的对象，例如`StructDescriptor`s，如以下示例所示，或者`ArrayDescriptor`s。

```java
final TestItem testItem = new TestItem(123L, "A test item",
        new SimpleDateFormat("yyyy-M-d").parse("2010-12-31"));

SqlTypeValue value = new AbstractSqlTypeValue() {
    protected Object createTypeValue(Connection conn, int sqlType, String typeName) throws SQLException {
        StructDescriptor itemDescriptor = new StructDescriptor(typeName, conn);
        Struct item = new STRUCT(itemDescriptor, conn,
        new Object[] {
            testItem.getId(),
            testItem.getDescription(),
            new java.sql.Date(testItem.getExpirationDate().getTime())
        });
        return item;
    }
};
```

这个`SqlTypeValue`现在可以加入到含有用于存储过程的执行呼叫的输入参数的地图。

另一个用途`SqlTypeValue`是将值数组传递给Oracle存储过程。Oracle有自己的内部`ARRAY`类，在这种情况下必须使用它，您可以使用它`SqlTypeValue`来创建Oracle的实例`ARRAY`并使用Java中的值填充它`ARRAY`。

```java
final Long[] ids = new Long[] {1L, 2L};

SqlTypeValue value = new AbstractSqlTypeValue() {
    protected Object createTypeValue(Connection conn, int sqlType, String typeName) throws SQLException {
        ArrayDescriptor arrayDescriptor = new ArrayDescriptor(typeName, conn);
        ARRAY idArray = new ARRAY(arrayDescriptor, conn, ids);
        return idArray;
    }
};
```

