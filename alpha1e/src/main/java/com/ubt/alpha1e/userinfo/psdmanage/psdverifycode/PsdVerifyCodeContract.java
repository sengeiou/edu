package com.ubt.alpha1e.userinfo.psdmanage.psdverifycode;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class PsdVerifyCodeContract {
    interface View extends BaseView {
        void onGetVerifyCode(boolean isSuccess,String errorMsg);

        void onVerifyCode(boolean isSuccess,String errorMsg);
    }

    interface  Presenter extends BasePresenter<View> {
        void doGetVerifyCode(String telephone);

        void doVerifyCode(String telephone,String verifyCode);
    }
}
