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
import android.widget.Toast;

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
import com.ubt.alpha1e.onlineaudioplayer.model.CategoryContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.PlayerEvent;
import com.ubt.alpha1e.onlineaudioplayer.searchActivity.OnlineResRearchActivity;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
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


public class OnlineCategoryListFragment extends MVPBaseFragment<OnlineAudioPlayerContract.View, OnlineAudioPlayerPresenter> implements OnlineAudioPlayerContract.View {

    String TAG = "OnlineCategoryListFragment";

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

    @BindView(R.id.ig_player_list)
    ImageView ig_player_list;

    public LinearLayoutManager mLayoutManager;
    public onlineresAdpater mAdapter;
    public List<OnlineresList> onlineresList = new ArrayList<>();
    public final static int LAUNCH_CATEGORY_ITEM = 1;
    public final static int STOP_CURRENT_PLAY=2;
    private static final int GET_MAX_CATEGORY = 50;
    private static final int REQUEST_RESULT=1001;
    private static final int REQUEST_RESULT_PLAYLIST=1002;
    Unbinder unbinder;
    private boolean playStatus = false;
    private OnlineAudioResourcesHelper mHelper = null;
    AlbumContentInfo mAlbumHistory;
    int index=-1;
    List<AudioContentInfo> mAudioContentList;
    private boolean isPause = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LAUNCH_CATEGORY_ITEM:
                    OnlineresList mCategory=(OnlineresList)msg.obj;
                    OnlineAlbumListFragment mfragment = OnlineAlbumListFragment.newInstance(mCategory);
                    startForResult(mfragment,REQUEST_RESULT);
                    onPause();
                    break;
                case STOP_CURRENT_PLAY:
                    isPause = true ;
                    pausePlay();
                    break;
            }
        }
    };


    public static OnlineCategoryListFragment newInstance() {
        OnlineCategoryListFragment onlineAudioResourcesFragment = new OnlineCategoryListFragment();
        return onlineAudioResourcesFragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.DistoryHelper();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHelper != null) {
            UbtLog.d(TAG, "--wmma--mHelper UnRegisterHelper! " + mHelper.getClass().getSimpleName());
            mHelper.UnRegisterHelper();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHelper != null) {
            UbtLog.d(TAG, "--wmma--mHelper RegisterHelper! " + mHelper.getClass().getSimpleName());
            mHelper.RegisterHelper();
        }
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        //获取机器人当前播放状态
        mHelper=OnlineAudioResourcesHelper.getInstance(getContext());
        mHelper.getRobotOnlineAudioStatus();
    }

    @OnClick({R.id.ib_return, R.id.ib_rearch, R.id.ig_player_button,R.id.ig_player_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_return:
                EventBus.getDefault().unregister(this);
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
                if(player_name != null && player_name.getVisibility() == View.VISIBLE && player_name.getText().toString().equals("暂无播放历史")){
                    return;
                }
                onlineAudioPlayerStatus();
                break;
            case R.id.ig_player_list:
                if(mAlbumHistory!=null) {
                    UbtLog.d(TAG,"456  mAlbumHistory  "+mAlbumHistory.getAlbumId());
                    if(!mAlbumHistory.getAlbumId().equals("")) {
                        OnlineAudioListFragment mfragment = OnlineAudioListFragment.newInstance(mAlbumHistory);
                        startForResult(mfragment, REQUEST_RESULT_PLAYLIST);
                        onPause();
                    }else {
                         Toast.makeText(getActivity(),"SERVICE REPLY NULL ALBUMID", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void onlineAudioPlayerStatus() {
            if(isPause){
                isPause = false;
                mHelper.continueEvent();
                playing();
                mHelper.playEvent("playing",mHelper.getmCategoryId(),mHelper.getAlbumId(),mHelper.getCurrentPlayingAudioIndex());
                ig_player_button.setImageResource(R.drawable.ic_ct_pause);
            }else {
                isPause = true;
                mHelper.pauseEvent();
                if(radiologicalWaveAnim!=null) {
                    radiologicalWaveAnim.stop();
                }
                ig_player_button.setImageResource(R.drawable.ic_ct_play_usable);
            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        //获取机器人当前播放状态 TODO ONRESUME  NO EXECTUION ??
        mHelper=OnlineAudioResourcesHelper.getInstance(getContext());
        mHelper.getRobotOnlineAudioStatus();
        return rootView;
    }

    @Override
    protected void initUI() {
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerview.setLayoutManager(mLayoutManager);
        mRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        DividerItemDecorationNew dividerItemDecoration = new DividerItemDecorationNew(getActivity(), DividerItemDecorationNew.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.linesharp));
        mRecyclerview.addItemDecoration(dividerItemDecoration);
        player_name.setText("暂无播放历史");
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
//        mHandler.sendEmptyMessage(STOP_CURRENT_PLAY);
        pausePlay();
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
    public void onStop() {
        super.onStop();
        UbtLog.d(TAG,"onStop  ");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showCourseList(List<CategoryContentInfo> album) {

    }

    @Override
    public void showAlbumList(Boolean status, List<AlbumContentInfo> album, String errorMsgs) {

    }

    @Override
    public void showAudioList(Boolean status, List<AudioContentInfo> album, String errorMsgs) {
        try{
        if(album!=null) {
            UbtLog.d(TAG,"show song name" + album.size());
            player_name.setText(album.get(index).contentName);
            //set playing content
            mHelper.setPlayingContent(album);
            if(ig_player_button!=null){
                ig_player_button.setVisibility(View.VISIBLE);
                ig_player_list.setImageResource(R.drawable.ic_list);
                ig_player_button.setVisibility(View.VISIBLE);
                if(isPause){
                    UbtLog.d(TAG,"isPause 1");
                    ig_player_button.setImageResource(R.drawable.ic_ct_play_usable);
                }else{
                    UbtLog.d(TAG,"isPause 2");
                    ig_player_button.setImageResource(R.drawable.ic_ct_pause);
                }
            }
        }}catch(Exception e){
            e.printStackTrace();
        }

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
        radiologicalWaveAnim.start();
        player_name.setVisibility(View.VISIBLE);
        ig_player_button.setVisibility(View.VISIBLE);
        ig_player_button.setImageResource(R.drawable.ic_ct_pause);
        ig_player_list.setImageResource(R.drawable.ic_list);
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
    public void pausePlay() {
        if(ig_player_state!=null) {
            ig_player_state.setVisibility(View.VISIBLE);
            ig_player_state.setBackground(getActivity().getDrawable(R.drawable.playindicator_animation));
        }
        if(player_name!=null)
        player_name.setVisibility(View.VISIBLE);
        if(ig_player_button!=null){
            UbtLog.d(TAG,"PAUSEPLAY");
            ig_player_button.setVisibility(View.VISIBLE);
            ig_player_button.setImageResource(R.drawable.ic_ct_play_usable);
            ig_player_list.setImageResource(R.drawable.ic_list);
        }
        if(radiologicalWaveAnim!=null) {
            radiologicalWaveAnim.stop();
        }

        if(player_name != null && player_name.getVisibility() == View.VISIBLE && player_name.getText().toString().equals("暂无播放历史")){
            ig_player_button.setImageResource(R.drawable.ic_play_disable);
            ig_player_list.setImageResource(R.drawable.ic_list_disable);
//            ig_player_state.setImageResource(R.drawable.cc_default_playindicator);
        }
    }

    @Subscribe
    public void onEvent(final PlayerEvent event) {
        UbtLog.d(TAG,"event = " + event.toString());
        if (event.getEvent() == PlayerEvent.Event.CONTROL_PLAY_NEXT) {
            UbtLog.d(TAG, "CONTROL_PLAY event = next " + event.getCurrentPlayingSongName());
            if(event.getCurrentPlayingSongName() == null ){
                return;
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        player_name.setText(event.getCurrentPlayingSongName());
                    };
                });
            }
        } else if (event.getEvent() == PlayerEvent.Event.TAP_HEAD_OR_VOICE_WAKE) {
            UbtLog.d(TAG,"TAP_HEAD_OR_VOICE_WAKE");
            mHandler.sendEmptyMessage(STOP_CURRENT_PLAY);
        } else if (event.getEvent() == PlayerEvent.Event.GET_ROBOT_ONLINEPLAYING_STATUS) {
            UbtLog.d(TAG, "show albumId  " + event.getAlbumId() + "    status:   "+event.getStatus()+"categoryID"+event.getCateogryId());
            //GET AUDIO SONG
            mPresenter.getAudioList(event.getAlbumId());
            mAlbumHistory = new AlbumContentInfo();
            mAlbumHistory.setAlbumId(event.getAlbumId());
            mAlbumHistory.setCategoryId(event.getCateogryId());
            //SET CURRENT PLAY INFORMATION
//            mHelper.setmCategoryId(event.getCateogryId());
//            mHelper.setAlbumId(event.getAlbumId());
            index = event.getCurrentPlayingIndex();
            if (event.getStatus().equals("playing")) {
                isPause = false;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UbtLog.d(TAG, "ONLINE STATUS PLAYING");
                            playing();
                        }

                        ;
                    });
                }
            } else if (event.getStatus().equals("pause")) {
                isPause = true;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UbtLog.d(TAG, "ONLINE STATUS PAUSE : " +player_name.getText().toString());
                            pausePlay();
                        }

                        ;
                    });
                }
            } else if (event.getStatus().equals("quit")) {
                mHandler.sendEmptyMessage(STOP_CURRENT_PLAY);
            }
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_RESULT){
            onResume();
        }else if(requestCode==REQUEST_RESULT_PLAYLIST){
            onResume();
        }
    }
}

