package com.ubt.alpha1e.blockly;

/**
 * @author wmma
 * @className LedColorEnum
 * @description  led color enum
 * @date 2017/5/4
 * @update
 *
 *  ['red','orange','yellow','green','cyan','blue','purple','white'];
 *  RGB:红（R：255 G：0 B：0）橙（R：255 G：156 B：0）黄（R：255 G：255 B：0）绿（R：0 G：255 B：0）
 *  青（R： G：255 B：255）蓝（R：0 G：0 B：255）紫（R：255 G： B：255
 */


public enum LedColorEnum {

    RED ("red", 255, 0, 0),
    ORANGE ("orange", 255, 156, 0),
    YELLOW ("yellow", 255, 255, 0),
    GREEN ("green", 0, 255, 0),
    CYAN ("cyan", 0, 255, 255),
    BLUE ("blue", 0 ,0, 255),
    PURPLE ("purple", 255, 0, 255),
    WHITE ("white", 255, 255, 255);


    private String color;
    private int r;
    private int g;
    private int b;

    LedColorEnum(String color, int r, int g, int b){
        this.color = color;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public String getColor(){
        return  color;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }




}
