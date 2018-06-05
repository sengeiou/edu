package com.ubt.alpha1e.edu.services;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * @author：liuhai
 * @date：2018/3/12 16:12
 * @modifier：ubt
 * @modify_date：2018/3/12 16:12
 * [A brief description]
 * version
 */

public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;
    private static boolean isForeground = false;//应用是否处于前端

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        isForeground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        isForeground = false;
        Log.d("test", "application is in foreground: resumed== " + resumed + " pause===" + paused + (resumed > paused));
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        Log.d("test", "application is visible: " + (started > stopped));
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        // 当所有 Activity 的状态中处于 resumed 的大于 paused 状态的，即可认为有Activity处于前台状态中
        return resumed > paused;
    }

    /**
     * 是否处于前台状态
     * @return
     */
    public static boolean isIsForeground() {
        return isForeground;
    }
}
