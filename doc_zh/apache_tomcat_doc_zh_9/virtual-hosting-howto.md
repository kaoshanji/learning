# 25.虚拟主机

### 假设条件

对于这个缘故如何做，假设你有两个主机名开发主机，`ren`和`stimpy`。我们还假设Tomcat的一个实例正在运行，因此可能`$CATALINA_HOME`是指它安装在任何地方`/usr/local/tomcat`。

另外，本操作方法使用Unix风格的路径分隔符和命令；如果您在Windows上，请进行相应的修改。

### server.xml

最简单的是，编辑文件的“ [引擎”](http://tomcat.apache.org/tomcat-9.0-doc/config/engine.html)部分，`server.xml`如下所示：

```xml
<Engine name="Catalina" defaultHost="ren">
    <Host name="ren"    appBase="renapps"/>
    <Host name="stimpy" appBase="stimpyapps"/>
</Engine>
```

请注意，每个主机在appBase下的目录结构不应相互重叠。

有关[引擎](http://tomcat.apache.org/tomcat-9.0-doc/config/engine.html)和[ 主机](http://tomcat.apache.org/tomcat-9.0-doc/config/host.html)元素的其他属性，请查阅配置文档 。

### Webapps目录

为每个虚拟主机创建目录：

```bash
mkdir $CATALINA_HOME/renapps
mkdir $CATALINA_HOME/stimpyapps
```

### 配置上下文

#### 一般

上下文通常位于appBase目录下。例如，要将`foobar`上下文部署为`ren`主机中的war文件，请使用 `$CATALINA_HOME/renapps/foobar.war`。请注意，其默认或ROOT上下文`ren`将部署为 `$CATALINA_HOME/renapps/ROOT.war`（WAR）或 `$CATALINA_HOME/renapps/ROOT`（目录）。

**注意：docBase上下文的绝不能与appBase主机的相同。**

#### context.xml-方法＃1

在您的上下文中，创建一个`META-INF`目录，然后将您的上下文定义放在名为的文件中 `context.xml`。即， `$CATALINA_HOME/renapps/ROOT/META-INF/context.xml` 这使部署更加容易，尤其是在分发WAR文件时。

#### context.xml-方法2

在`$CATALINA_HOME/conf/Catalina` 与您的虚拟主机相对应的目录下创建一个结构，例如：

```bash
mkdir $CATALINA_HOME/conf/Catalina/ren
mkdir $CATALINA_HOME/conf/Catalina/stimpy
```

请注意，结束目录名称“ Catalina”表示如上所述`name`的[Engine](http://tomcat.apache.org/tomcat-9.0-doc/config/engine.html)元素的 属性 。

现在，对于您的默认Web应用程序，添加：

```bash
$CATALINA_HOME/conf/Catalina/ren/ROOT.xml
$CATALINA_HOME/conf/Catalina/stimpy/ROOT.xml
```

如果要为每个主机使用Tomcat管理器webapp，则还需要在此处添加它：

```bash
cd $CATALINA_HOME/conf/Catalina
cp localhost/manager.xml ren/
cp localhost/manager.xml stimpy/
```

#### 每个主机的默认值

您可以覆盖在 主机特定的xml目录中名为和的文件中指定新值，`conf/context.xml` 并`conf/web.xml`通过指定新值来覆盖这些默认值。`context.xml.default``web.xml.default`

在前面的示例之后，您可以使用 `$CATALINA_HOME/conf/Catalina/ren/web.xml.default` 来自定义名为的虚拟主机中部署的所有Web应用程序的默认值`ren`。

#### 更多信息

有关[Context](http://tomcat.apache.org/tomcat-9.0-doc/config/context.html)元素的其他属性，请查阅配置文档 。