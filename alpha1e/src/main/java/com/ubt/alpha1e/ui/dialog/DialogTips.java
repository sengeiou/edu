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
import com.ubt.alpha1e.ui.ActionsNewEditActivity;

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
    private ActionsNewEditActivity activity;

    public DialogTips(Context context, String content, int type, ActionsNewEditActivity activity) {
        super(context);
        this.context = context;
        dialogTips = this;
        this.content = content;
        this.type = type;
        this.activity = activity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogTips.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_tips);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogTips.getWindow().getAttributes();
        lp.width = (int)((display.getWidth())*0.6); //设置宽度
        dialogTips.getWindow().setAttributes(lp);


        tvContent = (TextView) findViewById(R.id.tv_content);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);

        tvContent.setText(content);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(type == 1) {
                    activity.lostLeftLeg();
                }else if(type == 2){
                    activity.lostRightLeg();
                }
            }
        });
    }
}
