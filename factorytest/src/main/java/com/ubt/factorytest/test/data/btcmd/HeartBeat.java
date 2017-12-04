package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/23 16:20
 * @描述:
 */

public class HeartBeat extends BaseBTReq{
    byte    cmd = HEART_CMD;
    byte[] parm = {0x00};

    public HeartBeat() {
        initReq(cmd, parm);
    }
}
