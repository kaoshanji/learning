package top.kaoshanji;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * @author kaoshanji
 * @time 2019/5/29 14:36
 */
@Component
public class ErrorFilter extends ZuulFilter {

    private static Logger logger = LoggerFactory.getLogger(ErrorFilter.class);

    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run()  {

        RequestContext requestContext = RequestContext.getCurrentContext();
        Throwable throwable = requestContext.getThrowable();

        logger.error("this is a ErrorFilter: {}", throwable.getCause().getMessage());

        requestContext.set("error.status_code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        requestContext.set("error.exception", throwable.getCause());

        return null;
    }

}
