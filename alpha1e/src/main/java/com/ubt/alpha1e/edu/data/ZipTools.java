package com.ubt.alpha1e.edu.data;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipOutputStream;

public class ZipTools {

    public static final String MSG_SUCCESS = "MSG_SUCCESS";
    public static final String MSG_FAIL = "MSG_FAIL";
    public static final String MSG_NO_FILE = "MSG_NO_FILE";
    private static final int buffer = 2048;

    public synchronized static String unZip(String srcFile, String destFilePath) {
        String message = MSG_SUCCESS;
        File f = new File(srcFile);
        if (!f.exists()) {
            message = MSG_NO_FILE;
        } else {

            int count = -1;
            String savepath = "";
            File file = null;
            InputStream is = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            savepath = destFilePath + File.separator;
            new File(savepath).mkdir(); // 创建保存目录
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(srcFile, "gbk"); // 解决中文乱码问题
                Enumeration<?> entries = zipFile.getEntries();
                while (entries.hasMoreElements()) {
                    byte buf[] = new byte[buffer];
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    String filename = entry.getName();
                    boolean ismkdir = false;
                    if (filename.lastIndexOf("/") != -1) { // 检查此文件是否带有文件夹
                        ismkdir = true;
                    }
                    filename = savepath + filename;
                    if (entry.isDirectory()) { // 如果是文件夹先创建
                        file = new File(filename);
                        file.mkdirs();
                        continue;
                    }
                    file = new File(filename);
                    if (!file.exists()) { // 如果是目录先创建
                        if (ismkdir) {
                            new File(filename.substring(0,
                                    filename.lastIndexOf("/"))).mkdirs(); // 目录先创建
                        }
                    }
                    file.createNewFile(); // 创建文件
                    is = zipFile.getInputStream(entry);
                    fos = new FileOutputStream(file);
                    bos = new BufferedOutputStream(fos, buffer);
                    while ((count = is.read(buf)) > -1) {
                        bos.write(buf, 0, count);
                    }
                    bos.flush();
                    bos.close();
                    fos.close();
                    is.close();
                }
                zipFile.close();

            } catch (IOException e) {
                message = MSG_FAIL;
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                    if (zipFile != null) {
                        zipFile.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return message;
    }

    /**
     * 递归压缩文件
     *
     * @param source   源路径,可以是文件,也可以目录
     * @param destinct 目标路径,压缩文件名
     * @throws IOException
     */
    public static void doZip(String source, String destinct)
            throws IOException {
        List fileList = loadFilename(new File(source));
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
                new File(destinct)));

        byte[] buffere = new byte[8192];
        int length;
        BufferedInputStream bis;

        for (int i = 0; i < fileList.size(); i++) {
            File file = (File) fileList.get(i);
            zos.putNextEntry(new ZipEntry(getEntryName(source, file)));
            bis = new BufferedInputStream(new FileInputStream(file));

            while (true) {
                length = bis.read(buffere);
                if (length == -1)
                    break;
                zos.write(buffere, 0, length);
            }
            bis.close();
            zos.closeEntry();
        }
        zos.close();
    }

    /**
     * 递归获得该文件下所有文件名(不包括目录名)
     *
     * @param file
     * @return
     */
    private static List loadFilename(File file) {
        List filenameList = new ArrayList();
        if (file.isFile()) {
            filenameList.add(file);
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                filenameList.addAll(loadFilename(f));
            }
        }
        return filenameList;
    }

    /**
     * 获得zip entry 字符串
     *
     * @param base
     * @param file
     * @return
     */
    private static String getEntryName(String base, File file) {
        File baseFile = new File(base);
        String filename = file.getPath();

        if (1 == 0) {
            if (baseFile.getParentFile().getParentFile() == null)
                return filename.substring(baseFile.getParent().length());
            return filename.substring(baseFile.getParent().length() + 1);
        } else {
            return filename.substring(baseFile.getAbsolutePath().length() + 1);
        }
    }

}
