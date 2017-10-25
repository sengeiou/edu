package com.ubt.alpha1e.event;

/**
 * 类名
 *
 * @author wmma
 * @description 实现的主要功能。
 * @date $date
 * @update 修改者，修改日期，修改内容。
 */


public class NetworkEvent {

    private Event event;
    private String selectWifiName = null;
    private int connectStatus = 0; // 0 未连接  1 连接中   2 连接成功  3连接失败
    private String mConnectWifiName;

    public NetworkEvent(Event event) {
        this.event = event;
    }

    public enum Event{
        CHANGE_SELECT_WIFI,
        DO_CONNECT_WIFI,
        NETWORK_STATUS
    }

    public String getSelectWifiName() {
        return selectWifiName;
    }

    public void setSelectWifiName(String selectWifiName) {
        this.selectWifiName = selectWifiName;
    }

    public int getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(int connectStatus) {
        this.connectStatus = connectStatus;
    }

    public String getConnectWifiName() {
        return mConnectWifiName;
    }

    public void setConnectWifiName(String connectWifiName) {
        this.mConnectWifiName = connectWifiName;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
