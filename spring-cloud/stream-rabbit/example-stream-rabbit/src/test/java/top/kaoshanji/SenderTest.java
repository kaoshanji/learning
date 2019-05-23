package top.kaoshanji;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author kaoshanji
 * @time 2019/5/23 15:45
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StreamRabbitApp.class)
public class SenderTest {

    @Autowired
    private Sender sender;

    @Test
    public void send() {
        sender.send();
    }


}
