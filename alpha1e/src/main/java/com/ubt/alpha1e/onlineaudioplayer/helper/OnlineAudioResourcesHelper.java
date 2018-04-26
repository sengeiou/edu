package com.ubt.alpha1e.onlineaudioplayer.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.event.HibitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.EventPlayStatus;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.PlayerEvent;
import com.ubt.alpha1e.onlineaudioplayer.playerDialog.OnlineAudioPlayDialog;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/20.
 */

public class OnlineAudioResourcesHelper extends BaseHelper {

    private static final String TAG = OnlineAudioResourcesHelper.class.getSimpleName();
    private static List<AudioContentInfo> mPlayContentInfoList =  new ArrayList<>();
    private static List<AudioContentInfo> mPlayContentOriginInfoList =new ArrayList<>();
    private int currentPlaySeq = -1;
    private boolean isRecyclePlaying=false;
    private int mAlbumId;

    private static OnlineAudioResourcesHelper mOnlineAudioResourcesHelper=null;

    public int getPlayType() {
        return mPlayType;
    }

    public void setPlayType(int mPlayType) {
        this.mPlayType = mPlayType;
    }

    private int mPlayType=OnlineAudioPlayDialog.ORDER_AUDIO_LIST_PLAYING;
    MediaPlayer   mediaPlayer;
    //TEST PURPOSE LOCAL PLAYRING AUDIO
    private boolean local_player=true;
    public OnlineAudioResourcesHelper(Context context) {
        super(context);
    }
    public static OnlineAudioResourcesHelper getInstance(Context mContext){
        if(mOnlineAudioResourcesHelper==null){
            mOnlineAudioResourcesHelper=new OnlineAudioResourcesHelper(mContext);
        }
        return mOnlineAudioResourcesHelper;
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

        }else if(cmd==ConstValue.DV_TAP_HEAD){
            PlayerEvent mPlayerEvent=new PlayerEvent(PlayerEvent.Event.TAP_HEAD);
            EventBus.getDefault().post(mPlayerEvent);
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
        UbtLog.d(TAG,"playEventSound = " + params+"index"+index);
        setCurentPlayingAudioIndex(index);
        doSendComm(ConstValue.DV_ONLINEPLAYER_PLAY, BluetoothParamUtil.stringToBytes(url));
        for(AudioContentInfo mPlayContentInfo : getPlayContent()){
            mPlayContentInfo.isPlaying = false;
        }
        getPlayContent().get(index).isPlaying =true;
        PlayerEvent playStatusEvent = new PlayerEvent(PlayerEvent.Event.READ_EVENT_PLAY_STATUS);
        playStatusEvent.setCurrentPlayingIndex(index);
        EventBus.getDefault().post(playStatusEvent);
        if(local_player) {
            if(mediaPlayer!=null){
                mediaPlayer.stop();
            }
            mediaPlayer = MediaPlayer.create(mContext, Uri.parse(url));
            if(mediaPlayer!=null) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        UbtLog.d(TAG, "EMULATE THE PALY ENDING");
                        sendEventBusMesssage();
                    }
                });
            }
        }
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
    private void sendEventBusMesssage() {
        PlayerEvent mPlayerEvent=new PlayerEvent(PlayerEvent.Event.CONTROL_PLAY_NEXT);
        //NEXT AUDIO NAME INFORMATION
        mPlayerEvent.setCurrentPlayingSongName(mPlayContentInfoList.get(getCurrentPlayingAudioIndex()+1).contentName);
        EventBus.getDefault().post(mPlayerEvent);
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


    public void setCurentPlayingAudioIndex(int index) {
        currentPlaySeq = index;
    }

    public int getCurrentPlayingAudioIndex() {
        return  currentPlaySeq;
    }
    public void autoNextAudioPlay(){
        if(getPlayType()== OnlineAudioPlayDialog.ORDER_AUDIO_LIST_PLAYING) {
            if ((currentPlaySeq + 1) < mPlayContentInfoList.size()) {
                currentPlaySeq++;
            } else {
                    PlayerEvent mPlayerEvent = new PlayerEvent(PlayerEvent.Event.CONTROL_STOP);
                    EventBus.getDefault().post(mPlayerEvent);
            }
            stopEvent();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else if(getPlayType()==OnlineAudioPlayDialog.RECYCLE_AUDIO_LIST_PLAYING){
            if ((currentPlaySeq + 1) > mPlayContentInfoList.size()) {
                currentPlaySeq = 0;
            }
            currentPlaySeq++;
        }else if(getPlayType()==OnlineAudioPlayDialog.SINGLE_AUDIO_PLAYING){
             //NOTHING TO DO
        }
        playEvent(mPlayContentInfoList.get(currentPlaySeq).contentUrl, currentPlaySeq);
    }
    public void nextAudioPlay(){
            if((currentPlaySeq + 1) < mPlayContentInfoList.size()){
                currentPlaySeq++;
            }
            else {
            if(isRecyclePlaying) {
                currentPlaySeq = 0;
            }else{
                PlayerEvent mPlayerEvent=new PlayerEvent(PlayerEvent.Event.CONTROL_STOP);
                EventBus.getDefault().post(mPlayerEvent);
            }
        }
            stopEvent();
            try{
                Thread.sleep(200);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        playEvent(mPlayContentInfoList.get(currentPlaySeq).contentUrl, currentPlaySeq);
    }
    public void prevAudioPlay(){
            if((currentPlaySeq -1) >= 0){
                currentPlaySeq--;
            }else {
                currentPlaySeq = mPlayContentInfoList.size() -1;
            }
           // isPause = false;
           // currentPlayInfo = mPlayContentInfoList.get(currentPlaySeq);
            stopEvent();
            try{
                Thread.sleep(200);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            playEvent(mPlayContentInfoList.get(currentPlaySeq).contentUrl,currentPlaySeq);
         //   ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
         //   mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
    }
    public void autoAudioPlay(){
        if(currentPlaySeq < 0){
            currentPlaySeq = 0;
        }
        stopEvent();
        try{
                Thread.sleep(200);
            }catch(InterruptedException e){
                e.printStackTrace();
        }
        if(mPlayContentInfoList.size()!=0) {
            playEvent(mPlayContentInfoList.get(currentPlaySeq).contentUrl,currentPlaySeq);
        }


    }


    public  void updatePlayContentInfoList(){
//        mPlayContentInfoList.clear();
//        for(int i=0;i<mPlayContentOriginInfoList.size();i++) {
//            if (mPlayContentOriginInfoList.get(i).isSelect){
//                mPlayContentInfoList.add(mPlayContentOriginInfoList.get(i));
//            }
//        }
    }
    public List<AudioContentInfo> getPlayContent() {
        return mPlayContentInfoList;
    }

    public void setPlayContent(List<AudioContentInfo> playContentInfoList) {
        mPlayContentInfoList = playContentInfoList;
        mPlayContentOriginInfoList.clear();
        currentPlaySeq=-1;

        for(int i=0;i<mPlayContentInfoList.size();i++){
            mPlayContentOriginInfoList.add(mPlayContentInfoList.get(i));
        }
        for(int i = 0; i< mPlayContentInfoList.size();i++){
            UbtLog.d(TAG,"i = " + i + "     url = " /*+ mPlayContentInfoList.get(i).contentName + "/"*/ + mPlayContentInfoList.get(i).contentUrl);
        }
        UbtLog.d(TAG,"mPlayContentInfoList.size() = " + mPlayContentInfoList.size());
    }

    public void setAlbumId(int albumId) {
        mAlbumId = albumId;
    }

    public int getAlbumId() {
        return mAlbumId;
    }


}
