package top.kaoshanji;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@EnableTurbine
@EnableEurekaClient
@SpringBootApplication
public class TurbineApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TurbineApp.class, args);
    }

}
