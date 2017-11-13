package com.ubt.alpha1e.ui.main;

import android.text.TextUtils;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.XGGetAccessIdRequest;
import com.ubt.alpha1e.base.ResponseMode.XGBaseModule;
import com.ubt.alpha1e.base.ResponseMode.XGDeviceMode;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.xingepush.XGUBTManager;
import com.ubtechinc.base.ConstValue;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.MediaType;

import static android.R.attr.id;


public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {
    private String TAG = "MainPresenter";

    @Override
    public void requestCartoonAction(String json) {
        mView.showCartoonAction("text");
    }

    @Override
    public void requestCartoonText(String text) {

    }

    @Override
    public void requestBluetoothStatus(String status) {

    }

    @Override
    public void commandRobotAction(String json) {

    }

    @Override
    public void dealMessage(String json) {
        try {
            JSONObject mObject = new JSONObject(json);
            mObject.getString("cmd");
            mObject.getInt("len");
            mObject.getString("param").getBytes();
            UbtLog.d(TAG, "CMD  " + mObject.getString("cmd") + "len " + mObject.getString("param").getBytes()[0]);
            if (Integer.parseInt(mObject.getString("cmd")) == ConstValue.DV_READ_BATTERY) {
                mView.showCartoonAction("leg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取信鸽的AccessId 和信鸽的accessToken
     */
    public void getXGInfo() {
        String accessId = SPUtils.getInstance().getString(Constant.SP_XG_ACCESSID);
        String accessKey = SPUtils.getInstance().getString(Constant.SP_XG_ACCESSKEY);
           if (TextUtils.isEmpty(accessId) || TextUtils.isEmpty(accessKey)) {
        String Url = HttpEntity.getXGAppId + "?appName=ALPHA1E";
        UbtLog.d("XGREquest", "url===" + Url);
        OkHttpUtils.get()
                .addHeader("authorization", SPUtils.getInstance().getString(Constant.SP_LOGIN_TOKEN))
                .url(Url)
                .id(id)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d("XGREquest", "onError===" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d("XGREquest", "response===" + response);
                XGBaseModule xgBaseModule = GsonImpl.get().toObject(response, XGBaseModule.class);
                UbtLog.d("XGREquest", "response===" + xgBaseModule.toString());
                if (null != xgBaseModule && xgBaseModule.isSuccess()) {
                    for (XGDeviceMode xgDeviceMode : xgBaseModule.getData()) {
                        if (xgDeviceMode.getDevice().equals("a")) {
                            SPUtils.getInstance().put(Constant.SP_XG_ACCESSID, xgDeviceMode.getAccessId());
                            SPUtils.getInstance().put(Constant.SP_XG_ACCESSKEY, xgDeviceMode.getAccessKey());
                            BindXGServer(xgDeviceMode);
                            break;
                        }
                    }
                }
            }
        });
    }
      }

    public void BindXGServer(XGDeviceMode xgDeviceMode) {
        XGGetAccessIdRequest request = new XGGetAccessIdRequest();
        request.setAppId(xgDeviceMode.getAppId());
        request.setCreateTime(xgDeviceMode.getCreateTime());
        request.setUserId(SPUtils.getInstance().getString(Constant.SP_USER_ID));
        request.setToken(XGUBTManager.getInstance(AlphaApplication.getmContext()).getDeviceToken());
        OkHttpUtils.postString()
                .addHeader("authorization", SPUtils.getInstance().getString(Constant.SP_LOGIN_TOKEN))
                .url(HttpEntity.bindXGServer)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(GsonImpl.get().toJson(request))
                .id(id)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d("XGREquest", "onError===" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d("XGREquest", "response===" + response);
                AlphaApplication.initXG();
            }
        });
    }

}
