package com.ubt.alpha1e_edu.maincourse.model;

import java.util.List;

/**
 * @author：liuhai
 * @date：2017/11/17 16:22
 * @modifier：ubt
 * @modify_date：2017/11/17 16:22
 * [A brief description]
 * 第一关卡课时类
 */

public class ActionCourseOneContent {
    /**
     * 课时长度
     */
    private int size;
    /**
     * 课时名称
     */
    private String courseName;
    /**
     * 第几课时
     */
    private int index;

    private List<CourseOne1Content> mList;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<CourseOne1Content> getList() {
        return mList;
    }

    public void setList(List<CourseOne1Content> list) {
        mList = list;
    }

    @Override
    public String toString() {
        return "ActionCourseOneContent{" +
                "size=" + size +
                ", courseName='" + courseName + '\'' +
                ", index=" + index +
                ", mList=" + mList +
                '}';
    }
}
