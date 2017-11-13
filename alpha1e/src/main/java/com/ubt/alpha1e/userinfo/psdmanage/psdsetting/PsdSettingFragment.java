package com.ubt.alpha1e.userinfo.psdmanage.psdsetting;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.login.loginauth.CheckPhoneNumberUtil;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.custom.ClearableEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PsdSettingFragment extends MVPBaseFragment<PsdSettingContract.View, PsdSettingPresenter> implements PsdSettingContract.View {

    private static final String TAG = PsdSettingFragment.class.getSimpleName();

    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    Unbinder unbinder;
    @BindView(R.id.edt_password_1)
    ClearableEditText edtPassword1;
    @BindView(R.id.edt_password_2)
    ClearableEditText edtPassword2;

    public PsdSettingFragment() {

    }

    @Override
    protected void initUI() {
        setViewEnable(tvConfirm,false);

        edtPassword1.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (doCheck()) {
                    setViewEnable(tvConfirm, true);
                } else {
                    setViewEnable(tvConfirm, false);
                }
            }
        });

        edtPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (doCheck()) {
                    setViewEnable(tvConfirm, true);
                } else {
                    setViewEnable(tvConfirm, false);
                }
            }
        });
    }

    private boolean doCheck(){
        boolean isPass = false;
        String password1 = edtPassword1.getText().toString();
        if (!TextUtils.isEmpty(password1) && password1.length() == 6 ) {
            isPass = true;
        } else {
            isPass = false;
        }

        String password2 = edtPassword2.getText().toString();
        if (isPass && !TextUtils.isEmpty(password2) && password2.length() == 6 ) {
            isPass = true;
        } else {
            isPass = false;
        }
        return isPass;
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_pswmanage_setting;
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

    @OnClick({R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm:
                break;
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
}
