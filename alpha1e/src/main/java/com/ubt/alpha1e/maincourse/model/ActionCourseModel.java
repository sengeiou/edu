package com.ubt.alpha1e.maincourse.model;

import java.util.List;

/**
 * @author：liuhai
 * @date：2017/11/13 20:15
 * @modifier：ubt
 * @modify_date：2017/11/13 20:15
 * [A brief description]
 * 关卡类
 */

public class ActionCourseModel {

    private String userId;
    private String ActionCourcesName;
    private String title;
    private int ActionLockType;
    private int drawableId;
    private int ActionCourcesScore;
    /**
     * 当前进行到哪个课时
     */
    private int currentCourseIndex;

    private List<String> mList;


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

    public List<String> getList() {
        return mList;
    }

    public void setList(List<String> list) {
        mList = list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "ActionCourseModel{" +
                "userId='" + userId + '\'' +
                ", ActionCourcesName='" + ActionCourcesName + '\'' +
                ", title='" + title + '\'' +
                ", ActionLockType=" + ActionLockType +
                ", drawableId=" + drawableId +
                ", ActionCourcesScore=" + ActionCourcesScore +
                ", currentCourseIndex=" + currentCourseIndex +
                ", mList=" + mList +
                '}';
    }
}
