package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/21 18:08
 * @描述:
 */

public class SoftVersionReq extends BaseBTReq{

    byte    cmd = SOFT_VERSION;
    byte[] parm = {0x00};

    public SoftVersionReq() {
        initReq(cmd, parm);
    }
}
