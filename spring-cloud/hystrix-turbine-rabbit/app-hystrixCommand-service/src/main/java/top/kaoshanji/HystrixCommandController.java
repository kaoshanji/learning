package top.kaoshanji;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kaoshanji
 * @time 2019/5/16 17:23
 */
@RestController
public class HystrixCommandController {

    @GetMapping("/hystrixCommand")
    @HystrixCommand(fallbackMethod = "hystrixCommandError")
    public String hystrixCommand(){
        return "hystrixCommand";
    }

    public String hystrixCommandError() {
        return "hystrixCommandError";
    }


}
