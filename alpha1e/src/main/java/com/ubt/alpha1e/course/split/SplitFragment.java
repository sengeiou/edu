package com.ubt.alpha1e.course.split;


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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.course.CourseActivity;
import com.ubt.alpha1e.course.event.PrincipleEvent;
import com.ubt.alpha1e.course.feature.FeatureFragment;
import com.ubt.alpha1e.course.helper.PrincipleHelper;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
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
public class SplitFragment extends MVPBaseFragment<SplitContract.View, SplitPresenter> implements SplitContract.View {

    private static final String TAG = SplitFragment.class.getSimpleName();

    private static final int HIDE_VIEW = 1;
    private static final int GO_TO_NEXT = 2;
    private static final int SHOW_DIALOG = 3;
    private static final int HIDE_DIALOG = 4;

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
    @BindView(R.id.tv_msg_show)
    TextView tvMsgShow;

    private int containerWidth;
    private int containerHeight;
    private int scale = 0;

    private boolean hasInitRobot = false;
    private RelativeLayout.LayoutParams params = null;
    private Animation smallerLeftBottomAnim = null;
    private Animation biggerLeftBottomAnim = null;

    private boolean hasLostHandLeft = false;
    private boolean hasLostHandRight = false;
    private boolean hasLostLegLeft = false;
    private boolean hasLostLegRight = false;

