package com.ubt.factorytest.test.data.btcmd;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/15 15:20
 * @描述:
 */

public class GetWifiStatus extends BaseBTReq{
    byte    cmd = WIFI_STATUS;
    byte[] parm = {0x00};

    public GetWifiStatus() {
        initReq(cmd, parm);
    }
}
