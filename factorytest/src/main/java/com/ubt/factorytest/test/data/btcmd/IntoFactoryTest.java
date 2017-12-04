package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/23 20:09
 * @描述:
 */

public class IntoFactoryTest extends BaseBTReq{
    public static final byte START_TEST = 0x01;
    public static final byte STOP_TEST = 0x00;
    byte    cmd = INTO_FACTORY_TEST;
    byte[] parm = {0x00};

    public IntoFactoryTest(byte bparm) {
        parm[0] = bparm;
        initReq(cmd, parm);
    }
}
