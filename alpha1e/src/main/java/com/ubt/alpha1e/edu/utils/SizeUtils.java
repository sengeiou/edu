package com.ubt.alpha1e.edu.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.ubt.alpha1e.edu.utils.log.UbtLog;

/**
 * Created by yanjunhui
 * on 2016/9/29.
 * email:303767416@qq.com
 */
public class SizeUtils {

    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    /**
     * 判断是否全面屏
     * @param context
     * @return
     */
    public static boolean isComprehensiveScreen(Context context){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        float ratio = (1.0f * metrics.widthPixels) / metrics.heightPixels;

        UbtLog.d("SizeUtils","width/height = " + metrics.widthPixels + "/" + metrics.heightPixels + "    ratio = " + ratio);
        if(ratio > 1.8){
            return true;
        }
        return false;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dip2px(float dpValue,int scale) {
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 通过视图的宽，动态计算高度
     * @param width 视图的实际宽
     * @param height 视图的高，dp
     * @return
     */
    public static int getViewHeight(int width,int height){
        //int height_ = 0;
        //已手机分辨为准，处理平板高度，手机分辨率标准为：高=147dp,scale=3,屏宽=1080
        int height_ = (int)((width * height * 3.0f)/1080);
        return height_;
    }

    /**
     * 获取屏幕密度
     * @param context
     * @return
     */
    public static float getDensity(Context context){
        return context.getResources().getDisplayMetrics().density;
    }
}
