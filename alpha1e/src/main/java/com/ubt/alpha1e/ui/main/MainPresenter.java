package com.ubt.alpha1e.ui.main;

import android.text.TextUtils;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.XGGetAccessIdRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.MediaType;

import static android.R.attr.id;


public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {
    private String TAG = "MainPresenter";


    @Override
    public void requestCartoonAction(String json) {
        //  mView.showCartoonAction(1);

    }

    @Override
    public void requestCartoonText(String text) {

    }

    @Override
    public void requestBluetoothStatus(String status) {

    }

    @Override
    public void commandRobotAction(byte cmd, byte[] params) {
        MainUiBtHelper.getInstance(mView.getContext()).sendCommand(cmd, params);
    }

    @Override
    public void dealMessage(String json) {
        try {
            JSONObject mObject = new JSONObject(json);
            mObject.getString("cmd");
            mObject.getInt("len");
            mObject.getString("param").getBytes();
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
        String userId = SPUtils.getInstance().getString(Constant.SP_XG_USERID);
        UbtLog.d("XGREquest","getXGInfo  old userId"+userId+ "new USERID "+SPUtils.getInstance().getString(Constant.SP_USER_ID));
        //if (TextUtils.isEmpty(accessId) || TextUtils.isEmpty(accessKey)) {
        if(TextUtils.isEmpty(userId)||!userId.equals(SPUtils.getInstance().getString(Constant.SP_USER_ID))){
            if(!userId.equals(SPUtils.getInstance().getString(Constant.SP_USER_ID))){
               // UnBindXGServer();
                UnBindXGServer("20002",userId);
            }
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
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject object=jsonArray.getJSONObject(0);
                        XGDeviceMode xgDeviceMode =GsonImpl.get().toObject(object.toString(),XGDeviceMode.class);
                        UbtLog.d("XGREquest", "xgDeviceMode===" + xgDeviceMode.toString());
                        if (xgDeviceMode.getDevice().equals("a")) {
                            SPUtils.getInstance().put(Constant.SP_XG_ACCESSID, xgDeviceMode.getAccessId());
                            SPUtils.getInstance().put(Constant.SP_XG_ACCESSKEY, xgDeviceMode.getAccessKey());

                            BindXGServer(xgDeviceMode);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    XGBaseModule xgBaseModule = GsonImpl.get().toObject(response, XGBaseModule.class);
//                    UbtLog.d("XGREquest", "response===" + xgBaseModule.toString());
//                    //if (null != xgBaseModule && xgBaseModule.isSuccess()) {
//                    for (XGDeviceMode xgDeviceMode : xgBaseModule.getData()) {
//                        if (xgDeviceMode.getDevice().equals("a")) {
//                            SPUtils.getInstance().put(Constant.SP_XG_ACCESSID, xgDeviceMode.getAccessId());
//                            SPUtils.getInstance().put(Constant.SP_XG_ACCESSKEY, xgDeviceMode.getAccessKey());
//                            BindXGServer(xgDeviceMode);
//                            break;
//                        }
//                    }
                    //  }
                }
            });
        }else {
            UbtLog.d("XGREquest","accessId:   "+accessId+"accessKey:    "+accessKey);
        }
    }

    public void BindXGServer(XGDeviceMode xgDeviceMode) {
        XGGetAccessIdRequest request = new XGGetAccessIdRequest();
        request.setAppId(xgDeviceMode.getAppId());
        request.setCreateTime(xgDeviceMode.getCreateTime());
        request.setUserId(SPUtils.getInstance().getString(Constant.SP_USER_ID));
        request.setToken(XGUBTManager.getInstance(AlphaApplication.getmContext()).getDeviceToken());
        UbtLog.d("XGREquest", "url===" + HttpEntity.bindXGServer);
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
                SPUtils.getInstance().put(Constant.SP_XG_ACCESSKEY, SPUtils.getInstance().getString(Constant.SP_USER_ID));
                SPUtils.getInstance().put(Constant.SP_XG_USERID,SPUtils.getInstance().getString(Constant.SP_USER_ID));
                AlphaApplication.initXG();
            }
        });
    }

    public void UnBindXGServer(String appId,String userId) {
        XGGetAccessIdRequest request = new XGGetAccessIdRequest();
        request.setAppId(appId);
        request.setUserId(userId);
        request.setToken(XGUBTManager.getInstance(AlphaApplication.getmContext()).getDeviceToken());
        UbtLog.d("XGREquest", "url===" + HttpEntity.unbindXGServer +"unbind UserId: "+userId);
        OkHttpUtils.postString()
                .addHeader("authorization", SPUtils.getInstance().getString(Constant.SP_LOGIN_TOKEN))
                .url(HttpEntity.unbindXGServer)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(GsonImpl.get().toJson(request))
                .id(id)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d("XGREquest", "unbind onError===" + e.getMessage());
            }
            @Override
            public void onResponse(String response, int id) {
                UbtLog.d("XGREquest", "unbind response===" + response);
            }
        });
    }

    private void Test() {
        //Enter the course enter
        byte[] papram = new byte[1];
        papram[0] = 0x01;
        commandRobotAction(ConstValue.DV_ENTER_COURSE, papram);
        //Exit the course enter
        papram[0] = 0x0;
        commandRobotAction(ConstValue.DV_ENTER_COURSE, papram);

    }

}
