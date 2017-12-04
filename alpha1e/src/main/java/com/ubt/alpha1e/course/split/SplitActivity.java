package com.ubt.alpha1e.course.split;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.course.event.PrincipleEvent;
import com.ubt.alpha1e.course.helper.PrincipleHelper;
import com.ubt.alpha1e.course.merge.MergeActivity;
import com.ubt.alpha1e.course.principle.PrincipleActivity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.SizeUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SplitActivity extends MVPBaseActivity<SplitContract.View, SplitPresenter> implements SplitContract.View {

    private static final String TAG = SplitActivity.class.getSimpleName();

    private static final int HIDE_VIEW = 1;
    private static final int GO_TO_NEXT = 2;
    private static final int SHOW_DIALOG = 3;
    private static final int HIDE_DIALOG = 4;
    private static final int TAP_HEAD = 5;

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
                    doSaveCourseProgress(1,1,2);
                    MergeActivity.launchActivity(SplitActivity.this,true);
                    break;
                case SHOW_DIALOG:
                    ((PrincipleHelper)mHelper).playFile("拆卸.hts");
                    tvMsgShow.setText(getStringResources("ui_principle_lost_engine_tips"));
                    showView(tvMsgShow, true, biggerLeftBottomAnim);
                    break;
                case HIDE_DIALOG:
                    showView(tvMsgShow, false, smallerLeftBottomAnim);
                    break;
                case TAP_HEAD:
                    //拍头退出课程模式
                    ToastUtils.showShort(getStringResources("ui_setting_principle_tap_head"));
                    finish();
                    break;
            }
        }
    };

    public static void launchActivity(Activity activity, boolean isFinish) {
        Intent intent = new Intent(activity, SplitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        if(isFinish){
            activity.finish();
        }
    }

    @Subscribe
    public void onEventPrinciple(PrincipleEvent event) {

        if(event.getEvent() == PrincipleEvent.Event.PLAY_ACTION_FINISH){
            mHandler.sendEmptyMessage(HIDE_DIALOG);
        }else if(event.getEvent() == PrincipleEvent.Event.TAP_HEAD){
            mHandler.sendEmptyMessage(TAP_HEAD);
        }
    }

    @Override
    protected void initUI() {
        scale = (int) this.getResources().getDisplayMetrics().density;
        int screenWidth = SizeUtils.getScreenWidth(this);
        int screenHeight = SizeUtils.getScreenHeight(this);
        UbtLog.d(TAG,"screenWidth = " + screenWidth + "screenHeight = " + screenHeight + " scale = " + scale);
        if((screenWidth >= 1920 && scale == 2) || (screenHeight >= 1080 && scale == 2)){
            scale = 3;
        }

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
                UbtLog.d(TAG,"rlRobot = " + rlRobot + " mHandler = " + mHandler);
                if(rlRobot != null){
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
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_robot_split;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mHelper = new PrincipleHelper(this);
        initUI();
    }

    @OnClick({R.id.iv_back, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_next:
                doAplitAll();
                mHandler.sendEmptyMessageDelayed(GO_TO_NEXT, 600);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((PrincipleHelper)mHelper).doEnterCourse((byte)1);
    }

    @Override
    public void onBackPressed() {
        PrincipleActivity.launchActivity(this,true);
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

                            ((PrincipleHelper)mHelper).doLostLeftHand();
                        }else if(view.getId() == R.id.iv_hand_right){
                            targetX = containerWidth/2 + ivRobot.getWidth()/2 + ivHandRightBg.getWidth() + SizeUtils.dip2px(getContext(),30);
                            targetY = containerHeight/2 - view.getHeight()/2;
                            ivHandRightBg.setBackgroundResource(R.drawable.icon_principle_rightarm_white);
                            hasLostHandRight = true;
                            ((PrincipleHelper)mHelper).doLostRightHand();
                        }else if(view.getId() == R.id.iv_leg_left ){
                            targetX = containerWidth/2 - ivRobot.getWidth()/2 - ivHandLeftBg.getWidth()*2 - ivLegLeft.getWidth()- SizeUtils.dip2px(getContext(),30)*2;
                            targetY = containerHeight/2 - view.getHeight()/2;
                            ivLegLeftBg.setBackgroundResource(R.drawable.icon_principle_leftleg_white);
                            hasLostLegLeft = true;
                            if(hasLostLegRight){
                                ((PrincipleHelper)mHelper).doLostLeftFoot();
                                ((PrincipleHelper)mHelper).doLostRightFoot();
                            }
                        }else if(view.getId() == R.id.iv_leg_right){
                            targetX = containerWidth/2 + ivRobot.getWidth()/2 + ivHandRightBg.getWidth()*2 + SizeUtils.dip2px(getContext(),30)*2;
                            targetY = containerHeight/2 - view.getHeight()/2;
                            ivLegRightBg.setBackgroundResource(R.drawable.icon_principle_rightleg_white);
                            hasLostLegRight = true;
                            if(hasLostLegLeft){
                                ((PrincipleHelper)mHelper).doLostLeftFoot();
                                ((PrincipleHelper)mHelper).doLostRightFoot();
                            }
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

    /**
     * 获取课程进度
     * @param type
     */
    public void doGetCourseProgress(int type){
        mPresenter.doGetCourseProgress(type);
    }

    /**
     * 保存课程进度
     * @param type
     * @param courseOne
     * @param progressOne
     */
    public void doSaveCourseProgress(int type, int courseOne, int progressOne){
        mPresenter.doSaveCourseProgress(type,courseOne,progressOne);
    }

    @Override
    public void onSaveCourseProgress(boolean isSuccess, String msg) {

    }

    @Override
    public void onGetCourseProgress(boolean isSuccess, String msg, int progress) {

    }
}
