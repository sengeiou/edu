package com.ubt.alpha1e_edu.userinfo.photoshow;

import com.ubt.alpha1e_edu.mvp.BasePresenter;
import com.ubt.alpha1e_edu.mvp.BaseView;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class PhotoShowContract {
    interface View extends BaseView {
        void onSavePhoto(boolean isSuccess,String msg);
    }

    interface  Presenter extends BasePresenter<View> {
        void doSavePhoto();
    }
}
