package com.ubt.alpha1e;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class AlphaApplicationValues {

    public static enum Thrid_login_type {
        QQ, WECHAT, SINABLOG, FACEBOOK, TWITTER
    }

    public static final String THIRD_lOGIN_TYPE = "third_login_type";

    private static ApplicationInfo appInfo;

    public static enum EdtionCode {
        for_factory_edit, formal_edit
    }

    public static enum ChannelName {
        ubt_robot, google
    }

    public static boolean initInfo(Context mContext) {
        if (appInfo == null) {
            try {
                appInfo = mContext.getPackageManager()
                        .getApplicationInfo(mContext.getPackageName(),
                                PackageManager.GET_META_DATA);
                return true;
            } catch (NameNotFoundException e) {
                return false;
            }
        }
        return true;
    }

    public static boolean isDebug() {
        try {
            Integer isDebug = (Integer) appInfo.metaData
                    .get("com.ubt.alpha1e.isDebug");
            if (isDebug == 1)
                return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getLogTitle() {
        try {
            String title = appInfo.metaData
                    .getString("com.ubt.alpha1e.logTitle");
            return title;
        } catch (Exception e) {
            return "";
        }

    }

    public static EdtionCode getCurrentEdit() {
        try {
            String edtionCode = appInfo.metaData
                    .getString("com.ubt.alpha1e.currentEdit");
            if (edtionCode.equals(EdtionCode.for_factory_edit.name()))
                return EdtionCode.for_factory_edit;
            return EdtionCode.formal_edit;
        } catch (Exception e) {
            return EdtionCode.formal_edit;
        }
    }

    /**
     * 获取渠道名
     * @return 如果没有获取成功，那么返回值为
     */
    public static ChannelName getChannelName() {
        ChannelName channelName = null;

        try {
             String channelNameStr = appInfo.metaData.getString("UMENG_CHANNEL");
             switch (channelNameStr){
                 case "google":
                     channelName = ChannelName.google;
                     break;
                 case "ubt_robot":
                     channelName = ChannelName.ubt_robot;
                     break;
                 default:
                     channelName = ChannelName.ubt_robot;
                     break;
             }

        } catch (Exception e) {
            channelName = ChannelName.ubt_robot;
            e.printStackTrace();
        }
        return channelName;
    }

    /**
     * v2.7.1需求
     * alpha 1蓝牙名称更改需求如下：
     * 1S        "Alpha1S"
     * 1P        "Alpha1P_XXXX"
     * 1SP       "Alpha1S_XXXX"
     * 1P+       "Alpha1P+XXXX"
     * @param beforeBluetoothName
     * @return
     */
    public static String dealBluetoothName(String beforeBluetoothName){
        String afterBluetoothName = beforeBluetoothName;
        if(beforeBluetoothName.toUpperCase().startsWith("ALPHA 1S")){//1S
            afterBluetoothName = "Alpha1S";
        }else if(beforeBluetoothName.startsWith("Alpha1_")){//1P
            afterBluetoothName = beforeBluetoothName.replace("Alpha1_","Alpha1P_");
        }else if(beforeBluetoothName.startsWith("Alpha1S_")){//1SP
            //need not deal
        }else if(beforeBluetoothName.startsWith("Alpha1P+")){//1P+
            //need not deal
        }
        return afterBluetoothName;
    }

}
