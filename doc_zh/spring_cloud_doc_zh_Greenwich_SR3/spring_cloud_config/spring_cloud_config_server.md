# 5. Spring Cloud Config Server

## 5. Spring Cloud Config服务器

Spring Cloud Config Server为外部配置（名称-值对或等效的YAML内容）提供了一个基于HTTP资源的API。通过使用`@EnableConfigServer`注释，服务器可嵌入到Spring Boot应用程序中。因此，以下应用程序是配置服务器：

**ConfigServer.java。** 

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServer {
  public static void main(String[] args) {
    SpringApplication.run(ConfigServer.class, args);
  }
}
```



像所有Spring Boot应用程序一样，它默认在端口8080上运行，但是您可以通过各种方式将其切换到更传统的端口8888。最简单的方法也是设置默认配置存储库，方法是使用来启动它`spring.config.name=configserver`（`configserver.yml`Config Server jar中有一个）。另一种是使用您自己的`application.properties`，如以下示例所示：

**application.properties。** 

```properties
server.port: 8888
spring.cloud.config.server.git.uri: file://${user.home}/config-repo
```



哪里`${user.home}/config-repo`是一个包含YAML和属性文件的git存储库。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 在Windows上，如果文件URL带有驱动器前缀（例如，`file:///${user.home}/config-repo`）是绝对的，则需要在文件URL中添加一个额外的“ /” 。 |

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| The following listing shows a recipe for creating the git repository in the preceding example: |

```bash
$ cd $HOME
$ mkdir config-repo
$ cd config-repo
$ git init .
$ echo info.foo: bar > application.properties
$ git add -A .
$ git commit -m "Add application.properties"
```

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| 将本地文件系统用于git存储库仅用于测试。您应该使用服务器在生产环境中托管配置存储库。 |

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| 如果仅在其中存储文本文件，则配置存储库的初始克隆可以快速高效。如果存储二进制文件（尤其是大文件），则可能会在首次配置请求时遇到延迟，或者在服务器中遇到内存不足错误。 |

## 5.1环境资料库

您应该在哪里存储配置服务器的配置数据？控制此行为的策略是`EnvironmentRepository`服务`Environment`对象。这`Environment`是Spring的域的浅表副本`Environment`（包括`propertySources`作为主要功能）。该`Environment`资源由三个变量参数化：

- `{application}`，它映射到`spring.application.name`客户端。
- `{profile}`，它映射到`spring.profiles.active`客户端（逗号分隔列表）上。
- `{label}`，这是服务器端功能，标记了一组“版本化”的配置文件。

存储库实现通常表现为类似于Spring Boot应用程序，从`spring.config.name`等于`{application}`参数且`spring.profiles.active`等于参数的位置加载配置文件`{profiles}`。配置文件的优先规则也与常规Spring Boot应用程序中的规则相同：活动配置文件优先于默认配置，如果有多个配置文件，则最后一个优先（类似于向中添加条目`Map`）。

以下示例客户端应用程序具有此引导程序配置：

**bootstrap.yml。** 

```bash
spring:
  application:
    name: foo
  profiles:
    active: dev,mysql
```



（与Spring Boot应用程序一样，这些属性也可以由环境变量或命令行参数设置）。

如果存储库是基于文件的，服务器的创建 `Environment`从`application.yml`（所有客户端之间共享）和 `foo.yml`（与`foo.yml`采取优先次序）。如果YAML文件中包含指向Spring概要文件的文档，则会以更高的优先级应用这些文件（按列出的概要文件的顺序）。如果存在特定于配置文件的YAML（或属性）文件，这些文件也将以比默认文件更高的优先级应用。较高的优先级将转换为`PropertySource`早些时候上市`Environment`。（这些规则适用于独立的Spring Boot应用程序。）

您可以将spring.cloud.config.server.accept-empty设置为false，以便在未找到应用程序的情况下Server返回HTTP 404状态。默认情况下，此标志设置为true。

### 5.1.1 Git后端

的默认实现`EnvironmentRepository`使用Git后端，这对于管理升级和物理环境以及审核更改非常方便。要更改存储库的位置，可以`spring.cloud.config.server.git.uri`在Config Server中设置配置属性（例如，在中`application.yml`）。如果您使用`file:`前缀，它应该在本地存储库中工作，以便无需服务器即可快速轻松地开始使用。但是，在这种情况下，服务器无需克隆即可直接在本地存储库上运行（如果它不是裸露的，这并不重要，因为Config Server从不对“远程”存储库进行更改）。要扩展Config Server并使其高度可用，您需要使服务器的所有实例都指向同一存储库，因此仅共享文件系统可以工作。即使在那种情况下，最好对`ssh:`共享文件系统存储库使用该协议，以便服务器可以克隆该协议并将本地工作副本用作高速缓存。

此存储库实现将`{label}`HTTP资源的参数映射到git标签（提交ID，分支名称或标记）。如果git分支或标记名称包含斜杠（`/`），则应使用特殊字符串在HTTP URL中指定标签`(_)`（以避免与其他URL路径产生歧义）。例如，如果标签为`foo/bar`，则替换斜杠将导致以下标签：`foo(_)bar`。特殊字符串的包含`(_)`也可以应用于`{application}`参数。如果您使用命令行客户端（例如curl），请注意URL中的括号-您应使用单引号（''）将其从外壳中移出。

#### 跳过SSL证书验证

可以通过将该`git.skipSslValidation`属性设置为`true`（默认值为`false`）来禁用配置服务器对Git服务器的SSL证书的验证。

```bash
spring:
  cloud:
    config:
      server:
        git:
          uri: https://example.com/my/repo
          skipSslValidation: true
```

#### 设置HTTP连接超时

您可以配置配置服务器将等待获取HTTP连接的时间（以秒为单位）。使用该`git.timeout`属性。

```bash
spring:
  cloud:
    config:
      server:
        git:
          uri: https://example.com/my/repo
          timeout: 4
```

#### Git URI中的占位符

