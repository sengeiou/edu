package com.ubt.alpha1e.course.merge;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.ubt.alpha1e.course.helper.PrincipleHelper;
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

@SuppressLint("ValidFragment")
public class MergeFragment extends MVPBaseFragment<MergeContract.View, MergePresenter> implements MergeContract.View {

    private static final String TAG = MergeFragment.class.getSimpleName();

    private static final int HIDE_VIEW = 1;
    private static final int GO_TO_NEXT = 2;

    private final int ANIMATOR_TIME = 500;

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

    private PrincipleHelper mHelper = null;
    private boolean hasInitRobot = false;
    private RelativeLayout.LayoutParams params = null;

    private boolean hasLostHandLeft = true;
    private boolean hasLostHandRight = true;
    private boolean hasLostLegLeft = true;
    private boolean hasLostLegRight = true;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HIDE_VIEW:
                    int viewId = msg.arg1;
                    if(viewId == R.id.iv_hand_left){
                        ivHandLeftBg.setVisibility(View.INVISIBLE);
                    }else if(viewId == R.id.iv_hand_right){
                        ivHandRightBg.setVisibility(View.INVISIBLE);
                    }else if(viewId == R.id.iv_leg_left ){
                        ivLegLeftBg.setVisibility(View.INVISIBLE);
                    }else if(viewId == R.id.iv_leg_right){
                        ivLegRightBg.setVisibility(View.INVISIBLE);
                    }
                    break;
                case GO_TO_NEXT:
                    ((CourseActivity)getActivity()).doSaveCourseProgress(1,1,3);
                    ((CourseActivity) getContext()).switchFragment(CourseActivity.FRAGMENT_FEATURE);
                    break;
            }
        }
    };

    @SuppressLint("ValidFragment")
    public MergeFragment(PrincipleHelper helper){
        super();
        mHelper = helper;
    }

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

        initData();
    }

    private void initRobot() {

        if (scale == 3.0) {
            initViewLayout(ivRobot, scale);

            initViewLayout(ivHandLeft, scale);

            initViewLayout(ivHandRight, scale);

            initViewLayout(ivLegLeft, scale);

            initViewLayout(ivLegRight, scale);

            initViewLayout(ivHandLeftBg, scale);

            initViewLayout(ivHandRightBg, scale);

            initViewLayout(ivLegLeftBg, scale);

            initViewLayout(ivLegRightBg, scale);

        }
    }

    private void initViewLayout(View view, int scale ) {

        params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        UbtLog.d(TAG, "width start : " + params.width + " height : " + params.height + " width = " + ivRobot.getWidth()
                + "  height = " + ivRobot.getHeight() + "  topMargin = " + params.topMargin + " " +  params.leftMargin + " >> " + view.getTop() + "   " + view.getLeft() + "    = " + view.getX()+"/" + view.getY());

        params.width  = view.getWidth() / 2 * scale;
        params.height = view.getHeight() / 2 * scale;
        params.topMargin = params.topMargin / 2 * scale;
        view.setLayoutParams(params);

    }

    private void initData(){
        hasLostHandLeft = true;
        hasLostHandRight = true;
        hasLostLegLeft = true;
        hasLostLegRight = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHelper.doLostPower();
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
                doMergeAll();
                mHandler.sendEmptyMessageDelayed(GO_TO_NEXT,600);
                break;
        }
    }

    private void doMergeAll(){

        float targetX,targetY;
        if(hasLostHandLeft){
            targetX = ivHandLeftBg.getX();
            targetY = ivHandLeftBg.getY();
            hasLostHandLeft = false;
            doHideView(ivHandLeft);
            doAnimator(ivHandLeft, targetX, targetY);
        }
        if(hasLostHandRight){
            targetX = ivHandRightBg.getX();
            targetY = ivHandRightBg.getY();
            hasLostHandRight = false;
            doHideView(ivHandRight);
            doAnimator(ivHandRight, targetX, targetY);
        }

        if(hasLostLegLeft){
            targetX = ivLegLeftBg.getX();
            targetY = ivLegLeftBg.getY();
            hasLostLegLeft = false;
            doHideView(ivLegLeft);
            doAnimator(ivLegLeft, targetX, targetY);
        }

        if(hasLostLegRight){
            targetX = ivLegRightBg.getX();
            targetY = ivLegRightBg.getY();
            hasLostLegRight = false;
            doHideView(ivLegRight);
            doAnimator(ivLegRight, targetX, targetY);
        }
    }

    private void doAnimator(View view,float targetX, float targetY){
        // 属性动画移动
        ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", view.getY(), targetY);
        ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", view.getX(), targetX);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(x, y);
        animatorSet.setDuration(ANIMATOR_TIME);
        animatorSet.start();
    }

    private void doHideView(View view){
        Message hideViewMsg = new Message();
        hideViewMsg.what = HIDE_VIEW;
        hideViewMsg.arg1 = view.getId();
        mHandler.sendMessageDelayed(hideViewMsg,ANIMATOR_TIME);
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        private float lastX, lastY;

        private float targetX,targetY;

        private float startX,startY;

        private float relativeTargetX,relativeTargetY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            //UbtLog.d(TAG, "event.getActionMasked() = " + event.getActionMasked());
            if(!onTouchable(view)){
                return false;
            }

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getRawX();
                    lastY = event.getRawY();

                    startX = view.getX();
                    startY = view.getY();

                    if(view.getId() == R.id.iv_hand_left){
                        relativeTargetX = ivHandLeftBg.getX();
                        relativeTargetY = ivHandLeftBg.getY();
                    }else if(view.getId() == R.id.iv_hand_right){
                        relativeTargetX = ivHandRightBg.getX();
                        relativeTargetY = ivHandRightBg.getY();
                    }else if(view.getId() == R.id.iv_leg_left ){
                        relativeTargetX = ivLegLeftBg.getX();
                        relativeTargetY = ivLegLeftBg.getY();
                    }else if(view.getId() == R.id.iv_leg_right){
                        relativeTargetX = ivLegRightBg.getX();
                        relativeTargetY = ivLegRightBg.getY();
                    }

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

                    //当移动位置绝对值超出背景图时，变白色
                    if(Math.abs(nextX - relativeTargetX) > view.getWidth() || Math.abs(nextY - relativeTargetY) > view.getHeight()  ){
                        if(view.getId() == R.id.iv_hand_left){
                            ivHandLeftBg.setBackgroundResource(R.drawable.icon_principle_leftarm_white);
                        }else if(view.getId() == R.id.iv_hand_right){
                            ivHandRightBg.setBackgroundResource(R.drawable.icon_principle_rightarm_white);
                        }else if(view.getId() == R.id.iv_leg_left ){
                            ivLegLeftBg.setBackgroundResource(R.drawable.icon_principle_leftleg_white);
                        }else if(view.getId() == R.id.iv_leg_right){
                            ivLegRightBg.setBackgroundResource(R.drawable.icon_principle_rightleg_white);
                        }
                    }else {
                        if(view.getId() == R.id.iv_hand_left){
                            ivHandLeftBg.setBackgroundResource(R.drawable.icon_principle_leftarm_yellow);
                        }else if(view.getId() == R.id.iv_hand_right){
                            ivHandRightBg.setBackgroundResource(R.drawable.icon_principle_rightarm_yellow);
                        }else if(view.getId() == R.id.iv_leg_left ){
                            ivLegLeftBg.setBackgroundResource(R.drawable.icon_principle_leftleg_yellow);
                        }else if(view.getId() == R.id.iv_leg_right){
                            ivLegRightBg.setBackgroundResource(R.drawable.icon_principle_rightleg_yellow);
                        }
                    }

                }
                return false;
                case MotionEvent.ACTION_UP: {

                    if(Math.abs(view.getX() - relativeTargetX) > view.getWidth() || Math.abs(view.getY() - relativeTargetY) > view.getHeight()  ){
                        targetX = startX;
                        targetY = startY;
                    }else {
                        targetX = relativeTargetX;
                        targetY = relativeTargetY;

                        if(view.getId() == R.id.iv_hand_left){
                            hasLostHandLeft = false;
                            mHelper.doOnLeftHand();
                        }else if(view.getId() == R.id.iv_hand_right){
                            hasLostHandRight = false;
                            mHelper.doOnRightHand();
                        }else if(view.getId() == R.id.iv_leg_left ){
                            hasLostLegLeft = false;
                            mHelper.doOnLeftFoot();
                        }else if(view.getId() == R.id.iv_leg_right){
                            hasLostLegRight = false;
                            mHelper.doOnRightFoot();
                        }

                        Message hideViewMsg = new Message();
                        hideViewMsg.what = HIDE_VIEW;
                        hideViewMsg.arg1 = view.getId();
                        mHandler.sendMessageDelayed(hideViewMsg,ANIMATOR_TIME);
                    }

                    // 属性动画移动
                    ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", view.getY(), targetY);
                    ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", view.getX(), targetX);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(x, y);
                    animatorSet.setDuration(ANIMATOR_TIME);
                    animatorSet.start();

                    lastX = event.getRawX();
                    lastY = event.getRawY();
                }
                return false;
            }
            return false;
        }

        private boolean onTouchable(View view){
            boolean touchable = true;
            if(view.getId() == R.id.iv_hand_left && !hasLostHandLeft){
                touchable = false;
            }else if(view.getId() == R.id.iv_hand_right && !hasLostHandRight){
                touchable = false;
            }else if(view.getId() == R.id.iv_leg_left && !hasLostLegLeft){
                touchable = false;
            }else if(view.getId() == R.id.iv_leg_right && !hasLostLegRight){
                touchable = false;
            }
            return touchable;
        }
    };
}
