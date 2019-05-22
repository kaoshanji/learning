package top.kaoshanji;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kaoshanji
 * @time 2019-05-12 16:05
 */
@RefreshScope
@RestController
public class ConfigClientController {

    @Value("${from}")
    private String from;


    @GetMapping("/from")
    public String from() {
        return this.from;
    }

}
