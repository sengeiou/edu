package com.ubt.alpha1e_edu.utils;

import android.app.Activity;
import android.content.Intent;

import com.ubt.alpha1e_edu.utils.log.UbtLog;

/**
   * User: wilson <br>
   * Description: login util,manage login automatically <br>
   * Time: 2016/7/11 11:29 <br>
   */

public class LoginUtil{


    static LoginCallback mCallback;

    public interface LoginCallback {
        void onLogin();
    }

    public static void checkLogin(Activity context, LoginCallback callback) {
        //此处检查当前的登录状态
        boolean login = UserAccountManager.get(context).checkLoginState();
        if (login) {
            callback.onLogin();
        } else {
            mCallback = callback;
            NavigateUtil.INSTANCE.navigateToLoginActivityForResult(context,NavigateUtil.INSTANCE.LOGIN_REQUEST);
        }
    }


    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        UbtLog.d("wilson","-------onActivityResult-----------");
        if (requestCode == NavigateUtil.INSTANCE.LOGIN_REQUEST) {
            mCallback.onLogin();
        }
        mCallback = null;
    }
}
