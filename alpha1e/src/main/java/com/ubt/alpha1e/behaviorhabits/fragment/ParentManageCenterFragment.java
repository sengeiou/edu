package com.ubt.alpha1e.behaviorhabits.fragment;


import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsPresenter;
import com.ubt.alpha1e.behaviorhabits.adapter.HabitsEventRecyclerAdapter;
import com.ubt.alpha1e.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.behaviorhabits.model.UserScore;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.dialog.SLoadingDialog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.webcontent.WebContentActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ParentManageCenterFragment extends MVPBaseFragment<BehaviorHabitsContract.View, BehaviorHabitsPresenter> implements BehaviorHabitsContract.View {


    private static final String TAG = ParentManageCenterFragment.class.getSimpleName();

    public static final int CLICK_SWITCH_EVENT = 1;
    public static final int SHOW_EVENT_INFO = 2;
    private static final int UPDATE_UI_DATA = 3;
    private static final int UPDATE_SWITCH_RESULT = 4;
    private static final int NETWORK_EXCEPTION = 5;
    private static final int EDIT_UPDATE = 6;

    Unbinder unbinder;
    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.rv_habits_event)
    RecyclerView rvHabitsEvent;

    @BindView(R.id.iv_workdays)
    ImageView ivWorkdays;
    @BindView(R.id.tv_workdays)
    TextView tvWorkdays;
    @BindView(R.id.rl_workdays)
    RelativeLayout rlWorkdays;
    @BindView(R.id.iv_workdays_select)
    ImageView ivWorkdaysSelect;
    @BindView(R.id.iv_holidays)
    ImageView ivHolidays;
    @BindView(R.id.tv_holidays)
    TextView tvHolidays;
    @BindView(R.id.iv_holidays_select)
    ImageView ivHolidaysSelect;

    public HabitsEventRecyclerAdapter mAdapter;
    private List<HabitsEvent> mHabitsEventInfoDatas = null;
    private int mWorkdayMode = 1;//workday=1; holiday=2
    private HabitsEvent switchHabitsEvent = null;//

    protected Dialog mCoonLoadingDia;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CLICK_SWITCH_EVENT:
                    //切换开关
                    switchHabitsEvent = mHabitsEventInfoDatas.get(msg.arg1);

                    int status = "0".equals(switchHabitsEvent.status) ? 1 : 0;
                    mCoonLoadingDia.show();
                    mPresenter.enableBehaviourEvent(switchHabitsEvent.eventId, status);
                    break;
                case UPDATE_SWITCH_RESULT:

                    mCoonLoadingDia.cancel();
                    if((boolean)msg.obj){
                        UbtLog.d(TAG,"switchHabitsEvent.status = " + switchHabitsEvent.status);
                        switchHabitsEvent.status = "0".equals(switchHabitsEvent.status) ? "1" : "0";
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                case SHOW_EVENT_INFO:
                    //编辑
                    startForResult(HibitsEventEditFragment.newInstance(mHabitsEventInfoDatas.get(msg.arg1),mWorkdayMode), Constant.HIBITS_EVENT_EDIT_REQUEST_CODE);
                    break;
                case UPDATE_UI_DATA:
                    mCoonLoadingDia.cancel();
                    UserScore<List<HabitsEvent>> userScore = (UserScore<List<HabitsEvent>>) msg.obj;
                    if(userScore != null){
                        List<HabitsEvent> habitsEventList = userScore.details;
                        mHabitsEventInfoDatas.clear();
                        mHabitsEventInfoDatas.addAll(habitsEventList);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                case EDIT_UPDATE:
                    HabitsEvent habitsEvent = (HabitsEvent) msg.obj;
                    if(habitsEvent != null){
                        for(int index = 0 ;index < mHabitsEventInfoDatas.size();index++){
                            if(habitsEvent.eventId == mHabitsEventInfoDatas.get(index).eventId){
                                mHabitsEventInfoDatas.get(index).eventTime = habitsEvent.eventTime;
                                mAdapter.notifyItemChanged(index);
                                break;
                            }
                        }
                    }
                    break;
                case NETWORK_EXCEPTION:
                    mCoonLoadingDia.cancel();
                    ToastUtils.showShort(getStringRes("ui_common_network_request_failed"));
                    break;
            }
        }
    };

    public static ParentManageCenterFragment newInstance() {
        ParentManageCenterFragment manageCenterFragment = new ParentManageCenterFragment();
        Bundle bundle = new Bundle();
        manageCenterFragment.setArguments(bundle);
        return manageCenterFragment;
    }

    @Override
    protected void initUI() {
        mCoonLoadingDia = SLoadingDialog.getInstance(getContext());

        tvBaseTitleName.setText(getStringRes("ui_habits_parent_management_center"));
        ivTitleRight.setBackgroundResource(R.drawable.icon_habits_statistics);
        ivTitleRight.setVisibility(View.VISIBLE);

        mHabitsEventInfoDatas = new ArrayList<>();

        initRecyclerViews();

        switchMode(1);
    }

    public void initRecyclerViews() {
        UbtLog.d(TAG, "rvHabitsEvent = " + rvHabitsEvent);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvHabitsEvent.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator animator = rvHabitsEvent.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        rvHabitsEvent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 36;
            }
        });

        mAdapter = new HabitsEventRecyclerAdapter(getContext(), mHabitsEventInfoDatas,false, mHandler);
        rvHabitsEvent.setAdapter(mAdapter);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_parent_manage_center;
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
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
        UbtLog.d(TAG,"requestCode = " + requestCode);
        if(requestCode == Constant.HIBITS_EVENT_EDIT_REQUEST_CODE && resultCode == Constant.HIBITS_EVENT_EDIT_RESPONSE_CODE ){
            UbtLog.d(TAG,"resultCode = " + resultCode + "   " + data.getParcelable(Constant.HABITS_EVENT_INFO_KEY));
            Message msg = new Message();
            msg.what = EDIT_UPDATE;
            msg.obj = data.getParcelable(Constant.HABITS_EVENT_INFO_KEY);
            mHandler.sendMessage(msg);
        }

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
        if(status){
            Message msg = new Message();
            msg.what = UPDATE_UI_DATA;
            msg.obj = userScore;
            mHandler.sendMessage(msg);
        }else {
            mHandler.sendEmptyMessage(NETWORK_EXCEPTION);
        }
    }

    @Override
    public void showBehaviourEventContent(boolean status, EventDetail content, String errorMsg) {

    }

    @Override
    public void showBehaviourPlayContent(boolean status, ArrayList<PlayContentInfo> playList, String errorMsg) {

    }

    @Override
    public void onUserPassword(String password) {

    }



    @Override
    public void onAlertSelectItem(int index, String language, int alertType) {

    }

    @Override
    public void onRequestStatus(int requestType, int errorCode) {
        if(errorCode == mPresenter.NETWORK_SUCCESS){
            if(requestType == mPresenter.GET_BEHAVIOURCONTROL_CMD){
                Message msg = new Message();
                msg.what = UPDATE_SWITCH_RESULT;
                msg.obj = true;
                mHandler.sendMessage(msg);
            }
        }else {
            mHandler.sendEmptyMessage(NETWORK_EXCEPTION);
        }
    }

    @OnClick({R.id.ll_base_back, R.id.iv_title_right, R.id.rl_workdays, R.id.rl_holidays})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                pop();
                break;
            case R.id.iv_title_right:
                /*mPresenter.getBehaviourList("1","5");
                mPresenter.getBehaviourEvent("12345");
                mPresenter.getBehaviourPlayContent("1","6");
                mPresenter.setBehaviourEvent("1234",1);*/
                WebContentActivity.launchActivity(getActivity(), HttpEntity.HIBITS_STATISTICS,"");

                break;
            case R.id.rl_workdays:
                switchMode(1);
                break;
            case R.id.rl_holidays:
                switchMode(2);
                break;
        }
    }

    private void switchMode(int workdayMode){
        if(workdayMode == 1){

            mWorkdayMode = workdayMode;
            ivWorkdays.setBackgroundResource(R.drawable.icon_habits_workdays_selected);
            tvWorkdays.setTextColor(getResources().getColor(R.color.T25));
            ivWorkdaysSelect.setVisibility(View.VISIBLE);

            ivHolidays.setBackgroundResource(R.drawable.icon_habits_holidays_unselected);
            tvHolidays.setTextColor(getResources().getColor(R.color.T26));
            ivHolidaysSelect.setVisibility(View.INVISIBLE);


        }else {
            mWorkdayMode = workdayMode;
            ivWorkdays.setBackgroundResource(R.drawable.icon_habits_workdays_unselected);
            tvWorkdays.setTextColor(getResources().getColor(R.color.T26));
            ivWorkdaysSelect.setVisibility(View.INVISIBLE);

            ivHolidays.setBackgroundResource(R.drawable.icon_habits_holidays_selected);
            tvHolidays.setTextColor(getResources().getColor(R.color.T25));
            ivHolidaysSelect.setVisibility(View.VISIBLE);
        }

        mCoonLoadingDia.show();
        mPresenter.getParentBehaviourList("1","1",String.valueOf(workdayMode));
    }

}
