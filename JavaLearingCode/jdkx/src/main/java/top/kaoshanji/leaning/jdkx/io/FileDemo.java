package top.kaoshanji.leaning.jdkx.io;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * File 示例
 * @author kaoshanji
 * @time 2020-01-11 17:16
 */
public class FileDemo {


    /**
     * 向已有的 zip 文件中添加新文件的传统做法
     * @param zipFile
     * @param fileToAdd
     * @throws IOException
     */
    public void addFileToZip(File zipFile, File fileToAdd) throws IOException {
        File tempFile = File.createTempFile(zipFile.getName(), null);
        tempFile.delete();

        zipFile.renameTo(tempFile);

        ZipInputStream input = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream output = new ZipOutputStream(new FileOutputStream(zipFile));

        ZipEntry entry = input.getNextEntry();
        byte[] buf = new byte[8192];

        while (entry != null) {
            String name = entry.getName();
            if (!name.equals(fileToAdd.getName())) {
                output.putNextEntry(new ZipEntry(name));
                int len = 0;
                while ((len = input.read(buf)) > 0) {
                    output.write(buf, 0, len);
                }
            }
            entry = input.getNextEntry();
        }

        InputStream newFileInput = new FileInputStream(fileToAdd);
        output.putNextEntry(new ZipEntry(fileToAdd.getName()));
        int lenn = 0;
        while ((lenn = newFileInput.read(buf)) > 0) {
            output.write(buf, 0, lenn);
        }
        output.closeEntry();

        tempFile.delete();
    }




}
