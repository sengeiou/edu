package com.ubt.alpha1e.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.ubt.alpha1e.utils.log.UbtLog;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2016/6/17.
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";

    /**
     * 根据一个网络连接(String)获取bitmap图像
     *
     * @param imageUri
     * @return Bitmap
     */
    public static Bitmap loadBitmap(String imageUri, Context mContext) {

        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(mContext).load(imageUri).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
            UbtLog.d(TAG,"bitmap = " + bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
