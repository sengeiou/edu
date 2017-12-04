package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/24 11:09
 * @描述:
 */

public class SpeakerTestReq extends BaseBTReq{
    byte    cmd = SPEAKER_TEST;
    byte[] parm = {};

    public SpeakerTestReq(){
        initReq(cmd, parm);
    }
}
