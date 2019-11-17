# 17.心情示例应用程序

该`mood`示例应用程序位于tut-install` / examples / web / servlet / mood /`目录中，是一个简单的示例，它显示白天Duke在不同时间的心情。这个例子说明了如何使用来开发一个简单的应用程序 `@WebServlet`，`@WebFilter`和`@WebListener`注释来创建一个servlet，侦听器和过滤器。

在此解决以下主题：

- [情绪的组成部分示例应用程序](https://javaee.github.io/tutorial/servlets015.html#CHDEBFCB)
- [运行心情示例](https://javaee.github.io/tutorial/servlets015.html#GKCOJ)



### 情绪的组成部分示例应用程序

该`mood`示例应用程序由三个部分组成： `mood.web.MoodServlet`，`mood.web.TimeOfDayFilter`，和 `mood.web.SimpleServletListener`。

`MoodServlet`，即应用程序的表示层，根据一天中的时间以图形显示Duke的心情。该`@WebServlet` 批注指定的URL模式：

```oac_no_warn
@WebServlet("/report")
public class MoodServlet extends HttpServlet {
    ...
```

`TimeOfDayFilter` 设置一个初始化参数来指示Duke处于唤醒状态：

```oac_no_warn
@WebFilter(filterName = "TimeOfDayFilter",
urlPatterns = {"/*"},
initParams = {
    @WebInitParam(name = "mood", value = "awake")})
public class TimeOfDayFilter implements Filter {
    ...
```

过滤器调用`doFilter`方法，该方法包含一条`switch` 语句，该语句根据当前时间设置杜克的心情。

`SimpleServletListener`在Servlet的生命周期中记录更改。日志条目显示在服务器日志中。



### 运行心情示例

您可以使用NetBeans IDE或Maven来构建，打包，部署和运行`mood`示例。

在此解决以下主题：

- [使用NetBeans IDE运行心情示例](https://javaee.github.io/tutorial/servlets015.html#GKCOB)
- [使用Maven运行心情示例](https://javaee.github.io/tutorial/servlets015.html#GKCPJ)



#### 使用NetBeans IDE运行心情示例

1. 确保已启动GlassFish Server（请参阅“ [启动和停止GlassFish Server”](https://javaee.github.io/tutorial/usingexamples002.html#BNADI)）。

2. 在文件菜单上，选择打开项目。

3. 在“打开项目”对话框中，导航到：

   ```oac_no_warn
   tut-install/examples/web/servlet
   ```

4. 选择`mood`文件夹。

5. 单击打开项目。

6. 在“项目”选项卡中，右键单击该`mood`项目，然后选择“生成”。

7. 在Web浏览器中，输入以下URL：

   ```oac_no_warn
   http://localhost:8080/mood/report
   ```

   URL指定上下文根，后跟URL模式。

   出现一个网页，标题为“ Servlet MoodServlet at / mood”，描述杜克心情的文本字符串和说明性图形。



#### 使用Maven运行心情示例

1. 确保已启动GlassFish Server（请参阅“ [启动和停止GlassFish Server”](https://javaee.github.io/tutorial/usingexamples002.html#BNADI)）。

2. 在终端窗口中，转到：

   ```oac_no_warn
   tut-install/examples/web/servlet/mood/
   ```

3. 输入以下命令以部署应用程序：

   ```oac_no_warn
   mvn install
   ```

4. 在Web浏览器中，输入以下URL：

   ```oac_no_warn
   http://localhost:8080/mood/report
   ```

   URL指定上下文根，后跟URL模式。

   出现一个网页，标题为“ Servlet MoodServlet at / mood”，描述杜克心情的文本字符串和说明性图形。