package com.ubt.alpha1e.behaviorhabits.helper;

import android.content.Context;
import android.graphics.Bitmap;

import com.ubt.alpha1e.behaviorhabits.event.HibitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.EventPlayStatus;
import com.ubt.alpha1e.data.model.NetworkInfo;
import com.ubt.alpha1e.event.NetworkEvent;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/11/20.
 */

public class HabitsHelper extends BaseHelper {

    private static final String TAG = HabitsHelper.class.getSimpleName();

    public HabitsHelper(Context context) {
        super(context);
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);

        UbtLog.d(TAG,"cmd = " + cmd + "    = " + param[0]);
        if(cmd == ConstValue.DV_CONTROL_HIBITS_PLAY){
            HibitsEvent hibitsEvent = new HibitsEvent(HibitsEvent.Event.CONTROL_PLAY);
            hibitsEvent.setStatus(param[0]);
            EventBus.getDefault().post(hibitsEvent);
        }else if(cmd == ConstValue.DV_READ_HIBITS_PLAY_STATUS){

            String eventPlayStatusJson = BluetoothParamUtil.bytesToString(param);

            UbtLog.d(TAG,"cmd = " + cmd + "    eventPlayStatusJson = " + eventPlayStatusJson);
            EventPlayStatus eventPlayStatus = GsonImpl.get().toObject(eventPlayStatusJson,EventPlayStatus.class);

            HibitsEvent playStatusEvent = new HibitsEvent(HibitsEvent.Event.READ_EVENT_PLAY_STATUS);
            playStatusEvent.setEventPlayStatus(eventPlayStatus);
            EventBus.getDefault().post(eventPlayStatus);
        }

    }

    public void doTurnVol() {

        byte[] papram = new byte[1];
        if (mCurrentVoiceState) {
            papram[0] = 0;
            mCurrentVoiceState = false;
        } else {
            papram[0] = 1;
            mCurrentVoiceState = true;
        }

        doSendComm(ConstValue.DV_VOICE, papram);

    }

    public void doChangeVol(int _pow) {

        int pow = _pow * 255;
        int add_val = 0;
        if (_pow % 100 >= 50) {
            add_val = 1;
        }
        pow /= 100;
        pow += add_val;
        mCurrentVolume = pow;

        MyLog.writeLog("音量检测", "发送调整指令" + pow);

        byte[] param = new byte[1];
        param[0] = (byte) (pow & 0xff);
        doSendComm(ConstValue.DV_VOLUME, param);
    }

    public void playEventSound(String eventId, String playAudioSeq, String audioState){

        String params = BluetoothParamUtil.paramsToJsonString(new String[]{ eventId,playAudioSeq,audioState}, ConstValue.DV_DO_NETWORK_CONNECT);
        UbtLog.d(TAG,"playEventSound = " + params);
        doSendComm(ConstValue.DV_CONTROL_HIBITS_PLAY, BluetoothParamUtil.stringToBytes(params));
    }

    public void readPlayStatus(){

        UbtLog.d(TAG,"--readPlayStatus--");
        doSendComm(ConstValue.DV_READ_NETWORK_STATUS, null);
    }

    @Override
    public void onDeviceDisConnected(String mac) {
        UbtLog.d(TAG,"--onDeviceDisConnected--" + mac );

        super.onDeviceDisConnected(mac);
    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {

    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {

    }

    @Override
    public void DistoryHelper() {

    }
}
