package com.ubt.alpha1e.behaviorhabits.playeventlist;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.adapter.EventListRecyclerAdapter;
import com.ubt.alpha1e.behaviorhabits.event.HibitsEvent;
import com.ubt.alpha1e.behaviorhabits.helper.HabitsHelper;
import com.ubt.alpha1e.behaviorhabits.model.EventPlayStatus;
import com.ubt.alpha1e.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PlayEventListActivity extends MVPBaseActivity<PlayEventListContract.View, PlayEventListPresenter> implements PlayEventListContract.View {

    private static final String TAG = PlayEventListActivity.class.getSimpleName();
    public static final int DO_PLAY_OR_PAUSE = 1;
    private static final int UPDATE_PLAY_STATUS = 2;

    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.rv_event_list)
    RecyclerView rvEventList;

    public EventListRecyclerAdapter mAdapter;
    private List<PlayContentInfo> mPlayContentInfoDatas = null;
    private String currentEventId = "";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DO_PLAY_OR_PAUSE:
                    int position = msg.arg1;
                    PlayContentInfo playContentInfo = mPlayContentInfoDatas.get(position);

                    if("1".equals(playContentInfo.isSelect)){
                        playContentInfo.isSelect = "0";
                        mAdapter.notifyItemChanged(position);
                        ((HabitsHelper)mHelper).playEventSound(currentEventId, position + "","pause");
                    }else {
                        for(PlayContentInfo mPlayContentInfo : mPlayContentInfoDatas){
                            mPlayContentInfo.isSelect = "0";
                        }
                        playContentInfo.isSelect = "1";
                        mAdapter.notifyDataSetChanged();
                        ((HabitsHelper)mHelper).playEventSound(currentEventId, position + "","start");
                    }
                    break;
                case UPDATE_PLAY_STATUS:
                    EventPlayStatus eventPlayStatus = (EventPlayStatus) msg.obj;
                    if(eventPlayStatus != null && mPlayContentInfoDatas != null){
                        if("playing".equals(eventPlayStatus.audioState) && currentEventId.equals(eventPlayStatus.eventId)){
                            if(isStringNumber(eventPlayStatus.playAudioSeq)){
                                int seqNo = Integer.parseInt(eventPlayStatus.playAudioSeq);
                                if(mPlayContentInfoDatas != null && seqNo < mPlayContentInfoDatas.size()){
                                    for(PlayContentInfo mPlayContentInfo : mPlayContentInfoDatas){
                                        mPlayContentInfo.isSelect = "0";
                                    }
                                    mPlayContentInfoDatas.get(seqNo).isSelect = "1";
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

    public static void launchActivity(Activity mActivity, List<PlayContentInfo> playContentInfoDatas,String eventId) {
        ArrayList<Parcelable> playContentList = new ArrayList<>();
        if(playContentInfoDatas != null){
            for(PlayContentInfo playContentInfo : playContentInfoDatas){
                playContentList.add(PG.convertParcelable(playContentInfo));
            }
        }

        Intent intent = new Intent(mActivity, PlayEventListActivity.class);
        intent.putParcelableArrayListExtra(Constant.PLAY_CONTENT_INFO_LIST_KEY, playContentList);
        intent.putExtra(Constant.HIBITS_EVENT_ID, eventId);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    @Override
    protected void initUI() {
        tvBaseTitleName.setText(getStringResources("ui_habits_event_list"));

        UbtLog.d(TAG, "rvHabitsEvent => " + rvEventList);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvEventList.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator animator = rvEventList.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        mAdapter = new EventListRecyclerAdapter(getContext(), mPlayContentInfoDatas, mHandler);
        rvEventList.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((HabitsHelper)mHelper).readPlayStatus();
    }

    @Subscribe
    public void onEventHibits(HibitsEvent event) {
        UbtLog.d(TAG,"event = " + event);
        if(event.getEvent() == HibitsEvent.Event.CONTROL_PLAY){
            UbtLog.d(TAG,"event = " + event.getStatus());
        }else if(event.getEvent() == HibitsEvent.Event.READ_EVENT_PLAY_STATUS){
            UbtLog.d(TAG,"EventPlayStatus = " + event.getEventPlayStatus());
            EventPlayStatus eventPlayStatus = event.getEventPlayStatus();
            Message msg = new Message();
            msg.what = UPDATE_PLAY_STATUS;
            msg.obj = eventPlayStatus;
            mHandler.sendMessage(msg);
        }
    }

    /***
     * 判断字符串是否都是数字
     */
    public  boolean isStringNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_play_event_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

        mPlayContentInfoDatas = new ArrayList<>();
        if(getIntent() != null){
            ArrayList<? extends PlayContentInfo> playContentInfoList = getIntent().getParcelableArrayListExtra(Constant.PLAY_CONTENT_INFO_LIST_KEY);
            if(playContentInfoList != null){
                mPlayContentInfoDatas.addAll(playContentInfoList);
            }
            currentEventId = getIntent().getStringExtra(Constant.HIBITS_EVENT_ID);
        }
        mHelper = new HabitsHelper(this);
        initUI();
    }

    @OnClick({R.id.ll_base_back, R.id.tv_base_title_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                PlayEventListActivity.this.finish();
                break;
            case R.id.tv_base_title_name:
                break;
        }
    }
}
