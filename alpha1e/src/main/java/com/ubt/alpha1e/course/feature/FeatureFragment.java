package com.ubt.alpha1e.course.feature;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.animator.BezierView;
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

public class FeatureFragment extends MVPBaseFragment<FeatureContract.View, FeaturePresenter> implements FeatureContract.View {

    private static final String TAG = FeatureFragment.class.getSimpleName();

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


    private int containerWidth;
    private int containerHeight;
    private int scale = 0;

    private boolean hasInitRobot = false;
    private RelativeLayout.LayoutParams params = null;

    @Override
    protected void initUI() {

        scale = (int) this.getResources().getDisplayMetrics().density;
        containerWidth = this.getResources().getDisplayMetrics().widthPixels;
        containerHeight = this.getResources().getDisplayMetrics().heightPixels;

        UbtLog.d(TAG, "scale == " + scale
                + "  width = " + this.getResources().getDisplayMetrics().widthPixels
                + "  height = " + this.getResources().getDisplayMetrics().heightPixels
                + "  densityDpi = " + this.getResources().getDisplayMetrics().densityDpi);

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

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        scale = (int) this.getResources().getDisplayMetrics().density;
        if(scale == 3){
            return R.layout.fragment_robot_feature_3;
        }else {
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
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
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

            /*initViewLine(ivRobot, ivPrincipleSteeringEngine);
            initViewLine(ivRobot, ivPrincipleInfraredSensor);
            initViewLine(ivRobot, ivPrincipleSoundbox);
            initViewLine(ivRobot, ivPrincipleHead);
            initViewLine(ivRobot, ivPrincipleEye);
            initViewLine(ivRobot, ivPrincipleVoice);
            initViewLine(ivRobot, ivPrincipleVoiceObstacleAvoidance);*/
        }

        initViewLine(ivRobot, ivPrincipleSteeringEngine);
        initViewLine(ivRobot, ivPrincipleInfraredSensor);
        initViewLine(ivRobot, ivPrincipleSoundbox);
        initViewLine(ivRobot, ivPrincipleHead);
        initViewLine(ivRobot, ivPrincipleEye);
        initViewLine(ivRobot, ivPrincipleVoice);
        initViewLine(ivRobot, ivPrincipleVoiceObstacleAvoidance);
    }

    private void initViewLayout(View view, int scale, int margetTop) {

        params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        UbtLog.d(TAG, "width start :: " + params.width + " height : " + params.height + " width = " + ivRobot.getWidth()
                + "  height = " + ivRobot.getHeight() + "  topMargin = " + params.topMargin + " " +  params.leftMargin + " >> " + view.getTop() + "   " + view.getLeft() );

        params.width  = view.getWidth() / 2 * scale;
        params.height = view.getHeight() / 2 * scale;
        if (margetTop != 0) {
            params.topMargin = margetTop / 2 * scale;
        }
        view.setLayoutParams(params);

        UbtLog.d(TAG, "width center:> " + params.width + " height : " + params.height + " width = " + ivRobot.getWidth()
                + "  height = " + ivRobot.getHeight() + "  topMargin = " + params.topMargin + " " +  params.leftMargin + " >> " + view.getTop() + "   " + view.getLeft() );

        int newLeft = view.getLeft() - (params.width  - view.getWidth()) / 2;
        int newTop  = view.getTop() -  (params.height - view.getHeight())/ 2;

        view.layout(newLeft, newTop, newLeft + params.width, newTop + params.height);

        UbtLog.d(TAG, "width end:: " + params.width + " height : " + params.height + " width = " + ivRobot.getWidth()
                + "  height = " + ivRobot.getHeight() + "  topMargin = " + params.topMargin + " " +  params.leftMargin + " >> " + view.getTop() + "   " + view.getLeft() );
    }

