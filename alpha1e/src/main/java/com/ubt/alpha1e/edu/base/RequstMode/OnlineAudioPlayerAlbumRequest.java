package com.ubt.alpha1e.edu.base.RequstMode;

/**
 * @作者：ubt
 * @日期: 2018/4/11 11:09
 * @描述:
 */


public class OnlineAudioPlayerAlbumRequest extends BaseRequest {
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryName) {
        this.categoryId = categoryName;
    }

    private String categoryId;

}
