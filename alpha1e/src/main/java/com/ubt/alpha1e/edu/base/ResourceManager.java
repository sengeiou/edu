package com.ubt.alpha1e.edu.base;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.zhy.changeskin.SkinManager;

import java.util.Locale;

/**
 * @author：liuhai
 * @date：2017/11/14 17:32
 * @modifier：ubt
 * @modify_date：2017/11/14 17:32
 * [A brief description]
 * version
 */

public class ResourceManager {
    private static final String TAG = ResourceManager.class.getSimpleName();
    private Context mContext;
    private static volatile ResourceManager instance;


    private ResourceManager(Context context) {
        this.mContext = context;
    }

    public static ResourceManager getInstance(Context context) {
        if (instance == null) {
            synchronized (ResourceManager.class) {
                instance = new ResourceManager(context);
            }
        }
        return instance;
    }

    public String getSystemLanguage() {

        return Locale.getDefault().getLanguage();
    }

    public String getAppCurrentLanguage() {
        return SkinManager.getInstance().needChangeSkin() ? SkinManager.getInstance().getmResources().getConfiguration().locale.getLanguage()
                : mContext.getResources().getConfiguration().locale.getLanguage();
    }

    public String getAppCurrentCountry() {
        return SkinManager.getInstance().needChangeSkin() ? SkinManager.getInstance().getmResources().getConfiguration().locale.getCountry()
                : mContext.getResources().getConfiguration().locale.getCountry();
    }

    public String getStandardLocale(String str) {
        switch (str.toLowerCase()) {
            case "zh":
            case "zh_cn":
                return "CN";
            case "zh_tw":
            case "zh_hk":
                return "HK";
            case "hu":
                return "HU";
            case "es":
                return "ES";
            case "it":
                return "IT";
            case "ja":
                return "JA";
            case "ko":
                return "KO";
            case "de":
                return "DE";
            case "tr":
                return "TR";
            case "ru":
                return "RU";
            case "fr":
                return "FR";
            default:
                return "EN";
        }
    }


    public String getSystemCountry() {
        return Locale.getDefault().getCountry();
    }

    public String getStringResources(String str) {
        try {
            String string = "";
            //以下代码暂时注释，上传服务器需要去掉注释
            //Resources res = SkinManager.getInstance().needChangeSkin() ? SkinManager.getInstance().getmResources() : getResources();
            //string = res.getString(res.getIdentifier(str, "string", FileTools.package_name));
            if (TextUtils.isEmpty(string)) {
                Resources superRes = mContext.getResources();
                string = superRes.getString(superRes.getIdentifier(str, "string", FileTools.package_name));
            }
            return string;
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * 获取皮肤资源对象(针对获取字符串)
     *
     * @return
     */
    public Resources getMyResources() {
        if (!SkinManager.getInstance().needChangeSkin()) {
            return mContext.getResources();
        } else {
            return SkinManager.getInstance().getmResources();
        }
    }

    /**
     * 获取皮肤资源对象(针对获取图片与颜色)
     *
     * @return
     */
    public Resources getSkinResources(String string) {
        if (SkinManager.getInstance().needChangeSkin() && SkinManager.getInstance().getSkinMap().containsKey(string)) {
            return SkinManager.getInstance().getmResources();
        } else {
            return mContext.getResources();
        }
    }

    /**
     * 获取字符串数组
     *
     * @param string
     * @return
     */
    public String[] getStringArray(String string) {
        Resources res = getMyResources();
        return res.getStringArray(res.getIdentifier(string, "array", FileTools.package_name));
    }

    /**
     * 动态获取图片
     *
     * @param string 图片Key
     * @return
     */
    public Drawable getDrawableRes(String string) {
        try {
            Resources res = getSkinResources(string);
            int drawableId = res.getIdentifier(string, "drawable", FileTools.package_name);
            Drawable mDrawable = null;
            //UbtLog.d(TAG,"drawableId = " + drawableId + "  string:" + string);
            if (drawableId > 0) {
                mDrawable = res.getDrawable(drawableId);
                if ("progress_playing_ft".equals(string)) {
                    UbtLog.d(TAG, "drawableId = " + drawableId + " progress_playing_ft string:" + string + "   mDrawable = " + mDrawable);
                }
            } else {
                //ben app
                res = mContext.getResources();
                mDrawable = res.getDrawable(res.getIdentifier(string, "drawable", FileTools.package_name));
            }
            return mDrawable;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 动态获取颜色值
     *
     * @param string 颜色值Key
     * @return
     */
    public ColorStateList getColorRes(String string) {
        try {
            Resources res = getSkinResources(string);
            int colorId = res.getIdentifier(string, "color", FileTools.package_name);
            ColorStateList mColorStateList = null;
            //UbtLog.d(TAG,"colorId = " + colorId + "  string:" + string);
            if (colorId > 0) {
                mColorStateList = res.getColorStateList(colorId);
            } else {
                //ben app
                res = mContext.getResources();
                mColorStateList = res.getColorStateList(res.getIdentifier(string, "color", FileTools.package_name));
            }
            return mColorStateList;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 动态获取颜色值
     *
     * @param string 颜色值Key
     * @return
     */
    public int getColorId(String string) {
        try {
            Resources res = getSkinResources(string);
            int colorResId = res.getIdentifier(string, "color", FileTools.package_name);
            int colorId = 0;
            UbtLog.d(TAG, "colorResId = " + colorResId + "  string:" + string);
            if (colorResId > 0) {
                colorId = res.getColor(colorResId);
            } else {
                //ben app
                res = mContext.getResources();
                colorId = res.getColor(res.getIdentifier(string, "color", FileTools.package_name));
            }
            return colorId;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        //获取status_bar_height资源的ID
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
