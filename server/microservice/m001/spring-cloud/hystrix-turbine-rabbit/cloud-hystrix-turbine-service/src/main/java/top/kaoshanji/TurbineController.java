package top.kaoshanji;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;
import rx.subjects.PublishSubject;

import java.util.Map;

/**
 * @author kaoshanji
 * @time 2019/5/27 18:10
 */
public class TurbineController  extends org.springframework.cloud.netflix.turbine.stream.TurbineController {

    public TurbineController(PublishSubject<Map<String, Object>> hystrixSubject) {
        super(hystrixSubject);
    }

    @GetMapping(value = "/turbine.stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream() {
        return super.stream();
    }
}