春天的云配置Server支持的占位符一个Git仓库URL `{application}`和`{profile}`（和`{label}`如果你需要它，但请记住，标签作为一个git标签反正适用）。因此，您可以使用类似于以下的结构来支持“ 每个应用程序一个存储库 ”策略：

```bash
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/myorg/{application}
```

您也可以使用类似的模式来支持“ 每个配置文件一个存储库 ”策略 `{profile}`。

此外，在`{application}`参数中使用特殊字符串“（_）” 可以启用对多个组织的支持，如以下示例所示：

```bash
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/{application}
```

其中`{application}`被设置在请求时以下面的格式：`organization(_)application`。

#### 模式匹配和多个存储库

Spring Cloud Config还通过在应用程序和配置文件名称上进行模式匹配来支持更复杂的需求。模式格式是用逗号分隔的`{application}/{profile}`名称列表（带通配符）（请注意，以通配符开头的模式可能需要加引号），如以下示例所示：

```bash
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          repos:
            simple: https://github.com/simple/config-repo
            special:
              pattern: special*/dev*,*special*/dev*
              uri: https://github.com/special/config-repo
            local:
              pattern: local*
              uri: file:/home/configsvc/config-repo
```

如果`{application}/{profile}`与任何模式都不匹配，则使用在下定义的默认URI `spring.cloud.config.server.git.uri`。在上面的示例中，对于“ 简单 ”存储库，模式为`simple/*`（它仅匹配`simple`在所有概要文件中命名的一个应用程序）。在“ 本地 ”库匹配所有应用程序名称开头`local`的所有配置文件（该`/*`后缀会自动添加到没有档案资料匹配的任何模式）。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 的“ 单行 ”中所使用的短切“ 简单 ”的例子可以只用于如果唯一的属性被设置为URI。如果您需要设置其他任何内容（凭证，模式等），则需要使用完整表格。 |

的`pattern`在回购属性实际上是一个数组，所以可以使用一个YAML阵列（或`[0]`，`[1]`在属性文件等后缀）绑定到多个图案。如果要运行具有多个配置文件的应用程序，则可能需要这样做，如以下示例所示：

```bash
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          repos:
            development:
              pattern:
                - '*/development'
                - '*/staging'
              uri: https://github.com/development/config-repo
            staging:
              pattern:
                - '*/qa'
                - '*/production'
              uri: https://github.com/staging/config-repo
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| Spring Cloud猜测包含不以结尾结尾的配置文件的模式`*`意味着您实际上要匹配以该模式开头的配置文件列表（因此`*/staging`是的快捷方式`["*/staging", "*/staging,*"]`，依此类推）。例如，这很普遍，例如，您需要在本地的“ 开发 ”配置文件中运行应用程序，而又需要在远程的“ 云 ”配置文件中运行应用程序。 |

每个存储库还可以选择将配置文件存储在子目录中，用于搜索这些目录的模式可以指定为`searchPaths`。以下示例在顶层显示了一个配置文件：

```bash
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          searchPaths: foo,bar*
```

在前面的示例中，服务器在顶层，`foo/`子目录以及名称以开头的任何子目录中搜索配置文件`bar`。

默认情况下，第一次请求配置时，服务器会克隆远程存储库。可以将服务器配置为在启动时克隆存储库，如以下顶级示例所示：

```bash
spring:
  cloud:
    config:
      server:
        git:
          uri: https://git/common/config-repo.git
          repos:
            team-a:
                pattern: team-a-*
                cloneOnStart: true
                uri: https://git/team-a/config-repo.git
            team-b:
                pattern: team-b-*
                cloneOnStart: false
                uri: https://git/team-b/config-repo.git
            team-c:
                pattern: team-c-*
                uri: https://git/team-a/config-repo.git
```

在前面的示例中，服务器在接受任何请求之前会在启动时克隆team-a的config-repo。在请求从存储库进行配置之前，不会克隆所有其他存储库。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 设置要在Config Server启动时克隆的存储库可以帮助在Config Server启动时快速识别配置错误的配置源（例如无效的存储库URI）。如果`cloneOnStart`未启用配置源，则Config Server可能会以配置错误或无效的配置源成功启动，并且直到应用程序从该配置源请求配置时才检测到错误。 |

#### 认证方式

要在远程存储库上使用HTTP基本认证，请分别添加`username`和`password`属性（不在URL中），如以下示例所示：

```bash
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          username: trolley
          password: strongpassword
