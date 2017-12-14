package com.ubt.factorytest.test;

import com.ubt.factorytest.BasePresenter;
import com.ubt.factorytest.BaseView;
import com.ubt.factorytest.test.recycleview.TestClickEntity;

import java.util.List;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/16 17:57
 * @描述:
 */

public class TestContract {
    public static final int ADJUST_SUB = 0;
    public static final int ADJUST_ADD = 1;

    interface Presenter extends BasePresenter {
        List<TestClickEntity> getTestInitData();
        List<TestClickEntity> getDataCache();
        void setDataResult(int poisition, String result);
        void setDataisPass(int poisition, boolean isPass);
        void initBTListener();
        void reInitBT();
        void disconnectBT();
        void startTest(TestClickEntity item);
        void saveBTMac(String mac);
        void setBTRSSI(String rssi);
        void stopRobotRecord();
        void adjustVolume(int type);
        void startAgeing();
    }

    interface View extends BaseView<Presenter> {
        void notifyDataSetChanged();
        void notifyItemChanged(final int position);
        void btDisconnected();
        void showToast(final String msg);
        void startWifiConfig();
        void startActionTest();
    }
}
