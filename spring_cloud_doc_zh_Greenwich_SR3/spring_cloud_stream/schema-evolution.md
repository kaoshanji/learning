# 33. Schema Evolution Support

## 33.模式演化支持

Spring Cloud Stream提供了对模式演化的支持，因此数据可以随着时间的推移而演化，并且仍然可以与新旧的生产者和消费者一起使用，反之亦然。大多数序列化模型，尤其是旨在跨不同平台和语言进行移植的模型，都依赖于一种描述如何在二进制有效负载中序列化数据的模式。为了序列化数据然后解释它，发送方和接收方都必须有权访问描述二进制格式的模式。在某些情况下，可以从序列化时的有效负载类型或反序列化时的目标类型推断模式。但是，许多应用程序都可以从访问描述二进制数据格式的显式架构中受益。使用模式注册表，您可以以文本格式（通常为JSON）存储模式信息，并使该信息可用于需要它以二进制格式接收和发送数据的各种应用程序。模式可引用为一个元组，该元组包括：

- 主题，是架构的逻辑名称
- 模式版本
- 模式格式，描述数据的二进制格式

以下各节详细介绍了架构演变过程中涉及的各种组件。

## 33.1架构注册表客户端

与架构注册表服务器进行交互的客户端抽象是`SchemaRegistryClient`接口，该接口具有以下结构：

```java
public interface SchemaRegistryClient {

    SchemaRegistrationResponse register(String subject, String format, String schema);

    String fetch(SchemaReference schemaReference);

    String fetch(Integer id);

}
```

Spring Cloud Stream提供了开箱即用的实现，可用于与其自己的模式服务器进行交互以及与Confluent Schema Registry进行交互。

可以使用来配置Spring Cloud Stream模式注册表的客户端`@EnableSchemaRegistryClient`，如下所示：

```java
  @EnableBinding(Sink.class)
  @SpringBootApplication
  @EnableSchemaRegistryClient
  public static class AvroSinkApplication {
    ...
  }
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 对默认转换器进行了优化，不仅可以缓存来自远程服务器的模式，还可以缓存`parse()`and `toString()`方法，这是非常昂贵的。因此，它使用`DefaultSchemaRegistryClient`不缓存响应的。如果要更改默认行为，则可以直接在代码上使用客户端，并将其覆盖为所需的结果。为此，您必须将该属性添加`spring.cloud.stream.schemaRegistryClient.cached=true`到您的应用程序属性。 |

### 33.1.1架构注册表客户端属性

架构注册表客户端支持以下属性：

- `spring.cloud.stream.schemaRegistryClient.endpoint`

  模式服务器的位置。进行设置时，请使用完整的URL，包括协议（`http`或`https`），端口和上下文路径。

- 默认

  `http://localhost:8990/`

- `spring.cloud.stream.schemaRegistryClient.cached`

  客户端是否应缓存架构服务器响应。通常设置为`false`，因为在消息转换器中进行缓存。使用架构注册表客户端的客户端应将此设置为`true`。

- 默认

  `true`

## 33.2 Avro Schema Registry客户端消息转换器

对于已在应用程序上下文中注册了SchemaRegistryClient bean的应用程序，Spring Cloud Stream会自动配置Apache Avro消息转换器以进行模式管理。由于接收消息的应用程序可以轻松访问可以与自己的读取器模式进行协调的写入器模式，因此这简化了模式的演变。

对于出站消息，如果通道的内容类型设置为`application/*+avro`，则将`MessageConverter`激活，如以下示例所示：

```properties
spring.cloud.stream.bindings.output.contentType=application/*+avro
```

在出站转换期间，消息转换器尝试使用推断每个出站消息的模式（基于其类型）并将其注册到主题（基于有效负载类型）`SchemaRegistryClient`。如果已经找到相同的模式，则将检索对其的引用。如果不是，则注册架构，并提供新的版本号。`contentType`通过使用以下方案`application/[prefix].[subject].v[version]+avro`，消息将与标头一起发送：，其中`prefix`可以配置，并`subject`根据有效负载类型推导。

例如，该类型的消息`User`可能作为二进制有效负载发送，其内容类型为`application/vnd.user.v2+avro`，其中`user`主题和`2`版本号。

接收消息时，转换器从传入消息的标头中推断模式引用，并尝试检索它。该模式在反序列化过程中用作编写器模式。

### 33.2.1 Avro Schema注册表消息转换器属性

如果通过设置启用了基于Avro的架构注册表客户端`spring.cloud.stream.bindings.output.contentType=application/*+avro`，则可以通过设置以下属性来自定义注册行为。

- spring.cloud.stream.schema.avro.dynamicSchemaGenerationEnabled

  如果希望转换器使用反射从POJO推断模式，则启用。默认： `false`

