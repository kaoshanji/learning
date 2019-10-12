# 28、JSON

Spring Boot提供了与三个JSON映射库的集成：

- Gson
- Jackson
- JSON-B

Jackson是首选的默认库。

## 28.1 Jackson

提供了Jackson的自动配置，并且Jackson是的一部分`spring-boot-starter-json`。当Jackson放在类路径上时，将`ObjectMapper`自动配置Bean。提供了几个配置属性，用于[自定义的配置`ObjectMapper`](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/howto-spring-mvc.html#howto-customize-the-jackson-objectmapper)。

## 28.2 Gson

提供了Gson的自动配置。当Gson在类路径上时，将`Gson`自动配置一个bean。`spring.gson.*`提供了几个配置属性用于自定义配置。为了获得更多控制权，`GsonBuilderCustomizer`可以使用一个或多个bean。

## 28.3 JSON-B

提供了JSON-B的自动配置。当JSON-B API和实现位于类路径上时，`Jsonb`将自动配置Bean。首选的JSON-B实现是提供依赖管理的Apache Johnzon。