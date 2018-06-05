package com.ubt.alpha1e_edu.blockly;

/**
 * @ClassName DeviceDirectionEnum
 *
 * @author wmma
 * @date 2017/3/6
 * @Description: 枚举手机/平板状态
 *
 */

public enum WalkSpeedEnum {


    FAST("fast", (byte)3),
    MID("mid", (byte)2),
    SLOW("slow", (byte) 1);


    WalkSpeedEnum(String type, byte value) {
        this.type = type;
        this.value = value;
    }

    private String type;
    private byte value;


    public byte getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}

