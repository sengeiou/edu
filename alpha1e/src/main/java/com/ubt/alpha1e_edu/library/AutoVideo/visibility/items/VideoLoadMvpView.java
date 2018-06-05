package com.ubt.alpha1e_edu.library.AutoVideo.visibility.items;

import android.media.MediaPlayer;

import com.ubt.alpha1e_edu.library.AutoVideo.widget.TextureVideoView;


/**
 * @author Wayne
 */
public interface VideoLoadMvpView {

    TextureVideoView getVideoView();

    void videoBeginning();

    void videoStopped();

    void videoPrepared(MediaPlayer player);

    void videoResourceReady(String videoPath);

    void videoComplete();

    void videoBuffering(int percent);
}
