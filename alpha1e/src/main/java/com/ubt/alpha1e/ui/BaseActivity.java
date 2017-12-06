package com.ubt.alpha1e.ui;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.ThemeInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.ui.dialog.LowBatteryDialog;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.ui.helper.IUI;
import com.ubt.alpha1e.utils.StatusBarUtils;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.umeng.analytics.MobclickAgent;
import com.zhy.changeskin.SkinManager;
import com.zhy.changeskin.base.BaseSkinActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.ubt.alpha1e.ui.ActionUnpublishedActivity.KEY_ACTION_SYNC_STATE;
import static com.ubt.alpha1e.ui.custom.CommonCtrlView.KEY_CURRENT_PLAYING_ACTION_NAME;

public abstract class BaseActivity extends
        BaseSkinActivity implements IUI {


    private String mCurrentSetLanguage = "";

    protected Dialog mCoonLoadingDia;

    protected abstract void initUI();

    protected abstract void initControlListener();

    protected abstract void initBoardCastListener();

    public  BaseHelper mHelper;

    protected Handler mHandler = new Handler();

    private static String skinPath = FileTools.theme_pkg_file;

    private String activity_title = "";

    private String activity_save = "";

    private String TAG = "BaseActivity";

    private String currentActivityLable;

//    public ControlCenterActivity commonCtrlView;
    private resetFloatViewListener resetFloatViewListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //((AlphaApplication) this.getApplication()).addToActivityList(this);
        ((AlphaApplication) this.getApplication()).setBaseActivity(this);
        AppManager.getInstance().addActivity(this);
        initSkin();
        super.onCreate(savedInstanceState);
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

    /**
     * 初始化状态栏颜色
     * 1、部分页面需要节日定制，先判断是否节日定制页面，再判断有无节日主题
     */
    private void initWindowStatusBarColor(){
        if(this instanceof MyMainActivity
                || this instanceof RobotConnectedActivity
                || this instanceof MyActionsActivity
                || this instanceof RobotControlActivity
                || this instanceof RemoteSelActivity
                || this instanceof BannerDetailActivity
                || this instanceof WebContentActivity
                || this instanceof MyDynamicActivity
                || this instanceof SettingActivity
                || this instanceof MessageActivity
                || this instanceof ThemeActivity){

            if (SkinManager.getInstance().needChangeSkin() && SkinManager.getInstance().getSkinMap().containsKey("status_bar_color_ft")){
                Resources mResources = SkinManager.getInstance().getmResources();
                int colorid = mResources.getColor(mResources.getIdentifier("status_bar_color_ft","color",FileTools.package_name));
                StatusBarUtils.setWindowStatusBarColor(this, colorid,false);
            }else{
                StatusBarUtils.setWindowStatusBarColor(this, getResources().getColor(R.color.status_bar_color_ft),true);
            }
        }else {
            StatusBarUtils.setWindowStatusBarColor(this, getResources().getColor(R.color.TB1),true);
        }
    }

    public void initTitle(String _title_name) {

        activity_title = _title_name;

        ((TextView) this.findViewById(R.id.txt_base_title_name))
                .setText(activity_title);
        OnClickListener backListener = new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                BaseActivity.this.finish();
            }
        };
        setTitleBack(backListener);
    }

    public void initSkin(){

        boolean fromNotice = false;
        //从系统通知栏进入
        if(this instanceof ActionsLibPreviewWebActivity
                || this instanceof WebContentActivity){
            fromNotice = getIntent().getExtras().getBoolean(ActionsLibPreviewWebActivity.FROM_NOTICE,false);
        }

        if(this instanceof StartInitSkinActivity || fromNotice){
            initSkinPath();
        }else {
            if(SkinManager.getInstance().getSkinContext() == null){
                UbtLog.e(TAG,"getSkinContext = null" );
                //有时候报错的时候，没有crash到，没有重启，语言包context为空，调用语言包错误，
                initSkinPath();
            }
        }
    }

    private void initSkinPath(){

        //UbtLog.d(TAG,"--initSkinPath-->>" + this + "    fromNotice = " + fromNotice);
        try {
            ThemeInfo festival = new ThemeInfo().getThiz(FileTools.readFileStringSync(FileTools.theme_cache, FileTools.theme_festival_log_name));
            if(festival != null){
                String themeFilePath = festival.themeUrl;
                File themeFile = new File(themeFilePath);
                File festivalFile = new File(FileTools.theme_pkg_festival_file);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(festival.themeEffTime);
                Date endDate = sdf.parse(festival.themeExpTime);
                Date today = new Date();

                UbtLog.d(TAG,"initSkinPath themeFile = " + themeFile.length() + "  festivalFile = " + festivalFile.length());
                UbtLog.d(TAG,"initSkinPath today = " + today + "  startDate = " + startDate + "    endDate: " + endDate);
                if(themeFile.exists() && festivalFile.exists()
                        && themeFile.length() == festivalFile.length()
                        && today.getTime() > startDate.getTime()
                        && today.getTime() < endDate.getTime()){

                    skinPath = FileTools.theme_pkg_festival_file;
                }else {
                    skinPath = FileTools.theme_pkg_file;
                }

            }else {
                skinPath = FileTools.theme_pkg_file;
            }
        } catch (Exception e) {
            skinPath = FileTools.theme_pkg_file;
            e.printStackTrace();
        }

        SkinManager.getInstance().init(this,skinPath, FileTools.package_name,"");
        SkinManager.getInstance().changeSkin(skinPath, FileTools.package_name, null);
        UbtLog.d(TAG,"skinPath = " + skinPath);
    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        if (!"".equals(activity_title))
            ((TextView) this.findViewById(R.id.txt_base_title_name))
                    .setText(activity_title);
        if (!"".equals(activity_save))
            ((Button) this.findViewById(R.id.btn_base_save))
                    .setText(activity_save);
    }

    public void initTitleSave(OnClickListener saveListener) {
        initTitleSave(saveListener, "");
    }

    public void initTitleSave(OnClickListener saveListener, String save_str) {
        Button btn_save = ((Button) this.findViewById(R.id.btn_base_save));
        if (save_str.equals("")) {

        } else {
            btn_save.setText(save_str);
            activity_save = save_str;
        }
        btn_save.setVisibility(View.VISIBLE);
        btn_save.setOnClickListener(saveListener);
    }

    public void setTitleBack(OnClickListener listener) {

        (this.findViewById(R.id.lay_base_back))
                .setOnClickListener(listener);

        TextView back = ((TextView) this.findViewById(R.id.tv_base_back));

        back.setOnClickListener(listener);
    }

    public void onNoteLowPower(final int value ) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //LowPowerDialog.getInstance(BaseActivity.this).show();
                    new LowBatteryDialog(BaseActivity.this).setBatteryThresHold(value).builder().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (mHelper != null){
            mHelper.DistoryHelper();
        }

        //此Activity销毁后，取消Eventbus监听
        EventBus.getDefault().unregister(this);
