package com.ubt.alpha1e.edu.blocklycourse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
            bitmap.recycle();
        }
    }


    /**
     * 图片质量压缩
     * @param image
     * @param srcPath 要保存的路径
     * @return
     */
    public static Bitmap compressImage(Bitmap image, String srcPath) {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {    // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            options -= 10;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        try {
            FileOutputStream out = new FileOutputStream(srcPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
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


    public static String getVideoDir(Context context){
        String cacheDir = Environment.getExternalStorageDirectory().getPath()
                + File.separator + "Android"
                + File.separator + "data"
                + File.separator + context.getPackageName()
                + File.separator + "files" + File.separator
                + "data/videoCache";

        return cacheDir;
    }


}
