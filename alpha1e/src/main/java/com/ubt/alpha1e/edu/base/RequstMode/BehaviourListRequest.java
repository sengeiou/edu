package com.ubt.alpha1e.edu.base.RequstMode;

/**
 * @作者：ubt
 * @日期: 2017/12/19 15:41
 * @描述:
 */


public class BehaviourListRequest extends BaseRequest {
    String sex;
    String  grade;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type;

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


}
