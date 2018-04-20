package com.ubt.alpha1e.onlineaudioplayer.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;

import com.ubt.alpha1e.behaviorhabits.event.HibitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.EventPlayStatus;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.PlayerEvent;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Administrator on 2017/11/20.
 */

public class OnlineAudioResourcesHelper extends BaseHelper {

    private static final String TAG = OnlineAudioResourcesHelper.class.getSimpleName();


    MediaPlayer   mediaPlayer;
    private boolean local_player=true;
    private int songIndex=-1;
    private List<AudioContentInfo> mSongList;
    public OnlineAudioResourcesHelper(Context context) {
        super(context);
    }

    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);

        //UbtLog.d(TAG,"cmd = " + cmd + "    = " + param[0]);
        if(cmd == ConstValue.DV_ONLINEPLAYER_PLAY){
            UbtLog.d(TAG,"cmd = " + cmd + "    param[0] = " + param[0]);
            if(param[0]==0x01) {
            }else if(param[0]==0x02){
                sendEventBusMesssage();
            }
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

    public void playEvent( String url,int index){
        String params = url;
        UbtLog.d(TAG,"playEventSound = " + params);
        setCurentPlayingAudioIndex(index);
        doSendComm(ConstValue.DV_ONLINEPLAYER_PLAY, BluetoothParamUtil.stringToBytes(url));
        if(local_player) {
            mediaPlayer = MediaPlayer.create(mContext, Uri.parse(url));
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    sendEventBusMesssage();
                }
            });
        }
    }

    private void sendEventBusMesssage() {
        PlayerEvent mPlayerEvent=new PlayerEvent(PlayerEvent.Event.CONTROL_PLAY_NEXT);
        mPlayerEvent.setCurrentPlayingSongName(mSongList.get(getCurrentPlayingAudioIndex()).contentName);
        EventBus.getDefault().post(mPlayerEvent);
    }

    public void stopEvent(){
        UbtLog.d(TAG,"stopEventSound = " );
        byte[] mCmd={0};
        mCmd[0]=0;
        doSendComm(ConstValue.DV_ONLINEPLAYER_STOP, mCmd);
        if(local_player) {
            if(mediaPlayer!=null) {
                mediaPlayer.stop();
            }
        }
    }
    public void pauseEvent( String url){
        String params = url;
        UbtLog.d(TAG,"pauseEventSound = " + params);
        byte[] mCmd={0};
        mCmd[0]=0;
        doSendComm(ConstValue.DV_ONLINEPLAYER_PAUSE, mCmd);
    }
    public void continueEvent( String url){
        String params = url;
        UbtLog.d(TAG,"continueEventSound = " + params);
        byte[] mCmd={0};
        mCmd[0]=0;
        doSendComm(ConstValue.DV_ONLINEPLAYER_CONTINUE, mCmd);
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

    public void setPlayContentInfo(List<AudioContentInfo> mAudioList){
            mSongList=mAudioList;
    }
    public List<AudioContentInfo> getPlayContentInfo(){
            return mSongList;
    }

    public void setCurentPlayingAudioIndex(int index){
         songIndex=index;
    }
    public int getCurrentPlayingAudioIndex(){
        return songIndex;
    }


}
