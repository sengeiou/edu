package com.ubt.alpha1e.base.ResponseMode;

/**
 * @author：liuhai
 * @date：2017/11/22 9:49
 * @modifier：ubt
 * @modify_date：2017/11/22 9:49
 * [A brief description]
 * version
 */

public class CourseLastProgressModule {
    private String userId;
    private String courseOne;
    private String progressOne;
    private String courseTwo;
    private String progressTwo;
    private String type;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CourseLastProgressModule{" +
                "userId='" + userId + '\'' +
                ", courseOne='" + courseOne + '\'' +
                ", progressOne='" + progressOne + '\'' +
                ", courseTwo='" + courseTwo + '\'' +
                ", progressTwo='" + progressTwo + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
