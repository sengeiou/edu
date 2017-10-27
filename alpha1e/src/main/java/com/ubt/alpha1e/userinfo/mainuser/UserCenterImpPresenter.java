package com.ubt.alpha1e.userinfo.mainuser;


import android.app.Fragment;

import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.userinfo.manager.UserInfoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：liuhai
 * @date：2017/10/27 11:44
 * @modifier：ubt
 * @modify_date：2017/10/27 11:44
 * [A brief description]
 * version
 */

public class UserCenterImpPresenter extends BasePresenterImpl<UserCenterContact.UserCenterView> implements UserCenterContact.UserCenterPresenter {

    @Override
    public void initData() {
        if (isAttachView()) {
            List<LeftMenuModel> leftMenuModels = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                LeftMenuModel menuModel = new LeftMenuModel("test" + i);
                leftMenuModels.add(menuModel);
            }

            mView.loadData(leftMenuModels);
        }
    }

    @Override
    public void initFragmentData() {
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
           Fragment fragment = UserInfoFragment.newInstance("","");
            fragmentList.add(fragment);
        }
        mView.loadFragmentData(fragmentList);
    }
}
