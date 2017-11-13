package com.ubt.alpha1e.userinfo.helpfeedback.feelback;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class FeelbackContract {
    interface View extends BaseView {
        void onFeedbackFinish(boolean isSuccess, String errorMsg);
    }

    interface  Presenter extends BasePresenter<View> {
        void doFeedBack(String content,String email,String phone);

    }
}
