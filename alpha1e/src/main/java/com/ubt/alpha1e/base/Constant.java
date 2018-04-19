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
    public static final String SP_SHOW_COMMON_GUIDE = "sp_show_common_guide";
    public static final String SP_SHOW_MAINUI_GUIDE="sp_show_mainui_guide";
    public static final String SP_SHOW_SERVO_GUIDE="sp_show_servo_guide";

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


    public final static String SP_RECENT_SEARCH_KEY = "recentSearchKey";

    /**
     * 信鸽信息
     */
    public final static String SP_XG_ACCESSID ="accessid";
    public final static String SP_XG_ACCESSKEY ="accessKey";
    public final static String SP_XG_USERID="userId";

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
     * token异常处理响应码
     */
    public final static int INVALID_TOKEN = 88;

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

    /**
     * 是否要弹出绑定对话
     */
    public final static String IS_TOAST_BINDED = "is_toast_binded";

    /**
     * 上传服务器失败时保存当前的课程进度
     */
    public final static String SP_CURRENT_BLOCK_COURSE_ID = "currentCourseId";


    public final static int cartoon_action_swing_right_leg = 0;
    public final static int cartoon_action_swing_left_leg = 1;
    public final static int cartoon_action_swing_right_hand = 2;
    public final static int cartoon_action_swing_left_hand = 3;
    public final static int cartoon_action_hand_stand = 4;
    public final static int cartoon_action_hand_stand_reverse = 5;
    public final static int cartoon_action_squat = 6;
    public final static int cartoon_action_enjoy = 7;
    public final static int cartoon_action_fall = 8;
    public final static int cartoon_action_greeting = 9;
    public final static int cartoon_action_shiver = 10;
    public final static int cartoon_action_sleep = 11;
    public final static int cartoon_action_smile = 12;
    public final static int cartoon_aciton_squat_reverse = 13;

    public final static int ROBOT_HEAD_UP_STAND=1;
    public final static int ROBOT_HEAD_DOWN=2;
    public final static int ROBOT_LEFT_SHOULDER_SLEEP=3;
    public final static int ROBOT_RIGHT_SHOULDER_SLEEP=4;
    public final static int ROBOT_HEAD_UP_SLEEP=5;
    public final static int ROBOT_HEAD_DOWN_SLEEP=6;
    public final static int CARTOON_FRAME_INTERVAL=4;

    public final static byte APP_LAUNCH_STATUS=0x01;
    public final static byte APP_BLUETOOTH_CONNECTED =0x02;
    public final static byte APP_BLUETOOTH_CLOSE=0x03;
    public final static byte ROBOT_LOW_POWER_LESS_TWENTY_STATUS =0x04;
    public final static byte ROBOT_LOW_POWER_LESS_FIVE_STATUS =0x05;
    public final static byte ROBOT_SLEEP_EVENT=0x06;
    public final static byte ROBOT_HIT_HEAD=0x07;
    public final static byte ROBOT_WAKEUP_ACTION =0x08;
    public final static byte ROBOT_POWEROFF=0x09;
    public final static byte ROBOT_hand_stand =0x0a;
    public final static byte ROBOT_fall =0x0b;
    public final static byte ROBOT_CHARGING=0x0c;
    public final static byte ROBOT_UNCHARGING=0x0d;
    public final static byte ROBOT_CHARGING_ENOUGH=0x0e;
    public final static byte ROBOT_POWER_CAPACITY=0x0f;
    public final static byte ROBOT_default_gesture =0x10;
    public final static String WakeUpActionName="初始化";
    public final static int BUDDLE_LOW_BATTERY_TEXT=0;
    public final static int BUDDLE_RANDOM_TEXT=1;
    public final static int BUDDLE_INIT_TEXT=2;
    /**
     * 机器人productId 和DSN
     */
    public final static String SP_ROBOT_PRODUCT_ID = "sp_robot_product_id";
    public final static String SP_ROBOT_DSN = "sp_robot_dsn";


}
