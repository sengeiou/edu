package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/24 11:11
 * @描述:
 */

public class MicTestReq extends BaseBTReq{
    public static final byte START_RECORDER = 0x01;
    public static final byte STOP_RECORDER = 0x00;
    byte    cmd = MIC_TEST;
    byte[] parm = {0x00};

    public MicTestReq(byte bparm) {
        parm[0] = bparm;
        initReq(cmd, parm);
    }
}
