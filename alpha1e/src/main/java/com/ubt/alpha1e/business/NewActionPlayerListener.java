package com.ubt.alpha1e.business;

public interface NewActionPlayerListener {

    void onPlaying();

    void onPausePlay();

    void onFinishPlay();

    void onFrameDo(int index);

    void notePlayChargingError();
}
