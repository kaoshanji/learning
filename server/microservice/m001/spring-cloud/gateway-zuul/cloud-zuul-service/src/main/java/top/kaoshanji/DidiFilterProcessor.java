package top.kaoshanji;

import com.netflix.zuul.FilterProcessor;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * @author kaoshanji
 * @time 2019/5/29 14:34
 * 当过滤器执行抛出异常时捕获他，并向请求上下文中记录一些信息
 * 记录异常，在ErrorExtFilter里使用
 */
public class DidiFilterProcessor extends FilterProcessor {

    @Override
    public Object processZuulFilter(ZuulFilter filter) throws ZuulException {

        try {
            return super.processZuulFilter(filter);
        } catch (ZuulException e) {
            RequestContext requestContext = RequestContext.getCurrentContext();
            requestContext.set("failed.filter", filter);
            throw e;
        }
    }

}
