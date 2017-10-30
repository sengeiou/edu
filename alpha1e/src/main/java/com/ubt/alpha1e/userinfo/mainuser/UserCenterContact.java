package com.ubt.alpha1e.userinfo.mainuser;


import android.app.Fragment;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

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
        void loadData(List<LeftMenuModel> list,List<Fragment> fragmentList);

    }

    interface UserCenterPresenter extends BasePresenter<UserCenterView> {
        void initData();

    }

}
