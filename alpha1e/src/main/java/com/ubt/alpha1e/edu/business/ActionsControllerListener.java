package com.ubt.alpha1e.edu.business;

/**
 * Created by Administrator on 2016/5/18.
 */
public interface ActionsControllerListener {

    /**
     * 播放动作
     * */
    void onStartPlay();

    /**
     * 停止动作
     * */
    void onStopPlay();


    /**
     * 播放循环动作
     * */
    void OnStartLoop();

}
