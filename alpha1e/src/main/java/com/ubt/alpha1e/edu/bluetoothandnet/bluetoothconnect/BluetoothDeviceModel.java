package com.ubt.alpha1e.edu.bluetoothandnet.bluetoothconnect;
/**
 * 蓝牙设备连接状态
 * @author：dicy.cheng
 * @date：2017/11/3
 * [A brief description]
 */

public class BluetoothDeviceModel {
    private String deviceName;
    private boolean isConnecting; //是否正在连接

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    public void setConnecting(boolean connecting) {
        isConnecting = connecting;
    }
}
