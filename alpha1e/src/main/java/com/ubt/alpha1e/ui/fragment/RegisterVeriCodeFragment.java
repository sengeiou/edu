/*
 * Copyright (C) 2008-2016 UBT Corporation.  All rights reserved.
 * Redistribution,modification, and use in source and binary forms are not permitted unless
 * otherwise authorized by UBT.
 *
 *
 */

package com.ubt.alpha1e.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.model.RegisterInfo;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.FindPassWdActivity;
import com.ubt.alpha1e.ui.RegisterNextStepActivity;
import com.ubt.alpha1e.ui.helper.FindPwdHelper;
import com.ubt.alpha1e.ui.helper.RegisterHelper;
import com.ubt.alpha1e.utils.NavigateUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
  * User: wilson
  * Description: register fragment verify code 手机注册
  * Time: 2016/7/13 16:45
  */

public class RegisterVeriCodeFragment extends BaseRegisterFragment {

    private TextView txt_phone_num;
    private EditText edt_verify_code;
    private Button btn_get_verifycode;
    private Timer code_timer;
    private int total_time = 60;
    private RegisterInfo registInfo;


    public RegisterVeriCodeFragment() {
    }

    public static RegisterVeriCodeFragment newInstance(RegisterInfo info) {
        RegisterVeriCodeFragment fragment = new RegisterVeriCodeFragment();
        Bundle args = new Bundle();
        args.putParcelable(NavigateUtil.INSTANCE.REGISTER, PG.convertParcelable(info));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            registInfo = getArguments().getParcelable(NavigateUtil.INSTANCE.REGISTER);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden)
        {
            btn_get_verifycode
                    .setText( ((BaseActivity)mActivity).getStringResources("ui_register_get_vertify_code"));
            btn_get_verifycode.setTextSize(18);
            code_timer.cancel();
            code_timer = null;
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_register_veri_code;
    }

    @Override
    public void gotoNextStep() {

        if(mHelper!=null)
        {
            if(registInfo.isFindPws)//找回密码
            {
                ((FindPassWdActivity)mActivity).showLoadingDailog();
                ((FindPwdHelper)mHelper).doCheckVCode(registInfo.account,edt_verify_code.getText().toString());
            }else//注册
            {
                ((RegisterHelper)mHelper).doReigster(registInfo.countryCode,registInfo.account,registInfo.password,
                        edt_verify_code.getText().toString(), RegisterHelper.Register_type.Phone_type);
            }

        }
    }

    @Override
    protected void initViews() {
        txt_phone_num = (TextView)mView.findViewById(R.id.txt_verify_phone);
        edt_verify_code = (EditText)mView.findViewById(R.id.edt_phone_verification_code);
        btn_get_verifycode = (Button)mView.findViewById(R.id.btn_get_verify_code);
        txt_phone_num.setText(registInfo.account);
        doGetVerifyCode();
        doStartCodeTimer();
    }

    @Override
    protected void initListeners()
    {
        edt_verify_code.setFocusable(true);
        edt_verify_code.setFocusableInTouchMode(true);
        edt_verify_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(registInfo.isFindPws)
                {
                    ((FindPassWdActivity)mActivity).setNextButtonEnable(s.length()>0);
                }else
                    ((RegisterNextStepActivity)mActivity).setNextButtonEnable(s.length()>0);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_get_verifycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(code_timer!=null)
                    return;
                doGetVerifyCode();
                doStartCodeTimer();
            }
        });
    }

    private void doGetVerifyCode()
    {
        GetDataFromWeb.getJsonByPost(1001, HttpAddress
                        .getRequestUrl(HttpAddress.Request_type.get_verification), HttpAddress
                        .getParamsForPost(new String[]{registInfo.account},
                                HttpAddress.Request_type.get_verification, mActivity), new IJsonListener() {
                    @Override
                    public void onGetJson(boolean isSuccess, String json, long request_code) {

                    }
                }

        );

    }

    private void doStartCodeTimer()
    {
        code_timer = new Timer();
        code_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                total_time -= 1;
                if (total_time <= 0) {
                    total_time = 60;
                    // 可以点击获取验证码
                    btn_get_verifycode.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                btn_get_verifycode
                                        .setText( ((BaseActivity)mActivity).getStringResources("ui_register_get_vertify_code"));
                                btn_get_verifycode.setTextSize(18);
                                code_timer.cancel();
                                code_timer = null;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    // 不可点击获取验证码
                    btn_get_verifycode.post(new Runnable() {
                        @Override
                        public void run() {
                            // tv_reverseCount.setText(totle_time +
                            // "s");
                            // btn_phone_verification_code
                            // .setText("Later");
                            String newMessageInfo = ((BaseActivity)mActivity).getStringResources("ui_register_resend_time").replace("#","<font color='#e74c3c' size = '6'>"
                                    + total_time
                                    + "</font>"
                            );
                            btn_get_verifycode.setText(Html
                                    .fromHtml(newMessageInfo));
                            btn_get_verifycode.setTextSize(12);
                        }
                    });

                }
            }
        }, 0, 1000);
    }
}
