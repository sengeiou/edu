package com.ubt.alpha1e.edu.userinfo.helpfeedback.feedbacksearch;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.adapter.FeedbackRecyclerAdapter;
import com.ubt.alpha1e.edu.data.model.FeedbackInfo;
import com.ubt.alpha1e.edu.mvp.MVPBaseFragment;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class FeedbackSearchFragment extends MVPBaseFragment<FeedbackSearchContract.View, FeedbackSearchPresenter> implements FeedbackSearchContract.View {

    private static final String TAG = FeedbackSearchFragment.class.getSimpleName();

    private static final int REFRESH_DATA = 1;

    @BindView(R.id.rv_feedback)
    RecyclerView rvFeedback;
    @BindView(R.id.rl_empty_result)
    RelativeLayout rlEmptyResult;
    @BindView(R.id.tv_head_name)
    TextView tvHeadName;
    Unbinder unbinder;

    public FeedbackRecyclerAdapter mAdapter;

    private List<FeedbackInfo> mFeedbackInfoDatas = null;
    private List<FeedbackInfo> mAllFeedbackInfoDatas = null;

    private String[] mFeedBackTitle = new String[]{"机器人无法连接手机？",
                                                "如何确定机器人已经和手机APP连接上了？",
                                                "为什么机器人有时跳舞无法站稳？",
                                                "如何自定义行为习惯模版？",
                                                "课程页面为什么无法加载？",
                                                "机器人开机复位后，有个别关节不能工作？",
                                                "为什么机器人有声音，但是没有动作？",
                                                "机器人下载内容后，为什么不能正常工作？",
                                                "机器人开机、复位后，怎么有些舵机还是松弛的？",
                                                "机器人无法连接电脑？",
                                                "如何唤醒机器人？",
                                                "机器人可以添加新的动作吗？",
                                                "机器人充电需要多久，充一次电可以玩多久？",
                                                "机器人可以边玩边充电吗？",
                                                "为什么有时我的APP搜索不到机器人？",
                                                "机器人如何升级？"
    };

    private String[] mFeedBackContent = new String[]{"通过蓝牙在app内与机器人建立连接。如无法连接，可尝试重启机器人与app，多次失败可拨打售后电话进行详细咨询。",
                                                "连接成功时，app会有提示，且机器人也会有音效提示。",
                                                "机器人偏置变化，可自行重调偏置或拨打售后电话进行维修。",
                                                "家长可在行为习惯模版设置页面进行调整，对事项尽享打卡／关闭与时间设定。",
                                                "请确认网络良好，课程页面只需首次加载成功，后续无需网络，可直接使用。",
                                                "个别舵机线有松动或短路情况，当该舵机灯熄灭时，请检测舵机线是否接好或磨损；如果舵机发出马达转动的“嗡嗡”声，请返厂维修。",
                                                "请确认该文件包含动作，并检查机器人全身的连接线插头是否松动或接触不良。",
                                                "动作脚本有问题，可尝试其他动作。或电池电量不足以及个别舵机线有松动或短路情况，请检测舵机线是否接好或磨损。",
                                                "机器人电量不足，请关机充电。",
                                                "机器人的下载线接口接触不良，重新接插或更换USB数据线；或机器人系统异常，重新启动后再下载；也可检查是否电池电压不足，请充好电。",
                                                "连接蓝牙与网络后，通过“你好，阿尔法。”即可语音唤醒机器人。",
                                                "在动作编辑页面自行创建，或在app社区内下载动作，皆可。",
                                                "充满电需1.5小时，待机状态可持续约4个小时，运动状态可续航约2个小时。",
                                                "可以。机器人充电适用电压220V，且必须使用机器人的原装充电器充电。",
                                                "请重启机器人，或将手机蓝牙重新打开，再次搜索即可。",
                                                "如果您现在知道机器人有更高版本，需要将其升级，请确认机器人已经开启自动升级模式，您可以到“个人中心”>”设置”>”我的机器人”中查看自动升级模式状态。\n" +
                                                "在自动升级模式下，请将机器人重启，给他联网，并确保机器人电量大于35%。等待机器人下载安装包，等待时间会根据网速差异而有所不同，机器人开始升级前会有语音提示：检测到新版本，阿尔法现在开始升级。\n"
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_DATA:
                    UbtLog.d(TAG, "mFeedbackInfoDatas.size() = " + mFeedbackInfoDatas.size());
                    mAdapter.notifyDataSetChanged();
                    if (mFeedbackInfoDatas.size() == 0) {
                        rlEmptyResult.setVisibility(View.VISIBLE);
                    } else {
                        rlEmptyResult.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    public static FeedbackSearchFragment newInstance() {
        FeedbackSearchFragment searchFragment = new FeedbackSearchFragment();
        return searchFragment;
    }

    public FeedbackSearchFragment() {

    }

    @Override
    protected void initUI() {
        mFeedbackInfoDatas = new ArrayList<>();
        mAllFeedbackInfoDatas = new ArrayList<>();

        FeedbackInfo f = null;
        for(int i=0;i<mFeedBackTitle.length;i++){
            f = new FeedbackInfo();
            f.feedbackId = i;
            f.feedbackName = mFeedBackTitle[i];
            f.feedbackIntroduction = mFeedBackContent[i];
            mAllFeedbackInfoDatas.add(f);
        }
        initRecyclerViews();
    }

    public void initRecyclerViews() {
        mFeedbackInfoDatas.addAll(mAllFeedbackInfoDatas);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvFeedback.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator animator = rvFeedback.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mAdapter = new FeedbackRecyclerAdapter(getContext(), mFeedbackInfoDatas, mHandler);
        rvFeedback.setAdapter(mAdapter);
    }

    public void refreshSearchResult(String editStr) {
        if(TextUtils.isEmpty(editStr)){
            tvHeadName.setText(getStringRes("ui_setting_hot_question"));
        }else {
            tvHeadName.setText(getStringRes("ui_setting_search_result"));
        }

        mAdapter.setSearchName(editStr);
        mPresenter.doSearchResult(mAllFeedbackInfoDatas, editStr);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_feedback_search;
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
    public void onSearchResult(List<FeedbackInfo> feedbackInfoList) {
        mFeedbackInfoDatas.clear();
        mFeedbackInfoDatas.addAll(feedbackInfoList);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }
}
