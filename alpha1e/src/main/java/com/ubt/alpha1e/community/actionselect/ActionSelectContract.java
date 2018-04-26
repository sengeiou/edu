package com.ubt.alpha1e.community.actionselect;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class ActionSelectContract {
    interface View extends BaseView {
        void setDynamicData(boolean status,int type,List<DynamicActionModel> list);
        void deleteActionResult(boolean isSuccess);
    }

    interface  Presenter extends BasePresenter<View> {

        void getDynamicData(int type,int page,int offset);

    }
}
