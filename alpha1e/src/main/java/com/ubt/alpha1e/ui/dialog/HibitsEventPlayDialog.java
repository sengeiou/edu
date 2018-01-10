package com.ubt.alpha1e.ui.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.event.HibitsEvent;
import com.ubt.alpha1e.behaviorhabits.helper.HabitsHelper;
import com.ubt.alpha1e.behaviorhabits.model.EventPlayStatus;
import com.ubt.alpha1e.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.behaviorhabits.playeventlist.PlayEventListActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 网络搜索选择类
 * Created by lihai on 04/04/17.
 */
public class HibitsEventPlayDialog {
    private static final String TAG = HibitsEventPlayDialog.class.getSimpleName();

    private static final int UPDATE_PLAY_STATUS = 1;
    private static final int UPDATE_CURRENT_PLAY = 2;
    private static final int STOP_CURRENT_PLAY = 3;

    private Activity mActivity;
    private Dialog mDialog;
    private RelativeLayout rlBg;

    private ImageView ivPlayStatus;
    private ImageView ivMusicList;
    private ImageView ivMusicLast;
    private ImageView ivMusicPlay;
    private ImageView ivMusicStop;
    private ImageView ivMusicNext;
    private ImageView ivMusicVolume;

    private TextView tvPlayName;
    private SeekBar skbVolumeControl;
    private AnimationDrawable playStatusAnim = null;

    private Display mDisplay;
    private IHibitsEventPlayListener iHibitsEventPlayListener = null;

    private List<PlayContentInfo> mPlayContentInfoList = null;
    private String currentEventId = "";

    public HabitsHelper mHelper;

    private boolean isChangeVol = false;
    private boolean isPlaying = false;
    private PlayContentInfo currentPlayInfo = null;
    private int currentPlaySeq = 0;
    private boolean isPause = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_PLAY_STATUS:
                    EventPlayStatus eventPlayStatus = (EventPlayStatus) msg.obj;
                    if(eventPlayStatus != null && mPlayContentInfoList != null){
                        if("playing".equals(eventPlayStatus.audioState) && currentEventId.equals(eventPlayStatus.eventId)){
                            if(isStringNumber(eventPlayStatus.playAudioSeq)){
                                int seqNo = Integer.parseInt(eventPlayStatus.playAudioSeq);
                                if(mPlayContentInfoList != null && seqNo < mPlayContentInfoList.size()){
                                    isPlaying = true;
                                    currentPlaySeq = seqNo;
                                    currentPlayInfo = mPlayContentInfoList.get(seqNo);
                                    tvPlayName.setText("正在播放：" + currentPlayInfo.contentName);
                                    ivPlayStatus.setVisibility(View.VISIBLE);
                                    playStatusAnim.start();
                                }
                            }
                        }
                    }
                    isPlaying = true;
                    if(!isPlaying){
                        isPlaying = false;
                        currentPlaySeq = 0;
                        currentPlayInfo = null;
                        tvPlayName.setText("暂无播放内容");
                        ivPlayStatus.setVisibility(View.INVISIBLE);
                        playStatusAnim.stop();
                    }
                    initState();
                    break;
                case UPDATE_CURRENT_PLAY:

