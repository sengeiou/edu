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
    private String userId;
    private String nickName;
    private String sex;
    private String grade;
    private String age;
    public String headPic;
    private String phone;



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userId='" + userId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", sex='" + sex + '\'' +
                ", grade='" + grade + '\'' +
                ", age='" + age + '\'' +
                ", headPic='" + headPic + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
