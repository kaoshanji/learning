#   配置示例：mysql


##  效果

把所有应用的配置信息放在MySql上统一管理

##  项目列表
-   cloud-config-service：服务配置中心服务端，第一个启动
-   example-config-client：服务配置客户端示例，其次启动

spring cloud：
-   spring-cloud-config-server：配置管理中心服务端
-   spring-cloud-starter-config：配置管理客户端


##  访问

-   客户端REST查看配置信息：http://localhost:7200/from
    -   对应的是：application-dev.properties 里面的信息
    -   效果：![20190517113712](../images/20190517113712.png)
-   服务端查看：http://localhost:7100/example-config-client/test
    -   上述路径结尾是test，查看application-test.properties
    -   换成：dev、prod试试
    -   效果：![20190517113537](../images/20190517113537.png)


##  备注

添加mysql依赖，设置 JDBC，以及Sql语句即可

数据库表，表名可以自定义，但是需要与配置里面一致

````sql
DROP TABLE IF EXISTS `config_info`;
CREATE TABLE `config_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(50) DEFAULT NULL,
  `value` varchar(500) DEFAULT NULL,
  `application` varchar(50) DEFAULT NULL,
  `profile` varchar(50) DEFAULT NULL,
  `label` varchar(50) DEFAULT NULL,
  `remark` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

INSERT INTO `config_info` VALUES ('1', 'from', 'git-dev-client-1.1', 'example-config-client', 'dev', 'master', null);
INSERT INTO `config_info` VALUES ('2', 'from', 'git-test-client-1.0', 'example-config-client', 'test', 'master', null);
````


