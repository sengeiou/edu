package com.ubt.alpha1e.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.AlphaApplicationValues.Thrid_login_type;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.business.thrid_party.MyFaceBook;
import com.ubt.alpha1e.business.thrid_party.MyTencent;
import com.ubt.alpha1e.business.thrid_party.MyTwitter;
import com.ubt.alpha1e.data.model.AlphaStatics;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.ui.fragment.EmailRegisterFragment;
import com.ubt.alpha1e.ui.fragment.PhoneRegisterFragment;
import com.ubt.alpha1e.ui.helper.ILoginUI;
import com.ubt.alpha1e.ui.helper.LoginHelper;
import com.ubt.alpha1e.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.ui.helper.RegisterHelper;
import com.ubt.alpha1e.utils.log.MyLog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

public class RegisterActivity extends BaseActivity implements ILoginUI,
        BaseDiaUI {

    //	private TextView txt_agree;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private PhoneRegisterFragment mPhoneRegisterFragment;
    private EmailRegisterFragment mEmailRegisterFragment;
//    private Button btn_register_by_phone;
    private Button btn_register;
    private Thrid_login_type mCurrentThirdLoginType;
    private LoginHelper mLoginHelper;
    private boolean isFragmentEmail = true;
    private TextView mAlreadyAccount;
    private TextView mInvalidMessage;
    public boolean is_login_sigle_task = false;
    private LoadingDialog mLoadingDialog;

    private TextView btn_login_by_phone;
    private TextView btn_login_by_email;
    private ImageView im_phone_line, im_email_line;
    private ImageView img_cancel;
    private String registerAccount;
    private Boolean isPhoneLogin = true;
    private Fragment mCurrentFragment;
    public boolean isRegisterComplete = false;//注册流程是否完成
    //private TextView tvProvision;
    private TextView tvProvisionDes;
    private ImageView ivAgree;

    public interface EmailFragmentListener {

        void checkRegisterInput();
    }

    public interface PhoneFragmentListener {

        void checkRegisterInput();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._activity_register);
        initUI();
        initControlListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isRegisterComplete) {
            ((AlphaApplication) getApplication()).clearCurrentUserInfo();
        }
    }

    public void toastForWrongMeassage(String str) {

        mInvalidMessage.setVisibility(View.VISIBLE);
        mInvalidMessage.setText(str);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        mInvalidMessage.startAnimation(shake);
    }

    public void setLoginButtonEnable(boolean isEnable)
    {
        btn_register.setEnabled(isEnable);
    }

    public void clearWrongMessage() {
        mInvalidMessage.setText("");
    }

