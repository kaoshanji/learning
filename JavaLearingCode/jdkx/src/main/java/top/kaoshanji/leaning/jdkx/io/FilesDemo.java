package top.kaoshanji.leaning.jdkx.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * Files 示例
 * @author kaoshanji
 * @time 2020/1/10 下午8:45
 */
public class FilesDemo {

    private final static Logger logger = LoggerFactory.getLogger(FilesDemo.class);

    /**
     * Path 接口 示例
     */
    public void usePath() {
        Path path1 = Paths.get("folder1", "sub1");
        Path path2 = Paths.get("folder2", "sub2");

        path1.resolve(path2); // folder1\sub1\folder2\sub2
        path1.resolveSibling(path2); // folder1\folder2\sub2
        path1.relativize(path2); // ..\..\folder2\sub2
        path1.subpath(0, 1); //folder1
        path1.startsWith(path2); // false
        path1.endsWith(path2); //false
        Paths.get("folder1/./../folder2/my.text").normalize(); //folder2\my.text
    }

    /**
     * 目录列表流：当前目录下直接子目录或文件
     * 遍历出 java 文件
     */
    public void listFiles() {
        Path path = Paths.get("");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.java")){
            for (Path entry: stream) {
                logger.info(entry.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件目录树遍历
     * @throws IOException
     */
    public void cleanSVNInfo() throws IOException {
        Path path = Paths.get("C:", "chapter3");
        Files.walkFileTree(path, new SvnInfoCleanVisitor());
    }

    /**
     * 文件属性视图 示例
      * @throws IOException
     */
    public void useFileAttributeView() throws IOException {
        Path path = Paths.get("content.txt");
        DosFileAttributeView view = Files.getFileAttributeView(path, DosFileAttributeView.class);
        if (view != null) {
            DosFileAttributes attrs = view.readAttributes();
            logger.info(String.valueOf(attrs.isReadOnly()));
        }
    }

    /**
     * 获取文件的上次修改时间
     * @param path
     * @param intervalInMillis
     * @return
     * @throws IOException
     */
    public boolean checkUpdatesRequired(Path path, int intervalInMillis) throws IOException {
        FileTime lastModifiedTime = (FileTime) Files.getAttribute(path, "lastModifiedTime");
        long now = System.currentTimeMillis();
        return now - lastModifiedTime.toMillis() > intervalInMillis;
    }

    /**
     * 目录监视服务 示例
     * @throws IOException
     * @throws InterruptedException
     */
    public void calculate() throws IOException, InterruptedException {
        WatchService service = FileSystems.getDefault().newWatchService();
        Path path = Paths.get("").toAbsolutePath();
        path.register(service, StandardWatchEventKinds.ENTRY_CREATE);

        while (true) {
            WatchKey key = service.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                Path createdPath = (Path)event.context();
                createdPath = path.resolve(createdPath);
                long size = Files.size(createdPath);
                logger.info(createdPath + " ==> " + size);
            }
            key.reset();
        }
    }

}
