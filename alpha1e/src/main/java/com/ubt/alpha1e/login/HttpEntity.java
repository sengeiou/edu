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
    public static final String getXGAppId = XG_URL + "push/appInfo";

    public static final String bindXGServer = XG_URL + "push/userToken";




    //http://10.10.32.52:8080/ubx/sys/register  http://10.10.20.71:8010
    public static final String BASIC_THIRD_LOGIN_URL = "http://10.10.20.71:8010/user-service-rest/v2/"; //测试环境后续上线需要修改正式环境
    public static final String THRID_LOGIN_URL = BASIC_THIRD_LOGIN_URL + "user/login/third";

    //  http://10.10.1.14:8080
    public static final String BASIC_UBX_SYS = "http://10.10.1.14:8080/ubx/sys/"; //测试环境
    public static final String REQUEST_SMS_CODE = BASIC_UBX_SYS + "register";
    public static final String GET_USER_INFO = BASIC_UBX_SYS + "getUserInfo";
    public static final String BIND_ACCOUNT = BASIC_UBX_SYS + "bind";
    public static final String UPDATE_USERINFO = BASIC_UBX_SYS + "updateUserInfo";
    public static final String MODIFY_MANAGE_PASSWORD = BASIC_UBX_SYS + "updateUserPwd";
    public static final String SET_USER_PASSWORD = BASIC_UBX_SYS + "addUserPwd";
    public static final String VERIDATA_CODE = BASIC_UBX_SYS + "validateCode";
    public static final String ADD_FEEDBACK = BASIC_UBX_SYS + "addFeedback";
    public static final String CHECK_APP_UPDATE = BASIC_UBX_SYS + "updateApp";
    public static final String SAVE_COURSE_PROGRESS = BASIC_UBX_SYS + "saveCourseProgress";
    public static final String GET_COURSE_PROGRESS = BASIC_UBX_SYS + "getCourseProgress";


    public static final String COURSE_GET_PROGRESS = BASIC_UBX_SYS + "getCourseProgress";


    public static final String COURSE_SAVE_PROGRESS = BASIC_UBX_SYS + "saveCourseProgress";

    public static final String COURSE_SAVE_STATU = BASIC_UBX_SYS + "saveCourseStatus";

    public static final String COURSE_GET_STATU = BASIC_UBX_SYS + "getCourseStatus";

    /**
     * 保存blockly编程项目
     */
    public static final String SAVE_USER_PROGRAM = BASIC_UBX_SYS + "saveUserProgram";

    /**
     * 获取用户的Blockly编程项目
     */
    public static final String GET_USER_PROGRAM = BASIC_UBX_SYS + "listUserProgram";

    /**
     * 获取Blockly编程项目详情
     */
    public static final String GET_USER_PROGRAM_DETAIL = BASIC_UBX_SYS + "getUserProgram";

    /**
     * 删除Blockly编程项目
     */
    public static final String DEL_USER_PROGRAM = BASIC_UBX_SYS + "deleteUserProgram";

    /**
     * 修改blockly编程项目
     */
    public static final String UPDATE_USER_PROGRAM = BASIC_UBX_SYS + "updateUserProgram";


}
