package top.kaoshanji;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kaoshanji
 * @time 2019/5/15 18:40
 */
@RestController
public class ConsumerController {

    private final Logger logger = LoggerFactory.getLogger(ConsumerController.class);

    @Autowired
    ExampleHelloServiceApiDepend exampleHelloServiceApiDepend;

    @GetMapping("/helloConsumer")
    public String helloConsumer(){

        logger.info("......helloConsumer......");

        return exampleHelloServiceApiDepend.hello();
    }


}
