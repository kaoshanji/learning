#   JDBC

JDBC 是一个Java标准规范接口，提供连接关系型数据库的功能。

##  背景

Java运行时包含的数据对象，都是放在内存中，当程序关闭重启时，这些数据就没有了，这自然是不可以！

- 出现原因

需要一种方法，在重新运行时能够把关闭前的数据对象给找回来，这就是持久化，把数据放在硬盘上面，就是操作系统重启也没关系。

另外，程序运行时内存毕竟是有限的，而数据对象不知道有多少，全都放在内存也不合适，运行时需要的数据在内存中就可以了。

- 主要优势

数据库是专门存储数据，把这些数据对象放在数据库里面。然后，就需要一个接口，让Java程序能够连接数据库服务器，这就是 JDBC 库，他是面向关系型数据库，如：[MySql](https://www.mysql.com/)、[PostgreSQL](https://www.postgresql.org/) 等。

通过JDBC，Java程序在重启之后，还可以恢复原来的数据对象状态。

----

##  主要问题

需要搭建运行环境，依赖外部中间件，存在网络通信

数据库服务器需要专门管理，情况变得有些复杂

----

##  解决方案

> 使用 MySql 作为数据库服务器

从Java的角度看，MySql是一个存放数据的地方。

从MySql的角度看，Java是一种客户端，使用部分功能

- 搭建运行环境

1.  MySql 服务器

本地安装或云上运行的 MySql 服务器

使用某种MySql服务器的客户端连接，并且成功

2.  下载 Java版的MySql驱动包加入项目

3.  记录连接MySql服务器时使用的信息：地址、端口、用户名和密码

客户端连接MySql服务器的信息同样在 Java 里需要。

----

##  设计实现

- [JDBC 语法](https://www.processon.com/view/link/5e52131ee4b0cc44b5a6d8cf)

----

##  应用模式

- [JDBC 流程](https://www.processon.com/view/link/5dc8c8fbe4b07548229edd7e)

- Java对象属性与数据库表列字段相互映射
- 存入数据库时，使用Java对象属性值拼接SQL语句，发送给数据库执行
- 读取数据库时，使用查询SQL语句，解析响应结果，初始化Java对象
- Java对象名称一般对应数据库表名
- Java对象属性名称一般对应数据库表列字段
- Java对象属性类型与数据库表列字段可能不匹配

----

##  典型案例
- [MyBatis](https://github.com/kaoshanji/learning/tree/master/server/lang/l001/j004/mybatis)
- Spring MVC
- Hibernate

----

