package com.ubt.alpha1e_edu.userinfo.mainuser;

//import android.app.Fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.ubt.alpha1e_edu.mvp.BasePresenter;
import com.ubt.alpha1e_edu.mvp.BaseView;

import java.util.List;

/**
 * @author：liuhai
 * @date：2017/10/27 11:41
 * @modifier：ubt
 * @modify_date：2017/10/27 11:41
 * [A brief description]
 * version
 */

public interface UserCenterContact {

    interface UserCenterView extends BaseView {
        void loadData(List<LeftMenuModel> list, List<Fragment> fragmentList);

        void getUnReadMessage(boolean isSuccess, int count);
    }

    interface UserCenterPresenter extends BasePresenter<UserCenterView> {
        void initData(Context contex);

        void getUnReadMessage();
    }

}
