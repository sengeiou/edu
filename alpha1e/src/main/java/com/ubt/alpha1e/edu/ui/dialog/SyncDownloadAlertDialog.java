package com.ubt.alpha1e.edu.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.DataTools;

import java.util.Date;

/**
 * Created by lihai on 5/16/17.
 */
public class SyncDownloadAlertDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_title;
    private TextView txt_msg;
    private Button btn_neg;
    private Button btn_pos;
    private ImageView img_line;
    private ImageView btn_line;
    private ImageView img_connect_state;

    private TextView tvProgress;
    private ProgressBar pbProgress;

    private Display display;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private boolean showImg = false;
    private boolean showPropress = false;

    private int mMinWaitTime;
    private boolean mCancelable = false;
    private long mLastShowTime = -1;

    public SyncDownloadAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public SyncDownloadAlertDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_connectalertdialog, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setVisibility(View.GONE);
        txt_msg = (TextView) view.findViewById(R.id.txt_msg);
        txt_msg.setVisibility(View.GONE);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);
        img_line = (ImageView) view.findViewById(R.id.img_line);
        img_line.setVisibility(View.GONE);
        btn_line = (ImageView) view.findViewById(R.id.btn_line);
        img_connect_state = (ImageView) view.findViewById(R.id.img_connect_state);

        tvProgress = (TextView) view.findViewById(R.id.tv_progress);
        tvProgress.setVisibility(View.GONE);
        pbProgress = (ProgressBar) view.findViewById(R.id.pb_progress);
        pbProgress.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.75), LinearLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }

    public SyncDownloadAlertDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    public SyncDownloadAlertDialog setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    public SyncDownloadAlertDialog setProgress(int progress){
        showPropress = true;
        tvProgress.setText(progress + "%");
        pbProgress.setProgress(progress);

        tvProgress.setVisibility(View.VISIBLE);
        pbProgress.setVisibility(View.VISIBLE);
        return this;
    }

    public SyncDownloadAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        mCancelable = cancel;
        mMinWaitTime = -1;
        return this;
    }

    public SyncDownloadAlertDialog setCancelable(boolean cancel,int minTime) {
        dialog.setCancelable(cancel);
        if(cancel && minTime > 0){
            dialog.setCanceledOnTouchOutside(false);
        }
        mCancelable = cancel;
        mMinWaitTime = minTime;
        return this;
    }

    public SyncDownloadAlertDialog setPositiveButton(String text,
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

    public SyncDownloadAlertDialog setNegativeButton(String text,
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

    public SyncDownloadAlertDialog setImageResoure(int id) {
        showImg = true;
        img_connect_state.setImageResource(id);
        if(id == R.drawable.data_loading){
            LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams)img_connect_state.getLayoutParams();
            llParams.leftMargin = (int) DataTools.dip2px(context,-15);
            img_connect_state.setLayoutParams(llParams);
        }

        return this;
    }

    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText("提示");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
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

        if(showPropress){
            tvProgress.setVisibility(View.VISIBLE);
            pbProgress.setVisibility(View.VISIBLE);
        }

    }

    public void show() {
        setLayout();

        dialog.setOnKeyListener(onKeyListener);
        mLastShowTime = new Date().getTime();
        dialog.show();

    }

    private DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == keyEvent.ACTION_DOWN
                    && keyEvent.getKeyCode() == keyEvent.KEYCODE_BACK && mCancelable) {

                if (mMinWaitTime > 0) {
                    long time_deff = new Date().getTime() - mLastShowTime;
                    if (time_deff < (mMinWaitTime * 1000)){
                        return true;
                    }
                }

                if (isShowing()) {
                    display();
                }
            }

            return false;
        }
    };

    public void display(){
        dialog.dismiss();
    }

    public boolean isShowing(){
        return dialog.isShowing();
    }
}
