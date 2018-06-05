package com.ubt.alpha1e.edu.business;

public interface NewActionPlayerListener {

    void onPlaying();

    void onPausePlay();

    void onFinishPlay();

    void onFrameDo(int index);

    void notePlayChargingError();
}
