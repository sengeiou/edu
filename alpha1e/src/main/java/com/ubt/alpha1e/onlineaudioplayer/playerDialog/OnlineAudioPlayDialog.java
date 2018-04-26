package com.ubt.alpha1e.onlineaudioplayer.playerDialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.model.EventPlayStatus;
import com.ubt.alpha1e.onlineaudioplayer.helper.OnlineAudioResourcesHelper;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.PlayerEvent;
import com.ubt.alpha1e.utils.StringUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * 网络搜索选择类
 * Created by lihai on 04/04/17.
 */
public class OnlineAudioPlayDialog {
    private static final String TAG = OnlineAudioPlayDialog.class.getSimpleName();

    private static final int UPDATE_PLAY_STATUS = 1;
    private static final int UPDATE_CURRENT_PLAY = 2;
    private static final int STOP_CURRENT_PLAY = 3;

    private static OnlineAudioPlayDialog mDialogIntance = null;
    private Activity mActivity;
    private Dialog mDialog;
    private RelativeLayout rlBg;

    private ImageView ivPlayStatus;
    private ImageView ivPlayNone;
    private ImageView ivMusicList;
    private ImageView ivMusicLast;
    private ImageView ivMusicPlay;
    private ImageView ivMusicStop;
    private ImageView ivMusicNext;
    private ImageView ivMusicVolume;
    private ImageView ivRecycleButton;

    private TextView tvPlayName;
    private SeekBar skbVolumeControl;
    private AnimationDrawable playStatusAnim = null;

    private Display mDisplay;
    private IHibitsEventPlayListener iHibitsEventPlayListener = null;

    private String currentAlbumId = "";

    public OnlineAudioResourcesHelper mHelper;

