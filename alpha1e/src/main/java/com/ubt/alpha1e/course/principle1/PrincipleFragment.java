package com.ubt.alpha1e.course.principle1;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.animator.FloatAnimator;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.course.CourseActivity;
import com.ubt.alpha1e.course.event.PrincipleEvent;
import com.ubt.alpha1e.course.helper.PrincipleHelper;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.utils.SizeUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

@SuppressLint("ValidFragment")
public class PrincipleFragment extends MVPBaseFragment<PrincipleContract.View, PrinciplePresenter> implements PrincipleContract.View {

    private static final String TAG = PrincipleFragment.class.getSimpleName();

    private static final int START_FLOAT_ANIMATION_1 = 1;
    private static final int START_FLOAT_ANIMATION_2 = 2;
    private static final int START_FLOAT_ANIMATION_3 = 3;
    private static final int START_FLOAT_ANIMATION = 4;
    private static final int PLAY_ACTION_NEXT = 5;
    private static final int TAP_HEAD = 6;
    private static final int GO_NEXT = 7;


    @BindView(R.id.iv_principle_alpha)
    ImageView ivPrincipleAlpha;
    @BindView(R.id.rl_dialogue_1)
    RelativeLayout rlDialogue1;
    @BindView(R.id.rl_dialogue_2)
    RelativeLayout rlDialogue2;
    @BindView(R.id.rl_dialogue_3)
    RelativeLayout rlDialogue3;
    @BindView(R.id.tv_next)
    TextView tvNext;
    Unbinder unbinder;

    private PrincipleHelper mHelper = null;
    private FloatAnimator mFloatAnimator = null;
    private int scale = 0;
    private Animation biggerAnimation = null;
    private int mCurrentPlayProgress = 0;

    private static String[] playActionFile = {"原理1.hts","原理2.hts","原理3.hts"};

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_FLOAT_ANIMATION_1:
                    mFloatAnimator.addShow(rlDialogue1);
                    break;
                case START_FLOAT_ANIMATION_2:
                    mFloatAnimator.addShow(rlDialogue2);
                    break;
                case START_FLOAT_ANIMATION_3:
                    mFloatAnimator.addShow(rlDialogue3);
                    break;
                case START_FLOAT_ANIMATION:
                    int viewId = msg.arg1;
                    if(viewId == R.id.rl_dialogue_1){
                        mFloatAnimator.addShow(rlDialogue1);
                    }else if(viewId == R.id.rl_dialogue_2){
                        mFloatAnimator.addShow(rlDialogue2);
                    }else if(viewId == R.id.rl_dialogue_3){
                        mFloatAnimator.addShow(rlDialogue3);
                    }

                    break;
                case PLAY_ACTION_NEXT:
                    UbtLog.d(TAG,"mCurrentPlayProgress = " + mCurrentPlayProgress);
                    if(mCurrentPlayProgress == 0){
                        mHelper.playFile(playActionFile[0]);
                        rlDialogue1.setVisibility(View.VISIBLE);
                        rlDialogue1.startAnimation(biggerAnimation);
                        mCurrentPlayProgress = 1;
                        startFloatAnimation(rlDialogue1,500);
                    }else if(mCurrentPlayProgress == 1){
                        mHelper.playFile(playActionFile[1]);
                        rlDialogue2.setVisibility(View.VISIBLE);
                        rlDialogue2.startAnimation(biggerAnimation);
                        mCurrentPlayProgress = 2;
                        startFloatAnimation(rlDialogue2,500);
                    }else if(mCurrentPlayProgress == 2){
                        mHelper.playFile(playActionFile[2]);
                        rlDialogue3.setVisibility(View.VISIBLE);
                        rlDialogue3.startAnimation(biggerAnimation);
                        mCurrentPlayProgress = 3;
                        startFloatAnimation(rlDialogue3,500);
                    }else {
                        mCurrentPlayProgress = 4;
                        mHandler.sendEmptyMessage(GO_NEXT);
                    }
                    break;
                case GO_NEXT:

                    mHelper.doInit();
                    if(mCurrentPlayProgress == 1){
                        rlDialogue2.setVisibility(View.VISIBLE);
                        rlDialogue2.startAnimation(biggerAnimation);
                        rlDialogue3.setVisibility(View.VISIBLE);
                        rlDialogue3.startAnimation(biggerAnimation);
                        mCurrentPlayProgress = 4;
                        mHandler.sendEmptyMessageDelayed(GO_NEXT,500);
                    }else if(mCurrentPlayProgress == 2){
                        rlDialogue3.setVisibility(View.VISIBLE);
                        rlDialogue3.startAnimation(biggerAnimation);
                        mCurrentPlayProgress = 4;
                        mHandler.sendEmptyMessageDelayed(GO_NEXT,500);
                    }else {
                        ((CourseActivity)getActivity()).doSaveCourseProgress(1,1,1);
                        ((CourseActivity)getActivity()).switchFragment(CourseActivity.FRAGMENT_SPLIT);
                    }

