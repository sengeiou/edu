package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/23 15:45
 * @描述:
 */

public class PirTest extends BaseBTReq{
    public static final byte START_PIR = 0x01;
    public static final byte STOP_PIR = 0x00;
    byte    cmd = PIR_TEST;
    byte[] parm = {0x00};

    public PirTest(byte bparm) {
        parm[0] = bparm;
        initReq(cmd, parm);
    }
}
