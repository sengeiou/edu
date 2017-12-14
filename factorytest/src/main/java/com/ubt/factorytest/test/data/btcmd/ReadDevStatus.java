package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/14 14:36
 * @描述:
 */

public class ReadDevStatus extends BaseBTReq{
    byte    cmd = READ_DEV_STATUS;
    byte[] parm = {0x00};

    public ReadDevStatus(){
        initReq(cmd, parm);
    }
}
