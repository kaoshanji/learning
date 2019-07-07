# 22.6 flash属性

Flash属性为一个请求提供了一种存储打算在另一个请求中使用的属性的方法。重定向时最常需要这种方法 - 例如， *Post / Redirect / Get*模式。Flash重定向（通常在会话中）之前临时保存Flash属性，以便在重定向后立即删除请求。

Spring MVC有两个主要的抽象支持flash属性。`FlashMap`用于`FlashMapManager`存储，检索和管理 `FlashMap`实例时用于保存Flash属性。

Flash属性支持始终处于“打开”状态，并且不需要显式启用，但如果不使用，则永远不会导致创建HTTP会话。在每个请求上都有一个“输入”，`FlashMap`其中包含从先前请求（如果有）传递的属性，以及一个“输出”，`FlashMap`其中包含要为后续请求保存的属性。这两个`FlashMap` 实例都可以通过静态方法从Spring MVC中的任何位置访问`RequestContextUtils`。

带注释的控制器通常不需要`FlashMap`直接使用。相反， `@RequestMapping`方法可以接受类型的参数，`RedirectAttributes`并使用它为重定向方案添加闪存属性。添加的Flash属性 `RedirectAttributes`会自动传播到“输出”FlashMap。类似地，在重定向之后，来自“输入”的属性`FlashMap`被自动添加到 `Model`服务于目标URL的控制器中。

**匹配闪存属性的请求**

Flash属性的概念存在于许多其他Web框架中，并且已被证明有时会暴露于并发性问题。这是因为根据定义，闪存属性将被存储直到下一个请求。然而，非常“下一个”请求可能不是预期的接收者而是另一个异步请求（例如轮询或资源请求），在这种情况下，过早地移除闪存属性。

为了减少此类问题的可能性，请使用目标重定向URL的路径和查询参数`RedirectView`自动“标记” `FlashMap`实例。反过来，`FlashMapManager`当查找“输入”时，默认值将该信息与传入请求进行匹配`FlashMap`。

这并不能完全消除并发问题的可能性，但是使用重定向URL中已有的信息可以大大减少它。因此，建议主要针对重定向方案使用闪存属性。