package com.ubt.alpha1e.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.ubt.alpha1e.utils.log.UbtLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author：liuhai
 * @date：2017/11/14 10:36
 * @modifier：ubt
 * @modify_date：2017/11/14 10:36
 * [A brief description]
 * version
 */

public class FileUtils {
    /**
     * 获取应用专属缓存目录
     * android 4.4及以上系统不需要申请SD卡读写权限
     * 因此也不用考虑6.0系统动态申请SD卡读写权限问题，切随应用被卸载后自动清空 不会污染用户存储空间
     *
     * @param context 上下文
     * @param type    文件夹类型 可以为空，为空则返回API得到的一级目录
     * @return 缓存文件夹 如果没有SD卡或SD卡有问题则返回内存缓存目录，否则优先返回SD卡缓存目录
     */
    public static String getCacheDirectory(Context context, String type) {
        File appCacheDir = getExternalCacheDirectory(context, type);
        if (appCacheDir == null) {
            appCacheDir = getInternalCacheDirectory(context, type);
        }

        if (appCacheDir == null) {
            UbtLog.e("FileUtils", "getCacheDirectory fail ,the reason is mobile phone unknown exception !");
        } else {
            if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
                UbtLog.e("FileUtils", "getCacheDirectory fail ,the reason is make directory fail !");
            }
        }
        UbtLog.d("FileUtil", "appCacheDir===" + appCacheDir.getPath() + File.separator);
        return appCacheDir.getPath() + File.separator;
    }

    /**
     * 获取SD卡缓存目录
     *
     * @param context 上下文
     * @param type    文件夹类型 如果为空则返回 /storage/emulated/0/Android/data/app_package_name/cache
     *                否则返回对应类型的文件夹如Environment.DIRECTORY_PICTURES 对应的文件夹为 .../data/app_package_name/files/Pictures
     *                {@link android.os.Environment#DIRECTORY_MUSIC},
     *                {@link android.os.Environment#DIRECTORY_PODCASTS},
     *                {@link android.os.Environment#DIRECTORY_RINGTONES},
     *                {@link android.os.Environment#DIRECTORY_ALARMS},
     *                {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     *                {@link android.os.Environment#DIRECTORY_PICTURES}, or
     *                {@link android.os.Environment#DIRECTORY_MOVIES}.or 自定义文件夹名称
     * @return 缓存目录文件夹 或 null（无SD卡或SD卡挂载失败）
     */
    public static File getExternalCacheDirectory(Context context, String type) {
        File appCacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (TextUtils.isEmpty(type)) {
                appCacheDir = context.getExternalCacheDir();
            } else {
                appCacheDir = context.getExternalFilesDir(type);
            }

            if (appCacheDir == null) {// 有些手机需要通过自定义目录
                appCacheDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/cache/" + type);
            }

            if (appCacheDir == null) {
                UbtLog.e("FileUtils", "getExternalDirectory fail ,the reason is sdCard unknown exception !");
            } else {
                if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
                    UbtLog.e("FileUtils", "getExternalDirectory fail ,the reason is make directory fail !");
                }
            }
        } else {
            UbtLog.e("FileUtils", "getExternalDirectory fail ,the reason is sdCard nonexistence or sdCard mount fail !");
        }
        return appCacheDir;
    }

    /**
     * 获取内存缓存目录
     *
     * @param type 子目录，可以为空，为空直接返回一级目录
     * @return 缓存目录文件夹 或 null（创建目录文件失败）
     * 注：该方法获取的目录是能供当前应用自己使用，外部应用没有读写权限，如 系统相机应用
     */
    public static File getInternalCacheDirectory(Context context, String type) {
        File appCacheDir = null;
        if (TextUtils.isEmpty(type)) {
            appCacheDir = context.getCacheDir();// /data/data/app_package_name/cache
        } else {
            appCacheDir = new File(context.getFilesDir(), type);// /data/data/app_package_name/files/type
        }

        if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
            UbtLog.e("FileUtils", "getInternalDirectory fail ,the reason is make directory fail !");
        }
        return appCacheDir;
    }


    /**
     * 删除视频文件
     *
     * @param context
     * @param fileName 视频文件名称
     */
    public static void clearCacheFile(Context context, String fileName) {
        String filepath = getCacheDirectory(context, "");
        File file = new File(filepath + fileName);
        if (file.exists()) {
            file.delete();
        }

    }


    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     */
    public static Bitmap getBitmapFormUri(Context context, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }


    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    /**
     * 将Bitmap保存至缓存路径
     *
     * @param context
     * @param imageName
     * @param bitmap
     * @return
     */
    public static String SaveImage(Context context, String imageName, Bitmap bitmap) {
        File appDir = new File((getCacheDirectory(context, "") + "/images"));
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = imageName + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串换成UTF-8
     *
     * @param str
     * @return
     */
    public static String stringToUtf8(String str) {
        String result = null;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * utf-8换成字符串
     *
     * @param str
     * @return
     */
    public static String utf8ToString(String str) {
        String result = null;
        try {
            result = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
