package com.ubt.alpha1e.ui.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.ui.FindPassWdActivity;
import com.ubt.alpha1e.ui.helper.FindPwdHelper;

public class ResetPwdFragment extends BaseRegisterFragment {


    private EditText edt_pwd;


    public ResetPwdFragment(){}

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_reset_pwd;
    }

    @Override
    public void gotoNextStep() {

        if(mHelper!=null)
        {
            ((FindPassWdActivity)mActivity).showLoadingDailog();
            ((FindPwdHelper)mHelper).doResetPwd(edt_pwd.getText().toString());
        }
    }

    @Override
    protected void initViews() {
        edt_pwd = (EditText) mView.findViewById(R.id.edt_passwd);
    }

    @Override
    protected void initListeners() {
        edt_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((FindPassWdActivity)mActivity).setNextButtonEnable(s.length()>=6&& s.length()<=16);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }





}
