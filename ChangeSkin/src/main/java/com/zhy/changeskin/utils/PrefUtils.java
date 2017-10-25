package com.zhy.changeskin.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.zhy.changeskin.constant.SkinConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by zhy on 15/9/22.
 */
public class PrefUtils
{
    private Context mContext;
    private static ExecutorService pool = Executors.newSingleThreadExecutor();
    public enum DataType {
        USER_USE_RECORD, DEVICES_RECORD, MESSAGE_RECORD, APP_INFO_RECORD
    }

    public PrefUtils(Context context)
    {
        this.mContext = context;
    }

    public String getPluginPath()
    {
        SharedPreferences sp = mContext.getSharedPreferences(SkinConfig.PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(SkinConfig.KEY_PLUGIN_PATH, "");
    }

    public String getSuffix()
    {
        SharedPreferences sp = mContext.getSharedPreferences(SkinConfig.PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(SkinConfig.KEY_PLUGIN_SUFFIX, "");
    }

    public boolean clear()
    {
        SharedPreferences sp = mContext.getSharedPreferences(SkinConfig.PREF_NAME, Context.MODE_PRIVATE);
        return sp.edit().clear().commit();
    }

    public void  removePluginInfo()
    {

        pool.submit(new Runnable() {

            @Override
            public void run() {

                SharedPreferences.Editor editor = mContext
                        .getSharedPreferences(DataType.APP_INFO_RECORD.name(),
                                mContext.MODE_PRIVATE)
                        .edit();
                editor.putString("LANGUAGE_USING_INFO", "-1");
                editor.commit();
            }
        });
    }


    public void putPluginPath(String path)
    {
        SharedPreferences sp = mContext.getSharedPreferences(SkinConfig.PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(SkinConfig.KEY_PLUGIN_PATH, path).apply();
    }

    public void putPluginPkg(String pkgName)
    {
        SharedPreferences sp = mContext.getSharedPreferences(SkinConfig.PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(SkinConfig.KEY_PLUGIN_PKG, pkgName).apply();
    }

    public String getPluginPkgName()
    {
        SharedPreferences sp = mContext.getSharedPreferences(SkinConfig.PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(SkinConfig.KEY_PLUGIN_PKG, "");
    }

    public void putPluginSuffix(String suffix)
    {
        SharedPreferences sp = mContext.getSharedPreferences(SkinConfig.PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(SkinConfig.KEY_PLUGIN_SUFFIX, suffix).apply();
    }
}
