package top.kaoshanji;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kaoshanji
 * @time 2019/5/23 15:35
 * Rabbit 配置
 */
@Configuration
public class RabbitConfig {

    @Bean
    public Queue springcloudQueue () {
        return new Queue("springcloud");
    }


}
