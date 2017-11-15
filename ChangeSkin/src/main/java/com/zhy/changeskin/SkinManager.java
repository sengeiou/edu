package com.zhy.changeskin;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.zhy.changeskin.attr.SkinAttr;
import com.zhy.changeskin.attr.SkinView;
import com.zhy.changeskin.callback.ISkinChangedListener;
import com.zhy.changeskin.callback.ISkinChangingCallback;
import com.zhy.changeskin.utils.PrefUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zhy on 15/9/22.
 */
public class SkinManager {
    private Context mContext;
    private Resources mResources;
    private ResourceManager mResourceManager;
    private PrefUtils mPrefUtils;

    private boolean usePlugin;
    /**
     * 换肤资源后缀
     */
    private String mSuffix = "";
    private String mCurPluginPath;
    private String mCurPluginPkg;

    private String mCurrentLanguage = "";


    private Map<ISkinChangedListener, List<SkinView>> mSkinViewMaps = new HashMap<ISkinChangedListener, List<SkinView>>();
    private List<ISkinChangedListener> mSkinChangedListeners = new ArrayList<ISkinChangedListener>();

    private static SkinManager sInstance = null;

    private SkinManager() {
    }

    /*private static class SingletonHolder {
        static SkinManager sInstance = new SkinManager();
    }*/



    public static SkinManager getInstance() {
        if(sInstance == null){
            sInstance = new SkinManager();
        }
        return sInstance;
    }

    public Context getSkinContext(){
        if(sInstance != null && sInstance.mContext != null){
            return  sInstance.mContext;
        }
        return null;
    }


    public static void cleanInstance(){
        sInstance = null;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mPrefUtils = new PrefUtils(mContext);
        String skinPluginPath = mPrefUtils.getPluginPath();
        String skinPluginPkg = mPrefUtils.getPluginPkgName();
        Log.e("skin","skinPluginPath:"+skinPluginPath);
        mSuffix = mPrefUtils.getSuffix();
        if (TextUtils.isEmpty(skinPluginPath))
            return;
        File file = new File(skinPluginPath);
        if (!file.exists()) return;
        try {
            loadPlugin(skinPluginPath, skinPluginPkg, mSuffix);
            mCurPluginPath = skinPluginPath;
            mCurPluginPkg = skinPluginPkg;
        } catch (Exception e) {
            mPrefUtils.clear();
            e.printStackTrace();
        }
    }

    public void init(Context context,String skinPath, String skinPkgName, String suffix) {
        mContext = context.getApplicationContext();
        mPrefUtils = new PrefUtils(mContext);
        String skinPluginPath = null;
        String skinPluginPkg = null;
        if(!TextUtils.isEmpty(skinPath)){
            skinPluginPath = skinPath;
            skinPluginPkg = skinPkgName;
            mSuffix = suffix ;
        }else {
            skinPluginPath = mPrefUtils.getPluginPath();
            skinPluginPkg = mPrefUtils.getPluginPkgName();
            mSuffix = mPrefUtils.getSuffix();
        }

        Log.e("init","skinPluginPath:"+skinPluginPath);
        if (TextUtils.isEmpty(skinPluginPath))
            return;
        File file = new File(skinPluginPath);
        if (!file.exists()) return;
        try {
            loadPlugin(skinPluginPath, skinPluginPkg, mSuffix);
            mCurPluginPath = skinPluginPath;
            mCurPluginPkg = skinPluginPkg;
        } catch (Exception e) {
            mPrefUtils.clear();
            e.printStackTrace();
        }
    }

    public  Locale getLocale(String language) {

        if(language.equalsIgnoreCase("zh_cn"))
        {
            return  Locale.SIMPLIFIED_CHINESE;
        }else if(language.equalsIgnoreCase("zh_tw"))
        {
            return  Locale.TRADITIONAL_CHINESE;
        }else if(language.equalsIgnoreCase("ko"))
        {
            return  Locale.KOREA;
        }else
            return  new Locale(language);
    }

    private void loadPlugin(String skinPath, String skinPkgName, String suffix) throws Exception {
        //checkPluginParams(skinPath, skinPkgName);
        AssetManager assetManager = AssetManager.class.newInstance();
        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
        addAssetPath.invoke(assetManager, skinPath);
        Resources superRes = mContext.getResources();
        Configuration congiConfiguration = superRes.getConfiguration();
        DisplayMetrics displayMetrics = superRes.getDisplayMetrics();
        if(!TextUtils.isEmpty(mCurrentLanguage))
        {
            congiConfiguration.locale = getLocale(mCurrentLanguage);
        }
        mResources = new Resources(assetManager, displayMetrics, congiConfiguration);
        Log.d("skinPath", "skinPath = " + skinPath + " usePlugin == " + usePlugin);

        if(mResourceManager != null && usePlugin){
            mResourceManager.setResources(mResources);
        }else {
            mResourceManager = new ResourceManager(mResources, skinPkgName, suffix);
        }
        //mResourceManager = new ResourceManager(mResources, skinPkgName, suffix);

        superRes.updateConfiguration(congiConfiguration, displayMetrics);
        setChangeSkin(true);
    }

