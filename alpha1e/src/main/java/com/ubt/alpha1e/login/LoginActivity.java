package com.ubt.alpha1e.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.info.LoginInfoManager;
import com.tencent.ai.tvs.info.QQOpenInfoManager;
import com.tencent.ai.tvs.info.WxInfoManager;
import com.tencent.connect.common.Constants;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.base.loading.LoadingDialog;
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

public class LoginActivity extends BaseActivity implements LoginManger.OnLoginListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private String appidWx = "wxfa7003941d57a391";
    private String appidQQOpen = "1106515940";
    private LoginProxy proxy;
    private WxInfoManager wxInfoManager;
    private QQOpenInfoManager qqOpenInfoManager;


    RelativeLayout rlQQLgoin;
    RelativeLayout rlWXLogin;
    ImageView ivPrivacy;
    boolean select = true;

//    private int loginType = 0; //默认 0 QQ， 1 WX;

    public static final String PID = "95518e46-af79-494e-b2fa-a6db6409ae6b:77022ec0a7614dbcb33c7ab73d4e2ceb";
    public static final String DSN = "123456";

    public static final String INVALID_TOKEN = "INVALID_TOKEN";

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "LoginActivity onCreate");

        setContentView(R.layout.activity_login_with_tvs);
        rlQQLgoin = (RelativeLayout) findViewById(R.id.rl_qq_login);
        rlWXLogin = (RelativeLayout) findViewById(R.id.rl_wx_login);
        ivPrivacy = (ImageView) findViewById(R.id.iv_privacy);
        select = true;
        ivPrivacy.setSelected(select);


        boolean invalid = getIntent().getBooleanExtra(INVALID_TOKEN, false);
        if(invalid){
            ToastUtils.showLong("叮当登录异常，请重新登录");
        }

        initTVS();
        initControlListener();
    }

    private void initTVS() {

        LoginManger.getInstance().init(this, this);


 /*       proxy = LoginProxy.getInstance(appidWx, appidQQOpen);
        proxy.setOwnActivity(this);
        proxy.setAuthorizeListener(this);
        proxy.setLoginEnv(ELoginEnv.FORMAL);

        wxInfoManager = (WxInfoManager) proxy.getInfoManager(ELoginPlatform.WX);
        qqOpenInfoManager = (QQOpenInfoManager) proxy.getInfoManager(ELoginPlatform.QQOpen);

        if (proxy.isTokenExist(ELoginPlatform.WX, this)) {
            proxy.requestTokenVerify(ELoginPlatform.WX, PID, DSN);
        }

        if (proxy.isTokenExist(ELoginPlatform.QQOpen, this)) {
            proxy.requestTokenVerify(ELoginPlatform.QQOpen, PID, DSN);
        }
*/

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {
        rlQQLgoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loginType = 0;
//                proxy.clearToken(ELoginPlatform.QQOpen, LoginActivity.this);
//                proxy.requestLogin(ELoginPlatform.QQOpen, PID, DSN, LoginActivity.this);
                LoginManger.getInstance().loginQQ(LoginActivity.this);
            }
        });

        rlWXLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loginType = 1;
