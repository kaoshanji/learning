# 9. Push Notifications and Spring Cloud Bus

## 9.推送通知和Spring Cloud Bus

许多源代码存储库提供程序（例如Github，Gitlab，Gitea，Gitee，Gogs或Bitbucket）都通过Webhook通知您存储库中的更改。您可以通过提供者的用户界面将Webhook配置为URL和感兴趣的一组事件。例如，[Github](https://developer.github.com/v3/activity/events/types/#pushevent)使用POST到Webhook，其JSON主体包含提交列表和`X-Github-Event`设置为的标头（）`push`。如果您在`spring-cloud-config-monitor`库上添加依赖项并在Config Server中激活Spring Cloud Bus，那么将`/monitor`启用端点。

激活Webhook后，Config Server会`RefreshRemoteApplicationEvent`针对其认为可能已更改的应用程序发送目标。变化检测可以被策略化。但是，默认情况下，它会查找与应用程序名称匹配的文件中的更改（例如，`foo.properties`针对`foo`应用程序，而`application.properties`针对所有应用程序）。要覆盖此行为时，可以使用的策略是`PropertyPathNotificationExtractor`，该策略接受请求标头和正文作为参数，并返回已更改文件路径的列表。

默认配置可与Github，Gitlab，Gitea，Gitee，Gogs或Bitbucket一起使用。除了来自Github，Gitlab，Gitee或Bitbucket的JSON通知之外，您还可以通过POST来触发更改通知，该通知`/monitor`采用形式的形式编码的正文参数`path={name}`。这样做会向与该`{name}`模式匹配的应用程序广播（可以包含通配符）。

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 在`RefreshRemoteApplicationEvent`仅在发送`spring-cloud-bus`时在两个配置服务器和客户端应用程序激活。 |

| ![[注意]](https://cloud.spring.io/spring-cloud-static/Greenwich.SR3/multi/images/note.png) |
| ------------------------------------------------------------ |
| 默认配置还检测本地git存储库中的文件系统更改。在这种情况下，不使用Webhook。但是，一旦您编辑配置文件，就会广播刷新。 |