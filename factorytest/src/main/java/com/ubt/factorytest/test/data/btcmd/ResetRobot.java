package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/23 15:37
 * @描述:
 */

public class ResetRobot extends BaseBTReq{
    byte    cmd = RESET_ROBOT;
    byte[] parm = {};

    public ResetRobot() {
        initReq(cmd, parm);
    }
}
