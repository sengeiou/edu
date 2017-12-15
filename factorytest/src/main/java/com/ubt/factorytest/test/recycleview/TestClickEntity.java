package com.ubt.factorytest.test.recycleview;

import android.support.annotation.IntDef;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/11/16 18:26
 * @描述:
 */

public class TestClickEntity implements MultiItemEntity {
    public static final int CLICK_ITEM_VIEW = 1;
    public static final int CLICK_ITEM_SPEAKER = 2;

    /**测试命令项*/
    public static final int TEST_ITEM_START_TIME = 1;
    public static final int TEST_ITEM_SOFT_VERSION = 2;
    public static final int TEST_ITEM_HARDWARE_VER = 3;
    public static final int TEST_ITEM_ELECTRICCHARGE = 4;
    public static final int TEST_ITEM_LEDTEST = 5;
    public static final int TEST_ITEM_BTSENSITIVITY = 6;
    public static final int TEST_ITEM_WAKEUPTEST = 7;
    public static final int TEST_ITEM_INTERRUOTTEST = 8;
    public static final int TEST_ITEM_SPEAKERTEST = 9;
    public static final int TEST_ITEM_PIRTEST = 10;
    public static final int TEST_ITEM_GSENSIRTEST = 11;
    public static final int TEST_ITEM_WIFITEST = 12;
    public static final int TEST_ITEM_ROBOTRESET = 13;
    public static final int TEST_ITEM_SAVETESTPROFILE = 14;
    public static final int TEST_ITEM_RECORD_TEST = 15;
    public static final int TEST_ITEM_ACTION_TEST = 16;
    public static final int TEST_ITEM_AGEING_TEST = 17;


    @IntDef(value = {TEST_ITEM_START_TIME, TEST_ITEM_SOFT_VERSION,TEST_ITEM_HARDWARE_VER,TEST_ITEM_ELECTRICCHARGE,
            TEST_ITEM_LEDTEST, TEST_ITEM_BTSENSITIVITY, TEST_ITEM_WAKEUPTEST,TEST_ITEM_INTERRUOTTEST,TEST_ITEM_SPEAKERTEST,
            TEST_ITEM_PIRTEST,TEST_ITEM_GSENSIRTEST,TEST_ITEM_WIFITEST,TEST_ITEM_ROBOTRESET,TEST_ITEM_SAVETESTPROFILE,
            TEST_ITEM_RECORD_TEST, TEST_ITEM_ACTION_TEST, TEST_ITEM_AGEING_TEST})
    public @interface TestCMD{}

    public int Type;
    private String testItem;  //布局类型
    private String testResult;
    private boolean isPass = true;
    private int imgID;
    private int testID;

    public TestClickEntity(final int type, final String testItem) {
        Type = type;
        this.testItem = testItem;
    }

    public TestClickEntity(final int type, final String testItem, final int imgID, final @TestCMD int testID) {
        Type = type;
        this.testItem = testItem;
        this.imgID = imgID;
        this.testID = testID;
    }

    @Override
    public int getItemType() {
        return Type;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getTestItem() {
        return testItem;
    }

    public void setTestItem(String testItem) {
        this.testItem = testItem;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public boolean isPass() {
        return isPass;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }

    public int getImgID() {
        return imgID;
    }

    public void setImgID(int imgID) {
        this.imgID = imgID;
    }

    public int getTestID() {
        return testID;
    }

    @Override
    public String toString() {
        return "{" +
                "测试项:='" + testItem + '\'' +
                ", 测试结果:='" + testResult + '\'' +
                ", 是否通过:=" + isPass +
                '}';
    }
}