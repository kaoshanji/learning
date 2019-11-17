# 67. Consul Agent

## 67.领事代理

Consul Agent客户端必须对所有Spring Cloud Consul应用程序都可用。默认情况下，代理客户端应位于`localhost:8500`。有关如何启动代理客户端以及如何连接到Consul代理服务器群集的详细信息，请参阅[代理文档](https://consul.io/docs/agent/basics.html)。为了进行开发，在安装consul之后，可以使用以下命令启动Consul代理：

```bash
./src/main/bash/local_run_consul.sh
```

这将在服务器模式下的端口8500上启动代理，并且ui可从[http：// localhost：8500获得。](http://localhost:8500/)

