# 115. TLS / SSL

## 115. TLS / SSL

网关可以通过遵循常规的Spring服务器配置来侦听https上的请求。例：

**application.yml。** 

```properties
server:
  ssl:
    enabled: true
    key-alias: scg
    key-store-password: scg1234
    key-store: classpath:scg-keystore.p12
    key-store-type: PKCS12
```



网关路由可以同时路由到http和https后端。如果路由到https后端，则可以使用以下配置将网关配置为信任所有下游证书：

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      httpclient:
        ssl:
          useInsecureTrustManager: true
```



使用不安全的信任管理器不适用于生产。对于生产部署，可以为网关配置一组可以通过以下配置信任的已知证书：

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      httpclient:
        ssl:
          trustedX509Certificates:
          - cert1.pem
          - cert2.pem
```



如果未为Spring Cloud Gateway提供受信任的证书，则使用默认的信任存储（可以使用系统属性javax.net.ssl.trustStore覆盖）。

## 115.1 TLS握手

网关维护用于路由到后端的客户端池。通过https进行通信时，客户端会启动TLS握手。许多超时与此握手相关联。可以配置以下超时（显示默认值）：

**application.yml。** 

```properties
spring:
  cloud:
    gateway:
      httpclient:
        ssl:
          handshake-timeout-millis: 10000
          close-notify-flush-timeout-millis: 3000
          close-notify-read-timeout-millis: 0
```