package com.ubt.alpha1e.login;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class HttpEntity {

    public static final String HIBITS_STATISTICS = "http://10.10.1.14:8080/alpha1e";

    /**
     * 信鸽获取
     */
    public static final String XG_URL = "https://test79.ubtrobot.com/xinge-push-rest/";
    /**
     * 获取XG AppaccessId AppaccessKey
     */
    public static final String getXGAppId = XG_URL + "push/appInfo";

    public static final String bindXGServer = XG_URL + "push/userToken";
    public static final String unbindXGServer=XG_URL+"push/unbindToken";




    //http://10.10.32.52:8080/ubx/sys/register  http://10.10.20.71:8010
    public static final String BASIC_THIRD_LOGIN_URL = "http://10.10.20.71:8010/user-service-rest/v2/"; //测试环境后续上线需要修改正式环境
    public static final String THRID_LOGIN_URL = BASIC_THIRD_LOGIN_URL + "user/login/third";

    //  http://10.10.1.14:8080
//    public static final String BASIC_UBX_SYS = "http://10.10.1.14:8080/ubx/sys/"; //测试环境
    public static final String BASIC_UBX_SYS = "http://10.10.1.14:8080/alpha1e/"; //测试环境

    public static final String REQUEST_SMS_CODE = BASIC_UBX_SYS + "user/register";
    public static final String GET_USER_INFO = BASIC_UBX_SYS + "user/get";
    public static final String BIND_ACCOUNT = BASIC_UBX_SYS + "user/bind";
    public static final String UPDATE_USERINFO = BASIC_UBX_SYS + "user/update";
    public static final String MODIFY_MANAGE_PASSWORD = BASIC_UBX_SYS + "user/updateUserPwd";
    public static final String SET_USER_PASSWORD = BASIC_UBX_SYS + "user/addUserPwd";
    public static final String VERIDATA_CODE = BASIC_UBX_SYS + "user/validateCode";
    public static final String ADD_FEEDBACK = BASIC_UBX_SYS + "user/addFeedback";
    public static final String CHECK_APP_UPDATE = BASIC_UBX_SYS + "sys/appUpgrade";
    public static final String SAVE_COURSE_PROGRESS = BASIC_UBX_SYS + "course/addCourseProgress";
    public static final String GET_COURSE_PROGRESS = BASIC_UBX_SYS + "course/getCourseProgress";


    public static final String BASIC_UBX_SYS_BIND = "http://10.10.1.12:8085/equipment/"; //绑定相关 测试环境
    //查询绑定关系
    public static final String CHECK_IS_BIND = BASIC_UBX_SYS_BIND + "relation/check";

    //绑定
    public static final String ROBOT_BIND = BASIC_UBX_SYS_BIND + "relation/bind";

    //解绑
    public static final String ROBOT_UNBIND = BASIC_UBX_SYS_BIND + "relation/unbind";

    //查询本账户绑定的机器人信息
    public static final String CHECK_ROBOT_INFO = BASIC_UBX_SYS_BIND + "relation/getEquipmentInfo";

    //更新机器人自动升级状态
    public static final String UPDATE_AUTO_UPGRADE = BASIC_UBX_SYS_BIND + "relation/updateAutoUpgrade";



    public static final String COURSE_SAVE_STATU = BASIC_UBX_SYS + "course/addCourseStatus";

    public static final String COURSE_GET_STATU = BASIC_UBX_SYS + "course/getCourseStatus";

    /**
     * 获取消息列表
     */
    public static final String MESSAGE_GET_LIST = BASIC_UBX_SYS + "message/listByPage";
    /**
     * 获取未读消息数量
     */
    public static final String MESSAGE_UNREAD_TOTAL = BASIC_UBX_SYS + "message/countUnread";

    /**
     * 获取消息列表
     */
    public static final String MESSAGE_UPDATE_STATU = BASIC_UBX_SYS + "message/update";

    /**
     * 删除消息
     */
    public static final String MESSAGE_DELETE = BASIC_UBX_SYS + "message/deleteByMessageId";
    /**
     * 获取原创列表
     */
    public static final String ACTION_DYNAMIC_LIST = BASIC_UBX_SYS + "original/listByPage";
    /**
     * 删除动作ByID
     */
    public static final String ACTION_DYNAMIC_DELETE = BASIC_UBX_SYS + "original/deleteByActionId";
    /**
     * 保存动作
     */
    public static final String SAVE_ACTION = BASIC_UBX_SYS + "original/upload";


    /**
     * 保存blockly编程项目
     */
    public static final String SAVE_USER_PROGRAM = BASIC_UBX_SYS + "program/add";

    /**
     * 获取用户的Blockly编程项目
     */
    public static final String GET_USER_PROGRAM = BASIC_UBX_SYS + "program/list";

    /**
     * 获取Blockly编程项目详情
     */
    public static final String GET_USER_PROGRAM_DETAIL = BASIC_UBX_SYS + "program/get";

    /**
     * 删除Blockly编程项目
     */
    public static final String DEL_USER_PROGRAM = BASIC_UBX_SYS + "program/delete";

    /**
     * 修改blockly编程项目
     */
    public static final String UPDATE_USER_PROGRAM = BASIC_UBX_SYS + "program/update";


//    public static final String BLOCKLY_COURSE_BASE = "http://10.10.1.14:8080/alpha1e/graph/";

    /**
     * 获取逻辑编程课程列表
     */
    public static final String BLOCKLY_COURSE_LIST = BASIC_UBX_SYS + "graph/list";

    /**
     *更新Blockly 课程当前进度
     */
    public static final String UPDATE_BLOCKLY_COURSE = BASIC_UBX_SYS + "graph/update";


}
