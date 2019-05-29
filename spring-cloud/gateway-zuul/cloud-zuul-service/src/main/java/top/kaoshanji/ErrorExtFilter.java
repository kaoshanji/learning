package top.kaoshanji;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter;
import org.springframework.stereotype.Component;

/**
 * @author kaoshanji
 * @time 2019/5/29 14:35
 */
@Component
public class ErrorExtFilter extends SendErrorFilter {

    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        // 大于 ErrorFilter 的值
        return 30;
    }

    @Override
    public boolean shouldFilter() {
        // 仅处理来自 post 过滤器引起的异常
        RequestContext requestContext = RequestContext.getCurrentContext();
        ZuulFilter failedFilter = (ZuulFilter)requestContext.get("failed.filter");
        if (null!=failedFilter && "post".equals(failedFilter.filterType())) {
            return true;
        }

        return false;
    }

}
