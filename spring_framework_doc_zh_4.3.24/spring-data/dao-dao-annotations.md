# 18.3 注解DAO或资源类

保证数据访问对象（DAO）或存储库提供异常转换的最佳方法是使用`@Repository`注释。此批注还允许组件扫描支持查找和配置DAO和存储库，而无需为它们提供XML配置条目。

```java
@Repository
public class SomeMovieFinder implements MovieFinder {
    // ...
}
```

任何DAO或存储库实现都需要访问持久性资源，具体取决于所使用的持久性技术; 例如，基于JDBC的存储库需要访问JDBC `DataSource`; 基于JPA的存储库需要访问`EntityManager`。做到这一点最简单的方法就是有这个资源的依赖注射的使用的一个`@Autowired,`，`@Inject`，`@Resource`或`@PersistenceContext` 注解。以下是JPA存储库的示例：

```java
@Repository
public class JpaMovieFinder implements MovieFinder {

    @PersistenceContext
    private EntityManager entityManager;

    // ...

}
```

如果您使用的是经典的Hibernate API，那么您可以注入SessionFactory：

```java
@Repository
public class HibernateMovieFinder implements MovieFinder {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // ...

}
```

我们在这里展示的最后一个例子是典型的JDBC支持。您将 `DataSource`注入初始化方法，您可以使用此方法创建一个 `JdbcTemplate`和其他数据访问支持类。`SimpleJdbcCall``DataSource`

```java
@Repository
public class JdbcMovieFinder implements MovieFinder {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void init(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // ...

}
```

