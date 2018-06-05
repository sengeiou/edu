package com.ubt.alpha1e.edu.base.RequstMode;


/**
 * @作者：ubt
 * @日期: 2018/4/11 11:13
 * @描述:
 */


public class OnlineAudioPlayerAudiosRequest extends BaseRequest {
    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    private String albumId;
}
