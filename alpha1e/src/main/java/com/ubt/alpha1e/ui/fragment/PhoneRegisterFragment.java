package com.ubt.alpha1e.ui.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*import com.ant.country.CountryActivity;*/
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.DataCheckTools;
import com.ubt.alpha1e.data.model.RegisterInfo;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.PrivateInfoActivity;
import com.ubt.alpha1e.ui.RegisterActivity;
import com.ubt.alpha1e.ui.RegisterActivity.PhoneFragmentListener;
import com.ubt.alpha1e.ui.custom.EditTextCheck;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.ui.helper.IRegisterUI;
import com.ubt.alpha1e.ui.helper.LoginHelper;
import com.ubt.alpha1e.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.ui.helper.RegisterHelper;
import com.ubt.alpha1e.utils.NavigateUtil;

import org.json.JSONObject;

import java.util.Timer;

public class PhoneRegisterFragment extends BaseFragment implements IRegisterUI,
        BaseDiaUI, PhoneFragmentListener {

    private View mMainView;
    private RegisterHelper mHelper;

    private EditText edt_phone_num;
    private EditText edt_passwd;

    private Timer code_timer;
    private int totle_time = 60;

    private LinearLayout lay_contory;
    private TextView txt_country_code;
    private TextView txt_country_num;
    public int MSG_GET_COUNTRY = 11001;
    private String mCountryName = "";
    private String mCountryNumber = "";

    private Handler mHandler = new Handler();
    private RegisterActivity activity;

    private LoadingDialog mCoonLoadingDia;
    private String phoneRegisterAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_register_phone, null);
        initUI();
        initControlListener();
        mHelper = new RegisterHelper(this, (BaseActivity) getActivity());
        return mMainView;
    }
    public static PhoneRegisterFragment newInstance(String account) {
        PhoneRegisterFragment fragment = new PhoneRegisterFragment();
        Bundle args = new Bundle();
        args.putString(LoginHelper.ACCOUNT,account);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phoneRegisterAccount = getArguments().getString(LoginHelper.ACCOUNT);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        activity = (RegisterActivity) this.getActivity();
        setEditTextHint(edt_phone_num,activity.getStringResources("ui_login_phone_placeholder"));
        setEditTextHint(edt_passwd,activity.getStringResources("ui_login_password_placeholder"));
    }

    private void setEditTextHint(EditText edt,String hint)
    {
        SpannableString ss = new SpannableString(hint);
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14,true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        edt.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
    }

    @Override
    protected void initUI() {
        edt_phone_num = (EditText) mMainView.findViewById(R.id.edt_phone_num);
        edt_phone_num.setTypeface(Typeface.DEFAULT);
        edt_passwd = (EditText) mMainView.findViewById(R.id.edt_passwd);
        edt_passwd.setTypeface(Typeface.DEFAULT);
        edt_passwd.setTransformationMethod(new PasswordTransformationMethod());

        EditTextCheck.addCheckForPhone(edt_phone_num, this.getActivity());
        EditTextCheck.addCheckForPasswd(edt_passwd, this.getActivity());

        lay_contory = (LinearLayout) mMainView.findViewById(R.id.layout_country_choose);
        txt_country_code = (TextView) mMainView
                .findViewById(R.id.txt_country_code);
        txt_country_num = (TextView) mMainView
                .findViewById(R.id.txt_country_num);
        edt_phone_num.setText(phoneRegisterAccount);
        edt_phone_num.setFocusable(true);
        edt_phone_num.setFocusableInTouchMode(true);
    }

    @Override
    public void onPause() {
        super.onPause();
//        try {
//
//            btn_phone_verification_code.setText(PhoneRegisterFragment.this
//                    .getActivity().getResources()
//                    .getString(R.string.ui_register_get_vertify_code));
//            try {
//                code_timer.cancel();
//                code_timer = null;
//            } catch (Exception e) {
//                code_timer = null;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initControlListener() {

        edt_phone_num.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                if (arg0.length() > 0) {
//                    if (activity != null)
//                        activity.changeButtonText(true, true);

                } else {
//                    if (activity != null)
//                        activity.changeButtonText(false, false);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        edt_phone_num.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                // TODO Auto-generated method stub
            }
        });

        edt_passwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub

                if (arg0.length() > 0)
                {
                    edt_passwd.setTypeface(Typeface.MONOSPACE);
                    activity.setLoginButtonEnable(arg0.length()>0);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });



        lay_contory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /*if (code_timer == null) {
                    Intent intent = new Intent();
                    intent.setClass(PhoneRegisterFragment.this.getActivity(),
                            CountryActivity.class);
                    PhoneRegisterFragment.this.getActivity()
                            .startActivityForResult(intent, MSG_GET_COUNTRY);
                }*/
            }
        });

    }

    @Override
    public void onRegisterFinish(boolean is_success, JSONObject info,
                                 String error) {
        // TODO Auto-generated method stub

        if (mCoonLoadingDia != null)
            mCoonLoadingDia.cancel();

        if (is_success) {
            Intent inte = new Intent();
            inte.putExtra(PrivateInfoHelper.Edit_type,
                    PrivateInfoHelper.EditType.local_register_type);
            // inte.setClass(getActivity(), _PrivateInfoActivity.class);
            inte.setClass(getActivity(), PrivateInfoActivity.class);
            getActivity().startActivity(inte);
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), error, 500).show();
        }
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNoteVCodeInvalid() {
        // TODO Auto-generated method stub
        if (mCoonLoadingDia != null)
            mCoonLoadingDia.cancel();
        Toast.makeText(
                getActivity(),
                activity.getStringResources("ui_register_prompt_vertify_code_failed"), 500).show();
    }

    public void setCountryInfo(String _mCountryName, String _mCountryNumber) {
        mCountryName = _mCountryName;
        mCountryNumber = _mCountryNumber;
        txt_country_code.setText(mCountryName);
        txt_country_num.setText(_mCountryNumber);
    }

    @Override
    public void noteWaitWebProcressShutDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkRegisterInput() {
        // TODO Auto-generated method stub
        String account = edt_phone_num.getText().toString();
        String passwd = edt_passwd.getText().toString();

        // 0、要选择国家
        // if (mCountryNumber.equals("")) {
        // activity.toastForWrongMeassage(getActivity().getString(
        // R.string.ui_sel_contory_note));
        // // Toast.makeText(
        // // getActivity(),
        // // getActivity().getString(
        // // R.string.ui_sel_contory_note), 500).show();
        // return;
        // }
        // 1、用户名不可为空
        if (account == null || account.trim().equals("")) {
            activity.getStringResources("ui_login_prompt_empty_phone");
            // Toast.makeText(
            // getActivity(),
            // getActivity().getResources().getString(
            // R.string.ui_phone_empty_note), 500).show();
            return;
        }
        // 2、密码和重输密码不可为空
        if (passwd == null || passwd.trim().equals("")) {
            activity.  getStringResources("ui_login_prompt_empty_password");
            // Toast.makeText(
            // getActivity(),
            // getActivity().getResources().getString(
            // R.string.ui_pass_empty_note), 500).show();
            return;
        }
        // 3、验证码不能为空
        // 4、手机号要合法
        if (!DataCheckTools.isPhoneNum(account)) {
            activity.getStringResources("ui_login_prompt_phone_wrong_format");
            // Toast.makeText(
            // getActivity(),
            // getActivity().getResources().getString(
            // R.string.ui_phone_illegal), 500).show();
            return;
        }
        // 5、密码不能太短或者过长
        if (passwd.length() < 6) {
            activity.getStringResources("ui_login_prompt_password_too_short");
            // Toast.makeText(
            // getActivity(),
            // getActivity().getResources().getString(
            // R.string.ui_passwd_short), 500).show();
            return;
        }


        if (passwd.length() > 16) {
            activity.getStringResources("ui_login_prompt_passwprd_too_long");
            // Toast.makeText(
            // getActivity(),
            // getActivity().getResources().getString(
            // R.string.ui_pwd_to_long), 500).show();
            return;
        }



        // 7、密码格式要合法
        if (!DataCheckTools.isCorrectPswFormat(edt_passwd.getText().toString())
                ) {
            activity.getStringResources("ui_login_prompt_passwprd_error");
            // Toast.makeText(getActivity(),
            // getActivity().getString(R.string.ui_pwd_illegal),
            // 500).show();
            return;
        }

//        if (mCoonLoadingDia == null)
//            mCoonLoadingDia = LoadingDialog.getInstance(
//                    PhoneRegisterFragment.this.getActivity(),
//                    PhoneRegisterFragment.this);
//
//        mCoonLoadingDia.show();

//        mHelper.doReigster(mCountryNumber.substring(1), ""
//                        .equalsIgnoreCase(mCountryNumber) ? ("86" + account)
//                        : (mCountryNumber.substring(1) + account), passwd,
//                edt_phone_verification_code.getText().toString(),
//                Register_type.Phone_type);
        RegisterInfo info = new RegisterInfo();
        info.countryCode = "".equalsIgnoreCase(mCountryNumber) ?"86":mCountryNumber.substring(1);
        info.account = info.countryCode+account;
        info.password = passwd;
        NavigateUtil.INSTANCE.navigateToRegisterNextStepForResult(activity,info,RegisterHelper.RegisterCompleteCode);

    }

    

}
