package com.ubt.alpha1e.edu.userinfo.helpfeedback.feedback;

import com.ubt.alpha1e.edu.mvp.BasePresenter;
import com.ubt.alpha1e.edu.mvp.BaseView;

import java.io.File;
import java.util.Map;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class FeedbackContract {
    interface View extends BaseView {
        void onFeedbackFinish(boolean isSuccess, String errorMsg);
    }

    interface  Presenter extends BasePresenter<View> {
        void doFeedBack(String content, String email, String phone, Map<String,File> fileMap);

    }
}
