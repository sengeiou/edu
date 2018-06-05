package com.ubt.alpha1e.edu.ui.dialog;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.DataTools;
import com.ubt.alpha1e.edu.data.model.ActionColloInfo;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.NewActionInfo;
import com.ubt.alpha1e.edu.ui.helper.ActionsHelper.Action_type;

public class ActionRenameDialog extends BaseDialog {
    private static ActionRenameDialog mRenameDialog;

    private IRenameListener mListener;
    private Context mContext;
    private View mRootView;
    private TextView txt_ok;
    private TextView txt_cancel;
    private EditText edt_name;

    private Object mInfo;
    private Action_type mType;

    public interface IRenameListener {
        void onRename(boolean isOk, String new_name, Object info);
    }

    public ActionRenameDialog(Context context) {
        super(context);
    }

    public static ActionRenameDialog getInstance(Context _context,
                                                 IRenameListener _listener, Object _info, Action_type _type) {
        try {
            mRenameDialog.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRenameDialog = new ActionRenameDialog(_context);
        mRenameDialog.mContext = _context;
        mRenameDialog.mListener = _listener;
        mRenameDialog.mInfo = _info;
        mRenameDialog.mType = _type;
        mRenameDialog.initData();
        return mRenameDialog;
    }

    public void initData() {
        mRootView = View.inflate(mContext, R.layout.dialog_action_rename, null);
        // -------------------------------------------
        txt_ok = (TextView) mRootView.findViewById(R.id.txt_ok);
        txt_cancel = (TextView) mRootView.findViewById(R.id.txt_cancel);
        edt_name = (EditText) mRootView.findViewById(R.id.edt_name);
        edt_name.setHint(getActionName());
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
                mListener.onRename(false, "", null);
                ActionRenameDialog.this.cancel();
            }
        });
        txt_ok.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String new_name = edt_name.getText().toString();

                if (new_name.equals("")) {
                    Toast.makeText(
                            mContext,
                            mContext.getResources().getString(
                                    R.string.ui_action_name_empty), 500).show();
                    return;
                }

                int lenth = 0;
                try {
                    lenth = new_name.getBytes("GBK").length;
                } catch (UnsupportedEncodingException e) {
                    lenth = 0;
                }

                if (lenth > 32) {
                    Toast.makeText(
                            mContext,
                            mContext.getResources().getString(
                                    R.string.ui_action_name_too_long), 500).show();
                    return;
                }

                if (new_name.toString().contains(" ")) {
                    Toast.makeText(
                            mContext,
                            mContext.getResources().getString(
                                    R.string.ui_action_name_spaces), 500).show();
                    return;
                }

                if (mType == Action_type.My_new) {
                } else if (mType == Action_type.My_download) {
                } else {

                    if (mType == Action_type.Dance_type) {
                        new_name = "#" + new_name;
                    } else if (mType == Action_type.Base_type) {
                        new_name = "@" + new_name;
                    } else if (mType == Action_type.Story_type) {
                        new_name = "%" + new_name;
                    }
                }
                mListener.onRename(true, new_name, mInfo);
                ActionRenameDialog.this.cancel();

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
        if (name.equals("")) {
            try {
                name = ((NewActionInfo) mInfo).actionName;
            } catch (Exception e) {
                name = "";
            }
        }
        return name;
    }
}
