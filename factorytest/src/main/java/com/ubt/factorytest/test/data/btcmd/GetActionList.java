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
            parm = new byte[dir.length()+1];
            parm[0] = (byte) dir.length();
            System.arraycopy(dir.getBytes("UTF-8"), 0, parm, 1, dir.length());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        initReq(cmd, parm);
    }
}
