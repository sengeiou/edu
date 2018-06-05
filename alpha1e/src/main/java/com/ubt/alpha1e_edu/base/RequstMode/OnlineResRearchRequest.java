package com.ubt.alpha1e_edu.base.RequstMode;

/**
 * @author：dicy.cheng
 * @date：2017/12/22 14:03
 * @modifier：ubt
 * @modify_date：2017/12/22 14:03
 * [A brief description]
 * version
 */

public class OnlineResRearchRequest extends BaseRequest {
    private String albumKeyword;

    public String getAlbumKeyword() {
        return albumKeyword;
    }

    public void setAlbumKeyword(String albumKeyword) {
        this.albumKeyword = albumKeyword;
    }

    @Override
    public String toString() {
        return "SetUserPasswordRequest{" +
                "albumKeyword='" + albumKeyword + '\'' +
                '}';
    }
}
