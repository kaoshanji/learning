# 19.2 JDBC编程核心类和异常

### 19.2.1 JdbcTemplate

的`JdbcTemplate`类是在JDBC核心包的核心类。它处理资源的创建和释放，帮助您避免常见错误，例如忘记关闭连接。它执行核心JDBC工作流的基本任务，例如语句创建和执行，使应用程序代码提供SQL并提取结果。在`JdbcTemplate`完成SQL查询，更新语句和存储过程调用，执行了迭代`ResultSet`S和返回的参数值的提取。它还捕获JDBC异常并将它们转换为`org.springframework.dao`包中定义的通用的，信息更丰富的异常层次结构 。

当您使用`JdbcTemplate`代码时，您只需要实现回调接口，为它们提供明确定义的合同。该`PreparedStatementCreator` 回调接口创建给予了一份准备好的声明`Connection`该类提供，SQL和任何必要的参数。对于`CallableStatementCreator`创建可调用语句的接口也是如此 。所述 `RowCallbackHandler`接口从一个每一行中提取的值`ResultSet`。

的`JdbcTemplate`可以在DAO实现内通过直接实例化与使用`DataSource`参考，或在一个Spring IoC容器被配置和给予的DAO作为豆参考。

该`DataSource`应始终被配置为Spring IoC容器的bean。在第一种情况下，bean直接提供给服务; 在第二种情况下，它被给予准备好的模板。

此类发出的所有SQL都记录在与`DEBUG`模板实例的完全限定类名对应的类别下的级别（通常 `JdbcTemplate`，但如果您使用`JdbcTemplate`该类的自定义子类，则可能会有所不同 ）。

#### JdbcTemplate类用法的示例

本节提供了一些`JdbcTemplate`类使用示例。这些示例并非详尽列出了所有功能`JdbcTemplate`; 看到服务员javadocs。

##### 查询（SELECT）

这是一个简单的查询，用于获取关系中的行数：

```java
int rowCount = this.jdbcTemplate.queryForObject("select count(*) from t_actor", Integer.class);
```

使用绑定变量的简单查询：

```java
int countOfActorsNamedJoe = this.jdbcTemplate.queryForObject(
        "select count(*) from t_actor where first_name = ?", Integer.class, "Joe");
```

查询`String`：

```java
String lastName = this.jdbcTemplate.queryForObject(
        "select last_name from t_actor where id = ?",
        new Object[]{1212L}, String.class);
```

查询和填充*单个*域对象：

```java
Actor actor = this.jdbcTemplate.queryForObject(
        "select first_name, last_name from t_actor where id = ?",
        new Object[]{1212L},
        new RowMapper<Actor>() {
            public Actor mapRow(ResultSet rs, int rowNum) throws SQLException {
                Actor actor = new Actor();
                actor.setFirstName(rs.getString("first_name"));
                actor.setLastName(rs.getString("last_name"));
                return actor;
            }
        });
```

查询和填充许多域对象：

```java
List<Actor> actors = this.jdbcTemplate.query(
        "select first_name, last_name from t_actor",
        new RowMapper<Actor>() {
            public Actor mapRow(ResultSet rs, int rowNum) throws SQLException {
                Actor actor = new Actor();
                actor.setFirstName(rs.getString("first_name"));
                actor.setLastName(rs.getString("last_name"));
                return actor;
            }
        });
```

如果最后两个代码片段实际存在于同一个应用程序中，那么删除两个`RowMapper`匿名内部类中存在的重复并将它们提取到单个类（通常是`static`嵌套类）中是有意义的，然后可以通过它们引用它们。根据需要使用DAO方法。例如，最好编写最后一个代码片段，如下所示：

```java
public List<Actor> findAllActors() {
    return this.jdbcTemplate.query( "select first_name, last_name from t_actor", new ActorMapper());
}

private static final class ActorMapper implements RowMapper<Actor> {

    public Actor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Actor actor = new Actor();
        actor.setFirstName(rs.getString("first_name"));
        actor.setLastName(rs.getString("last_name"));
        return actor;
    }
}
```

##### 使用JdbcTemplate更新（INSERT / UPDATE / DELETE）

您可以使用该`update(..)`方法执行插入，更新和删除操作。参数值通常作为var args提供，或者作为对象数组提供。

```java
this.jdbcTemplate.update(
        "insert into t_actor (first_name, last_name) values (?, ?)",
        "Leonor", "Watling");
this.jdbcTemplate.update(
        "update t_actor set last_name = ? where id = ?",
        "Banjo", 5276L);
this.jdbcTemplate.update(
        "delete from actor where id = ?",
        Long.valueOf(actorId));
```

