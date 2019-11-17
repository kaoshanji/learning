# 20、开发过程中的工具

Spring Boot包含一组额外的工具，这些工具可以使应用程序开发体验更加愉快。该`spring-boot-devtools`模块可以包含在任何项目中，以提供其他开发时功能。要包括devtools支持，请将模块依赖项添加到您的构建中，如以下Maven和Gradle清单所示：

**Maven。**

```xml
<dependencies>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-devtools</artifactId>
		<optional>true</optional>
	</dependency>
</dependencies>
```