package com.ubt.alpha1e.userinfo.usermanager;

import android.content.Context;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class UserInfoPresenter extends BasePresenterImpl<UserInfoContract.View> implements UserInfoContract.Presenter {

    @Override
    public void getUserInfo(Context context) {
        if (isAttachView()) {
            mView.setUserInfo(((AlphaApplication) mView.getContext().getApplicationContext()).getCurrentUserInfo());
        }
    }
}
