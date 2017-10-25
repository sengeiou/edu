package com.ubt.alpha1e.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ubt.alpha1e.R;

/**
 * Created by liuqiang on 10/23/15.
 */
public class InstructionAlertDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private LinearLayout llInstrucitonPlayText;
    private LinearLayout llInstrucitonPlayAction;

    //初始默认横竖屛默认值 默认为横屏
    private int mScreenOrientation = 0;

    private Display display;

    public InstructionAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public InstructionAlertDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.view_instruction_dialog_landscape, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        llInstrucitonPlayText = (LinearLayout) view.findViewById(R.id.ll_instruciton_play_text);
        llInstrucitonPlayAction = (LinearLayout) view.findViewById(R.id.ll_instruciton_play_action);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        /*lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.75), LinearLayout.LayoutParams.WRAP_CONTENT));*/

        return this;
    }

    public InstructionAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public InstructionAlertDialog setPlayTextListener(final View.OnClickListener listener) {

        llInstrucitonPlayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });

        return this;
    }

    public InstructionAlertDialog setPlayActionListener(final View.OnClickListener listener) {

        llInstrucitonPlayAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });

        return this;
    }

    public void show() {
        dialog.show();
    }
}
