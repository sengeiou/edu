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

    public static final String BASIC_UBX_SYS = "http://10.10.1.14:8080/ubx/sys/";
    public static final String REQUEST_SMS_CODE = BASIC_UBX_SYS + "register";
    public static final String GET_USER_INFO = BASIC_UBX_SYS + "getUserInfo";
    public static final String BIND_ACCOUNT = BASIC_UBX_SYS + "bind";
    public static final String UPDATE_USERINFO = BASIC_UBX_SYS + "updateUserInfo";
    public static final String MODIFY_MANAGE_PASSWORD = BASIC_UBX_SYS + "updateUserPwd";
    public static final String SET_USER_PASSWORD = BASIC_UBX_SYS + "addUserPwd";
    public static final String VERIDATA_CODE = BASIC_UBX_SYS + "validateCode";
    public static final String ADD_FEEDBACK = BASIC_UBX_SYS + "addFeedback";
    public static final String CHECK_APP_UPDATE = BASIC_UBX_SYS + "updateApp";

}
