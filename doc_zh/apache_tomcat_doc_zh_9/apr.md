# 24.APR

Tomcat可以使用[Apache Portable Runtime](https://apr.apache.org/)提供卓越的可伸缩性，性能以及与本机服务器技术的更好集成。Apache Portable Runtime是一个高度可移植的库，它是Apache HTTP Server 2.x的核心。APR有许多用途，包括访问高级IO功能（例如sendfile，epoll和OpenSSL），操作系统级别的功能（生成随机数，系统状态等）以及本机进程处理（共享内存，NT管道和Unix套接字）。

这些功能使Tomcat成为通用的Web服务器，可以更好地与其他本机Web技术集成，并且总体上使Java作为完整的Web服务器平台而不是仅以后端为中心的技术，更加可行。

### 安装

APR支持需要安装三个主要的本机组件：

- APR库
- Tomcat使用的APR的JNI包装器（libtcnative）
- OpenSSL库

#### Windows

Windows二进制文件为tcnative-1提供，tcnative-1是一个静态编译的.dll，其中包括OpenSSL和APR。它可以从以下网址下载[此](https://tomcat.apache.org/download-native.cgi) 为32位或AMD x86-64的二进制文件。在注重安全的生产环境中，建议对OpenSSL，APR和libtcnative-1使用单独的共享dll，并根据安全公告根据需要更新它们。Windows OpenSSL二进制文件是从[OpenSSL官方网站](https://www.openssl.org/)链接的（请参阅相关/二进制文件）。

#### Linux

大多数Linux发行版都会提供APR和OpenSSL的软件包。然后将必须编译JNI包装器（libtcnative）。它取决于APR，OpenSSL和Java标头。

要求：

- APR 1.2+开发标头（libapr1-dev软件包）
- OpenSSL 1.0.2+开发标头（libssl-dev软件包）
- 来自Java兼容的JDK 1.4+的JNI标头
- GNU开发环境（gcc，make）

包装器库源位于`bin/tomcat-native.tar.gz`归档文件的Tomcat二进制捆绑软件中 。安装了构建环境并提取了源归档文件后，可以使用（从包含configure脚本的文件夹中）编译包装器库：

```
./configure && make && make install
```

### APR组件

一旦正确安装了库并且可用于Java（如果加载失败，将显示库路径），Tomcat连接器将自动使用APR。连接器的配置与常规连接器相似，但是具有一些额外的属性，用于配置APR组件。请注意，对于大多数用例，应该对默认值进行适当调整，并且不需要进行其他调整。

启用APR时，还将在Tomcat中启用以下功能：

- 默认情况下，在所有平台上都会安全地生成会话ID（Linux以外的平台都需要使用配置的熵来生成随机数）
- 状态Servlet显示有关Tomcat进程的内存使用率和CPU使用率的OS级统计信息

### APR生命周期侦听器配置

请参阅[侦听器配置](http://tomcat.apache.org/tomcat-9.0-doc/config/listeners.html#APR_Lifecycle_Listener_-_org.apache.catalina.core.AprLifecycleListener)。

### APR连接器配置

#### HTTP / HTTPS

有关HTTP配置，请参阅[HTTP](http://tomcat.apache.org/tomcat-9.0-doc/config/http.html) 连接器配置文档。

有关HTTPS配置，请参阅 [HTTPS](http://tomcat.apache.org/tomcat-9.0-doc/config/http.html#SSL_Support)连接器配置文档。

SSL连接器声明示例为：

```xml
<Connector port="443" maxHttpHeaderSize="8192"
                 maxThreads="150"
                 enableLookups="false" disableUploadTimeout="true"
                 acceptCount="100" scheme="https" secure="true"
                 SSLEnabled="true"
                 SSLCertificateFile="${catalina.base}/conf/localhost.crt"
                 SSLCertificateKeyFile="${catalina.base}/conf/localhost.key" />
```

#### AJP

有关AJP配置，请参阅[AJP](http://tomcat.apache.org/tomcat-9.0-doc/config/ajp.html) 连接器配置文档。