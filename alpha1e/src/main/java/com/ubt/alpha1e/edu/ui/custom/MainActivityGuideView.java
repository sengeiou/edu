package com.ubt.alpha1e.edu.ui.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.Constant;
import com.ubt.alpha1e.edu.base.SPUtils;
import com.ubt.alpha1e.edu.event.RobotEvent;
import com.ubt.alpha1e.edu.utils.SizeUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * CommonGuideView
 *
 * @author wmma
 * @description 全局控制按钮导航
 * @date 2016/9/18
 * @update 
 */


public class MainActivityGuideView{

    private String TAG = "MainActivityGuideView";
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;


    private RelativeLayout rlGuideLayout;

    private boolean created = false;
    private TextView jump_exit;
    static MainActivityGuideView  mMainActivityGuideView;
    Unbinder mUnbinder;

    @BindView(R.id.rl_hibits_event)
    RelativeLayout rlHibitsEvent;
    @BindView(R.id.rl_hibits_event_guids)
    RelativeLayout  rlHibitsGuideEvent;
    @BindView(R.id.iv_hibits_reminder)
    ImageView habits_reminder;
    @BindView(R.id.iv_habits)
    ImageView ivHabits;
    @BindView(R.id.ll_remote)
    LinearLayout llRemote;
    @BindView(R.id.ll_remote_guides)
    RelativeLayout LlGuideRemote;
    @BindView(R.id.iv_remote)
    ImageView ivRemote;
    @BindView(R.id.ll_action)
    LinearLayout llAction;
    @BindView(R.id.rl_action_guides)
    RelativeLayout rlActionGuides;
    @BindView(R.id.iv_action)
    ImageView ivAction;
    @BindView(R.id.ll_program)
    LinearLayout llProgram;
    @BindView(R.id.rl_program_guides)
    RelativeLayout rlProgramGuides;
    @BindView(R.id.iv_program)
    ImageView ivProgram;
    @BindView(R.id.ll_community)
    LinearLayout llCommunity;
    @BindView(R.id.rl_community_guides)
    RelativeLayout rlCommunityGuides;
    @BindView(R.id.iv_community)
    ImageView ivCommunity;
    @BindView(R.id.tv_community)
    TextView tvCommunity;
    @BindView(R.id.rl_course_center)
    RelativeLayout rlCourseCenter;
    @BindView(R.id.rl_course_center_guides)
    RelativeLayout rlCourseCenterGuides;
    @BindView(R.id.iv_course)
    ImageView ivCourse;
    @BindView(R.id.top_icon)
    ImageView topIcon;
    @BindView(R.id.top_icon2)
    ImageView topIcon2;
    @BindView(R.id.top_icon2_guides)
    RelativeLayout rltoopIcon2Guide;
    @BindView(R.id.top_icon2_disconnect)
    ImageView topIcon2Disconnect;
    @BindView(R.id.top_icon3)
    ImageView topIcon3;

    private boolean hasInitUI = false;


    public static MainActivityGuideView  getInstant(Context mContext){
          if(mMainActivityGuideView==null){
               mMainActivityGuideView=new MainActivityGuideView(mContext);
          }
          return mMainActivityGuideView;
    }

