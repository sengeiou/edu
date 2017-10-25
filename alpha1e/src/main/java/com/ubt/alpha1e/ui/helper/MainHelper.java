package com.ubt.alpha1e.ui.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.business.ActionPlayer;
import com.ubt.alpha1e.business.MessageRecordManager;
import com.ubt.alpha1e.business.MessageRecordManagerListener;
import com.ubt.alpha1e.business.NewActionPlayer;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.data.ISharedPreferensListenet;
import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.data.model.UpgradeProgressInfo;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.net.http.basic.IImageListener;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.EventBus;

public class MainHelper extends BaseHelper implements ISharedPreferensListenet,
        IImageListener {

    private static final String TAG = "MainHelper";

    private IMainUI mUI;
    // -------------------------------
    private static final int MSG_DO_NOTE_CHARGING = 1001;
    private static final int MSG_DO_UPDATE_BATTERY = 1002;
    private static final int MSG_DO_NOTE_DISCOON = 1003;
    private static final int MSG_DO_NOTE_LIGHT_ON = 1004;
    private static final int MSG_DO_NOTE_LIGHT_OFF = 1005;
    private static final int MSG_DO_NOTE_VOL = 1006;
    private static final int MSG_DO_NOTE_VOL_STATE = 1007;
    private static final int MSG_DO_READ_USER_HEAD_IMG = 1008;
    public static final int  SCAN_ACTIVITY_REQUEST_CODE = 10086;
    // -------------------------------
    private int do_clear_login_info = 10001;
    // -------------------------------

    private String battery_charge_pro_state;
    private static final String battery_charge_state_doing = "battery_charge_state_doing";
    private static final String battery_charge_state_waiting = "battery_charge_state_waiting";
    // -------------------------------
    private Boolean light_state = null;
    private boolean mCurrentVolState;
    private int mCurrentVol;

    public void doRegisterListenerUI(IMainUI _mUI) {
        UbtLog.d("MainHelper", "--wmma--doRegisterListenerUI!");
        mUI = _mUI;
    }

    public void doUnRegisterListenerUI(IMainUI _mUI) {
        mUI = null;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_DO_READ_USER_HEAD_IMG) {
                if (msg.obj != null) {
                    try {
                        mUI.onReadHeadImgFinish(true, (Bitmap) msg.obj);
                        mBaseActivity.onReadHeadImgFinish(true,(Bitmap) msg.obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mUI != null){
                        mUI.onReadHeadImgFinish(false, null);
                    }
                }
            }
            if (msg.what == MSG_DO_NOTE_CHARGING) {
                if (mUI != null){
                    mUI.noteCharging();
                }

                EventBus.getDefault().post(new RobotEvent(RobotEvent.Event.CHARGING));
            }
            if (msg.what == MSG_DO_UPDATE_BATTERY) {
                if (mUI != null){
                    mUI.updateBattery((Integer) msg.obj);
                }

                if(EventBus.getDefault().hasSubscriberForEvent(RobotEvent.class)) {
                    RobotEvent batteryEvent = new RobotEvent(RobotEvent.Event.UPDATING_POWER);
                    batteryEvent.setPower((Integer) msg.obj);
                    EventBus.getDefault().post(batteryEvent);
                }

            }
            if (msg.what == MSG_DO_NOTE_DISCOON) {
                if (mUI != null){
                    mUI.noteDiscoonected();
                }
                if(EventBus.getDefault().hasSubscriberForEvent(RobotEvent.class)) {
                    RobotEvent disconnectEvent = new RobotEvent(RobotEvent.Event.DISCONNECT);
                    EventBus.getDefault().post(disconnectEvent);
                }
            }
            if (msg.what == MSG_DO_NOTE_LIGHT_ON) {
                if (mUI != null){
                    mUI.noteLightOn();
                }
            }
            if (msg.what == MSG_DO_NOTE_LIGHT_OFF) {
                if (mUI != null){
                    mUI.noteLightOff();
                }
            }
            if (msg.what == MSG_DO_NOTE_VOL) {
                // MyLog.writeLog(" ������������", "��������ֵ-->" + mCurrentVol);
                // for (int i = 0; i < mUIs.size(); i++) {
                // mUIs.get(i).onNoteVol((mCurrentVol * 100) / 255);
                // }
                int value = mCurrentVol;
                value *= 100;
                int add_val = 0;
                if (value % 255 >= 127.5) {
                    add_val = 1;
                }
                value /= 255;
                value += add_val;
                if (mUI != null){
                    mUI.onNoteVol(value);
                }
            }
            if (msg.what == MSG_DO_NOTE_VOL_STATE) {
                if (mUI != null){
                    mUI.onNoteVolState(mCurrentVolState);
                }
            }
        }
    };

    public boolean isFirstUseMain() {
        if (BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                .doReadSync(BasicSharedPreferencesOperator.IS_FIRST_USE_MAIN_KEY)
                .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_MAIN_VALUE_TRUE))
            return false;
        return true;
    }

    public void changeFirstUseMainState() {
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_FIRST_USE_MAIN_KEY,
                BasicSharedPreferencesOperator.IS_FIRST_USE_MAIN_VALUE_TRUE,
                null, -1);
    }

    public void doStopAllActions() {
        if (NewActionPlayer.getPlayer() != null && NewActionPlayer.getPlayer().getState() != NewActionPlayer.PlayerState.STOPING)
            NewActionPlayer.getPlayer().StopPlayer();
        else if (ActionPlayer.getInstance() != null) {
            ActionPlayer.getInstance().doStopPlay();
        }
    }

    public MainHelper(BaseActivity baseActivity) {
        super(baseActivity);
        if (((AlphaApplication) mBaseActivity.getApplicationContext())
                .getBlueToothManager() != null) {
            UbtLog.d("MainHelper", "--wmma--new MainHelper!");
            battery_charge_pro_state = battery_charge_state_waiting;

        }
    }

    public void doReadState() {

        battery_charge_pro_state = battery_charge_state_waiting;

    }

    public String getCurrentName() {
        return ((AlphaApplication) mBaseActivity.getApplicationContext())
                .getCurrentDeviceName();
    }

    public void doTurnLight() {

        if (light_state == null)
            return;
        byte[] papram = new byte[1];
        if (light_state) {
            papram[0] = 0;// �ر�
        } else {
            papram[0] = 1;// ��
        }
        doSendComm(ConstValue.DV_LIGHT, papram);

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeviceDisConnected(String mac) {
        ((AlphaApplication) mBaseActivity.getApplicationContext())
                .setCurrentBluetooth(null);

        Message msg = new Message();
        msg.what = MSG_DO_NOTE_DISCOON;
        mHandler.sendMessage(msg);
        UnRegisterHelper();

    }

    public void UnRegisterHelper() {
        super.UnRegisterHelper();
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
        if (cmd == ConstValue.DV_READ_BATTERY) {
            byte charge = param[2];
            byte power = param[3];
            if (charge == 0x01) {
                // 上报充电中
                if (battery_charge_pro_state
                        .equals(battery_charge_state_waiting)) {
                    Message msg = new Message();
                    msg.what = MSG_DO_NOTE_CHARGING;
                    mHandler.sendMessage(msg);
                    battery_charge_pro_state = battery_charge_state_doing;
                }

            } else {
                int i = power;
                Message msg = new Message();
                msg.what = MSG_DO_UPDATE_BATTERY;
                msg.obj = i;
                mHandler.sendMessage(msg);
                battery_charge_pro_state = battery_charge_state_waiting;
            }

            byte[] params = new byte[1];
            params[0] = 0;

        } else if ((cmd & 0xff) == (ConstValue.DV_READSTATUS & 0xff)) {

            // ����״̬
            if (param[0] == 0) {
                // ����
                if (param[1] == 1) {

                    mCurrentVolState = false;
                }
                // ������
                else {

                    mCurrentVolState = true;
                }
                Message msg = new Message();
                msg.what = MSG_DO_NOTE_VOL_STATE;
                mHandler.sendMessage(msg);

            }
            // ����״̬
            else if (param[0] == 1) {
                // ��ͣ
                if (param[1] == 0) {

                }
                // ����ͣ
                else {

                }
            }
            // ����״̬
            else if (param[0] == 2) {

                int nCurrentVolume = param[1];
                if (nCurrentVolume < 0) {
                    nCurrentVolume += 256;
                }

                mCurrentVol = nCurrentVolume;
                if (mCurrentVolState) {
                    Message msg = new Message();
                    msg.what = MSG_DO_NOTE_VOL;
                    mHandler.sendMessage(msg);
                }
            }
            // �����״̬
            else if (param[0] == 3) {
                // ��
                if (param[1] == 0) {
                    light_state = false;
                    Message msg = new Message();
                    msg.what = MSG_DO_NOTE_LIGHT_OFF;
                    mHandler.sendMessage(msg);
                }
                // ��
                else {
                    light_state = true;
                    Message msg = new Message();
                    msg.what = MSG_DO_NOTE_LIGHT_ON;
                    mHandler.sendMessage(msg);
                }
            } else if (param[0] == 4) {
                // �γ�
                if (param[1] == 0) {

                }
                // ����
                else {

                }
            }
        }else if(cmd == ConstValue.DV_READ_NETWORK_STATUS){

            String networkInfoJson = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG,"cmd = " + cmd + "    networkInfoJson = " + networkInfoJson);

            NetworkInfo networkInfo = GsonImpl.get().toObject(networkInfoJson,NetworkInfo.class);

            RobotEvent event = new RobotEvent(RobotEvent.Event.NETWORK_STATUS);
            event.setNetworkInfo(networkInfo);
            EventBus.getDefault().post(event);

        }else if(cmd == ConstValue.DV_READ_AUTO_UPGRADE_STATE ){
            UbtLog.d(TAG,"cmd = " + cmd + "    param " + ConstValue.DV_READ_AUTO_UPGRADE_STATE);

            RobotEvent event = new RobotEvent(RobotEvent.Event.AUTO_UPGRADE_STATUS);
            event.setAutoUpgradeStatus(param[0]);
            EventBus.getDefault().post(event);
        }else if(cmd == ConstValue.DV_SET_AUTO_UPGRADE){

            RobotEvent event = new RobotEvent(RobotEvent.Event.SET_AUTO_UPGRADE_STATUS);
            event.setAutoUpgradeStatus(param[0]);
            EventBus.getDefault().post(event);
        }else if(cmd == ConstValue.DV_DO_UPGRADE_PROGRESS){

            String upgradeProgressJson = BluetoothParamUtil.bytesToString(param);

            boolean flag = EventBus.getDefault().isRegistered(RobotEvent.class);
            boolean flag1 = EventBus.getDefault().hasSubscriberForEvent(RobotEvent.class);

            UbtLog.d(TAG,"flag = " + flag + "   flag1 = " + flag1);
            UpgradeProgressInfo upgradeProgressInfo = GsonImpl.get().toObject(upgradeProgressJson,UpgradeProgressInfo.class);
            RobotEvent event = new RobotEvent(RobotEvent.Event.UPGRADE_PROGRESS);
            event.setUpgradeProgressInfo(upgradeProgressInfo);
            EventBus.getDefault().post(event);
        }
    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {
        // TODO Auto-generated method stub

    }

    /**
     * 读取 1E 联网状态
     */
    public void readNetworkStatus(){
        UbtLog.d(TAG,"--readNetworkStatus-- " );
        doSendComm(ConstValue.DV_READ_NETWORK_STATUS, null);
    }

    /**
     * 读取 1E 升级状态
     */
    public void readAutoUpgradeStatus(){
        UbtLog.d(TAG,"--readAutoUpgradeStatus-- " );
        doSendComm(ConstValue.DV_READ_AUTO_UPGRADE_STATE, null);
    }

    /**
     * 改变 1E 自动升级状态
     * @param is0pen false 为未开启， true为已开启
     */
    public void doChangeAutoUpgrade(boolean is0pen) {

        byte[] params = new byte[1];
        if(is0pen){
            params[0] = 1;
        }else {
            params[0] = 0;
        }
        doSendComm(ConstValue.DV_SET_AUTO_UPGRADE, params);
    }

    public void doLostCoon(Activity act) {
        ((AlphaApplication) mBaseActivity.getApplicationContext()).doLostConn(act);
        EventBus.getDefault().post(new RobotEvent(RobotEvent.Event.DISCONNECT));
    }

    public void doGotoPcUpdate(Activity act) {
        ((AlphaApplication) mBaseActivity.getApplicationContext()).doGotoPcUpdate(
                act);
    }

    @Override
    public void onSharedPreferenOpreaterFinish(boolean isSuccess,
                                               long request_code, String value) {
        if (request_code == do_clear_login_info) {
            // 重启app
            doReStartApp();
        }

        if (do_read_user_request == request_code) {
            MyLog.writeLog("MainHelper", "UserInfo value=" + value);
        }

    }

    public void doReStartApp() {
        ((AlphaApplication) mBaseActivity.getApplicationContext())
                .doExitApp(false);
        ((AlphaApplication) mBaseActivity.getApplicationContext())
                .doRestartApp();
    }

    @Override
    public void DistoryHelper() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
        if (readUserHeadImgRequest == request_code) {
            if (isSuccess) {
                Message msg = new Message();
                msg.obj = bitmap;
                msg.what = MSG_DO_READ_USER_HEAD_IMG;
                mHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.obj = null;
                msg.what = MSG_DO_READ_USER_HEAD_IMG;
                mHandler.sendMessage(msg);
            }
        }
    }

    public void doCheckIsAnyUnReadMessage(MessageRecordManagerListener listener) {
        MessageRecordManager.getInstance(mBaseActivity, getCurrentUser(),
                listener).doGetMessages(2,1,11);
    }

    public void doCheckIsAnyUnReadMsgByType(MessageRecordManagerListener listener,int type,int pageSize) {
        MessageRecordManager.getInstance(mBaseActivity, getCurrentUser(),
                listener).doGetMessages(type,1,pageSize);
    }

    public boolean isFirstEditMain() {
        if (BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.IS_FIRST_USE_EDIT_ACTION)
                .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_EDIT_ACTION_FALSE))
            return false;
        return true;
    }

    public boolean isFirstUseLeft() {
        if (BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.IS_FIRST_USE_LEFT)
                .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_LEFT_FALSE)) {

            return false;
        }
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_FIRST_USE_LEFT,
                BasicSharedPreferencesOperator.IS_FIRST_USE_LEFT_FALSE,
                null, -1);
        return true;
    }




    public boolean isFirstUseTheme() {
        if (BasicSharedPreferencesOperator
                .getInstance(mBaseActivity, DataType.USER_USE_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.IS_FIRST_USE_THEME)
                .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_THEME_FALSE)) {

            return false;
        }
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.USER_USE_RECORD).doWrite(
                BasicSharedPreferencesOperator.IS_FIRST_USE_THEME,
                BasicSharedPreferencesOperator.IS_FIRST_USE_THEME_FALSE,
                null, -1);
        return true;

    }

    private int do_read_user_request = 11001;
    public void doReadUser() {
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.USER_USE_RECORD).doReadAsync(
                BasicSharedPreferencesOperator.LOGIN_USER_INFO, this,
                do_read_user_request);
    }


}