    private void initViewLine(View startView, View targetView) {
        UbtLog.d(TAG, "targetView = " + targetView);
        int[] startPos = getlocation(startView);
        int[] targetPos = getlocation(targetView);
        int startX,startY,targetX,targetY;
        if (targetView.getId() == R.id.iv_principle_steering_engine) {
            startX = startPos[0] + startView.getWidth() / 4;
            startY = startPos[1] + startView.getHeight() - 14 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleSteeringEngine.addPoint(startX , startY);
            bzvPrincipleSteeringEngine.addPoint(targetX + (startX - targetX)/2 , targetY - (targetY - startY) / 2 - 30);
            bzvPrincipleSteeringEngine.addPoint(targetX, targetY);

        }else if(targetView.getId() == R.id.iv_principle_infrared_sensor){

            startX = startPos[0] + startView.getWidth() / 2;
            startY = startPos[1] + startView.getHeight() / 2 + 26 / 2 * scale ;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleInfraredSensor.addPoint(startX , startY);
            bzvPrincipleInfraredSensor.addPoint(targetX + (startX - targetX)/2 , startY+50);
            bzvPrincipleInfraredSensor.addPoint(targetX, targetY);

        }else if(targetView.getId() == R.id.iv_principle_soundbox){

            startX = startPos[0] + startView.getWidth() / 2 - 46 / 2 * scale;
            startY = startPos[1] + 58 / 2 * scale ;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleSoundbox.addPoint(startX , startY);
            bzvPrincipleSoundbox.addPoint(targetX + (startX - targetX)/2, targetY + 100);
            bzvPrincipleSoundbox.addPoint(targetX, targetY);

        }else if(targetView.getId() == R.id.iv_principle_head){

            startX = startPos[0] + startView.getWidth() / 2;
            startY = startPos[1] ;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleHead.addPoint(startX , startY);
            bzvPrincipleHead.addPoint(targetX, targetY);

        }else if(targetView.getId() == R.id.iv_principle_eye){

            startX = startPos[0] + startView.getWidth() / 2 + 24 / 2 * scale;
            startY = startPos[1] + 58 / 2 * scale ;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleEye.addPoint(startX , startY);
            bzvPrincipleEye.addPoint(targetX - (targetX - startX)/2, targetY + 100);
            bzvPrincipleEye.addPoint(targetX, targetY);

        }else if(targetView.getId() == R.id.iv_principle_voice){

            startX = startPos[0] + startView.getWidth() / 2 + 46 / 2 * scale;
            startY = startPos[1] + 118 / 2 * scale;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleVoice.addPoint(startX , startY);
            bzvPrincipleVoice.addPoint(targetX - (targetX - startX)/2 , startY+50);
            bzvPrincipleVoice.addPoint(targetX, targetY);

        }else if(targetView.getId() == R.id.iv_principle_voice_obstacle_avoidance){

            startX = startPos[0] + startView.getWidth() / 2;
            startY = startPos[1] + 200 / 2 * scale ;
            targetX = targetPos[0] + targetView.getWidth() / 2;
            targetY = targetPos[1] + targetView.getHeight() / 2;

            bzvPrincipleVoiceObstacleAvoidance.addPoint(startX , startY);
            bzvPrincipleVoiceObstacleAvoidance.addPoint(targetX - (targetX - startX)/2 , targetY - (targetY - startY) / 2 - 30);
            bzvPrincipleVoiceObstacleAvoidance.addPoint(targetX , targetY);
        }

    }

    private int[] getlocation(View view) {
        int x, y;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        x = location[0];
        y = location[1];

        UbtLog.d(TAG, "Screenx--->>" + x + "  " + "Screeny--->" + y + "     = " + view.getLeft() + "    " + view.getTop() );
        return location;
    }


    @OnClick({R.id.iv_back, R.id.tv_next, R.id.iv_principle_steering_engine, R.id.iv_principle_infrared_sensor, R.id.iv_principle_soundbox, R.id.iv_principle_head, R.id.iv_principle_eye, R.id.iv_principle_voice, R.id.iv_principle_voice_obstacle_avoidance})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //((CourseActivity) getContext()).switchFragment(CourseActivity.FRAGMENT_MERGE);
                ((CourseActivity) getContext()).finish();
                break;
            case R.id.tv_next:
                //((CourseActivity) getContext()).finish();

                bzvPrincipleSteeringEngine.start();
                bzvPrincipleInfraredSensor.start();
                bzvPrincipleSoundbox.start();
                bzvPrincipleHead.start();
                bzvPrincipleEye.start();
                bzvPrincipleVoice.start();
                bzvPrincipleVoiceObstacleAvoidance.start();
                break;
            case R.id.iv_principle_steering_engine:
                //getPo(ivPrincipleSteeringEngine);
                break;
            case R.id.iv_principle_infrared_sensor:
                //getPo(ivPrincipleInfraredSensor);
                break;
            case R.id.iv_principle_soundbox:
                break;
            case R.id.iv_principle_head:
                break;
            case R.id.iv_principle_eye:
                break;
            case R.id.iv_principle_voice:
                break;
            case R.id.iv_principle_voice_obstacle_avoidance:
                break;
        }
    }
}
