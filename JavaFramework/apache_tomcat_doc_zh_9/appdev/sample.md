# 3.6、示例应用程序

该示例应用已打包为war文件，可在[此处](http://tomcat.apache.org/tomcat-9.0-doc/appdev/sample/sample.war)下载 （注意：请确保您的浏览器不会更改文件扩展名或添加新的扩展名）。

运行此应用程序的最简单方法是将war文件移动到**CATALINA_BASE / webapps**目录。默认的Tomcat安装将自动为您扩展和部署应用程序。您可以使用以下URL进行查看（假设您正在默认端口8080上运行tomcat）：
[http：// localhost：8080 / sample](http://localhost:8080/sample)

如果只想浏览内容，则可以使用**jar**命令将war文件解压缩。

```
        jar -xvf sample.war
      
```

注意：**CATALINA_BASE**通常是您在其中解压缩Tomcat发行版的目录。有关**CATALINA_HOME**，**CATALINA_BASE**以及它们之间的差异的更多信息 ，请参阅解压缩Tomcat发行版的目录中的**RUNNING.txt**。