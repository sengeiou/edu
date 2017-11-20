package com.ubt.alpha1e.maincourse.model;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * @author：liuhai
 * @date：2017/11/13 20:15
 * @modifier：ubt
 * @modify_date：2017/11/13 20:15
 * [A brief description]
 * version
 */

public class ActionCourseModel extends DataSupport {
    private String userId;
    private String ActionCourcesName;
    private int ActionLockType;
    private int drawableId;
    private int ActionCourcesScore;
    /**
     * 当前进行到哪个课时
     */
    private int currentCourseIndex;

    /**
     * 课时列表
     */
    private List<ActionCourseContent> mContents;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public int getCurrentCourseIndex() {
        return currentCourseIndex;
    }

    public void setCurrentCourseIndex(int currentCourseIndex) {
        this.currentCourseIndex = currentCourseIndex;
    }

    public List<ActionCourseContent> getContents() {
        return mContents;
    }

    public void setContents(List<ActionCourseContent> contents) {
        mContents = contents;
    }

    @Override
    public String toString() {
        return "ActionCourseModel{" +
                "userId='" + userId + '\'' +
                ", ActionCourcesName='" + ActionCourcesName + '\'' +
                ", ActionLockType=" + ActionLockType +
                ", drawableId=" + drawableId +
                ", ActionCourcesScore=" + ActionCourcesScore +
                ", currentCourseIndex=" + currentCourseIndex +
                ", mContents=" + mContents +
                '}';
    }
}
