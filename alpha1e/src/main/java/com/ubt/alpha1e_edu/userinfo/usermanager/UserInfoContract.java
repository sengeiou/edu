package com.ubt.alpha1e_edu.userinfo.usermanager;

import android.content.Context;

import com.ubt.alpha1e_edu.data.model.UserInfo;
import com.ubt.alpha1e_edu.mvp.BasePresenter;
import com.ubt.alpha1e_edu.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class UserInfoContract {
    interface View extends BaseView {
        void setUserInfo(UserInfo userInfo);
    }

    interface Presenter extends BasePresenter<View> {
        void getUserInfo(Context context);
    }
}
