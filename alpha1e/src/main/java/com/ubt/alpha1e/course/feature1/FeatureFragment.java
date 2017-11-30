package com.ubt.alpha1e.course.feature1;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.animator.BezierView;
import com.ubt.alpha1e.animator.FloatAnimator;
import com.ubt.alpha1e.animator.IBezierView;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.course.CourseActivity;
import com.ubt.alpha1e.course.event.PrincipleEvent;
import com.ubt.alpha1e.course.helper.PrincipleHelper;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.dialog.ConfirmDialog;
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
public class FeatureFragment extends MVPBaseFragment<FeatureContract.View, FeaturePresenter> implements FeatureContract.View,IBezierView {

    private static final String TAG = FeatureFragment.class.getSimpleName();

    private static final int SHOW_VIEW = 1;
    private static final int BEZIER_ANIMATOR_FINISH = 2;
    private static final int PLAY_ACTION_FINISH = 3;
    private static final int ENABLE_ALL_VIEW = 4;
    private static final int RECEVICE_SENSOR = 5;
    private static final int RECEVICE_HEAD = 6;
    private static final int RECEVICE_VOICE_WAIT = 7;
    private static final int LEARN_FINISH = 8;
    private static final int TAP_HEAD = 9;

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
    @BindView(R.id.iv_principle_steering_engine)
    ImageView ivPrincipleSteeringEngine;
    @BindView(R.id.iv_principle_infrared_sensor)
    ImageView ivPrincipleInfraredSensor;
    @BindView(R.id.iv_principle_soundbox)
    ImageView ivPrincipleSoundbox;
    @BindView(R.id.iv_principle_head)
    ImageView ivPrincipleHead;
    @BindView(R.id.iv_principle_eye)
    ImageView ivPrincipleEye;
    @BindView(R.id.iv_principle_voice)
    ImageView ivPrincipleVoice;
    @BindView(R.id.iv_principle_voice_obstacle_avoidance)
    ImageView ivPrincipleVoiceObstacleAvoidance;
    @BindView(R.id.bzv_principle_steering_engine)
    BezierView bzvPrincipleSteeringEngine;
    @BindView(R.id.bzv_principle_infrared_sensor)
    BezierView bzvPrincipleInfraredSensor;
    @BindView(R.id.bzv_principle_soundbox)
    BezierView bzvPrincipleSoundbox;
    @BindView(R.id.bzv_principle_head)
    BezierView bzvPrincipleHead;
    @BindView(R.id.bzv_principle_eye)
    BezierView bzvPrincipleEye;
    @BindView(R.id.bzv_principle_voice)
    BezierView bzvPrincipleVoice;
    @BindView(R.id.bzv_principle_voice_obstacle_avoidance)
    BezierView bzvPrincipleVoiceObstacleAvoidance;
    @BindView(R.id.rl_principle_steering_engine_intro)
    RelativeLayout rlPrincipleSteeringEngineIntro;
    @BindView(R.id.rl_principle_infrared_sensor_intro)
    RelativeLayout rlPrincipleInfraredSensorIntro;
    @BindView(R.id.rl_principle_soundbox_intro)
    RelativeLayout rlPrincipleSoundboxIntro;
    @BindView(R.id.rl_principle_head_intro)
    RelativeLayout rlPrincipleHeadIntro;
    @BindView(R.id.rl_principle_eye_intro)
    RelativeLayout rlPrincipleEyeIntro;
    @BindView(R.id.rl_principle_voice_intro)
    RelativeLayout rlPrincipleVoiceIntro;
    @BindView(R.id.rl_principle_voice_obstacle_avoidance_intro)
    RelativeLayout rlPrincipleVoiceObstacleAvoidanceIntro;
    @BindView(R.id.tv_msg_show)
    TextView tvMsgShow;

    private Animation biggerLeftBottomAnim = null;
    private Animation biggerLeftTopAnim = null;
    private Animation biggerRightTopAnim = null;
    private Animation smallerLeftBottomAnim = null;
    private Animation smallerLeftTopAnim = null;
    private Animation smallerRightTopAnim = null;

    private PrincipleHelper mHelper = null;
    private int containerWidth;
    private int containerHeight;
    private int scale = 0;
    private int playIndex = 0;// 1、SteeringEngine 2、InfraredSensor 3、Soundbox  4、head  5、eye  6、voice   7、VoiceObstacleAvoidance
    private int playCount = 0;

    private boolean hasInitRobot = false;
    private RelativeLayout.LayoutParams params = null;
    private FloatAnimator mFloatAnimator = null;
    private boolean hasReceviceSensor = false;
    private boolean hasReceviceHead = false;
    private boolean hasReceviceVoice = false;

