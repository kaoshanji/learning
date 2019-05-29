package top.kaoshanji;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author kaoshanji
 * @time 2019/5/29 10:33
 */
public interface StreamTopic {

    String OUTPUT = "stream-topic-output";
    String INPUT = "stream-topic-input";

    @Output(OUTPUT)
    MessageChannel output();

    @Input(INPUT)
    SubscribableChannel input();

}
