package com.ubt.alpha1e.edu.base.RequstMode;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class UpdateUserInfoRequest extends BaseRequest  {

    private String nickName;
    private String sex;
    private String grade;
    private String age;
    public String headPic;
    private String phone;

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
        return "UpdateUserInfoRequest{" +
                "nickName='" + nickName + '\'' +
                ", sex='" + sex + '\'' +
                ", grade='" + grade + '\'' +
                ", age='" + age + '\'' +
                ", headPic='" + headPic + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
