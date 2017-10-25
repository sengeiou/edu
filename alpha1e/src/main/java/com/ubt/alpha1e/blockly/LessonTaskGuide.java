package com.ubt.alpha1e.blockly;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 *
 *LessonTaskGuide
 * @author wmma
 * @description 课程导航指引
 * @date 2016/10/25
 */


public class LessonTaskGuide {

    private static final String TAG = "LessonTaskGuide";

    //定义浮动窗口布局
    private RelativeLayout mFloatLayout,rlLessonTaskDetail,rlLessonTaskDetail1;
    private WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    private Context mContext;
    private static LessonTaskGuide mLessonTaskGuide = null;

    private ImageView ivTaskGuide,iv_lesson_task_detail;
    private Animation animTaskGuide;
    private int scale = 0;

    private boolean isShowLessonTaskDetail = true;

    public static LessonTaskGuide getInstace(Context context){
        if(mLessonTaskGuide != null){
            mLessonTaskGuide.onDestroy();
            mLessonTaskGuide = null;
        }
        mLessonTaskGuide = new LessonTaskGuide(context);
        return mLessonTaskGuide;
    }

    public static void closeLessonTaskGuide(){
        if(mLessonTaskGuide != null){
            UbtLog.d(TAG,"mLessonTaskGuide = " + mLessonTaskGuide.mContext + "  = " + ((BlocklyCourseActivity)(mLessonTaskGuide.mContext)).isFinishing());
            mLessonTaskGuide.onDestroy();
            mLessonTaskGuide = null;
        }
    }

    public LessonTaskGuide(Context context) {
        Log.d(TAG, "Float View  Created!");
        mContext = context;
    }

    public void show(){
        createFloatView();
    }

    private void createFloatView() {
        boolean isPad = AlphaApplication.isPad();
        scale = (int)mContext.getResources().getDisplayMetrics().density;
        int layoutId = R.layout.view_lesson_task_guide;

        Log.d(TAG, "createFloatView!");

        wmParams = new WindowManager.LayoutParams();
        //通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.CENTER | Gravity.TOP;


        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        //获取浮动窗口视图所在布局
        mFloatLayout = (RelativeLayout) inflater.inflate(layoutId, null);

        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(150, 0, 0, 0));
        if (Build.VERSION.SDK_INT >= 16) {
            mFloatLayout.setBackground(colorDrawable);
        }else{
            mFloatLayout.setBackgroundDrawable(colorDrawable);
        }

        initView(mFloatLayout);

        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "--LessonTask onTouched--");
                return false;
            }
        });

        mWindowManager.addView(mFloatLayout, wmParams);
    }

    /**
     * 初始化控件和设置点击事件
     * @param view
     */
    private void initView(View view) {
        animTaskGuide = new TranslateAnimation(0, 50*scale, 0, 0);
        animTaskGuide.setDuration(1000);
        //animTaskGuide.setRepeatCount(1000);
        animTaskGuide.setRepeatCount(1);
        animTaskGuide.setRepeatMode(Animation.REVERSE);

        ivTaskGuide = (ImageView) view.findViewById(R.id.iv_task_guide);
        iv_lesson_task_detail = (ImageView) view.findViewById(R.id.iv_lesson_task_detail);
        ivTaskGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeLessonTaskGuide();
            }
        });

        View v = view.findViewById(R.id.rl_lesson_task_operation);
        rlLessonTaskDetail = (RelativeLayout) view.findViewById(R.id.rl_lesson_task_detail);
        rlLessonTaskDetail1 = (RelativeLayout) view.findViewById(R.id.rl_lesson_task_detail1);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrHiddenTaskDetail();
            }
        });

        ivTaskGuide.startAnimation(animTaskGuide);
    }

    private void showOrHiddenTaskDetail(){
        UbtLog.d(TAG,"showOrHiddenTaskDetail == " + isShowLessonTaskDetail);
        if(isShowLessonTaskDetail){
            isShowLessonTaskDetail = false;
            Animation slideOutAnimation = AnimationUtils.loadAnimation(mContext,R.anim.slide_out_up);
            rlLessonTaskDetail.startAnimation(slideOutAnimation);
            //slideOutAnimation.setFillAfter(true);
            UbtLog.d(TAG,"rlLessonTaskDetail.getTop() ==>> " + rlLessonTaskDetail.getTop() + "     rlLessonTaskDetail.getHeight() = " +rlLessonTaskDetail.getHeight());
            slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    rlLessonTaskDetail.clearAnimation();//解决闪烁的关键代码
                    rlLessonTaskDetail1.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }else {

            isShowLessonTaskDetail = true;
            Animation slideInAnimation = AnimationUtils.loadAnimation(mContext,R.anim.slide_in_up);
            rlLessonTaskDetail.startAnimation(slideInAnimation);
            rlLessonTaskDetail1.setVisibility(View.VISIBLE);

        }
    }

    public void onDestroy() {
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
    }


}


