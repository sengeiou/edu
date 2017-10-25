package com.ubt.alpha1e.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ubt.alpha1e.BuildConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BasicSharedPreferencesOperator {

    public enum DataType {
        USER_USE_RECORD, DEVICES_RECORD, MESSAGE_RECORD, APP_INFO_RECORD
    }

    public final static String NO_VALUE = "NO_VALUE";
    // --------------------------------------------------USER_USE_RECORD
    public final static String IS_FIRST_USE_APP_KEY = "IS_FIRST_USE_APP_KEY" + "_V" + BuildConfig.VERSION_NAME;
    public final static String IS_FIRST_USE_APP_VALUE_TRUE = "IS_FIRST_USE_APP_VALUE_TRUE";
    public final static String IS_FIRST_USE_REMOTE_KEY = "IS_FIRST_USE_REMOTE_KEY";
    public final static String IS_FIRST_USE_REMOTE_VALUE_TRUE = "IS_FIRST_USE_REMOTE_VALUE_TRUE";
    public final static String IS_FIRST_USE_MAIN_KEY = "IS_FIRST_USE_MAIN_KEY";
    public final static String IS_FIRST_USE_MAIN_VALUE_TRUE = "IS_FIRST_USE_MAIN_VALUE_TRUE";
    public final static String IS_FIRST_USE_EDIT_ACTION = "IS_FIRST_USE_EDIT_ACTION";
    public final static String IS_FIRST_USE_EDIT_ACTION_FALSE = "IS_FIRST_USE_EDIT_ACTION_FALSE";

    public final static String IS_FIRST_USE_LEFT = "IS_FIRST_USE_LEFT";
    public final static String IS_FIRST_USE_LEFT_FALSE = "IS_FIRST_USE_LEFT_FALSE";
    public final static String IS_FIRST_USE_THEME = "IS_FIRST_USE_THEME";
    public final static String IS_FIRST_USE_THEME_FALSE = "IS_FIRST_USE_THEME_FALSE";
    public final static String IS_FIRST_USE_SETTINGS = "IS_FIRST_USE_SETTINGS";
    public final static String IS_FIRST_USE_SETTINGS_FALSE = "IS_FIRST_USE_SETTINGS_FALSE";
    public final static String IS_FIRST_USE_LANGUAGE = "IS_FIRST_USE_LANGUAGE";
    public final static String IS_FIRST_USE_LANGUAGE_FALSE = "IS_FIRST_USE_LANGUAGE_FALSE";

    public final static String IS_NEW_NEW_ACTION = "IS_NEW_NEW_ACTION";
    public final static String IS_NEW_NEW_ACTION_TRUE = "IS_NEW_NEW_ACTION_TRUE";
    public final static String IS_NEW_DOWNLOAD_ACTION = "IS_NEW_DOWNLOAD_ACTION";
    public final static String IS_NEW_DOWNLOAD_ACTION_TRUE = "IS_NEW_DOWNLOAD_ACTION_TRUE";
    public final static String IS_ONLY_WIFI_DOWNLOAD_KEY = "IS_ONLY_WIFI_DOWNLOAD_KEY";
    public final static String IS_ONLY_WIFI_DOWNLOAD_VALUE_TRUE = "IS_ONLY_WIFI_DOWNLOAD_VALUE_TRUE";
    public final static String IS_ONLY_WIFI_DOWNLOAD_VALUE_FALSE = "IS_ONLY_WIFI_DOWNLOAD_VALUE_FALSE";
    public final static String IS_MESSAGE_NOTE_KEY = "IS_MESSAGE_NOTE_KEY";
    public final static String IS_MESSAGE_NOTE_VALUE_TRUE = "IS_MESSAGE_NOTE_VALUE_TRUE";
    public final static String IS_MESSAGE_NOTE_VALUE_FALSE = "IS_MESSAGE_NOTE_VALUE_FALSE";
    public final static String IS_CHAGING_PALYING_KEY = "IS_CHAGING_PALYING_KEY";
    public final static String IS_CHAGING_PALYING_VALUE_TRUE = "IS_CHAGING_PALYING_VALUE_TRUE";
    public final static String IS_CHAGING_PALYING_VALUE_FALSE = "IS_CHAGING_PALYING_VALUE_FALSE";
    public final static String IS_AUTO_CONNECT_KEY = "IS_AUTO_CONNECT_KEY";
    public final static String IS_AUTO_CONNECT_VALUE_TRUE = "IS_AUTO_CONNECT_VALUE_TRUE";
    public final static String IS_AUTO_CONNECT_VALUE_FALSE = "IS_AUTO_CONNECT_VALUE_FALSE";
    public final static String IS_ACTIVATE_DEVICES = "IS_ACTIVATE_DEVICES";
    public final static String IS_REMOTE_HEAD_PROMPT = "IS_REMOTE_HEAD_PROMPT";
    public final static String IS_FIRST_PLAY_ACTION = "IS_FIRST_PLAY_ACTION";
    public final static String IS_FIRST_USE_BLOCKLY = "IS_FIRST_USE_BLOCKLY";
    public final static String IS_FIRST_USE_BLOCKLY_FALSE = "IS_FIRST_USE_BLOCKLY_FALSE";
    public final static String IS_FIRST_USE_TASK = "IS_FIRST_USE_TASK_";
    public final static String IS_FIRST_USE_TASK_FALSE = "IS_FIRST_USE_TASK_FALSE";

    // --------------------------------------------------
    public final static String LOGIN_USER_INFO = "LOGIN_USER_INFO";
    public final static String REMOTE_SET_INFO_DEFAULT = "REMOTE_SET_INFO_DEFAULT";
    public final static String REMOTE_SET_INFO_FOOTBALL = "REMOTE_SET_INFO_FOOTBALL";
    public final static String REMOTE_SET_INFO_COMBAT = "REMOTE_SET_INFO_COMBAT";
    public final static String REMOTE_SET_INFO_DANCER = "REMOTE_SET_INFO_DANCER";
    public final static String THRIED_LOGIN_RECORD = "THRIED_LOGIN_RECORD";
    public final static String ALARM_SET_RECORDS = "ALARM_SET_RECORDS";
    public final static String REMOTE_HEAD_PROMPT = "REMOTE_HEAD_PROMPT";
    public final static String REMOTE_ACTIONS_LENGTH = "REMOTE_ACTIONS_LENGTH";
    // --------------------------------------------------
    public final static String MESSAGE_RECOED = "MESSAGE_RECOED";
    public final static String SCHEME_RECOED = "SCHEME_RECOED";
    // --------------------------------------------------

    public final static String MESSAGE_CLICKED_RECORD = "MESSAGE_CLICKED";
    public final static String MESSAGE_ID = "MESSAGE_ID";

    public final static String IS_NEED_SHOW_GUIDE_VIEW = "isNeedShowGuideView";
    public final static String NO_NEED_SHOW_GUIDE_VIEW = "noNeedShowGuideView";

    public final static String KEY_GUIDE_STEP = "keyGuideStep";
    public final static String KEY_ACTION_CUIDE_STEP = "keyActionGuideStep";

    public final static String KEY_SCHEME_SHOW_STATE = "schemeShowState";
    public final static String KEY_DUB_GUIDE_SHOW_STATE = "dubGuideShowState";
    public final static String KEY_DOWNLOAD_SUCCESS_DUB_STATE = "downloadSuccessDubState";

    public final static String KEY_DYNAMIC_GUIDE_SHOW_STATE = "dynamicGuideShowState";
    public final static String KEY_REMOTE_GUIDE_SHOW_STATE = "remoteGuideShowState";

    public final static String KEY_HAS_FLOAT_SHOW = "keyHasFloatShow";

    // --------------------------------------------------APP_INFO_RECORD--start
    public final static String LANGUAGE_SET_KEY = "LANGUAGE_SET_KEY";
    public final static String THREME_USING_INFO_COMMON = "THREME_USING_INFO_COMMON";
    public final static String THREME_USING_INFO_FESTIVAL = "THREME_USING_INFO_FESTIVAL";
    public final static String LANGUAGE_USING_INFO = "LANGUAGE_USING_INFO";
    // --------------------------------------------------APP_INFO_RECORD--end

    //--------------------------------------------------APP_DB_VERSION--START
    public final static String APP_DB_VERSION = "APP_DB_VERSION";
    //-------------------------------------------------APP_DB_VERSION--END

    //逻辑编程blockly 版本号
    public static final String BLOCKLY_VERSION = "blockly_version";

    private static ExecutorService pool = Executors.newSingleThreadExecutor();
    private static BasicSharedPreferencesOperator mPreferencesOpreater;
    // --------------------------------------------------
    private String mdata_type;
    private Context mContext;

    private BasicSharedPreferencesOperator() {
    }

    public static BasicSharedPreferencesOperator getInstance(Context _context,
                                                             DataType _data_type) {
        if (mPreferencesOpreater == null) {
            mPreferencesOpreater = new BasicSharedPreferencesOperator();
        }
        mPreferencesOpreater.mdata_type = _data_type.name();
        mPreferencesOpreater.mContext = _context;
        return mPreferencesOpreater;

    }

    public void doWrite(final String key, final String value,
                        final ISharedPreferensListenet listener, final int request_code) {

        pool.submit(new Runnable() {

            @Override
            public void run() {

                SharedPreferences.Editor editor = mPreferencesOpreater.mContext
                        .getSharedPreferences(mPreferencesOpreater.mdata_type,
                                mPreferencesOpreater.mContext.MODE_PRIVATE)
                        .edit();
                editor.putString(key, value);
                editor.commit();

                if (listener != null)
                    listener.onSharedPreferenOpreaterFinish(true, request_code,
                            NO_VALUE);
            }
        });
    }

    public String doReadSync(String key) {

        SharedPreferences reader = mPreferencesOpreater.mContext
                .getSharedPreferences(mPreferencesOpreater.mdata_type,
                        mPreferencesOpreater.mContext.MODE_PRIVATE);
        return reader.getString(key, NO_VALUE);
    }

    public void doReadAsync(final String key,
                            final ISharedPreferensListenet listener, final int request_code) {

        pool.submit(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                SharedPreferences reader = mPreferencesOpreater.mContext
                        .getSharedPreferences(mPreferencesOpreater.mdata_type,
                                mPreferencesOpreater.mContext.MODE_PRIVATE);
                String value = reader.getString(key, NO_VALUE);

                listener.onSharedPreferenOpreaterFinish(true, request_code,
                        value);
            }
        });

    }

}
