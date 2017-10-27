package com.ubt.alpha1e.test;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class TestPresenter extends BasePresenterImpl<TestContract.View> implements TestContract.Presenter{
    @Override
    public void login(String username, String password) {
        if("test".equals(username)&&"123456".equals(password)){
            mView.loginSuccess(null);
        }else {
            mView.loginFailed("帐号和密码不正确");
        }
    }
}
