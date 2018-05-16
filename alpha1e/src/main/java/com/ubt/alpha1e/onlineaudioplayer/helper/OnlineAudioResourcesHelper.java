package com.ubt.alpha1e.onlineaudioplayer.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;


import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.onlineaudioplayer.Fragment.OnlineAudioListFragment;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.PlayerEvent;
import com.ubt.alpha1e.ui.helper.BaseHelper;
import com.ubt.alpha1e.utils.BluetoothParamUtil;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ConstValue;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.util.Const;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/20.
 */

public class OnlineAudioResourcesHelper extends BaseHelper {

    private static final String TAG = OnlineAudioResourcesHelper.class.getSimpleName();
    private static List<AudioContentInfo> mPlayContentInfoList = new ArrayList<>();
    private static List<AudioContentInfo> mPlayingContentInfoList = new ArrayList<>();
//    private static List<AudioContentInfo> mPlayContentOriginInfoList = new ArrayList<>();
    private int currentPlaySeq = 0;
//    private boolean isRecyclePlaying = true;
    private String mAlbumId;

    public String getmCategoryId() {
        return mCategoryId;
    }

    public void setmCategoryId(String mCategoryId) {
        this.mCategoryId = mCategoryId;
    }

    private String mCategoryId;

    private static OnlineAudioResourcesHelper mOnlineAudioResourcesHelper = null;


    /**
     * get loop mode
     * @return
     */
    public int getPlayType() {
        return mPlayType;
    }

    /**
     * set loop mode
     * @param mPlayType
     */
    public void setPlayType(int mPlayType) {
        this.mPlayType = mPlayType;
    }

    private int mPlayType = OnlineAudioListFragment.ORDER_AUDIO_LIST_PLAYING;

    public boolean ismPlayStatus() {
        return mPlayStatus;
    }

    public void setmPlayStatus(boolean mPlayStatus) {
        this.mPlayStatus = mPlayStatus;
    }

    private boolean mPlayStatus=false;  //false play true pause;
    MediaPlayer mediaPlayer;
    //TEST PURPOSE LOCAL PLAYRING AUDIO
    private boolean local_player = false;
    //NEW PROTOCOL
    private boolean offlinePlaying_protocol=true;

    public OnlineAudioResourcesHelper(Context context) {
        super(context);
    }