```

如果您不使用HTTPS和用户凭据，则在将密钥存储在默认目录（`~/.ssh`）中且URI指向SSH位置（例如）时，SSH也应立即可用`git@github.com:configuration/cloud-configuration`。重要的是，`~/.ssh/known_hosts`文件中应包含Git服务器的条目，并采用`ssh-rsa`格式。`ecdsa-sha2-nistp256`不支持其他格式（例如）。为避免意外，您应该确保`known_hosts`Git服务器的文件中仅存在一个条目，并且该条目与您提供给配置服务器的URL匹配。如果您在URL中使用主机名，则要在文件中包含该主机名（而不是IP）`known_hosts`。使用JGit访问存储库，因此您找到的任何文档都应该适用。可以在以下位置设置HTTPS代理设置`~/.git/config`或（与其他JVM进程相同）具有系统属性（`-Dhttps.proxyHost`和`-Dhttps.proxyPort`）。

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 如果您不知道`~/.git`目录在哪里，请使用`git config --global`来操纵设置（例如`git config --global http.sslVerify false`）。 |

#### 使用AWS CodeCommit进行身份验证

Spring Cloud Config Server还支持[AWS CodeCommit](https://docs.aws.amazon.com/codecommit/latest/userguide/welcome.html)身份验证。从命令行使用Git时，AWS CodeCommit使用身份验证帮助程序。该帮助程序未与JGit库一起使用，因此，如果Git URI与AWS CodeCommit模式匹配，则会为AWS CodeCommit创建一个JGit CredentialProvider。AWS CodeCommit URI遵循以下模式：: //git-codecommit.$ {AWS_REGION} .amazonaws.com / $ {repopath}。

如果您提供带有AWS CodeCommit URI的用户名和密码，则它们必须是提供对存储库访问权限的[AWS accessKeyId和secretAccessKey](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSGettingStartedGuide/AWSCredentials.html)。如果您未指定用户名和密码，则使用[AWS Default Credential Provider链](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html)检索accessKeyId和secretAccessKey 。

如果您的Git URI与CodeCommit URI模式（如前所示）匹配，则必须在用户名和密码或默认凭据提供程序链支持的位置之一中提供有效的AWS凭据。AWS EC2实例可以将[IAM角色用于EC2实例](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/iam-roles-for-amazon-ec2.html)。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 该`aws-java-sdk-core`罐是一个可选的依赖。如果`aws-java-sdk-core`jar不在您的类路径中，则无论git服务器URI如何，都不会创建AWS Code Commit凭证提供程序。 |

#### 使用属性进行Git SSH配置

默认情况下，当通过SSH URI连接到Git存储库时，Spring Cloud Config Server使用的JGit库使用SSH配置文件，例如`~/.ssh/known_hosts`和`/etc/ssh/ssh_config`。在Cloud Foundry之类的云环境中，本地文件系统可能是临时的，或者不容易访问。对于那些情况，可以使用Java属性设置SSH配置。为了激活基于属性的SSH配置，`spring.cloud.config.server.git.ignoreLocalSshSettings`必须将属性设置为`true`，如以下示例所示：

```properties
  spring:
    cloud:
      config:
        server:
          git:
            uri: git@gitserver.com:team/repo1.git
            ignoreLocalSshSettings: true
            hostKey: someHostKey
            hostKeyAlgorithm: ssh-rsa
            privateKey: |
                         -----BEGIN RSA PRIVATE KEY-----
                         MIIEpgIBAAKCAQEAx4UbaDzY5xjW6hc9jwN0mX33XpTDVW9WqHp5AKaRbtAC3DqX
                         IXFMPgw3K45jxRb93f8tv9vL3rD9CUG1Gv4FM+o7ds7FRES5RTjv2RT/JVNJCoqF
                         ol8+ngLqRZCyBtQN7zYByWMRirPGoDUqdPYrj2yq+ObBBNhg5N+hOwKjjpzdj2Ud
                         1l7R+wxIqmJo1IYyy16xS8WsjyQuyC0lL456qkd5BDZ0Ag8j2X9H9D5220Ln7s9i
                         oezTipXipS7p7Jekf3Ywx6abJwOmB0rX79dV4qiNcGgzATnG1PkXxqt76VhcGa0W
                         DDVHEEYGbSQ6hIGSh0I7BQun0aLRZojfE3gqHQIDAQABAoIBAQCZmGrk8BK6tXCd
                         fY6yTiKxFzwb38IQP0ojIUWNrq0+9Xt+NsypviLHkXfXXCKKU4zUHeIGVRq5MN9b
                         BO56/RrcQHHOoJdUWuOV2qMqJvPUtC0CpGkD+valhfD75MxoXU7s3FK7yjxy3rsG
                         EmfA6tHV8/4a5umo5TqSd2YTm5B19AhRqiuUVI1wTB41DjULUGiMYrnYrhzQlVvj
                         5MjnKTlYu3V8PoYDfv1GmxPPh6vlpafXEeEYN8VB97e5x3DGHjZ5UrurAmTLTdO8
                         +AahyoKsIY612TkkQthJlt7FJAwnCGMgY6podzzvzICLFmmTXYiZ/28I4BX/mOSe
                         pZVnfRixAoGBAO6Uiwt40/PKs53mCEWngslSCsh9oGAaLTf/XdvMns5VmuyyAyKG
                         ti8Ol5wqBMi4GIUzjbgUvSUt+IowIrG3f5tN85wpjQ1UGVcpTnl5Qo9xaS1PFScQ
                         xrtWZ9eNj2TsIAMp/svJsyGG3OibxfnuAIpSXNQiJPwRlW3irzpGgVx/AoGBANYW
                         dnhshUcEHMJi3aXwR12OTDnaLoanVGLwLnkqLSYUZA7ZegpKq90UAuBdcEfgdpyi
                         PhKpeaeIiAaNnFo8m9aoTKr+7I6/uMTlwrVnfrsVTZv3orxjwQV20YIBCVRKD1uX
                         VhE0ozPZxwwKSPAFocpyWpGHGreGF1AIYBE9UBtjAoGBAI8bfPgJpyFyMiGBjO6z
                         FwlJc/xlFqDusrcHL7abW5qq0L4v3R+FrJw3ZYufzLTVcKfdj6GelwJJO+8wBm+R
                         gTKYJItEhT48duLIfTDyIpHGVm9+I1MGhh5zKuCqIhxIYr9jHloBB7kRm0rPvYY4
                         VAykcNgyDvtAVODP+4m6JvhjAoGBALbtTqErKN47V0+JJpapLnF0KxGrqeGIjIRV
                         cYA6V4WYGr7NeIfesecfOC356PyhgPfpcVyEztwlvwTKb3RzIT1TZN8fH4YBr6Ee
                         KTbTjefRFhVUjQqnucAvfGi29f+9oE3Ei9f7wA+H35ocF6JvTYUsHNMIO/3gZ38N
                         CPjyCMa9AoGBAMhsITNe3QcbsXAbdUR00dDsIFVROzyFJ2m40i4KCRM35bC/BIBs
                         q0TY3we+ERB40U8Z2BvU61QuwaunJ2+uGadHo58VSVdggqAo0BSkH58innKKt96J
                         69pcVH/4rmLbXdcmNYGm6iu+MlPQk4BUZknHSmVHIFdJ0EPupVaQ8RHT
                         -----END RSA PRIVATE KEY-----
