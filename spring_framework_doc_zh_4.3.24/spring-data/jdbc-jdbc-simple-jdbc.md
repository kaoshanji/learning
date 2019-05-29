# 19.5 SimpleJdbc

`SimpleJdbcInsert`和`SimpleJdbcCall`类通过取可通过JDBC驱动被检索数据库的元数据的优点提供了一个简化的配置。这意味着预先配置较少，但如果您希望提供代码中的所有详细信息，则可以覆盖或关闭元数据处理。

### 19.5.1使用SimpleJdbcInsert插入数据

让我们从`SimpleJdbcInsert`具有最少量配置选项的类开始。您应该`SimpleJdbcInsert`在数据访问层的初始化方法中实例化。对于此示例，初始化方法是`setDataSource`方法。您不需要继承`SimpleJdbcInsert`该类; 只需创建一个新实例并使用该`withTableName`方法设置表名。此类的配置方法遵循返回实例的“流体”样式，`SimpleJdbcInsert`允许您链接所有配置方法。此示例仅使用一种配置方法; 稍后您将看到多个示例。

```java
public class JdbcActorDao implements ActorDao {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertActor;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource).withTableName("t_actor");
    }

    public void add(Actor actor) {
        Map<String, Object> parameters = new HashMap<String, Object>(3);
        parameters.put("id", actor.getId());
        parameters.put("first_name", actor.getFirstName());
        parameters.put("last_name", actor.getLastName());
        insertActor.execute(parameters);
    }

    // ... additional methods
}
```

这里使用的execute方法将plain `java.utils.Map`作为唯一参数。这里需要注意的重要一点是，用于Map的键必须与数据库中定义的表的列名匹配。这是因为我们读取元数据以构造实际的insert语句。

### 19.5.2使用SimpleJdbcInsert检索自动生成的密钥

此示例使用与前面相同的插入，但不是传入id，而是检索自动生成的密钥并将其设置在新的Actor对象上。创建时`SimpleJdbcInsert`，除了指定表名外，还可以使用方法指定生成的键列的名称`usingGeneratedKeyColumns`。

```java
public class JdbcActorDao implements ActorDao {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertActor;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("t_actor")
                .usingGeneratedKeyColumns("id");
    }

    public void add(Actor actor) {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("first_name", actor.getFirstName());
        parameters.put("last_name", actor.getLastName());
        Number newId = insertActor.executeAndReturnKey(parameters);
        actor.setId(newId.longValue());
    }

    // ... additional methods
}
```

通过第二种方法执行插入时的主要区别在于您不将ID添加到Map并调用该`executeAndReturnKey`方法。这将返回一个 `java.lang.Number`对象，您可以使用该对象创建我们的域类中使用的数字类型的实例。您不能依赖所有数据库来返回特定的Java类; `java.lang.Number`是您可以信赖的基类。如果您有多个自动生成的列，或者生成的值是非数字的，则可以使用`KeyHolder`从该`executeAndReturnKeyHolder`方法返回的值。

### 19.5.3为SimpleJdbcInsert指定列

您可以通过使用以下`usingColumns`方法指定列名列表来限制插入的列 ：

```java
public class JdbcActorDao implements ActorDao {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertActor;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("t_actor")
                .usingColumns("first_name", "last_name")
                .usingGeneratedKeyColumns("id");
    }

    public void add(Actor actor) {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("first_name", actor.getFirstName());
        parameters.put("last_name", actor.getLastName());
        Number newId = insertActor.executeAndReturnKey(parameters);
        actor.setId(newId.longValue());
    }

    // ... additional methods
}
```

插入的执行与您依赖元数据来确定要使用的列相同。

### 19.5.4使用SqlParameterSource提供参数值

使用a `Map`提供参数值工作正常，但它不是最方便使用的类。Spring提供了几个`SqlParameterSource` 可以使用的接口实现。第一个是`BeanPropertySqlParameterSource`，如果你有一个包含你的值的JavaBean兼容类，这是一个非常方便的类。它将使用相应的getter方法来提取参数值。这是一个例子：

```java
public class JdbcActorDao implements ActorDao {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertActor;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("t_actor")
                .usingGeneratedKeyColumns("id");
    }

    public void add(Actor actor) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(actor);
        Number newId = insertActor.executeAndReturnKey(parameters);
        actor.setId(newId.longValue());
    }

    // ... additional methods
}
```

另一种选择是`MapSqlParameterSource`类似于Map，但提供了`addValue`一种可以链接的更方便的方法。

```java
public class JdbcActorDao implements ActorDao {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertActor;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("t_actor")
                .usingGeneratedKeyColumns("id");
    }

    public void add(Actor actor) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("first_name", actor.getFirstName())
                .addValue("last_name", actor.getLastName());
        Number newId = insertActor.executeAndReturnKey(parameters);
        actor.setId(newId.longValue());
    }

    // ... additional methods
}
```

如您所见，配置是相同的; 只有执行代码必须更改才能使用这些替代输入类。

### 19.5.5使用SimpleJdbcCall调用存储过程

