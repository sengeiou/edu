package com.ubt.alpha1e.login;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class HttpEntity {


    /**
     * 信鸽获取
     */
    public static final String XG_URL = "https://test79.ubtrobot.com/xinge-push-rest/";
    /**
     * 获取XG AppaccessId AppaccessKey
     */
    public static final String getXGAppId = XG_URL+"push/appInfo";

    public static final String bindXGServer = XG_URL+"push/userToken";

    //http://10.10.32.52:8080/ubx/sys/register
    public static final String BASIC_THIRD_LOGIN_URL = "http://10.10.20.71:8010/user-service-rest/v2/";
    public static final String THRID_LOGIN_URL = BASIC_THIRD_LOGIN_URL + "user/login/third";

    public static final String BASIC_UBX_SYS = "http://10.10.1.14:8080/ubx/sys/";
    public static final String REQUEST_SMS_CODE = BASIC_UBX_SYS + "register";
    public static final String GET_USER_INFO = BASIC_UBX_SYS + "getUserInfo";
    public static final String BIND_ACCOUNT = BASIC_UBX_SYS + "bind";
    public static final String UPDATE_USERINFO = BASIC_UBX_SYS + "updateUserInfo";
    public static final String MODIFY_MANAGE_PASSWORD = BASIC_UBX_SYS + "updateUserPwd";


}
