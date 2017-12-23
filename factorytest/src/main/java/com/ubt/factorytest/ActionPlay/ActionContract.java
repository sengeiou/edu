package com.ubt.factorytest.ActionPlay;

import com.ubt.factorytest.ActionPlay.recycleview.ActionBean;
import com.ubt.factorytest.BasePresenter;
import com.ubt.factorytest.BaseView;

import java.util.List;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/14 19:16
 * @描述:
 */

public class ActionContract {
    interface Presenter extends BasePresenter {
        List<ActionBean> getActionList();
        void playAction(String actionName);
    }


    interface View extends BaseView<ActionContract.Presenter> {
        void notifyDataSetChanged();
        void btDisconnected();
    }
}
