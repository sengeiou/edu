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
import com.ubt.alpha1e.utils.StringUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * Created by liuqiang on 10/23/15.
 */
public class InputPasswordDialog {
    private static final String TAG = InputPasswordDialog.class.getSimpleName();

    private Context mContext;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private RelativeLayout rlInputPassword;
    private RelativeLayout rlInputPasswordOver;

    private TextView tvMsg;
    private EditText edtPassword1;
    private EditText edtPassword2;
    private EditText edtPassword3;
    private EditText edtPassword4;
    private EditText edtPassword5;
    private EditText edtPassword6;
    private TextView tvErrorTip;

    private Button btnInputAgain;
    private Button btnFindPsw;

    private Display display;
    private String mPassword = "";
    private int inputCount = 0;
    private IInputPasswordListener mInputPasswordListener;

    public InputPasswordDialog(Context context) {
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public InputPasswordDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_input_password, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        rlInputPassword = (RelativeLayout) view.findViewById(R.id.rl_input_password);
        rlInputPasswordOver = (RelativeLayout) view.findViewById(R.id.rl_input_password_over);

        tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        tvErrorTip = (TextView) view.findViewById(R.id.tv_error_tip);
        edtPassword1 = (EditText) view.findViewById(R.id.edt_password_1);
        edtPassword2 = (EditText) view.findViewById(R.id.edt_password_2);
        edtPassword3 = (EditText) view.findViewById(R.id.edt_password_3);
        edtPassword4 = (EditText) view.findViewById(R.id.edt_password_4);
        edtPassword5 = (EditText) view.findViewById(R.id.edt_password_5);
        edtPassword6 = (EditText) view.findViewById(R.id.edt_password_6);

        btnInputAgain = (Button) view.findViewById(R.id.btn_input_again);
        btnFindPsw    = (Button) view.findViewById(R.id.btn_find_psw);

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

        btnInputAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputCount = 0;
                tvErrorTip.setText("");

                edtPassword1.setText("");
                edtPassword2.setText("");
                edtPassword3.setText("");
                edtPassword4.setText("");
                edtPassword5.setText("");
                edtPassword6.setText("");

                rlInputPassword.setVisibility(View.VISIBLE);
                rlInputPasswordOver.setVisibility(View.GONE);
            }
        });

        btnFindPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(mInputPasswordListener != null){
                    mInputPasswordListener.onFindPassword();
                }
            }
        });

        // 定义Dialog布局和参数
        dialog = new Dialog(mContext, R.style.NewAlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }

    public InputPasswordDialog setTitle(String title) {
        return this;
    }

    public InputPasswordDialog setMsg(String msg) {
        tvMsg.setText(msg);
        return this;
    }

    public InputPasswordDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public InputPasswordDialog setPassword(String password) {
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

    public View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            UbtLog.d(TAG,"keyCode = " + keyCode + " KeyEvent = " + KeyEvent.KEYCODE_0 +"/" + KeyEvent.KEYCODE_1 + "/" + KeyEvent.KEYCODE_2);
            if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if(view.getId() == R.id.edt_password_6){
                    UbtLog.d(TAG,"edtPassword6 = " + edtPassword6.getText().toString());
                    if(TextUtils.isEmpty(edtPassword6.getText().toString())){
                        edtPassword5.setText("");
                        edtPassword5.requestFocus();
                        edtPassword5.setSelection(edtPassword5.getText().length());
                    }else {
                        edtPassword6.setText("");
                    }
                }else if(view.getId() == R.id.edt_password_5){
                    UbtLog.d(TAG,"edtPassword5 = " + edtPassword5.getText().toString());
                    edtPassword6.setText("");
                    if(TextUtils.isEmpty(edtPassword5.getText().toString())){
                        edtPassword4.setText("");
                        edtPassword4.requestFocus();
                        edtPassword4.setSelection(edtPassword4.getText().length());
                    }else {
                        edtPassword5.setText("");
                        UbtLog.d(TAG,"edtPassword5 =>> " + edtPassword5.getText().toString());
                    }
                }else if(view.getId() == R.id.edt_password_4){
                    edtPassword6.setText("");
                    edtPassword5.setText("");
                    if(TextUtils.isEmpty(edtPassword4.getText().toString())){
                        edtPassword3.setText("");
                        edtPassword3.requestFocus();
                        edtPassword3.setSelection(edtPassword3.getText().length());
                    }else {
                        edtPassword4.setText("");
                    }
                }else if(view.getId() == R.id.edt_password_3){
                    edtPassword6.setText("");
                    edtPassword5.setText("");
                    edtPassword4.setText("");
                    if(TextUtils.isEmpty(edtPassword3.getText().toString())){
                        edtPassword2.setText("");
                        edtPassword2.requestFocus();
                        edtPassword2.setSelection(edtPassword2.getText().length());
                    }else {
                        edtPassword3.setText("");
                    }
                }else if(view.getId() == R.id.edt_password_2){
                    edtPassword6.setText("");
                    edtPassword5.setText("");
                    edtPassword4.setText("");
                    edtPassword3.setText("");
                    if(TextUtils.isEmpty(edtPassword2.getText().toString())){
                        edtPassword1.setText("");
                        edtPassword1.requestFocus();
                        edtPassword1.setSelection(edtPassword1.getText().length());
                    }else {
                        edtPassword2.setText("");
                    }
                }else if(view.getId() == R.id.edt_password_1){
                    edtPassword1.setText("");
                }
                return true;
            }
            return false;
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
                inputCount++;
                if(isCorrectPassword()){
                    tvErrorTip.setText("");
                    if(mInputPasswordListener != null){
                        mInputPasswordListener.onCorrectPassword();
                    }
                    dismiss();
                }else {
                    if(inputCount >3 ){
                        edtPassword1.setText("");
                        edtPassword2.setText("");
                        edtPassword3.setText("");
                        edtPassword4.setText("");
                        edtPassword5.setText("");
                        edtPassword6.setText("");

                        rlInputPasswordOver.setVisibility(View.VISIBLE);
                        rlInputPassword.setVisibility(View.GONE);
                    }else {
                        tvErrorTip.setText(mContext.getResources().getString(R.string.ui_habits_password_error_tip));
                        edtPassword1.setText("");
                        edtPassword2.setText("");
                        edtPassword3.setText("");
                        edtPassword4.setText("");
                        edtPassword5.setText("");
                        edtPassword6.setText("");
                        edtPassword1.requestFocus();
                    }
                }
            }else {

                if(edtPassword1.isFocused() && !TextUtils.isEmpty(editable.toString())){
                    edtPassword2.requestFocus();
                }else if(edtPassword2.isFocused() && !TextUtils.isEmpty(editable.toString())){
                    edtPassword3.requestFocus();
                }else if(edtPassword3.isFocused() && !TextUtils.isEmpty(editable.toString())){
                    edtPassword4.requestFocus();
                }else if(edtPassword4.isFocused() && !TextUtils.isEmpty(editable.toString())){
                    edtPassword5.requestFocus();
                }else if(edtPassword5.isFocused() && !TextUtils.isEmpty(editable.toString())){
                    edtPassword6.requestFocus();
                }
            }
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

    private boolean isCorrectPassword(){
        String inputPassword = edtPassword1.getText().toString()
                +edtPassword2.getText().toString()
                +edtPassword3.getText().toString()
                +edtPassword4.getText().toString()
                +edtPassword5.getText().toString()
                +edtPassword6.getText().toString();
        UbtLog.d("InputPasswordDialog","inputPassword = " + inputPassword + " = " + Md5.getMD5(inputPassword).toLowerCase() + " mPassword = " + mPassword);
        if(Md5.getMD5(inputPassword).toLowerCase().equals(mPassword)){
            return true;
        }else {
            return false;
        }
    }

    public InputPasswordDialog setCallbackListener(IInputPasswordListener listener) {
        mInputPasswordListener = listener;
        return this;
    }

    public interface IInputPasswordListener{

        void onCorrectPassword();

        void onFindPassword();
    }
}
