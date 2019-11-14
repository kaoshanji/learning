# 74. Install Zookeeper

## 74.安装Zookeeper

请参阅[安装文档](https://zookeeper.apache.org/doc/current/zookeeperStarted.html)以获取有关如何安装Zookeeper的说明。

Spring Cloud Zookeeper在后台使用Apache Curator。尽管Zookeeper开发团队仍将Zookeeper 3.5.x视为“测试版”，但现实是许多用户在生产中使用了它。但是，Zookeeper 3.4.x也用于生产中。在Apache Curator 4.0之前，两个版本的Apache Curator支持两种版本的Zookeeper。从Curator 4.0开始，通过相同的Curator库支持两个版本的Zookeeper。

如果您要与版本3.4集成，则需要更改随附的Zookeeper依赖项，`curator`因此`spring-cloud-zookeeper`。为此，只需排除该依赖性并添加如下所示的3.4.x版本。

**专家。** 

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zookeeper-all</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.12</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```



**摇篮。** 

```json
compile('org.springframework.cloud:spring-cloud-starter-zookeeper-all') {
  exclude group: 'org.apache.zookeeper', module: 'zookeeper'
}
compile('org.apache.zookeeper:zookeeper:3.4.12') {
  exclude group: 'org.slf4j', module: 'slf4j-log4j12'
}
```