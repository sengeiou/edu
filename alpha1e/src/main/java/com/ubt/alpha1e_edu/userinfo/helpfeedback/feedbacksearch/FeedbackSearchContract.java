package com.ubt.alpha1e_edu.userinfo.helpfeedback.feedbacksearch;

import com.ubt.alpha1e_edu.data.model.FeedbackInfo;
import com.ubt.alpha1e_edu.mvp.BasePresenter;
import com.ubt.alpha1e_edu.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class FeedbackSearchContract {
    interface View extends BaseView {
        void onSearchResult(List<FeedbackInfo> feedbackInfoList);
    }

    interface  Presenter extends BasePresenter<View> {
        void doSearchResult(List<FeedbackInfo> feedbackInfos, String editStr);
    }
}
