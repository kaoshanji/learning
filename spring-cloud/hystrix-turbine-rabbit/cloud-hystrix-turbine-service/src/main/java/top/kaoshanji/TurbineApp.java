package top.kaoshanji;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;
import org.springframework.context.annotation.Bean;
import rx.subjects.PublishSubject;

import java.util.Map;

@EnableTurbineStream
@EnableEurekaClient
@SpringBootApplication
public class TurbineApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TurbineApp.class, args);
    }

    /*@Bean
    public org.springframework.cloud.netflix.turbine.stream.TurbineController turbineController(PublishSubject<Map<String, Object>> hystrixSubject) {
        return new TurbineController(hystrixSubject);
    }*/
}
