package com.ubt.alpha1e.usercenter.model;

/**
 * @author：liuhai
 * @date：2017/10/27 11:52
 * @modifier：ubt
 * @modify_date：2017/10/27 11:52
 * [A brief description]
 * version
 */

public class LeftMenuModel {
    private String nameString;
    private boolean chick;   //标识

    public LeftMenuModel(String nameString) {
        this.nameString = nameString;
    }

    public String getNameString() {
        return nameString;
    }

    public void setNameString(String nameString) {
        this.nameString = nameString;
    }

    public boolean isChick() {
        return chick;
    }

    public void setChick(boolean chick) {
        this.chick = chick;
    }

}
