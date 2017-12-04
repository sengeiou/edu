package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/23 15:13
 * @描述:
 */

public class StartTimeReq extends BaseBTReq{
    byte    cmd = START_TIME;
    byte[] parm = {};

    public StartTimeReq(){
        initReq(cmd, parm);
    }
}
