package top.kaoshanji;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author kaoshanji
 * @time 2019/5/16 10:57
 * 在业务层，调用提供者服务
 * 指定服务名称，配置断路处理
 */
@FeignClient(name = "app-producer-service", fallback = ExampleHelloServiceApiFallbackFactory.class)
public interface ExampleHelloServiceApiDepend {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    String hello();

}
