package com.ubt.alpha1e.edu.blockly.bean;

import java.util.List;


public class QueryResult {

    private List<RobotSensor> phone;
    private List<RobotSensor> Infrared;
    private List<RobotSensor> Touch;
    private List<RobotSensor> Gyro;
    private List<RobotSensor> Light;
    private List<RobotSensor> Gravity;
    private List<RobotSensor> Robot;
    private List<RobotSensor> Acceleration;

    public void setPhone(List<RobotSensor> Phone){
        this.phone = Phone;
    }

    public List<RobotSensor> getPhone(){
        return this.phone;
    }


    public List<RobotSensor> getGravity() {
        return Gravity;
    }

    public List<RobotSensor> getGyro() {
        return Gyro;
    }

    public void setInfrared(List<RobotSensor> Infrared){
        this.Infrared = Infrared;
    }

    public List<RobotSensor> getInfrared() {
        return Infrared;
    }

    public List<RobotSensor> getLight() {
        return Light;
    }

    public List<RobotSensor> getTouch() {
        return Touch;
    }

    public void setRobot(List<RobotSensor> Robot) {
        this.Robot = Robot;
    }

    public List<RobotSensor> getRobot() {
        return Robot;
    }

    public void setAcceleration(List<RobotSensor> acceleration) {
        this.Acceleration = acceleration;
    }

    public List<RobotSensor> getAcceleration() {
        return Acceleration;
    }

}
