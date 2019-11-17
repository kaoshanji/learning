# 19.6 指定JDBC操作

该`org.springframework.jdbc.object`包包含允许您以面向对象的方式访问数据库的类。例如，您可以执行查询并将结果作为包含业务对象的列表返回，其中关系列数据映射到业务对象的属性。您还可以执行存储过程并运行update，delete和insert语句。

许多Spring开发人员认为下面描述的各种RDBMS操作类（除了[`StoredProcedure`](jdbc.html#jdbc-StoredProcedure)类）通常可以用直接`JdbcTemplate`调用替换。通常，编写简单地直接调用方法的DAO方法`JdbcTemplate`（而不是将查询封装为完整的类）更简单。

但是，如果您从使用RDBMS操作类获得可测量的值，请继续使用这些类。

### 19.6.1 SqlQuery

`SqlQuery`是一个可重用的线程安全类，它封装了一个SQL查询。子类必须实现该`newRowMapper(..)`方法以提供一个`RowMapper`实例，该实例可以通过迭代在`ResultSet`查询执行期间创建的每一行来创建一个对象。在`SqlQuery`因为类很少直接使用`MappingSqlQuery`的子类提供映射行Java类的更方便的实现。其他扩展的实现`SqlQuery`是 `MappingSqlQueryWithParameters`和`UpdatableSqlQuery`。

### 19.6.2 MappingSqlQuery

`MappingSqlQuery`是一个可重用的查询，其中具体的子类必须实现抽象`mapRow(..)`方法，以将提供的每一行转换`ResultSet`为指定类型的对象。以下示例显示了一个自定义查询，该查询将`t_actor`关系中的数据映射到`Actor`类的实例。

```java
public class ActorMappingQuery extends MappingSqlQuery<Actor> {

    public ActorMappingQuery(DataSource ds) {
        super(ds, "select id, first_name, last_name from t_actor where id = ?");
        declareParameter(new SqlParameter("id", Types.INTEGER));
        compile();
    }

    @Override
    protected Actor mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Actor actor = new Actor();
        actor.setId(rs.getLong("id"));
        actor.setFirstName(rs.getString("first_name"));
        actor.setLastName(rs.getString("last_name"));
        return actor;
    }

}
```

该类`MappingSqlQuery`使用`Actor`类型进行参数化扩展。此客户查询的构造函数`DataSource`将唯一参数作为参数。在此构造函数中`DataSource`，使用应执行的SQL和SQL来调用超类上的构造函数，以检索此查询的行。此SQL将用于创建一个，`PreparedStatement`因此它可能包含在执行期间传递的任何参数的占位符。您必须使用`declareParameter` 传入的方法声明每个参数`SqlParameter`。在`SqlParameter`需要一个名称和如在定义的JDBC类型`java.sql.Types`。定义所有参数后，调用`compile()`方法，以便可以准备并稍后执行该语句。这个类在编译后是线程安全的，因此只要在初始化DAO时创建这些实例，它们就可以作为实例变量保存并重用。

```java
private ActorMappingQuery actorMappingQuery;

@Autowired
public void setDataSource(DataSource dataSource) {
    this.actorMappingQuery = new ActorMappingQuery(dataSource);
}

public Customer getCustomer(Long id) {
    return actorMappingQuery.findObject(id);
}
```

此示例中的方法使用作为唯一参数传入的id检索客户。由于我们只想要返回一个对象，我们只需调用`findObject`带有id作为参数的方法。如果我们改为返回一个对象列表并获取其他参数的查询，那么我们将使用一个执行方法，该方法将传入的参数值数组作为varargs。

```java
public List<Actor> searchForActors(int age, String namePattern) {
    List<Actor> actors = actorSearchMappingQuery.execute(age, namePattern);
    return actors;
}
```

### 19.6.3 SqlUpdate

本`SqlUpdate`类封装了一个SQL更新。与查询一样，更新对象是可重用的，并且与所有`RdbmsOperation`类一样，更新可以具有参数并在SQL中定义。此类提供了许多`update(..)`类似于 `execute(..)`查询对象方法的方法。这`SQLUpdate`门课是具体的。例如，可以将其子类化为添加自定义更新方法，如下面的代码片段所示`execute`。但是，您不必对类进行子`SqlUpdate` 类化，因为可以通过设置SQL和声明参数来轻松地对其进行参数化。

```java
import java.sql.Types;
import javax.sql.DataSource;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

public class UpdateCreditRating extends SqlUpdate {

    public UpdateCreditRating(DataSource ds) {
        setDataSource(ds);
        setSql("update customer set credit_rating = ? where id = ?");
        declareParameter(new SqlParameter("creditRating", Types.NUMERIC));
        declareParameter(new SqlParameter("id", Types.NUMERIC));
        compile();
    }

    /**
     * @param id for the Customer to be updated
     * @param rating the new value for credit rating
     * @return number of rows updated
     */
    public int execute(int id, int rating) {
        return update(rating, id);
    }
}
```

### 19.6.4 StoredProcedure

的`StoredProcedure`类是RDBMS存储过程的对象的抽象类的超类。这个类是`abstract`，并且它的各种`execute(..)`方法具有 `protected`访问权限，防止除了通过提供更紧密键入的子类之外的使用。

继承的`sql`属性将是RDBMS中存储过程的名称。

要为`StoredProcedure`类定义参数，请使用`SqlParameter`其子类或其中一个子类。您必须在构造函数中指定参数名称和SQL类型，如以下代码段中所示。使用`java.sql.Types` 常量指定SQL类型。

```java
new SqlParameter("in_id", Types.NUMERIC),
    new SqlOutParameter("out_first_name", Types.VARCHAR),
```

第一行`SqlParameter`声明了一个IN参数。IN参数既可用于存储过程调用，也可用于使用`SqlQuery`下一节中介绍的及其子类的查询。

第二行`SqlOutParameter`声明了一个`out`在存储过程调用中使用的参数。还有一个`SqlInOutParameter`for `I` `nOut`参数，为`in`过程提供值并且还返回值的参数。

对于`i` `n`参数，除了名称和SQL类型之外，还可以为数字数据指定比例，或为自定义数据库类型指定类型名称。对于`out`参数，您可以提供一个`RowMapper`处理从REF游标返回的行的映射。另一个选项是指定一个`SqlReturnType`允许您定义返回值的自定义处理的选项。

下面是一个使用a `StoredProcedure`来调用函数的简单DAO的示例，该函数 `sysdate()`随任何Oracle数据库一起提供。要使用存储过程功能，您必须创建一个扩展的类`StoredProcedure`。在此示例中，`StoredProcedure`类是内部类，但如果需要重用，则将其 `StoredProcedure`声明为顶级类。此示例没有输入参数，但输出参数使用类声明为日期类型 `SqlOutParameter`。该`execute()`方法执行该过程并从结果中提取返回的日期`Map`。结果`Map`具有每个声明的输出参数的条目，在这种情况下只有一个，使用参数名称作为键。

```java
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.object.StoredProcedure;

public class StoredProcedureDao {

    private GetSysdateProcedure getSysdate;

    @Autowired
    public void init(DataSource dataSource) {
        this.getSysdate = new GetSysdateProcedure(dataSource);
    }

    public Date getSysdate() {
        return getSysdate.execute();
    }

    private class GetSysdateProcedure extends StoredProcedure {

        private static final String SQL = "sysdate";

        public GetSysdateProcedure(DataSource dataSource) {
            setDataSource(dataSource);
            setFunction(true);
            setSql(SQL);
            declareParameter(new SqlOutParameter("date", Types.DATE));
            compile();
        }

        public Date execute() {
            // the 'sysdate' sproc has no input parameters, so an empty Map is supplied...
            Map<String, Object> results = execute(new HashMap<String, Object>());
            Date sysdate = (Date) results.get("date");
            return sysdate;
        }
    }

}
```

以下a的示例`StoredProcedure`有两个输出参数（在本例中为Oracle REF游标）。

```java
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.object.StoredProcedure;

public class TitlesAndGenresStoredProcedure extends StoredProcedure {

    private static final String SPROC_NAME = "AllTitlesAndGenres";

    public TitlesAndGenresStoredProcedure(DataSource dataSource) {
        super(dataSource, SPROC_NAME);
        declareParameter(new SqlOutParameter("titles", OracleTypes.CURSOR, new TitleMapper()));
        declareParameter(new SqlOutParameter("genres", OracleTypes.CURSOR, new GenreMapper()));
        compile();
    }

    public Map<String, Object> execute() {
        // again, this sproc has no input parameters, so an empty Map is supplied
        return super.execute(new HashMap<String, Object>());
    }
}
```

注意构造函数中`declareParameter(..)`使用的方法的重载变体是如何`TitlesAndGenresStoredProcedure`传递`RowMapper` 实现实例的; 这是重用现有功能的一种非常方便和强大的方法。`RowMapper`下面提供了这两种实现的代码。

所述`TitleMapper`类映射一个`ResultSet`到一个`Title`用于在提供的各行的域对象`ResultSet`：

```java
import java.sql.ResultSet;
import java.sql.SQLException;
import com.foo.domain.Title;
import org.springframework.jdbc.core.RowMapper;

public final class TitleMapper implements RowMapper<Title> {

    public Title mapRow(ResultSet rs, int rowNum) throws SQLException {
        Title title = new Title();
        title.setId(rs.getLong("id"));
        title.setName(rs.getString("name"));
        return title;
    }
}
```

所述`GenreMapper`类映射一个`ResultSet`到一个`Genre`用于在提供的各行的域对象`ResultSet`。

```java
import java.sql.ResultSet;
import java.sql.SQLException;
import com.foo.domain.Genre;
import org.springframework.jdbc.core.RowMapper;

public final class GenreMapper implements RowMapper<Genre> {

    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getString("name"));
    }
}
```

要将参数传递给在RDBMS的定义中具有一个或多个输入参数的存储过程，您可以编写一个强类型`execute(..)`方法，该方法将委托给超类的无类型`execute(Map parameters)`方法（具有 `protected`访问权限）; 例如：

```java
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

public class TitlesAfterDateStoredProcedure extends StoredProcedure {

    private static final String SPROC_NAME = "TitlesAfterDate";
    private static final String CUTOFF_DATE_PARAM = "cutoffDate";

    public TitlesAfterDateStoredProcedure(DataSource dataSource) {
        super(dataSource, SPROC_NAME);
        declareParameter(new SqlParameter(CUTOFF_DATE_PARAM, Types.DATE);
        declareParameter(new SqlOutParameter("titles", OracleTypes.CURSOR, new TitleMapper()));
        compile();
    }

    public Map<String, Object> execute(Date cutoffDate) {
        Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put(CUTOFF_DATE_PARAM, cutoffDate);
        return super.execute(inputs);
    }
}
```

