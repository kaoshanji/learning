package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.SortedMap;

import static top.kaoshanji.leaning.jdkx.io.books.b002.util.Print.print;
import static top.kaoshanji.leaning.jdkx.io.books.b002.util.Print.printnb;

/**
 * Charset类编码
 * @author kaoshanji
 * @time 2020/2/9 下午8:18
 */
public class AvailableCharSets {

    public static void main(String[] args) {

        SortedMap<String, Charset> charSets = Charset.availableCharsets();

        Iterator<String> it = charSets.keySet().iterator();
        while(it.hasNext()) {
            String csName = it.next();
            printnb(csName);
            Iterator aliases = charSets.get(csName).aliases().iterator();

            if(aliases.hasNext())
                printnb(": ");
            while(aliases.hasNext()) {
                printnb(aliases.next());
                if(aliases.hasNext())
                    printnb(", ");
            }
            print();
        }
    }


}
