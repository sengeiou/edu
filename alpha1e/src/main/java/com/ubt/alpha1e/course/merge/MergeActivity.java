package com.ubt.alpha1e.course.merge;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
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
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.course.event.PrincipleEvent;
import com.ubt.alpha1e.course.feature.FeatureActivity;
import com.ubt.alpha1e.course.helper.PrincipleHelper;
import com.ubt.alpha1e.course.principle.PrincipleActivity;
import com.ubt.alpha1e.course.split.SplitActivity;
import com.ubt.alpha1e.maincourse.main.MainCourseActivity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
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

public class MergeActivity extends MVPBaseActivity<MergeContract.View, MergePresenter> implements MergeContract.View {

    private static final String TAG = MergeActivity.class.getSimpleName();

    private static final int HIDE_VIEW = 1;
    private static final int GO_TO_NEXT = 2;
    private static final int SHOW_DIALOG = 3;
    private static final int HIDE_DIALOG = 4;
    private static final int TAP_HEAD = 5;
    private static final int SHOW_NEXT_OVER_TIME = 6;
    private static final int BLUETOOTH_DISCONNECT = 7;

    private final int ANIMATOR_TIME = 500;
    private final int OVER_TIME = (15 + 10) * 1000;//(15S音频+ 10S操作)超时

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

    private boolean hasLostHandLeft = true;
    private boolean hasLostHandRight = true;
    private boolean hasLostLegLeft = true;
    private boolean hasLostLegRight = true;

    private boolean hasLostRobot = false;
    private boolean hasPlaySoundAudioFinish = false;
    private boolean isGoingNext = false;



