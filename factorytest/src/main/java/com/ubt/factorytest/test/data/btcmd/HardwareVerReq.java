package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/22 10:38
 * @描述:
 */

public class HardwareVerReq extends BaseBTReq{

    byte    cmd = HARDWARE_VER;
    byte[] parm = {0x00};

    public HardwareVerReq(){
        initReq(cmd, parm);
    }
}