该`SimpleJdbcCall`级利用元数据在数据库中查找的名称`in` 和`out`参数，使您不必明确声明他们。如果您愿意，可以声明参数，或者如果您具有诸如`ARRAY` 或`STRUCT`没有自动映射到Java类的参数。第一个示例显示了一个简单的过程，该过程仅返回MySQL数据库中的标量值`VARCHAR`和`DATE`格式。该示例过程读取指定的演员项并返回`first_name`，`last_name`以及`birth_date`在形式列`out`参数。

```sql
CREATE PROCEDURE read_actor (
    IN in_id INTEGER,
    OUT out_first_name VARCHAR(100),
    OUT out_last_name VARCHAR(100),
    OUT out_birth_date DATE)
BEGIN
    SELECT first_name, last_name, birth_date
    INTO out_first_name, out_last_name, out_birth_date
    FROM t_actor where id = in_id;
END;
```

该`in_id`参数包含`id`您正在查找的actor的名称。该`out` 参数返回从表中读取数据。

在`SimpleJdbcCall`以类似的方式申报`SimpleJdbcInsert`。您应该在数据访问层的初始化方法中实例化和配置类。与StoredProcedure类相比，您不必创建子类，也不必声明可在数据库元数据中查找的参数。以下是使用上述存储过程的SimpleJdbcCall配置示例。除了之外，唯一的配置选项`DataSource`是存储过程的名称。

```java
public class JdbcActorDao implements ActorDao {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall procReadActor;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.procReadActor = new SimpleJdbcCall(dataSource)
                .withProcedureName("read_actor");
    }

    public Actor readActor(Long id) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("in_id", id);
        Map out = procReadActor.execute(in);
        Actor actor = new Actor();
        actor.setId(id);
        actor.setFirstName((String) out.get("out_first_name"));
        actor.setLastName((String) out.get("out_last_name"));
        actor.setBirthDate((Date) out.get("out_birth_date"));
        return actor;
    }

    // ... additional methods
}
```

为执行调用而编写的代码涉及创建`SqlParameterSource` 包含IN参数的代码。将为输入值提供的名称与存储过程中声明的参数名称的名称相匹配非常重要。该案例不必匹配，因为您使用元数据来确定应如何在存储过程中引用数据库对象。存储过程的源中指定的内容不一定是存储在数据库中的方式。某些数据库将名称转换为全部大写，而其他数据库使用小写或使用指定的大小写。

该`execute`方法获取IN参数并返回一个Map，该Map包含`out` 由存储过程中指定的名称键入的任何参数。在这种情况下，他们是 `out_first_name, out_last_name`和`out_birth_date`。

该`execute`方法的最后一部分创建一个Actor实例，用于返回检索到的数据。同样，`out`在存储过程中声明它们时使用参数的名称也很重要。此外，`out` 结果映射中存储的参数名称中的大小写`out`与数据库中参数名称的大小写相匹配，这些参数名称可能因数据库而异。为了使代码更具可移植性，您应该进行不区分大小写的查找或指示Spring使用`LinkedCaseInsensitiveMap`。要执行后者，您可以创建自己`JdbcTemplate`的`setResultsMapCaseInsensitive` 属性并将属性设置为`true`。然后将此自定义`JdbcTemplate`实例传递给您的构造函数`SimpleJdbcCall`。以下是此配置的示例：

```java
public class JdbcActorDao implements ActorDao {

    private SimpleJdbcCall procReadActor;

    public void setDataSource(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        this.procReadActor = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("read_actor");
    }

    // ... additional methods
}
```

通过执行此操作，可以避免在用于返回`out`参数名称的情况下发生冲突。

### 19.5.6显式声明用于SimpleJdbcCall的参数

您已经了解了如何根据元数据推导出参数，但如果您愿意，可以明确声明。您可以通过`SimpleJdbcCall`使用`declareParameters`方法创建和配置来完成此操作，该方法将可变数量的`SqlParameter`对象作为输入。有关如何定义的详细信息，请参阅下一节`SqlParameter`。

如果您使用的数据库不是Spring支持的数据库，则必须使用显式声明。目前，Spring支持以下数据库的存储过程调用的元数据查找：Apache Derby，DB2，MySQL，Microsoft SQL Server，Oracle和Sybase。我们还支持MySQL，Microsoft SQL Server和Oracle的存储函数的元数据查找。

您可以选择明确声明一个，一些或所有参数。在未明确声明参数的情况下，仍会使用参数元数据。要绕过对潜在参数的元数据查找的所有处理并仅使用声明的参数，请将该方法`withoutProcedureColumnMetaDataAccess`作为声明的一部分进行调用。假设您为数据库函数声明了两个或多个不同的调用签名。在这种情况下，您调用`useInParameterNames`to来指定要包含给定签名的IN参数名称列表。

以下示例使用前面示例中的信息显示完全声明的过程调用。

