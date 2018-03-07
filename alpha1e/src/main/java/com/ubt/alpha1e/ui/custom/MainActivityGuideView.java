package com.ubt.alpha1e.ui.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.AppManager;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.bluetoothandnet.bluetoothandnetconnectstate.BluetoothandnetconnectstateActivity;
import com.ubt.alpha1e.bluetoothandnet.bluetoothguidestartrobot.BluetoothguidestartrobotActivity;
import com.ubt.alpha1e.course.feature.FeatureActivity;
import com.ubt.alpha1e.course.merge.MergeActivity;
import com.ubt.alpha1e.course.principle.PrincipleActivity;
import com.ubt.alpha1e.course.split.SplitActivity;
import com.ubt.alpha1e.event.RobotEvent;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;

/**
 * CommonGuideView
 *
 * @author wmma
 * @description 全局控制按钮导航
 * @date 2016/9/18
 * @update 
 */


public class MainActivityGuideView {

    private String TAG = "MainActivityGuideView";
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;

    private RelativeLayout rlGuideLayout;

    private boolean created = false;
    private RelativeLayout rlHabit;
    private RelativeLayout rlGuideHabit;
    private LinearLayout LlRemote;
    private RelativeLayout LlGuideRemote;
    private LinearLayout LlAction;
    private RelativeLayout rlGuideAction;
    private LinearLayout LlProgram;
    private RelativeLayout rlGuideProgram;
    private LinearLayout LlCommunity;
    private RelativeLayout rlGuideCommunity;
    private RelativeLayout rlBookCenter;
    private RelativeLayout rlGuideBookCenter;
    private ImageView top_icon2;
    private RelativeLayout rlGuideTopIcon2;
    private TextView jump_exit;


    public MainActivityGuideView(Context context) {
        mContext = context;
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
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |WindowManager.LayoutParams.FLAG_FULLSCREEN;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(180, 0, 0, 0));

        LayoutInflater inflater = LayoutInflater.from(mContext.getApplicationContext());
        rlGuideLayout = (RelativeLayout) inflater.inflate(R.layout.layout_mainui_guid, null);


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
        UbtLog.d(TAG,"lihai-------initGuideView----");
        rlHabit=(RelativeLayout)view.findViewById(R.id.rl_hibits_event);
        rlGuideHabit=(RelativeLayout)view.findViewById(R.id.rl_hibits_event_guids);
        LlRemote=(LinearLayout) view.findViewById(R.id.ll_remote);
        LlGuideRemote=(RelativeLayout) view.findViewById(R.id.ll_remote_guides);
        LlAction = (LinearLayout) view.findViewById(R.id.ll_action);
        rlGuideAction=(RelativeLayout)view.findViewById(R.id.rl_action_guides);
        LlProgram=(LinearLayout)view.findViewById(R.id.ll_program);
        rlGuideProgram=(RelativeLayout)view.findViewById(R.id.rl_program_guides);
        LlCommunity=(LinearLayout)view.findViewById(R.id.ll_community);
        rlGuideCommunity=(RelativeLayout)view.findViewById(R.id.rl_community_guides);
        rlBookCenter=(RelativeLayout)view.findViewById(R.id.rl_course_center);
        rlGuideBookCenter=(RelativeLayout)view.findViewById(R.id.rl_course_center_guides);
        top_icon2=(ImageView)view.findViewById(R.id.top_icon2);
        top_icon2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                closeGuideView();
                recordGuideState();
                RobotEvent robotEvent = new RobotEvent(RobotEvent.Event.ENTER_CONNECT_DEVICE);
                EventBus.getDefault().post(robotEvent);
            }
        });
        rlGuideTopIcon2=(RelativeLayout)view.findViewById(R.id.top_icon2_guides);
        jump_exit=(TextView)view.findViewById(R.id.jump_exit);
        jump_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeGuideView();
                recordGuideState();
            }
        });

    }

    private void setOnclick(final View view){

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rlGuideHabit.getVisibility()==View.VISIBLE){
                    rlGuideHabit.setVisibility(View.INVISIBLE);
                    rlHabit.setVisibility(View.INVISIBLE);
                    LlGuideRemote.setVisibility(View.VISIBLE);
                    LlRemote.setVisibility(View.VISIBLE);
                }else if(LlGuideRemote.getVisibility()==View.VISIBLE){
                    LlGuideRemote.setVisibility(View.INVISIBLE);
                    LlRemote.setVisibility(View.INVISIBLE);
                    rlGuideAction.setVisibility(View.VISIBLE);
                    LlAction.setVisibility(View.VISIBLE);
                }else if(rlGuideAction.getVisibility()==View.VISIBLE){
                    rlGuideAction.setVisibility(view.GONE);
                    LlAction.setVisibility(View.INVISIBLE);
                    rlGuideProgram.setVisibility(View.VISIBLE);
                    LlProgram.setVisibility(View.VISIBLE);
                } else if( rlGuideProgram.getVisibility()==View.VISIBLE){
                    rlGuideProgram.setVisibility(View.INVISIBLE);
                    LlProgram.setVisibility(View.INVISIBLE);
                    rlGuideCommunity.setVisibility(View.VISIBLE);
                    LlCommunity.setVisibility(View.VISIBLE);
                }else if(rlGuideCommunity.getVisibility()==View.VISIBLE){
                    rlGuideCommunity.setVisibility(View.INVISIBLE);
                    LlCommunity.setVisibility(View.INVISIBLE);
                    rlGuideBookCenter.setVisibility(View.VISIBLE);
                    rlGuideBookCenter.setVisibility(View.VISIBLE);
                }else if(rlGuideBookCenter.getVisibility()==View.VISIBLE){
                    rlGuideBookCenter.setVisibility(View.INVISIBLE);
                    rlGuideBookCenter.setVisibility(View.INVISIBLE);
                    top_icon2.setVisibility(View.VISIBLE);
                    rlGuideTopIcon2.setVisibility(View.VISIBLE);
                }else if(rlGuideTopIcon2.getVisibility()==View.VISIBLE){
                    top_icon2.setVisibility(View.INVISIBLE);
                    rlGuideTopIcon2.setVisibility(View.INVISIBLE);
                    closeGuideView();
                    recordGuideState();
                }
            }

        });
    }

    public void closeGuideView() {
        if(mWindowManager != null){
            mWindowManager.removeView(rlGuideLayout);
            mWindowManager = null;
        }
    }

    public void recordGuideState() {
        SPUtils.getInstance().put(Constant.SP_SHOW_COMMON_GUIDE, true);
    }

    public static boolean hasShowGuide() {
        return SPUtils.getInstance().getBoolean(Constant.SP_SHOW_COMMON_GUIDE,false);
    }
}
