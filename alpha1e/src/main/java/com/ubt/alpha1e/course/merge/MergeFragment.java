package com.ubt.alpha1e.course.merge;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.course.CourseActivity;
import com.ubt.alpha1e.course.split.SplitFragment;
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

public class MergeFragment extends MVPBaseFragment<MergeContract.View, MergePresenter> implements MergeContract.View {

    private static final String TAG = MergeFragment.class.getSimpleName();

    @BindView(R.id.tv_next)
    TextView tvNext;
    Unbinder unbinder;
    @BindView(R.id.iv_robot)
    ImageView ivRobot;
    @BindView(R.id.iv_hand_left)
    ImageView ivHandLeft;
    @BindView(R.id.iv_hand_right)
    ImageView ivHandRight;
    @BindView(R.id.iv_leg_left)
    ImageView ivLegLeft;
    @BindView(R.id.iv_leg_right)
    ImageView ivLegRight;
    @BindView(R.id.rl_robot)
    RelativeLayout rlRobot;
    @BindView(R.id.iv_hand_left_bg)
    ImageView ivHandLeftBg;
    @BindView(R.id.iv_hand_right_bg)
    ImageView ivHandRightBg;
    @BindView(R.id.iv_leg_left_bg)
    ImageView ivLegLeftBg;
    @BindView(R.id.iv_leg_right_bg)
    ImageView ivLegRightBg;

    private int containerWidth;
    private int containerHeight;
    private int scale = 0;

    private boolean hasInitRobot = false;


