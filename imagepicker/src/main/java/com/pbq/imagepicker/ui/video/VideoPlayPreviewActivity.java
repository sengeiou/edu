package com.pbq.imagepicker.ui.video;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.pbq.imagepicker.ImagePicker;
import com.pbq.imagepicker.R;
import com.pbq.imagepicker.bean.ImageItem;


/**
 * 视频预览界面
 */
public class VideoPlayPreviewActivity extends AppCompatActivity {

    private static final String TAG = VideoPlayPreviewActivity.class.getSimpleName();

    private ImageItem mVideoItem = null;

    private VideoView mVideoView = null;
    private ImageView ivBack = null;
    private ImageView ivPlayStatus = null;
    private TextView tvFinish = null;
    private View vVideoTran = null;

    private static final int time = 3*1000;
    private static final int time_over = 1;

    private MediaController mMediaController = null;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case time_over:
                    if(ivPlayStatus != null && ivPlayStatus.getVisibility() == View.VISIBLE){
                        hideViewAnimator(ivPlayStatus);
                        ivPlayStatus.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagepick_activity_video_play);

        mVideoItem = (ImageItem) getIntent().getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEM);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivPlayStatus = (ImageView) findViewById(R.id.iv_play_status);
        tvFinish = (TextView) findViewById(R.id.tv_finish);
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        vVideoTran = findViewById(R.id.v_video_tran);

        ivBack.setOnClickListener(onClickListener);
        ivPlayStatus.setOnClickListener(onClickListener);
        tvFinish.setOnClickListener(onClickListener);
        vVideoTran.setOnClickListener(onClickListener);

        //mMediaController = new MediaController(this);//实例化控制器

        Log.d(TAG,"mVideoItem.path = " + mVideoItem.path);
        mVideoView.setVideoPath(mVideoItem.path);

        //mMediaController.setMediaPlayer(mVideoView);
        //mVideoView.setMediaController(mMediaController);

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(mHandler.hasMessages(time_over)){
                    mHandler.removeMessages(time_over);
                }

                ivPlayStatus.setImageResource(R.drawable.imagepick_icon_video_play);
                if(ivPlayStatus.getVisibility() == View.GONE || ivPlayStatus.getAlpha() == 0){
                    showViewAnimator(ivPlayStatus);
                    ivPlayStatus.setVisibility(View.VISIBLE);
                }
            }
        });

        mVideoView.start();
        ivPlayStatus.setImageResource(R.drawable.imagepick_icon_video_stop);

        hideViewAnimator(ivPlayStatus);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mVideoView.isPlaying()){
            mVideoView.pause();
            ivPlayStatus.setImageResource(R.drawable.imagepick_icon_video_play);
            ivPlayStatus.setVisibility(View.VISIBLE);
        }
    }



    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG,"view = " + view.getId());
            if(view.getId() == R.id.iv_back){
                finish();
            }else if(view.getId() == R.id.tv_finish){
                setResult(ImagePicker.RESULT_VIDEO_BACK , new Intent());
                finish();
            }else if(view.getId() == R.id.iv_play_status){
                if(mHandler.hasMessages(time_over)){
                    mHandler.removeMessages(time_over);
                }
                mHandler.sendEmptyMessageDelayed(time_over,time);

                if(mVideoView.isPlaying()){
                    mVideoView.pause();
                    ivPlayStatus.setImageResource(R.drawable.imagepick_icon_video_play);
                }else {
                    mVideoView.start();
                    ivPlayStatus.setImageResource(R.drawable.imagepick_icon_video_stop);
                }

            }else if(view.getId() == R.id.v_video_tran){
                if(mVideoView.isPlaying()){
                    if(ivPlayStatus.getVisibility() == View.GONE){
                        showViewAnimator(ivPlayStatus);
                        ivPlayStatus.setVisibility(View.VISIBLE);

                        if(mHandler.hasMessages(time_over)){
                            mHandler.removeMessages(time_over);
                        }
                        mHandler.sendEmptyMessageDelayed(time_over,time);

                    }else {
                        hideViewAnimator(ivPlayStatus);
                        ivPlayStatus.setVisibility(View.GONE);
                    }
                }else {
                    Log.d(TAG,"ivPlayStatus ==---" + ivPlayStatus.getAlpha());
                    if(ivPlayStatus.getVisibility() == View.GONE || ivPlayStatus.getAlpha() == 0){
                        ivPlayStatus.setImageResource(R.drawable.imagepick_icon_video_play);
                        showViewAnimator(ivPlayStatus);
                        ivPlayStatus.setVisibility(View.VISIBLE);
                    }

                }
            }

        }
    };

    private void showViewAnimator(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        animator.setDuration(500);
        animator.start();
    }

    private void hideViewAnimator(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        animator.setDuration(500);
        animator.start();
    }

    @Override
    protected void onDestroy() {
        if(mHandler.hasMessages(time_over)){
            mHandler.removeMessages(time_over);
        }
        super.onDestroy();
    }

}
