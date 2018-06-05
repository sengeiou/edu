package com.ubt.alpha1e.edu.maincourse.courselayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

/**
 * AppGuideView
 *
 * @author wmma
 * @description 主要实现改版指引页面
 * @date 2016/9/18
 * @update 
 */


public class ActionOneGuideView {

    private String TAG = "AppGuideView";
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;

    private RelativeLayout rlGuideLayout;

    private AppGuideCloseListener listener;
    private boolean created = false;

    //指引
    private TextView tvClickRobot;
    private TextView tvClickBasic;
    private TextView tvClickAdvance;
    private TextView tvClickMusic;
    private TextView tvClickReset;
    private TextView tvClickAuto;
    private TextView tvClickHelp;
    private TextView tvClickResetIndex;
    private TextView tvClickMark;
    private TextView tvClickAddFrame;
    private TextView tvClickItem;
    private TextView tvClickChangeTime;
    private RelativeLayout rlThumb;

    private ImageView ivRobot;
    private ImageView ivHandLeft, ivHandRight, ivLegLeft, ivLegRight;
    private float density = 1;

    public ActionOneGuideView(Context context, AppGuideCloseListener listener, float density) {
        mContext = context;
        this.listener = listener;
        this.density = density;
        createGuideView();

    }

    private void createGuideView() {

        if(created){
            UbtLog.d(TAG, "app guide view already created!");
            return;
        }
        mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |WindowManager.LayoutParams.FLAG_FULLSCREEN ;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(100, 0, 0, 0));

        LayoutInflater inflater = LayoutInflater.from(mContext.getApplicationContext());
        if(AlphaApplication.isPad()){
            rlGuideLayout = (RelativeLayout) inflater.inflate(R.layout.layout_action_guide_for_pad, null);
        }else{
            rlGuideLayout = (RelativeLayout) inflater.inflate(R.layout.layout_action_one_guide, null);
        }

        if (Build.VERSION.SDK_INT >= 16) {
            rlGuideLayout.setBackground(colorDrawable);
        }else{
            rlGuideLayout.setBackgroundDrawable(colorDrawable);
        }
        initGuideView(rlGuideLayout);
        setOnclick(rlGuideLayout);

