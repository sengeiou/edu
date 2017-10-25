package com.ubt.alpha1e.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.ui.ActionsNewEditActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * @author wmma
 * @className
 * @description
 * @date
 * @update
 */


public class DialogMusic extends Dialog {

    private static final String TAG = "DialogMusic";
    private DialogMusic dialogMusic;

    private Context context;

    private TextView tvCancel;
    private TextView tvConfirm;
    private TextView tvTitle;
    private TextView tvContent;
    private LinearLayout llTips;

    private ActionsNewEditActivity activity;

    private int type = 0;


    public DialogMusic(Context context, ActionsNewEditActivity activity, int type){
        super(context);
        dialogMusic = this;
        this.context = context;
        this.activity = activity;
        this.type = type;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogMusic.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_music_tips);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogMusic.getWindow().getAttributes();
        lp.width = (int)((display.getWidth())*0.6); //设置宽度
        dialogMusic.getWindow().setAttributes(lp);

        initView();
    }


    private void initView(){
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContent = (TextView) findViewById(R.id.tv_tip_content);
        llTips = (LinearLayout) findViewById(R.id.ll_tips);

        if(type == 1){
            llTips.setVisibility(View.GONE);
            tvTitle.setText(activity.getStringResources("ui_create_auto_record"));
            tvContent.setText(activity.getStringResources("ui_create_auto_record_tips"));
            tvConfirm.setText(activity.getStringResources("ui_create_start"));
        }else{
            llTips.setVisibility(View.VISIBLE);
        }

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==0){
                    UbtLog.d(TAG, "select music");
                    activity.setMusic();
                }else if(type == 1){
                    activity.startAutoRead();
                }
                dismiss();

            }
        });


    }


}
