package com.ubt.alpha1e.userinfo.psdmanage.psdmodify;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.custom.ClearableEditText;
import com.ubt.alpha1e.ui.dialog.SLoadingDialog;
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

public class PsdModifyFragment extends MVPBaseFragment<PsdModifyContract.View, PsdModifyPresenter> implements PsdModifyContract.View {

    private static final String TAG = PsdModifyFragment.class.getSimpleName();

    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    Unbinder unbinder;
    @BindView(R.id.edt_old_password)
    ClearableEditText edtOldPassword;
    @BindView(R.id.edt_new_password)
    ClearableEditText edtNewPassword;

    protected Dialog mCoonLoadingDia;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public static PsdModifyFragment newInstance() {
        PsdModifyFragment psdModifyFragment = new PsdModifyFragment();
        return psdModifyFragment;
    }

    @Override
    protected void initUI() {
        mCoonLoadingDia = SLoadingDialog.getInstance(getContext());
        setViewEnable(tvConfirm, false);

        edtOldPassword.addTextChangedListener(new TextWatcher() {
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

        edtNewPassword.addTextChangedListener(new TextWatcher() {
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

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        if(AlphaApplication.isPad()){
            return R.layout.fragment_pswmanage_modify_pad;
        }else {
            return R.layout.fragment_pswmanage_modify;
        }
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
        if (mCoonLoadingDia != null) {
            mCoonLoadingDia.cancel();
        }
        unbinder.unbind();
    }

    @OnClick({R.id.tv_confirm, R.id.tv_forget_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm:
                doModifyPassword();
                break;
            case R.id.tv_forget_password:
                ((PsdManageActivity) getActivity()).switchFragment(PsdManageActivity.FRAGMENT_FORGET_PASSWORD);
                break;
        }
    }

    @Override
    public void onModifyPassword(final boolean isSuccess, final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCoonLoadingDia != null) {
                    mCoonLoadingDia.cancel();
                }
                if (isSuccess) {
                    ToastUtils.showShort(((MVPBaseActivity) getActivity()).getStringResources("ui_setting_password_modify_success"));
                    getActivity().finish();
                } else {
                    ToastUtils.showShort(msg);
                }
            }
        });
    }

    private boolean doCheck() {

        String oldPassword = edtOldPassword.getText().toString();
        String newPassword = edtNewPassword.getText().toString();

        if (TextUtils.isEmpty(oldPassword) || oldPassword.length() < 6) {
            //ToastUtils.showShort(getStringRes("ui_old_password_request"));
            return false;
        }

        if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
            //ToastUtils.showShort(getStringRes("ui_new_password_request"));
            return false;
        }
        return true;
    }

    private void doModifyPassword() {

        if (mCoonLoadingDia == null) {
            mCoonLoadingDia = SLoadingDialog.getInstance(getContext());
        }
        mCoonLoadingDia.cancel();
        mCoonLoadingDia.show();

        mPresenter.doModifyPassword(edtOldPassword.getText().toString(), edtNewPassword.getText().toString());
    }

    private void setViewEnable(View mView, boolean enable) {
        mView.setEnabled(enable);
        if (enable) {
            mView.setAlpha(1f);
        } else {
            mView.setAlpha(0.3f);
        }
    }
}
