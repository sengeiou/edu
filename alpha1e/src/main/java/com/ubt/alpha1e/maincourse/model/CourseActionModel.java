package com.ubt.alpha1e.maincourse.model;

/**
 * @author：liuhai
 * @date：2017/12/15 10:11
 * @modifier：ubt
 * @modify_date：2017/12/15 10:11
 * [A brief description]
 * version
 */

public class CourseActionModel {

    private String courseName;
    private int statu;

    public CourseActionModel(String courseName, int statu) {
        this.courseName = courseName;
        this.statu = statu;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }

    @Override
    public String toString() {
        return "CourseActionModel{" +
                "courseName='" + courseName + '\'' +
                ", statu=" + statu +
                '}';
    }
}
