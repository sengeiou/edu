package com.ubt.alpha1e.edu.userinfo.dynamicaction;

import com.ubt.alpha1e.edu.mvp.BasePresenter;
import com.ubt.alpha1e.edu.mvp.BaseView;
import com.ubt.alpha1e.edu.userinfo.model.DynamicActionModel;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class DynamicActionContract {
    interface View extends BaseView {
        void setDynamicData(boolean status,int type,List<DynamicActionModel> list);
        void deleteActionResult(boolean isSuccess);
    }

    interface  Presenter extends BasePresenter<View> {
        void getDynamicData(int type);

        void getDynamicData(int pullType, int dataType, int page, int offset);

        void deleteActionById(int actionId);
    }
}
