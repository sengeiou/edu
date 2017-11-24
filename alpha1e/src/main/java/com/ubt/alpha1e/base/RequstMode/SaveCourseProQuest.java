package com.ubt.alpha1e.base.RequstMode;

/**
 * @author：liuhai
 * @date：2017/11/21 19:10
 * @modifier：ubt
 * @modify_date：2017/11/21 19:10
 * [A brief description]
 * version
 */

public class SaveCourseProQuest extends BaseRequest {
    private String courseOne;//表示第一层关卡  1
    private String progressOne;//第一层进度 第几个关卡 2
    private String courseTwo; //表示第二层 2
    private String progressTwo; //第几个课时
    private int type;

    public String getCourseOne() {
        return courseOne;
    }

    public void setCourseOne(String courseOne) {
        this.courseOne = courseOne;
    }

    public String getProgressOne() {
        return progressOne;
    }

    public void setProgressOne(String progressOne) {
        this.progressOne = progressOne;
    }

    public String getCourseTwo() {
        return courseTwo;
    }

    public void setCourseTwo(String courseTwo) {
        this.courseTwo = courseTwo;
    }

    public String getProgressTwo() {
        return progressTwo;
    }

    public void setProgressTwo(String progressTwo) {
        this.progressTwo = progressTwo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SaveCourseProQuest{" +
                "courseOne='" + courseOne + '\'' +
                ", progressOne='" + progressOne + '\'' +
                ", courseTwo='" + courseTwo + '\'' +
                ", progressTwo='" + progressTwo + '\'' +
                ", type=" + type +
                '}';
    }
}
