package com.ubt.alpha1e.onlineaudioplayer.Fragment;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.bluetoothandnet.bluetoothguidestartrobot.BluetoothguidestartrobotActivity;
import com.ubt.alpha1e.course.feature.FeatureActivity;
import com.ubt.alpha1e.course.merge.MergeActivity;
import com.ubt.alpha1e.course.principle.PrincipleActivity;
import com.ubt.alpha1e.course.split.SplitActivity;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.onlineaudioplayer.categoryActivity.OnlineAudioPlayerContract;
import com.ubt.alpha1e.onlineaudioplayer.categoryActivity.OnlineAudioPlayerPresenter;
import com.ubt.alpha1e.onlineaudioplayer.adapter.OnlineAudioListRecyclerAdapter;
import com.ubt.alpha1e.onlineaudioplayer.helper.OnlineAudioResourcesHelper;
import com.ubt.alpha1e.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.CategoryContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.PlayerEvent;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class OnlineAudioListFragment extends MVPBaseFragment<OnlineAudioPlayerContract.View, OnlineAudioPlayerPresenter> implements OnlineAudioPlayerContract.View {

    private static final String TAG = OnlineAudioListFragment.class.getSimpleName();
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    RecyclerView rvEventList;
    ImageView mBack;
    private LinearLayoutManager mLayoutManager = null;
    public OnlineAudioListRecyclerAdapter mAdapter;
    // public static List<AudioContentInfo> mPlayContentInfoDatas =  new ArrayList<>();
    private boolean isStartPlayProcess = true;//是否开启播放流程
    private static String currentAlbumId = "";
    private static String currentCategoryId = "";
    private static String mAlbumName = "";
    private int currentPlaySeq = -1;
    private boolean isFirstPlay = true;
    private View mView;
    private static OnlineAudioResourcesHelper mHelper;
    private ImageView ivPlayStatus;
    private ImageView ivPlayNone;
    private ImageView ivMusicLast;
    private ImageView ivMusicPlay;
    private ImageView ivMusicStop;
    private ImageView ivMusicNext;
    private ImageView ivRecycleButton;
    RelativeLayout rl_no_net;
    RelativeLayout rl_content;
    RelativeLayout rl_play_content;
    public static int SINGLE_AUDIO_PLAYING = 2;
    public static int RECYCLE_AUDIO_LIST_PLAYING = 1;
    public static int ORDER_AUDIO_LIST_PLAYING = 0;
    private int isRecycleType = ORDER_AUDIO_LIST_PLAYING;
    private AnimationDrawable playStatusAnim = null;
    private TextView tvPlayName;
    private static final int UPDATE_CURRENT_PLAY = 1;
    private static final int PAUSE_CURRENT_PLAY = 2;
    private static final int STOP_CURRENT_PLAY = 3;
    private static final int DIFFALBUM_CURRENT_PLAY = 4;
    private AudioContentInfo currentPlayInfo = null;
    private String ORDER_LOOP = "0";
    private String RECYCLE_LOOP = "1";
    private String SINGLE_LOOP = "2";
    private String mPlayingalbumID;
    private String mInitTextName = "暂无播放内容";
    private String PLAYING_STATE = "playing";
    private String PAUSE_STATE = "pause";
    private String STOP_STATE = "quit";
    private boolean isShowHibitsDialog = false;

    @Override
    public void onResume() {
        super.onResume();
        UbtLog.d(TAG, "onResume");
        if (mHelper != null) {
            UbtLog.d(TAG, "--wmma--mHelper RegisterHelper! " + mHelper.getClass().getSimpleName());
            mHelper.RegisterHelper();
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        //获取列表
        mPresenter.getAudioList(currentAlbumId);
        if (mHelper.isStartHibitsProcess()) {
            isShowHibitsDialog = true;
            String msg = "行为习惯正在进行中，请先完成";
            String position = "好的";
            new ConfirmDialog(getActivity())
                    .builder()
                    .setMsg(msg)
                    .setCancelable(false)
                    .setPositiveButton(position, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isShowHibitsDialog = false;
                            if (mHelper.isStartHibitsProcess()) {
                                //行为习惯流程未结束，退出当前流程
                                if (getActivity() != null) {
                                    getActivity().finish();
                                }
                            }
                        }
                    }).show();

        }
    }

    @Override
    public void onPause() {
        UbtLog.d(TAG, "onPause");
        super.onPause();
        if (mHelper != null) {
            UbtLog.d(TAG, "--wmma--mHelper UnRegisterHelper! " + mHelper.getClass().getSimpleName());
            mHelper.UnRegisterHelper();
        }
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    public static OnlineAudioListFragment newInstance(AlbumContentInfo mAlbum) {
        UbtLog.d(TAG, "current album ID" + mAlbum.getAlbumId() + "albumName " + mAlbumName + "mCategoryId" + mAlbum.getCategoryId());
        OnlineAudioListFragment mOnlineAudioEventListFragment = new OnlineAudioListFragment();
        currentAlbumId = mAlbum.getAlbumId();
        if (mAlbum.getCategoryId() != null)
            currentCategoryId = mAlbum.getCategoryId();
        mAlbumName = mAlbum.albumName;
        return mOnlineAudioEventListFragment;
    }

    @Override
    protected void initUI() {
        UbtLog.d(TAG, "rvHabitsEvent => " + rvEventList);
        RecyclerView.ItemAnimator animator = rvEventList.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    protected void initBoardCastListener() {

    }

    /**
     * 移动到某个下标
     *
     * @param playIndex
     */
    private void moveToPosition(int playIndex) {

        if (playIndex == -1 || !isFirstPlay) {
            return;
        }

        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = mLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLayoutManager.findLastVisibleItemPosition();
        UbtLog.d(TAG, "moveToPosition firstItem = " + firstItem + "    lastItem = " + lastItem + "   playIndex = " + playIndex);
        //然后区分情况
        if (playIndex <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            rvEventList.scrollToPosition(playIndex);
        } else if (playIndex <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int top = rvEventList.getChildAt(playIndex - firstItem).getTop();
            rvEventList.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            rvEventList.scrollToPosition(playIndex);
            //这里这个变量是用在RecyclerView滚动监听里面的
            //move = true;
        }
        isFirstPlay = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_play_event_list, container, false);
        UbtLog.d(TAG, "onCreatView  ");
        ButterKnife.bind(getActivity());
        mHelper = OnlineAudioResourcesHelper.getInstance(getContext());
        mHelper.setmCategoryId(currentCategoryId);
        mHelper.setAlbumId(currentAlbumId);
        mAdapter = new OnlineAudioListRecyclerAdapter(getContext(), mHandler, mHelper);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvEventList = (RecyclerView) mView.findViewById(R.id.rv_event_list);
        mBack = (ImageView) mView.findViewById(R.id.iv_back);
        tvBaseTitleName = (TextView) mView.findViewById(R.id.tv_base_title_name);
        ivPlayNone = mView.findViewById(R.id.iv_play_status_none);
        ivMusicLast = mView.findViewById(R.id.iv_music_prev);
        ivMusicPlay = mView.findViewById(R.id.iv_music_play);
        ivMusicStop = mView.findViewById(R.id.iv_music_stop);
        ivMusicNext = mView.findViewById(R.id.iv_music_next);
        ivRecycleButton = mView.findViewById(R.id.iv_music_circle);
        ivPlayStatus = mView.findViewById(R.id.iv_play_status);
        tvPlayName = mView.findViewById(R.id.tv_play_name);
        rl_no_net=mView.findViewById(R.id.rl_no_net);
        rl_content =mView.findViewById(R.id.rl_content);
        rl_play_content =mView.findViewById(R.id.rl_play_content);

        ivMusicLast.setOnClickListener(mOnClickListener);
        ivMusicPlay.setOnClickListener(mOnClickListener);
        ivMusicStop.setOnClickListener(mOnClickListener);
        ivMusicNext.setOnClickListener(mOnClickListener);
        ivRecycleButton.setOnClickListener(mOnClickListener);
        tvPlayName.setOnClickListener(mOnClickListener);
        mBack.setOnClickListener(mOnClickListener);
        rl_no_net.setOnClickListener(mOnClickListener);

        playStatusAnim = (AnimationDrawable) ivPlayStatus.getBackground();
        playStatusAnim.setOneShot(false);
        playStatusAnim.setVisible(true, true);
        tvBaseTitleName.setText(mAlbumName);
        rvEventList.setLayoutManager(mLayoutManager);
        mBack.setVisibility(View.VISIBLE);
        return mView;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_music_list:
                    break;
                case R.id.iv_music_prev:
                    if (tvPlayName.getText() != null && tvPlayName.getText().toString().equals(mInitTextName)) {
                        return;
                    }
                    prevAudioPlay();
                    break;
                case R.id.iv_music_play:
                    if (tvPlayName.getText() != null && tvPlayName.getText().toString().equals(mInitTextName)) {
                        return;
                    }
                    if (mHelper.ismPlayStatus()) {
                        mHelper.setmPlayStatus(false);
                        mHelper.continueEvent();
                        ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
                        playStatusAnim.start();
                        initState(PLAYING_STATE);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mHelper.setmPlayStatus(true);
                        mHelper.pauseEvent();
                        ivMusicPlay.setImageResource(R.drawable.ic_ct_play_usable);
                        playStatusAnim.stop();
                        initState(PAUSE_STATE);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.iv_music_next:
                    if (tvPlayName.getText() != null && tvPlayName.getText().toString().equals(mInitTextName)) {
                        return;
                    }
                    if (isStartPlayProcess) {
                        nextAudioPlay();
                    }
                    break;
                case R.id.iv_music_circle:
                    if (tvPlayName.getText() != null && tvPlayName.getText().toString().equals(mInitTextName)) {
                        return;
                    }
                    loopModeUiShow();
                    break;
                case R.id.tv_play_name:
                    //DESTROY PREVIOUS AND TO NEXT
                    if (mPlayingalbumID != null && !mPlayingalbumID.equals(currentAlbumId)) {
                        UbtLog.d(TAG, "JUMP TO ALBUM DIFF ");
                        EventBus.getDefault().unregister(this);
                        pop();
                        AlbumContentInfo mAlbum = new AlbumContentInfo();
                        mAlbum.setAlbumId(mPlayingalbumID);
                        OnlineAudioListFragment mfragment = OnlineAudioListFragment.newInstance(mAlbum);
                        start(mfragment);
                    } else {
                        UbtLog.d(TAG, "JUMP TO ALBUM equal");
                    }
                    break;
                case R.id.iv_back:
                    EventBus.getDefault().unregister(this);
                    pop();
                    break;
                case R.id.rl_no_net:
                    try {
                        mPresenter.getAudioList(currentAlbumId);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void loopModeUiShow() {
        if (isRecycleType == ORDER_AUDIO_LIST_PLAYING) {
            isRecycleType = RECYCLE_AUDIO_LIST_PLAYING;
            mHelper.setPlayType(RECYCLE_AUDIO_LIST_PLAYING);
            ivRecycleButton.setImageResource(R.drawable.ic_circle_list);
            mHelper.sendOnlineAudioLoopMode(RECYCLE_LOOP);
            // Toast.makeText(getActivity(),"RECYCLE", Toast.LENGTH_LONG).show();
        } else if (isRecycleType == RECYCLE_AUDIO_LIST_PLAYING) {
            isRecycleType = SINGLE_AUDIO_PLAYING;
            mHelper.setPlayType(SINGLE_AUDIO_PLAYING);
            ivRecycleButton.setImageResource(R.drawable.ic_circle_single);
            mHelper.sendOnlineAudioLoopMode(SINGLE_LOOP);
            //  Toast.makeText(getActivity(),"SINGLE", Toast.LENGTH_LONG).show();
        } else if (isRecycleType == SINGLE_AUDIO_PLAYING) {
            isRecycleType = ORDER_AUDIO_LIST_PLAYING;
            mHelper.setPlayType(ORDER_AUDIO_LIST_PLAYING);
            ivRecycleButton.setImageResource(R.drawable.ic_circle_listplay);
            mHelper.sendOnlineAudioLoopMode(ORDER_LOOP);
            //  Toast.makeText(getActivity(),"ORDER", Toast.LENGTH_LONG).show();
        }
    }

    private void loopModeStatusShow() {
        if (isRecycleType == ORDER_AUDIO_LIST_PLAYING) {
            ivRecycleButton.setImageResource(R.drawable.ic_circle_listplay);
            // Toast.makeText(getActivity(),"RECYCLE", Toast.LENGTH_LONG).show();
        } else if (isRecycleType == RECYCLE_AUDIO_LIST_PLAYING) {
            ivRecycleButton.setImageResource(R.drawable.ic_circle_list);
            //  Toast.makeText(getActivity(),"SINGLE", Toast.LENGTH_LONG).show();
        } else if (isRecycleType == SINGLE_AUDIO_PLAYING) {
            ivRecycleButton.setImageResource(R.drawable.ic_circle_single);
            //  Toast.makeText(getActivity(),"ORDER", Toast.LENGTH_LONG).show();
        }
    }

    private void prevAudioPlay() {
        mHelper.setmPlayStatus(false);
        mHelper.prevAudioPlay();
        mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
    }

    private void nextAudioPlay() {
        mHelper.setmPlayStatus(false);
        mHelper.nextAudioPlay();
        mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
    }

    @Override
    public void showCourseList(List<CategoryContentInfo> album) {

    }


    @Override
    public void showAlbumList(Boolean status, List<AlbumContentInfo> album, String errorMsgs) {

    }

    @Override
    public void showAudioList(Boolean status, List<AudioContentInfo> album, String errorMsgs) {
        UbtLog.d(TAG, "request result from back-end " + album);
        if (album != null) {
            rl_content.setVisibility(View.VISIBLE);
            rl_no_net.setVisibility(View.INVISIBLE);
            rl_play_content.setVisibility(View.VISIBLE);
            UbtLog.d(TAG, "request result from back-end notnull " + album);
            mHelper.setPlayContent(album);
            mAdapter.setData(album);
            rvEventList.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            //获取机器人当前播放状态 TODO: SOMETIME CAN'T SHOW ANIMATION EFFECTS.
            mHelper.getRobotOnlineAudioStatus();
        } else {
            Toast.makeText(getActivity(), "后台出错，没有配置数据", Toast.LENGTH_LONG).show();
            rl_content.setVisibility(View.INVISIBLE);
            rl_no_net.setVisibility(View.VISIBLE);
            rl_play_content.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRequestStatus(int requestType, int errorCode) {
        rl_content.setVisibility(View.INVISIBLE);
        rl_no_net.setVisibility(View.VISIBLE);
        rl_play_content.setVisibility(View.INVISIBLE);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CURRENT_PLAY:
                    if (currentPlayInfo == null || currentPlaySeq == -1) {
                        if (mHelper.getPlayContent() != null && mHelper.getPlayContent().size() > 0) {
                            currentPlayInfo = mHelper.getPlayContent().get(0);
                            currentPlaySeq = 0;
                        } else {
                            return;
                        }
                    }
                    //set pause/play status false is equal playing
                    mHelper.setmPlayStatus(false);
                    ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
                    ivPlayNone.setVisibility(View.GONE);
                    ivPlayStatus.setVisibility(View.VISIBLE);
                    playStatusAnim.start();
                    clearPlayingStatusFlag();
                    UbtLog.d(TAG, "UPDATE_CURRENT_PLAY " + mHelper.getCurrentPlayingAudioIndex());
                    UbtLog.d(TAG, "UPDATE_CURRENT_PLAY  currentAlbumId  " + currentAlbumId);
                    UbtLog.d(TAG, "UPDATE_CURRENT_PLAY  getmAlbumPlayingId" + mHelper.getmAlbumPlayingId());
                    if (currentAlbumId.equals(mHelper.getmAlbumPlayingId())) {
                        setPlayingStatusFlag(true);
                        moveToPosition(mHelper.getCurrentPlayingAudioIndex());
                    }
                    initState(PLAYING_STATE);
                    mAdapter.notifyDataSetChanged();
                    break;
                case STOP_CURRENT_PLAY:
                    mHelper.setmPlayStatus(true);
//                    tvPlayName.setText("暂无播放内容"+mHelper.getPlayingContent().get(msg.arg1).contentName);
                    clearPlayingStatusFlag();
                    UbtLog.d(TAG, "INDEX " + msg.arg1);
                    //moveToPosition(mHelper.getCurrentPlayingAudioIndex());
                    initState(STOP_STATE);
                    mAdapter.notifyDataSetChanged();
                    break;
                case PAUSE_CURRENT_PLAY:
                    UbtLog.d(TAG, "PAUSE PLAYING STATUS");
                    //clearPlayingStatusFlag();
                    mHelper.setmPlayStatus(true);
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    initState(PAUSE_STATE);
                    break;
                case DIFFALBUM_CURRENT_PLAY:
                    mPlayingalbumID = (String) msg.obj;
                    UbtLog.d(TAG, "DIFFALBUM_CURRENT_PLAY   " + mPlayingalbumID + mHelper.getAlbumId());
                    initState(PLAYING_STATE);
                    break;
                default:
                    break;
            }
        }
    };

    private void setPlayingStatusFlag(boolean status) {
        mHelper.getPlayingContent().get(mHelper.getCurrentPlayingAudioIndex()).isPlaying = status;
        UbtLog.d(TAG, "mHelper isPlaying " + mHelper.getPlayingContent().get(mHelper.getCurrentPlayingAudioIndex()).isPlaying);
    }

    private void clearPlayingStatusFlag() {
        for (AudioContentInfo mPlayContentInfo : mHelper.getPlayingContent()) {
            mPlayContentInfo.isPlaying = false;
        }
    }

    /**
     * 初始化机器人状态
     */
    private void initState(String playStatus) {
        UbtLog.d(TAG, "initRobotState mCurrentVolume = " + mHelper.mCurrentVolume + "   mCurrentVoiceState " + mHelper.mCurrentVoiceState + "   mLightState = " + mHelper.mLightState);
        if ("playing".equals(playStatus)) {
            ivPlayNone.setVisibility(View.GONE);
            ivPlayStatus.setVisibility(View.VISIBLE);
            ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
            playStatusAnim.start();
            ivMusicLast.setImageResource(R.drawable.ic_music_last_usable);
            ivMusicStop.setImageResource(R.drawable.ic_ct_stop);
            ivMusicNext.setImageResource(R.drawable.ic_music_next_usable);
            String playName = "正在播放：" + mHelper.getPlayingContent().get(mHelper.getCurrentPlayingAudioIndex()).contentName /*+ "_" + currentPlaySeq*/;
            tvPlayName.setText(playName);
        } else if ("pause".equals(playStatus)) {
            ivPlayNone.setVisibility(View.GONE);
            ivPlayStatus.setVisibility(View.VISIBLE);
            ivMusicPlay.setImageResource(R.drawable.ic_ct_play_usable);
            playStatusAnim.stop();
            ivMusicLast.setImageResource(R.drawable.ic_music_last_usable);
            ivMusicStop.setImageResource(R.drawable.ic_ct_stop);
            ivMusicNext.setImageResource(R.drawable.ic_music_next_usable);
            tvPlayName.setText("暂停播放:" + mHelper.getPlayingContent().get(mHelper.getCurrentPlayingAudioIndex()).contentName);
        } else if ("quit".equals(playStatus)) {
            //noPlay
            ivPlayNone.setVisibility(View.VISIBLE);
            ivPlayStatus.setVisibility(View.GONE);
            ivMusicPlay.setImageResource(R.drawable.ic_ct_play_disable);
            playStatusAnim.stop();
            ivMusicLast.setImageResource(R.drawable.ic_music_last_disable);
            ivMusicStop.setImageResource(R.drawable.ic_ct_stop);
            ivMusicNext.setImageResource(R.drawable.ic_music_next_disable);
            tvPlayName.setText(mInitTextName);
        }
    }


    @Subscribe
    public void onEventPlayerEvent(final PlayerEvent event) {
        UbtLog.d(TAG, "event = " + event.toString());
        if (event.getEvent() == PlayerEvent.Event.CONTROL_PLAY_NEXT) {
            UbtLog.d(TAG, "EventBus Receive: CONTROL_PLAY event = next " + event.getCurrentPlayingSongName());
            mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
        } else if (event.getEvent() == PlayerEvent.Event.GET_ROBOT_ONLINEPLAYING_STATUS) {
            UbtLog.d(TAG, "EventBus Receive: ONELINE STATUS " + event.getStatus());
            //TODO REFACTOR
            isRecycleType = mHelper.getPlayType();
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loopModeStatusShow();
                    }

                    ;
                });
            }
            if (event.getStatus().equals("playing")||event.getStatus().equals("")) {
                UbtLog.d(TAG, "PLAYING STATUS PLAYING");
                if (currentAlbumId.equals(event.getAlbumId())) {
                    mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
                } else {
                    //TODO NEW REQUIREMENT FROM PRODUCT MANAGER
                    Message mDiffAlbum = new Message();
                    mDiffAlbum.what = DIFFALBUM_CURRENT_PLAY;
                    mDiffAlbum.arg1 = event.getCurrentPlayingIndex();
                    mDiffAlbum.obj = event.getAlbumId();
                    mHandler.sendMessage(mDiffAlbum);
                }
                //SET CURRENT PLAYING ID
                setPlayingIdInfo(event);
            } else if (event.getStatus().equals("pause")) {
                UbtLog.d(TAG, "PAUSE STATUS PLAYING");
                //if (currentAlbumId.equals(event.getAlbumId())) {
                Message pauseInfo = new Message();
                pauseInfo.what = PAUSE_CURRENT_PLAY;
                pauseInfo.arg1 = event.getCurrentPlayingIndex();
                mHandler.sendMessage(pauseInfo);
//                if (getActivity() != null) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            initState("pause");
//                        }
//
//                        ;
//                    });
//                }
                //SET CURRENT PLAYING ID
                setPlayingIdInfo(event);
                //  }
            } else if (event.getStatus().equals("quit")) {
                UbtLog.d(TAG, "QUIT STATUS PLAYING");
//                if (getActivity() != null) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            initState("quit");
//                        }
//
//                        ;
//                    });
//                }
                mHandler.sendEmptyMessage(STOP_CURRENT_PLAY);
            }
        } else if (event.getEvent() == PlayerEvent.Event.CONTROL_STOP) {
            UbtLog.d(TAG, "EventBus Receive: CONTROL_STOP STATUS");
            mHandler.sendEmptyMessage(STOP_CURRENT_PLAY);
        } else if (event.getEvent() == PlayerEvent.Event.TAP_HEAD_OR_VOICE_WAKE) {
            UbtLog.d(TAG, "EventBus Receive: TAP_HEAD_OR_VOICE_WAKE");
        }else if(event.getEvent()==PlayerEvent.Event.ROBOT_NETWORK_DISCONNECT){
            UbtLog.d(TAG, "ROBOT_NETWORK_DISCONNECT");
            ToastUtils.showShort("机器人网络未连接，请给机器人配网");
        }
    }

    private void setPlayingIdInfo(PlayerEvent event) {
        mHelper.setmCategoryPlayingId(event.getCateogryId());
        mHelper.setmAlbumPlayingId(event.getAlbumId());
        mHelper.setCurentPlayingAudioIndex(event.getCurrentPlayingIndex());
    }

    //Conflict Deal: habit behaviour and bluetooth disconnect
    @Subscribe
    public void onEventRobot(RobotEvent event) {
        if (event.getEvent() == RobotEvent.Event.HIBITS_PROCESS_STATUS) {
            //流程开始，收到行为提醒状态改变，开始则退出流程，并Toast提示
            UbtLog.d(TAG, "onEventRobot = obj == 2" + event.isHibitsProcessStatus());
            if (event.isHibitsProcessStatus() && !isShowHibitsDialog) {
                UbtLog.d(TAG, "onEventRobot = obj == 3");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "行为习惯正在进行中，请先完成";
                        String position = "好的";
                        msg = ResourceManager.getInstance(getActivity()).getStringResources("ui_habits_process_starting");
                        position = ResourceManager.getInstance(getActivity()).getStringResources("ui_common_ok");
                        new ConfirmDialog(getActivity()).builder()
                                .setTitle("提示")
                                .setMsg(msg)
                                .setCancelable(false)
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        UbtLog.d(TAG, "确定");
                                        if (getActivity() != null) {
                                            getActivity().finish();
                                        }
                                    }
                                }).show();
                    }
                });
                //行为习惯流程未结束，退出当前流程
            }
        } else if (event.getEvent() == RobotEvent.Event.DISCONNECT) {
            UbtLog.d(TAG, "DISCONNECT THE BLUETOOTH");
            mHandler.sendEmptyMessage(STOP_CURRENT_PLAY);
        }
    }




}