- spring.cloud.stream.schema.avro.readerSchema

  Avro通过查看写入器模式（原始有效负载）和读取器模式（您的应用程序有效负载）来比较模式版本。有关更多信息，请参见[Avro文档](https://avro.apache.org/docs/1.7.6/spec.html)。如果设置，它将覆盖模式服务器上的所有查找，并将本地模式用作读取器模式。默认：`null`

- spring.cloud.stream.schema.avro.schema位置

  `.avsc`向Schema Server 注册此属性中列出的所有文件。默认： `empty`

- spring.cloud.stream.schema.avro.prefix

  Content-Type标头上要使用的前缀。默认： `vnd`

## 33.3 Apache Avro消息转换器

Spring Cloud Stream通过其`spring-cloud-stream-schema`模块为基于模式的消息转换器提供支持。当前，基于模式的消息转换器唯一支持的开箱即用的序列化格式是Apache Avro，将来的版本中将添加更多格式。

该`spring-cloud-stream-schema`模块包含两种可用于Apache Avro序列化的消息转换器：

- 转换器使用序列化或反序列化对象的类信息或启动时具有已知位置的模式。
- 使用架构注册表的转换器。它们在运行时定位架构，并随着域对象的发展动态注册新架构。

## 33.4具有模式支持的转换器

`AvroSchemaMessageConverter`通过使用预定义的架构或通过使用类中可用的架构信息（以反射方式或包含在中`SpecificRecord`），支持对消息进行序列化和反序列化。如果提供自定义转换器，则不会创建默认的AvroSchemaMessageConverter bean。以下示例显示了一个自定义转换器：

要使用自定义转换器，您只需将其添加到应用程序上下文中，就可以选择指定一个或多个`MimeTypes`与其关联的对象。默认`MimeType`值为`application/avro`。

如果转换的目标类型是`GenericRecord`，则必须设置一个架构。

以下示例显示了如何通过在`MessageConverter`没有预定义架构的情况下注册Apache Avro在接收器应用程序中配置转换器。在此示例中，请注意，mime类型的值为`avro/bytes`，而不是default `application/avro`。

```java
@EnableBinding(Sink.class)
@SpringBootApplication
public static class SinkApplication {

  ...

  @Bean
  public MessageConverter userMessageConverter() {
      return new AvroSchemaMessageConverter(MimeType.valueOf("avro/bytes"));
  }
}
```

相反，以下应用程序使用预定义的架构（在类路径上找到）注册一个转换器：

```java
@EnableBinding(Sink.class)
@SpringBootApplication
public static class SinkApplication {

  ...

  @Bean
  public MessageConverter userMessageConverter() {
      AvroSchemaMessageConverter converter = new AvroSchemaMessageConverter(MimeType.valueOf("avro/bytes"));
      converter.setSchemaLocation(new ClassPathResource("schemas/User.avro"));
      return converter;
  }
}
```

## 33.5架构注册表服务器

Spring Cloud Stream提供了架构注册表服务器实现。要使用它，可以将`spring-cloud-stream-schema-server`工件添加到项目中并使用`@EnableSchemaRegistryServer`批注，这会将架构注册表服务器REST控制器添加到您的应用程序。该注释旨在与Spring Boot Web应用程序一起使用，并且服务器的监听端口由`server.port`属性控制。该`spring.cloud.stream.schema.server.path`属性可用于控制模式服务器的根路径（尤其是当它嵌入在其他应用程序中时）。该`spring.cloud.stream.schema.server.allowSchemaDeletion`布尔属性允许模式的缺失。默认情况下，这是禁用的。

架构注册表服务器使用关系数据库来存储架构。默认情况下，它使用嵌入式数据库。您可以使用[Spring Boot SQL数据库和JDBC配置选项](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-sql)来自定义模式存储。

以下示例显示了启用架构注册表的Spring Boot应用程序：

```java
@SpringBootApplication
@EnableSchemaRegistryServer
public class SchemaRegistryServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchemaRegistryServerApplication.class, args);
    }
}
```

### 33.5.1架构注册表服务器API

Schema Registry Server API包含以下操作：

- `POST /` —请参阅“ [称为“注册新架构”的部分](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-registering-new-schema) ”
- 'GET / {subject} / {format} / {version}'-参见“ [称为“按主题，格式和版本检索现有模式的部分”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-retrieve-schema-subject-format-version) ”
- `GET /{subject}/{format}` —请参阅“ [称为“按主题和格式检索现有模式的部分”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-retrieve-schema-subject-format) ”
- `GET /schemas/{id}` —请参阅“ [称为“通过ID检索现有模式](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-retrieve-schema-id) ” [的部分”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-retrieve-schema-id) ”
- `DELETE /{subject}/{format}/{version}` —请参阅“ [名为“按主题，格式和版本删除架构的部分”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-deleting-schema-subject-format-version) ”
- `DELETE /schemas/{id}` —请参阅“ [称为“通过ID删除架构](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-deleting-schema-id) ” [的部分”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-deleting-schema-id) ”
- `DELETE /{subject}` —请参阅“ [称为“按主题删除架构的部分”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-deleting-schema-subject) ”

#### 注册新架构

要注册新模式，`POST`请向`/`端点发送请求。

的`/`接受与以下字段的有效载荷JSON：

- `subject`：架构主题
- `format`：架构格式
- `definition`：模式定义

它的响应是JSON中的架构对象，具有以下字段：

- `id`：模式ID
- `subject`：架构主题
- `format`：架构格式
- `version`：模式版本
- `definition`：模式定义

#### 通过主题，格式和版本检索现有架构

要按主题，格式和版本检索现有模式，请将`GET`请求发送到`/{subject}/{format}/{version}`端点。

它的响应是JSON中的架构对象，具有以下字段：

- `id`：模式ID
- `subject`：架构主题
- `format`：架构格式
- `version`：模式版本
- `definition`：模式定义

#### 通过主题和格式检索现有模式

要按主题和格式检索现有模式，`GET`请向`/subject/format`端点发送请求。

它的响应是JSON中每个模式对象的模式列表，其中包含以下字段：

- `id`：模式ID
- `subject`：架构主题
- `format`：架构格式
- `version`：模式版本
- `definition`：模式定义

#### 通过ID检索现有架构

要通过其ID检索模式，`GET`请向`/schemas/{id}`端点发送请求。

它的响应是JSON中的架构对象，具有以下字段：

- `id`：模式ID
- `subject`：架构主题
- `format`：架构格式
- `version`：模式版本
- `definition`：模式定义

#### 按主题，格式和版本删除架构

要删除由其主题，格式和版本标识的模式，`DELETE`请向`/{subject}/{format}/{version}`端点发送请求。

#### 通过ID删除架构

要通过其ID删除模式，`DELETE`请向`/schemas/{id}`端点发送请求。

#### 按主题删除架构

```
DELETE /{subject}
```

按主题删除现有架构。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 本说明仅适用于Spring Cloud Stream 1.1.0.RELEASE的用户。Spring Cloud Stream 1.1.0.RELEASE使用表名称`schema`来存储`Schema`对象。`Schema`是许多数据库实现中的关键字。为了避免将来发生任何冲突，从1.1.1.RELEASE开始，我们选择`SCHEMA_REPOSITORY`了存储表的名称。任何升级的Spring Cloud Stream 1.1.0.RELEASE用户都应在升级之前将其现有模式迁移到新表。 |

### 33.5.2使用Confluent的架构注册表

默认配置创建一个`DefaultSchemaRegistryClient`bean。如果要使用Confluent模式注册表，则需要创建一个类型为bean的bean `ConfluentSchemaRegistryClient`，该bean 取代框架默认情况下配置的bean 。以下示例显示了如何创建这样的bean：

```java
@Bean
public SchemaRegistryClient schemaRegistryClient(@Value("${spring.cloud.stream.schemaRegistryClient.endpoint}") String endpoint){
  ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient();
  client.setEndpoint(endpoint);
  return client;
}
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| ConfluentSchemaRegistryClient已针对Confluent平台4.0.0版进行了测试。 |

## 33.6模式注册和解析

为了更好地了解Spring Cloud Stream如何注册和解析新模式及其对Avro模式比较功能的使用，我们提供了两个单独的小节：

- “ [第33.6.1节“模式注册过程（序列化）”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-schema-registration-process) ”
- “ [第33.6.2节“模式解析过程（反序列化）”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-schema-resolution-process) ”

### 33.6.1模式注册过程（序列化）

注册过程的第一部分是从通过通道发送的有效负载中提取模式。Avro类型（例如`SpecificRecord`或`GenericRecord`已经包含一个架构）可以立即从实例中检索。对于POJO，如果`spring.cloud.stream.schema.avro.dynamicSchemaGenerationEnabled`属性设置为`true`（默认值），则将推断模式。



**图33.1 模式编写器解析过程**

![模式解析](../images/schema_resolution.png)



获得一个模式，转换器从远程服务器加载其元数据（版本）。首先，它查询本地缓存。如果未找到结果，它将数据提交给服务器，服务器将提供版本信息。转换器始终缓存结果，以避免为每个需要序列化的新消息查询Schema Server的开销。



**图33.2。模式注册过程**

![注册](../images/registration.png)



转换器使用模式版本信息来设置`contentType`消息的标头以携带版本信息，例如：`application/vnd.user.v1+avro`。

### 33.6.2模式解析过程（反序列化）

当读取包含版本信息的消息（即`contentType`具有类似“ [第33.6.1节，“模式注册过程（序列化”](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-schema-registration-process) ”）[”中](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi_schema-evolution.html#spring-cloud-stream-overview-schema-registration-process)所述的方案的标头）时，转换器将查询Schema服务器以获取以下消息的编写者模式：消息。一旦找到了传入消息的正确架构，它将检索阅读器架构，并使用Avro的架构解析支持将其读入阅读器定义（设置默认值和所有缺少的属性）。



**图33.3 模式阅读解析过程**

![模式阅读](../images/schema_reading.png)



| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 您应该了解编写者架构（编写消息的应用程序）和阅读者架构（接收应用程序）之间的区别。我们建议[花点时间](https://avro.apache.org/docs/1.7.6/spec.html)阅读[Avro术语](https://avro.apache.org/docs/1.7.6/spec.html)并了解其过程。Spring Cloud Stream始终会获取编写者架构，以确定如何读取消息。如果要使Avro的模式演变支持正常工作，则需要确保`readerSchema`已为应用程序正确设置。 |