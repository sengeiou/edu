package com.ubt.alpha1e.userinfo.helpfeedback.feedbacksearch;

import android.content.Context;

import com.ubt.alpha1e.data.model.FeedbackInfo;
import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

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
