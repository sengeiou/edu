package com.ubt.alpha1e.library.AutoVideo.visibility.items;

/**
 * @author Wayne
 */
public class VideoItem {
    private String mVideoUrl;
    private String mCoverUrl;

    public VideoItem(String videoUrl, String coverUrl) {
        mVideoUrl = videoUrl;
        mCoverUrl = coverUrl;
    }
    public VideoItem(String videoUrl) {
        mVideoUrl = videoUrl;
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public String getVideoUrl() {
        return mVideoUrl;
    }
}
