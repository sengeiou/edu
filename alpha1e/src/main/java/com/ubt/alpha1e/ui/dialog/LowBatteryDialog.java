package com.ubt.alpha1e.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
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
import com.ubt.alpha1e.base.AppManager;

/**
 * Created by liuqiang on 10/23/15.
 */
public class LowBatteryDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_lowBattery;
    private ImageView mBatteryIcon;

    private Display display;
    int power=0;
    private boolean showTitle = false;
    private boolean showMsg = false;
    protected Handler mHandler = new Handler();
    private int DIALOG_SHOW_TIMING=2000;//EQUAL SHORT TOAST 2000, LONG TOAST 3500

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
        txt_lowBattery.setText(R.string.lowpower_indicator_dialog);
        txt_lowBattery.setVisibility(View.VISIBLE);

        mBatteryIcon=(ImageView)view.findViewById(R.id.lowbattery_icon);

        if(power==5){
            mBatteryIcon.setBackground(context.getDrawable(R.drawable.img_tip_power_5));
        }else if(power==20){
            mBatteryIcon.setBackground(context.getDrawable(R.drawable.img_tip_power_20));
        }
        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        //2秒后自动消失
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                     cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },DIALOG_SHOW_TIMING);
        return this;
    }


    public LowBatteryDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }
    public boolean isShowing(){
        return dialog.isShowing();
    }
   public void cancel(){
        dialog.cancel();
   }
    public void show() {
        dialog.show();
    }
}
