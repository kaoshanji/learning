package top.kaoshanji;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author kaoshanji
 * @time 2019/5/15 18:40
 */
@RestController
public class ConsumerController {

    private final Logger logger = LoggerFactory.getLogger(ConsumerController.class);

    @Autowired
    RestTemplate restTemplate;

    private String producerUrl = "http://localhost:8101/";

    @GetMapping("/helloConsumer")
    public String helloConsumer(){

        logger.info("......consumer......");

        return restTemplate.getForEntity(producerUrl+"hello", String.class).getBody();
    }


}