    private boolean checkPluginParams(String skinPath, String skinPkgName) {
        File file = new File(skinPath);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    private boolean checkPluginParamsThrow(String skinPath, String skinPkgName,String suffix) {
        if (!checkPluginParams(skinPath, skinPkgName)) {
//            throw new IllegalArgumentException("skinPluginPath or skinPkgName can not be empty ! ");
            clearPluginInfo();
            Log.e("lang","----------checkPluginParamsThrow---------");
            mPrefUtils.removePluginInfo();
            Resources resources = mContext.getResources();// 获得res资源对象
            Configuration config = resources.getConfiguration();// 获得设置对象
            DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
            config.locale = getLocale(mCurrentLanguage);
            resources.updateConfiguration(config, dm);
            notifyChangedListeners();
            return false;
        }
        return true;
    }


    public void removeAnySkin() {


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                clearPluginInfo();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                notifyChangedListeners();

            }
        }.execute();
    }


    public boolean needChangeSkin() {
        //return usePlugin ;
        //暂时不考虑切肤2017.11.6
        return false;
    }

    public void setChangeSkin(boolean usePlugin)
    {
        this.usePlugin = usePlugin;
    }

    public Resources getmResources()
    {
        return mResources;
    }

    public ResourceManager getResourceManager() {
        if (!usePlugin) {
            mResourceManager = new ResourceManager(mContext.getResources(), mContext.getPackageName(), mSuffix);
        }
        return mResourceManager;
    }

    public void changeViewSkin(View view, String resName)
    {
        Drawable drawable = SkinManager.getInstance().getResourceManager().getDrawableByName(
                resName);
        if (drawable == null)
            return;
        ((ImageView)view).setImageDrawable(drawable);


    }


    /**
     * 应用内换肤，传入资源区别的后缀
     *
     * @param suffix
     */
    public void changeSkin(String suffix) {
        clearPluginInfo();//clear before
        mSuffix = suffix;
        mPrefUtils.putPluginSuffix(suffix);
        notifyChangedListeners();
    }

    private void clearPluginInfo() {
        mCurPluginPath = null;
        mCurPluginPkg = null;
        setChangeSkin(false);
        mSuffix = null;
        mPrefUtils.clear();
    }

    private void updatePluginInfo(String skinPluginPath, String pkgName, String suffix) {
        mPrefUtils.putPluginPath(skinPluginPath);
        mPrefUtils.putPluginPkg(pkgName);
        mPrefUtils.putPluginSuffix(suffix);
        mCurPluginPkg = pkgName;
        mCurPluginPath = skinPluginPath;
        mSuffix = suffix;
    }


    public void changeSkin(final String skinPluginPath, final String pkgName, ISkinChangingCallback callback) {
        changeSkin(skinPluginPath, pkgName, "", callback);
    }

    public void setLanguage(String lang)
    {
        this.mCurrentLanguage = lang;
    }

    /**
     * 根据suffix选择插件内某套皮肤，默认为""
     *
     * @param skinPluginPath
     * @param pkgName
     * @param suffix
     * @param callback
     */


    public void changeSkin(final String skinPluginPath, final String pkgName, final String suffix, ISkinChangingCallback callback) {
        if (callback == null)
            callback = ISkinChangingCallback.DEFAULT_SKIN_CHANGING_CALLBACK;
        final ISkinChangingCallback skinChangingCallback = callback;

        skinChangingCallback.onStart();
        if(!checkPluginParamsThrow(skinPluginPath, pkgName,suffix))
        {
            return;
        }

        if (skinPluginPath.equals(mCurPluginPath) && pkgName.equals(mCurPluginPkg)) {
            // return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    loadPlugin(skinPluginPath, pkgName, suffix);
                } catch (Exception e) {
                    e.printStackTrace();
                    skinChangingCallback.onError(e);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    updatePluginInfo(skinPluginPath, pkgName, suffix);
                    notifyChangedListeners();
                    skinChangingCallback.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    skinChangingCallback.onError(e);
                }

            }
        }.execute();
    }


    public void addSkinView(ISkinChangedListener listener, List<SkinView> skinViews) {
        mSkinViewMaps.put(listener, skinViews);
        //Log.e("wilson---","add: "+listener.toString()+", "+mSkinViewMaps.size());
    }

    public List<SkinView> getSkinViews(ISkinChangedListener listener) {
        //Log.e("wilson---","remove: "+listener.toString()+", "+mSkinViewMaps.size());
        //Log.e("SkinManager","listener = " + listener);
        //Log.e("SkinManager","mSkinViewMaps = " + mSkinViewMaps);
        //Log.e("SkinManager","mSkinViewMaps.get(listener) = " + mSkinViewMaps.get(listener));
        return mSkinViewMaps.get(listener);
    }


    public void apply(ISkinChangedListener listener) {
        //Log.e("wilson---","apply: "+listener.toString());
        List<SkinView> skinViews = getSkinViews(listener);

        if(skinViews != null){
            for (SkinView skinView : skinViews) {
                skinView.apply();
            }
        }else {
            Log.e("SkinManager","skinViews = " + skinViews);
        }
    }



    //去除不用换肤资源
    public void shrinkResources(List<SkinView> skinViews, List<SkinView> newSkinViews) {
        for (SkinView skinView : skinViews) {
            for (SkinAttr attr : skinView.getAttrs()) {
                if (mResourceManager.skinMap.containsKey(attr.resName)) {
                    newSkinViews.add(skinView);
                    break;
                }
            }
        }
    }

    public void addChangedListener(ISkinChangedListener listener) {
        mSkinChangedListeners.add(listener);
    }


    public void removeChangedListener(ISkinChangedListener listener) {
        mSkinChangedListeners.remove(listener);
        mSkinViewMaps.remove(listener);
    }


    public void notifyChangedListeners() {
        for (ISkinChangedListener listener : mSkinChangedListeners) {
            listener.onSkinChanged();
        }
    }

    public Map<String,String> getSkinMap(){
        return mResourceManager.skinMap;
    }


}
