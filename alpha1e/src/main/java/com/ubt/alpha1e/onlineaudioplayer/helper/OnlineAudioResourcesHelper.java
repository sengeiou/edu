package com.ubt.alpha1e.onlineaudioplayer.helper;

import android.content.Context;
import android.graphics.Bitmap;

import com.ubt.alpha1e.behaviorhabits.event.HibitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.EventPlayStatus;
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

public class OnlineAudioResourcesHelper extends BaseHelper {

    private static final String TAG = OnlineAudioResourcesHelper.class.getSimpleName();

    public OnlineAudioResourcesHelper(Context context) {
        super(context);
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);

        //UbtLog.d(TAG,"cmd = " + cmd + "    = " + param[0]);
        if(cmd == ConstValue.DV_ONLINEPLAYER_PLAY){
            UbtLog.d(TAG,"cmd = " + cmd + "    param[0] = " + param[0]);
            EventBus.getDefault().post(ConstValue.DV_ONLINEPLAYER_PLAY);
        }else if(cmd == ConstValue.DV_ONLINEPLAYER_STOP){
            String eventPlayStatusJson = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG,"cmd = " + cmd + "    eventPlayStatusJson = " + eventPlayStatusJson);
            EventBus.getDefault().post(ConstValue.DV_ONLINEPLAYER_STOP);
        }else if(cmd==ConstValue.DV_ONLINEPLAYER_PAUSE){

        }else if(cmd==ConstValue.DV_ONLINEPLAYER_CONTINUE){

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

    public void playEventSound( String url){
        String params = url;
        UbtLog.d(TAG,"playEventSound = " + params);
        doSendComm(ConstValue.DV_ONLINEPLAYER_PLAY, BluetoothParamUtil.stringToBytes(url));
    }
    public void stopEventSound( String url){
        String params = url;
        UbtLog.d(TAG,"stopEventSound = " + params);
        byte[] mCmd={0};
        mCmd[0]=0;
        doSendComm(ConstValue.DV_ONLINEPLAYER_STOP, mCmd);
    }
    public void pauseEventSound( String url){
        String params = url;
        UbtLog.d(TAG,"pauseEventSound = " + params);
        byte[] mCmd={0};
        mCmd[0]=0;
        doSendComm(ConstValue.DV_ONLINEPLAYER_PAUSE, mCmd);
    }
    public void continueEventSound( String url){
        String params = url;
        UbtLog.d(TAG,"continueEventSound = " + params);
        byte[] mCmd={0};
        mCmd[0]=0;
        doSendComm(ConstValue.DV_ONLINEPLAYER_PAUSE, mCmd);
    }

    public void readPlayStatus(){

        UbtLog.d(TAG,"--readPlayStatus-->" + ConstValue.DV_READ_HIBITS_PLAY_STATUS);
      //  doSendComm(ConstValue.DV_READ_HIBITS_PLAY_STATUS, null);
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
