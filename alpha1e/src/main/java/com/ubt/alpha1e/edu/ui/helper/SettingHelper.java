package com.ubt.alpha1e.edu.ui.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.FileTools.State;
import com.ubt.alpha1e.edu.data.IFileListener;
import com.ubt.alpha1e.edu.data.ISharedPreferensListenet;
import com.ubt.alpha1e.edu.net.http.basic.IJsonListener;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;


public class SettingHelper extends BaseHelper implements IJsonListener,
        ISharedPreferensListenet, IFileListener {

    private static final String TAG = "SettingHelper";
    private ISettingUI mUI;
    private int do_clear_login_info = 10001;
    private static int do_set_message_note = 10002;

    public SettingHelper(ISettingUI _ui, BaseActivity _baseActivity) {
        super(_baseActivity);
        this.mUI = _ui;
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
        if (cmd == ConstValue.SET_PALYING_CHARGING) {
            if (param[0] == 0) {
                UbtLog.d(TAG, "设置禁止边充边玩成功");
            } else {
                UbtLog.d(TAG, "设置开启边充边玩成功");
            }
        }
    }

    public static boolean isMessageNote(Context _context) {
        String value = BasicSharedPreferencesOperator.getInstance(_context,
                DataType.USER_USE_RECORD).doReadSync(
                BasicSharedPreferencesOperator.IS_MESSAGE_NOTE_KEY);
        if (BasicSharedPreferencesOperator.IS_MESSAGE_NOTE_VALUE_FALSE
                .equalsIgnoreCase(value))
            return false;
        else
            return true;
    }

    public static boolean isPlayCharging(Context _context) {
        String value = BasicSharedPreferencesOperator.getInstance(_context,
                DataType.USER_USE_RECORD).doReadSync(
                BasicSharedPreferencesOperator.IS_CHAGING_PALYING_KEY);
        if (value
                .equals(BasicSharedPreferencesOperator.IS_CHAGING_PALYING_VALUE_TRUE))
            return true;
        else
            return false;
    }


    public static boolean isAutoConnect(Context _context) {
        String value = BasicSharedPreferencesOperator.getInstance(_context,
                DataType.USER_USE_RECORD).doReadSync(
                BasicSharedPreferencesOperator.IS_AUTO_CONNECT_KEY);
        if (value.equals(BasicSharedPreferencesOperator.IS_AUTO_CONNECT_VALUE_FALSE)) {
            return false;
        }else {
            return true;
        }
    }

    public static void doSetMessageNote(Context _context, boolean isNote) {
        if (isNote) {
            BasicSharedPreferencesOperator.getInstance(_context,
                    DataType.USER_USE_RECORD).doWrite(
                    BasicSharedPreferencesOperator.IS_MESSAGE_NOTE_KEY,
                    BasicSharedPreferencesOperator.IS_MESSAGE_NOTE_VALUE_TRUE,
                    null, do_set_message_note);

            // if (JPushInterface.isPushStopped(_context)) {
            // JPushInterface.resumePush(_context);
            // }

        } else

        {
            BasicSharedPreferencesOperator.getInstance(_context,
                    DataType.USER_USE_RECORD).doWrite(
                    BasicSharedPreferencesOperator.IS_MESSAGE_NOTE_KEY,
                    BasicSharedPreferencesOperator.IS_MESSAGE_NOTE_VALUE_FALSE,
                    null, do_set_message_note);
            // if (!JPushInterface.isPushStopped(_context)) {
            // JPushInterface.stopPush(_context);
            // }
        }
    }

    public void doSetPalyCharging(boolean isPaly) {
        byte[] params = new byte[1];
        if (isPaly) {
            BasicSharedPreferencesOperator
                    .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                    .doWrite(
                            BasicSharedPreferencesOperator.IS_CHAGING_PALYING_KEY,
                            BasicSharedPreferencesOperator.IS_CHAGING_PALYING_VALUE_TRUE,
                            null, -1);
            params[0] = 1;
        } else {
            BasicSharedPreferencesOperator
                    .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                    .doWrite(
                            BasicSharedPreferencesOperator.IS_CHAGING_PALYING_KEY,
                            BasicSharedPreferencesOperator.IS_CHAGING_PALYING_VALUE_FALSE,
                            null, -1);
            params[0] = 0;
        }
        if (((AlphaApplication) mBaseActivity.getApplicationContext())
                .getCurrentBluetooth() != null) {
            doSendComm(ConstValue.SET_PALYING_CHARGING, params);
        }
    }

    public void doSetAutoConnect(boolean isAutoConnect) {
        if (isAutoConnect) {
            BasicSharedPreferencesOperator
                    .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                    .doWrite(
                            BasicSharedPreferencesOperator.IS_AUTO_CONNECT_KEY,
                            BasicSharedPreferencesOperator.IS_AUTO_CONNECT_VALUE_TRUE,
                            null, -1);
        } else {
            BasicSharedPreferencesOperator
                    .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                    .doWrite(
                            BasicSharedPreferencesOperator.IS_AUTO_CONNECT_KEY,
                            BasicSharedPreferencesOperator.IS_AUTO_CONNECT_VALUE_FALSE,
                            null, -1);
        }
    }

    public static boolean isOnlyWifiDownload(Context _context) {
        String value = BasicSharedPreferencesOperator.getInstance(_context,
                DataType.USER_USE_RECORD).doReadSync(
                BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_KEY);
        if (value
                .equals(BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_VALUE_TRUE))
            return true;
        else
            return false;
    }

    public static void doSetOnlyWifiDownload(Context _context, boolean isOnly) {
        if (isOnly) {
            BasicSharedPreferencesOperator
                    .getInstance(_context, DataType.USER_USE_RECORD)
                    .doWrite(
                            BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_KEY,
                            BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_VALUE_TRUE,
                            null, -1);
        } else

        {
            BasicSharedPreferencesOperator
                    .getInstance(_context, DataType.USER_USE_RECORD)
                    .doWrite(
                            BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_KEY,
                            BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_VALUE_FALSE,
                            null, -1);
        }
    }

    public void doExitLogin() {
        if (((AlphaApplication) mBaseActivity.getApplicationContext())
                .getCurrentUserInfo() != null) {
            // 清除内存登录信息
            ((AlphaApplication) mBaseActivity.getApplicationContext())
                    .LoginOut();

            //清除短缓存数据
            ((AlphaApplication) mBaseActivity.getApplicationContext())
                    .clearCacheData();

            // 清除外存登录信息
            BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                    DataType.USER_USE_RECORD).doWrite(
                    BasicSharedPreferencesOperator.LOGIN_USER_INFO, "NO_VALUE",
                    this, do_clear_login_info);

            mCourseAccessToken = "";
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
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetJson(boolean isSuccess, String json, long request_code) {
        // TODO Auto-generated method stub

    }

    public void doReadCacheSize() {
        FileTools.readCacheSize(this);
    }

    public void doClearCache() {
        //清除短缓存数据
        ((AlphaApplication) mBaseActivity.getApplicationContext())
                .clearCacheData();

        FileTools.clearCacheSize(this);
    }

    @Override
    public void onWriteDataFinish(long requestCode, State state) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadCacheSize(int size) {
        mUI.onReadCacheSize(size);
    }

    @Override
    public void onClearCache() {
        mUI.onClaerCache();
    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSharedPreferenOpreaterFinish(boolean isSuccess,
                                               long request_code, String value) {
        if (request_code == do_clear_login_info) {
            // 重新登录
            /*((AlphaApplication) mBaseActivity.getApplicationContext())
                    .doExitApp(false);
            ((AlphaApplication) mBaseActivity.getApplicationContext())
                    .doRestartApp();*/
        } else if (request_code == do_set_message_note) {

        }

    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result,
                                    boolean result_state, long request_code) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result,
                                     long request_code) {
        // TODO Auto-generated method stub

    }

    public void doChangeLanguage(String language,final Activity activity) {
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.APP_INFO_RECORD).doWrite(
                BasicSharedPreferencesOperator.LANGUAGE_SET_KEY, language,
                new ISharedPreferensListenet() {
                    @Override
                    public void onSharedPreferenOpreaterFinish(
                            boolean isSuccess, long request_code, String value) {
//                         mBaseActivity.doCheckLanguage();
                        //清除短缓存数据
                        ((AlphaApplication) mBaseActivity.getApplicationContext())
                                .clearCacheData();

                        activity.finish();

                    }
                }, -1);
    }

    public String getLanguageCurrentStr() {
        String init_info = BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.APP_INFO_RECORD).doReadSync(
                BasicSharedPreferencesOperator.LANGUAGE_SET_KEY);
        int index = getLanguageIndex(init_info);
        if (index == -1) {
            return mBaseActivity.getStringResources("ui_settings_language_not_set");
        } else {
            return  mBaseActivity.getStringArray("ui_lanuages")[index].toString();
        }
    }

    public int getLanguageCurrentIndex() {
        return getLanguageIndex(BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.APP_INFO_RECORD).doReadSync(
                BasicSharedPreferencesOperator.LANGUAGE_SET_KEY));
    }

    public void setLanguageCurrentIndex()
    {

    }

    public int getLanguageIndex(String language) {
        CharSequence[] languages =  mBaseActivity.getStringArray("ui_lanuages_up");
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].toString().toUpperCase().contains(language.toUpperCase())) {
                return i;
            }
        }
        return -1;
    }

    public String getLanguage(int index) {
        CharSequence[] languages = mBaseActivity.getStringArray("ui_lanuages_up");
        if (index < languages.length && index >= 0)
            return languages[index].toString().split(",")[0];
        return "";
    }

    public boolean isFirstUseLanguage() {
        if (BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.IS_FIRST_USE_LANGUAGE)
                .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_LANGUAGE_FALSE)) {

            return false;
        }
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_FIRST_USE_LANGUAGE,
                BasicSharedPreferencesOperator.IS_FIRST_USE_LANGUAGE_FALSE,
                null, -1);
        return true;
    }

}
