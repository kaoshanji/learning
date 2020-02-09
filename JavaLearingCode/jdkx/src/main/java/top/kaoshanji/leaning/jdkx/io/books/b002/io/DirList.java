package top.kaoshanji.leaning.jdkx.io.books.b002.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 目录过滤器
 * @author kaoshanji
 * @time 2020/2/9 下午12:21
 */
public class DirList {

    public static void main(String[] args) {

        File path = new File(".");
        String[] list;
        if(args.length == 0)
            list = path.list();
        else
            list = path.list(new DirFilter(args[0]));
        Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);
        for(String dirItem : list)
            System.out.println(dirItem);
    }
}

    // 过滤条件接口
    class DirFilter implements FilenameFilter {

        private Pattern pattern;
        public DirFilter(String regex) {
            pattern = Pattern.compile(regex);
        }
        // 条件逻辑
        public boolean accept(File dir, String name) {
            return pattern.matcher(name).matches();
        }
    }
