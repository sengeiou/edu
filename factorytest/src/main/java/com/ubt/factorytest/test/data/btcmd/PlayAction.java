package com.ubt.factorytest.test.data.btcmd;

import java.io.UnsupportedEncodingException;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/14 17:32
 * @描述:
 */

public class PlayAction extends BaseBTReq{

    public static final byte START_PIR = 0x01;
    byte    cmd = PLAY_ACTION;
    byte[] parm = {0x00};

    public PlayAction(String actionName){
        try {
            initReq(cmd, actionName.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
