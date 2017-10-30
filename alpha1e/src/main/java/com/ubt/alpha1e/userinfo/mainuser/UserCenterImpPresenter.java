package com.ubt.alpha1e.userinfo.mainuser;


import android.app.Fragment;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.userinfo.usermanager.UserInfoFragment;

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
            LeftMenuModel menuModel0 = new LeftMenuModel("leon");
            menuModel0.setImageId(R.drawable.actions_square_detail_downloaded);
            leftMenuModels.add(menuModel0);
            LeftMenuModel menuModel1 = new LeftMenuModel("成就");
            menuModel1.setImageId(R.drawable.actions_online_download_ft);
            leftMenuModels.add(menuModel1);
            LeftMenuModel menuModel2 = new LeftMenuModel("消息");
            menuModel2.setImageId(R.drawable.actions_online_download_ft);
            leftMenuModels.add(menuModel2);
            LeftMenuModel menuModel3 = new LeftMenuModel("动态");
            menuModel3.setImageId(R.drawable.actions_online_download_ft);
            leftMenuModels.add(menuModel3);
            LeftMenuModel menuModel4 = new LeftMenuModel("原创");
            menuModel4.setImageId(R.drawable.actions_online_download_ft);
            leftMenuModels.add(menuModel4);
            LeftMenuModel menuModel5 = new LeftMenuModel("下载");
            menuModel5.setImageId(R.drawable.actions_online_download_ft);
            leftMenuModels.add(menuModel5);
            LeftMenuModel menuModel6 = new LeftMenuModel("设置");
            menuModel6.setImageId(R.drawable.actions_online_download_ft);
            leftMenuModels.add(menuModel6);

            List<Fragment> fragmentList = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                Fragment fragment = UserInfoFragment.newInstance(leftMenuModels.get(i).getNameString(), "");
                fragmentList.add(fragment);
            }

            mView.loadData(leftMenuModels, fragmentList);
        }
    }


}
