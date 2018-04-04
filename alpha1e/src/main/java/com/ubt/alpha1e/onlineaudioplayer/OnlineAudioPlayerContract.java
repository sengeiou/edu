package com.ubt.alpha1e.onlineaudioplayer;

import android.content.Context;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class OnlineAudioPlayerContract {
   public interface View extends BaseView {
       void showGradeList();
       void showAlbumList(List<String> album);



    }

   public interface  Presenter extends BasePresenter<View> {
        void getGradeList();
        void getAlbumList();
        void getAudioList();
        void getSearchList();
    }
}