        mWindowManager.addView(rlGuideLayout, wmParams);
        created = true;

    }

    private void initGuideView(View view) {

        tvClickRobot = (TextView) view.findViewById(R.id.tv_click_robot);
        tvClickBasic = (TextView) view.findViewById(R.id.tv_click_basic);
        tvClickAdvance = (TextView)view.findViewById(R.id.tv_click_advance);
        tvClickMusic = (TextView) view.findViewById(R.id.tv_click_music);
        tvClickReset = (TextView) view.findViewById(R.id.tv_click_reset);
        tvClickAuto = (TextView) view.findViewById(R.id.tv_click_auto);
        tvClickHelp = (TextView) view.findViewById(R.id.tv_click_help);
        tvClickResetIndex = (TextView) view.findViewById(R.id.tv_click_reset_index);
        tvClickMark = (TextView) view.findViewById(R.id.tv_click_mark);
        tvClickAddFrame = (TextView) view.findViewById(R.id.tv_click_add);
        tvClickItem = (TextView) view.findViewById(R.id.tv_click_item);
        tvClickChangeTime = (TextView) view.findViewById(R.id.tv_click_change_time);
        rlThumb = (RelativeLayout) view.findViewById(R.id.rl_thumb) ;

        ivRobot = (ImageView) view.findViewById(R.id.iv_robot);
        ivHandLeft = (ImageView)view. findViewById(R.id.iv_hand_left);
        ivHandRight = (ImageView)view. findViewById(R.id.iv_hand_right);
        ivLegLeft = (ImageView) view.findViewById(R.id.iv_leg_left);
        ivLegRight = (ImageView) view.findViewById(R.id.iv_leg_right);
        initRobot();


        String step = readGuideStep();
        UbtLog.d(TAG, "step:" + step);
        if(step.equals("1")){
            tvClickRobot.setVisibility(View.GONE);
            tvClickReset.setVisibility(View.VISIBLE);
        }else if(step.equals("2")){
            tvClickRobot.setVisibility(View.GONE);
            tvClickReset.setVisibility(View.GONE);
            tvClickAuto.setVisibility(View.VISIBLE);
        }else if(step.equals("3")){
            tvClickRobot.setVisibility(View.GONE);
            tvClickReset.setVisibility(View.GONE);
            tvClickAuto.setVisibility(View.GONE);
            tvClickHelp.setVisibility(View.VISIBLE);
        }else if(step.equals("4")){
            tvClickRobot.setVisibility(View.GONE);
            tvClickReset.setVisibility(View.GONE);
            tvClickAuto.setVisibility(View.GONE);
            tvClickHelp.setVisibility(View.GONE);
            tvClickBasic.setVisibility(View.VISIBLE);
        }else if(step.equals("5")){
            tvClickRobot.setVisibility(View.GONE);
            tvClickReset.setVisibility(View.GONE);
            tvClickAuto.setVisibility(View.GONE);
            tvClickHelp.setVisibility(View.GONE);
            tvClickBasic.setVisibility(View.INVISIBLE);
            tvClickAdvance.setVisibility(View.VISIBLE);
        }else if(step.equals("6")){
            tvClickRobot.setVisibility(View.GONE);
            tvClickReset.setVisibility(View.GONE);
            tvClickAuto.setVisibility(View.GONE);
            tvClickHelp.setVisibility(View.GONE);
            tvClickBasic.setVisibility(View.INVISIBLE);
            tvClickAdvance.setVisibility(View.INVISIBLE);
            tvClickMusic.setVisibility(View.VISIBLE);
        }else if(step.equals("7")){
            tvClickRobot.setVisibility(View.GONE);
            tvClickReset.setVisibility(View.GONE);
            tvClickAuto.setVisibility(View.GONE);
            tvClickHelp.setVisibility(View.GONE);
            tvClickBasic.setVisibility(View.GONE);
            tvClickAdvance.setVisibility(View.GONE);
            tvClickMusic.setVisibility(View.GONE);
            tvClickMark.setVisibility(View.VISIBLE);
        }else if(step.equals("8")){
            tvClickRobot.setVisibility(View.GONE);
            tvClickReset.setVisibility(View.GONE);
            tvClickAuto.setVisibility(View.GONE);
            tvClickHelp.setVisibility(View.GONE);
            tvClickBasic.setVisibility(View.GONE);
            tvClickAdvance.setVisibility(View.GONE);
            tvClickMusic.setVisibility(View.GONE);
            tvClickMark.setVisibility(View.GONE);
            tvClickResetIndex.setVisibility(View.VISIBLE);
        }else if(step.equals("9")){
            tvClickRobot.setVisibility(View.GONE);
            tvClickReset.setVisibility(View.GONE);
            tvClickAuto.setVisibility(View.GONE);
            tvClickHelp.setVisibility(View.GONE);
            tvClickBasic.setVisibility(View.GONE);
            tvClickAdvance.setVisibility(View.GONE);
            tvClickMusic.setVisibility(View.GONE);
            tvClickMark.setVisibility(View.GONE);
            tvClickResetIndex.setVisibility(View.GONE);
            tvClickItem.setVisibility(View.VISIBLE);
            rlThumb.setVisibility(View.VISIBLE);
        }else if(step.equals("10")){
            tvClickRobot.setVisibility(View.GONE);
            tvClickReset.setVisibility(View.GONE);
            tvClickAuto.setVisibility(View.GONE);
            tvClickHelp.setVisibility(View.GONE);
            tvClickBasic.setVisibility(View.GONE);
            tvClickAdvance.setVisibility(View.GONE);
            tvClickMusic.setVisibility(View.GONE);
            tvClickMark.setVisibility(View.GONE);
            tvClickResetIndex.setVisibility(View.GONE);
            tvClickItem.setVisibility(View.GONE);
            rlThumb.setVisibility(View.GONE);
            tvClickChangeTime.setVisibility(View.VISIBLE);
        }else if(step.equals("11")){
            tvClickRobot.setVisibility(View.GONE);
            tvClickReset.setVisibility(View.GONE);
            tvClickAuto.setVisibility(View.GONE);
            tvClickHelp.setVisibility(View.GONE);
            tvClickBasic.setVisibility(View.GONE);
            tvClickAdvance.setVisibility(View.GONE);
            tvClickMusic.setVisibility(View.GONE);
            tvClickMark.setVisibility(View.GONE);
            tvClickResetIndex.setVisibility(View.GONE);
            tvClickItem.setVisibility(View.GONE);
            tvClickChangeTime.setVisibility(View.GONE);
            tvClickAddFrame.setVisibility(View.VISIBLE);
        }else if(step.equals("12")){
            tvClickRobot.setVisibility(View.GONE);
            tvClickReset.setVisibility(View.GONE);
            tvClickAuto.setVisibility(View.GONE);
            tvClickHelp.setVisibility(View.GONE);
            tvClickBasic.setVisibility(View.GONE);
            tvClickAdvance.setVisibility(View.GONE);
            tvClickMusic.setVisibility(View.GONE);
            tvClickMark.setVisibility(View.GONE);
            tvClickResetIndex.setVisibility(View.GONE);
            tvClickItem.setVisibility(View.GONE);
            tvClickChangeTime.setVisibility(View.GONE);
            tvClickAddFrame.setVisibility(View.GONE);
        }

    }


    private void initRobot(){
        if(density == 3.0){
            ViewGroup.LayoutParams params = ivRobot.getLayoutParams();

            UbtLog.d(TAG, "width:"+ params.width + "--height:" + params.height);
            params.width = params.width/2*3;
            params.height = params.height/2*3;
            ivRobot.setLayoutParams(params);
            UbtLog.d(TAG, "ivRobot:" + ivRobot.getWidth() + "/" + ivRobot.getHeight());

            params = ivHandLeft.getLayoutParams();
            params.width = params.width/2*3;
            params.height = params.height/2*3;
            ivHandLeft.setLayoutParams(params);
            UbtLog.d(TAG, "ivHandLeft:" + ivHandLeft.getWidth() + "/" + ivHandLeft.getHeight());

            params = ivHandRight.getLayoutParams();
            params.width = params.width/2*3;
            params.height= params.height/2*3;
            ivHandRight.setLayoutParams(params);
            UbtLog.d(TAG, "ivHandRight:" + ivHandRight.getWidth() + "/" + ivHandRight.getHeight());

            params = ivLegLeft.getLayoutParams();
            params.width = params.width/2*3;
            params.height = params.height/2*3;
            ivLegLeft.setLayoutParams(params);
            UbtLog.d(TAG, "ivLegLeft:" + ivLegLeft.getWidth() + "/" + ivLegLeft.getHeight());

            params = ivLegRight.getLayoutParams();
            params.width = params.width/2*3;
            params.height = params.height/2*3;
            ivLegRight.setLayoutParams(params);
            UbtLog.d(TAG, "ivLegRight:" + ivLegRight.getWidth() + "/" + ivLegRight.getHeight());

        }else if(density == 4.0){
            ViewGroup.LayoutParams params = ivRobot.getLayoutParams();

            UbtLog.d(TAG, "width:"+ params.width + "--height:" + params.height);
            params.width = params.width/2*4;
            params.height = params.height/2*4;
            ivRobot.setLayoutParams(params);
            UbtLog.d(TAG, "ivRobot:" + ivRobot.getWidth() + "/" + ivRobot.getHeight());

            params = ivHandLeft.getLayoutParams();
            params.width = params.width/2*4;
            params.height = params.height/2*4;
            ivHandLeft.setLayoutParams(params);
            UbtLog.d(TAG, "ivHandLeft:" + ivHandLeft.getWidth() + "/" + ivHandLeft.getHeight());

            params = ivHandRight.getLayoutParams();
            params.width = params.width/2*4;
            params.height= params.height/2*4;
            ivHandRight.setLayoutParams(params);
            UbtLog.d(TAG, "ivHandRight:" + ivHandRight.getWidth() + "/" + ivHandRight.getHeight());

            params = ivLegLeft.getLayoutParams();
            params.width = params.width/2*4;
            params.height = params.height/2*4;
            ivLegLeft.setLayoutParams(params);
            UbtLog.d(TAG, "ivLegLeft:" + ivLegLeft.getWidth() + "/" + ivLegLeft.getHeight());

            params = ivLegRight.getLayoutParams();
            params.width = params.width/2*4;
            params.height = params.height/2*4;
            ivLegRight.setLayoutParams(params);
            UbtLog.d(TAG, "ivLegRight:" + ivLegRight.getWidth() + "/" + ivLegRight.getHeight());
        }
    }





    private int step = 0;
    private void setOnclick(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbtLog.d(TAG, "step=" +step);
                if(tvClickRobot.getVisibility() == View.VISIBLE) {
                    tvClickRobot.setVisibility(View.GONE);
                    tvClickReset.setVisibility(View.VISIBLE);
                    step = 1;
                }else if(tvClickReset.getVisibility() == View.VISIBLE){
                    tvClickReset.setVisibility(View.GONE);
                    tvClickAuto.setVisibility(View.VISIBLE);
                    step = 2;
                }else if(tvClickAuto.getVisibility() == View.VISIBLE){
                    tvClickAuto.setVisibility(View.GONE);
                    tvClickHelp.setVisibility(View.VISIBLE);
                    step = 3;
                }else if(tvClickHelp.getVisibility() == View.VISIBLE){
                    tvClickHelp.setVisibility(View.GONE);
                    tvClickBasic.setVisibility(View.VISIBLE);
                    step = 4;
                }else if(tvClickBasic.getVisibility() == View.VISIBLE){
                   tvClickBasic.setVisibility(View.INVISIBLE);
                    tvClickAdvance.setVisibility(View.VISIBLE);
                    step = 5;
                }else if(tvClickAdvance.getVisibility() == View.VISIBLE){
                    tvClickAdvance.setVisibility(View.INVISIBLE);
                    tvClickMusic.setVisibility(View.VISIBLE);
                    step = 6;
                }else if(tvClickMusic.getVisibility() == View.VISIBLE){
                    tvClickMusic.setVisibility(View.GONE);
                    tvClickMark.setVisibility(View.VISIBLE);
                    step = 7;
                }else if(tvClickMark.getVisibility() == View.VISIBLE){
                    tvClickMark.setVisibility(View.GONE);
                    tvClickResetIndex.setVisibility(View.VISIBLE);
                    step = 8;
                }else if(tvClickResetIndex.getVisibility() == View.VISIBLE){
                    tvClickResetIndex.setVisibility(View.GONE);
                    tvClickItem.setVisibility(View.VISIBLE);
                    rlThumb.setVisibility(View.VISIBLE);
                    step = 9;
                }else if(tvClickItem.getVisibility() == View.VISIBLE){
                    tvClickItem.setVisibility(View.GONE);
                    tvClickChangeTime.setVisibility(View.VISIBLE);
                    step = 10;
                }else if(tvClickChangeTime.getVisibility() == View.VISIBLE){
                    tvClickChangeTime.setVisibility(View.GONE);
                    tvClickAddFrame.setVisibility(View.VISIBLE);
                    step = 11;
                }else if(tvClickAddFrame.getVisibility() == View.VISIBLE){
                    tvClickAddFrame.setVisibility(View.GONE);
                    step = 12;
                    closeAppGuideView();
                }


            }
        });
    }

    public void closeAppGuideView() {
        if(mWindowManager != null){
            mWindowManager.removeView(rlGuideLayout);
            mWindowManager = null;
        }
    }


    //该回调用于解决第一次启动时改版指引和活动推送Banner画面重叠问题.当改版指引结束后回调通知更新活动内容
    public interface AppGuideCloseListener{
        void appGuideClose(boolean finish);
    }


    public void recordGuideStep(String step) {
        BasicSharedPreferencesOperator.getInstance(mContext, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(BasicSharedPreferencesOperator.KEY_ACTION_CUIDE_STEP,
                step, null, -1);
    }

    public String readGuideStep() {
        return BasicSharedPreferencesOperator.getInstance(mContext, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(BasicSharedPreferencesOperator.KEY_ACTION_CUIDE_STEP);
    }



}
