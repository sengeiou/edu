package com.ubt.alpha1e_edu.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e_edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e_edu.ui.fragment.BaseRegisterFragment;
import com.ubt.alpha1e_edu.ui.fragment.FindPswByEmailFragment;
import com.ubt.alpha1e_edu.ui.fragment.FindPswPhoneNumFragment;
import com.ubt.alpha1e_edu.ui.fragment.FindPswEmailLoginFragment;
import com.ubt.alpha1e_edu.ui.fragment.ResetPwdFragment;
import com.ubt.alpha1e_edu.ui.helper.FindPwdHelper;
import com.ubt.alpha1e_edu.ui.helper.FindPwdHelper.Find_Fail_type;
import com.ubt.alpha1e_edu.ui.helper.FindPwdHelper.Find_Type;
import com.ubt.alpha1e_edu.ui.helper.IFindPwdUI;
import com.ubt.alpha1e_edu.utils.NavigateUtil;

import org.json.JSONObject;

public class FindPassWdActivity extends BaseActivity implements IFindPwdUI,
        BaseDiaUI {

    private LinearLayout lay_back;
    public boolean isPhoneLogin = true;
    public Fragment mCurrentFragment;
    private Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pass_wd);

        mHelper = new FindPwdHelper(this);
        ((FindPwdHelper) mHelper).addToListenerList(this);
        Bundle b = this.getIntent().getExtras();
        isPhoneLogin = b.getBoolean("isPhoneLogin", true);
        initUI();
        initControlListener();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NavigateUtil.INSTANCE.NAV_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String mCountryName = bundle.getString("countryName");
                String mCountryNumber = bundle.getString("countryNumber");
                if (mCurrentFragment != null)
                    ((FindPswPhoneNumFragment) mCurrentFragment).setCountryInfo(mCountryName, mCountryNumber);

            }
        }
    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub
//        mFragmentManager = this.getFragmentManager();
//        mFragmentTransaction = this.mFragmentManager.beginTransaction();
//        mFindPwdFragment = new FindPwdFragment((FindPwdHelper) mHelper);
//        Bundle b = new Bundle();
//        b.putBoolean("isPhoneLogin", isPhoneLogin);
//        mFindPwdFragment.setArguments(b);
//        mFragmentTransaction.add(R.id.lay_input_info, mFindPwdFragment);
//        mFragmentTransaction.commit();
        mCoonLoadingDia = LoadingDialog.getInstance(this, this);
        if (isPhoneLogin) {
            FindPswPhoneNumFragment findPswPhoneNumberFragment = FindPswPhoneNumFragment.newInstance();
            loadFragment(findPswPhoneNumberFragment);
        } else {
            FindPswByEmailFragment findPswByEmailFragment = new FindPswByEmailFragment();
            loadFragment(findPswByEmailFragment);
        }
        btn_next = (Button) findViewById(R.id.btn_base_save);
        lay_back = (LinearLayout) findViewById(R.id.lay_base_back);
        initTitle(getStringResources("ui_forget_pwd"));
        initTitleSave(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((BaseRegisterFragment) mCurrentFragment).gotoNextStep();
            }
        }, getStringResources("ui_perfect_next"));
        setNextButtonEnable(false);

    }

    public void setNextButtonEnable(boolean isEnable) {

        if (isEnable) {
            btn_next.setTextColor(getResources().getColor(R.color.T5));
        } else {
            btn_next.setTextColor(getResources().getColor(R.color.T7));
        }
        btn_next.setEnabled(isEnable);

    }

    public void setNextButtonGone() {
        btn_next.setVisibility(View.GONE);
    }

    public void showLoadingDailog() {
        if (mCoonLoadingDia != null && !mCoonLoadingDia.isShowing() && !isFinishing())
            mCoonLoadingDia.show();

    }

    public void dismissLoadingDailog() {
        if (mCoonLoadingDia != null && mCoonLoadingDia.isShowing() && !isFinishing())
            mCoonLoadingDia.cancel();

    }

    @Override
    protected void initControlListener() {

        android.view.View.OnClickListener back_listener = new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                FindPassWdActivity.this.finish();
            }
        };
        lay_back.setOnClickListener(back_listener);
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStartFindPwd(boolean is_success, Find_Type type,
                               JSONObject info) {
//        dismissLoadingDailog();
//        if (is_success) {
//            if (mFindPwdByPhoneFragment == null)
//                mFindPwdByPhoneFragment = new FindPwdByPhoneFragment(
//                        (FindPwdHelper) mHelper);
//            mFragmentTransaction = this.mFragmentManager.beginTransaction();
//            mFragmentTransaction.replace(R.id.lay_input_info,
//                    mFindPwdByPhoneFragment);
//            mFragmentTransaction.commit();
//
//        } else {
//            Toast.makeText(this,
//                    this.getResources().getString(R.string.ui_login_forget_find_fail),
//                    500).show();
//        }
    }

    @Override
    public void onNoteVCodeInvalid() {
        // TODO Auto-generated method stub
        dismissLoadingDailog();
        showToast("ui_register_prompt_vertify_code_failed");
    }

    @Override
    public void doReSetPwd(String account, Find_Type type) {
        dismissLoadingDailog();
        if (type == Find_Type.by_email) {

            FindPswEmailLoginFragment findPswEmailLoginFragment = FindPswEmailLoginFragment.newInstance(account);
            loadFragment(findPswEmailLoginFragment);
        } else {
            ResetPwdFragment mResetPwdFragment = new ResetPwdFragment();
            loadFragment(mResetPwdFragment);
        }
    }

    @Override
    public void onNoteResetPwd(Boolean obj) {
        dismissLoadingDailog();
        if (obj) {
            Toast.makeText(
                    this,getStringResources("ui_forget_reset_password_success"), Toast.LENGTH_SHORT).show();
            this.finish();

        } else {
            Toast.makeText(
                    this,
                    getStringResources("ui_forget_reset_password_failed"), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void doReSetPwdFail(Find_Fail_type type) {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteWaitWebProcressShutDown() {
        // TODO Auto-generated method stub

    }

    public void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            if (mCurrentFragment == null)
                transaction.add(R.id.lay_input_info, targetFragment).commit();
            else {
                setNextButtonEnable(false);
                transaction
                        .hide(mCurrentFragment)
                        .add(R.id.lay_input_info, targetFragment)
                        .commit();
            }
        } else {
            transaction
                    .hide(mCurrentFragment)
                    .show(targetFragment)
                    .commit();
        }
        mCurrentFragment = targetFragment;
    }

}
