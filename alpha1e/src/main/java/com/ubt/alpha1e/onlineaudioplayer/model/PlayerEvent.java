package com.ubt.alpha1e.onlineaudioplayer.model;


/**
 * @作者：ubt
 * @日期: 2018/4/20 13:43
 * @描述:
 */


public class PlayerEvent {
    Event event;
    public String getCurrentPlayingSongName() {
        return currentPlayingSongName;
    }

    public void setCurrentPlayingSongName(String currentPlayingSongName) {
        this.currentPlayingSongName = currentPlayingSongName;
    }

    private String currentPlayingSongName;

    public int getCurrentPlayingIndex() {
        return currentPlayingIndex;
    }

    public void setCurrentPlayingIndex(int currentPlayingIndex) {
        this.currentPlayingIndex = currentPlayingIndex;
    }

    private int currentPlayingIndex;

    public int getCurrentClickingIndex() {
        return currentClickingIndex;
    }

    public void setCurrentClickingIndex(int currentClickingIndex) {
        this.currentClickingIndex = currentClickingIndex;
    }

    private int currentClickingIndex;

    public PlayerEvent(Event event) {
        this.event = event;
    }
    public enum Event{
        CONTROL_PLAY_NEXT,
        CONTROL_STOP,
        GET_PLAYING_INDEX,
        TAP_HEAD
    }
    public PlayerEvent.Event getEvent() {
        return event;
    }
}

