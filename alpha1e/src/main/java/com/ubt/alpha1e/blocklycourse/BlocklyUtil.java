package com.ubt.alpha1e.blocklycourse;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class BlocklyUtil {

    private static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String NAME = "BlocklyUtil";
    public static final String VIDEO_CACHE = "videoCache";

    public static void saveBitmap(Bitmap bitmap, String name) throws FileNotFoundException {
        if (bitmap != null) {
            File file = new File(getPath(), name + ".jpg");
            OutputStream outputStream;
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            bitmap.recycle();
        }
    }

    public static String getPath() {
        String path = getAppPath(NAME);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getAppPath(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(SD_PATH);
        sb.append(File.separator);
        sb.append(name);
        sb.append(File.separator);
        return sb.toString();
    }


    public static String getVideoPath() {
        String path = getAppPath(VIDEO_CACHE);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }


}
