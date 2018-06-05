package com.ubt.alpha1e_edu.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.DataTools;

public class MyAlertDialog extends BaseDialog {

    private static MyAlertDialog mMyAlertDialog;

    private IMessageListeter mListener;
    private Context mContext;
    private String messageContent;
    private String negtiveText;
    private String positiveText;

    private View mRootView;
    private TextView txt_message;
    private TextView txt_ok;
    private TextView txt_cancel;

    public MyAlertDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public static MyAlertDialog getInstance(Context _context, String message,
                                            String negative, String positive, IMessageListeter listener) {
        if (mMyAlertDialog != null && mMyAlertDialog.isShowing()) {
            mMyAlertDialog.cancel();
            mMyAlertDialog = null;
        }
        mMyAlertDialog = new MyAlertDialog(_context);
        mMyAlertDialog.mContext = _context;
        mMyAlertDialog.messageContent = message;
        mMyAlertDialog.negtiveText = negative;
        mMyAlertDialog.positiveText = positive;
        mMyAlertDialog.mListener = listener;
        mMyAlertDialog.initData();
        return mMyAlertDialog;
    }

    public void initData() {
        mRootView = View.inflate(mContext, R.layout.dialog_myalertdialog, null);
        // -------------------------------------------
        txt_message = (TextView) mRootView.findViewById(R.id.txt_message);
        txt_message.setText(messageContent);
        txt_ok = (TextView) mRootView.findViewById(R.id.txt_ok);
        txt_ok.setText(positiveText);
        txt_cancel = (TextView) mRootView.findViewById(R.id.txt_cancel);
        txt_cancel.setText(negtiveText);
        // -------------------------------------------
        this.setContentView(mRootView);
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(0, 0, 0, 0));
        this.getWindow().setBackgroundDrawable(colorDrawable);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
    }

    @Override
    public void show() {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        // 设置窗口宽度
        lp.width = (int) DataTools.dip2px(mContext, 300);
        dialogWindow.setAttributes(lp);
        initControlListener();
        super.show();
    }

    private void initControlListener() {

        txt_cancel.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mListener != null)
                    mListener.onViewAction(false);
                MyAlertDialog.this.cancel();
            }
        });
        txt_ok.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mListener != null)
                    mListener.onViewAction(true);
                MyAlertDialog.this.cancel();
            }
        });

    }

}
