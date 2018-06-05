package com.ubt.alpha1e_edu.utils;

/**
 * 防止二次点击
 */

public class AviodTwoClicksUtils {
    // 两次点击按钮之间的点击间隔不能少于500毫秒
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime = 0;

    /**
     * @return
     * true : 是快速点击
     * false ：不是快速点击
     */
    public static boolean isFastClick() {
        boolean flag = true;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = false;
            lastClickTime = curClickTime;
            return  flag;
        }
        return flag;
    }
}
