package com.ubt.alpha1e.edu.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.TimeTools;
import com.ubt.alpha1e.edu.ui.DubActivity;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

/**
 * 类名
 *
 * @author wmma
 * @description 实现的主要功能。
 * @date $date
 * @update 修改者，修改日期，修改内容。
 */


public class DubPreviewFragment extends BaseFragment {

    private final String TAG = DubPreviewFragment.class.getSimpleName();

    private View view;
    private ImageView ivPlay;
    private TextView tvTimes;
    private SeekBar seekBar;
    private ImageView ivPreviewDubRipple;
    private GifImageView gifPreviewDubRipple;

    private long actionTime;
    private DubActivity dubActivity;

    private MediaPlayer player;
    private AudioManager mAudioManager;
    private boolean isPlaying = false;

    private DubPreviewCountDown dubPreviewCountDown;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dubActivity = (DubActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_preview_dub, null);
        actionTime = getArguments().getLong(dubActivity.ACTION_TIME);
        dubPreviewCountDown = new DubPreviewCountDown(actionTime*1000, 1000);
        initUI();
        return view;
    }


    @Override
    protected void initUI() {
        ivPlay = (ImageView) view.findViewById(R.id.iv_play_dub);
        tvTimes = (TextView) view.findViewById(R.id.tv_dub_time);
        seekBar = (SeekBar) view.findViewById(R.id.skb_volume);
        ivPreviewDubRipple = (ImageView) view.findViewById(R.id.iv_preview_dub_ripple);
        gifPreviewDubRipple = (GifImageView) view.findViewById(R.id.gif_preview_dub_ripple);

        tvTimes.setText(TimeTools.getMMTimeForPublish((int)actionTime)+"");

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    stop();
                    dubActivity.doDefault();
                    dubPreviewCountDown.onFinish();
                    dubPreviewCountDown.cancel();
                    isPlaying = false;
                    ivPreviewDubRipple.setVisibility(View.VISIBLE);
                    gifPreviewDubRipple.setVisibility(View.GONE);
                }else{
                    play();
                    ivPlay.setImageResource(R.drawable.icon_stop_preview);
                    isPlaying = true;
                    ivPreviewDubRipple.setVisibility(View.GONE);
                    gifPreviewDubRipple.setVisibility(View.VISIBLE);
                    dubActivity.doPlay();
                }

            }
        });

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int syscurrenvolume= mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        seekBar.setMax(maxVolume);
                // 进度条绑定当前音量
        seekBar.setProgress(syscurrenvolume);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int tmpInt = seekBar.getProgress();

                 // 当进度小于1时，设置成1，防止太小。
                UbtLog.d(TAG, "tmpInt=" + tmpInt);
                 if (tmpInt < 1) {
                       tmpInt = 1;
                 }

                 // 根据当前进度改变亮度
                 mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, tmpInt, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if(isPlaying == false){
            isPlaying = false;
            ivPreviewDubRipple.setVisibility(View.VISIBLE);
            gifPreviewDubRipple.setVisibility(View.GONE);
            ivPlay.setImageResource(R.drawable.icon_play_dub);
            tvTimes.setText(TimeTools.getMMTimeForPublish((int)actionTime)+"");
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        UbtLog.d(TAG, "--onStop--");
        if(dubPreviewCountDown != null){
            dubPreviewCountDown.onFinish();
            dubPreviewCountDown.cancel();
        }
//        if(player != null){
//            UbtLog.d(TAG, "--onStop-- play music ");
//            player.stop();
//        }
        if(dubActivity != null){
            dubActivity.doDefault();
        }
        stop();
        isPlaying = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG, "--onDestroy--");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    private Timer mp3_delay;
    public void play() {
        try {
            if(player != null){
                player.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        player = null;
//        mAudioManager = null;
        final String path = FileTools.actions_new_cache+File.separator+"tep_audio_recorder_for_mp3.mp3";
        final File mp3 = new File(path);
        if (mp3.exists()) {


                if (player == null) {
                    player = new MediaPlayer();
                }
      /*          if (mAudioManager == null) {
                    try {
                        mAudioManager = (AudioManager) getActivity()
                                .getSystemService(Context.AUDIO_SERVICE);
                    } catch (Exception e) {
                        mAudioManager = null;
                    }
                }*/
                player.reset();

                try {
                    mp3_delay.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mp3_delay = new Timer();
                mp3_delay.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            player.setDataSource(path);
                            player.prepare();
                            player.start();
                            dubPreviewCountDown.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, 1000);

            }
        }

    public  void stop() {
        if (mp3_delay != null) {
            try {
                mp3_delay.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (player != null) {
            try {
                player.stop();
            } catch (Exception e) {
                UbtLog.e("MP3播放", e.getMessage());
            }
        }
    }


    private long time = 0;
    class DubPreviewCountDown extends CountDownTimer {

        public DubPreviewCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            time = 0;
            tvTimes.setText(TimeTools.getMMTimeForPublish((int)actionTime)+"");
            ivPlay.setImageResource(R.drawable.icon_play_dub);
            ivPreviewDubRipple.setVisibility(View.VISIBLE);
            gifPreviewDubRipple.setVisibility(View.GONE);
            isPlaying = false;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            UbtLog.d(TAG, "onTick");
//            ivPlay.setImageResource(R.drawable.icon_stop_preview);
             time = time + 1000;
            tvTimes.setText(TimeTools.getMMTime((int)time)+"");

        }
    }



}
