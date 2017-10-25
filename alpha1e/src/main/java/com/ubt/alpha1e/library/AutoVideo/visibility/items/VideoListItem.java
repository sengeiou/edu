package com.ubt.alpha1e.library.AutoVideo.visibility.items;

import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.ubt.alpha1e.library.AutoVideo.widget.TextureVideoView;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;



/**
 * @author Wayne
 */
public class VideoListItem implements ListItem {

    private static final int STATE_IDLE = 0;
    private static final int STATE_ACTIVED = 1;
    private static final int STATE_DEACTIVED = 2;

    private int mState = STATE_IDLE;

    private String mCoverUrl;
    public String mImageUrl;
    public  String mVideoUrl;
    public  String mVideoPath;
    private ImageView mVideoCover;
    private TextureVideoView mVideoView;
//    public  boolean isVideo;
    public  ArrayList<String> imageArray;
    private final Rect mCurrentViewRect = new Rect();

    public VideoListItem( String videoUrl) {
//        this.isVideo = isVideo;
        mVideoPath = videoUrl;
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void bindView(TextureVideoView videoView, final ImageView videoCover) {
        mVideoView = videoView;
        mVideoCover = videoCover;
        Glide.with(videoView.getContext())
                .using(VideoListGlideModule.getOkHttpUrlLoader(), InputStream.class)
                .load(new GlideUrl(mVideoPath))
                .as(File.class)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        mVideoView.setMediaPlayerCallback(new TextureVideoView.MediaPlayerCallback() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoCover.setVisibility(View.INVISIBLE);
                UbtLog.d("wilson","setMediaPlayerCallback---onPrepared");
            }

            @Override
            public void onCompletion(MediaPlayer mp) {
                videoCover.setVisibility(View.VISIBLE);
                UbtLog.d("wilson","setMediaPlayerCallback---onCompletion");
            }

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                UbtLog.d("wilson","setMediaPlayerCallback:"+percent);
            }

            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

            }

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                return false;
            }

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

    public void setVideoPath(String videoPath) {
        mVideoPath = videoPath;
        if(videoPath != null) {
            mVideoView.setVideoPath(videoPath);
            if(mState == STATE_ACTIVED) {
                mVideoView.start();
            }
        }
    }

    public String getVideoUrl() {
        return mVideoPath;
    }

    private boolean viewIsPartiallyHiddenBottom(int height) {
        return mCurrentViewRect.bottom > 0 && mCurrentViewRect.bottom < height;
    }

    private boolean viewIsPartiallyHiddenTop() {
        return mCurrentViewRect.top > 0;
    }


    @Override
    public void setActive(View currentView, int newActiveViewPosition) {
        Log.e("VideoListItem", "setActive " + newActiveViewPosition + " path " + mVideoPath);
        mState = STATE_ACTIVED;
        if (mVideoPath != null&&mVideoView!=null) {
            if(!mVideoView.isPlaying())
            {
                mVideoView.setVideoPath(mVideoPath);
                mVideoView.start();
            }else
                mVideoView.play();
        }
    }

    @Override
    public void deactivate(View currentView, int position) {
        Log.e("VideoListItem", "deactivate " + position);
        if(mVideoView==null)
            return;
        mState = STATE_DEACTIVED;
        if(mVideoView.isPlaying())
        {
            mVideoView.pause();
        }else {
            mVideoView.stop();
        }
        mVideoCover.setVisibility(View.VISIBLE);

    }
}
