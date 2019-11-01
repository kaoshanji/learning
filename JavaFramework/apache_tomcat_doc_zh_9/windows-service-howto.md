# 30.Windows服务

### Tomcat服务应用

**Tomcat9**是用于将Tomcat 9作为Windows服务运行的服务应用程序。

### Tomcat监视器应用程序

**Tomcat9w**是用于监视和配置Tomcat服务的GUI应用程序。

可用的命令行选项包括：

| **// **ES**//** | 编辑服务配置 | 这是默认操作。如果提供了no选项，但是将可执行文件重命名为**servicenameW.exe**，则调用该方法 |
| --------------- | ------------ | ------------------------------------------------------------ |
| **//**MS**//**  | 监控服务     | 将图标放入系统托盘                                           |

### 命令行参数

每个命令行指令的形式为**// XX // ServiceName**

可用的命令行选项包括：

| **// TS //** | 将服务作为控制台应用程序运行 | 这是默认操作。如果提供no选项，则调用它。ServiceName是不带exe后缀的可执行文件的名称，表示Tomcat9 |
| ------------ | ---------------------------- | ------------------------------------------------------------ |
| **// RS //** | 运行服务                     | 仅从ServiceManager调用                                       |
| **// SS //** | 停止服务                     |                                                              |
| **//我们//** | 更新服务参数                 |                                                              |
| **// IS //** | 安装服务                     |                                                              |
| **// DS //** | 删除服务                     | 如果正在运行，则停止服务                                     |

### 命令行参数

每个命令行参数都以**-**为前缀。如果命令行参数以**++**为前缀，则其值将附加到现有选项中。如果与命令行参数同名但前缀为的环境变量`PR_`存在，则将具有优先级。例如：

```
set PR_CLASSPATH=xx.jar
```

相当于提供

```
--Classpath=xx.jar
```

作为命令行参数。

