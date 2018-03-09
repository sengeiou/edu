package com.ubt.alpha1e.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.ant.country.CountryActivity;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.ui.LoginActivity;

public class PhoneLoginFragment extends BaseFragment {

    private ILoginUI mLoginUI;
    private View mMainView;
    private LinearLayout lay_contory;
    private TextView txt_country_code, txt_country_num;
    private EditText edt_phone_num;
    private EditText edt_passwd;
    private LoginActivity loginActivity;

    @SuppressLint("ValidFragment")
    public PhoneLoginFragment(ILoginUI _ui) {
        mLoginUI = _ui;
    }

    public PhoneLoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_login_phone, null);
        initUI();
        initControlListener();
        return mMainView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        loginActivity = (LoginActivity) this.getActivity();
        loginActivity.clearWrongMessage();
        setEditTextHint(edt_phone_num,loginActivity.getStringResources("ui_login_phone_placeholder"));
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
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();

    }

    @Override
    protected void initUI() {
        lay_contory = (LinearLayout) mMainView.findViewById(R.id.layout_country_choose);
        txt_country_code = (TextView) mMainView
                .findViewById(R.id.txt_country_code);
        txt_country_num = (TextView) mMainView
                .findViewById(R.id.txt_country_num);
        edt_phone_num = (EditText) mMainView.findViewById(R.id.edt_phone_num);
        edt_passwd = (EditText) mMainView.findViewById(R.id.edt_passwd);
        edt_passwd.setTypeface(Typeface.DEFAULT);
        edt_phone_num.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    protected void initControlListener() {
        lay_contory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /*Intent intent = new Intent();
                intent.setClass(PhoneLoginFragment.this.getActivity(),
                        CountryActivity.class);
                PhoneLoginFragment.this.getActivity().startActivityForResult(
                        intent, LoginActivity.MSG_GET_COUNTRY);*/
            }
        });

        edt_phone_num.setFocusable(true);
        edt_phone_num.setFocusableInTouchMode(true);
        edt_phone_num.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

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
                // TODO Auto-generated method stub

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

    public void setCotury(String mCountryName, String mCountryNum) {
        txt_country_code.setText(mCountryName);
        txt_country_num.setText(mCountryNum);
    }

    public void doLogin() {
        mLoginUI.onDoLogin(edt_phone_num.getText().toString(), edt_passwd
                .getText().toString());
    }
}
