package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/14 14:29
 * @描述:
 */

public class VolumeAdjust extends BaseBTReq{
    byte    cmd = VOL_ADJUST;
    byte[] parm = {0x7f};

    public VolumeAdjust(byte bparm){
        parm[0] = bparm;
        initReq(cmd, parm);
    }
}
