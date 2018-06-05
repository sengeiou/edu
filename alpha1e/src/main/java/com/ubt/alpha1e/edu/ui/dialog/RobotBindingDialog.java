package com.ubt.alpha1e.edu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.ubt.alpha1e.edu.R;

/**
 * Created by lihai on 5/16/17.
 */
public class RobotBindingDialog {
    private Context context;
    private Dialog dialog;
    private Display display;

    public RobotBindingDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public RobotBindingDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_binding, null);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);


        return this;
    }


    public RobotBindingDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
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
