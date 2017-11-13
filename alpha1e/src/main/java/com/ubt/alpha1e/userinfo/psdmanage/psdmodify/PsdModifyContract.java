package com.ubt.alpha1e.userinfo.psdmanage.psdmodify;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class PsdModifyContract {
    interface View extends BaseView {
        void onModifyPassword(boolean isSuccess, String error_msg);
    }

    interface  Presenter extends BasePresenter<View> {
        void doModifyPassword(String oldPassword,String newPassword);
    }
}
