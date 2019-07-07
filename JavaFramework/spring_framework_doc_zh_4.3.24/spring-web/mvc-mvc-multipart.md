# 22.10 文件上传

### 22.10.1简介

Spring的内置多部分支持处理Web应用程序中的文件上载。您可以使用程序包中`MultipartResolver`定义的可插入对象 启用此多部分支持`org.springframework.web.multipart`。Spring提供了一个`MultipartResolver` 与[*Commons FileUpload*](https://jakarta.apache.org/commons/fileupload)一起使用的实现，另一个实现与Servlet 3.0多部分请求解析一起使用。

默认情况下，Spring不进行多部分处理，因为一些开发人员希望自己处理多部分。您可以通过向Web应用程序的上下文添加多部分解析程序来启用Spring多部分处理。检查每个请求以查看它是否包含多部分。如果未找到多部分，请求将按预期继续。如果在请求中找到了多部分，`MultipartResolver`则使用在您的上下文中声明的部分。之后，请求中的multipart属性将被视为任何其他属性。

### 22.10.2将MultipartResolver与*Commons FileUpload*配合使用

以下示例显示如何使用`CommonsMultipartResolver`：

```xml
<bean id="multipartResolver"
        class="org.springframework.web.multipart.commons.CommonsMultipartResolver">

    <!-- one of the properties available; the maximum file size in bytes -->
    <property name="maxUploadSize" value="100000"/>

</bean>
```

当然，您还需要在类路径中放置适当的jar，以使多部分解析器工作。在这种情况下`CommonsMultipartResolver`，您需要使用 `commons-fileupload.jar`。

当Spring `DispatcherServlet`检测到多部分请求时，它会激活已在您的上下文中声明的解析程序并移交请求。然后解析器将当前包装`HttpServletRequest`到`MultipartHttpServletRequest`支持多部分文件上载的内容中。使用`MultipartHttpServletRequest`，您可以获取有关此请求包含的多部分的信息，并实际访问控制器中的多部分文件。

### 22.10.3在*Servlet 3.0中*使用MultipartResolver

为了使用基于Servlet 3.0的多部分解析，您需要`DispatcherServlet`使用一个`"multipart-config"`部分标记 `web.xml`，或者使用`javax.servlet.MultipartConfigElement`程序化Servlet注册，或者在Servlet类可能带有`javax.servlet.annotation.MultipartConfig` 注释的自定义Servlet类的情况下。由于Servlet 3.0不允许从MultipartResolver完成这些设置，因此需要在该Servlet注册级别应用配置设置（如最大大小或存储位置）。

一旦以上述方式之一启用了Servlet 3.0多部分解析，您就可以将其添加`StandardServletMultipartResolver`到Spring配置中：

```xml
<bean id="multipartResolver"
        class="org.springframework.web.multipart.support.StandardServletMultipartResolver">
</bean>
```

### 22.10.4处理表单中的文件上载

在之后`MultipartResolver`完成了工作，请求处理像任何其他。首先，创建一个带有文件输入的表单，允许用户上传表单。encoding属性（`enctype="multipart/form-data"`）让浏览器知道如何将表单编码为多部分请求：

```html
<html>
    <head>
        <title>Upload a file please</title>
    </head>
    <body>
        <h1>Please upload a file</h1>
        <form method="post" action="/form" enctype="multipart/form-data">
            <input type="text" name="name"/>
            <input type="file" name="file"/>
            <input type="submit"/>
        </form>
    </body>
</html>
```

下一步是创建一个处理文件上载的控制器。这个控制器非常类似于[普通的注释`@Controller`](mvc.html#mvc-ann-controller)，除了我们使用`MultipartHttpServletRequest`或`MultipartFile`在方法参数中：

```java
@Controller
public class FileUploadController {

    @PostMapping("/form")
    public String handleFormUpload(@RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            // store the bytes somewhere
            return "redirect:uploadSuccess";
        }

        return "redirect:uploadFailure";
    }

}
```

请注意`@RequestParam`方法参数如何映射到表单中声明的输入元素。在这个例子中，没有做任何事情`byte[]`，但实际上你可以将它保存在数据库中，将其存储在文件系统上，等等。

使用Servlet 3.0多部分解析时，您还可以使用`javax.servlet.http.Part`方法参数：

```java
@Controller
public class FileUploadController {

    @PostMapping("/form")
    public String handleFormUpload(@RequestParam("name") String name,
            @RequestParam("file") Part file) {

        InputStream inputStream = file.getInputStream();
        // store bytes from uploaded file somewhere

        return "redirect:uploadSuccess";
    }

}
```

### 22.10.5处理程序客户端的文件上载请求

还可以在RESTful服务方案中从非浏览器客户端提交多部分请求。所有上述示例和配置也适用于此处。但是，与通常提交文件和简单表单字段的浏览器不同，编程客户端也可以发送特定内容类型的更复杂数据 - 例如带有文件的多部分请求和带有JSON格式数据的第二部分：

```json
POST /someUrl
Content-Type: multipart/mixed

--edt7Tfrdusa7r3lNQc79vXuhIIMlatb7PQg7Vp
Content-Disposition: form-data; name="meta-data"
Content-Type: application/json; charset=UTF-8
Content-Transfer-Encoding: 8bit

{
	"name": "value"
}
--edt7Tfrdusa7r3lNQc79vXuhIIMlatb7PQg7Vp
Content-Disposition: form-data; name="file-data"; filename="file.properties"
Content-Type: text/xml
Content-Transfer-Encoding: 8bit
... File Data ...
```

您可以使用`@RequestParam("meta-data") String metadata`控制器方法参数访问名为“元数据”的部件。但是，您可能更愿意接受从请求部分正文中的JSON格式数据初始化的强类型对象，这非常类似于在`@RequestBody`非帮助下将非多部分请求的主体转换为目标对象的方式`HttpMessageConverter`。

为此，您可以使用`@RequestPart`注释而不是`@RequestParam`注释。它允许您通过`HttpMessageConverter`考虑`'Content-Type'`多部分的标题来传递特定多部分的内容：

```java
@PostMapping("/someUrl")
public String onSubmit(@RequestPart("meta-data") MetaData metadata,
        @RequestPart("file-data") MultipartFile file) {

    // ...

}
```

请注意如何`MultipartFile`使用`@RequestParam`或 `@RequestPart`可互换地访问方法参数。但是，`@RequestPart("meta-data") MetaData`在这种情况下，方法参数根据其`'Content-Type'`标题读取为JSON内容，并在帮助下转换`MappingJackson2HttpMessageConverter`。

