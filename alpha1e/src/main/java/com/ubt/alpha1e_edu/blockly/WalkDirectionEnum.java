package com.ubt.alpha1e_edu.blockly;

/**
 * @ClassName DeviceDirectionEnum
 *
 * @author wmma
 * @date 2017/3/6
 * @Description: 枚举手机/平板状态
 *
 */

public enum WalkDirectionEnum {


    FORWARD("forward", (byte)0),
    BACK("back", (byte)1),
    LEFT("left", (byte)2),
    RIGHT("right", (byte)3);


    WalkDirectionEnum(String type, byte value) {
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

