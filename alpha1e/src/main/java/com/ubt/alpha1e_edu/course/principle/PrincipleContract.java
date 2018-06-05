package com.ubt.alpha1e_edu.course.principle;

import com.ubt.alpha1e_edu.mvp.BasePresenter;
import com.ubt.alpha1e_edu.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class PrincipleContract {
    interface View extends BaseView {
        void onSaveCourseProgress(boolean isSuccess,String msg);

        void onGetCourseProgress(boolean isSuccess,String msg,int progress);
    }

    interface  Presenter extends BasePresenter<PrincipleContract.View> {
        void doSaveCourseProgress(int type, int courseOne, int progressOne);

        void doGetCourseProgress(int type);

        int doGetLocalProgress();
    }
}