```

下表描述了SSH配置属性。



**表5.1。SSH配置属性**

| 物业名称                     | 备注                                                         |
| ---------------------------- | ------------------------------------------------------------ |
| **ignoreLocalSshSettings**   | 如果为`true`，请使用基于属性而不是基于文件的SSH配置。必须设置为`spring.cloud.config.server.git.ignoreLocalSshSettings`，**而不是**在存储库定义中。 |
| **私钥**                     | 有效的SSH私钥。如果`ignoreLocalSshSettings`为true并且Git URI为SSH格式，则必须设置。 |
| **hostKey**                  | 有效的SSH主机密钥。如果`hostKeyAlgorithm`还设置，则必须设置。 |
| **hostKeyAlgorithm**         | 之一`ssh-dss, ssh-rsa, ecdsa-sha2-nistp256, ecdsa-sha2-nistp384, or ecdsa-sha2-nistp521`。如果`hostKey`还设置，则必须设置。 |
| **strictHostKeyChecking**    | `true`或`false`。如果为false，请忽略主机密钥错误。           |
| **knownHostsFile**           | 自定义`.known_hosts`文件的位置。                             |
| **preferredAuthentications** | 覆盖服务器身份验证方法顺序。如果服务器在此`publickey`方法之前具有键盘交互身份验证，则应该可以避免登录提示。 |



#### Git搜索路径中的占位符

春季云配置服务器也支持占位符的搜索路径`{application}`和`{profile}`（和`{label}`如果需要的话），如下面的例子：

```properties
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          searchPaths: '{application}'
```

上面的清单导致在存储库中搜索与目录（以及顶层）同名的文件。通配符在带有占位符的搜索路径中也有效（搜索中包括任何匹配的目录）。

#### 强制拉入Git存储库

如前所述，Spring Cloud Config Server会克隆远程git存储库，以防本地副本变脏（例如，操作系统进程更改了文件夹内容），使得Spring Cloud Config Server无法从远程存储库更新本地副本。

为了解决这个问题，有一个`force-pull`属性可以使Spring Cloud Config Server在本地副本脏的情况下从远程存储库强制拉出，如以下示例所示：

```properties
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          force-pull: true
```

如果您具有多个存储库配置，则可`force-pull`以为每个存储库配置属性，如以下示例所示：

```properties
spring:
  cloud:
    config:
      server:
        git:
          uri: https://git/common/config-repo.git
          force-pull: true
          repos:
            team-a:
                pattern: team-a-*
                uri: https://git/team-a/config-repo.git
                force-pull: true
            team-b:
                pattern: team-b-*
                uri: https://git/team-b/config-repo.git
                force-pull: true
            team-c:
                pattern: team-c-*
                uri: https://git/team-a/config-repo.git
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| `force-pull`属性的默认值为`false`。                          |

#### 删除Git存储库中未跟踪的分支

由于Spring Cloud Config Server在将分支检出到本地存储库（例如，通过标签获取属性）后具有远程git存储库的克隆，因此它将永久保留该分支，直到下一个服务器重启（这将创建新的本地存储库）。因此，有可能删除远程分支，但仍可获取其本地副本。而且，如果Spring Cloud Config Server客户端服务以`--spring.cloud.config.label=deletedRemoteBranch,master` 该服务开头，它将从`deletedRemoteBranch`本地分支获取属性，而不是从`master`。

为了使本地存储库分支保持整洁并保持远程状态- `deleteUntrackedBranches`可以设置属性。它将使Spring Cloud Config Server **强制**从本地存储库中删除未跟踪的分支。例：

```properties
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          deleteUntrackedBranches: true
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| `deleteUntrackedBranches`属性的默认值为`false`。             |

#### Git刷新率

您可以使用来控制配置服务器多久从Git后端获取更新的配置数据`spring.cloud.config.server.git.refreshRate`。以秒为单位指定此属性的值。默认情况下，该值为0，这意味着配置服务器将在每次请求时从Git存储库中获取更新的配置。

### 5.1.2版本控制后端文件系统使用

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| 使用基于VCS的后端（git，svn），检出文件或将其克隆到本地文件系统。默认情况下，它们以前缀放置在系统临时目录中`config-repo-`。例如，在linux上，它可能是`/tmp/config-repo-`。一些操作系统[通常会清除](https://serverfault.com/questions/377348/when-does-tmp-get-cleared/377349#377349)临时目录。这可能导致意外的行为，例如缺少属性。为避免此问题，请通过设置将Config Server使用的目录更改为`spring.cloud.config.server.git.basedir`或`spring.cloud.config.server.svn.basedir`不在系统临时结构中的目录。 |

### 5.1.3文件系统后端

Config Server中还有一个“ 本机 ”配置文件，该配置文件不使用Git，而是从本地类路径或文件系统（您要使用指向的任何静态URL）中加载配置文件`spring.cloud.config.server.native.searchLocations`。要使用本机配置文件，请使用启动启动配置服务器`spring.profiles.active=native`。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 切记`file:`对文件资源使用前缀（不带前缀的默认值通常是类路径）。与任何Spring Boot配置一样，您可以嵌入`${}`样式的环境占位符，但请记住Windows中的绝对路径需要额外的`/`（例如`file:///${user.home}/config-repo`）。 |

| ![[警告]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/warning.png) |
| ------------------------------------------------------------ |
| 的默认值`searchLocations`与本地Spring Boot应用程序（即`[classpath:/, classpath:/config, file:./, file:./config]`）相同。这不会`application.properties`从服务器向所有客户端公开，因为服务器中存在的所有属性源在发送给客户端之前都会被删除。 |

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 文件系统后端非常适合快速入门和测试。要在生产环境中使用它，您需要确保文件系统可靠并且可以在Config Server的所有实例之间共享。 |

搜索位置可以包含占位符`{application}`，`{profile}`和`{label}`。这样，您可以隔离路径中的目录并选择对您有意义的策略（例如，每个应用程序的子目录或每个配置文件的子目录）。

