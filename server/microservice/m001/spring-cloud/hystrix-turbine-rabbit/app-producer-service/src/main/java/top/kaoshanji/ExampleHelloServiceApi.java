package top.kaoshanji;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author kaoshanji
 * @time 2019/5/16 10:46
 * 定义对外接口
 */
@RequestMapping
public interface ExampleHelloServiceApi {

    /**
     * 注解有点坑，与 mvc 并不是 100% 兼容
     * @return
     */
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    String hello();

}
