package com.ubt.alpha1e.edu.blockly;

/**
 * @ClassName DeviceDirectionEnum
 *
 * @author wmma
 * @date 2017/3/6
 * @Description: 枚举机器人状态
 *
 *  tilt_takeUp: "被拿起",
tilt_putDown: "被放下",
tilt_kitFront: "前面受击",
tilt_kitBack: "后面受击",
tilt_kitLeft: "左面受击",
tilt_kitRight: "右面受击",
tilt_turnForward: "前倾",
tilt_turnBack: "后倾",
tilt_turnLeft: "左倾",
tilt_turnRight: "右倾",
 *
 */

public enum RobotStateEnum {

    NONE("none", 0),       //None
    TAKE_UP("takeUp", 1),       //被拿起
    PUT_DOWN("putDown", 2),     //被放下
    HIT_FRONT("hitFront", 3),           //前面受击
    HIT_BACK("hitBack", 4),       //后面受击
    HIT_LEFT("hitLeft", 5),     //左面受击
    HIT_RIGHT("hitRight", 6),         //右面受击
    TURN_FORWARD("turnForward", 7),   //前倾
    TURN_BACK("turnBack", 8),         //后倾
    TURN_LEFT("turnLeft", 9),         //左倾
    TURN_RIGHT("turnRight", 10);      //右倾




    RobotStateEnum(String value, int type) {
        this.value = value;
        this.type = type;
    }

    private String value;
    private int type;

    public String getValue() {
        return this.value;
    }

    public int getType() {
        return type;
    }
}

