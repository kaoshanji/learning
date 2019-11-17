# 34.URL重写

### 介绍

重写阀以与Apache HTTP Server中的mod_rewrite非常相似的方式实现URL重写功能。

### 组态

重写阀使用`org.apache.catalina.valves.rewrite.RewriteValve` 类名配置为阀。

重写阀可以配置为主机中添加的阀。有关如何配置[虚拟服务器](http://tomcat.apache.org/tomcat-9.0-doc/config/host.html)的信息，请参见[虚拟服务器](http://tomcat.apache.org/tomcat-9.0-doc/config/host.html)文档。它将使用`rewrite.config`包含重写指令的文件，必须将其放置在主机配置文件夹中。

它也可以位于webapp的context.xml中。然后，该阀将使用`rewrite.config`包含重写指令的文件，必须将其放置在Web应用程序的WEB-INF文件夹中

### 指令

rewrite.config文件包含一系列指令，这些指令与mod_rewrite使用的指令非常相似，尤其是中央的RewriteRule和RewriteCond指令。以`#`字符开头的 行将被视为注释，并且将被忽略。

注意：本部分是mod_rewrite文档的修改版本，该文档的版权为1995-2006 The Apache Software Foundation，并根据Apache License 2.0版获得许可。

#### RewriteCond

句法： `RewriteCond TestString CondPattern`

RewriteCond指令定义规则条件。一个或多个RewriteCond可以位于RewriteRule指令之前。然后，仅当URI的当前状态都与它的模式匹配并且满足这些条件时，才使用以下规则。

*TestString*是一个字符串，除了纯文本之外，*它还*可以包含以下扩展结构：

- **RewriteRule反向引用**：这些是形式**$N** （0 <= N <= 9）的反向引用，可用于访问模式的分组部分（在括号中）， `RewriteRule`该部分受当前`RewriteCond`条件的约束。

- **RewriteCond反向引用**：这些是形式**%N** （1 <= N <= 9）的反向引用，从`RewriteCond`当前条件集中的最后一个匹配项提供对模式的分组部分（再次用括号括起来）的访问 。

- **RewriteMap扩展**：这些是表单的扩展**${mapname:key|default}**。有关更多详细信息，请参见[RewriteMap文档](http://tomcat.apache.org/tomcat-9.0-doc/rewrite.html#mapfunc)。

- 服务器变量

  ：这些是形式为

  `%{` *NAME_OF_VARIABLE的* `}`

  变量， 其中

  NAME_OF_VARIABLE

  可以是以下列表中的字符串：

  - **HTTP标头：**

    HTTP_USER_AGENT
    HTTP_REFERER
    HTTP_COOKIE
    HTTP_FORWARDED
    HTTP_HOST
    HTTP_PROXY_CONNECTION
    HTTP_ACCEPT

  - **连接和请求：**

    REMOTE_ADDR
    REMOTE_HOST
    REMOTE_PORT
    REMOTE_USER
    REMOTE_IDENT
    REQUEST_METHOD
    SCRIPT_FILENAME
    REQUEST_PATH
    CONTEXT_PATH
    SERVLET_PATH
    PATH_INFO
    QUERY_STRING
    AUTH_TYPE

  - **服务器内部：**

    DOCUMENT_ROOT
    SERVER_NAME
    SERVER_ADDR
    SERVER_PORT
    SERVER_PROTOCOL
    SERVER_SOFTWARE

  - **日期和时间：**

    TIME_YEAR
    TIME_MON
    TIME_DAY
    TIME_HOUR
    TIME_MIN
    TIME_SEC
    TIME_WDAY
    TIME

  - **特价：**

    THE_REQUEST
    REQUEST_URI
    REQUEST_FILENAME
    HTTPS

  这些变量都对应于类似命名的HTTP MIME标头和Servlet API方法。大部分记录在手册或CGI规范的其他地方。重写阀专用的那些包括以下内容。

  - `REQUEST_PATH`

    对应于用于映射的完整路径。

  - `CONTEXT_PATH`

    对应于映射上下文的路径。

  - `SERVLET_PATH`

    对应于servlet路径。

  - `THE_REQUEST`

    浏览器发送到服务器的完整HTTP请求行（例如“ `GET /index.html HTTP/1.1`”）。这不包括浏览器发送的任何其他标头。

  - `REQUEST_URI`

    在HTTP请求行所请求的资源。（在上面的示例中，该名称为“ /index.html”。）

  - `REQUEST_FILENAME`

    匹配请求的文件或脚本的完整本地文件系统路径。

  - `HTTPS`

    如果连接使用的是SSL / TLS，则将包含文本“ on”，否则将包含“ off”。

您应该注意的其他事项：

1. 变量SCRIPT_FILENAME和REQUEST_FILENAME包含相同的值-Apache 服务器`filename`内部`request_rec`结构的字段 的值 。第一个名称是众所周知的CGI变量名称，第二个名称是REQUEST_URI的对应名称（其中包含`uri`字段的值 `request_rec`）。
2. `%{ENV:variable}`，其中*variable*可以是任何Java系统属性，也可以使用。
3. `%{SSL:variable}`，其中*variable*是SSL环境变量的名称，尚未实现。示例： `%{SSL:SSL_CIPHER_USEKEYSIZE}`可以扩展为 `128`。
4. `%{HTTP:header}`（*标头*可以是任何HTTP MIME标头名称），始终可以用来获取HTTP请求中发送的标头的值。示例：`%{HTTP:Proxy-Connection}`HTTP标头“ `Proxy-Connection:`” 的值。

*CondPattern*是条件模式，它是一个正则表达式，适用于*TestString*的当前实例。 首先对*TestString*进行评估，然后再与*CondPattern*进行匹配 。

**请记住：** *CondPattern*是与 *Perl兼容的正则表达式*，其中包含一些附加内容：

1. 您可以使用' `!`'字符（感叹号）作为模式字符串的前缀，以指定 **不**匹配的模式。

2. 

   CondPatterns

    有一些特殊的变体。除了真正的正则表达式字符串，您还可以使用以下之一：

   - “ **<CondPattern** ”（在字典上位于前面）
     将*CondPattern*视为纯字符串，并在字典*上将*其与*TestString*进行比较。如果*TestString在*字典上位于*CondPattern*之前， 则为true 。
   - ' **> CondPattern** '（按字典顺序排列）
     将*CondPattern*视为纯字符串，并按字典顺序将其与*TestString*进行比较。如果*TestString在*字典上遵循 *CondPattern，*则为true 。
   - ' **= CondPattern** '（在字典上相等）
     将*CondPattern*视为纯字符串并将其在字典上与*TestString*进行比较。如果*TestString*在字典上等于 *CondPattern*（两个字符串完全相同，一个字符一个字符），则为true 。如果*CondPattern* 为`""`（两个引号），则将*TestString*与空字符串进行比较。
   - ' **-d** '（is **d** irectory）
     将*TestString*视为路径名并测试它是否存在，并且是目录。
   - “ **-f** ”（是常规 **˚F** ILE）
     对待*的TestString*作为路径名并测试它是否存在，并且是一个普通文件。
   - “ **-s** ”（是常规文件，与 **小号** IZE）
     对待*的TestString*作为路径名并测试它是否存在，并与尺寸大于零的常规文件。

   注意：

    所有这些测试也可以在前面加上感叹号（'！'），以否定其含义。

3. 您还可以通过将**标志**添加 为 指令的第三个参数来为

   CondPattern

   设置特殊标志 ，其中*标志*是以下任何*标志*的逗号分隔列表： 

   `[``]`

   

   ```
   RewriteCond
   ```

   

   

   

   - “ **nocase|NC**”（**ñ** ö **Ç** ASE）
     这使得测试不区分大小写的-之间的差“AZ”和“AZ”被忽略，无论是在膨胀*的TestString*和*CondPattern*。该标志仅对*TestString*和*CondPattern*之间的比较有效 。它对文件系统和子请求检查没有影响。

   - ' 

     `ornext|OR`

     '（

     或

     下一个条件）

     使用此命令将规则条件与局部OR（而非隐式AND）组合。典型示例：

     ```
     RewriteCond %{REMOTE_HOST}  ^host1.*  [OR]
     RewriteCond %{REMOTE_HOST}  ^host2.*  [OR]
     RewriteCond %{REMOTE_HOST}  ^host3.*
     RewriteRule ...some special stuff for any of these hosts...
     ```

     没有此标志，您将必须写入条件/规则对三遍。

**例：**

要根据请求的“ `User-Agent:`”标头重写网站的首页，可以使用以下方法：

```
RewriteCond  %{HTTP_USER_AGENT}  ^Mozilla.*
RewriteRule  ^/$                 /homepage.max.html  [L]

RewriteCond  %{HTTP_USER_AGENT}  ^Lynx.*
RewriteRule  ^/$                 /homepage.min.html  [L]

RewriteRule  ^/$                 /homepage.std.html  [L]
```

说明：如果您使用的自身标识为“Mozilla公司（包括Netscape Navigator中，Mozilla的ETC）的浏览器，那么你得到的最大的主页（其中可能包括帧，或其他特殊功能）。如果你使用的浏览器山猫（这是基于终端），那么你得到的最小化的主页（这可能是一个版本，专为方便，只支持文本浏览）。如果这些条件均不适用（您使用任何其他浏览器，或者您的浏览器将自身标识为非标准），则您将获得std（标准）主页。

#### 重写地图

句法： `RewriteMap name rewriteMapClassName optionalParameters`

使用用户必须实现的接口来实现地图。其类名称为`org.apache.catalina.valves.rewrite.RewriteMap`，其代码为：

```java
package org.apache.catalina.valves.rewrite;

public interface RewriteMap {
    public String setParameters(String params);
    public String lookup(String key);
}
```

#### 重写规则

句法： `RewriteRule Pattern Substitution`

RewriteRule指令是真正的重写主力。该指令可以多次出现，每个实例定义一个重写规则。定义这些规则的顺序很重要-这是它们在运行时应用的顺序。

模式是与Perl兼容的正则表达式，应用于当前URL。“当前”是指应用此规则时URL的值。这可能不是最初请求的URL，它可能已经与以前的规则匹配，并且已被更改。

**安全警告：**由于Java的正则表达式匹配完成的方式，格式不正确的正则表达式模式容易受到“灾难性回溯”（也称为“正则表达式拒绝服务”或ReDoS）的攻击。因此，应特别注意RewriteRule模式。通常，很难自动检测到这种易受攻击的正则表达式，因此，好的防御方法是对灾难性的回溯问题有所了解。[ OWASP ReDoS指南](https://www.owasp.org/index.php/Regular_expression_Denial_of_Service_-_ReDoS)是一个很好的参考 。

关于正则表达式语法的一些提示：

```
文本：
  .            任何单个字符
   [char ]     字符类：'chars'类的任何字符
   [^chars ]    字符类：不是'chars'类的字符
  text1 |text2备选：text1或text2

量词：
  ?            0或1出现在前的文本
   *           0或N出现在前的文本（N> 0）
   +           1或N出现在前的文本（N> 1）

分组：
  (文本)      分组文本
              （用于设置上述替代项的边界，或者
              进行反向引用，第N组可以
              在RewriteRule的RHS上称为$N）

锚点：
  ^            行
   $           尾锚点

转义：
  \ char逃逸给定的char
              （例如，指定字符“ .[]()” 等。）
```

有关正则表达式的更多信息，请查看perl正则表达式联机帮助页（“ [perldoc perlre](https://perldoc.perl.org/perlre.html) ”）。如果您对有关正则表达式及其变体（POSIX正则表达式等）的更详细信息感兴趣，则可以参考以下书籍：

*Mastering Regular Expressions，第二版*
Jeffrey EF Friedl
O'Reilly＆Associates，Inc. 2002
ISBN 978-0-596-00289-3

在规则中，NOT字符（' `!`'）也可用作可能的模式前缀。这使您可以否定模式。例如：“ *如果当前URL 与该模式**不**匹配* ”。这可以用于例外情况，在这种情况下更容易匹配否定模式，或者用作最后的默认规则。

注意：使用NOT字符取反模式时，不能在该模式中包括分组的通配符部分。这是因为，如果模式不匹配（即，否定匹配），则组中没有内容。因此，如果使用取反的模式，则不能`$N`在替换字符串中使用！

所述*取代*重写规则的是其被取代（或替换），其原始URL字符串*模式* 匹配。除纯文本外，还可以包括

1. `$N`对RewriteRule模式的反向引用（）
2. 向后引用（`%N`）到最后匹配的RewriteCond模式
3. 规则条件测试字符串（`%{VARNAME}`）中的服务器变量
4. [映射函数](http://tomcat.apache.org/tomcat-9.0-doc/rewrite.html#mapfunc)调用（`${mapname:key|default}`）

反向引用是形式为`$`**N** （**N** = 0..9）的标识符，该标识符 将由匹配*Pattern*的第**N**组内容替换。服务器变量是相同的*的TestString*一个的 指令。映射功能来自该 指令，并在此处进行了说明。这三种类型的变量按上述顺序扩展。`RewriteCond``RewriteMap`

如前所述，所有重写规则都将应用于*替换*（按在配置文件中定义的顺序）。该URL **完全**由*Substitution替换*，重写过程将继续进行，直到应用了所有规则，或者由`**L**`标志显式终止了 该URL 。

特殊字符`$`和`%`可以通过在其前面加上反斜杠字符来引用 `\`。

有一个名为' `-`' 的特殊替换字符串，表示：**NO替换**！这在提供**仅**与URL匹配但不替代任何URL的重写规则时很有用。它通常与**C**（链）标志一起使用，以便在替换发生之前应用多个模式。

与较新的mod_rewrite版本不同，Tomcat重写阀不自动支持绝对URL（必须使用特定的重定向标志才能指定绝对URL，请参见下文）或直接文件服务。

另外，您可以通过将**标志** 作为 指令的第三个参数附加到标志中来为*Substitution*设置特殊标志。*标志*是以下任何*标志*的逗号分隔列表：**[]**`RewriteRule`

- “ **chain|C**”（**ç**与下一个规则hained）
  该标志链与下一个规则（其本身可以用下面的规则被链接，等等）当前规则。这具有以下效果：如果规则匹配，则处理将照常继续-该标志无效。如果该规则 **不**匹配，则以下链接的规则将被跳过。例如`.www`，当您进行外部重定向时（在其中`.www`不应出现“- ”部分），它可以用于删除每个目录规则集中的“- ”部分。

- ' *NAME*：*VAL*：*域* [：*寿命* [：*路径* ]]'（设置**共** okie） 这将设置在客户端的浏览器的cookie。Cookie的名称由*NAME*指定，值为 *VAL*。该*域*字段是cookie的域，例如“.apache.org”，可选的*寿命* 是以分钟cookie的寿命，和可选的 *路径*是cookie的路径**cookie|CO=**

- “ *VAR*：*VAL* ”（集合**Ë** nvironment变量） 这迫使名为请求属性*VAR*被设置为值*VAL*，其中*VAL*可以包含正则表达式反向引用（和 ），其将被扩展。您可以多次使用此标志，以设置多个变量。**env|E=**
  `$N``%N`

- “ **forbidden|F**”（力URL被**˚F** orbidden）
  这会强制禁止当前URL -它立即反馈403的HTTP响应（禁止）。将此标志与适当的RewriteConds结合使用可有条件地阻止某些URL。

- ' **gone|G**'（强制URL为 **g**）
  这将强制当前URL消失-它立即发回HTTP响应410（GONE）。使用此标志标记不再存在的页面。

- “ **host|H**= *主机* ”（申请重写**ħ** OST）
  相反的是重写的URL，虚拟主机将被重写。

- “ **last|L**”（**升** AST规则）
  在这里停止重写的过程，不施加任何更多的重写规则。这对应于Perl `last`命令或`break`C语言中的命令。使用此标志可以防止通过遵循规则进一步重写当前重写的URL。例如，使用它将根路径URL（' `/`'）重写为真实的URL ，*例如* ' `/e/www/`'。

- “ **next|N**”（**Ñ**分机圆）
  重新运行改写处理（与第一重写规则重新开始）。这次，要匹配的URL不再是原始URL，而是最后重写规则返回的URL。这对应于Perl `next`命令或`continue`C语言中的命令。使用此标志可以重新开始重写过程-立即转到循环顶部。
  **注意不要创建无限循环！**

- “ **nocase|NC**”（**ñ** ö **Ç** ASE）
  这使*模式*不区分大小写，忽略之间差“时AZ”和“AZ” *模式*被与当前URL匹配。

- “ 

  `noescape|NE`

  ”（

  ñ

   öURI 

  Ë

   scaping输出的）

  该标志防止重写阀应用常规的URI转义规则重写的结果。通常，特殊字符（例如'％'，'$'，';'等）将被转义为等效的十六进制代码（分别为'％25'，'％24'和'％3B'）；该标志可防止这种情况的发生。允许百分号出现在输出中，如

  ```
  RewriteRule /foo/(.*) /bar?arg=P1\%3d$1 [R,NE]
  ```

  这将把' 

  ```
  /foo/zed
  ```

  '变成对' 

  ```
  /bar?arg=P1=zed
  ```

  ' 的安全请求。

- “ **qsappend|QSA**”（**q** uery **小号**特林 **一个** PPEND）
  此标记强制重写引擎追加而不是替换它替换字符串到现有串的查询字符串的一部分，。当您想通过重写规则将更多数据添加到查询字符串时，请使用此选项。

- ' **redirect|R [= code ]** '（force **r** edirect） 前缀*替换*为 （使新URL成为URI），以强制进行外部重定向。如果未提供任何 *代码*，则将返回HTTP响应302（找到，先前已临时移动）。如果要使用300-399范围内的其他响应代码，只需指定适当的数字或使用以下符号名称之一：（ 默认），， 。使用此规则来规范化URL并将其返回给客户端-将' '转换为'
  `http://thishost[:thisport]/``temp``permanent``seeother``/~``/u/`'，或始终在`/u/`*用户*等
  **后面**加上斜杠 。**注意：**使用此标志时，请确保替换字段是有效的URL！否则，您将被重定向到无效位置。请记住，此标志本身只会 `http://thishost[:thisport]/`放在URL之前，并且重写将继续。通常，您将需要在此时停止重写并立即重定向。要停止重写，您应该添加“ L”标志。

- “ **skip|S**= *NUM* ”（**小号**千磅下一个规则（一个或多个））
  这标记强制重写引擎跳过下一 *NUM*在顺序规则，如果当前规则相匹配。使用它来构造伪if-then-else构造：then子句的最后一条规则变为 `skip=N`，其中N是else子句中的规则数目。（这是**不**一样的“链| C”！标记）

- “ 

  `type|T`

  = 

  MIME类型

   ”（力MIME 

  吨

   YPE）

  强制MIME类型目标文件的是 

  MIME类型

  。这可用于根据某些条件设置内容类型。例如，下面的代码片段允许

  ```
  .php
  ```

  将文件

  显示

  的

  ```
  mod_php
  ```

  ，如果他们被称为与

  ```
  .phps
  ```

  扩展：

  ```
  RewriteRule ^(.+\.php)s$ $1 [T=application/x-httpd-php-source]
  ```