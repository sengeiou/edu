package com.ubt.alpha1e_edu.userinfo.usermanager;

import android.content.Context;

import com.ubt.alpha1e_edu.AlphaApplication;
import com.ubt.alpha1e_edu.mvp.BasePresenterImpl;

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
