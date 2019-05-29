package top.kaoshanji;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kaoshanji
 * @time 2019/5/29 10:41
 */
@RestController
public class StreamController {

    @Autowired
    private StreamTopic streamTopic;

    @GetMapping("/sendMessage")
    public String messageWithMQ(@RequestParam String message) {
        streamTopic.output().send(MessageBuilder.withPayload(message).build());
        return "ok";
    }

}
