package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/23 15:35
 * @描述:
 */

public class TestLed extends BaseBTReq{

    byte    cmd = TEST_LED;
    byte[] parm = {};

    public TestLed(){
        initReq(cmd, parm);
    }
}
