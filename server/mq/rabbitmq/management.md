# 管理

- 管理内容
  - 启动与停止
  - 配置文件
  - 用户及权限
  - 交换器
  - 队列

**启动、停止节点**

安装目录：/usr/lib/rabbitmq

节点指代 RabbitMQ 服务器实例，描述的是一个 Erlang 节点运行着一个 Erlang 应用程序。

启动： RabbitMQ 安装目录下找到 ./sbin 目录，运行 ./rabbitmq-server，后台运行：./rabbitmq-server -detached

出现任何错误：/var/log/rabbitme/...log

停止：RabbitMQ 安装目录下 ./sbin/rabbitmqctl stop(停掉了RabbitMQ和Erlang节点)，./rabbitmqctl stop_app(仅停掉RabbitMQ)

// 服务状态
sudo service rabbitmq-server status

// 停止
sudo service rabbitmq-server stop

// 开启
sudo service rabbitmq-server start

**配置文件**

允许设置系统范围的可调参数并通过配置文件进行设置。

地址：/etc/rabbitmq/rabbitmq.config，这个文件以JSON格式化一下就清晰了

允许更改 RabbitMQ 运作的方方面面，除了访问控制。


**请求许可**

首先创建用户，然后为其赋予权限，有一套访问控制列表(ACL)风格的权限系统。

- 控制粒度：
  - 被授予访问权限的用户
  - 权限控制应用的 vhost
  - 需要授予的读/写/配置权限的组合
  - 权限范围：客户端/服务端命名队列、交换器

这些是以 vhost 为界限，如果在两个 vhost 配置相同的权限，需要配置两边。


**Web端管理**

启用 Management插件 开启管理界面

- 主要组成
  - 服务器数据统计概览：以投递的消息，服务器内存信息
  - 导入/导出服务器配置
  - 监控服务器连接
  - 打开的信道列表
  - 交换器 列表和添加
  - 队列 列表和添加、绑定
  - 用户 列表和添加
  - vhost 列表和添加

该插件包含在安装包里，只需要启用不需要安装

查看插件：安装目录，`ls plugins/`

地址： http://localhost:5672/ ， 用户名/密码： guest/guest

- 地址： http://localhost:5672/ 
  - Web UI：管理界面
  - HTTP API：api接口，与UI界面相容

----