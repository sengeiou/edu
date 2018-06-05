package com.ubt.alpha1e_edu.userinfo.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author：liuhai
 * @date：2017/11/8 19:41
 * @modifier：ubt
 * @modify_date：2017/11/8 19:41
 * [A brief description]
 * version
 */

public class UserAllModel implements Serializable{
    private String userId;
    private String phone;
    private String nickName;
    private String sex;
    private String age;
    private String grade;
    private String headPic;
    private List<String> ageList;
    private List<String> gradeList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public List<String> getAgeList() {
        return ageList;
    }

    public void setAgeList(List<String> ageList) {
        this.ageList = ageList;
    }

    public List<String> getGradeList() {
        return gradeList;
    }

    public void setGradeList(List<String> gradeList) {
        this.gradeList = gradeList;
    }

    @Override
    public String toString() {
        return "UserAllModel{" +
                "userId='" + userId + '\'' +
                ", phone='" + phone + '\'' +
                ", nickName='" + nickName + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", grade='" + grade + '\'' +
                ", headPic='" + headPic + '\'' +
                ", ageList=" + ageList +
                ", gradeList=" + gradeList +
                '}';
    }
}
