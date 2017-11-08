package com.ubt.alpha1e.login;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class HttpEntity {

    //http://10.10.32.52:8080/ubx/sys/register
    public static final String BASIC_THIRD_LOGIN_URL = "http://10.10.20.71:8010/user-service-rest/v2/";
    public static final String THRID_LOGIN_URL = BASIC_THIRD_LOGIN_URL + "user/login/third";

    public static final String BASIC_UBX_SYS = "http://10.10.32.52:8080/ubx/sys/";
    public static final String REQUEST_SMS_CODE = BASIC_UBX_SYS + "register";
    public static final String BIND_ACCOUNT = BASIC_UBX_SYS + "bind";


}