```java
public class JdbcActorDao implements ActorDao {

    private SimpleJdbcCall procReadActor;

    public void setDataSource(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        this.procReadActor = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("read_actor")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("in_id")
                .declareParameters(
                        new SqlParameter("in_id", Types.NUMERIC),
                        new SqlOutParameter("out_first_name", Types.VARCHAR),
                        new SqlOutParameter("out_last_name", Types.VARCHAR),
                        new SqlOutParameter("out_birth_date", Types.DATE)
                );
    }

    // ... additional methods
}
```

两个例子的执行和结束结果是相同的; 这个明确指定所有细节而不是依赖元数据。

### 19.5.7如何定义SqlParameters

要为SimpleJdbc类以及[第19.6节“将JDBC操作建模为Java对象”中](jdbc.html#jdbc-object)所述的RDBMS操作类定义参数，请使用`SqlParameter`其子类或其中一个子类。您通常在构造函数中指定参数名称和SQL类型。使用`java.sql.Types`常量指定SQL类型。我们已经看过如下声明：

```java
new SqlParameter("in_id", Types.NUMERIC),
    new SqlOutParameter("out_first_name", Types.VARCHAR),
```

第一行`SqlParameter`声明了一个IN参数。IN参数既可用于存储过程调用，也可用于使用`SqlQuery`下一节中介绍的及其子类的查询。

第二行`SqlOutParameter`声明了一个`out`在存储过程调用中使用的参数。还有一个`SqlInOutParameter`for `InOut`参数，为`IN`过程提供值并且还返回值的参数。

只有声明为`SqlParameter`和`SqlInOutParameter`将用于提供输入值的参数。这与`StoredProcedure`类不同，为了向后兼容性原因，允许为声明为的参数提供输入值`SqlOutParameter`。

对于IN参数，除了名称和SQL类型之外，还可以为数字数据指定比例，或为自定义数据库类型指定类型名称。对于`out`参数，您可以提供一个`RowMapper`处理从`REF`游标返回的行的映射。另一种选择是指定一个`SqlReturnType`提供定义返回值的自定义处理的机会。

### 19.5.8使用SimpleJdbcCall调用存储的函数

您调用存储函数的方式与调用存储过程几乎相同，只是提供函数名而不是过程名。您可以将此 `withFunctionName`方法用作配置的一部分，以指示我们要对函数进行调用，并生成函数调用的相应字符串。一个专门的执行调用，`executeFunction,`用于执行该函数，它返回函数返回值作为指定类型的对象，这意味着您不必从结果映射中检索返回值。`executeObject`对于只有一个`out` 参数的存储过程，也可以使用类似的便捷方法。以下示例基于名为的存储函数`get_actor_name` ，该函数返回actor的全名。以下是此函数的MySQL源代码：

```sql
CREATE FUNCTION get_actor_name (in_id INTEGER)
RETURNS VARCHAR(200) READS SQL DATA
BEGIN
    DECLARE out_name VARCHAR(200);
    SELECT concat(first_name, ' ', last_name)
        INTO out_name
        FROM t_actor where id = in_id;
    RETURN out_name;
END;
```

要调用此函数，我们再次`SimpleJdbcCall`在初始化方法中创建一个。

```java
public class JdbcActorDao implements ActorDao {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall funcGetActorName;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        this.funcGetActorName = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("get_actor_name");
    }

    public String getActorName(Long id) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("in_id", id);
        String name = funcGetActorName.executeFunction(String.class, in);
        return name;
    }

    // ... additional methods
}
```

使用的execute方法返回`String`包含函数调用的返回值的a 。

### 19.5.9从SimpleJdbcCall返回ResultSet / REF游标

调用返回结果集的存储过程或函数有点棘手。某些数据库在JDBC结果处理期间返回结果集，而其他数据库则需要`out`特定类型的显式注册参数。这两种方法都需要额外的处理来循环结果集并处理返回的行。随着`SimpleJdbcCall`你使用的`returningResultSet`方法和声明一个`RowMapper` 实现用于特定参数。如果在结果处理期间返回结果集，则不会定义任何名称，因此返回的结果必须与声明`RowMapper` 实现的顺序相匹配。指定的名称仍用于将处理的结果列表存储在从execute语句返回的结果映射中。

下一个示例使用不带IN参数的存储过程，并返回t_actor表中的所有行。以下是此过程的MySQL源代码：

```sql
CREATE PROCEDURE read_all_actors()
BEGIN
 SELECT a.id, a.first_name, a.last_name, a.birth_date FROM t_actor a;
END;
```

要调用此过程，请声明`RowMapper`。因为要映射到的类遵循JavaBean规则，所以可以使用`BeanPropertyRowMapper`通过传入所需的类来映射到`newInstance`方法中创建的类。

```java
public class JdbcActorDao implements ActorDao {

    private SimpleJdbcCall procReadAllActors;

    public void setDataSource(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        this.procReadAllActors = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("read_all_actors")
                .returningResultSet("actors",
                BeanPropertyRowMapper.newInstance(Actor.class));
    }

    public List getActorsList() {
        Map m = procReadAllActors.execute(new HashMap<String, Object>(0));
        return (List) m.get("actors");
    }

    // ... additional methods
}
```

执行调用传入一个空Map，因为此调用不接受任何参数。然后从结果映射中检索Actors列表并返回给调用者。