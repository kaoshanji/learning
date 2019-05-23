package top.kaoshanji;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author kaoshanji
 * @time 2019/5/23 15:33
 * 消费者
 */
@Component
public class Receiver {

    @RabbitListener(queues = "springcloud")
    public void process(Message msg) {
        System.out.println("....Receiver...process:"+msg.toString());
    }


}
