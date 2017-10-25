package com.ubt.alpha1e.ui.helper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.business.ThemeManager;
import com.ubt.alpha1e.business.ThemeManagerLitener;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.data.DB.ActionsRecordOperater;
import com.ubt.alpha1e.data.DB.ActionsRecordOperaterOld;
import com.ubt.alpha1e.data.DB.UpgradeOperater;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.ISharedPreferensListenet;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.data.model.ThemeInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.ubt.alpha1e.services.ActivationService;
import com.ubt.alpha1e.services.GetResourcesService;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.dialog.IUpdateListener;
import com.ubt.alpha1e.ui.dialog.UpdateDialog;
import com.ubt.alpha1e.update.ApkUpdateManager;
import com.ubt.alpha1e.update.UpdateTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartHelper extends BaseHelper implements
        ISharedPreferensListenet, IJsonListener, ThemeManagerLitener {
    private static final String TAG = "StartHelper";
    private IStartUI mUI;
    // -------------------------------
    private static final int MSG_DO_READ_USER_INFO = 1001;
    // -------------------------------
    private int do_read_user_request = 11001;
    private long do_check_update_request = 11002;
    // -------------------------------
    private boolean isNeedUpdateApk = false;
    private boolean isNeedCompleteInfo = false;
    private boolean isNeedUpdateTheme = false;
    private boolean isNeedUpdateLanguage = false;

    private ThemeManager mThemeManager;


    public boolean isNeedUpdateApk() {
        return isNeedUpdateApk;
    }

    public boolean isNeedUpdateTheme() {
        return isNeedUpdateTheme;
    }
    public boolean isNeedUpdateLanguage() {
        return isNeedUpdateLanguage;
    }

    public boolean isNeedCompleteInfo() {
        return isNeedCompleteInfo;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_DO_READ_USER_INFO) {
                UserInfo mCurrentUser = new UserInfo().getThiz((String) msg.obj);
                UbtLog.d(TAG, "start helper mCurrentUser =" + mCurrentUser);
                if (mCurrentUser != null) {
                    if (mCurrentUser.countryCode == null
                            || mCurrentUser.countryCode.equals("")) {
                        isNeedCompleteInfo = true;
                    }

                    //判断如果用户名为空时，清除缓存记录
                    if(TextUtils.isEmpty(mCurrentUser.userName)){
                        ((AlphaApplication) mBaseActivity.getApplicationContext()).clearCurrentUserInfo();
                        mCurrentUser = null;
                    }

                    //新的登录方式，一定要token,暂时注释
                    /*if(mCurrentUser.token == null || !mCurrentUser.token.endsWith(mCurrentUser.userId+"")){
                        UbtLog.d(TAG,"mCurrentUser.token = " + mCurrentUser.token + "   " + mCurrentUser.token.endsWith(mCurrentUser.userId+""));
                        ((AlphaApplication) mBaseActivity.getApplicationContext()).clearCurrentUserInfo();
                        mCurrentUser = null;
                    }*/
                }
                ((AlphaApplication) mBaseActivity.getApplicationContext()).setCurrentUserInfo(mCurrentUser);
                mUI.onReadFinish(true);
            }
        }
    };

    public StartHelper(IStartUI _ui, BaseActivity _baseActivity) {
        super(_baseActivity);
        this.mUI = _ui;
        mThemeManager = ThemeManager.getInstance(mBaseActivity);
        mThemeManager.addListener(this);
    }

    @Override
    public void RegisterHelper() {
        super.RegisterHelper();
        mThemeManager.addListener(this);
    }

    @Override
    public void UnRegisterHelper() {
        mThemeManager.removeListener(this);
        super.UnRegisterHelper();
    }

    public void doUpdateApk() {
        boolean isRequestWifi = BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_KEY)
                .equals(BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_VALUE_TRUE);
        if (isRequestWifi && (!isWifiCoon())) {
            isNeedUpdateApk = false;
        } else {
            GetDataFromWeb.getJsonByPost(do_check_update_request, HttpAddress
                    .getRequestUrl(Request_type.check_update), HttpAddress
                    .getParamsForPost(new String[]{"1", "2", "1"},
                            Request_type.check_update, mBaseActivity), this);
        }
    }

    public void doReadUser() {
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.USER_USE_RECORD).doReadAsync(
                BasicSharedPreferencesOperator.LOGIN_USER_INFO, this,
                do_read_user_request);
    }

    public void doCkeckThemeInfo() {
        MyLog.writeLog("主题检查程序", "doCkeckThemeInfo");
        mThemeManager.doCheckThemeState();
    }
    public void doCkeckLanguageInfo() {
        MyLog.writeLog("主题检查程序", "doCkeckLanguageInfo");
        mThemeManager.doCheckLanguageState();
    }

    public void doUpdateThemes() {
        MyLog.writeLog("主题检查程序", "UpdateThemes");
        mThemeManager.doApplyThemeAsync(null);
    }

    public void doUpdateLanguage()
    {
        mThemeManager.doUpdateLanguages();
    }

    @Override
    public void onSharedPreferenOpreaterFinish(boolean isSuccess,
                                               long request_code, String value) {
        // TODO Auto-generated method stub
        if (do_read_user_request == request_code) {
            Message msg = new Message();
            msg.obj = value;
            msg.what = MSG_DO_READ_USER_INFO;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onGetJson(boolean isSuccess, String json, long request_code) {
        // TODO Auto-generated method stub
        if (do_check_update_request == request_code) {
            if (JsonTools.getJsonStatus(json)) {
                UbtLog.d(TAG,"json = " + json);
                JSONObject obj = JsonTools.getJsonModel(json);
                String versionName = "";
                String versionPath = "";
                String versionSize = "";
                String versionInfo = "";
                try {
                    versionName = obj.getString("versionName");
                    if (versionName.toLowerCase().startsWith("v")) {
                        versionName = versionName.substring(1);
                    }
                    versionPath = obj.getString("versionPath");
                } catch (JSONException e1) {
                    versionName = "";
                    versionPath = "";
                }

                try {
                    versionSize = obj.getString("versionSize");
                } catch (JSONException e1) {
                    versionSize = "";
                }

                try {
                    versionInfo = obj.getString("versionResume");
                } catch (JSONException e1) {
                    versionInfo = "";
                }

                if (UpdateTools.compareVersion(versionName, mBaseActivity)) {
                    isNeedUpdateApk = true;
                    final String _versionPath = versionPath;
                    final String _versionNameSizeInfo = versionName + "#"
                            + versionSize + "#" + versionInfo;

                    ApkUpdateManager.isNewersion = false;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                UpdateDialog.getInstance(mBaseActivity,
                                        _versionNameSizeInfo.split("#"),
                                        new IUpdateListener() {

                                            @Override
                                            public void doUpdate() {
                                                // google play不自主升级,跳转Google升级-------------------start
                                                UbtLog.d(TAG,"app channel name:"+AlphaApplicationValues.getChannelName());
                                                if(AlphaApplicationValues.getChannelName() == AlphaApplicationValues.ChannelName.google){
                                                    String appPackageName = mBaseActivity.getPackageName();
                                                    try {
                                                        mBaseActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                        UbtLog.d(TAG,"has install market and has this app:"+appPackageName);
                                                    } catch (ActivityNotFoundException anfe) {
                                                        UbtLog.d(TAG,"no install market or market has not this app");
                                                        mBaseActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }

                                                }else {
                                                    ApkUpdateManager.getInstance(
                                                            mBaseActivity,
                                                            _versionPath).Update();
                                                    StartHelper.this.mUI.gotoNext();
                                                }
                                                // google play不自主升级-------------------end
                                            }

                                            @Override
                                            public void doIgnore() {
                                                StartHelper.this.mUI.gotoNext();
                                            }
                                        }).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                } else {
                    ApkUpdateManager.isNewersion = true;
                }

            }
        }

    }

    public boolean isWifiCoon() {
        ConnectivityManager connManager = (ConnectivityManager) mBaseActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.getState() == State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {
        // TODO Auto-generated method stub

    }

    @Override
    public void DistoryHelper() {
        mThemeManager.removeListener(this);
    }

    public void doGetLocation() {
        Intent i = new Intent(Intent.ACTION_RUN);
        i.setClass(mBaseActivity, ActivationService.class);
        mBaseActivity.startService(i);
    }

    public void doRunGetResServices() {
        Intent i = new Intent(Intent.ACTION_RUN);
        i.setClass(this.mBaseActivity, GetResourcesService.class);
        this.mBaseActivity.startService(i);
    }

    public void UpgadeDB(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UpgradeOperater.getInstance(StartHelper.this.mBaseActivity, FileTools.db_log_cache, FileTools.db_log_name).execUpgradeOperater();

                replyOldDownloadData();
            }
        }).start();
    }

    /**
     * 恢复 UbtLogs_20160422001 我的下载中的数据
     */
    public void replyOldDownloadData(){

        final String dbFilePath = FileTools.db_log_cache + File.separator + "UbtLogs_20160422001";
        File dbFile = new File(dbFilePath);
        if(dbFile.exists()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean isSuccess = false;
                    try {
                        List<ActionRecordInfo> oldInfos = ActionsRecordOperaterOld.getInstance(StartHelper.this.mBaseActivity, FileTools.db_log_cache, "UbtLogs_20160422001").getAllRecoed();
                        if(oldInfos.size() != 0){
                            List<ActionRecordInfo> infos = new ArrayList<ActionRecordInfo>();
                            infos.addAll(oldInfos);

                            List<ActionRecordInfo> mCurrentInfos = ActionsRecordOperater.getInstance(StartHelper.this.mBaseActivity, FileTools.db_log_cache, FileTools.db_log_name).getAllRecoed();
                            for(ActionRecordInfo info : infos){
                                for(ActionRecordInfo currentInfo : mCurrentInfos){
                                    if(info.action.actionId == currentInfo.action.actionId){
                                        oldInfos.remove(info);
                                        break;
                                    }
                                }
                            }
                        }

                        //处理过后，看是否还有数据
                        if(oldInfos.size() != 0){
                            for(ActionRecordInfo oldInfo : oldInfos){
                                ActionsRecordOperater.getInstance(StartHelper.this.mBaseActivity, FileTools.db_log_cache, FileTools.db_log_name).addRecord(oldInfo);
                            }
                        }
                        isSuccess = true;
                    }catch (Exception ex){
                        isSuccess = false;
                        ex.printStackTrace();
                    }

                    if(isSuccess){
                        new File(dbFilePath).delete();
                    }
                }
            }).start();
        }
    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {

    }

    @Override
    public void onGetThemesOnLine(List<ThemeInfo> infos, boolean isSuccess, String errorInfo) {

    }

    @Override
    public void onGetThemesLocal(List<ThemeInfo> infos) {

    }

    @Override
    public void onGetFileLenth(ThemeInfo info, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(ThemeInfo info, FileDownloadListener.State state) {

    }

    @Override
    public void onReportProgress(ThemeInfo info, double progess) {

    }

    @Override
    public void onDownLoadFileFinish(ThemeInfo info, FileDownloadListener.State state) {

    }

    @Override
    public void onGetUsingThemeInfo(ThemeInfo info) {


    }

    @Override
    public void onApplyThemeFinish(ThemeInfo info, boolean isSuccess) {
        mUI.themeCheckFinish();
    }

    @Override
    public void onGetUsingFestivalThemeInfo(ThemeInfo info) {

    }

    @Override
    public void noteThemeNeedupdate(String string) {//暂用主题接口
        if(!TextUtils.isEmpty(string)&&string.equalsIgnoreCase("lang"))
        {
            isNeedUpdateLanguage  =true;
        }else{
            isNeedUpdateTheme = true;
            UbtLog.d(TAG,"--doUpdateThemes---");
            doUpdateThemes();
        }

    }
}