    public MainActivityGuideView(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext.getApplicationContext());
        rlGuideLayout = (RelativeLayout) inflater.inflate(R.layout.layout_mainui_guid, null);
        mUnbinder = ButterKnife.bind(this,rlGuideLayout);
        createGuideView();
        recordGuideState();
        initView();

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
        topIcon2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                closeGuideView();
                recordGuideState();
                RobotEvent robotEvent = new RobotEvent(RobotEvent.Event.ENTER_CONNECT_DEVICE);
                EventBus.getDefault().post(robotEvent);
            }
        });
        jump_exit=(TextView)view.findViewById(R.id.jump_exit);
        jump_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeGuideView();
                recordGuideState();
            }
        });

    }

    private void initView() {
        {
            if(SPUtils.getInstance().getBoolean(Constant.SP_EDU_MODULE, false)){
                ivCommunity.setImageResource(R.drawable.ic_home_community_grey);
                tvCommunity.setAlpha(0.2f);
            }

            if (SizeUtils.isComprehensiveScreen(mContext)||AlphaApplication.isPad()) {
                rlHibitsEvent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        UbtLog.d(TAG, "rlHibitsEvent = " + rlHibitsEvent);
                        if (rlHibitsEvent != null) {
                            rlHibitsEvent.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!hasInitUI && rlHibitsEvent.getHeight() > 0) {
                                        hasInitUI = true;
                                        // 获取屏幕密度（方法2）
                                        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
                                        UbtLog.d(TAG, "isComprehensiveScreen = " + SizeUtils.isComprehensiveScreen(mContext) + "/" + dm.density + "/" + dm.scaledDensity);

                                        float tensileRatio = 1.0f * dm.widthPixels / 1920;//屏幕拉升比例，以宽度1920为基准
                                        float densityRatio = 3.0f / dm.density;//屏幕密度比例，以密度3.0为基准

                                        //行为习惯按键
                                        RelativeLayout.LayoutParams rlHibitsLp = (RelativeLayout.LayoutParams) rlHibitsEvent.getLayoutParams();
                                        rlHibitsLp.leftMargin = (int) (rlHibitsLp.leftMargin * densityRatio * tensileRatio);
                                        UbtLog.d(TAG, "rlHibitsLp.leftMargin = " + rlHibitsLp.leftMargin + "/" + rlHibitsLp.width + "/" + rlHibitsLp.height);
                                        rlHibitsEvent.setLayoutParams(rlHibitsLp);

                                        RelativeLayout.LayoutParams ivHibitsLp = (RelativeLayout.LayoutParams) ivHabits.getLayoutParams();
                                        ivHibitsLp.width = (int) (ivHabits.getWidth() * densityRatio * tensileRatio);
                                        ivHibitsLp.height = (int) (ivHabits.getHeight() * densityRatio);
                                        UbtLog.d(TAG, "ivHibitsLp.width = " + ivHibitsLp.width + "/" + ivHibitsLp.height);
                                        ivHabits.setLayoutParams(ivHibitsLp);

                                        //课程中心按键
                                        RelativeLayout.LayoutParams rlCourseLp = (RelativeLayout.LayoutParams) rlCourseCenter.getLayoutParams();
                                        rlCourseLp.rightMargin = (int) (rlCourseLp.rightMargin * densityRatio * tensileRatio);
                                        UbtLog.d(TAG, "rlCourseLp.rightMargin = " + rlCourseLp.rightMargin);
                                        rlCourseCenter.setLayoutParams(rlCourseLp);

                                        RelativeLayout.LayoutParams ivCourseLp = (RelativeLayout.LayoutParams) ivCourse.getLayoutParams();
                                        ivCourseLp.width = (int) (ivCourse.getWidth() * densityRatio * tensileRatio);
                                        ivCourseLp.height = (int) (ivCourse.getHeight() * densityRatio);
                                        UbtLog.d(TAG, "ivCourseLp.width = " + ivCourseLp.width);
                                        ivCourse.setLayoutParams(ivCourseLp);

                                        //个人中心按键
                                        RelativeLayout.LayoutParams topIconLp = (RelativeLayout.LayoutParams) topIcon.getLayoutParams();
                                        topIconLp.leftMargin = (int) (topIconLp.leftMargin * densityRatio * tensileRatio);
                                        topIconLp.topMargin = (int) (topIconLp.topMargin * densityRatio);
                                        topIconLp.width = (int) (topIcon.getWidth() * densityRatio * tensileRatio);
                                        topIconLp.height = (int) (topIcon.getHeight() * densityRatio);
                                        topIcon.setLayoutParams(topIconLp);

                                        //蓝牙连接按键
                                        RelativeLayout.LayoutParams topIcon2Lp = (RelativeLayout.LayoutParams) topIcon2.getLayoutParams();
                                        topIcon2Lp.topMargin = (int) (topIcon2Lp.topMargin * densityRatio);
                                        topIcon2Lp.width = (int) (topIcon2.getWidth() * densityRatio * tensileRatio);
                                        topIcon2Lp.height = (int) (topIcon2.getHeight() * densityRatio);
                                        topIcon2.setLayoutParams(topIcon2Lp);

                                        RelativeLayout.LayoutParams topIcon2DisconnectLp = (RelativeLayout.LayoutParams) topIcon2Disconnect.getLayoutParams();
                                        topIcon2DisconnectLp.topMargin = (int) (topIcon2DisconnectLp.topMargin * densityRatio);
                                        topIcon2DisconnectLp.width = (int) (topIcon2Disconnect.getWidth() * densityRatio * tensileRatio);
                                        topIcon2DisconnectLp.height = (int) (topIcon2Disconnect.getHeight() * densityRatio);
                                        topIcon2Disconnect.setLayoutParams(topIcon2DisconnectLp);

//                                    //指令按键
//                                    RelativeLayout.LayoutParams topIcon4Lp = (RelativeLayout.LayoutParams) topIcon4.getLayoutParams();
//                                    topIcon4Lp.topMargin = (int) (topIcon4Lp.topMargin * densityRatio);
//                                    topIcon4Lp.width = (int) (topIcon4.getWidth() * densityRatio * tensileRatio);
//                                    topIcon4Lp.height = (int) (topIcon4.getHeight() * densityRatio);
//                                    topIcon4.setLayoutParams(topIcon4Lp);

                                        //播放按键开始
                                        RelativeLayout.LayoutParams topIcon3Lp = (RelativeLayout.LayoutParams) topIcon3.getLayoutParams();
                                        topIcon3Lp.rightMargin = (int) (topIcon3Lp.rightMargin * densityRatio * tensileRatio);
                                        topIcon3Lp.topMargin = (int) (topIcon3Lp.topMargin * densityRatio);
                                        topIcon3Lp.width = (int) (topIcon3.getWidth() * densityRatio * tensileRatio);
                                        topIcon3Lp.height = (int) (topIcon3.getHeight() * densityRatio);
                                        topIcon3.setLayoutParams(topIcon3Lp);

//                                    RelativeLayout.LayoutParams indicatorLp = (RelativeLayout.LayoutParams) indicator.getLayoutParams();
//                                    indicatorLp.rightMargin = (int) (indicatorLp.rightMargin * densityRatio * tensileRatio);
//                                    indicatorLp.topMargin = (int) (indicatorLp.topMargin * densityRatio);
//                                    indicatorLp.width = (int) (indicator.getWidth() * densityRatio * tensileRatio);
//                                    indicatorLp.height = (int) (indicator.getHeight() * densityRatio);
//                                    indicator.setLayoutParams(indicatorLp);
//
//                                    RelativeLayout.LayoutParams actionIndicatorLp = (RelativeLayout.LayoutParams) actionIndicator.getLayoutParams();
//                                    actionIndicatorLp.rightMargin = (int) (actionIndicatorLp.rightMargin * densityRatio * tensileRatio);
//                                    actionIndicatorLp.topMargin = (int) (actionIndicatorLp.topMargin * densityRatio);
//                                    actionIndicatorLp.width = (int) (actionIndicator.getWidth() * densityRatio * tensileRatio);
//                                    actionIndicatorLp.height = (int) (actionIndicator.getHeight() * densityRatio);
//                                    actionIndicator.setLayoutParams(actionIndicatorLp);
                                        //播放按键结束

                                        //动作按键
                                        RelativeLayout.LayoutParams llActionLp = (RelativeLayout.LayoutParams) llAction.getLayoutParams();
                                        llActionLp.bottomMargin = (int) (llActionLp.bottomMargin * densityRatio);
                                        llActionLp.rightMargin = (int) (llActionLp.rightMargin * densityRatio);
                                        llAction.setLayoutParams(llActionLp);

                                        LinearLayout.LayoutParams ivActionLp = (LinearLayout.LayoutParams) ivAction.getLayoutParams();
                                        ivActionLp.width = (int) (ivAction.getWidth() * densityRatio * tensileRatio);
                                        ivActionLp.height = (int) (ivAction.getHeight() * densityRatio);
                                        ivAction.setLayoutParams(ivActionLp);

                                        //遥控器按键
                                        RelativeLayout.LayoutParams llRemoteLp = (RelativeLayout.LayoutParams) llRemote.getLayoutParams();
                                        llRemoteLp.bottomMargin = (int) (llRemoteLp.bottomMargin * densityRatio);
                                        llRemoteLp.rightMargin = (int) (llRemoteLp.rightMargin * densityRatio);
                                        llRemote.setLayoutParams(llRemoteLp);

                                        LinearLayout.LayoutParams ivRemoteLp = (LinearLayout.LayoutParams) ivRemote.getLayoutParams();
                                        ivRemoteLp.width = (int) (ivRemote.getWidth() * densityRatio * tensileRatio);
                                        ivRemoteLp.height = (int) (ivRemote.getHeight() * densityRatio);
                                        ivRemote.setLayoutParams(ivRemoteLp);

                                        //编程按键
                                        RelativeLayout.LayoutParams llProgramLp = (RelativeLayout.LayoutParams) llProgram.getLayoutParams();
                                        llProgramLp.bottomMargin = (int) (llProgramLp.bottomMargin * densityRatio);
                                        llProgramLp.leftMargin = (int) (llProgramLp.leftMargin * densityRatio);
                                        llProgram.setLayoutParams(llProgramLp);

                                        LinearLayout.LayoutParams ivProgramLp = (LinearLayout.LayoutParams) ivProgram.getLayoutParams();
                                        ivProgramLp.width = (int) (ivProgram.getWidth() * densityRatio * tensileRatio);
                                        ivProgramLp.height = (int) (ivProgram.getHeight() * densityRatio);
                                        ivProgram.setLayoutParams(ivProgramLp);

                                        //社区按键
                                        RelativeLayout.LayoutParams llCommunityLp = (RelativeLayout.LayoutParams) llCommunity.getLayoutParams();
                                        llCommunityLp.bottomMargin = (int) (llCommunityLp.bottomMargin * densityRatio);
                                        llCommunityLp.leftMargin = (int) (llCommunityLp.leftMargin * densityRatio);
                                        llCommunity.setLayoutParams(llCommunityLp);

                                        LinearLayout.LayoutParams ivCommunityLp = (LinearLayout.LayoutParams) ivCommunity.getLayoutParams();
                                        ivCommunityLp.width = (int) (ivCommunity.getWidth() * densityRatio * tensileRatio);
                                        ivCommunityLp.height = (int) (ivCommunity.getHeight() * densityRatio);
                                        ivCommunity.setLayoutParams(ivCommunityLp);
                                        //社区按键
//                                    RelativeLayout.LayoutParams buddleTextLp = (RelativeLayout.LayoutParams) buddleText.getLayoutParams();
//                                    buddleTextLp.topMargin = (int) (buddleTextLp.topMargin * densityRatio);
//                                    buddleTextLp.leftMargin = (int) (buddleTextLp.leftMargin * densityRatio);
//                                    buddleText.setLayoutParams(buddleTextLp);
                                    }
                                }
                            });
                        }
                    }
                });
            }


        }

    }
    private void setOnclick(final View view){

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rlHibitsGuideEvent.getVisibility()==View.VISIBLE){
                   rlHibitsGuideEvent.setVisibility(View.INVISIBLE);
                   rlHibitsEvent.setVisibility(View.INVISIBLE);
                   LlGuideRemote.setVisibility(View.VISIBLE);
                   llRemote.setVisibility(View.VISIBLE);
                }else if(LlGuideRemote.getVisibility()==View.VISIBLE){
                    LlGuideRemote.setVisibility(View.INVISIBLE);
                    llRemote.setVisibility(View.INVISIBLE);
                    llAction.setVisibility(View.VISIBLE);
                    rlActionGuides.setVisibility(View.VISIBLE);
                }else if(rlActionGuides.getVisibility()==View.VISIBLE) {
                    llAction.setVisibility(View.INVISIBLE);
                    rlActionGuides.setVisibility(View.INVISIBLE);
                    llProgram.setVisibility(View.VISIBLE);
                    rlProgramGuides.setVisibility(View.VISIBLE);
                }else if(rlProgramGuides.getVisibility()==View.VISIBLE) {
                    llProgram.setVisibility(View.INVISIBLE);
                    rlProgramGuides.setVisibility(View.INVISIBLE);
                    llCommunity.setVisibility(View.VISIBLE);
                    rlCommunityGuides.setVisibility(View.VISIBLE);
                }else if(rlCommunityGuides.getVisibility()==View.VISIBLE) {
                    llCommunity.setVisibility(View.INVISIBLE);
                    rlCommunityGuides.setVisibility(View.INVISIBLE);
                    rlCourseCenter.setVisibility(View.VISIBLE);
                    rlCourseCenterGuides.setVisibility(View.VISIBLE);
                }else if(rlCourseCenterGuides.getVisibility()==View.VISIBLE) {
                    rlCourseCenter.setVisibility(View.INVISIBLE);
                    rlCourseCenterGuides.setVisibility(View.INVISIBLE);
                    rltoopIcon2Guide.setVisibility(View.VISIBLE);
                    topIcon2.setVisibility(View.VISIBLE);
                    jump_exit.setVisibility(View.INVISIBLE);
                }else if(rltoopIcon2Guide.getVisibility()==View.VISIBLE){
                    rltoopIcon2Guide.setVisibility(View.INVISIBLE);
                    topIcon2.setVisibility(View.INVISIBLE);
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
            mUnbinder.unbind();
        }
    }

    public void recordGuideState() {
        SPUtils.getInstance().put(Constant.SP_SHOW_MAINUI_GUIDE, true);
    }

    public static boolean hasShowGuide() {
        return SPUtils.getInstance().getBoolean(Constant.SP_SHOW_MAINUI_GUIDE,false);
    }
}
