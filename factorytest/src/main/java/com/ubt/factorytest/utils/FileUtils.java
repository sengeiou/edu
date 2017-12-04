package com.ubt.factorytest.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/22 13:54
 * @描述:
 */

public class FileUtils {
    private static final String TAG = "FileUtils";


    /**
     * 获取程序外部的缓存目录
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context) {
        return context.getExternalFilesDir("factoryTest");
    }


    /**
     * 获取可以使用的缓存目录
     * @param context
     * @return
     */
    public static File getDiskCacheDir(Context context) {
        final String cachePath = getExternalCacheDir(context).getPath();

        File cacheDirFile = new File(cachePath);
        if (!cacheDirFile.exists()) {
            cacheDirFile.mkdirs();
        }

        return cacheDirFile;
    }

    public static boolean writeStringToFile(String content,
                                            String directoryPath, String fileName, boolean isAppend) {
        if (!TextUtils.isEmpty(content)) {
            if (!TextUtils.isEmpty(directoryPath)) {// 是否需要创建新的目录
                final File threadListFile = new File(directoryPath);
                if (!threadListFile.exists()) {
                    threadListFile.mkdirs();
                }
            }
            boolean bFlag = false;
            final int iLen = content.length();
            final File file = new File(directoryPath+"/"+fileName);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                final FileOutputStream fos = new FileOutputStream(file,
                        isAppend);
                byte[] buffer = new byte[iLen];
                try {
                    buffer = content.getBytes();
                    fos.write(buffer);
                    if (isAppend) {
                        fos.write("||".getBytes());
                    }
                    fos.flush();
                    bFlag = true;
                } catch (IOException ioex) {
                     ioex.printStackTrace();
                } finally {
                    fos.close();
                    buffer = null;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } catch (OutOfMemoryError o) {
                    o.printStackTrace();
            }
            return bFlag;
        }
        return false;
    }
}
