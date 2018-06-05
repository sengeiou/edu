package com.ubt.alpha1e_edu.ui.custom;

import android.content.Context;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ubt.alpha1e_edu.R;


/**
 *
 *InstructionAlertView
 * @author wmma
 * @description 全局浮动控制窗口
 * @date 2016/10/25
 */


public class InstructionAlertView {

    private static final String TAG = "InstructionAlertView";
    //定义浮动窗口布局

    private WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;
    private int paddingBottomHeight ; //定义浮动按钮距离页面底部的高度


    private RelativeLayout mDialogLayout;

    private LinearLayout llInstrucitonPlayText;
    private LinearLayout llInstrucitonPlayAction;
    private View v_transparent = null;

    //初始默认横竖屛默认值 默认为横屏
    private int mScreenOrientation = 0;

    private Context mContext;
    private static InstructionAlertView mInstructionAlertView = null;

    public static InstructionAlertView getInstace(Context context){
        if(mInstructionAlertView != null){
            mInstructionAlertView.onDestroy();
        }
        mInstructionAlertView = new InstructionAlertView(context);
        return mInstructionAlertView;
    }

    public static void closeInstructionAlertView(){
        if(mInstructionAlertView != null){
            mInstructionAlertView.onDestroy();
        }
    }

    public  InstructionAlertView(Context context) {
        Log.d(TAG, "Float View  Created!");
        mContext = context;
        createFloatView();
    }


    private void createFloatView() {

        Log.d(TAG, "createFloatView!");

        wmParams = new WindowManager.LayoutParams();
        //通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        final float scale = mContext.getResources().getDisplayMetrics().density;
        paddingBottomHeight = (int)(50 * scale + 0.5f);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
//        wmParams.x = 16;
        wmParams.y = paddingBottomHeight;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        //获取浮动窗口视图所在布局
        mDialogLayout = (RelativeLayout) inflater.inflate(R.layout.view_instruction_dialog_portrait, null);

        //初始化控件和设置点击事件
        initView(mDialogLayout);

        wmParams.gravity = Gravity.BOTTOM;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(120, 0, 0, 0));
        mDialogLayout.setBackground(colorDrawable);
        mWindowManager.addView(mDialogLayout, wmParams);

    }

    private void initView(View view) {

        llInstrucitonPlayText = (LinearLayout) view.findViewById(R.id.ll_instruciton_play_text);
        llInstrucitonPlayAction = (LinearLayout) view.findViewById(R.id.ll_instruciton_play_action);
        v_transparent = view.findViewById(R.id.v_transparent);

        v_transparent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onDestroy();
                return false;
            }
        });
    }

    public InstructionAlertView setPlayTextListener(final View.OnClickListener listener) {

        llInstrucitonPlayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                onDestroy();
            }
        });

        return mInstructionAlertView;
    }

    public InstructionAlertView setPlayActionListener(final View.OnClickListener listener) {

        llInstrucitonPlayAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                onDestroy();
            }
        });
        return this;
    }

    public InstructionAlertView setScreenOrientation(int screenOrientation) {
        mScreenOrientation = screenOrientation;
        return mInstructionAlertView;
    }

    public void onDestroy() {
        if(mInstructionAlertView != null){
            mWindowManager.removeView(mDialogLayout);
            mInstructionAlertView = null;
        }
    }

}


