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
            mView.setDynamicData(true, type, getData());
        } else {
            List<DynamicActionModel> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DynamicActionModel dynamicActionModel = new DynamicActionModel();
                dynamicActionModel.setActionId(14456);
                dynamicActionModel.setActionName("%蚂蚁与鸽子");
                dynamicActionModel.setActionTime(3000);
                list.add(dynamicActionModel);
            }
            mView.setDynamicData(true, type, list);
        }
    }

    private List<DynamicActionModel> getData() {
        List<DynamicActionModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DynamicActionModel dynamicActionModel = new DynamicActionModel();
            dynamicActionModel.setActionId(14456);
            dynamicActionModel.setActionName("%蚂蚁与鸽子");
            dynamicActionModel.setActionTime(3000);
            list.add(dynamicActionModel);
        }
        return list;
    }


    /**
     * 根据ID获取位置
     *
     * @param id
     * @param list
     * @return
     */
    public int getPositionById(long id, List<DynamicActionModel> list) {
        int n = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getActionId() == id) {
                n = i;
                break;
            }
        }
        return n;
    }
}
