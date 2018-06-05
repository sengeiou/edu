package com.ubt.alpha1e.edu.ui;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.model.ActionOnlineInfo;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

public class VideoPlayerActivity extends BaseActivity implements UniversalVideoView.VideoViewCallback {

    private static final String TAG = "VideoPlayerActivity";
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    private UniversalVideoView mVideoView;
    private UniversalMediaController mMediaController;
    private View mVideoLayout;
    private  String VIDEO_URL = "";
    private int mSeekPosition;
    private int cachedHeight;
    private boolean isFullscreen;

    private ActionOnlineInfo actionInfo;


    public static void launchActivity(Context activity, ActionOnlineInfo actionInfo)
    {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(activity,VideoPlayerActivity.class);
        intent.putExtra("actionInfo", PG.convertParcelable(actionInfo));
        activity.startActivity(intent);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        actionInfo = getIntent().getParcelableExtra("actionInfo");
        VIDEO_URL = actionInfo.actionVideoPath;

        //包含空格播放不了
        VIDEO_URL = VIDEO_URL.replaceAll(" ","%20");
        initUI();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState Position=" + mVideoView.getCurrentPosition());
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
        if (mSeekPosition > 0) {
            mVideoView.seekTo(mSeekPosition);
        }
        Log.d(TAG, "onRestoreInstanceState Position=" + mSeekPosition);
    }


    @Override
    protected void initUI() {
        initTitle(actionInfo.actionName);
        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
        mVideoLayout = findViewById(R.id.video_layout);
        mVideoView.setMediaController(mMediaController);
        setVideoAreaSize();
        mVideoView.setVideoViewCallback(this);
        startVideoPlay();

    }

    private void startVideoPlay()
    {
        if (mSeekPosition > 0) {
            UbtLog.d(TAG, "mSeekPosition=" + mSeekPosition);
            mVideoView.seekTo(mSeekPosition);
        }
        mVideoView.start();
        mMediaController.setTitle(actionInfo.actionName);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSeekPosition>0){
            UbtLog.d(TAG, "onResume mSeekPosition=" + mSeekPosition);
            mVideoView.seekTo(mSeekPosition);
            mVideoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause ");
        if (mVideoView != null && mVideoView.isPlaying()) {
            mSeekPosition = mVideoView.getCurrentPosition();
            Log.d(TAG, "onPause mSeekPosition=" + mSeekPosition);
            mVideoView.pause();
        }
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);
            findViewById(R.id.layout_title).setVisibility(View.GONE);

        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.cachedHeight;
            mVideoLayout.setLayoutParams(layoutParams);
            findViewById(R.id.layout_title).setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {

    }

    /**
     * 置视频区域大小
     */
    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                cachedHeight = (int) (width * 405f / 720f);
//                cachedHeight = (int) (width * 3f / 4f);
//                cachedHeight = (int) (width * 9f / 16f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                mVideoLayout.setLayoutParams(videoLayoutParams);
                mVideoView.setVideoPath(VIDEO_URL);
                mVideoView.requestFocus();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (this.isFullscreen) {
            mVideoView.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }

}
