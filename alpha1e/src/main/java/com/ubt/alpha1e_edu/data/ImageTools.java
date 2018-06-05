package com.ubt.alpha1e_edu.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.ubt.alpha1e_edu.data.FileTools.State;
import com.ubt.alpha1e_edu.net.http.basic.IImageListener;
import com.ubt.alpha1e_edu.utils.log.MyLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import Decoder.BASE64Encoder;

public class ImageTools {

    public static String getImgStr(Bitmap img) {
        if (img == null)
            return "";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] img_bytes = baos.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(img_bytes).replace("\n", "");
    }

    public static Bitmap compressImage(Resources res, int resId,
                                       float newHeight, float newWidth) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        int rate = 1;
        int old_h = options.outHeight;
        int old_w = options.outWidth;
        int rate_tmp = (int) (old_h / newHeight);
        if (rate_tmp > rate)
            rate = rate_tmp;
        rate_tmp = (int) (old_w / newWidth);
        if (rate_tmp > rate)
            rate = rate_tmp;
        options.inSampleSize = rate;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);

    }

    /**
     * 缩放倍数
     *
     * @param res
     * @param resId
     * @param rate
     * @return
     */
    public static Bitmap compressImage(Resources res, int resId, int rate) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = rate;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);

    }

    private static Bitmap compressImage(Bitmap image, int KB,
                                        boolean isCleanCache) {

        if (image == null)
            return null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        MyLog.writeLog("图片性能提升", "压缩开始：image-->" + image.getByteCount() / 1024
                + ",baos" + "-->" + baos.toByteArray().length / 1024);

        if (isCleanCache)
            image.recycle();

        int rate = 100;
        if (baos.toByteArray().length / 1024 > KB) {
            rate = 100 * KB / (baos.toByteArray().length / 1024);
        }
        if (rate < 1) {
            rate = 1;
        }
        MyLog.writeLog("图片性能提升", "rate-->" + rate);
        Bitmap new_image = BitmapFactory.decodeStream(new ByteArrayInputStream(
                baos.toByteArray()), null, null);
        baos.reset();
        new_image.compress(Bitmap.CompressFormat.JPEG, rate, baos);

        new_image.recycle();

        Bitmap result = BitmapFactory.decodeStream(new ByteArrayInputStream(
                baos.toByteArray()), null, null);
        int resute_kb = baos.toByteArray().length / 1024;
        MyLog.writeLog("图片性能提升", "压缩结束：image-->" + result.getByteCount() / 1024
                + ",baos" + "-->" + baos.toByteArray().length / 1024);
        baos.reset();

        return result;

    }

    public static Bitmap compressImage(Bitmap image, int KB) {

        return compressImage(image, KB, true);

    }

    public synchronized static Bitmap compressImage(File img_file,
                                                    final double newWidth, final double newHeight, boolean is_fill_max) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(img_file.getAbsolutePath(), newOpts);

        newOpts.inJustDecodeBounds = false;

        int rate = 1;
        int rate_w = 1;
        int rate_h = 1;
        if (newWidth > 0 && newHeight > 0) {
            rate_w = (int) (newOpts.outWidth / newWidth);
            rate_h = (int) (newOpts.outHeight / newHeight);
        }

        int rate_finily;
        // 如果质量优先
        if (is_fill_max) {
            rate_finily = rate_h > rate_w ? rate_w : rate_h;
        } else {
            rate_finily = rate_h > rate_w ? rate_h : rate_w;
        }

        if (rate_finily > rate)
            rate = rate_finily;

        newOpts.inSampleSize = rate;// 设置采样率

        newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 当系统内存不够时候图片自动被回收

        try {
            bitmap = BitmapFactory.decodeFile(img_file.getAbsolutePath(),
                    newOpts);
        } catch (Throwable t) {
            bitmap = null;
        }

        return bitmap;
    }

    public static Bitmap compressImage(InputStream in, final double newWidth,
                                       final double newHeight) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeStream(in, null, newOpts);
        newOpts.inJustDecodeBounds = false;

        int rate = 1;
        int rate_tmp = (int) (newOpts.outWidth / newWidth);
        if (rate_tmp > rate)
            rate = rate_tmp;
        rate_tmp = (int) (newOpts.outHeight / newHeight);
        if (rate_tmp > rate)
            rate = rate_tmp;

        newOpts.inSampleSize = rate;// 设置采样率

        newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

        if (1 == 0) {
            bitmap = BitmapFactory.decodeStream(in, null, newOpts);
        } else {
            try {
                byte[] data = getBytes(in);
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                        newOpts);
            } catch (IOException e) {
                bitmap = null;
            }
        }

        return bitmap;
    }

    public static Bitmap compressImageSync(InputStream in,
                                           final double newWidth, final double newHeight, String key) {

        boolean isSuccessWrite = FileTools.writeDateToSDCardSync(in, key);
        if (isSuccessWrite) {
            return compressImage(new File(FileTools.image_cache, key),
                    newWidth, newHeight, false);
        } else {
            return null;
        }

    }

    public static void compressImage(InputStream in, final double newWidth,
                                     final double newHeight, final IImageListener listener,
                                     final boolean is_fill_max) {

        // 先将图片缓存到本地
        FileTools.writeDateToSDCard(in, new IFileListener() {

            @Override
            public void onWriteDataFinish(long requestCode, State state) {
                // TODO Auto-generated method stub
                if (state == State.Success) {

                    File file = new File(FileTools.image_cache, requestCode
                            + "");

                    Bitmap bitmap = compressImage(file, newWidth, newHeight,
                            is_fill_max);

                    listener.onGetImage(true, bitmap, requestCode);

                } else {
                    listener.onGetImage(false, null, requestCode);
                }
            }

            @Override
            public void onReadImageFinish(Bitmap img, long request_code) {

            }

            @Override
            public void onReadCacheSize(int size) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onClearCache() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onReadFileStrFinish(String erroe_str, String result,
                                            boolean result_state, long request_code) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onWriteFileStrFinish(String erroe_str, boolean result,
                                             long request_code) {
                // TODO Auto-generated method stub

            }
        });

    }


    public static String SaveImage(String imageName, Bitmap bitmap) {
        File appDir = new File(FileTools.image_cache);
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

    public static Bitmap compressImage(Bitmap bgimage, double newWidth,
                                       double newHeight, boolean isFillImg) {

        MyLog.writeLog("图片性能提升", "compressImage:" + bgimage.getWidth() + ","
                + bgimage.getHeight() + "-->" + newWidth + "," + newHeight);
        if (bgimage == null || bgimage.isRecycled())
            return null;
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        if (width == newWidth && height == newHeight)
            return bgimage;
        else if ((width < newWidth || height < newHeight) && newHeight > 400) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int rate_h = (int) ((height * 100) / newHeight);
            int rate_w = (int) ((width * 100) / newWidth);

            int rate_finaly = 0;

            if (isFillImg) {
                rate_finaly = rate_h > rate_w ? rate_h : rate_w;
            } else {
                rate_finaly = rate_h < rate_w ? rate_h : rate_w;
            }

            if (rate_finaly < 1) {
                rate_finaly = 1;
            }

            bgimage.compress(Bitmap.CompressFormat.JPEG, rate_finaly, baos);
            bgimage.recycle();
            bgimage = BitmapFactory.decodeStream(
                    new ByteArrayInputStream(baos.toByteArray()), null, null);
            baos.reset();
        }
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap bitmap = null;
        try {

            bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                    (int) height, matrix, true);
        } catch (Throwable t) {
            bitmap = null;
        }

        if (!bgimage.isRecycled())
            bgimage.recycle();
        return bitmap;

    }

    // 剪裁函数
    public static Bitmap ImageCrop(Bitmap bitmap) {
        // 得到图片的宽，高
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        // 裁切后所取的正方形区域边长
        int r = w > h ? h : w;
        // 原图的几何中心点
        int _x = w / 2;
        int _y = h / 2;
        // 基于原图，取正方形上边沿中点
        int x = _x - (r / 2);
        int y = _y - (r / 2);

        Bitmap new_bitmap = null;

        try {
            new_bitmap = Bitmap.createBitmap(bitmap, x, y, r, r, null, false);
            if (!bitmap.isRecycled() && bitmap != new_bitmap)
                bitmap.recycle();
        } catch (Exception e) {
            new_bitmap = null;
        }
        return new_bitmap;
    }

    private static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            outstream.write(buffer, 0, len);
        }
        outstream.close();
        return outstream.toByteArray();
    }

    public synchronized static Bitmap toRoundCorner(Bitmap bitmap, int power) {

        Bitmap output = null;

        output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Config.ARGB_8888);

        int lenth = bitmap.getHeight() > bitmap.getWidth() ? bitmap.getWidth()
                : bitmap.getHeight();
        int pixels = (lenth * power) / 100;
        Canvas canvas = new Canvas(output);
        final int color = 0xffffffff;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        try {
            bitmap.recycle();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;

    }

}
