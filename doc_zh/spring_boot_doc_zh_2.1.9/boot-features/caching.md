# 33、缓存

Spring框架提供了对向应用程序透明添加缓存的支持。从本质上讲，抽象将缓存应用于方法，从而根据缓存中可用的信息减少执行次数。缓存逻辑是透明应用的，不会对调用者造成任何干扰。只要通过`@EnableCaching`注释启用了缓存支持，Spring Boot就会自动配置缓存基础结构。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 检查Spring Framework参考的[相关部分](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/integration.html#cache)以获取更多详细信息。 |

简而言之，将缓存添加到服务的操作就像将相关注释添加到其方法一样容易，如以下示例所示：

```java
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class MathService {

	@Cacheable("piDecimals")
	public int computePiDecimal(int i) {
		// ...
	}

}
```

本示例说明了在潜在的昂贵操作上使用缓存的方法。在调用之前`computePiDecimal`，抽象将在`piDecimals`高速缓存中寻找与`i`参数匹配的条目。如果找到条目，则高速缓存中的内容会立即返回给调用方，并且不会调用该方法。否则，将调用该方法，并在返回值之前更新缓存。

| ![[警告]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/caution.png) | 警告 |
| ------------------------------------------------------------ | ---- |
| 您还可以`@CacheResult`透明地使用标准JSR-107（JCache）批注（例如）。但是，我们强烈建议您不要混合使用Spring Cache和JCache批注。 |      |

如果您不添加任何特定的缓存库，Spring Boot会自动配置一个使用内存中并发映射的[简单提供程序](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-simple)。当需要缓存时（例如`piDecimals`前面的示例），此提供程序将为您创建它。实际上，不建议将简单的提供程序用于生产用途，但是它对于入门并确保您了解功能非常有用。确定要使用的缓存提供程序后，请确保阅读其文档，以了解如何配置应用程序使用的缓存。几乎所有提供程序都要求您显式配置在应用程序中使用的每个缓存。有些提供了一种自定义`spring.cache.cache-names`属性定义的默认缓存的方法。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 还可以透明地[更新](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/integration.html#cache-annotations-put)或从缓存中[逐出](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/integration.html#cache-annotations-evict)数据。 |

## 33.1支持的缓存提供程序

缓存抽象不提供实际的存储，而是依赖于`org.springframework.cache.Cache`和`org.springframework.cache.CacheManager`接口实现的抽象。

如果尚未定义类型`CacheManager`或`CacheResolver`名称的bean `cacheResolver`（请参阅参考资料[`CachingConfigurer`](https://docs.spring.io/spring/docs/5.1.10.RELEASE/javadoc-api/org/springframework/cache/annotation/CachingConfigurer.html)），Spring Boot会尝试检测以下提供程序（按指示的顺序）：

1. [Generic](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-generic)
2. [JCache (JSR-107)](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-jcache) (EhCache 3, Hazelcast, Infinispan, and others)
3. [EhCache 2.x](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-ehcache2)
4. [Hazelcast](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-hazelcast)
5. [Infinispan](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-infinispan)
6. [Couchbase](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-couchbase)
7. [Redis](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-redis)
8. [Caffeine](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-caffeine)
9. [Simple](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-simple)

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 也可以通过设置属性来*强制*特定的缓存提供程序`spring.cache.type`。如果您需要在某些环境（例如测试）中[完全禁用缓存，](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-none)请使用此属性。 |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 使用`spring-boot-starter-cache`“入门”快速添加基本的缓存依赖项。起动器带进来`spring-context-support`。如果手动添加依赖项，则必须包括`spring-context-support`才能使用JCache，EhCache 2.x或Caffeine支持。 |

如果`CacheManager`Spring Boot自动配置了，您可以通过公开实现该`CacheManagerCustomizer`接口的bean，在完全初始化之前进一步调整其配置。下面的示例设置一个标志，指示`null`应将值向下传递到基础映射：

```java
@Bean
public CacheManagerCustomizer<ConcurrentMapCacheManager> cacheManagerCustomizer() {
	return new CacheManagerCustomizer<ConcurrentMapCacheManager>() {
		@Override
		public void customize(ConcurrentMapCacheManager cacheManager) {
			cacheManager.setAllowNullValues(false);
		}
	};
}
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 在前面的示例中，需要进行自动配置`ConcurrentMapCacheManager`。如果不是这种情况（您提供了自己的配置，或者自动配置了其他缓存提供程序），则根本不会调用定制程序。您可以根据需要拥有任意数量的定制程序，也可以使用`@Order`或对其进行排序`Ordered`。 |

### 33.1.1 Generic

如果上下文定义*了至少*一个`org.springframework.cache.Cache`bean，则使用通用缓存。将`CacheManager`包装所有该类型的bean。

### 33.1.2 JCache（JSR-107）

[JCache](https://jcp.org/en/jsr/detail?id=107)通过`javax.cache.spi.CachingProvider`类路径上的存在进行引导（即，类路径上存在符合JSR-107的缓存库），并且`JCacheCacheManager`由`spring-boot-starter-cache`“启动器”提供。提供了各种兼容的库，Spring Boot为Ehcache 3，Hazelcast和Infinispan提供了依赖管理。也可以添加任何其他兼容的库。

可能会出现多个提供者，在这种情况下，必须明确指定提供者。即使JSR-107标准没有强制采用标准化的方式来定义配置文件的位置，Spring Boot也会尽其所能以设置带有实现细节的缓存，如以下示例所示：

```bash
   # Only necessary if more than one provider is present
spring.cache.jcache.provider=com.acme.MyCachingProvider
spring.cache.jcache.config=classpath:acme.xml
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 当缓存库同时提供本机实现和JSR-107支持时，Spring Boot会首选JSR-107支持，因此，如果您切换到其他JSR-107实现，则可以使用相同的功能。 |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| Spring Boot [对Hazelcast](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-hazelcast.html)具有[常规支持](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-hazelcast.html)。如果单个`HazelcastInstance`可用，则`CacheManager`除非`spring.cache.jcache.config`指定了该属性，否则它也会自动重用于。 |

有两种方法可以自定义基础`javax.cache.cacheManager`：

- 可以在启动时通过设置`spring.cache.cache-names`属性来创建缓存。如果定义了定制`javax.cache.configuration.Configuration`bean，则将其用于定制它们。
- `org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer`使用的引用调用bean `CacheManager`进行完全定制。

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 如果`javax.cache.CacheManager`定义了标准bean，它将自动包装在`org.springframework.cache.CacheManager`抽象期望的实现中。不再对其应用定制。 |

### 33.1.3 EhCache 2.x

如果`ehcache.xml`可以在类路径的根目录找到名为的文件，则使用[EhCache2.x](https://www.ehcache.org/)。如果找到EhCache 2.x，则使用“启动器” `EhCacheCacheManager`提供的`spring-boot-starter-cache`启动程序来引导缓存管理器。也可以提供备用配置文件，如以下示例所示：

```bash
spring.cache.ehcache.config=classpath:config/another-config.xml
```

### 33.1.4 Hazelcast

Spring Boot [对Hazelcast](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-hazelcast.html)具有[常规支持](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-hazelcast.html)。如果`HazelcastInstance`已经自动配置了，则会自动将其包装在中`CacheManager`。

### 33.1.5 Infinispan

[Infinispan](https://infinispan.org/)没有默认配置文件位置，因此必须明确指定。否则，将使用默认的引导程序。

```bash
spring.cache.infinispan.config=infinispan.xml
```

可以在启动时通过设置`spring.cache.cache-names`属性来创建缓存。如果定义了定制`ConfigurationBuilder`bean，那么它将用于定制高速缓存。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| Spring Boot对Infinispan的支持仅限于嵌入式模式，并且非常基础。如果您需要更多选择，则应该使用官方的Infinispan Spring Boot启动程序。有关更多详细信息，请参见[Infinispan的文档](https://github.com/infinispan/infinispan-spring-boot)。 |

### 33.1.6 Couchbase

如果[Couchbase](https://www.couchbase.com/) Java客户端和`couchbase-spring-cache`实现可用，并且已[配置](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-nosql.html#boot-features-couchbase) Couchbase ，`CouchbaseCacheManager`则会自动配置a。通过设置该`spring.cache.cache-names`属性，还可以在启动时创建其他缓存。这些缓存在`Bucket`自动配置的上运行。您可以*还*创建另一个附加缓存中`Bucket`，通过使用定制。假设您在“ main”上需要两个缓存（`cache1`和`cache2`），在“ another”上需要`Bucket`一个（`cache3`2）秒的自定义生存时间`Bucket`。您可以通过配置创建前两个缓存，如下所示：

```
spring.cache.cache名称 = cache1，cache2
```

然后，您可以定义一个`@Configuration`类来配置Extra `Bucket`和`cache3`缓存，如下所示：

```java
@Configuration
public class CouchbaseCacheConfiguration {

	private final Cluster cluster;

	public CouchbaseCacheConfiguration(Cluster cluster) {
		this.cluster = cluster;
	}

	@Bean
	public Bucket anotherBucket() {
		return this.cluster.openBucket("another", "secret");
	}

	@Bean
	public CacheManagerCustomizer<CouchbaseCacheManager> cacheManagerCustomizer() {
		return c -> {
			c.prepareCache("cache3", CacheBuilder.newInstance(anotherBucket())
					.withExpiration(2));
		};
	}

}
```

此样本配置重用了`Cluster`通过自动配置创建的。

### 33.1.7 Redis

如果[Redis](https://redis.io/)可用并已配置，`RedisCacheManager`则会自动配置a。可以通过设置`spring.cache.cache-names`属性在启动时创建其他缓存，并且可以使用`spring.cache.redis.*`属性配置缓存默认值。例如，以下配置创建`cache1`和`cache2`缓存的*生存时间为* 10分钟：

```bash
spring.cache.cache-names=cache1,cache2
spring.cache.redis.time-to-live=600000
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 默认情况下，会添加密钥前缀，这样，如果两个单独的缓存使用相同的密钥，则Redis不会有重叠的密钥，也不会返回无效值。如果您创建自己的，我们强烈建议将此设置保持启用状态`RedisCacheManager`。 |

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 您可以通过添加`RedisCacheConfiguration` `@Bean`自己的a来完全控制配置。如果您要自定义序列化策略，这将很有用。 |

### 33.1.8 Caffeine

[Caffeine](https://github.com/ben-manes/caffeine)是对Guava缓存的Java 8重写，它取代了对Guava的支持。如果存在咖啡因，`CaffeineCacheManager`则将`spring-boot-starter-cache`自动配置（由“入门”提供）。缓存可以在启动时通过设置`spring.cache.cache-names`属性来创建，并且可以通过以下方式之一（按指示的顺序）进行自定义：

1. 缓存规范由 `spring.cache.caffeine.spec`
2. `com.github.benmanes.caffeine.cache.CaffeineSpec`定义了一个bean
3. `com.github.benmanes.caffeine.cache.Caffeine`定义了一个bean

例如，以下配置创建`cache1`和`cache2`缓存最大大小为500，*生存时间为* 10分钟

```bash
spring.cache.cache-names=cache1,cache2
spring.cache.caffeine.spec=maximumSize=500,expireAfterAccess=600s
```

如果`com.github.benmanes.caffeine.cache.CacheLoader`定义了bean，它将自动与关联`CaffeineCacheManager`。由于`CacheLoader`将会与缓存管理器管理的*所有*缓存相关联，因此必须将其定义为`CacheLoader<Object, Object>`。自动配置将忽略任何其他通用类型。

### 33.1.9简单

如果找不到其他提供者，`ConcurrentHashMap`则配置使用作为缓存存储的简单实现。如果您的应用程序中不存在任何缓存库，则这是默认设置。默认情况下，将根据需要创建缓存，但是您可以通过设置`cache-names`属性来限制可用缓存的列表。例如，如果只需要`cache1`和`cache2`缓存，则按`cache-names`如下所示设置属性：

```bash
spring.cache.cache-names=cache1,cache2
```

如果这样做，并且您的应用程序使用了未列出的缓存，那么当需要该缓存时，它将在运行时失败，但不会在启动时失败。这类似于使用未声明的缓存时“实际”缓存提供程序的行为。