                    tvPlayName.setText("正在播放：" + currentPlayInfo.contentName);
                    ivPlayStatus.setVisibility(View.VISIBLE);
                    playStatusAnim.start();
                    break;
                case STOP_CURRENT_PLAY:
                    isPlaying = false;
                    currentPlaySeq = 0;
                    currentPlayInfo = null;
                    tvPlayName.setText("暂无播放内容");
                    ivPlayStatus.setVisibility(View.INVISIBLE);
                    playStatusAnim.stop();
                    initState();
                    break;
                default:
                    break;
            }
        }
    };

    /***
     * 判断字符串是否都是数字
     */
    public  boolean isStringNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 类构造函数
     * @param activity 上下文
     */
    public HibitsEventPlayDialog(Activity activity) {
        this.mActivity = activity;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();

    }

    public HibitsEventPlayDialog builder() {
        EventBus.getDefault().register(this);

        mHelper = new HabitsHelper(mActivity);

        // 获取Dialog布局
        View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_hibits_event_play, null);

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
                .getWidth()), (int)(mDisplay.getHeight() * 0.5)));

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
        ivMusicList = view.findViewById(R.id.iv_music_list);
        ivMusicLast = view.findViewById(R.id.iv_music_last);
        ivMusicPlay = view.findViewById(R.id.iv_music_play);
        ivMusicStop = view.findViewById(R.id.iv_music_stop);
        ivMusicNext = view.findViewById(R.id.iv_music_next);
        ivMusicVolume = view.findViewById(R.id.iv_music_volume);

        tvPlayName = view.findViewById(R.id.tv_play_name);

        skbVolumeControl = view.findViewById(R.id.skb_volume_control);

        ivMusicList.setOnClickListener(mOnClickListener);
        ivMusicLast.setOnClickListener(mOnClickListener);
        ivMusicPlay.setOnClickListener(mOnClickListener);
        ivMusicStop.setOnClickListener(mOnClickListener);
        ivMusicNext.setOnClickListener(mOnClickListener);
        ivMusicVolume.setOnClickListener(mOnClickListener);

        //ivPlayStatus.setBackgroundResource(R.drawable.img_ct_playing_none);
        ivPlayStatus.setVisibility(View.INVISIBLE);
        playStatusAnim = (AnimationDrawable)ivPlayStatus.getBackground();
        playStatusAnim.setOneShot(false);
        playStatusAnim.setVisible(true,true);

        skbVolumeControl.setThumb(mActivity.getDrawable(R.drawable.ic_ct_sound_pro_disable));
        skbVolumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                if(isPlaying){
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

            }
        });
    }

    @Subscribe
    public void onEventHibits(HibitsEvent event) {
        UbtLog.d(TAG,"event = " + event);
        if(event.getEvent() == HibitsEvent.Event.CONTROL_PLAY){
            UbtLog.d(TAG,"event = " + event.getStatus());
        }else if(event.getEvent() == HibitsEvent.Event.READ_EVENT_PLAY_STATUS){
            UbtLog.d(TAG,"EventPlayStatus = " + event.getEventPlayStatus());
            EventPlayStatus eventPlayStatus = event.getEventPlayStatus();
            Message msg = new Message();
            msg.what = UPDATE_PLAY_STATUS;
            msg.obj = eventPlayStatus;
            mHandler.sendMessage(msg);
        }
    }


    public void onNoteVolState(boolean vol_state) {
        if (vol_state) {
            if (mHelper.mCurrentVolume < 0) {
                mHelper.mCurrentVolume *= -1;
                mHelper.doChangeVol(mHelper.mCurrentVolume);
            }
            onNoteVol(mHelper.mCurrentVolume);
            ivMusicVolume.setImageDrawable(mActivity.getDrawable(R.drawable.ic_ct_sound_on));
        } else {
            if (skbVolumeControl.getProgress() != 0){
                mHelper.mCurrentVolume = -1 * skbVolumeControl.getProgress();
            }
            skbVolumeControl.setProgress(0);
            ivMusicVolume.setImageDrawable(mActivity.getDrawable(R.drawable.ic_ct_sound_mute));
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
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_music_list:
                    mDialog.cancel();
                    PlayEventListActivity.launchActivity(mActivity,mPlayContentInfoList,currentEventId);

                    break;
                case R.id.iv_music_last:
                    if(isPlaying){
                        if((currentPlaySeq -1) > 0){
                            currentPlaySeq--;
                        }else {
                            currentPlaySeq = mPlayContentInfoList.size() -1;
                        }

                        currentPlayInfo = mPlayContentInfoList.get(currentPlaySeq);
                        mHelper.playEventSound(currentEventId,currentPlaySeq+"","start");
                        mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
                    }
                    break;
                case R.id.iv_music_play:
                    if(isPlaying){
                        if(isPause){
                            isPause = false;
                            mHelper.playEventSound(currentEventId,currentPlaySeq+"","unpause");
                            ivMusicPlay.setBackgroundResource(R.drawable.ic_ct_play_usable);
                        }else {
                            isPause = true;
                            mHelper.playEventSound(currentEventId,currentPlaySeq+"","pause");
                            ivMusicPlay.setBackgroundResource(R.drawable.ic_ct_pause);
                        }
                    }
                    break;
                case R.id.iv_music_stop:
                    if(isPlaying){
                        mHelper.playEventSound(currentEventId,currentPlaySeq+"","stop");
                        mHandler.sendEmptyMessage(STOP_CURRENT_PLAY);
                    }
                    break;
                case R.id.iv_music_next:
                    if(isPlaying){
                        if((currentPlaySeq + 1) < mPlayContentInfoList.size()){
                            currentPlaySeq++;
                        }else {
                            currentPlaySeq = 0;
                        }

                        currentPlayInfo = mPlayContentInfoList.get(currentPlaySeq);
                        mHelper.playEventSound(currentEventId,currentPlaySeq+"","start");
                        mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
                    }
                    break;
                case R.id.iv_music_volume:
                    if(isPlaying){
                        mHelper.doTurnVol();
                        onNoteVolState(mHelper.mCurrentVoiceState);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public HibitsEventPlayDialog setCallbackListener(IHibitsEventPlayListener iHibitsEventPlayListener){
        this.iHibitsEventPlayListener = iHibitsEventPlayListener;
        return this;
    }
    /**
     * 注册对话框消失监听器
     */
    public DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener(){

        @Override
        public void onDismiss(DialogInterface dialog) {
            EventBus.getDefault().unregister(this);
            mHelper.UnRegisterHelper();
            if(iHibitsEventPlayListener != null){
                iHibitsEventPlayListener.onDismissCallback();
            }
        }
    };

    public HibitsEventPlayDialog setLayout(){

        return this;
    }

    /**
     * 设置是否点击周围可以取消
     * @param cancel
     * @return
     */
    public HibitsEventPlayDialog setCancelable(boolean cancel) {
        mDialog.setCancelable(cancel);
        return this;
    }

    public HibitsEventPlayDialog setPlayContent(List<PlayContentInfo> playContentInfoList) {
        mPlayContentInfoList = playContentInfoList;
        return this;
    }

    public HibitsEventPlayDialog setCurrentEventId(String eventId) {
        currentEventId = eventId;
        return this;
    }


    /**
     * 初始化机器人状态
     */
    private void initState(){
        UbtLog.d(TAG,"initRobotState mCurrentVolume = " + mHelper.mCurrentVolume + "   mCurrentVoiceState " + mHelper.mCurrentVoiceState + "   mLightState = " + mHelper.mLightState);
        if(isPlaying){
            ivMusicLast.setBackgroundResource(R.drawable.ic_music_last_usable);
            ivMusicPlay.setBackgroundResource(R.drawable.ic_ct_play_usable);
            ivMusicStop.setBackgroundResource(R.drawable.ic_ct_stop);
            ivMusicNext.setBackgroundResource(R.drawable.ic_music_next_usable);
            skbVolumeControl.setThumb(mActivity.getDrawable(R.drawable.ic_ct_sound_pro));

            onNoteVol(mHelper.mCurrentVolume);
            onNoteVolState(mHelper.mCurrentVoiceState);
        }else {

            ivMusicLast.setBackgroundResource(R.drawable.ic_music_last_disable);
            ivMusicPlay.setBackgroundResource(R.drawable.ic_ct_play_disable);
            ivMusicStop.setBackgroundResource(R.drawable.ic_ct_stop_disable);
            ivMusicNext.setBackgroundResource(R.drawable.ic_music_next_disable);
            skbVolumeControl.setThumb(mActivity.getDrawable(R.drawable.ic_ct_sound_pro_disable));
        }
    }

    public void show() {
        mHelper.RegisterHelper();
        mHelper.readPlayStatus();
        initState();
        setLayout();
        mDialog.show();
    }

    public interface IHibitsEventPlayListener{

        void onDismissCallback();
    }
}
