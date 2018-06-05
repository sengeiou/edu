package com.ubt.alpha1e.edu.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.ubt.alpha1e.edu.AlphaApplicationValues;
import com.ubt.alpha1e.edu.AlphaApplicationValues.Thrid_login_type;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.business.thrid_party.MyFaceBook;
import com.ubt.alpha1e.edu.business.thrid_party.MyTencent;
import com.ubt.alpha1e.edu.business.thrid_party.MyTwitter;
import com.ubt.alpha1e.edu.data.DataCheckTools;
import com.ubt.alpha1e.edu.data.model.RegisterInfo;
import com.ubt.alpha1e.edu.data.model.UserInfo;
import com.ubt.alpha1e.edu.ui.dialog.AlertDialog;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.ui.fragment.EmailLoginFragment;
import com.ubt.alpha1e.edu.ui.fragment.PhoneLoginFragment;
import com.ubt.alpha1e.edu.ui.helper.ILoginUI;
import com.ubt.alpha1e.edu.ui.helper.LoginHelper;
import com.ubt.alpha1e.edu.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.edu.ui.helper.RegisterHelper;
import com.ubt.alpha1e.edu.utils.NavigateUtil;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends BaseActivity implements ILoginUI, BaseDiaUI,
        com.ubt.alpha1e.edu.ui.fragment.ILoginUI {

    private static final String TAG = "LoginActivity";
    private Button btn_login;
    // --------------------------------
    private boolean isPhoneLogin = true;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private PhoneLoginFragment mPhoneLoginFragment;
    private EmailLoginFragment mEmailLoginFragment;
    private TextView btn_login_by_phone;
    private TextView btn_login_by_email;
    private TextView txt_forget_pwd;
    // --------------------------------
    private ImageButton btn_logo_twitter;
    private ImageButton btn_logo_facebook;
    private ImageButton btn_logo_qq;
    private ImageButton btn_logo_weixin;
    private ImageButton btn_logo_weibo;
    private String mCountryName = "";
    private String mCountryNumber = "";
    private Thrid_login_type mCurrentThirdLoginType;
    public boolean is_login_sigle_task = false;
    public static final int MSG_GET_COUNTRY = 1001;
    public static final int MSG_CANCEL_DIALOG = 1002;

    private LoadingDialog mLoadingDialog;
    private ImageView im_phone_line, im_email_line;
    private TextView tv_create_account;
    private TextView mInvalidMessage;
    private ImageView img_cancel;
    private String account;


    public static void launchActivity(Activity context, boolean loginType, int requestCode)
    {
        Intent intent = new Intent();
        intent.putExtra(LoginHelper.IS_LOGIN_SIGLE, loginType);
        intent.setClass(context,LoginActivity.class);
        context.startActivityForResult(intent,requestCode);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            is_login_sigle_task = getIntent().getExtras().getBoolean(LoginHelper.IS_LOGIN_SIGLE);
        } catch (Exception e) {
            is_login_sigle_task = false;
        }
        mHelper = new LoginHelper(this, this);
        initUI();
        initControlListener();

        /**
         * KeyHash
         * */
        try {
                PackageInfo info = getPackageManager().getPackageInfo(
                        "com.ubt.alpha1e", PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    Log.d("KeyHash:",Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        setCurrentActivityLable(LoginActivity.class.getSimpleName());
        super.onResume();
        clearWrongMessage();
    }

    @Override
    protected void initUI() {
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login_by_phone = (TextView) findViewById(R.id.btn_login_by_phone);
        im_phone_line = (ImageView) findViewById(R.id.im_phone_line);
        btn_login_by_email = (TextView) findViewById(R.id.btn_login_by_email);
        im_email_line = (ImageView) findViewById(R.id.im_email_line);
        mFragmentManager = this.getFragmentManager();
        mFragmentTransaction = this.mFragmentManager.beginTransaction();
        mPhoneLoginFragment = new PhoneLoginFragment(this);
        mFragmentTransaction.add(R.id.lay_content, mPhoneLoginFragment);
        mFragmentTransaction.commit();
        isPhoneLogin = true;
        // --------------------------------------
        btn_logo_facebook = (ImageButton) findViewById(R.id.btn_logo_facebook);
        btn_logo_twitter = (ImageButton) findViewById(R.id.btn_logo_twitter);
        btn_logo_qq = (ImageButton) findViewById(R.id.btn_logo_qq);
        btn_logo_weixin = (ImageButton) findViewById(R.id.btn_logo_weixin);
        // btn_logo_weibo = (ImageButton) findViewById(R.id.btn_logo_weibo);
        txt_forget_pwd = (TextView) findViewById(R.id.txt_forget_pwd);
        tv_create_account = (TextView) findViewById(R.id.tv_account_already);
        mInvalidMessage = (TextView) findViewById(R.id.tv_invalid_msg);

        img_cancel = (ImageView)findViewById(R.id.img_cancel);
        setLoginButtonEnable(false);
    }

    public void toastForWrongMeassage(String str) {

        mInvalidMessage.setVisibility(View.VISIBLE);
        mInvalidMessage.setText(str);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        mInvalidMessage.startAnimation(shake);
    }

    public void setLoginButtonEnable(boolean isEnable)
    {
        btn_login.setEnabled(isEnable);
    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        ((TextView)findViewById(R.id.txt_base_title_name)).setText(getStringResources("ui_guide_login"));
    }

    public void clearWrongMessage() {
        mInvalidMessage.setText("");
    }

    @Override
    protected void initControlListener() {

        txt_forget_pwd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent inte = new Intent();
                Bundle b = new Bundle();
                b.putBoolean("isPhoneLogin", isPhoneLogin);
                inte.setClass(LoginActivity.this, FindPassWdActivity.class);
                inte.putExtras(b);
                LoginActivity.this.startActivity(inte);
            }
        });

        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isPhoneLogin) {
                    mPhoneLoginFragment.doLogin();
                } else {
                    mEmailLoginFragment.doLogin();
                }
            }
        });

        btn_login_by_email.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mEmailLoginFragment == null){
                    mEmailLoginFragment = new EmailLoginFragment(LoginActivity.this);
                }

                mFragmentTransaction = LoginActivity.this.mFragmentManager.beginTransaction();
                mFragmentTransaction.replace(R.id.lay_content,mEmailLoginFragment);
                mFragmentTransaction.commit();
                isPhoneLogin = false;
                btn_login_by_email.setTextColor(getResources().getColor(R.color.T5));
                btn_login_by_phone.setTextColor(getResources().getColor(R.color.T7));
                im_email_line.setVisibility(View.VISIBLE);
                im_phone_line.setVisibility(View.INVISIBLE);
                im_email_line.setBackgroundColor(getResources().getColor(R.color.T5));
            }
        });
        btn_login_by_phone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (mPhoneLoginFragment == null){
                    mPhoneLoginFragment = new PhoneLoginFragment(LoginActivity.this);
                }

                mFragmentTransaction = LoginActivity.this.mFragmentManager.beginTransaction();
                mFragmentTransaction.replace(R.id.lay_content,mPhoneLoginFragment);
                mFragmentTransaction.commit();
                isPhoneLogin = true;
                btn_login_by_email.setTextColor(getResources().getColor(R.color.T7));
                btn_login_by_phone.setTextColor(getResources().getColor(R.color.T5));
                im_email_line.setVisibility(View.INVISIBLE);
                im_phone_line.setVisibility(View.VISIBLE);
            }
        });

        btn_logo_facebook.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                ((LoginHelper) mHelper).doLogin(Thrid_login_type.FACEBOOK);
                mCurrentThirdLoginType = Thrid_login_type.FACEBOOK;
            }
        });

        btn_logo_twitter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((LoginHelper) mHelper).doLogin(Thrid_login_type.TWITTER);
                mCurrentThirdLoginType = Thrid_login_type.TWITTER;
            }
        });

        btn_logo_qq.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                UbtLog.d(TAG,"QQ登录");
                ((LoginHelper) mHelper).doLogin(Thrid_login_type.QQ);
                mCurrentThirdLoginType = Thrid_login_type.QQ;
            }
        });

        btn_logo_weixin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                UbtLog.d(TAG,"weixin登录");
                ((LoginHelper) mHelper).doLogin(Thrid_login_type.WECHAT);
                mCurrentThirdLoginType = Thrid_login_type.WECHAT;
            }
        });

        // btn_logo_weibo.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View arg0) {
        // ((LoginHelper) mHelper).doLogin(Thrid_login_type.SINABLOG);
        // mCurrentThirdLoginType = Thrid_login_type.SINABLOG;
        // }
        // });
        tv_create_account.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                NavigateUtil.INSTANCE.navigateToRgsActivityForResult(LoginActivity.this,isPhoneLogin,RegisterHelper.RegisterCompleteCode);
                finish();
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
    public void onLoginFinish(boolean is_success, JSONObject info,
                              String error_info) {
        // TODO Auto-generated method stub
        LoginActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog != null){
                    mLoadingDialog.cancel();
                }
            }
        });
        if (is_success) {
            ((LoginHelper) mHelper).doRecordUser(info);//将用户信息写入缓存
            setResult(RESULT_OK);
            this.finish();
        } else
        {
            //新接口，用户不存在，也返回108,旧接口返回1001
            if(error_info.equalsIgnoreCase("1006")){
                new AlertDialog(this).builder()
                        .setMsg(getStringResources("ui_login_account_not_exists"))
                        .setCancelable(true)
                        .setPositiveButton(getStringResources("ui_register"), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                NavigateUtil.INSTANCE.navigateToRgsActivityForResult(LoginActivity.this,account,isPhoneLogin,RegisterHelper.RegisterCompleteCode);
                            }
                        }).setNegativeButton(getStringResources("ui_common_cancel"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }else if(error_info.equalsIgnoreCase("108") || error_info.equalsIgnoreCase("1001"))
            {
                showToast("ui_login_prompt_wrong_user_password");
            }else
            {
                showToast(error_info);
            }
        }
    }

    @Override
    public void onNoteNameEmpty() {
        // TODO Auto-generated method stub
        if (mLoadingDialog != null){
            mLoadingDialog.cancel();
        }
        Toast.makeText(this,
                getStringResources("ui_login_prompt_empty_username"), Toast.LENGTH_SHORT)
                .show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UbtLog.d(TAG,"requestCode : " + requestCode + " resultCode : " + resultCode);
        if (requestCode == MSG_GET_COUNTRY) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                mCountryName = bundle.getString("countryName");
                mCountryNumber = bundle.getString("countryNumber");
                mPhoneLoginFragment.setCotury(mCountryName, mCountryNumber);
            }
        } else if(requestCode== RegisterHelper.RegisterCompleteCode)
        {
            finish();

        }else {
            if (MyTencent.isNeedOnResualt) {
                Tencent.onActivityResultData(requestCode, resultCode, data,
                        (IUiListener) mHelper);
                MyTencent.isNeedOnResualt = false;
            } else if (MyFaceBook.isNeedOnResualt) {
                MyFaceBook.onActivityResult(requestCode, resultCode, data);
                MyFaceBook.isNeedOnResualt = false;
            } else if (MyTwitter.requestCode_tw == requestCode) {
                MyTwitter.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onNotePassEmpty() {
        if (mLoadingDialog != null){
            mLoadingDialog.cancel();
        }

        Toast.makeText(this,getStringResources("ui_login_prompt_empty_password"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onThridLogin() {
        if (mLoadingDialog == null){
            mLoadingDialog = LoadingDialog.getInstance(this,this);
        }
        mLoadingDialog.show();
    }

    @Override
    public void onThridLoginFinish(boolean is_success, boolean is_registed,
                                   UserInfo info) {
        // TODO Auto-generated method stub
        LoginActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog != null){
                    mLoadingDialog.cancel();
                }
            }
        });
        if (!is_success) {
            Toast.makeText(this,getStringResources("ui_register_prompt_system_error"),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!is_registed) {
            RegisterInfo registerInfo = new RegisterInfo();
            registerInfo.thirdLoginType = mCurrentThirdLoginType;
            NavigateUtil.INSTANCE.navigateToRegisterNextStepForResult(LoginActivity.this,registerInfo,RegisterHelper.RegisterCompleteCode);
        } else {

            if (info.countryCode != null && !info.countryCode.equals("")) {
                ((LoginHelper) mHelper).doRecordUser(UserInfo.getModelStr(info));
                this.finish();
            } else {
                onCompleteCountry(info);
            }

        }

    }

    @Override
    public void noteWeixinNotInstalled() {
        Toast.makeText(this,
                getStringResources("ui_action_share_no_wechat"),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void noteWaitWebProcressShutDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDoLogin(String account, String _pwd) {

        this.account = account;
        // 手机登录
        if ((!account.contains("@"))) {
            if (!DataCheckTools.isPhoneNum(account)) {
                toastForWrongMeassage(getStringResources("ui_login_prompt_phone_wrong_format"));
                return;
            }
        }
        // 邮箱登录
        else {
            if (!DataCheckTools.isEmail(account)) {
                toastForWrongMeassage(getStringResources("ui_login_prompt_email_wrong_format"));
                return;
            }
        }

        if (account.equals("")) {
            onNoteNameEmpty();
            return;
        }

        if (mLoadingDialog == null){
            mLoadingDialog = LoadingDialog.getInstance(LoginActivity.this,LoginActivity.this);
        }
        mLoadingDialog.show();

        if (account.contains("@")) {

        } else {
            account = ("".equalsIgnoreCase(mCountryNumber)) ? ("86" + account)
                    : (mCountryNumber.substring(1) + account);
        }
        UbtLog.d(TAG,"account = " + account + " _pwd = " + _pwd);

        ((LoginHelper) mHelper).doLogin(account, _pwd);

    }

    @Override
    public void onCompleteCountry(UserInfo current_uer) {
        // TODO Auto-generated method stub

        Intent inte = new Intent();
        inte.putExtra(PrivateInfoHelper.Edit_type,PrivateInfoHelper.EditType.complete_info_type);
        inte.putExtra(AlphaApplicationValues.THIRD_lOGIN_TYPE,mCurrentThirdLoginType);
        inte.putExtra(LoginHelper.IS_LOGIN_SIGLE, is_login_sigle_task);
        inte.setClass(LoginActivity.this, PrivateInfoActivity.class);
        LoginActivity.this.startActivity(inte);
        LoginActivity.this.finish();
    }

}
