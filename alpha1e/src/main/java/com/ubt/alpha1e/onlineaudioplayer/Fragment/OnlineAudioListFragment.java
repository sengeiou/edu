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
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.behaviorhabits.event.HibitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.EventPlayStatus;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.onlineaudioplayer.categoryActivity.OnlineAudioPlayerContract;
import com.ubt.alpha1e.onlineaudioplayer.categoryActivity.OnlineAudioPlayerPresenter;
import com.ubt.alpha1e.onlineaudioplayer.adapter.OnlineAudioListRecyclerAdapter;
import com.ubt.alpha1e.onlineaudioplayer.helper.OnlineAudioResourcesHelper;
import com.ubt.alpha1e.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.CourseContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.playerDialog.OnlineAudioPlayDialog;
import com.ubt.alpha1e.ui.dialog.HibitsEventPlayDialog;
import com.ubt.alpha1e.utils.StringUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

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
    public static final int DO_PLAY_OR_PAUSE = 1;
    private static final int UPDATE_PLAY_STATUS = 2;
    public static final int SELECT_ADD=3;
    public static final int DESELECT_DELETE=4;

    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    RecyclerView rvEventList;
    ImageView mConfirm;

    private LinearLayoutManager mLayoutManager = null;
    public OnlineAudioListRecyclerAdapter mAdapter;
    public static List<AudioContentInfo> mPlayContentInfoDatas =  new ArrayList<>();
    private boolean isStartPlayProcess = true;//是否开启播放流程
    private static String currentAlbumId = "";
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
                case SELECT_ADD:
                    UbtLog.d(TAG,"ADD");
                    OnlineAudioListFragment.mPlayContentInfoDatas.get(msg.arg1).isSelect=true;
                    break;
                case DESELECT_DELETE:
                    UbtLog.d(TAG,"DELETE");
                    OnlineAudioListFragment.mPlayContentInfoDatas.get(msg.arg1).isSelect=false;
                    break;
                case DO_PLAY_OR_PAUSE:
                    if(isStartPlayProcess){
                        int position = msg.arg1;
                        AudioContentInfo playContentInfo = mPlayContentInfoDatas.get(position);

//                        if("1".equals(playContentInfo.isSelect)){
//                            playContentInfo.isSelect = "0";
//                            mAdapter.notifyItemChanged(position);
//                            ((HabitsHelper)mHelper).playEventSound(currentEventId, position + "","pause");
//                        }else {
//                            for(PlayContentInfo mPlayContentInfo : mPlayContentInfoDatas){
//                                mPlayContentInfo.isSelect = "0";
//                            }
//
//                            playContentInfo.isSelect = "1";
//                            mAdapter.notifyDataSetChanged();
//
//                            if(currentPlaySeq == position){
//                                ((HabitsHelper)mHelper).playEventSound(currentEventId, position + "","unpause");
//                            }else {
//                                ((HabitsHelper)mHelper).playEventSound(currentEventId, position + "","start");
//                            }
//                            currentPlaySeq = position;
//                        }
                    }else {
                        ToastUtils.showShort("本事项播放提醒流程尚未开始！");
                    }
                    break;
                case UPDATE_PLAY_STATUS:
                    EventPlayStatus eventPlayStatus = (EventPlayStatus) msg.obj;
                    if(eventPlayStatus != null && mPlayContentInfoDatas != null){
                        if(StringUtils.isInteger(eventPlayStatus.playAudioSeq)){
                            int seqNo = Integer.parseInt(eventPlayStatus.playAudioSeq);
                            currentPlaySeq = seqNo;
                            if(currentAlbumId.equals(eventPlayStatus.eventId) && "1".equals(eventPlayStatus.eventState) && seqNo >= 0){
                                isStartPlayProcess = true;
                                if("playing".equals(eventPlayStatus.audioState) || "pause".equals(eventPlayStatus.audioState)){
                                    if(mPlayContentInfoDatas != null && seqNo < mPlayContentInfoDatas.size()){
//                                        for(PlayContentInfo mPlayContentInfo : mPlayContentInfoDatas){
//                                            mPlayContentInfo.isSelect = "0";
//                                        }
//
//                                        if("playing".equals(eventPlayStatus.audioState)){
//                                            mPlayContentInfoDatas.get(seqNo).isSelect = "1";
//                                        }
                                        mAdapter.notifyDataSetChanged();
                                        moveToPosition(seqNo);
                                    }
                                }else {
                                    if(mPlayContentInfoDatas != null){
//                                        for(PlayContentInfo mPlayContentInfo : mPlayContentInfoDatas){
//                                            mPlayContentInfo.isSelect = "0";
//                                        }
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            }else {
                                isStartPlayProcess = false;
                                if(mPlayContentInfoDatas != null){
//                                    for(PlayContentInfo mPlayContentInfo : mPlayContentInfoDatas){
//                                        mPlayContentInfo.isSelect = "0";
//                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };

    public static OnlineAudioListFragment newInstance(String mAlbumId) {
        OnlineAudioListFragment mOnlineAudioEventListFragment=new OnlineAudioListFragment();
        currentAlbumId=mAlbumId;
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
    public void onEventHibits(HibitsEvent event) {
        UbtLog.d(TAG,"event = " + event);
//        if(event.getEvent() == HibitsEvent.Event.CONTROL_PLAY){
//            UbtLog.d(TAG,"event = " + event.getStatus());
//        }else if(event.getEvent() == HibitsEvent.Event.READ_EVENT_PLAY_STATUS){
//            UbtLog.d(TAG,"EventPlayStatus = " + event.getEventPlayStatus());
//            EventPlayStatus eventPlayStatus = event.getEventPlayStatus();
//            Message msg = new Message();
//            msg.what = UPDATE_PLAY_STATUS;
//            msg.obj = eventPlayStatus;
//            mHandler.sendMessage(msg);
//        }
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
        mHelper=OnlineAudioResourcesHelper.getInstance(getContext());
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvEventList=(RecyclerView)mView.findViewById(R.id.rv_event_list);
        mConfirm=(ImageView)mView.findViewById(R.id.iv_back);
        rvEventList.setLayoutManager(mLayoutManager);
        mAdapter = new OnlineAudioListRecyclerAdapter(getContext(),mPlayContentInfoDatas, mHandler);
        rvEventList.setAdapter(mAdapter);

        mConfirm.setVisibility(View.VISIBLE);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelper.updatePlayContentInfoList();
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
                HibitsEventPlayDialog.refreshStatus();
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
        showPlayEventDialog(album,"");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestStatus(int requestType, int errorCode) {

    }

    /**
     * 选择播放事项
     */
    private void showPlayEventDialog(List<AudioContentInfo> playContentInfoList, String albumId){
        if(mPlayDialogOnlineAudioPlayDialog == null){
            mPlayDialogOnlineAudioPlayDialog= new OnlineAudioPlayDialog (getActivity())
                    .builder()
                    .setCancelable(true)
                    .setPlayContent(playContentInfoList)
                    .setCurrentAlbumId(albumId)
                    .setCallbackListener(new OnlineAudioPlayDialog.IHibitsEventPlayListener() {
                        @Override
                        public void onDismissCallback() {
                            UbtLog.d(TAG,"onDismissCallback");
                            mPlayDialogOnlineAudioPlayDialog.hidden();
                        }
                    });
        }else {
            if (albumId != mPlayDialogOnlineAudioPlayDialog.getCurrentAlbumId()) {
                mPlayDialogOnlineAudioPlayDialog.setCurrentAlbumId(albumId);
                mPlayDialogOnlineAudioPlayDialog.setPlayContent(playContentInfoList);
                mPlayDialogOnlineAudioPlayDialog.recoveryPlayerUi();
            }
        }
        mPlayDialogOnlineAudioPlayDialog.startPlay();
        mPlayDialogOnlineAudioPlayDialog.show();
    }
}