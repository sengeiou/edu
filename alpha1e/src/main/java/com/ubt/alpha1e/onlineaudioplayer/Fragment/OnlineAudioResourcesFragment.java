package com.ubt.alpha1e.onlineaudioplayer.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.fragment.HibitsEventFragment;
import com.ubt.alpha1e.behaviorhabits.helper.HabitsHelper;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.onlineaudioplayer.OnlineAudioPlayerContract;
import com.ubt.alpha1e.onlineaudioplayer.OnlineAudioPlayerPresenter;
import com.ubt.alpha1e.onlineaudioplayer.helper.OnlineAudioResourcesHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @作者：ubt
 * @日期: 2018/4/4 10:37
 * @描述:
 */


public class OnlineAudioResourcesFragment extends MVPBaseFragment<OnlineAudioPlayerContract.View, OnlineAudioPlayerPresenter> implements OnlineAudioPlayerContract.View {

    Unbinder unbinder;
    private  OnlineAudioResourcesHelper mHelper = null;
    public static OnlineAudioResourcesFragment newInstance() {
        OnlineAudioResourcesFragment onlineAudioResourcesFragment = new OnlineAudioResourcesFragment();
        return onlineAudioResourcesFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        mHelper = new OnlineAudioResourcesHelper(getContext());
        return rootView;
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
        return R.layout.fragment_hibits_event;
    }

    @Override
    public void showGradeList() {

    }

    @Override
    public void showAlbumList(List<String> albumList) {


    }


}
