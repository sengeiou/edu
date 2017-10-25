package com.ubt.alpha1e.ui.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.DataCheckTools;

public class EditTextCheck {
    public static void addCheckForPasswd(EditText _edt, Context _context) {
        _edt.setOnFocusChangeListener(new PassWdFocusChangeListener(_edt,
                _context));
    }

    public static void addCheckForRePasswd(EditText edt_passwd,
                                           EditText edt_re_passwd, Context _context) {
        edt_re_passwd.setOnFocusChangeListener(new RePassWdFocusChangeListener(
                edt_passwd, edt_re_passwd, _context));

    }

    public static void addCheckForPhone(EditText edt_phone_num, Context _context) {
        edt_phone_num.setOnFocusChangeListener(new phoneChangeListener(
                edt_phone_num, _context));
    }

    public static void addCheckForEmail(EditText edt_email_address,
                                        Context _context) {
        edt_email_address.setOnFocusChangeListener(new emailChangeListener(
                edt_email_address, _context));
    }

    public static void addCheckForLenth(final EditText edt, final int num, final Context _context) {
        edt.addTextChangedListener(new TextWatcher() {

            private String txt_old = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                txt_old = s + "";
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String txt = s + "";
                try {
                    int lenth = txt.getBytes("GBK").length;
                    if (lenth > num) {
                        edt.setText(txt_old);
                        Toast.makeText(_context, _context.getResources().getString(R.string.ui_about_feedback_input_too_long), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

class PassWdFocusChangeListener implements OnFocusChangeListener {

    private EditText mEditView = null;
    private Context mContext = null;

    public PassWdFocusChangeListener(EditText _editView, Context _context) {
        mEditView = _editView;
        mContext = _context;
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1) {
        // TODO Auto-generated method stub
        if (!arg1) {
            if (mEditView.getText().toString().length() < 6) {
                Toast.makeText(
                        mContext,
                        mContext.getResources().getString(
                                R.string.ui_login_prompt_password_too_short), Toast.LENGTH_SHORT).show();
                return;
            }

            if (mEditView.getText().toString().length() > 16) {
                Toast.makeText(
                        mContext,
                        mContext.getResources().getString(
                                R.string.ui_login_prompt_passwprd_too_long), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!DataCheckTools.isCorrectPswFormat(mEditView.getText()
                    .toString())) {
                Toast.makeText(
                        mContext,
                        mContext.getResources().getString(
                                R.string.ui_login_prompt_passwprd_error), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
}

class RePassWdFocusChangeListener implements OnFocusChangeListener {

    private EditText mPreEditView = null;
    private EditText mEditView = null;
    private Context mContext = null;

    public RePassWdFocusChangeListener(EditText edt_passwd, EditText _editView,
                                       Context _context) {
        mEditView = _editView;
        mContext = _context;
        mPreEditView = edt_passwd;
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1) {
        // TODO Auto-generated method stub
        if (!arg1) {
            if (mEditView.getText().toString().length() < 6) {
                Toast.makeText(
                        mContext,
                        mContext.getResources().getString(
                                R.string.ui_login_prompt_password_too_short), Toast.LENGTH_SHORT).show();
                return;
            }

            if (mEditView.getText().toString().length() > 16) {
                Toast.makeText(
                        mContext,
                        mContext.getResources().getString(
                                R.string.ui_login_prompt_passwprd_too_long), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!DataCheckTools.isCorrectPswFormat(mEditView.getText()
                    .toString())) {
                Toast.makeText(
                        mContext,
                        mContext.getResources().getString(
                                R.string.ui_login_prompt_passwprd_error), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!mPreEditView.getText().toString()
                    .equals(mEditView.getText().toString())) {
                Toast.makeText(
                        mContext,
                        mContext.getResources().getString(
                                R.string.ui_register_different_password), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
}

class phoneChangeListener implements OnFocusChangeListener {

    private String mPwd;
    private EditText mEditView = null;
    private Context mContext = null;

    public phoneChangeListener(EditText phone, Context _context) {
        mEditView = phone;
        mContext = _context;
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1) {
        // TODO Auto-generated method stub
        if (!arg1) {
            if (!DataCheckTools.isPhoneNum(mEditView.getText().toString())
                    && !mEditView.getText().toString().equals("")) {
                Toast.makeText(
                        mContext,
                        mContext.getResources().getString(
                                R.string.ui_login_prompt_phone_wrong_format), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
}

class emailChangeListener implements OnFocusChangeListener {

    private String mPwd;
    private EditText mEditView = null;
    private Context mContext = null;

    public emailChangeListener(EditText email, Context _context) {
        mEditView = email;
        mContext = _context;
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1) {
        // TODO Auto-generated method stub
        if (!arg1) {
            if (!DataCheckTools.isEmail(mEditView.getText().toString())
                    && !mEditView.getText().toString().equals("")) {
                Toast.makeText(
                        mContext,
                        mContext.getResources().getString(
                                R.string.ui_login_prompt_email_wrong_format), Toast.LENGTH_SHORT).show();
                return;
            }

        }
    }
}