                    break;
                case TAP_HEAD:
                    //拍头退出课程模式
                    UbtLog.d(TAG,"TAP_HEAD == " );
                    ToastUtils.showShort(getStringRes("ui_setting_principle_tap_head"));
                    getActivity().finish();
                    break;
            }
        }
    };

    private void startFloatAnimation(View view,int delayTime){
        Message msg = new Message();
        msg.what = START_FLOAT_ANIMATION;
        msg.arg1 = view.getId();
        mHandler.sendMessageDelayed(msg,delayTime);
    }

    @SuppressLint("ValidFragment")
    public PrincipleFragment(PrincipleHelper helper){
        super();
        mHelper = helper;
    }

    @Override
    protected void initUI() {

        scale = (int) this.getResources().getDisplayMetrics().density;
        UbtLog.d(TAG, "scale = " + scale
                + "  width = " + this.getResources().getDisplayMetrics().widthPixels
                + "  height = " + this.getResources().getDisplayMetrics().heightPixels
                + "  densityDpi = " + this.getResources().getDisplayMetrics().densityDpi);

        rlDialogue2.setVisibility(View.INVISIBLE);
        rlDialogue3.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams ivPrincipleAlphaParams = (RelativeLayout.LayoutParams) ivPrincipleAlpha.getLayoutParams();
        ivPrincipleAlphaParams.width = SizeUtils.dip2px(getContext(), 130);
        ivPrincipleAlphaParams.height = SizeUtils.dip2px(getContext(), 236);
        ivPrincipleAlpha.setLayoutParams(ivPrincipleAlphaParams);

        RelativeLayout.LayoutParams rlDialogue1Params = (RelativeLayout.LayoutParams) rlDialogue1.getLayoutParams();
        rlDialogue1Params.width = SizeUtils.dip2px(getContext(), 146);
        rlDialogue1Params.height = SizeUtils.dip2px(getContext(), 135);
        rlDialogue1.setLayoutParams(rlDialogue1Params);

        RelativeLayout.LayoutParams rlDialogue2Params = (RelativeLayout.LayoutParams) rlDialogue2.getLayoutParams();
        rlDialogue2Params.width = SizeUtils.dip2px(getContext(), 163);
        rlDialogue2Params.height = SizeUtils.dip2px(getContext(), 133);
        rlDialogue2.setLayoutParams(rlDialogue2Params);

        RelativeLayout.LayoutParams rlDialogue3Params = (RelativeLayout.LayoutParams) rlDialogue3.getLayoutParams();
        rlDialogue3Params.width = SizeUtils.dip2px(getContext(), 121);
        rlDialogue3Params.height = SizeUtils.dip2px(getContext(), 107);
        rlDialogue3.setLayoutParams(rlDialogue3Params);

        UbtLog.d(TAG, "ivPrincipleAlphaParams = " + ivPrincipleAlphaParams.width + "  " + ivPrincipleAlphaParams.height);

        mFloatAnimator = FloatAnimator.getIntanse();

        biggerAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.scan_bigger_anim);
        biggerAnimation.setDuration(500);

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_principle;
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Subscribe
    public void onEventPrinciple(PrincipleEvent event) {
        UbtLog.d(TAG,"getCurrentFragment : " + ((CourseActivity)getActivity()).getCurrentFragment());
        if(!(((CourseActivity)getActivity()).getCurrentFragment() instanceof PrincipleFragment) ){
            return;
        }

        if(event.getEvent() == PrincipleEvent.Event.PLAY_ACTION_FINISH){
            mHandler.sendEmptyMessage(PLAY_ACTION_NEXT);
        }else if(event.getEvent() == PrincipleEvent.Event.TAP_HEAD){
            mHandler.sendEmptyMessage(TAP_HEAD);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((CourseActivity)getActivity()).doGetCourseProgress(1);

        mHandler.sendEmptyMessage(PLAY_ACTION_NEXT);
    }

    @Override
    public void onDestroyView() {
        mFloatAnimator.stopFloatAnimator();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @OnClick({R.id.iv_back, R.id.rl_dialogue_2, R.id.rl_dialogue_1, R.id.rl_dialogue_3, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().finish();
                break;
            case R.id.rl_dialogue_2:
                break;
            case R.id.rl_dialogue_1:
                break;
            case R.id.rl_dialogue_3:
                break;
            case R.id.tv_next:
                mHandler.sendEmptyMessage(GO_NEXT);

                break;
        }
    }

}
