package com.ubt.alpha1e.behaviorhabits.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsActivity;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsContract;
import com.ubt.alpha1e.behaviorhabits.BehaviorHabitsPresenter;
import com.ubt.alpha1e.behaviorhabits.model.EventDetail;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEvent;
import com.ubt.alpha1e.behaviorhabits.model.HabitsEventDetail;
import com.ubt.alpha1e.behaviorhabits.model.PlayContent;
import com.ubt.alpha1e.behaviorhabits.model.UserScore;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class Test1Fragment extends MVPBaseFragment<BehaviorHabitsContract.View, BehaviorHabitsPresenter> implements BehaviorHabitsContract.View {

    @BindView(R.id.rl_test)
    RelativeLayout rlTest;
    Unbinder unbinder;
    @BindView(R.id.tv_text_do)
    TextView tvTextDo;

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_1;
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


    @OnClick({R.id.tv_text_do, R.id.rl_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_text_do:
                mPresenter.doTest();
                mPresenter.getBehaviourList("1","5");
                mPresenter.getBehaviourEvent("12345");
                mPresenter.getBehaviourPlayContent("1","6");
                mPresenter.setBehaviourEvent("1234",1);
                break;
            case R.id.rl_test:
                ((BehaviorHabitsActivity) getActivity()).switchMode(2);
                break;
        }
    }
}
