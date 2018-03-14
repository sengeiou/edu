package com.ubt.alpha1e.event;

import android.bluetooth.BluetoothDevice;

import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.data.model.UpgradeProgressInfo;

/**
 * @className RobotEvent
 *
 * @author wmma
 * @description 机器人相关事件
 * @date 2017/4/12
 * @update
 */


public class RobotEvent {

    public int power = 0;
    private Event event;

    private NetworkInfo mNetworkInfo;

    private int mAutoUpgradeStatus;

    private UpgradeProgressInfo mUpgradeProgressInfo;

    private BluetoothDevice mBluetoothDevice;

    private short rssi;

    private boolean hibitsProcessStatus = true;

    public RobotEvent(int power) {
        this.power = power;
    }

    public RobotEvent(Event event) {
        this.event = event;
    }

    public enum Event{
        CHARGING,
        UPDATING_POWER,
        DISCONNECT,
        CONNECT_SUCCESS,
        NETWORK_STATUS,
        AUTO_UPGRADE_STATUS,
        SET_AUTO_UPGRADE_STATUS,
        UPGRADE_PROGRESS,
        SCAN_ROBOT,
        SCAN_ROBOT_FINISH,
        BLUETOOTH_TURN_ON,
        BLUETOOTH_SEND_CLIENTID_SUCCESS,
        BLUETOOTH_GET_ROBOT_SN_SUCCESSS,
        BLUETOOTH_GET_ROBOT_UPGRADE,
        ENTER_CONNECT_DEVICE,
        HIBITS_PROCESS_STATUS
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public NetworkInfo getNetworkInfo() {
        return mNetworkInfo;
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        this.mNetworkInfo = networkInfo;
    }

    public int getAutoUpgradeStatus() {
        return mAutoUpgradeStatus;
    }

    public void setAutoUpgradeStatus(int autoUpgradeStatus) {
        this.mAutoUpgradeStatus = autoUpgradeStatus;
    }

    public UpgradeProgressInfo getUpgradeProgressInfo() {
        return mUpgradeProgressInfo;
    }

    public void setUpgradeProgressInfo(UpgradeProgressInfo upgradeProgressInfo) {
        this.mUpgradeProgressInfo = upgradeProgressInfo;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.mBluetoothDevice = bluetoothDevice;
    }

    public short getRssi() {
        return rssi;
    }

    public void setRssi(short rssi) {
        this.rssi = rssi;
    }

    public boolean isHibitsProcessStatus() {
        return hibitsProcessStatus;
    }

    public void setHibitsProcessStatus(boolean hibitsProcessStatus) {
        this.hibitsProcessStatus = hibitsProcessStatus;
    }
}