//                proxy.clearToken(ELoginPlatform.WX, LoginActivity.this);
//                proxy.requestLogin(ELoginPlatform.WX, PID, DSN, LoginActivity.this);
                LoginManger.getInstance().loginWX(LoginActivity.this);
            }
        });

        ivPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UbtLog.d(TAG, "ivPrivacy onClick");
                if(select){
                    select = false;
                    ivPrivacy.setSelected(select);
                }else{
                    select = true;
                    ivPrivacy.setSelected(select);
                }
            }
        });
    }

    @Override
    protected void initBoardCastListener() {

    }


    @Override
    public void onSuccess(int i, LoginInfoManager loginInfoManager) {
        Log.e(TAG, "login onSuccess" + i + "--loginInfoManager:" + loginInfoManager.toString() + "--openID:" + loginInfoManager.openID);

        String accessToken = loginInfoManager.accessToken;
        String openID = loginInfoManager.openID;
        doThirdLogin(accessToken, openID);


/*        if(i==AuthorizeListener.WX_TVSIDRECV_TYPE){  //和机器人联调的
            UbtLog.d(TAG, "sss wx:"+ proxy.getClientId(ELoginPlatform.WX));
            SPUtils.getInstance().put(SP_CLIENT_ID, proxy.getClientId(ELoginPlatform.WX));
        }

        if(i== AuthorizeListener.QQOPEN_TVSIDRECV_TYPE){
            UbtLog.d(TAG, "sss qq:"+ proxy.getClientId(ELoginPlatform.QQOpen));
            SPUtils.getInstance().put(SP_CLIENT_ID, proxy.getClientId(ELoginPlatform.QQOpen));
        }


        String accessToken = "";
        String openID = "";
        String appID = "";
        if (loginType == 0) {
            accessToken = qqOpenInfoManager.accessToken;
            openID = qqOpenInfoManager.openID;
            appID = qqOpenInfoManager.appId;
            if (i == AuthorizeListener.USERINFORECV_TYPE) {
                    doThirdLogin(accessToken, openID);  //QQ登录会回调2次onSuccess,只在type为5的时候执行登录
            }

        } else {
            accessToken = wxInfoManager.accessToken;
            openID = wxInfoManager.openID;
            if(i==AuthorizeListener.USERINFORECV_TYPE){
                doThirdLogin(accessToken, openID);
            }

        }

        Log.e(TAG, "accessToken:" + accessToken + "--openID:" + openID + "--appID:" + appID);*/

    }

    @Override
    public void onError(int i) {
        UbtLog.d(TAG, "login onError:" + i);
        ToastUtils.showShort("登录失败,请重新登录");
    }

    @Override
    public void onCancel(int i) {
        UbtLog.d(TAG, "login onCancel:" + i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            if (resultCode == -1) {
//                proxy.handleQQOpenIntent(requestCode, resultCode, data);
                LoginManger.getInstance().handleQQOpenIntent(requestCode, resultCode, data);
            }
        }
    }

    //登录页面点击返回退出app,防止在设置清除用户信息之后调到登录页面点击返回回到主页面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ((AlphaApplication) this.getApplication()).doExitApp(false);
        }
        return super.onKeyDown(keyCode, event);
    }


    private void doThirdLogin(String accessToken, String openID) {

        String params = "";
        UbtLog.d(TAG, "loginType:" + SPUtils.getInstance().getInt(Constant.SP_LOGIN_TYPE));

        if (SPUtils.getInstance().getInt(Constant.SP_LOGIN_TYPE) == 1) {
            params = "{"
                    + "\"accessToken\":" + "\"" + accessToken + "\""
                    + ",\n\"appId\":" + "\"" + appidQQOpen + "\""
                    + ",\n\"loginType\":" + "\"" + "QQ" + "\""
                    + ",\n\"openId\":" + "\"" + openID + "\""
                    + ",\n\"ubtAppId\":"  + 100010011
                    + "}";
        } else {
            params = "{"
                    + "\"accessToken\":" + "\"" + accessToken + "\""
                    + ",\n\"loginType\":" + "\"" + "WX" + "\""
                    + ",\n\"openId\":" + "\"" + openID + "\""
                    + ",\n\"ubtAppId\":"  + 100010011
                    + "}";
        }

        UbtLog.d(TAG, "doThirdLogin accessToken:" + accessToken + "openID:" + openID + "params:" + params);
        LoadingDialog.show(this);

        OkHttpClientUtils.getJsonByPutRequest(HttpEntity.THRID_LOGIN_URL, params, 0).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "onError:" + e.getMessage());
                LoadingDialog.dismiss(LoginActivity.this);
                ToastUtils.showShort("登录失败");
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
            SPUtils.getInstance().saveObject(Constant.SP_USER_INFO, userModel);
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
                LoadingDialog.dismiss(LoginActivity.this);
                ToastUtils.showShort("获取用户信息失败");
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "getUser__response==" + response);
                LoadingDialog.dismiss(LoginActivity.this);
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
                            saveUserInfo(userAllModel);
                            intent.setClass(LoginActivity.this, MainActivity.class);
                        } else {
                            saveUserInfo(userAllModel);
                            intent.setClass(LoginActivity.this, UserEditActivity.class);
                        }
                    } else {
                        //手机号码绑定流程
                         intent.setClass(LoginActivity.this, LoginAuthActivity.class);
                    }

                    startActivity(intent);
                    LoginActivity.this.finish();
                }
            }
        });
    }



    private void saveUserInfo(UserAllModel userAllModel){
        UserModel userModel = new UserModel();
        userModel.setNickName(userAllModel.getNickName());
        userModel.setHeadPic(userAllModel.getHeadPic());
        userModel.setPhone(userAllModel.getPhone());
        userModel.setAge(userAllModel.getAge());
        userModel.setSex(userAllModel.getSex());
        userModel.setGrade(userAllModel.getGrade());
        SPUtils.getInstance().saveObject(Constant.SP_USER_INFO, userModel);
    }


}



