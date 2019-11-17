# 16.HTTP预告片

HTTP预告片是响应正文之后的一种特殊类型的HTTP标头的集合。尾部响应标头允许发送方在分块消息的末尾包括其他字段，以便提供在发送消息正文时可能动态生成的元数据，例如消息完整性检查，数字签名或后处理状态。

如果预告片标头准备就绪，`isTrailerFieldsReady()`将返回 `true`。然后，Servlet可以使用`getTrailerFields`该`HttpServletRequest`接口的方法读取HTTP请求的尾部标头 。如果预告片标头尚未准备好读取，则`isTrailerFieldsReady()`返回`false` 并会导致`IllegalStateException`。

Servlet可以通过提供接口`setTrailerFields()`方法的提供者来将尾部标头写入响应`HttpServletResponse`。下面的标题和类型的页眉必须*不*被包含在传递到地图密钥的集合`setTrailerFields()`：`Transfer-Encoding`， `Content-Length`，`Host`，控制和有条件的头，认证头，`Content-Encoding`，`Content-Type`，`Content-Range`，和`Trailer`。发送响应预告片时，必须包含一个名为的常规标头，`Trailer`其值是提供给该`setTrailerFields()`方法的映射中所有键的逗号分隔列表。`Trailer`标头的值可让客户端知道需要哪些预告片。

可以通过访问接口的`getTrailerFields()`方法来获取拖车头的供应商 `HttpServletResponse`。

请参阅[的javadoc](https://javaee.github.io/javaee-spec/)的`getTrailerFields()` 和`isTrailerFieldsReady()`在`HttpServletRequest`，并且`getTrailerFields()` 和`setTrailerFields()`在`HttpServletResponse`。