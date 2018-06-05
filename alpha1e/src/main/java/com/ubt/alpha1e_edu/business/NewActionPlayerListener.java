package com.ubt.alpha1e_edu.business;

public interface NewActionPlayerListener {

    void onPlaying();

    void onPausePlay();

    void onFinishPlay();

    void onFrameDo(int index);

    void notePlayChargingError();
}
