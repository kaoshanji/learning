package top.kaoshanji;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableEurekaClient
@EnableHystrix
@EnableFeignClients
@SpringBootApplication
public class ConsumerApp {

    public static void main( String[] args ) {
        SpringApplication.run(ConsumerApp.class, args);
    }

}
