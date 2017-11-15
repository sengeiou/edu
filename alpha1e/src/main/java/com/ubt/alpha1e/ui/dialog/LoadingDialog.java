package com.ubt.alpha1e.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.DataTools;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.net.http.basic.BaseWebRunnable;

import java.util.Date;

public class LoadingDialog extends BaseDialog {

    private Context mContext;
    private ImageView img_dia_logo;
    private TextView txt_msg;
    private static LoadingDialog mDia;

    public BaseWebRunnable mCurrentTask;
    private BaseDiaUI mUi;
    private boolean mCancelable = false;
    private long mLastShowTime = -1;
    private int mMinWaitTime;

    @Override
    public void show() {

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        // 设置窗口宽度
        lp.width = (int) DataTools.dip2px(mContext, 100);
        dialogWindow.setAttributes(lp);
        //txt_msg.setVisibility(View.INVISIBLE);
        txt_msg.setVisibility(View.GONE);
        doShow();
    }

    public void showMessage(String msg) {
        try {
            txt_msg.setText(msg);
            txt_msg.setVisibility(View.VISIBLE);

            Window dialogWindow = this.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            // 设置窗口宽度
            lp.width = -2;
            dialogWindow.setAttributes(lp);

            doShow();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doShow() {
        try {
            mLastShowTime = new Date().getTime();
            super.show();
            Animation operatingAnim = AnimationUtils.loadAnimation(mContext,
                    R.anim.turn_around_anim);
            operatingAnim.setInterpolator(new LinearInterpolator());
            img_dia_logo.startAnimation(operatingAnim);
        } catch (Exception e) {
            MyLog.writeLog("对话框功能", "对话框弹失败-->" + e.getMessage());
            e.printStackTrace();
        }
    }

    private LoadingDialog(Context context) {
        super(context);
    }

    public static LoadingDialog getInstance(Context _context, BaseDiaUI _ui) {

        try {
            if (mDia != null && mDia.isShowing())
                mDia.cancel();
        } catch (Exception e) {
            MyLog.writeLog("对话框功能", "对话框取消失败-->" + e.getMessage());
            e.printStackTrace();
        }
        mDia = new LoadingDialog(_context);
        mDia.mContext = _context;
        mDia.mUi = _ui;
        mDia.mCancelable = false;
        mDia.initDia();
        return mDia;

    }

    public static LoadingDialog getInstance(Context _context) {

        try {
            if (mDia != null && mDia.isShowing())
                mDia.cancel();
        } catch (Exception e) {
            MyLog.writeLog("对话框功能", "对话框取消失败-->" + e.getMessage());
            e.printStackTrace();
        }
        mDia = new LoadingDialog(_context);
        mDia.mContext = _context;
        mDia.mCancelable = false;
        mDia.initDia();
        return mDia;
    }

    private void initDia() {

        View root = View.inflate(mContext, R.layout.dialog_loading, null);
        img_dia_logo = (ImageView) root.findViewById(R.id.img_dia_logo);
        txt_msg = (TextView) root.findViewById(R.id.txt_msg);
        this.setContentView(root);
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(0, 0, 0, 0));
        this.getWindow().setBackgroundDrawable(colorDrawable);
        this.setCancelable(false);
    }

    public void setDoCancelable(boolean _cancelable) {
        mCancelable = _cancelable;
        mMinWaitTime = -1;
    }

    public void setDoCancelable(boolean _cancelable, int minTime) {
        mCancelable = _cancelable;
        mMinWaitTime = minTime;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == event.ACTION_DOWN
                && event.getKeyCode() == event.KEYCODE_BACK && mCancelable) {

            if (mMinWaitTime > 0) {
                long time_deff = new Date().getTime() - mLastShowTime;
                if (time_deff < (mMinWaitTime * 1000))
                    return super.onKeyDown(keyCode, event);
            }

            if (this.isShowing()) {
                this.cancel();
                if (this.mCurrentTask != null) {
                    this.mCurrentTask.disableTask();
                    this.mCurrentTask = null;
                }
                if (this.mUi != null)
                    this.mUi.noteWaitWebProcressShutDown();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
