# 13、构建系统

强烈建议您选择一个支持[*依赖关系管理*](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-build-systems.html#using-boot-dependency-management)并且可以使用发布到“ Maven Central”存储库的工件的构建系统。我们建议您选择Maven或Gradle。可以使Spring Boot与其他构建系统（例如，Ant）一起使用，但是它们并没有得到很好的支持。



## 13.1依赖管理

每个Spring Boot版本都提供了它所支持的依赖关系的精选列表。实际上，您不需要为构建配置中的所有这些依赖项提供版本，因为Spring Boot会为您管理该版本。当您升级Spring Boot本身时，这些依赖项也会以一致的方式升级。

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 您仍然可以指定版本，并在需要时覆盖Spring Boot的建议。        |

精选列表包含可与Spring Boot一起使用的所有spring模块以及完善的第三方库列表。该列表作为可与[Maven](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-build-systems.html#using-boot-maven-parent-pom)和[Gradle](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-build-systems.html#using-boot-gradle)一起使用的标准[材料清单（`spring-boot-dependencies`）](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-build-systems.html#using-boot-maven-without-a-parent)提供。

| ![[警告]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/warning.png) |
| ------------------------------------------------------------ |
| Spring Boot的每个发行版都与Spring Framework的基本版本相关联。我们**强烈**建议您不要指定其版本。 |

## 13.2 Maven

Maven用户可以从`spring-boot-starter-parent`项目继承以获得合理的默认值。父项目提供以下功能：

- Java 1.8是默认的编译器级别。
- UTF-8源编码。
- 一个[依赖管理部分](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-build-systems.html#using-boot-dependency-management)，从春天启动依赖性继承POM，管理公共依赖的版本。当在自己的pom中使用这些依赖关系时，可以为这些依赖关系省略<version>标记。
- 具有执行ID 的[`repackage`目标](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/maven-plugin/repackage-mojo.html)的`repackage`执行。
- 明智的[资源过滤](https://maven.apache.org/plugins/maven-resources-plugin/examples/filter.html)。
- 明智的插件配置（[exec插件](https://www.mojohaus.org/exec-maven-plugin/)，[Git提交ID](https://github.com/ktoso/maven-git-commit-id-plugin)和[shade](https://maven.apache.org/plugins/maven-shade-plugin/)）。
- 针对`application.properties`和`application.yml`包括特定于配置文件的文件的明智资源过滤（例如`application-dev.properties`和`application-dev.yml`）

请注意，由于`application.properties`和`application.yml`文件都接受Spring样式的占位符（`${…}`），因此Maven过滤已更改为使用`@..@`占位符。（您可以通过设置名为的Maven属性来覆盖它`resource.delimiter`。）

### 13.2.1继承入门级父级

要将您的项目配置为继承自`spring-boot-starter-parent`，请设置`parent`如下：

```xml
<！-从Spring Boot继承默认设置-> 
<parent> 
	<groupId> org.springframework.boot </ groupId> 
	<artifactId> spring-boot-starter-parent </ artifactId> 
	<version> 2.1.9.RELEASE < / version> 
</ parent>
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 您只需要为此依赖项指定Spring Boot版本号。如果导入其他启动器，则可以安全地省略版本号。 |

使用该设置，您还可以通过覆盖自己项目中的属性来覆盖各个依赖项。例如，要升级到另一个Spring Data发布系列，您可以将以下内容添加到您的`pom.xml`：

```xml
<properties>
	<spring-data-releasetrain.version>Fowler-SR2</spring-data-releasetrain.version>
</properties>
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 检查[`spring-boot-dependencies`pom](https://github.com/spring-projects/spring-boot/tree/v2.1.9.RELEASE/spring-boot-project/spring-boot-dependencies/pom.xml)以获取受支持属性的列表。 |

### 13.2.2在没有父POM的情况下使用Spring Boot

并非每个人都喜欢从`spring-boot-starter-parent`POM 继承。您可能需要使用自己的公司标准父级，或者可能希望显式声明所有Maven配置。

如果您不想使用`spring-boot-starter-parent`，则仍然可以通过使用`scope=import`依赖项来保留依赖项管理（而不是插件管理）的好处，如下所示：

```xml
<dependencyManagement>
	<dependencies>
		<dependency>
			<!-- Import dependency management from Spring Boot -->
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-dependencies</artifactId>
			<version>2.1.9.RELEASE</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
```

如上所述，前面的示例设置不允许您使用属性来覆盖各个依赖项。为了达到同样的效果，你需要添加的条目`dependencyManagement`项目的**之前**的`spring-boot-dependencies`条目。例如，要升级到另一个Spring Data发布系列，可以将以下元素添加到`pom.xml`：

```xml
<dependencyManagement>
	<dependencies>
		<!-- Override Spring Data release train provided by Spring Boot -->
		<dependency>
			<groupId>org.springframework.data</groupId>
			<artifactId>spring-data-releasetrain</artifactId>
			<version>Fowler-SR2</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-dependencies</artifactId>
			<version>2.1.9.RELEASE</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 在前面的示例中，我们指定了*BOM*，但是可以以相同的方式覆盖任何依赖项类型。 |

### 13.2.3使用Spring Boot Maven插件

Spring Boot包含一个[Maven插件](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/build-tool-plugins-maven-plugin.html)，可以将项目打包为可执行jar。`<plugins>`如果要使用插件，请将该插件添加到您的部分，如以下示例所示：

```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-maven-plugin</artifactId>
		</plugin>
	</plugins>
</build>
```

| ![[注意]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/note.png) |
| ------------------------------------------------------------ |
| 如果您使用Spring Boot启动器的父pom，则只需添加插件。除非您要更改父级中定义的设置，否则无需对其进行配置。 |