如果您不在搜索位置中使用占位符，则此存储库还将`{label}`HTTP资源的参数附加到搜索路径上的后缀，因此将从每个搜索位置**和**与标签同名的子目录（即标记的属性在Spring Environment中优先）。因此，没有占位符的默认行为与添加以结尾的搜索位置相同`/{label}/`。例如，`file:/tmp/config`与相同`file:/tmp/config,file:/tmp/config/{label}`。可以通过设置禁用此行为`spring.cloud.config.server.native.addLabelLocations=false`。

### 5.1.4保管库后端

Spring Cloud Config Server还支持将[Vault](https://www.vaultproject.io/)作为后端。

保险柜是用于安全访问机密的工具。秘密是您要严格控制访问权限的任何内容，例如API密钥，密码，证书和其他敏感信息。保险柜提供了对任何机密信息的统一界面，同时提供了严格的访问控制并记录了详细的审核日志。

有关Vault的更多信息，请参阅[Vault快速入门指南](https://learn.hashicorp.com/vault/?track=getting-started#getting-started)。

要使配置服务器能够使用Vault后端，您可以使用`vault`配置文件运行配置服务器。例如，在您的配置服务器中`application.properties`，您可以添加`spring.profiles.active=vault`。

默认情况下，配置服务器假定您的Vault服务器运行在`http://127.0.0.1:8200`。它还假定backend的名称为`secret`，密钥为`application`。所有这些默认值都可以在配置服务器的中配置`application.properties`。下表描述了可配置的保管库属性：

| 名称              | 默认值    |
| ----------------- | --------- |
| 主办              | 127.0.0.1 |
| 港口              | 8200      |
| 方案              | http      |
| 后端              | 秘密      |
| defaultKey        | 应用      |
| profileSeparator  | ，        |
| kv版本            | 1个       |
| skipSslValidation | 假        |
| 暂停              | 5         |
| 命名空间          | 空值      |

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 上表中的所有属性都必须`spring.cloud.config.server.vault`在复合配置的正确Vault部分中添加前缀或放置在其中。 |      |

所有可配置的属性都可以在中找到`org.springframework.cloud.config.server.environment.VaultEnvironmentProperties`。

Vault 0.10.0引入了版本化的键值后端（k / v后端版本2），该后端公开了与早期版本不同的API，现在它需要`data/`在安装路径和实际上下文路径之间使用，并将秘密包装在`data`对象中。设置`kvVersion=2`将考虑到这一点。

（可选）支持Vault Enterprise `X-Vault-Namespace`标头。要将其发送到保管库，请设置`namespace`属性。

在配置服务器运行的情况下，您可以向服务器发出HTTP请求，以从Vault后端检索值。为此，您需要Vault服务器的令牌。

首先，将一些数据放入Vault中，如以下示例所示：

```bash
$ vault kv put secret/application foo=bar baz=bam
$ vault kv put secret/myapp foo=myappsbar
```

其次，向配置服务器发出HTTP请求以检索值，如以下示例所示：

```bash
$ curl -X "GET" "http://localhost:8888/myapp/default" -H "X-Config-Token: yourtoken"
```

您应该看到类似于以下内容的响应：

```bash
{
   "name":"myapp",
   "profiles":[
      "default"
   ],
   "label":null,
   "version":null,
   "state":null,
   "propertySources":[
      {
         "name":"vault:myapp",
         "source":{
            "foo":"myappsbar"
         }
      },
      {
         "name":"vault:application",
         "source":{
            "baz":"bam",
            "foo":"bar"
         }
      }
   ]
}
```

#### 多个属性来源

使用Vault时，可以为您的应用程序提供多个属性源。例如，假设您已将数据写入Vault中的以下路径：

```properties
secret/myApp,dev
secret/myApp
secret/application,dev
secret/application
```

[使用Config Server](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_config_server.html)写入的属性`secret/application`可用于[所有应用程序](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_config_server.html)。随着名称的应用程序，`myApp`，必须写入任何属性`secret/myApp`，并`secret/application`提供给它。当`myApp`有`dev`启用配置文件，写入到上述所有路径的性能将提供给它，在列表中的优先级比其他的第一路径属性。

### 5.1.5通过代理访问后端

配置服务器可以通过HTTP或HTTPS代理访问Git或Vault后端。通过`proxy.http`和下的设置可以控制Git或Vault的此行为`proxy.https`。这些设置是针对每个存储库的，因此，如果您使用[组合环境存储库](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_config_server.html#composite-environment-repositories)，则必须分别为组合中的每个后端配置代理设置。如果使用的网络需要HTTP和HTTPS URL分别使用代理服务器，则可以为单个后端配置HTTP和HTTPS代理设置。

下表描述了HTTP和HTTPS代理的代理配置属性。所有这些属性都必须以`proxy.http`或作为前缀`proxy.https`。



**表5.2。代理配置属性**

| 物业名称          | 备注                                                         |
| ----------------- | ------------------------------------------------------------ |
| **主办**          | 代理的主机。                                                 |
| **港口**          | 用于访问代理的端口。                                         |
| **nonProxyHosts** | 配置服务器应在代理外部访问的所有主机。如果同时为`proxy.http.nonProxyHosts`和提供值，则将使用`proxy.https.nonProxyHosts`该`proxy.http`值。 |
| **用户名**        | 用来验证代理的用户名。如果同时为`proxy.http.username`和提供值，则将使用`proxy.https.username`该`proxy.http`值。 |
| **密码**          | 用来验证代理的密码。如果同时为`proxy.http.password`和提供值，则将使用`proxy.https.password`该`proxy.http`值。 |



以下配置使用HTTPS代理访问Git存储库。

```properties
spring:
  profiles:
    active: git
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          proxy:
            https:
              host: my-proxy.host.io
              password: myproxypassword
              port: '3128'
              username: myproxyusername
              nonProxyHosts: example.com
```

### 5.1.6与所有应用程序共享配置

所有应用程序之间的共享配置根据您采用的方法而异，如以下主题所述：

- [称为“基于文件的存储库”的部分](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_config_server.html#spring-cloud-config-server-file-based-repositories)
- [“保管库服务器”部分](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_config_server.html#spring-cloud-config-server-vault-server)

#### 基于文件的存储库

基于文件（GIT，SVN和本地）仓库，并在文件名的资源`application*`（`application.properties`，`application.yml`，`application-*.properties`，等）的所有客户端应用程序之间共享。您可以使用具有这些文件名的资源来配置全局默认值，并在需要时使用特定于应用程序的文件覆盖它们。

\#_property_overrides [属性覆盖]功能还可用于设置全局默认值，允许使用占位符应用程序在本地覆盖它们。

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 随着“ 原生 ”配置文件（本地文件系统后端），你应该使用显式搜索的位置，是不是服务器的配置的一部分。否则，`application*`默认搜索位置中的资源将被删除，因为它们是服务器的一部分。 |

#### 保管箱服务器

使用Vault作为后端时，可以通过将配置放在中来与所有应用程序共享配置`secret/application`。例如，如果您运行以下Vault命令，则使用配置服务器的所有应用程序都将具有属性`foo`并`baz`可供其使用：

```properties
$ vault write secret/application foo=bar baz=bam
```

### 5.1.7 JDBC后端

Spring Cloud Config Server支持JDBC（关系数据库）作为配置属性的后端。您可以通过添加`spring-jdbc`到类路径并使用`jdbc`配置文件或通过添加类型的Bean 来启用此功能`JdbcEnvironmentRepository`。如果您在类路径上包括正确的依赖项（有关更多详细信息，请参见用户指南），Spring Boot会配置数据源。

数据库需要有一个表`PROPERTIES`，该表包含名为`APPLICATION`，`PROFILE`和`LABEL`（具有通常的`Environment`含义）的列，加上`KEY`和`VALUE`表示键和值对的`Properties`样式。Java中的所有字段均为String类型，因此您可以根据`VARCHAR`需要设置它们的长度。属性值的行为方式与它们来自名为的Spring Boot属性文件的行为相同`{application}-{profile}.properties`，包括所有加密和解密，这将作为后处理步骤应用（即，不直接在存储库实现中使用）。

### 5.1.8 CredHub后端

Spring Cloud Config Server支持[CredHub](https://docs.cloudfoundry.org/credhub)作为配置属性的后端。您可以通过向[Spring CredHub](https://spring.io/projects/spring-credhub)添加依赖项来启用此功能。

**pom.xml。** 

```xml
<dependencies>
	<dependency>
		<groupId>org.springframework.credhub</groupId>
		<artifactId>spring-credhub-starter</artifactId>
	</dependency>
</dependencies>
```



以下配置使用双向TLS访问CredHub：

```properties
spring:
  profiles:
    active: credhub
  cloud:
    config:
      server:
        credhub:
          url: https://credhub:8844
```

这些属性应存储为JSON，例如：

```json
credhub set --name "/demo-app/default/master/toggles" --type=json
value: {"toggle.button": "blue", "toggle.link": "red"}

credhub set --name "/demo-app/default/master/abs" --type=json
value: {"marketing.enabled": true, "external.enabled": false}
```

具有该名称的所有客户端应用程序`spring.cloud.config.name=demo-app`将具有以下可用属性：

```bash
{
    toggle.button: "blue",
    toggle.link: "red",
    marketing.enabled: true,
    external.enabled: false
}
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 当未指定配置文件`default`时，`master`将使用默认值，当未指定标签时，将使用默认值。 |

#### OAuth 2.0

您可以使用[UAA](https://docs.cloudfoundry.org/concepts/architecture/uaa.html)作为提供程序通过[OAuth 2.0](https://oauth.net/2/)进行身份验证。

**pom.xml。** 

```xml
<dependencies>
	<dependency>
		<groupId>org.springframework.security</groupId>
		<artifactId>spring-security-config</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.security</groupId>
		<artifactId>spring-security-oauth2-client</artifactId>
	</dependency>
</dependencies>
```



以下配置使用OAuth 2.0和UAA访问CredHub：

```properties
spring:
  profiles:
    active: credhub
  cloud:
    config:
      server:
        credhub:
          url: https://credhub:8844
          oauth2:
            registration-id: credhub-client
  security:
    oauth2:
      client:
        registration:
          credhub-client:
            provider: uaa
            client-id: credhub_config_server
            client-secret: asecret
            authorization-grant-type: client_credentials
        provider:
          uaa:
            token-uri: https://uaa:8443/oauth/token
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 使用的UAA客户ID应该具有`credhub.read`范围。                  |

### 5.1.9复合环境存储库

在某些情况下，您可能希望从多个环境存储库中提取配置数据。为此，您可以`composite`在配置服务器的应用程序属性或YAML文件中启用配置文件。例如，如果要从Subversion存储库以及两个Git存储库中提取配置数据，则可以为配置服务器设置以下属性：

```properties
spring:
  profiles:
    active: composite
  cloud:
    config:
      server:
        composite:
        -
          type: svn
          uri: file:///path/to/svn/repo
        -
          type: git
          uri: file:///path/to/rex/git/repo
        -
          type: git
          uri: file:///path/to/walter/git/repo
```

使用此配置，优先级由`composite`键下存储库列出的顺序确定。在上面的示例中，首先列出了Subversion存储库，因此在Subversion存储库中找到的值将覆盖在其中一个Git存储库中为同一属性找到的值。在`rex`Git存储库中找到的值将在Git存储库中为相同属性找到的值之前使用`walter`。

如果只想从各自不同类型的存储库中提取配置数据，则可以`composite`在配置服务器的应用程序属性或YAML文件中启用相应的配置文件，而不是配置文件。例如，如果要从单个Git存储库和单个HashiCorp Vault服务器提取配置数据，则可以为配置服务器设置以下属性：

```properties
spring:
  profiles:
    active: git, vault
  cloud:
    config:
      server:
        git:
          uri: file:///path/to/git/repo
          order: 2
        vault:
          host: 127.0.0.1
          port: 8200
          order: 1
```

使用此配置，可以通过`order`属性确定优先级。您可以使用该`order`属性为所有存储库指定优先级顺序。`order`属性的数值越低，其优先级越高。存储库的优先级顺序有助于解决包含相同属性值的存储库之间的任何潜在冲突。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果您的复合环境包括上一个示例中的Vault服务器，则在对配置服务器的每个请求中都必须包含Vault令牌。请参阅[保管库后端](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/multi__spring_cloud_config_server.html#vault-backend)。 |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 从环境存储库中检索值时，任何类型的故障都会导致整个组合环境的故障。 |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 使用复合环境时，所有存储库都包含相同的标签很重要。如果您的环境与前面的示例中的环境类似，并且您请求带有`master`标签的配置数据，但是Subversion存储库不包含名为的分支`master`，则整个请求将失败。 |

#### 定制复合环境存储库

除了使用Spring Cloud中的一个环境存储库之外，您还可以提供自己的`EnvironmentRepository`bean，以将其包含在复合环境中。为此，您的bean必须实现该`EnvironmentRepository`接口。如果要`EnvironmentRepository`在复合环境中控制自定义的优先级，则还应该实现该`Ordered`接口并重写该`getOrdered`方法。如果您不实现该`Ordered`接口，则您`EnvironmentRepository`的优先级最低。

### 5.1.10属性替代

Config Server具有“ 替代 ”功能，使操作员可以为所有应用程序提供配置属性。应用程序使用常规的Spring Boot钩子不会意外更改重写的属性。要声明覆盖，请将名称/值对的映射添加到`spring.cloud.config.server.overrides`，如以下示例所示：

```properties
spring:
  cloud:
    config:
      server:
        overrides:
          foo: bar
```

前面的示例使所有作为配置客户端的应用程序读取`foo=bar`，而与它们自己的配置无关。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 配置系统不能强制应用程序以任何特定方式使用配置数据。因此，覆盖无法执行。但是，它们确实为Spring Cloud Config客户端提供了有用的默认行为。 |

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 通常，`${}`可以使用反斜杠（`\`）对`$`或进行转义，以逃避（并在客户端上解析）Spring环境占位符`{`。例如，`\${app.foo:bar}`解析为`bar`，除非应用提供自己的`app.foo`。 |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 在YAML中，您不需要转义反斜杠本身。但是，在属性文件中，在服务器上配置替代时，您确实需要转义反斜杠。 |

您可以通过设置`spring.cloud.config.overrideNone=true`远程存储库中的标志（默认为false），使客户端中所有替代的优先级更像默认值，让应用程序在环境变量或系统属性中提供自己的值。

## 5.2健康指标

Config Server带有运行状况指示器，用于检查配置的`EnvironmentRepository`是否正常。默认情况下，它会要求`EnvironmentRepository`提供一个名为`app`，`default`配置文件和`EnvironmentRepository`实现提供的默认标签的应用程序。

您可以配置运行状况指示器以检查更多应用程序以及自定义配置文件和自定义标签，如以下示例所示：

```properties
spring:
  cloud:
    config:
      server:
        health:
          repositories:
            myservice:
              label: mylabel
            myservice-dev:
              name: myservice
              profiles: development
```

您可以通过设置禁用运行状况指示器`spring.cloud.config.server.health.enabled=false`。

## 5.3安全性

您可以使用对您有意义的任何方式来保护Config Server（从物理网络安全性到OAuth2承载令牌），因为Spring Security和Spring Boot提供了对许多安全性安排的支持。

要使用默认的Spring Boot配置的HTTP Basic安全性，请在类路径中包含Spring Security（例如，通过`spring-boot-starter-security`）。默认值为用户名`user`和随机生成的密码。随机密码在实践中没有用，因此我们建议您（通过设置`spring.security.user.password`）配置密码并对其进行加密（有关如何操作的说明，请参见下文）。

## 5.4加密和解密

| ![[重要]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/important.png) | 重要 |
| ------------------------------------------------------------ | ---- |
| 要使用加密和解密功能，您需要在JVM中安装完整强度的JCE（默认情况下不包括）。您可以从Oracle 下载“ Java密码学扩展（JCE）无限强度辖区策略文件 ”并按照安装说明进行操作（本质上，您需要用下载的JRE lib / security目录替换这两个策略文件）。 |      |

如果远程属性源包含加密的内容（以开头的值`{cipher}`），则在通过HTTP发送给客户端之前，将对它们进行解密。此设置的主要优点是，当属性值处于“ 静止 ”状态时（例如，在git存储库中），不需要使用纯文本格式。如果无法解密某个值，则将其从属性源中删除，并使用相同的键但以开头的附加属性添加`invalid`一个值，该值表示“ 不适用 ”（通常为``）。这在很大程度上是为了防止密文用作密码并意外泄漏。

如果为配置客户端应用程序设置远程配置存储库，则它可能包含`application.yml`与以下内容类似的内容：

**application.yml。** 

```properties
spring:
  datasource:
    username: dbuser
    password: '{cipher}FKSAJDFGYOS8F7GLHAKERGFHLSAJ'
```



.properties文件中的加密值不能用引号引起来。否则，该值不会解密。以下示例显示了有效的值：

**application.properties。** 

```properties
spring.datasource.username: dbuser
spring.datasource.password: {cipher}FKSAJDFGYOS8F7GLHAKERGFHLSAJ
```



您可以安全地将此纯文本推送到共享的git存储库，并且秘密密码仍然受到保护。

服务器还公开`/encrypt`和`/decrypt`终结点（假设它们是安全的，并且只能由授权的代理访问）。如果您编辑远程配置文件，则可以使用Config Server通过POST到`/encrypt`端点来加密值，如以下示例所示：

```bash
$ curl localhost:8888/encrypt -d mysecret
682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 如果您加密的值中包含需要URL编码的字符，则应使用该`--data-urlencode`选项来`curl`确保对它们进行正确的编码。 |

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 确保不要在加密值中包含任何curl命令统计信息。将值输出到文件可以帮助避免此问题。 |

反向操作也可以通过以下方式获得`/decrypt`（如果服务器配置了对称密钥或完整密钥对），如以下示例所示：

```bash
$ curl localhost:8888/decrypt -d 682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
mysecret
```

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 如果您使用curl进行测试，请使用`--data-urlencode`（而不是`-d`）或设置显式值`Content-Type: text/plain`，以确保在有特殊字符时，curl可以正确编码数据（'+'特别棘手）。 |

在将加密的值`{cipher}`放入YAML或属性文件之前，以及在提交并将其推送到远程（可能不安全）存储之前，请获取加密的值并添加前缀。

该`/encrypt`和`/decrypt`终点还兼有接受的形式路径`/*/{name}/{profiles}`，当客户打电话到主环境资源，这可以用来控制加密的每个应用程序（名称）和每个配置文件的基础。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 要以这种精细的方式控制密码，您还必须提供一种`@Bean`类型`TextEncryptorLocator`，该类型针对每个名称和配置文件创建不同的加密器。默认情况下不提供（所有加密使用相同的密钥）。 |

该`spring`命令行客户机（安装弹簧云CLI扩展）也可以用于加密和解密，因为显示在下面的例子：

```bash
$ spring encrypt mysecret --key foo
682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
$ spring decrypt --key foo 682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
mysecret
```

要使用文件中的密钥（例如用于加密的RSA公钥），请在密钥值前添加“ @”并提供文件路径，如以下示例所示：

```bash
$ spring encrypt mysecret --key @${HOME}/.ssh/id_rsa.pub
AQAjPgt3eFZQXwt8tsHAVv/QHiY5sI2dRcR+...
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 该`--key`参数是强制性的（尽管有`--`前缀）。                  |

## 5.5密钥管理

Config Server可以使用对称（共享）密钥或非对称密钥（RSA密钥对）。非对称选择在安全性方面是优越的，但是使用对称密钥通常更方便，因为它是在中配置的单个属性值`bootstrap.properties`。

要配置对称密钥，您需要将其设置`encrypt.key`为秘密字符串（或使用`ENCRYPT_KEY`环境变量将其保留在纯文本配置文件之外）。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 您不能使用来配置非对称密钥`encrypt.key`。                    |

要配置非对称密钥，请使用密钥库（例如，由`keytool`JDK附带的实用程序创建的密钥库）。密钥存储属性是`encrypt.keyStore.*`与`*`等于

| 属性                        | 描述                                  |
| --------------------------- | ------------------------------------- |
| `encrypt.keyStore.location` | 包含`Resource`位置                    |
| `encrypt.keyStore.password` | 持有用于解锁密钥库的密码              |
| `encrypt.keyStore.alias`    | 标识商店中要使用的密钥                |
| `encrypt.keyStore.type`     | 要创建的KeyStore的类型。默认为`jks`。 |

加密是使用公钥完成的，解密需要私钥。因此，原则上，如果您只想加密（并准备使用私钥在本地解密值），则只能在服务器中配置公钥。实际上，您可能不希望在本地进行解密，因为它会将密钥管理过程分布在所有客户端上，而不是将其集中在服务器上。另一方面，如果您的配置服务器相对不安全并且只有少数客户端需要加密的属性，那么它可能是一个有用的选项。

## 5.6创建密钥库进行测试

要创建用于测试的密钥库，可以使用类似于以下内容的命令：

```bash
$ keytool -genkeypair -alias mytestkey -keyalg RSA \
  -dname "CN=Web Server,OU=Unit,O=Organization,L=City,S=State,C=US" \
  -keypass changeme -keystore server.jks -storepass letmein
```

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 使用JDK 11或更高版本时，使用上述命令时可能会收到以下警告。在这种情况下，您可能要确保`keypass`和`storepass`值匹配。 |

```
警告：PKCS12 KeyStore不支持不同的存储和密钥密码。忽略用户指定的-keypass值。
```

将`server.jks`文件放入类路径（例如），然后在`bootstrap.yml`Config Server的中，创建以下设置：

```properties
encrypt:
  keyStore:
    location: classpath:/server.jks
    password: letmein
    alias: mytestkey
    secret: changeme
```

## 5.7使用多个键和键旋转

除了`{cipher}`加密属性值中的前缀之外，Config Server `{name:value}`在（Base64编码的）密文开始之前查找零个或多个前缀。密钥被传递给a `TextEncryptorLocator`，后者可以执行`TextEncryptor`为密码找到a 所需的任何逻辑。如果已配置密钥库（`encrypt.keystore.location`），则默认定位器将查找具有`key`前缀提供的别名的密钥，该密钥的密码文本类似于以下内容：

```properties
foo:
  bar: `{cipher}{key:testkey}...`
```

定位器查找名为“ testkey”的密钥。也可以通过使用`{secret:…}`前缀中的值来提供机密。但是，如果未提供，则默认为使用密钥库密码（这是在构建密钥库且未指定密钥时得到的密码）。如果确实提供了机密，则还应该使用custom加密机密`SecretLocator`。

当密钥仅用于加密几个字节的配置数据时（也就是说，它们未在其他地方使用），从密码的角度讲，几乎不需要旋转密钥。但是，您有时可能需要更改密钥（例如，在发生安全漏洞时）。在这种情况下，所有客户端都需要更改其源配置文件（例如，在git中），并`{key:…}`在所有密码中使用新的前缀。请注意，客户端需要首先检查Config Server密钥库中的密钥别名是否可用。

| ![[小费]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/tip.png) |
| ------------------------------------------------------------ |
| 如果要让Config Server处理所有加密以及解密，`{name:value}`则还可以将前缀作为纯文本添加到发布到`/encrypt`端点。 |

## 5.8提供加密的属性

有时，您希望客户端在本地解密配置，而不是在服务器中进行解密。在这种情况下，如果提供`encrypt.*`用于定位密钥的配置，则仍然可以具有`/encrypt`和`/decrypt`端点，但是需要通过放置`spring.cloud.config.server.encrypt.enabled=false`在中来显式关闭对传出属性的解密`bootstrap.[yml|properties]`。如果您不关心端点，则在不配置键或启用标志的情况下都可以使用。