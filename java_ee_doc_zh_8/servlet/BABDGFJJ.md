# 18.fileupload示例应用程序

该`fileupload`示例位于tut-install` / examples / web / servlet / fileupload /目录中，说明了如何实现和使用文件上传功能。

杜克森林（Duke's Forest）案例研究提供了一个更复杂的示例，该示例上载图像文件并将其内容存储在数据库中。



在此解决以下主题：

- [文件上传示例应用程序的体系结构](https://javaee.github.io/tutorial/servlets016.html#CHDFGBGI)
- [运行文件上传示例](https://javaee.github.io/tutorial/servlets016.html#CHDIHJCI)



### 文件上传示例应用程序的体系结构

该`fileupload`示例应用程序由单个Servlet和一个HTML表单组成，该HTML表单向Servlet发出文件上载请求。

此示例包括一个非常简单的HTML表单，其中包含两个字段：文件和目标。输入类型`file`允许用户浏览本地文件系统以选择文件。选择文件后，它将作为POST请求的一部分发送到服务器。在此过程中，将两个强制性限制应用于输入类型为的表单`file`。

- 该`enctype`属性必须设置为的值 `multipart/form-data`。
- 它的方法必须是POST。

当以此方式指定表单时，整个请求将以编码形式发送到服务器。然后，该Servlet使用其自己的方法来处理该请求，以处理传入的文件数据并从流中提取文件。目的地是文件将保存在计算机上的位置的路径。按下表单底部的Upload按钮，将数据发布到servlet，该servlet将文件保存在指定的目标位置。

HTML格式`index.html`如下：

```html
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>File Upload</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <form method="POST" action="upload" enctype="multipart/form-data" >
            File:
            <input type="file" name="file" id="file" /> <br/>
            Destination:
            <input type="text" value="/tmp" name="destination"/>
            </br>
            <input type="submit" value="Upload" name="upload" id="upload" />
        </form>
    </body>
</html>
```

当客户端需要将数据发送到服务器作为请求的一部分时，例如上载文件或提交完整的表单时，将使用POST请求方法。相反，GET请求方法仅将URL和标头发送到服务器，而POST请求还包含消息正文。这允许将任何类型的任意长度的数据发送到服务器。POST请求中的标头字段通常指示消息正文的Internet媒体类型。

提交表单时，浏览器将所有部分组合在一起，流式传输内容，每个部分代表一个表单字段。零件以`input`元素命名，并通过名为的字符串定界符彼此分隔`boundary`。

`fileupload`在选择`sample.txt`作为要上载到`tmp` 本地文件系统上的目录的文件之后，这是从表单提交的数据的样子：

```oac_no_warn
POST /fileupload/upload HTTP/1.1
Host: localhost:8080
Content-Type: multipart/form-data;
boundary=---------------------------263081694432439 Content-Length: 441
-----------------------------263081694432439
Content-Disposition: form-data; name="file"; filename="sample.txt"
Content-Type: text/plain
 Data from sample file
-----------------------------263081694432439
Content-Disposition: form-data; name="destination"
 /tmp
-----------------------------263081694432439
Content-Disposition: form-data; name="upload"
 Upload
