package com.ubt.alpha1e.ui.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * AppGuideView
 *
 * @author wmma
 * @description 主要实现改版指引页面
 * @date 2016/9/18
 * @update 
 */


public class AppGuideView {

    private String TAG = "AppGuideView";
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;

    private RelativeLayout rlGuideLayout;
    private RelativeLayout rlGuideSquare, rlGuideRobot, rlGuideMy, rlGuideControl, rlGuideConnect, rlGuideXiu;
    private TextView tvGuideSquare, tvGuideRobot, tvGuideMy, tvGuideControl, tvGuideConnect, tvGuideXiu;

    private AppGuideCloseListener listener;
    private boolean created = false;

    public AppGuideView(Context context, AppGuideCloseListener listener) {
        mContext = context;
        this.listener = listener;
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
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(180, 0, 0, 0));

        LayoutInflater inflater = LayoutInflater.from(mContext.getApplicationContext());
        rlGuideLayout = (RelativeLayout) inflater.inflate(R.layout.layout_first_guid, null);
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
        rlGuideSquare = (RelativeLayout)view.findViewById(R.id.rl_square_guide);
        rlGuideRobot = (RelativeLayout)view.findViewById(R.id.rl_robot_guide);
        rlGuideMy = (RelativeLayout)view.findViewById(R.id.rl_my_guide);
        rlGuideControl = (RelativeLayout)view.findViewById(R.id.rl_control_guide);
        rlGuideConnect = (RelativeLayout)view.findViewById(R.id.rl_connect_guide);
        rlGuideXiu = (RelativeLayout)view.findViewById(R.id.rl_xiu_guide);

        tvGuideSquare = (TextView)view.findViewById(R.id.tv_guide_square);
        tvGuideRobot = (TextView)view.findViewById(R.id.tv_guide_robot);
        tvGuideMy = (TextView)view.findViewById(R.id.tv_guide_my);
        tvGuideControl = (TextView)view.findViewById(R.id.tv_guide_control);
        tvGuideConnect = (TextView)view.findViewById(R.id.tv_guide_connect);
        tvGuideXiu = (TextView)view.findViewById(R.id.tv_guide_xiu);

        tvGuideSquare.setText(AlphaApplication.getBaseActivity().getStringResources("ui_introduction_square"));
        tvGuideRobot.setText(AlphaApplication.getBaseActivity().getStringResources("ui_introduction_control"));
        tvGuideMy.setText(AlphaApplication.getBaseActivity().getStringResources("ui_introduction_mine"));
        tvGuideControl.setText(AlphaApplication.getBaseActivity().getStringResources("ui_introduction_play_control"));
        tvGuideConnect.setText(AlphaApplication.getBaseActivity().getStringResources("ui_introduction_connect"));
        tvGuideXiu.setText(AlphaApplication.getBaseActivity().getStringResources("ui_show_guide_tip"));

        String step = readGuideStep();
        if(step.equals("1")){
            rlGuideSquare.setVisibility(View.GONE);
            rlGuideRobot.setVisibility(View.VISIBLE);
        }else if(step.equals("2")){
            rlGuideSquare.setVisibility(View.GONE);
            rlGuideRobot.setVisibility(View.GONE);
            rlGuideMy.setVisibility(View.VISIBLE);
        }else if(step.equals("3")){
            rlGuideSquare.setVisibility(View.GONE);
            rlGuideRobot.setVisibility(View.GONE);
            rlGuideMy.setVisibility(View.GONE);
            rlGuideConnect.setVisibility(View.VISIBLE);
        }else if(step.equals("4")){
            rlGuideSquare.setVisibility(View.GONE);
            rlGuideRobot.setVisibility(View.GONE);
            rlGuideMy.setVisibility(View.GONE);
            rlGuideConnect.setVisibility(View.GONE);
            rlGuideXiu.setVisibility(View.VISIBLE);
        }else if(step.equals("5")){
            rlGuideSquare.setVisibility(View.GONE);
            rlGuideRobot.setVisibility(View.GONE);
            rlGuideMy.setVisibility(View.GONE);
            rlGuideConnect.setVisibility(View.GONE);
            rlGuideXiu.setVisibility(View.GONE);
        }



    }

    private int step = 0;
    private void setOnclick(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                closeAppGuideView();
                UbtLog.d(TAG, "step=" +step);
                if(rlGuideSquare.getVisibility()==View.VISIBLE){
                    rlGuideSquare.setVisibility(View.GONE);
                    rlGuideRobot.setVisibility(View.VISIBLE);
                    step =1;
                }else

                if(rlGuideRobot.getVisibility()==View.VISIBLE){
                    rlGuideRobot.setVisibility(View.GONE);
                    rlGuideMy.setVisibility(View.VISIBLE);
                    step =2;
                }else

                if(rlGuideMy.getVisibility()==View.VISIBLE){
                    rlGuideMy.setVisibility(View.GONE);
                    rlGuideConnect.setVisibility(View.VISIBLE);
                    step =3;
                }else



                if(rlGuideConnect.getVisibility()==View.VISIBLE){
                    rlGuideConnect.setVisibility(View.GONE);
                    rlGuideXiu.setVisibility(View.VISIBLE);
                    UbtLog.d(TAG, "rlGuideXiu show");
//                    tvGuideXiu.setVisibility(View.VISIBLE);
//                    listener.appGuideClose(true);
                    step =4;
//                    closeAppGuideView();

                }else if(rlGuideXiu.getVisibility()==View.VISIBLE){
                    UbtLog.d(TAG,"11ndjdkdkdk");
                    rlGuideXiu.setVisibility(View.GONE);
                    listener.appGuideClose(true);
                    step = 5;
                    closeAppGuideView();
                }

                recordGuideStep(""+step);


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
        BasicSharedPreferencesOperator.getInstance(mContext, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(BasicSharedPreferencesOperator.KEY_GUIDE_STEP,
                step, null, -1);
    }

    public String readGuideStep() {
        return BasicSharedPreferencesOperator.getInstance(mContext, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(BasicSharedPreferencesOperator.KEY_GUIDE_STEP);
    }



}
