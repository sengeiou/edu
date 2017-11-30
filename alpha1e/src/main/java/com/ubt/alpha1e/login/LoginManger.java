package com.ubt.alpha1e.login;

import android.app.Activity;
import android.content.Intent;

import com.tencent.ai.tvs.AuthorizeListener;
import com.tencent.ai.tvs.LoginApplication;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.env.ELoginEnv;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.info.LoginInfoManager;
import com.tencent.ai.tvs.info.QQOpenInfoManager;
import com.tencent.ai.tvs.info.WxInfoManager;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class LoginManger implements AuthorizeListener {

    private static final String TAG = "LoginManger";

    private String appidWx = "wxfa7003941d57a391";
    private String appidQQOpen = "1106515940";
    private LoginProxy proxy;
    private WxInfoManager wxInfoManager;
    private QQOpenInfoManager qqOpenInfoManager;


    public static final String PID = "";
    public static final String DSN = "";

    private OnLoginListener onLoginListener;
    private int loginType = 0; // 0表示WX， 1表示QQOpen
    private OnRefreshListener onRefreshListener;

    private volatile static LoginManger instance;

    public static LoginManger getInstance(){
        if(instance == null){
            instance = new LoginManger();
        }
        return  instance;
    }

    public LoginManger(){
        proxy = LoginProxy.getInstance(appidWx, appidQQOpen);
        proxy.setAuthorizeListener(this);
        proxy.setLoginEnv(ELoginEnv.FORMAL);
        wxInfoManager = (WxInfoManager) proxy.getInfoManager(ELoginPlatform.WX);
        qqOpenInfoManager = (QQOpenInfoManager) proxy.getInfoManager(ELoginPlatform.QQOpen);

    }

    public void init(Activity activity, OnLoginListener onLoginListener){
        proxy.setOwnActivity(activity);
        this.onLoginListener = onLoginListener;
    }

    public void refreshLoginToken(String productId, String dsn, OnRefreshListener onRefreshListener){
        UbtLog.d(TAG, "refreshLoginToken");
        this.onRefreshListener = onRefreshListener;
        int type = SPUtils.getInstance().getInt(Constant.SP_LOGIN_TYPE);
        if(type == 0){
            if (proxy.isTokenExist(ELoginPlatform.WX, LoginApplication.getInstance())) {
                UbtLog.d(TAG, "refreshLoginToken WX");
                proxy.requestTokenVerify(ELoginPlatform.WX, productId, dsn);
            }
        }else if(type == 1){
            if (proxy.isTokenExist(ELoginPlatform.QQOpen, LoginApplication.getInstance())) {
                UbtLog.d(TAG, "refreshLoginToken QQOpen");
                proxy.requestTokenVerify(ELoginPlatform.QQOpen, productId, dsn);
            }
        }

    }

    public void loginWX( Activity activity){

        if (!proxy.isWXAppInstalled()) {
            ToastUtils.showShort("您还没有安装微信，请先安装微信客户端");
            return;
        }
        if (!proxy.isWXAppSupportAPI()) {
            ToastUtils.showShort("您的微信版本太低，请更新版本");
            return;
        }
        UbtLog.d(TAG, "loginWX");
        loginType = 0;
        SPUtils.getInstance().put(Constant.SP_LOGIN_TYPE, loginType);
        proxy.clearToken(ELoginPlatform.WX, LoginApplication.getInstance());
        proxy.requestLogin(ELoginPlatform.WX, PID, DSN, activity);
    }

    public void loginQQ(Activity activity){
        loginType = 1;
        SPUtils.getInstance().put(Constant.SP_LOGIN_TYPE, loginType);
        proxy.clearToken(ELoginPlatform.QQOpen, LoginApplication.getInstance());
        proxy.requestLogin(ELoginPlatform.QQOpen, PID, DSN, activity);
    }

    public void handleQQOpenIntent(int requestCode, int resultCode, Intent data){
        proxy.handleQQOpenIntent(requestCode, resultCode, data);
    }

    public String getClientId(){
        int type = SPUtils.getInstance().getInt(Constant.SP_LOGIN_TYPE);
        if(type == 1){
            return proxy.getClientId(ELoginPlatform.QQOpen);
        }else{
            return proxy.getClientId(ELoginPlatform.WX);
        }


    }

    public void loginOut(){
        int type = SPUtils.getInstance().getInt(Constant.SP_LOGIN_TYPE);
        if(type == 1){
            proxy.clearToken(ELoginPlatform.QQOpen, LoginApplication.getInstance());
        }else{
            proxy.clearToken(ELoginPlatform.WX, LoginApplication.getInstance());
        }
    }


    @Override
    public void onSuccess(int i) {

        UbtLog.d(TAG, "onSuccess:" + i);
        if(i == AuthorizeListener.TOKENVERIFY_TYPE){
            UbtLog.d(TAG, "refresh login token success!");
        }


        if(i == AuthorizeListener.USERINFORECV_TYPE){
            if(onLoginListener != null){
                if(loginType == 0){
                    onLoginListener.onSuccess(i,  wxInfoManager);
                }else if(loginType == 1){
                    onLoginListener.onSuccess(i,  qqOpenInfoManager);
                }

            }
        }else if(i == AuthorizeListener.QQOPEN_TVSIDRECV_TYPE){
            UbtLog.d(TAG, "client qq" );
            if(onRefreshListener != null){
                onRefreshListener.onSuccess();
            }
        }else if( i == AuthorizeListener.WX_TVSIDRECV_TYPE){
            UbtLog.d(TAG, "client wx");
            if(onRefreshListener != null){
                onRefreshListener.onSuccess();
            }
        }





    }

    @Override
    public void onError(int i) {
        UbtLog.d(TAG, "onError:" + i);
        if(onLoginListener != null){
            onLoginListener.onError(i);
        }

        if(i == AuthorizeListener.QQOPEN_TVSIDRECV_TYPE || i == AuthorizeListener.WX_TVSIDRECV_TYPE){
            if(onRefreshListener != null){
                onRefreshListener.onError();
            }
        }
    }

    @Override
    public void onCancel(int i) {
        UbtLog.d(TAG, "onCancel:" + i);
        if(onLoginListener != null){
            onLoginListener.onCancel(i);
        }
    }


    public interface OnLoginListener{
        void onSuccess(int i,LoginInfoManager infoManager);
        void onError(int i);
        void onCancel(int i);
    }

    public interface OnRefreshListener{
        void onSuccess();
        void onError();
    }
}
