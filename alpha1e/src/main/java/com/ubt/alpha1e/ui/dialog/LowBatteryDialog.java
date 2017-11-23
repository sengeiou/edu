package com.ubt.alpha1e.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;

/**
 * Created by liuqiang on 10/23/15.
 */
public class LowBatteryDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_lowBattery;
    private Display display;
    int power=0;
    private boolean showTitle = false;
    private boolean showMsg = false;


    public LowBatteryDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public LowBatteryDialog setBatteryThresHold(int value){
           power=value;
        return this ;
    }

    public LowBatteryDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.view_lowbatterydialog, null);

        // 获取自定义Dialog布局中的控件
       // lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_lowBattery = (TextView) view.findViewById(R.id.txt_lowBattery);
        txt_lowBattery.setText("电量低于"+power+"%");
        txt_lowBattery.setVisibility(View.VISIBLE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
//        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
//                .getWidth() * 0.75), LinearLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }




    public LowBatteryDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }




    public void show() {
        dialog.show();
    }
}
