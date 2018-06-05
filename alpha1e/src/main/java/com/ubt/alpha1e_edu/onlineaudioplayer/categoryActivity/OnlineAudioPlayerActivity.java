package com.ubt.alpha1e_edu.onlineaudioplayer.categoryActivity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e_edu.mvp.MVPBaseFragment;
import com.ubt.alpha1e_edu.onlineaudioplayer.Fragment.OnlineAlbumListFragment;
import com.ubt.alpha1e_edu.onlineaudioplayer.Fragment.OnlineCategoryListFragment;
import com.ubt.alpha1e_edu.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e_edu.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e_edu.onlineaudioplayer.model.CategoryContentInfo;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.util.List;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class OnlineAudioPlayerActivity extends MVPBaseActivity<OnlineAudioPlayerContract.View, OnlineAudioPlayerPresenter> implements OnlineAudioPlayerContract.View {

    private String TAG="OnlineAudioPlayerActivity";

    private int TYPE = 0;

    private MVPBaseFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TYPE = getIntent().getIntExtra("TYPE",0);

        if(TYPE == 0){
            fragment = findFragment(OnlineCategoryListFragment.class);
            if (fragment == null) {
                fragment = OnlineCategoryListFragment.newInstance();
            }
        }else if(TYPE == 1){
            fragment = findFragment(OnlineAlbumListFragment.class);
            AlbumContentInfo info = (AlbumContentInfo)getIntent().getExtras().getSerializable("AlbumContentInfo");
            if (fragment == null) {
                fragment = OnlineAlbumListFragment.newInstance(info);
            }
        }
        UbtLog.d(TAG, "OnlineAudioPlayerActivity = " + fragment);
        loadRootFragment(R.id.rl_content, fragment);
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
    public void showCourseList(List<CategoryContentInfo> album) {

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
