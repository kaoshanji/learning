# 13.SSL/TLS

### 快速开始

*下面的描述使用变量名$ CATALINA_BASE来引用可解决大多数相对路径的基本目录。如果尚未通过设置CATALINA_BASE目录为多个实例配置Tomcat，则$ CATALINA_BASE将设置为$ CATALINA_HOME的值，该目录已将Tomcat安装到该目录中。*

要在Tomcat上安装和配置SSL / TLS支持，您需要遵循以下简单步骤。有关更多信息，请阅读本方法的其余部分。

1. 通过执行以下命令，创建密钥库文件来存储服务器的私钥和自签名证书：

   视窗：

   ```
   "%JAVA_HOME%\bin\keytool" -genkey -alias tomcat -keyalg RSA
   ```

   Unix：

   ```
   $JAVA_HOME/bin/keytool -genkey -alias tomcat -keyalg RSA
   ```

   并将密码值指定为“ changeit”。

2. `$CATALINA_BASE/conf/server.xml`在下面的“ [配置”部分](http://tomcat.apache.org/tomcat-9.0-doc/ssl-howto.html#Configuration)中取消注释“ SSL HTTP / 1.1 Connector”条目 并进行修改。

### SSL / TLS简介

传输层安全性（TLS）及其前身安全套接字层（SSL）是允许Web浏览器和Web服务器通过安全连接进行通信的技术。这意味着要发送的数据在处理之前先由一方加密，传输，然后由另一方解密。这是一个双向过程，这意味着服务器和浏览器都在发送数据之前对所有流量进行加密。

SSL / TLS协议的另一个重要方面是身份验证。这意味着，在您首次尝试通过安全连接与Web服务器进行通信时，该服务器将以“证书”的形式向您的Web浏览器提供一组凭据，以证明该网站是谁及其声称的身份成为。在某些情况下，服务器可能还会从您的Web浏览器中请求证书，以要求提供证明*您*是谁。这被称为“客户端身份验证”，尽管实际上，与个人用户相比，这种方式更多地用于企业对企业（B2B）交易。大多数启用了SSL的Web服务器都不请求客户端身份验证。

### SSL / TLS和Tomcat

重要的是要注意，通常只有将Tomcat作为独立的Web服务器运行时，才需要配置Tomcat以利用安全套接字。可以在[安全注意事项文档中](http://tomcat.apache.org/tomcat-9.0-doc/security-howto.html)找到详细信息 。当主要将Tomcat作为Servlet / JSP容器运行在另一个Web服务器（例如Apache或Microsoft IIS）之后时，通常需要配置主Web服务器以处理来自用户的SSL连接。通常，此服务器将协商所有与SSL相关的功能，然后仅在解密那些请求之后才传递发往Tomcat容器的所有请求。同样，Tomcat将返回明文响应，该响应将在返回用户浏览器之前进行加密。在这种环境下，Tomcat知道主Web服务器和客户端之间的通信是通过安全连接进行的（因为您的应用程序需要能够对此进行询问），但是它本身并不参与加密或解密。

Tomcat能够使用基础环境提供的任何加密协议。Java本身通过[JCE / JCA](https://docs.oracle.com/javase/9/security/java-cryptography-architecture-jca-reference-guide.htm)提供加密功能， 并通过[JSSE](https://docs.oracle.com/javase/9/security/java-secure-socket-extension-jsse-reference-guide.htm)提供加密的通信功能。任何兼容的加密“提供者”都可以向Tomcat提供加密算法。内置提供程序（SunJCE）包括对各种SSL / TLS版本（如SSLv3，TLSv1，TLSv1.1等）的支持。请查看您的Java版本的文档，以获取有关协议和算法支持的详细信息。

如果使用可选`tcnative`库，则可以通过JCA / JCE / JSSE 使用[OpenSSL](https://www.openssl.org/)密码提供程序，这可能会提供与SunJCE提供程序不同的密码算法选择和/或性能优势。您还可以`tcnative`用来启用[APR](http://tomcat.apache.org/tomcat-9.0-doc/apr.html) 连接器，该连接器使用OpenSSL进行加密操作。请查看您的OpenSSL版本的文档，以获取有关协议和算法支持的详细信息。

### 证明书

为了实现SSL，Web服务器必须为每个接受安全连接的外部接口（IP地址）具有关联的证书。这种设计背后的理论是，服务器应提供某种合理的保证，确保您的所有者是您认为的所有者，尤其是在接收任何敏感信息之前。虽然对证书的更广泛的解释超出了本文档的范围，但是可以将证书视为Internet地址的“数字护照”。它说明了站点与哪个组织相关联，以及有关站点所有者或管理员的一些基本联系信息。

该证书由其所有者加密签名，因此其他任何人都很难伪造。为了使证书能在访问者浏览器中正常运行而不会发出警告，它需要由受信任的第三方签名。这些称为*证书颁发机构*（CA）。要获得签名证书，您需要选择一个CA并按照您选择的CA提供的说明来获取证书。有一系列的CA，包括一些免费提供证书的CA。

Java提供了一个相对简单的命令行工具，称为 `keytool`，可以轻松创建“自签名”证书。自签名证书只是用户生成的证书，尚未由著名的CA进行签名，因此根本无法保证它们是真实的。尽管自签名证书对于某些测试方案很有用，但它们不适合任何形式的生产使用。

### 运行SSL的一般提示

使用SSL保护网站时，务必确保通过SSL提供该网站使用的所有资产，以使攻击者无法通过将恶意内容注入javascript文件或类似内容来绕过安全性。为了进一步增强网站的安全性，您应该评估使用HSTS标头。它允许您向浏览器传达始终应通过https访问您的站点的信息。

在安全连接上使用基于名称的虚拟主机，需要仔细配置在单个证书或提供服务器名称指示（SNI）支持的Tomcat 8.5及更高版本中指定的名称。SNI允许将具有不同名称的多个证书与单个TLS连接器关联。

### 组态

#### 准备证书密钥库

Tomcat的目前才动作`JKS`，`PKCS11`或 `PKCS12`格式的密钥库。该`JKS`格式是Java的标准“ Java KeyStore”格式，并且是`keytool`命令行实用程序创建的格式 。该工具包含在JDK中。该`PKCS12`格式是互联网标准，可以通过OpenSSL和Microsoft的Key-Manager进行操作。

密钥库中的每个条目都由别名字符串标识。尽管许多密钥库实现以不区分大小写的方式对待别名，但区分大小写的实现仍然可用。`PKCS11`例如，该规范要求别名区分大小写。为避免与别名区分大小写有关的问题，建议不要使用仅大小写不同的别名。

要将现有证书导入`JKS`密钥库，请阅读有关文档（在您的JDK文档包中）`keytool`。请注意，OpenSSL通常在密钥之前添加可读的注释，但`keytool`不支持该注释 。因此，如果您的证书在关键数据之前有注释，请先删除它们，然后再使用导入证书 `keytool`。

要将您自己的CA签名的现有证书`PKCS12` 使用OpenSSL 导入密钥库，您可以执行以下命令：

```bash
openssl pkcs12 -export -in mycert.crt -inkey mykey.key
                       -out mycert.p12 -name tomcat -CAfile myCA.crt
                       -caname root -chain
```

有关更高级的情况，请参阅 [OpenSSL文档](https://www.openssl.org/)。

要`JKS`从头开始创建一个包含一个自签名证书的新密钥库，请从终端命令行执行以下命令：

视窗：

```bash
"%JAVA_HOME%\bin\keytool" -genkey -alias tomcat -keyalg RSA
```

Unix：

```bash
$JAVA_HOME/bin/keytool -genkey -alias tomcat -keyalg RSA
```

（应该优先选择RSA算法作为安全算法，这还可以确保与其他服务器和组件的一般兼容性。）

此命令将在运行该文件的用户的主目录中创建一个名为“ `.keystore`” 的新文件。要指定其他位置或文件`-keystore`名，请在`keytool`上面显示的命令中添加参数，并在密钥库文件中添加完整路径名。您还将需要在`server.xml`配置文件中反映此新位置，如稍后所述。例如：

视窗：

```bash
"%JAVA_HOME%\bin\keytool" -genkey -alias tomcat -keyalg RSA
  -keystore \path\to\my\keystore
```

Unix：

```bash
$JAVA_HOME/bin/keytool -genkey -alias tomcat -keyalg RSA
  -keystore /path/to/my/keystore
```

执行此命令后，将首先提示您输入密钥库密码。Tomcat使用的默认密码是“ `changeit`”（全部小写），但是您可以根据需要指定自定义密码。您还将需要在`server.xml`配置文件中指定自定义密码 ，如稍后所述。

接下来，系统将提示您输入有关此证书的常规信息，例如公司，联系人姓名等。该信息将显示给尝试访问您应用程序中安全页面的用户，因此请确保此处提供的信息与他们期望的信息匹配。

最后，将提示您输入*密钥密码*，这是专用于此证书的密码（与存储在同一密钥库文件中的任何其他证书相反）。该`keytool`提示会告诉你，按ENTER键会自动使用相同的密码密钥作为密钥库。您可以自由使用相同的密码或选择自定义密码。如果选择与密钥库密码不同的密码，则还需要在`server.xml` 配置文件中指定自定义密码。

如果一切成功，那么您现在将拥有一个带有证书的密钥库文件，服务器可以使用该证书。

#### 编辑Tomcat配置文件

Tomcat可以使用三种不同的SSL实现：

- 作为Java运行时的一部分提供的JSSE实现
- 使用OpenSSL的JSSE实现
- APR实现，默认情况下使用OpenSSL引擎

确切的配置详细信息取决于所使用的实现。如果通过指定泛型来配置连接器， `protocol="HTTP/1.1"`则将自动选择Tomcat使用的实现。如果安装使用[APR](http://tomcat.apache.org/tomcat-9.0-doc/apr.html) （即您已经安装了Tomcat本机库），则它将使用JSSE OpenSSL实现，否则将使用Java JSSE实现。

如果需要，可以避免自动选择实现。通过在[Connector](http://tomcat.apache.org/tomcat-9.0-doc/config/http.html)的**协议**属性中指定类名来完成此操作。

要定义Java（JSSE）连接器，无论是否加载了APR库，请使用以下方法之一：

```xml
<!-- Define an HTTP/1.1 Connector on port 8443, JSSE NIO implementation -->
<Connector protocol="org.apache.coyote.http11.Http11NioProtocol"
           sslImplementationName="org.apache.tomcat.util.net.jsse.JSSEImplementation"
           port="8443" .../>

<!-- Define an HTTP/1.1 Connector on port 8443, JSSE NIO2 implementation -->
<Connector protocol="org.apache.coyote.http11.Http11Nio2Protocol"
           sslImplementationName="org.apache.tomcat.util.net.jsse.JSSEImplementation"
           port="8443" .../>
```

如果需要，还可以显式配置OpenSSL JSSE实现。如果已安装APR库（与使用APR连接器一样），则使用sslImplementationName属性可以启用它。使用OpenSSL JSSE实现时，配置可以使用JSSE属性或OpenSSL属性（用于APR连接器），但不得在同一SSLHostConfig或Connector元素中混合两种类型的属性。

```xml
<!-- Define an HTTP/1.1 Connector on port 8443, JSSE NIO implementation and OpenSSL -->
<Connector protocol="org.apache.coyote.http11.Http11NioProtocol" port="8443"
           sslImplementationName="org.apache.tomcat.util.net.openssl.OpenSSLImplementation"
           .../>
```

或者，要指定一个APR连接器（APR库必须可用），请使用：

```xml
<!-- Define an HTTP/1.1 Connector on port 8443, APR implementation -->
<Connector protocol="org.apache.coyote.http11.Http11AprProtocol"
           port="8443" .../>
```

如果使用的是APR或JSSE OpenSSL，则可以选择配置OpenSSL的替代引擎。

```xml
<Listener className="org.apache.catalina.core.AprLifecycleListener"
          SSLEngine="someengine" SSLRandomSeed="somedevice" />
```

默认值为

```xml
<Listener className="org.apache.catalina.core.AprLifecycleListener"
          SSLEngine="on" SSLRandomSeed="builtin" />
```

此外，该`useAprConnector`属性还可用于使Tomcat默认使用APR连接器而不是NIO连接器：

```xml
<Listener className="org.apache.catalina.core.AprLifecycleListener"
          useAprConnector="true" SSLEngine="on" SSLRandomSeed="builtin" />
```

因此，要启用OpenSSL，请确保SSLEngine属性设置为以外的其他值`off`。默认值为`on`，如果您指定其他值，则必须为有效的OpenSSL引擎名称。

SSLRandomSeed允许指定熵的来源。生产系统需要可靠的熵源，但是熵可能需要大量时间才能收集，因此测试系统不能使用诸如“ / dev / urandom”之类的阻塞熵源，从而可以更快地启动Tomcat。

最后一步是在`$CATALINA_BASE/conf/server.xml`文件中配置连接器 ，其中 `$CATALINA_BASE`代表Tomcat实例的基本目录。 Tomcat `<Connector>`随附的默认`server.xml`文件中包含SSL连接器的示例元素。要配置使用JSSE的SSL连接器，您将需要删除注释并对其进行编辑，使其看起来像这样：

```xml
<!-- Define an SSL Coyote HTTP/1.1 Connector on port 8443 -->
<Connector
           protocol="org.apache.coyote.http11.Http11NioProtocol"
           port="8443" maxThreads="200"
           scheme="https" secure="true" SSLEnabled="true"
           keystoreFile="${user.home}/.keystore" keystorePass="changeit"
           clientAuth="false" sslProtocol="TLS"/>
```

注意：如果安装了tomcat-native，则配置将使用带有OpenSSL实现的JSSE，该实现支持此配置或下面给出的APR配置示例。

APR连接器对许多SSL设置使用不同的属性，尤其是密钥和证书。APR配置的示例是：

```xml
<!-- Define an SSL Coyote HTTP/1.1 Connector on port 8443 -->
<Connector
           protocol="org.apache.coyote.http11.Http11AprProtocol"
           port="8443" maxThreads="200"
           scheme="https" secure="true" SSLEnabled="true"
           SSLCertificateFile="/usr/local/ssl/server.crt"
           SSLCertificateKeyFile="/usr/local/ssl/server.pem"
           SSLVerifyClient="optional" SSLProtocol="TLSv1+TLSv1.1+TLSv1.2"/>
```

[HTTP连接器](http://tomcat.apache.org/tomcat-9.0-doc/config/http.html#SSL_Support)配置参考的“ SSL支持”部分中记录了配置选项和关于哪些属性是必需的信息 。确保为使用的连接器使用正确的属性。除非安装了JSSE OpenSSL实现（在这种情况下，它支持JSSE或OpenSSL配置样式），否则NIO和NIO2连接器使用JSSE，而APR / native连接器使用APR。

该`port`属性是Tomcat将在其上侦听安全连接的TCP / IP端口号。您可以将其更改为所需的任何端口号（例如，用于`https`通信的默认端口 443）。但是，在许多操作系统上，要在端口号小于1024的端口上运行Tomcat，必须进行特殊设置（超出本文档的范围）。

*如果您在此处更改端口号，则还应该更改redirectPort为非SSL连接器上的属性指定的值。这使Tomcat可以自动重定向尝试访问页面的用户，这些用户使用Servlet规范所要求的安全性约束来指定需要SSL。*

完成这些配置更改后，您必须像平常一样重新启动Tomcat，并且应该可以正常工作。您应该能够通过SSL访问Tomcat支持的任何Web应用程序。例如，尝试：

```
https://localhost:8443/
```

并且您应该看到通常的Tomcat启动页面（除非您已经修改了ROOT Web应用程序）。如果这不起作用，则以下部分包含一些故障排除提示。

### 从证书颁发机构安装证书

要从证书颁发机构（例如verisign.com，thawte.com或trustcenter.de）获取并安装证书，请阅读上一节，然后按照以下说明进行操作：

#### 创建本地证书签名请求（CSR）

为了从您选择的证书颁发机构获得证书，您必须创建一个所谓的证书签名请求（CSR）。证书颁发机构将使用该CSR来创建一个证书，该证书将您的网站标识为“安全”。要创建CSR，请按照以下步骤操作：

- 创建本地自签名证书（如上一节所述）：

  ```bash
  keytool -genkey -alias tomcat -keyalg RSA
      -keystore <your_keystore_filename>
  ```

  注意：在某些情况下，您必须

  ```
  www.myside.org
  ```

  在“名字和姓氏”字段中输入网站的域（即），以创建有效的证书。

- 然后使用以下方法创建CSR：

  ```
  keytool -certreq -keyalg RSA -alias tomcat -file certreq.csr
      -keystore <your_keystore_filename>
  ```

现在，您有一个名为的文件`certreq.csr`，可以将其提交给证书颁发机构（请参阅证书颁发机构网站上的文档以了解如何执行此操作）。作为回报，您将获得证书。

#### 导入证书

有了证书后，您可以将其导入本地密钥库。首先，您必须将所谓的链证书或根证书导入到密钥库中。之后，您可以继续导入证书。

- 从获得证书的证书颁发机构下载链证书。
  对于Verisign.com商业证书，请访问：http://www.verisign.com/support/install/intermediate.html
  对于Verisign.com试用证书，请访问：http://www.verisign.com/support/verisign-intermediate -ca / Trial_Secure_Server_Root / index.html
  对于Trustcenter.de，请访问：http://www.trustcenter.de/certservices/cacerts/en/en.htm#server
  对于Thawte.com，请访问：http：//www.thawte .com / certs / trustmap.html

- 导入链证书到密钥存储

  ```
  keytool -import -alias root -keystore <your_keystore_filename>
      -trustcacerts -file <filename_of_the_chain_certificate>
  ```

- 最后导入您的新证书

  ```
  keytool -import -alias tomcat -keystore <your_keystore_filename>
      -file <your_certificate_filename>
  ```

### 使用OCSP证书

要将在线证书状态协议（OCSP）与Apache Tomcat一起使用，请确保已下载，安装并配置了 [Tomcat本机连接器](https://tomcat.apache.org/download-native.cgi)。此外，如果使用Windows平台，请确保下载支持ocsp的连接器。

要使用OCSP，您需要满足以下条件：

- 启用OCSP的证书
- 带有SSL APR连接器的Tomcat
- 配置的OCSP响应器

#### 生成启用OCSP的证书

Apache Tomcat要求启用OCSP的证书在证书中编码OCSP响应者位置。`openssl.cnf`文件中与OCSP相关的基本证书颁发机构设置可能如下所示：

```
#... omitted for brevity

[x509]
x509_extensions = v3_issued

[v3_issued]
subjectKeyIdentifier=hash
authorityKeyIdentifier=keyid,issuer
# The address of your responder
authorityInfoAccess = OCSP;URI:http://127.0.0.1:8088
keyUsage = critical,digitalSignature,nonRepudiation,keyEncipherment,dataEncipherment,keyAgreement,keyCertSign,cRLSign,encipherOnly,decipherOnly
basicConstraints=critical,CA:FALSE
nsComment="Testing OCSP Certificate"

#... omitted for brevity
```

上面的设置将OCSP响应者地址编码 `127.0.0.1:8088`为证书。请注意，对于以下步骤，您必须已`openssl.cnf`准备好CA的和其他配置。生成启用OCSP的证书：

- 创建一个私钥：

  ```
  openssl genrsa -aes256 -out ocsp-cert.key 4096
  ```

- 创建签名请求（CSR）：

  ```
  openssl req -config openssl.cnf -new -sha256 \
    -key ocsp-cert.key -out ocsp-cert.csr
  ```

- 签署企业社会责任：

  ```
  openssl ca -openssl.cnf -extensions ocsp -days 375 -notext \
    -md sha256 -in ocsp-cert.csr -out ocsp-cert.crt
  ```

- 您可以验证证书：

  ```
  openssl x509 -noout -text -in ocsp-cert.crt
  ```

#### 配置OCSP连接器

要配置OCSP连接器，请首先确认您正在加载Tomcat APR库。检查[ 基于Tomcat的基于Apache可移植运行时（APR）的本机库，](http://tomcat.apache.org/tomcat-9.0-doc/apr.html#Installation) 以获取有关安装APR的更多信息。`server.xml`文件中启用了OCSP的基本连接器定义如下所示：

```xml
<Connector
    port="8443"
    protocol="org.apache.coyote.http11.Http11AprProtocol"
    secure="true"
    scheme="https"
    SSLEnabled="true"
  <SSLHostConfig
      caCertificateFile="/path/to/ca.pem"
      certificateVerification="require"
      certificateVerificationDepth="10" >
    <Certificate
        certificateFile="/path/to/ocsp-cert.crt"
        certificateKeyFile="/path/to/ocsp-cert.key" />
  </SSLHostConfig>
```

#### 启动OCSP响应程序

Apache Tomcat将查询OCSP响应器服务器以获取证书状态。测试时，创建OCSP响应程序的一种简单方法是执行以下命令：

```
openssl ocsp -port 127.0.0.1:8088 \
    -text -sha256 -index index.txt \
    -CA ca-chain.cert.pem -rkey ocsp-cert.key \
    -rsigner ocsp-cert.crt
```



请注意，使用OCSP时，连接器证书中编码的响应者必须正在运行。有关更多信息，请参见 [OCSP文档 ](https://www.openssl.org/docs/man1.1.0/apps/ocsp.html)。

### 故障排除

下面列出了设置SSL通信时可能遇到的常见问题，以及如何解决这些问题。

- Tomcat启动时，出现类似“ java.io.FileNotFoundException：未找到{some-directory} / {some-file}”的异常。

  可能的解释是Tomcat无法在其查找位置找到密钥库文件。缺省情况下，Tomcat期望密钥存储文件`.keystore`在运行Tomcat的用户主目录中进行命名（可能与您的：-相同或不同）。如果密钥库文件在其他任何位置，则需要 在[Tomcat配置文件中](http://tomcat.apache.org/tomcat-9.0-doc/ssl-howto.html#Edit_the_Tomcat_Configuration_File)`keystoreFile`为`<Connector>`元素添加 属性。

- Tomcat启动时，出现类似“ java.io.FileNotFoundException：密钥库被篡改或密码错误”的异常。

  假设某人*实际上*并未篡改您的密钥库文件，最可能的原因是Tomcat使用的密码与创建密钥库文件时使用的密码不同。要解决此问题，您可以返回并 [重新创建密钥库文件](http://tomcat.apache.org/tomcat-9.0-doc/ssl-howto.html#Prepare_the_Certificate_Keystore)，也可以在[Tomcat配置文件中](http://tomcat.apache.org/tomcat-9.0-doc/ssl-howto.html#Edit_the_Tomcat_Configuration_File)`keystorePass` 的`<Connector>`元素 上添加或更新属性。 **提醒** -密码区分大小写！

- Tomcat启动时，出现类似“ java.net.SocketException：SSL握手错误javax.net.ssl.SSLException：没有可用的证书或密钥与启用的SSL密码套件相对应的异常”。

  可能的解释是Tomcat在指定的密钥库中找不到服务器密钥的别名。检查是否正确 `keystoreFile`，并`keyAlias`在指定 `<Connector>`的元素中的 [Tomcat配置文件](http://tomcat.apache.org/tomcat-9.0-doc/ssl-howto.html#Edit_the_Tomcat_Configuration_File)。 **提醒** - `keyAlias`值可能区分大小写！

- 我的基于Java的客户端中止了握手，并出现诸如“ java.lang.RuntimeException：无法生成DH密钥对”和“ java.security.InvalidAlgorithmParameterException”的异常：素数必须是64的倍数，并且只能在512到1024之间（包括512） ）”

  如果使用的是APR /本机连接器或JSSE OpenSSL实现，它将根据RSA证书的密钥大小确定临时DH密钥的强度。例如，2048位RSA密钥将导致DH密钥使用2048位素数。不幸的是，Java 6仅支持768位，而Java 7仅支持1024位。因此，如果您的证书具有更强的密钥，则旧的Java客户端可能会产生此类握手失败。作为缓解措施，您可以尝试通过配置适当的`SSLCipherSuite`activate 来强迫他们使用另一种密码`SSLHonorCipherOrder`，或者在证书文件中嵌入弱的DH参数。不建议使用后一种方法，因为它会削弱SSL安全性（logjam攻击）。

如果仍然有问题，则**TOMCAT-USER**邮件列表是一个很好的信息来源 。您可以在https://tomcat.apache.org/lists.html上找到指向该列表中先前消息的归档的指针以及订阅和取消订阅的信息 。

### 在应用程序中使用SSL进行会话跟踪

这是Servlet 3.0规范中的新功能。因为它使用与物理客户端-服务器连接关联的SSL会话ID，所以存在一些限制。他们是：

- Tomcat必须具有属性**isSecure**设置为的连接器 `true`。
- 如果SSL连接是由代理或硬件加速器管理的，则它们必须填充SSL请求标头（请参阅 [SSLValve](http://tomcat.apache.org/tomcat-9.0-doc/config/valve.html)），以便Tomcat可以看到SSL会话ID。
- 如果Tomcat终止了SSL连接，则将无法使用会话复制，因为SSL会话ID在每个节点上都不同。

要启用SSL会话跟踪，您需要使用上下文监听器将上下文的跟踪模式设置为仅SSL（如果启用了任何其他跟踪模式，则将优先使用它）。它可能看起来像：

```java
package org.apache.tomcat.example;

import java.util.EnumSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.SessionTrackingMode;

public class SessionTrackingModeListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Do nothing
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        EnumSet<SessionTrackingMode> modes =
            EnumSet.of(SessionTrackingMode.SSL);

        context.setSessionTrackingModes(modes);
    }

}
```

注意：SSL会话跟踪是针对NIO和NIO2连接器实现的。APR连接器尚未实现。

### 杂项技巧

要从请求访问SSL会话ID，请使用：

```java
String sslID = (String)request.getAttribute("javax.servlet.request.ssl_session_id");
```

有关此区域的其他讨论，请参见 [Bugzilla](https://bz.apache.org/bugzilla/show_bug.cgi?id=22679)。

要终止SSL会话，请使用：

```java
// Standard HTTP session invalidation
session.invalidate();

// Invalidate the SSL Session
org.apache.tomcat.util.net.SSLSessionManager mgr =
    (org.apache.tomcat.util.net.SSLSessionManager)
    request.getAttribute("javax.servlet.request.ssl_session_mgr");
mgr.invalidateSession();

// Close the connection since the SSL session will be active until the connection
// is closed
response.setHeader("Connection", "close");
```

请注意，由于使用了SSLSessionManager类，因此该代码特定于Tomcat。当前仅适用于NIO和NIO2连接器，不适用于APR / native连接器。