#   JDBC

JDBC API 是一种 Java API，用于访问几乎任何类型的表格数据，由一组用 Java 编写的类和接口组成。

JDBC API 是 Java 语言与各种数据库之间无关连接的行业标准。

JDBC API 是一种执行SQL语句的API，JDBC 驱动才是真正的接口实现，所有的网络逻辑和特定于数据库的通信协议都隐藏和独立于供应商的JDBC API后面。

sun 公司只是提供 JDBC API，每个数据库厂商都有自己的驱动来连接自己家的数据库。

-   [JDBC驱动](https://www.processon.com/view/link/5e801456e4b03b99653ded51)

JDBC API 采用了桥接的设计模式，提供了两套接口，JDBC  Driver Planager 面向数据库厂商，如 Oracle、MySQL等； JDBC API 面向 JDBC 使用者，如 Java 应用。

##  目录
-   [背景](s010x.md)
-   [主要问题](s020x.md)
-   [解决方案](s030x.md)
-   [设计实现](s040x.md)
-   [应用模式](s050x.md)


##  典型案例
-   [MyBatis](https://github.com/kaoshanji/learning/tree/master/server/lang/l001/j004/mybatis)
-   Hikaricp/Druid

----

