package com.ubt.factorytest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ubt on 2017/12/15.
 */

public class SpUtils{

        private Context mContext;
        private String saveWifiPwd = "saveWifiPwd";
        private static ExecutorService pool = Executors.newSingleThreadExecutor();


        public SpUtils(Context context)
        {
            this.mContext = context;
        }

        public String getWifiPwd(String wifiName)
        {
            SharedPreferences sp = mContext.getSharedPreferences(saveWifiPwd, Context.MODE_PRIVATE);
            return sp.getString(wifiName, "");
        }

        public boolean clear()
        {
            SharedPreferences sp = mContext.getSharedPreferences(saveWifiPwd, Context.MODE_PRIVATE);
            return sp.edit().clear().commit();
        }



        public void putWifiPwd(String wifiName,String wifiPwd)
        {
            SharedPreferences sp = mContext.getSharedPreferences(saveWifiPwd, Context.MODE_PRIVATE);
            sp.edit().putString(wifiName, wifiPwd).apply();
        }

}
