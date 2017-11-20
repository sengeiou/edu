package com.ubt.alpha1e.userinfo.helpfeedback.feedbacksearch;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.FeedbackRecyclerAdapter;
import com.ubt.alpha1e.data.model.FeedbackInfo;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.utils.log.UbtLog;

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
    Unbinder unbinder;

    public FeedbackRecyclerAdapter mAdapter;
    private List<FeedbackInfo> mFeedbackInfoDatas = null;
    private List<FeedbackInfo> mAllFeedbackInfoDatas = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESH_DATA:
                    UbtLog.d(TAG,"mFeedbackInfoDatas.size() = " + mFeedbackInfoDatas.size());
                    mAdapter.notifyDataSetChanged();
                    if(mFeedbackInfoDatas.size() == 0){

                    }
                    break;
            }
        }
    };

    public FeedbackSearchFragment() {

    }

    @Override
    protected void initUI() {
        mFeedbackInfoDatas = new ArrayList<>();
        mAllFeedbackInfoDatas = new ArrayList<>();

        FeedbackInfo f = new FeedbackInfo();
        f.feedbackId = 1;
        f.feedbackName = "机器人无法连接手机";
        f.feedbackIntroduction = f.feedbackName + "解决方法";
        mAllFeedbackInfoDatas.add(f);

        f = new FeedbackInfo();
        f.feedbackId = 1;
        f.feedbackName = "机器人无法连接电脑";
        f.feedbackIntroduction = f.feedbackName + "解决方法";
        mAllFeedbackInfoDatas.add(f);

        f = new FeedbackInfo();
        f.feedbackId = 1;
        f.feedbackName = "机器人可以添加新的舞蹈吗?";
        f.feedbackIntroduction = f.feedbackName + "解决方法";
        mAllFeedbackInfoDatas.add(f);

        f = new FeedbackInfo();
        f.feedbackId = 1;
        f.feedbackName = "机器人怎样添加动作?";
        f.feedbackIntroduction = f.feedbackName + "解决方法";
        mAllFeedbackInfoDatas.add(f);

        f = new FeedbackInfo();
        f.feedbackId = 1;
        f.feedbackName = "机器人启动后不能正常复位?";
        f.feedbackIntroduction = f.feedbackName + "解决方法";
        mAllFeedbackInfoDatas.add(f);

        f = new FeedbackInfo();
        f.feedbackId = 1;
        f.feedbackName = "机器人启动后不能正常复位?";
        f.feedbackIntroduction = f.feedbackName + "解决方法";
        mAllFeedbackInfoDatas.add(f);

        f = new FeedbackInfo();
        f.feedbackId = 1;
        f.feedbackName = "移动端控制不了机器人?";
        f.feedbackIntroduction = f.feedbackName + "解决方法";
        mAllFeedbackInfoDatas.add(f);

        for (int i = 0; i < 2; i++) {
            FeedbackInfo f1 = new FeedbackInfo();
            f1.feedbackId = 1;
            f1.feedbackName = ((MVPBaseActivity)getActivity()).getStringResources("ui_setting_hot_question") + "标题_" + i;
            f1.feedbackIntroduction = ((MVPBaseActivity)getActivity()).getStringResources("ui_setting_hot_question") + "内容_" + i;
            mAllFeedbackInfoDatas.add(f1);
        }

        initRecyclerViews();
    }

    public void initRecyclerViews() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvFeedback.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator animator = rvFeedback.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mAdapter = new FeedbackRecyclerAdapter(getContext(), mFeedbackInfoDatas, mHandler);
        rvFeedback.setAdapter(mAdapter);
    }

    public void refreshSearchResult(String editStr){
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