    private boolean hasLearnEngine = false;
    private boolean hasLearnSensor = false;
    private boolean hasLearnSoundbox = false;
    private boolean hasLearnHead = false;
    private boolean hasLearnEye = false;
    private boolean hasLearnVoice = false;
    private boolean hasLearnObstacle = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_VIEW:
                    {
                        int viewId = msg.arg1;
                        if(viewId == R.id.bzv_principle_steering_engine){
                            bzvPrincipleSteeringEngine.start();
                        }else if(viewId == R.id.bzv_principle_infrared_sensor){
                            bzvPrincipleInfraredSensor.start();
                        }else if(viewId == R.id.bzv_principle_soundbox){
                            bzvPrincipleSoundbox.start();
                        }else if(viewId == R.id.bzv_principle_head){
                            bzvPrincipleHead.start();
                        }else if(viewId == R.id.bzv_principle_eye){
                            bzvPrincipleEye.start();
                        }else if(viewId == R.id.bzv_principle_voice){
                            bzvPrincipleVoice.start();
                        }else if(viewId == R.id.bzv_principle_voice_obstacle_avoidance){
                            bzvPrincipleVoiceObstacleAvoidance.start();
                        }
                    }

                    break;
                case BEZIER_ANIMATOR_FINISH:
                    int viewId = msg.arg1;
                    if(viewId == R.id.bzv_principle_steering_engine){
                        ivPrincipleSteeringEngine.setVisibility(View.VISIBLE);
                        //mFloatAnimator.addShow(ivPrincipleSteeringEngine);
                    }else if(viewId == R.id.bzv_principle_infrared_sensor){
                        ivPrincipleInfraredSensor.setVisibility(View.VISIBLE);
                        //mFloatAnimator.addShow(ivPrincipleInfraredSensor);
                    }else if(viewId == R.id.bzv_principle_soundbox){
                        ivPrincipleSoundbox.setVisibility(View.VISIBLE);
                        //mFloatAnimator.addShow(ivPrincipleSoundbox);
                    }else if(viewId == R.id.bzv_principle_head){
                        ivPrincipleHead.setVisibility(View.VISIBLE);
                        //mFloatAnimator.addShow(ivPrincipleHead);
                    }else if(viewId == R.id.bzv_principle_eye){
                        ivPrincipleEye.setVisibility(View.VISIBLE);
                        //mFloatAnimator.addShow(ivPrincipleEye);
                    }else if(viewId == R.id.bzv_principle_voice){
                        ivPrincipleVoice.setVisibility(View.VISIBLE);
                        //mFloatAnimator.addShow(ivPrincipleVoice);
                    }else if(viewId == R.id.bzv_principle_voice_obstacle_avoidance){
                        ivPrincipleVoiceObstacleAvoidance.setVisibility(View.VISIBLE);
                        //mFloatAnimator.addShow(ivPrincipleVoiceObstacleAvoidance);
                    }
                    break;
                case PLAY_ACTION_FINISH:
                    if(playIndex == 1){
                        /*if(playCount == 0){
                            if (tvMsgShow.getVisibility() == View.VISIBLE) {
                                showView(tvMsgShow, false, smallerLeftBottomAnim);
                            }
                            showView(rlPrincipleSteeringEngineIntro, true, biggerLeftBottomAnim);
                            playCount = 1;
                            playSound("id_elephant.wav");
                        }else if(playCount == 1){*/
                            //finish
                            playIndex = 0;
                            playCount = 0;
                            hasLearnEngine = true;
                            showView(rlPrincipleSteeringEngineIntro, false, smallerLeftBottomAnim);
                            mHandler.sendEmptyMessageDelayed(LEARN_FINISH,500);
                        //}
                    }else if(playIndex == 2){

                        if(playCount == 0){
                            /*if (tvMsgShow.getVisibility() == View.VISIBLE) {
                                showView(tvMsgShow, false, smallerLeftBottomAnim);
                            }
                            showView(rlPrincipleInfraredSensorIntro, true, biggerRightTopAnim);
                            playCount = 1;
                            playSound("id_elephant.wav");*/

                        }else if(playCount == 1){
                            //finish
                            /*playIndex = 0;
                            playCount = 0;
                            hasLearnSensor = true;
                            showView(rlPrincipleInfraredSensorIntro, false, smallerRightTopAnim);
                            mHelper.playFile("红外2.hts");
                            mHelper.doReadInfraredSensor((byte)0);
                            mHandler.sendEmptyMessageDelayed(LEARN_FINISH,500);*/

                            if (tvMsgShow.getVisibility() == View.VISIBLE) {
                                showView(tvMsgShow, false, smallerLeftBottomAnim);
                            }
                            showView(rlPrincipleInfraredSensorIntro, true, biggerRightTopAnim);
                            mHelper.playFile("红外传感.hts");
                            playCount = 2;
                        }else if(playCount == 2){
                            //finish
                            playIndex = 0;
                            playCount = 0;
                            hasLearnSensor = true;
                            showView(rlPrincipleInfraredSensorIntro, false, smallerRightTopAnim);
                            mHelper.doReadInfraredSensor((byte)0);
                            mHandler.sendEmptyMessageDelayed(LEARN_FINISH,500);
                        }

                    }else if(playIndex == 3){

                        //finish
                        playIndex = 0;
                        playCount = 0;
                        hasLearnSoundbox = true;
                        showView(rlPrincipleSoundboxIntro, false, smallerRightTopAnim);
                        mHandler.sendEmptyMessageDelayed(LEARN_FINISH,500);
                    }else if(playIndex == 4){
                        if(playCount == 0){
                            /*if (tvMsgShow.getVisibility() == View.VISIBLE) {
                                showView(tvMsgShow, false, smallerLeftBottomAnim);
                            }
                            showView(rlPrincipleHeadIntro, true, biggerLeftTopAnim);
                            playCount = 1;
                            playSound("id_elephant.wav");*/
                        }else if(playCount == 1){
                            if (tvMsgShow.getVisibility() == View.VISIBLE) {
                                showView(tvMsgShow, false, smallerLeftBottomAnim);
                            }
                            showView(rlPrincipleHeadIntro, true, biggerLeftTopAnim);
                            mHelper.playFile("触摸传感.hts");
                            playCount = 2;
                        }else if(playCount == 2){
                            //finish
                            playIndex = 0;
                            playCount = 0;
                            hasLearnHead = true;
                            showView(rlPrincipleHeadIntro, false, smallerLeftTopAnim);
                            mHandler.sendEmptyMessageDelayed(LEARN_FINISH,500);
                            mHelper.doReadHeadClick((byte)0);
                        }
                    }else if(playIndex == 5){

                        //finish
                        playIndex = 0;
                        playCount = 0;
                        hasLearnEye = true;
                        showView(rlPrincipleEyeIntro, false, smallerLeftTopAnim);
                        mHandler.sendEmptyMessageDelayed(LEARN_FINISH,500);
                    }else if(playIndex == 6){
                        if(playCount == 0){
                            /*if (tvMsgShow.getVisibility() == View.VISIBLE) {
                                showView(tvMsgShow, false, smallerLeftBottomAnim);
                            }
                            showView(rlPrincipleVoiceIntro, true, biggerLeftBottomAnim);
                            playCount = 1;
                            playSound("id_elephant.wav");*/
                        }else if(playCount == 1){
                            if (tvMsgShow.getVisibility() == View.VISIBLE) {
                                showView(tvMsgShow, false, smallerLeftBottomAnim);
                            }
                            showView(rlPrincipleVoiceIntro, true, biggerLeftBottomAnim);
                            playActionFile("麦克风.hts");
                            playCount = 2;

                        }else if(playCount == 2){
                            //finish
                            playIndex = 0;
                            playCount = 0;
                            hasLearnVoice = true;
                            showView(rlPrincipleVoiceIntro, false, smallerLeftBottomAnim);
                            mHandler.sendEmptyMessageDelayed(LEARN_FINISH,500);
                        }
                    }else if(playIndex == 7){
                        if(playCount == 0){
                            if (tvMsgShow.getVisibility() == View.VISIBLE) {
                                showView(tvMsgShow, false, smallerLeftBottomAnim);
                            }
                            showView(rlPrincipleVoiceObstacleAvoidanceIntro, true, biggerLeftBottomAnim);
                            playCount = 1;
                            playActionFile("百宝袋.hts");
                        }else if(playCount == 1){
                            //finish
                            playIndex = 0;
                            playCount = 0;
                            hasLearnObstacle = true;
                            showView(rlPrincipleVoiceObstacleAvoidanceIntro, false, smallerLeftBottomAnim);
                            mHandler.sendEmptyMessageDelayed(LEARN_FINISH,500);
                        }
                    }