//    public void changeButtonText(boolean isChange, boolean isEmail) {
//        if (btn_register_by_phone != null) {
//            if (isChange) {
//                btn_register_by_phone.setText(RegisterActivity.this
//                        .getResources().getString(R.string.ui_register));
//            } else {
//                if (isEmail) {
//                    btn_register_by_phone.setText(RegisterActivity.this
//                            .getResources().getString(
//                                    R.string.ui_register_via_phone));
//                } else {
//                    btn_register_by_phone.setText(RegisterActivity.this
//                            .getResources().getString(
//                                    R.string.ui_register_via_email));
//                }
//            }
//        }
//    }

    @Override
    protected void initUI() {

        try {
            is_login_sigle_task = getIntent().getExtras().getBoolean(
                    LoginHelper.IS_LOGIN_SIGLE);
            registerAccount =  getIntent().getExtras().getString(LoginHelper.ACCOUNT);
            isPhoneLogin = getIntent().getExtras().getBoolean(LoginHelper.PHONE_REGISTER);
        } catch (Exception e) {
            is_login_sigle_task = false;
            registerAccount = "";
            isPhoneLogin = true;
        }

        btn_register = (Button)findViewById(R.id.btn_login);
        btn_login_by_phone = (TextView) findViewById(R.id.btn_login_by_phone);
        im_phone_line = (ImageView) findViewById(R.id.im_phone_line);
        btn_login_by_email = (TextView) findViewById(R.id.btn_login_by_email);
        im_email_line = (ImageView) findViewById(R.id.im_email_line);
        mInvalidMessage = (TextView) findViewById(R.id.tv_invalid_msg);
//        btn_register_by_phone = (Button) findViewById(R.id.btn_register_by_phone_num);
//        btn_register_by_phone.setText(getStringResources("ui_register_via_phone"));
        mAlreadyAccount = (TextView) findViewById(R.id.tv_account_already);
        img_cancel = (ImageView)findViewById(R.id.img_cancel);
        // --------------------------------------
		//tvProvision = (TextView) findViewById(R.id.txt_provision);
        tvProvisionDes = (TextView) findViewById(R.id.txt_provision_black);
        ivAgree = (ImageView)findViewById(R.id.img_register_select);
        ivAgree.setSelected(true);


        //ui_register_accept
        String registerAccept = getStringResources("ui_register_accept");
        String registerClause = getStringResources("ui_register_clause");

        SpannableString style=new SpannableString(registerAccept);
        style.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.T1));
                ds.setUnderlineText(false);      //设置下划线
            }

            @Override
            public void onClick(View widget) {
                ivAgree.setSelected(!ivAgree.isSelected());
                setLoginButtonEnable(ivAgree.isSelected());
            }
        }, 0, registerAccept.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.T1)),0,registerAccept.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        /*style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.txt_provision)),registerAccept.length(),
                registerAccept.length()+registerClause.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
        tvProvisionDes.setText(style);

        SpannableString spStr = new SpannableString(registerClause);
        spStr.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.txt_provision));
                ds.setUnderlineText(false);      //设置下划线
            }

            @Override
            public void onClick(View widget) {
                WebContentActivity.launchActivity(RegisterActivity.this, HttpAddress.getRequestUrl(HttpAddress.Request_type.getServiceRule),"",false);
            }
        }, 0, registerClause.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvProvisionDes.append(spStr);
        tvProvisionDes.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件

//		int index_0 = txt_agree.getText().toString().indexOf("”");
//		int index_1 = index_0
//				+ txt_agree.getText().toString().substring(index_0)
//				.indexOf("“");
//		int index_2 = index_1
//				+ txt_agree.getText().toString().substring(index_1)
//				.indexOf("”");
//		int index_3 = index_2
//				+ txt_agree.getText().toString().substring(index_2)
//				.indexOf("“");
//		int index_4 = index_3
//				+ txt_agree.getText().toString().substring(index_3)
//				.indexOf("”");
//		SpannableStringBuilder builder = new SpannableStringBuilder(txt_agree
//				.getText().toString());
//		ForegroundColorSpan greenSpan = new ForegroundColorSpan(this
//				.getResources().getColor(R.color.txt_green));
//		ForegroundColorSpan greenSpan_2 = new ForegroundColorSpan(this
//				.getResources().getColor(R.color.txt_green));
//		builder.setSpan(greenSpan, index_1 + 1, index_2,
//				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		builder.setSpan(greenSpan_2, index_3 + 1, index_4,
//				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		txt_agree.setText(builder);
        // --------------------------------------
        navigateFragment(isPhoneLogin);
        setLoginButtonEnable(false);
        registerAccount = "";

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        MyLog.writeLog("第三方登录", requestCode + "-->" + resultCode);

        if (mPhoneRegisterFragment != null
                && requestCode == mPhoneRegisterFragment.MSG_GET_COUNTRY) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String mCountryName = bundle.getString("countryName");
                String mCountryNumber = bundle.getString("countryNumber");
                if (mPhoneRegisterFragment != null) {
                    try {
                        mPhoneRegisterFragment.setCountryInfo(mCountryName,
                                mCountryNumber);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if(requestCode == RegisterHelper.RegisterCompleteCode)
        {
            if(data == null)
                return;
            isRegisterComplete = (Boolean) data.getExtras().get(
                    RegisterHelper.REGISTER_SUCCESS);
            Intent intent = new Intent();
            intent.putExtra(RegisterHelper.REGISTER_SUCCESS,isRegisterComplete);
            setResult(RegisterHelper.RegisterCompleteCode, intent);
            finish();

        }else {
            if (MyTencent.isNeedOnResualt) {
                Tencent.onActivityResultData(requestCode, resultCode, data,
                        (IUiListener) mLoginHelper);
                MyTencent.isNeedOnResualt = false;
            } else if (MyFaceBook.isNeedOnResualt) {
                MyLog.writeLog("第三方登录", "MyFaceBook.isNeedOnResualt");
                MyFaceBook.onActivityResult(requestCode, resultCode, data);
                MyFaceBook.isNeedOnResualt = false;
            } else if (MyTwitter.requestCode_tw == requestCode) {
                MyLog.writeLog("第三方登录", "MyTwitter.requestCode_tw");
                MyTwitter.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        ((TextView)findViewById(R.id.txt_base_title_name)).setText(getStringResources("ui_register"));
    }

    private void navigateFragment(boolean isPhoneLogin)
    {
        if(isPhoneLogin)
        {
            if (mPhoneRegisterFragment == null)
                mPhoneRegisterFragment = PhoneRegisterFragment.newInstance(registerAccount);

            loadFragment(mPhoneRegisterFragment);
            isFragmentEmail = false;
            btn_login_by_email.setTextColor(getResources().getColor(
                    R.color.T7));
            btn_login_by_phone.setTextColor(getResources().getColor(
                    R.color.T5));
            im_email_line.setVisibility(View.INVISIBLE);
            im_phone_line.setVisibility(View.VISIBLE);
        }else
        {
            if (mEmailRegisterFragment == null)
                mEmailRegisterFragment = EmailRegisterFragment.newInstance(registerAccount);

            loadFragment(mEmailRegisterFragment);
            isFragmentEmail = true;
            btn_login_by_email.setTextColor(getResources().getColor(
                    R.color.T5));
            btn_login_by_phone.setTextColor(getResources().getColor(
                    R.color.T7));
            im_email_line.setVisibility(View.VISIBLE);
            im_phone_line.setVisibility(View.INVISIBLE);
            im_email_line.setBackgroundColor(getResources().getColor(R.color.T5));
        }
    }



    @Override
    protected void initControlListener() {
        // TODO Auto-generated method stub

        android.view.View.OnClickListener back_listener = new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                RegisterActivity.this.finish();
            }
        };
        btn_login_by_email.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                navigateFragment(false);
//                if (mEmailRegisterFragment == null)
//                    mEmailRegisterFragment = new EmailRegisterFragment();
//                mFragmentTransaction = RegisterActivity.this.mFragmentManager
//                        .beginTransaction();
//                mFragmentTransaction.replace(R.id.lay_content,
//                        mEmailRegisterFragment);
//                mFragmentTransaction.commit();
//                isFragmentEmail = true;
//                btn_login_by_email.setTextColor(getResources().getColor(
//                        R.color.T5));
//                btn_login_by_phone.setTextColor(getResources().getColor(
//                        R.color.T7));
//                im_email_line.setVisibility(View.VISIBLE);
//                im_phone_line.setVisibility(View.INVISIBLE);
//                im_email_line.setBackgroundColor(getResources().getColor(R.color.T5));
            }
        });
        btn_login_by_phone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                navigateFragment(true);

//                if (mPhoneRegisterFragment == null)
//                    mPhoneRegisterFragment = new PhoneRegisterFragment();
//                mFragmentTransaction = RegisterActivity.this.mFragmentManager
//                        .beginTransaction();
//                mFragmentTransaction.replace(R.id.lay_content,
//                        mPhoneRegisterFragment);
//                mFragmentTransaction.commit();
//                isFragmentEmail = false;
//                btn_login_by_email.setTextColor(getResources().getColor(
//                        R.color.T7));
//                btn_login_by_phone.setTextColor(getResources().getColor(
//                        R.color.T5));
//                im_email_line.setVisibility(View.INVISIBLE);
//                im_phone_line.setVisibility(View.VISIBLE);
            }
        });
        btn_register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFragmentEmail) {
                        if (mEmailRegisterFragment != null)
                            mEmailRegisterFragment.checkRegisterInput();
                    } else {
                        if ((mPhoneRegisterFragment != null))
                            mPhoneRegisterFragment.checkRegisterInput();
                    }
                    MobclickAgent.onEvent(RegisterActivity.this, AlphaStatics.REGISTER_TRY);//注册次数统计
            }
        });

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAgree.setSelected(!ivAgree.isSelected());
                setLoginButtonEnable(ivAgree.isSelected());
            }
        };
        ivAgree.setOnClickListener(listener);
        //tvProvisionDes.setOnClickListener(listener);

        /*tvProvision.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebContentActivity.launchActivity(RegisterActivity.this,"");
            }
        });*/
