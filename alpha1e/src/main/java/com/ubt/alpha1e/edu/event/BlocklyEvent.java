package com.ubt.alpha1e.edu.event;

/**
 * @className BlocklyEvent
 *
 * @author wmma
 * @description Blockly相关事件event。
 * @date 2017/4/27
 * @update 修改者，修改日期，修改内容。
 */


public class BlocklyEvent<T>{

    public static final int CALL_JAVASCRIPT = 0;            //js回调
    public static final int CALL_ROBOT_POWER_DOWN = 1;      //机器人掉电
    public static final int CALL_ROBOT_READ_ANGLE = 2;      //机器人回读舵机角度
    public static final int CALL_ROBOT_PLAY_READ_ACTION = 3; //播放机器人回读动作
    public static final int CALL_START_INFRARED = 4;        //开启红外传感器
    public static final int CALL_GET_INFRARED_DISTANCE = 5;  //获取红外返回距离
    public static final int CALL_ROBOT_FALL_DOWN_STATE = 6;  //机器人跌倒状态
    public static final int CALL_ROBOT_PLAY_SOUND_FINISH = 7; //机器人播放音效完成
    public static final int CALL_ROBOT_GET_GYROSCOPE_DATA = 8; //获取到机器人上报陀螺仪数据
    public static final int CALL_READ_WALK_FILE_FINISH = 9;
    public static final int CALL_CONNECT_SOCKET = 10; //连接socket服务端
    public static final int CALL_TAPPED_ROBOT_HEAD = 11;
    public static final int CALL_ROBOT_FALL_DOWN = 12;
    public static final int CALL_ROBOT_WALK_STOP = 13;
    public static final int CALL_6D_GESTURE = 14;
    public static final int CALL_TEMPERATURE = 15;
    public static final int CALL_ROBOT_PLAY_EMOJI_FINISH = 16;


    private int type;
    private T e;

    public BlocklyEvent(int type) {
        this.type = type;
    }

    public BlocklyEvent(int type, T e){
        this.type = type;
        this.e = e;
    }

    public int getType(){
        return type;
    }

    public T getMessage(){
        return e;
    }


}