    public static OnlineAudioResourcesHelper getInstance(Context mContext) {
        if (mOnlineAudioResourcesHelper == null) {
            mOnlineAudioResourcesHelper = new OnlineAudioResourcesHelper(mContext);
        }
        return mOnlineAudioResourcesHelper;
    }
    @Override
    public void onReceiveData(String mac, byte cmd, byte[] param, int len) {
        super.onReceiveData(mac, cmd, param, len);
        if(cmd==ConstValue.DV_GETONLINEPLAYER_ROBOTSTATUS){
            UbtLog.d(TAG, "cmd = " + cmd + "    param[0] = " + param[0]);
                try {
                    JSONObject mCmd = new JSONObject(BluetoothParamUtil.bytesToString(param));
                    mCmd.get("status");//play, pause, continue, stop, complete
                    mCmd.get("categoryId");
                    mCmd.get("albumId");
                    mCmd.get("index");
                    mCmd.get("loop");
                    UbtLog.d(TAG, "cmd = " + cmd + " mCmd.get(\"index\")" + mCmd.get("status") +"index : "+   mCmd.get("index")+"loop mode :"+        mCmd.get("loop")+"cmd "+mCmd.toString());
                    if (mCmd.get("status").equals("playing")) {
                        notifyUiCurrentRobotStatus(mCmd);
                    } else if (mCmd.get("status").equals("pause")) {
                        notifyUiCurrentRobotStatus(mCmd);
                    } else if (mCmd.get("status").equals("quit")) {
                        //GET ROBOT ONLINE PLAY STATUS STOP
                        notifyUiCurrentRobotStatus(mCmd);
                    } else {
                        UbtLog.d(TAG, "0X55 " + mCmd.get("status"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        } else if (cmd == ConstValue.DV_NOTIFYONLINEPLAYER_PLAY) {
            UbtLog.d(TAG, "cmd = " + cmd + "    param[0] = " + param[0]);
            if (param[0] == 0x01) {
                UbtLog.d(TAG, "cmd = " + cmd + "    reply" + param[0]);
            }else {
                UbtLog.d(TAG, "cmd = " + cmd + "  next audio notify" + BluetoothParamUtil.bytesToString(param));
                    try {
                        JSONObject mCmd = new JSONObject(BluetoothParamUtil.bytesToString(param));
                        mCmd.get("status");//play, pause, continue, stop, complete,next
                        mCmd.get("categoryId");
                        mCmd.get("albumId");
                        mCmd.get("index");
                        UbtLog.d(TAG, "cmd = " + cmd + "  next audio notify:   " + mCmd.get("status") +" next index : "+   mCmd.get("index"));
                        if (mCmd.get("status").equals("next")) {
                            notifyUiNextAudio(Integer.parseInt(mCmd.get("index").toString()));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }

        } else if (cmd == ConstValue.DV_NOTIFYONLINEPLAYER_EXIT) {
            String eventPlayStatusJson = BluetoothParamUtil.bytesToString(param);
            UbtLog.d(TAG, "cmd = " + cmd + "    eventPlayStatusJson = " + eventPlayStatusJson);
            EventBus.getDefault().post(ConstValue.DV_NOTIFYONLINEPLAYER_EXIT);
        } else if (cmd == ConstValue.DV_NOTIFYONLINEPLAYER_PAUSE) {

        } else if (cmd == ConstValue.DV_NOTIFYONLINEPLAYER_CONTINUE) {

        } else if (cmd == ConstValue.DV_TAP_HEAD||cmd==ConstValue.DV_VOICE_WAIT) {
            UbtLog.d(TAG, "cmd = " + cmd + "  VOICE & TAP" );
            PlayerEvent mPlayerEvent = new PlayerEvent(PlayerEvent.Event.TAP_HEAD_OR_VOICE_WAKE);
            EventBus.getDefault().post(mPlayerEvent);
        }
    }

    private void notifyUiCurrentRobotStatus(JSONObject mCmd) throws JSONException {
//        saveAudioHistory(Integer.parseInt(mCmd.get("index").toString()));
//        if(!mCmd.get("index").toString().equals("")) {
//            setCurentPlayingAudioIndex(Integer.parseInt(mCmd.get("index").toString()));
//        }
        notifyCurrentRobotOnlineStatus(mCmd);
    }

    private void notifyCurrentRobotOnlineStatus(JSONObject mCmd) throws JSONException {
        PlayerEvent mPlayerEvent = new PlayerEvent(PlayerEvent.Event.GET_ROBOT_ONLINEPLAYING_STATUS);
        mPlayerEvent.setCateogryId(mCmd.get("categoryId").toString());
        mPlayerEvent.setAlbumId(mCmd.get("albumId").toString());
        mPlayerEvent.setCurrentPlayingIndex(Integer.parseInt( mCmd.get("index").toString()));
        mPlayerEvent.setStatus(mCmd.get("status").toString());
        mPlayerEvent.setLoop(mCmd.get("loop").toString());
        setPlayType(Integer.parseInt(mCmd.get("loop").toString()));
        EventBus.getDefault().post(mPlayerEvent);
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

    public void playEvent(String playStatus, String categoryId, String albumId, int index) {
        notifyUiNextAudio(index);
        if(categoryId!=null&&albumId!=null) {
            sendControlCommand(playStatus, categoryId, albumId, Integer.toString(index));
        }else {
            UbtLog.e(TAG,"ALBUMID IS NULL");
        }
        if (local_player) {
            localAudioplay(mPlayContentInfoList.get(index).contentUrl);
        }
    }

    private void notifyUiNextAudio(int index) {
//        saveAudioHistory(index);
        setCurentPlayingAudioIndex(index);
        notifyNextAudioMesssage(index);
    }

//    private void saveAudioHistory(int index) {
//        AudioContentInfo mHistory = new AudioContentInfo();
//        mHistory.index = index;
//        SPUtils.getInstance().saveObject(Constant.SP_ONLINEAUDIO_HISTORY, mHistory);
//    }

    public void exitEvent() {
        UbtLog.d(TAG, "stopEventSound = ");
//        byte[] mCmd = {0};
//        mCmd[0] = 0;
//        doSendComm(ConstValue.DV_NOTIFYONLINEPLAYER_EXIT, mCmd);
        if (local_player) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        }
    }

    public void pauseEvent() {
        UbtLog.d(TAG, "pauseEventSound = ");
        byte[] mCmd = {0};
        mCmd[0] = 0;
        doSendComm(ConstValue.DV_NOTIFYONLINEPLAYER_PAUSE, mCmd);
    }

    public void continueEvent() {
        UbtLog.d(TAG, "continueEventSound = ");
        byte[] mCmd = {0};
        mCmd[0] = 0;
        doSendComm(ConstValue.DV_NOTIFYONLINEPLAYER_CONTINUE, mCmd);
    }

    private void notifyNextAudioMesssage(int index) {
        PlayerEvent mPlayerEvent = new PlayerEvent(PlayerEvent.Event.CONTROL_PLAY_NEXT);
        //NEXT AUDIO NAME INFORMATION
        if(mPlayContentInfoList.size()>0) {
            mPlayerEvent.setCurrentPlayingSongName(mPlayContentInfoList.get(index).contentName);
        }
        mPlayerEvent.setCurrentPlayingIndex(index);
        EventBus.getDefault().post(mPlayerEvent);
    }


    @Override
    public void onDeviceDisConnected(String mac) {
        UbtLog.d(TAG, "--onDeviceDisConnected--" + mac);

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
        return currentPlaySeq;
    }

    public void nextAudioPlay() {
        if ((currentPlaySeq + 1) < mPlayContentInfoList.size()) {
            currentPlaySeq++;
        } else {
            currentPlaySeq = 0;
        }
        exitEvent();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UbtLog.d(TAG,"NEXTS AUDIO "+currentPlaySeq);
        //NEXT AUDIO PLAY
        playEvent("playing",getmCategoryId(),getAlbumId(),currentPlaySeq);
    }

    public void prevAudioPlay() {
        if ((currentPlaySeq - 1) >= 0) {
            currentPlaySeq--;
        } else {
            currentPlaySeq = mPlayContentInfoList.size() - 1;
        }
        // isPause = false;
        // currentPlayInfo = mPlayContentInfoList.get(currentPlaySeq);
        exitEvent();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UbtLog.d(TAG,"PRE AUDIO "+currentPlaySeq);
        //PREV AUDIO PLAY
        playEvent("playing",getmCategoryId(),getAlbumId(),currentPlaySeq);
        //   ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
        //   mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
    }

    /**
     * set current playing content
     * @param playContentInfoList
     */

    public void setPlayingContent(List<AudioContentInfo> playContentInfoList){
        if (playContentInfoList == null) {
            UbtLog.d(TAG, "setPlayContent is null");
            return;
        }
        mPlayingContentInfoList.clear();
        mPlayingContentInfoList.addAll(playContentInfoList);
    }

    /**
     *
     * @return playing content mPlaying...
     */
    public  List<AudioContentInfo> getPlayingContent(){
        return  mPlayingContentInfoList;
    }

    /**
     * Return ready to play contents mPlay...
     * @return
     */
    public List<AudioContentInfo> getPlayContent() {
        return mPlayContentInfoList;
    }
    /**
     * Ready to play content
     * @param playContentInfoList
     */
    public void setPlayContent(List<AudioContentInfo> playContentInfoList) {
        if (playContentInfoList == null) {
            UbtLog.d(TAG, "setPlayContent is null");
            return;
        }
        mPlayContentInfoList.clear();
        mPlayContentInfoList.addAll(playContentInfoList);
        for (int i = 0; i < mPlayContentInfoList.size(); i++) {
            UbtLog.d(TAG, "i = " + i + "     name = " + mPlayContentInfoList.get(i).contentName + "url: " + mPlayContentInfoList.get(i).contentUrl);
        }
        UbtLog.d(TAG, "mPlayContentInfoList.size() = " + mPlayContentInfoList.size());
    }

    public void setAlbumId(String albumId) {
        mAlbumId = albumId;
    }

    public String getAlbumId() {
        return mAlbumId;
    }

    protected void localAudioplay(String url) {
        try {
            mediaPlayer = new MediaPlayer();
            // 设置指定的流媒体地址
            mediaPlayer.setDataSource(url);
            // 设置音频流的类型
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过异步的方式装载媒体资源
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 装载完毕 开始播放流媒体
                    mediaPlayer.start();
                    Toast.makeText(mContext, "开始播放", Toast.LENGTH_LONG).show();
                }
            });
            // 设置循环播放
            // mediaPlayer.setLooping(true);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 在播放完毕被回调
                    Toast.makeText(mContext, "播放完毕", Toast.LENGTH_LONG).show();
                   // sendNextAudioMesssage();
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 如果发生错误，重新播放
                    Toast.makeText(mContext, "播放错误", Toast.LENGTH_LONG).show();
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "播放失败", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * get the robot currently oneline playing status
     */
    public void getRobotOnlineAudioStatus() {
        byte[] params = new byte[1];
        params[0] =0;
        doSendComm(ConstValue.DV_GETONLINEPLAYER_ROBOTSTATUS, BluetoothParamUtil.stringToBytes(""));
    }
    /**
     * tell the robot oneline playing loop mode
     *
     * @param mode : "0" --single song mode
     *               "1"----loop song mode
     *             "2"----order song mode
     *
     */
    public void sendOnlineAudioLoopMode(String mode) {
        doSendComm(ConstValue.DV_NOTIFYONLINEPLAYER_ROBOTLOOPMODE, BluetoothParamUtil.stringToBytes(mode));
    }

    /**
     * control play or jump to category/album/some songs
     *
     * @param playStatus: play, pause, continue
     * @param categoryId: id number
     * @param albumId:    id number
     * @param index:      song index
     */
    public void sendControlCommand(String playStatus, String categoryId, String albumId, String index) {
        JSONObject mCmd = new JSONObject();
        try {
            mCmd.put("status", playStatus);
            mCmd.put("categoryId", categoryId);
            mCmd.put("albumId", albumId);
            mCmd.put("index", index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UbtLog.d(TAG,"SEND TO ROBOT, PLAYING  "+mCmd.toString());
        doSendComm(ConstValue.DV_NOTIFYONLINEPLAYER_PLAY, BluetoothParamUtil.stringToBytes(mCmd.toString()));
    }

    /**
     * exit the online playing status
     */
    private void sendExitOnlineAudioStatus() {
        byte[] mCmd = {0};
        mCmd[0] = 0;
        doSendComm(ConstValue.DV_NOTIFYONLINEPLAYER_EXIT, mCmd);
    }






}
