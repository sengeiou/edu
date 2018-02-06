package com.ubt.alpha1e.behaviorhabits.fragment;


import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsPresenter;
import com.ubt.alpha1e.behaviorhabits.FlowLayoutManager;
import com.ubt.alpha1e.behaviorhabits.adapter.FlowPlayContentRecyclerAdapter;
import com.ubt.alpha1e.behaviorhabits.adapter.SampleAdapter;
import com.ubt.alpha1e.behaviorhabits.drag.DragRecyclerView;
import com.ubt.alpha1e.behaviorhabits.drag.HoldTouchHelper;
import com.ubt.alpha1e.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.behaviorhabits.model.SampleEntity;
import com.ubt.alpha1e.behaviorhabits.model.UserScore;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.dialog.SLoadingDialog;
import com.ubt.alpha1e.utils.DateUtils;
import com.ubt.alpha1e.utils.StringUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class HibitsEventEditFragment extends MVPBaseFragment<BehaviorHabitsContract.View, BehaviorHabitsPresenter> implements BehaviorHabitsContract.View {

    private static final String TAG = HibitsEventEditFragment.class.getSimpleName();

    private static final int UPDATE_UI_DATA = 1;
    private static final int NETWORK_EXCEPTION = 2;
    private static final int SAVE_SUCCESS = 3;

    Unbinder unbinder;
    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.lv_hour)
    LoopView lvHour;
    @BindView(R.id.lv_minute)
    LoopView lvMinute;
    @BindView(R.id.tv_alert_one)
    TextView tvAlertOne;
    @BindView(R.id.tv_alert_two)
    TextView tvAlertTwo;
    @BindView(R.id.tv_alert_one_title)
    TextView tvAlertOneTitle;
    @BindView(R.id.tv_alert_two_title)
    TextView tvAlertTwoTitle;
    @BindView(R.id.rl_alert_one)
    RelativeLayout rlAlertOne;
    @BindView(R.id.rl_alert_two)
    RelativeLayout rlAlertTwo;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rv_play_content)
    DragRecyclerView rvPlayContent;


    private String[] mHourArr = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
                                "10", "11","12","13","14","15","16","17","18","19",
                                "20", "21","22","23"};
    private String[] mMinuteArr = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
            "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};

    private String[] mAlertArr = {"5", "10", "15", "20"};

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    private HabitsEvent mHabitsEvent = null;

    public FlowPlayContentRecyclerAdapter mAdapter;
    private List<SampleEntity> mPlayContentInfoDatas = new ArrayList<>();
    private int mRemindFirstIndex = 0;
    private int mRemindSecondIndex = 0;
    private EventDetail<List<PlayContentInfo>> originEventDetail = null;
    private EventDetail<List<PlayContentInfo>> newEventDetail = null;
    private FlowLayoutManager flowLayoutManager = null;

    private ArrayList<? extends HabitsEvent> mHabitsEventInfoDatas = null;
    private int mWorkdayMode = 1;

    protected Dialog mCoonLoadingDia;

    public static HibitsEventEditFragment newInstance(List<HabitsEvent> habitsEventList, int editIndex,int dayType) {
        ArrayList<Parcelable> mHabitsEventList = new ArrayList<>();
        for(HabitsEvent mHabitsEvent : habitsEventList){
            mHabitsEventList.add(PG.convertParcelable(mHabitsEvent));
        }

        HibitsEventEditFragment eventEditFragment = new HibitsEventEditFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constant.HABITS_EVENT_INFO_LIST_KEY, mHabitsEventList);
        bundle.putInt(Constant.INDEX_KEY, editIndex);
        bundle.putInt(Constant.PLAY_HIBITS_EVENT_WORKDAY_TYPE, dayType);
        eventEditFragment.setArguments(bundle);
        return eventEditFragment;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_UI_DATA:
                    mCoonLoadingDia.cancel();
                    originEventDetail = (EventDetail<List<PlayContentInfo>>) msg.obj;
                    UbtLog.d(TAG,"originEventDetail = " + originEventDetail.contentIds + "   = " + originEventDetail.contents);
                    newEventDetail = EventDetail.cloneNewInstance(originEventDetail);
                    UbtLog.d(TAG,"originEventDetail = " + originEventDetail + "  mHabitsEvent.eventType = " + mHabitsEvent.eventType);
                    if(originEventDetail != null){
                        String[] eventTime = originEventDetail.eventTime.split(":");
                        if(eventTime.length == 2){
                            lvHour.setInitPosition(0);
                            lvHour.setCurrentPosition(getHourIndex(eventTime[0]));

                            lvMinute.setInitPosition(0);
                            lvMinute.setCurrentPosition(getMinuteIndex(eventTime[1]));
                        }

                        if("2".equals(mHabitsEvent.eventType) || "3".equals(mHabitsEvent.eventType)){
                            //2：午休 3：睡晚觉 没有第一次第二次提醒
                            tvAlertOne.setText("");
                            tvAlertTwo.setText("");
                            tvAlertOneTitle.setTextColor(getResources().getColor(R.color.T28));
                            tvAlertTwoTitle.setTextColor(getResources().getColor(R.color.T28));
                            rlAlertOne.setEnabled(false);
                            rlAlertTwo.setEnabled(false);
                        }else {
                            //mRemindFirstIndex = getAlertIndex(originEventDetail.remindFirst);
                            //mRemindSecondIndex = getAlertIndex(originEventDetail.remindSecond);

                            tvAlertOne.setText(originEventDetail.remindFirst + getStringRes("ui_habits_minute_later"));
                            tvAlertTwo.setText(originEventDetail.remindSecond + getStringRes("ui_habits_minute_later"));
                        }
                        updatePlayContentData(originEventDetail.contents);
                    }
                    break;
                case NETWORK_EXCEPTION:
                    if(mCoonLoadingDia.isShowing()){
                        mCoonLoadingDia.cancel();
                    }
                    ToastUtils.showShort(getStringRes("ui_common_network_request_failed"));
                    break;
                case SAVE_SUCCESS:
                    mCoonLoadingDia.cancel();
                    mHabitsEvent.eventTime = newEventDetail.eventTime;

                    Bundle resultBundle = new Bundle();
                    resultBundle.putParcelable(Constant.HABITS_EVENT_INFO_KEY, PG.convertParcelable(mHabitsEvent));
                    setFragmentResult(Constant.HIBITS_EVENT_EDIT_RESPONSE_CODE, resultBundle);
                    pop();
                    break;
            }
        }
    };

    private int getHourIndex(String hour){
        int index = 0;
        for(String h : mHourArr){
            if(StringUtils.isStringNumber(hour) && Integer.parseInt(h) == Integer.parseInt(hour) ){
                return index;
            }
            index++;
        }
        return 0;
    }

    private int getMinuteIndex(String minute){
        int index = 0;
        for(String m : mMinuteArr){
            if(StringUtils.isStringNumber(minute) && Integer.parseInt(m) == Integer.parseInt(minute) ){
                return index;
            }
            index++;
        }
        return 0;
    }

    private int getAlertIndex(String alertTime){
        int index = 0;
        for(String a : mAlertArr){
            if(StringUtils.isStringNumber(alertTime) && Integer.parseInt(a) == Integer.parseInt(alertTime) ){
                return index;
            }
            index++;
        }
        return 0;
    }

    @Override
    protected void initUI() {
        mCoonLoadingDia = SLoadingDialog.getInstance(getContext());

        ivBack.setBackgroundResource(R.drawable.action_close);
        if(mHabitsEvent != null){
            tvBaseTitleName.setText(getStringRes("ui_common_edit") + mHabitsEvent.eventName);
        }else {
            tvBaseTitleName.setText(getStringRes("ui_common_edit"));
        }

        ivTitleRight.setVisibility(View.VISIBLE);

        // 设置原始数据
        lvHour.setCenterTextColor(getResources().getColor(R.color.tv_center_color));
        lvHour.setItems(Arrays.asList(mHourArr));
        lvHour.setInitPosition(0);
        lvHour.setCurrentPosition(2);

        lvHour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    UbtLog.d(TAG,"hour select start ...");
                }
                return false;
            }
        });

        lvHour.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                UbtLog.d(TAG,"index = " + index);
                UbtLog.d(TAG,"hour select end ...");
            }
        });

        // 设置原始数据
        lvMinute.setCenterTextColor(getResources().getColor(R.color.tv_center_color));
        lvMinute.setItems(Arrays.asList(mMinuteArr));
        lvMinute.setInitPosition(0);
        lvMinute.setCurrentPosition(10);

        initRecyclerViews();

        if(mHabitsEvent != null){
            mCoonLoadingDia.show();
            mPresenter.getBehaviourEvent(mHabitsEvent.eventId);
        }
    }

    public void initRecyclerViews() {
        flowLayoutManager = new FlowLayoutManager();
        rvPlayContent.setLayoutManager(flowLayoutManager);
        RecyclerView.ItemAnimator animator = rvPlayContent.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        rvPlayContent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 30;
                outRect.bottom = 30;
            }
        });

        mAdapter = new FlowPlayContentRecyclerAdapter(getContext(), mPlayContentInfoDatas, mHandler);
        rvPlayContent.setAdapter(mAdapter);

        /** custom setting */
        rvPlayContent
                .dragEnable(true)
                .showDragAnimation(true)
                .setDragAdapter(mAdapter)
                .bindEvent(onItemTouchEvent);
    }

    public void initRecyclerViews1() {

        flowLayoutManager = new FlowLayoutManager();
        rvPlayContent.setLayoutManager(flowLayoutManager);

        mAdapter = new FlowPlayContentRecyclerAdapter(getContext(), mPlayContentInfoDatas, mHandler);
        rvPlayContent.setAdapter(mAdapter);

        /** custom setting */
        rvPlayContent
                .dragEnable(true)
                .showDragAnimation(true)
                .setDragAdapter(mAdapter)
                .bindEvent(onItemTouchEvent);
    }

    HoldTouchHelper.OnItemTouchEvent onItemTouchEvent = new HoldTouchHelper.OnItemTouchEvent() {
        @Override
        public void onLongPress(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int position) {
            UbtLog.e(TAG,"onLongPress position = " + position);
            //暂时屏蔽不给拖
            /*if (((SampleAdapter) recyclerView.getAdapter()).onItemDrag(position)) {
                ((DragRecyclerView) recyclerView).startDrag(position);
            }*/
        }

        @Override
        public void onItemClick(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int position) {
            //String text = mAllDeviceDatas.get(position).getDeviceEntity().getDeviceName();
            //Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_hibits_event_edit;
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        if (getArguments() != null) {
            mHabitsEventInfoDatas = getArguments().getParcelableArrayList(Constant.HABITS_EVENT_INFO_LIST_KEY);
            mHabitsEvent = mHabitsEventInfoDatas.get(getArguments().getInt(Constant.INDEX_KEY,0));
            mWorkdayMode = getArguments().getInt(Constant.PLAY_HIBITS_EVENT_WORKDAY_TYPE,1);
            UbtLog.d(TAG,"mHabitsEventInfoDatas = " + mHabitsEventInfoDatas.size() + "  mHabitsEvent = " + mHabitsEvent);
        }

        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);

        UbtLog.d(TAG, "mHabitsEvent = " + mHabitsEvent);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == Constant.PLAY_CONTENT_SELECT_REQUEST_CODE && resultCode == Constant.PLAY_CONTENT_SELECT_RESPONSE_CODE) {
            ArrayList<? extends PlayContentInfo> playContentInfoList = data.getParcelableArrayList(Constant.PLAY_CONTENT_INFO_LIST_KEY);
            UbtLog.d(TAG, "playContentInfoList = " + playContentInfoList.size());
            initRecyclerViews1();
            updatePlayContentData(playContentInfoList);
        }
    }

    private void updatePlayContentData(List<? extends PlayContentInfo> playContentInfList){
        mPlayContentInfoDatas.clear();

        SampleEntity sampleEntity = null;
        UbtLog.d(TAG,"updatePlayContentData playContentInfList = " + playContentInfList);
        if(playContentInfList != null){
            for(PlayContentInfo playContentInfo : playContentInfList){
                sampleEntity = new SampleEntity();
                sampleEntity.setDragEnable(true);
                sampleEntity.setDropEnable(true);
                sampleEntity.setPlayContentInfo(playContentInfo);
                mPlayContentInfoDatas.add(sampleEntity);
            }

            List<String> contentIds = new ArrayList<>();
            for(int index = 0; index < mPlayContentInfoDatas.size();index++ ){
                UbtLog.d(TAG,"contentId = " + mPlayContentInfoDatas.get(index).getPlayContentInfo().contentId);
                contentIds.add(mPlayContentInfoDatas.get(index).getPlayContentInfo().contentId);
            }
            newEventDetail.contentIds = contentIds;

        }else {
            newEventDetail.contentIds = new ArrayList<>();
        }
        mAdapter.notifyDataSetChanged();
        UbtLog.d(TAG,"newEventDetail => " + newEventDetail.contentIds);
    }

    @Override
    public void onTest(boolean isSuccess) {
        UbtLog.d("Test1Fragment", "isSuccess = " + isSuccess);
    }

    @Override
    public void showBehaviourList(boolean status, UserScore<List<HabitsEvent>> userScore, String errorMsg) {

    }

    @Override
    public void showParentBehaviourList(boolean status, UserScore<List<HabitsEvent>> userScore, String errorMsg) {

    }

    @Override
    public void showBehaviourEventContent(boolean status, EventDetail content, String errorMsg) {

        if(status){
            Message msg = new Message();
            msg.what = UPDATE_UI_DATA;
            msg.obj = content;
            mHandler.sendMessage(msg);
        }else {
            mHandler.sendEmptyMessage(NETWORK_EXCEPTION);
        }
    }

    @Override
    public void showBehaviourPlayContent(boolean status, ArrayList<PlayContentInfo> playList, String errorMsg) {

    }

    @Override
    public void onUserPassword(String password) {

    }


    @Override
    public void onAlertSelectItem(int index, String alertVal, int alertType) {
        if (alertType == 1) {
            mRemindFirstIndex = index;
            tvAlertOne.setText(mAlertArr[mRemindFirstIndex] + getStringRes("ui_habits_minute_later"));
            newEventDetail.remindFirst = mAlertArr[mRemindFirstIndex];
        } else if (alertType == 2) {
            mRemindSecondIndex = index;
            tvAlertTwo.setText(mAlertArr[mRemindSecondIndex] + getStringRes("ui_habits_minute_later"));
            newEventDetail.remindSecond = mAlertArr[mRemindSecondIndex];
        }
    }

    @Override
    public void onRequestStatus(int requestType, int errorCode) {
        if(errorCode == mPresenter.NETWORK_SUCCESS){
            if(requestType == mPresenter.GET_BEHAVIOURSAVEUPDATE_CMD){
                mHandler.sendEmptyMessage(SAVE_SUCCESS);
            }
        }else {
            mHandler.sendEmptyMessage(NETWORK_EXCEPTION);
        }

    }

    @OnClick({R.id.ll_base_back, R.id.iv_title_right, R.id.rl_alert_one, R.id.rl_alert_two, R.id.rl_play_content_tip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                if(isHasEdit()){
                    exitEditConfirm();
                }else {
                    pop();
                }
                break;
            case R.id.iv_title_right:
                saveHibitsEvent();
                break;
            case R.id.rl_alert_one:
                //mPresenter.showAlertDialog(getContext(), mRemindFirstIndex, Arrays.asList(mAlertArr), 1);
                break;
            case R.id.rl_alert_two:
                //mPresenter.showAlertDialog(getContext(), mRemindSecondIndex, Arrays.asList(mAlertArr), 2);
                break;
            case R.id.rl_play_content_tip:
                UbtLog.d(TAG,"newEventDetail = " + newEventDetail.contentIds);
                startForResult(PlayContentSelectFragment.newInstance(newEventDetail), Constant.PLAY_CONTENT_SELECT_REQUEST_CODE);
                break;
        }
    }

    private void saveHibitsEvent(){
        if(originEventDetail != null && newEventDetail != null){
            HabitsEvent overlapEvent = getOverlapEvent();
            if(overlapEvent != null){
                String msg = "与" + overlapEvent.eventName + "事项时间("+overlapEvent.eventTime + "-" + overlapEvent.finishTime + ")冲突，是否继续修改？" ;
                new ConfirmDialog(getContext()).builder()
                        .setMsg(msg)
                        .setCancelable(false)
                        .setPositiveButton(getStringRes("ui_common_continue"), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                doSave();
                            }
                        }).setNegativeButton(getStringRes("ui_common_cancel"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();

            }else {
                doSave();
            }
        }
    }

    private void doSave(){
        if(isHasEdit()){
            List<String> contentIds = new ArrayList<>();
            for(int index = 0; index < mPlayContentInfoDatas.size();index++ ){
                UbtLog.d(TAG,"contentId = " + mPlayContentInfoDatas.get(index).getPlayContentInfo().contentId);
                contentIds.add(mPlayContentInfoDatas.get(index).getPlayContentInfo().contentId);
            }
            newEventDetail.contentIds = contentIds;

            mCoonLoadingDia.show();
            mPresenter.saveBehaviourEvent(newEventDetail, mWorkdayMode);
        }else {
            pop();
        }
    }

    private boolean isHasEdit(){
        if(originEventDetail != null && newEventDetail != null){
            newEventDetail.eventTime = mHourArr[lvHour.getSelectedItem()] + ":" + mMinuteArr[lvMinute.getSelectedItem()];
            if(!originEventDetail.eventTime.equals(newEventDetail.eventTime)){
                return true;
            }
            if(!originEventDetail.remindFirst.equals(newEventDetail.remindFirst)){
                return true;
            }
            if(!originEventDetail.remindSecond.equals(newEventDetail.remindSecond)){
                return true;
            }

            if(originEventDetail.contents == null){
                if(mPlayContentInfoDatas.size() > 0){
                    return true;
                }
            }else {
                if(originEventDetail.contents.size() != (mPlayContentInfoDatas.size())){
                    return true;
                }else {
                    for(int index = 0; index < originEventDetail.contents.size();index++ ){
                        PlayContentInfo originInfo = originEventDetail.contents.get(index);
                        PlayContentInfo newInfo = mPlayContentInfoDatas.get(index).getPlayContentInfo();
                        if(!originInfo.contentId.equals(newInfo.contentId)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private HabitsEvent getOverlapEvent(){
        String eventTime = mHourArr[lvHour.getSelectedItem()] + ":" + mMinuteArr[lvMinute.getSelectedItem()];
        UbtLog.d(TAG,"eventTime = " + eventTime);
        boolean isOverlap = false;
        HabitsEvent overlapEvent = null;
        for(HabitsEvent habitsEvent : mHabitsEventInfoDatas){
            if(!newEventDetail.eventId.equals(habitsEvent.eventId) && "1".equals(habitsEvent.status)){
                isOverlap = isOverlapTime(eventTime,habitsEvent.eventTime,habitsEvent.finishTime);
                UbtLog.d(TAG,"eventTime = " + habitsEvent.eventTime + "_"+habitsEvent.finishTime + " isOverlap = " + isOverlap);
                if(isOverlap){
                    overlapEvent = habitsEvent;
                    break;
                }
            }
        }
        return overlapEvent;
    }

    private boolean isOverlapTime(String eventTime, String startTime,String endTime){
        Date date;
        Date startDate ;
        Date endDate ;
        try {
            date  = sdf.parse(eventTime);
            startDate = sdf.parse(startTime);
            endDate = sdf.parse(endTime);
            if(startDate.getTime() > endDate.getTime()){
                endDate = DateUtils.getSpecifiedDayAfter(endDate);
            }
            if(date.getTime() >= startDate.getTime() && date.getTime() <= endDate.getTime()){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void exitEditConfirm() {
        new ConfirmDialog(getContext()).builder()
                .setMsg(getStringRes("ui_habits_exit_edit_tip"))
                .setCancelable(false)
                .setPositiveButton(getStringRes("ui_habits_continue_edit"), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).setNegativeButton(getStringRes("ui_habits_exit_edit"), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pop();
            }
        }).show();
    }

}
