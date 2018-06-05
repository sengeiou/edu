package com.ubt.alpha1e_edu.action.model;

import java.util.List;

/**
 * @author：liuhai
 * @date：2017/11/16 10:38
 * @modifier：ubt
 * @modify_date：2017/11/16 10:38
 * [A brief description]
 * version
 */

public class PrepareDataModel {
    private String prepareName;
    private int drawableId;
    private List<ActionDataModel> mList;
    private boolean isSelected;

    public String getPrepareName() {
        return prepareName;
    }

    public void setPrepareName(String prepareName) {
        this.prepareName = prepareName;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public List<ActionDataModel> getList() {
        return mList;
    }

    public void setList(List<ActionDataModel> list) {
        mList = list;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "PrepareDataModel{" +
                "prepareName='" + prepareName + '\'' +
                ", drawableId=" + drawableId +
                ", mList=" + mList +
                ", isSelected=" + isSelected +
                '}';
    }
}
