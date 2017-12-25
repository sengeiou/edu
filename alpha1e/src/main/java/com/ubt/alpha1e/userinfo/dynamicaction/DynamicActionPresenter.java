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
            mView.setDynamicData(true, type, list);
        }
    }

    private List<DynamicActionModel> getData() {
        List<DynamicActionModel> list = new ArrayList<>();

        DynamicActionModel dynamicActionModel1 = new DynamicActionModel();
        dynamicActionModel1.setActionId(14456);
        dynamicActionModel1.setActionName("%蚂蚁与鸽子");
        dynamicActionModel1.setDownloadUrl("https://services.ubtrobot.com/action/16/3/蚂蚁与鸽子.zip");
        dynamicActionModel1.setActionTime(3000);
        dynamicActionModel1.setDownload(false);
        list.add(dynamicActionModel1);

        DynamicActionModel dynamicActionModel2 = new DynamicActionModel();
        dynamicActionModel2.setActionId(14457);
        dynamicActionModel2.setActionName("音乐轴");
        dynamicActionModel2.setDownload(true);
        dynamicActionModel2.setActionTime(3000);
        list.add(dynamicActionModel2);

        DynamicActionModel dynamicActionModel3 = new DynamicActionModel();
        dynamicActionModel3.setActionId(14458);
        dynamicActionModel3.setActionName("动作帧");
        dynamicActionModel3.setDownload(true);
        dynamicActionModel3.setActionTime(3000);
        list.add(dynamicActionModel3);

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
