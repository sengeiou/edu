package com.ubt.alpha1e.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.action.ActionsCreateActivity;
import com.ubt.alpha1e.ui.BaseActivity;

/**
 * @author wmma
 * @className
 * @description
 * @date
 * @update
 */


public class DialogTips extends Dialog {

    private DialogTips dialogTips;
    private Context context;

    private TextView tvContent;
    private TextView tvConfirm;

    private String content = "";
    private int type = 0;
    private BaseActivity activity;
    OnLostClickListener onLostClickListener;

    public DialogTips(Context context, String content, int type, BaseActivity activity) {
        super(context);
        this.context = context;
        dialogTips = this;
        this.content = content;
        this.type = type;
        this.activity = activity;
    }

    public DialogTips(Context context, String content, int type, BaseActivity activity, OnLostClickListener onLostClickListener) {
        super(context);
        this.context = context;
        dialogTips = this;
        this.content = content;
        this.type = type;
        this.activity = activity;
        this.onLostClickListener = onLostClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogTips.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_tips);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogTips.getWindow().getAttributes();
        lp.width = (int) ((display.getWidth()) * 0.6); //设置宽度
        dialogTips.getWindow().setAttributes(lp);

        tvContent = (TextView) findViewById(R.id.tv_content);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);

        tvContent.setText(content);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (type == 1) {
                    if (null != onLostClickListener) {
                        onLostClickListener.lostLeftLeg();
                    }
                    if (null != activity) {
                        ((ActionsCreateActivity) activity).lostLeftLeg();
                    }
                } else if (type == 2) {
                    if (null != onLostClickListener) {
                        onLostClickListener.lostRightLeg();
                    }
                    if (null != activity) {
                        ((ActionsCreateActivity) activity).lostRightLeg();
                    }
                }
            }
        });
    }

    public interface OnLostClickListener {
        void lostLeftLeg();

        void lostRightLeg();
    }
}
