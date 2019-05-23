package top.kaoshanji;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author kaoshanji
 * @time 2019/5/23 15:29
 * 生产者创建消息
 */
@Component
public class Sender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send() {

        String context = "hello" + System.currentTimeMillis();
        System.out.println("....send....context:"+context);

        Message msg = MessageBuilder.withBody(context.getBytes()).build();

        amqpTemplate.convertAndSend("springcloud", msg);

    }


}
