package com.ubt.alpha1e_edu.userinfo.psdmanage.psdsetting;

import com.ubt.alpha1e_edu.mvp.BasePresenter;
import com.ubt.alpha1e_edu.mvp.BaseView;

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
