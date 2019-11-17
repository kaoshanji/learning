## 52.启用生产就绪功能

该[`spring-boot-actuator`](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-actuator)模块提供了Spring Boot生产就绪的所有功能。启用功能的最简单方法是向`spring-boot-starter-actuator`“启动器” 添加依赖项。

**执行器的定义**

致动器是制造术语，是指用于移动或控制某些物体的机械设备。执行器可以通过很小的变化产生大量的运动。

要将执行器添加到基于Maven的项目中，请添加以下“ Starter”依赖项：

```xml
<dependencies>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-actuator</artifactId>
	</dependency>
</dependencies>
```