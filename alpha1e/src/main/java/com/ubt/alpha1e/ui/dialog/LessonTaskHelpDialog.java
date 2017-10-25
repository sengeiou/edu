package com.ubt.alpha1e.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;

/**
 * Created by lihai on 5/16/17.
 */
public class LessonTaskHelpDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;

    private ImageView ivLessonPosition;
    private TextView tvLessonTaskHelp;
    private Display display;

    public LessonTaskHelpDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public LessonTaskHelpDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_lesson_task_help_dialog, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);

        ivLessonPosition = (ImageView) view.findViewById(R.id.iv_lesson_position);
        tvLessonTaskHelp = (TextView) view.findViewById(R.id.tv_lesson_task_help);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.55), LinearLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }

    public LessonTaskHelpDialog setMsg(String msg) {
        tvLessonTaskHelp.setText(msg);
        return this;
    }

    public LessonTaskHelpDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public LessonTaskHelpDialog setPositiveButton(final View.OnClickListener listener) {
        ivLessonPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setLayout() {

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
