# 6.主机管理

### 介绍

在**Tomcat的主机管理**应用程序，您可以创建，删除，否则在Tomcat管理虚拟主机。最佳做法是此指南随附以下文档：

- [虚拟主机操作方法，以](virtual-hosting-howto.md)获取有关虚拟主机的更多信息。
- [主机容器](http://tomcat.apache.org/tomcat-9.0-doc/config/host.html)，以获取有关虚拟主机的基础xml配置和属性描述的更多信息。

在**Tomcat的主机管理**应用程序是Tomcat安装的一部分，默认情况下可以通过如下的背景：`/host-manager`。您可以通过以下方式使用主机管理器：

- 利用图形用户界面，可从以下位置访问 `{server}:{port}/host-manager/html`。
- 使用一组适合脚本编写的最小HTTP请求。您可以在以下位置访问此模式： `{server}:{port}/host-manager/text`。

两种方式都可以使您添加，删除，启动和停止虚拟主机。可以通过使用该`persist`命令来进行更改。本文档重点介绍文本界面。有关图形界面的更多信息，请参阅 [Host Manager App-HTML Interface](http://tomcat.apache.org/tomcat-9.0-doc/html-host-manager-howto.html)。

### 配置Manager应用程序访问

*下面的描述$CATALINA_HOME用于引用基本的Tomcat目录。这是安装Tomcat的目录，例如C:\tomcat9或 /usr/share/tomcat9。*

主机管理器应用程序要求用户具有以下角色之一：

- `admin-gui` -将此角色用于图形Web界面。
- `admin-script` -在脚本Web界面中使用此角色。

要允许访问主机管理器应用程序的文本界面，请为Tomcat用户授予适当的角色，或者创建一个具有正确角色的新角色。例如，打开 `${CATALINA_BASE}/conf/tomcat-users.xml`并输入以下内容：

```xml
<user username="test" password="chang3m3N#w" roles="admin-script"/>
```

无需其他设置。现在访问时 `{server}:{port}/host-manager/text/${COMMAND}`，便可以使用创建的凭据登录。例如：

```xml
$ curl -u ${USERNAME}:${PASSWORD} http://localhost:8080/host-manager/text/list
OK - Listed hosts
localhost:
```



请注意，如果您使用，或 机制来检索用户 `DataSourceRealm`，请分别在数据库或目录服务器中添加适当的角色。 `JDBCRealm``JNDIRealm`

### 命令清单

支持以下命令：

- list
- add
- remove
- start
- stop
- persist

在以下小节中，假定用户名和密码为 **test：test**。对于您的环境，请使用前面几节中创建的凭据。

#### 清单命令

使用**list**命令查看Tomcat实例上的可用虚拟主机。

*示例命令*：

```bash
curl -u test:test http://localhost:8080/host-manager/text/list
```

*响应示例*：

```bash
OK - Listed hosts
localhost:
```

#### 添加命令

使用**add**命令添加新的虚拟主机。用于**add**命令的参数：

- 字符串**名称**：虚拟主机的名称。**需要**
- 字符串**别名**：虚拟主机的别名。
- 字符串**appBase**：此虚拟主机将服务的应用程序的基本路径。提供相对或绝对路径。
- 布尔值**管理器**：如果为true，则将Manager应用程序添加到虚拟主机。您可以使用*/ manager*上下文访问它。
- 布尔值**autoDeploy**：如果为true，则Tomcat自动重新部署放置在appBase目录中的应用程序。
- 布尔值**deployOnStartup**：如果为true，则Tomcat在启动时会自动部署放置在appBase目录中的应用程序。
- 布尔值**deployXML**：如果为true，则将 读取*/META-INF/context.xml*文件并由Tomcat使用。
- 布尔值**copyXML**：如果为true，则Tomcat复制*/META-INF/context.xml* 文件并使用原始副本，而不管应用程序的*/META-INF/context.xml*文件是否更新 。

*示例命令*：

```bash
curl -u test:test http://localhost:8080/host-manager/text/add?name=www.awesomeserver.com&aliases=awesomeserver.com&appBase/mnt/appDir&deployOnStartup=true
```

*响应示例*：

```bash&#39;
add: Adding host [www.awesomeserver.com]
```

#### 删除命令

使用**remove**命令删除虚拟主机。用于**remove**命令的参数：

- 字符串**名称**：要删除的虚拟主机的名称。 **需要**

*示例命令*：

```
curl -u test:test http://localhost:8080/host-manager/text/remove?name=www.awesomeserver.com
```

*响应示例*：

```
remove: Removing host [www.awesomeserver.com]
```

#### 启动命令

使用**start**命令启动虚拟主机。用于**启动**命令的参数：

- 字符串**名称**：要启动的虚拟主机的名称。 **需要**

*示例命令*：

```
curl -u test:test http://localhost:8080/host-manager/text/start?name=www.awesomeserver.com
```

*响应示例*：

```
OK - Host www.awesomeserver.com started
```

#### 停止指令

使用**stop**命令停止虚拟主机。用于**stop**命令的参数：

- 字符串**名称**：要停止的虚拟主机的名称。 **需要**

*示例命令*：

```
curl -u test:test http://localhost:8080/host-manager/text/stop?name=www.awesomeserver.com
```

*响应示例*：

```
OK - Host www.awesomeserver.com stopped
```

#### 坚持命令

使用**persist**命令将虚拟主机持久存储到 **server.xml中**。用于**持久**命令的参数：

- 字符串**名称**：要保留的虚拟主机的名称。 **需要**

默认情况下禁用此功能。要启用此选项，必须首先配置`StoreConfigLifecycleListener`侦听器。为此，请将以下侦听器添加到*server.xml中*：

```xml
<Listener className="org.apache.catalina.storeconfig.StoreConfigLifecycleListener"/>
```

*示例命令*：

```
curl -u test:test http://localhost:8080/host-manager/text/persist?name=www.awesomeserver.com
```

*响应示例*：

```
OK - Configuration persisted
```

*手动输入示例*：

```xml
<Host appBase="www.awesomeserver.com" name="www.awesomeserver.com" deployXML="false" unpackWARs="false">
</Host>
```