# 3.5、开发过程

### 开发过程

尽管应用程序开发可以采取多种形式，但本手册提出了使用Tomcat创建Web应用程序的相当通用的过程。以下各节重点介绍您作为代码开发人员将执行的命令和任务。只要您有适当的源代码控制系统和内部团队规则（涉及谁在任何给定时间在谁的应用程序上工作），当涉及多个程序员时，相同的基本方法就可以工作。

下面的任务描述假定您将使用CVS进行源代码控制，并且已经配置了对适当CVS存储库的访问权限。有关说明，超出了本手册的范围。如果使用其他源代码控制环境，则需要找出系统的相应命令。

#### 一次性设置Ant和Tomcat进行开发

为了利用与*Manager* Web应用程序交互的特殊Ant任务 ，您需要一次执行以下任务（无论您计划开发多少Web应用程序）。

- *配置Ant定制任务*。Ant定制任务的实现代码位于一个名为的JAR文件中`$CATALINA_HOME/lib/catalina-ant.jar`，该文件 必须复制到`lib`您的Ant安装目录中。
- *定义一个或多个Tomcat用户*。该*管理器*的Web应用程序要求用户先登录，并有安全角色的安全约束下运行`manager-script`分配给他或她。定义此类用户的方式取决于您在Tomcat的`conf/server.xml`文件中[配置](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html)的[领域-](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html)有关详细信息，请参阅 [领域配置方法](http://tomcat.apache.org/tomcat-9.0-doc/realm-howto.html)。您可以使用该`manager-script`角色定义任意数量的用户（具有所需的用户名和密码）。

#### 创建项目源代码目录

第一步是创建一个新的项目源目录，并自定义将要使用的`build.xml`和`build.properties`文件。目录结构在[上一节](http://tomcat.apache.org/tomcat-9.0-doc/appdev/source.html)中进行[了](http://tomcat.apache.org/tomcat-9.0-doc/appdev/source.html)描述，或者您可以将 [示例应用程序](http://tomcat.apache.org/tomcat-9.0-doc/appdev/sample/)用作起点。

创建项目源目录，并在CVS存储库中定义它。这可以通过一系列类似的命令来完成，其中 `{project}`，项目的名称应存储在CVS存储库中，而{username}是您的登录用户名：

```bash
cd {my home directory}
mkdir myapp <-- Assumed "project source directory"
cd myapp
mkdir docs
mkdir src
mkdir web
mkdir web/WEB-INF
cvs import -m "Initial Project Creation" {project} \
    {username} start
```

现在，要验证它是在CVS中正确创建的，我们将对新项目执行检出：

```bash
cd ..
mv myapp myapp.bu
cvs checkout {project}
```

接下来，您将需要创建并签入`build.xml`用于开发的脚本的初始版本 。为了快速，轻松地入门，请以本手册随附`build.xml`的[基本build.xml文件](http://tomcat.apache.org/tomcat-9.0-doc/appdev/build.xml.txt)为 [基础](http://tomcat.apache.org/tomcat-9.0-doc/appdev/build.xml.txt)，或从头开始对其进行编码。

```bash
cd {my home directory}
cd myapp
emacs build.xml     <-- if you want a real editor :-)
cvs add build.xml
cvs commit
```

在执行CVS提交之前，您的更改位于您自己的开发目录的本地。提交可使那些更改对共享相同CVS存储库的团队中其他开发人员可见。

下一步是自定义脚本中命名的Ant *属性*`build.xml`。这是通过`build.properties`在项目的顶级目录中创建一个命名文件来完成的。示例`build.xml`脚本内的注释中列出了受支持的属性。通常，至少需要定义`catalina.home`属性，该属性定义Tomcat的安装位置以及管理器应用程序的用户名和密码。您可能最终会得到如下结果：

```properties
# Context path to install this application on
app.path=/hello

# Tomcat installation directory
catalina.home=/usr/local/apache-tomcat-9.0

# Manager webapp username and password
manager.username=myusername
manager.password=mypassword
```

一般情况下，你会**不会**要检查的 `build.properties`文件到CVS仓库，因为它是唯一的每个开发人员的环境。

现在，创建Web应用程序部署描述符的初始版本。您可以`web.xml`基于 [基本的web.xml文件](http://tomcat.apache.org/tomcat-9.0-doc/appdev/web.xml.txt)，也可以从头开始对其进行编码。

```bash
cd {my home directory}
cd myapp/web/WEB-INF
emacs web.xml
cvs add web.xml
cvs commit
```

请注意，这只是一个示例web.xml文件。部署描述符文件的完整定义在 [Servlet规范中。](https://wiki.apache.org/tomcat/Specifications)

#### 编辑源代码和页面

编辑/构建/测试任务通常是开发和维护期间最常见的活动。以下一般原则适用。如“ [源组织”中所述](http://tomcat.apache.org/tomcat-9.0-doc/appdev/source.html)，新创建的源文件应位于项目源目录下的相应子目录中。

每当您希望刷新开发目录以反映其他开发人员执行的工作时，您都将要求CVS为您完成：

```bash
cd {my home directory}
cd myapp
cvs update -dP
```

要创建一个新文件，请转到相应的目录，创建文件，然后在CVS中注册它。对内容满意后（在构建和测试成功之后），将新文件提交到存储库。例如，要创建一个新的JSP页面：

```bash
cd {my home directory}
cd myapp/web        <-- Ultimate destination is document root
emacs mypage.jsp
cvs add mypage.jsp
... build and test the application ...
cvs commit
```

软件包中定义的Java源代码必须按照与软件包名称匹配的目录层次结构（在**src /**子目录下）进行组织。例如，一个名为的Java类 `com.mycompany.mypackage.MyClass.java`应存储在file中 `src/com/mycompany/mypackage/MyClass.java`。每当您创建一个新的子目录时，请不要忘记在CVS中注册它。

要编辑现有的源文件，通常只需开始编辑和测试，然后在一切正常的情况下提交更改的文件。尽管可以将CVS配置为要求您“签出”或“锁定”要修改的文件，但是通常不使用它。

#### 构建Web应用程序

准备好编译应用程序时，发出以下命令（通常，您需要打开一个设置为项目源目录的Shell窗口，以便仅需要最后一个命令）：

```bash
cd {my home directory}
cd myapp        <-- Normally leave a window open here
ant
```

Ant工具将执行文件中默认的“编译”目标`build.xml`，该目标 将编译任何新的或更新的Java代码。如果这是您在“构建清理”之后第一次进行编译，则将导致重新编译所有内容。

要强制重新编译整个应用程序，请执行以下操作：

```bash
cd {my home directory}
cd myapp
ant all
```

在检查更改之前，这是一个很好的习惯，以确保您没有引入Javac条件检查没有发现的任何细微问题。

#### 测试您的Web应用程序

要测试您的应用程序，您需要将其安装在Tomcat下。最快的方法是使用示例`build.xml`脚本中包含的定制Ant任务。使用这些命令可能遵循以下模式：

- *如果需要，启动Tomcat*。如果Tomcat尚未运行，则需要以通常的方式启动它。
- *编译您的应用程序*。使用`ant compile` 命令（或仅使用`ant`，因为这是默认设置）。确保没有编译错误。
- *安装应用程序*。使用`ant install` 命令。这告诉Tomcat立即在`app.path`build属性中定义的上下文路径上开始运行您的应用程序。Tomcat的确实**不**具有重新启动，这才会生效。
- *测试应用程序*。使用浏览器或其他测试工具，测试应用程序的功能。
- *根据需要修改和重建*。当您发现需要进行更改时，请在原始**源** 文件中而不是在输出构建目录中进行更改，然后重新发出 `ant compile`命令。这样可以确保您的更改稍后可用于保存（通过`cvs commit`）-输出构建目录将被删除并根据需要重新创建。
- *重新加载应用程序*。Tomcat将自动识别JSP页面中的更改，但是它将继续使用任何servlet或JavaBean类的旧版本，直到重新加载应用程序为止。您可以通过执行`ant reload`命令来触发它。
- *完成后删除该应用程序*。当您处理此应用程序时，可以通过运行`ant remove`命令将其从实时执行中删除。

完成测试后，请不要忘记将更改提交到源代码存储库！

#### 创建发布

当您通过添加新功能并且已经对所有内容进行了测试（您可以进行测试，而不是:-）时，就该创建可以在生产服务器上部署的Web应用程序的可分发版本。需要执行以下常规步骤：

- `ant all`从项目源目录发出命令，以最后一次重新构建所有内容。
- 使用该`cvs tag`命令为用于创建此发行版的所有源文件创建标识符。这使您可以在以后可靠地重建发布（从源）。
- 发出命令`ant dist`以创建可分发的Web应用程序归档（WAR）文件以及包含相应源代码的JAR文件。
- 根据组织使用的标准发布过程，`dist`使用**tar**或**zip**实用程序将目录 的内容**打包**。