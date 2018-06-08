package com.ubt.alpha1e.edu.blockly.bean;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class ActionNameModule {

    private String actionKey;
    private String actionValue;

    public String getActionKey() {
        return actionKey;
    }

    public void setActionKey(String actionKey) {
        this.actionKey = actionKey;
    }

    public String getActionValue() {
        return actionValue;
    }

    public void setActionValue(String actionValue) {
        this.actionValue = actionValue;
    }

    @Override
    public String toString() {
        return "ActionDataModule{" +
                "actionKey='" + actionKey + '\'' +
                ", actionValue='" + actionValue + '\'' +
                '}';
    }
}
