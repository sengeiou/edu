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
            LeftMenuModel menuModel1 = new LeftMenuModel("宝宝信息");
            leftMenuModels.add(menuModel1);
            LeftMenuModel menuModel2 = new LeftMenuModel("学习成就");
            leftMenuModels.add(menuModel2);
            LeftMenuModel menuModel3 = new LeftMenuModel("数据统计");
            leftMenuModels.add(menuModel3);
            LeftMenuModel menuModel4 = new LeftMenuModel("我的消息");
            leftMenuModels.add(menuModel4);
            LeftMenuModel menuModel5 = new LeftMenuModel("我的动态");
            leftMenuModels.add(menuModel5);
            LeftMenuModel menuModel6 = new LeftMenuModel("我的原创");
            leftMenuModels.add(menuModel6);
            LeftMenuModel menuModel7 = new LeftMenuModel("我的下载");
            leftMenuModels.add(menuModel7);
            LeftMenuModel menuModel8 = new LeftMenuModel("设置");
            leftMenuModels.add(menuModel8);

            List<Fragment> fragmentList = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                Fragment fragment = UserInfoFragment.newInstance(leftMenuModels.get(i).getNameString(), "");
                fragmentList.add(fragment);
            }

            mView.loadData(leftMenuModels, fragmentList);
        }
    }


}
