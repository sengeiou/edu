package com.ubt.factorytest.ActionPlay.recycleview;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/14 19:27
 * @描述:
 */

public class ActionBean implements MultiItemEntity{
    public static final int ACTION_ITEM_VIEW = 1;
    private int type;
    private String actionName;


    public ActionBean(int type){
        this.type = type;
    }

    public ActionBean(int type, String actionName) {
        this.type = type;
        this.actionName = actionName;
    }

    @Override
    public int getItemType() {
        return type;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
