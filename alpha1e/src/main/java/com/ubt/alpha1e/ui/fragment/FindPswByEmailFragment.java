/*
 * Copyright (C) 2008-2016 UBT Corporation.  All rights reserved.
 * Redistribution,modification, and use in source and binary forms are not permitted unless
 * otherwise authorized by UBT.
 *
 *
 */

package com.ubt.alpha1e.ui.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.DataCheckTools;
import com.ubt.alpha1e.ui.FindPassWdActivity;
import com.ubt.alpha1e.ui.helper.FindPwdHelper;

public class FindPswByEmailFragment extends BaseRegisterFragment {


    private EditText edtEmail;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_find_pwd_by_email;
    }

    @Override
    public void gotoNextStep() {

        if(mHelper!=null)
        {
            ((FindPassWdActivity)mActivity).showLoadingDailog();
            ((FindPwdHelper)mHelper).doStartFind(edtEmail.getText().toString());
        }
    }

    @Override
    protected void initViews() {
        edtEmail =(EditText) mView.findViewById(R.id.edt_name);
    }

    @Override
    protected void initListeners() {
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((FindPassWdActivity)mActivity).setNextButtonEnable(s.length()>0&&DataCheckTools.isEmail(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


}
