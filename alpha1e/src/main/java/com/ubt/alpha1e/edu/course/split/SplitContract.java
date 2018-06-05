package com.ubt.alpha1e.edu.course.split;

import com.ubt.alpha1e.edu.mvp.BasePresenter;
import com.ubt.alpha1e.edu.mvp.BaseView;

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
