package com.ubt.alpha1e.onlineaudioplayer.Fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.onlineresAdpater;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.GotoBindRequest;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.onlineaudioplayer.DataObj.CategoryMax;
import com.ubt.alpha1e.onlineaudioplayer.DataObj.OnlineresList;
import com.ubt.alpha1e.onlineaudioplayer.helper.DividerItemDecorationNew;
import com.ubt.alpha1e.onlineaudioplayer.categoryActivity.OnlineAudioPlayerContract;
import com.ubt.alpha1e.onlineaudioplayer.categoryActivity.OnlineAudioPlayerPresenter;
import com.ubt.alpha1e.onlineaudioplayer.helper.OnlineAudioResourcesHelper;

import com.ubt.alpha1e.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.CourseContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.HistoryAudio;
import com.ubt.alpha1e.onlineaudioplayer.model.PlayerEvent;
import com.ubt.alpha1e.onlineaudioplayer.onlinereSrearch.OnlineResRearchActivity;
import com.ubt.alpha1e.onlineaudioplayer.playerDialog.OnlineAudioPlayDialog;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * @作者：ubt
 * @日期: 2018/4/4 10:37
 * @描述:
 */


public class OnlineAudioResourcesFragment extends MVPBaseFragment<OnlineAudioPlayerContract.View, OnlineAudioPlayerPresenter> implements OnlineAudioPlayerContract.View {

    String TAG = "OnlineAudioResourcesFragment";

    @BindView(R.id.online_res_list)
    RecyclerView mRecyclerview;

    @BindView(R.id.ib_return)
    ImageButton ib_return;

    @BindView(R.id.ib_rearch)
    ImageButton ib_rearch;

    @BindView(R.id.ig_player_state)
    ImageView ig_player_state;

    @BindView(R.id.player_name)
    TextView player_name;

    @BindView(R.id.ig_player_button)
    ImageView ig_player_button;

