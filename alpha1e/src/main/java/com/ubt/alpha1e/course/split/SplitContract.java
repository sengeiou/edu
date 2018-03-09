package com.ubt.alpha1e.course.split;

import android.content.Context;

import com.ubt.alpha1e.course.principle.PrincipleContract;
import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class SplitContract {
    interface View extends BaseView {
        void onSaveCourseProgress(boolean isSuccess,String msg);

        void onGetCourseProgress(boolean isSuccess,String msg,int progress);
    }

    interface  Presenter extends BasePresenter<SplitContract.View> {
        void doSaveCourseProgress(int type, int courseOne, int progressOne);

        void doGetCourseProgress(int type);

        int doGetLocalProgress();
    }
}