| 参数名称      | 默认                                         | 描述                                                         |
| :------------ | :------------------------------------------- | :----------------------------------------------------------- |
| - Description |                                              | 服务名称说明（最多1024个字符）                               |
| - DisplayName | 服务名称                                     | 服务显示名称                                                 |
| - Install     | procrun.exe // RS //服务名                   | 安装图片                                                     |
| - Startup     | 手册                                         | 服务启动模式可以是**自动**或**手动**                         |
| - DependsOn   |                                              | 该服务所依赖的服务列表。使用**＃**或**;**分隔相关服务**。**人物 |
| - Environment |                                              | 将以**key = value**形式提供给服务的环境变量列表。它们用**＃**或**;**分隔 **。**字符。如果您需要使用**＃** 或**;** 字符，然后将整个值括在单引号内。 |
| - User        |                                              | 用于运行可执行文件的用户帐户。它仅用于StartMode **java**或**exe，**并在没有LogonAsService特权的情况下以帐户身份运行应用程序。 |
| - Password    |                                              | --User参数设置的用户帐户密码                                 |
| --JavaHome    | JAVA_HOME                                    | 设置不同于JAVA_HOME环境变量定义的JAVA_HOME                   |
| -Jvm          | JAVA_HOME                                    | 使用**自动**（即从Windows注册表中找到JVM）或指定**jvm.dll**的完整路径。您可以在此处使用环境变量扩展。 |
| --JvmOptions  | -Xrs                                         | 将以**-D**或**-X**形式显示的选项列表，这些选项将传递给JVM。使用**＃**或**;**分隔选项 **。**字符。如果需要嵌入**＃**或 **;** 字符，将它们放在单引号内。（在**exe**模式下不使用 。） |
| --JvmOptions9 |                                              | 当在Java 9或更高版本上运行时，将以**-D**或**-X**形式显示的选项列表将传递给JVM。使用**＃**或**;**分隔选项**。**字符。如果需要嵌入**＃**或**;** 字符，将它们放在单引号内。（在**exe**模式下不使用。） |
| --Classpath   |                                              | 设置Java类路径。（在**exe**模式下不使用。）                  |
| --JvmMs       |                                              | 初始内存池大小，以MB为单位。（在**exe**模式下不使用。）      |
| --JvmMx       |                                              | 最大内存池大小（以MB为单位）。（在**exe**模式下不使用。）    |
| --JvmSs       |                                              | 线程堆栈大小（以KB为单位）。（在**exe**模式下不使用。）      |
| -StartMode    |                                              | 一个**JVM**，**Java的**或**exe文件**。这些模式是：jvm-启动Java进程。取决于jvm.dll，请参阅**--Jvm**。Java-与exe相同，但会自动使用默认Java可执行文件，即％JAVA_HOME％\ bin \ java.exe。确保正确设置了JAVA_HOME，或使用--JavaHome提供正确的位置。如果两者均未设置，则procrun将尝试从Windows注册表中查找默认的JDK（而非JRE）。exe-将图像作为单独的进程运行 |
| --StartImage  |                                              | 将运行的可执行文件。仅适用于**exe**模式。                    |
| --StartPath   |                                              | 起始映像可执行文件的工作路径。                               |
| --StartClass  | Main                                         | 包含启动方法的类。适用于**jvm**和 **Java**模式。（在**exe**模式下不使用。） |
| --StartMethod | Main                                         | 方法名称如果不同则主要                                       |
| --StartParams |                                              | 将传递给StartImage或StartClass的参数列表。使用**＃**或 **;**分隔参数**。**字符。 |
| -StopMode     |                                              | 一个**JVM**，**Java的**或**exe文件**。有关 更多详细信息，请参见**--StartMode**。 |
| --StopImage   |                                              | 将在Stop服务信号上运行的可执行文件。仅适用于 **exe**模式。   |
| --StopPath    |                                              | 停止图像可执行文件的工作路径。不适用于**JVM** 模式。         |
| --StopClass   | Main                                         | 将在“停止”服务信号上使用的类。适用于 **jvm**和**Java**模式。 |
| --StopMethod  | Main                                         | 方法名称如果不同则主要                                       |
| -StopParams   |                                              | 将传递给StopImage或StopClass的参数列表。使用**＃**或 **;**分隔参数**。**字符。 |
| --StopTimeout | No Timeout                                   | 定义procrun等待服务正常退出的超时时间（以秒为单位）。        |
| --LogPath     | ％SystemRoot％\ System32 \ LogFiles \ Apache | 定义日志记录的路径。如有必要，创建目录。                     |
| --LogPrefix   | commons-daemon                               | 定义服务日志文件名前缀。日志文件在带有`.YEAR-MONTH-DAY.log`后缀的LogPath目录中创建 |
| --LogLevel    | Info                                         | 定义日志记录级别，可以是**Error**， **Info**，**Warn**或**Debug**。（不区分大小写）。 |
| --StdOutput   |                                              | 重定向标准输出文件名。如果命名为**auto，**则会在**LogPath**内部创建名为**service-stdout.YEAR-MONTH-DAY.log的文件**。 |
| --StdError    |                                              | 重定向的stderr文件名。如果命名为**auto，**则会在**LogPath**内部创建名为**service-stderr.YEAR-MONTH-DAY.log的文件**。 |
| --PidFile     |                                              | 定义用于存储正在运行的进程ID的文件名。实际文件在**LogPath**目录中创建 |

### 安装服务

手动安装服务的最安全方法是使用提供的 **service.bat**脚本。运行此脚本需要管理员特权。如有必要，可以使用`/user`开关指定要用于安装服务的用户。

