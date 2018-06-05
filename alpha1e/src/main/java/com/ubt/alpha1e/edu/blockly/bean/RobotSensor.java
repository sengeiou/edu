package com.ubt.alpha1e.edu.blockly.bean;

import android.support.annotation.NonNull;

/**
 * @className RobotSensor
 *
 * @author wmma
 * @description 传感器实例
 * @date 2017/4/19
 * @update
 */


public class RobotSensor {

    private SensorType mSensorType;
    private int mSensorId;
    private int mSensorEvent;

    public RobotSensor(SensorType mSensorType, int mSensorId, int mSensorEvent){
        this.mSensorType = mSensorType;
        this.mSensorId = mSensorId;
        this.mSensorEvent = mSensorEvent;
    }

    public SensorType getSensorType() {
        return mSensorType;
    }

    public void setSensorType(SensorType mSensorType) {
        this.mSensorType = mSensorType;
    }

    public int getSensorId() {
        return mSensorId;
    }

    public void setSensorId(int mSensorId) {
        this.mSensorId = mSensorId;
    }

    public int getSensorEvent() {
        return mSensorEvent;
    }

    public void setSensorEvent(int mSensorEvent) {
        this.mSensorEvent = mSensorEvent;
    }



    /**
     * 传感器类型枚举：手机、触摸、红外、陀螺仪、灯光、重力加速度、语音、机器人状态
     */

    public enum SensorType{
        PHONE("phone"),
        TOUCH("touch"),
        INFRARED("infrared"),
        GYROSCOPE("gyroscope"),
        LIGHT("light"),
        GRAVITY("gravity"),
        SPEECH("speech"),
        ROBOT("robot"),
        ACCELERATION("addSpeed")
        ;


        private String name;
        private SensorType(String name){
            this.name = name;
        }

        public String getName(){
            return this.name;
        }

        public boolean sameSensor(@NonNull String name){
            String lowerCase = this.name.toLowerCase();
            return lowerCase.equals(name.toLowerCase());
        }
    }




}