    private ConfirmDialog mTapHeadDialog = null;

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
                    doSaveCourseProgress(1,1,3);
                    FeatureActivity.launchActivity(MergeActivity.this,true);
                    break;
                case SHOW_DIALOG:
                    mHandler.sendEmptyMessageDelayed(SHOW_NEXT_OVER_TIME, OVER_TIME);
                    ((PrincipleHelper)mHelper).playSoundAudio("{\"filename\":\"组装.mp3\",\"playcount\":1}");
                    ((PrincipleHelper)mHelper).playFile("蹲下.hts");
                    tvMsgShow.setText(getStringResources("ui_principle_on_engine_tips"));
                    showView(tvMsgShow, true, biggerLeftBottomAnim);
                    break;
                case HIDE_DIALOG:
                    hasPlaySoundAudioFinish = true;
                    showView(tvMsgShow, false, smallerLeftBottomAnim);
                    break;
                case TAP_HEAD:
                    //拍头退出课程模式
                    showTapHeadDialog();
                    break;
                case SHOW_NEXT_OVER_TIME:
                    setViewEnable(tvNext,true,1f);
                    break;
                case BLUETOOTH_DISCONNECT:
                    ToastUtils.showShort(getStringResources("ui_robot_disconnect"));
                    MainCourseActivity.finishByMySelf();
                    finish();
                    break;
            }
        }
    };

    public static void launchActivity(Activity activity, boolean isFinish) {
        Intent intent = new Intent(activity, MergeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        if(isFinish){
            activity.finish();
        }
    }

    @Override
    protected void initUI() {
        setViewEnable(tvNext, false, 0.5f);

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
    }

    private void initData(){
        hasLostHandLeft = true;
        hasLostHandRight = true;
        hasLostLegLeft = true;
        hasLostLegRight = true;
    }



    @Subscribe
    public void onEventPrinciple(PrincipleEvent event) {
        if(event.getEvent() == PrincipleEvent.Event.PLAY_SOUND){
            int status = event.getStatus();
            if(status == 1){
                mHandler.sendEmptyMessage(HIDE_DIALOG);
            }
        }else if(event.getEvent() == PrincipleEvent.Event.TAP_HEAD){
            mHandler.sendEmptyMessage(TAP_HEAD);
        }if(event.getEvent() == PrincipleEvent.Event.PLAY_ACTION_FINISH){
            UbtLog.d(TAG,"--PLAY_ACTION_FINISH--");
            hasLostRobot = true;
            ((PrincipleHelper)mHelper).doLostPower();
        }else if(event.getEvent() == PrincipleEvent.Event.DISCONNECTED){
            mHandler.sendEmptyMessage(BLUETOOTH_DISCONNECT);
        }
    }

    private void showTapHeadDialog(){
        if(mTapHeadDialog == null){
            mTapHeadDialog = new ConfirmDialog(getContext()).builder()
                    .setMsg(getStringResources("ui_course_principle_exit_tip"))
                    .setCancelable(false)
                    .setPositiveButton(getStringResources("ui_common_yes"), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((PrincipleHelper) mHelper).doInit();
                            ((PrincipleHelper) mHelper).doEnterCourse((byte) 0);
                            MainCourseActivity.finishByMySelf();
                            MergeActivity.this.finish();
                            MergeActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
                        }
                    }).setNegativeButton(getStringResources("ui_common_no"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
        }
        if(!mTapHeadDialog.isShowing()){
            mTapHeadDialog.show();
        }
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_robot_merge;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mHelper = new PrincipleHelper(this);
        initUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((PrincipleHelper)mHelper).stopPlayAudio();
    }

    @Override
    protected void onDestroy() {

        if(mHandler.hasMessages(SHOW_NEXT_OVER_TIME)){
            mHandler.removeMessages(SHOW_NEXT_OVER_TIME);
        }
        if(mHandler.hasMessages(HIDE_VIEW)){
            mHandler.removeMessages(HIDE_VIEW);
        }

        if(mHandler.hasMessages(GO_TO_NEXT)){
            mHandler.removeMessages(GO_TO_NEXT);
        }
        super.onDestroy();
    }

    @OnClick({R.id.iv_back, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_next:
                if(isGoingNext){
                    //避免重复点击
                    return;
                }
                isGoingNext = true;
                doMergeAll();
                mHandler.sendEmptyMessageDelayed(GO_TO_NEXT,600);
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
        if(SPUtils.getInstance().getInt(Constant.PRINCIPLE_ENTER_PROGRESS, 0) > 1 ){
            ((PrincipleHelper) mHelper).doInit();
            ((PrincipleHelper) mHelper).doEnterCourse((byte) 0);
            this.finish();
            this.overridePendingTransition(0, R.anim.activity_close_down_up);
        }else {
            SplitActivity.launchActivity(this,true);
        }
    }

    private boolean hasLearnFinish(){
        if(!hasLostHandLeft && !hasLostHandRight && !hasLostLegLeft && !hasLostLegRight){
            return true;
        }else {
            return false;
        }
    }

    private void setViewEnable(View mView, boolean enable, float alpha) {
        mView.setClickable(enable);
        if (enable) {
            mView.setAlpha(1f);
        } else {
            mView.setAlpha(alpha);
        }
    }

    /**
     * 组装全部
     */
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

    /**
     * 显示View
     * @param view 操作视图
     * @param isShow 是否显示/隐藏
     * @param anim 显示隐藏的动画
     */
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

    /**
     * 属性动画移动
     * @param view
     * @param targetX
     * @param targetY
     */
    private void doAnimator(View view, float targetX, float targetY){
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

        private float moveViewCenterX, moveViewCenterY, targetViewCenterX, targetViewCenterY;

        private View relativeTargetView;

        private boolean hasInitViewStartXy = false;

        private float mHandLeftStartX,mHandLeftStartY,mHandRightStartX,mHandRightStartY,
                mLegLeftStartX,mLegLeftStartY,mLegRightStartX,mLegRightStartY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            //UbtLog.d(TAG, "event.getActionMasked() = " + event.getActionMasked());
            if(!hasPlaySoundAudioFinish || !onTouchable(view)){
                return false;
            }

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    initViewXy();
                    view.bringToFront();

                    lastX = event.getRawX();
                    lastY = event.getRawY();

                    if(view.getId() == R.id.iv_hand_left){
                        startX = mHandLeftStartX;
                        startY = mHandLeftStartY;
                        relativeTargetView = ivHandLeftBg;
                    }else if(view.getId() == R.id.iv_hand_right){
                        startX = mHandRightStartX;
                        startY = mHandRightStartY;
                        relativeTargetView = ivHandRightBg;
                    }else if(view.getId() == R.id.iv_leg_left ){
                        startX = mLegLeftStartX;
                        startY = mLegLeftStartY;
                        relativeTargetView = ivLegLeftBg;
                    }else if(view.getId() == R.id.iv_leg_right){
                        startX = mLegRightStartX;
                        startY = mLegRightStartY;
                        relativeTargetView = ivLegRightBg;
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

                    isTargetView(view, ivHandLeftBg, nextX, nextY);
                    isTargetView(view, ivHandRightBg, nextX, nextY);
                    isTargetView(view, ivLegLeftBg,  nextX, nextY);
                    isTargetView(view, ivLegRightBg, nextX, nextY);

                }
                return false;
                case MotionEvent.ACTION_UP: {

                    if(!isTargetView(view,relativeTargetView,view.getX(),view.getY())){
                        targetX = startX;
                        targetY = startY;
                    }else {
                        targetX = relativeTargetView.getX();
                        targetY = relativeTargetView.getY();
                        UbtLog.d(TAG,"targetX = " + targetX + "     targetY = " + targetY);

                        if(view.getId() == R.id.iv_hand_left){
                            hasLostHandLeft = false;
                            ((PrincipleHelper)mHelper).doOnLeftHand();
                        }else if(view.getId() == R.id.iv_hand_right){
                            hasLostHandRight = false;
                            ((PrincipleHelper)mHelper).doOnRightHand();
                        }else if(view.getId() == R.id.iv_leg_left ){
                            hasLostLegLeft = false;
                            if(!hasLostLegRight){
                                ((PrincipleHelper)mHelper).doOnLeftFoot();
                                ((PrincipleHelper)mHelper).doOnRightFoot();
                            }
                        }else if(view.getId() == R.id.iv_leg_right){
                            hasLostLegRight = false;
                            if(!hasLostLegLeft){
                                ((PrincipleHelper)mHelper).doOnLeftFoot();
                                ((PrincipleHelper)mHelper).doOnRightFoot();
                            }
                        }

                        doHideView(view);
                        if(hasLearnFinish()){
                            setViewEnable(tvNext,true,1f);
                        }
                    }

                    UbtLog.d(TAG,"targetX => " + targetX + "     targetY = " + targetY + "  view = " +view.getWidth() + "   = " + view.getHeight());
                    // 属性动画移动
                    ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", view.getY(), targetY);
                    ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", view.getX(), targetX);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(x, y);
                    animatorSet.setDuration(ANIMATOR_TIME);
                    animatorSet.start();

                    lastX = event.getRawX();
                    lastY = event.getRawY();

                    resetBg();
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

        /**
         * 移动View的中心点，在目标View的中心的1/4位置，是叠加到另一个View上，显示变色
         * @param moveView 移动的View
         * @param targetView 目标View
         * @param nextX 移动View的X坐标
         * @param nextY 移动View的Y坐标
         * @return
         */
        private boolean isTargetView(View moveView, View targetView, float nextX, float nextY){

            moveViewCenterX = nextX + moveView.getWidth() / 2;
            moveViewCenterY = nextY + moveView.getHeight() / 2;

            targetViewCenterX = targetView.getX() + targetView.getWidth() / 2;
            targetViewCenterY = targetView.getY() + targetView.getHeight() / 2;

            if( targetViewCenterX - targetView.getWidth()/4 < moveViewCenterX
                    && targetViewCenterX + targetView.getWidth()/4 > moveViewCenterX
                    && targetViewCenterY - targetView.getHeight()/4 < moveViewCenterY
                    && targetViewCenterY + targetView.getHeight()/4 > moveViewCenterY
                    ){
                if(moveView.getId() == R.id.iv_hand_left){
                    if(targetView.getId() == R.id.iv_hand_left_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_leftarm_yellow);
                    }else if(targetView.getId() == R.id.iv_hand_right_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_rightarm_red);
                    }else if(targetView.getId() == R.id.iv_leg_left_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_leftleg_red);
                    }else if(targetView.getId() == R.id.iv_leg_right_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_rightleg_red);
                    }

                }else if(moveView.getId() == R.id.iv_hand_right){
                    if(targetView.getId() == R.id.iv_hand_left_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_leftarm_red);
                    }else if(targetView.getId() == R.id.iv_hand_right_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_rightarm_yellow);
                    }else if(targetView.getId() == R.id.iv_leg_left_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_leftleg_red);
                    }else if(targetView.getId() == R.id.iv_leg_right_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_rightleg_red);
                    }

                }else if(moveView.getId() == R.id.iv_leg_left ){
                    if(targetView.getId() == R.id.iv_hand_left_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_leftarm_red);
                    }else if(targetView.getId() == R.id.iv_hand_right_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_rightarm_red);
                    }else if(targetView.getId() == R.id.iv_leg_left_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_leftleg_yellow);
                    }else if(targetView.getId() == R.id.iv_leg_right_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_rightleg_red);
                    }

                }else if(moveView.getId() == R.id.iv_leg_right){
                    if(targetView.getId() == R.id.iv_hand_left_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_leftarm_red);
                    }else if(targetView.getId() == R.id.iv_hand_right_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_rightarm_red);
                    }else if(targetView.getId() == R.id.iv_leg_left_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_leftleg_red);
                    }else if(targetView.getId() == R.id.iv_leg_right_bg){
                        targetView.setBackgroundResource(R.drawable.icon_principle_rightleg_yellow);
                    }
                }
                return true;
            }else {
                if(targetView.getId() == R.id.iv_hand_left_bg){
                    ivHandLeftBg.setBackgroundResource(R.drawable.icon_principle_leftarm_white);
                }else if(targetView.getId() == R.id.iv_hand_right_bg){
                    ivHandRightBg.setBackgroundResource(R.drawable.icon_principle_rightarm_white);
                }else if(targetView.getId() == R.id.iv_leg_left_bg){
                    ivLegLeftBg.setBackgroundResource(R.drawable.icon_principle_leftleg_white);
                }else if(targetView.getId() == R.id.iv_leg_right_bg){
                    ivLegRightBg.setBackgroundResource(R.drawable.icon_principle_rightleg_white);
                }
                return false;
            }
        }

        /**
         *  重置还原背景原色
         */
        private void resetBg(){
            if(hasLostHandLeft){
                ivHandLeftBg.setBackgroundResource(R.drawable.icon_principle_leftarm_white);
            }
            if(hasLostHandRight){
                ivHandRightBg.setBackgroundResource(R.drawable.icon_principle_rightarm_white);
            }
            if(hasLostLegLeft){
                ivLegLeftBg.setBackgroundResource(R.drawable.icon_principle_leftleg_white);
            }
            if(hasLostLegRight){
                ivLegRightBg.setBackgroundResource(R.drawable.icon_principle_rightleg_white);
            }
        }

        /**
         * 初始化视图的XY坐标
         */
        private void initViewXy(){
            if(!hasInitViewStartXy){
                mHandLeftStartX = ivHandLeft.getX();
                mHandLeftStartY = ivHandLeft.getY();
                mHandRightStartX = ivHandRight.getX();
                mHandRightStartY = ivHandRight.getY();

                mLegLeftStartX = ivLegLeft.getX();
                mLegLeftStartY = ivLegLeft.getY();
                mLegRightStartX = ivLegRight.getX();
                mLegRightStartY = ivLegRight.getY();
                hasInitViewStartXy = true;
            }
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
