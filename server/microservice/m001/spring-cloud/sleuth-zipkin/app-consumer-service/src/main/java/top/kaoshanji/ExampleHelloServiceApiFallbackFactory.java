package top.kaoshanji;

import org.springframework.stereotype.Component;

/**
 * @author kaoshanji
 * @time 2019/5/16 10:57
 * 依赖服务出现异常时被执行
 */
@Component
public class ExampleHelloServiceApiFallbackFactory implements ExampleHelloServiceApiDepend {

    @Override
    public String hello() {
        return "hello";
    }

}
