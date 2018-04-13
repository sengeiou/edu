package com.ubt.alpha1e.onlineaudioplayer.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.fragment.HibitsEventFragment;
import com.ubt.alpha1e.behaviorhabits.helper.HabitsHelper;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.onlineaudioplayer.OnlineAudioPlayerContract;
import com.ubt.alpha1e.onlineaudioplayer.OnlineAudioPlayerPresenter;
import com.ubt.alpha1e.onlineaudioplayer.helper.OnlineAudioResourcesHelper;
import com.ubt.alpha1e.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.CourseContentInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @作者：ubt
 * @日期: 2018/4/4 10:37
 * @描述:
 */


public class OnlineAudioResourcesFragment extends MVPBaseFragment<OnlineAudioPlayerContract.View, OnlineAudioPlayerPresenter> implements OnlineAudioPlayerContract.View {
    @Override
    public void showAudioList(Boolean status, List<AudioContentInfo> album, String errorMsgs) {

    }

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_base_title_name)
    TextView mTitleName;
    @BindView(R.id.rl_base_search)
    RelativeLayout mRlSearch;
    @BindView(R.id.iv_search)
    ImageView mIvSearch;
    @BindView(R.id.button2)
    Button mButton;

    private  OnlineAudioResourcesHelper mHelper = null;
    public static OnlineAudioResourcesFragment newInstance() {
        OnlineAudioResourcesFragment onlineAudioResourcesFragment = new OnlineAudioResourcesFragment();
        return onlineAudioResourcesFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mHelper = new OnlineAudioResourcesHelper(getContext());
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   OnlineAudioAlbumPlayerFragment mfragment = OnlineAudioAlbumPlayerFragment.newInstance();
                   start(mfragment);
                }
        });
        mIvBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
               pop();
            }
        });
        return rootView;
    }

    @Override
    protected void initUI() {
         mTitleName.setText("在线资源库");
         mRlSearch.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_onlineaudio_album;
    }



    @Override
    public void showCourseList(List<CourseContentInfo> album) {

    }


    @Override
    public void showAlbumList(Boolean status, List<AlbumContentInfo> album, String errorMsgs) {

    }


    @Override
    public void onRequestStatus(int requestType, int errorCode) {

    }


}
