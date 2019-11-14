# 120. Troubleshooting

## 120.故障排除

## 120.1日志级别

以下是一些有用的记录器，其中包含在`DEBUG`和`TRACE`级别记录信息的宝贵故障。

- `org.springframework.cloud.gateway`
- `org.springframework.http.server.reactive`
- `org.springframework.web.reactive`
- `org.springframework.boot.autoconfigure.web`
- `reactor.netty`
- `redisratelimiter`

## 120.2窃听

反应堆的Netty `HttpClient`和`HttpServer`可具有窃听功能。当与将`reactor.netty`日志级别设置为`DEBUG`或结合使用时，`TRACE`将启用对信息的记录，例如通过网络发送和接收的标头和正文。要启用此功能，请 分别为和设置`spring.cloud.gateway.httpserver.wiretap=true`和/或 。`spring.cloud.gateway.httpclient.wiretap=true``HttpServer``HttpClient`