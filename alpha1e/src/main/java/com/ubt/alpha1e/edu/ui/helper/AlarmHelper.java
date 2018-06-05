package com.ubt.alpha1e.edu.ui.helper;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.edu.data.ISharedPreferensListenet;
import com.ubt.alpha1e.edu.ui.BaseActivity;
import com.ubtechinc.base.AlarmInfo;
import com.ubtechinc.base.ConstValue;

import java.io.UnsupportedEncodingException;

public class AlarmHelper extends BaseHelper implements ISharedPreferensListenet {

    private IAlarmUI mUI;
    // -------------------------------
    private static final int MSG_DO_READ_ALARM = 1001;
    private static final int MSG_ON_GET_ALARM_NAME = 1002;
    // -------------------------------
    private static final int request_get_alarm_name = 10001;
    // -------------------------------
    public static final String alarm_info = "alarm_info";
    public static final String alarm_name = "alarm_name";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_DO_READ_ALARM) {

                if (msg.obj == null) {
                    mUI.onNoteNoAlarm();
                } else {
                    mUI.onReadAlarmFinish((AlarmInfo) msg.obj);
                }
            }
            if (msg.what == MSG_ON_GET_ALARM_NAME) {
                mUI.onReadAlarmName((String) msg.obj);
            }
        }
    };

    public AlarmHelper(IAlarmUI _ui, BaseActivity _baseActivity) {
        super(_baseActivity);
        this.mUI = _ui;
        this.RegisterHelper();
    }


    public void doReadAlarm() {

        byte[] param = new byte[1];
        param[0] = 0;

        this.doSendComm(ConstValue.DV_READ_ALARM, param);

    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
        if (cmd == ConstValue.DV_READ_ALARM) {

            if (len == 1) {
                // 读取失败
                Message msg = new Message();
                msg.what = MSG_DO_READ_ALARM;
                msg.obj = null;
                mHandler.sendMessage(msg);
            } else {
                // 读取成功
                AlarmInfo alarmInfo = new AlarmInfo();
                if (param[0] == 1) {
                    alarmInfo.setOpen(true);
                }
                if (param[1] == 1) {
                    alarmInfo.setRepaet(true);
                }
                alarmInfo.setHh(param[2]);
                alarmInfo.setMm(param[3]);
                alarmInfo.setSs(param[4]);
                byte[] actionNameParam = new byte[param[5]];
                System.arraycopy(param, 6, actionNameParam, 0,
                        actionNameParam.length);
                String name = "";
                try {
                    name = new String(actionNameParam, "GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                alarmInfo.setActionName(name);
                Message msg = new Message();
                msg.what = MSG_DO_READ_ALARM;
                msg.obj = alarmInfo;
                mHandler.sendMessage(msg);

            }
        } else if (cmd == ConstValue.DV_WRITE_ALARM) {
            if (param[0] == 0) {
                // 成功
                mUI.onNoteWriteAlarmFinish(true);
            } else {
                // 失败
                mUI.onNoteWriteAlarmFinish(false);
            }
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

    public void doEdit() {
        // TODO Auto-generated method stub
        mUI.onNoteEdit();
    }

    public void doWriteAlarm(AlarmInfo info) {

        if (info == null) {
            mUI.onNoteEditAlarmCancel();
            return;
        }

        byte[] param = packWriteAlarmInfo(info);

        this.doSendComm(ConstValue.DV_WRITE_ALARM, param);

        mUI.onNoteWriteAlarmStart();
    }

    private byte[] packWriteAlarmInfo(AlarmInfo info) {
        byte usable = 0;
        byte repeat = 0;
        if (info.isOpen) {
            usable = 1;
        }
        if (info.isRepaet) {
            repeat = 1;
        }
        byte hh = info.hh;
        byte mm = info.mm;
        byte ss = info.ss;

        try {
            byte[] name = info.actionName.getBytes("GBK");
            byte len = (byte) name.length;
            byte[] param = new byte[6 + len];

            param[0] = usable;
            param[1] = repeat;
            param[2] = hh;
            param[3] = mm;
            param[4] = ss;
            param[5] = len;
            System.arraycopy(name, 0, param, 6, len);
            return param;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void doWriteAlarmInfo(String info) {

        if (!mBaseActivity.checkCoon()) {
            return;
        }

        String msg = ((AlphaApplication) mBaseActivity.getApplicationContext())
                .getCurrentBluetooth().getAddress() + "/" + info;
        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.USER_USE_RECORD)
                .doWrite(BasicSharedPreferencesOperator.ALARM_SET_RECORDS, msg,
                        null, -1);
    }

    public void doGetAlarmName() {

        BasicSharedPreferencesOperator.getInstance(mBaseActivity,
                DataType.USER_USE_RECORD).doReadAsync(
                BasicSharedPreferencesOperator.ALARM_SET_RECORDS, this,
                request_get_alarm_name);
    }

    @Override
    public void onSharedPreferenOpreaterFinish(boolean isSuccess,
                                               long request_code, String value) {
        if (request_code == request_get_alarm_name) {
            if (!value.equals(BasicSharedPreferencesOperator.NO_VALUE)) {
                if (!mBaseActivity.checkCoon()) {
                    return;
                }
                String mac = value.split("/")[0];
                if (mac.equals(((AlphaApplication) mBaseActivity
                        .getApplicationContext()).getCurrentBluetooth()
                        .getAddress())) {
                    Message msg = new Message();
                    msg.what = MSG_ON_GET_ALARM_NAME;
                    msg.obj = value.split("/")[1];
                    mHandler.sendMessage(msg);
                }

            }
        }

    }

    @Override
    public void onDeviceDisConnected(String mac) {
        // TODO Auto-generated method stub

    }

    @Override
    public void DistoryHelper() {
        // TODO Auto-generated method stub
        this.UnRegisterHelper();
    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
        // TODO Auto-generated method stub

    }
}