//        btn_register_by_phone.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//                if (btn_register_by_phone.getText().equals(
//                        RegisterActivity.this.getResources().getString(
//                                R.string.ui_register_via_phone))) {
//                    if (mPhoneRegisterFragment == null)
//                        mPhoneRegisterFragment = new PhoneRegisterFragment();
//                    mFragmentTransaction = RegisterActivity.this.mFragmentManager
//                            .beginTransaction();
//                    mFragmentTransaction.replace(R.id.lay_input_info,
//                            mPhoneRegisterFragment);
//                    mFragmentTransaction.commit();
//                    isFragmentEmail = false;
//                    btn_register_by_phone.setText(RegisterActivity.this
//                            .getResources().getString(
//                                    R.string.ui_register_via_email));
//                } else if (btn_register_by_phone.getText().equals(
//                        RegisterActivity.this.getResources().getString(
//                                R.string.ui_register_via_email))) {
//                    if (mEmailRegisterFragment == null)
//                        mEmailRegisterFragment = new EmailRegisterFragment();
//                    mFragmentTransaction = RegisterActivity.this.mFragmentManager
//                            .beginTransaction();
//                    mFragmentTransaction.replace(R.id.lay_input_info,
//                            mEmailRegisterFragment);
//                    mFragmentTransaction.commit();
//                    isFragmentEmail = true;
//                    btn_register_by_phone.setText(RegisterActivity.this
//                            .getResources().getString(
//                                    R.string.ui_register_via_phone));
//                } else {
//                    if (isFragmentEmail) {
//                        if (mEmailRegisterFragment != null)
//                            mEmailRegisterFragment.checkRegisterInput();
//                    } else {
//                        if ((mPhoneRegisterFragment != null))
//                            mPhoneRegisterFragment.checkRegisterInput();
//                    }
//                    MobclickAgent.onEvent(RegisterActivity.this, AlphaStatics.REGISTER_TRY);//注册次数统计
//                }
//            }
//        });


        mAlreadyAccount.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(intent);
                RegisterActivity.this.finish();

            }
        });
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteWeixinNotInstalled() {
        Toast.makeText(this,getStringResources("ui_action_share_no_wechat"),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginFinish(boolean is_success, JSONObject info,
                              String error_info) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNoteNameEmpty() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNotePassEmpty() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onThridLogin() {
        if (mLoadingDialog == null)
            mLoadingDialog = LoadingDialog.getInstance(this, this);
        mLoadingDialog.show();
    }

    @Override
    public void onThridLoginFinish(boolean is_success, boolean is_registed,
                                   UserInfo info) {
        if (mLoadingDialog != null)
            mLoadingDialog.cancel();
        if (!is_success) {
            Toast.makeText(this,getStringResources("ui_register_prompt_system_error"),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!is_registed) {
            MobclickAgent.onEvent(RegisterActivity.this, AlphaStatics.REGISTER_SUCCESS);
            Intent inte = new Intent();
            inte.putExtra(PrivateInfoHelper.Edit_type,
                    PrivateInfoHelper.EditType.thired_register_type);
            inte.putExtra(AlphaApplicationValues.THIRD_lOGIN_TYPE,
                    mCurrentThirdLoginType);
            inte.putExtra(LoginHelper.IS_LOGIN_SIGLE, is_login_sigle_task);
            inte.setClass(this, PrivateInfoActivity.class);
            this.startActivity(inte);
            this.finish();
        } else {
            if (info.countryCode != null && !info.countryCode.equals("")) {
                mLoginHelper.doRecordUser(UserInfo.getModelStr(info));
                this.finish();
            } else {
                onCompleteCountry(info);
            }

        }
    }

    @Override
    public void noteWaitWebProcressShutDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCompleteCountry(UserInfo current_uer) {
        // TODO Auto-generated method stub
        Intent inte = new Intent();
        inte.putExtra(PrivateInfoHelper.Edit_type,
                PrivateInfoHelper.EditType.complete_info_type);
        inte.putExtra(LoginHelper.IS_LOGIN_SIGLE, is_login_sigle_task);
        inte.putExtra(AlphaApplicationValues.THIRD_lOGIN_TYPE,
                mCurrentThirdLoginType);
        inte.setClass(RegisterActivity.this, PrivateInfoActivity.class);
        RegisterActivity.this.startActivity(inte);
        RegisterActivity.this.finish();
    }


    public void loadFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            if(mCurrentFragment == null)
                transaction.add(R.id.lay_content,targetFragment).commit();
            else
            {
                transaction
                        .hide(mCurrentFragment)
                        .add(R.id.lay_content, targetFragment)
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
