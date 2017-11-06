package com.ubt.alpha1e.userinfo.dynamicaction;

import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.userinfo.model.DynamicActionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class DynamicActionPresenter extends BasePresenterImpl<DynamicActionContract.View> implements DynamicActionContract.Presenter {
    @Override
    public void getDynamicData(int type) {
        if (type == 0) {
            mView.setDynamicData(null);
        } else {
            List<DynamicActionModel> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DynamicActionModel dynamicActionModel = new DynamicActionModel();
                dynamicActionModel.setActionId(i);
                dynamicActionModel.setActionName("动作舞蹈" + i);
                dynamicActionModel.setActionTime(3000);
                list.add(dynamicActionModel);
            }
            mView.setDynamicData(list);
        }
    }
}
