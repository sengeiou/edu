package com.ubt.alpha1e.edu.blocklycourse.videoPlayer;

import com.shuyu.gsyvideoplayer.listener.StandardVideoAllCallBack;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class BlocklyVideoPlayerListener implements StandardVideoAllCallBack {

    private static String TAG = "BlocklyVideoPlayerListener";

    @Override
    public void onPrepared(String s, Object... objects) {
        UbtLog.d(TAG, "onPrepared");
    }

    @Override
    public void onClickStartIcon(String s, Object... objects) {
        UbtLog.d(TAG, "onClickStartIcon");
    }

    @Override
    public void onClickStartError(String s, Object... objects) {
        UbtLog.d(TAG, "onClickStartError");
    }

    @Override
    public void onClickStop(String s, Object... objects) {
        UbtLog.d(TAG, "onClickStop");
    }

    @Override
    public void onClickStopFullscreen(String s, Object... objects) {
        UbtLog.d(TAG, "onClickStopFullscreen");
    }

    @Override
    public void onClickResume(String s, Object... objects) {
        UbtLog.d(TAG, "onClickResume");
    }

    @Override
    public void onClickResumeFullscreen(String s, Object... objects) {
        UbtLog.d(TAG, "onClickResumeFullscreen");
    }

    @Override
    public void onClickSeekbar(String s, Object... objects) {
        UbtLog.d(TAG, "onClickSeekbar");
    }

    @Override
    public void onClickSeekbarFullscreen(String s, Object... objects) {
        UbtLog.d(TAG, "onClickSeekbarFullscreen");
    }

    @Override
    public void onAutoComplete(String s, Object... objects) {
        UbtLog.d(TAG, "onAutoComplete");
    }

    @Override
    public void onEnterFullscreen(String s, Object... objects) {
        UbtLog.d(TAG, "onEnterFullscreen");
    }

    @Override
    public void onQuitFullscreen(String s, Object... objects) {
        UbtLog.d(TAG, "onQuitFullscreen");
    }

    @Override
    public void onQuitSmallWidget(String s, Object... objects) {
        UbtLog.d(TAG, "onQuitSmallWidget");
    }

    @Override
    public void onEnterSmallWidget(String s, Object... objects) {
        UbtLog.d(TAG, "onEnterSmallWidget");
    }

    @Override
    public void onTouchScreenSeekVolume(String s, Object... objects) {
        UbtLog.d(TAG, "onTouchScreenSeekVolume");
    }

    @Override
    public void onTouchScreenSeekPosition(String s, Object... objects) {
        UbtLog.d(TAG, "onTouchScreenSeekPosition");
    }

    @Override
    public void onTouchScreenSeekLight(String s, Object... objects) {
        UbtLog.d(TAG, "onTouchScreenSeekLight");
    }

    @Override
    public void onPlayError(String s, Object... objects) {
        UbtLog.d(TAG, "onPlayError");
    }

    @Override
    public void onClickStartThumb(String s, Object... objects) {
        UbtLog.d(TAG, "onClickStartThumb");
    }

    @Override
    public void onClickBlank(String s, Object... objects) {
        UbtLog.d(TAG, "onClickBlank");
    }

    @Override
    public void onClickBlankFullscreen(String s, Object... objects) {
        UbtLog.d(TAG, "onClickBlankFullscreen");
    }
}
