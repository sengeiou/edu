package com.ubt.alpha1e.Course.model;

/**
 * @author：liuhai
 * @date：2017/11/13 18:17
 * @modifier：ubt
 * @modify_date：2017/11/13 18:17
 * [A brief description]
 * version
 */

public class CourseModel {

    private String MainCourcesName;
    private int LockType;
    private int drawableId;
    private int MainCourcesScore;


    public String getMainCourcesName() {
        return MainCourcesName;
    }

    public void setMainCourcesName(String mainCourcesName) {
        MainCourcesName = mainCourcesName;
    }

    public int getLockType() {
        return LockType;
    }

    public void setLockType(int lockType) {
        LockType = lockType;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public int getMainCourcesScore() {
        return MainCourcesScore;
    }

    public void setMainCourcesScore(int mainCourcesScore) {
        MainCourcesScore = mainCourcesScore;
    }

    @Override
    public String toString() {
        return "CourseModel{" +
                "MainCourcesName='" + MainCourcesName + '\'' +
                ", LockType=" + LockType +
                ", drawableId=" + drawableId +
                ", MainCourcesScore=" + MainCourcesScore +
                '}';
    }
}
