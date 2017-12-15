package com.ubt.factorytest.test.data.btcmd;

import com.ubt.factorytest.bluetooth.ubtbtprotocol.UbtBTProtocol;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/21 18:03
 * @描述:
 */

public class BaseBTReq {

    public static final byte HEART_CMD = 0x08;  //心跳命令
    public static final byte START_TIME = (byte) 0xE3;  //获取开机时间
    public static final byte SOFT_VERSION = 0x11;   //获取软件版本
    public static final byte HARDWARE_VER = 0x20;   //获取硬件版本
    public static final byte ELECTRICCHARGE = 0x18;   //获取当前电量
    public static final byte TEST_LED = (byte) 0xE4;  //进入LED测试
    public static final byte RESET_ROBOT = (byte) 0xE5;  //机器人复位
    public static final byte PIR_TEST = 0x50;           //红外测试
    public static final byte GSENSIR_TEST = 0x52;       //陀螺仪测试
    public static final byte PRESSHEAD_UP = 0x70;       //拍头事件上报
    public static final byte WAKEUP_UP = 0x78;       //唤醒事件上报
    public static final byte INTO_FACTORY_TEST = (byte) 0xE2;       //进入工厂测试模式
    public static final byte SPEAKER_TEST = (byte) 0xE7;       //喇叭测试
    public static final byte MIC_TEST = (byte) 0xE6;       //MIC录音测试
    public static final byte VOL_ADJUST = (byte) 0x0B;       //音量调节
    public static final byte READ_DEV_STATUS = (byte) 0x0A;       //读取设备状态
    public static final byte GET_ACTION_LIST = (byte) 0x02;       //读取设备状态
    public static final byte PLAY_ACTION = (byte) 0x03;       //执行动作
    public static final byte CONNECT_WIFI = (byte) 0x39;       //连接WIFI
    /**
     * 动作结束主动上报
     */
    public static final byte DV_ACTION_FINISH = (byte) 0x31;


    private UbtBTProtocol btcmd;

    protected void initReq(byte cmd, byte[] param){
        btcmd = new UbtBTProtocol(cmd, param);
    }

    public byte[] toByteArray(){
        return btcmd.toRawBytes();
    }

    public String toString(){
        return btcmd.toString();
    }
}
