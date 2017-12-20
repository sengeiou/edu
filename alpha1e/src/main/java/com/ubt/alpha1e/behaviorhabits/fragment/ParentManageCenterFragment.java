package com.ubt.alpha1e.behaviorhabits.fragment;


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
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsPresenter;
import com.ubt.alpha1e.behaviorhabits.adapter.HabitsEventRecyclerAdapter;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEventDetail;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEventInfo;
import com.ubt.alpha1e.behaviorhabits.model.PlayContent;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.utils.log.UbtLog;

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
    private List<HabitsEventInfo> mHabitsEventInfoDatas = null;
    private boolean isWorkdayMode = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CLICK_SWITCH_EVENT:
                    //切换开关
                    HabitsEventInfo habitsEventInfo = mHabitsEventInfoDatas.get(msg.arg1);
                    if("1".equals(habitsEventInfo.eventSwitch)){
                        habitsEventInfo.eventSwitch = "0";
                    }else {
                        habitsEventInfo.eventSwitch = "1";
                    }
                    mAdapter.notifyItemChanged(msg.arg1);
                    break;
                case SHOW_EVENT_INFO:
                    //显示详情
                    break;
            }
        }
    };

    @Override
    protected void initUI() {
        tvBaseTitleName.setText(getStringRes("ui_habits_parent_management_center"));
        ivTitleRight.setBackgroundResource(R.drawable.icon_habits_statistics);
        ivTitleRight.setVisibility(View.VISIBLE);

        mHabitsEventInfoDatas = new ArrayList<>();

        HabitsEventInfo h = new HabitsEventInfo();
        h.eventId = "1";
        h.eventTime = "07:45";
        h.eventName = "早餐";
        h.eventSwitch = "1";
        mHabitsEventInfoDatas.add(h);

        h = new HabitsEventInfo();
        h.eventId = "1";
        h.eventTime = "09:45";
        h.eventName = "午餐";
        h.eventSwitch = "0";
        mHabitsEventInfoDatas.add(h);

        h = new HabitsEventInfo();
        h.eventId = "1";
        h.eventTime = "09:45";
        h.eventName = "晚餐";
        h.eventSwitch = "1";
        mHabitsEventInfoDatas.add(h);

        h = new HabitsEventInfo();
        h.eventId = "1";
        h.eventTime = "09:45";
        h.eventName = "夜宵";
        h.eventSwitch = "0";
        mHabitsEventInfoDatas.add(h);

        h = new HabitsEventInfo();
        h.eventId = "1";
        h.eventTime = "09:45";
        h.eventName = "夜宵";
        h.eventSwitch = "0";
        mHabitsEventInfoDatas.add(h);

        h = new HabitsEventInfo();
        h.eventId = "1";
        h.eventTime = "09:45";
        h.eventName = "夜宵";
        h.eventSwitch = "0";
        mHabitsEventInfoDatas.add(h);

        initRecyclerViews();
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

        mAdapter = new HabitsEventRecyclerAdapter(getContext(), mHabitsEventInfoDatas, mHandler);
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
    public void onTest(boolean isSuccess) {
        UbtLog.d("Test1Fragment", "isSuccess = " + isSuccess);
    }

    @Override
    public void showBehaviourList(boolean status, List<HabitsEvent> modelList, String errorMsg) {

    }

    @Override
    public void showBehaviourEventContent(boolean status, HabitsEventDetail content, String errorMsg) {

    }

    @Override
    public void showBehaviourPlayContent(boolean status, List<PlayContent> playList, String errorMsg) {

    }

    @Override
    public void showNetworkRequestError() {

    }

    @OnClick({R.id.ll_base_back, R.id.iv_title_right, R.id.rl_workdays, R.id.rl_holidays})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                getActivity().finish();
                break;
            case R.id.iv_title_right:
                mPresenter.getBehaviourList("1","5");
                mPresenter.getBehaviourEvent("12345");
                mPresenter.getBehaviourPlayContent("1","6");
                mPresenter.setBehaviourEvent("1234",1);
                break;
            case R.id.rl_workdays:
                switchMode();
                break;
            case R.id.rl_holidays:
                switchMode();
                break;
        }
    }

    private void switchMode(){
        if(isWorkdayMode){
            isWorkdayMode = false;
            ivWorkdays.setBackgroundResource(R.drawable.icon_habits_workdays_unselected);
            tvWorkdays.setTextColor(getResources().getColor(R.color.T26));
            ivWorkdaysSelect.setVisibility(View.INVISIBLE);

            ivHolidays.setBackgroundResource(R.drawable.icon_habits_holidays_selected);
            tvHolidays.setTextColor(getResources().getColor(R.color.T25));
            ivHolidaysSelect.setVisibility(View.VISIBLE);

        }else {
            isWorkdayMode = true;
            ivWorkdays.setBackgroundResource(R.drawable.icon_habits_workdays_selected);
            tvWorkdays.setTextColor(getResources().getColor(R.color.T25));
            ivWorkdaysSelect.setVisibility(View.VISIBLE);

            ivHolidays.setBackgroundResource(R.drawable.icon_habits_holidays_unselected);
            tvHolidays.setTextColor(getResources().getColor(R.color.T26));
            ivHolidaysSelect.setVisibility(View.INVISIBLE);
        }

    }

}