-----------------------------263081694432439--
```

servlet `FileUploadServlet.java`开始如下：

```java
@WebServlet(name = "FileUploadServlet", urlPatterns = {"/upload"})
@MultipartConfig
public class FileUploadServlet extends HttpServlet {
    private final static Logger LOGGER =
            Logger.getLogger(FileUploadServlet.class.getCanonicalName());
```

该`@WebServlet`注释使用`urlPatterns`属性来定义servlet映射。

该`@MultipartConfig`注解表明该servlet希望使用形式提出`multipart/form-data`MIME类型。

该`processRequest`方法从请求中检索目标和文件部分，然后调用该`getFileName`方法以从文件部分中检索文件名。然后，该方法将创建一个`FileOutputStream` 并将文件复制到指定的目标位置。该方法的错误处理部分捕获并处理了无法找到文件的一些最常见原因。该`processRequest`和 `getFileName`方法是这样的：

```java
protected void processRequest(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");

    // Create path components to save the file
    final String path = request.getParameter("destination");
    final Part filePart = request.getPart("file");
    final String fileName = getFileName(filePart);

    OutputStream out = null;
    InputStream filecontent = null;
    final PrintWriter writer = response.getWriter();

    try {
        out = new FileOutputStream(new File(path + File.separator
                + fileName));
        filecontent = filePart.getInputStream();

        int read = 0;
        final byte[] bytes = new byte[1024];

        while ((read = filecontent.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
        writer.println("New file " + fileName + " created at " + path);
        LOGGER.log(Level.INFO, "File{0}being uploaded to {1}",
                new Object[]{fileName, path});
    } catch (FileNotFoundException fne) {
        writer.println("You either did not specify a file to upload or are "
                + "trying to upload a file to a protected or nonexistent "
                + "location.");
        writer.println("<br/> ERROR: " + fne.getMessage());

        LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}",
                new Object[]{fne.getMessage()});
    } finally {
        if (out != null) {
            out.close();
        }
        if (filecontent != null) {
            filecontent.close();
        }
        if (writer != null) {
            writer.close();
        }
    }
}

private String getFileName(final Part part) {
    final String partHeader = part.getHeader("content-disposition");
    LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
    for (String content : part.getHeader("content-disposition").split(";")) {
        if (content.trim().startsWith("filename")) {
            return content.substring(
                    content.indexOf('=') + 1).trim().replace("\"", "");
        }
    }
    return null;
}
```



### 运行文件上传示例

您可以使用NetBeans IDE或Maven来构建，打包，部署和运行`fileupload`示例。

在此解决以下主题：

- [使用NetBeans IDE构建，打包和部署文件上传示例](https://javaee.github.io/tutorial/servlets016.html#CHDGDJCI)
- [使用Maven构建，打包和部署fileupload示例](https://javaee.github.io/tutorial/servlets016.html#CHDCFADG)
- [运行文件上传示例](https://javaee.github.io/tutorial/servlets016.html#CHDDDAAJ)



#### 使用NetBeans IDE构建，打包和部署文件上传示例

1. 确保已启动GlassFish Server（请参阅“ [启动和停止GlassFish Server”](https://javaee.github.io/tutorial/usingexamples002.html#BNADI)）。

2. 在文件菜单上，选择打开项目。

3. 在“打开项目”对话框中，导航到：

   ```oac_no_warn
   tut-install/examples/web/servlet
   ```

4. 选择`fileupload`文件夹。

5. 单击打开项目。

6. 在“项目”选项卡中，右键单击该`fileupload`项目，然后选择“生成”。



#### 使用Maven构建，打包和部署fileupload示例

1. 确保已启动GlassFish Server（请参阅“ [启动和停止GlassFish Server”](https://javaee.github.io/tutorial/usingexamples002.html#BNADI)）。

2. 在终端窗口中，转到：

   ```oac_no_warn
   tut-install/examples/web/servlet/fileupload/
   ```

3. 输入以下命令以部署应用程序：

   ```oac_no_warn
   mvn install
   ```



#### 运行文件上传示例

1. 在Web浏览器中，输入以下URL：

   ```oac_no_warn
   http://localhost:8080/fileupload/
   ```

2. 在“文件上传”页面上，单击“选择文件”以显示文件浏览器窗口。

3. 选择要上传的文件，然后单击“打开”。

   所选文件的名称显示在“文件”字段中。如果不选择文件，则将引发异常。

4. 在目标字段中，键入一个目录名。

   该目录必须已经创建并且必须可写。如果您没有输入目录名称，或者输入了不存在或受保护的目录的名称，则会引发异常。

5. 单击上载，将所选文件上载到“目标”字段中指定的目录。

   一条消息报告该文件是在您指定的目录中创建的。

6. 转到在“目标”字段中指定的目录，并验证是否存在上载的文件。