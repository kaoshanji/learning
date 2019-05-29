package top.kaoshanji;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author kaoshanji
 * @time 2019/5/29 14:31
 * 访问过滤器
 * 请求 token 处理
 */
@Component
public class AccessFilter extends ZuulFilter {

    private static Logger logger = LoggerFactory.getLogger(AccessFilter.class);

    @Override
    public String filterType() {
        // 过滤器的类型，决定过滤器在请求的那个生命周期中执行
        // pre(1)：请求被路由之前调用，比如请求的效验
        // routing(2)：路由请求转发阶段，将外部请求转发到具体服务实例上去的过程
        // post(3)：在 routing 和 error 过滤器之后被调用
        // 可以获取请求信息，服务实例返回信息，可以对结果进行一些加工或转换等内容
        // 将最终结果返回给请求客户端
        // error(x)：处理请求时发生错误时被调用
        // 上述三个阶段中发生异常的时候才会触发，但是最后流向post类型的过滤器
        return "pre";
    }

    @Override
    public int filterOrder() {
        // 过滤器执行顺序，在一个阶段中存在多个过滤器时，需要该值来以此执行
        // 数值越小优先级越高
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        // 判断该过滤器是否需要被执行
        // 利用函数指定过滤器有效范围，比如那些需要token，那些不需要
        return true;
    }

    @Override
    public Object run()  {
        // 过滤器的具体逻辑
        // 实现自定义的过滤逻辑判断是否要拦截当前的请求，不对其进行后续的路由或是在请求路由返回结果之后对结果做一些加工

        // 获取请求上下文
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        logger.info("send {} request to {}", request.getMethod(), request.getRequestURI());

        // 获取token
        String token = request.getParameter("token");

        // 设置响应
        if (StringUtils.isEmpty(token)) {
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(401);
            requestContext.setResponseBody("token is empty");
            return null;
        }

        return null;
    }

}
