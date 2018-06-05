package com.ubt.alpha1e_edu.data.model;

import com.ubt.alpha1e_edu.data.Md5;
import com.ubt.alpha1e_edu.data.TimeTools;

/**
 * Created by wilson on 2016/5/26.
 */
public class BaseRequestModel {

    public  String appType = "1";
    public  String serviceVersion = "V1.0.0.0";
    public  String requestTime =  getRequestInfo()[0];
    public  String requestKey =  getRequestInfo()[1];
    public  String token = "";
    public  String systemLanguage = "";

    public  String[] getRequestInfo() {
        String[] req = new String[2];
        req[0] = TimeTools.getTimeVal();
        req[1] = Md5.getMD5(req[0]
                + "UBTech832%1293*6", 32);
        return req;
    }
}
