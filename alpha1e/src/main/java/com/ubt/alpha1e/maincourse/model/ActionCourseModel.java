package com.ubt.alpha1e.maincourse.model;

/**
 * @author：liuhai
 * @date：2017/11/13 20:15
 * @modifier：ubt
 * @modify_date：2017/11/13 20:15
 * [A brief description]
 * version
 */

public class ActionCourseModel {
    private String ActionCourcesName;
    private int ActionLockType;
    private int drawableId;
    private int ActionCourcesScore;

    public String getActionCourcesName() {
        return ActionCourcesName;
    }

    public void setActionCourcesName(String actionCourcesName) {
        ActionCourcesName = actionCourcesName;
    }

    public int getActionLockType() {
        return ActionLockType;
    }

    public void setActionLockType(int actionLockType) {
        ActionLockType = actionLockType;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public int getActionCourcesScore() {
        return ActionCourcesScore;
    }

    public void setActionCourcesScore(int actionCourcesScore) {
        ActionCourcesScore = actionCourcesScore;
    }

    @Override
    public String toString() {
        return "ActionCourseModel{" +
                "ActionCourcesName='" + ActionCourcesName + '\'' +
                ", ActionLockType=" + ActionLockType +
                ", drawableId=" + drawableId +
                ", ActionCourcesScore=" + ActionCourcesScore +
                '}';
    }
}
