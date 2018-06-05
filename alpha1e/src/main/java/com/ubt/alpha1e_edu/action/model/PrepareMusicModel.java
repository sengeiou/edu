package com.ubt.alpha1e_edu.action.model;

/**
 * @author：liuhai
 * @date：2017/11/16 15:20
 * @modifier：ubt
 * @modify_date：2017/11/16 15:20
 * [A brief description]
 * version
 */

public class PrepareMusicModel {
    private String musicName;
    private String musicPath;
    private int musicType;
    private boolean isSelected;
    private boolean playing;

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public int getMusicType() {
        return musicType;
    }

    public void setMusicType(int musicType) {
        this.musicType = musicType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    @Override
    public String toString() {
        return "PrepareMusicModel{" +
                "musicName='" + musicName + '\'' +
                ", musicPath='" + musicPath + '\'' +
                ", musicType='" + musicType + '\'' +
                ", isSelected=" + isSelected +
                ", playing=" + playing +
                '}';
    }
}
