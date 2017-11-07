package com.ubt.alpha1e.userinfo.mainuser;


import android.app.Fragment;
import android.content.Context;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.userinfo.dynamicaction.DynamicActionFragment;
import com.ubt.alpha1e.userinfo.notice.NoticeFragment;
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
    public void initData(Context context) {
        if (isAttachView()) {
            List<LeftMenuModel> leftMenuModels = new ArrayList<>();
            LeftMenuModel menuModel0 = new LeftMenuModel(context.getResources().getString(R.string.user_center_info));
            menuModel0.setImageId(R.drawable.radio_selector_main_left_info);
            leftMenuModels.add(menuModel0);
            LeftMenuModel menuModel1 = new LeftMenuModel(context.getResources().getString(R.string.user_center_achievement));
            menuModel1.setImageId(R.drawable.radio_selector_main_left_achievement);
            leftMenuModels.add(menuModel1);
            LeftMenuModel menuModel2 = new LeftMenuModel(context.getResources().getString(R.string.user_center_message));
            menuModel2.setImageId(R.drawable.radio_selector_main_left_message);
            leftMenuModels.add(menuModel2);
            LeftMenuModel menuModel3 = new LeftMenuModel(context.getResources().getString(R.string.user_center_dynamic));
            menuModel3.setImageId(R.drawable.radio_selector_main_left_dynaic);
            leftMenuModels.add(menuModel3);
            LeftMenuModel menuModel4 = new LeftMenuModel(context.getResources().getString(R.string.user_center_original));
            menuModel4.setImageId(R.drawable.radio_selector_main_left_create);
            leftMenuModels.add(menuModel4);
            LeftMenuModel menuModel5 = new LeftMenuModel(context.getResources().getString(R.string.user_center_download));
            menuModel5.setImageId(R.drawable.radio_selector_main_left_download);
            leftMenuModels.add(menuModel5);
            LeftMenuModel menuModel6 = new LeftMenuModel(context.getResources().getString(R.string.user_center_setting));
            menuModel6.setImageId(R.drawable.radio_selector_main_left_setting);
            leftMenuModels.add(menuModel6);

            List<Fragment> fragmentList = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                if (i == 0) {
                    fragmentList.add(UserInfoFragment.newInstance(leftMenuModels.get(i).getNameString(), ""));
                } else if (i == 4) {
                    fragmentList.add(DynamicActionFragment.newInstance("", ""));
                } else {
                    fragmentList.add(NoticeFragment.newInstance("", ""));
                }
            }

            mView.loadData(leftMenuModels, fragmentList);
        }
    }


}
