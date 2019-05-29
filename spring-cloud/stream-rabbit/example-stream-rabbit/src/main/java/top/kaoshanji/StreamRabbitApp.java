package top.kaoshanji;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(StreamTopic.class)
@SpringBootApplication
public class StreamRabbitApp {

    public static void main( String[] args ) {
        SpringApplication.run(StreamRabbitApp.class, args);
    }

}
