package com.ubt.alpha1e.onlineaudioplayer.model;

import java.io.Serializable;

/**
 * @作者：ubt
 * @日期: 2018/4/20 10:22
 * @描述:
 */


public class HistoryAudio implements Serializable {
    private String grade;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    private String albumId;

    @Override
    public String toString() {
        return "HistoryAudio{" +
                "grade='" + grade + '\'' +
                ", albumId='" + albumId + '\'' +
                ", albumId='" + albumId + '\'' +
                '}';
    }

    private String albumName;
}
