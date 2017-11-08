package com.ubt.alpha1e.login;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.tencent.ai.tvs.AuthorizeListener;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.env.ELoginEnv;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.info.QQOpenInfoManager;
import com.tencent.ai.tvs.info.WxInfoManager;
import com.tencent.connect.common.Constants;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.loginauth.LoginAuthActivity;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.main.MainActivity;
import com.ubt.alpha1e.userinfo.model.UserAllModel;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.ubt.alpha1e.userinfo.useredit.UserEditActivity;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class LoginActivity extends BaseActivity implements AuthorizeListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private String appidWx = "wxfa7003941d57a391";
    private String appidQQOpen = "1106515940";
    private LoginProxy proxy;
    private WxInfoManager wxInfoManager;
    private QQOpenInfoManager qqOpenInfoManager;


    RelativeLayout rlQQLgoin;
    RelativeLayout rlWXLogin;

    private int loginType = 0; //默认 0 QQ， 1 WX;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "LoginActivity onCreate");

        setContentView(R.layout.activity_login_with_tvs);
        rlQQLgoin = (RelativeLayout) findViewById(R.id.rl_qq_login);
        rlWXLogin = (RelativeLayout) findViewById(R.id.rl_wx_login);

        initTVS();
        initControlListener();
    }

    private void initTVS() {
        proxy = LoginProxy.getInstance(appidWx, appidQQOpen);
        proxy.setOwnActivity(this);
        proxy.setAuthorizeListener(this);
        proxy.setLoginEnv(ELoginEnv.FORMAL);

        wxInfoManager = (WxInfoManager) proxy.getInfoManager(ELoginPlatform.WX);
        qqOpenInfoManager = (QQOpenInfoManager) proxy.getInfoManager(ELoginPlatform.QQOpen);


    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {
        rlQQLgoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginType = 0;
                proxy.requestLogin(ELoginPlatform.QQOpen, "", "", LoginActivity.this);
//                Intent intent = new Intent();
//                intent.setClass(LoginActivity.this, LoginAuthActivity.class);
//                startActivity(intent);
            }
        });

        rlWXLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginType = 1;
                proxy.clearToken(ELoginPlatform.WX, LoginActivity.this);
                proxy.requestLogin(ELoginPlatform.WX, "", "", LoginActivity.this);
            }
        });
    }

    @Override
    protected void initBoardCastListener() {

    }


    @Override
    public void onSuccess(int i) {
        if (i == 5) {
            Log.e(TAG, "login onSuccess" + i);
            String accessToken = "";
            String openID = "";
            String appID = "";
            if (loginType == 0) {
                accessToken = qqOpenInfoManager.accessToken;
                openID = qqOpenInfoManager.openID;
                appID = qqOpenInfoManager.appId;
            } else {
                accessToken = wxInfoManager.accessToken;
                openID = wxInfoManager.openID;
            }

            Log.e(TAG, "accessToken:" + accessToken + "--openID:" + openID + "--appID:" + appID);

            Toast.makeText(this, "onSuccess", Toast.LENGTH_LONG).show();
            doThirdLogin(accessToken, openID);
        }
    }

    @Override
    public void onError(int i) {
        UbtLog.d(TAG, "login onError:" + i);
        Toast.makeText(this, "onError", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel(int i) {
        UbtLog.d(TAG, "login onCancel:" + i);
        Toast.makeText(this, "onCancel", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            UbtLog.d(TAG, "data:" + data.toString());
            if (resultCode == -1) {
                proxy.handleQQOpenIntent(requestCode, resultCode, data);
            }
        }
    }


    private void doThirdLogin(String accessToken, String openID) {

        String params = "";

        if (loginType == 0) {
            params = "{"
                    + "\"accessToken\":" + "\"" + accessToken + "\""
                    + ",\n\"appId\":" + "\"" + appidQQOpen + "\""
                    + ",\n\"loginType\":" + "\"" + "QQ" + "\""
                    + ",\n\"openId\":" + "\"" + openID + "\""
                    + "}";
        } else {
            params = "{"
                    + "\"accessToken\":" + "\"" + accessToken + "\""
                    + ",\n\"loginType\":" + "\"" + "WX" + "\""
                    + ",\n\"openId\":" + "\"" + openID + "\""
                    + "}";
        }

        UbtLog.d(TAG, "doThirdLogin accessToken:" + accessToken + "openID:" + openID + "params:" + params);
        OkHttpClientUtils.getJsonByPutRequest(HttpEntity.THRID_LOGIN_URL, params, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "onError:" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "onResponse:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String token = jsonObject.getString("token");
                    saveThirdLoginToken(token);

                    String user = jsonObject.getString("user");
                    saveThirdLoginUserId(user);
                    //getUserPhone(user);
                    getUserInfo();
                } catch (JSONException ex) {

                }


            }
        });
    }


    //保存第三方登录成功，后台返回的token，用于获取用户个人信息
    public void saveThirdLoginToken(String token) {

        try {
            JSONObject jsonObject = new JSONObject(token);
            String spToken = jsonObject.getString("token");
            SPUtils.getInstance().put(Constant.SP_LOGIN_TOKEN, spToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void saveThirdLoginUserId(String userInfo) {
        try {

            JSONObject jsonObject = new JSONObject(userInfo);
            String userId = jsonObject.getString("userId");
            String userImage = jsonObject.getString("userImage");
            String nickName = jsonObject.getString("nickName");
            UserModel userModel = new UserModel();
            userModel.setNickName(nickName);
            userModel.setHeadPic(userImage);
            userModel.setUserId(userId);
            SPUtils.getInstance().put(Constant.SP_USER_ID, userId);
            SPUtils.getInstance().put(Constant.SP_USER_IMAGE, userImage);
            SPUtils.getInstance().put(Constant.SP_USER_NICKNAME, nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void getUserInfo() {
        BaseRequest baseRequest = new BaseRequest();
        OkHttpClientUtils.getJsonByPostRequest(HttpEntity.GET_USER_INFO, baseRequest, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "onError:" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "getUser__response==" + response);
                BaseResponseModel<UserAllModel> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<UserAllModel>>() {
                        }.getType());
                if (baseResponseModel.status) {
                    Intent intent = new Intent();
                    UserAllModel userAllModel = baseResponseModel.models;
                    UbtLog.d(TAG, "userAllModel==" + userAllModel.toString());
                    if (!TextUtils.isEmpty(userAllModel.getPhone())) {
                        //用户已绑定电话号码，直接通过后台去获取用户信息
                        if (!TextUtils.isEmpty(userAllModel.getAge())) {
                            UserModel userModel = new UserModel();
                            userModel.setNickName(userAllModel.getNickName());
                            userModel.setHeadPic(userAllModel.getHeadPic());
                            userModel.setPhone(userAllModel.getPhone());
                            userModel.setAge(userAllModel.getAge());
                            userModel.setSex(userAllModel.getSex());
                            userModel.setGrade(userAllModel.getGrade());
                            SPUtils.getInstance().saveObject(Constant.SP_USER_INFO, userModel);
                            intent.setClass(LoginActivity.this, MainActivity.class);
                        } else {
                            intent.setClass(LoginActivity.this, UserEditActivity.class);
                        }
                    } else {
                        //手机号码绑定流程
                        intent.putExtra("userInfo",userAllModel);
                        intent.setClass(LoginActivity.this, LoginAuthActivity.class);
                    }
                    startActivity(intent);
                    LoginActivity.this.finish();
                }
            }
        });
    }


}
