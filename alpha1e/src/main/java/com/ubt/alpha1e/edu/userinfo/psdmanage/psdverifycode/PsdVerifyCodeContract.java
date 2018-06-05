package com.ubt.alpha1e.edu.userinfo.psdmanage.psdverifycode;

import com.ubt.alpha1e.edu.mvp.BasePresenter;
import com.ubt.alpha1e.edu.mvp.BaseView;

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
