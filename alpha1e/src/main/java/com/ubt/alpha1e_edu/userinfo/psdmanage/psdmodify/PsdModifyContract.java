package com.ubt.alpha1e_edu.userinfo.psdmanage.psdmodify;

import com.ubt.alpha1e_edu.mvp.BasePresenter;
import com.ubt.alpha1e_edu.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class PsdModifyContract {
    interface View extends BaseView {
        void onModifyPassword(boolean isSuccess, String msg);
    }

    interface  Presenter extends BasePresenter<View> {
        void doModifyPassword(String oldPassword,String newPassword);
    }
}
