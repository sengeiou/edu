package com.ubt.factorytest.bluetooth.bluetoothLib.base;

import android.bluetooth.BluetoothDevice;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/21 11:24
 * @描述:
 */

public abstract class BluetoothListenerAdapter implements BluetoothListener{

    @Override
    public void onReadData(BluetoothDevice device, byte[] data) {

    }

    @Override
    public void onActionStateChanged(int preState, int state) {

    }

    @Override
    public void onActionDiscoveryStateChanged(String discoveryState) {

    }

    @Override
    public void onActionScanModeChanged(int preScanMode, int scanMode) {

    }

    @Override
    public void onBluetoothServiceStateChanged(int state) {

    }

    @Override
    public void onActionDeviceFound(BluetoothDevice device, short rssi) {

    }
}
