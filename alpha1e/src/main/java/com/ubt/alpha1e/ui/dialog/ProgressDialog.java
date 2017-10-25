package com.ubt.alpha1e.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.R;

public class ProgressDialog extends BaseDialog {
    public static ProgressDialog mDia;
    private ImageView img_dia_logo;
    private TextView txt_message;
    private TextView txt_progress;
    private Context mContext;
    private View mRootView;

    private ProgressDialog(Context context) {
        super(context);
    }

    public static ProgressDialog getInstance(Context _context) {
        if (mDia != null && mDia.isShowing())
            mDia.cancel();
        mDia = new ProgressDialog(_context);
        mDia.mContext = _context;
        mDia.initDia();
        return mDia;
    }

    private void initDia() {
        mRootView = View.inflate(mContext, R.layout.dialog_progress, null);
        // -------------------------------------------
        img_dia_logo = (ImageView) mRootView.findViewById(R.id.img_dia_logo);
        txt_message = (TextView) mRootView.findViewById(R.id.txt_message);
        txt_progress = (TextView) mRootView.findViewById(R.id.txt_progress);
        // -------------------------------------------
        this.setContentView(mRootView);
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(0, 0, 0, 0));
        this.getWindow().setBackgroundDrawable(colorDrawable);
        this.setCancelable(false);
    }

    public void showMsg(String msg) {
        try {
            super.show();
            Animation operatingAnim = AnimationUtils.loadAnimation(mContext,
                    R.anim.turn_around_anim);
            operatingAnim.setInterpolator(new LinearInterpolator());
            img_dia_logo.startAnimation(operatingAnim);
            txt_message.setText(msg);
            txt_progress.setText("0%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProgress(String progress) {
        try {
            txt_progress.setText(progress + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTitleMsg(String msg) {
        try {
            txt_message.setText(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCountDown(String countDown){
        try {
            txt_progress.setText(countDown);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
