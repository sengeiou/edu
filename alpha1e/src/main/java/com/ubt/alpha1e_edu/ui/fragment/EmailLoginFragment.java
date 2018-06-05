package com.ubt.alpha1e_edu.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.DataCheckTools;
import com.ubt.alpha1e_edu.ui.LoginActivity;
import com.ubt.alpha1e_edu.ui.custom.EditTextCheck;

public class EmailLoginFragment extends BaseFragment {

    private ILoginUI mLoginUI;
    private View mMainView;
    private EditText edt_name;
    private EditText edt_passwd;
    private LoginActivity loginActivity;

    @SuppressLint("ValidFragment")
    public EmailLoginFragment(ILoginUI _ui) {
        mLoginUI = _ui;
    }

    public EmailLoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_login_email, null);
        initUI();
        initControlListener();
        return mMainView;
    }


    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        loginActivity = (LoginActivity) this.getActivity();
        loginActivity.clearWrongMessage();
        setEditTextHint(edt_name,loginActivity.getStringResources("ui_login_email_placeholder"));
        setEditTextHint(edt_passwd,loginActivity.getStringResources("ui_login_password_placeholder"));
    }

    private void setEditTextHint(EditText edt,String hint)
    {
        SpannableString ss = new SpannableString(hint);
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14,true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        edt.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
    }

    @Override
    protected void initUI() {
        edt_name = (EditText) mMainView.findViewById(R.id.edt_name);
        EditTextCheck.addCheckForEmail(edt_name, this.getActivity());
        edt_passwd = (EditText) mMainView.findViewById(R.id.edt_passwd);
        edt_passwd.setTypeface(Typeface.DEFAULT);
    }

    @Override
    protected void initControlListener() {

        edt_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
                loginActivity.clearWrongMessage();
                loginActivity.setLoginButtonEnable(arg0.length()>0);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

        edt_passwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                if (arg0.length() > 0)
                {
                    loginActivity.clearWrongMessage();
                    edt_passwd.setTypeface(Typeface.MONOSPACE);
                    loginActivity.setLoginButtonEnable(arg0.length()>0);
                }


            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    public void doLogin() {
        if (!DataCheckTools.isEmail(edt_name.getText().toString())) {
            loginActivity.toastForWrongMeassage(getActivity().getResources()
                    .getString(R.string.ui_login_prompt_email_wrong_format));
            // Toast.makeText(
            // getActivity(),
            // getActivity().getResources().getString(
            // R.string.ui_email_illegal), 500).show();
            return;
        }
        mLoginUI.onDoLogin(edt_name.getText().toString(), edt_passwd.getText()
                .toString());
    }
}