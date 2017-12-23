package com.ubt.factorytest.test.data.btcmd;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/15 10:16
 * @描述:
 */

public class ConnectWifi extends BaseBTReq{
    byte    cmd = CONNECT_WIFI;
    byte[] parm = {0x00};

    public ConnectWifi(String wifiName, String wifiPwd){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ESSID", wifiName);
            jsonObject.put("PassKey",wifiPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        parm = jsonObject.toString().getBytes();
        initReq(cmd, parm);
    }
}
