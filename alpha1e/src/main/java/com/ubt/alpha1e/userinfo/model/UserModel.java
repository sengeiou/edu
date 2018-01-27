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

public class UserModel implements Serializable {
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

    /**
     * string转换为type
     *
     * @return
     */
    public String getGradeByType() {
        String result = "0";
        if (grade.equals("幼儿园大班")) {
            result = "0";
        } else if (grade.equals("小学一年级")) {
            result = "1";
        } else if (grade.equals("小学二年级")) {
            result = "2";
        } else if (grade.equals("小学三年级")) {
            result = "3";
        } else if (grade.equals("小学四年级")) {
            result = "4";
        } else if (grade.equals("小学五年级以上")) {
            result = "5";
        }

        return result;
    }


    /**
     * string转换为type
     *
     * @return
     */
    public String getAgeByType() {
        String result = "5";
        if (age.equals("5岁及以下")) {
            result = "5";
        } else if (age.equals("6岁")) {
            result = "6";
        } else if (age.equals("7岁")) {
            result = "7";
        } else if (age.equals("8岁")) {
            result = "8";
        } else if (age.equals("9岁")) {
            result = "9";
        } else if (age.equals("10岁及以上")) {
            result = "10";
        }

        return result;
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
