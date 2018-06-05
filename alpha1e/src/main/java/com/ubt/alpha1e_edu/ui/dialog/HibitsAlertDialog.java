package com.ubt.alpha1e_edu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.base.ToastUtils;
import com.ubt.alpha1e_edu.services.HibitsAlertService;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.weigan.loopview.LoopView;

import java.util.Arrays;

/**
 * Created by liuqiang on 10/23/15.
 */
public class HibitsAlertDialog {

    private static final String TAG = HibitsAlertDialog.class.getSimpleName();
    private static final int ALERT_TIME_OVER = 1;
    private static final int ALERT_TIME_LIMITE = 2*60*1000;//2分钟

    private Context mContext;
    private Dialog dialog;
    private LinearLayout lLayout_bg;

    private TextView tvMsg;
    private LoopView lvAlert;

    private Button btnCancel;
    private Button btnConfirm;

    private Display display;
    private String[] mAlert = {"准时提醒", "延时5分钟", "延时10分钟", "关闭提醒"};
    private String[] mAlertVal = {"0", "5", "10", "-1"};


    private String mEventId = "";
    private boolean isTimeOver = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ALERT_TIME_OVER:
                    isTimeOver = true;
                    break;
            }
        }
    };

    public HibitsAlertDialog(Context context) {
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public HibitsAlertDialog builder() {
            // 获取Dialog布局
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_hibits_alert, null);

            // 获取自定义Dialog布局中的控件
            lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);

            tvMsg = (TextView) view.findViewById(R.id.tv_msg);
            lvAlert = (LoopView) view.findViewById(R.id.lv_alert);

            lvAlert.setCenterTextColor(mContext.getResources().getColor(R.color.tv_center_color));
            lvAlert.setItems(Arrays.asList(mAlert));
            lvAlert.setInitPosition(0);
            lvAlert.setCurrentPosition(0);

            btnCancel = (Button) view.findViewById(R.id.btn_cancel);
            btnConfirm = (Button) view.findViewById(R.id.btn_confirm);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UbtLog.d(TAG,"mEventId = " + mEventId + "  DelayTime = " + mAlertVal[lvAlert.getSelectedItem()] + " isTimeOver = " + isTimeOver);
                    if(!isTimeOver){
                        Intent mIntent = new Intent(mContext, HibitsAlertService.class);
                        mIntent.putExtra("EventId",mEventId);
                        mIntent.putExtra("DelayTime",mAlertVal[lvAlert.getSelectedItem()]);
                        mContext.startService(mIntent);
                    }else {
                        ToastUtils.showShort("该事项已经确认，无法更改");
                    }
                    dialog.dismiss();
                }
            });

            // 定义Dialog布局和参数
            dialog = new Dialog(mContext, R.style.NewAlertDialogStyle);
            dialog.setContentView(view);

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if(mHandler.hasMessages(ALERT_TIME_OVER)){
                        mHandler.removeMessages(ALERT_TIME_OVER);
                    }
                }
            });

            // 调整dialog背景大小
            lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                    .getWidth() * 0.75), LinearLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }

    public HibitsAlertDialog setTitle(String title) {
        return this;
    }

    public HibitsAlertDialog setMsg(String msg) {
        UbtLog.d(TAG,"msg = " + msg);
        tvMsg.setText(msg);
        return this;
    }

    public HibitsAlertDialog setEventId(String eventId) {
        this.mEventId = eventId;
        return this;
    }

    public HibitsAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    private void setLayout() {

    }

    public boolean isShowing(){
        return dialog.isShowing();
    }

    public void show() {
        setLayout();
        mHandler.sendEmptyMessageDelayed(ALERT_TIME_OVER, ALERT_TIME_LIMITE);
        dialog.show();
    }

    public void dismiss() {
        if(dialog != null){
            dialog.dismiss();
        }
    }
}
