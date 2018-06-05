package com.ubt.alpha1e.edu.base.RequstMode;

/**
 * @author：liuhai
 * @date：2017/11/21 19:21
 * @modifier：ubt
 * @modify_date：2017/11/21 19:21
 * [A brief description]
 * version"type":"2",
"status":"1",
"course":"33"

 */

public class SaveCourseStatuRequest extends BaseRequest{
    private int type;
    private String status;
    private String course;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
