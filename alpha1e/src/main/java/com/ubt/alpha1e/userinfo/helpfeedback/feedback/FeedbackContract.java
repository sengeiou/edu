package com.ubt.alpha1e.userinfo.helpfeedback.feedback;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class FeedbackContract {
    interface View extends BaseView {
        void onFeedbackFinish(boolean isSuccess, String errorMsg);
    }

    interface  Presenter extends BasePresenter<View> {
        void doFeedBack(String content,String email,String phone);

    }
}
