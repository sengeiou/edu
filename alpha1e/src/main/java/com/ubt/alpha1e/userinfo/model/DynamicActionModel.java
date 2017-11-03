package com.ubt.alpha1e.userinfo.model;

/**
 * @author：liuhai
 * @date：2017/11/3 14:45
 * @modifier：ubt
 * @modify_date：2017/11/3 14:45
 * [A brief description]
 * version
 */

public class DynamicActionModel {
    private int ActionId;
    private String ActionName;
    private int ActionTime;
    private String ActionType;
    private int ActionStatu;//状态 播放1 暂停0

    public String getActionName() {
        return ActionName;
    }

    public void setActionName(String actionName) {
        ActionName = actionName;
    }


    public int getActionTime() {
        return ActionTime;
    }

    public void setActionTime(int actionTime) {
        ActionTime = actionTime;
    }

    public String getActionType() {
        return ActionType;
    }

    public void setActionType(String actionType) {
        ActionType = actionType;
    }

    public int getActionStatu() {
        return ActionStatu;
    }

    public void setActionStatu(int actionStatu) {
        ActionStatu = actionStatu;
    }

    public int getActionId() {
        return ActionId;
    }

    public void setActionId(int actionId) {
        ActionId = actionId;
    }

    @Override
    public String toString() {
        return "DynamicActionModel{" +
                "ActionId=" + ActionId +
                ", ActionName='" + ActionName + '\'' +
                ", ActionTime=" + ActionTime +
                ", ActionType='" + ActionType + '\'' +
                ", ActionStatu=" + ActionStatu +
                '}';
    }
}
