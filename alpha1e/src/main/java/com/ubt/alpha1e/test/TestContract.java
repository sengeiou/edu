package com.ubt.alpha1e.test;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class TestContract {
    interface View extends BaseView {
        //在View层回调，根据Presenter逻辑调用
        void loginSuccess(Object user);

        void loginFailed(String message);
    }

    interface  Presenter extends BasePresenter<View> {
        //在View层调用，在Presenter中实现
        void login(String username, String password);
    }
}
