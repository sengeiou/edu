package com.ubt.alpha1e.userinfo.psdmanage.psdsetting;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class PsdSettingContract {
    interface View extends BaseView {
        void onSetUserPassword(boolean isSuccess, String errorMsg);
    }

    interface  Presenter extends BasePresenter<View> {
        void doSetUserPassword(String password);
    }
}
