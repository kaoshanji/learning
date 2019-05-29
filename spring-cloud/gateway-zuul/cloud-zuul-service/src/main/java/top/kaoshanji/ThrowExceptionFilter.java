package top.kaoshanji;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * @author kaoshanji
 * @time 2019/5/29 14:38
 * 业务异常过滤器
 * 添加下列参数把异常交给zuul
 * error.status_code：错误编码
 * error.exception：Exception 异常对象
 * error.message：错误信息
 */
@Component
public class ThrowExceptionFilter extends ZuulFilter {

    private static Logger logger = LoggerFactory.getLogger(ThrowExceptionFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {

        RequestContext requestContext = RequestContext.getCurrentContext();

        try {
            doSomething();
        } catch (Exception e) {

            logger.error("........");

            requestContext.set("error.status_code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            requestContext.set("error.exception", e);
        }

        return null;
    }

    private void doSomething() {
        throw new RuntimeException("Exist some errors....");
    }


}
