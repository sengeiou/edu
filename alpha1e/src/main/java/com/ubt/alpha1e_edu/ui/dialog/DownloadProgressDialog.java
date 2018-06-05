package com.ubt.alpha1e_edu.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;

public class DownloadProgressDialog extends BaseDialog {
    public static DownloadProgressDialog mDialog;

    private TextView tvTitle;
    private TextView tvMessage;
    private TextView tvProgress;
    private ProgressBar pbProgress;
    private Context mContext;
    private View mRootView;

    private DownloadProgressDialog(Context context) {
        super(context);
    }

    public static DownloadProgressDialog getInstance(Context context) {
        if (mDialog != null && mDialog.isShowing()){
            mDialog.cancel();
        }
        mDialog = new DownloadProgressDialog(context);
        mDialog.mContext = context;
        mDialog.initDialog();
        return mDialog;
    }

    private void initDialog() {
        mRootView = View.inflate(mContext, R.layout.dialog_download_progress, null);
        // -------------------------------------------
        tvTitle = (TextView) mRootView.findViewById(R.id.tv_title);
        tvMessage = (TextView) mRootView.findViewById(R.id.tv_message);
        tvProgress = (TextView) mRootView.findViewById(R.id.tv_progress);
        pbProgress = (ProgressBar) mRootView.findViewById(R.id.pb_progress);
        // -------------------------------------------
        this.setContentView(mRootView);
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(0, 0, 0, 0));
        this.getWindow().setBackgroundDrawable(colorDrawable);
        this.setCancelable(false);
    }

    public void showMsg(String msg) {
        try {
            super.show();
            tvMessage.setText(msg);
            tvProgress.setText("0%");
            pbProgress.setProgress(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitle(String title) {
        try {
            tvTitle.setText(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProgress(String progress) {
        try {
            tvProgress.setText(progress + "%");
            pbProgress.setProgress(Integer.parseInt(progress));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMsg(String msg) {
        try {
            tvMessage.setText(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
