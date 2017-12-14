package com.ubt.factorytest.test.data.btcmd;

import java.io.UnsupportedEncodingException;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/14 17:14
 * @描述:
 */

public class GetActionList extends BaseBTReq{
    byte    cmd = GET_ACTION_LIST;
    byte[] parm = {0x00};

    public GetActionList(String dir) {
        try {
            String actions = dir.length()+dir;
            parm[0] = (byte) dir.length();
//            System.arraycopy();
            parm = dir.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        initReq(cmd, parm);
    }
}
