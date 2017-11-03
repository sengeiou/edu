package com.ubt.alpha1e.userinfo.dynamicaction;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class DynamicActionContract {
    interface View extends BaseView {
        void setDynamicData(List<DynamicActionModel> list);
    }

    interface  Presenter extends BasePresenter<View> {
        void getDynamicData(int type);
    }
}