##### 其他JdbcTemplate操作

您可以使用该`execute(..)`方法执行任意SQL，因此该方法通常用于DDL语句。它采用了回调接口，绑定变量数组等变体而严重超载。

```java
this.jdbcTemplate.execute("create table mytable (id integer, name varchar(100))");
```

以下示例调用一个简单的存储过程。[稍后](jdbc.html#jdbc-StoredProcedure)将[介绍](jdbc.html#jdbc-StoredProcedure)更复杂的存储过程支持。

```java
this.jdbcTemplate.update(
        "call SUPPORT.REFRESH_ACTORS_SUMMARY(?)",
        Long.valueOf(unionId));
```

#### JdbcTemplate最佳实践

*一旦配置*，`JdbcTemplate`该类的实例就是*线程安全的*。这很重要，因为这意味着您可以配置a的单个实例，`JdbcTemplate` 然后将此*共享*引用安全地注入多个DAO（或存储库）。该`JdbcTemplate`是有状态的，因为它保持一个参考`DataSource`，但这种状态是*不是*会话状态。

使用`JdbcTemplate`类（和关联的 [`NamedParameterJdbcTemplate`](jdbc.html#jdbc-NamedParameterJdbcTemplate)类）时的一个常见做法是`DataSource`在Spring配置文件中配置一个，然后依赖注入该共享`DataSource`bean到您的DAO类中; 将`JdbcTemplate`在二传手的创建`DataSource`。这导致DAO看起来部分如下：

```java
public class JdbcCorporateEventDao implements CorporateEventDao {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // JDBC-backed implementations of the methods on the CorporateEventDao follow...
}
```

相应的配置可能如下所示。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <bean id="corporateEventDao" class="com.example.JdbcCorporateEventDao">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="${jdbc.driverClassName}"/>
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <context:property-placeholder location="jdbc.properties"/>

</beans>
```

显式配置的替代方法是使用组件扫描和注释支持依赖注入。在这种情况下，您使用`@Repository` （使其成为组件扫描的候选者）注释类并使用注释`DataSource`setter方法`@Autowired`。

```java
@Repository
public class JdbcCorporateEventDao implements CorporateEventDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // JDBC-backed implementations of the methods on the CorporateEventDao follow...
}
```

相应的XML配置文件如下所示：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <!-- Scans within the base package of the application for @Component classes to configure as beans -->
    <context:component-scan base-package="org.springframework.docs.test" />

    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="${jdbc.driverClassName}"/>
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <context:property-placeholder location="jdbc.properties"/>

</beans>
```

如果您正在使用Spring的`JdbcDaoSupport`类，并且您的各种JDBC支持的DAO类从它扩展，那么您的子类将继承该类中的`setDataSource(..)`方法`JdbcDaoSupport`。您可以选择是否继承此类。该 `JdbcDaoSupport`课程仅为方便起见而提供。

无论您选择使用（或不使用）上述哪种模板初始化样式，`JdbcTemplate`每次要执行SQL时，很少需要创建类的新实例。配置完成后，`JdbcTemplate`实例就是线程安全的。`JdbcTemplate`如果您的应用程序访问多个数据库，您可能需要多个实例，这需要多个数据库，`DataSources`然后多个不同的配置`JdbcTemplates`。

### 19.2.2 NamedParameterJdbcTemplate

本`NamedParameterJdbcTemplate`类增加了支持使用命名参数如何在SQL语句，如只使用常规的占位符（而不是如何在SQL语句`'?'`）的参数。本`NamedParameterJdbcTemplate`类包装一个 `JdbcTemplate`，并委托给包裹`JdbcTemplate`做太多的工作。本节仅描述`NamedParameterJdbcTemplate`班级中与`JdbcTemplate`自身不同的区域; 即，使用命名参数编写JDBC语句。

```java
// some JDBC-backed DAO class...
private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

public void setDataSource(DataSource dataSource) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
}

public int countOfActorsByFirstName(String firstName) {

    String sql = "select count(*) from T_ACTOR where first_name = :first_name";

    SqlParameterSource namedParameters = new MapSqlParameterSource("first_name", firstName);

    return this.namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
}
```

请注意在赋给`sql` 变量的值中使用命名参数表示法，以及插入`namedParameters` 变量（类型`MapSqlParameterSource`）的相应值。

或者，您可以`NamedParameterJdbcTemplate`使用`Map`基于-Based的样式将命名参数及其相应的值传递给 实例。`NamedParameterJdbcOperations`由`NamedParameterJdbcTemplate`类公开并由类实现的 其余方法遵循类似的模式，此处不再介绍。

以下示例显示了`Map`基于-Based样式的用法。

```java
// some JDBC-backed DAO class...
private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

public void setDataSource(DataSource dataSource) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
}

public int countOfActorsByFirstName(String firstName) {

    String sql = "select count(*) from T_ACTOR where first_name = :first_name";

    Map<String, String> namedParameters = Collections.singletonMap("first_name", firstName);

    return this.namedParameterJdbcTemplate.queryForObject(sql, namedParameters,  Integer.class);
}
```

与接口相关的一个很好的功能`NamedParameterJdbcTemplate`（并且存在于相同的Java包中）`SqlParameterSource`。您已经在前面的一个代码片段（`MapSqlParameterSource`该类）中看到了此接口的实现示例 。An `SqlParameterSource`是a的命名参数值的来源`NamedParameterJdbcTemplate`。该`MapSqlParameterSource`班是一个非常简单的实现它仅仅是一个适配器`java.util.Map`，其中的键是参数名称和值的参数值。

另一个`SqlParameterSource`实现是`BeanPropertySqlParameterSource` 类。此类包装任意JavaBean（即，遵循[JavaBean约定](https://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html)的类的实例），并使用包装的JavaBean的属性作为命名参数值的来源。

```java
public class Actor {

    private Long id;
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Long getId() {
        return this.id;
    }

    // setters omitted...

}
```

```java
// some JDBC-backed DAO class...
private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

public void setDataSource(DataSource dataSource) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
}

public int countOfActors(Actor exampleActor) {

    // notice how the named parameters match the properties of the above 'Actor' class
    String sql = "select count(*) from T_ACTOR where first_name = :firstName and last_name = :lastName";

    SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(exampleActor);

    return this.namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
}
```

请记住，`NamedParameterJdbcTemplate`该类*包装*了一个经典`JdbcTemplate` 模板; 如果您需要访问包装`JdbcTemplate`实例以访问仅存在于`JdbcTemplate`类中的功能，则可以使用该 `getJdbcOperations()`方法`JdbcTemplate`通过 `JdbcOperations`接口访问包装。

有关在应用程序上下文中使用该类的指南， 另请参阅[“JdbcTemplate最佳实践”](jdbc.html#jdbc-JdbcTemplate-idioms)`NamedParameterJdbcTemplate`一节。

### 19.2.3 SQLExceptionTranslator

`SQLExceptionTranslator`是一个可以在类`SQLExceptions`和Spring 之间进行转换的类实现的接口`org.springframework.dao.DataAccessException`，这与数据访问策略无关。实现可以是通用的（例如，使用JDBC的SQLState代码）或专有的（例如，使用Oracle错误代码）以获得更高的精度。

`SQLErrorCodeSQLExceptionTranslator`是`SQLExceptionTranslator` 默认使用的实现。此实现使用特定供应商代码。它比`SQLState`实施更精确。错误代码转换基于JavaBean类型类中保存的代码`SQLErrorCodes`。此类是由`SQLErrorCodesFactory`名称建议的工厂创建和填充的，该工厂`SQLErrorCodes`基于名为的配置文件的内容进行创建`sql-error-codes.xml`。此文件使用供应商代码填充，并基于 `DatabaseProductName`从中获取的`DatabaseMetaData`。使用您正在使用的实际数据库的代码。

将`SQLErrorCodeSQLExceptionTranslator`按照下列顺序应用匹配规则：

在`SQLErrorCodesFactory`默认情况下使用定义错误代码和自定义异常翻译。`sql-error-codes.xml`在类路径中命名的文件中查找它们，并`SQLErrorCodes`根据正在使用的数据库的数据库元数据中的数据库名称找到匹配的实例。

- 由子类实现的任何自定义转换。通常使用提供的混凝土 `SQLErrorCodeSQLExceptionTranslator`，因此该规则不适用。它仅适用于您实际提供了子类实现的情况。
- `SQLExceptionTranslator`作为类的`customSqlExceptionTranslator`属性提供 的接口的任何自定义实现`SQLErrorCodes`。
- 为`CustomSQLErrorCodesTranslation`类的`customTranslations`属性提供的类 的实例列表`SQLErrorCodes`将搜索匹配项。
- 应用错误代码匹配。
- 使用后备翻译器。`SQLExceptionSubclassTranslator`是默认的后备翻译器。如果这个翻译不可用，那么下一个后备翻译就是`SQLStateSQLExceptionTranslator`。

你可以扩展 `SQLErrorCodeSQLExceptionTranslator:`

```java
public class CustomSQLErrorCodesTranslator extends SQLErrorCodeSQLExceptionTranslator {

    protected DataAccessException customTranslate(String task, String sql, SQLException sqlex) {
        if (sqlex.getErrorCode() == -12345) {
            return new DeadlockLoserDataAccessException(task, sqlex);
        }
        return null;
    }
}
```

在此示例中，特定错误代码`-12345`被翻译，其他错误由默认翻译器实现保留。要使用此自定义转换程序，必须将其传递给`JdbcTemplate`方法，`setExceptionTranslator`并将其`JdbcTemplate`用于需要此转换程序的所有数据访问处理。以下是如何使用此自定义转换程序的示例：

```java
private JdbcTemplate jdbcTemplate;

public void setDataSource(DataSource dataSource) {

    // create a JdbcTemplate and set data source
    this.jdbcTemplate = new JdbcTemplate();
    this.jdbcTemplate.setDataSource(dataSource);

    // create a custom translator and set the DataSource for the default translation lookup
    CustomSQLErrorCodesTranslator tr = new CustomSQLErrorCodesTranslator();
    tr.setDataSource(dataSource);
    this.jdbcTemplate.setExceptionTranslator(tr);

}

public void updateShippingCharge(long orderId, long pct) {
    // use the prepared JdbcTemplate for this update
    this.jdbcTemplate.update("update orders" +
        " set shipping_charge = shipping_charge * ? / 100" +
        " where id = ?", pct, orderId);
}
```

自定义转换程序将传递一个数据源，以便在其中查找错误代码 `sql-error-codes.xml`。

### 19.2.4执行语句

执行SQL语句只需要很少的代码。你需要一个`DataSource`和一个 `JdbcTemplate`，包括提供的便利方法 `JdbcTemplate`。以下示例显示了为创建新表的最小但功能齐全的类所需要包含的内容：

```java
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class ExecuteAStatement {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void doExecute() {
        this.jdbcTemplate.execute("create table mytable (id integer, name varchar(100))");
    }
}
```

### 19.2.5运行查询

某些查询方法返回单个值。要从一行检索计数或特定值，请使用`queryForObject(..)`。后者将返回的JDBC转换`Type`为作为参数传入的Java类。如果类型转换无效，则`InvalidDataAccessApiUsageException`抛出a。下面是一个包含两个查询方法的示例，一个用于查询，另一个用于`int`查询`String`。

```java
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class RunAQuery {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from mytable", Integer.class);
    }

    public String getName() {
        return this.jdbcTemplate.queryForObject("select name from mytable", String.class);
    }
}
```

除了单个结果查询方法之外，还有几个方法返回一个列表，其中包含查询返回的每一行的条目。最通用的方法是 `queryForList(..)`返回`List`每个条目所在的位置，其中`Map`地图中的每个条目表示该行的列值。如果您向上面的示例添加一个方法来检索所有行的列表，它将如下所示：

```java
private JdbcTemplate jdbcTemplate;

public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
}

public List<Map<String, Object>> getList() {
    return this.jdbcTemplate.queryForList("select * from mytable");
}
```

返回的列表看起来像这样：

```bash
[{name=Bob, id=1}, {name=Mary, id=2}]
```

### 19.2.6更新数据库

以下示例显示了针对某个主键更新的列。在此示例中，SQL语句具有行参数的占位符。参数值可以作为varargs传递，也可以作为对象数组传递。因此，原语应该显式地包装在原始包装类中或使用自动装箱。

```java
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class ExecuteAnUpdate {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setName(int id, String name) {
        this.jdbcTemplate.update("update mytable set name = ? where id = ?", name, id);
    }
}
```

### 19.2.7检索自动生成的密钥

的`update()`便利方法支持由数据库生成主键的检索。这种支持是JDBC 3.0标准的一部分; 有关详细信息，请参阅规范的第13.6章。该方法将a `PreparedStatementCreator`作为其第一个参数，这是指定所需insert语句的方式。另一个参数是a `KeyHolder`，它包含从更新成功返回时生成的密钥。没有标准的单一方法来创建适当的`PreparedStatement` （这解释了为什么方法签名就是这样）。以下示例适用于Oracle，但可能无法在其他平台上运行：

```java
final String INSERT_SQL = "insert into my_test (name) values(?)";
final String name = "Rob";

KeyHolder keyHolder = new GeneratedKeyHolder();
jdbcTemplate.update(
    new PreparedStatementCreator() {
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[] {"id"});
            ps.setString(1, name);
            return ps;
        }
    },
    keyHolder);

// keyHolder.getKey() now contains the generated key
```

