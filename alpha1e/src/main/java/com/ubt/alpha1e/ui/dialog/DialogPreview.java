package com.ubt.alpha1e.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.ui.ActionsNewEditActivity;
import com.ubt.alpha1e.utils.TimeUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author wmma
 * @className
 * @description
 * @date
 * @update
 */


public class DialogPreview extends Dialog {

    private static String TAG = "DialogPreview";
    private Context context;
    private DialogPreview dialogPreview;

    private ImageView ivPlay;
    private TextView tvIndex;
    private TextView tvTime;

    private TextView tvCancel, tvConfirm;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private ActionsNewEditActivity activity;


    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {

            mHandler.sendEmptyMessage(1);

        }
    };

    private boolean playFinish = false;

    private int index = 0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                index++;
                tvIndex.setText(index+ "");
                tvTime.setText(TimeUtils.getTimeFromMillisecond((long)index*200));
            }else if(msg.what == 2) {
                playFinish = true;
                ivPlay.setImageResource(R.drawable.button_play);
                UbtLog.d(TAG, "finish:" + list.size());
                timer.cancel();
                mHandler.sendEmptyMessageDelayed(3,200);
            }else if(msg.what == 3){
                if(list.size()<5){
                    tvTime.setText("00:01");
                }
            }
        }
    };


    public DialogPreview(Context context, List<Map<String, Object>> list, ActionsNewEditActivity activity) {
        super(context);
        this.context = context;
        dialogPreview = this;
        this.list = list;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogPreview.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_preview);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogPreview.getWindow().getAttributes();
        lp.width = (int)((display.getWidth())*0.6); //设置宽度
        dialogPreview.getWindow().setAttributes(lp);
        dialogPreview.setCanceledOnTouchOutside(false);

        ivPlay = (ImageView) findViewById(R.id.iv_preview_play);
        tvIndex = (TextView) findViewById(R.id.tv_index);
        tvTime = (TextView) findViewById(R.id.tv_preview_time);

        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);

        tvIndex.setText("" + list.size());
        if(list.size() < 5){
            tvTime.setText("00:01");
        }else{
            tvTime.setText(TimeUtils.getTimeFromMillisecond((long)list.size()*200));
        }


        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activity.canPlay()){
                    return;
                }
                activity.doPlayAutoRead();
                ivPlay.setImageResource(R.drawable.icon_pause_nor);
                int time = list.size()*200;
                playFinish = false;
                index = 0;
                final int milliseconds = 200;
                new Thread(){
                    @Override
                    public void run(){
                        while(true && !playFinish){
                            try {
                                sleep(milliseconds);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            mHandler.sendEmptyMessage(1);
                        }
                    }
                }.start();
                mHandler.sendEmptyMessageDelayed(2, time);
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 0;
                timer.cancel();
                activity.cancelAutoData();
                list.clear();
                dismiss();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 0;
                list.clear();
                timer.cancel();
                dismiss();
            }
        });


    }
}
