package com.ubt.alpha1e.onlineaudioplayer;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class OnlineAudioPlayerPresenter extends BasePresenterImpl<OnlineAudioPlayerContract.View> implements OnlineAudioPlayerContract.Presenter{
    private String[] albumList={"语文课堂","趣味课堂","英语名著","语文课堂"};
    @Override
    public void getGradeList() {

        mView.showGradeList();
    }

    @Override
    public void getAlbumList() {

    }

    @Override
    public void getAudioList() {



    }

    @Override
    public void getSearchList() {



    }
}
