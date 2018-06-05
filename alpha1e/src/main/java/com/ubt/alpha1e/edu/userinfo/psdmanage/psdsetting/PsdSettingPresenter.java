package com.ubt.alpha1e.edu.userinfo.psdmanage.psdsetting;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.edu.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.edu.base.RequstMode.SetUserPasswordRequest;
import com.ubt.alpha1e.edu.data.model.BaseModel;
import com.ubt.alpha1e.edu.data.model.BaseResponseModel;
import com.ubt.alpha1e.edu.login.HttpEntity;
import com.ubt.alpha1e.edu.mvp.BasePresenterImpl;
import com.ubt.alpha1e.edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e.edu.utils.GsonImpl;
import com.ubt.alpha1e.edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class PsdSettingPresenter extends BasePresenterImpl<PsdSettingContract.View> implements PsdSettingContract.Presenter{

    private static final String TAG = PsdSettingPresenter.class.getSimpleName();

    private static final int SET_USER_PASSWORD = 1;

    @Override
    public void doSetUserPassword(String password) {

        SetUserPasswordRequest setUserPasswordRequest = new SetUserPasswordRequest();
        setUserPasswordRequest.setPassword(password);

        String url = HttpEntity.SET_USER_PASSWORD;
        doRequestFromWeb(url,setUserPasswordRequest,SET_USER_PASSWORD);

    }

    /**
     * 请求网络操作
     */
    public void doRequestFromWeb(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doRequestFromWeb onError:" + e.getMessage());
                switch (id){
                    case SET_USER_PASSWORD:
                        mView.onSetUserPassword(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_setting_password_modify_fail"));
                        break;
                }

            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"response = " + response);
                BaseResponseModel<BaseModel> baseResponseModel = GsonImpl.get().toObject(response,new TypeToken<BaseResponseModel<BaseModel>>() {}.getType());

                switch (id){
                    case SET_USER_PASSWORD:
                    {
                        if (baseResponseModel.status) {
                            mView.onSetUserPassword(true,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_setting_password_modify_success"));
                        }else {
                            mView.onSetUserPassword(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_setting_password_modify_fail"));
                        }
                    }
                    break;

                }

            }
        });

    }
}
