# 36. Health Indicator

## 36.健康指标

Spring Cloud Stream为活页夹提供了健康指标。它以名称注册`binders`，可以通过设置`management.health.binders.enabled`属性来启用或禁用。

默认情况下`management.health.binders.enabled`设置为`false`。设置`management.health.binders.enabled`为`true`启用运行状况指示器，允许您访问`/health`端点以检索绑定程序运行状况指示器。

健康指标是特定于活页夹的，某些活页夹实现不一定提供健康指标。