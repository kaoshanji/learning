# 32、使用NoSQL技术

Spring Data提供了其他项目来帮助您访问各种NoSQL技术，包括：

- [https://spring.io/projects/spring-data-ldap)
- [MongoDB](https://spring.io/projects/spring-data-mongodb)
- [Neo4J](https://spring.io/projects/spring-data-neo4j)
- [Elasticsearch](https://spring.io/projects/spring-data-elasticsearch)
- [Solr](https://spring.io/projects/spring-data-solr)
- [Redis](https://spring.io/projects/spring-data-redis)
- [Gemfire](https://spring.io/projects/spring-data-gemfire) or [Geode](https://spring.io/projects/spring-data-geode)
- [Cassandra](https://spring.io/projects/spring-data-cassandra)
- [Couchbase](https://spring.io/projects/spring-data-couchbase)
- [LDAP](https://spring.io/projects/spring-data-ldap)

Spring Boot为Redis，MongoDB，Neo4j，Elasticsearch，Solr Cassandra，Couchbase和LDAP提供自动配置。您可以使用其他项目，但必须自己进行配置。请参阅相应的参考文档，位于[spring.io/projects/spring-data](https://spring.io/projects/spring-data)。

## 32.1 Redis

[Redis](https://redis.io/)是一个缓存，消息代理和功能丰富的键值存储。Spring Boot为[Lettuce](https://github.com/lettuce-io/lettuce-core/)和[Jedis](https://github.com/xetorthio/jedis/)客户端库以及[Spring Data Redis](https://github.com/spring-projects/spring-data-redis)提供的最基本的抽象提供了基本的自动配置。

有一个`spring-boot-starter-data-redis`“启动器”可以方便地收集依赖关系。默认情况下，它使用[Lettuce](https://github.com/lettuce-io/lettuce-core/)。该启动程序可以处理传统应用程序和响应式应用程序。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 我们还提供了一个`spring-boot-starter-data-redis-reactive`“入门级”软件，可与其他商店保持一致，并提供响应性支持。 |

### 32.1.1连接到Redis

您可以像插入其他任何Spring Bean一样注入自动配置的`RedisConnectionFactory`，`StringRedisTemplate`或Vanilla `RedisTemplate`实例。默认情况下，实例尝试连接到Redis服务器`localhost:6379`。下面的清单显示了这种Bean的示例：

```java
@Component
public class MyBean {

	private StringRedisTemplate template;

	@Autowired
	public MyBean(StringRedisTemplate template) {
		this.template = template;
	}

	// ...

}
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 您还可以注册任意数量的Bean，以实现`LettuceClientConfigurationBuilderCustomizer`更高级的自定义。如果您使用Jedis，`JedisClientConfigurationBuilderCustomizer`也可以使用。 |

如果添加自己`@Bean`的任何一种自动配置类型，它将替换默认类型（除非是`RedisTemplate`，当排除基于Bean名称`redisTemplate`而不是其类型时为）。默认情况下，如果`commons-pool2`在类路径上，则会得到一个池化连接工厂。