package com.ubt.alpha1e.userinfo.psdmanage.psdverifycode;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
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

    protected Dialog mCoonLoadingDia;
    @BindView(R.id.tv_tel_prefix)
    TextView tvTelPrefix;

    private String[] mPhoneItems;
    private String mPhonePrefix = "86";

    public static PsdVerifyCodeFragment newInstance() {
        PsdVerifyCodeFragment psdVerifyCodeFragment = new PsdVerifyCodeFragment();
        return psdVerifyCodeFragment;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_TO_NEXT:
                    requestCountDown.cancel();
                    ((PsdManageActivity) getActivity()).switchFragment(PsdManageActivity.FRAGMENT_SETTING_PASSWORD);
                    break;
            }
        }
    };


    public PsdVerifyCodeFragment() {

    }

    @Override
    protected void initUI() {
        mPhoneItems = getResources().getStringArray(R.array.login_phone_area);
        setViewEnable(tvGetVerifyCode, false);
        setViewEnable(tvConfirm, false);
        requestCountDown = new RequestCountDown(REQUEST_TIME, 1000);
        mCoonLoadingDia = SLoadingDialog.getInstance(getContext());

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
                if (telephone.length() > 0) {
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
                        && edtPhone.getText().toString().length() > 0) {
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
        if (AlphaApplication.isPad()) {
            return R.layout.fragment_pswmanage_verify_code_pad;
        } else {
            return R.layout.fragment_pswmanage_verify_code;
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
        unbinder.unbind();
    }

    @OnClick({R.id.tv_get_verify_code, R.id.tv_confirm, R.id.tv_tel_prefix})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_get_verify_code:
                requestCountDown.cancel();
                requestCountDown.start();
                setViewEnable(tvGetVerifyCode, false);

                mCoonLoadingDia.cancel();
                mCoonLoadingDia.show();
                mPresenter.doGetVerifyCode(mPhonePrefix+edtPhone.getText().toString());
                break;
            case R.id.tv_confirm:
                mCoonLoadingDia.cancel();
                mCoonLoadingDia.show();
                mPresenter.doVerifyCode(mPhonePrefix + edtPhone.getText().toString(), edtVerifyCode.getText().toString());
                break;
            case R.id.tv_tel_prefix:
                showPhonePop();
                break;
        }
    }

    @Override
    public void onGetVerifyCode(final boolean isSuccess, String msg) {
        UbtLog.d(TAG, "onGetVerifyCode = " + isSuccess);

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (mCoonLoadingDia != null) {
                    mCoonLoadingDia.cancel();
                }

                if (isSuccess) {
                    edtPhone.setFocusable(false);
                    edtPhone.setFocusableInTouchMode(false);
                    ToastUtils.showShort(getStringRes("ui_setting_verify_code_send_success"));
                } else {
                    edtPhone.setFocusable(true);
                    edtPhone.setFocusableInTouchMode(true);

                    ToastUtils.showShort(getStringRes("ui_setting_verify_code_send_fail"));
                    requestCountDown.cancel();
                    tvGetVerifyCode.setText(getStringRes("ui_register_get_vertify_code"));
                    setViewEnable(tvGetVerifyCode, true);
                }
            }
        });
    }

    @Override
    public void onVerifyCode(final boolean isSuccess, String errorMsg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCoonLoadingDia != null) {
                    mCoonLoadingDia.cancel();
                }

                if (isSuccess) {
                    mHandler.sendEmptyMessage(GO_TO_NEXT);
                } else {
                    ToastUtils.showShort(getStringRes("ui_setting_password_verify_fail"));
                }
            }
        });

    }

    class RequestCountDown extends CountDownTimer {

        public RequestCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            edtPhone.setFocusable(true);
            edtPhone.setFocusableInTouchMode(true);

            tvGetVerifyCode.setText(getStringRes("ui_register_get_vertify_code"));
            setViewEnable(tvGetVerifyCode, true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvGetVerifyCode.setText("" + (millisUntilFinished / 1000) + " s");
        }
    }

    private void setViewEnable(View mView, boolean enable) {
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
        if (mCoonLoadingDia != null) {
            mCoonLoadingDia.cancel();
        }
        super.onDestroy();
    }

    private void showPhonePop() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.phone_popwin_dropdown_stytle, null);
        ListView lsvMore = (ListView) contentView.findViewById(R.id.lsvMore);
        lsvMore.setAdapter(new ArrayAdapter<String>(mContext, R.layout.item_phone_prefix, mPhoneItems));

        final PopupWindow mPopWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ffffff")));
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.update();
        mPopWindow.showAsDropDown(tvTelPrefix, 0, 0);
        lsvMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str = mPhoneItems[position];
                mPhonePrefix = str.replace("+", "");
                tvTelPrefix.setText(str);
                UbtLog.e(TAG, "position:" + position+ "   mPhonePrefix:"+mPhonePrefix);
                mPopWindow.dismiss();
            }
        });
    }
}
