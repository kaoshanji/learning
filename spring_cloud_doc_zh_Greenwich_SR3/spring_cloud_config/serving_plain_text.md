# 7. Serving Plain Text

## 7.提供纯文本

`Environment`您的应用程序可能需要使用针对其环境量身定制的通用纯文本配置文件，而不是使用抽象（或使用YAML或属性格式的抽象表示之一）。在配置服务器通过额外的端点提供了这些`/{name}/{profile}/{label}/{path}`，在那里`name`，`profile`和`label`的含义与常规环境终点相同，但是`path`是一个文件名（如`log.xml`）。该端点的源文件与环境端点的定位方式相同。属性和YAML文件使用相同的搜索路径。但是，不是汇总所有匹配资源，而是仅返回要匹配的第一个资源。

找到资源后`${…}`，使用`Environment`提供的应用程序名称，配置文件和标签的有效名称来解析常规格式（）的占位符。这样，资源端点与环境端点紧密集成在一起。考虑以下用于GIT或SVN存储库的示例：

```properties
application.yml
nginx.conf
```

其中，`nginx.conf`如下所示：

```
server {
    listen              80;
    server_name         ${nginx.server.name};
}
```

而`application.yml`像这样：

```
nginx:
  server:
    name: example.com
---
spring:
  profiles: development
nginx:
  server:
    name: develop.com
```

该`/foo/default/master/nginx.conf`资源可能如下：

```
server {
    listen              80;
    server_name         example.com;
}
```

而`/foo/development/master/nginx.conf`像这样：

```
server {
    listen              80;
    server_name         develop.com;
}
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 与用于环境配置的源文件一样，`profile`用来解析文件名。因此，如果要使用特定于配置文件的文件，`/*/development/*/logback.xml`可以通过一个名为`logback-development.xml`（优先于`logback.xml`）的文件来解决。 |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果您不想提供，而是`label`让服务器使用默认标签，则可以提供一个`useDefaultLabel`请求参数。因此，`default`配置文件的前面的示例可能是`/foo/default/nginx.conf?useDefaultLabel`。 |