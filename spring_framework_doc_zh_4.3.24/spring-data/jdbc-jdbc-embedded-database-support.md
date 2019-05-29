# 19.8 嵌入式数据库

该`org.springframework.jdbc.datasource.embedded`软件包为嵌入式Java数据库引擎提供支持。本机提供对[HSQL](http://www.hsqldb.org)， [H2](https://www.h2database.com)和[Derby的](https://db.apache.org/derby)支持。您还可以使用可扩展API来插入新的嵌入式数据库类型和 `DataSource`实现。

### 19.8.1为什么要使用嵌入式数据库？

嵌入式数据库在项目的开发阶段非常有用，因为它具有轻量级特性。优点包括易于配置，快速启动时间，可测试性以及在开发过程中快速发展SQL的能力。

### 19.8.2使用Spring XML创建嵌入式数据库

如果要在Spring中将嵌入式数据库实例公开为bean `ApplicationContext`，请使用命名空间中的`embedded-database`标记`spring-jdbc`：

```xml
<jdbc:embedded-database id="dataSource" generate-name="true">
    <jdbc:script location="classpath:schema.sql"/>
    <jdbc:script location="classpath:test-data.sql"/>
</jdbc:embedded-database>
```

上述配置创建了一个嵌入式HSQL数据库，该数据库填充了SQL from `schema.sql`和`test-data.sql`类路径根目录中的资源。此外，作为最佳实践，将为嵌入式数据库分配唯一生成的名称。嵌入式数据库作为类型的bean可用于Spring容器， `javax.sql.DataSource`然后可以根据需要将其注入数据访问对象。

### 19.8.3以编程方式创建嵌入式数据库

的`EmbeddedDatabaseBuilder`类提供一个流畅API以编程构造的嵌入式数据库。当您需要在独立环境中创建嵌入式数据库或在以下示例中的独立集成测试中使用此选项时，请使用此选项。

```java
EmbeddedDatabase db = new EmbeddedDatabaseBuilder()
		.generateUniqueName(true)
		.setType(H2)
		.setScriptEncoding("UTF-8")
		.ignoreFailedDrops(true)
		.addScript("schema.sql")
		.addScripts("user_data.sql", "country_data.sql")
		.build();

// perform actions against the db (EmbeddedDatabase extends javax.sql.DataSource)

db.shutdown()
```

有关`EmbeddedDatabaseBuilder`所有支持选项的更多详细信息，请参阅Javadoc 。

的`EmbeddedDatabaseBuilder`也可用于创建使用Java配置像在下面的例子中的嵌入式数据库。

```java
@Configuration
public class DataSourceConfig {

	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.generateUniqueName(true)
				.setType(H2)
				.setScriptEncoding("UTF-8")
				.ignoreFailedDrops(true)
				.addScript("schema.sql")
				.addScripts("user_data.sql", "country_data.sql")
				.build();
	}
}
```

### 19.8.4选择嵌入式数据库类型

#### 使用HSQL

Spring支持HSQL 1.8.0及更高版本。如果未明确指定类型，HSQL是默认的嵌入式数据库。要显式指定HSQL，请将标记的`type`属性 设置`embedded-database`为`HSQL`。如果您使用的是构建器API，请使用调用 `setType(EmbeddedDatabaseType)`方法`EmbeddedDatabaseType.HSQL`。

#### 使用H2

Spring也支持H2数据库。要启用H2，请将标记的`type`属性 设置`embedded-database`为`H2`。如果您使用的是构建器API，请使用调用`setType(EmbeddedDatabaseType)`方法`EmbeddedDatabaseType.H2`。

#### 使用德比

Spring还支持Apache Derby 10.5及更高版本。要启用Derby，请将标记的`type` 属性设置`embedded-database`为`DERBY`。如果您使用的是构建器API，请使用调用`setType(EmbeddedDatabaseType)`方法`EmbeddedDatabaseType.DERBY`。

### 19.8.5使用嵌入式数据库测试数据访问逻辑

嵌入式数据库提供了一种轻量级的方法来测试数据访 以下是使用嵌入式数据库的数据访问集成测试模板。当嵌入式数据库不需要跨测试类重用时，使用这样的模板对于*一次性*可能很有用。但是，如果您希望创建在测试套件中共享的嵌入式数据库，请考虑使用[Spring TestContext Framework](integration-testing.html#testcontext-framework)并将嵌入式数据库配置为Spring中的bean，`ApplicationContext`如[第19.8.2节“使用Spring创建嵌入式数据库”中所述XML“](jdbc.html#jdbc-embedded-database-xml)和 [第19.8.3节”以编程方式创建嵌入式数据库“](jdbc.html#jdbc-embedded-database-java)。

```java
public class DataAccessIntegrationTestTemplate {

    private EmbeddedDatabase db;

    @Before
    public void setUp() {
        // creates an HSQL in-memory database populated from default scripts
        // classpath:schema.sql and classpath:data.sql
        db = new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .addDefaultScripts()
                .build();
    }

    @Test
    public void testDataAccess() {
        JdbcTemplate template = new JdbcTemplate(db);
        template.query( /* ... */ );
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

}
```

### 19.8.6为嵌入式数据库生成唯一名称

如果测试套件无意中尝试重新创建同一数据库的其他实例，则开发团队经常会遇到嵌入式数据库的错误。如果XML配置文件或`@Configuration`类负责创建嵌入式数据库，并且相应的配置随后在同一测试套件中的多个测试场景中重复使用（即，在同一JVM进程中），则可以非常容易地发生这种情况- 例如，集成针对嵌入式数据库的测试，其 `ApplicationContext`配置仅针对哪些bean定义配置文件处于活动状态而不同

这种错误的根本原因是Spring `EmbeddedDatabaseFactory`（由`<jdbc:embedded-database>`XML命名空间元素和 `EmbeddedDatabaseBuilder`Java Config内部使用）将设置嵌入式数据库的名称（ `"testdb"`如果没有另外指定）。对于这种情况`<jdbc:embedded-database>`，嵌入式数据库通常被赋予一个等于bean的名称`id`（即，通常是类似的`"dataSource"`）。因此，后续创建嵌入式数据库的尝试不会产生新的数据库。相反，将重用相同的JDBC连接URL，并且尝试创建新的嵌入式数据库实际上将指向从相同配置创建的现有嵌入式数据库。

为了解决这个常见问题，Spring Framework 4.2支持为嵌入式数据库生成 *唯一*名称。要启用生成的名称，请使用以下选项之一。

- `EmbeddedDatabaseFactory.setGenerateUniqueDatabaseName()`
- `EmbeddedDatabaseBuilder.generateUniqueName()`
- `<jdbc:embedded-database generate-name="true" … >`

### 19.8.7扩展嵌入式数据库支持

Spring JDBC嵌入式数据库支持可以通过两种方式扩展：

- 实现`EmbeddedDatabaseConfigurer`以支持新的嵌入式数据库类型。
- 实现`DataSourceFactory`以支持新`DataSource`实现，例如用于管理嵌入式数据库连接的连接池。

我们鼓励您在[jira.spring.io](https://jira.spring.io/browse/SPR)上为Spring社区贡献回扩展 。