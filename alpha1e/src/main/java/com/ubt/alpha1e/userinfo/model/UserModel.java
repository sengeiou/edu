package com.ubt.alpha1e.userinfo.model;

import java.io.Serializable;

/**
 * @author：liuhai
 * @date：2017/11/1 14:00
 * @modifier：ubt
 * @modify_date：2017/11/1 14:00
 * [A brief description]
 * version
 */

public class UserModel implements Serializable{
    private String userName;
    private int sex;
    private String grade;
    private int age;
    public String userImage;
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userName='" + userName + '\'' +
                ", sex=" + sex +
                ", grade='" + grade + '\'' +
                ", age=" + age +
                ", userImage='" + userImage + '\'' +
                '}';
    }
}