    @Override
    protected void initUI() {
        scale = (int) this.getResources().getDisplayMetrics().density;
        containerWidth = this.getResources().getDisplayMetrics().widthPixels;
        containerHeight = this.getResources().getDisplayMetrics().heightPixels;

        UbtLog.d(TAG, "scale = " + scale
                + "  width = " + this.getResources().getDisplayMetrics().widthPixels
                + "  height = " + this.getResources().getDisplayMetrics().heightPixels
                + "  densityDpi = " + this.getResources().getDisplayMetrics().densityDpi);

        ivHandLeft.setOnTouchListener(onTouchListener);
        ivHandRight.setOnTouchListener(onTouchListener);
        ivLegLeft.setOnTouchListener(onTouchListener);
        ivLegRight.setOnTouchListener(onTouchListener);

        rlRobot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                rlRobot.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!hasInitRobot && ivRobot.getHeight() > 0) {
                            hasInitRobot = true;
                            initRobot();
                        }
                    }
                });
            }
        });
    }

    private void initRobot() {

        if (scale == 3.0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivRobot.getLayoutParams();

            UbtLog.d(TAG, "width : " + params.width + " height : " + params.height + " width = " + ivRobot.getWidth() + "  height = " + ivRobot.getHeight() + "  topMargin = " + params.topMargin);
            params.width = ivRobot.getWidth() / 2 * 3;
            params.height = ivRobot.getHeight() / 2 * 3;
            ivRobot.setLayoutParams(params);
            UbtLog.d(TAG, "ivRobot:" + ivRobot.getWidth() + "/" + ivRobot.getHeight());

            UbtLog.d(TAG, "ivHandLeftBg start :" + ivHandLeftBg.getWidth() + "/" + ivHandLeftBg.getHeight());
            params = (RelativeLayout.LayoutParams) ivHandLeft.getLayoutParams();
            params.width = ivHandLeft.getWidth() / 2 * 3;
            params.height = ivHandLeft.getHeight() / 2 * 3;
            params.topMargin = 370;
            ivHandLeft.setLayoutParams(params);

            UbtLog.d(TAG, "ivHandLeft:" + ivHandLeft.getWidth() + "/" + ivHandLeft.getHeight());

            params = (RelativeLayout.LayoutParams) ivHandRight.getLayoutParams();
            params.width = ivHandRight.getWidth() / 2 * 3;
            params.height = ivHandRight.getHeight() / 2 * 3;
            params.topMargin = 370;
            ivHandRight.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) ivLegLeft.getLayoutParams();
            params.width = ivLegLeft.getWidth() / 2 * 3;
            params.height = ivLegLeft.getHeight() / 2 * 3;
            params.topMargin = 51 * (-1);
            ivLegLeft.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) ivLegRight.getLayoutParams();
            params.width = ivLegLeft.getWidth() / 2 * 3;
            params.height = ivLegLeft.getHeight() / 2 * 3;
            params.topMargin = 51 * (-1);
            ivLegRight.setLayoutParams(params);

            //bg
            params = (RelativeLayout.LayoutParams) ivHandLeftBg.getLayoutParams();
            params.width = ivHandLeftBg.getWidth() / 2 * 3;
            params.height = ivHandLeftBg.getHeight() / 2 * 3;
            params.topMargin = 370;
            ivHandLeftBg.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) ivHandRightBg.getLayoutParams();
            params.width = ivHandRightBg.getWidth() / 2 * 3;
            params.height = ivHandRightBg.getHeight() / 2 * 3;
            params.topMargin = 370;
            ivHandRightBg.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) ivLegLeftBg.getLayoutParams();
            params.width = ivLegLeftBg.getWidth() / 2 * 3;
            params.height = ivLegLeftBg.getHeight() / 2 * 3;
            params.topMargin = 51 * (-1);
            ivLegLeftBg.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) ivLegRightBg.getLayoutParams();
            params.width = ivLegRightBg.getWidth() / 2 * 3;
            params.height = ivLegRightBg.getHeight() / 2 * 3;
            params.topMargin = 51 * (-1);
            ivLegRightBg.setLayoutParams(params);

            UbtLog.d(TAG, "ivHandLeftBg end :" + ivHandLeftBg.getWidth() + "/" + ivHandLeftBg.getHeight());

            //initImagePosition(ivHandLeft);
            //initImagePosition(ivHandRight);
            //initImagePosition(ivLegLeft);
            //initImagePosition(ivLegRight);
        }
    }

    private void initImagePosition(ImageView view){
        int targetX = 0 ;
        int targetY = 0;
        if(view.getId() == R.id.iv_hand_left){
            targetX = containerWidth/2 - ivRobot.getWidth()/2 - ivHandLeftBg.getWidth() - ivHandLeftBg.getWidth() - SizeUtils.dip2px(getContext(),30);
            targetY = containerHeight/2 - ivHandLeftBg.getHeight()/2;
            UbtLog.d(TAG,"targetX = " + targetX + " targetY = " + targetY + " width = " + ivHandLeftBg.getWidth() + " height = " + ivHandLeftBg.getHeight()+ "   view = " + view);
        }else if(view.getId() == R.id.iv_leg_left ){
            targetX = containerWidth/2 - ivRobot.getWidth()/2 - ivHandLeftBg.getWidth()*2 - ivLegLeft.getWidth()- SizeUtils.dip2px(getContext(),30)*2;
            targetY = containerHeight/2 - view.getHeight()/2;
        }else if(view.getId() == R.id.iv_hand_right){
            targetX = containerWidth/2 + ivRobot.getWidth()/2 + ivHandRightBg.getWidth() + SizeUtils.dip2px(getContext(),30);
            targetY = containerHeight/2 - view.getHeight()/2;
        }else if(view.getId() == R.id.iv_leg_right){
            targetX = containerWidth/2 + ivRobot.getWidth()/2 + ivHandRightBg.getWidth()*2 + SizeUtils.dip2px(getContext(),30)*2;
            targetY = containerHeight/2 - view.getHeight()/2;
        }

        /*// 属性动画移动
        ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", view.getY(), targetX);
        ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", view.getX(), targetY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(x, y);
        animatorSet.setDuration(0);
        animatorSet.start();*/


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        //params.setMargins(targetX,targetY, targetX + view.getWidth(), targetY + view.getHeight());

        //params.topMargin = targetY;
        //params.leftMargin = targetX;
        view.setLayoutParams(params);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_robot_merge;
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

    @OnClick({R.id.iv_back, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ((CourseActivity) getContext()).switchFragment(CourseActivity.FRAGMENT_SPLIT);
                break;
            case R.id.tv_next:
                ((CourseActivity) getContext()).switchFragment(CourseActivity.FRAGMENT_FEATURE);
                break;
        }
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        private float lastX, lastY;

        private float targetX,targetY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            //UbtLog.d(TAG, "event.getActionMasked() = " + event.getActionMasked());
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    //  不要直接用getX和getY,这两个获取的数据已经是经过处理的,容易出现图片抖动的情况
                    float distanceX = lastX - event.getRawX();
                    float distanceY = lastY - event.getRawY();

                    float nextY = view.getY() - distanceY;
                    float nextX = view.getX() - distanceX;

                    // 不能移出屏幕
                    if (nextY < 0) {
                        nextY = 0;
                    } else if (nextY > containerHeight - view.getHeight()) {
                        nextY = containerHeight - view.getHeight();
                    }
                    if (nextX < 0) {
                        nextX = 0;
                    } else if (nextX > containerWidth - view.getWidth()) {
                        nextX = containerWidth - view.getWidth();
                    }

                    // 属性动画移动
                    ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", view.getY(), nextY);
                    ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", view.getX(), nextX);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(x, y);
                    animatorSet.setDuration(0);
                    animatorSet.start();

                    lastX = event.getRawX();
                    lastY = event.getRawY();
                }
                return false;
                case MotionEvent.ACTION_UP: {

                    if(view.getId() == R.id.iv_hand_left){
                        targetX = ivHandLeftBg.getX();
                        targetY = ivHandLeftBg.getY();
                    }else if(view.getId() == R.id.iv_leg_left ){
                        targetX = ivLegLeftBg.getX();
                        targetY = ivLegLeftBg.getY();
                    }else if(view.getId() == R.id.iv_hand_right){
                        targetX = ivHandRightBg.getX();
                        targetY = ivHandRightBg.getY();
                    }else if(view.getId() == R.id.iv_leg_right){
                        targetX = ivLegRightBg.getX();
                        targetY = ivLegRightBg.getY();
                    }

                    // 属性动画移动
                    ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", view.getY(), targetY);
                    ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", view.getX(), targetX);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(x, y);
                    animatorSet.setDuration(500);
                    animatorSet.start();

                    lastX = event.getRawX();
                    lastY = event.getRawY();
                }
                return false;
            }
            return false;
        }
    };
}
