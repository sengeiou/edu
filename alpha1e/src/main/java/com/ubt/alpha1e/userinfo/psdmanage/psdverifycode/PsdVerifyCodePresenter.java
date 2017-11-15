package com.ubt.alpha1e.userinfo.psdmanage.psdverifycode;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.GetCodeRequest;
import com.ubt.alpha1e.base.RequstMode.ModifyMessagePsdRequest;
import com.ubt.alpha1e.base.RequstMode.VerifyCodeRequest;
import com.ubt.alpha1e.data.model.BaseModel;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.userinfo.psdmanage.PsdManagePresenter;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class PsdVerifyCodePresenter extends BasePresenterImpl<PsdVerifyCodeContract.View> implements PsdVerifyCodeContract.Presenter{

    private static final String TAG = PsdVerifyCodePresenter.class.getSimpleName();

    private static final int GET_VERIFY_CODE = 1;
    private static final int DO_VERIFY_CODE = 2;

    @Override
    public void doGetVerifyCode(String telephone) {

        GetCodeRequest getCodeRequest = new GetCodeRequest();
        getCodeRequest.setPhone(telephone);

        String url = HttpEntity.REQUEST_SMS_CODE;
        doRequestFromWeb(url,getCodeRequest,GET_VERIFY_CODE);
    }

    @Override
    public void doVerifyCode(String telephone,String verifyCode) {

        VerifyCodeRequest verifyCodeRequest = new VerifyCodeRequest();
        verifyCodeRequest.setPhone(telephone);
        verifyCodeRequest.setCode(verifyCode);

        String url = HttpEntity.VERIDATA_CODE;
        doRequestFromWeb(url,verifyCodeRequest, DO_VERIFY_CODE);
    }

    /**
     * 请求网络操作
     */
    public void doRequestFromWeb(String url,BaseRequest baseRequest,int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doModifyFromWeb onError:" + e.getMessage());
                switch (id){
                    case GET_VERIFY_CODE:
                        mView.onGetVerifyCode(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_setting_password_modify_fail"));
                        break;
                    case DO_VERIFY_CODE:
                        mView.onVerifyCode(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_setting_password_modify_fail"));
                        break;
                }

            }

            @Override
            public void onResponse(String response, int id) {

                BaseResponseModel<BaseModel> baseResponseModel = GsonImpl.get().toObject(response,new TypeToken<BaseResponseModel<BaseModel>>() {}.getType());

                switch (id){
                    case GET_VERIFY_CODE:
                        {
                            if (baseResponseModel.status) {
                                mView.onGetVerifyCode(true,"");
                            }else {
                                mView.onGetVerifyCode(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_setting_password_modify_fail"));
                            }
                        }
                        break;
                    case DO_VERIFY_CODE:
                        {
                            if (baseResponseModel.status) {
                                mView.onVerifyCode(true,"");
                            }else {
                                mView.onVerifyCode(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_setting_password_modify_fail"));
                            }
                        }
                        break;
                }

            }
        });

    }
}
