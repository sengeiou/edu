package com.ubt.alpha1e.userinfo.photoshow;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

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
