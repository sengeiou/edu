package com.ubt.alpha1e.ui.helper;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.blockly.BlocklyCourseActivity;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.Md5;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.event.LessonEvent;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.IImageListener;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.dialog.AlertDialog;
import com.ubt.alpha1e.ui.main.MainActivity;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;
import com.ubtechinc.base.PublicInterface.BlueToothInteracter;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

public abstract class BaseHelper implements BlueToothInteracter, IImageListener {

    private static final String TAG = "BaseHelper";

    protected BaseActivity mBaseActivity;
    protected Context mContext = null;
    private static Timer read_battery_timer = null;
    private static Date lastTime_doReadState = null;
    private static boolean isNeedNoteLowPower = true;
    protected static long readUserHeadImgRequest = 1111001;
    public static boolean hasHandshakeBseven = false;
    public static boolean hasSupportA2DP = false;
    public static boolean hasGetScheme = false;

    public static boolean hasConnectNetwork = false;//Alpha 1E 是否联网

    public static boolean hasSdcard = true;
    public static boolean mCurrentVoiceState = true;
    public static int mCurrentVolume = 0;
    public static boolean mLightState = true;

    public static String mCourseAccessToken = "";


    private static boolean isCharging = false; //用来判断机器人当前是否在充电中,false 表示没有充电中,true表示充电中.
    /*public BaseHelper(BaseActivity _baseActivity) {
        mBaseActivity = _baseActivity;
    }*/

    public void setBaseActivity(BaseActivity _baseActivity) {
        mBaseActivity = _baseActivity;
    }

    public BaseHelper(Context context) {
        mContext = context;
        if (context instanceof BaseActivity) {
            mBaseActivity = (BaseActivity) context;
        }
    }

    public boolean isLostCoon() {
        if (((AlphaApplication) mContext.getApplicationContext())
                .getCurrentBluetooth() == null) {
            return true;
        } else {
            return false;
        }
    }

    protected void doSendComm(String mac, byte cmd, byte[] param) {
        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager().sendCommand(mac, cmd, param,
                param == null ? 0 : param.length, false);
    }

    public void doSendComm(byte cmd, byte[] param) {
        // MyLog.writeLog("
        // �����", ByteHexHelper.byteToHexString(cmd) + "");
        doSendComm(cmd, param, false);
    }

