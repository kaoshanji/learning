package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 回现输入的每一行
 * @author kaoshanji
 * @time 2020/2/9 下午5:44
 */
public class Echo {

    public static void main(String[] args)
            throws IOException {

        BufferedReader stdin = new BufferedReader(
                new InputStreamReader(System.in));

        String s;
        while((s = stdin.readLine()) != null && s.length()!= 0)
            System.out.println(s);
        // An empty line or Ctrl-Z terminates the program
    }

}
