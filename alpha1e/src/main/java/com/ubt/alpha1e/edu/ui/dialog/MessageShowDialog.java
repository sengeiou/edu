package com.ubt.alpha1e.edu.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;

public class MessageShowDialog extends BaseDialog {
    public static MessageShowDialog mDia;
    private Context mContext;
    private IMessageListeter mListener;

    private String mMessage;

    private View mRootView;
    private TextView txt_message;
    private TextView txt_ok;
    private TextView txt_cancel;

    private MessageShowDialog(Context context) {
        super(context);
    }

    public static MessageShowDialog getInstance(Context _context,
                                                String message, IMessageListeter listener) {
        if (mDia != null && mDia.isShowing())
            mDia.cancel();
        mDia = new MessageShowDialog(_context);
        mDia.mContext = _context;
        mDia.mMessage = message;
        mDia.mListener = listener;
        mDia.initDia();
        return mDia;
    }

    private void initDia() {
        mRootView = View
                .inflate(mContext, R.layout.dialog_message_notify, null);
        // -------------------------------------------
        txt_message = (TextView) mRootView.findViewById(R.id.txt_message);
        txt_message.setText(mMessage);
        txt_ok = (TextView) mRootView.findViewById(R.id.txt_ok);
        txt_cancel = (TextView) mRootView.findViewById(R.id.txt_cancel);
        // -------------------------------------------
        this.setContentView(mRootView);
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(0, 0, 0, 0));
        this.getWindow().setBackgroundDrawable(colorDrawable);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
    }

    public void doShowMsg() {
        initControlListener();
        super.show();
    }

    private void initControlListener() {

        txt_cancel.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mListener.onViewAction(false);
                MessageShowDialog.this.cancel();
            }
        });
        txt_ok.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mListener.onViewAction(true);
                MessageShowDialog.this.cancel();
            }
        });

    }
}
