package com.ubt.alpha1e.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.ubt.alpha1e.action.actioncreate.ActionTestActivity;
import com.ubt.alpha1e.course.feature.FeatureActivity;
import com.ubt.alpha1e.course.merge.MergeActivity;
import com.ubt.alpha1e.course.principle.PrincipleActivity;
import com.ubt.alpha1e.course.split.SplitActivity;
import com.ubt.alpha1e.maincourse.actioncourse.ActionCourseActivity;
import com.ubt.alpha1e.maincourse.courseone.CourseLevelOneActivity;
import com.ubt.alpha1e.maincourse.main.MainCourseActivity;
import com.ubt.alpha1e.ui.RemoteActivity;
import com.ubt.alpha1e.ui.RemoteSelActivity;

import java.util.Stack;

/**
 * @author：liuhai
 * @date：2017/10/25 20:14
 * @modifier：ubt
 * @modify_date：2017/10/25 20:14
 * Activity管理类
 * version
 */

public class AppManager {
    private static Stack<Activity> activityStack;

    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getInstance() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束正在使用的蓝牙的页面
     */
    public void finishUseBluetoothActivity() {
        Activity mActivity = null;
        for (int i = 0; i < activityStack.size(); i++) {
            try {
                mActivity = activityStack.get(i);
                if (mActivity instanceof RemoteActivity
                        || mActivity instanceof RemoteSelActivity
                        || mActivity instanceof MainCourseActivity
                        || mActivity instanceof PrincipleActivity
                        || mActivity instanceof SplitActivity
                        || mActivity instanceof MergeActivity
                        || mActivity instanceof FeatureActivity
                        ||mActivity instanceof ActionTestActivity
                        ||mActivity instanceof ActionCourseActivity
                        ||mActivity instanceof CourseLevelOneActivity
                        ) {
                    mActivity.finish();
                }
                continue;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = activityStack.size()-1; i >= 0; i--) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }



    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
