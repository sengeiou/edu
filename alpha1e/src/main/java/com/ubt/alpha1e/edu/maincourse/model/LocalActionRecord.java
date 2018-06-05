package com.ubt.alpha1e.edu.maincourse.model;

import org.litepal.crud.DataSupport;

/**
 * @author：liuhai
 * @date：2017/11/20 10:59
 * @modifier：ubt
 * @modify_date：2017/11/20 10:59
 * [A brief description]
 * 本地保存课时记录
 */

public class LocalActionRecord extends DataSupport {
    private String userId;
    /**
     * 第几关
     */
    private int CourseLevel;
    /**
     * 第几课时
     */
    private int periodLevel;

    /**
     * 是否上传数据
     */
    private boolean isUpload;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCourseLevel() {
        return CourseLevel;
    }

    public void setCourseLevel(int courseLevel) {
        CourseLevel = courseLevel;
    }

    public int getPeriodLevel() {
        return periodLevel;
    }

    public void setPeriodLevel(int periodLevel) {
        this.periodLevel = periodLevel;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    @Override
    public String toString() {
        return "LocalActionRecord{" +
                "userId='" + userId + '\'' +
                ", CourseLevel=" + CourseLevel +
                ", periodLevel=" + periodLevel +
                ", isUpload=" + isUpload +
                '}';
    }
}