    protected void doSendComm(byte cmd, byte[] param, boolean isMonopolyRight) {
        if (isLostCoon()) {
            UbtLog.e(TAG, "----onLostBtCoon---" + cmd + "    mContext = " + mContext);
            if (mContext != null && mContext instanceof BaseActivity) {
                ((BaseActivity) mContext).onLostBtCoon();
            } else if (mContext != null && mContext instanceof MVPBaseActivity) {
                ((MVPBaseActivity) mContext).onLostBtCoon();
            } else {
                //自动连接等
                ((AlphaApplication) mContext.getApplicationContext()).doLostConn(AlphaApplication.getBaseActivity());
            }
            return;
        }

        ((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager()
                .sendCommand(
                        ((AlphaApplication) mContext
                                .getApplicationContext()).getCurrentBluetooth()
                                .getAddress(), cmd,
                        param, param == null ? 0 : param.length,
                        isMonopolyRight);
    }

    public void UnRegisterHelper() {
        UbtLog.d(TAG, this.getClass().getName() + "DistoryHelper");
        if (((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager() != null) {
            ((AlphaApplication) mContext.getApplicationContext())
                    .getBlueToothManager().removeBlueToothInteraction(this);
        }
    }

    public abstract void DistoryHelper();

    public void RegisterHelper() {
        UbtLog.d(TAG, this.getClass().getName() + "RegisterHelper");

        if (((AlphaApplication) mContext.getApplicationContext())
                .getBlueToothManager() != null) {
            ((AlphaApplication) mContext.getApplicationContext())
                    .getBlueToothManager().addBlueToothInteraction(this);
        }
    }

    @Override
    public void onDeviceDisConnected(String mac) {

        UbtLog.d(TAG, "--onDeviceDisConnected--" + this);
        try {
            if (read_battery_timer != null) {
                read_battery_timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        read_battery_timer = null;

        // if (isLostCoon()
        // && ((AlphaApplication) mBaseActivity.getApplicationContext())
        // .getCurrentBluetooth().getAddress().equals(mac))
        // ((AlphaApplication) mBaseActivity.getApplicationContext())
        // .setCurrentBluetooth(null);

        if (isLostCoon()) {
        } else {
            if (((AlphaApplication) mContext.getApplicationContext())
                    .getCurrentBluetooth().getAddress().equals(mac)) {
                ((AlphaApplication) mContext.getApplicationContext())
                        .setCurrentBluetooth(null);

            }
        }

        if (mContext != null && mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).onLostBtCoon();
        }
    }

    public void doSendReadStateComm() {

        //读机器人状态
        byte[] params = new byte[1];
        params[0] = 0;
        doSendComm(ConstValue.DV_READSTATUS, params);

        //定时读电量
        if (read_battery_timer == null) {
            read_battery_timer = new Timer();
            read_battery_timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isLostCoon()) {
                        return;
                    }

                    byte[] param = new byte[1];
                    param[0] = 4;
                    doSendComm(ConstValue.DV_READ_BATTERY, param);
                }
            }, 0, 2000);
        }
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
         JSONObject mData = new JSONObject();
         /*JSONObject mData=new JSONObject();
         try {
            mData.put("mac", mac);
            mData.put("cmd", cmd);
            mData.put("param", param.toString());
            mData.put("len", len);
            EventBus.getDefault().post(new MainActivity.MessageEvent(mData.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        if (cmd == ConstValue.DV_READ_BATTERY) {
            // ��ֹ�ظ���Ӧ-------------------start
            Date curDate = new Date(System.currentTimeMillis());
            float time_difference = 500;
            if (lastTime_doReadState != null) {
                time_difference = curDate.getTime()
                        - lastTime_doReadState.getTime();
            }
            if (time_difference < 500) {
                return;
            }
            lastTime_doReadState = curDate;

            try {
                //根据机器人上报的状态来判断当前是否在充电中，0x01表示充电中， 0x00表示未在充电。
                byte charge = param[2];
                if (charge == 0x01) {
                    //UbtLog.d("BaseHelper", "charging" + "   power = " + param[3]);
                    setChargingState(true);
                } else {
                    //UbtLog.d("BaseHelper", "not charging" + "   power = " + param[3]);
                    setChargingState(false);
                }

                int power = param[3];
                if (power <= 10 && isNeedNoteLowPower) {
                    isNeedNoteLowPower = false;
                    AlphaApplication.getBaseActivity().onNoteLowPower();
                } else if (power > 10) {
                    isNeedNoteLowPower = true;
                }
            } catch (Exception e) {
                UbtLog.e(TAG, "Exception:" + e.getMessage());
            }
        } else if (cmd == ConstValue.DV_READSTATUS) {
            // 声音状态
            if (param[0] == 0) {
                // 静音
                if (param[1] == 1) {
                    mCurrentVoiceState = false;
                }
                // 有声音
                else {
                    mCurrentVoiceState = true;
                }
            }
            // 播放状态
            else if (param[0] == 1) {
                if (param[1] == 0) {
                    // 暂停
                } else {
                    // 非暂停
                }
            }
            // 音量状态
            else if (param[0] == 2) {

                int nCurrentVolume = param[1];
                if (nCurrentVolume < 0) {
                    nCurrentVolume += 256;
                }
                mCurrentVolume = nCurrentVolume;
            }
            // 舵机灯状态
            else if (param[0] == 3) {
                if (param[1] == 0) {
                    // 灭
                    mLightState = false;
                } else {
                    // 亮
                    mLightState = true;
                }
            }
            //TF卡状态
            else if (param[0] == 4) {
                // 拔出
                if (param[1] == 0) {
                    hasSdcard = false;
                }
                // 插入
                else {
                    hasSdcard = true;
                }
            }
        } else if (cmd == ConstValue.DV_DO_UPGRADE_STATUS) {
            final int upgradeStatus = param[0];
            UbtLog.d(TAG, "收到升级指令 :: " + upgradeStatus);

                /*mBaseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(upgradeStatus == 1){
                            Toast.makeText(mBaseActivity,mBaseActivity.getStringResources("ui_robot_upgrade_start"),Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(mBaseActivity,mBaseActivity.getStringResources("ui_upgrade_robot_low_battery"),Toast.LENGTH_LONG).show();
                        }
                    }
                });*/
        } else if (cmd == ConstValue.DV_READ_NETWORK_STATUS) {
            String networkInfoJson = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG, "cmd = " + cmd + "    networkInfoJson = " + networkInfoJson);

            NetworkInfo networkInfo = GsonImpl.get().toObject(networkInfoJson, NetworkInfo.class);
            if (networkInfo.status) {
                hasConnectNetwork = true;
            } else {
                hasConnectNetwork = false;
            }
        }

    }

    public UserInfo getCurrentUser() {
        return ((AlphaApplication) mContext.getApplicationContext()).getCurrentUserInfo();
    }

    public void doRecordActionDownloadFinish(ActionInfo mAction) {
        // TODO Auto-generated method stub

    }

    public void readUserHead(int h, int w) {

        UserInfo currentUser = ((AlphaApplication) mContext
                .getApplicationContext()).getCurrentUserInfo();

        if (currentUser == null)
            return;
        String str = currentUser.userImage;

        GetDataFromWeb.getImageFromHttp(((AlphaApplication) mContext
                        .getApplicationContext()).getCurrentUserInfo().userImage,
                readUserHeadImgRequest, this, -1, -1, -1, -1);

    }

    public boolean isRightName(String name, int maxLenth, boolean isSpc,
                               String formate) {

        if (!isSpc && name.contains(" ")) {
            AlphaApplication.getBaseActivity().showToast("ui_action_name_spaces");
            return false;
        }

        // "[0-9A-Za-z_]*"

        if (!formate.equals("") && !name.matches(formate)) {
            AlphaApplication.getBaseActivity().showToast("ui_action_name_error");
            return false;
        }

        int lenth = 0;
        try {
            lenth = name.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(mContext,
                    AlphaApplication.getBaseActivity().getStringResources("ui_remote_synchoronize_unknown_error"), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        if (maxLenth > 0 && lenth > maxLenth) {
            Toast.makeText(mContext,
                    AlphaApplication.getBaseActivity().getStringResources("ui_action_name_too_long"), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        return true;
    }


    public void doClickedMessage() {
        BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.MESSAGE_CLICKED_RECORD,
                BasicSharedPreferencesOperator.MESSAGE_CLICKED_RECORD,
                null, -1);
    }

    public boolean clickedMessage() {
        return BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(BasicSharedPreferencesOperator.MESSAGE_CLICKED_RECORD).equals(BasicSharedPreferencesOperator.MESSAGE_CLICKED_RECORD);
    }

    public String getRecordMessageId() {
        return BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(BasicSharedPreferencesOperator.MESSAGE_ID);
    }

    public void setMessageIdRecord(String messageId) {
        BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.MESSAGE_ID,
                messageId,
                null, -1);
    }

    public boolean isFirstUseSetting() {
        if (BasicSharedPreferencesOperator
                .getInstance(mContext, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.IS_FIRST_USE_SETTINGS)
                .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_SETTINGS_FALSE)) {

            return false;
        }

        return true;
    }

    public void setFirstUsedSetting() {
        BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_FIRST_USE_SETTINGS,
                BasicSharedPreferencesOperator.IS_FIRST_USE_SETTINGS_FALSE,
                null, -1);
    }

    public boolean isFirstPlayAction() {
        String value = BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(
                BasicSharedPreferencesOperator.IS_FIRST_PLAY_ACTION);
        if (value.equals(BasicSharedPreferencesOperator.IS_FIRST_PLAY_ACTION)) {
            return false;
        } else {
            return true;
        }
    }

    public void setIsFirstPlayAction() {
        BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_FIRST_PLAY_ACTION,
                BasicSharedPreferencesOperator.IS_FIRST_PLAY_ACTION, null, 0);
    }

    /**
     * 显示首次播放提示
     */
    public void showFirstPlayTip() {
        new AlertDialog(AlphaApplication.getBaseActivity())
                .builder()
                .setMsg(AlphaApplication.getBaseActivity().getStringResources("ui_first_play_tips"))
                .setCancelable(true)
                .setPositiveButton(AlphaApplication.getBaseActivity().getStringResources("ui_common_confirm"), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
    }


    public void initCourseAccessToken() {

        if (!TextUtils.isEmpty(mCourseAccessToken)) {
            return;
        }

        UserInfo userInfo = ((AlphaApplication) mContext.getApplicationContext()).getCurrentUserInfo();
        if (userInfo == null) {
            return;
        }

        String url = "";
        String param = "";

        if (userInfo.userRelationType > 0) {//第三方登录

            url = HttpAddress.getRequestUrl(HttpAddress.Request_type.thrid_login);
            param = HttpAddress.getParamsForPost(new String[]{userInfo.userRelationId, userInfo.userRelationType + ""},
                    HttpAddress.Request_type.thrid_login, this.mBaseActivity);

        } else {
            if (TextUtils.isEmpty(userInfo.userPassword)) {
                return;
            }

            UbtLog.d(TAG, "userInfo userPhone = " + userInfo.userPhone + "	userEmail = " + userInfo.userEmail + "	userPassword = " + userInfo.userPassword + "   convertMD5 = " + Md5.convertMD5(userInfo.userPassword));

            url = HttpAddress.getRequestUrl(HttpAddress.Request_type.login);

            if (!TextUtils.isEmpty(userInfo.userPhone)) {
                param = HttpAddress.getParamsForPost(new String[]{userInfo.userPhone, Md5.convertMD5(userInfo.userPassword)},
                        HttpAddress.Request_type.login, this.mBaseActivity);

            } else {
                param = HttpAddress.getParamsForPost(new String[]{userInfo.userEmail, Md5.convertMD5(userInfo.userPassword)},
                        HttpAddress.Request_type.login, this.mBaseActivity);
            }
        }

        UbtLog.d(TAG, "url = " + url);
        UbtLog.d(TAG, "param = " + param);

        OkHttpClientUtils
                .getJsonByPostRequest(url, param, -1)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.d(TAG, "initCourseAccessToken exception : " + e.getMessage() + "   mContext = " + mContext);
                        //mHandler.sendEmptyMessage(DO_GET_COURSE_TOKEN_FAIL);
                        if (mContext instanceof BlocklyCourseActivity) {
                            LessonEvent lessonEvent = new LessonEvent(LessonEvent.Event.DO_GET_COURSE_ACCESS_TOKEN_FAIL);
                            EventBus.getDefault().post(lessonEvent);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        BaseResponseModel<List<UserInfo>> baseResponseModel = GsonImpl.get().toObject(response, new TypeToken<BaseResponseModel<List<UserInfo>>>() {
                        }.getType());

                        UbtLog.d(TAG, "initCourseAccessToken response " + response + "   mContext = " + mContext);
                        if (baseResponseModel.status) {
                            UserInfo current_uer = baseResponseModel.models.get(0);

                            mCourseAccessToken = current_uer.accessToken;
                            if (mContext instanceof BlocklyCourseActivity) {
                                LessonEvent lessonEvent = new LessonEvent(LessonEvent.Event.DO_GET_COURSE_ACCESS_TOKEN_SUCCESS);
                                EventBus.getDefault().post(lessonEvent);
                            }
                        } else {
                            if (mContext instanceof BlocklyCourseActivity) {
                                LessonEvent lessonEvent = new LessonEvent(LessonEvent.Event.DO_GET_COURSE_ACCESS_TOKEN_FAIL);
                                EventBus.getDefault().post(lessonEvent);
                            }
                        }
                    }
                });
    }


    //设置当前是否充电中
    private void setChargingState(boolean charging) {
        isCharging = charging;
    }

    //获取当前是否充电中
    public boolean getChargingState() {
        return isCharging;
    }


}
