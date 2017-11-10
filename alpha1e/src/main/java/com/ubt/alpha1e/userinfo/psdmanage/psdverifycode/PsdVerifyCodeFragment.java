package com.ubt.alpha1e.userinfo.psdmanage.psdverifycode;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.login.loginauth.CheckPhoneNumberUtil;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.custom.ClearableEditText;
import com.ubt.alpha1e.userinfo.psdmanage.PsdManageActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PsdVerifyCodeFragment extends MVPBaseFragment<PsdVerifyCodeContract.View, PsdVerifyCodePresenter> implements PsdVerifyCodeContract.View {

    private static final String TAG = PsdVerifyCodeFragment.class.getSimpleName();

    private static final int GO_TO_NEXT = 1;

    private static final long REQUEST_TIME = 61 * 1000;

    RequestCountDown requestCountDown;
    @BindView(R.id.edt_phone)
    ClearableEditText edtPhone;
    @BindView(R.id.tv_get_verify_code)
    TextView tvGetVerifyCode;
    @BindView(R.id.edt_verify_code)
    ClearableEditText edtVerifyCode;

    Unbinder unbinder;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GO_TO_NEXT:
                    break;
            }
        }
    };


    public PsdVerifyCodeFragment() {

    }

    @Override
    protected void initUI() {
        setViewEnable(tvGetVerifyCode,false);
        setViewEnable(tvConfirm,false);
        requestCountDown = new RequestCountDown(REQUEST_TIME, 1000);

        edtPhone.addTextChangedListener(new TextWatcher() {
            String telephone = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                telephone = edtPhone.getText().toString();
                if (CheckPhoneNumberUtil.isChinaPhoneLegal(telephone)) {
                    setViewEnable(tvGetVerifyCode, true);
                } else {
                    setViewEnable(tvGetVerifyCode, false);
                    setViewEnable(tvConfirm, false);
                }
            }
        });

        edtVerifyCode.addTextChangedListener(new TextWatcher() {
            String verifyCode = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                verifyCode = edtVerifyCode.getText().toString();
                if (!TextUtils.isEmpty(verifyCode) && verifyCode.length() == 4
                        && CheckPhoneNumberUtil.isChinaPhoneLegal(edtPhone.getText().toString())) {
                    setViewEnable(tvConfirm, true);
                } else {
                    setViewEnable(tvConfirm, false);
                }
            }
        });

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_pswmanage_verify_code;
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_get_verify_code, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_get_verify_code:
                requestCountDown.cancel();
                requestCountDown.start();
                setViewEnable(tvGetVerifyCode,false);
                //mPresenter.doGetVerifyCode(edtPhone.getText().toString());
                break;
            case R.id.tv_confirm:
                //mPresenter.doVerifyCode(edtVerifyCode.getText().toString());
                ((PsdManageActivity)getActivity()).switchFragment(PsdManageActivity.FRAGMENT_SETTING_PASSWORD);
                break;

        }
    }

    @Override
    public void onGetVerifyCode(boolean isSuccess, String errorMsg) {
        UbtLog.d(TAG, "onGetVerifyCode = " + isSuccess);
        if (isSuccess) {

        }
    }

    @Override
    public void onVerifyCode(boolean isSuccess, String errorMsg) {
        if (isSuccess) {
            mHandler.sendEmptyMessage(GO_TO_NEXT);
        }else {

        }
    }

    class RequestCountDown extends CountDownTimer {

        public RequestCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {

            tvGetVerifyCode.setText(getStringRes("ui_register_get_vertify_code"));
            setViewEnable(tvGetVerifyCode,true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvGetVerifyCode.setText("" + (millisUntilFinished / 1000) + " s");
        }
    }

    private void setViewEnable(View mView, boolean enable){
        mView.setEnabled(enable);
        if (enable) {
            mView.setAlpha(1f);
        } else {
            mView.setAlpha(0.3f);
        }
    }

    @Override
    public void onDestroy() {
        requestCountDown.cancel();
        super.onDestroy();
    }
}