//        ((AlphaApplication) this.getApplication()).removeActivityList(this);
        AppManager.getInstance().finishActivity(this);
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();

//        ((AlphaApplication) this.getApplication()).setBaseActivity(this);
//        MobclickAgent.onResume(this);
//
//        doCheckLanguage();
//
        if (mHelper != null){
            mHelper.RegisterHelper();
        }
//
//        UbtLog.d(TAG, "--wmma--onResume!");
//
//        if (((AlphaApplication) this.getApplicationContext())
//                .getCurrentBluetooth() != null){
//            UbtLog.d(TAG, "--wmma--bluetooth ssss");
//            if(stopFloatService() /*&& isServiceRun(getApplicationContext(), "com.ubt.alpha1e.ui.custom.FloatControlViewService")*/){
//                ControlCenterActivity.closeCommonCtrlView();
//            }else{
//                ControlCenterActivity.getInstace(this);
//            }
//        }else{
//            ControlCenterActivity.closeCommonCtrlView();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mHelper != null) {
//            UbtLog.d(TAG, "--wmma--mHelper UnRegisterHelper! " + mHelper.getClass().getSimpleName());
//            mHelper.UnRegisterHelper();
//        }
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Subscribe
    public void onEventRobot(RobotEvent event) {
//        if(event.getEvent() == RobotEvent.Event.CONNECT_SUCCESS){
//            UbtLog.d(TAG,"--CONNECT_SUCCESS--");
//            if (!stopFloatService()){
//                ControlCenterActivity.getInstace(this);
//            }
//        }
    }

    public void doCheckLanguage() {
        mCurrentSetLanguage = BasicSharedPreferencesOperator.getInstance(this,
                DataType.APP_INFO_RECORD).doReadSync(
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
        UbtLog.d(TAG,"skinPath = " + skinPath );
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
                        BaseActivity.this,getStringResources("ui_home_conn_lost"), Toast.LENGTH_SHORT).show();
                MyLog.writeLog("蓝牙掉线", this.getClass().getName() + "-->onLostBtCoon");
//                ((AlphaApplication) BaseActivity.this.getApplication()).doLostConn(BaseActivity.this);
            }
        });


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

    public boolean checkLoginState()
    {
        UserInfo userInfo  = ((AlphaApplication)getApplication()).getCurrentUserInfo();
        return userInfo == null?false:true;
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

    public void showToastDirect(String str)
    {
        Toast.makeText(this.getApplicationContext(),str, Toast.LENGTH_SHORT).show();
    }

    public String getStringResources(String str) {
        try {
            String string = "";
            //以下代码暂时注释，上传服务器需要去掉注释
            //Resources res = SkinManager.getInstance().needChangeSkin() ? SkinManager.getInstance().getmResources() : getResources();
            //string = res.getString(res.getIdentifier(str, "string", FileTools.package_name));
            if(TextUtils.isEmpty(string))
            {
                Resources superRes = getResources();
                string = superRes.getString(superRes.getIdentifier(str,"string",FileTools.package_name));
            }
            return string;
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * 获取皮肤资源对象(针对获取字符串)
     * @return
     */
    public Resources getMyResources() {
        if (!SkinManager.getInstance().needChangeSkin()){
            return getResources();
        } else{
            return SkinManager.getInstance().getmResources();
        }
    }

    /**
     * 获取皮肤资源对象(针对获取图片与颜色)
     * @return
     */
    public Resources getSkinResources(String string) {
        if (SkinManager.getInstance().needChangeSkin() && SkinManager.getInstance().getSkinMap().containsKey(string)){
            return SkinManager.getInstance().getmResources();
        } else{
            return getResources();
        }
    }

    /**
     * 获取字符串数组
     * @param string
     * @return
     */
    public String[] getStringArray(String string) {
        Resources res = getMyResources();
        return res.getStringArray(res.getIdentifier(string, "array", FileTools.package_name));
    }

    /**
     * 动态获取图片
     * @param string 图片Key
     * @return
     */
    public Drawable getDrawableRes(String string) {
        try {
            Resources res = getSkinResources(string);
            int drawableId = res.getIdentifier(string, "drawable", FileTools.package_name);
            Drawable mDrawable = null;
            //UbtLog.d(TAG,"drawableId = " + drawableId + "  string:" + string);
            if(drawableId > 0){
                mDrawable = res.getDrawable(drawableId);
                if("progress_playing_ft".equals(string)){
                    UbtLog.d(TAG,"drawableId = " + drawableId + " progress_playing_ft string:" + string + "   mDrawable = " + mDrawable);
                }
            }else {
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
     * @param string 颜色值Key
     * @return
     */
    public ColorStateList getColorRes(String string){
        try {
            Resources res = getSkinResources(string);
            int colorId = res.getIdentifier(string, "color", FileTools.package_name);
            ColorStateList mColorStateList = null;
            //UbtLog.d(TAG,"colorId = " + colorId + "  string:" + string);
            if(colorId > 0){
                mColorStateList = res.getColorStateList(colorId);
            }else {
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
     * @param string 颜色值Key
     * @return
     */
    public int getColorId(String string){
        try {
            Resources res = getSkinResources(string);
            int colorResId = res.getIdentifier(string, "color", FileTools.package_name);
            int colorId = 0;
            UbtLog.d(TAG,"colorResId = " + colorResId + "  string:" + string);
            if(colorResId > 0){
                colorId = res.getColor(colorResId);
            }else {
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
     * @return
     */
    private boolean stopFloatService(){

        if(currentActivityLable == null){
            return false;
        }
        UbtLog.d(TAG, "current Activity lable=" + currentActivityLable);
        if(currentActivityLable.equals(getResources().getString(R.string.title_activity_setting))){
            return  true;
        }

        if(currentActivityLable.equals(getResources().getString(R.string.title_activity_about_us))){
            return  true;
        }

        if(currentActivityLable.equals(getResources().getString(R.string.title_activity_actions_edit))) {
            return true;
        }

        if(currentActivityLable.equals(getResources().getString(R.string.title_activity_actions_edit_save))){
            return true;
        }

        if(currentActivityLable.equals(getResources().getString(R.string.title_activity_remote))){
            return true;
        }

        if(currentActivityLable.equals(getResources().getString(R.string.title_activity_remote_sel))){
            return true;
        }

        if(currentActivityLable.equals(getResources().getString(R.string.title_activity_login))){
            return true;
        }

        if(currentActivityLable.equals(getResources().getString(R.string.title_activity_private_info))){
            return true;
        }

        if(currentActivityLable.equals("PrivateInfoShowActivity")){
            return true;
        }

        if(currentActivityLable.equals(getResources().getString(R.string.title_activity_private_info_edit))){
            return  true;
        }

        if(currentActivityLable.equals(getResources().getString(R.string.title_activity_web_content))){
            return  true;
        }

        if(currentActivityLable.equals("ActionsLibPreviewWebActivity")){
            return true;
        }

        if(currentActivityLable.equals(getResources().getString(R.string.title_activity_banner_detail))){
            return true;
        }

        if(currentActivityLable.equals(getResources().getString(R.string.title_activity_remote_set))){
            return true;
        }

        if(currentActivityLable.equals("DubActivity")) {
            return true;
        }

        if(currentActivityLable.equals("BlocklyActivity")) {
            return true;
        }

        if(currentActivityLable.equals("RobotNetConnectActivity")) {
            return true;
        }

        if(currentActivityLable.equals("BlocklyCourseActivity")){
            return true;
        }

        if(currentActivityLable.equals("ActionsNewEditActivity")){
            return true;
        }
        if(currentActivityLable.equals("ActionTestActivity")){
            return true;
        }



        return false;
    }


    private boolean isForeground(){
        String [] activityPackages = getActivePackages();
        if (activityPackages != null) {
            for (String activePackage : activityPackages) {
                if (activePackage.equals("com.ubt.alpha1e")) {
                    return true;
                }
            }
        }
        return  false;
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

    public void setCurrentActivityLable(String lable){
        currentActivityLable = lable;
    }

    public interface resetFloatViewListener{
        void resetFloatView(boolean reset);
    }


    //新增判断是否是第一次启动App，false则显示改版指引，true则不显示。
    public boolean isLaunchered() {
        return BasicSharedPreferencesOperator
                .getInstance(BaseActivity.this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
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
        BasicSharedPreferencesOperator.getInstance(BaseActivity.this,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_NEED_SHOW_GUIDE_VIEW,
                BasicSharedPreferencesOperator.NO_NEED_SHOW_GUIDE_VIEW,
                null, -1);
    }

    public void recordSchemeShowState(String step) {
        BasicSharedPreferencesOperator.getInstance(BaseActivity.this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(BasicSharedPreferencesOperator.KEY_SCHEME_SHOW_STATE,
                step, null, -1);
    }

    public String readSchemeShowState() {
        return BasicSharedPreferencesOperator.getInstance(BaseActivity.this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(BasicSharedPreferencesOperator.KEY_SCHEME_SHOW_STATE);
    }

    //以下方法用于保存当前播放的动作名称，已解决显示过程中我的创建和下载的动作的名称为数字的问题
    public void saveCurrentPlayingActionName(String actionName) {
        BasicSharedPreferencesOperator.getInstance(this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(KEY_CURRENT_PLAYING_ACTION_NAME,
                actionName, null, -1);

    }

    public String readCurrentPlayingActionName(){
        return BasicSharedPreferencesOperator.getInstance(this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(KEY_CURRENT_PLAYING_ACTION_NAME);
    }


    //以下方法用于记录从未发的动作详情页进入配音页面同步后的状态
    public void saveActionSyncState(String state){
        BasicSharedPreferencesOperator.getInstance(this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(KEY_ACTION_SYNC_STATE,
                state, null, -1);
    }

    public String readActionSyncState(){
        return BasicSharedPreferencesOperator.getInstance(this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(KEY_ACTION_SYNC_STATE);
    }

}