**注意：**如果启用了用户帐户控制（UAC），则脚本启动'Tomcat9.exe'时，系统将要求您提供其他特权。
如果要将其他选项作为`PR_*`环境变量传递给服务安装程序 ，则必须在OS中全局配置它们，或者启动以较高特权设置它们的程序（例如，右键单击cmd.exe并选择“以管理员身份运行”） ;在Windows 8（或更高版本）或Windows Server 2012（或更高版本），您可以通过点击“文件”菜单栏上打开从资源管理器的当前目录的命令提示符）。有关详细信息，请参见问题[56143](https://bz.apache.org/bugzilla/show_bug.cgi?id=56143)。

```bash
Install the service named 'Tomcat9'
C:\> service.bat install
```

有一个第二个可选参数，可让您指定服务的名称，如Windows服务中所示。

```bash
Install the service named 'MyService'
C:\> service.bat install MyService
```

使用非默认名称安装服务时，tomcat9.exe和tomcat9w.exe可能会重命名以匹配所选的服务名称。为此，请使用该`--rename` 选项。

```bash
Install the service named 'MyService' with renaming
C:\> service.bat install MyService --rename
```

如果使用tomcat9.exe，则需要使用**// IS //**参数。

```bash
Install the service named 'Tomcat9'
C:\> tomcat9 //IS//Tomcat9 --DisplayName="Apache Tomcat 9" ^
     --Install="C:\Program Files\Tomcat\bin\tomcat9.exe" --Jvm=auto ^
     --StartMode=jvm --StopMode=jvm ^
     --StartClass=org.apache.catalina.startup.Bootstrap --StartParams=start ^
     --StopClass=org.apache.catalina.startup.Bootstrap --StopParams=stop
```

### 更新服务

要更新服务参数，您需要使用**// US //**参数。

```bash
Update the service named 'Tomcat9'
C:\> tomcat9 //US//Tomcat9 --Description="Apache Tomcat Server - https://tomcat.apache.org/ " ^
     --Startup=auto --Classpath=%JAVA_HOME%\lib\tools.jar;%CATALINA_HOME%\bin\bootstrap.jar
```

如果为服务提供了一个可选名称，则需要这样指定：

```bash
Update the service named 'MyService'
C:\> tomcat9 //US//MyService --Description="Apache Tomcat Server - https://tomcat.apache.org/ " ^
     --Startup=auto --Classpath=%JAVA_HOME%\lib\tools.jar;%CATALINA_HOME%\bin\bootstrap.jar
```

### 删除服务

要删除该服务，您需要使用**// DS //**参数。
如果服务正在运行，它将停止然后删除。

```bash
Remove the service named 'Tomcat9'
C:\> tomcat9 //DS//Tomcat9
```

如果为服务提供了一个可选名称，则需要这样指定：

```bash
Remove the service named 'MyService'
C:\> tomcat9 //DS//MyService
```

### 调试服务

要在控制台模式下运行服务，您需要使用**// TS //**参数。可以通过按**CTRL + C**或 **CTRL + BREAK**来启动服务关闭。如果将tomcat9.exe重命名为testservice.exe，则只需执行testservice.exe，默认情况下将执行此命令模式。

```bash
Run the service named 'Tomcat9' in console mode
C:\> tomcat9 //TS//Tomcat9 [additional arguments]
Or simply execute:
C:\> tomcat9
```

### 多个实例

Tomcat支持安装多个实例。您可以一次安装Tomcat，同时在不同IP /端口组合上运行多个实例，或者在不同IP /端口上运行一个或多个实例的多个Tomcat版本。

每个实例文件夹将需要以下结构：

- conf
- 日志
- 温度
- 网络应用
- 工作

conf至少应包含CATALINA_HOME \ conf \中以下文件的副本。默认情况下，将从CATALINA_HOME \ conf中拾取所有未复制和编辑的文件，即CATALINA_BASE \ conf文件将覆盖CATALINA_HOME \ conf中的默认文件。

- server.xml
- web.xml

您必须编辑CATALINA_BASE \ conf \ server.xml，以指定实例要侦听的唯一IP /端口。找到包含的行 `<Connector port="8080" ...`并添加地址属性和/或更新端口号，以指定唯一的IP /端口组合。

要安装实例，请首先将CATALINA_HOME环境变量设置为Tomcat安装目录的名称。然后创建第二个环境变量CATALINA_BASE并将其指向实例文件夹。然后运行“ service.bat install”命令，指定服务名称。

```bash
set CATALINA_HOME=c:\tomcat_9
set CATALINA_BASE=c:\tomcat_9\instances\instance1
service.bat install instance1
```

要修改服务设置，可以运行**tomcat9w // ES // instance1**。

对于其他实例，请创建其他实例文件夹，更新CATALINA_BASE环境变量，然后再次运行“ service.bat install”。

```bash
set CATALINA_BASE=c:\tomcat_9\instances\instance2
service.bat install instance2
```