# 19.4 JDBC批量处理

如果批量多次调用同一个预准备语句，则大多数JDBC驱动程序都可以提高性能。通过将更新分组到批次中，可以限制到数据库的往返次数。

### 19.4.1使用JdbcTemplate进行基本批处理操作

您可以`JdbcTemplate`通过实现特殊接口的两个方法来完成批处理`BatchPreparedStatementSetter`，并将其作为`batchUpdate`方法调用中的第二个参数传递。使用该`getBatchSize`方法提供当前批次的大小。使用此`setValues`方法设置预准备语句的参数值。此方法将被称为您在`getBatchSize`呼叫中指定的次数。以下示例根据列表中的条目更新actor表。在此示例中，整个列表用作批处理：

```java
public class JdbcActorDao implements ActorDao {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int[] batchUpdate(final List<Actor> actors) {
        return this.jdbcTemplate.batchUpdate(
                "update t_actor set first_name = ?, last_name = ? where id = ?",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, actors.get(i).getFirstName());
                        ps.setString(2, actors.get(i).getLastName());
                        ps.setLong(3, actors.get(i).getId().longValue());
                    }
                    public int getBatchSize() {
                        return actors.size();
                    }
                });
    }

    // ... additional methods
}
```

如果您正在处理更新流或从文件读取，那么您可能具有首选批量大小，但最后一批可能没有该数量的条目。在这种情况下，您可以使用该`InterruptibleBatchPreparedStatementSetter`界面，该界面允许您在输入源耗尽后中断批处理。该`isBatchExhausted`方法允许您发出批次结束的信号。

### 19.4.2使用对象列表进行批处理操作

无论是`JdbcTemplate`与`NamedParameterJdbcTemplate`提供了提供批更新的替代方式。您可以将调用中的所有参数值作为列表提供，而不是实现特殊的批处理接口。框架循环遍历这些值并使用内部预处理语句setter。API会根据您是否使用命名参数而有所不同。对于命名参数，您为`SqlParameterSource`批处理的每个成员提供一个数组，一个条目。您可以使用 `SqlParameterSourceUtils.createBatch`便捷方法来创建此数组，传入一个bean样式对象数组（使用与参数对应的getter方法）和/或String-keyed Maps（包含相应参数作为值）。

此示例显示使用命名参数的批量更新：

```java
public class JdbcActorDao implements ActorDao {

    private NamedParameterTemplate namedParameterJdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public int[] batchUpdate(List<Actor> actors) {
        return this.namedParameterJdbcTemplate.batchUpdate(
                "update t_actor set first_name = :firstName, last_name = :lastName where id = :id",
                SqlParameterSourceUtils.createBatch(actors.toArray()));
    }

    // ... additional methods
}
```

对于使用经典“？”的SQL语句 在占位符中，传入包含具有更新值的对象数组的列表。此对象数组必须在SQL语句中为每个占位符分配一个条目，并且它们的顺序必须与SQL语句中定义的顺序相同。

使用经典JDBC的相同示例“？” 占位符：

```java
public class JdbcActorDao implements ActorDao {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int[] batchUpdate(final List<Actor> actors) {
        List<Object[]> batch = new ArrayList<Object[]>();
        for (Actor actor : actors) {
            Object[] values = new Object[] {
                    actor.getFirstName(), actor.getLastName(), actor.getId()};
            batch.add(values);
        }
        return this.jdbcTemplate.batchUpdate(
                "update t_actor set first_name = ?, last_name = ? where id = ?",
                batch);
    }

    // ... additional methods
}
```

所有上述批处理更新方法都返回一个int数组，其中包含每个批处理条目的受影响行数。JDBC驱动程序报告此计数。如果计数不可用，则JDBC驱动程序返回-2值。

### 19.4.3多批次的批处理操作

批量更新的最后一个示例处理的批量非常大，您希望将它们分成几个较小的批次。您当然可以通过对方法进行多次调用来使用上述方法执行此操作`batchUpdate`，但现在有一种更方便的方法。除了SQL语句之外，此方法还包含一个包含参数的对象集合，为每个批处理生成的更新数量，以及`ParameterizedPreparedStatementSetter`用于设置预准备语句的参数值的方法。框架循环提供的值并将更新调用分解为指定大小的批处理。

此示例显示批量更新为100的批量更新：

```java
public class JdbcActorDao implements ActorDao {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int[][] batchUpdate(final Collection<Actor> actors) {
        int[][] updateCounts = jdbcTemplate.batchUpdate(
                "update t_actor set first_name = ?, last_name = ? where id = ?",
                actors,
                100,
                new ParameterizedPreparedStatementSetter<Actor>() {
                    public void setValues(PreparedStatement ps, Actor argument) throws SQLException {
                        ps.setString(1, argument.getFirstName());
                        ps.setString(2, argument.getLastName());
                        ps.setLong(3, argument.getId().longValue());
                    }
                });
        return updateCounts;
    }

    // ... additional methods
}
```

此调用的批处理更新方法返回一个int数组数组，其中包含每个批处理的数组条目，其中包含每个更新的受影响行数的数组。顶级数组的长度表示执行的批次数，第二级数组的长度表示该批次中的更新数。每个批次中的更新数量应该是为所有批次提供的批量大小，但最后一个批次可能更少，具体取决于提供的更新对象总数。每个更新语句的更新计数是JDBC驱动程序报告的更新计数。如果计数不可用，则JDBC驱动程序返回-2值。