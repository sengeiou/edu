package com.ubt.alpha1e.onlineaudioplayer;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsActivity;
import com.ubt.alpha1e.behaviorhabits.fragment.HibitsEventFragment;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.onlineaudioplayer.Fragment.OnlineAudioResourcesFragment;
import com.ubt.alpha1e.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.CourseContentInfo;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class OnlineAudioPlayerActivity extends MVPBaseActivity<OnlineAudioPlayerContract.View, OnlineAudioPlayerPresenter> implements OnlineAudioPlayerContract.View {

    private String TAG="OnlineAudioPlayerActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnlineAudioResourcesFragment fragment = findFragment(OnlineAudioResourcesFragment.class);
        UbtLog.d(TAG, "OnlineAudioPlayerActivity = " + fragment);
        if (fragment == null) {
            fragment = OnlineAudioResourcesFragment.newInstance();
            loadRootFragment(R.id.rl_content, fragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, OnlineAudioPlayerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }



    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_behavior_habits;
    }



    @Override
    public void showCourseList(List<CourseContentInfo> album) {

    }

    @Override
    public void showAlbumList(Boolean status, List<AlbumContentInfo> album, String errorMsgs) {

    }

    @Override
    public void showAudioList(Boolean status, List<AudioContentInfo> album, String errorMsgs) {

    }

    @Override
    public void onRequestStatus(int requestType, int errorCode) {

    }
}