                    break;
                case ENABLE_ALL_VIEW:
                    setAllEnable(true);
                    break;
                case LEARN_FINISH:
                    mHandler.sendEmptyMessage(ENABLE_ALL_VIEW);
                    UbtLog.d(TAG,"getLearnCount() = " + getLearnCount());
                    if(getLearnCount() == 7){
                        ((CourseActivity)getActivity()).doSaveCourseProgress(1,1,4);
                        playActionFile("胜利.hts");
                        new ConfirmDialog(getContext()).builder()
                                .setTitle(getStringRes("ui_setting_principle_learn_finish"))
                                .setMsg(getStringRes("ui_setting_principle_learn_next"))
                                .setCancelable(false)
                                .setPositiveButton(getStringRes("ui_common_confirm"), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        getActivity().finish();
                                    }
                                }).show();
                    }else {
                        if(getLearnCount() < 5){
                            setViewEnable(tvNext,false,0.3f);
                        }else {
                            setViewEnable(tvNext,true,0.3f);
                        }
                    }
                    break;

                case RECEVICE_SENSOR:
                    if(!hasReceviceSensor && playIndex == 2){
                        hasReceviceSensor = true;
                        playActionFile("红外传感2.hts");
                        playCount = 1;
                    }
                    break;
                case RECEVICE_HEAD:
                    if(!hasReceviceHead && playIndex == 4){
                        hasReceviceHead = true;
                        playCount = 1;
                        playActionFile("触摸传感2.hts");
                    }
                    break;
                case RECEVICE_VOICE_WAIT:
                    if(!hasReceviceVoice && playIndex == 6){
                        hasReceviceVoice = true;
                        playCount = 1;
                        playActionFile("麦克2.hts");
                    }
                    break;
                case TAP_HEAD:
                    //拍头退出课程模式
                    ToastUtils.showShort(getStringRes("ui_setting_principle_tap_head"));
                    getActivity().finish();
                    break;
            }

        }
    };

    private int getLearnCount(){
        int i = 0;
        if(hasLearnEngine) i++;
        if(hasLearnSensor) i++;
        if(hasLearnSoundbox) i++;
        if(hasLearnHead) i++;
        if(hasLearnEye) i++;
        if(hasLearnVoice) i++;
        if(hasLearnObstacle) i++;
        return i;
    }

    @SuppressLint("ValidFragment")
    public FeatureFragment(PrincipleHelper helper) {
        super();
        mHelper = helper;
    }

    @Override
    protected void initUI() {
        tvNext.setText(getStringRes("ui_setting_principle_end"));
        if(((CourseActivity)getActivity()).getEnterPropress() < 4){
            setViewEnable(tvNext,false,0.5f);
        }
        ivPrincipleSteeringEngine.setVisibility(View.INVISIBLE);
        ivPrincipleInfraredSensor.setVisibility(View.INVISIBLE);
        ivPrincipleSoundbox.setVisibility(View.INVISIBLE);
        ivPrincipleHead.setVisibility(View.INVISIBLE);
        ivPrincipleEye.setVisibility(View.INVISIBLE);
        ivPrincipleVoice.setVisibility(View.INVISIBLE);
        ivPrincipleVoiceObstacleAvoidance.setVisibility(View.INVISIBLE);

        scale = (int) this.getResources().getDisplayMetrics().density;
        containerWidth = this.getResources().getDisplayMetrics().widthPixels;
        containerHeight = this.getResources().getDisplayMetrics().heightPixels;
        mFloatAnimator = FloatAnimator.getIntanse();
        mFloatAnimator.clear();

        biggerLeftBottomAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_bigger_anim_left_bottom);
        smallerLeftBottomAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_smaller_anim_left_bottom);

        biggerLeftTopAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_bigger_anim_left_top);
        smallerLeftTopAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_smaller_anim_left_top);

        biggerRightTopAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_bigger_anim_right_top);
        smallerRightTopAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scan_smaller_anim_right_top);

        UbtLog.d(TAG, "scale = " + scale
                + "  width = " + this.getResources().getDisplayMetrics().widthPixels
                + "  height = " + this.getResources().getDisplayMetrics().heightPixels
                + "  densityDpi = " + this.getResources().getDisplayMetrics().densityDpi);

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
    }

    @Override
    public void onResume() {
        super.onResume();

        mHelper.doInit();
        showView(bzvPrincipleSteeringEngine,100);
        showView(bzvPrincipleInfraredSensor,300);
        showView(bzvPrincipleSoundbox,500);
        showView(bzvPrincipleHead,700);
        showView(bzvPrincipleEye,900);
        showView(bzvPrincipleVoice,1100);
        showView(bzvPrincipleVoiceObstacleAvoidance,1300);

    }

    private void showView(View view,int delayTime){
        Message msg = new Message();
        msg.what = SHOW_VIEW;
        msg.arg1 = view.getId();
        mHandler.sendMessageDelayed(msg,delayTime);
    }

    @Subscribe
    public void onEventPrinciple(PrincipleEvent event) {
        if(!(((CourseActivity)getActivity()).getCurrentFragment() instanceof FeatureFragment) ){
            return;
        }

        if(event.getEvent() == PrincipleEvent.Event.PLAY_ACTION_FINISH){

            mHandler.sendEmptyMessage(PLAY_ACTION_FINISH);
        }else if(event.getEvent() == PrincipleEvent.Event.CALL_GET_INFRARED_DISTANCE){
            int infraredDistance = event.getInfraredDistance();
            UbtLog.d(TAG,"infraredDistance = " + infraredDistance);
            if(infraredDistance > 0 && infraredDistance < 30){
                mHandler.sendEmptyMessage(RECEVICE_SENSOR);
            }
        }else if(event.getEvent() == PrincipleEvent.Event.CALL_CLICK_HEAD){
            int status = event.getStatus();
            UbtLog.d(TAG,"CALL_CLICK_HEAD = " + status);
            if(status == 2){
                mHandler.sendEmptyMessage(RECEVICE_HEAD);
            }
        }else if(event.getEvent() == PrincipleEvent.Event.VOICE_WAIT){
            mHandler.sendEmptyMessage(RECEVICE_VOICE_WAIT);
        }else if(event.getEvent() == PrincipleEvent.Event.TAP_HEAD){
            mHandler.sendEmptyMessage(TAP_HEAD);
        }
    }

    private void reset(){
        playIndex = 0;
        playCount = 0;
        setAllEnable(true);
        mHelper.doInit();
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        scale = (int) this.getResources().getDisplayMetrics().density;
        if (scale >= 3) {
            return R.layout.fragment_robot_feature_3;
        } else {
            return R.layout.fragment_robot_feature;
        }
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
    public boolean isBulueToothConnected() {
        return super.isBulueToothConnected();
    }

    @Override
    public void onDestroyView() {
        if(mHandler.hasMessages(SHOW_VIEW)){
            mHandler.removeMessages(SHOW_VIEW);
        }
        if(mHandler.hasMessages(ENABLE_ALL_VIEW)){
            mHandler.removeMessages(ENABLE_ALL_VIEW);
        }
        if(mHandler.hasMessages(LEARN_FINISH)){
            mHandler.removeMessages(LEARN_FINISH);
        }

        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initRobot() {

        if (scale == 2.0) {

            initViewLayout(ivRobot, scale, 0);

            initViewLayout(ivHandLeft, scale, 110);

            initViewLayout(ivHandRight, scale, 110);

            initViewLayout(ivLegLeft, scale, -36);

            initViewLayout(ivLegRight, scale, -36);

            initViewLayout(ivPrincipleSteeringEngine, scale, 0);

            initViewLayout(ivPrincipleInfraredSensor, scale, 0);

            initViewLayout(ivPrincipleSoundbox, scale, 0);

            initViewLayout(ivPrincipleHead, scale, 0);

            initViewLayout(ivPrincipleEye, scale, 0);

            initViewLayout(ivPrincipleVoice, scale, 0);

            initViewLayout(ivPrincipleVoiceObstacleAvoidance, scale, 0);

        }

        initViewLine(ivRobot, ivPrincipleSteeringEngine);
        initViewLine(ivRobot, ivPrincipleInfraredSensor);
        initViewLine(ivRobot, ivPrincipleSoundbox);
        initViewLine(ivRobot, ivPrincipleHead);
        initViewLine(ivRobot, ivPrincipleEye);
        initViewLine(ivRobot, ivPrincipleVoice);
        initViewLine(ivRobot, ivPrincipleVoiceObstacleAvoidance);

        bzvPrincipleSteeringEngine.setCallbackListence(this);
        bzvPrincipleInfraredSensor.setCallbackListence(this);
        bzvPrincipleSoundbox.setCallbackListence(this);
        bzvPrincipleHead.setCallbackListence(this);
        bzvPrincipleEye.setCallbackListence(this);
        bzvPrincipleVoice.setCallbackListence(this);
        bzvPrincipleVoiceObstacleAvoidance.setCallbackListence(this);
    }

    private void initViewLayout(View view, int scale, int margetTop) {

        params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        UbtLog.d(TAG, "width start :: " + params.width + " height : " + params.height + " width = " + ivRobot.getWidth()
                + "  height = " + ivRobot.getHeight() + "  topMargin = " + params.topMargin + " " + params.leftMargin + " >> " + view.getTop() + "   " + view.getLeft());

        params.width = view.getWidth() / 2 * scale;
        params.height = view.getHeight() / 2 * scale;
        if (margetTop != 0) {
            params.topMargin = margetTop / 2 * scale;
        }
        view.setLayoutParams(params);
        int newLeft = view.getLeft() - (params.width - view.getWidth()) / 2;
        int newTop = view.getTop() - (params.height - view.getHeight()) / 2;

        view.layout(newLeft, newTop, newLeft + params.width, newTop + params.height);

    }

    private void initViewLine(View startView, View targetView) {
        UbtLog.d(TAG, "targetView = " + targetView);
        int[] startPos = getlocation(startView);
        int[] targetPos = getlocation(targetView);
        int startX, startY, targetX, targetY;
        if (targetView.getId() == R.id.iv_principle_steering_engine) {
            startX = startPos[0] + startView.getWidth() / 4;
            startY = startPos[1] + startView.getHeight() - 14 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleSteeringEngine.addPoint(startX, startY);
            bzvPrincipleSteeringEngine.addPoint(targetX + (startX - targetX) / 2, targetY - (targetY - startY) / 2 - 20*scale);
            bzvPrincipleSteeringEngine.addPoint(targetX, targetY);

        } else if (targetView.getId() == R.id.iv_principle_infrared_sensor) {

            startX = startPos[0] + startView.getWidth() / 2;
            startY = startPos[1] + startView.getHeight() / 2 + 26 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleInfraredSensor.addPoint(startX, startY);
            bzvPrincipleInfraredSensor.addPoint(startX - 40*scale, startY );
            bzvPrincipleInfraredSensor.addPoint(startX - 45*scale, startY - 20*scale);
            bzvPrincipleInfraredSensor.addPoint(targetX, targetY);

        } else if (targetView.getId() == R.id.iv_principle_soundbox) {

            startX = startPos[0] + startView.getWidth() / 2 - 46 / 2 * scale;
            startY = startPos[1] + 58 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleSoundbox.addPoint(startX, startY);
            bzvPrincipleSoundbox.addPoint(startX - 30 * scale, startY);
            bzvPrincipleSoundbox.addPoint(targetX + (startX - targetX) / 2 , targetY + 60*scale);
            bzvPrincipleSoundbox.addPoint(targetX, targetY);

        } else if (targetView.getId() == R.id.iv_principle_head) {

            startX = startPos[0] + startView.getWidth() / 2 ;
            startY = startPos[1] + 5*scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleHead.addPoint(startX, startY);
            bzvPrincipleHead.addPoint(startX-5*scale, startY-5*scale);
            bzvPrincipleHead.addPoint(targetX, targetY);

        } else if (targetView.getId() == R.id.iv_principle_eye) {

            startX = startPos[0] + startView.getWidth() / 2 + 24 / 2 * scale;
            startY = startPos[1] + 58 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleEye.addPoint(startX, startY);
            bzvPrincipleEye.addPoint(targetX - 10*scale , targetY + 50*scale);
            bzvPrincipleEye.addPoint(targetX, targetY);

        } else if (targetView.getId() == R.id.iv_principle_voice) {

            startX = startPos[0] + startView.getWidth() / 2 + 46 / 2 * scale;
            startY = startPos[1] + 126 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleVoice.addPoint(startX, startY);
            bzvPrincipleVoice.addPoint(targetX - (targetX - startX) / 2 , startY);
            bzvPrincipleVoice.addPoint(targetX, targetY);

        } else if (targetView.getId() == R.id.iv_principle_voice_obstacle_avoidance) {

            startX = startPos[0] + startView.getWidth() / 2;
            startY = startPos[1] + 200 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleVoiceObstacleAvoidance.addPoint(startX, startY);
            bzvPrincipleVoiceObstacleAvoidance.addPoint(targetX - (targetX - startX) / 2, targetY - (targetY - startY) / 2 - 20*scale);
            bzvPrincipleVoiceObstacleAvoidance.addPoint(targetX, targetY);
        }
    }

    private int[] getlocation(View view) {
        int x, y;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        x = location[0];
        y = location[1];

        UbtLog.d(TAG, "Screenx--->>" + x + "  " + "Screeny--->" + y + "     = " + view.getLeft() + "    " + view.getTop());
        return location;
    }


    @OnClick({R.id.iv_back,
            R.id.tv_next,
            R.id.tv_msg_show,
            R.id.iv_principle_steering_engine,
            R.id.iv_principle_infrared_sensor,
            R.id.iv_principle_soundbox,
            R.id.iv_principle_head,
            R.id.iv_principle_eye,
            R.id.iv_principle_voice,
            R.id.iv_principle_voice_obstacle_avoidance,
            R.id.rl_principle_steering_engine_intro,
            R.id.rl_principle_infrared_sensor_intro,
            R.id.rl_principle_soundbox_intro,
            R.id.rl_principle_head_intro,
            R.id.rl_principle_eye_intro,
            R.id.rl_principle_voice_intro,
            R.id.rl_principle_voice_obstacle_avoidance_intro
            })
    public void onViewClicked(View view) {
        UbtLog.d(TAG, "view = " + view);
        switch (view.getId()) {
            case R.id.iv_back:
                ((CourseActivity) getContext()).switchFragment(CourseActivity.FRAGMENT_MERGE);
                break;
            case R.id.tv_next:
                if(((CourseActivity)getActivity()).getEnterPropress() < 4 && getLearnCount() < 7){
                    new ConfirmDialog(getContext()).builder()
                            .setMsg(getStringRes("ui_setting_principle_skip_tip"))
                            .setCancelable(true)
                            .setPositiveButton(getStringRes("ui_common_confirm"), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((CourseActivity)getActivity()).doSaveCourseProgress(1,1,4);
                                    getActivity().finish();
                                }
                            }).setNegativeButton(getStringRes("ui_common_cancel"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }else {
                    getActivity().finish();
                }

                break;

            case R.id.iv_principle_steering_engine:
                if (tvMsgShow.getVisibility() == View.VISIBLE) {
                    showView(tvMsgShow, false, smallerLeftBottomAnim);
                }
                showView(rlPrincipleSteeringEngineIntro, true, biggerLeftBottomAnim);
                startPlayActionFile(ivPrincipleSteeringEngine,1,"舵机.hts");
                ivPrincipleSteeringEngine.bringToFront();
                break;
            case R.id.iv_principle_infrared_sensor:
                hasReceviceSensor = false;
                tvMsgShow.setText(getStringRes("ui_principle_sensor_tips"));
                showView(tvMsgShow, true, biggerLeftBottomAnim);
                startPlayActionFile(ivPrincipleInfraredSensor,2,"红外传感1.hts");
                mHelper.doReadInfraredSensor((byte)1);
                ivPrincipleInfraredSensor.bringToFront();
                break;
            case R.id.iv_principle_soundbox:

                if (tvMsgShow.getVisibility() == View.VISIBLE) {
                    showView(tvMsgShow, false, smallerLeftBottomAnim);
                }
                showView(rlPrincipleSoundboxIntro, true, biggerRightTopAnim);
                startPlayActionFile(ivPrincipleSoundbox,3,"扬声器.hts");
                ivPrincipleSoundbox.bringToFront();
                break;
            case R.id.iv_principle_head:
                hasReceviceHead = false;
                tvMsgShow.setText(getStringRes("ui_principle_head_tips"));
                showView(tvMsgShow, true, biggerLeftBottomAnim);
                startPlayActionFile(ivPrincipleHead,4,"触摸传感1.hts");
                mHelper.doReadHeadClick((byte)1);
                ivPrincipleHead.bringToFront();
                break;
            case R.id.iv_principle_eye:
                if (tvMsgShow.getVisibility() == View.VISIBLE) {
                    showView(tvMsgShow, false, smallerLeftBottomAnim);
                }
                showView(rlPrincipleEyeIntro, true, biggerLeftTopAnim);
                startPlayActionFile(ivPrincipleEye,5,"眼睛LED.hts");
                ivPrincipleEye.bringToFront();
                break;
            case R.id.iv_principle_voice:
                hasReceviceVoice = false;
                tvMsgShow.setText(getStringRes("ui_principle_voice_tips"));
                showView(tvMsgShow, true, biggerLeftBottomAnim);
                startPlayActionFile(ivPrincipleVoice,6,"麦克1.hts");
                ivPrincipleVoice.bringToFront();
                break;
            case R.id.iv_principle_voice_obstacle_avoidance:
                tvMsgShow.setText(getStringRes("ui_principle_obstacle_tips"));
                showView(tvMsgShow, true, biggerLeftBottomAnim);
                startPlayActionFile(ivPrincipleVoiceObstacleAvoidance,7,"百宝1.hts");
                ivPrincipleVoiceObstacleAvoidance.bringToFront();
                break;
            case R.id.tv_msg_show:
                showView(tvMsgShow, false, smallerLeftBottomAnim);
                reset();
                break;
            case R.id.rl_principle_steering_engine_intro:
                showView(rlPrincipleSteeringEngineIntro, false, smallerLeftBottomAnim);
                reset();
                break;
            case R.id.rl_principle_infrared_sensor_intro:
                showView(rlPrincipleInfraredSensorIntro, false, smallerRightTopAnim);
                reset();
                break;
            case R.id.rl_principle_soundbox_intro:
                showView(rlPrincipleSoundboxIntro, false, smallerRightTopAnim);
                reset();
                break;
            case R.id.rl_principle_head_intro:
                showView(rlPrincipleHeadIntro, false, smallerLeftTopAnim);
                reset();
                break;
            case R.id.rl_principle_eye_intro:
                showView(rlPrincipleEyeIntro, false, smallerLeftTopAnim);
                reset();
                break;
            case R.id.rl_principle_voice_intro:
                showView(rlPrincipleVoiceIntro, false, smallerLeftBottomAnim);
                reset();
                break;
            case R.id.rl_principle_voice_obstacle_avoidance_intro:
                showView(rlPrincipleVoiceObstacleAvoidanceIntro, false, smallerLeftBottomAnim);
                reset();
                break;

        }
    }

    private void startPlayActionFile(View view, int index, String actionFile){
        playCount = 0;
        playIndex = index;
        setAllEnable(false);
        setViewEnable(view,true,1);
        playActionFile(actionFile);

    }

    private void playActionFile(String acitonFile){
        mHelper.playFile(acitonFile);
    }

    private void setAllEnable(boolean enable){
        setViewEnable(ivPrincipleSteeringEngine,enable,0.3f);
        setViewEnable(ivPrincipleInfraredSensor,enable,0.3f);
        setViewEnable(ivPrincipleSoundbox,enable,0.3f);
        setViewEnable(ivPrincipleHead,enable,0.3f);
        setViewEnable(ivPrincipleEye,enable,0.3f);
        setViewEnable(ivPrincipleVoice,enable,0.3f);
        setViewEnable(ivPrincipleVoiceObstacleAvoidance,enable,0.3f);

        setViewEnable(bzvPrincipleSteeringEngine,enable,0);
        setViewEnable(bzvPrincipleInfraredSensor,enable,0);
        setViewEnable(bzvPrincipleSoundbox,enable,0);
        setViewEnable(bzvPrincipleHead,enable,0);
        setViewEnable(bzvPrincipleEye,enable,0);
        setViewEnable(bzvPrincipleVoice,enable,0);
        setViewEnable(bzvPrincipleVoiceObstacleAvoidance,enable,0);
    }

    private void setViewEnable(View mView, boolean enable,float alpha){
        //mView.setEnabled(enable);
        mView.setClickable(enable);
        if (enable) {
            mView.setAlpha(1f);
        } else {
            mView.setAlpha(alpha);
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

            if(rlPrincipleSteeringEngineIntro.getVisibility() == View.VISIBLE){
                rlPrincipleSteeringEngineIntro.setVisibility(View.GONE);
            }

            if(rlPrincipleInfraredSensorIntro.getVisibility() == View.VISIBLE){
                rlPrincipleInfraredSensorIntro.setVisibility(View.GONE);
            }

            if(rlPrincipleSoundboxIntro.getVisibility() == View.VISIBLE){
                rlPrincipleSoundboxIntro.setVisibility(View.GONE);
            }

            if(rlPrincipleHeadIntro.getVisibility() == View.VISIBLE){
                rlPrincipleHeadIntro.setVisibility(View.GONE);
            }

            if(rlPrincipleEyeIntro.getVisibility() == View.VISIBLE){
                rlPrincipleEyeIntro.setVisibility(View.GONE);
            }

            if(rlPrincipleVoiceIntro.getVisibility() == View.VISIBLE){
                rlPrincipleVoiceIntro.setVisibility(View.GONE);
            }

            if(rlPrincipleVoiceObstacleAvoidanceIntro.getVisibility() == View.VISIBLE){
                rlPrincipleVoiceObstacleAvoidanceIntro.setVisibility(View.GONE);
            }

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

    @Override
    public void onAnimatorEnd(View view) {
        UbtLog.d(TAG,"onAnimatorEnd = " + view);
        Message msg = new Message();
        msg.what = BEZIER_ANIMATOR_FINISH;
        msg.arg1 = view.getId();
        mHandler.sendMessage(msg);
    }
}
