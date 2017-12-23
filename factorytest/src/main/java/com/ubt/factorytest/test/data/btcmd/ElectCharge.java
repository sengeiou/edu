package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/22 10:49
 * @描述:
 */

public class ElectCharge extends BaseBTReq {

    byte    cmd = ELECTRICCHARGE;
    byte[] parm = {0x04};

    public ElectCharge(){
        initReq(cmd, parm);
    }
}
