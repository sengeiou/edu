package com.zhy.changeskin;

import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.zhy.changeskin.log.MyLog;
import com.zhy.changeskin.utils.L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by zhy on 15/9/22.
 */
public class ResourceManager {
    private static final String DEFTYPE_DRAWABLE = "drawable";
    private static final String DEFTYPE_COLOR = "color";
    private static final String DEFTYPE_TEXT = "string";
    private static final String DEFTYPE_SHAPE = "shape";
    private Resources mResources;
    private String mPluginPackageName;
    private String mSuffix;
    public Map<String,String> skinMap;

    public ResourceManager(Resources res, String pluginPackageName, String suffix) {
        mResources = res;
        mPluginPackageName = pluginPackageName;

        if (suffix == null) {
            suffix = "";
        }
        mSuffix = suffix;
        skinMap = new HashMap<>();
        initSkinArray(res, skinMap);

    }

    public void setResources(Resources res){
        mResources = res;
    }

    public void initSkinArray(Resources res, Map<String,String> skinMap) {
        AssetManager assetManager = res.getAssets();

        //皮肤配置
        MyLog.writeLog("皮肤配置", "开始");

        //节日配置
        MyLog.writeLog("皮肤配置", "节日配置");
        try {
            InputStream is = assetManager.open("skin_f.txt");
            BufferedReader bfr = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = bfr.readLine()) != null) {
                Log.d("节日配置","skin_f::" + line + "   skinList.size: " + skinMap.size());
                //skinList.add(line);
                if(!skinMap.containsKey(line)){
                    skinMap.put(line,line);
                }
            }
            bfr.close();
            is.close();
        } catch (IOException e) {
            MyLog.writeLog("皮肤配置", e.getMessage());
        }

    }

    public String getTextString(String name) {
        try {

            name = appendSuffix(name);
            return mResources.getString(mResources.getIdentifier(name, DEFTYPE_TEXT, mPluginPackageName));

        } catch (Resources.NotFoundException e) {

            //e.printStackTrace();
            return null;
        }
    }

    public Drawable getDrawableByName(String name) {

        if (name.startsWith("gif")) {
            try {
                return new GifDrawable(mResources, mResources.getIdentifier(name, DEFTYPE_DRAWABLE, mPluginPackageName));
            } catch (Exception e) {
                return null;
            }
        }

        if (1 == 0) {
            GifDrawable gif = null;
            try {
                gif = new GifDrawable(mResources, mResources.getIdentifier(name, DEFTYPE_DRAWABLE, mPluginPackageName));
            } catch (Exception e) {
                gif = null;
            }
            if (gif != null)
                return gif;
        }

        Drawable result = null;

        try {
            name = appendSuffix(name);
            result = mResources.getDrawable(mResources.getIdentifier(name, DEFTYPE_DRAWABLE, mPluginPackageName));
        } catch (Resources.NotFoundException e) {
            result = null;
        }

        if (result != null)
            return result;

        try {
            return mResources.getDrawable(mResources.getIdentifier(name, DEFTYPE_COLOR, mPluginPackageName));
        } catch (Resources.NotFoundException e2) {
            result = null;
        }

        if (result != null)
            return result;

        if (1 == 0) {
            try {
                return mResources.getDrawable(mResources.getIdentifier(name, DEFTYPE_SHAPE, mPluginPackageName));
            } catch (Resources.NotFoundException e2) {
                result = null;
            }
        }

        return null;
    }

    public int getColor(String name) {
        try {
            name = appendSuffix(name);
            return mResources.getColor(mResources.getIdentifier(name, DEFTYPE_COLOR, mPluginPackageName));

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return -1;
        }

    }

    public ColorStateList getColorStateList(String name) {
        try {
            name = appendSuffix(name);
            return mResources.getColorStateList(mResources.getIdentifier(name, DEFTYPE_COLOR, mPluginPackageName));

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    private String appendSuffix(String name) {
        if (!TextUtils.isEmpty(mSuffix))
            return name += "_" + mSuffix;
        return name;
    }

}
