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
import com.ubt.alpha1e_edu.data.model.ActionColloInfo;
import com.ubt.alpha1e_edu.data.model.ActionInfo;

public class ActionDelDialog extends BaseDialog {
    private static ActionDelDialog mRenameDialog;

    private IDelListener mListener;
    private Context mContext;
    private View mRootView;
    private TextView txt_ok;
    private TextView txt_cancel;
    private TextView txt_name;

    private Object mInfo;

    public interface IDelListener {
        void onDel(boolean isOk, Object info);
    }

    public ActionDelDialog(Context context) {
        super(context);
    }

    public static ActionDelDialog getInstance(Context _context,
                                              IDelListener _listener, Object _info) {
        try {
            mRenameDialog.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRenameDialog = new ActionDelDialog(_context);
        mRenameDialog.mContext = _context;
        mRenameDialog.mListener = _listener;
        mRenameDialog.mInfo = _info;
        mRenameDialog.initData();
        return mRenameDialog;
    }

    public void initData() {
        mRootView = View.inflate(mContext, R.layout.dialog_action_del, null);
        // -------------------------------------------
        txt_ok = (TextView) mRootView.findViewById(R.id.txt_ok);
        txt_cancel = (TextView) mRootView.findViewById(R.id.txt_cancel);
        txt_name = (TextView) mRootView.findViewById(R.id.txt_name);
//        txt_name.setText(mContext.getResources()
//                .getString(R.string.ui_del_action_note)
//                .replace("?", getActionName()));
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
        lp.width = (int) DataTools.dip2px(mContext, 260);
        dialogWindow.setAttributes(lp);
        initControlListener();
        super.show();
    }

    private void initControlListener() {
        txt_cancel.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mListener.onDel(false, null);
                ActionDelDialog.this.cancel();
            }
        });
        txt_ok.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mListener.onDel(true, mInfo);
                ActionDelDialog.this.cancel();
            }
        });
    }

    private String getActionName() {
        String name = "";

        try {
            name = (String) mInfo;
        } catch (Exception e) {
            name = "";
        }
        if (name.equals("")) {
            try {
                name = ((ActionInfo) mInfo).actionName;
            } catch (Exception e) {
                name = "";
            }
        }
        if (name.equals("")) {
            try {
                name = ((ActionColloInfo) mInfo).collectName;
            } catch (Exception e) {
                name = "";
            }
        }
        return name;
    }
}
