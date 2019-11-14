# 117. Reactor Netty Access Logs

## 117. Reactor Netty访问日志

要启用Reactor Netty访问日志，请设置`-Dreactor.netty.http.server.accessLogEnabled=true`。（它必须是Java System属性，而不是Spring Boot属性）。

日志系统可以配置为具有单独的访问日志文件。以下是示例登录配置：

**logback.xml。** 

```xml
    <appender name="accessLog" class="ch.qos.logback.core.FileAppender">
        <file>access_log.log</file>
        <encoder>
            <pattern>%msg%n</pattern>
        </encoder>
    </appender>
    <appender name="async" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="accessLog" />
    </appender>

    <logger name="reactor.netty.http.server.AccessLog" level="INFO" additivity="false">
        <appender-ref ref="async"/>
    </logger>
```