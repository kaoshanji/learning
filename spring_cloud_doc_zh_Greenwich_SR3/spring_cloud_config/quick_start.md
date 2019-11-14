# 4. Quick Start

## 4.快速入门

该快速入门介绍了如何同时使用Spring Cloud Config Server的服务器和客户端。

首先，启动服务器，如下所示：

```bash
$ cd spring-cloud-config-server
$ ../mvnw spring-boot:run
```

该服务器是Spring Boot应用程序，因此，如果愿意，您可以从IDE中运行它（主类是`ConfigServerApplication`）。

接下来尝试一个客户端，如下所示：

```bash
$ curl localhost:8888/foo/development
{"name":"foo","label":"master","propertySources":[
  {"name":"https://github.com/scratches/config-repo/foo-development.properties","source":{"bar":"spam"}},
  {"name":"https://github.com/scratches/config-repo/foo.properties","source":{"foo":"bar"}}
]}
```

定位属性源的默认策略是克隆git存储库（位于`spring.cloud.config.server.git.uri`）并使用它初始化mini `SpringApplication`。迷你应用程序`Environment`用于枚举属性源并将其发布在JSON端点上。

HTTP服务具有以下形式的资源：

```properties
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```

其中`application`注入作为`spring.config.name`在`SpringApplication`（什么是通常`application`以规则的弹簧引导应用程序），`profile`是一个有效简表（或逗号分隔的属性列表），并且`label`是一个可选的git标签（默认为`master`）。

Spring Cloud Config Server从各种来源为远程客户端提取配置。以下示例从git存储库（必须提供）中获取配置，如以下示例所示：

```properties
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
```

其他来源包括任何与JDBC兼容的数据库，Subversion，Hashicorp Vault，Credhub和本地文件系统。

## 4.1客户端使用

要在应用程序中使用这些功能，您可以将其构建为依赖于spring-cloud-config-client的Spring Boot应用程序（例如，请参阅config-client或示例应用程序的测试用例）。添加依赖项最方便的方法是使用Spring Boot启动器`org.springframework.cloud:spring-cloud-starter-config`。`spring-cloud-starter-parent`对于Maven用户，还有一个父pom和BOM（），对于Gradle和Spring CLI用户，还有一个Spring IO版本管理属性文件。以下示例显示了典型的Maven配置：

**pom.xml。** 

```xml
   <parent>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-parent</artifactId>
       <version>{spring-boot-docs-version}</version>
       <relativePath /> <!-- lookup parent from repository -->
   </parent>

<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-dependencies</artifactId>
			<version>{spring-cloud-version}</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>

<dependencies>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-config</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-test</artifactId>
		<scope>test</scope>
	</dependency>
</dependencies>

<build>
	<plugins>
           <plugin>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-maven-plugin</artifactId>
           </plugin>
	</plugins>
</build>

   <!-- repositories also needed for snapshots and milestones -->
```



现在，您可以创建一个标准的Spring Boot应用程序，例如以下HTTP服务器：

```java
@SpringBootApplication
@RestController
public class Application {

    @RequestMapping("/")
    public String home() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

当此HTTP服务器运行时，它将从端口8888上的默认本地配置服务器（如果正在运行）中拾取外部配置。要修改启动行为，可以使用以下命令更改配置服务器的位置`bootstrap.properties`（类似于，`application.properties`但对于应用程序上下文的引导阶段），如以下示例所示：

```bash
spring.cloud.config.uri: http://myconfigserver.com
```

默认情况下，如果未设置应用程序名称，`application`将使用。要修改名称，可以将以下属性添加到`bootstrap.properties`文件中：

```bash
spring.application.name: myapp
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 设置属性时，`${spring.application.name}`请勿在您的应用名称前加上保留字，`application-`以防止解析正确的属性源时出现问题。 |

引导程序属性在`/env`端点中显示为高优先级属性源，如以下示例所示。

```bash
$ curl localhost:8080/env
{
  "profiles":[],
  "configService:https://github.com/spring-cloud-samples/config-repo/bar.properties":{"foo":"bar"},
  "servletContextInitParams":{},
  "systemProperties":{...},
  ...
}
```

名为的属性源```configService:/`包含`foo`值为`bar`且具有最高优先级的属性。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 属性源名称中的URL是git存储库，而不是配置服务器URL。          |