    public LinearLayoutManager mLayoutManager;
    public onlineresAdpater mAdapter;
    public List<OnlineresList> onlineresList = new ArrayList<>();
    public final static int LAUNCH_CATEGORY_ITEM = 1;
    private static final int GET_MAX_CATEGORY = 50;
    Unbinder unbinder;
    private boolean playStatus = false;
    private OnlineAudioResourcesHelper mHelper = null;
    OnlineAudioPlayDialog mPlayDialogOnlineAudioPlayDialog;
    HistoryAudio mHistory;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LAUNCH_CATEGORY_ITEM:
                    OnlineAudioAlbumPlayerFragment mfragment = OnlineAudioAlbumPlayerFragment.newInstance(msg.obj.toString());
                    start(mfragment);
                    break;
            }
        }
    };


    public static OnlineAudioResourcesFragment newInstance() {
        OnlineAudioResourcesFragment onlineAudioResourcesFragment = new OnlineAudioResourcesFragment();
        return onlineAudioResourcesFragment;
    }


    @OnClick({R.id.ib_return, R.id.ib_rearch, R.id.ig_player_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_return:
                if (mPlayDialogOnlineAudioPlayDialog != null) {
                    mPlayDialogOnlineAudioPlayDialog.stopPlay();
                }
                getActivity().finish();
                break;
            case R.id.ib_rearch:
                UbtLog.d(TAG, "ib_rearch");
                Intent i = new Intent();
                i.setClass(getActivity(), OnlineResRearchActivity.class);
                getActivity().startActivity(i);
                break;
            case R.id.ig_player_button:
                UbtLog.d(TAG, "ig_player_button");
                if (!playStatus) {
                    playStatus = true;
                    ig_player_button.setImageResource(R.drawable.ic_ct_stop);
                    mPresenter.getAudioList(mHistory.getAlbumId());
                } else {
                    ig_player_button.setImageResource(R.drawable.ic_ct_play_usable);
                    if (mPlayDialogOnlineAudioPlayDialog != null) {
                        mPlayDialogOnlineAudioPlayDialog.stopPlay();
                    }
                    playStatus = false;
                }
                break;
        }
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
        playing();
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerview.setLayoutManager(mLayoutManager);
        mRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        DividerItemDecorationNew dividerItemDecoration = new DividerItemDecorationNew(getActivity(), DividerItemDecorationNew.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.linesharp));
        mRecyclerview.addItemDecoration(dividerItemDecoration);
        mRecyclerview.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 100;
                outRect.left = 1;
                outRect.top = 45;
                outRect.bottom = 45;
            }
        });
        mAdapter = new onlineresAdpater(getActivity().getApplicationContext(), onlineresList, mHandler);
        mRecyclerview.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        getMaxCategory();
        mHistory = (HistoryAudio) SPUtils.getInstance().readObject(Constant.SP_ONLINEAUDIO_HISTORY);
        if (SPUtils.getInstance().readObject(Constant.SP_ONLINEAUDIO_HISTORY) != null) {
            player_name.setText(mHistory.getAlbumName());
        } else {
            player_name.setText("暂无播放历史");
        }
    }

    //拉最大的类别
    public void getMaxCategory() {
        com.ubt.alpha1e.base.loading.LoadingDialog.show(getActivity());
        GotoBindRequest gotoBindRequest = new GotoBindRequest();
        gotoBindRequest.setEquipmentId(AlphaApplication.currentRobotSN);
        gotoBindRequest.setSystemType("3");

        String url = HttpEntity.GET_MAX_CATERGOTY;
        doRequestGetMaxCategory(url, gotoBindRequest, GET_MAX_CATEGORY);

    }

    /**
     * 网络请求
     */
    public void doRequestGetMaxCategory(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "getMaxCategory onError:" + e.getMessage());
                switch (id) {
                    case GET_MAX_CATEGORY:
                        com.ubt.alpha1e.base.loading.LoadingDialog.dismiss(getActivity());
                        ToastUtils.showShort("请求失败");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "getMaxCategory response = " + response);
                switch (id) {
                    case GET_MAX_CATEGORY:
                        com.ubt.alpha1e.base.loading.LoadingDialog.dismiss(getActivity());
                        BaseResponseModel<ArrayList<CategoryMax>> modle = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<ArrayList<CategoryMax>>>() {
                                }.getType());//加上type转换，避免泛型擦除
                        if (modle.status) {
                            UbtLog.d(TAG, "请求成功");
                            if (modle.models.size() == 0) {
                                UbtLog.d(TAG, "没有类别");
                                return;
                            } else {
                                UbtLog.d(TAG, "size = " + modle.models.size());
                                int size = modle.models.size();
                                onlineresList.clear();
                                for (int i = 0; i < size; i++) {
                                    OnlineresList s = new OnlineresList();
                                    s.setRes_id(modle.models.get(i).getCategoryId());
                                    s.setRes_name(modle.models.get(i).getCategoryName());
                                    onlineresList.add(s);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            UbtLog.d(TAG, "请求失败");
                            ToastUtils.showShort("请求失败");
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {

        return R.layout.fragment_onlineres_list;
    }


    @Override
    public void showCourseList(List<CourseContentInfo> album) {

    }

    @Override
    public void showAlbumList(Boolean status, List<AlbumContentInfo> album, String errorMsgs) {

    }

    @Override
    public void showAudioList(Boolean status, List<AudioContentInfo> album, String errorMsgs) {
        playEvent(album, mHistory.getAlbumId());
    }

    @Override
    public void onRequestStatus(int requestType, int errorCode) {

    }

    private AnimationDrawable radiologicalWaveAnim = null;

    //正在播放
    public void playing() {
        ig_player_state.setVisibility(View.VISIBLE);
        ig_player_state.setBackground(getActivity().getDrawable(R.drawable.playindicator_animation));
        radiologicalWaveAnim = (AnimationDrawable) ig_player_state.getBackground();
        radiologicalWaveAnim.setOneShot(false);
        radiologicalWaveAnim.setVisible(true, true);
        player_name.setVisibility(View.VISIBLE);
        ig_player_button.setVisibility(View.VISIBLE);

    }

    //没有播放
    public void noPlaying() {
        ig_player_state.setVisibility(View.VISIBLE);
        ig_player_state.setBackgroundResource(R.drawable.cc_default_playindicator);
        player_name.setVisibility(View.VISIBLE);
        player_name.setText("当前无历史播放记录");
        ig_player_button.setVisibility(View.INVISIBLE);
    }

    //停止播放
    public void stopPlay() {
        ig_player_state.setVisibility(View.VISIBLE);
        ig_player_state.setBackgroundResource(R.drawable.cc_default_playindicator);
        player_name.setVisibility(View.VISIBLE);
        ig_player_button.setVisibility(View.VISIBLE);
    }

    private void playEvent(List<AudioContentInfo> playContentInfoList, String albumId) {
        if (mPlayDialogOnlineAudioPlayDialog == null) {
            mPlayDialogOnlineAudioPlayDialog = new OnlineAudioPlayDialog(getActivity())
                    .builder()
                    .setCancelable(true)
                    .setPlayContent(playContentInfoList)
                    .setCurrentAlbumId(albumId)
                    .setCallbackListener(new OnlineAudioPlayDialog.IHibitsEventPlayListener() {
                        @Override
                        public void onDismissCallback() {
                            UbtLog.d(TAG, "onDismissCallback");
                            mPlayDialogOnlineAudioPlayDialog.hidden();
                        }
                    });
        }
        mPlayDialogOnlineAudioPlayDialog.startPlay();

    }

    @Subscribe
    public void onEventOnlinePlayerEvent(final PlayerEvent mPlayerEvent) {
        if (mPlayerEvent.getEvent() == PlayerEvent.Event.CONTROL_PLAY_NEXT) {
            UbtLog.d(TAG, "CONTROL_PLAY event = next " + mPlayerEvent.getCurrentPlayingSongName());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player_name.setText(mPlayerEvent.getCurrentPlayingSongName());
                }

                ;
            });

        }
    }
}

