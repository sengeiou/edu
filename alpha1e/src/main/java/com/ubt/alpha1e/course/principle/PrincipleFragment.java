package com.ubt.alpha1e.course.principle;


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
import com.ubt.alpha1e.course.CourseActivity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.utils.SizeUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PrincipleFragment extends MVPBaseFragment<PrincipleContract.View, PrinciplePresenter> implements PrincipleContract.View {

    private static final String TAG = PrincipleFragment.class.getSimpleName();

    private static final int START_BIGGER_ANIMATION_1 = 1;
    private static final int START_BIGGER_ANIMATION_2 = 2;
    private static final int START_BIGGER_ANIMATION_3 = 3;
    private static final int START_FLOAT_ANIMATION = 4;

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

    private FloatAnimator mFloatAnimator = null;
    private int scale = 0;

    private Animation biggerAnimation = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_BIGGER_ANIMATION_1:
                    rlDialogue1.setVisibility(View.VISIBLE);
                    rlDialogue1.startAnimation(biggerAnimation);
                    break;
                case START_BIGGER_ANIMATION_2:
                    rlDialogue2.setVisibility(View.VISIBLE);
                    rlDialogue2.startAnimation(biggerAnimation);
                    break;
                case START_BIGGER_ANIMATION_3:
                    rlDialogue3.setVisibility(View.VISIBLE);
                    rlDialogue3.startAnimation(biggerAnimation);
                    break;
                case START_FLOAT_ANIMATION:
                    mFloatAnimator.startFloatAnimator();
                    break;
            }
        }
    };

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

        mFloatAnimator = new FloatAnimator.Builder()
                .view(rlDialogue1)
                .view(rlDialogue2)
                .view(rlDialogue3)
                .build();

        biggerAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.scan_bigger_anim);
        biggerAnimation.setDuration(500);

        mHandler.sendEmptyMessageDelayed(START_BIGGER_ANIMATION_1,0);
        mHandler.sendEmptyMessageDelayed(START_BIGGER_ANIMATION_2,200);
        mHandler.sendEmptyMessageDelayed(START_BIGGER_ANIMATION_3,400);
        mHandler.sendEmptyMessageDelayed(START_FLOAT_ANIMATION,900);
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
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFloatAnimator.stopFloatAnimator();
        unbinder.unbind();
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
                ((CourseActivity)getContext()).switchFragment(CourseActivity.FRAGMENT_SPLIT);
                break;
        }
    }
}
