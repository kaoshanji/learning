package top.kaoshanji;

import org.springframework.stereotype.Component;

/**
 * @author kaoshanji
 * @time 2019/5/29 10:36
 */
@Component
public class StreamListener {

    @org.springframework.cloud.stream.annotation.StreamListener(StreamTopic.INPUT)
    public void receive(String payload) {
        System.out.println("Received: " + payload);
       // log.info("Received: " + payload);
    }


}
