package com.ubt.factorytest.bluetooth;

import com.ubt.factorytest.BasePresenter;
import com.ubt.factorytest.BaseView;
import com.ubt.factorytest.bluetooth.recycleview.BTBean;

import java.util.List;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/21 09:46
 * @描述:
 */

public class BluetoothContract {
    interface Presenter extends BasePresenter {
        void reInitBT();
        void initBTListener();
        boolean bStartBTScan();
        boolean bCancelBTScan();
        boolean isBTScaning();
        List<BTBean> getBTList();
        void cleanBTList();
        void connectBT(String mac);
        boolean isBTEnabled();
        boolean openBluetooth();


    }

    interface View extends BaseView<BluetoothContract.Presenter> {
        void setProcessBarOn(final boolean isOn);
        void setScanMenuTitle(String title);
        void notifyDataSetChanged();

        void btConnectting();
        void isBTConnectOK(boolean isOK);
    }
}
