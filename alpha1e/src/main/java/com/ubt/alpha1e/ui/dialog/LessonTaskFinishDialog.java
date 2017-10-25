package com.ubt.alpha1e.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.model.LessonTaskInfo;
import com.ubt.alpha1e.ui.BaseActivity;

/**
 * Created by liuqiang on 10/23/15.
 */
public class LessonTaskFinishDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView tvTaskError,tvTaskTime;
    private ImageView ivTaskError,ivTaskTime;

    private Button btn_neg;
    private Button btn_pos;
    private ImageView img_line;
    private ImageView btn_line;
    private Display display;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;

    private LessonTaskInfo mTaskInfo = null;
    private int mChallengeTime;
    private int mChallengeFailCount;

    public LessonTaskFinishDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public LessonTaskFinishDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_lesson_task_finish_dialog, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        tvTaskError = (TextView) view.findViewById(R.id.tv_task_error);
        tvTaskTime = (TextView) view.findViewById(R.id.tv_task_time);

        ivTaskError = (ImageView) view.findViewById(R.id.iv_task_error);
        ivTaskTime = (ImageView) view.findViewById(R.id.iv_task_time);

        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);
        img_line = (ImageView) view.findViewById(R.id.img_line);
        img_line.setVisibility(View.GONE);
        btn_line = (ImageView) view.findViewById(R.id.btn_line);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.75), LinearLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }

    public LessonTaskFinishDialog setData(LessonTaskInfo taskInfo,int challengeTime,int challengeFailCount) {
        mTaskInfo = taskInfo;
        mChallengeTime = challengeTime;
        mChallengeFailCount = challengeFailCount;
        return this;
    }

    public LessonTaskFinishDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public LessonTaskFinishDialog setPositiveButton(String text,
                                                    final View.OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public LessonTaskFinishDialog setNegativeButton(String text,
                                                    final View.OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("取消");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private String getChallengeTime(){
        String minute = (mChallengeTime/60) + "";
        String second = (mChallengeTime%60) + "";
        if(minute.length() < 2){
            minute = "0" + minute;
        }
        if(second.length() < 2){
            second = "0" + second;
        }
        return minute + ":" + second;
    }

    private void setLayout() {

        tvTaskError.setText(((BaseActivity)context).getStringResources("ui_task_error") + " " + mChallengeFailCount);
        tvTaskTime.setText(((BaseActivity)context).getStringResources("ui_task_time") + " "+ getChallengeTime());

        if(mChallengeFailCount > 0){
            ivTaskError.setImageResource(R.drawable.icon_stars);
        }else {
            ivTaskError.setImageResource(R.drawable.icon_stars_sel);
        }

        if(mChallengeTime > mTaskInfo.task_duration){
            ivTaskTime.setImageResource(R.drawable.icon_stars);
        }else {
            ivTaskTime.setImageResource(R.drawable.icon_stars_sel);
        }

        if (!showPosBtn && !showNegBtn) {
            btn_line.setVisibility(View.GONE);
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }
    }

    public void show() {
        setLayout();
        dialog.show();
    }

    public void display(){
        dialog.dismiss();
    }

    public boolean isShowing(){
        return dialog.isShowing();
    }
}
