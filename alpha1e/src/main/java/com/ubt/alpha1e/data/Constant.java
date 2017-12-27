package com.ubt.alpha1e.data;

/**
 * Created by Administrator on 2017/3/9.
 */
public class Constant {

    //创建动作进入详情
    public static final int FROM_CREATE_ACTION_TO_DETAIL = 10001;
    public static final int FROM_DETAIL_PLAY_BACK_TO_CREATE = 10002;

    //指令选择请求码
    public static final int INSTRUCTION_SELECT_REQUEST_CODE = 10003;
    public static final int INSTRUCTION_SELECT_RESPONSE_CODE = 10004;

    //指令播放文本添加
    public static final int INSTRUCTION_PLAY_TEXT_REQUEST_CODE = 10005;
    public static final int INSTRUCTION_PLAY_TEXT_RESPONSE_CODE = 10006;

    //指令播放动作添加
    public static final int INSTRUCTION_PLAY_ACTION_REQUEST_CODE = 10007;
    public static final int INSTRUCTION_PLAY_ACTION_RESPONSE_CODE = 10008;

    //蓝牙连接
    public static final int BLUETOOTH_CONNECT_REQUEST_CODE = 10009;
    public static final int BLUETOOTH_CONNECT_RESPONSE_CODE = 10010;

    //用户登录
    public static final int USER_LOGIN_REQUEST_CODE = 10011;
    public static final int USER_LOGIN_RESPONSE_CODE = 10012;

    //行为习惯编辑
    public static final int HIBITS_EVENT_EDIT_REQUEST_CODE = 10013;
    public static final int HIBITS_EVENT_EDIT_RESPONSE_CODE = 10014;

    //行为习惯播放内容编辑
    public static final int PLAY_CONTENT_SELECT_REQUEST_CODE = 10015;
    public static final int PLAY_CONTENT_SELECT_RESPONSE_CODE = 10016;

    //定义字符串常量KEY
    public static final String SCREEN_ORIENTATION = "SCREEN_ORIENTATION";
    public static final String INSTRUCTION_INFO_KEY = "INSTRUCTION_INFO_KEY";
    public static final String INSTRUCTION_PLAY_TEXT = "INSTRUCTION_PLAY_TEXT";
    public static final String INSTRUCTION_SET_POSITION = "INSTRUCTION_SET_POSITION";
    public static final String INSTRUCTION_TYPE = "INSTRUCTION_TYPE";
    public static final String IS_CONNECT_TO_UPGRADE = "IS_CONNECT_TO_UPGRADE";
    public static final String FROM_ACTIVITY_NAME = "FROM_ACTIVITY_NAME";
    public static final String IS_FROM_COURSE = "IS_FROM_COURSE";
    public static final String LESSON_INFO = "LESSON_INFO";
    public static final String SHARE_IMAGE_PATH = "SHARE_IMAGE_PATH";
    public static final String SHARE_BLOCKLY_CHAllANGE = "SHARE_BLOCKLY_CHAllANGE";

    public static final String FEEDBACK_INFO_KEY = "FEEDBACK_INFO_KEY";
    public static final String HABITS_EVENT_INFO_KEY = "HABITS_EVENT_INFO_KEY";
    public static final String PLAY_CONTENT_INFO_LIST_KEY = "PLAY_CONTENT_INFO_LIST_KEY";

    public static final String WakeUpActionName="初始化";
}
