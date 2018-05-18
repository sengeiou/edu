package com.ubt.alpha1e.onlineaudioplayer.categoryActivity;

import com.ubt.alpha1e.mvp.BasePresenter;
import com.ubt.alpha1e.mvp.BaseView;
import com.ubt.alpha1e.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.CategoryContentInfo;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class OnlineAudioPlayerContract {
   public interface View extends BaseView {
       void showCourseList(List<CategoryContentInfo>album);
       void showAlbumList(Boolean status, List<AlbumContentInfo> album, String errorMsgs);
       void showAudioList(Boolean status, List<AudioContentInfo> album, String errorMsgs);
       //网络请求错误回调  requestType 网络请求的标识，errorCode 错误码
       void onRequestStatus(int requestType, int errorCode);
    }

   public interface  Presenter extends BasePresenter<View> {
        void getAlbumList(String courseName);
        void getAudioList(String albumId);
    }
}