    private boolean isChangeVol = false;
    private boolean isStartPlayProcess = true;//是否开启播放流程
    private String playStatus = "";//流程开启后的播放状态
    private AudioContentInfo currentPlayInfo = null;
    private int currentPlaySeq = -1;
    private boolean isPause = false;
    private boolean isRecycle=false;
    public static int SINGLE_AUDIO_PLAYING=1;
    public static int RECYCLE_AUDIO_LIST_PLAYING=2;
    public static int ORDER_AUDIO_LIST_PLAYING=3;
    private int  isRecycleType=ORDER_AUDIO_LIST_PLAYING;
    private int volumeProgress = 0;
    private boolean isShow=false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_PLAY_STATUS:
                    EventPlayStatus eventPlayStatus = (EventPlayStatus) msg.obj;
                    UbtLog.d(TAG,"eventPlayStatus = " + eventPlayStatus + " currentEventId = " + currentAlbumId);
                    if(eventPlayStatus != null && mHelper.getPlayContent() != null){
                        UbtLog.d(TAG,"seqNo = " + eventPlayStatus.playAudioSeq + "  isInteger = "+ StringUtils.isInteger(eventPlayStatus.playAudioSeq));
                        if(StringUtils.isInteger(eventPlayStatus.playAudioSeq)){
                            int seqNo = Integer.parseInt(eventPlayStatus.playAudioSeq);
                            if(currentAlbumId.equals(eventPlayStatus.eventId) && "1".equals(eventPlayStatus.eventState) && seqNo >= 0 && mHelper.getPlayContent().size() > 0){
                                isStartPlayProcess = true;
                                playStatus = eventPlayStatus.audioState;
                                if("playing".equals(playStatus) || "pause".equals(playStatus)){
                                    currentPlaySeq = seqNo;
                                    currentPlayInfo = mHelper.getPlayContent().get(seqNo);
                                    String playContent = "正在播放：" + currentPlayInfo.contentName /*+ "_" + currentPlaySeq*/;
                                    SpannableString style = new SpannableString(playContent);
                                    style.setSpan(new ForegroundColorSpan(mActivity.getResources().getColor(R.color.T32)),0, "正在播放：".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tvPlayName.setText(style);

                                    if("pause".equals(playStatus)){
                                        isPause = true;
                                    }
                                }else {
                                    //audioState = noPlay
                                    tvPlayName.setText("暂无播放内容");
                                }
                            }else {
                                isStartPlayProcess = false;
                            }
                        }
                    }
                    UbtLog.d(TAG,"isStartPlayProcess = " + isStartPlayProcess);
                    if(!isStartPlayProcess) {
                        recoveryPlayerUi();
                    }
                    initState();
                    break;
                case UPDATE_CURRENT_PLAY:
                    if(currentPlayInfo == null || currentPlaySeq == -1){
                        if(mHelper.getPlayContent() != null && mHelper.getPlayContent().size() > 0){
                            currentPlayInfo = mHelper.getPlayContent().get(0);
                            currentPlaySeq = 0;
                        }else {
                            return;
                        }
                    }
                    String playContent = "正在播放：" + mHelper.getPlayContent().get(mHelper.getCurrentPlayingAudioIndex()).contentName /*+ "_" + currentPlaySeq*/;
                    SpannableString style = new SpannableString(playContent);
                    style.setSpan(new ForegroundColorSpan(mActivity.getResources().getColor(R.color.T32)),0, "正在播放：".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvPlayName.setText(style);

                    ivPlayNone.setVisibility(View.GONE);
                    ivPlayStatus.setVisibility(View.VISIBLE);
                    playStatusAnim.start();
                    break;
                case STOP_CURRENT_PLAY:
                    //currentPlaySeq = -1;
                    //currentPlayInfo = null;
                    tvPlayName.setText("暂无播放内容");
                    ivMusicPlay.setImageResource(R.drawable.ic_ct_play_usable);
                    ivPlayNone.setVisibility(View.VISIBLE);
                    ivPlayStatus.setVisibility(View.GONE);
                    playStatusAnim.stop();
                    break;
                default:
                    break;
            }
        }
    };

    public void recoveryPlayerUi() {
        currentPlaySeq = -1;
        currentPlayInfo = null;
        tvPlayName.setText("暂无播放内容");
    }

    /**
     * 类构造函数
     * @param activity 上下文
     */
    public OnlineAudioPlayDialog(Activity activity) {
        this.mActivity = activity;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();

    }

    public OnlineAudioPlayDialog builder() {
        mDialogIntance = this;
        EventBus.getDefault().register(mDialogIntance);
        mHelper = OnlineAudioResourcesHelper.getInstance(this.mActivity);

        // 获取Dialog布局
        View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_onlineplayer_event_play, null);

        initBaseView(view);

        // 定义Dialog布局和参数
        mDialog = new Dialog(mActivity, R.style.NewAlertDialogStyle);
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        mDialog.setContentView(view);
        mDialog.setOnDismissListener(mOnDismissListener);

        // 调整dialog背景大小
        rlBg.setLayoutParams(new FrameLayout.LayoutParams((int) (mDisplay
                .getWidth()), (int)(mDisplay.getHeight() * 0.17)));

        return this;
    }

    /**
     * 初始基本控件
     * @param view
     */
    public void initBaseView(View view){

        // 获取自定义Dialog布局中的控件

        rlBg = view.findViewById(R.id.rl_hibits_event_play);
        ivPlayStatus = view.findViewById(R.id.iv_play_status);
        ivPlayNone = view.findViewById(R.id.iv_play_status_none);
        ivMusicList = view.findViewById(R.id.iv_music_list);
        ivMusicLast = view.findViewById(R.id.iv_music_prev);
        ivMusicPlay = view.findViewById(R.id.iv_music_play);
        ivMusicStop = view.findViewById(R.id.iv_music_stop);
        ivMusicNext = view.findViewById(R.id.iv_music_next);
        ivMusicVolume = view.findViewById(R.id.iv_music_volume);
        ivRecycleButton=view.findViewById(R.id.iv_music_circle);

        tvPlayName = view.findViewById(R.id.tv_play_name);

        skbVolumeControl = view.findViewById(R.id.skb_volume_control);

        ivMusicList.setOnClickListener(mOnClickListener);
        ivMusicLast.setOnClickListener(mOnClickListener);
        ivMusicPlay.setOnClickListener(mOnClickListener);
        ivMusicStop.setOnClickListener(mOnClickListener);
        ivMusicNext.setOnClickListener(mOnClickListener);
        ivMusicVolume.setOnClickListener(mOnClickListener);
        ivRecycleButton.setOnClickListener(mOnClickListener);

        playStatusAnim = (AnimationDrawable)ivPlayStatus.getBackground();
        playStatusAnim.setOneShot(false);
        playStatusAnim.setVisible(true,true);

        skbVolumeControl.setThumb(ContextCompat.getDrawable(mActivity,R.drawable.ic_ct_sound_pro_disable));
        skbVolumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                if(isStartPlayProcess){
                    volumeProgress = arg0.getProgress();
                    if(volumeProgress == 0){
                        mHelper.mCurrentVoiceState = true;
                    }else{
                        mHelper.mCurrentVoiceState = false;
                    }
                    UbtLog.d(TAG,"mCurrentVoiceState = " + mHelper.mCurrentVoiceState + "   volumeProgress = " +volumeProgress);
                    if(!mHelper.mCurrentVoiceState){
                        //静音先解静音
                        mHelper.doTurnVol();
                        onNoteVolState(mHelper.mCurrentVoiceState);
                    }else {
                        if(arg0.getProgress()==0) {
                            mHelper.doTurnVol();
                            onNoteVolState(mHelper.mCurrentVoiceState);
                        }
                    }
                    //修改为，移动停之后，再发送改变音量
                    mHelper.doChangeVol(arg0.getProgress());

                }else {
                    skbVolumeControl.setProgress(0);
                }
                isChangeVol = false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                isChangeVol = true;
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1,boolean arg2) {
                if(!isStartPlayProcess){
                    skbVolumeControl.setProgress(0);
                }
            }
        });
    }

    @Subscribe
    public void onEventOnlinePlayerEvent(PlayerEvent mPlayerEvent) {
        if(mPlayerEvent.getEvent()==PlayerEvent.Event.CONTROL_PLAY_NEXT){
            UbtLog.d(TAG,"CONTROL_PLAY event = next "+mPlayerEvent.getCurrentPlayingSongName());
            autoNextAudioPlay();
        }else if(mPlayerEvent.getEvent()==PlayerEvent.Event.CONTROL_STOP){
            mHandler.sendEmptyMessage(STOP_CURRENT_PLAY);
        }else if(mPlayerEvent.getEvent()==PlayerEvent.Event.TAP_HEAD){
            mHandler.sendEmptyMessage(STOP_CURRENT_PLAY);
        }
    }


    public void onNoteVolState(boolean vol_state) {
        if (vol_state) {
            if (mHelper.mCurrentVolume < 0) {
                mHelper.mCurrentVolume *= -1;
                mHelper.doChangeVol(mHelper.mCurrentVolume);
            }
            onNoteVol(mHelper.mCurrentVolume);
            if(volumeProgress != 0){
                ivMusicVolume.setImageDrawable(ContextCompat.getDrawable(mActivity,R.drawable.ic_ct_sound_on));
            }
        } else {
            if (skbVolumeControl.getProgress() != 0){
                mHelper.mCurrentVolume = -1 * skbVolumeControl.getProgress();
            }
            skbVolumeControl.setProgress(0);
            ivMusicVolume.setImageDrawable(ContextCompat.getDrawable(mActivity,R.drawable.ic_ct_sound_mute));
        }
    }

    public void onNoteVol(int vol_pow) {
        if (!isChangeVol) {
            int value = vol_pow;
            value *= 100;
            int add_val = 0;
            if (value % 255 >= 127.5) {
                add_val = 1;
            }
            value /= 255;
            value += add_val;
            skbVolumeControl.setProgress(value);
            volumeProgress = value;
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_music_list:
                    break;
                case R.id.iv_music_prev:
                    prevAudioPlay();
                    break;
                case R.id.iv_music_play:
                    if(isStartPlayProcess) {
                        onlineAudioPlayer();
                    }
                    break;
                case R.id.iv_music_stop:
                    UbtLog.d(TAG,"doStop isStartPlayProcess = " + isStartPlayProcess + "  currentPlaySeq =" + currentPlaySeq );
                    if(isStartPlayProcess && currentPlaySeq >= 0){
                        mHelper.stopEvent();
                        isPause = false;
                        mHandler.sendEmptyMessage(STOP_CURRENT_PLAY);
                    }
                    break;
                case R.id.iv_music_next:
                    if(isStartPlayProcess){
                        nextAudioPlay();
                    }
                    break;
                case R.id.iv_music_volume:
                    if(isStartPlayProcess){
                        mHelper.doTurnVol();
                        onNoteVolState(mHelper.mCurrentVoiceState);
                    }
                    break;
                case R.id.iv_music_circle:
                    if(isRecycleType==ORDER_AUDIO_LIST_PLAYING) {
                        isRecycleType=RECYCLE_AUDIO_LIST_PLAYING;
                        mHelper.setPlayType(RECYCLE_AUDIO_LIST_PLAYING);
                        ivRecycleButton.setImageResource(R.drawable.ic_circle_list);
                        Toast.makeText(mActivity,"RECYCLE", Toast.LENGTH_LONG).show();
                    }else if(isRecycleType==RECYCLE_AUDIO_LIST_PLAYING){
                        isRecycleType=SINGLE_AUDIO_PLAYING;
                        mHelper.setPlayType(SINGLE_AUDIO_PLAYING);
                        ivRecycleButton.setImageResource(R.drawable.ic_circle_single);
                        Toast.makeText(mActivity,"SINGLE", Toast.LENGTH_LONG).show();
                    }else if(isRecycleType==SINGLE_AUDIO_PLAYING){
                        isRecycleType=ORDER_AUDIO_LIST_PLAYING;
                        mHelper.setPlayType(ORDER_AUDIO_LIST_PLAYING);
                        ivRecycleButton.setImageResource(R.drawable.ic_circle_list);
                        Toast.makeText(mActivity,"ORDER", Toast.LENGTH_LONG).show();
                     }
                    break;
                default:
                    break;
            }
        }
    };

    private void prevAudioPlay() {
        if(isStartPlayProcess){
            isPause = false;
            mHelper.prevAudioPlay();
            ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
            mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
        }
    }

    private void nextAudioPlay() {
        isPause = false;
        mHelper.nextAudioPlay();
        ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
        mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
    }
    private void autoNextAudioPlay() {
        isPause = false;
        mHelper.autoNextAudioPlay();
        ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
        mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
    }

    private void onlineAudioPlayer() {
        if("暂无播放内容".equals(tvPlayName.getText().toString())){
            mHelper.autoAudioPlay();
            isPause = false;
            ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
            mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
        }else {
            if(isPause){
                isPause = false;
                mHelper.continueEvent(mHelper.getPlayContent().get(currentPlaySeq).contentUrl);
                ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
                playStatusAnim.start();
            }else {
                isPause = true;
                mHelper.pauseEvent(mHelper.getPlayContent().get(currentPlaySeq).contentUrl);
                ivMusicPlay.setImageResource(R.drawable.ic_ct_play_usable);
                playStatusAnim.stop();
            }
        }
    }

    public OnlineAudioPlayDialog setCallbackListener(IHibitsEventPlayListener iHibitsEventPlayListener){
        this.iHibitsEventPlayListener = iHibitsEventPlayListener;
        return this;
    }
    /**
     * 注册对话框消失监听器
     */
    public DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener(){

        @Override
        public void onDismiss(DialogInterface dialog) {
//            if(mDialogIntance!=null)
//            EventBus.getDefault().unregister(mDialogIntance);
//            mHelper.UnRegisterHelper();
            if(iHibitsEventPlayListener != null){
                iHibitsEventPlayListener.onDismissCallback();
            }
//            mDialogIntance = null;
//            mDialogIntance.hidden();
        }
    };

    public OnlineAudioPlayDialog setLayout(){

        return this;
    }

    /**
     * 设置是否点击周围可以取消
     * @param cancel
     * @return
     */
    public OnlineAudioPlayDialog setCancelable(boolean cancel) {
        mDialog.setCancelable(cancel);
        return this;
    }

    public OnlineAudioPlayDialog setPlayContent(List<AudioContentInfo> playContentInfoList) {
        mHelper.setPlayContent(playContentInfoList);
        return this;
    }

    public OnlineAudioPlayDialog setCurrentAlbumId(String eventId) {
        currentAlbumId = eventId;
        return this;
    }
    public String getCurrentAlbumId(){
        return  currentAlbumId;
    }


    public static void refreshStatus(){
        if(mDialogIntance != null){
            mDialogIntance.mHelper.readPlayStatus();
        }
    }

    /**
     * 初始化机器人状态
     */
    private void initState(){
        UbtLog.d(TAG,"initRobotState mCurrentVolume = " + mHelper.mCurrentVolume + "   mCurrentVoiceState " + mHelper.mCurrentVoiceState + "   mLightState = " + mHelper.mLightState);
        if(isStartPlayProcess){

            if("playing".equals(playStatus)){
                ivPlayNone.setVisibility(View.GONE);
                ivPlayStatus.setVisibility(View.VISIBLE);
                ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
                playStatusAnim.start();

            }else if("pause".equals(playStatus)){
                ivPlayNone.setVisibility(View.GONE);
                ivPlayStatus.setVisibility(View.VISIBLE);
                ivMusicPlay.setImageResource(R.drawable.ic_ct_play_usable);
                playStatusAnim.stop();
            } else {
                //noPlay
                ivPlayNone.setVisibility(View.VISIBLE);
                ivPlayStatus.setVisibility(View.GONE);
                ivMusicPlay.setImageResource(R.drawable.ic_ct_play_usable);
                playStatusAnim.stop();
            }

            /*if(!isPause){
                ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
            }else {
                ivMusicPlay.setImageResource(R.drawable.ic_ct_play_usable);
            }*/

            ivMusicLast.setImageResource(R.drawable.ic_music_last_usable);
            ivMusicStop.setImageResource(R.drawable.ic_ct_stop);
            ivMusicNext.setImageResource(R.drawable.ic_music_next_usable);
            skbVolumeControl.setThumb(ContextCompat.getDrawable(mActivity, R.drawable.ic_ct_sound_pro));

            onNoteVol(mHelper.mCurrentVolume);
            onNoteVolState(mHelper.mCurrentVoiceState);
        }else {
            ivPlayNone.setVisibility(View.VISIBLE);
            ivPlayStatus.setVisibility(View.GONE);
            playStatusAnim.stop();

            ivMusicLast.setImageResource(R.drawable.ic_music_last_disable);
            ivMusicPlay.setImageResource(R.drawable.ic_ct_play_disable);
            ivMusicStop.setImageResource(R.drawable.ic_ct_stop_disable);
            ivMusicNext.setImageResource(R.drawable.ic_music_next_disable);
            skbVolumeControl.setThumb(ContextCompat.getDrawable(mActivity, R.drawable.ic_ct_sound_pro_disable));
            skbVolumeControl.setProgress(0);
            ivMusicVolume.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_ct_sound_disable));
        }
    }
    public void startPlay(){
        mHelper.RegisterHelper();
        onlineAudioPlayer();
    }
    public void stopPlay(){
        mHelper.stopEvent();
    }
    public void show() {
        mHelper.RegisterHelper();
        initState();
        setLayout();
        isShow=true;
        mDialog.show();
    }
    public void hidden(){
        isShow=false;
        mDialog.hide();

    }
    public boolean isShow(){
         return isShow;
    }
    public void destroy(){
           if(mDialogIntance!=null)
            EventBus.getDefault().unregister(mDialogIntance);
            mHelper.UnRegisterHelper();
           if(iHibitsEventPlayListener != null){
            iHibitsEventPlayListener.onDismissCallback();
          }
            mDialogIntance = null;
    }

    public interface IHibitsEventPlayListener{
        void onDismissCallback();
    }

}


