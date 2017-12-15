package com.ubt.alpha1e.base;

/**
 * @author：liuhai
 * @date：2017/11/2 11:22
 * @modifier：ubt
 * @modify_date：2017/11/2 11:22
 * [A brief description]
 * version
 */

public class Constant {
    public final static String SP_USERINFOMODEL = "sp_userinfo";
    public final static String SP_LOGIN_TOKEN = "sp_login_token";

    /**
     * 保存从腾讯tvs后台获取的clientId
     */
    public final static String SP_CLIENT_ID = "sp_client_id";
    public final static String SP_LOGIN_TYPE = "sp_login_type";


    /**
     * 用户信息保存
     */
    public final static String SP_USER_INFO = "sp_user_info";
    public final static String SP_USER_ID = "sp_login_userId";
    public final static String SP_USER_IMAGE = "sp_user_image";
    public final static String SP_USER_NICKNAME = "sp_user_nickname";
    public static final String PRINCIPLE_PROGRESS = "sp_principle_progress_";
    public static final String SP_SHOW_REMOTE_PUBLISH = "sp_show_remote_publish";
    public static final String SP_AUTO_UPGRADE = "sp_auto_upgrade";
    public static final String PRINCIPLE_ENTER_PROGRESS = "sp_principle_enter_progress";

    /**
     * 首次申请权限
     */
    public final static String SP_FIRST_PERMISSION = "first_permission";

    public final static String SP_PERMISSION_LOCATION = "sp_location_permission";
    public final static String SP_PERMISSION_STORAGE = "sp_storage_permission";
    public final static String SP_PERMISSION_CAMERA = "sp_camera_permission";
    public final static String SP_PERMISSION_MICROPHONE = "sp_microphone_permission";

    public final static String SP_ACTION_COURSE_CARD_ONE = "sp_action_course_one";

    public final static String SP_ACTION_COURSE_CARD_TWO = "sp_action_course_two";

    /**
     * 信鸽信息
     */
    public final static String SP_XG_ACCESSID ="accessid";
    public final static String SP_XG_ACCESSKEY ="accessKey";

    /**
     * 首次获取蓝牙权限
     */
    public final static String SP_START_GTE_PERMISSION = "get_first_permission";


    /**
     * 昵称
     */
    public final static int KEY_NICK_NAME = 1;

    /**
     * 性别
     */
    public final static int KEY_NICK_SEX = 2;

    /**
     * 年龄
     */
    public final static int KEY_NICK_AGE = 3;

    /**
     * 年级
     */
    public final static int KEY_NICK_GRADE = 4;

    /**
     * 头像
     */
    public final static int KEY_NICK_HEAD = 5;

    /**
     * 记录动作编辑指引tag
     */
    public final static String SP_GUIDE_STEP = "sp_guide_step";


    /**
     * 动作课程播放路径
     */
    public final static  String COURSE_ACTION_PATH ="action/course/motion/";

    /**
     * Block跳转蓝牙连接页面标志
     */
    public final static String BLUETOOTH_REQUEST = "bluetooth_request";
}
