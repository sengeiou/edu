package com.ubt.alpha1e.mvp;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ThemeInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.ui.dialog.LowPowerDialog;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.IUI;
import com.ubt.alpha1e.ui.main.MainActivity;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.umeng.analytics.MobclickAgent;
import com.zhy.changeskin.SkinManager;
import com.zhy.changeskin.attr.SkinAttr;
import com.zhy.changeskin.attr.SkinAttrSupport;
import com.zhy.changeskin.attr.SkinView;
import com.zhy.changeskin.callback.ISkinChangedListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.ubt.alpha1e.ui.ActionUnpublishedActivity.KEY_ACTION_SYNC_STATE;
import static com.ubt.alpha1e.ui.custom.CommonCtrlView.KEY_CURRENT_PLAYING_ACTION_NAME;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public abstract class MVPBaseActivity<V extends BaseView, T extends BasePresenterImpl<V>> extends AppCompatActivity implements ISkinChangedListener, LayoutInflaterFactory, IUI, BaseView {

    private String mCurrentSetLanguage = "";

    protected Dialog mCoonLoadingDia;

    protected abstract void initUI();

    protected abstract void initControlListener();

    protected abstract void initBoardCastListener();

    public BaseHelper mHelper;

    protected Handler mHandler = new Handler();

    private static String skinPath = FileTools.theme_pkg_file;

    private String activity_title = "";

    private String activity_save = "";

    private String TAG = "BaseMvpActivity";

    private String currentActivityLable;

    //    public ControlCenterActivity commonCtrlView;
    private resetFloatViewListener resetFloatViewListener;

    static final Class<?>[] sConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};
    private static final Map<String, Constructor<? extends View>> sConstructorMap
            = new ArrayMap<>();
    private final Object[] mConstructorArgs = new Object[2];
    private static Method sCreateViewMethod;
    static final Class<?>[] sCreateViewSignature = new Class[]{View.class, String.class, Context.class, AttributeSet.class};
    private List<SkinAttr> skinAttrList;

    public T mPresenter;
    Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        LayoutInflaterCompat.setFactory(layoutInflater, this);
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        mUnbinder = ButterKnife.bind(this);
        SkinManager.getInstance().addChangedListener(this);
        //((AlphaApplication) this.getApplication()).addToActivityList(this);
        // ((AlphaApplication) this.getApplication()).setBaseActivity(this);
         ((AlphaApplication) this.getApplication()).setCurrentActivity(this);
        AppManager.getInstance().addActivity(this);
        ((AlphaApplication) this.getApplication()).addToActivityList(this);
        //AlphaApplication.getCurrentActivity()

        mPresenter = getInstance(this, 1);
        mPresenter.attachView((V) this);
        initSkin();
        initWindowStatusBarColor();
