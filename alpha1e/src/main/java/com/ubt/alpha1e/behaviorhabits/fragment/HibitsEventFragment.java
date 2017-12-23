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
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsPresenter;
import com.ubt.alpha1e.behaviorhabits.adapter.HabitsEventRecyclerAdapter;
import com.ubt.alpha1e.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEventInfo;
import com.ubt.alpha1e.behaviorhabits.model.PlayContent;
import com.ubt.alpha1e.behaviorhabits.model.UserScore;
import com.ubt.alpha1e.data.Constant;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.custom.CircleBar;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
import com.ubt.alpha1e.ui.dialog.InputPasswordDialog;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class HibitsEventFragment extends MVPBaseFragment<BehaviorHabitsContract.View, BehaviorHabitsPresenter> implements BehaviorHabitsContract.View {

    private static final String TAG = HibitsEventFragment.class.getSimpleName();

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    Unbinder unbinder;
    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.rv_habits_event)
    RecyclerView rvHabitsEvent;
    @BindView(R.id.tv_today)
    TextView tvToday;
    @BindView(R.id.cb_score)
    CircleBar cbScore;
    @BindView(R.id.tv_ratio)
    TextView tvRatio;
    @BindView(R.id.tv_score)
    TextView tvScore;

    public HabitsEventRecyclerAdapter mAdapter;
    private List<HabitsEventInfo> mHabitsEventInfoDatas = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    public static HibitsEventFragment newInstance() {
        HibitsEventFragment hibitsEventFragment = new HibitsEventFragment();
        return hibitsEventFragment;
    }

    @Override
    protected void initUI() {
        UbtLog.d(TAG, "--initUI--");
        tvBaseTitleName.setText(getStringRes("ui_habits_alert_event"));
        ivTitleRight.setBackgroundResource(R.drawable.icon_habits_parentscentral);
        ivTitleRight.setVisibility(View.VISIBLE);

        tvToday.setText(sdf.format(new Date()));
        tvRatio.setText(getStringRes("ui_habits_has_finish") + "25%");
        tvScore.setText("30");
        cbScore.setSweepAngle(25f);

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

        mAdapter = new HabitsEventRecyclerAdapter(getContext(), mHabitsEventInfoDatas, true, mHandler);
        rvHabitsEvent.setAdapter(mAdapter);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_hibits_event;
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
        UbtLog.d(TAG, "requestCode = " + requestCode);
        if (requestCode == Constant.HIBITS_EVENT_EDIT_REQUEST_CODE && resultCode == Constant.HIBITS_EVENT_EDIT_RESPONSE_CODE) {
            UbtLog.d(TAG, "resultCode = " + resultCode + "   " + data.getParcelable(Constant.HABITS_EVENT_INFO_KEY));
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
    public void showBehaviourEventContent(boolean status, EventDetail content, String errorMsg) {

    }

    @Override
    public void showBehaviourPlayContent(boolean status, List<PlayContent> playList, String errorMsg) {

    }

    @Override
    public void showNetworkRequestError() {

    }

    @Override
    public void onAlertSelectItem(int index, String language, int alertType) {

    }


    @OnClick({R.id.ll_base_back, R.id.iv_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                getActivity().finish();
                break;
            case R.id.iv_title_right:
                enterParentManageCenter();
                break;
        }
    }

    /**
     * 进入家长管理中心
     */
    private void enterParentManageCenter() {

        new InputPasswordDialog(getContext()).builder()
                .setMsg(getStringRes("ui_habits_password_input_tip"))
                .setCancelable(true)
                .setPassword("123456")
                .setCallbackListener(new InputPasswordDialog.IInputPasswordListener() {
                    @Override
                    public void onCorrectPassword() {
                        startForResult(ParentManageCenterFragment.newInstance(), 0);
                    }

                    @Override
                    public void onFindPassword() {

                    }
                })
                .show();

    }

}
