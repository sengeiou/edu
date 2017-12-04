package com.ubt.factorytest.bluetooth.recycleview;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/21 11:37
 * @描述:
 */

public class BTBean implements MultiItemEntity {
    public static final int BT_ITEM_VIEW = 1;
    public int type;
    private String btName;
    private String btMac;
    private String btRSSI;

    public BTBean(int type){
        this.type = type;
    }

    public BTBean(int type, String btName, String btMac, String btRSSI) {
        this.type = type;
        this.btName = btName;
        this.btMac = btMac;
        this.btRSSI = btRSSI;
    }

    public String getBtName() {
        return btName;
    }

    public void setBtName(String btName) {
        this.btName = btName;
    }

    public String getBtMac() {
        return btMac;
    }

    public void setBtMac(String btMac) {
        this.btMac = btMac;
    }

    public String getBtRSSI() {
        return btRSSI;
    }

    public void setBtRSSI(String btRSSI) {
        this.btRSSI = btRSSI;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
