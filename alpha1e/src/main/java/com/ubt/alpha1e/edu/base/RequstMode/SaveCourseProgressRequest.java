package com.ubt.alpha1e.edu.base.RequstMode;

/**
 * @author：liuhai
 * @date：2017/11/8 14:03
 * @modifier：ubt
 * @modify_date：2017/11/8 14:03
 * [A brief description]
 * version
 */

public class SaveCourseProgressRequest extends BaseRequest {
    private int type;

    private int courseOne;

    private int progressOne;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCourseOne() {
        return courseOne;
    }

    public void setCourseOne(int courseOne) {
        this.courseOne = courseOne;
    }

    public int getProgressOne() {
        return progressOne;
    }

    public void setProgressOne(int progressOne) {
        this.progressOne = progressOne;
    }

    @Override
    public String toString() {
        return "SaveCourseProgressRequest{" +
                "type='" + type + '\'' +
                "courseOne='" + courseOne + '\'' +
                "progressOne='" + progressOne + '\'' +
                '}';
    }
}
