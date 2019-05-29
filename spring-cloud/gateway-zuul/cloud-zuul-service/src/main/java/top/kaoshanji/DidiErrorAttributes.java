package top.kaoshanji;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * @author kaoshanji
 * @time 2019/5/29 14:33
 * 自定义异常信息
 */
public class DidiErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {

        Map<String, Object> result = super.getErrorAttributes(webRequest, includeStackTrace);
        result.remove("exception");
        return result;

    }
}