    private PrincipleHelper mHelper = null;

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
                    ((CourseActivity)getActivity()).doSaveCourseProgress(1,1,2);
                    ((CourseActivity) getContext()).switchFragment(CourseActivity.FRAGMENT_MERGE);
                    break;
                case SHOW_DIALOG:
                    mHelper.playFile("拆卸.hts");
                    tvMsgShow.setText(getStringRes("ui_principle_lost_engine_tips"));
                    showView(tvMsgShow, true, biggerLeftBottomAnim);
                    break;
                case HIDE_DIALOG:
                    showView(tvMsgShow, false, smallerLeftBottomAnim);
                    break;
            }
        }
    };

    @Subscribe
    public void onEventPrinciple(PrincipleEvent event) {
        if(!(((CourseActivity)getActivity()).getCurrentFragment() instanceof SplitFragment) ){
            return;
        }

        if(event.getEvent() == PrincipleEvent.Event.PLAY_SOUND){
            int status = event.getStatus();
            mHandler.sendEmptyMessage(HIDE_DIALOG);
        }
    }

    @SuppressLint("ValidFragment")
    public SplitFragment(PrincipleHelper helper){
        super();
        mHelper = helper;
    }

    @Override
    protected void initUI() {

        scale = (int) this.getResources().getDisplayMetrics().density;
        containerWidth = this.getResources().getDisplayMetrics().widthPixels;
        containerHeight = this.getResources().getDisplayMetrics().heightPixels;

        biggerLeftBottomAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_bigger_anim_left_bottom);
        smallerLeftBottomAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_smaller_anim_left_bottom);

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

        if (scale >= 3.0) {

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

        mHandler.sendEmptyMessage(SHOW_DIALOG);
    }

    private void initViewLayout(View view, int scale ) {

        params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        UbtLog.d(TAG, "width start : " + params.width + " height : " + params.height + " width = " + ivRobot.getWidth()
                + "  height = " + ivRobot.getHeight() + "  topMargin = " + params.topMargin + " " +  params.leftMargin + " >> " + view.getTop() + "   " + view.getLeft() + "    = " + view.getX()+"/" + view.getY());

        params.width  = view.getWidth() / 2 * scale;
        params.height = view.getHeight() / 2 * scale;
        params.topMargin = params.topMargin / 2 * scale;
        view.setLayoutParams(params);

        /*int newLeft = view.getLeft() - (params.width  - view.getWidth()) / 2;
        int newTop  = view.getTop() -  (params.height - view.getHeight())/ 2;
        view.layout(newLeft, newTop, newLeft + params.width, newTop + params.height);

        UbtLog.d(TAG, "width end:: " + params.width + " height : " + params.height + " width = " + ivRobot.getWidth()
                + "  height = " + ivRobot.getHeight() + "  topMargin = " + params.topMargin + " " +  params.leftMargin + " >> " + view.getTop() + "   " + view.getLeft() + "    = " + view.getX()+"/" + view.getY());*/
    }

    private void initData(){
        hasLostHandLeft = false;
        hasLostHandRight = false;
        hasLostLegLeft = false;
        hasLostLegRight = false;
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_robot_split;
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_back, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                int enterPropress = ((CourseActivity) getContext()).getEnterPropress();
                if(enterPropress > 0){
                    ((CourseActivity) getContext()).finish();
                }else {
                    ((CourseActivity) getContext()).switchFragment(CourseActivity.FRAGMENT_PRINCIPLE);
                }
                break;
            case R.id.tv_next:
                doAplitAll();
                mHandler.sendEmptyMessageDelayed(GO_TO_NEXT, 600);
                break;
        }
    }

    private void showView(View view, boolean isShow, Animation anim) {
        if (view.getVisibility() == View.VISIBLE && isShow) {
            return;
        }

        if (view.getVisibility() != View.VISIBLE && !isShow) {
            return;
        }

        if (isShow) {

            if(tvMsgShow.getVisibility() == View.VISIBLE){
                tvMsgShow.setVisibility(View.GONE);
            }

            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        if (anim != null) {
            view.startAnimation(anim);
        }
    }

    private void doAplitAll(){

        float targetX,targetY;
        if(!hasLostHandLeft){
            targetX = containerWidth/2 - ivRobot.getWidth()/2 - ivHandLeftBg.getWidth() - ivHandLeft.getWidth() - SizeUtils.dip2px(getContext(),30);
            targetY = containerHeight/2 - ivHandLeft.getHeight()/2;
            ivHandLeftBg.setBackgroundResource(R.drawable.icon_principle_leftarm_white);
            ivHandLeftBg.setVisibility(View.VISIBLE);
            hasLostHandLeft = true;
            doAnimator(ivHandLeft, targetX, targetY);
        }
        if(!hasLostHandRight){
            targetX = containerWidth/2 + ivRobot.getWidth()/2 + ivHandRight.getWidth() + SizeUtils.dip2px(getContext(),30);
            targetY = containerHeight/2 - ivHandRight.getHeight()/2;
            ivHandRightBg.setBackgroundResource(R.drawable.icon_principle_rightarm_white);
            ivHandRightBg.setVisibility(View.VISIBLE);
            hasLostHandRight = true;
            doAnimator(ivHandRight, targetX, targetY);
        }

        if(!hasLostLegLeft){
            targetX = containerWidth/2 - ivRobot.getWidth()/2 - ivHandLeftBg.getWidth()*2 - ivLegLeft.getWidth()- SizeUtils.dip2px(getContext(),30)*2;
            targetY = containerHeight/2 - ivLegLeft.getHeight()/2;
            ivLegLeftBg.setBackgroundResource(R.drawable.icon_principle_leftleg_white);
            ivLegLeftBg.setVisibility(View.VISIBLE);
            hasLostLegLeft = true;
            doAnimator(ivLegLeft, targetX, targetY);
        }

        if(!hasLostLegRight){
            targetX = containerWidth/2 + ivRobot.getWidth()/2 + ivHandRightBg.getWidth()*2 + SizeUtils.dip2px(getContext(),30)*2;
            targetY = containerHeight/2 - ivLegRight.getHeight()/2;
            ivLegRightBg.setBackgroundResource(R.drawable.icon_principle_rightleg_white);
            ivLegRightBg.setVisibility(View.VISIBLE);
            hasLostLegRight = true;
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

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        private float lastX, lastY;

        private float targetX,targetY;

        private float startX,startY;

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
                        ivHandLeftBg.setBackgroundResource(R.drawable.icon_principle_leftarm_yellow);
                        ivHandLeftBg.setVisibility(View.VISIBLE);
                    }else if(view.getId() == R.id.iv_hand_right){
                        ivHandRightBg.setBackgroundResource(R.drawable.icon_principle_rightarm_yellow);
                        ivHandRightBg.setVisibility(View.VISIBLE);
                    }else if(view.getId() == R.id.iv_leg_left){
                        ivLegLeftBg.setBackgroundResource(R.drawable.icon_principle_leftleg_yellow);
                        ivLegLeftBg.setVisibility(View.VISIBLE);
                    }else if(view.getId() == R.id.iv_leg_right){
                        ivLegRightBg.setBackgroundResource(R.drawable.icon_principle_rightleg_yellow);
                        ivLegRightBg.setVisibility(View.VISIBLE);
                    }
                    UbtLog.d(TAG,"targetX = " + targetX + " targetY = " + targetY + " width = " + ivHandLeftBg.getWidth() + " height = " + ivHandLeftBg.getHeight()+ "   view = " + view.getX()+"/"+view.getY());
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
                    if(Math.abs(nextX - startX) > view.getWidth() || Math.abs(nextY - startY) > view.getHeight()  ){
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

                    //UbtLog.d(TAG,"ivHandLeftBg = " + ivHandLeftBg.getX() + "/" + ivHandLeftBg.getY() + "    ivHandLeft = " + ivHandLeft.getX()+"/"+ivHandLeft.getY() + "    last = " + lastX+"/" +lastY);

                }
                return false;
                case MotionEvent.ACTION_UP: {

                    //当移动位置绝对值移出原来位置时，执行掉点，否则不掉电
                    if(Math.abs(view.getX() - startX) > view.getWidth() || Math.abs(view.getY() - startY) > view.getHeight()  ){
                        if(view.getId() == R.id.iv_hand_left){
                            targetX = containerWidth/2 - ivRobot.getWidth()/2 - ivHandLeftBg.getWidth() - ivHandLeft.getWidth() - SizeUtils.dip2px(getContext(),30);
                            targetY = containerHeight/2 - view.getHeight()/2;
                            ivHandLeftBg.setBackgroundResource(R.drawable.icon_principle_leftarm_white);
                            hasLostHandLeft = true;

                            mHelper.doLostLeftHand();
                        }else if(view.getId() == R.id.iv_hand_right){
                            targetX = containerWidth/2 + ivRobot.getWidth()/2 + ivHandRightBg.getWidth() + SizeUtils.dip2px(getContext(),30);
                            targetY = containerHeight/2 - view.getHeight()/2;
                            ivHandRightBg.setBackgroundResource(R.drawable.icon_principle_rightarm_white);
                            hasLostHandRight = true;
                            mHelper.doLostRightHand();
                        }else if(view.getId() == R.id.iv_leg_left ){
                            targetX = containerWidth/2 - ivRobot.getWidth()/2 - ivHandLeftBg.getWidth()*2 - ivLegLeft.getWidth()- SizeUtils.dip2px(getContext(),30)*2;
                            targetY = containerHeight/2 - view.getHeight()/2;
                            ivLegLeftBg.setBackgroundResource(R.drawable.icon_principle_leftleg_white);
                            hasLostLegLeft = true;
                            mHelper.doLostLeftFoot();
                        }else if(view.getId() == R.id.iv_leg_right){
                            targetX = containerWidth/2 + ivRobot.getWidth()/2 + ivHandRightBg.getWidth()*2 + SizeUtils.dip2px(getContext(),30)*2;
                            targetY = containerHeight/2 - view.getHeight()/2;
                            ivLegRightBg.setBackgroundResource(R.drawable.icon_principle_rightleg_white);
                            hasLostLegRight = true;
                            mHelper.doLostRightFoot();
                        }
                    }else {
                        targetX = startX;
                        targetY = startY;

                        Message hideViewMsg = new Message();
                        hideViewMsg.what = HIDE_VIEW;
                        hideViewMsg.arg1 = view.getId();
                        mHandler.sendMessageDelayed(hideViewMsg,ANIMATOR_TIME);
                    }

                    UbtLog.d(TAG,"targetX = " + targetX + " targetY = " + targetY + " width = " + ivHandLeftBg.getWidth() + " height = " + ivHandLeftBg.getHeight()+ "   view = " + view);

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
            if(view.getId() == R.id.iv_hand_left && hasLostHandLeft){
                touchable = false;
            }else if(view.getId() == R.id.iv_hand_right && hasLostHandRight){
                touchable = false;
            }else if(view.getId() == R.id.iv_leg_left && hasLostLegLeft){
                touchable = false;
            }else if(view.getId() == R.id.iv_leg_right && hasLostLegRight){
                touchable = false;
            }
            return touchable;
        }
    };
}
