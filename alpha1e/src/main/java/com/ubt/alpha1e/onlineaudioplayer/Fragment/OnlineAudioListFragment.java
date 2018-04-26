package com.ubt.alpha1e.onlineaudioplayer.Fragment;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.model.EventPlayStatus;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.onlineaudioplayer.categoryActivity.OnlineAudioPlayerContract;
import com.ubt.alpha1e.onlineaudioplayer.categoryActivity.OnlineAudioPlayerPresenter;
import com.ubt.alpha1e.onlineaudioplayer.adapter.OnlineAudioListRecyclerAdapter;
import com.ubt.alpha1e.onlineaudioplayer.helper.OnlineAudioResourcesHelper;
import com.ubt.alpha1e.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.CourseContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.PlayerEvent;
import com.ubt.alpha1e.utils.StringUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class OnlineAudioListFragment extends MVPBaseFragment<OnlineAudioPlayerContract.View, OnlineAudioPlayerPresenter> implements OnlineAudioPlayerContract.View {

    private static final String TAG = OnlineAudioListFragment.class.getSimpleName();
    private static final int UPDATE_PLAY_STATUS = 1;

    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    RecyclerView rvEventList;
    ImageView mConfirm;

    private LinearLayoutManager mLayoutManager = null;
    public OnlineAudioListRecyclerAdapter mAdapter;
    public static List<AudioContentInfo> mPlayContentInfoDatas =  new ArrayList<>();
    private boolean isStartPlayProcess = true;//是否开启播放流程
    private static String currentAlbumId = "";
    private static String mAlbumName="";
    private int currentPlaySeq = -1;
    private boolean isFirstPlay = true;
    private View mView;
    private static OnlineAudioResourcesHelper mHelper;
    private boolean isDialogHidden=false;
    private ImageView ivPlayStatus;
    private ImageView ivPlayNone;
    private ImageView ivMusicLast;
    private ImageView ivMusicPlay;
    private ImageView ivMusicStop;
    private ImageView ivMusicNext;
    private ImageView ivRecycleButton;
    private boolean isPause = false;
    public static int SINGLE_AUDIO_PLAYING=1;
    public static int RECYCLE_AUDIO_LIST_PLAYING=2;
    public static int ORDER_AUDIO_LIST_PLAYING=3;
    private int  isRecycleType=ORDER_AUDIO_LIST_PLAYING;
    private AnimationDrawable playStatusAnim = null;
    private TextView tvPlayName;
    private static final int UPDATE_CURRENT_PLAY = 2;
    private static final int STOP_CURRENT_PLAY = 3;
    private AudioContentInfo currentPlayInfo = null;
    private String playStatus = "";//流程开启后的播放状态

    public static OnlineAudioListFragment newInstance(AlbumContentInfo mAlbum) {
        OnlineAudioListFragment mOnlineAudioEventListFragment=new OnlineAudioListFragment();
        currentAlbumId=mAlbum.albumId;
        mAlbumName=mAlbum.albumName;
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


    @Subscribe
    public void onEventPlayerEvent(PlayerEvent event) {
        UbtLog.d(TAG,"event = " + event);
       if(event.getEvent() == PlayerEvent.Event.READ_EVENT_PLAY_STATUS){
            UbtLog.d(TAG,"EventPlayStatus = " + event.getEvent());
            Message msg = new Message();
            msg.what = UPDATE_PLAY_STATUS;
            msg.arg1=event.getCurrentPlayingIndex();
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 移动到某个下标
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
        ButterKnife.bind(getActivity());
        EventBus.getDefault().register(this);
        mHelper=OnlineAudioResourcesHelper.getInstance(getContext());
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvEventList=(RecyclerView)mView.findViewById(R.id.rv_event_list);
        mConfirm=(ImageView)mView.findViewById(R.id.iv_back);
        tvBaseTitleName=(TextView)mView.findViewById(R.id.tv_base_title_name);
        ivPlayNone = mView.findViewById(R.id.iv_play_status_none);
        ivMusicLast = mView.findViewById(R.id.iv_music_prev);
        ivMusicPlay = mView.findViewById(R.id.iv_music_play);
        ivMusicStop = mView.findViewById(R.id.iv_music_stop);
        ivMusicNext = mView.findViewById(R.id.iv_music_next);
        ivRecycleButton=mView.findViewById(R.id.iv_music_circle);
        ivPlayStatus = mView.findViewById(R.id.iv_play_status);
        tvPlayName = mView.findViewById(R.id.tv_play_name);
        playStatusAnim = (AnimationDrawable)ivPlayStatus.getBackground();
        playStatusAnim.setOneShot(false);
        playStatusAnim.setVisible(true,true);
        tvBaseTitleName.setText(mAlbumName);
        rvEventList.setLayoutManager(mLayoutManager);
        mAdapter = new OnlineAudioListRecyclerAdapter(getContext(),mPlayContentInfoDatas, mHandler);
        rvEventList.setAdapter(mAdapter);
        mConfirm.setVisibility(View.VISIBLE);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // mHelper.updatePlayContentInfoList();
                EventBus.getDefault().unregister(this);
                pop();
            }
        });
        mPresenter.getAudioList(currentAlbumId);
        return mView;
    }

    @OnClick({R.id.ll_base_back, R.id.tv_base_title_name,R.id.iv_music_prev,R.id.iv_music_play,R.id.iv_music_next,R.id.iv_music_circle
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                pop();
                break;
            case R.id.tv_base_title_name:
                break;
            case R.id.iv_music_prev:
                prevAudioPlay();
                break;
            case R.id.iv_music_play:
                if(isStartPlayProcess) {
                    onlineAudioPlayer();
                }
                break;
            case R.id.iv_music_stop:
                UbtLog.d(TAG,"doStop isStartPlayProcess = " + isStartPlayProcess + "  currentPlaySeq =" + currentPlaySeq );
                if(isStartPlayProcess && currentPlaySeq >= 0){
                    mHelper.stopEvent();
                    isPause = false;
                    mHandler.sendEmptyMessage(STOP_CURRENT_PLAY);
                }
                break;
            case R.id.iv_music_next:
                if(isStartPlayProcess){
                    nextAudioPlay();
                }
                break;
            case R.id.iv_music_circle:
                if(isRecycleType==ORDER_AUDIO_LIST_PLAYING) {
                    isRecycleType=RECYCLE_AUDIO_LIST_PLAYING;
                    mHelper.setPlayType(RECYCLE_AUDIO_LIST_PLAYING);
                    ivRecycleButton.setImageResource(R.drawable.ic_circle_list);
                    Toast.makeText(getActivity(),"RECYCLE", Toast.LENGTH_LONG).show();
                }else if(isRecycleType==RECYCLE_AUDIO_LIST_PLAYING){
                    isRecycleType=SINGLE_AUDIO_PLAYING;
                    mHelper.setPlayType(SINGLE_AUDIO_PLAYING);
                    ivRecycleButton.setImageResource(R.drawable.ic_circle_single);
                    Toast.makeText(getActivity(),"SINGLE", Toast.LENGTH_LONG).show();
                }else if(isRecycleType==SINGLE_AUDIO_PLAYING){
                    isRecycleType=ORDER_AUDIO_LIST_PLAYING;
                    mHelper.setPlayType(ORDER_AUDIO_LIST_PLAYING);
                    ivRecycleButton.setImageResource(R.drawable.ic_circle_list);
                    Toast.makeText(getActivity(),"ORDER", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
    private void prevAudioPlay() {
        if(isStartPlayProcess){
            isPause = false;
            mHelper.prevAudioPlay();
            ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
            mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
        }
    }

    private void nextAudioPlay() {
        isPause = false;
        mHelper.nextAudioPlay();
        ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
        mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
    }
    private void autoNextAudioPlay() {
        isPause = false;
        mHelper.autoNextAudioPlay();
        ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
        mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
    }


    private void onlineAudioPlayer() {
        if("暂无播放内容".equals(tvPlayName.getText().toString())){
            mHelper.autoAudioPlay();
            isPause = false;
            ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
            mHandler.sendEmptyMessage(UPDATE_CURRENT_PLAY);
        }else {
            if(isPause){
                isPause = false;
                mHelper.continueEvent(mHelper.getPlayContent().get(currentPlaySeq).contentUrl);
                ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
                playStatusAnim.start();
            }else {
                isPause = true;
                mHelper.pauseEvent(mHelper.getPlayContent().get(currentPlaySeq).contentUrl);
                ivMusicPlay.setImageResource(R.drawable.ic_ct_play_usable);
                playStatusAnim.stop();
            }
        }
    }


    @Override
    public void showCourseList(List<CourseContentInfo> album) {

    }


    @Override
    public void showAlbumList(Boolean status, List<AlbumContentInfo> album, String errorMsgs) {

    }

    @Override
    public void showAudioList(Boolean status, List<AudioContentInfo> album, String errorMsgs) {
        mPlayContentInfoDatas.clear();
        mPlayContentInfoDatas.addAll(album);
        mHelper.setPlayContent(album);
        mAdapter.notifyDataSetChanged();
        initState();
        onlineAudioPlayer();

    }

    @Override
    public void onRequestStatus(int requestType, int errorCode) {

    }




    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_PLAY_STATUS:
                    mAdapter.notifyDataSetChanged();
                    moveToPosition(msg.arg1);
                    break;
                case UPDATE_CURRENT_PLAY:
                    if(currentPlayInfo == null || currentPlaySeq == -1){
                        if(mHelper.getPlayContent() != null && mHelper.getPlayContent().size() > 0){
                            currentPlayInfo = mHelper.getPlayContent().get(0);
                            currentPlaySeq = 0;
                        }else {
                            return;
                        }
                    }
                    String playContent = "正在播放：" + mHelper.getPlayContent().get(mHelper.getCurrentPlayingAudioIndex()).contentName /*+ "_" + currentPlaySeq*/;
                    SpannableString style = new SpannableString(playContent);
                    style.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.T32)),0, "正在播放：".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvPlayName.setText(style);

                    ivPlayNone.setVisibility(View.GONE);
                    ivPlayStatus.setVisibility(View.VISIBLE);
                    playStatusAnim.start();
                    break;
                case STOP_CURRENT_PLAY:
                    //currentPlaySeq = -1;
                    //currentPlayInfo = null;
                    tvPlayName.setText("暂无播放内容");
                    ivMusicPlay.setImageResource(R.drawable.ic_ct_play_usable);
                    ivPlayNone.setVisibility(View.VISIBLE);
                    ivPlayStatus.setVisibility(View.GONE);
                    playStatusAnim.stop();
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 初始化机器人状态
     */
    private void initState(){
        UbtLog.d(TAG,"initRobotState mCurrentVolume = " + mHelper.mCurrentVolume + "   mCurrentVoiceState " + mHelper.mCurrentVoiceState + "   mLightState = " + mHelper.mLightState);
        if(isStartPlayProcess){
            if("playing".equals(playStatus)){
                ivPlayNone.setVisibility(View.GONE);
                ivPlayStatus.setVisibility(View.VISIBLE);
                ivMusicPlay.setImageResource(R.drawable.ic_ct_pause);
                playStatusAnim.start();

            }else if("pause".equals(playStatus)){
                ivPlayNone.setVisibility(View.GONE);
                ivPlayStatus.setVisibility(View.VISIBLE);
                ivMusicPlay.setImageResource(R.drawable.ic_ct_play_usable);
                playStatusAnim.stop();
            } else {
                //noPlay
                ivPlayNone.setVisibility(View.VISIBLE);
                ivPlayStatus.setVisibility(View.GONE);
                ivMusicPlay.setImageResource(R.drawable.ic_ct_play_usable);
                playStatusAnim.stop();
            }
            ivMusicLast.setImageResource(R.drawable.ic_music_last_usable);
            ivMusicStop.setImageResource(R.drawable.ic_ct_stop);
            ivMusicNext.setImageResource(R.drawable.ic_music_next_usable);
        }else {
            ivPlayNone.setVisibility(View.VISIBLE);
            ivPlayStatus.setVisibility(View.GONE);
            playStatusAnim.stop();

            ivMusicLast.setImageResource(R.drawable.ic_music_last_disable);
            ivMusicPlay.setImageResource(R.drawable.ic_ct_play_disable);
            ivMusicStop.setImageResource(R.drawable.ic_ct_stop_disable);
            ivMusicNext.setImageResource(R.drawable.ic_music_next_disable);
        }
    }

}
