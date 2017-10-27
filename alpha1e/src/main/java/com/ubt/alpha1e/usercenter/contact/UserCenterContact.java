package com.ubt.alpha1e.usercenter.contact;

import android.app.Fragment;

import com.ubt.alpha1e.base.BasePresenter;
import com.ubt.alpha1e.base.BaseView;
import com.ubt.alpha1e.usercenter.model.LeftMenuModel;

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

    interface UserCenterView extends BaseView{
        void loadData(List<LeftMenuModel> list);
        void loadFragmentData(List<Fragment> fragments);
    }

    interface UserCenterPresenter extends BasePresenter<UserCenterView> {
        void initData();
        void initFragmentData();
    }

}
