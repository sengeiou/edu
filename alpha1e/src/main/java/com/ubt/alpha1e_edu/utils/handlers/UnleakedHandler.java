package com.ubt.alpha1e_edu.utils.handlers;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/5/18.
 */
public class UnleakedHandler {

    public  static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null) {

            }
        }
    }

    public static  Handler getHandlerInstance(Activity activity)
    {
        return new MyHandler(activity);
    }
}
