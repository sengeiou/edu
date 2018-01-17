package com.ubt.alpha1e.userinfo.helpfeedback.feedback;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.base.FileUtils;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.FeedbackRequest;
import com.ubt.alpha1e.data.model.BaseModel;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class FeedbackPresenter extends BasePresenterImpl<FeedbackContract.View> implements FeedbackContract.Presenter{

    private static final String TAG = FeedbackPresenter.class.getSimpleName();

    private static final int DO_ADD_FEEDBACK = 1;

    @Override
    public void doFeedBack(String content, String email, String phone,Map<String,File> fileMap) {

        content = FileUtils.stringToUtf8(content);

        UbtLog.d(TAG,"content = " + content );

        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.setContent(content);
        feedbackRequest.setPhone(phone);
        feedbackRequest.setEmail(email);

        String url = HttpEntity.ADD_FEEDBACK;
        doRequestFromWeb(url,feedbackRequest,fileMap,DO_ADD_FEEDBACK);
    }

    /**
     * 请求网络操作
     */
    public void doRequestFromWeb(String url, BaseRequest baseRequest,Map<String,File> fileMap, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, fileMap, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doRequestFromWeb onError:" + e.getMessage());
                switch (id){
                    case DO_ADD_FEEDBACK:
                        mView.onFeedbackFinish(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_common_network_request_failed"));
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"response = " + response);
                BaseResponseModel<BaseModel> baseResponseModel = GsonImpl.get().toObject(response,new TypeToken<BaseResponseModel<BaseModel>>() {}.getType());

                switch (id){
                    case DO_ADD_FEEDBACK:
                    {
                        if (baseResponseModel.status) {
                            mView.onFeedbackFinish(true,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_about_feedback_success"));
                        }else {
                            mView.onFeedbackFinish(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_register_prompt_system_error"));
                        }
                    }
                    break;
                }

            }
        });

    }
}
