package com.ubt.alpha1e.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
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

/**
 * Created by liuqiang on 10/23/15.
 */
public class UpdateAlertDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout llBg;
    private TextView tvTitle;
    private TextView tvUpgradeDes;
    private TextView tvVersionMsg;
    private TextView tvMsg;
    private Button btnNeg;
    private Button btnPos;
    private ImageView ivLine;
    private Display display;

    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;

    private int mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    public UpdateAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public UpdateAlertDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_update_robot, null);

        // 获取自定义Dialog布局中的控件
        llBg = (LinearLayout) view.findViewById(R.id.ll_bg);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.GONE);
        tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        tvMsg.setVisibility(View.GONE);

        tvUpgradeDes = (TextView) view.findViewById(R.id.tv_upgrade_des);
        tvVersionMsg = (TextView) view.findViewById(R.id.tv_version_msg);

        btnNeg = (Button) view.findViewById(R.id.btn_neg);
        btnNeg.setVisibility(View.GONE);
        btnPos = (Button) view.findViewById(R.id.btn_pos);
        btnPos.setVisibility(View.GONE);
        ivLine = (ImageView) view.findViewById(R.id.iv_line);
        ivLine.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        float witchProportion = 0.75f;//竖屏
        if(mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            witchProportion = 0.5f;
        }

        // 调整dialog背景大小
        llBg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * witchProportion), LinearLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }

    public UpdateAlertDialog setScreenOrientation(int screenOrientation){
        mScreenOrientation = screenOrientation;
        return this;
    }

    public UpdateAlertDialog setTitle(String title) {
        showTitle = true;
        tvTitle.setText(title);
        return this;
    }

    public UpdateAlertDialog setMsg(String msg) {
        showMsg = true;
        tvMsg.setText(msg);
        return this;
    }

    public UpdateAlertDialog setUpgradeDes(String upgradeDes) {
        tvUpgradeDes.setText(upgradeDes);
        return this;
    }

    public UpdateAlertDialog setVersionMsg(String versionMsg) {
        tvVersionMsg.setText(versionMsg);
        return this;
    }

    public UpdateAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public UpdateAlertDialog setPositiveButton(String text,
                                               final View.OnClickListener listener) {
        showPosBtn = true;
        btnPos.setText(text);
        btnPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public UpdateAlertDialog setNegativeButton(String text,
                                               final View.OnClickListener listener) {
        showNegBtn = true;
        btnNeg.setText(text);
        btnNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setLayout() {

        if (showTitle) {
            tvTitle.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            tvMsg.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            btnPos.setText(context.getString(R.string.ui_common_confirm));
            btnPos.setVisibility(View.VISIBLE);
            btnPos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            btnPos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btnPos.setVisibility(View.VISIBLE);
            btnPos.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btnNeg.setVisibility(View.VISIBLE);
            btnNeg.setBackgroundResource(R.drawable.alertdialog_left_selector);
            ivLine.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btnPos.setVisibility(View.VISIBLE);
            btnPos.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btnNeg.setVisibility(View.VISIBLE);
            btnNeg.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }
    }

    public void show() {
        setLayout();
        dialog.show();
    }
}
