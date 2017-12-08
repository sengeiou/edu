package com.ubt.alpha1e.course.principle;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.animator.FloatAnimator;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.course.event.PrincipleEvent;
import com.ubt.alpha1e.course.helper.PrincipleHelper;
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


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PrincipleActivity extends MVPBaseActivity<PrincipleContract.View, PrinciplePresenter> implements PrincipleContract.View {

    private static final String TAG = PrincipleActivity.class.getSimpleName();

    private static final int START_FLOAT_ANIMATION_1 = 1;
    private static final int START_FLOAT_ANIMATION_2 = 2;
    private static final int START_FLOAT_ANIMATION_3 = 3;
    private static final int START_FLOAT_ANIMATION = 4;
    private static final int PLAY_ACTION_NEXT = 5;
    private static final int TAP_HEAD = 6;
    private static final int GO_NEXT = 7;
    private static final int SHOW_NEXT_OVER_TIME = 8;
    private static final int BLUETOOTH_DISCONNECT = 9;

    private final int OVER_TIME = 35 * 1000;//超时

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
                        mHandler.sendEmptyMessageDelayed(SHOW_NEXT_OVER_TIME, OVER_TIME);
                        ((PrincipleHelper)mHelper).playFile(playActionFile[0]);
                        rlDialogue1.setVisibility(View.VISIBLE);
                        rlDialogue1.startAnimation(biggerAnimation);
                        mCurrentPlayProgress = 1;
                        startFloatAnimation(rlDialogue1,500);
                    }else if(mCurrentPlayProgress == 1){
                        ((PrincipleHelper)mHelper).playFile(playActionFile[1]);
                        rlDialogue2.setVisibility(View.VISIBLE);
                        rlDialogue2.startAnimation(biggerAnimation);
                        mCurrentPlayProgress = 2;
                        startFloatAnimation(rlDialogue2,500);
                    }else if(mCurrentPlayProgress == 2){
                        ((PrincipleHelper)mHelper).playFile(playActionFile[2]);
                        rlDialogue3.setVisibility(View.VISIBLE);
                        rlDialogue3.startAnimation(biggerAnimation);
                        mCurrentPlayProgress = 3;
                        startFloatAnimation(rlDialogue3,500);
                    }else {
                        mCurrentPlayProgress = 4;
                        setViewEnable(tvNext, true, 1f);
                        doSaveCourseProgress(1,1,1);
                        if(mHandler.hasMessages(SHOW_NEXT_OVER_TIME)){
                            mHandler.removeMessages(SHOW_NEXT_OVER_TIME);
                        }
                        //mHandler.sendEmptyMessage(GO_NEXT);
                    }
                    break;
                case GO_NEXT:

                    ((PrincipleHelper)mHelper).doInit();
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
                        SplitActivity.launchActivity(PrincipleActivity.this,true);
                    }

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

    public static void launchActivity(Activity activity,boolean isFinish) {
        Intent intent = new Intent(activity, PrincipleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        if(isFinish){
            activity.finish();
        }
    }

    private void startFloatAnimation(View view,int delayTime){
        Message msg = new Message();
        msg.what = START_FLOAT_ANIMATION;
        msg.arg1 = view.getId();
        mHandler.sendMessageDelayed(msg,delayTime);
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

        UbtLog.d(TAG, "scale = " + scale
                + "  width = " + this.getResources().getDisplayMetrics().widthPixels
                + "  height = " + this.getResources().getDisplayMetrics().heightPixels
                + "  densityDpi = " + this.getResources().getDisplayMetrics().densityDpi);

        rlDialogue2.setVisibility(View.INVISIBLE);
        rlDialogue3.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams ivPrincipleAlphaParams = (RelativeLayout.LayoutParams) ivPrincipleAlpha.getLayoutParams();
        ivPrincipleAlphaParams.width = SizeUtils.dip2px(130, scale);
        ivPrincipleAlphaParams.height = SizeUtils.dip2px(236, scale);
        ivPrincipleAlpha.setLayoutParams(ivPrincipleAlphaParams);

        RelativeLayout.LayoutParams rlDialogue1Params = (RelativeLayout.LayoutParams) rlDialogue1.getLayoutParams();
        rlDialogue1Params.width = SizeUtils.dip2px(146, scale);
        rlDialogue1Params.height = SizeUtils.dip2px(135, scale);
        rlDialogue1.setLayoutParams(rlDialogue1Params);

        RelativeLayout.LayoutParams rlDialogue2Params = (RelativeLayout.LayoutParams) rlDialogue2.getLayoutParams();
        rlDialogue2Params.width = SizeUtils.dip2px(163, scale);
        rlDialogue2Params.height = SizeUtils.dip2px(133, scale);
        rlDialogue2.setLayoutParams(rlDialogue2Params);

        RelativeLayout.LayoutParams rlDialogue3Params = (RelativeLayout.LayoutParams) rlDialogue3.getLayoutParams();
        rlDialogue3Params.width = SizeUtils.dip2px(121, scale);
        rlDialogue3Params.height = SizeUtils.dip2px(107, scale);
        rlDialogue3.setLayoutParams(rlDialogue3Params);

        UbtLog.d(TAG, "ivPrincipleAlphaParams = " + ivPrincipleAlphaParams.width + "  " + ivPrincipleAlphaParams.height);

        mFloatAnimator = FloatAnimator.getIntanse();
        mFloatAnimator.setDistance(30F);

        biggerAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.scan_bigger_anim);
        biggerAnimation.setDuration(500);
    }

    private void showTapHeadDialog(){
        new ConfirmDialog(getContext()).builder()
                .setMsg(getStringResources("ui_course_principle_exit_tip"))
                .setCancelable(false)
                .setPositiveButton(getStringResources("ui_common_yes"), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((PrincipleHelper) mHelper).doInit();
                        ((PrincipleHelper) mHelper).doEnterCourse((byte) 0);
                        MainCourseActivity.finishByMySelf();
                        PrincipleActivity.this.finish();
                        PrincipleActivity.this.overridePendingTransition(0, R.anim.activity_close_down_up);
                    }
                }).setNegativeButton(getStringResources("ui_common_no"), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_principle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

        mHelper = new PrincipleHelper(this);
        initUI();
    }

    @Subscribe
    public void onEventPrinciple(PrincipleEvent event) {
        UbtLog.d(TAG,"onEventPrinciple = " + event.getEvent());
        if(event.getEvent() == PrincipleEvent.Event.PLAY_ACTION_FINISH){
            mHandler.sendEmptyMessage(PLAY_ACTION_NEXT);
        }else if(event.getEvent() == PrincipleEvent.Event.TAP_HEAD){
            mHandler.sendEmptyMessage(TAP_HEAD);
        }else if(event.getEvent() == PrincipleEvent.Event.DISCONNECTED){
            mHandler.sendEmptyMessage(BLUETOOTH_DISCONNECT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((PrincipleHelper)mHelper).doEnterCourse((byte)1);

        doGetCourseProgress(1);
        mHandler.sendEmptyMessage(PLAY_ACTION_NEXT);
    }

    @Override
    protected void onDestroy() {
        if(mHandler.hasMessages(SHOW_NEXT_OVER_TIME)){
            mHandler.removeMessages(SHOW_NEXT_OVER_TIME);
        }
        if(mHandler.hasMessages(START_FLOAT_ANIMATION)){
            mHandler.removeMessages(START_FLOAT_ANIMATION);
        }
        if(mHandler.hasMessages(GO_NEXT)){
            mHandler.removeMessages(GO_NEXT);
        }
        mFloatAnimator.stopFloatAnimator();
        super.onDestroy();
    }

    @OnClick({R.id.iv_back, R.id.rl_dialogue_2, R.id.rl_dialogue_1, R.id.rl_dialogue_3, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
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

    @Override
    public void onBackPressed() {
        ((PrincipleHelper)mHelper).doInit();
        ((PrincipleHelper)mHelper).doEnterCourse((byte)0);

        this.finish();
        this.overridePendingTransition(0, R.anim.activity_close_down_up);
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
