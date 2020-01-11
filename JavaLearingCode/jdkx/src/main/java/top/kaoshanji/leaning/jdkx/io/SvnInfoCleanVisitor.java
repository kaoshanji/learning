package top.kaoshanji.leaning.jdkx.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * FileVisitor 接口示例
 * 删除指定文件夹下 svn 元数据
 * 所有的子目录，包含嵌套很多层的子目录
 * spring cloud 网关 定制逻辑??
 * 访问者模式的实现
 * @author kaoshanji
 * @time 2020/1/10 下午9:20
 */
public class SvnInfoCleanVisitor extends SimpleFileVisitor<Path> {

    private boolean cleanMark = false;

    private boolean isSvnFolder(Path dir) {
        return ".svn".equals(dir.getFileName().toString());
    }

    /**
     * 正在访问某个文件
     * @param file
     * @param attrs
     * @return
     * @throws IOException
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (cleanMark) {
            Files.setAttribute(file, "dos:readonly", false);
            Files.delete(file);
        }

        return FileVisitResult.CONTINUE;
    }

    /**
     * 访问某个文件出现错误
     * @param file
     * @param exc
     * @return
     * @throws IOException
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return super.visitFileFailed(file, exc);
    }

    /**
     * 在访问一个目录的全部子目录的内容之前被调用
     * @param dir
     * @param attrs
     * @return
     * @throws IOException
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (isSvnFolder(dir)) {
            cleanMark = true;
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * 在访问一个目录的全部子目录的内容之后被调用
     * @param dir
     * @param exc
     * @return
     * @throws IOException
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc == null && cleanMark) {
            Files.delete(dir);
            if (isSvnFolder(dir)) {
                cleanMark = false;
            }
        }
        return FileVisitResult.CONTINUE;
    }
}
