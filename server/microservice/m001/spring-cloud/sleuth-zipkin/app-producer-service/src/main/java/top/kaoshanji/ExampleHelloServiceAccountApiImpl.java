package top.kaoshanji;

import org.springframework.web.bind.annotation.RestController;

/**
 * @author kaoshanji
 * @time 2019/5/16 10:50
 * 具体实现
 */
@RestController
public class ExampleHelloServiceAccountApiImpl implements ExampleHelloServiceApi {

    @Override
    public String hello() {
        return "kaoshanji";
    }
}
