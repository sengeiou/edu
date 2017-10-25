package com.ubt.alpha1e.data.DB;

import android.content.Context;

import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;

import java.util.regex.Pattern;

public class MyDbVersion {

    /**
     * 获取SharePreferences中获取数据库版本号
     * @param mContext
     * @return
     */
    public static int initDBVersionFromSharePreferences(Context mContext) {
        String dbVersion = BasicSharedPreferencesOperator
                .getInstance(mContext, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(BasicSharedPreferencesOperator.APP_DB_VERSION);

        int version = -1;
        if(isStringNumber(dbVersion)){
            version = Integer.parseInt(dbVersion);
        }
        return version;
    }

    /**
     * 将数据版本号保存到SharePreferences
     * @param mContext
     * @param version
     */
    public static void saveDBVersionToSharePreferences(Context mContext,int version) {
        BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.APP_DB_VERSION,
                version+"",
                null, -1);
    }

    /***
     * 判断字符串是否都是数字
     */
    public static  boolean isStringNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

}
