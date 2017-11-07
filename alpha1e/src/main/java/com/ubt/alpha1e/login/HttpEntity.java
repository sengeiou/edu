package com.ubt.alpha1e.login;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class HttpEntity {

    public static final String BASIC_LOGIN_URL = "http://10.10.20.71:8010/user-service-rest/v2/";
    public static final String THRID_LOGIN_URL = BASIC_LOGIN_URL + "user/login/third";
    public static final String REQUEST_SMS_CODE = BASIC_LOGIN_URL + "user/captcha";
    public static final String BIND_ACCOUNT = BASIC_LOGIN_URL + "user/account/bind";


}
