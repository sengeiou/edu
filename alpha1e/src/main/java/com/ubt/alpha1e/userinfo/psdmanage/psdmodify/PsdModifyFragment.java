package com.ubt.alpha1e.userinfo.psdmanage.psdmodify;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.custom.ClearableEditText;
import com.ubt.alpha1e.userinfo.psdmanage.PsdManageActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PsdModifyFragment extends MVPBaseFragment<PsdModifyContract.View, PsdModifyPresenter> implements PsdModifyContract.View {

    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    Unbinder unbinder;
    @BindView(R.id.edt_old_password)
    ClearableEditText edtOldPassword;
    @BindView(R.id.edt_new_password)
    ClearableEditText edtNewPassword;

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_pswmanage_modify;
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

    @OnClick({R.id.tv_confirm, R.id.tv_forget_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm:
                mPresenter.doModifyPassword(edtOldPassword.getText().toString(),edtNewPassword.getText().toString());
                break;
            case R.id.tv_forget_password:
                ((PsdManageActivity)getActivity()).switchFragment(PsdManageActivity.FRAGMENT_FORGET_PASSWORD);
                break;
        }
    }

    @Override
    public void onModifyPassword(boolean isSuccess, String error_msg) {
        if(isSuccess){
            ToastUtils.showShort(((MVPBaseActivity)getActivity()).getStringResources("ui_setting_password_modify_success"));
        }else {
            ToastUtils.showShort(error_msg);
        }
    }
}
