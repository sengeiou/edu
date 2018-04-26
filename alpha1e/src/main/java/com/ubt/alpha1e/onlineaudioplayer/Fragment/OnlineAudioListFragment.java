package com.ubt.alpha1e.onlineaudioplayer.Fragment;


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
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.onlineaudioplayer.categoryActivity.OnlineAudioPlayerContract;
import com.ubt.alpha1e.onlineaudioplayer.categoryActivity.OnlineAudioPlayerPresenter;
import com.ubt.alpha1e.onlineaudioplayer.adapter.OnlineAudioListRecyclerAdapter;
import com.ubt.alpha1e.onlineaudioplayer.helper.OnlineAudioResourcesHelper;
import com.ubt.alpha1e.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.CourseContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.PlayerEvent;
import com.ubt.alpha1e.onlineaudioplayer.playerDialog.OnlineAudioPlayDialog;
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
    OnlineAudioPlayDialog mPlayDialogOnlineAudioPlayDialog;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_PLAY_STATUS:
                    mAdapter.notifyDataSetChanged();
                    moveToPosition(msg.arg1);
//                    OnlinePlayStatus eventPlayStatus = (OnlinePlayStatus) msg.obj;
//                    if(eventPlayStatus != null && mPlayContentInfoDatas != null){
//                        if(StringUtils.isInteger(eventPlayStatus.playAudioSeq)){
//                            int seqNo = Integer.parseInt(eventPlayStatus.playAudioSeq);
//                            currentPlaySeq = seqNo;
//                            if(currentAlbumId.equals(eventPlayStatus.eventId) && "1".equals(eventPlayStatus.eventState) && seqNo >= 0){
//                                isStartPlayProcess = true;
//                                if("playing".equals(eventPlayStatus.audioState) || "pause".equals(eventPlayStatus.audioState)){
//                                    if(mPlayContentInfoDatas != null && seqNo < mPlayContentInfoDatas.size()){
//                                        for(AudioContentInfo mPlayContentInfo : mPlayContentInfoDatas){
//                                            mPlayContentInfo.isPlaying = false;
//                                        }
//                                        if("playing".equals(eventPlayStatus.audioState)){
//                                            mPlayContentInfoDatas.get(seqNo).isPlaying =true;
//                                        }
//                                        mAdapter.notifyDataSetChanged();
//                                        moveToPosition(seqNo);
//                                    }
//                                }else {
//                                    if(mPlayContentInfoDatas != null){
//                                        for(AudioContentInfo mPlayContentInfo : mPlayContentInfoDatas){
//                                            mPlayContentInfo.isPlaying=false;
//                                        }
//                                        mAdapter.notifyDataSetChanged();
//                                    }
//                                }
//                            }else {
//                                isStartPlayProcess = false;
//                                if(mPlayContentInfoDatas != null){
//                                    for(AudioContentInfo mPlayContentInfo : mPlayContentInfoDatas){
//                                        mPlayContentInfo.isPlaying = false;
//                                    }
//                                    mAdapter.notifyDataSetChanged();
//                                }
//                            }
//                        }
//                    }
                    break;

                default:
                    break;
            }
        }
    };

    public static OnlineAudioListFragment newInstance(AlbumContentInfo mAlbum) {
        OnlineAudioListFragment mOnlineAudioEventListFragment=new OnlineAudioListFragment();
        currentAlbumId=mAlbum.albumId;
        mAlbumName=mAlbum.albumName;
        return mOnlineAudioEventListFragment;
    }

    @Override
    protected void initUI() {
       // tvBaseTitleName.setText(getContext().getStringResources("ui_onlineaudio_event_list"));

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
        }else if(event.getEvent()==PlayerEvent.Event.CONTROL_PLAYER_SHOW){
           showPlayEventDialog(mHelper.getPlayContent(),event.getCurrentClickingIndex());
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

    @OnClick({R.id.ll_base_back, R.id.tv_base_title_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                pop();
                break;
            case R.id.tv_base_title_name:
                break;
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
        showPlayEventDialog(album,0);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestStatus(int requestType, int errorCode) {

    }

    /**
     * 选择播放事项
     */
    private void showPlayEventDialog(List<AudioContentInfo> playContentInfoList, int index){
        if(mPlayDialogOnlineAudioPlayDialog == null){
            mPlayDialogOnlineAudioPlayDialog= new OnlineAudioPlayDialog (getActivity())
                    .builder()
                    .setCancelable(true)
                    .setPlayContent(playContentInfoList)
                    .setCurrentAlbumId("")
                    .setCallbackListener(new OnlineAudioPlayDialog.IHibitsEventPlayListener() {
                        @Override
                        public void onDismissCallback() {
                            UbtLog.d(TAG,"onDismissCallback");
                            mPlayDialogOnlineAudioPlayDialog.hidden();
                        }
                    });
            mPlayDialogOnlineAudioPlayDialog.startPlay();

        }else {
            if (index != mHelper.getCurrentPlayingAudioIndex()) {
                mHelper.setCurentPlayingAudioIndex(index);
                mPlayDialogOnlineAudioPlayDialog.startPlay();
            }
        }
        mPlayDialogOnlineAudioPlayDialog.show();
    }
}
