# 21. HTTP Clients

## 21. HTTP客户端

Spring Cloud Netflix会自动为您创建Ribbon，Feign和Zuul使用的HTTP客户端。但是，您也可以根据需要提供自定义的HTTP客户端。为此，`ClosableHttpClient`如果您使用的是Apache Http Cient或`OkHttpClient`使用OK HTTP ，则可以创建一个类型的bean 。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 创建自己的HTTP客户端时，您还负责为这些客户端实施正确的连接管理策略。这样做不当会导致资源管理问题。 |