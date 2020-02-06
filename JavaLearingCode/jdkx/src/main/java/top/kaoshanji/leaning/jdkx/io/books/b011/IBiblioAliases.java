package top.kaoshanji.leaning.jdkx.io.books.b011;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 两个主机名是否相等
 * 只要 IP地址相同就是相等
 * @author kaoshanji
 * @time 2020/2/5 下午3:23
 */
public class IBiblioAliases {

    private final static Logger logger = LoggerFactory.getLogger(IBiblioAliases.class);

    public static void main(String[] args) throws UnknownHostException {

        InetAddress ibiblio = InetAddress.getByName("www.ibiblio.org");
        InetAddress helios = InetAddress.getByName("helios.ibiblio.org");

        if (ibiblio.equals(helios)) {
            logger.info("www.ibiblio.org is the same as helios.ibiblio.org");
        } else {
            logger.info("www.ibiblio.org is not the same as helios.ibiblio.org");
        }
    }


}
