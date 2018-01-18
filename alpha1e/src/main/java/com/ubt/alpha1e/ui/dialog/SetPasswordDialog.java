package com.ubt.alpha1e.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.data.Md5;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * Created by liuqiang on 10/23/15.
 */
public class SetPasswordDialog {

    private static final String TAG = SetPasswordDialog.class.getSimpleName();

    private Context mContext;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private RelativeLayout rlInputPassword;
    private RelativeLayout rlInputPasswordOver;
    private LinearLayout llOperation;

    private TextView tvMsg;
    private TextView tvMsgTip;
    private TextView tvErrorMsg;
    private EditText edtPassword1;
    private EditText edtPassword2;
    private EditText edtPassword3;
    private EditText edtPassword4;
    private EditText edtPassword5;
    private EditText edtPassword6;

    private Button btnCancel;
    private Button btnConfirm;

    private Display display;
    private String mPassword = "";
    private ISetPasswordListener mISetPasswordListener;
    private String mPasswordFirst = "";
    private String mPasswordSecond = "";

    public SetPasswordDialog(Context context) {
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public SetPasswordDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.viev_set_password, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        rlInputPassword = (RelativeLayout) view.findViewById(R.id.rl_input_password);
        rlInputPasswordOver = (RelativeLayout) view.findViewById(R.id.rl_input_password_over);
        llOperation = (LinearLayout) view.findViewById(R.id.ll_operation);

        tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        tvMsgTip = (TextView) view.findViewById(R.id.tv_msg_tip);
        tvErrorMsg = (TextView) view.findViewById(R.id.tv_error_msg);
        edtPassword1 = (EditText) view.findViewById(R.id.edt_password_1);
        edtPassword2 = (EditText) view.findViewById(R.id.edt_password_2);
        edtPassword3 = (EditText) view.findViewById(R.id.edt_password_3);
        edtPassword4 = (EditText) view.findViewById(R.id.edt_password_4);
        edtPassword5 = (EditText) view.findViewById(R.id.edt_password_5);
        edtPassword6 = (EditText) view.findViewById(R.id.edt_password_6);

        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnConfirm    = (Button) view.findViewById(R.id.btn_confirm);

        edtPassword1.addTextChangedListener(mTextWatcher);
        edtPassword2.addTextChangedListener(mTextWatcher);
        edtPassword3.addTextChangedListener(mTextWatcher);
        edtPassword4.addTextChangedListener(mTextWatcher);
        edtPassword5.addTextChangedListener(mTextWatcher);
        edtPassword6.addTextChangedListener(mTextWatcher);

        edtPassword1.setOnKeyListener(onKeyListener);
        edtPassword2.setOnKeyListener(onKeyListener);
        edtPassword3.setOnKeyListener(onKeyListener);
        edtPassword4.setOnKeyListener(onKeyListener);
        edtPassword5.setOnKeyListener(onKeyListener);
        edtPassword6.setOnKeyListener(onKeyListener);

        edtPassword1.setOnFocusChangeListener(onFocusChangeListener);
        edtPassword2.setOnFocusChangeListener(onFocusChangeListener);
        edtPassword3.setOnFocusChangeListener(onFocusChangeListener);
        edtPassword4.setOnFocusChangeListener(onFocusChangeListener);
        edtPassword5.setOnFocusChangeListener(onFocusChangeListener);
        edtPassword6.setOnFocusChangeListener(onFocusChangeListener);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mPasswordSecond)){
                    ToastUtils.showShort(mContext.getString(R.string.ui_habits_password_confirm_req));
                    return;
                }
                dialog.dismiss();
                if(mISetPasswordListener != null){
                    mISetPasswordListener.onSetPassword(mPasswordFirst);
                }
            }
        });

        // 定义Dialog布局和参数
        dialog = new Dialog(mContext, R.style.NewAlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.75), LinearLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }

    public SetPasswordDialog setTitle(String title) {
        return this;
    }

    public SetPasswordDialog setMsg(String msg) {
        tvMsg.setText(msg);
        return this;
    }

    public SetPasswordDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public SetPasswordDialog setPassword(String password) {
        mPassword = password;
        return this;
    }

    private void setLayout() {

    }

    public boolean isShowing(){
        return dialog.isShowing();
    }

    public void show() {
        setLayout();
        dialog.show();
    }

    public void dismiss() {
        if(dialog != null){
            dialog.dismiss();
        }
    }

    public View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View view, boolean b) {
            if(b){
                UbtLog.d(TAG,"view.getId() = " + view.getId() );

                switch (view.getId()){
                    case R.id.edt_password_1:
                        break;
                    case R.id.edt_password_2:
                        if(TextUtils.isEmpty(edtPassword1.getText().toString())){
                            edtPassword1.requestFocus();
                        }
                        break;
                    case R.id.edt_password_3:
                        if(TextUtils.isEmpty(edtPassword1.getText().toString())){
                            edtPassword1.requestFocus();
                        }else if(TextUtils.isEmpty(edtPassword2.getText().toString())){
                            edtPassword2.requestFocus();
                        }
                        break;
                    case R.id.edt_password_4:
                        if(TextUtils.isEmpty(edtPassword1.getText().toString())){
                            edtPassword1.requestFocus();
                        }else if(TextUtils.isEmpty(edtPassword2.getText().toString())){
                            edtPassword2.requestFocus();
                        }else if(TextUtils.isEmpty(edtPassword3.getText().toString())){
                            edtPassword3.requestFocus();
                        }
                        break;
                    case R.id.edt_password_5:
                        if(TextUtils.isEmpty(edtPassword1.getText().toString())){
                            edtPassword1.requestFocus();
                        }else if(TextUtils.isEmpty(edtPassword2.getText().toString())){
                            edtPassword2.requestFocus();
                        }else if(TextUtils.isEmpty(edtPassword3.getText().toString())){
                            edtPassword3.requestFocus();
                        }else if(TextUtils.isEmpty(edtPassword4.getText().toString())){
                            edtPassword4.requestFocus();
                        }
                        break;
                    case R.id.edt_password_6:
                        if(TextUtils.isEmpty(edtPassword1.getText().toString())){
                            edtPassword1.requestFocus();
                        }else if(TextUtils.isEmpty(edtPassword2.getText().toString())){
                            edtPassword2.requestFocus();
                        }else if(TextUtils.isEmpty(edtPassword3.getText().toString())){
                            edtPassword3.requestFocus();
                        }else if(TextUtils.isEmpty(edtPassword4.getText().toString())){
                            edtPassword4.requestFocus();
                        }else if(TextUtils.isEmpty(edtPassword5.getText().toString())){
                            edtPassword5.requestFocus();
                        }
                        break;
                }
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(isInputFinish()){
                if(TextUtils.isEmpty(mPasswordFirst)){
                    mPasswordFirst = getInputPassword();

                    edtPassword1.setText("");
                    edtPassword2.setText("");
                    edtPassword3.setText("");
                    edtPassword4.setText("");
                    edtPassword5.setText("");
                    edtPassword6.setText("");
                    edtPassword1.requestFocus();

                    tvMsgTip.setVisibility(View.GONE);
                    tvMsg.setText(mContext.getString(R.string.ui_habits_password_confirm_title));

                    tvErrorMsg.setText("");
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    llOperation.setVisibility(View.VISIBLE);
                }else {
                    mPasswordSecond = getInputPassword();
                    UbtLog.d(TAG,"mPasswordFirst = " + mPasswordFirst + "   mPasswordSecond = " + mPasswordSecond);
                    if(isSavePassword()){
                        tvErrorMsg.setText("");
                        tvErrorMsg.setVisibility(View.VISIBLE);
                    }else {
                        edtPassword1.setText("");
                        edtPassword2.setText("");
                        edtPassword3.setText("");
                        edtPassword4.setText("");
                        edtPassword5.setText("");
                        edtPassword6.setText("");

                        mPasswordSecond = "";
                        tvErrorMsg.setText(mContext.getString(R.string.ui_habits_password_difference_tip));
                        tvErrorMsg.setVisibility(View.VISIBLE);
                        edtPassword1.requestFocus();
                    }
                }
            }else {
                tvErrorMsg.setText("");
                if(edtPassword1.isFocused() && !TextUtils.isEmpty(edtPassword1.getText().toString())){
                    edtPassword2.requestFocus();
                }else if(edtPassword2.isFocused() && !TextUtils.isEmpty(edtPassword2.getText().toString())){
                    edtPassword3.requestFocus();
                }else if(edtPassword3.isFocused() && !TextUtils.isEmpty(edtPassword3.getText().toString())){
                    edtPassword4.requestFocus();
                }else if(edtPassword4.isFocused() && !TextUtils.isEmpty(edtPassword4.getText().toString())){
                    edtPassword5.requestFocus();
                }else if(edtPassword5.isFocused() && !TextUtils.isEmpty(edtPassword5.getText().toString())){
                    edtPassword6.requestFocus();
                }
            }
        }
    };

    public View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

            if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if(view.getId() == R.id.edt_password_6){
                    edtPassword6.setText("");
                    edtPassword5.requestFocus();
                    edtPassword5.setSelection(edtPassword5.getText().length());
                }else if(view.getId() == R.id.edt_password_5){
                    edtPassword6.setText("");
                    edtPassword5.setText("");
                    edtPassword4.requestFocus();
                    edtPassword4.setSelection(edtPassword4.getText().length());
                }else if(view.getId() == R.id.edt_password_4){
                    edtPassword6.setText("");
                    edtPassword5.setText("");
                    edtPassword4.setText("");
                    edtPassword3.requestFocus();
                    edtPassword3.setSelection(edtPassword3.getText().length());
                }else if(view.getId() == R.id.edt_password_3){
                    edtPassword6.setText("");
                    edtPassword5.setText("");
                    edtPassword4.setText("");
                    edtPassword3.setText("");
                    edtPassword2.requestFocus();
                    edtPassword2.setSelection(edtPassword2.getText().length());
                }else if(view.getId() == R.id.edt_password_2){
                    edtPassword6.setText("");
                    edtPassword5.setText("");
                    edtPassword4.setText("");
                    edtPassword3.setText("");
                    edtPassword2.setText("");
                    edtPassword1.requestFocus();
                    edtPassword1.setSelection(edtPassword1.getText().length());
                }else if(view.getId() == R.id.edt_password_1){
                    edtPassword1.setText("");
                }
                return true;
            }
            return false;
        }
    };

    private boolean isInputFinish(){
        if(!TextUtils.isEmpty(edtPassword1.getText().toString())
                && !TextUtils.isEmpty(edtPassword2.getText().toString())
                && !TextUtils.isEmpty(edtPassword3.getText().toString())
                && !TextUtils.isEmpty(edtPassword4.getText().toString())
                && !TextUtils.isEmpty(edtPassword5.getText().toString())
                && !TextUtils.isEmpty(edtPassword6.getText().toString())
                ){
            return true;
        }else {
            return false;
        }
    }

    private String getInputPassword(){
        String inputPassword = edtPassword1.getText().toString()
                +edtPassword2.getText().toString()
                +edtPassword3.getText().toString()
                +edtPassword4.getText().toString()
                +edtPassword5.getText().toString()
                +edtPassword6.getText().toString();
        UbtLog.d("inputPassword","inputPassword = " + inputPassword);
        return inputPassword;
    }

    private boolean isSavePassword(){
        if(mPasswordFirst.equals(mPasswordSecond)){
            return true;
        }else {
            return false;
        }
    }

    public SetPasswordDialog setCallbackListener(ISetPasswordListener listener) {
        mISetPasswordListener = listener;
        return this;
    }

    public interface ISetPasswordListener{

        void onSetPassword(String password);

    }
}
