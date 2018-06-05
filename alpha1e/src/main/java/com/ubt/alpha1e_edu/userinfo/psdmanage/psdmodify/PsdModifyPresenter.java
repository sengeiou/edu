package com.ubt.alpha1e_edu.userinfo.psdmanage.psdmodify;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e_edu.base.RequstMode.BaseRequest;
import com.ubt.alpha1e_edu.base.RequstMode.ModifyMessagePsdRequest;
import com.ubt.alpha1e_edu.data.model.BaseModel;
import com.ubt.alpha1e_edu.data.model.BaseResponseModel;
import com.ubt.alpha1e_edu.login.HttpEntity;
import com.ubt.alpha1e_edu.mvp.BasePresenterImpl;
import com.ubt.alpha1e_edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e_edu.utils.GsonImpl;
import com.ubt.alpha1e_edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class PsdModifyPresenter extends BasePresenterImpl<PsdModifyContract.View> implements PsdModifyContract.Presenter{

    private static final String TAG = PsdModifyPresenter.class.getSimpleName();

    @Override
    public void doModifyPassword(String oldPassword, String newPassword) {

        ModifyMessagePsdRequest modifyMessagePsdRequest = new ModifyMessagePsdRequest();
        modifyMessagePsdRequest.setOldPassword(oldPassword);
        modifyMessagePsdRequest.setNewPassword(newPassword);
        String url = HttpEntity.MODIFY_MANAGE_PASSWORD;

        doRequestFromWeb(url,modifyMessagePsdRequest,0);
    }

    /**
     * 获取列表数据
     */
    public void doRequestFromWeb(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doModifyFromWeb onError:" + e.getMessage());
                mView.onModifyPassword(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_setting_password_modify_fail"));
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "doModifyFromWeb response==" + HttpEntity.MODIFY_MANAGE_PASSWORD);
                UbtLog.d(TAG, "doModifyFromWeb response==" + response);
                BaseResponseModel<BaseModel> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<BaseModel>>() {
                        }.getType());
                if (baseResponseModel.status) {
                    mView.onModifyPassword(true,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_setting_password_modify_success"));
                }else {
                    mView.onModifyPassword(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_setting_password_modify_fail"));
                }
            }
        });

    }
}
