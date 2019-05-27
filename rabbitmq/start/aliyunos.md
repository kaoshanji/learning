# 阿里云OS环境下安装 RabbitMQ

**`阿里云OS`： Ubuntu 18.04 LTS**

````bash

sudo apt-key adv --keyserver "hkps.pool.sks-keyservers.net" --recv-keys "0x6B73A36E6026DFCA"

sudo tee /etc/apt/sources.list.d/bintray.rabbitmq.list <<EOF
deb https://dl.bintray.com/rabbitmq-erlang/debian bionic erlang-21.x
deb https://dl.bintray.com/rabbitmq/debian bionic main
EOF

sudo apt-get update -y

cd  /etc/apt/sources.list.d/

rm -f bintray.rabbitmq.list 

sudo apt-get install rabbitmq-server -y --fix-missing

````

**阿里云开放端口**

实例详情，右边：本实例安全组

添加安全组规则，放行端口15672和5672入方向规则。

`入方向` ，点击快速创建规则：自定义端口填写：15672/15672、授权对象填写： 0.0.0.0/0

浏览器： http://IP:15672/  admin/admin123456

----
