package com.ubt.alpha1e.edu.userinfo.helpfeedback.feedbacksearch;

import android.text.TextUtils;

import com.ubt.alpha1e.edu.data.model.FeedbackInfo;
import com.ubt.alpha1e.edu.mvp.BasePresenterImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class FeedbackSearchPresenter extends BasePresenterImpl<FeedbackSearchContract.View> implements FeedbackSearchContract.Presenter{

    @Override
    public void doSearchResult(List<FeedbackInfo> feedbackInfos, String editStr) {
        List<FeedbackInfo> result = new ArrayList<>();
        for(FeedbackInfo feedbackInfo : feedbackInfos){
            if(!TextUtils.isEmpty(feedbackInfo.feedbackName) && feedbackInfo.feedbackName.contains(editStr)){
                result.add(feedbackInfo);
            }
        }
        mView.onSearchResult(result);
    }

}