//        if (mCurrentApplanguage == null || mCurrentApplanguage.equals(""))
//            mCurrentApplanguage = this.getResources().getConfiguration().locale
//                    .getCountry();
//        mCurrentSetLanguage = BasicSharedPreferencesOperator.getInstance(this,
//                DataType.APP_INFO_RECORD).doReadSync(
//                BasicSharedPreferencesOperator.LANGUAGE_SET_KEY);
//        if (!mCurrentSetLanguage.equals("")
//                && !mCurrentSetLanguage
//                .equals(BasicSharedPreferencesOperator.NO_VALUE)) {
//            mCurrentApplanguage = mCurrentSetLanguage;
//            SkinManager.getInstance().setLanguage(mCurrentSetLanguage);
//            Resources resources = getResources();// 获得res资源对象
//            Configuration config = resources.getConfiguration();// 获得设置对象
//            DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
//            config.locale = LanguageTools.getLocale(mCurrentSetLanguage);
//            resources.updateConfiguration(config, dm);
//        }
//        SkinManager.getInstance().changeSkin(skinPath, FileTools.package_name, null);

        EventBus.getDefault().register(this);


    }

    public abstract int getContentViewId();

    public <T> T getInstance(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        AppCompatDelegate delegate = getDelegate();
        View view = null;
        try {
            //public View createView
            // (View parent, final String name, @NonNull Context context, @NonNull AttributeSet attrs)
            if (sCreateViewMethod == null) {
                Method methodOnCreateView = delegate.getClass().getMethod("createView", sCreateViewSignature);
                sCreateViewMethod = methodOnCreateView;
            }
            Object object = sCreateViewMethod.invoke(delegate, parent, name, context, attrs);
            view = (View) object;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        skinAttrList = SkinAttrSupport.getSkinAttrs(attrs, context, SkinManager.getInstance().getResourceManager().skinMap);
//        List<SkinView> skinViews = SkinManager.getInstance().getSkinViews(this);
//        if (skinViews == null)
//        {
//            skinViews = new ArrayList<SkinView>();
//            SkinManager.getInstance().addSkinView(this, skinViews);
//        }
//        skinViews.add(new SkinView(view, skinAttrList));
//        if (!SkinManager.getInstance().needChangeSkin())
//        {
//            return view;
//        }
        if (view == null) {
            view = createViewFromTag(context, name, attrs);
        }
        injectSkin(view, skinAttrList);
        return view;

    }

    private void injectSkin(View view, List<SkinAttr> skinAttrList) {
//            Log.e("attr","view id: "+view.toString());
        //do some skin inject
        List<SkinView> skinViews = SkinManager.getInstance().getSkinViews(this);
        if (skinViews == null) {
            skinViews = new ArrayList<SkinView>();
            SkinManager.getInstance().addSkinView(this, skinViews);
        }
        skinViews.add(new SkinView(view, skinAttrList));
//        if (skinAttrList.size() != 0)
//        {
//
//            if (SkinManager.getInstance().needChangeSkin())
//            {
//                SkinManager.getInstance().apply(this);
////                skinViews.clear();
//            }
//        }
    }


    private View createViewFromTag(Context context, String name, AttributeSet attrs) {
        try {
            mConstructorArgs[0] = context;
            mConstructorArgs[1] = attrs;

            if (-1 == name.indexOf('.')) {
                // try the android.widget prefix first...
                String prefix = "android.widget.";
                if (TextUtils.equals(name, "View")) {
                    prefix = "android.view.";
                }
                return createView(context, name, prefix);
            } else {
                return createView(context, name, null);
            }
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        } finally {
            // Don't retain references on context.
            mConstructorArgs[0] = null;
            mConstructorArgs[1] = null;
        }
    }

    private View createView(Context context, String name, String prefix)
            throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);

        try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                Class<? extends View> clazz = context.getClassLoader().loadClass(
                        prefix != null ? (prefix + name) : name).asSubclass(View.class);

                constructor = clazz.getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            }
            constructor.setAccessible(true);
            return constructor.newInstance(mConstructorArgs);
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        }
    }

    /**
     * 初始化状态栏颜色
     * 1、部分页面需要节日定制，先判断是否节日定制页面，再判断有无节日主题
     */
    private void initWindowStatusBarColor() {
//        if(this instanceof MyMainActivity
//                || this instanceof RobotConnectedActivity
//                || this instanceof MyActionsActivity
//                || this instanceof RobotControlActivity
//                || this instanceof RemoteSelActivity
//                || this instanceof BannerDetailActivity
//                || this instanceof WebContentActivity
//                || this instanceof MyDynamicActivity
//                || this instanceof SettingActivity
//                || this instanceof MessageActivity
//                || this instanceof ThemeActivity){
//
//            if (SkinManager.getInstance().needChangeSkin() && SkinManager.getInstance().getSkinMap().containsKey("status_bar_color_ft")){
//                Resources mResources = SkinManager.getInstance().getmResources();
//                int colorid = mResources.getColor(mResources.getIdentifier("status_bar_color_ft","color",FileTools.package_name));
//                StatusBarUtils.setWindowStatusBarColor(this, colorid,false);
//            }else{
//                StatusBarUtils.setWindowStatusBarColor(this, getResources().getColor(R.color.status_bar_color_ft),true);
//            }
//        }else {
//            StatusBarUtils.setWindowStatusBarColor(this, getResources().getColor(R.color.TB1),true);
//        }
    }


    public void initSkin() {


        if(this instanceof MainActivity ){
            initSkinPath();
        }else {
            if(SkinManager.getInstance().getSkinContext() == null){
                UbtLog.e(TAG,"getSkinContext = null" );
                //有时候报错的时候，没有crash到，没有重启，语言包context为空，调用语言包错误，
                initSkinPath();
            }
        }
    }

    private void initSkinPath() {

        //UbtLog.d(TAG,"--initSkinPath-->>" + this + "    fromNotice = " + fromNotice);
        try {
            ThemeInfo festival = new ThemeInfo().getThiz(FileTools.readFileStringSync(FileTools.theme_cache, FileTools.theme_festival_log_name));
            if (festival != null) {
                String themeFilePath = festival.themeUrl;
                File themeFile = new File(themeFilePath);
                File festivalFile = new File(FileTools.theme_pkg_festival_file);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(festival.themeEffTime);
                Date endDate = sdf.parse(festival.themeExpTime);
                Date today = new Date();

                UbtLog.d(TAG, "initSkinPath themeFile = " + themeFile.length() + "  festivalFile = " + festivalFile.length());
                UbtLog.d(TAG, "initSkinPath today = " + today + "  startDate = " + startDate + "    endDate: " + endDate);
                if (themeFile.exists() && festivalFile.exists()
                        && themeFile.length() == festivalFile.length()
                        && today.getTime() > startDate.getTime()
                        && today.getTime() < endDate.getTime()) {

                    skinPath = FileTools.theme_pkg_festival_file;
                } else {
                    skinPath = FileTools.theme_pkg_file;
                }

            } else {
                skinPath = FileTools.theme_pkg_file;
            }
        } catch (Exception e) {
            skinPath = FileTools.theme_pkg_file;
            e.printStackTrace();
        }

        SkinManager.getInstance().init(this, skinPath, FileTools.package_name, "");
        SkinManager.getInstance().changeSkin(skinPath, FileTools.package_name, null);
        UbtLog.d(TAG, "skinPath = " + skinPath);
    }

    @Override
    public void onSkinChanged() {
        SkinManager.getInstance().apply(this);
    }


    public void onNoteLowPower() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    LowPowerDialog.getInstance(MVPBaseActivity.this).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (mHelper != null) {
            mHelper.DistoryHelper();
        }

        //此Activity销毁后，取消Eventbus监听
        EventBus.getDefault().unregister(this);
        AppManager.getInstance().finishActivity(this);
        ((AlphaApplication) this.getApplication()).removeActivityList(this);
        super.onDestroy();
        mUnbinder.unbind();//解除绑定，官方文档只对fragment做了解绑
        SkinManager.getInstance().removeChangedListener(this);
        if (null != mPresenter) {
            mPresenter.detachView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ((AlphaApplication) this.getApplication()).setBaseActivity(this);
        MobclickAgent.onResume(this);

        doCheckLanguage();

        if (mHelper != null) {
            mHelper.RegisterHelper();
        }

        UbtLog.d(TAG, "--wmma--onResume!");


//        if (((AlphaApplication) this.getApplicationContext())
//                .getCurrentBluetooth() != null) {
//            UbtLog.d(TAG, "--wmma--bluetooth ssss");
//            if (stopFloatService() /*&& isServiceRun(getApplicationContext(), "com.ubt.alpha1e.ui.custom.FloatControlViewService")*/) {
//                CommonCtrlView.closeCommonCtrlView();
//            } else {
//                CommonCtrlView.getInstace(this);
//            }
//        } else {
//            CommonCtrlView.closeCommonCtrlView();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHelper != null) {
            UbtLog.d(TAG, "--wmma--mHelper UnRegisterHelper! " + mHelper.getClass().getSimpleName());
            mHelper.UnRegisterHelper();
        }
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Subscribe
    public void onEventRobot(RobotEvent event) {
//        if (event.getEvent() == RobotEvent.Event.CONNECT_SUCCESS) {
//            UbtLog.d(TAG, "--CONNECT_SUCCESS--");
//            if (!stopFloatService()) {
//                ControlCenterActivity.getInstace(this);
//            }
//        }
    }

    public void doCheckLanguage() {
        mCurrentSetLanguage = BasicSharedPreferencesOperator.getInstance(this,
                BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doReadSync(
                BasicSharedPreferencesOperator.LANGUAGE_SET_KEY);
        String mCurrentApplanguage = getAppCurrentLanguage();
        String mSystemLanguage = getSystemLanguage();

        // 如果没有设置APP语言
        if (mCurrentSetLanguage.equals("")
                || mCurrentSetLanguage
                .equals(BasicSharedPreferencesOperator.NO_VALUE)) {
            // 如果系统语言和本地语言不一致，重启App
            if (!mCurrentApplanguage.equalsIgnoreCase(mSystemLanguage)) {
                SkinManager.getInstance().setLanguage(mSystemLanguage);
            } else {
                if (mCurrentApplanguage.equalsIgnoreCase("zh")) {
                    if (getSystemCountry().equalsIgnoreCase("tw") || getSystemCountry().equalsIgnoreCase("hk")) {
                        SkinManager.getInstance().setLanguage("zh_tw");
                    }
                }
            }

        }
        // 如果设置了APP语言
        else {
            if (!mCurrentSetLanguage.equals(mCurrentApplanguage)) {
                SkinManager.getInstance().setLanguage(mCurrentSetLanguage);
            }
        }
        UbtLog.d(TAG, "skinPath = " + skinPath);
        SkinManager.getInstance().changeSkin(skinPath, FileTools.package_name, null);

    }

    @Override
    public void onLostBtCoon() {

        UbtLog.d(TAG, "onLostBtCoon " + this);
        saveCurrentPlayingActionName("");
        /*if (mHelper != null) {
            mHelper.UnRegisterHelper();
        }*/
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(
                        MVPBaseActivity.this, getStringResources("ui_home_conn_lost"), Toast.LENGTH_SHORT).show();
            }
        });
        MyLog.writeLog("蓝牙掉线", this.getClass().getName() + "-->onLostBtCoon");
        //((AlphaApplication) this.getApplication()).doLostConn(this);

    }

    public boolean checkCoon() {
        if (((AlphaApplication) this.getApplicationContext())
                .getCurrentBluetooth() == null) {
            this.onLostBtCoon();
            return false;
        } else {
            return true;
        }

    }

    public boolean isBulueToothConnected() {

        if (((AlphaApplication) this.getApplicationContext())
                .getCurrentBluetooth() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkLoginState() {
        UserInfo userInfo = ((AlphaApplication) getApplication()).getCurrentUserInfo();
        return userInfo == null ? false : true;
    }

    public void onReadHeadImgFinish(boolean b, Bitmap obj) {
    }


    public String getSystemLanguage() {

        return Locale.getDefault().getLanguage();
    }

    public String getAppCurrentLanguage() {
        return SkinManager.getInstance().needChangeSkin() ? SkinManager.getInstance().getmResources().getConfiguration().locale.getLanguage()
                : getResources().getConfiguration().locale.getLanguage();
    }

    public String getAppCurrentCountry() {
        return SkinManager.getInstance().needChangeSkin() ? SkinManager.getInstance().getmResources().getConfiguration().locale.getCountry()
                : getResources().getConfiguration().locale.getCountry();
    }

    public String getAppSetLanguage() {
        return (mCurrentSetLanguage.equals("")
                || mCurrentSetLanguage
                .equals(BasicSharedPreferencesOperator.NO_VALUE)) ? getSystemLanguage() : mCurrentSetLanguage;
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


    public void showToast(String str) {
        Toast.makeText(this.getApplicationContext(), getStringResources(str), Toast.LENGTH_SHORT).show();
    }

    public void showToastDirect(String str) {
        Toast.makeText(this.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    public String getStringResources(String str) {
        try {
            String string = "";
            //以下代码暂时注释，上传服务器需要去掉注释
            //Resources res = SkinManager.getInstance().needChangeSkin() ? SkinManager.getInstance().getmResources() : getResources();
            //string = res.getString(res.getIdentifier(str, "string", FileTools.package_name));
            if (TextUtils.isEmpty(string)) {
                Resources superRes = getResources();
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
            return getResources();
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
            return getResources();
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
                res = getResources();
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
                res = getResources();
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
                res = getResources();
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
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 停止全局控制播放控件
     *
     * @return
     */
    private boolean stopFloatService() {

        if (currentActivityLable == null) {
            return false;
        }
        UbtLog.d(TAG, "current Activity lable=" + currentActivityLable);
        if (currentActivityLable.equals(getResources().getString(R.string.title_activity_setting))) {
            return true;
        }

        if (currentActivityLable.equals(getResources().getString(R.string.title_activity_about_us))) {
            return true;
        }

        if (currentActivityLable.equals(getResources().getString(R.string.title_activity_actions_edit))) {
            return true;
        }

        if (currentActivityLable.equals(getResources().getString(R.string.title_activity_actions_edit_save))) {
            return true;
        }

        if (currentActivityLable.equals(getResources().getString(R.string.title_activity_remote))) {
            return true;
        }

        if (currentActivityLable.equals(getResources().getString(R.string.title_activity_remote_sel))) {
            return true;
        }

        if (currentActivityLable.equals(getResources().getString(R.string.title_activity_login))) {
            return true;
        }

        if (currentActivityLable.equals(getResources().getString(R.string.title_activity_private_info))) {
            return true;
        }

        if (currentActivityLable.equals("PrivateInfoShowActivity")) {
            return true;
        }

        if (currentActivityLable.equals(getResources().getString(R.string.title_activity_private_info_edit))) {
            return true;
        }

        if (currentActivityLable.equals(getResources().getString(R.string.title_activity_web_content))) {
            return true;
        }

        if (currentActivityLable.equals("ActionsLibPreviewWebActivity")) {
            return true;
        }

        if (currentActivityLable.equals(getResources().getString(R.string.title_activity_banner_detail))) {
            return true;
        }

        if (currentActivityLable.equals(getResources().getString(R.string.title_activity_remote_set))) {
            return true;
        }

        if (currentActivityLable.equals("DubActivity")) {
            return true;
        }

        if (currentActivityLable.equals("BlocklyActivity")) {
            return true;
        }

        if (currentActivityLable.equals("RobotNetConnectActivity")) {
            return true;
        }

        if (currentActivityLable.equals("BlocklyCourseActivity")) {
            return true;
        }

        if (currentActivityLable.equals("ActionsNewEditActivity")) {
            return true;
        }

        return false;
    }


    private boolean isForeground() {
        String[] activityPackages = getActivePackages();
        if (activityPackages != null) {
            for (String activePackage : activityPackages) {
                if (activePackage.equals("com.ubt.alpha1e")) {
                    return true;
                }
            }
        }
        return false;
    }


    String[] getActivePackages() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final Set<String> activePackages = new HashSet<String>();
        final List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                activePackages.addAll(Arrays.asList(processInfo.pkgList));
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }

    public void setCurrentActivityLable(String lable) {
        currentActivityLable = lable;
    }

    public interface resetFloatViewListener {
        void resetFloatView(boolean reset);
    }


    //新增判断是否是第一次启动App，false则显示改版指引，true则不显示。
    public boolean isLaunchered() {
        return BasicSharedPreferencesOperator
                .getInstance(MVPBaseActivity.this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.IS_NEED_SHOW_GUIDE_VIEW)
                .equals(BasicSharedPreferencesOperator.NO_NEED_SHOW_GUIDE_VIEW);
 /*       if (BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.IS_NEED_SHOW_GUIDE_VIEW)
                .equals(BasicSharedPreferencesOperator.NO_NEED_SHOW_GUIDE_VIEW)) {

            return false;
        }
        return true;*/
    }


    public void doWriteLauncherState() {
        BasicSharedPreferencesOperator.getInstance(MVPBaseActivity.this,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_NEED_SHOW_GUIDE_VIEW,
                BasicSharedPreferencesOperator.NO_NEED_SHOW_GUIDE_VIEW,
                null, -1);
    }

    public void recordSchemeShowState(String step) {
        BasicSharedPreferencesOperator.getInstance(MVPBaseActivity.this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(BasicSharedPreferencesOperator.KEY_SCHEME_SHOW_STATE,
                step, null, -1);
    }

    public String readSchemeShowState() {
        return BasicSharedPreferencesOperator.getInstance(MVPBaseActivity.this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(BasicSharedPreferencesOperator.KEY_SCHEME_SHOW_STATE);
    }

    //以下方法用于保存当前播放的动作名称，已解决显示过程中我的创建和下载的动作的名称为数字的问题
    public void saveCurrentPlayingActionName(String actionName) {
        BasicSharedPreferencesOperator.getInstance(this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(KEY_CURRENT_PLAYING_ACTION_NAME,
                actionName, null, -1);

    }

    public String readCurrentPlayingActionName() {
        return BasicSharedPreferencesOperator.getInstance(this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(KEY_CURRENT_PLAYING_ACTION_NAME);
    }


    //以下方法用于记录从未发的动作详情页进入配音页面同步后的状态
    public void saveActionSyncState(String state) {
        BasicSharedPreferencesOperator.getInstance(this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(KEY_ACTION_SYNC_STATE,
                state, null, -1);
    }

    public String readActionSyncState() {
        return BasicSharedPreferencesOperator.getInstance(this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(KEY_ACTION_SYNC_STATE);
    }
}
