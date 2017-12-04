package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/23 15:52
 * @描述:
 */

public class GsensirTest extends BaseBTReq{
    public static final byte START_GSENSIR = 0x01;
    public static final byte STOP_GSENSIR = 0x00;
    byte    cmd = GSENSIR_TEST;
    byte[] parm = {0x00};

    public GsensirTest(byte bparm) {
        parm[0] = bparm;
        initReq(cmd, parm);
    }
}
